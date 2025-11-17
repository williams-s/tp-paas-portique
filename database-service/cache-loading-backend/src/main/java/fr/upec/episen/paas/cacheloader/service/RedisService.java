package fr.upec.episen.paas.cacheloader.service;

import fr.upec.episen.paas.cacheloader.model.Student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

@Service
public class RedisService {


    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RedisTemplate<String, Object> redisTemplate;
    
    // Clé Redis pour stocker la liste des personnes autorisées
    private static final String ALLOWED_PEOPLE_TIMESTAMP_KEY = "allowed:people:timestamp";
    private static final String STUDENT_KEY = "student";

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Écrit la liste des personnes autorisées dans Redis
     * @param people Liste des personnes à stocker
     */
    public void saveAllowedPeople(Map<String, Student> studentsMap) {
        try {

            Set<String> keys = redisTemplate.keys(STUDENT_KEY);
            System.out.println("########### Il y a "+keys.size()+" enregistrements");
            System.out.println("suppression");
            for (String key : keys) {
                System.out.println(key);
                redisTemplate.delete(key);
            }
            
            keys = redisTemplate.keys(STUDENT_KEY);
            System.out.println("########### Il y a "+keys.size()+" enregistrements");
            

            ObjectMapper objectMapper = new ObjectMapper();

            // Sérialise chaque Student en JSON et stocke dans Redis sur l'index du people
            for (String studentId : studentsMap.keySet()) {
                // Supprime l'ancienne valeur
                //redisTemplate.delete(STUDENT_KEY+":"+studentId);
                
                String jsonStudent = objectMapper.writeValueAsString(studentsMap.get(studentId));
                JsonNode node = objectMapper.readTree(jsonStudent);
                redisTemplate.opsForValue().set(STUDENT_KEY+":"+studentId, node);
            }
            
            // Enregistre le timestamp de la mise à jour
            redisTemplate.opsForValue().set(
                ALLOWED_PEOPLE_TIMESTAMP_KEY, 
                LocalDateTime.now().format(formatter)
            );
            
            System.out.println("[Redis] Sauvegardé " + studentsMap.size() + " personnes autorisées");
        } catch (Exception e) {
            System.err.println("[Redis] Erreur lors de la sauvegarde : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
