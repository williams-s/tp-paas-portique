package fr.upec.episen.paas.cacheloader.controller;

import fr.upec.episen.paas.cacheloader.model.People;
import fr.upec.episen.paas.cacheloader.repository.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/people") // Base URL pour toutes les requêtes de ce contrôleur
public class PeopleController {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleController(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
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
}