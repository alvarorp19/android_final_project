package dte.masteriot.mdp.listofitems;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public interface JSONParsing {

    //Constants

     int NUMBER_OF_LINES = 26;

     String lines_numbers []  = {"1","2","4","6","10","12","14","15","16","18","20","21","24","25","26","28","30","31","34","35","36","41","42","43","44","71"};
     //methods

    default void JSONParseAllLines(String content, ArrayList<String> LinesDescriptionList) throws JSONException, IOException {

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
