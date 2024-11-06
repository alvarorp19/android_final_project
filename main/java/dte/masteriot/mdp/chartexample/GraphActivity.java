package dte.masteriot.mdp.chartexample;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity implements SensorEventListener {

    private Sensor lightSensor;
    private SensorManager sensorManager;
    private LineChart chart;
    private LineDataSet dataSetLightSensor;
    private List<Entry> entriesLightSensor = new ArrayList<>();
    private List<String> timeLabels = new ArrayList<>();
    private static final int MAX_MEASUREMENTS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // Initialize the sensor manager and light sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(GraphActivity.this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Initialize the LineChart from XML layout
        chart = findViewById(R.id.chart);

        // Create dataset for light sensor values
        dataSetLightSensor = new LineDataSet(entriesLightSensor, "Light Sensor");
        dataSetLightSensor.setColor(Color.BLUE);
        dataSetLightSensor.setCircleColor(Color.RED);
        dataSetLightSensor.setValueTextColor(Color.WHITE);

        // Create LineData from dataset
        LineData lineData = new LineData(dataSetLightSensor);
        chart.setData(lineData);
        chart.getLegend().setTextColor(Color.WHITE);

        // Configure chart
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(timeLabels));
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);

        chart.getDescription().setTextColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.animateX(3000); // Animation to draw chart
        chart.invalidate();   // Refresh
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float lightValue = event.values[0];  // Get current light measurement

        // Get current time in "HH:mm:ss" format
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        if (entriesLightSensor.size() < MAX_MEASUREMENTS) {
            entriesLightSensor.add(new Entry(entriesLightSensor.size(), lightValue));
            timeLabels.add(currentTime);
        } else {
            entriesLightSensor.remove(0);
            timeLabels.remove(0);

            for (int i = 0; i < entriesLightSensor.size(); i++) {
                entriesLightSensor.get(i).setX(i);
            }

            entriesLightSensor.add(new Entry(MAX_MEASUREMENTS - 1, lightValue));
            timeLabels.add(currentTime);
        }

        dataSetLightSensor.notifyDataSetChanged();
        chart.getData().notifyDataChanged();
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(timeLabels));
        chart.notifyDataSetChanged();
        chart.invalidate();  // Redraw the chart
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action needed here
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(GraphActivity.this);
    }
}
