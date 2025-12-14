package fr.upec.episen.paas.operational_backend.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/core_operational_backend/admin")
public class AdminController {

    @GetMapping("/health")
    public Map<String,Object> health() {
        return Map.of("status", "UP", "time", Instant.now().toString());
    }
}
