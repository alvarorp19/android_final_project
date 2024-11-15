package dte.masteriot.mdp.mdp_mqttclient;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;  // Import TextView
import androidx.appcompat.app.AppCompatActivity;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.MqttClient;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    String TAG = "TAG_MDPMQTT";
    String serverHost = "192.168.1.130";  // Replace with your Mosquitto broker's IP if different
    int serverPort = 1883;

    // Topics
    String newsTopic = "GIJONBOARD/NEWS";
    String stopRequestTopic = "GIJONBOARD/idlinea/idtrayecto";

    // MQTT Client
    Mqtt3AsyncClient client;
    Handler handler = new Handler();
    Runnable publishRunnable;

    // Declare the TextView
    TextView newsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the TextView
        newsTextView = findViewById(R.id.newsTextView);

        createMQTTclient();
        connectToBroker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPeriodicPublishing();
        disconnectFromBroker();
    }

    void createMQTTclient() {
        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier("my-mqtt-client-id")
                .serverHost(serverHost)
                .serverPort(serverPort)
                .buildAsync();
    }

    void connectToBroker() {
        if (client != null) {
            client.connectWith()
                    .send()
                    .whenComplete((connAck, throwable) -> {
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

                    // Update the TextView on the main thread
                    runOnUiThread(() -> newsTextView.setText(message));
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
}
