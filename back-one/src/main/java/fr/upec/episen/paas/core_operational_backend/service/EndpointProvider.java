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
        
        if (cores != null) {
            for (int i = 0; i < cores.size(); i++) {
                String endpoint = cores.get(i);
                
                endpoint = endpoint.trim();
                if (endpoint.startsWith("- ")) {
                    endpoint = endpoint.substring(2).trim();
                }
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