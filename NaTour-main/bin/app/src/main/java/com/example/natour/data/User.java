package com.example.natour.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {

    private String username;
    private String name;
    private String surname;
    private String email; // id entificativo
    private ArrayList<Compilation> compilations;
    private String imageAccountKey = null;


    public User(String username, String name, String surname, String email) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public User() {
    }

    public void setImageAccountKey(String imageAccountKey) {
        this.imageAccountKey = imageAccountKey;
    }

    public String getImageAccountKey() {
        return imageAccountKey;
    }

    protected User(Parcel in) {
        username = in.readString();
        name = in.readString();
        surname = in.readString();
        email = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public ArrayList<Compilation> getCompilations() {
        return compilations;
    }

    public void setCompilations(ArrayList<Compilation> compilations) {
        this.compilations = compilations;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(username);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(email);
    }

}
