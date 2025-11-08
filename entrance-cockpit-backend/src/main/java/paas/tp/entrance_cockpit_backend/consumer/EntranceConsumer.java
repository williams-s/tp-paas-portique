package paas.tp.entrance_cockpit_backend.consumer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import paas.tp.entrance_cockpit_backend.utils.QueueEntrance;
import paas.tp.entrance_cockpit_backend.webSocket.WebSocketHandler;


@Component
@RequiredArgsConstructor
public class EntranceConsumer {
    private static final Logger logger = LogManager.getLogger(EntranceConsumer.class);

    private final WebSocketHandler webSocketHandler;

    @KafkaListener(topics = "entrance-logs", groupId = "entrance-cockpit-backend")
    public void consumeAcceptedEntrance(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);
            logger.info("Access granted: " + jsonNode);
            QueueEntrance.addAcceptedEntrance(jsonNode);
            sendToWebSocket(jsonNode);
        } catch (Exception e) {
            logger.error("Error parsing JSON, sending raw message");
            webSocketHandler.sendMessage(message);
            logger.info("Raw message sent to WebSocket");
        }
    }

    @KafkaListener(topics = "logs", groupId = "entrance-cockpit-backend")
    public void consumeAllLogs(String message) {
        logger.info("Received message in topic logs: " + message);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);
            if (isAccessDenied(jsonNode)) {
                sendToWebSocket(jsonNode);
            }
        } catch (Exception e) {
            logger.error("Error parsing JSON, sending raw message");
        }
    }

    private void sendToWebSocket(JsonNode jsonNode) {
        webSocketHandler.sendMessage(jsonNode.toString());
        logger.info(jsonNode.toString());
        logger.info("Json sent to WebSocket");
    }

    private boolean isAccessDenied (JsonNode jsonNode) {
        JsonNode serviceName = jsonNode.get("service");
        if (serviceName != null) {
            if (serviceName.asText().equals("core-operational-backend")) {
                JsonNode allowed = jsonNode.get("allowed");
                if (allowed != null) {
                    logger.info("Access denied: " + jsonNode);
                    return !allowed.asBoolean();
                }
            }
        }
        return false;
    }

}
