package com.example.natour.data;

public class Photo {
    private String s3_key;
    private String emailUtente;
    public Photo(String s3_key, String emailUtente){
        this.s3_key = s3_key;
        this.emailUtente = emailUtente;
    }

    public String getEmailUtente() {
        return emailUtente;
    }

    public String getS3_key() {
        return s3_key;
    }

}
