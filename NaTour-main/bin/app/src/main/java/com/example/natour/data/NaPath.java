package com.example.natour.data;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.natour.enumeration.DifficultyEnum;
import com.example.natour.interfacce.Percorso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class NaPath implements Percorso, Parcelable {

    private static final String TAG = "NaPath";
    private String pathName;
    private DifficultyEnum difficultyEnum;
    private GeoPoint startPath;
    private GeoPoint endPath;
    private Integer durata;
    private String descrizione;
    private Boolean accessibilitaDisabili;
    private JSONArray percorso_geografico;

    private ArrayList<Photo> photos;
    private ArrayList<Sights> sights;
    private String emailCreatore;
    private ArrayList<Review> review;
    private ArrayList<Compilation> compilations;
    private String region;
    private String country;

    public NaPath(String pathName, DifficultyEnum difficultyEnum, GeoPoint startPath, GeoPoint endPath, String region,String country, Integer durataMinuti, String descrizione, Boolean accessibilitaDisabili, JSONArray percorso_geografico, String emailCreatore ) {
        this.pathName = pathName;
        this.difficultyEnum = difficultyEnum;
        this.startPath = startPath;
        this.endPath = endPath;
        this.durata = durataMinuti;
        this.descrizione = descrizione;
        this.accessibilitaDisabili = accessibilitaDisabili;
        this.percorso_geografico = percorso_geografico;
        this.emailCreatore = emailCreatore;
        this.region = region;
        this.country = country;
    }

    protected NaPath(Parcel in) {
        pathName = in.readString();
        int difficult = in.readInt();
        if(difficult == 0){
            this.difficultyEnum = DifficultyEnum.NULL;
        }else if(difficult == 1){
            this.difficultyEnum = DifficultyEnum.LOW;
        }else if(difficult == 2){
            this.difficultyEnum = DifficultyEnum.AVERAGE;
        }else if(difficult == 3){
            this.difficultyEnum = DifficultyEnum.HIGH;
        }else if(difficult == 4){
            this.difficultyEnum = DifficultyEnum.EXPERT;
        }
        String percorsoStr = in.readString();
        try {
            percorso_geografico = new JSONArray(percorsoStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startPath = in.readParcelable(GeoPoint.class.getClassLoader());
        endPath = in.readParcelable(GeoPoint.class.getClassLoader());
        if (in.readByte() == 0) {
            durata = null;
        } else {
            durata = in.readInt();
        }
        descrizione = in.readString();
        byte tmpAccessibilitaDisabili = in.readByte();
        accessibilitaDisabili = tmpAccessibilitaDisabili == 0 ? null : tmpAccessibilitaDisabili == 1;
        emailCreatore = in.readString();
        region = in.readString();
        country = in.readString();
    }

    public static final Creator<NaPath> CREATOR = new Creator<NaPath>() {
        @Override
        public NaPath createFromParcel(Parcel in) {
            return new NaPath(in);
        }

        @Override
        public NaPath[] newArray(int size) {
            return new NaPath[size];
        }
    };

    public NaPath() {}

    public JSONArray getPercorso_geografico() {
        return percorso_geografico;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public Boolean isAccessibilitaDisabili() {
        return accessibilitaDisabili;
    }

    public ArrayList<Sights> getSights() {
        return sights;
    }

    public void setSights(ArrayList<Sights> sights) {
        this.sights = sights;
    }

    public String getEmailCreatore() {
        return emailCreatore;
    }

    public void setCreatore(String emailCreatore) {
        this.emailCreatore = emailCreatore;
    }

    public ArrayList<Review> getRecensioni() {
        return review;
    }

    public void setRecensioni(ArrayList<Review> review) {
        this.review = review;
    }

    public ArrayList<Compilation> getCompilations() {
        return compilations;
    }

    public void setCompilations(ArrayList<Compilation> compilations) {
        this.compilations = compilations;
    }

    public void setAccessibilitaDisabili(Boolean accessibilitaDisabili) {
        this.accessibilitaDisabili = accessibilitaDisabili;
    }
    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public DifficultyEnum getDifficultyEnum() {
        return difficultyEnum;
    }

    public void setDifficultyEnum(DifficultyEnum difficultyEnum) {
        this.difficultyEnum = difficultyEnum;
    }


    public Integer getDurata() {
        return durata;
    }

    public void setDurata(Integer durata) {
        this.durata = durata;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public GeoPoint getStartPath() {
        return startPath;
    }

    @Override
    public GeoPoint getEndPath() {
        return endPath;
    }

    @Override
    public void setStartPath(GeoPoint startPath) {
            this.startPath = startPath;
    }

    @Override
    public void setEndPath(GeoPoint endPath) {
            this.endPath = endPath;
    }

    public String getRegion(){
        return region;
    }

    public String getCountry(){
        return country;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pathName);
        int difficult = this.difficultyEnum.ordinal();
        dest.writeInt(difficult);
        String percorsoStr = String.valueOf(this.percorso_geografico);
        dest.writeString(percorsoStr);
        dest.writeParcelable(startPath, flags);
        dest.writeParcelable(endPath, flags);
        if (durata == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(durata);
        }
        dest.writeString(descrizione);
        dest.writeByte((byte) (accessibilitaDisabili == null ? 0 : accessibilitaDisabili ? 1 : 2));
        dest.writeString(emailCreatore);
        dest.writeString(region);
        dest.writeString(country);
    }

    public NaPath jsonToNaPath(JSONObject naPathJSON){
        NaPath naPath = null;
        try{
            String namePath = naPathJSON.getString("nome");
            String difficulty = naPathJSON.getString("difficolta");
            Double inizio_latitudine = naPathJSON.getDouble("inizio_latitudine");
            Double fine_latitudine = naPathJSON.getDouble("fine_latitudine");
            Double inizio_longitudine = naPathJSON.getDouble("inizio_longitudine");
            Double fine_longitudine = naPathJSON.getDouble("fine_longitudine");
            Integer durata = naPathJSON.getInt("durata");
            String descrizione = naPathJSON.getString("descrizione");
            Boolean accessibiltaDisabili = !naPathJSON.getString("accessibilitaDisabili").equals("0");
            JSONArray percorso_geografico = new JSONArray(naPathJSON.getString("percorso_geografico"));
            String emailCreatore = naPathJSON.getString("email_creatore");
            String region = naPathJSON.getString("region");
            String country = naPathJSON.getString("country");

            //Setting for the class NaPath
            DifficultyEnum difficultyEnum = DifficultyEnum.LOW;
            if (difficulty.equals("BASSA")){
                difficultyEnum = DifficultyEnum.LOW;
            }else if(difficulty.equals("MEDIA")){
                difficultyEnum = DifficultyEnum.AVERAGE;
            }else if(difficulty.equals("ALTA")){
                difficultyEnum = DifficultyEnum.HIGH;
            }else if(difficulty.equals("ESPERTO")){
                difficultyEnum = DifficultyEnum.EXPERT;
            }
            GeoPoint startPath = new GeoPoint(inizio_latitudine,inizio_longitudine);
            GeoPoint endPath = new GeoPoint(fine_latitudine, fine_longitudine);
            naPath = new NaPath(namePath, difficultyEnum, startPath, endPath, region, country, durata, descrizione, accessibiltaDisabili, percorso_geografico, emailCreatore);

        }catch (JSONException e){
            Log.d(TAG, e.getMessage());
        }
        return naPath;
    }
}
