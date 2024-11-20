package dte.masteriot.mdp.listofitems;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;


public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    // https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter

    private static final String TAG = "TAGListOfItems, MyAdapter";

    private final Dataset dataset; // reference to the dataset
    private SelectionTracker<Long> selectionTracker; // this is set through method setSelectionTracker()

    private Context context;

    private int caller_task;

    public static final int ID_CALLER_MAIN_ACTIVITY = 0;
    public static final int ID_CALLER_THIRD_ACTIVITY = 1;

    public MyAdapter(Dataset dataset, Context context, int caller_task) {
        super();
        Log.d(TAG, "MyAdapter() called");
        this.dataset = dataset;
        this.context = context;
        this.caller_task = caller_task;
    }

    // ------ Implementation of methods of RecyclerView.Adapter ------ //

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // this method has to actually inflate the item view and return the view holder.
        // it does not give values to the elements of the view holder.
        View v = null;

        if (caller_task == ID_CALLER_MAIN_ACTIVITY){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        }else if (caller_task == ID_CALLER_THIRD_ACTIVITY){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item2, parent, false);
        }

        return new MyViewHolder(v,context);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // this method gives values to the elements of the view holder 'holder'
        // (values corresponding to the item in 'position')

        final Item item = dataset.getItemAtPosition(position);
        Long itemKey = item.getKey();
        boolean isItemSelected = selectionTracker.isSelected(itemKey);

        Log.d(TAG, "onBindViewHolder() called for element in position " + position +
                ", Selected? = " + isItemSelected);
        holder.bindValues(item, isItemSelected);
    }

    @Override
    public int getItemCount() {
        return dataset.getSize();
    }

    // ------ Other methods useful for the app ------ //

    public Long getKeyAtPosition(int pos) {
        return (dataset.getKeyAtPosition(pos));
    }

    public int getPositionOfKey(Long searchedkey) {
        //Log.d(TAG, "getPositionOfKey() called for key " + searchedkey + ", returns " + position);
        int position = dataset.getPositionOfKey(searchedkey);
        return position;
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

}