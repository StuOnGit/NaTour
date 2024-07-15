package com.example.natour.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.natour.R;
import com.example.natour.controllers.ControllerAddPath;
import com.example.natour.data.NaPath;
import com.example.natour.data.Sights;
import com.example.natour.enumeration.SightTypeEnum;
import com.example.natour.utils.dialogs.AddSightDialog;
import com.example.natour.utils.MapUtils;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class AddSightActivity extends AppCompatActivity implements AddSightDialog.OnInputListener {

    private static final int REQUEST_CODE = 0;
    private static final String TAG = "addPointOfInterestActivity";
    private MapView map;
    private String emailUser;
    private ControllerAddPath controllerAddPath;
    private Button buttonNext;
    private Button buttonCancel;
    private NaPath napath;
    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Sights> sights = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_point_of_interest);

        Configuration.getInstance().setUserAgentValue("MyOwnUserAgent/1.0");
        map = findViewById(R.id.mapPOI);
        buttonNext = findViewById(R.id.buttonNext);
        emailUser = getIntent().getStringExtra("emailUser");
        napath = getIntent().getParcelableExtra("path");
        buttonCancel= findViewById(R.id.button_cancel_add_pointOfInterest);

        MapUtils mapUtils = new MapUtils(map, this);
        mapUtils.showPath(napath, AddSightActivity.this);
        enableInsertPointOfInterest();

        controllerAddPath = new ControllerAddPath();
        buttonNext.setOnClickListener(view -> {
            AlertDialog.Builder builderConfermDialog = new AlertDialog.Builder(AddSightActivity.this);
            builderConfermDialog.setTitle("Add Point Of interest to Path?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) -> controllerAddPath.addPointOfInterestToDatabase(sights, napath, AddSightActivity.this, emailUser))
                    .setNegativeButton("N0", (dialogInterface, i) -> { });
            AlertDialog.Builder builderPOIisEmpty = new AlertDialog.Builder(AddSightActivity.this);
            builderPOIisEmpty.setTitle("Nessun Punto di Interesse inserito")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialogInterface, i) -> {});
            if(sights.isEmpty()){
                AlertDialog dialog = builderPOIisEmpty.create();
                dialog.show();
            }else{
                AlertDialog dialog = builderConfermDialog.create();
                dialog.show();
            }

        });
        buttonCancel.setOnClickListener(view ->
                {
                    controllerAddPath.openAddPhotoToPath(AddSightActivity.this, napath, emailUser);
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }

    private void enableInsertPointOfInterest() {
        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver(){

            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                addPointOfInterest(p);
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

    private void addPointOfInterest(GeoPoint p) {

        AddSightDialog addSightDialog = new AddSightDialog(p);
        addSightDialog.show(getSupportFragmentManager(),"AddPointOfInterestDialog");

        /*Marker markerPOI = new Marker(map);
        markerPOI.setPosition(p);
        markerPOI.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(markerPOI);
        markers.add(markerPOI);*/

    }


    private SightTypeEnum typologyAdapter(String typologyPointOfInterest) {
        if(typologyPointOfInterest.equalsIgnoreCase("GROTTA")){
            return SightTypeEnum.GROTTA;
        }else if(typologyPointOfInterest.equalsIgnoreCase("SORGENTE")){
            return SightTypeEnum.SORGENTE;
        }else if(typologyPointOfInterest.equalsIgnoreCase("PUNTO PANORAMICO")){
            return SightTypeEnum.PUNTO_PANORAMICO;
        }else if(typologyPointOfInterest.equalsIgnoreCase("AREA PIC-NIC")){
            return SightTypeEnum.AREA_PIC_NIC;
        }else if(typologyPointOfInterest.equalsIgnoreCase("BAITA")){
            return SightTypeEnum.BAITA;
        }else if(typologyPointOfInterest.equalsIgnoreCase("FLORA")){
            return SightTypeEnum.FLORA;
        }else if(typologyPointOfInterest.equalsIgnoreCase("LUOGO DI INTERESSE ARTISTICO")){
            return SightTypeEnum.LUOGO_DI_INTERESSE_ARTISTICO;
        }else {
            return SightTypeEnum.ALTRO;
        }
    }

    @Override
    public void sendInput(String namePointOfInterest, String typologyPointOfInterest, GeoPoint point) {

        Marker markerPOI = new Marker(map);
        markerPOI.setPosition(point);
        markerPOI.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(markerPOI);
        markerPOI.setTitle(namePointOfInterest);
        map.invalidate();
        SightTypeEnum sightType = typologyAdapter(typologyPointOfInterest);
        NaPath path = getIntent().getParcelableExtra("path");
        Sights sight = new Sights(namePointOfInterest, point, sightType, path);
        sights.add(sight);
    }

    @Override
    public void onBackPressed() {
        controllerAddPath.openAddPhotoToPath(AddSightActivity.this, napath, emailUser);
    }
}
