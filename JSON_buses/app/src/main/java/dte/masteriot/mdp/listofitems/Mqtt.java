package dte.masteriot.mdp.listofitems;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;  // Import TextView
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.MqttClient;
import java.nio.charset.StandardCharsets;


public class Mqtt implements Runnable{

    private String TAG = "TAG_MDPMQTT";
    String serverHost = "broker.hivemq.com";  // Replace with your Mosquitto broker's IP if different
    int serverPort = 1883;

    // Topics
    String newsTopic = "GIJONBOARD/NEWS";
    String stopRequestTopic = "stop/1/1";

    // MQTT Client
    Mqtt3AsyncClient client;
    Handler handler = new Handler();
    Runnable publishRunnable;

    // Declare the TextView
    TextView newsTextView;

    void createMQTTclient() {
        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier("my-mqtt-client-id")
                .serverHost(serverHost)
                .serverPort(serverPort)
                .automaticReconnectWithDefaultConfig()
                .buildAsync();
    }

    void connectToBroker() {
        if (client != null) {
            client.connect().whenComplete((connAck, throwable) -> {
                // Handle connection complete
                if (throwable != null) {
                    Log.d(TAG, "Problem connecting to server:");
                    Log.d(TAG, throwable.toString());
                } else {
                    Log.d(TAG, "Connected to server");
                    subscribeToNewsTopic();
                    startPeriodicPublishing(); // Start periodic publishing
                }
            });
        } else {
            Log.d(TAG, "Cannot connect client (null)");
        }
    }

    void subscribeToNewsTopic() {
        client.subscribeWith()
                .topicFilter(newsTopic)
                .callback(publish -> {
                    String message = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
                    Log.d(TAG, "News Message Received: " + message);

                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        Log.d(TAG, "Problem subscribing to news topic:");
                        Log.d(TAG, throwable.toString());
                    } else {
                        Log.d(TAG, "Subscribed to news topic");
                    }
                });
    }

    void publishStopRequest() {
        String message = "Stop request from Android";
        client.publishWith()
                .topic(stopRequestTopic)
                .payload(message.getBytes(StandardCharsets.UTF_8))
                .send()
                .whenComplete((publish, throwable) -> {
                    if (throwable != null) {
                        Log.d(TAG, "Problem publishing stop request:");
                        Log.d(TAG, throwable.toString());
                    } else {
                        Log.d(TAG, "Stop request published");
                    }
                });
    }

    void startPeriodicPublishing() {
        publishRunnable = new Runnable() {
            @Override
            public void run() {
                publishStopRequest();
                handler.postDelayed(this, 5000); // Re-run every 5 seconds
            }
        };
        handler.post(publishRunnable); // Start the first publish
    }

    void stopPeriodicPublishing() {
        if (publishRunnable != null) {
            handler.removeCallbacks(publishRunnable);
        }
    }

    void disconnectFromBroker() {
        if (client != null) {
            client.disconnect()
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
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


    @Override
    public void run() {

        //thread routine where MQTT service will be running
        Log.d(TAG,"Executing MQTT thread");
        createMQTTclient();
        //here we are subscribing to news topic and for this moment we are publishing info in one
        //topic in order to check MQQT functionalities 
        connectToBroker();

    }
}
