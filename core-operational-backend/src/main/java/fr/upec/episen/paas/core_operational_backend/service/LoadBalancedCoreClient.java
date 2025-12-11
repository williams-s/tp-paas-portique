package fr.upec.episen.paas.core_operational_backend.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*; 
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LoadBalancedCoreClient {

    private final List<String> cores;
    private Map<String, String> statusMap = new HashMap<>();
    private int currentIndex = 0;

    private static final String STATUS_FILE = System.getenv("shared_file_path");

    public LoadBalancedCoreClient(List<String> cores) {
        this.cores = cores;
        updateStatus();
    }

    public void updateStatus() {
        try {
            byte[] jsonBytes = Files.readAllBytes(Paths.get(STATUS_FILE));
            ObjectMapper mapper = new ObjectMapper();
            statusMap = mapper.readValue(jsonBytes, HashMap.class);
        } catch (Exception e) {
            System.out.println("Unable to load status file: " + e.getMessage());
            statusMap = new HashMap<>();
        }
    }

    public synchronized String getNextUpCore() {
        updateStatus();

        List<String> upCores = cores.stream()
            .filter(c -> "UP".equalsIgnoreCase(statusMap.get(c)))
            .toList();

        if (upCores.isEmpty()) {
            throw new RuntimeException("No available core (all DOWN)");
        }

        String selected = upCores.get(currentIndex % upCores.size());
        currentIndex++;
        return selected;
    }
}