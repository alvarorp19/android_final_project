// Parts of the code of this example app have ben taken from:
// https://enoent.fr/posts/recyclerview-basics/
// https://developer.android.com/guide/topics/ui/layout/recyclerview

package dte.masteriot.mdp.listofitems;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    // App-specific dataset:
    private Dataset dataset;

    private RecyclerView recyclerView;
    private SelectionTracker<Long> tracker;
    private MyOnItemActivatedListener myOnItemActivatedListener;

    //type of content to be shown at running time
    final static int TYPE_ALL_LINES_LIST = 0;

    //URLs
    final static String  URL_JSON_ALL_LINES_BUSES = "https://vitesia.mytrama.com/emtusasiri/lineas/lineas";

    //MIME types
    static final String CONTENT_TYPE_JSON = "application/json";

    //constants for debugging puporses
    final static String LOADWEBTAG = "LOADWEB";
    final static String PARSINGJSONTAG = "PARSINGJSONTAG";
    final static String SHORTCLICKTAG = "SHORTCLICKTAG";
    final static String MAINACTIVITYTAG = "MAINACTIVITYTAG";

    //Handler Keys

    final static String HANDLER_KEY_JSON = "jsonInfo";
    final static String HANDLER_KEY_MQTT1 = "MQTT1Info";

    //news textView

    TextView MQTTnews;

    //Executor
    ExecutorService es;

    //JSON content to be processed

    private String content = "";

    public Mqtt myMqtt;

    private TextView textNoConnection;
    // Define the handler that will receive the messages from the background thread that processes the HTML request:
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // message received from background thread: load complete (or failure)
            String string_result;
            super.handleMessage(msg);
            Log.d(LOADWEBTAG, "Message received from background thread");

            Bundle data = msg.getData();  // Obtén el Bundle asociado al Message

            //All lines
            if (data.containsKey(HANDLER_KEY_JSON)) {
                if ((string_result = msg.getData().getString(HANDLER_KEY_JSON)) != null) {
                    content = string_result;
                    Log.d(LOADWEBTAG, "Contenido web recibido en el hilo princial UI" + content);

                    if (content.equals("") == false){
                        textNoConnection.setText("");
                    }

                    generateListWithAllLines();
                }
            }else if (data.containsKey(HANDLER_KEY_MQTT1)) {

                Log.d(MAINACTIVITYTAG, "latest news from MQTT received");

                MQTTnews.setText(msg.getData().getString(HANDLER_KEY_MQTT1));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (savedInstanceState != null) {
//            // Restore state related to selections previously made
//            tracker.onRestoreInstanceState(savedInstanceState);
//        }
        textNoConnection = findViewById(R.id.textNoConnection1);

        MQTTnews = findViewById(R.id.textView1_2);

        //initializes the executor for background threads
        es = Executors.newSingleThreadExecutor();

        //retrieves content from all Gijon buses lines
        loadAllLines();

        //connecting with MQTT broker
        runMQTTservice();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //tracker.onSaveInstanceState(outState); // Save state about selections.
    }

    // ------ Buttons' on-click listeners ------ //


//    public void gridLayout(View view) {
//        // Button to see in a grid fashion has been clicked:
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//    }

//    public void seeCurrentSelection(View view) {
//        // Button "see current selection" has been clicked:
//
//        Iterator<Long> iteratorSelectedItemsKeys = tracker.getSelection().iterator();
//        // This iterator allows to navigate through the keys of the currently selected items.
//        // Complete info on getSelection():
//        // https://developer.android.com/reference/androidx/recyclerview/selection/SelectionTracker#getSelection()
//        // Complete info on class Selection (getSelection() returns an object of this class):
//        // https://developer.android.com/reference/androidx/recyclerview/selection/Selection
//
//        String text = "";
//        while (iteratorSelectedItemsKeys.hasNext()) {
//            text += iteratorSelectedItemsKeys.next().toString();
//            if (iteratorSelectedItemsKeys.hasNext()) {
//                text += ", ";
//            }
//        }
//        text = "Keys of currently selected items = \n" + text;
//        Intent i = new Intent(this, SecondActivity.class);
//        i.putExtra("text", text);
//        startActivity(i);
//    }
//
//    public void DeleteCurrentSelection(View view) {
//
//        //eliminar todos los botones seleccionados
//
//        Iterator<Long> iteratorSelectedItemsKeys = tracker.getSelection().iterator();
//
//        while (iteratorSelectedItemsKeys.hasNext()) {
//
//            //eliminacion del item
//            iteratorSelectedItemsKeys.next();
//            iteratorSelectedItemsKeys.remove();
//        }
//        //actualizar la lista
//        MyAdapter myAdapter = (MyAdapter) recyclerView.getAdapter();
//        myAdapter.notifyDataSetChanged();
//    }


    private void loadAllLines() {

        // Execute the loading task in background in order to get the JSON with all information lines:
        LoadURLContents loadURLContents = new LoadURLContents(handler, CONTENT_TYPE_JSON, URL_JSON_ALL_LINES_BUSES);
        es.execute(loadURLContents);
    }


    private void generateListWithAllLines(){

        //Get information using a JSON
        dataset = new Dataset(this,TYPE_ALL_LINES_LIST,content);
        myOnItemActivatedListener =
                new MyOnItemActivatedListener(this, dataset, MyOnItemActivatedListener.state.ACTIVITY1);
        // Prepare the RecyclerView:
        recyclerView = findViewById(R.id.recyclerView);
        MyAdapter recyclerViewAdapter = new MyAdapter(dataset,this,MyAdapter.ID_CALLER_MAIN_ACTIVITY);
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


    public void runMQTTservice(){

        //running MQTT services in a background thread
        this.myMqtt = new Mqtt(handler);
        es.execute(myMqtt);

    }



}