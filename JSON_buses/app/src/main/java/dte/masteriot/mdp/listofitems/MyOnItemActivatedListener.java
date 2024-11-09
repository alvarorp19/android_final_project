package dte.masteriot.mdp.listofitems;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;

public class MyOnItemActivatedListener implements OnItemActivatedListener<Long> {

    // This class serves to "Register an OnItemActivatedListener to be notified when an item
    // is activated (tapped or double clicked)."
    // [https://developer.android.com/reference/androidx/recyclerview/selection/OnItemActivatedListener]

    private final Context context;
    private Dataset dataset; // reference to the dataset, so that the activated item's data can be accessed if necessary
    private String lines_numbers []; // this string will contain the bus lines shorted as is being represented on the main recicler view

    public static final String EXTRA_INFO_TO_SECOND_ACTIVITY = "EXTRA_INFO_2";

    public MyOnItemActivatedListener(Context context, Dataset ds) {
        this.context = context;
        this.dataset = ds;
        this.lines_numbers = dataset.getLinesShorted();
    }

    // ------ Implementation of methods ------ //

    @SuppressLint("LongLogTag")
    @Override
    public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails itemdetails,
                                   @NonNull MotionEvent e) {
        // From [https://developer.android.com/reference/androidx/recyclerview/selection/OnItemActivatedListener]:
        // "Called when an item is "activated". An item is activated, for example,
        // when no selection exists and the user taps an item with her finger,
        // or double clicks an item with a pointing device like a Mouse."

//        Log.d(TAG, "Clicked item with position = " + itemdetails.getPosition()
//                + " and key = " + itemdetails.getSelectionKey());

//         i.putExtra("text", "Clicked item with position = " + itemdetails.getPosition()
//                + " and key = " + itemdetails.getSelectionKey());
        Intent intent = new Intent(context, SecondActivity.class);
        intent.putExtra(EXTRA_INFO_TO_SECOND_ACTIVITY,this.lines_numbers[itemdetails.getPosition()]);
        context.startActivity(intent);
        Log.d(MainActivity.SHORTCLICKTAG,"Pulsacion corta sobre la linea " + this.lines_numbers[itemdetails.getPosition()]);
        return true;
    }
}
