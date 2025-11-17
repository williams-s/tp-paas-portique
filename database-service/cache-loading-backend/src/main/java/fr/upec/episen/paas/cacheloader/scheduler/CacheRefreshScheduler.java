package fr.upec.episen.paas.cacheloader.scheduler;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@EnableScheduling
public class CacheRefreshScheduler {

    private final PeopleRepository peopleRepository;
    private final RedisService redisService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Removed gating flag; scheduler runs periodically now.
    private static boolean dbHasBeenUpdated = false;


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
    @Scheduled(fixedRate = 30000)
    private void refreshPeopleCache() {
        
        if (!dbHasBeenUpdated) {
            System.out.println("[Scheduler] " + LocalDateTime.now().format(formatter) + " - Pas de changement enregistré, retry dans 30s...");
            return;
        }
        try {
            System.out.println("[Scheduler] " + LocalDateTime.now().format(formatter) + " - Rafraîchissement du cache Redis...");
            
            // Récupère les personnes autorisées (équivalent de getPersonAllowed())
            List<People> allowedPeople = peopleRepository.findAllAllowedNow();
            List<People> deniedPeople = peopleRepository.findAllNotAllowedNow();

            Map<String, Student> students = new HashMap<>();
            
            for (People people : allowedPeople) {
                Student s = new Student();
                s.setFirstname(people.getFirstName());
                s.setLastname(people.getLastName());
                s.setNum(people.getNum());
                s.setIsAuthorized("true");
                students.put(people.getId().toString(), s);
            }

            for (People people : deniedPeople) {
                Student s = new Student();
                s.setFirstname(people.getFirstName());
                s.setLastname(people.getLastName());
                s.setNum(people.getNum());
                s.setIsAuthorized("false");
                students.put(people.getId().toString(), s);
            }

            
            // Écrit dans Redis
            redisService.saveAllowedPeople(students);
    

            System.out.println("[Scheduler] ✓ Cache mis à jour avec " + students.size() + " personnes");
            
            // TODO dbHasBeenUpdated = false;
        } catch (Exception e) {
            System.err.println("[Scheduler] ✗ Erreur lors du rafraîchissement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Optionnel : Tâche initiale au démarrage de l'application
     * Attends 5 secondes avant la première exécution
     */
    @Scheduled(initialDelay = 5000)
    private void initializeCache() {
        dbHasBeenUpdated = true;
        System.out.println("[Scheduler] Application démarrée, initialisation du cache...");
        refreshPeopleCache();
    }

    public void tableHasBeenUpdated() {
        System.out.println("[Scheduler] Notification reçue : mise à jour de la table, déclenchement immédiat...");
        dbHasBeenUpdated = true;
        // Trigger an immediate refresh in a background thread
        //new Thread(this::refreshPeopleCache).start();
    }
}
