package fr.upec.episen.paas.core_operational_backend.service;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import fr.upec.episen.paas.core_operational_backend.dto.StudentDTO;
import io.netty.channel.ChannelOption;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoreApiClient {

    private final EndpointProvider endpointProvider;
    private final Logger logger = LogManager.getLogger(CoreApiClient.class);

    private final WebClient webClient = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(
                    HttpClient.create()
                            .responseTimeout(Duration.ofSeconds(5))
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)))
            .build();

    public StudentDTO fetchStudent(Long id) {
        String core1 = endpointProvider.getPrimary();
        String core2 = endpointProvider.getSecondary();

        return callCore(core1, id)
                .onErrorResume(e -> {
                    logger.warn("Core 1 down, basculement vers Core 2"); // plus clean
                    if (core2 != null) {
                        return callCore(core2, id)
                                .onErrorResume(e2 -> {
                                    logger.error("Core 2 également down", e2);
                                    return Mono.error(new RuntimeException("Aucun core disponible"));
                                });
                    }
                    return Mono.error(new RuntimeException("Aucun core disponible"));
                })
                .block();
    }

    private Mono<StudentDTO> callCore(String baseUrlWithPort, Long id) {
        baseUrlWithPort = baseUrlWithPort.trim();
        if (baseUrlWithPort.startsWith("- ")) {
            baseUrlWithPort = baseUrlWithPort.substring(2).trim();
        }

        String url = "http://" + baseUrlWithPort + "/core_operational_backend/student/" + id;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(StudentDTO.class);
    }
}
