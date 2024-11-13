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
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThirdActivity extends AppCompatActivity implements JSONParsing{

    private String url_line_trajectory = "https://vitesia.mytrama.com/emtusasiri/trayectos/trayectos/";
    private String lineSelected = "0";
    private String trajectorySelected = "0";

    private String LOADWEB_THIRD_ACTIVITY_TAG = "LOADWEB_THIRD_ACTIVITY_TAG";
    private static final String THIRD_ACTIVITY_TAG = "THIRD_ACTIVITY_TAG";

    public static final String EXTRA_INFO_TO_THIRD_ACTIVITY_LINE = "EXTRA_INFO_3_LINE";
    public static final String EXTRA_INFO_TO_THIRD_ACTIVITY_TRAJECTORY = "EXTRA_INFO_3_TRAJECTORY";

    private static final String HANDLER_KEY_JSON = "jsonInfo";

    private static final String CONTENT_TYPE_JSON = "application/json";

    private String content;
    private Boolean TrayectorycontentHasBeenRetrieved = false;

    private ExecutorService es;

    private Dataset dataset2;

    static final int TYPE_SPECIFIC_TRAJECTORY_LIST = 1;

    private RecyclerView recyclerView;
    private SelectionTracker<Long> tracker;
    private MyOnItemActivatedListener myOnItemActivatedListener;

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
                    //initializes reciclerView with trayectory info
                    generateListWithTrajectoryInfo();

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

        //mounting URL
        url_line_trajectory = url_line_trajectory + "/" + lineSelected + "/" + trajectorySelected;

        //initializes the executor for background threads
        es = Executors.newSingleThreadExecutor();

        //requesting content from created URL through HTTP
        loadSpecifictrayectory();

    }


    private void loadSpecifictrayectory(){

        // Execute the loading task in background in order to get the JSON with a specific information lines:
        LoadURLContents loadURLContents = new LoadURLContents(handler, CONTENT_TYPE_JSON, url_line_trajectory);
        es.execute(loadURLContents);
    }



    private void generateListWithTrajectoryInfo(){

        //Get information using a JSON
        dataset2 = new Dataset(this,TYPE_SPECIFIC_TRAJECTORY_LIST,content);
        myOnItemActivatedListener =
                new MyOnItemActivatedListener(this, dataset2);
        // Prepare the RecyclerView:
        recyclerView = findViewById(R.id.recyclerView2);
        MyAdapter recyclerViewAdapter = new MyAdapter(dataset2,this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Choose the layout manager to be set.
        // some options for the layout manager:  GridLayoutManager, LinearLayoutManager, StaggeredGridLayoutManager
        // by default, a linear layout is chosen:
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Selection tracker (to allow for selection of items):
        tracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new MyItemKeyProvider(ItemKeyProvider.SCOPE_MAPPED, recyclerView),
//                new StableIdKeyProvider(recyclerView), // This caused the app to crash on long clicks
                new MyItemDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(myOnItemActivatedListener)
                .build();
        recyclerViewAdapter.setSelectionTracker(tracker);
    }
}
