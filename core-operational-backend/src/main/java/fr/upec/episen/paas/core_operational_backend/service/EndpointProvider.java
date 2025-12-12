package fr.upec.episen.paas.core_operational_backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class EndpointProvider {
    private final List<String> endpoints;

    public EndpointProvider() throws IOException {
        this.endpoints = Files.readAllLines(Path.of(System.getenv("shared_file_path")));
    }

    public String getPrimary() {
        return endpoints.get(0);
    }

    public String getSecondary() {
        return endpoints.size() > 1 ? endpoints.get(1) : null;
    }
}
