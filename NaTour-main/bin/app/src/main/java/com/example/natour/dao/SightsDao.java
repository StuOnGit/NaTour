package com.example.natour.dao;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.natour.data.NaPath;
import com.example.natour.data.Sights;
import com.example.natour.enumeration.SightTypeEnum;
import com.example.natour.interfacce.FailListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class SightsDao {

    private static final String baseUrl = "https://2g9qh1tkd7.execute-api.us-east-1.amazonaws.com";
    private static final String TAG = "SightsDao";

    public void getSightsByPath(NaPath path, Context context, Consumer<ArrayList<Sights>> onSuccess, FailListener onError){
        String urlRouting = "/getSightsByPath";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        new IdDao().getIdPath(path, context,
                id -> {
                    String firstParam = "?idSentiero=" + id;
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl + urlRouting + firstParam,
                            response -> {
                                try{
                                    JSONArray jsonArray = new JSONArray(response);
                                    ArrayList<Sights> sights = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        String name = jsonObject.getString("nome");
                                        Double longitudine = jsonObject.getDouble("longitudine");
                                        Double latitudine = jsonObject.getDouble("latitudine");
                                        GeoPoint location = new GeoPoint(latitudine, longitudine);
                                        String sightTypeStr = jsonObject.getString("tipologia");
                                        SightTypeEnum sightType = toTypeEnum(sightTypeStr);
                                        Sights sight = new Sights(name, location, sightType, path);
                                        sights.add(sight);
                                    }
                                    onSuccess.accept(sights);
                                }catch (JSONException e){
                                    Log.d(TAG, e.getMessage());
                                    onError.onJSONException(e.getMessage());
                                }

                            },
                            error -> {
                                Log.d(TAG, error.getMessage());
                                onError.onHTTPError(error.getMessage());
                            });
                    requestQueue.add(stringRequest);
                }, new FailListener() {
                    @Override
                    public void onAuthFail(String msg) {
                            onError.onAuthFail(msg);
                    }
                    @Override
                    public void onDBFail(String msg) {
                        onError.onDBFail(msg);
                    }

                    @Override
                    public void onLocalProblem(String msg) {
                        onError.onAuthFail(msg);
                    }

                    @Override
                    public void onJSONException(String msg) {
                        onError.onJSONException(msg);
                    }

                    @Override
                    public void onHTTPError(String msg) {
                        onError.onHTTPError(msg);
                    }
                });

    }

    public void insertSight (Sights sight, Integer idSentiero,  Context context, String emailUser, Consumer<String> onSuccess, FailListener onError){
        String urlRouting = "/insertSight";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject bodyParams = new JSONObject();

        try {
            bodyParams.put("latitudine", sight.getLocation().getLatitude());
            bodyParams.put("longitudine", sight.getLocation().getLongitude());
            bodyParams.put("tipologia", sight.getSightType().toString());
            bodyParams.put("nome", sight.getName());
            bodyParams.put("emailUtente", emailUser);
            bodyParams.put("idSentiero", idSentiero);

        }catch (JSONException e){
            onError.onJSONException(e.getMessage());
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl + urlRouting,
                response -> {
                    Log.d(TAG,response);
                        onSuccess.accept(response);
                },
                error -> {
                    Log.d(TAG, error.getMessage());
                    onError.onHTTPError(error.getMessage());
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


    private SightTypeEnum toTypeEnum(String typologyPointOfInterest) {
        if(typologyPointOfInterest.equals("GROTTA")){
            return SightTypeEnum.GROTTA;
        }else if(typologyPointOfInterest.equals("SORGENTE")){
            return SightTypeEnum.SORGENTE;
        }else if(typologyPointOfInterest.equals("PUNTO_PANORAMICO")){
            return SightTypeEnum.PUNTO_PANORAMICO;
        }else if(typologyPointOfInterest.equals("AREA_PIC_NIC")){
            return SightTypeEnum.AREA_PIC_NIC;
        }else if(typologyPointOfInterest.equals("BAITA")){
            return SightTypeEnum.BAITA;
        }else if(typologyPointOfInterest.equals("FLORA")){
            return SightTypeEnum.FLORA;
        }else if(typologyPointOfInterest.equals("LUOGO_DI_INTERESSE_ARTISTICO")){
            return SightTypeEnum.LUOGO_DI_INTERESSE_ARTISTICO;
        }else {
            return SightTypeEnum.ALTRO;
        }
    }
}

