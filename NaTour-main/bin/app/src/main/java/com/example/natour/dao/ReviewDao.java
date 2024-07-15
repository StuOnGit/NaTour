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
import com.example.natour.data.Review;
import com.example.natour.enumeration.DifficultyEnum;
import com.example.natour.enumeration.ValutazioneEnum;
import com.example.natour.interfacce.FailListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class ReviewDao {
    private static final String TAG = "ReviewDao";
    private final String baseUrl = "https://wg1wr0qqcb.execute-api.us-east-1.amazonaws.com";

    public void reqReviews(NaPath naPath, Context context, Consumer<ArrayList<Review>> onSuccess, FailListener onError){

        new IdDao().getIdPath(naPath, context,
                id -> {
                    String routeUrl = "/getReviews";
                    RequestQueue queue = Volley.newRequestQueue(context);
                    String firstParam = "?idSentiero=" + id.toString();
                    StringRequest stringRequest = new StringRequest(Request.Method.GET,baseUrl +  routeUrl + firstParam,
                            response -> {
                                Log.d(TAG,"Get reviews with namePath: " + naPath.getPathName());
                                ArrayList<Review> reviews = new ArrayList<>();
                                try{
                                    JSONArray jsonArray = new JSONArray(response);
                                    for(int i = 0; i < jsonArray.length(); i++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        String descrizione = jsonObject.getString("descrizione");
                                        String emailUtente = jsonObject.getString("emailUtente");
                                        Integer valutazione = jsonObject.getInt("valutazione");
                                        ValutazioneEnum valutazioneEnum = getValutazione(valutazione);
                                        Review review = new Review(descrizione,valutazioneEnum, naPath, emailUtente);
                                        reviews.add(review);
                                    }
                                    onSuccess.accept(reviews);
                                }catch (JSONException e){
                                    Log.d(TAG, e.getMessage());
                                    onError.onJSONException(e.getMessage());
                                }
                            },
                            error -> {
                                onError.onHTTPError(error.getMessage());
                            });
                    queue.add(stringRequest);
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
                        onError.onLocalProblem(msg);
                    }

                    @Override
                    public void onJSONException(String msg) {
                        onError.onJSONException(msg);
                    }

                    @Override
                    public void onHTTPError(String msg) {
                        onError.onHTTPError(msg);
                    }
                }
        );

    }

    public ValutazioneEnum getValutazione(Integer valutazione) {
        ValutazioneEnum valutazioneEnum = ValutazioneEnum.UNA_STELLA;
        switch (valutazione){
            case 2:
                valutazioneEnum = ValutazioneEnum.DUE_STELLE;
                break;
            case 3:
                valutazioneEnum = ValutazioneEnum.TRE_STELLE;
                break;
            case 4:
                valutazioneEnum = ValutazioneEnum.QUATTRO_STELLE;
                break;
            case 5:
                valutazioneEnum = ValutazioneEnum.CINQUE_STELLE;
                break;
            default:
                break;
        }
        return valutazioneEnum;
    }

    public void insertReview(Review review, Context context, Consumer<String> onSuccess, FailListener onError){

        new IdDao().getIdPath(review.getPath(), context,
                id -> {
                    String routeUrl = "/insertReview";
                    RequestQueue queue = Volley.newRequestQueue(context);
                    JSONObject bodyParams = new JSONObject();
                    try{
                        bodyParams.put("identificativo", JSONObject.NULL);
                        bodyParams.put("descrizione", review.getDescription());
                        bodyParams.put("emailUtente", review.getEmailUtente());
                        bodyParams.put("valutazione", review.getValutazione().ordinal() + 1);
                        bodyParams.put("idSentiero", id);
                    }catch (JSONException e){
                        Log.d(TAG, "JSON exception: " + e.getLocalizedMessage());
                        onError.onJSONException(e.getMessage());
                    }
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl +  routeUrl,
                            response -> {
                                Log.d(TAG, response);
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
                    queue.add(stringRequest);
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
                        onError.onLocalProblem(msg);
                    }

                    @Override
                    public void onJSONException(String msg) {
                        onError.onJSONException(msg);
                    }

                    @Override
                    public void onHTTPError(String msg) {
                        Log.d(TAG, msg);
                        onError.onHTTPError(msg);
                    }
                });

    }

    public void reqReviewsWithEmail(String email, Context context, Consumer<ArrayList<Review>> onSuccess, FailListener onError){
        RequestQueue queue = Volley.newRequestQueue(context);
        String routeUrl = "/getMyReviews";
        String firstParams = "?email=" + email;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl +  routeUrl + firstParams,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        ArrayList<Review> reviews = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonReview = jsonArray.getJSONObject(i);
                            /*
                             * Path
                             */
                            JSONObject jsonPath = jsonReview.getJSONObject("percorso");
                            String name = jsonPath.getString("nome");
                            String difficulty = jsonPath.getString("difficolta");
                            Object inizio_latitudine =  jsonPath.get("inizio_latitudine") == JSONObject.NULL? null : jsonPath.getDouble("inizio_latitudine");
                            Double fine_latitudine = jsonPath.getDouble("fine_latitudine");
                            Double inizio_longitudine = jsonPath.getDouble("inizio_longitudine");
                            Double fine_longitudine = jsonPath.getDouble("fine_longitudine");
                            Integer durata = jsonPath.getInt("durata");
                            String descrizionePath = jsonPath.getString("descrizione");
                            Boolean accessibiltaDisabili = !jsonPath.getString("accessibilitaDisabili").equals("0");
                            JSONArray percorso_geografico = new JSONArray(jsonPath.getString("percorso_geografico"));
                            String emailCreatore = jsonPath.getString("email_creatore");
                            String region = jsonPath.getString("region");
                            String country = jsonPath.getString("country");
                            //Setting for the class NaPath
                            DifficultyEnum difficultyEnum = DifficultyEnum.LOW;
                            if (difficulty.equals("LOW")){
                                difficultyEnum = DifficultyEnum.LOW;
                            }else if(difficulty.equals("AVERAGE")){
                                difficultyEnum = DifficultyEnum.AVERAGE;
                            }else if(difficulty.equals("HIGH")){
                                difficultyEnum = DifficultyEnum.HIGH;
                            }else if(difficulty.equals("EXPERT")){
                                difficultyEnum = DifficultyEnum.EXPERT;
                            }
                            GeoPoint startPath = new GeoPoint((Double) inizio_latitudine,(Double) inizio_longitudine);
                            GeoPoint endPath = new GeoPoint(fine_latitudine, fine_longitudine);
                            NaPath naPath = new NaPath(name, difficultyEnum, startPath, endPath,region, country, durata, descrizionePath, accessibiltaDisabili, percorso_geografico, emailCreatore);
                            /*
                             * Path
                             */
                            String descrizioneReview = jsonReview.getString("descrizione");
                            String emailUtente = jsonReview.getString("emailUtente");
                            ValutazioneEnum valutazione = getValutazione(jsonReview.getInt("valutazione"));
                            Review review = new Review(descrizioneReview, valutazione, naPath, emailUtente);
                            reviews.add(review);
                        }
                        onSuccess.accept(reviews);
                    }catch (JSONException e){
                        onError.onJSONException(e.getMessage());
                    }
                },
                error -> {
                    Log.d(TAG, error.getMessage());
                    onError.onHTTPError(error.getMessage());
                });
        queue.add(stringRequest);
    }



}
