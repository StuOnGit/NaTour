package com.example.natour.interfacce;

import org.osmdroid.util.GeoPoint;

public interface Percorso {

     GeoPoint getStartPath();

     GeoPoint getEndPath();

     void setStartPath(GeoPoint startPath);

     void setEndPath(GeoPoint endPath);

}
