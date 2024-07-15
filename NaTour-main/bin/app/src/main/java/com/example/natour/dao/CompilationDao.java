package com.example.natour.dao;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.natour.controllers.ControllerPath;
import com.example.natour.data.Compilation;
import com.example.natour.data.NaPath;
import com.example.natour.interfacce.FailListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class CompilationDao {
    private final static String TAG = "CompilationDao";
    private final static String baseUrl = "https://jam1xlzht9.execute-api.us-east-1.amazonaws.com";

    public void insertCompilation(Compilation compilation, Context context, Consumer<String> onSuccess, FailListener onError){
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject bodyParams = new JSONObject();
        try{

            bodyParams.put("identificativo", JSONObject.NULL);
            bodyParams.put("nome", compilation.getName());
            bodyParams.put("emailUtente", compilation.getEmailCreatore());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String routeUrl = "/insertCompilation";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl + routeUrl,
                response -> {
                    onSuccess.accept(response);
                },
                error -> {
                    onError.onHTTPError(error.getMessage());
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
        queue.add(stringRequest);
    }


    public void insertCompilationPathRel(NaPath naPath, Compilation compilation, Context context, Consumer<String> onSuccess, FailListener onError){
        RequestQueue queue = Volley.newRequestQueue(context);
        String routeUrl = "/insertCompilationSentieroRel";
            new IdDao().getIdPath(naPath, context,
                    idPath -> {
                        new IdDao().getIdCompilation(compilation, context,
                                idCompilation -> {
                                    JSONObject bodyParams = new JSONObject();
                                    try {
                                        bodyParams.put("idCompilation", idCompilation);
                                        bodyParams.put("idSentiero", idPath);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        onError.onJSONException(e.getMessage());
                                    }
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl + routeUrl,
                                            response -> {
                                                onSuccess.accept(response);
                                            },
                                            error -> {
                                                onError.onHTTPError(error.getMessage());
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
                                });
                    },
                    new FailListener() {
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
                    });

    }

    public void getCompilationsByEmail(String email, Context context, Consumer<ArrayList<Compilation>> onSuccess, FailListener onError){
        RequestQueue queue = Volley.newRequestQueue(context);
        String routeUrl = "/getCompilationsByEmail";
        String params = "?emailUtente=" + email;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl +  routeUrl + params,
                response -> {
                    ArrayList<Compilation> compilations = new ArrayList<>();
                    try {

                        JSONArray jsonArray = new JSONArray(response);
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonCompilation = jsonArray.getJSONObject(i);
                            String name = jsonCompilation.getString("nome");
                            String emailCreatore = jsonCompilation.getString("emailUtente");
                            JSONArray jsonPercorsi = jsonCompilation.getJSONArray("percorsi");
                            Compilation compilation = new Compilation(name, emailCreatore);
                            ArrayList<NaPath> paths = new ArrayList<>();
                            for(int j = 0; j < jsonPercorsi.length(); j++){
                                NaPath naPath = new ControllerPath().jsonToNaPath(jsonPercorsi.getJSONObject(j));
                                paths.add(naPath);
                            }
                            compilation.setPaths(paths);
                            compilations.add(compilation);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        onError.onJSONException(e.getMessage());
                    }
                    onSuccess.accept(compilations);
                },
                error ->{
                        onError.onHTTPError(error.getMessage());
                });
        queue.add(stringRequest);
    }

}
