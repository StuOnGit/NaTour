package com.example.natour.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.natour.application.NaTourApplication;
import com.example.natour.R;
import com.example.natour.auth0.auth0AuthenticationEmbedded;
import com.example.natour.dao.UserDao;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.dialogs.ErrorDialog;
import com.example.natour.utils.dialogs.LoadingDialog;
import com.example.natour.utils.TypeOfError;


public class StartActivity extends AppCompatActivity {

    private static final String TAG = "StartActivity";

    private ImageView iconImage;
    private LinearLayout linearLayout;
    private Button register, login;
    private static final int STORAGE_PERMISSION_CODE = 1;

    private Button loginWeb;
    private Button loginEmbedded;
    private Button signupEmbedded;
    private ImageView loginFacebook;
    private ImageView loginGoogle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starting");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //setUpPermissions();
        setUpActivity();
    }


    private void setUpActivity(){
        Log.d(TAG, "setUpActivity: setting up the StartActivity");
        iconImage = findViewById(R.id.image_logo);
      //  linearLayout = findViewById(R.id.linear_layout);
        register = findViewById(R.id.register_button);
        login = findViewById(R.id.login_button);

     //   loginWeb = findViewById(R.id.login_web);
     //   loginEmbedded = findViewById(R.id.login_embedded_auth0);
     //   signupEmbedded = findViewById(R.id.signup_embedded);

        loginFacebook = findViewById(R.id.login_facebook);
        loginGoogle = findViewById(R.id.login_google);

        //setUpAnimation();
        enableLogin();
        enableRegistration();

//        enableLoginWeb();
//        enableLoginEmbedded();
//        enableSingupEmbedded();

        enableLoginFacebbok();
        enableLoginGoogle();

    }

    private void enableLoginFacebbok() {
        Context context = this;
        loginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth0AuthenticationEmbedded auth0AuthenticationEmbedded = new auth0AuthenticationEmbedded(getString(R.string.com_auth0_client_id), getString(R.string.com_auth0_domain), getString(R.string.database_connections));
                LoadingDialog loadingDialog = new LoadingDialog(StartActivity.this);
                runOnUiThread(loadingDialog::startLoadingDialog);
                auth0AuthenticationEmbedded.loginFacebook(context,
                        user -> {
                            runOnUiThread(() -> {
                                loadingDialog.dismissDialog();
                                Intent myIntent = new Intent(StartActivity.this, MainActivity.class);
                                myIntent.putExtra("Utente", user);
                                ((NaTourApplication)getApplication()).user = user;
                                startActivity(myIntent);
                                finish();
                            });
                            new UserDao().insertWithFBorGoogle(user, context,
                                    response -> {
                                            Log.d(TAG, response + "\nInserimento con provider.");
                                    }, new FailListener() {
                                        @Override
                                        public void onAuthFail(String msg) {
                                            runOnUiThread(() -> {
                                                new ErrorDialog(StartActivity.this).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                                            });
                                        }

                                        @Override
                                        public void onDBFail(String msg) {
                                            runOnUiThread(() -> {
                                                new ErrorDialog(StartActivity.this).startDialog(TypeOfError.DB_ERROR.toString(), msg);
                                            });
                                        }

                                        @Override
                                        public void onLocalProblem(String msg) {
                                            runOnUiThread(() -> {

                                                new ErrorDialog(StartActivity.this).startDialog(TypeOfError.LOCAL_PROBLEM.toString(), msg);
                                            });

                                        }

                                        @Override
                                        public void onJSONException(String msg) {
                                            runOnUiThread(() -> {

                                                new ErrorDialog(StartActivity.this).startDialog(TypeOfError.JSON_EXCEPTION.toString(), msg);
                                            });
                                        }

                                        @Override
                                        public void onHTTPError(String msg) {
                                            runOnUiThread(()->{
                                                new ErrorDialog(StartActivity.this).startDialog(TypeOfError.HTTP_ERROR.toString(), msg);
                                            });
                                        }
                                    });
                        }, new FailListener() {
                            @Override
                            public void onAuthFail(String msg) {
                                runOnUiThread(() -> {

                                    loadingDialog.dismissDialog();
                                    new ErrorDialog(StartActivity.this).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                                });
                            }

                            @Override
                            public void onDBFail(String msg) {
                                runOnUiThread(() -> {

                                    loadingDialog.dismissDialog();
                                    new ErrorDialog(StartActivity.this).startDialog(TypeOfError.DB_ERROR.toString(), msg);
                                });

                            }

                            @Override
                            public void onLocalProblem(String msg) {
                                runOnUiThread(() -> {

                                    loadingDialog.dismissDialog();
                                    new ErrorDialog(StartActivity.this).startDialog(TypeOfError.LOCAL_PROBLEM.toString(), msg);
                                });

                            }

                            @Override
                            public void onJSONException(String msg) {
                                runOnUiThread(() -> {

                                    loadingDialog.dismissDialog();
                                    new ErrorDialog(StartActivity.this).startDialog(TypeOfError.JSON_EXCEPTION.toString(), msg);
                                });

                            }

                            @Override
                            public void onHTTPError(String msg) {
                                runOnUiThread(() -> {

                                    loadingDialog.dismissDialog();
                                    new ErrorDialog(StartActivity.this).startDialog(TypeOfError.HTTP_ERROR.toString(), msg);
                                });

                            }
                        }
                );
            }
        });
    }

    private void enableLoginGoogle() {
        Context context = this;
        loginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth0AuthenticationEmbedded auth0AuthenticationEmbedded = new auth0AuthenticationEmbedded(getString(R.string.com_auth0_client_id), getString(R.string.com_auth0_domain), getString(R.string.database_connections));
                LoadingDialog loadingDialog = new LoadingDialog(StartActivity.this);
                runOnUiThread(loadingDialog::startLoadingDialog);
                auth0AuthenticationEmbedded.loginGoogle(context,
                        user -> {
                            runOnUiThread(() -> {
                                loadingDialog.dismissDialog();
                                Intent myIntent = new Intent(StartActivity.this, MainActivity.class);
                                myIntent.putExtra("Utente", user);
                                ((NaTourApplication)getApplication()).user = user;
                                startActivity(myIntent);
                                finish();
                            });
                            new UserDao().insertWithFBorGoogle(user, context,
                                    response -> {
                                        Log.d(TAG, response + "\nInserimento con provider.");
                                    }, new FailListener() {
                                        @Override
                                        public void onAuthFail(String msg) {
                                            runOnUiThread(() -> {

                                                new ErrorDialog(StartActivity.this).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                                            });
                                        }

                                        @Override
                                        public void onDBFail(String msg) {
                                            runOnUiThread(() -> {

                                                new ErrorDialog(StartActivity.this).startDialog(TypeOfError.DB_ERROR.toString(), msg);
                                            });
                                        }

                                        @Override
                                        public void onLocalProblem(String msg) {
                                            runOnUiThread(() -> {

                                                new ErrorDialog(StartActivity.this).startDialog(TypeOfError.LOCAL_PROBLEM.toString(), msg);
                                            });

                                        }

                                        @Override
                                        public void onJSONException(String msg) {
                                            runOnUiThread(() -> {

                                                new ErrorDialog(StartActivity.this).startDialog(TypeOfError.JSON_EXCEPTION.toString(), msg);
                                            });
                                        }

                                        @Override
                                        public void onHTTPError(String msg) {

                                            runOnUiThread(() -> {

                                                new ErrorDialog(StartActivity.this).startDialog(TypeOfError.HTTP_ERROR.toString(), msg);
                                            });
                                        }
                                    });
                        }, new FailListener() {
                            @Override
                            public void onAuthFail(String msg) {
                                runOnUiThread(() -> {
                                    loadingDialog.dismissDialog();
                                    new ErrorDialog(StartActivity.this).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                                });
                            }

                            @Override
                            public void onDBFail(String msg) {
                                runOnUiThread(() -> {
                                    loadingDialog.dismissDialog();
                                    new ErrorDialog(StartActivity.this).startDialog(TypeOfError.DB_ERROR.toString(), msg);
                                });

                            }

                            @Override
                            public void onLocalProblem(String msg) {
                                runOnUiThread(() -> {
                                    loadingDialog.dismissDialog();
                                    new ErrorDialog(StartActivity.this).startDialog(TypeOfError.LOCAL_PROBLEM.toString(), msg);
                                });

                            }

                            @Override
                            public void onJSONException(String msg) {
                                runOnUiThread(() -> {
                                    loadingDialog.dismissDialog();
                                    new ErrorDialog(StartActivity.this).startDialog(TypeOfError.JSON_EXCEPTION.toString(), msg);
                                });

                            }

                            @Override
                            public void onHTTPError(String msg) {
                                runOnUiThread(() -> {
                                    loadingDialog.dismissDialog();
                                    new ErrorDialog(StartActivity.this).startDialog(TypeOfError.HTTP_ERROR.toString(), msg);
                                });

                            }
                        }
                );
            }
        });
    }


