package fr.upec.episen.paas.watchdog.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WatchdogService {

    private final List<String> cores;
    private final Logger logger = LogManager.getLogger(WatchdogService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    public WatchdogService(List<String> cores) {
        this.cores = cores;
        logger.info("Loaded cores from config: {}", cores);
        System.out.println("Loaded cores from config: " + cores);
    }

    @Scheduled(fixedDelay = 1000)
    public void poll() {
        for (String core : cores) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(core + "/operational_backend/admin/health", String.class);

                boolean up = response.getStatusCode().is2xxSuccessful();

                logger.info("Polled {} → {}", core, up ? "UP" : "DOWN");
                System.out.println("Polled " + core + " → " + (up ? "UP" : "DOWN"));
            } catch (Exception e) {
                logger.warn("Core {} unreachable", core);
                System.out.println("Core " + core + " unreachable");
            }
        }
    }
}