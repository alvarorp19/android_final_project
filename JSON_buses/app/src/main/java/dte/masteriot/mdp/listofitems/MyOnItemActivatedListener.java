package dte.masteriot.mdp.listofitems;

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

    //defines for uris

    private static final String ARDILLA = "https://es.wikipedia.org/wiki/Ardilla";
    private static final String CERDA = "https://es.wikipedia.org/wiki/Sus_scrofa_domestica";
    private static final String COLIBRI = "https://es.wikipedia.org/wiki/Colibri";
    private static final String CONEJO = "https://es.wikipedia.org/wiki/Oryctolagus_cuniculus";
    private static final String ERIZO = "https://es.wikipedia.org/wiki/Erinaceinae";
    private static final String MAPACHE = "https://es.wikipedia.org/wiki/Procyon";
    private static final String PANDA = "https://es.wikipedia.org/wiki/Ailuropoda_melanoleuca";
    private static final String TUCAN = "https://es.wikipedia.org/wiki/Ramphastidae";

    private static final String TAG = "TAGListOfItems, MyOnItemActivatedListener";

    private final Context context;
    private Dataset dataset; // reference to the dataset, so that the activated item's data can be accessed if necessary

    String [] Uris = {ARDILLA,CERDA,COLIBRI,CONEJO,ERIZO,MAPACHE,PANDA,TUCAN};

    public MyOnItemActivatedListener(Context context, Dataset ds) {
        this.context = context;
        this.dataset = ds;
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

//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Uris[itemdetails.getPosition()]));
//        context.startActivity(intent);
////        i.putExtra("text", "Clicked item with position = " + itemdetails.getPosition()
////                + " and key = " + itemdetails.getSelectionKey());
//        context.startActivity(intent);
        return true;
    }
}
