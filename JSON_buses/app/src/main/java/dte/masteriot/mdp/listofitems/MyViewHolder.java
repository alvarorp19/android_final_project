package dte.masteriot.mdp.listofitems;

import static dte.masteriot.mdp.listofitems.R.*;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public class MyViewHolder extends RecyclerView.ViewHolder {

    // Holds references to individual item views
    TextView lineNumber;
    TextView subtitle;

    private static final String TAG = "TAGListOfItems, MyViewHolder";

    private Context context;

    public MyViewHolder(View itemView,Context context) {
        super(itemView);

        subtitle = itemView.findViewById(R.id.subtitle);
        lineNumber = itemView.findViewById(R.id.lineNumber);

        this.context = context;
    }

    @SuppressLint("ResourceAsColor")
    void bindValues(Item item, Boolean isSelected) {
        // give values to the elements contained in the item view.

        subtitle.setText(item.getSubtitle());
        lineNumber.setText(item.getTitle());

        if(isSelected) {
            //subtitle.setTextColor(Color.BLUE);
        } else {
            //subtitle.setTextColor(Color.BLACK);
        }
    }

}