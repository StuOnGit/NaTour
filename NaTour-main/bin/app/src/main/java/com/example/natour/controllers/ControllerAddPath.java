package com.example.natour.controllers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.natour.activities.AddPhotoToPathActivity;
import com.example.natour.activities.AddSightActivity;
import com.example.natour.dao.IdDao;
import com.example.natour.dao.PathDao;
import com.example.natour.dao.SightsDao;
import com.example.natour.data.NaPath;
import com.example.natour.data.Sights;
import com.example.natour.enumeration.DifficultyEnum;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.MapUtils;
import com.example.natour.utils.TypeOfError;
import com.example.natour.utils.dialogs.ErrorDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import io.ticofab.androidgpxparser.parser.GPXParser;
import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.Route;
import io.ticofab.androidgpxparser.parser.domain.RoutePoint;
import io.ticofab.androidgpxparser.parser.domain.Track;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;
import io.ticofab.androidgpxparser.parser.domain.TrackSegment;
import io.ticofab.androidgpxparser.parser.domain.WayPoint;


public class ControllerAddPath {


    private static final String TAG = "ControllerAddPath";

    public void createPathWithGPX(String name, String difficulties, int duration, boolean disableAccessibility, String description, Uri uriGPX, String userEmail, Activity activity) {
        ArrayList<GeoPoint> path = GPXParser(uriGPX);
        GeoPoint firstPoint = path.get(0);
        MapUtils mapUtils = new MapUtils(activity);
        String country = mapUtils.getCountryFromCoordinates(firstPoint);
        String region = mapUtils.getRegionFromCoordinates(firstPoint);
        createNaPath(name, difficulties, duration, disableAccessibility, description, path, userEmail, country, region, activity);
    }

