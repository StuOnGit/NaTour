package com.example.natour.utils.dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.natour.R;
import com.example.natour.activities.AddPathGPXActivity;
import com.example.natour.activities.AddPathManuallyActivity;
import com.example.natour.data.User;


public class ChooseFileDialogFragment extends DialogFragment {

    private static final String TAG = "ChooseFileDialogFragment";
    private static final int STORAGE_PERMISSION_CODE = 1;
    ImageView closeDialog;
    ImageView insertGPXPath;
    ImageView insertManualyPath;
    User user;

    ActivityResultLauncher<String> openFileGPX = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result != null && result.getPath().endsWith("gpx")){
                Log.d(TAG, "onActivityResult: gpx ok" );
                Intent intentAddPathWithGPX= new Intent(getActivity(), AddPathGPXActivity.class);
                intentAddPathWithGPX.putExtra("uriPathGPX", result);
                intentAddPathWithGPX.putExtra("userEmail", user.getEmail());
                startActivity(intentAddPathWithGPX);
                dismiss();
            }else{
                Log.d(TAG, "onActivityResult: gpx not ok: " + (result == null));
                Toast.makeText(getActivity(), "File non valido", Toast.LENGTH_SHORT);
            }
        }
    });

    ActivityResultLauncher<String[]> externalStoragePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineExternalStorageGranted = result.getOrDefault(
                                Manifest.permission.READ_EXTERNAL_STORAGE, false);
                        if (fineExternalStorageGranted) {
                            // Precise location access granted.
                            openFileGPX.launch("*/*");
                        }  else {
                            // No location access granted.
                            Toast.makeText(getActivity(), "Senza permessi non è possibile procedere", Toast.LENGTH_SHORT).show();
                        }
                    }
            );



    public ChooseFileDialogFragment(User user) {
        this.user = user;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.choose_file_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeDialog = view.findViewById(R.id.close_addpath_dialog);
        insertGPXPath = view.findViewById(R.id.upload_gpx);
        insertManualyPath = view.findViewById(R.id.upload_edit_path);

        closeDialog.setOnClickListener(view13 -> dismiss());

        insertGPXPath.setOnClickListener(view12 -> {
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                openFileGPX.launch("*/*");
            }else{
                requestStoragePermissions();
            }
        });

        insertManualyPath.setOnClickListener(view1 -> {
            Intent intentAddPathManualyActivity = new Intent(getActivity(), AddPathManuallyActivity.class);
            intentAddPathManualyActivity.putExtra("userEmail", user.getEmail());
            startActivity(intentAddPathManualyActivity);
            dismiss();
        });

    }
    private void requestStoragePermissions(){

        externalStoragePermissionLauncher.launch(new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE
        });
    }

}
