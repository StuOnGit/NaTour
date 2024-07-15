package com.example.natour.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.natour.R;
import com.example.natour.controllers.ControllerAddPath;
import com.example.natour.dao.PathDao;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.TypeOfError;
import com.example.natour.utils.dialogs.ErrorDialog;

public class AddPathManuallyActivity extends AppCompatActivity{

    private static final String TAG = "AddPathManualyActivity";
    private Spinner spinnerDifficulties;
    private Button confirmButton;
    private EditText nameEditText;
    private EditText durationEditText;
    private Switch disabledAccessibilitySwitch;
    private EditText descriptionEditText;
    private Button cancelButton;

    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                            if(gpsIsEnabled()){
                                createPath();
                            }else{
                                enabledGPS();
                            }
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                            Toast.makeText(this, "Senza permessi non è possibile procedere", Toast.LENGTH_SHORT).show();
                        } else {
                            // No location access granted.
                            Toast.makeText(this, "Senza permessi non è possibile procedere", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    private void enabledGPS() {
        new AlertDialog.Builder(this)
                .setMessage("Attiva il GPS per proseguire")
                .setPositiveButton("OK", null)
                .show();
    }

    private boolean gpsIsEnabled() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception ex){

        }
        if(!gps_enabled && !network_enabled) {
           return false;
        }else{
            return  true;
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_path_manualy);

        disabledAccessibilitySwitch = findViewById(R.id.switchDisabledAccessibility);
        confirmButton = findViewById(R.id.confirmButton);
        spinnerDifficulties = findViewById(R.id.difficultiesSpinner);
        nameEditText = findViewById(R.id.namePath);
        durationEditText = findViewById(R.id.editTextTime);
        descriptionEditText = findViewById(R.id.editTextTextMultiLine);
        cancelButton = findViewById(R.id.cancelButton);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.difficulties_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulties.setAdapter(adapter);

        confirmButton.setOnClickListener(view ->{
            if(ContextCompat.checkSelfPermission(AddPathManuallyActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if(gpsIsEnabled()){
                    createPath();
                }else{
                    enabledGPS();
                }
            }else{
                requestGPSPermission();
            }
        });
        cancelButton.setOnClickListener(view -> finish());
    }

    private void requestGPSPermission() {
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void createPath(){
        if(checkInput()){
            String name = nameEditText.getText().toString();
            String difficulties = spinnerDifficulties.getSelectedItem().toString();
            int duration = Integer.parseInt(durationEditText.getText().toString());
            boolean disableAccessibility = disabledAccessibilitySwitch.isChecked();
            String description = descriptionEditText.getText().toString();
            String userEmail = getIntent().getStringExtra("userEmail");

            InsertPath(name, userEmail, difficulties, duration, disableAccessibility, description);
        }
    }

    private boolean checkInput() {
        ControllerAddPath controllerAddPath = new ControllerAddPath();
        String resultCheckDuration = controllerAddPath.checkInputDuration(durationEditText);
        String name = nameEditText.getText().toString();
        String resultCheckName = controllerAddPath.checkInputName(name);
        if(resultCheckDuration != null){
            durationEditText.setError(resultCheckDuration);
            return false;
        }else if(resultCheckName != null){
            nameEditText.setError(resultCheckName);
            return false;
        }else{
            return true;
        }
    }

    private void InsertPath(String name, String  userEmail, String difficulties, int duration, boolean disableAccessibility, String description){
        ControllerAddPath controllerAddPath = new ControllerAddPath();
        new PathDao().getPathsWithEmail(userEmail, this,
                naPaths -> {
                    if(!controllerAddPath.nameAlreadyExists(naPaths, name)){
                        openMapInsertPath(name, difficulties, duration, disableAccessibility, description, userEmail);
                        finish();
                    }else{
                        nameEditText.setError("Già hai creato un percorso con questo nome");
                    }
                }, new FailListener() {
                    @Override
                    public void onAuthFail(String msg) {
                        Log.d(TAG, "onAuthFail: " + msg);
                        new ErrorDialog(AddPathManuallyActivity.this).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                    }
                    @Override
                    public void onDBFail(String msg) {
                        Log.d(TAG, "onDBFail: "+ msg);
                        new ErrorDialog(AddPathManuallyActivity.this).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                    }

                    @Override
                    public void onLocalProblem(String msg) {
                        Log.d(TAG, "onLocalProblem: " + msg);
                        new ErrorDialog(AddPathManuallyActivity.this).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                    }

                    @Override
                    public void onJSONException(String msg) {
                        Log.d(TAG, "onJSONException: " + msg);
                        new ErrorDialog(AddPathManuallyActivity.this).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                    }

                    @Override
                    public void onHTTPError(String msg) {
                        Log.d(TAG, "onHTTPError: " + msg);
                        new ErrorDialog(AddPathManuallyActivity.this).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                    }
                });
    }


    private void openMapInsertPath(String name, String difficulties, int duration, boolean disableAccessibility, String description, String userEmail){
        Intent intentMapInsertPath = new Intent(AddPathManuallyActivity.this, InsertMapPathActivity.class);
        intentMapInsertPath.putExtra("namePath", name);
        intentMapInsertPath.putExtra("difficultiesPath", difficulties);
        intentMapInsertPath.putExtra("durationPath", duration);
        intentMapInsertPath.putExtra("disableAccessibilityPath", disableAccessibility);
        intentMapInsertPath.putExtra("descriptionPath", description);
        intentMapInsertPath.putExtra("userEmail", userEmail);
        startActivity(intentMapInsertPath);
    }
}
