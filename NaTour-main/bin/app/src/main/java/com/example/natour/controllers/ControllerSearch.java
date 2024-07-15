package com.example.natour.controllers;

import android.util.Log;

import com.example.natour.data.NaPath;
import com.example.natour.data.User;
import com.example.natour.enumeration.DifficultyEnum;
import com.example.natour.utils.NaPathFilters;

import java.util.ArrayList;

public class ControllerSearch {

    private static final String TAG = "ControllerSearch";
    public ArrayList<User> searchUserWith(String data, ArrayList<User> users_result) {
        ArrayList<User> searched_users = new ArrayList<>();
        for (User user : users_result) {
            if (user.getEmail().contains(data)) {
                searched_users.add(user);
            }
        }
        return searched_users;
    }

    public ArrayList<NaPath> searchPathWith(String namePath, ArrayList<NaPath> paths, NaPathFilters naPathFilters) {
        Log.d(TAG, "searchPathWith: starting...");
        ArrayList<NaPath> paths_result = new ArrayList<>(paths);
        if (naPathFilters.getFiltered()) {
             Log.d(TAG,"Entrato in filtraggio");
            if (naPathFilters.getAccessible() != null) {
                Log.d(TAG,"Entrato in filtro/accessubilità");
                paths_result = filterDisableAccess(paths_result, naPathFilters.getAccessible());
            }
            if(naPathFilters.getDifficult() != null){
                Log.d(TAG,"Entrato in filtro/difficoltà");
                paths_result = filterDifficult(paths_result, naPathFilters.getDifficult());
            }
            if(naPathFilters.getCountry() != null){
                Log.d(TAG,"Entrato in filtro/country");
                paths_result = filterCountry(paths_result, naPathFilters.getCountry());
            }
            if(naPathFilters.getRegion() != null){
                Log.d(TAG,"Sono entrato in filtro/region");
                paths_result = filterRegion(paths_result, naPathFilters.getRegion());
            }
            if(naPathFilters.getMinDuration() != null){
                Log.d(TAG, "Entrato in filtro/minDuration");
                paths_result = filterDuration(paths_result, naPathFilters.getMinDuration());
            }
        }
        paths_result.removeIf(path -> !(path.getPathName().contains(namePath)));
        System.out.println("Size of paths_result: " + paths_result.size());
        return paths_result;

    }




    private ArrayList<NaPath> filterDisableAccess(ArrayList<NaPath> paths, Boolean accessible) {
        ArrayList<NaPath> paths_result = new ArrayList<>();
        if (!paths.isEmpty()) {
            for (NaPath path : paths) {
                if (path.isAccessibilitaDisabili() == accessible) {
                    paths_result.add(path);
                }
            }
        }
        return paths_result;
    }

    private ArrayList<NaPath> filterDifficult(ArrayList<NaPath> paths, DifficultyEnum diffcult){
        ArrayList<NaPath> paths_result = new ArrayList<>();
        if(!paths.isEmpty()){
            for (NaPath path: paths){
                if(path.getDifficultyEnum() == diffcult){
                    paths_result.add(path);
                }
            }
        }
        return paths_result;
    }

    private ArrayList<NaPath> filterCountry(ArrayList<NaPath> paths, String country) {
        ArrayList<NaPath> paths_result = new ArrayList<>();
        if(!paths.isEmpty()){
            for (NaPath path: paths){
                if(path.getCountry().equals(country)){
                    paths_result.add(path);
                }
            }
        }
        return paths_result;
    }

    private ArrayList<NaPath> filterRegion(ArrayList<NaPath> paths, String region) {
        ArrayList<NaPath> paths_result = new ArrayList<>();
        if(!paths.isEmpty()){
            for (NaPath path : paths){
                if(path.getRegion().equals(region)){
                    paths_result.add(path);
                }
            }
        }
        return  paths_result;
    }

    private ArrayList<NaPath> filterDuration(ArrayList<NaPath> paths, Integer minDuration) {
        ArrayList<NaPath> paths_result = new ArrayList<>();
        if(!paths.isEmpty()){
            for (NaPath path: paths){
                if(path.getDurata() >= minDuration){
                    paths_result.add(path);
                }
            }
        }
        return  paths_result;
    }

}