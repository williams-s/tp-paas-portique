package fr.upec.episen.paas;

import java.io.InputStream;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.net.ssl.SSLContext;

public class BadgeSensorMock {
    private static String HOST = System.getenv("MQTT_BROKER_HOST");
    private static String PORT = System.getenv("MQTT_BROKER_PORT");
    private static String TOPIC = System.getenv("MQTT_BADGE_TOPIC");

    private static final Logger logger = LogManager.getLogger(BadgeSensorMock.class);

    public static void main(String[] args){
        if (HOST == null || PORT == null || TOPIC == null) {
            logger.error("Environment variables for MQTT broker are not set.");
            return;
        }

        String brokerUrl = "ssl://" + HOST + ":" + PORT;
        String clientId = "BadgeSensorMockClient";
        List<DoorDTO> doors = loadDoors();

        try {
            //logger.info("Before connnection");
            //System.out.println("Before connnection");
            IMqttClient mqttClient = new MqttClient(brokerUrl, clientId);
            MqttConnectOptions options = new MqttConnectOptions();

            // TLS sans vérification de certificat ET sans vérification hostname
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new javax.net.ssl.X509TrustManager[]{
                    new javax.net.ssl.X509TrustManager() {
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[0]; }
                    }
            }, new java.security.SecureRandom());

            options.setSocketFactory(sslContext.getSocketFactory());

            options.setHttpsHostnameVerificationEnabled(false);

            options.setConnectionTimeout(60);
            options.setKeepAliveInterval(60);
            options.setAutomaticReconnect(true);
            //logger.info("After creation");
            mqttClient.connect(options);
            //logger.info("After connnection");
            //System.out.println("After connnection");
            Random random = new Random();
            while (true) {
                long studentId = random.nextInt(100) + 1;
                int doorId = random.nextInt(4) + 1;
                String doorName = doors.get(doorId - 1).name();
                String payload = "{\"studentId\":" + studentId + ",\"doorId\":" + doorId + ",\"doorName\":\"" + doorName + "\"}";
                MqttMessage message = new MqttMessage(payload.getBytes());
                mqttClient.publish(TOPIC, message);
                logger.warn("Published message: " + payload + " to topic: " + TOPIC);
                System.out.println("Published message: " + payload + " to topic: " + TOPIC);
                Thread.sleep(5000);
            }
        }
        catch (Exception e) {
            logger.error("Error in BadgeSensorMock: ", e);
        }
    }

    public static List<DoorDTO> loadDoors() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return List.of(mapper.readValue(
                    BadgeSensorMock.class.getResourceAsStream("/doors.json"),
                    DoorDTO[].class
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
