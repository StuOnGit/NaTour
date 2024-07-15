package com.example.natour.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.natour.R;
import com.example.natour.application.NaTourApplication;
import com.example.natour.auth0.auth0AuthenticationEmbedded;
import com.example.natour.controllers.ControllerRegistration;
import com.example.natour.dao.UserDao;
import com.example.natour.interfacce.FailListener;
import com.example.natour.interfacce.PasswordError;
import com.example.natour.utils.dialogs.ErrorDialog;
import com.example.natour.utils.dialogs.LoadingDialog;
import com.example.natour.utils.TypeOfError;

public class RegisterActivity extends AppCompatActivity {

    private final static String TAG = "RegisterActivity";
    private Button buttonRegister ;

    private EditText inputName;
    private EditText inputGivenName;
    private EditText inputUsername;

    private TextView alreadyHaveAnAccountText;
    //For Registration
    private EditText inputMail;
    private EditText inputPassword;
    private EditText inputConfirmPassword;


    /*final String regexName = "[a-zA-Z]*";
    final String regexUsername = "[a-zA-Z0-9]*";*/
    final String regexName = "^[a-zA-Z]+( [a-zA-Z]+)*$";
    final String regexUsername = "^[a-zA-Z0-9]+( [a-zA-Z0-9]+)*$";
    final String regexMail = "[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,4}";
    final String regexPassword = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$"; // min 1 UpperCase, min 1 special Character, min 8 length, min 1 number

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starting");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setUpActivity();
        setUpRegister();
        setUpalreadyHaveAnAccount();
    }

    private void setUpActivity(){

        Log.d(TAG, "setUpActivity: setting up the activity");
        buttonRegister = findViewById(R.id.registerButton);

        inputName = findViewById(R.id.registerName);
        inputGivenName = findViewById(R.id.registerGivenName);
        inputUsername = findViewById(R.id.registerUsername);

        inputMail = findViewById(R.id.registerMail);
        inputPassword = findViewById(R.id.registerPassword);
        inputConfirmPassword = findViewById(R.id.registerConfirmPassword);
        alreadyHaveAnAccountText = findViewById(R.id.already_an_account_text);

    }

    private boolean checkData(EditText inputName, EditText inputGivenName, EditText inputUsername, EditText inputMail, EditText inputPassword, EditText confirmPassword ){
        if (checkName(inputUsername, regexUsername)
                && checkName(inputName, regexName)
                && checkName(inputGivenName, regexName)
                && checkName(inputMail, regexMail)
                && checkPassword(inputPassword.getText().toString(), confirmPassword.getText().toString())) {
            Log.d(TAG, "checkData: Data are correct");
            return  true;
        }else{
            Log.d(TAG, "checkData: Data are incorrect");
            return false;
        }
    }

    private boolean checkName(EditText editText, String regex){
        ControllerRegistration controllerRegistration = new ControllerRegistration();
        String error = controllerRegistration.checkName(editText.getText().toString(), regex);
        boolean checked = true;
        if(error != null){
            editText.setError(error);
            checked = false;
        };
        return checked;
    }

    private boolean checkPassword(String password, String confirmPassword){
        ControllerRegistration controllerRegistration = new ControllerRegistration();
        Integer error = controllerRegistration.checkPassword(password, regexPassword, confirmPassword);
        String errorStr = null;
        if(error == null){
            return true;
        }
        switch (error){
            case PasswordError.EMPTY:
                errorStr = "Password vuota";
                break;
            case PasswordError.NOT_EQUAL:
                errorStr = "Passwords non sono uguali";
                break;
            case PasswordError.REGEX:
                errorStr = "Password deve avere 8 caratteri, di cui uno maiuscolo, uno speciale e un numero";
                break;
            default:
                break;
        }
        if(errorStr != null){
            inputPassword.setError(errorStr);
            return false;
        }else{
            return true;
        }
    }

    private void setUpRegister(){
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkData(inputName, inputGivenName, inputUsername, inputMail, inputPassword, inputConfirmPassword )){
                    Log.d(TAG, "onClick: Input Checked");
                    registerUser();
                }
            }
        });
    }

    private void registerUser(){
        auth0AuthenticationEmbedded auth0AuthenticationEmbedded = new auth0AuthenticationEmbedded(getString(R.string.com_auth0_client_id),getString(R.string.com_auth0_domain), getString(R.string.database_connections));
        LoadingDialog loadingDialog = new LoadingDialog(RegisterActivity.this);
        loadingDialog.startLoadingDialog();

        auth0AuthenticationEmbedded.signup2(inputMail.getText().toString(), inputPassword.getText().toString(), inputUsername.getText().toString(), inputName.getText().toString(), inputGivenName.getText().toString(),
                user -> {
                    new UserDao().insertUser(user, this,
                            onSuccess -> {
                                runOnUiThread(() -> loadingDialog.dismissDialog());
                                Intent myIntent = new Intent(this, MainActivity.class);
                                myIntent.putExtra("Utente", user);
                                ((NaTourApplication)getApplication()).user = user;
                                startActivity(myIntent);
                                finish();
                            }, new FailListener() {
                                @Override
                                public void onAuthFail(String msg) {
                                    runOnUiThread(() -> {
                                        loadingDialog.dismissDialog();
                                    });
                                }

                                @Override
                                public void onDBFail(String msg) {
                                    runOnUiThread(() -> {
                                        loadingDialog.dismissDialog();
                                    });
                                }

                                @Override
                                public void onLocalProblem(String msg) {
                                    runOnUiThread(() -> {
                                        loadingDialog.dismissDialog();
                                    });
                                }

                                @Override
                                public void onJSONException(String msg) {
                                    runOnUiThread(() -> {
                                        loadingDialog.dismissDialog();
                                        new ErrorDialog(RegisterActivity.this).startDialog(TypeOfError.JSON_EXCEPTION.toString(), msg);
                                    });
                                }

                                @Override
                                public void onHTTPError(String msg) {
                                    runOnUiThread(() -> {
                                        loadingDialog.dismissDialog();
                                        new ErrorDialog(RegisterActivity.this).startDialog(TypeOfError.HTTP_ERROR.toString(), msg);
                                    });

                                }
                            }

                    );
                }, new FailListener() {
                    @Override
                    public void onAuthFail(String msg) {
                        runOnUiThread(()->{
                            loadingDialog.dismissDialog();
                            new ErrorDialog(RegisterActivity.this).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
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

    private void setUpalreadyHaveAnAccount(){
        alreadyHaveAnAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
