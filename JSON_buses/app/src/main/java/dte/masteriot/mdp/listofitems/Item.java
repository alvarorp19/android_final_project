package dte.masteriot.mdp.listofitems;

import android.graphics.drawable.Drawable;

public class Item {
    // This class contains the actual data of each item of the dataset

    private String title;
    private String subtitle;
    private Long key; // In this app we use keys of type Long
    private Drawable refDrawable;

    Item(String title, String subtitle, Long key, Drawable refDrawable) {
        this.title = title;
        this.subtitle = subtitle;
        this.key = key;
        this.refDrawable = refDrawable;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public Long getKey() {
        return key;
    }

    public Drawable getDrawable() {
        return refDrawable;
    }

    // We override the "equals" operator to only compare keys
    // (useful when searching for the position of a specific key in a list of Items):
    public boolean equals(Object other) {
        return this.key == ((Item) other).getKey();
    }

}