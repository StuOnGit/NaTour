package com.example.natour.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.example.natour.R;
import com.example.natour.dao.SightsDao;
import com.example.natour.data.NaPath;
import com.example.natour.data.Sights;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.dialogs.ErrorDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapUtils implements LocationListener {

    private static final String MY_USER_AGENT = "foot";
    private EditText editTextEndPoint;
    private EditText editTextStartPoint;
    private MapView map;
    private Activity activity;
    private IMapController iMapController;
    private ArrayList<GeoPoint> insertPoints;
    private ArrayList<Sights> PointsOfInterst = new ArrayList<>();
    private Marker lastMarkers;
    private Marker firstMarker;
    private Polyline roadOverlay;
    private FragmentManager supportFragmentManager;
    private  ArrayList<Sights> sights;
    private String region;
    private String country;
    private static final int FIRST_ELEMENT_INDEX = 0;
    private LocationManager locationManager;
    private static final int HANDLER_DELAY = 1000 * 60 * 5;
    private static final int START_HANDLER_DELAY = 0;
    private static final int GPS_TIME_INTERVAL = 1000 * 60 * 5;
    private static final int GPS_DISTANCE = 1000;
    private Road road;
    private static final String TAG = "MapUtils";



    public MapUtils(MapView map, Activity activity){
        this.map = map;
        this.activity = activity;
        setupMapWithOutPosition();
    }

    private void setupMapWithOutPosition() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        map.setMultiTouchControls(true);
        iMapController = map.getController();
        iMapController.setZoom(15.00);
    }

    public MapUtils(MapView map, Activity activity, EditText editTextStartPoint, EditText editTextEndPoint) {
        this.map = map;
        this.activity = activity;
        this.editTextStartPoint = editTextStartPoint;
        this.editTextEndPoint = editTextEndPoint;
        insertPoints = new ArrayList<>();
        setupMap();
    }

    public MapUtils(Activity activity){
        this.activity = activity;
    }

    private void setupMap() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        map.setMultiTouchControls(true);
        iMapController = map.getController();
        iMapController.setZoom(15.00);
        GeoPoint defaultGeoPoint = new GeoPoint(40.82872, 14.19044);
        iMapController.setCenter(defaultGeoPoint );
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "setupMap: Permessi non presenti");
        } else {
            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setCenterOfMap();
                    handler.postDelayed(this, HANDLER_DELAY);
                }
            }, START_HANDLER_DELAY);
        }

    }

    private void setCenterOfMap() {
        if(locationManager == null){
         locationManager = (LocationManager) activity.getSystemService(Activity.LOCATION_SERVICE);
        }
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                System.out.println("terzo if");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_TIME_INTERVAL, GPS_DISTANCE,  this);
            }
        }
    }

    public ArrayList<GeoPoint> getPathPoints() {
        if(roadOverlay!=null){
            List<GeoPoint> points = roadOverlay.getActualPoints();
            ArrayList<GeoPoint> arrayListPoints = new ArrayList<>(points);
            return arrayListPoints;
        }
        return null;
    }

    private void setCenter(Location location){
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
        iMapController.setCenter(geoPoint);
    }

    public void enableInsertPoint() {
        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver(){
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                insertPoints.add(p);
                updatePath(insertPoints);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        map.getOverlays().add(0, mapEventsOverlay);
    }

    private void updatePath(ArrayList<GeoPoint> insertPoints) {
        int size = insertPoints.size();
        if(size == 1){
            Log.d(TAG, "updatePath: if size 1" );
            addStartMarker(insertPoints.get(FIRST_ELEMENT_INDEX));
        }else if(size == 2){
            Log.d(TAG, "updatePath: if size 2" );
            addEndMarker(insertPoints.get(size - 1));
            showRoad(insertPoints);
        }else if(size > 2){
            Log.d(TAG, "updatePath: if size >2" );
            lastMarkers.remove(map);
            addEndMarker(insertPoints.get(size -1 ));
            clearRoad();
            showRoad(insertPoints);
        }
        map.invalidate();
    }

    private ArrayList<GeoPoint> getPointsFromJSON(JSONArray percorsoGeografico){
        ArrayList<GeoPoint> points = new ArrayList<>();
        for(int i=0;i<percorsoGeografico.length();i++){
            try {
                JSONObject JSONObjectPoint = percorsoGeografico.getJSONObject(i);
                Double latitudine = JSONObjectPoint.getDouble("latitude");
                Double longitudine = JSONObjectPoint.getDouble("longitude");
                GeoPoint point = new GeoPoint(latitudine, longitudine);
                points.add(point);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return points;
    }

    public  void showPath(NaPath path, Activity activity){
        ArrayList<GeoPoint> insertPoints = getPointsFromJSON(path.getPercorso_geografico());
        GeoPoint startPoint = insertPoints.get(FIRST_ELEMENT_INDEX);
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setIcon( activity.getResources().getDrawable(R.drawable.ic_start_point_marker, null));
        startMarker.setTitle("Start Point");
        map.getOverlays().add(startMarker);
        map.invalidate();
        iMapController.setCenter(startPoint);

        int insertPointsSize = insertPoints.size();
        GeoPoint lastPoint = insertPoints.get(insertPointsSize - 1 );
        Marker lastPointMarker = new Marker(map);
        lastPointMarker.setPosition(lastPoint);
        lastPointMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        lastPointMarker.setIcon( activity.getResources().getDrawable(R.drawable.ic_end_point_marker, null));
        lastPointMarker.setTitle("Finish");
        map.getOverlays().add(lastPointMarker);
        map.invalidate();

        Road road = new Road(insertPoints);
        roadOverlay = RoadManager.buildRoadOverlay(road);
        roadOverlay.setPoints(insertPoints);
        map.getOverlays().add(roadOverlay);
        map.invalidate();

        SightsDao sightsDao = new SightsDao();
        sightsDao.getSightsByPath(path, activity, sights -> {
                showSights(sights);
        }, new FailListener() {
            @Override
            public void onAuthFail(String msg) {
                Log.d(TAG, "onAuthFail: " + msg);
                new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
            }

            @Override
            public void onDBFail(String msg) {
                Log.d(TAG, "onDBFail: "+ msg);
                new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
            }

            @Override
            public void onLocalProblem(String msg) {
                Log.d(TAG, "onLocalProblem: " + msg);
                new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
            }

            @Override
            public void onJSONException(String msg) {
                Log.d(TAG, "onJSONException: " + msg);
                new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
            }

            @Override
            public void onHTTPError(String msg) {
                Log.d(TAG, "onHTTPError: " + msg);
                new ErrorDialog(activity).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
            }
        });
    }

    private void showSights(ArrayList<Sights> sights) {
        Log.d(TAG, "showSights: ");
        for (Sights sight: sights) {
            GeoPoint sightPoint = sight.getLocation();
            Marker sightMarker = new Marker(map);
            sightMarker.setPosition(sightPoint);
            sightMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            sightMarker.setTitle(sight.getName() + ", tipologia: " + sight.getSightType().toString());
            map.getOverlays().add(sightMarker);
            map.invalidate();
        }
    }

    private void showRoad(ArrayList<GeoPoint> insertPoints){
        RoadManager roadManager =  new OSRMRoadManager(activity, MY_USER_AGENT);
        ((OSRMRoadManager)roadManager).setMean(OSRMRoadManager.MEAN_BY_FOOT);
        road = roadManager.getRoad(insertPoints);
        roadOverlay = RoadManager.buildRoadOverlay(road);
        map.getOverlays().add(roadOverlay);
        map.invalidate();
    }

    public void back() {
        int insertPointsSize = insertPoints.size();
        if(insertPointsSize == 1 ){
            Log.d(TAG, "back: " + insertPointsSize);
            editTextStartPoint.setText("");
            insertPoints.remove(FIRST_ELEMENT_INDEX);
            firstMarker.remove(map);
            map.invalidate();
        }else if(insertPointsSize == 2){
            editTextEndPoint.setText("");
            int lastElementIndex = insertPointsSize - 1;
            insertPoints.remove(lastElementIndex);
            clearRoad();
            lastMarkers.remove(map);
        }else if(insertPointsSize >2 ){
            editTextEndPoint.setText("");
            int lastElementIndex = insertPointsSize - 1;
            insertPoints.remove(lastElementIndex);
            clearRoad();
            lastMarkers.remove(map);
            updatePath(insertPoints);
        }
    }

    private void clearRoad() {
        if(roadOverlay != null){
            roadOverlay.setVisible(false);
        }
        roadOverlay = null;
    }

    private void clearAllMap(){
        clearRoad();
        if(firstMarker != null){
            firstMarker.remove(map);
        }
        if(lastMarkers != null){
            lastMarkers.remove(map);
        }
        if(!insertPoints.isEmpty()){
            insertPoints.clear();
        }
        map.invalidate();
    }

    private void addStartMarker(GeoPoint startPoint){
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setIcon( activity.getResources().getDrawable(R.drawable.ic_start_point_marker, null));
        startMarker.setTitle("Start Point");
        map.getOverlays().add(startMarker);
        map.invalidate();
        firstMarker = startMarker;
        setEditTextStartPointFromCoordinatesPoint(startPoint);
        iMapController.setCenter(startPoint);
    }

    private void addEndMarker(GeoPoint lastPoint){
        Marker lastPointMarker = new Marker(map);
        lastPointMarker.setPosition(lastPoint);
        lastPointMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        lastPointMarker.setIcon( activity.getResources().getDrawable(R.drawable.ic_end_point_marker, null));
        lastPointMarker.setTitle("Finish");
        map.getOverlays().add(lastPointMarker);
        map.invalidate();
        lastMarkers  = lastPointMarker;
        setEditTextEndPointFromCoordinatesPoint(lastPoint);
    }

    private void setEditTextEndPointFromCoordinatesPoint(GeoPoint point){
        String addressName = getAddressNameFromCoordinatesPoint(point);
        editTextEndPoint.setText(addressName);
    }

    private void setEditTextStartPointFromCoordinatesPoint(GeoPoint point){
        String addressName = getAddressNameFromCoordinatesPoint(point);
        editTextStartPoint.setText(addressName);
    }

    private String getAddressNameFromCoordinatesPoint(GeoPoint point){
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        double latitude = point.getLatitude();
        double longitude = point.getLongitude();
        List<Address> addresses = null;
        String addressName = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude , 1);
            if(addresses.size()>0){
                addressName = addresses.get(0).getAddressLine(0);
                country = addresses.get(0).getCountryName();
                region = addresses.get(0).getAdminArea();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressName;
    }

    public String getCountryFromCoordinates(GeoPoint point){
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        double latitude = point.getLatitude();
        double longitude = point.getLongitude();
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude , 1);
            if(addresses.size()>0){
                country = addresses.get(0).getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return country;
    }

    public  String getRegionFromCoordinates(GeoPoint point){
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        double latitude = point.getLatitude();
        double longitude = point.getLongitude();
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude , 1);
            if(addresses.size()>0){
                region = addresses.get(0).getAdminArea();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return region;
    }

    public void findPathFromTwoAddressName(String startPointAddress, String endPointAddress) {
        AlertDialog.Builder builderError = new AlertDialog.Builder(activity);
        builderError.setMessage("Inserire degli indirizzi validi")
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> { });

        GeoPoint startPoint = getGeoPointFromAddressName(startPointAddress);
        GeoPoint endPoint = getGeoPointFromAddressName(endPointAddress);
        if(startPoint == null || endPoint == null) {
            AlertDialog alert = builderError.create();
            alert.setTitle("Inserimento Sentiero");
            alert.show();
        }else{
            clearAllMap();
            showPathFromTwoPoints(startPoint, endPoint);
        }
    }

    private void showPathFromTwoPoints(GeoPoint startPoint, GeoPoint endPoint){
        addStartMarker(startPoint);
        addEndMarker(endPoint);

        insertPoints.add(startPoint);
        insertPoints.add(endPoint);
        showRoad(insertPoints);
    }

    private GeoPoint getGeoPointFromAddressName(String addressName){
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        List<Address> address;
        GeoPoint geoPoint = null;
        double longitude, latitude;
        try {
            address = geocoder.getFromLocationName(addressName, 1);
            if(address.size() > 0){
                longitude = address.get(0).getLongitude();
                latitude = address.get(0).getLatitude();
                geoPoint = new GeoPoint(latitude, longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  geoPoint;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        setCenter(location);
        locationManager.removeUpdates(this);
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

}
