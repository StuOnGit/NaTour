package com.example.natour.data;

import com.example.natour.enumeration.ValutazioneEnum;

public class Review {

    private String description;
    private ValutazioneEnum valutazione;
    private NaPath naPath;
    private String emailUtente;

    public Review(String description, ValutazioneEnum valutazione, NaPath naPath, String emailUtente){
        this.description = description;
        this.valutazione = valutazione;
        this.naPath = naPath;
        this.emailUtente = emailUtente;
    }

    public Review(NaPath naPath){
        this.naPath = naPath;
    }

    public void setEmailUtente(String emailUtente){
        this.emailUtente = emailUtente;
    }

    public String getEmailUtente() {
        return emailUtente;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ValutazioneEnum getValutazione() {
        return valutazione;
    }

    public void setValutazione(ValutazioneEnum valutazione) {
        this.valutazione = valutazione;
    }

    public NaPath getPath() {
        return naPath;
    }
}
