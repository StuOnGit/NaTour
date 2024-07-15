package com.example.natour.activities;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.natour.R;
import com.example.natour.dao.CompilationDao;
import com.example.natour.data.Compilation;
import com.example.natour.data.User;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.SharedViewModel;
import com.example.natour.utils.TypeOfError;
import com.example.natour.utils.dialogs.ChooseFileDialogFragment;
import com.example.natour.utils.dialogs.ErrorDialog;
import com.example.natour.utils.dialogs.LoadingDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    private FloatingActionButton plusButton;
    private FloatingActionButton addCompilationButton;
    private FloatingActionButton addPathButton;
    private boolean clicked = false;
    private User user;
    private NavController navController;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: starting");
        enableNavigation();
        setUpFab();
        Bundle extras;
        if(savedInstanceState == null){
            extras = getIntent().getExtras();
            if(extras == null){
                //Error message, (dovrebbe provare a rientrare)
            }else{
                setUser(extras);
            }
        }
    }

    private void setUser(Bundle user_extras){
        user = user_extras.getParcelable("Utente");
    }

    private void enableNavigation(){
        Log.d(TAG, "enableNavigation: SetUp NavControllor and BottomView");
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
    }


    private Animation rotateOpen() {
        return AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
    }

    private Animation rotateClose() {
        return AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
    }

    private Animation fromBotton() {
        return AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
    }

    private Animation toBottom() {
        return AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
    }

    private void setUpFab() {
        plusButton = findViewById(R.id.main_plus_fab);
        addPathButton = findViewById(R.id.add_path_fab);
        addCompilationButton = findViewById(R.id.compilation_fab);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onPlusButtonClicked();
            }
        });
        addPathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onAddPathButtonClicked();
            }
        });
        addCompilationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onAddCompilationClicked();
            }
        });

    }

    private void onPlusButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        setClickable(clicked);
        clicked = !clicked;
    }

    private void onAddCompilationClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        setClickable(clicked);
        clicked = !clicked;
        setUpCompilationDialog();
    }

    private void setUpCompilationDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(MainActivity.this);
        edittext.setHint("Compilation name");
        alert.setMessage("Write a name for your compilation");
        alert.setTitle("Add Compilation");

        alert.setView(edittext);

        alert.setPositiveButton("Confirm", (dialog, whichButton) -> {
            if(edittext.getText().toString().isEmpty()){
                new ErrorDialog(MainActivity.this).startDialog("Error empty", "Non puoi aggiungere una compilation senza nome, eddai");
            }else{
                Compilation compilation = new Compilation(edittext.getText().toString(), user.getEmail());
                LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);
                loadingDialog.startLoadingDialog();
                new CompilationDao().insertCompilation(compilation, MainActivity.this,
                        response -> {
                            loadingDialog.dismissDialog();
                            Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                        }, new FailListener() {
                            @Override
                            public void onAuthFail(String msg) {
                                loadingDialog.dismissDialog();
                                new ErrorDialog(MainActivity.this).startDialog(TypeOfError.AUTH_ERROR.toString(),msg);
                            }

                            @Override
                            public void onDBFail(String msg) {
                                loadingDialog.dismissDialog();
                                new ErrorDialog(MainActivity.this).startDialog(TypeOfError.DB_ERROR.toString(),msg);
                            }

                            @Override
                            public void onLocalProblem(String msg) {
                                loadingDialog.dismissDialog();
                                new ErrorDialog(MainActivity.this).startDialog(TypeOfError.LOCAL_PROBLEM.toString(),msg);
                            }

                            @Override
                            public void onJSONException(String msg) {
                                loadingDialog.dismissDialog();
                                new ErrorDialog(MainActivity.this).startDialog(TypeOfError.JSON_EXCEPTION.toString(),msg);
                            }

                            @Override
                            public void onHTTPError(String msg) {
                                loadingDialog.dismissDialog();
                                new ErrorDialog(MainActivity.this).startDialog(TypeOfError.HTTP_ERROR.toString(),msg);
                            }
                        });
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });
        alert.show();
    }

    private void onAddPathButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        setClickable(clicked);
        clicked = !clicked;

        ChooseFileDialogFragment chooseFileDialogFragment = new ChooseFileDialogFragment(user);
        chooseFileDialogFragment.show(getSupportFragmentManager(), "FragmentAddpathButton");
    }

    private void setVisibility(boolean clicked) {
            if(!clicked){
                addPathButton.setVisibility(View.VISIBLE);
                addCompilationButton.setVisibility(View.VISIBLE);
            }else{
                addPathButton.setVisibility(View.INVISIBLE);
                addCompilationButton.setVisibility(View.INVISIBLE);
            }
    }

    private void setAnimation(boolean clicked) {
            if(!clicked){
                addPathButton.startAnimation(fromBotton());
                addCompilationButton.startAnimation(fromBotton());
                plusButton.startAnimation(rotateOpen());
            }else{
                addPathButton.startAnimation(toBottom());
                addCompilationButton.startAnimation(toBottom());
                plusButton.startAnimation(rotateClose());
            }
    }

    private void setClickable(boolean clicked) {
        if(!clicked){
            addPathButton.setClickable(true);
            addCompilationButton.setClickable(true);
        }else{
            addPathButton.setClickable(false);
            addCompilationButton.setClickable(false);
        }
    }

    @Override
    public void onBackPressed() {
        if(navController.getCurrentDestination().getId() == R.id.navigation_home){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setMessage("Sicuro di voler uscire?");
            alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.super.onBackPressed();
                }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "Meno male, ci fa piacere che rimani", Toast.LENGTH_SHORT).show();
                }
            });
            alertDialog.show();
        }else{
            MainActivity.super.onBackPressed();
        }
    }
}