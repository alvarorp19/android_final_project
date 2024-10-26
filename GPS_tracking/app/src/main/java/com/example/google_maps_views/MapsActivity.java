package com.example.google_maps_views;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.Manifest.permission;
import android.graphics.LinearGradient;
import android.health.connect.changelog.ChangeLogTokenResponse;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.example.google_maps_views.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import  com.google.android.gms.tasks.Task.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient mFusedLocationClient;
    private CancellationTokenSource cancellationTokenSource;
    private Location currentLocation;
    private LocationCallback locationCallback;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Listeners
        Button satelliteButton = findViewById(R.id.satelliteMode);
        Button hybridButton = findViewById(R.id.hybridMode);
        Button terrainButton = findViewById(R.id.TerrainMode);
        Button mapButton = findViewById(R.id.mapMode);

        satelliteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                Log.d("GPS","MODO SATELITE ON");
            }
        });

        hybridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                Log.d("GPS","MODO HYBRID ON");
            }
        });

        terrainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                Log.d("GPS","MODO TERRAIN ON");
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Log.d("GPS","MODO NORMAL ON");
            }
        });

        //gets the current location from the GPS sensor and request permissions to use the GPS
        enableMyLocation();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        CurrentLocationRequest.Builder locationRequest = new CurrentLocationRequest.Builder();
        locationRequest.setDurationMillis(10000);
        locationRequest.setPriority( Priority.PRIORITY_HIGH_ACCURACY);
        try{
            mFusedLocationClient.getCurrentLocation( locationRequest.build(),null).addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>()  {
                // Got last known location. In some rare situations this can be null.
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        //do your thing
                        currentLocation = location;
                        LatLng Clocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(Clocation).title("Current mobile location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(Clocation));
                        Log.d("GPS_app","LOCATION GOT IT!");
                    }
                }
            }).addOnFailureListener(MapsActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("GPS_app","EXCEPTION APP : " + e);
                }
            });
        }catch (Exception e){
            Log.d("GPS_app","EXCEPCION: " + e);
        }
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
//        LatLng sydney = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Current mobile location"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */

    private void enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

//        // 2. Otherwise, request location permissions from the user.
       //PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, true);
    }

}