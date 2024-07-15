package com.example.natour.mytests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.natour.controllers.ControllerRegistration;
import com.example.natour.dao.PathDao;
import com.example.natour.data.NaPath;
import com.example.natour.enumeration.DifficultyEnum;
import com.example.natour.interfacce.FailListener;
import com.example.natour.interfacce.PasswordError;
import com.mysql.jdbc.Connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.osmdroid.util.GeoPoint;


import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class PathDaoTest {

    Context context;
    final private String URL = "jdbc:mysql://rdsdatabasemysql.cf6dgx7xj3aa.us-east-1.rds.amazonaws.com:3306/cloudrds";
    final private String USER = "master";
    final private String PASSWORD = "NaT0urDatabase";
    @Test
    public void testInsertPath() throws JSONException, InterruptedException {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        final CountDownLatch signal = new CountDownLatch(1);

        PathDao pathDao = new PathDao();
        GeoPoint startPoint = new GeoPoint(36.95299, -121.76148);
        GeoPoint endPoint = new GeoPoint(36.95566, -121.76042);
        String StringPointsArray =  "[{\"latitude\": 36.95299, \"longitude\": -121.76148}," +
                " {\"latitude\": 36.95304, \"longitude\": -121.76135}, " +
                "{\"latitude\": 36.95303, \"longitude\": -121.76128}," +
                " {\"latitude\": 36.95306, \"longitude\": -121.76083}, " +
                "{\"latitude\": 36.95305, \"longitude\": -121.76062}, " +
                "{\"latitude\": 36.95341, \"longitude\": -121.76066}," +
                " {\"latitude\": 36.95365, \"longitude\": -121.76062}," +
                " {\"latitude\": 36.954, \"longitude\": -121.76064}," +
                "{\"latitude\": 36.95437, \"longitude\": -121.76064}," +
                " {\"latitude\": 36.95467, \"longitude\": -121.76062}," +
                " {\"latitude\": 36.95493, \"longitude\": -121.76057}, " +
                "{\"latitude\": 36.95505, \"longitude\": -121.76052}," +
                " {\"latitude\": 36.95518, \"longitude\": -121.76045}," +
                " {\"latitude\": 36.95566, \"longitude\": -121.76038}," +
                " {\"latitude\": 36.95566, \"longitude\": -121.76042}," +
                "{\"latitude\": 36.95299, \"longitude\": -121.76148}," +
                " {\"latitude\": 36.95304, \"longitude\": -121.76135}, " +
                "{\"latitude\": 36.95303, \"longitude\": -121.76128}," +
                " {\"latitude\": 36.95306, \"longitude\": -121.76083}," +
                " {\"latitude\": 36.95305, \"longitude\": -121.76062}, " +
                "{\"latitude\": 36.95341, \"longitude\": -121.76066}," +
                " {\"latitude\": 36.95365, \"longitude\": -121.76062}, " +
                "{\"latitude\": 36.954, \"longitude\": -121.76064}, " +
                "{\"latitude\": 36.95437, \"longitude\": -121.76064}," +
                " {\"latitude\": 36.95467, \"longitude\": -121.76062}," +
                " {\"latitude\": 36.95493, \"longitude\": -121.76057}, " +
                "{\"latitude\": 36.95505, \"longitude\": -121.76052}, " +
                "{\"latitude\": 36.95518, \"longitude\": -121.76045}," +
                " {\"latitude\": 36.95566, \"longitude\": -121.76038}," +
                " {\"latitude\": 36.95566, \"longitude\": -121.76042}]";


        JSONArray percorso = new JSONArray(StringPointsArray);
        System.out.println("qua ci arrivo");
        NaPath naPath = new NaPath("TestPath",
                                    DifficultyEnum.EXPERT,
                                    startPoint, endPoint,
                                    "Campania",
                                    "Italia",
                                    30,
                                    "TestDescrizione",
                                    true,
                                    percorso,
                                    "destasiofrancesco2000@gmail.com");
        pathDao.insertPath(naPath, context,
                response -> {
                    System.out.println("inserito");
                    signal.countDown();
                }, new FailListener() {
                    @Override
                    public void onAuthFail(String msg) {
                        System.out.println("onAuthFail: " +  msg);
                    }

                    @Override
                    public void onDBFail(String msg) {
                        System.out.println("onDBFail: " +  msg);
                    }

                    @Override
                    public void onLocalProblem(String msg) {
                        System.out.println("onLocalProblem: " +  msg);
                    }

                    @Override
                    public void onJSONException(String msg) {
                        System.out.println("onJSONException: " +  msg);
                    }

                    @Override
                    public void onHTTPError(String msg) {
                        System.out.println("onHTTPError: " +  msg);
                    }
                });
        signal.await();
        HashMap<String, String> values = (HashMap<String, String>) getResultFromDB();
        assertEquals(naPath.getPathName(), values.get("nome"));
        assertEquals(naPath.getEmailCreatore(),values.get("emailUtente"));
    }


    @Test
    public void useAppContext() {
        // Context of the app under test.
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.natour", context.getPackageName());
    }

    public Map<String,String> getResultFromDB(){
        Map<String, String> info = new HashMap<>();
        try (Connection connection = (Connection) DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT Sentiero.nome, Sentiero.emailUtente  FROM Sentiero Where Sentiero.nome =" +
                    "\"TestPath\" AND Sentiero.emailUtente = \"destasiofrancesco2000@gmail.com\" LIMIT 1";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                info.put("nome", resultSet.getString("nome"));
                info.put("emailUtente", resultSet.getString("emailUtente"));
            }
        } catch (Exception e) {
            Log.e("InfoAsyncTask", "Error reading school information", e);
        }
        System.out.println(info.get("nome"));
        System.out.println(info.get("emailUtente"));
        return info;
    }


}