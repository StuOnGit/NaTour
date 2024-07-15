package com.example.natour.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.natour.R;
import com.example.natour.application.NaTourApplication;
import com.example.natour.auth0.auth0AuthenticationEmbedded;
import com.example.natour.dao.UserDao;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.TypeOfError;
import com.example.natour.utils.dialogs.ErrorDialog;
import com.example.natour.utils.dialogs.LoadingDialog;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private Button logIn;
    private EditText email, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starting");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpActivity();
    }

    private void setUpActivity(){
        Log.d(TAG, "settingUpActivity; setting Up LoginActivity");
        logIn = findViewById(R.id.log_in);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        logginIn();
    }

    private void logginIn(){
        logIn.setOnClickListener(v -> authenticateUser());
    }

    private void authenticateUser(){
        auth0AuthenticationEmbedded auth0AuthenticationEmbedded = new auth0AuthenticationEmbedded(getString(R.string.com_auth0_client_id), getString(R.string.com_auth0_domain), getString(R.string.database_connections));
        LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
        loadingDialog.startLoadingDialog();
        String emailStrNoSpace = email.getText().toString().trim();
        auth0AuthenticationEmbedded.login2(emailStrNoSpace, password.getText().toString(), this,
                emailStr -> {
                    new UserDao().reqUserbyEmail(emailStr, this,
                            user -> {
                                runOnUiThread(() -> {
                                    loadingDialog.dismissDialog();
                                    Intent myIntent = new Intent(this, MainActivity.class);
                                    myIntent.putExtra("Utente", user);
                                    ((NaTourApplication)getApplication()).user = user;
                                    startActivity(myIntent);
                                    finish();
                                });
                            }, new FailListener() {
                                @Override
                                public void onAuthFail(String msg) {
                                    runOnUiThread(() -> {
                                        loadingDialog.dismissDialog();
                                        new ErrorDialog(LoginActivity.this).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                                    });
                                }

                                @Override
                                public void onDBFail(String msg) {
                                    runOnUiThread(() -> {
                                        loadingDialog.dismissDialog();
                                        new ErrorDialog(LoginActivity.this).startDialog(TypeOfError.DB_ERROR.toString(), msg);
                                    });

                                }

                                @Override
                                public void onLocalProblem(String msg) {
                                    runOnUiThread(() -> {
                                        loadingDialog.dismissDialog();
                                        new ErrorDialog(LoginActivity.this).startDialog(TypeOfError.LOCAL_PROBLEM.toString(), msg);
                                    });

                                }

                                @Override
                                public void onJSONException(String msg) {
                                    runOnUiThread(() -> {
                                        loadingDialog.dismissDialog();
                                        new ErrorDialog(LoginActivity.this).startDialog(TypeOfError.JSON_EXCEPTION.toString(), msg);
                                    });

                                }

                                @Override
                                public void onHTTPError(String msg) {
                                    runOnUiThread(() -> {
                                        loadingDialog.dismissDialog();
                                        new ErrorDialog(LoginActivity.this).startDialog(TypeOfError.HTTP_ERROR.toString(), msg);
                                    });

                                }
                            }
                    );
                }, new FailListener() {
                    @Override
                    public void onAuthFail(String msg) {
                        runOnUiThread(()->{
                            loadingDialog.dismissDialog();
                            new ErrorDialog(LoginActivity.this).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                        });
                    }

                    @Override
                    public void onDBFail(String msg) {
                        runOnUiThread(loadingDialog::dismissDialog);
                    }

                    @Override
                    public void onLocalProblem(String msg) {
                        runOnUiThread(loadingDialog::dismissDialog);
                    }

                    @Override
                    public void onJSONException(String msg) {
                        runOnUiThread(loadingDialog::dismissDialog);
                    }

                    @Override
                    public void onHTTPError(String msg) {
                        runOnUiThread(loadingDialog::dismissDialog);
                    }
                }
        );
    }

}
