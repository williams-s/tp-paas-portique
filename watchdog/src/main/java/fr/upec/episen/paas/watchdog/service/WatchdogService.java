package fr.upec.episen.paas.watchdog.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

@Service
public class WatchdogService {

    private final List<String> cores;
    private final String statusFile = System.getenv("shared_file_path");
    private final Logger logger = LogManager.getLogger(WatchdogService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    public WatchdogService(List<String> cores) {
        this.cores = cores;
        logger.info("Loaded cores from config: {}", cores);
        System.out.println("Loaded cores from config: " + cores);
    }

    @Scheduled(fixedDelay = 1000)
    public void poll() {
        Map<String, String> statusMap = new HashMap<>();

        for (String core : cores) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(core + "/operational_backend/admin/health", String.class);

                boolean up = response.getStatusCode().is2xxSuccessful();
                statusMap.put(core, up ? "UP" : "DOWN");

                logger.info("Polled {} → {}", core, up ? "UP" : "DOWN");
                System.out.println("Polled " + core + " → " + (up ? "UP" : "DOWN"));

            } catch (Exception e) {
                logger.warn("Core {} unreachable", core);
                System.out.println("Core " + core + " unreachable");
                statusMap.put(core, "DOWN");
            }
        }

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);

        try (FileWriter writer = new FileWriter(statusFile)) {
            yaml.dump(Map.of("cores", statusMap.keySet().stream().toList()), writer);
        } catch (IOException e) {
            logger.error("Error writing status file", e);
        }
    }
}