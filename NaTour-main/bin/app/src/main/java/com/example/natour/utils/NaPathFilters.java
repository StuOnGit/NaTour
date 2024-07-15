package com.example.natour.utils;

import com.example.natour.enumeration.DifficultyEnum;

public class NaPathFilters {
    private String region;
    private String country;
    private Boolean accessible;
    private Integer minDuration;
    private DifficultyEnum difficult;
    private Boolean filtered = false;

    public NaPathFilters(String region, String country, Boolean accessible, Integer minDuration, DifficultyEnum difficult){
        this.region = region;
        this.country = country;
        this.accessible = accessible;
        this.minDuration = minDuration;
        this.difficult = difficult;
    }

    public Boolean getFiltered() {
        return filtered;
    }

    public void setFiltered(Boolean filtered) {
        this.filtered = filtered;
    }

    public NaPathFilters(){}

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    public Boolean getAccessible() {
        return accessible;
    }

    public Integer getMinDuration() {
        return minDuration;
    }

    public DifficultyEnum getDifficult(){
        return difficult;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setAccessible(Boolean accessible) {
        this.accessible = accessible;
    }

    public void setMinDuration(Integer minDuration) {
        this.minDuration = minDuration;
    }

    public void setDifficult(DifficultyEnum difficult) {
        this.difficult = difficult;
    }
}
