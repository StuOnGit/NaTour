package com.example.natour.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.natour.data.User;

public class ControllerAccount {

   private static final String TAG = "ControllerAccount";
   private User user;

   public void setUser(Intent intent){
       Bundle extras = intent.getExtras();
       if(extras == null){
           Log.d(TAG, "extras is null, user not setted");
       }else{
           user = extras.getParcelable("User");
       }
   }

   public void setUser(User user){
       this.user = user;
   }

   public User getUser(){
       return user;
   }
}
