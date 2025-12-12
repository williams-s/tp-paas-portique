package fr.upec.episen.paas.core_operational_backend.service;

import java.time.Duration;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import fr.upec.episen.paas.core_operational_backend.dto.StudentDTO;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import reactor.netty.http.client.HttpClient;

@Service
@RequiredArgsConstructor
public class CoreApiClient {
    private final EndpointProvider endpointProvider;

    private final WebClient webClient = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(
                    HttpClient.create()
                            .responseTimeout(Duration.ofSeconds(2))
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)))
            .build();

    private static final int MAX_RETRY = 2;

    public StudentDTO fetchStudent(Long id) {
        String core1 = endpointProvider.getPrimary();
        String core2 = endpointProvider.getSecondary();

        if (tryCore(core1, id)) {
            return callCore(core1, id);
        }

        System.err.println("Core1 DOWN → Restarting...");
        restartDocker("core1");

        if (core2 != null && tryCore(core2, id)) {
            return callCore(core2, id);
        }

        System.err.println("Core2 DOWN → Restarting...");
        restartDocker("core2");

        throw new RuntimeException("Aucun core disponible");
    }

    private boolean tryCore(String url, Long id) {
        for (int i = 0; i < MAX_RETRY; i++) {
            try {
                callCore(url, id);
                return true;
            } catch (Exception e) {
                System.err.println("Tentative " + (i+1) + "/" + MAX_RETRY + " échouée pour " + url);
            }
        }
        return false;
    }

    private StudentDTO callCore(String baseUrl, Long id) {
        return webClient.get()
                .uri(baseUrl + "/operational_backend/student/" + id)
                .retrieve()
                .bodyToMono(StudentDTO.class)
                .block();
    }

    private void restartDocker(String container) {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(System.getenv("ssh_user_private_key"));

            Session session = jsch.getSession(System.getenv("ssh_user"), System.getenv("ssh_host"), 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(3000);

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("docker restart " + container);
            channel.connect();

            System.out.println("Restart exit code: " + channel.getExitStatus());

            channel.disconnect();
            session.disconnect();

        } catch (Exception e) {
            System.err.println("Erreur SSH: " + e.getMessage());
        }
    }
}

