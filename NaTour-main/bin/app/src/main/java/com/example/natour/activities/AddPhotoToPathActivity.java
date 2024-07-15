package com.example.natour.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour.R;
import com.example.natour.dao.IdDao;
import com.example.natour.dao.PhotoDao;
import com.example.natour.data.NaPath;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.dialogs.ErrorDialog;
import com.example.natour.utils.cardadapter.PhotoCardAdapter;
import com.example.natour.utils.TypeOfError;

import java.io.IOException;
import java.util.ArrayList;

public class AddPhotoToPathActivity extends AppCompatActivity {

    private static final String TAG = "AddPhotoToPath";
    private String emailUser;
    private Button buttonConferm;
    private Button buttonAddPhoto;
    private Button buttonCancel;
    private ProgressBar progressBar;
    private ArrayList<Bitmap> photosBitmap = new ArrayList<>();
    private RecyclerView recyclerViewPhotos;
    PhotoCardAdapter photoCardAdapter;
    private NaPath naPath;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Intent intent = result.getData();
            if(intent != null){
                try {
                    Bitmap photoBitMap = MediaStore.Images.Media.getBitmap(getContentResolver(), intent.getData());
                    photosBitmap.add(photoBitMap);
                    photoCardAdapter = new PhotoCardAdapter(AddPhotoToPathActivity.this, photosBitmap);
                    recyclerViewPhotos.setAdapter(photoCardAdapter);
                    recyclerViewPhotos.setLayoutManager(new LinearLayoutManager(AddPhotoToPathActivity.this));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private void uploadPhotoToDatabase(Bitmap photoBitMap, int idPath){
        progressBar.setVisibility(View.VISIBLE);
        new PhotoDao().insertPhoto(idPath, emailUser, photoBitMap, this,
                onSuccess -> {
                    Log.d(TAG, "Photo inserted: " +  onSuccess);
                    progressBar.setVisibility(View.INVISIBLE);
                }, new FailListener() {
                    @Override
                    public void onAuthFail(String msg) {
                        Log.d(TAG, "AuthFail: " + msg);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onDBFail(String msg) {
                        Log.d(TAG, "onDBFail: " + msg);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onLocalProblem(String msg) {
                        Log.d(TAG, "onLocalProblem: " + msg);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onJSONException(String msg) {
                        Log.d(TAG, "onJSONException: " + msg);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onHTTPError(String msg) {
                        Log.d(TAG, "onHTTPError: " + msg);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo_to_path);
        emailUser = getIntent().getStringExtra("emailUser");
        buttonConferm = findViewById(R.id.ConfermButton);
        buttonAddPhoto = findViewById(R.id.buttonAddPhoto);
        buttonCancel = findViewById(R.id.button_cance_addPhoto);
        progressBar = findViewById(R.id.progress_Bar_AddPhoto_to_Path);
        recyclerViewPhotos = findViewById(R.id.recyclerViewPhotos);

        emailUser = getIntent().getStringExtra("emailUser");
        naPath = getIntent().getParcelableExtra("NaPath");

        buttonAddPhoto.setOnClickListener(view -> {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                if(photosBitmap.size()<5){
                    openAddPhoto();
                }else{
                    buttonAddPhoto.setClickable(false);
                    buttonAddPhoto.setText("Max 5 photos");
                }
            }else{
                requestStoragePermissions();
            }

        });

        buttonCancel.setOnClickListener(view -> finish());

        buttonConferm.setOnClickListener(view -> {
                AlertDialog.Builder builderConfermDialog = new AlertDialog.Builder(AddPhotoToPathActivity.this);
                builderConfermDialog.setTitle("Add photo to path?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            uploadPhotos();
                            finish();
                        })
                        .setNegativeButton("N0", (dialogInterface, i) -> { });
            AlertDialog.Builder builderPhotosEmpty = new AlertDialog.Builder(AddPhotoToPathActivity.this);
            builderPhotosEmpty.setTitle("Nessuna foto inserita")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialogInterface, i) -> {});
                if(photosBitmap.isEmpty()){
                    AlertDialog dialog = builderPhotosEmpty.create();
                    dialog.show();
                }else{
                    AlertDialog dialog = builderConfermDialog.create();
                    dialog.show();
                }
            });
    }

    private void uploadPhotos() {
        new IdDao().getIdPath(naPath, AddPhotoToPathActivity.this,
                idPath -> {
                    for (Bitmap photo: photosBitmap) {
                        uploadPhotoToDatabase(photo, idPath);
                    }
                }, new FailListener() {
                    @Override
                    public void onAuthFail(String msg) {
                        runOnUiThread(()->{new ErrorDialog(AddPhotoToPathActivity.this).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);});
                    }

                    @Override
                    public void onDBFail(String msg) {
                        runOnUiThread(()->{new ErrorDialog(AddPhotoToPathActivity.this).startDialog(TypeOfError.DB_ERROR.toString(), msg);});
                    }

                    @Override
                    public void onLocalProblem(String msg) {
                        runOnUiThread(()->{new ErrorDialog(AddPhotoToPathActivity.this).startDialog(TypeOfError.LOCAL_PROBLEM.toString(), msg);});
                    }

                    @Override
                    public void onJSONException(String msg) {
                        runOnUiThread(()->{new ErrorDialog(AddPhotoToPathActivity.this).startDialog(TypeOfError.JSON_EXCEPTION.toString(), msg);});
                    }

                    @Override
                    public void onHTTPError(String msg) {
                        runOnUiThread(()->{new ErrorDialog(AddPhotoToPathActivity.this).startDialog(TypeOfError.HTTP_ERROR.toString(), msg);});
                    }
                });
    }

    private void openAddPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK); // MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.setType("image/png");
        launcher.launch(intent);
    }



    ActivityResultLauncher<String[]> photoPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineExternalStorageGranted = result.getOrDefault(
                                Manifest.permission.READ_EXTERNAL_STORAGE, false);
                        if (fineExternalStorageGranted) {
                            if(photosBitmap.size()<5){
                                openAddPhoto();
                            }else{
                                buttonAddPhoto.setClickable(false);
                                buttonAddPhoto.setText("Max 5 photos");
                            }
                        }  else {
                            Toast.makeText(this, "Senza permessi non è possibile procedere", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    private void requestStoragePermissions(){
        photoPermissionLauncher.launch(new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE
        });
    }
}
