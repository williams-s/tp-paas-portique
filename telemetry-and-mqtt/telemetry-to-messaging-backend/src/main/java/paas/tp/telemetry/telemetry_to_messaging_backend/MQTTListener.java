package paas.tp.telemetry.telemetry_to_messaging_backend;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

@Service
public class MQTTListener {

    private static final Logger logger = LogManager.getLogger(MQTTListener.class);

    private static String HOST = System.getenv("MQTT_BROKER_HOST");
    private static String PORT = System.getenv("MQTT_BROKER_PORT");
    private static String TOPIC = System.getenv("MQTT_TOPIC");

    private final KafkaSender kafkaSender;
    private MqttClient client;
    public MQTTListener(KafkaSender kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    @PostConstruct
    public void init() throws Exception {
        if (HOST == null || PORT == null || TOPIC == null) {
            logger.error("Environment variables for MQTT broker are not set.");
            return;
        }

        String brokerUrl = "tcp://" + HOST + ":" + PORT;
        String clientId = "TelemetryToMessagingBackendClient";

        try {
            client = new MqttClient(brokerUrl, clientId);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    logger.error("Connection to MQTT broker lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage msg) throws Exception {
                    String message = new String(msg.getPayload());
                    logger.info("Received message: " + message + " from topic: " + topic);
                    kafkaSender.send(message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    logger.info("Delivery complete for token: " + token);
                }
            });

            client.connect(options);
            client.subscribe(TOPIC);

        } catch (Exception e) {
            logger.error("Error in ListenerMQTT: ", e);
        }
    }
}
