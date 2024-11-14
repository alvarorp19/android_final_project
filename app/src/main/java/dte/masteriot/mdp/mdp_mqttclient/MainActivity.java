package dte.masteriot.mdp.mdp_mqttclient;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.MqttClient;

public class MainActivity extends AppCompatActivity {

    String TAG = "TAG_MDPMQTT";
    String serverHost = "192.168.56.1";
    int serverPort = 1883;
    String subscriptionTopic = "ubuntu/#";
    String publishingTopic = "android/topic";
    Mqtt3AsyncClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        createMQTTclient();
        connectToBroker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        disconnectFromBroker();
    }

    void createMQTTclient() {
        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier("my-mqtt-client-id")
                .serverHost(serverHost)
                .serverPort(serverPort)
                //.useSslWithDefaultConfig()
                .buildAsync();
    }

    void connectToBroker() {
        if(client != null) {
            client.connectWith()
                    //.simpleAuth()
                    //.username("")
                    //.password("".getBytes())
                    //.applySimpleAuth()
                    .send()
                    .whenComplete((connAck, throwable) -> {
                        if (throwable != null) {
                            // handle failure
                            Log.d(TAG, "Problem connecting to server:");
                            Log.d(TAG, throwable.toString());
                        } else {
                            // connected -> setup subscribes and publish a message
                            Log.d(TAG, "Connected to server");
                            subscribeToTopic();
                            publishMessage();
                        }
                    });
        } else {
            Log.d(TAG, "Cannot connect client (null)");
        }
    }

    void subscribeToTopic() {
        client.subscribeWith()
                .topicFilter(subscriptionTopic)
                .callback(publish -> {
                    String receivedMessage = new String(publish.getPayloadAsBytes());
                    Log.d(TAG, "Message received: " + receivedMessage);

                    // If the message is from the broker
                    if (receivedMessage.equals("hello from the broker")) {
                        Log.d(TAG, "Broker sent a reply: " + receivedMessage);
                    }
                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        // Handle failure to subscribe
                        Log.d(TAG, "Problem subscribing to topic:");
                        Log.d(TAG, throwable.toString());
                    } else {
                        // Handle successful subscription
                        Log.d(TAG, "Subscribed to topic");
                    }
                });
    }

    void publishMessage() {
        client.publishWith()
                .topic(publishingTopic)
                .payload("hello from android".getBytes())  // Modified message
                .send()
                .whenComplete((publish, throwable) -> {
                    if (throwable != null) {
                        // handle failure to publish
                        Log.d(TAG, "Problem publishing on topic:");
                        Log.d(TAG, throwable.toString());
                    } else {
                        // handle successful publish
                        Log.d(TAG, "Message published");
                    }
                });
    }

    void disconnectFromBroker() {
        if (client != null) {
            client.disconnect()
                    .whenComplete ((result, throwable) -> {
                        if (throwable != null) {
                            // handle failure
                            Log.d(TAG, "Problem disconnecting from server:");
                            Log.d(TAG, throwable.toString());
                        } else {
                            Log.d(TAG, "Disconnected from server");
                        }
                    });
        } else {
            Log.d(TAG, "Cannot disconnect client (null)");
        }
    }
}