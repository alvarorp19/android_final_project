package dte.masteriot.mdp.listofitems;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;  // Import TextView
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.MqttClient;
import java.nio.charset.StandardCharsets;


public class Mqtt implements Runnable{

    public final static String CONNECTED = "1";

    private static String TAG = "TAG_MDPMQTT";
    String serverHost = "broker.hivemq.com";  // Replace with your Mosquitto broker's IP if different
    int serverPort = 1883;

    // Topics
    private static String newsTopic = "gijonboard/news";
    private static String stopRequestTopic = "stop/";

    // MQTT Client
    private static Mqtt3AsyncClient client;
    Handler handler = new Handler();
    Runnable publishRunnable;

    // Declare the TextView
    TextView newsTextView;

    //thread info

    private static Handler creator; //here we store the handler object created by the UI thread
    private static Message msg;
    private static Bundle msg_data;

    public enum connectionStatus {
        DISCONNECTED, CONNECTED
    }

    private  static Object lock = new Object();

    static connectionStatus status = connectionStatus.DISCONNECTED;

    //constructor 1
    Mqtt(Handler handler){

        this.creator = handler;
    }


    void createMQTTclient() {
        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier("my-mqtt-client-id")
                .serverHost(serverHost)
                .serverPort(serverPort)
                .automaticReconnectWithDefaultConfig()
                .buildAsync();
    }

    public static void connectToBroker() {
        if (client != null) {
            client.connect().whenComplete((connAck, throwable) -> {
                // Handle connection complete
                if (throwable != null) {
                    Log.d(TAG, "Problem connecting to server:");
                    Log.d(TAG, throwable.toString());
                } else {
                    Log.d(TAG, "Connected to server");

                    //changing the client status
                    status = connectionStatus.CONNECTED;

                    //Notifying UI thread that MQTT client has been connected successfully
                    //msg_data.putString(MainActivity.HANDLER_KEY_MQTT1,CONNECTED);
                    //msg.sendToTarget();

                    subscribeToNewsTopic();
                }
            });
        } else {
            Log.d(TAG, "Cannot connect client (null)");
        }
    }

    private static void subscribeToNewsTopic() {
        client.subscribeWith()
                .topicFilter(newsTopic)
                .callback(publish -> {
                    String message = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
                    Log.d(TAG, "News Message Received: " + message);

                    //sending the news to main activity
                    msg = creator.obtainMessage();
                    msg_data = msg.getData();
                    msg_data.putString(MainActivity.HANDLER_KEY_MQTT1,message);
                    msg.sendToTarget();

                    //notifying self task
                    synchronized (lock){
                        lock.notify();
                    }

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

    public static void publishStopRequest(String line, String route) {

        String message = "Stop request in line " + line + " route " + route;
        String newRequestTopic = stopRequestTopic + line + "/" + route;

        Log.d(TAG,"Publishing at " + newRequestTopic);

        client.publishWith()
                .topic(newRequestTopic)
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

//    void startPeriodicPublishing() {
//        publishRunnable = new Runnable() {
//            @Override
//            public void run() {
//                publishStopRequest();
//                handler.postDelayed(this, 5000); // Re-run every 5 seconds
//            }
//        };
//        handler.post(publishRunnable); // Start the first publish
//    }

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


    public static boolean MQTTclientIsConnected(){

        if (status == connectionStatus.CONNECTED){

            return true;

        }else{

            return false;

        }

    }

    void chargeNewHandler(Handler newHandler){

        this.handler = newHandler;


    }


    @Override
    public void run() {

        //thread routine where MQTT service will be running
        Log.d(TAG,"Executing MQTT thread");

        //if(status == connectionStatus.DISCONNECTED){

        //creating MQTT client
        createMQTTclient();
        //here we are subscribing to news topic and for this moment we are publishing info in one
        //topic in order to check MQQT functionalities
        connectToBroker();

        //}else if(status == connectionStatus.CONNECTED){

            //when the program reach this point means that the button has been pressed
            //so we publish on the topic
            //publishStopRequest();

        //}

        while(true){

            //wait until an event occurs
            synchronized (lock){

                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }


        }

    }
}
