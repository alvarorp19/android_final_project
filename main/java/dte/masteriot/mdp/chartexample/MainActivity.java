package dte.masteriot.mdp.chartexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button lightSensorButton = findViewById(R.id.light_sensor_button);                          // Create the button object and link it to the UI element
        Button mapsButton = findViewById(R.id.maps_button);
        Button JSONButton = findViewById(R.id.json_parsing_button);

        // Set a click listener to launch GraphActivity when the button is clicked -----------------
        lightSensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // FUNCTION ONCLICK (WHAT IT IS DONE AFTER SHORT CLICKING) -----------------------------
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GraphActivity.class);   // The intent is launched,
                startActivity(intent);                                                              // to start the activity
            }
        });

        // Set a click listener to launch MapsActivity when the button is clicked ------------------
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // FUNCTION ONCLICK (WHAT IT IS DONE AFTER SHORT CLICKING) -----------------------------
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);   // The intent is launched,
                startActivity(intent);                                                              // to start the activity
            }
        });

        // Set a click listener to launch MapsActivity when the button is clicked ------------------
        JSONButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // FUNCTION ONCLICK (WHAT IT IS DONE AFTER SHORT CLICKING) -----------------------------
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JSONActivity.class);   // The intent is launched,
                startActivity(intent);                                                              // to start the activity
            }
        });
    }
}
