package fr.upec.episen.paas.cacheloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class CacheLoaderApplication {
    public static void main(String[] args) {
        // Force JVM default timezone to Europe/Paris to ensure LocalDateTime handling is consistent
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
        SpringApplication.run(CacheLoaderApplication.class, args);
    }
}