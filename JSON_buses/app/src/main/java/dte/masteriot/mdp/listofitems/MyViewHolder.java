package dte.masteriot.mdp.listofitems;

import static dte.masteriot.mdp.listofitems.R.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public class MyViewHolder extends RecyclerView.ViewHolder {

    // Holds references to individual item views
    TextView titleNumber;
    TextView subtitle;
    LinearLayout elementLayout;

    private static final String MYVIEWHOLDERTAG = "MyViewHolder";

    private static String ID_CALLER_MAIN_ACTIVITY = "MainActivity";
    private static String ID_CALLER_THIRD_ACTIVITY = "ThirdActivity";

    private Context context;
    private String caller_activity = "";

    public MyViewHolder(View itemView,Context context) {
        super(itemView);

        int name_position;
        String caller_activity_full_name = ((Activity) context).getClass().getName();

        subtitle = itemView.findViewById(R.id.subtitle);
        titleNumber = itemView.findViewById(R.id.lineNumber);
        elementLayout =  itemView.findViewById(R.id.item_layout);
        this.context = context;
        name_position = caller_activity_full_name.lastIndexOf(".");
        this.caller_activity = caller_activity_full_name.substring(name_position + 1);

        Log.d(MYVIEWHOLDERTAG,this.caller_activity);
    }

    @SuppressLint("ResourceAsColor")
    void bindValues(Item item, Boolean isSelected) {
        // give values to the elements contained in the item view.

        subtitle.setText(item.getSubtitle());
        titleNumber.setText(item.getTitle());
        elementLayout.setBackground(item.getDrawable());

//        if(isSelected) {
//            //subtitle.setTextColor(Color.BLUE);
//        } else {
//            //subtitle.setTextColor(Color.BLACK);
//        }
    }

}