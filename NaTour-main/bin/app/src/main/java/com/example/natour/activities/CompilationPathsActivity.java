package com.example.natour.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour.application.NaTourApplication;
import com.example.natour.R;
import com.example.natour.data.Compilation;
import com.example.natour.data.NaPath;
import com.example.natour.data.User;
import com.example.natour.utils.cardadapter.PathCardAdapter;

import java.util.ArrayList;

public class CompilationPathsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Compilation compilation;
    TextView title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paths_of_compilation);
        setUpActivity();

    }

    public void setUpActivity(){
        recyclerView = findViewById(R.id.recycler_view_paths_compilation);
        compilation = getIntent().getParcelableExtra("compilation");
        title = findViewById(R.id.titleCompilation);
        ArrayList<NaPath> naPaths = compilation.getPaths();

        title.setText(compilation.getName());
        System.out.println("Compilation paths size: " + compilation.getPaths().size());
        User user = ((NaTourApplication) getApplication()).user;
        PathCardAdapter adapter = new PathCardAdapter(this,naPaths, user);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
