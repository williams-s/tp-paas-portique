package fr.upec.episen.paas.cacheloader.scheduler;

import fr.upec.episen.paas.cacheloader.mapper.PeopleToRedisModelMapper;
import fr.upec.episen.paas.cacheloader.model.People;
import fr.upec.episen.paas.cacheloader.model.Student;
import fr.upec.episen.paas.cacheloader.repository.PeopleRepository;
import fr.upec.episen.paas.cacheloader.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Component
@EnableScheduling
public class CacheRefreshScheduler {

    private final PeopleRepository peopleRepository;
    private final RedisService redisService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    PeopleToRedisModelMapper peopleToRedisModelMapper = new PeopleToRedisModelMapper();

    @Autowired
    public CacheRefreshScheduler(PeopleRepository peopleRepository, RedisService redisService) {
        this.peopleRepository = peopleRepository;
        this.redisService = redisService;
    }

    /**
     * Exécute la tâche toutes les 30 secondes (30000 ms)
     * Vous pouvez ajuster l'intervalle selon vos besoins :
     * - 60000 ms = 1 minute
     * - 300000 ms = 5 minutes
     * - 900000 ms = 15 minutes
     */
    //@Scheduled(fixedRate = 30000)
    public void refreshPeopleCache() {
        try {
            System.out.println("[Scheduler] " + LocalDateTime.now().format(formatter) + " - Rafraîchissement du cache Redis...");
            
            // Récupère les personnes autorisées (équivalent de getPersonAllowed())
            List<People> allowedPeople = peopleRepository.findAllAllowedNow();
            List<People> deniedPeople = peopleRepository.findAllNotAllowedNow();

            List<Student> combinedStudent = Stream.concat(
                peopleToRedisModelMapper.peopleToStudent(allowedPeople, true).stream(),
                peopleToRedisModelMapper.peopleToStudent(deniedPeople, false).stream()).toList();
            
            // Écrit dans Redis
            redisService.saveAllowedPeople(combinedStudent);
            
            System.out.println("[Scheduler] ✓ Cache mis à jour avec " + combinedStudent.size() + " personnes");
        } catch (Exception e) {
            System.err.println("[Scheduler] ✗ Erreur lors du rafraîchissement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Optionnel : Tâche initiale au démarrage de l'application
     * Attends 5 secondes avant la première exécution
     */
    @Scheduled(initialDelay = 5000, fixedRate = 30000)
    public void initializeCache() {
        System.out.println("[Scheduler] Application démarrée, initialisation du cache...");
        refreshPeopleCache();
    }
}
