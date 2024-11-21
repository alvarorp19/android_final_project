package dte.masteriot.mdp.listofitems;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dte.masteriot.mdp.listofitems.databinding.ActivityThirdBinding;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.example.google_maps_views.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import  com.google.android.gms.tasks.Task.*;

public class ThirdActivity extends AppCompatActivity implements JSONParsing, OnMapReadyCallback{

    private String url_line_trajectory = "https://vitesia.mytrama.com/emtusasiri/trayectos/trayectos/";
    private String lineSelected = "0";
    private String trajectorySelected = "0";

    private String LOADWEB_THIRD_ACTIVITY_TAG = "LOADWEB_THIRD_ACTIVITY_TAG";
    private static final String THIRD_ACTIVITY_TAG = "THIRD_ACTIVITY_TAG";

    public static final String EXTRA_INFO_TO_THIRD_ACTIVITY_LINE = "EXTRA_INFO_3_LINE";
    public static final String EXTRA_INFO_TO_THIRD_ACTIVITY_TRAJECTORY = "EXTRA_INFO_3_TRAJECTORY";

    private static final String HANDLER_KEY_JSON = "jsonInfo";
    public static final String HANDLER_KEY_MQTT = "MQTTinfo";

    private static final String CONTENT_TYPE_JSON = "application/json";

    private String content;
    private Boolean TrayectorycontentHasBeenRetrieved = false;

    private ExecutorService es;

    private Dataset dataset2;

    static final int TYPE_SPECIFIC_TRAJECTORY_LIST = 1;

    private RecyclerView recyclerView;
    private SelectionTracker<Long> tracker;
    private MyOnItemActivatedListener myOnItemActivatedListener;

    private ActivityThirdBinding binding;

    private GoogleMap mMap;
    Map<Integer, LatLng> markersMap = new HashMap<>();

    private Button stopButton;

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

                    //getting maps info
                    getMarkersFromSelectedTrayectory();

                    //setting markers on maps
                    putMarkersOnMaps();

                    //setting trayectory on maps

                    joinMarkersOnMaps();

                    //running MQTT service

                    //runMQTTservice();

                }
            }
        }
    };



    Handler handler2 = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            Bundle data = msg.getData();  // Obtén el Bundle asociado al Message

            //All lines
            if(data.containsKey(HANDLER_KEY_MQTT)){

                Log.d(THIRD_ACTIVITY_TAG,"MQTT message received!");

                if ((msg.getData().getString(HANDLER_KEY_MQTT)).equals(Mqtt.CONNECTED)) {

                    Log.d(THIRD_ACTIVITY_TAG,"callback for request button has been enabled");

                    stopButton.setText("STOP REQUEST");

                    stopButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            //publishing on MQTT topic

                            Log.d(THIRD_ACTIVITY_TAG,"Stop requested button clicked");

                            //runMQTTservice();

                            //notifying user that neeeds to shake the phone in order to stop the bus

                            Toast.makeText(ThirdActivity.this, "SHAKE THE PHONE TO STOP THE BUS!!", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        }
    };

    Mqtt myMqtt = new Mqtt(handler2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_third);

        binding = ActivityThirdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get the trajectory selected
        Intent inputIntent = getIntent();
        lineSelected = inputIntent.getStringExtra(EXTRA_INFO_TO_THIRD_ACTIVITY_LINE);
        trajectorySelected = inputIntent.getStringExtra(EXTRA_INFO_TO_THIRD_ACTIVITY_TRAJECTORY);

        Log.d(THIRD_ACTIVITY_TAG,"LINE: " + lineSelected + "Trajectory: " + trajectorySelected);

        stopButton = findViewById(R.id.stopButton);

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
                new MyOnItemActivatedListener(this, dataset2,MyOnItemActivatedListener.state.ACTIVITY3);
        // Prepare the RecyclerView:
        recyclerView = findViewById(R.id.recyclerView2);
        MyAdapter recyclerViewAdapter = new MyAdapter(dataset2,this,MyAdapter.ID_CALLER_THIRD_ACTIVITY);
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        //nothing to do here now. We can't the marker here beacuse we don't have the current location yet.

        // Add a marker in the current location (mobile location)
        Log.d(THIRD_ACTIVITY_TAG,"google maps initialized");

        //Gijon bound
        LatLngBounds gijonBounds = new LatLngBounds(
                new LatLng(43.501074, -5.706608), // Suroeste
                new LatLng(43.563619, -5.604365)  // Noreste
        );

        //moving maps camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(gijonBounds, 100));

    }


    private void getMarkersFromSelectedTrayectory(){

        for (Integer i =0; i < dataset2.getTrayectoryMap().size();i++){
            String parada [] = dataset2.getTrayectoryMap().get(i);

            LatLng mapPosition = new LatLng(Double.parseDouble(parada[3]),Double.parseDouble(parada[2]));
            this.markersMap.put(i,mapPosition);
            Log.d("TERCERAACTIVIDAD",Double.parseDouble(parada[3]) + " " + Double.parseDouble(parada[2]));
        }

    }


    private void putMarkersOnMaps(){

        for (Integer i = 0; i < this.markersMap.size();i++){

            String parada [] = dataset2.getTrayectoryMap().get(i);
            mMap.addMarker(new MarkerOptions().position(this.markersMap.get(i)).title(parada[0] + " (No: "+ parada[1] + ")" + ", " + parada[6] + " minutes left"));
        }
    }


    private void joinMarkersOnMaps(){

        for (int i = 0; i < (this.markersMap.size() - 1); i++){

            //here we need to join two points

            mMap.addPolyline(new PolylineOptions()
                            .add(markersMap.get(i),markersMap.get(i + 1))//points to join
                            .width(10f)//line width
                            .color(Color.RED));


        }

    }

}

