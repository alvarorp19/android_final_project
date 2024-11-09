package dte.masteriot.mdp.listofitems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SecondActivity extends AppCompatActivity implements JSONParsing{

    private static final String SECOND_ACTIVITY_TAG = "SECOND_ACTIVITY_TAG";
    private static final String LOADWEB_SECOND_ACTIVITY_TAG = "LOADWEB_SECOND_ACTIVITY_TAG";

    private static final String HANDLER_KEY_JSON = "jsonInfo";

    private Button returnButton;
    private Button outboundButton;

    private String url_line = "https://vitesia.mytrama.com/emtusasiri/lineas/lineas/"; //info about all line stops
    private String url_line_trajectory = "https://vitesia.mytrama.com/emtusasiri/trayectos/trayectos/";

    private ArrayList<Integer> IdtrayectosLine = new ArrayList<>(); //here we are going to store all idtrayectos field forn a specific line
    private String content = ""; //web content

    static final String CONTENT_TYPE_JSON = "application/json";

    private ExecutorService es;

    // Define the handler that will receive the messages from the background thread that processes the HTML request:
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // message received from background thread: load complete (or failure)
            String string_result;
            super.handleMessage(msg);
            Log.d(LOADWEB_SECOND_ACTIVITY_TAG, "Message received from background thread");

            Bundle data = msg.getData();  // Obtén el Bundle asociado al Message

            //All lines
            if (data.containsKey(HANDLER_KEY_JSON)) {
                if ((string_result = msg.getData().getString(HANDLER_KEY_JSON)) != null) {
                    content = string_result;
                    Log.d(LOADWEB_SECOND_ACTIVITY_TAG, "Contenido web recibido en el hilo secundario UI" + content);

                    JSONParseALine(content,IdtrayectosLine);

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Get the text to be shown from the calling intent and set it in the layout
        Intent inputIntent = getIntent();
        String lineSelected = inputIntent.getStringExtra(MyOnItemActivatedListener.EXTRA_INFO_TO_SECOND_ACTIVITY);

        //filling line URL
        url_line = url_line + lineSelected;

        //buttons initialization
        outboundButton = findViewById(R.id.button1);
        returnButton = findViewById(R.id.button2);

        outboundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(SECOND_ACTIVITY_TAG, "OUTBOUND button pressed");
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(SECOND_ACTIVITY_TAG, "RETURN button pressed");
            }
        });

        //initializes the executor for background threads
        es = Executors.newSingleThreadExecutor();

        //get line information from URL (this will retrieves a JON file)
        loadSpecificLine();

    }



    private void loadSpecificLine() {

        // Execute the loading task in background in order to get the JSON with all information lines:
        LoadURLContents loadURLContents = new LoadURLContents(handler, CONTENT_TYPE_JSON, url_line);
        es.execute(loadURLContents);
    }

}