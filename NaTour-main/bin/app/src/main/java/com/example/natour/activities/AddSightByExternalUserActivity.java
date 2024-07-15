package com.example.natour.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.natour.R;
import com.example.natour.application.NaTourApplication;
import com.example.natour.controllers.ControllerAddPath;
import com.example.natour.data.NaPath;
import com.example.natour.data.Sights;
import com.example.natour.enumeration.SightTypeEnum;
import com.example.natour.utils.MapUtils;
import com.example.natour.utils.dialogs.AddSightDialog;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class AddSightByExternalUserActivity extends AppCompatActivity implements AddSightDialog.OnInputListener {

    private Button cancel;
    private Button next;
    private MapView map;
    private ControllerAddPath controllerAddPath;
    private ArrayList<Sights> sights = new ArrayList<>();
    private NaPath naPath;
    private String emailUser;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_sights_by_external_user);
        Configuration.getInstance().setUserAgentValue("MyOwnUserAgent/1.0");
        setupUI();
        super.onCreate(savedInstanceState);
    }

    private void setupUI() {
        cancel = findViewById(R.id.button_cancel_add_sights);
        next = findViewById(R.id.button_next_sights);
        map = findViewById(R.id.mapSights);
        naPath = getIntent().getParcelableExtra("naPath");
        cancel.setOnClickListener(view -> finish());

        controllerAddPath = new ControllerAddPath();
        emailUser = ((NaTourApplication) getApplication()).user.getEmail();

        MapUtils mapUtils = new MapUtils(map, this);
        mapUtils.showPath(naPath, AddSightByExternalUserActivity.this);
        enableInsertPointOfInterest();

        AlertDialog.Builder builderConfermDialog = new AlertDialog.Builder(AddSightByExternalUserActivity.this);
        builderConfermDialog.setTitle("Add Points Of interest to Path?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialogInterface, i) -> controllerAddPath.addPointOfInterestFromExternalUser(sights, naPath, AddSightByExternalUserActivity.this, emailUser))
                .setNegativeButton("N0", (dialogInterface, i) -> { });
        AlertDialog.Builder builderSightsisEmpty = new AlertDialog.Builder(AddSightByExternalUserActivity.this);
        builderSightsisEmpty.setTitle("Nessun Punto di Interesse inserito")
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {});

        next.setOnClickListener(view -> {
            if(sights.isEmpty()){
                AlertDialog dialog = builderSightsisEmpty.create();
                dialog.show();
            }else{
                AlertDialog dialog = builderConfermDialog.create();
                dialog.show();
            }
        });
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
        addSightDialog.show(getSupportFragmentManager(),"AddSightstDialog");

    }

    @Override
    public void sendInput(String namePointOfInterest, String typologyPointOfInterest, GeoPoint point) {

        Marker markerPOI = new Marker(map);
        markerPOI.setPosition(point);
        markerPOI.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(markerPOI);
        markerPOI.setTitle(namePointOfInterest);
        markerPOI.setTitle(namePointOfInterest + ", tipologia: " + typologyPointOfInterest);
        map.invalidate();
        SightTypeEnum sightType = typologyAdapter(typologyPointOfInterest);
        NaPath path = getIntent().getParcelableExtra("naPath");
        Sights sight = new Sights(namePointOfInterest, point, sightType, path);
        sights.add(sight);
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
}
