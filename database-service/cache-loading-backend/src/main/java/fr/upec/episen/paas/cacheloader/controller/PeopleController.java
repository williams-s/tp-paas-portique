package fr.upec.episen.paas.cacheloader.controller;

import fr.upec.episen.paas.cacheloader.model.People;
import fr.upec.episen.paas.cacheloader.model.Student;
import fr.upec.episen.paas.cacheloader.repository.PeopleRepository;
import fr.upec.episen.paas.cacheloader.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/people") // Base URL pour toutes les requêtes de ce contrôleur
public class PeopleController {

    private final PeopleRepository peopleRepository;
    private final RedisService redisService;

    @Autowired
    public PeopleController(PeopleRepository peopleRepository, RedisService redisService) {
        this.peopleRepository = peopleRepository;
        this.redisService = redisService;
    }

    @GetMapping
    public ResponseEntity<List<People>> getAllPeople() {
        List<People> people = peopleRepository.findAll();
        return ResponseEntity.ok(people);
    }

    @GetMapping("/allowed-now")
    public ResponseEntity<List<People>> getPersonAllowed() {
        List<People> people = peopleRepository.findAllAllowedNow();
        return ResponseEntity.ok(people);
    }

    /**
     * Endpoint pour récupérer les personnes autorisées depuis le cache Redis
     */
    @GetMapping("/cache/allowed")
    public ResponseEntity<List<Student>> getCachedAllowedPeople() {
        List<Student> cachedPeople = redisService.getAllowedPeopleFromCache();
        return ResponseEntity.ok(cachedPeople);
    }

    /**
     * Endpoint pour consulter l'état du cache
     */
    @GetMapping("/cache/status")
    public ResponseEntity<Map<String, Object>> getCacheStatus() {
        Map<String, Object> status = new HashMap<>();
        List<Student> cachedPeople = redisService.getAllowedPeopleFromCache();
        Long lastUpdate = redisService.getLastUpdateTimestamp();
        
        status.put("cached_count", cachedPeople.size());
        status.put("last_update_timestamp", lastUpdate);
        status.put("cached_people", cachedPeople);
        
        return ResponseEntity.ok(status);
    }

    /**
     * Endpoint pour vider manuellement le cache Redis
     */
    @GetMapping("/cache/clear")
    public ResponseEntity<Map<String, String>> clearCache() {
        redisService.clearCache();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cache Redis vidé avec succès");
        return ResponseEntity.ok(response);
    }
}