//    private void enableLoginEmbedded() {
//        String email = "giuseppebove3000@gmail.com";
//        String password = "Pippo123!";
//        loginEmbedded.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                auth0AuthenticationEmbedded auth0AuthenticationEmbedded = new auth0AuthenticationEmbedded(getString(R.string.com_auth0_client_id), getString(R.string.com_auth0_domain), getString(R.string.database_connections));
//                auth0AuthenticationEmbedded.login(email, password);
//            }
//        });
//
//    }

//    private void enableSingupEmbedded() {
//
//            String email = "giuseppebove3000@gmail.com";
//            String password = "Pippo123!";
//            signupEmbedded.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    auth0AuthenticationEmbedded auth0AuthenticationEmbedded = new auth0AuthenticationEmbedded(getString(R.string.com_auth0_client_id), getString(R.string.com_auth0_domain), getString(R.string.database_connections));
//                    auth0AuthenticationEmbedded.signup(email, password);
//                }
//            });
//    }

//    private void enableLoginWeb() {
//        Context context = this;
//        loginWeb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                auth0AuthenticationWeb auth0AuthenticationWeb = new auth0AuthenticationWeb(getString(R.string.com_auth0_client_id), getString(R.string.com_auth0_domain));
//                auth0AuthenticationWeb.login(context);
//            }
//        });
//    }


    private void setUpAnimation(){
        Log.d(TAG, "setUpAnimation: setting up the animations");
        linearLayout.animate().alpha(0f).setDuration(1);
        loginFacebook.animate().alpha(0f).setDuration(1);
        loginGoogle.animate().alpha(0f).setDuration(1);
        TranslateAnimation animation = new TranslateAnimation(0, 0 ,0, -1500);
        animation.setDuration(1000);
        animation.setFillAfter(false);
        animation.setAnimationListener(new MyAnimationListener());

        iconImage.setAnimation(animation);
    }


    private void enableLogin(){
        Log.d(TAG,"enableLogin: enabling log in" );
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);

               // finish(); //conclude l'activity
            }
        });
    }

    private void enableRegistration() {
        Log.d(TAG, "enableRegistration: enabling registration");
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private class MyAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }
        @Override
        public void onAnimationEnd(Animation animation) {
                    iconImage.clearAnimation();
                    iconImage.setVisibility(View.INVISIBLE);
                    linearLayout.animate().alpha(1f).setDuration(1000);
                    loginFacebook.animate().alpha(1f).setDuration(1000);
                    loginGoogle.animate().alpha(1f).setDuration(1000);
        }
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

/*    private void setUpPermissions(){
        if(ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(StartActivity.this, "already given permission",Toast.LENGTH_SHORT);
        }else{
            requestPermissions();
        }

        *//*if(ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(StartActivity.this, "already given permission for GPS",Toast.LENGTH_SHORT);
        }else{
            requestGPSPermission();
        }*//*
    }*/

    private void requestGPSPermission() {
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                            }
                        }
                );

        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void requestPermissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to config the application")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT);
            }
        }
    }
}
