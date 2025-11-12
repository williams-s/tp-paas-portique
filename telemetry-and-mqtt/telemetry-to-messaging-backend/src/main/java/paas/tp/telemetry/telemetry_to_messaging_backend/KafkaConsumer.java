package paas.tp.telemetry.telemetry_to_messaging_backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private static final Logger logger = LogManager.getLogger(KafkaConsumer.class);
    private final MQTTProducer mqttProducer;

    @KafkaListener(topics = "attemps-logs", groupId = "telemetry-to-messaging-backend")
    public void consumeAttemps(String message) {
        logger.info("Received message: " + message);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(message);
            boolean shouldOpen = false;

            shouldOpen = jsonNode.get("allowed").asBoolean();
            if (shouldOpen) {
                String payload = "{\"shouldOpen\":true" + "}";
                mqttProducer.publish(payload);
            }
        }
        catch (Exception e) {
            logger.error("Error processing message: ", e);
        }
    }
}
