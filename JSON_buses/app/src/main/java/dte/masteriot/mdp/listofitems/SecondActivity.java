package dte.masteriot.mdp.listofitems;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private TextView tv;
    private String url_line = "https://vitesia.mytrama.com/emtusasiri/lineas/lineas/"; //info about all line stops
    private String url_line_trajectory = "https://vitesia.mytrama.com/emtusasiri/trayectos/trayectos/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //tv = findViewById(R.id.lineSelected);

        // Get the text to be shown from the calling intent and set it in the layout
        Intent inputIntent = getIntent();
        String inputText = inputIntent.getStringExtra(MyOnItemActivatedListener.EXTRA_INFO_TO_SECOND_ACTIVITY);
        //     tv.setText(inputText);
    }
}