package paas.tp.entrance_cockpit_backend.controller;


import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import paas.tp.entrance_cockpit_backend.DTO.DoorDTO;
import paas.tp.entrance_cockpit_backend.services.DoorService;

@RestController
@RequestMapping("/api/door")
@RequiredArgsConstructor
public class DoorController {

    private final Logger logger = LogManager.getLogger(DoorController.class);

    @Autowired
    private DoorService doorService;

    @PostMapping("/open")
    public boolean openDoor(@RequestBody DoorDTO door) {
        logger.info("Trying to open door {} ({})", door.id(), door.name());
        return doorService.openDoor(door);
    }

}
