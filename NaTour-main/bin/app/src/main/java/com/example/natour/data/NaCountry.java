package com.example.natour.data;

import java.util.ArrayList;

public class NaCountry {
    String name;
    ArrayList<String> regions = new ArrayList<>();
    public NaCountry(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addRegion(String region){
        regions.add(region);
    }

    public ArrayList<String> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<String> regions) {
        this.regions = regions;
    }

}
