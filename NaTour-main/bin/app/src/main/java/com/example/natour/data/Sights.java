package com.example.natour.data;

import com.example.natour.enumeration.SightTypeEnum;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class Sights { //punti di interesse

    private String name;
    private GeoPoint location;
    private SightTypeEnum sightType;
    private NaPath path;


    public Sights(String name, GeoPoint location, SightTypeEnum sightType, NaPath path) {
        this.name = name;
        this.location = location;
        this.sightType = sightType;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public SightTypeEnum getSightType() {
        return sightType;
    }

    public void setSightType(SightTypeEnum sightType) {
        this.sightType = sightType;
    }

    public NaPath getPath() {
        return path;
    }

    public void setPaths(NaPath path) {
        this.path = path;
    }
}
