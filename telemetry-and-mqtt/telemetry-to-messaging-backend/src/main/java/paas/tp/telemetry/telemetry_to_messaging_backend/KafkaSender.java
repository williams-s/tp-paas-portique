package paas.tp.telemetry.telemetry_to_messaging_backend;

import com.fasterxml.jackson.core.JsonProcessingException;
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
public class KafkaSender {

    @Autowired
    private KafkaTemplate<String, ObjectNode> kafkaTemplate;

    private static final Logger logger = LogManager.getLogger(KafkaSender.class);

    public void send(String message)  {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ObjectNode jsonNode = (ObjectNode) mapper.readTree(message);
            jsonNode.put("timestamp", System.currentTimeMillis());
            jsonNode.put("service", "telemetry-to-messaging-backend");
            //String messageToSend = mapper.writeValueAsString(jsonNode);

            logger.info("Sending message: " + jsonNode);
            kafkaTemplate.send("attemps-logs", jsonNode);
            kafkaTemplate.send("logs", jsonNode);

        } catch (Exception e) {
            logger.error("Error during JSON parsing or sending message: " + message, e);
        }

    }
}
