package com.example.natour.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Compilation implements Parcelable {

    private String name;

    private ArrayList<NaPath> paths = new ArrayList<>();
    private String emailCreatore;

    public Compilation(String name, String emailCreatore){
        this.emailCreatore = emailCreatore;
        this.name = name;
    };

    protected Compilation(Parcel in) {
        name = in.readString();
        paths = in.createTypedArrayList(NaPath.CREATOR);
        emailCreatore = in.readString();
    }

    public static final Creator<Compilation> CREATOR = new Creator<Compilation>() {
        @Override
        public Compilation createFromParcel(Parcel in) {
            return new Compilation(in);
        }

        @Override
        public Compilation[] newArray(int size) {
            return new Compilation[size];
        }
    };

    public void setPaths(ArrayList<NaPath> paths) {
        this.paths = paths;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<NaPath> getPaths() {
        return paths;
    }

    public void addPath(NaPath path) {
        this.paths.add(path) ;
    }

    public String getEmailCreatore() {
        return emailCreatore;
    }

    public void setEmailCreatore(String emailCreatore) {
        this.emailCreatore = emailCreatore;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeTypedList(paths);
        parcel.writeString(emailCreatore);
    }
}
