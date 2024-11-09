package dte.masteriot.mdp.listofitems;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadURLContents implements Runnable {
    // Class to download a text-based content (e.g. HTML, XML, JSON, ...) from a URL
    // and populate a String with it that will be sent in a Message

    Handler creator; // handler to the main activity, who creates this task
    private final String expectedContent_type;
    private String strURL;


    public LoadURLContents(Handler handler, String cnt_type, String strURL) {
        // The constructor accepts 3 arguments:
        // The handler to the creator of this object
        // The content type expected (e.g. "application/vnd.google-earth.kml+xml").
        // The URL to load.
        this.creator = handler;
        this.expectedContent_type = cnt_type;
        this.strURL = strURL;

    }

    @SuppressLint("LongLogTag")
    @Override
    public void run() {
        // initial preparation of the message to communicate with the UI Thread:
        Message msg = creator.obtainMessage();
        Bundle msg_data = msg.getData();

        //for Cam Data
        String response = ""; // This string will contain the loaded contents of a text resource
        StringBuilder textBuilder = new StringBuilder(); // textBuilder to store the contents of a text resource line by line
        HttpURLConnection urlConnection;

        // Build the string with thread and Class names (used in logs):
        String threadAndClass = "Thread = " + Thread.currentThread().getName() + ", Class = " +
                this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);

        Log.d(MainActivity.LOADWEBTAG, threadAndClass + ": run() called, starting load");

        try {
            URL url = new URL(strURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            String actualContentType = urlConnection.getContentType(); // content-type header from HTTP server
            InputStream is = urlConnection.getInputStream();

            // Extract MIME type and subtype (get rid of the possible parameters present in the content-type header
            // Content-type: type/subtype;parameter1=value1;parameter2=value2...
            if((actualContentType != null) && (actualContentType.contains(";"))) {
                Log.d(MainActivity.LOADWEBTAG, threadAndClass + ": Complete HTTP content-type header from server = " + actualContentType);
                int beginparam = actualContentType.indexOf(";", 0);
                actualContentType = actualContentType.substring(0, beginparam);
            }
            Log.d(MainActivity.LOADWEBTAG, threadAndClass + ": MIME type reported by server = " + actualContentType);

            if (expectedContent_type.equals(actualContentType)) {
                // We check that the actual content type got from the server is the expected one
                // and if it is, download text
                switch (actualContentType){

                    case MainActivity.CONTENT_TYPE_JSON:

                        InputStreamReader reader2 = new InputStreamReader(is);
                        BufferedReader in2 = new BufferedReader(reader2);
                        // We read the text contents line by line and add them to a StringBuilder:
                        String line2 = in2.readLine();

                        while (line2 != null) {
                            textBuilder.append(line2).append("\n");
                            line2 = in2.readLine();
                        }
                        response = textBuilder.toString();

                        Log.d(MainActivity.LOADWEBTAG, threadAndClass + ": load complete, sending message to UI thread");
                        if ("".equals(response) == false) {
                            msg_data.putString(MainActivity.HANDLER_KEY_JSON, response);
                        }

                        break;
                }
            } else { // content type not supported
                response = "Actual content type different from expected ("+
                        actualContentType + " vs " + expectedContent_type + ")";
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            response = e.toString();
            Log.d(MainActivity.LOADWEBTAG,"Excepcion:" + e);
        }

        msg.sendToTarget();
    }
}
