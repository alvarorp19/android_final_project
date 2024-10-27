package dte.masteriot.mdp.listofitems;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;


public class MyViewHolder extends RecyclerView.ViewHolder {

    // Holds references to individual item views
    ImageView image;
    TextView subtitle;

    private static final String TAG = "TAGListOfItems, MyViewHolder";

    private Context context;

    public MyViewHolder(View itemView,Context context) {
        super(itemView);
        subtitle = itemView.findViewById(R.id.subtitle);
        this.context = context;
    }

    void bindValues(Item item, Boolean isSelected) {
        // give values to the elements contained in the item view.
        // formats the title's text color depending on the "isSelected" argument.
        //int resourceId = getResources().getIdentifier(item.getTitle(), "drawable", getPackageName());
        //image.setImageResource(resourceId);
        //int resourceImage = activity.getResources().getIdentifier(element.getImageName(), "drawable", activity.getPackageName());
        //image.setImageResource(resourceImage);
        subtitle.setText(item.getSubtitle());
        if(isSelected) {
            subtitle.setTextColor(Color.BLUE);
        } else {
            subtitle.setTextColor(Color.BLACK);
        }
    }

}