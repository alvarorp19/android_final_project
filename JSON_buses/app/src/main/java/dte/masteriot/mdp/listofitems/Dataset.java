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


public class Dataset {

    private List<Item> listofitems;
    private ArrayList<String> LinesDescriptionList = new ArrayList<>();
    private static Context context;
    private int type;
    private String content;

    //Gijon lines information
    private static int NUMBER_OF_LINES = 26;
    private static String lines_numbers []  = {"1","2","4","6","10","12","14","15","16","18","20","21","24","25","26","28","30","31","34","35","36","41","42","43","44","71"};

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
                JSONParseAllLines();
                for (int i = 0; i < LinesDescriptionList.size(); ++i) {
                    listofitems.add(new Item(lines_numbers[i], LinesDescriptionList.get(i) , (long) i, refDrawables[i]));
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

    private void JSONParseAllLines() throws JSONException, IOException {

        Log.d(MainActivity.PARSINGJSONTAG,"init parsing");
        String string_json = content;
        String special_char = "�";
        String cleanDescription = "";
        Map<String, String[]> strange_characters = Map.ofEntries(
                Map.entry("1", new String[]{"Ñ"}),
                Map.entry("2", new String[]{"Ñ"}),
                Map.entry("10", new String[]{"Í", "Ñ"}),
                Map.entry("14", new String[]{"Ñ", "Ó"}),
                Map.entry("15", new String[]{"Ñ"}),
                Map.entry("16", new String[]{"Ó"}),
                Map.entry("18", new String[]{"Ó", "Ñ"}),
                Map.entry("20", new String[]{"Ó"}),
                Map.entry("25", new String[]{"Ñ", "Ó"}),
                Map.entry("26", new String[]{"Ó"}),
                Map.entry("28", new String[]{"Ó"}),
                Map.entry("35", new String[]{"Ñ"}),
                Map.entry("71", new String[]{"Á","Ñ"})
        );

        try{
            JSONObject json_obj = new JSONObject(string_json);
            JSONObject jsonlines = json_obj.getJSONObject("lineas");
            Log.d(MainActivity.PARSINGJSONTAG,"Processing the JSON containing all lines");

            for (int i = 0; i < NUMBER_OF_LINES; i++) {
                JSONObject line = jsonlines.getJSONObject(lines_numbers[i]);
                String lineDescription = line.getString("descripcion");

                //cleaning special char
                if (lineDescription.contains(special_char)){
                    String ElementsToReplace [] = strange_characters.get(lines_numbers[i]);
                    Log.d(MainActivity.PARSINGJSONTAG,"Elementos que se van a añadir: " + ElementsToReplace.length);
                    for(int j = 0; j < ElementsToReplace.length; j++){
                        Log.d(MainActivity.PARSINGJSONTAG,"CAMBIANDO " + special_char + " -> " + ElementsToReplace[j]);
                        cleanDescription= lineDescription.replaceFirst(special_char,ElementsToReplace[j]);
                        lineDescription = cleanDescription;
                        Log.d(MainActivity.PARSINGJSONTAG,"Descripcion cambidada "+ cleanDescription);
                    }
                }

                String lineDescriptionCleanedAndTrim = lineDescription.trim();
                LinesDescriptionList.add(lineDescriptionCleanedAndTrim);
                Log.d(MainActivity.PARSINGJSONTAG,"New element (number of element = " + i + ") (line = " + lines_numbers[i] + ") appended to dataset: " + lineDescriptionCleanedAndTrim);
            }

        }catch (Exception e){
            Log.d(MainActivity.PARSINGJSONTAG,"EXCEPCION " + e);
        }

    }

}