    private ArrayList<GeoPoint> GPXParser(Uri path){
        if(path.getPath().endsWith("gpx")){

            GPXParser parser = new GPXParser();
            Gpx parsedGPX = null;

            try {
                //File gpxFile = new File(path.substring(4));
                //String pathUri = path.getLastPathSegment().substring(4);

                //String pathUri = path.getPath();
                
                String pathUri = null;
                if(path.getLastPathSegment().startsWith("raw")){
                    pathUri = path.getLastPathSegment().substring(4);
                }else if(path.getLastPathSegment().startsWith("primary")) {
                    pathUri ="/storage/emulated/0/"+path.getLastPathSegment().replace("primary:","");
                }else if(path.getLastPathSegment().startsWith("secondary")) {
                    pathUri ="/storage/emulated/0/"+path.getLastPathSegment().replace("secondary:","");
                }
                /*int index = path.getLastPathSegment().indexOf(":");
                String pathUri = path.getLastPathSegment().substring(index);*/

                //System.out.println("pathUri"+ pathUri);
                File gpxFile = new File(pathUri);
                InputStream inputStream = new FileInputStream(gpxFile);
                parsedGPX = parser.parse(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }catch(XmlPullParserException e){
                e.printStackTrace();
            }

            if(parsedGPX == null){
                Log.d(TAG, "onActivityResult: Error to load GPX File");
                return null;
            }else{
                return getGeoPointsFromGPX(parsedGPX);
            }

        }else{
            Log.d(TAG, "GPXParse: Not GPX file");
            return null;
        }
    }

    private ArrayList<GeoPoint> getGeoPointsFromGPX(Gpx parsedGPX){

        List<Track> GPXTracks = parsedGPX.getTracks();
        List<Route> GPXRoute =  parsedGPX.getRoutes();
        List<WayPoint> GPXWayPoint = parsedGPX.getWayPoints();

        if(GPXRoute.size() != 0){
            return GPXRouteAdapter(GPXRoute);
        }else if(GPXTracks.size() != 0){
            return GPXTrackAdapter(GPXTracks);
        }else if(GPXWayPoint.size() != 0){
            return GPXWayPointAdapter(GPXWayPoint);
        }
        return  null;
    }

    private ArrayList<GeoPoint> GPXTrackAdapter(List<Track> GPXTracks){
        ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();

        for (Track track: GPXTracks) {
            List<TrackSegment> trackSegmentList = track.getTrackSegments();
            for (TrackSegment trackSegment: trackSegmentList) {
                List<TrackPoint> trackPointList = trackSegment.getTrackPoints();
                for (TrackPoint trackPoint: trackPointList) {
                    double lat = trackPoint.getLatitude();
                    double lon = trackPoint.getLongitude();
                    GeoPoint geoPoint = new GeoPoint(lat, lon);
                    geoPoints.add(geoPoint);
                }
            }
        }
        return geoPoints;
    }

    private ArrayList<GeoPoint> GPXRouteAdapter(List<Route> GPXRoute){
        ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();

        for (Route route: GPXRoute) {
            List<RoutePoint> routePointList = route.getRoutePoints();
            for (RoutePoint routePoint: routePointList) {
                double lat = routePoint.getLatitude();
                double lon = routePoint.getLongitude();
                GeoPoint geoPoint = new GeoPoint(lat, lon);
                geoPoints.add(geoPoint);
            }
        }
        return geoPoints;
    }

    private ArrayList<GeoPoint> GPXWayPointAdapter(List<WayPoint> GPXTracks){
        ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();

        for (WayPoint wayPoint: GPXTracks) {
            double lat = wayPoint.getLatitude();
            double lon = wayPoint.getLongitude();
            GeoPoint geoPoint = new GeoPoint(lat, lon);
            geoPoints.add(geoPoint);
        }
        return geoPoints;
    }


    private void openInsertPointOfInterest(JSONObject response, Activity activity, NaPath path, String emailUser) {
        AlertDialog.Builder builderPOI = new AlertDialog.Builder(activity);
        builderPOI.setMessage("Vuoi inserire dei punti di interesse a questo percorso?")
                .setCancelable(false)
                .setPositiveButton("Si", (dialogInterface, i) -> {
                    try {
                        int statusCode = response.getInt("statusCode");
                        if(statusCode == 200){
                            Log.d(TAG, "openInsertPointOfInterest: " + statusCode);
                            Log.d(TAG, "openInsertPointOfInterest: " + activity);
                            Intent intentAddPointOfInterest = new Intent(activity, AddSightActivity.class);
                            intentAddPointOfInterest.putExtra("path", path);
                            intentAddPointOfInterest.putExtra("emailUser", emailUser);
                            activity.startActivity(intentAddPointOfInterest);
                            Toast.makeText(activity, "Percorso Inserito correttamente", Toast.LENGTH_SHORT).show();
                            activity.finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("No", (dialogInterface, i) -> openAddPhotoToPath(activity, path, emailUser));
        AlertDialog alert = builderPOI.create();
        alert.setTitle("Punti di interesse");
        alert.show();
    }

    public void addPathtoDatabase(NaPath naPath, Activity activity) {

        AlertDialog.Builder builderError = new AlertDialog.Builder(activity);
        builderError.setMessage("Inserimento percroso fallito")
                .setCancelable(false)
                .setPositiveButton("OK", null);

        PathDao pathDao = new PathDao();
        pathDao.insertPath(naPath,activity , s -> {
            try {
                JSONObject response = new JSONObject(s);
                int statusCode = response.getInt("statusCode");
                if(statusCode == 200){
                    openInsertPointOfInterest(response,activity, naPath, naPath.getEmailCreatore());
                }else{
                    AlertDialog alertError = builderError.create();
                    alertError.setTitle("Inserimento Percorso");
                    alertError.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, new FailListener(){

            @Override
            public void onAuthFail(String msg) {

            }

            @Override
            public void onDBFail(String msg) {

            }

            @Override
            public void onLocalProblem(String msg) {

            }

            @Override
            public void onJSONException(String msg) {

            }

            @Override
            public void onHTTPError(String msg) {

            }
        });
    }


    public void createNaPath(String name, String difficulties, int duration, boolean disableAccessibility, String description, ArrayList<GeoPoint> path, String userEmail, String country, String region, Activity activity) {

        DifficultyEnum difficultyEnum = difficultyEnumAdapter(difficulties);
        GeoPoint startpoint = path.get(0);
        GeoPoint endPath = path.get(path.size() -1);
        JSONArray pathCoordinates = pathJSONAdapter(path);

        NaPath naPath = new NaPath(name,difficultyEnum,startpoint,endPath,region, country, duration, description, disableAccessibility, pathCoordinates, userEmail);
        addPathtoDatabase(naPath, activity);
    }

    private JSONArray pathJSONAdapter(ArrayList<GeoPoint> path){
        JSONArray pathCoordinates = new JSONArray();
        for (GeoPoint pathPoint : path) {
            JSONObject point = new JSONObject();
            try {
                point.put("latitude",pathPoint.getLatitude());
                point.put("longitude",pathPoint.getLongitude());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pathCoordinates.put(point);
        }
        return pathCoordinates;
    }

    private  DifficultyEnum difficultyEnumAdapter(String difficulties){
        DifficultyEnum difficultyEnum = null;
        if(difficulties.equals("Low")){
            difficultyEnum = DifficultyEnum.LOW;
        }else if(difficulties.equals("Average")){
            difficultyEnum =  DifficultyEnum.AVERAGE;
        }else if (difficulties.equals("High")){
            difficultyEnum = DifficultyEnum.HIGH;
        }else if(difficulties.equals("Expert")){
            difficultyEnum = DifficultyEnum.EXPERT;
        }
        return difficultyEnum;
    }

    public void addPointOfInterestFromExternalUser(ArrayList<Sights> sights, NaPath naPath, Activity activity, String emailUser){
        SightsDao sightsDao = new SightsDao();
        new IdDao().getIdPath(naPath, activity, id -> {
            for (Sights sight: sights) {
                sightsDao.insertSight(sight, id, activity, emailUser, response->{
                    Log.d(TAG, "addPointOfInterestToDatabase: " + response);
                }, new FailListener(){

                    @Override
                    public void onAuthFail(String msg) {
                        activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});

                    }

                    @Override
                    public void onDBFail(String msg) {
                        activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});
                    }

                    @Override
                    public void onLocalProblem(String msg) {
                        activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});
                    }

                    @Override
                    public void onJSONException(String msg) {
                        activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});
                    }

                    @Override
                    public void onHTTPError(String msg) {
                        activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});
                    }
                });
            }
        }, new FailListener() {
            @Override
            public void onAuthFail(String msg) {
                activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});

            }

