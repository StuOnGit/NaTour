package com.example.natour.controllers;

import android.util.Log;

import com.example.natour.data.NaPath;
import com.example.natour.enumeration.DifficultyEnum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

public class ControllerPath {

    private static final String TAG = "ControllerPath";

    public NaPath jsonToNaPath(JSONObject naPathJSON){
        NaPath naPath = null;
        try{
            String namePath = naPathJSON.getString("nome");
            String difficulty = naPathJSON.getString("difficolta");
            Double inizio_latitudine = naPathJSON.getDouble("inizio_latitudine");
            Double fine_latitudine = naPathJSON.getDouble("fine_latitudine");
            Double inizio_longitudine = naPathJSON.getDouble("inizio_longitudine");
            Double fine_longitudine = naPathJSON.getDouble("fine_longitudine");
            Integer durata = naPathJSON.getInt("durata");
            String descrizione = naPathJSON.getString("descrizione");
            Boolean accessibiltaDisabili = !naPathJSON.getString("accessibilitaDisabili").equals("0");
            JSONArray percorso_geografico = new JSONArray(naPathJSON.getString("percorso_geografico"));
            String emailCreatore = naPathJSON.getString("emailUtente");
            String region = naPathJSON.getString("region");
            String country = naPathJSON.getString("country");

            //Setting for the class NaPath
            DifficultyEnum difficultyEnum = DifficultyEnum.LOW;
            if (difficulty.equals("BASSA")){
                difficultyEnum = DifficultyEnum.LOW;
            }else if(difficulty.equals("MEDIA")){
                difficultyEnum = DifficultyEnum.AVERAGE;
            }else if(difficulty.equals("ALTA")){
                difficultyEnum = DifficultyEnum.HIGH;
            }else if(difficulty.equals("ESPERTO")){
                difficultyEnum = DifficultyEnum.EXPERT;
            }
            GeoPoint startPath = new GeoPoint(inizio_latitudine,inizio_longitudine);
            GeoPoint endPath = new GeoPoint(fine_latitudine, fine_longitudine);
            naPath = new NaPath(namePath, difficultyEnum, startPath, endPath, region, country, durata, descrizione, accessibiltaDisabili, percorso_geografico, emailCreatore);

        }catch (JSONException e){
            Log.d(TAG, e.getMessage());
        }
        return naPath;
    }

}
