package paas.tp.entrance_cockpit_backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoorService {

    private final Logger logger = LogManager.getLogger(DoorService.class);

    @Autowired
    private KafkaTemplate<String, ObjectNode> kafkaTemplate;

    public boolean openDoor() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("allowed", true);
            jsonNode.put("service", "entrance-cockpit-backend");
            jsonNode.put("timestamp", System.currentTimeMillis());
            jsonNode.put("manually_open_door", true);
            logger.info("Sending message: " + jsonNode);
            kafkaTemplate.send("attemps-logs",jsonNode);
            return true;
        } catch (Exception e) {
            logger.error("Error during JSON parsing or sending message: ", e);
        }
        return false;
    }
}
