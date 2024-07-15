package com.example.natour.activities;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.natour.R;
import com.example.natour.controllers.ControllerAddPath;
import com.example.natour.utils.MapUtils;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

public class InsertMapPathActivity extends AppCompatActivity {

    private MapView map = null;
    private MapUtils mapUtils;
    private Button backButton;
    private AutoCompleteTextView editTextStartPoint;
    private AutoCompleteTextView editTextEndPoint;
    private Button enableStartPoint;
    private Button enableEndPoint;
    private Button buttonSearchPathWithNamesAddresses;
    private Button buttonConferm;
    private ProgressBar progressBar;
    private static final String TAG = "MapInsertPath";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_path_map);
        enableEndPoint = findViewById(R.id.enableEndPointText);
        enableStartPoint = findViewById(R.id.enableStartPointText);
        backButton = findViewById(R.id.buttonBack);
        map = findViewById(R.id.map);
        editTextEndPoint = findViewById(R.id.editTextTextEndPoint);
        editTextStartPoint = findViewById(R.id.editTextTextStartPoint);
        buttonConferm = findViewById(R.id.buttonConferm);
        buttonSearchPathWithNamesAddresses = findViewById(R.id.buttonSearchWIthNamesAddresses);
        Configuration.getInstance().setUserAgentValue("MyOwnUserAgent/1.0");

        mapUtils = new MapUtils(map, InsertMapPathActivity.this, editTextStartPoint, editTextEndPoint);

        enableEndPoint();
        enableStartPoint();
        changecolorStartPoint(enableStartPoint);
        changecolorStartPoint(enableEndPoint);

        backButton.setOnClickListener(view -> mapUtils.back());

        buttonSearchPathWithNamesAddresses.setOnClickListener(view -> {
            String startPointAddressName = editTextStartPoint.getText().toString();
            String endPointAdressName = editTextEndPoint.getText().toString();
            if(!startPointAddressName.isEmpty() && !endPointAdressName.isEmpty()){
                mapUtils.findPathFromTwoAddressName(startPointAddressName, endPointAdressName);
            }else{
                Toast.makeText(InsertMapPathActivity.this, " Start Point and End Point ara empty", Toast.LENGTH_SHORT);
            }
        });

        buttonConferm.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(InsertMapPathActivity.this);
                builder.setTitle("Create new path?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialogInterface, i) -> createPath())
                        .setNegativeButton("N0", (dialogInterface, i) -> {

                        });
                AlertDialog dialog = builder.create();
                dialog.show();
        });

        mapUtils.enableInsertPoint();
    }

    private void createPath(){
        ControllerAddPath controllerAddPath = new ControllerAddPath();

        String name = getIntent().getStringExtra("namePath");
        String difficulties = getIntent().getStringExtra("difficultiesPath");
        int duration =  getIntent().getIntExtra("durationPath", 0);
        boolean disableAccessibility = getIntent().getBooleanExtra("disableAccessibilityPath", false);
        String description = getIntent().getStringExtra("descriptionPath");
        ArrayList<GeoPoint> path = mapUtils.getPathPoints();
        String userEmail = getIntent().getStringExtra("userEmail");
        String country = mapUtils.getCountry();
        String region  = mapUtils.getRegion();

        if(path!=null){
            controllerAddPath.createNaPath(name, difficulties, duration, disableAccessibility, description, path, userEmail, country, region, this);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(InsertMapPathActivity.this);
            builder.setTitle("Nessun percorso inserito")
                    .setCancelable(false)
                    .setPositiveButton("ok", (dialogInterface, i) -> {});
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    private void enableStartPoint(){
        enableStartPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextStartPoint.setEnabled(!editTextStartPoint.isEnabled());
                changecolorStartPoint(enableStartPoint);
            }
        });
    }

    private void enableEndPoint() {
        enableEndPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextEndPoint.setEnabled(!editTextEndPoint.isEnabled());
                changecolorEndPoint(enableEndPoint);
            }
        });
    }

    private void changecolorEndPoint(Button button) {
        if(editTextEndPoint.isEnabled()){
            button.setBackgroundColor(Color.GREEN);
        }else{
            button.setBackgroundColor(Color.RED);
        }
    }

    private void changecolorStartPoint(Button button) {
        if(editTextStartPoint.isEnabled()){
            button.setBackgroundColor(Color.GREEN);
        }else{
            button.setBackgroundColor(Color.RED);
        }
    }

}
