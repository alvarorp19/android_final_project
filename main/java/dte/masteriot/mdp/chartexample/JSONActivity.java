package dte.masteriot.mdp.chartexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class JSONActivity extends AppCompatActivity {
    public static final String LOADWEBTAG = "LOAD_WEB_TAG"; // to easily filter logs
    private String threadAndClass; // to clearly identify logs
    private static final String URL_CAMERAS = "https://informo.madrid.es/informo/tmadrid/CCTV.kml";
    private static final String CONTENT_TYPE_KML = "application/vnd.google-earth.kml+xml";
    private Button btKML;
    private TextView text;
    ExecutorService es;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);

        // Build the logTag with the Thread and Class names:
        threadAndClass = "Thread = " + Thread.currentThread().getName() + ", Class = " +
                this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);

        // Get references to UI elements:
        btKML = findViewById(R.id.loadKML);
        text = findViewById(R.id.HTTPTextView);

        //  Set initial text:
        text.setText("Click button to load the contents of " + URL_CAMERAS);

        // Create an executor for the background tasks:
        es = Executors.newSingleThreadExecutor();
    }

    // Define the handler that will receive the messages from the background thread:
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // message received from background thread: load complete (or failure)
            String string_result;
            super.handleMessage(msg);
            Log.d(LOADWEBTAG, threadAndClass + ": message received from background thread");
            if((string_result = msg.getData().getString("text")) != null) {
                text.setText(string_result);
            }
            toggle_buttons(true); // re-enable the buttons when the load returns
        }
    };

    // Listener for the button:
    public void readKML(View view) {
        toggle_buttons(false); // disable the buttons until the load is complete
        text.setText("Loading " + URL_CAMERAS + "..."); // Inform the user by means of the TextView

        // Execute the loading task in background:
        dte.masteriot.mdp.chartexample.LoadURLContents loadURLContents = new dte.masteriot.mdp.chartexample.LoadURLContents(handler, CONTENT_TYPE_KML, URL_CAMERAS);
        es.execute(loadURLContents);
    }

    private void toggle_buttons(boolean state) {
        // enable or disable buttons (depending on state)
        btKML.setEnabled(state);
    }

}