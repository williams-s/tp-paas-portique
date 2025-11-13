package fr.upec.episen.paas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import tools.jackson.databind.ObjectMapper;

public class EntranceDoorLock {
    private static final Logger logger = LogManager.getLogger(EntranceDoorLock.class);

    private static String HOST = System.getenv("MQTT_BROKER_HOST");
    private static String PORT = System.getenv("MQTT_BROKER_PORT");
    private static String TOPIC = System.getenv("MQTT_DOOR_TOPIC");

    public static void main(String[] args) {
        if (HOST == null || PORT == null || TOPIC == null) {
            logger.error("Environment variables for MQTT broker are not set.");
            return;
        }

        String brokerUrl = "tcp://" + HOST + ":" + PORT;
        String clientId = "EntranceDoorLockClient";

        try {
            IMqttClient mqttClient = new MqttClient(brokerUrl, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(10);

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    logger.error("Connection to MQTT broker lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage msg) throws Exception {
                    String message = new String(msg.getPayload());
                    ObjectMapper objectMapper = new ObjectMapper();
                    boolean shouldOpen = objectMapper.readTree(message).get("shouldOpen").asBoolean();
                    if (shouldOpen) {
                        logger.warn("Door opened");
                        Thread.sleep(3000);
                        logger.warn("Door closed");
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    logger.warn("Delivery complete for token: " + token);
                }
            });

            mqttClient.connect(options);
            mqttClient.subscribe(TOPIC);
            logger.warn("Subscribed to topic " + TOPIC + " on broker " + brokerUrl);

            while(true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            logger.error("Error in EntranceDoorLock: ", e);
        }
    }
}