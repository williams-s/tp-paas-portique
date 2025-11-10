package fr.upec.episen.paas.cacheloader.service;

import fr.upec.episen.paas.cacheloader.model.People;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    // Clé Redis pour stocker la liste des personnes autorisées
    private static final String ALLOWED_PEOPLE_KEY = "allowed:people";
    private static final String ALLOWED_PEOPLE_TIMESTAMP_KEY = "allowed:people:timestamp";

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Écrit la liste des personnes autorisées dans Redis
     * @param people Liste des personnes à stocker
     */
    public void saveAllowedPeople(List<People> people) {
        try {
            // Supprime l'ancienne liste
            redisTemplate.delete(ALLOWED_PEOPLE_KEY);
            
            // Stocke chaque personne avec un index
            for (int i = 0; i < people.size(); i++) {
                redisTemplate.opsForList().rightPush(ALLOWED_PEOPLE_KEY, people.get(i));
            }
            
            // Enregistre le timestamp de la mise à jour
            redisTemplate.opsForValue().set(
                ALLOWED_PEOPLE_TIMESTAMP_KEY, 
                System.currentTimeMillis()
            );
            
            // Les données restent dans Redis indéfiniment (ou définissez une TTL si nécessaire)
            // redisTemplate.expire(ALLOWED_PEOPLE_KEY, 1, TimeUnit.HOURS); // Optionnel
            
            System.out.println("[Redis] Sauvegardé " + people.size() + " personnes autorisées");
        } catch (Exception e) {
            System.err.println("[Redis] Erreur lors de la sauvegarde : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Récupère la liste des personnes autorisées depuis Redis
     * @return Liste des personnes ou liste vide
     */
    public List<People> getAllowedPeopleFromCache() {
        try {
            List<Object> items = redisTemplate.opsForList().range(ALLOWED_PEOPLE_KEY, 0, -1);
            if (items != null && !items.isEmpty()) {
                return items.stream()
                    .filter(item -> item instanceof People)
                    .map(item -> (People) item)
                    .toList();
            }
            return List.of();
        } catch (Exception e) {
            System.err.println("[Redis] Erreur lors de la lecture : " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Récupère le timestamp de la dernière mise à jour
     * @return Timestamp en millisecondes ou -1 si non trouvé
     */
    public Long getLastUpdateTimestamp() {
        try {
            Object timestamp = redisTemplate.opsForValue().get(ALLOWED_PEOPLE_TIMESTAMP_KEY);
            if (timestamp instanceof Long) {
                return (Long) timestamp;
            }
            return -1L;
        } catch (Exception e) {
            return -1L;
        }
    }

    /**
     * Vide le cache Redis
     */
    public void clearCache() {
        try {
            redisTemplate.delete(ALLOWED_PEOPLE_KEY);
            redisTemplate.delete(ALLOWED_PEOPLE_TIMESTAMP_KEY);
            System.out.println("[Redis] Cache vidé");
        } catch (Exception e) {
            System.err.println("[Redis] Erreur lors du vidage du cache : " + e.getMessage());
        }
    }
}
