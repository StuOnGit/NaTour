package com.example.natour.dao;

import android.content.Context;
import android.util.Log;


import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.natour.data.NaPath;
import com.example.natour.data.User;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.NaPathFilters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class UserDao {
    private final String TAG = "UserDao";
    String baseUrl = "https://36z787weva.execute-api.us-east-1.amazonaws.com";

    public void reqUserbyEmail(String email, Context context, Consumer<User> onSuccess, FailListener onError){

        RequestQueue queue = Volley.newRequestQueue(context);
        String routeUrl = "/getUserByEmail";
        String params = "?email=" + email;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl + routeUrl + params,
                response -> {
                        Log.d(TAG, "Get User with email: " + email + "\nResponse: " +  response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String username = jsonObject.getString("username");
                        String name = jsonObject.getString("name");
                        String addressEmail = jsonObject.getString("email");
                        String surname = jsonObject.getString("surname");


                        User user = new User(username, name, surname, addressEmail);
                        onSuccess.accept(user);

                    } catch (JSONException exception) {
                        Log.d(TAG, "An error occurred");
                        exception.printStackTrace();
                        onError.onJSONException(exception.getMessage());
                    }
                },
                error -> {
                    Log.d(TAG, "getUserByEmail error: " + error.getLocalizedMessage());
                    onError.onHTTPError(error.getLocalizedMessage());
                });

        queue.add(stringRequest);
    }

    public void insertUser(User user, Context context, Consumer<String> onSuccess, FailListener onError){

        RequestQueue queue = Volley.newRequestQueue(context);
        String routeUrl = "/insertUser";
        JSONObject bodyParams = new JSONObject();
        try {
            bodyParams.put("name", user.getName() != null? user.getName() : JSONObject.NULL);
            bodyParams.put("surname",user.getSurname() != null? user.getSurname() : JSONObject.NULL);
            bodyParams.put("username", user.getUsername() != null? user.getUsername() : JSONObject.NULL);
            bodyParams.put("email", user.getEmail() != null? user.getEmail() : JSONObject.NULL);
        } catch (JSONException exception) {
            exception.printStackTrace();
            onError.onJSONException(exception.getMessage());
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl + routeUrl,
            response -> {
                Log.d(TAG, "Inserted User with email: " + user.getEmail() + "\nResponse: " +  response);
                onSuccess.accept(response);
            },
            error -> {
                Log.d(TAG, "An error occurred");
                onError.onHTTPError(error.getMessage());
            }){
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
    }




    public void search(String charsToSearch, Context context,  Consumer<ArrayList<User>> onSuccess, FailListener onError) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String routeUrl = "/getUser";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl + routeUrl + "?email=^" + charsToSearch,
                response -> {
                    Log.d(TAG, "Get users with email: " + charsToSearch + "\nResponse: " + response);
                    ArrayList<User> users = new ArrayList<>();
                    try{
                        JSONArray jsonArray = new JSONArray(response);
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String name = jsonObject.getString("name");
                            String surname = jsonObject.getString("surname");
                            String username = jsonObject.getString("username");
                            String email = jsonObject.getString("email");
                            User user = new User(username, name, surname, email);
                            users.add(user);
                        }
                        onSuccess.accept(users);

                    }catch (JSONException e){
                        Log.d(TAG, "An error occurred: " +  e.getLocalizedMessage());
                        onError.onJSONException(e.getMessage());
                    }

                },
                error -> {
                    Log.d(TAG, error.getLocalizedMessage());
                    onError.onHTTPError(error.getMessage());
                });


        queue.add(stringRequest);
    }


    public void insertWithFBorGoogle(User user, Context context, Consumer<String> onSuccess, FailListener onError){
        RequestQueue queue = Volley.newRequestQueue(context);
        String routeUrl = "/insertReplaceUserGoogleFacebook";
        JSONObject bodyParams = new JSONObject();
        try {
            bodyParams.put("name", user.getName() != null? user.getName() : JSONObject.NULL);
            bodyParams.put("surname",user.getSurname() != null? user.getSurname() : JSONObject.NULL);
            bodyParams.put("username", user.getUsername() != null? user.getUsername() : JSONObject.NULL);
            bodyParams.put("email", user.getEmail() != null? user.getEmail() : JSONObject.NULL);
        } catch (JSONException exception) {
            exception.printStackTrace();
            onError.onJSONException(exception.getMessage());
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl + routeUrl,
                response -> {
                    Log.d(TAG, "Inserted User with email: " + user.getEmail() + "\nResponse: " +  response);
                    onSuccess.accept(response);
                },
                error -> {
                    Log.d(TAG, "An error occurred");
                    onError.onHTTPError(error.getMessage());
                }){
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
    }
}
