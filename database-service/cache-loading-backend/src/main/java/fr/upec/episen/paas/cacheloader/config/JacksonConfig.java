package fr.upec.episen.paas.cacheloader.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Support Java 8 date/time types
        mapper.registerModule(new JavaTimeModule());
        // Write ISO dates (not timestamps)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Set default timezone to Europe/Paris
        mapper.setTimeZone(java.util.TimeZone.getTimeZone(ZoneId.of("Europe/Paris")));
        return mapper;
    }
}
