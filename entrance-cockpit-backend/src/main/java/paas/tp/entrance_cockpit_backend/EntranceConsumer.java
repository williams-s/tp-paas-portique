package paas.tp.entrance_cockpit_backend;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import paas.tp.entrance_cockpit_backend.webSocket.WebSocketHandler;

@Component
@RequiredArgsConstructor
public class EntranceConsumer {
    private static final Logger logger = LogManager.getLogger(EntranceConsumer.class);

    private final WebSocketHandler webSocketHandler;


    @KafkaListener(topics = "entrance-logs", groupId = "entrance-cockpit-backend")
    public void consume(String message) {
        logger.info("Received Kafka message: " + message);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);
            webSocketHandler.sendMessage(jsonNode.toString());
            logger.info(jsonNode.toString());
            logger.info("Json sent to WebSocket");
        } catch (Exception e) {
            logger.error("Error parsing JSON, sending raw message");
            webSocketHandler.sendMessage(message);
            logger.info("Raw message sent to WebSocket");
        }
    }
}
