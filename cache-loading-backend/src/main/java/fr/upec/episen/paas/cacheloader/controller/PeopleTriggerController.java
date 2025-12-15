package fr.upec.episen.paas.cacheloader.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.upec.episen.paas.cacheloader.scheduler.CacheRefreshScheduler;

@RestController
@RequestMapping("/api/v1/people")
public class PeopleTriggerController {

    @Autowired
    CacheRefreshScheduler crs;

    /**
     * Simple endpoint invoked by the DB trigger script. No parameters.
     * The user requested no business logic here; they will implement it later.
     */
    @PostMapping("/cache/refresh")
    public void refreshCache() {
        System.out.println("[Controlleur] Changements détectés, plannification d'une mise à jour...");
        crs.tableHasBeenUpdated();
    }
}
