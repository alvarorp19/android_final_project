package dte.masteriot.mdp.listofitems;

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


public class Dataset {

    // This dataset is a list of Items

    private final static int TYPE_CAMARAS_LIST = 0;
    private final static int TYPE_GARDENS_LIST = 1;

    private static final String TAG = "TAGListOfItems, Dataset";
    private List<Item> listofitems;
    private final int ITEM_COUNT = 8;
    private ArrayList<String> camerasList = new ArrayList<>();
    private ArrayList<String> GardensList = new ArrayList<>();
    private Context context;
    private int type;

    Dataset(Context context,int typeList) {
        Log.d(TAG, "Dataset() called");
        listofitems = new ArrayList<>();
        this.context = context;
        this.type = typeList;
//        try {
//            this.getStoredImages();
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }

        if(this.type == TYPE_CAMARAS_LIST){
            xmlParser();
            for (int i = 0; i < camerasList.size(); ++i) {
                listofitems.add(new Item(camerasList.get(i), camerasList.get(i) , (long) i));
            }
        }else if(this.type == TYPE_GARDENS_LIST){
            try {
                JSONParser();
                for (int i = 0; i < GardensList.size(); ++i) {
                    listofitems.add(new Item(GardensList.get(i), GardensList.get(i) , (long) i));
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
        int position = listofitems.indexOf(new Item("placeholder", "placeholder", searchedkey));
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

    private void xmlParser(){

        XmlPullParserFactory parserFactory;
        try {
            Log.d("XMLPARSER","init list");
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            AssetManager assetManager = this.context.getAssets();
            InputStream is = assetManager.open("CCTV.kml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            int eventType = parser.getEventType(); // current event state of the parser
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String elementName = null;
                elementName = parser.getName(); // name of the current element
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("Data".equals(elementName)) {
                            String dataElement = parser.getAttributeValue(null,"name");
                            if (("Nombre".equals(dataElement))){
                                for (int i = 0; i<3;  i++){
                                    parser.next();//avanzamos hasta el contenido de cada param
                                }
                                String cameraURL = parser.getText(); // if next element is TEXT then element content is returned
                                camerasList.add( cameraURL );
                                Log.d("XMLPARSER","tipo" + cameraURL);
                            }
                        }

                    break;
                }
                eventType = parser.next(); // Get next parsing event
            } // while()
        } catch (Exception e) {
            Log.d("XMLPARSER","Exception arises" + e);
          }

    }

    private void JSONParser() throws JSONException, IOException {

        Log.d("JSONPARSER","init list");
        AssetManager assetManager = this.context.getAssets();
        InputStream is = assetManager.open("200761-0-parques-jardines.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String string_json = new String(buffer, "UTF-8");
        JSONObject json_obj = new JSONObject(string_json);
        JSONArray jsonArray = (JSONArray)json_obj.get("@graph");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject position = (JSONObject) jsonArray.get(i);
            GardensList.add(position.getString("title"));
        }

    }

}
