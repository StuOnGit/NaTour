package com.example.natour.dao;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.natour.data.Compilation;
import com.example.natour.data.NaPath;
import com.example.natour.interfacce.FailListener;

import java.util.function.Consumer;

public class IdDao {
    private final static String TAG = "IdDao";
    String baseUrl = "https://n7zth3ld72.execute-api.us-east-1.amazonaws.com";
    public void getIdPath(NaPath naPath, Context context, Consumer<Integer> onSuccess, FailListener onError){
        RequestQueue queue = Volley.newRequestQueue(context);
        String routeUrl = "/getPathId";
        String firstParam = "?namePath=" + naPath.getPathName();
        String emailUser = "&emailUtente=" + naPath.getEmailCreatore();
        String params = firstParam + emailUser;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl + routeUrl + params,
                response -> {
                        onSuccess.accept(Integer.valueOf(response));
                },
                error -> {
                    Log.d(TAG, error.getLocalizedMessage());
                    onError.onHTTPError(error.getMessage());
                });
        queue.add(stringRequest);
    }


    public void getIdCompilation(Compilation compilation, Context context, Consumer<Integer> onSuccess, FailListener onError){
        RequestQueue queue = Volley.newRequestQueue(context);
        String routeUrl = "/getCompilationId";
        String firstParam = "?email=" + compilation.getEmailCreatore();
        String nome = "&nome=" + compilation.getName();
        String params = firstParam + nome;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl + routeUrl + params,
                response -> {
                    onSuccess.accept(Integer.valueOf(response));
                },
                error -> {
                    Log.d(TAG, String.valueOf(error.networkResponse));
                    onError.onHTTPError(error.getMessage());
                });
        queue.add(stringRequest);
    }
}
