package paas.tp.entrance_cockpit_backend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
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

    @PostMapping("/open")
    public boolean openDoor() {
        logger.info("trying to open the door");
        return doorService.openDoor();
    }
}
