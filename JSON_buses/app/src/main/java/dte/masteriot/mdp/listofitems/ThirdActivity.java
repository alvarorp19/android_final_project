package dte.masteriot.mdp.listofitems;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity implements JSONParsing{

    private String url_line_trajectory = "https://vitesia.mytrama.com/emtusasiri/trayectos/trayectos/";
    private String lineSelected = "0";
    private String trajectorySelected = "0";

    private String LOADWEB_THIRD_ACTIVITY_TAG = "LOADWEB_THIRD_ACTIVITY_TAG";
    private static final String THIRD_ACTIVITY_TAG = "THIRD_ACTIVITY_TAG";

    public static final String EXTRA_INFO_TO_THIRD_ACTIVITY_LINE = "EXTRA_INFO_3_LINE";
    public static final String EXTRA_INFO_TO_THIRD_ACTIVITY_TRAJECTORY = "EXTRA_INFO_3_TRAJECTORY";

    private static final String HANDLER_KEY_JSON = "jsonInfo";

    private String content;
    private Boolean TrayectorycontentHasBeenRetrieved = false;

    // Define the handler that will receive the messages from the background thread that processes the HTML request:
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // message received from background thread: load complete (or failure)
            String string_result;
            super.handleMessage(msg);
            Log.d(LOADWEB_THIRD_ACTIVITY_TAG, "Message received from background thread");

            Bundle data = msg.getData();  // Obtén el Bundle asociado al Message

            //All lines
            if (data.containsKey(HANDLER_KEY_JSON)) {
                if ((string_result = msg.getData().getString(HANDLER_KEY_JSON)) != null) {
                    content = string_result;
                    Log.d(LOADWEB_THIRD_ACTIVITY_TAG, "Contenido web recibido en el hilo secundario UI" + content);
                    TrayectorycontentHasBeenRetrieved = true;
                    //JSONParseALine(content,IdtrayectosLine);

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        // Get the trajectory selected
        Intent inputIntent = getIntent();
        lineSelected = inputIntent.getStringExtra(EXTRA_INFO_TO_THIRD_ACTIVITY_LINE);
        trajectorySelected = inputIntent.getStringExtra(EXTRA_INFO_TO_THIRD_ACTIVITY_TRAJECTORY);

        Log.d(THIRD_ACTIVITY_TAG,"LINE: " + lineSelected + "Trajectory: " + trajectorySelected);

    }


    private void loadSpecifictrayectory(String trayectoryNumber){

        // Execute the loading task in background in order to get the JSON with a specific information lines:
        url_line_trajectory = url_line_trajectory + lineSelected + "/" + trayectoryNumber;
        //LoadURLContents loadURLContents = new LoadURLContents(handler, CONTENT_TYPE_JSON, url_line_trajectory, HANDLER_KEY_JSON2);
        //es.execute(loadURLContents);
    }
}
