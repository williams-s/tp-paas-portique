package paas.tp.telemetry.telemetry_to_messaging_backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

@Component
public class MQTTProducer {
    private static final Logger logger = LogManager.getLogger(MQTTProducer.class);

    private static String HOST = System.getenv("MQTT_BROKER_HOST");
    private static String PORT = System.getenv("MQTT_BROKER_PORT");
    private static String TOPIC = System.getenv("MQTT_DOOR_TOPIC");

    private IMqttClient mqttClient;

    public MQTTProducer() {
        String brokerUrl = "tcp://" + HOST + ":" + PORT;
        String clientId = "TelemetryMQTTProducer";

        try {
            mqttClient = new MqttClient(brokerUrl, clientId);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(10);

            mqttClient.connect(options);
            logger.info("Connected to MQTT broker at " + brokerUrl);
        }
        catch (Exception e) {
            logger.error("Error initializing MQTT client: ", e);
        }
    }
    public void publish(String payload) {

        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            mqttClient.publish(TOPIC, message);
            logger.info("Published message to topic " + TOPIC + ": " + payload);
        }
        catch (Exception e) {
            logger.error("Error publishing MQTT message: ", e);
        }
    }
}
