package com.example.natour.dao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;



import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.natour.data.NaPath;
import com.example.natour.data.Photo;
import com.example.natour.interfacce.FailListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class PhotoDao {

    private static final String TAG = "PhotoDao";

    private final String baseUrl = "https://v9pqbuxlh9.execute-api.us-east-1.amazonaws.com";

    public void insertPhoto(@NotNull Integer idPath, @NotNull String emailUser, @NotNull Bitmap photoBitMap, Context context, Consumer<String> onSuccess, FailListener onError) {
        String urlRouting = "/insertPhoto";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject bodyParams = new JSONObject();
        try {
            bodyParams.put("identificativo", JSONObject.NULL);
            bodyParams.put("idSentiero", idPath);
            bodyParams.put("emailUtente", emailUser);
            bodyParams.put("file", imageToString(photoBitMap));
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
                    return bodyParams.toString().getBytes("utf-8");
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

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)){
            Log.d(TAG, "Compressione della photo avvenuta con successo");
        }
        byte[] imageBytes = outputStream.toByteArray();

        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        if(encodedImage.contains("png")){
            Log.d(TAG, "File png, corretto");
        }else{
            Log.d(TAG, "non è un file png, scorretto");
        }
        return encodedImage;
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
//            encodedString = encodedString.replace("\n", "");
          /*  String temp = encodedString.replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,","");
            String pureBase64Encoded = temp.substring(encodedString.indexOf(",") + 1);
            byte [] encodeByte = Base64.decode(pureBase64Encoded.getBytes(),Base64.URL_SAFE);
            */

            byte[] decodedString = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            return bitmap;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void getPhotosByEmail(String emailCreatore, Context context, Consumer<ArrayList<Photo>> onSuccess, FailListener onError) {
        String urlRouting = "/getPhotosByEmail";
        RequestQueue queue =  Volley.newRequestQueue(context);
        String emailParam = "?email=" + emailCreatore;
        String params =  emailParam;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl + urlRouting + params,
                response -> {
                    Log.d(TAG, "Get photos with email: " + emailCreatore + "\nResponse: " + response);
                    ArrayList<Photo> photos = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for(int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String s3_key = jsonObject.getString("s3_key");
                                Photo photo = new Photo(s3_key, emailCreatore);
                                photos.add(photo);
                        }
                        onSuccess.accept(photos);
                    }catch (JSONException e){
                            Log.d(TAG, "An error occurred:" + e.getLocalizedMessage());
                            onError.onJSONException(e.getMessage());
                    }
                },
                error -> {
                    Log.d(TAG, "An error occurred:" + error.getLocalizedMessage());
                    onError.onHTTPError(error.getMessage());
                });
        queue.add(stringRequest);
    }

    public void getPhotosByPath(NaPath naPath, Context context, Consumer<ArrayList<Photo>> onSuccess, FailListener onError) {
        String urlRouting = "/getPhotosByIdPath";
        RequestQueue queue =  Volley.newRequestQueue(context);
        String pathName = "?pathName=" + naPath.getPathName();
        String email = "&email=" +  naPath.getEmailCreatore();
        String params =  pathName + email;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl + urlRouting + params,
                response -> {
                    ArrayList<Photo> photos = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for(int i = 0;  i < jsonArray.length(); i++){
                            JSONObject jsonPhoto = jsonArray.getJSONObject(i);
                            String s3_key = jsonPhoto.getString("s3_key");
                            String emailUtente = jsonPhoto.getString("emailUtente");
                            Photo photo = new Photo(s3_key,  emailUtente);
                            photos.add(photo);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    onSuccess.accept(photos);
                },
                error -> {

                });
        queue.add(stringRequest);
    }

    public void getPhotoInBitmap(String PhotoKey, Context context, Consumer<Bitmap> onSuccess, FailListener onError){
        String urlRouting = "/getPhoto";

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl + urlRouting + "?key="+PhotoKey,
                response -> {
                    Bitmap bitmap = StringToBitMap(response);
                    if(bitmap == null){
                        System.out.println("bitmap null");
                    }
                    onSuccess.accept(bitmap);
                },
                error -> {
                        Log.d(TAG, error.getMessage());
                        onError.onHTTPError(error.getMessage());
                });
        queue.add(stringRequest);
    }
}
