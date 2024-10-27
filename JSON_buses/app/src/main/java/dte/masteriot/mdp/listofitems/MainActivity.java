// Parts of the code of this example app have ben taken from:
// https://enoent.fr/posts/recyclerview-basics/
// https://developer.android.com/guide/topics/ui/layout/recyclerview

package dte.masteriot.mdp.listofitems;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    // App-specific dataset:
    private Dataset dataset;

    private RecyclerView recyclerView;
    private SelectionTracker<Long> tracker;
    private MyOnItemActivatedListener myOnItemActivatedListener;

    private final static int TYPE_CAMARAS_LIST = 0;
    private final static int TYPE_GARDENS_LIST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            // Restore state related to selections previously made
            tracker.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        tracker.onSaveInstanceState(outState); // Save state about selections.
    }

    // ------ Buttons' on-click listeners ------ //

    public void listLayout(View view) {
        //mostrar contenido del XML

        dataset = new Dataset(this,TYPE_CAMARAS_LIST);
        myOnItemActivatedListener =
                new MyOnItemActivatedListener(this, dataset);
        // Prepare the RecyclerView:
        recyclerView = findViewById(R.id.recyclerView);
        MyAdapter recyclerViewAdapter = new MyAdapter(dataset,this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Choose the layout manager to be set.
        // some options for the layout manager:  GridLayoutManager, LinearLayoutManager, StaggeredGridLayoutManager
        // by default, a linear layout is chosen:
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Selection tracker (to allow for selection of items):
        tracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new MyItemKeyProvider(ItemKeyProvider.SCOPE_MAPPED, recyclerView),
//                new StableIdKeyProvider(recyclerView), // This caused the app to crash on long clicks
                new MyItemDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(myOnItemActivatedListener)
                .build();
        recyclerViewAdapter.setSelectionTracker(tracker);

    }


    public void listLayout2(View view) {
        //mostrar contenido del XML

        dataset = new Dataset(this,TYPE_GARDENS_LIST);
        myOnItemActivatedListener =
                new MyOnItemActivatedListener(this, dataset);
        // Prepare the RecyclerView:
        recyclerView = findViewById(R.id.recyclerView);
        MyAdapter recyclerViewAdapter = new MyAdapter(dataset,this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Choose the layout manager to be set.
        // some options for the layout manager:  GridLayoutManager, LinearLayoutManager, StaggeredGridLayoutManager
        // by default, a linear layout is chosen:
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Selection tracker (to allow for selection of items):
        tracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new MyItemKeyProvider(ItemKeyProvider.SCOPE_MAPPED, recyclerView),
//                new StableIdKeyProvider(recyclerView), // This caused the app to crash on long clicks
                new MyItemDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(myOnItemActivatedListener)
                .build();
        recyclerViewAdapter.setSelectionTracker(tracker);

    }

    public void gridLayout(View view) {
        // Button to see in a grid fashion has been clicked:
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    public void seeCurrentSelection(View view) {
        // Button "see current selection" has been clicked:

        Iterator<Long> iteratorSelectedItemsKeys = tracker.getSelection().iterator();
        // This iterator allows to navigate through the keys of the currently selected items.
        // Complete info on getSelection():
        // https://developer.android.com/reference/androidx/recyclerview/selection/SelectionTracker#getSelection()
        // Complete info on class Selection (getSelection() returns an object of this class):
        // https://developer.android.com/reference/androidx/recyclerview/selection/Selection

        String text = "";
        while (iteratorSelectedItemsKeys.hasNext()) {
            text += iteratorSelectedItemsKeys.next().toString();
            if (iteratorSelectedItemsKeys.hasNext()) {
                text += ", ";
            }
        }
        text = "Keys of currently selected items = \n" + text;
        Intent i = new Intent(this, SecondActivity.class);
        i.putExtra("text", text);
        startActivity(i);
    }

    public void DeleteCurrentSelection(View view) {

        //eliminar todos los botones seleccionados

        Iterator<Long> iteratorSelectedItemsKeys = tracker.getSelection().iterator();

        while (iteratorSelectedItemsKeys.hasNext()) {

            //eliminacion del item
            iteratorSelectedItemsKeys.next();
            iteratorSelectedItemsKeys.remove();
        }
        //actualizar la lista
        MyAdapter myAdapter = (MyAdapter) recyclerView.getAdapter();
        myAdapter.notifyDataSetChanged();
    }




}