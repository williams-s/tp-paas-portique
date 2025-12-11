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

import javax.net.ssl.SSLContext;

public class EntranceDoorLock {
    private static final Logger logger = LogManager.getLogger(EntranceDoorLock.class);

    private static String HOST = System.getenv("MQTT_BROKER_HOST");
    private static String PORT = System.getenv("MQTT_BROKER_PORT");
    private static String TOPIC = System.getenv("MQTT_DOOR_TOPIC");

    public static void main(String[] args) {
        if (HOST == null || PORT == null || TOPIC == null) {
            System.out.println("Environment variables for MQTT broker are not set.");
            return;
        }

        String brokerUrl = "ssl://" + HOST + ":" + PORT;
        String clientId = "EntranceDoorLockClient";

        try {
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

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection to MQTT broker lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage msg) throws Exception {
                    String message = new String(msg.getPayload());
                    ObjectMapper objectMapper = new ObjectMapper();
                    boolean shouldOpen = objectMapper.readTree(message).get("shouldOpen").asBoolean();
                    String doorId = objectMapper.readTree(message).get("doorId").asText();
                    String doorName = objectMapper.readTree(message).get("doorName").asText();
                    if (shouldOpen) {
                        //System.out.println("Door n°" + doorId + " opened");
                        System.out.println("Door " + doorName + " opened");
                        Thread.sleep(3000);
                        System.out.flush();
                        //System.out.println("Door n°" + doorId + " closed");
                        System.out.println("Door " + doorName + " closed");
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Delivery complete for token: " + token);
                }
            });

            mqttClient.connect(options);
            mqttClient.subscribe(TOPIC);
            System.out.println("Subscribed to topic " + TOPIC + " on broker " + brokerUrl);
            while(true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("Error in EntranceDoorLock: " + e.getMessage());
        }
    }
}