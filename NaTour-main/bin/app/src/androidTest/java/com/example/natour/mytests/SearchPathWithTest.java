package com.example.natour.mytests;

import static org.junit.Assert.*;

import com.example.natour.controllers.ControllerSearch;
import com.example.natour.data.NaPath;
import com.example.natour.enumeration.DifficultyEnum;
import com.example.natour.utils.NaPathFilters;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class SearchPathWithTest {
    // Test White Box
    ControllerSearch controllerSearch = new ControllerSearch();
    ArrayList<NaPath> naPaths = new ArrayList<>();
    NaPath naPath;
    @Before
    public void populateArray() throws JSONException {
        GeoPoint startPoint = new GeoPoint(36.95299, -121.76148);
        GeoPoint endPoint = new GeoPoint(36.95566, -121.76042);
        String StringPointsArray =
                "[{\"latitude\": 36.95299, \"longitude\": -121.76148}," +
                " {\"latitude\": 36.95304, \"longitude\": -121.76135}, " +
                "{\"latitude\": 36.95303, \"longitude\": -121.76128}," +
                " {\"latitude\": 36.95306, \"longitude\": -121.76083}, " +
                "{\"latitude\": 36.95305, \"longitude\": -121.76062}, " +
                "{\"latitude\": 36.95341, \"longitude\": -121.76066}," +
                " {\"latitude\": 36.95365, \"longitude\": -121.76062}," +
                " {\"latitude\": 36.954, \"longitude\": -121.76064}," +
                "{\"latitude\": 36.95437, \"longitude\": -121.76064}," +
                " {\"latitude\": 36.95467, \"longitude\": -121.76062}," +
                " {\"latitude\": 36.95493, \"longitude\": -121.76057}, " +
                "{\"latitude\": 36.95505, \"longitude\": -121.76052}," +
                " {\"latitude\": 36.95518, \"longitude\": -121.76045}," +
                " {\"latitude\": 36.95566, \"longitude\": -121.76038}," +
                " {\"latitude\": 36.95566, \"longitude\": -121.76042}," +
                "{\"latitude\": 36.95299, \"longitude\": -121.76148}," +
                " {\"latitude\": 36.95304, \"longitude\": -121.76135}, " +
                "{\"latitude\": 36.95303, \"longitude\": -121.76128}," +
                " {\"latitude\": 36.95306, \"longitude\": -121.76083}," +
                " {\"latitude\": 36.95305, \"longitude\": -121.76062}, " +
                "{\"latitude\": 36.95341, \"longitude\": -121.76066}," +
                " {\"latitude\": 36.95365, \"longitude\": -121.76062}, " +
                "{\"latitude\": 36.954, \"longitude\": -121.76064}, " +
                "{\"latitude\": 36.95437, \"longitude\": -121.76064}," +
                " {\"latitude\": 36.95467, \"longitude\": -121.76062}," +
                " {\"latitude\": 36.95493, \"longitude\": -121.76057}, " +
                "{\"latitude\": 36.95505, \"longitude\": -121.76052}, " +
                "{\"latitude\": 36.95518, \"longitude\": -121.76045}," +
                " {\"latitude\": 36.95566, \"longitude\": -121.76038}," +
                " {\"latitude\": 36.95566, \"longitude\": -121.76042}]";

        JSONArray percorso = new JSONArray(StringPointsArray);
        naPath = new NaPath("TestPath",
                DifficultyEnum.EXPERT,
                startPoint,
                endPoint,
                "Campania",
                "Italia",
                30,
                "TestDescrizione",
                true,
                percorso,
                "destasiofrancesco2000@gmail.com");
        naPaths.add(naPath);
    }
    @Test // 1, 2, 13, 15
    public void searchPathWithFilteredFalseTest() {
        NaPathFilters naPathFilters = new NaPathFilters();
        ArrayList<NaPath> pathsTest = controllerSearch.searchPathWith("TestPath", naPaths, naPathFilters);
        assertEquals(1, pathsTest.size());
    }

    @Test // 1, 2, 3, 5, 7, 9, 11, 13, 14, 15
    public void searchPathWithContainName(){
        NaPathFilters naPathFilters = new NaPathFilters();
        naPathFilters.setFiltered(true);
        ArrayList<NaPath> pathsTest = controllerSearch.searchPathWith("NoPathTest", naPaths, naPathFilters);
        assertEquals(0, pathsTest.size());
    }

    @Test // 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 15
    public void searchPathWithFilterCompleted(){
        NaPathFilters naPathFilters = new NaPathFilters("Campania", "Italia",true, 30, DifficultyEnum.EXPERT);
        naPathFilters.setFiltered(true);
        ArrayList<NaPath>  pathsTest = controllerSearch.searchPathWith("TestPath", naPaths, naPathFilters);
        assertEquals(1, pathsTest.size());
    }

}