            @Override
            public void onDBFail(String msg) {
                activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});
            }

            @Override
            public void onLocalProblem(String msg) {
                activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});
            }

            @Override
            public void onJSONException(String msg) {
                activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});
            }

            @Override
            public void onHTTPError(String msg) {
                activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});
            }
        });
        activity.finish();
    }


    public void addPointOfInterestToDatabase(ArrayList<Sights> sights, NaPath naPath, Activity activity,String emailUser) {
        SightsDao sightsDao = new SightsDao();
            new IdDao().getIdPath(naPath, activity,
                    id -> {
                        for (Sights sight: sights) {
                                sightsDao.insertSight(sight, id, activity, emailUser, response->{
                                Log.d(TAG, "addPointOfInterestToDatabase: " + response);
                            }, new FailListener(){

                                @Override
                                public void onAuthFail(String msg) {
                                    activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});

                                }

                                @Override
                                public void onDBFail(String msg) {
                                    activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});
                                }

                                @Override
                                public void onLocalProblem(String msg) {
                                    activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});
                                }

                                @Override
                                public void onJSONException(String msg) {
                                    activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});
                                }

                                @Override
                                public void onHTTPError(String msg) {
                                    activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});
                                }
                            });
                        }
                    }, new FailListener() {
                        @Override
                        public void onAuthFail(String msg) {
                            activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});
                        }

                        @Override
                        public void onDBFail(String msg) {
                            activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.DB_ERROR.toString(), msg);});
                        }

                        @Override
                        public void onLocalProblem(String msg) {
                            activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.LOCAL_PROBLEM.toString(), msg);});
                        }

                        @Override
                        public void onJSONException(String msg) {
                            activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.JSON_EXCEPTION.toString(), msg);});
                        }

                        @Override
                        public void onHTTPError(String msg) {
                            activity.runOnUiThread(()->{new ErrorDialog(activity).startDialog(TypeOfError.HTTP_ERROR.toString(), msg);});
                        }
                    });
        openAddPhotoToPath(activity, naPath, emailUser);
    }

    public void openAddPhotoToPath(Activity activity, NaPath naPath, String emailUser){
        AlertDialog.Builder builderPhoto = new AlertDialog.Builder(activity);
        builderPhoto.setMessage("Vuoi inserire delle foto al percorso appena creato?")
                .setCancelable(false)
                .setPositiveButton("Si", (dialogInterface, i) -> {
                    Intent intentAddPhoto = new Intent(activity, AddPhotoToPathActivity.class);
                    intentAddPhoto.putExtra("NaPath", naPath);
                    intentAddPhoto.putExtra("emailUser", emailUser);
                    activity.startActivity(intentAddPhoto);
                    activity.finish();
                })
                .setNegativeButton("No", (dialogInterface, i) -> { activity.finish();});

        AlertDialog alertPhoto = builderPhoto.create();
        alertPhoto.setTitle("Foto percorso");
        alertPhoto.show();
    }

    public String checkInputDuration(EditText durationEditText) {
        String result = null;
        String regex = "[0-9]*";
        if(!durationEditText.getText().toString().equals("") && Pattern.matches(regex, durationEditText.getText())){
            int duration = Integer.parseInt(durationEditText.getText().toString());
            if(duration<=0){
                result = "Inseririe una durata valida in minuti";
            }
        }else{
            result =  "Inserire una durata valida in minuti";
        }
        return result;
    }

    public String checkInputName(String namePath) {
        String result = null;
        if(namePath.isEmpty()){
           result = "Inserire un nome valido";
        }
        return result;
    }

    public boolean nameAlreadyExists(ArrayList<NaPath> naPaths, String name) {
        boolean exists = false;
        int i = 0;
        int size = naPaths.size();
        while(i<size && !naPaths.get(i).getPathName().equalsIgnoreCase(name)){
            Log.d(TAG, "nameAlreadyExist: " + naPaths.get(i).getPathName());
            i++;
        }
        if(i==size){
            exists = false;
        }
        else if(naPaths.get(i).getPathName().equalsIgnoreCase(name)){
            exists = true;
        }
        return exists;
    }
}
