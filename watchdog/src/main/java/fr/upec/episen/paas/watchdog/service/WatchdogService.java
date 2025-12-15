package fr.upec.episen.paas.watchdog.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WatchdogService {
    private final List<String> cores;
    private final RestTemplate restTemplate = new RestTemplate();
    private final AtomicInteger checkCount = new AtomicInteger(0);
    private volatile long lastRestartTime = 0;
    private static final long RESTART_COOLDOWN_MS = 30000;
    private static final long BOOT_GRACE_PERIOD_MS = 45000;

    @Scheduled(fixedDelay = 10000)
    public void monitorCores() {
        int checkNumber = checkCount.incrementAndGet();

        long now = System.currentTimeMillis();
        long timeSinceRestart = now - lastRestartTime;

        if (timeSinceRestart < BOOT_GRACE_PERIOD_MS) {
            log.debug("Boot en cours ({}s restants) - skip check #{}",
                    (BOOT_GRACE_PERIOD_MS - timeSinceRestart) / 1000, checkNumber);
            return;
        }

        log.info("=== Check #{} ===", checkNumber);

        String swarmServicesEnv = System.getenv("SWARM_SERVICES");
        if (swarmServicesEnv == null || swarmServicesEnv.isEmpty()) {
            log.error("SWARM_SERVICES non defini");
            return;
        }

        Map<String, CoreStatus> coreStatusMap = new HashMap<>();
        String[] services = swarmServicesEnv.split(",");
        
        for (int i = 0; i < Math.min(cores.size(), services.length); i++) {
            String hostWithPort = cores.get(i);
            String serviceName = services[i].trim();
            
            String host = hostWithPort.split(":")[0];
            String port = hostWithPort.split(":")[1];

            String healthUrl = String.format("http://%s:%s/core_operational_backend/admin/health", host, port);
            boolean isUp = isCoreUp(healthUrl);

            CoreStatus status = new CoreStatus(serviceName, host, port, isUp);
            coreStatusMap.put(serviceName, status);

            if (isUp) {
                log.info("{} ({}:{}) = UP", serviceName, host, port);
            } else {
                log.info("{} ({}:{}) = DOWN", serviceName, host, port);
            }
        }

        long upCount = coreStatusMap.values().stream().filter(CoreStatus::isUp).count();
        long downCount = coreStatusMap.values().stream().filter(s -> !s.isUp()).count();

        log.info("Resume: {} UP / {} DOWN", upCount, downCount);

        if (downCount > 0) {
            if (timeSinceRestart < RESTART_COOLDOWN_MS) {
                log.warn("Cooldown actif (redemarrage il y a {}s)", timeSinceRestart / 1000);
                log.info("=== Fin check #{} (cooldown) ===", checkNumber);
                return;
            }

            log.warn("{} core(s) DOWN -> Redemarrage des services DOWN uniquement", downCount);

            boolean restartSuccess = false;
            for (CoreStatus status : coreStatusMap.values()) {
                if (!status.isUp()) {
                    if (restartSwarmService(status.getServiceName())) {
                        log.info("Service DOWN redemarre: {}", status.getServiceName());
                        restartSuccess = true;
                    }
                }
            }

            if (restartSuccess) {
                log.info("Redemarrage Swarm effectue pour les services DOWN");
                lastRestartTime = System.currentTimeMillis();

                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                log.error("Echec du redemarrage des services DOWN");
            }
        } else {
            log.info("Tous les cores sont UP");
        }

        log.info("=== Fin check #{} ===", checkNumber);
    }

    private boolean isCoreUp(String healthUrl) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean restartSwarmService(String serviceName) {
        try {
            log.info("Redemarrage du service: {}", serviceName);

            Process process = new ProcessBuilder(
                    "docker", "service", "update", "--force", serviceName.trim()).start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Service {} redemarre avec succes", serviceName);
                return true;
            } else {
                BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()));
                String error = errorReader.readLine();
                log.error("Echec pour {}: {}", serviceName, error);
                return false;
            }

        } catch (Exception e) {
            log.error("Erreur Swarm pour {}: {}", serviceName, e.getMessage());
            return false;
        }
    }

    private static class CoreStatus {
        private final String serviceName;
        private final String host;
        private final String port;
        private final boolean up;

        public CoreStatus(String serviceName, String host, String port, boolean up) {
            this.serviceName = serviceName;
            this.host = host;
            this.port = port;
            this.up = up;
        }

        public String getServiceName() { return serviceName; }
        public String getHost() { return host; }
        public String getPort() { return port; }
        public boolean isUp() { return up; }
    }
}