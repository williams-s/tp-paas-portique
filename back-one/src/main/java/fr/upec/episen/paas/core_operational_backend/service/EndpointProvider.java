package fr.upec.episen.paas.core_operational_backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

@Service
public class EndpointProvider {
    private final List<String> endpoints;

    public EndpointProvider() throws IOException {
        Yaml yaml = new Yaml();
        var data = yaml.load(Files.newInputStream(Path.of(System.getenv("SHARED_FILE_PATH"))));
        
        @SuppressWarnings("unchecked")
        List<String> cores = (List<String>) ((Map<?, ?>) data).get("cores");
        
        // Nettoyer les endpoints
        if (cores != null) {
            for (int i = 0; i < cores.size(); i++) {
                String endpoint = cores.get(i);
                
                // 1. Enlever le "- " au début
                endpoint = endpoint.trim();
                if (endpoint.startsWith("- ")) {
                    endpoint = endpoint.substring(2).trim();
                }
                
                // 2. Enlever ":null" à la fin si présent
                if (endpoint.endsWith(":null")) {
                    endpoint = endpoint.substring(0, endpoint.length() - 5);
                }
                
                // 3. S'assurer qu'il n'y a pas d'autres ":null" au milieu
                endpoint = endpoint.replace(":null", "");
                
                cores.set(i, endpoint);
            }
        }
        
        this.endpoints = cores;
        
        if (this.endpoints == null || this.endpoints.isEmpty()) {
            throw new IOException("No cores found in configuration file");
        }
    }

    public String getPrimary() {
        return endpoints.get(0);
    }

    public String getSecondary() {
        return endpoints.size() > 1 ? endpoints.get(1) : null;
    }
}