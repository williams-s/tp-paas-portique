package fr.upec.episen.paas;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class BadgeSensorMock {
    private static String HOST = System.getenv("MQTT_BROKER_HOST");
    private static String PORT = System.getenv("MQTT_BROKER_PORT");
    private static String TOPIC = System.getenv("MQTT_TOPIC");

    private static final Logger logger = LogManager.getLogger(BadgeSensorMock.class);

    public static void main(String[] args){
        if (HOST == null || PORT == null || TOPIC == null) {
            logger.error("Environment variables for MQTT broker are not set.");
            return;
        }

        String brokerUrl = "tcp://" + HOST + ":" + PORT;
        String clientId = "BadgeSensorMockClient";

        try {
            IMqttClient mqttClient = new MqttClient(brokerUrl, clientId);
            mqttClient.connect();

            Random random = new Random();
            while (true) {
                long studentId = random.nextInt(100);
                long doorId = random.nextInt(4) + 1;
                String payload = "{\"studentId\":" + studentId + ",\"doorId\":" + doorId + "}";
                MqttMessage message = new MqttMessage(payload.getBytes());
                mqttClient.publish(TOPIC, message);
                logger.info("Published message: " + payload + " to topic: " + TOPIC);
                System.out.println("Published message: " + payload + " to topic: " + TOPIC);
                Thread.sleep(5000);
            }
        }
        catch (Exception e) {
            logger.error("Error in BadgeSensorMock: ", e);
        }
    }
}
