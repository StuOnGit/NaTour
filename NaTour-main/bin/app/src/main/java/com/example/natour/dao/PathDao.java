package com.example.natour.dao;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.natour.controllers.ControllerPath;
import com.example.natour.data.NaCountry;
import com.example.natour.data.NaPath;
import com.example.natour.interfacce.FailListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.function.Consumer;


public class PathDao {

    private static final String TAG = "PathDao";

    private String baseUrl = "https://5y7ejh6i50.execute-api.us-east-1.amazonaws.com";

    public void insertPath(NaPath naPath, Context context, Consumer<String> onSuccess, FailListener onError){

        String urlRouting = "/insertPath";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject bodyParams = new JSONObject();
        try {

            bodyParams.put("identificativo", JSONObject.NULL);
            bodyParams.put("durata", naPath.getDurata() != null? naPath.getDurata().toString() : JSONObject.NULL);
            bodyParams.put("inizio_latitudine",naPath.getStartPath() != null? naPath.getStartPath().getLatitude() : JSONObject.NULL);
            bodyParams.put("inizio_longitudine", naPath.getStartPath() != null? naPath.getStartPath().getLongitude() :JSONObject.NULL);
            bodyParams.put("fine_latitudine", naPath.getEndPath() != null? naPath.getEndPath().getLatitude() :JSONObject.NULL);
            bodyParams.put("fine_longitudine", naPath.getEndPath() != null? naPath.getEndPath().getLongitude() :JSONObject.NULL);
            bodyParams.put("percorso_geografico", naPath.getPercorso_geografico() != null? naPath.getPercorso_geografico().toString() : JSONObject.NULL);
            bodyParams.put("difficolta", naPath.getDifficultyEnum() != null? naPath.getDifficultyEnum().toString() : JSONObject.NULL);
            bodyParams.put("descrizione", naPath.getDescrizione() != null? naPath.getDescrizione() : JSONObject.NULL);
            bodyParams.put("emailUtente", naPath.getEmailCreatore() != null? naPath.getEmailCreatore() : JSONObject.NULL);
            bodyParams.put("nome", naPath.getPathName() != null? naPath.getPathName() : JSONObject.NULL);
            bodyParams.put("accessibilitaDisabili", naPath.isAccessibilitaDisabili() != null? naPath.isAccessibilitaDisabili() : JSONObject.NULL);
            bodyParams.put("region", naPath.getRegion() != null? naPath.getRegion() : JSONObject.NULL);
            bodyParams.put("country", naPath.getCountry() != null? naPath.getCountry() : JSONObject.NULL);

        }catch (JSONException e){
            Log.d(TAG, e.getLocalizedMessage());
            onError.onJSONException(e.getMessage());
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl + urlRouting,
                response -> {
                    Log.d(TAG, "All right: " + response);
                    onSuccess.accept(response);
                },
                error -> {
                    Log.d(TAG, "All failed: " + error.getLocalizedMessage());
                    onError.onHTTPError(error.getLocalizedMessage());
                })
            {
                @Override
                public byte[] getBody() throws AuthFailureError {
                try {
                    return bodyParams == null ? null : bodyParams.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    onError.onHTTPError(uee.getMessage());
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", bodyParams.toString(), "utf-8");
                    return null;
                }
            }
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

        requestQueue.add(stringRequest);

    }

    public void reqPathsMostVoted(Integer maxPaths, Context context, Consumer<ArrayList<NaPath>> onSuccess, FailListener onError) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String routeUrl = "/getPathsMostVoted";
        String firstParam = "?maxPaths=" + maxPaths;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl + routeUrl + firstParam,
                response ->  {
                        Log.d(TAG,"Get Paths most voted with maxPaths : " + maxPaths + "\nResponse: " +  response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            ArrayList<NaPath> paths = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject= jsonArray.getJSONObject(i);
                                NaPath naPath = new ControllerPath().jsonToNaPath(jsonObject);
                                paths.add(naPath);
                            }

                            onSuccess.accept(paths);

                        }catch (JSONException exception){
                            Log.d(TAG, "An error occurred");
                            exception.printStackTrace();
                            onError.onJSONException(exception.getMessage());
                        }

                },
                error -> {
                        Log.d(TAG,"An error occurred:" +  error.getLocalizedMessage());
                        onError.onHTTPError(error.getMessage());
                });
        queue.add(stringRequest);
    }

    public void search(String nomePath, Context context, Consumer<ArrayList<NaPath>> onSuccess, FailListener onError) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String routeUrl = "/getPath";
        String params = "?nomePath=^" + nomePath;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl + routeUrl + params,
                response -> {
                    Log.d(TAG, "Get paths with name: " + nomePath + "\nResponse: " + response);
                    ArrayList<NaPath> paths = new ArrayList<>();
                    try{
                        JSONArray jsonArray = new JSONArray(response);
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject= jsonArray.getJSONObject(i);
                            NaPath naPath = new ControllerPath().jsonToNaPath(jsonObject);
                            paths.add(naPath);
                        }
                        onSuccess.accept(paths);

                    }catch (JSONException e){
                        Log.d(TAG, "An error occurred:" +  e.getLocalizedMessage());
                        onError.onJSONException(e.getMessage());
                    }

                },
                error -> {
                    Log.d(TAG, "An error occurred:" + error.getLocalizedMessage());
                    onError.onHTTPError(error.getMessage());
                });

        queue.add(stringRequest);
    }

    public void getPathsWithEmail(String email, Context context, Consumer<ArrayList<NaPath>> onSuccess, FailListener onError){
        RequestQueue queue = Volley.newRequestQueue(context);
        String routeUrl = "/getPaths";
        String firstParam = "?email=" + email;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl + routeUrl + firstParam,
                response -> {
                    Log.d(TAG,"Get Paths with email : " + email + "\nResponse: " +  response);
                    ArrayList<NaPath> paths = new ArrayList<>();
                    try{
                        JSONArray jsonArray = new JSONArray(response);
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject= jsonArray.getJSONObject(i);
                            NaPath naPath = new ControllerPath().jsonToNaPath(jsonObject);
                            paths.add(naPath);
                        }
                        onSuccess.accept(paths);

                    }catch (JSONException e){
                        Log.d(TAG, "An error occurred:" +  e.getLocalizedMessage());
                        onError.onJSONException(e.getMessage());
                    }
                },
                error -> {
                    Log.d(TAG,"An error occurred:" +  error.getLocalizedMessage());
                    onError.onHTTPError(error.getMessage());
                });
        queue.add(stringRequest);
    }

    public void getPathById(Integer idSentiero, Context context, Consumer<NaPath> onSuccess, FailListener onError){
        RequestQueue queue = Volley.newRequestQueue(context);
        String routeUrl = "/getPathById";
        String params = "?idSentiero=" + idSentiero.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl + routeUrl + params,
                response -> {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            NaPath naPath = new ControllerPath().jsonToNaPath(jsonObject);
                            onSuccess.accept(naPath);
                        }catch (JSONException e){
                            onError.onJSONException(e.getMessage());
                        }
                },
                error -> {
                    onError.onHTTPError(error.getMessage());
                });
        queue.add(stringRequest);
    }



    public void reqRegionAndCountry(Context context,Consumer<ArrayList<NaCountry>> onSuccess, FailListener onError){
            RequestQueue queue = Volley.newRequestQueue(context);
            String routeUrl = "/getRegionsAndCountries";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl+routeUrl,
                    response -> {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            ArrayList<NaCountry> countries = getNaCountriesFromJSON(jsonArray);
                            onSuccess.accept(countries);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onError.onJSONException(e.getMessage());
                        }
                    },
                    error -> {
                        onError.onHTTPError(error.getMessage());
                    });
            queue.add(stringRequest);
    }

    private ArrayList<NaCountry> getNaCountriesFromJSON(JSONArray jsonArray) throws JSONException {
        ArrayList<NaCountry> countries = new ArrayList<>();
        if(jsonArray.length() != 0){
            JSONObject primoJsonObject = jsonArray.getJSONObject(0);
            NaCountry primoCountry = new NaCountry(primoJsonObject.getString("country"));
            String region = primoJsonObject.getString("region");
            primoCountry.addRegion(region);
            countries.add(primoCountry);
            for(int i = 1; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int j = 0;
                boolean isThere = false;
                while(j < countries.size() && !isThere){
                    if(jsonObject.get("country").equals(countries.get(j).getName())){
                        isThere = true;
                    }
                    j++;
                }
                if(isThere){
                    int index = j-1;
                    countries.get(index).addRegion(jsonObject.getString("region"));
                }else{
                    NaCountry country = new NaCountry(jsonObject.getString("country"));
                    country.addRegion(jsonObject.getString("region"));
                    countries.add(country);
                }
            }
        }
        return countries;

    }
}
