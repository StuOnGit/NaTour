package com.example.natour.controllers;

import android.util.Log;

import com.example.natour.interfacce.PasswordError;

import java.util.regex.Pattern;

public class ControllerRegistration {
    private static final String TAG = "ControllerRegistration";

    //null when check is OK
    public String checkName(String name, String regex){
        String ret_String = null;
        name = name.trim();
        if(name.isEmpty()){
            ret_String = "E' vuoto";
            Log.d(TAG, "checkName:"+ ret_String);
        }else{
            if(!Pattern.matches(regex, name)) {
                ret_String = "Invalid";
                Log.d(TAG, "checkName:" + ret_String);
            }
        }
        return ret_String;
    }
    //null when check is OK
    public Integer checkPassword(String password, String regex, String confirmedPassword){
        Integer passwordError = null;
        password = password.trim();
        confirmedPassword = confirmedPassword.trim();
        if(password.isEmpty()){
            passwordError = PasswordError.EMPTY;
            Log.d(TAG, "checkPassword: " + "password empty");
        }else{
            if(!Pattern.matches(regex, password)){
                passwordError = PasswordError.REGEX;
                Log.d(TAG, "checkPassword: " + "regex don't match");
            }else{
                if(!password.equals(confirmedPassword) ) {
                    passwordError = PasswordError.NOT_EQUAL;
                    Log.d(TAG, "checkPassword: " + "passwords not equal");
                }
             }
        }
        return passwordError;
    }


}
