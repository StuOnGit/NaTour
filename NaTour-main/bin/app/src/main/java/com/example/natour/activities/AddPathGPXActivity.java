package com.example.natour.activities;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.natour.R;
import com.example.natour.controllers.ControllerAddPath;
import com.example.natour.dao.PathDao;
import com.example.natour.interfacce.FailListener;

public class AddPathGPXActivity extends AppCompatActivity {

    private static final String TAG = "AddPathWithGPXActivity";
    private Spinner spinnerDifficulties;
    private Button confirmButton;
    private Button cancelButton;
    private EditText descriptionEditText;
    private Switch disabledAccessibilitySwitch;
    private EditText durationEditText;
    private EditText nameEditText;
    private Uri uriGPX;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_path_with_gpx);

        spinnerDifficulties = findViewById(R.id.difficultiesSpinner);
        confirmButton = findViewById(R.id.confirmButton);
        nameEditText = findViewById(R.id.namePath);
        durationEditText = findViewById(R.id.editTextTime);
        disabledAccessibilitySwitch = findViewById(R.id.switchDisabledAccessibility);
        descriptionEditText = findViewById(R.id.editTextTextMultiLine);
        cancelButton = findViewById(R.id.cancelButton);
        uriGPX = getIntent().getParcelableExtra("uriPathGPX");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.difficulties_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulties.setAdapter(adapter);

        confirmButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddPathGPXActivity.this);
            builder.setTitle("Add path?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialogInterface, i) ->{
                        createPath();
                    })
                    .setNegativeButton("N0", (dialogInterface, i) -> { });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        cancelButton.setOnClickListener(view -> finish());
    }

    private void createPath(){
        if(checkInput()){
            String name = nameEditText.getText().toString();
            String difficulties = spinnerDifficulties.getSelectedItem().toString();
            int duration = Integer.parseInt(durationEditText.getText().toString()) ;
            boolean disableAccessibility = disabledAccessibilitySwitch.isChecked();
            String description = descriptionEditText.getText().toString();
            String userEmail = getIntent().getStringExtra("userEmail");

            InsertPath(name, userEmail, difficulties, duration, disableAccessibility, description);
        }
    }

    private boolean checkInput(){
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

    private void InsertPath(String name, String userEmail, String difficulties, int duration, boolean disableAccessibility, String description) {
        ControllerAddPath controllerAddPath = new ControllerAddPath();
        new PathDao().getPathsWithEmail(userEmail, this,
                naPaths -> {
                    if(!controllerAddPath.nameAlreadyExists(naPaths, name)){
                        controllerAddPath.createPathWithGPX(name, difficulties, duration, disableAccessibility, description, uriGPX, userEmail, AddPathGPXActivity.this);
                    }else{
                        nameEditText.setError("Già hai creato un percorso con questo nome");
                    }
                }, new FailListener() {
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


}
