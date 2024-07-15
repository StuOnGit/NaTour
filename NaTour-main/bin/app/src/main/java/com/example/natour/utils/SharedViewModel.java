package com.example.natour.utils;

import android.graphics.Bitmap;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.natour.data.Photo;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<Bitmap> photoLiveData = new MutableLiveData<Bitmap>();

    public LiveData<Bitmap> getPhoto(){
        return photoLiveData;
    }

    public void setPhoto(Bitmap profilePhotoBitMap) {
        photoLiveData.postValue(profilePhotoBitMap);
    }

}
