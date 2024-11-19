package dte.masteriot.mdp.listofitems;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParser;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;


public class Dataset implements JSONParsing{

    private List<Item> listofitems;
    private ArrayList<String> LinesDescriptionList = new ArrayList<>();
    Map<Integer, String []> TrayectoryMap = new HashMap<>();
    private static Context context;
    private int type;
    private String content;

    //drawables references sorted by JSON lines order
    private Drawable refDrawables [];

    Dataset(Context context, int typeList, String content) {

        listofitems = new ArrayList<>();
        this.context = context;
        this.type = typeList;
        this.content = content; //JSON string to be processed
        //initializes drawables references
        Drawable drawables_lines [] = {

                    ContextCompat.getDrawable(context,R.drawable.line1_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line2_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line4_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line6_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line10_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line12_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line14_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line15_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line16_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line18_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line20_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line21_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line24_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line25_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line26_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line28_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line30_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line31_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line34_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line35_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line36_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line41_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line42_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line43_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line44_bus_icon),
                    ContextCompat.getDrawable(context,R.drawable.line71_bus_icon)
        };

        this.refDrawables = drawables_lines;

         if(this.type == MainActivity.TYPE_ALL_LINES_LIST){//show all lines
            try {
                JSONParseAllLines(this.content, this.LinesDescriptionList);
                for (int i = 0; i < LinesDescriptionList.size(); ++i) {
                    listofitems.add(new Item(lines_numbers[i], LinesDescriptionList.get(i) , (long) i, refDrawables[i]));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else if (this.type == ThirdActivity.TYPE_SPECIFIC_TRAJECTORY_LIST){
            try {
                JSONParseATrajectory(this.content, this.TrayectoryMap);
                for (Integer i = 0; i < TrayectoryMap.size(); ++i) {
                    String parada [] = TrayectoryMap.get(i);
                    listofitems.add(new Item(parada[1], parada[0] , (long) i, drawables_lines[getLinePositionInJson(parada[8])]));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    int getSize() {
        return listofitems.size();
    }

    Item getItemAtPosition(int pos) {
        return listofitems.get(pos);
    }

    Long getKeyAtPosition(int pos) {
        return (listofitems.get(pos).getKey());
    }

    public int getPositionOfKey(Long searchedkey) {
        // Look for the position of the Item with key = searchedkey.
        // The following works because in Item, the method "equals" is overriden to compare only keys:
        //int position = listofitems.indexOf(new Item("placeholder", "placeholder", searchedkey),1);
        int position = 0;
        //Log.d(TAG, "getPositionOfKey() called for key " + searchedkey + ", returns " + position);
        return position;
    }

    void removeItemAtPosition(int i) {
        listofitems.remove(i);
    }

    void removeItemWithKey(Long key) {
        removeItemAtPosition(getPositionOfKey(key));
    }

//    private void getStoredImages() throws IllegalAccessException {
//
//        Field[] drawables = R.drawable.class.getFields();
//
//        for (Field field : drawables) {
//
//            if (field.getName().startsWith("animal_")) { // Cambia esto a tu convención de nombres
//                int id = field.getInt(null);
//                foundImages.add(field.getName());
//                Log.d("DrawableResource", "Encontrada imagen: " + id);
//            }
//
//        }
//
//    }

    public String [] getLinesShorted(){

        return lines_numbers;
    }


    public Map<Integer, String []> getTrayectoryMap(){

        return this.TrayectoryMap;
    }
    

}