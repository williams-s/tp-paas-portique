package paas.tp.entrance_cockpit_backend.controller;


import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paas.tp.entrance_cockpit_backend.services.DoorService;

@RestController
@RequestMapping("/api/door")
@RequiredArgsConstructor
public class DoorController {

    private final Logger logger = LogManager.getLogger(DoorController.class);

    @Autowired
    private DoorService doorService;

    @PostMapping("/open/{id}")
    public boolean openDoor(@PathVariable String id) {
        logger.info("trying to open the door {} ", id);
        return doorService.openDoor(id);
    }
}
