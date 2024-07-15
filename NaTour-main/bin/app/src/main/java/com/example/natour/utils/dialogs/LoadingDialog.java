package com.example.natour.utils.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.natour.R;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;
    private Boolean progress;

    public LoadingDialog(Activity activity){
        this.activity = activity;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public Boolean getProgress() {
        return progress;
    }

    public void setProgress(Boolean progress) {
        this.progress = progress;
    }
}
