package org.cyberange;

import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

public class MqttHelper {
    private static final String BROKER_URL = "tcp://broker.hivemq.com:1883"; // Public MQTT broker URL
    private static final String CLIENT_ID = "modclient1"; // Unique client ID
    private MqttAsyncClient client;

    public MqttHelper( IMqttMessageListener listener) {
        try {
            // Initialize the MQTT client with the broker URL, client ID, and in-memory persistence
            client = new MqttAsyncClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());
            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setAutomaticReconnect(true); // Reconnect automatically if connection is lost
            options.setCleanStart(true); // Start with a clean session
            client.connect(options).waitForCompletion(); // Connect to the broker
            System.out.println("Connected to MQTT broker at " + BROKER_URL);

            client.setCallback(new MqttCallback() {

                @Override
                public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
                    System.out.println("Disconnected from MQTT broker at " + BROKER_URL);
                }

                @Override
                public void mqttErrorOccurred(MqttException e) {

                }

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("topic: " + topic);
                    System.out.println("qos: " + message.getQos());
                    System.out.println("message content: " + new String(message.getPayload()));
                    listener.messageArrived(topic, message);
       }

                @Override
                public void deliveryComplete(IMqttToken iMqttToken) {

                }

                @Override
                public void connectComplete(boolean b, String s) {

                }

                @Override
                public void authPacketArrived(int i, MqttProperties mqttProperties) {

                }
            });

        } catch (Exception e) {
            System.err.println("Failed to connect to the MQTT broker: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Publishes a message to the given MQTT topic.
     *
     * @param topic   the topic to publish to
     * @param payload the message payload
     */
    public void publish(String topic, String payload) {
        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1); // Quality of Service level 1
            client.publish(topic, message);
            System.out.println("Published message to topic: " + topic);
        } catch (Exception e) {
            System.err.println("Failed to publish message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Subscribes to a given MQTT topic with a custom message listener.
     *
     * @param topic    the topic to subscribe to
     *
     */
    public void subscribe(String topic) {
        try {
            // Create a subscription object with the topic and QoS level
            MqttSubscription subscription = new MqttSubscription(topic, 1);

            // Subscribe to the topic
            client.subscribe(topic, 1);

            // Set the message listener


        } catch (Exception e) {
            System.err.println("Failed to subscribe to topic: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Disconnects from the MQTT broker and cleans up resources.
     */
    public void disconnect() {
        try {
            if (client.isConnected()) {
                client.disconnect().waitForCompletion(); // Disconnect from the broker
                System.out.println("Disconnected from MQTT broker.");
            }
        } catch (Exception e) {
            System.err.println("Failed to disconnect from MQTT broker: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                client.close(); // Close the client
            } catch (Exception e) {
                System.err.println("Failed to close MQTT client: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Example usage of MqttHelper

    }

