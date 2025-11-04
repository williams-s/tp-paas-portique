package paas.tp.entrance_cockpit_backend;


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
    public void consume(Object message) {
        logger.info("Received message: " + message);
        webSocketHandler.sendMessage(message.toString());
        logger.info("Message sent to WebSocket");
    }
}
