package com.example.natour.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.natour.R;
import com.example.natour.data.NaPath;
import com.example.natour.utils.MapUtils;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;

public class SeeMapActivity extends AppCompatActivity {

    private static final String TAG = "SeeMapActivity" ;
    MapView mapView;
    MapUtils mapUtils;
    NaPath naPath;
    Button button;
    TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_map);
        naPath = getIntent().getParcelableExtra("naPath");
        button = findViewById(R.id.button_back_see_activity);
        title = findViewById(R.id.text_title_see_activity);
        setUpMapUI();

        button.setOnClickListener(view -> finish());

        title.setText(naPath.getPathName());
    }

    private void setUpMapUI(){
        mapView = findViewById(R.id.map_see_map_activity);
        mapUtils = new MapUtils(mapView, SeeMapActivity.this);
        Configuration.getInstance().setUserAgentValue("myOwnUserAgent/1.0");
        mapUtils.showPath(naPath, SeeMapActivity.this);
    }


}
