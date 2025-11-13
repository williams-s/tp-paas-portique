package fr.upec.episen.paas.cacheloader.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * Configure le RedisTemplate avec des sérialiseurs JSON
     * pour stocker les données en JSON dans Redis
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Sérialiseur pour les clés (String)
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        
    // Sérialiseur pour les valeurs (JSON) en utilisant l'ObjectMapper configuré (JavaTimeModule, timezone)
    GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // Configuration des sérialiseurs
        template.setKeySerializer(stringSerializer);
    template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
    template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
