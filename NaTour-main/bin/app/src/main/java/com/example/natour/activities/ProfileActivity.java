package com.example.natour.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.natour.R;
import com.example.natour.controllers.ControllerAccount;
import com.example.natour.data.User;
import com.example.natour.ui.account.adapter.AdapterPathFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TextView usernameProfilo;
    private TextView emailProfilo;
    private User user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_searched);

        if(savedInstanceState == null){
              ControllerAccount controllerAccount = new ControllerAccount();
              controllerAccount.setUser(getIntent());
              setUpActivity(controllerAccount);
        }
    }

    private void setUpActivityUI(){
        tabLayout = findViewById(R.id.tabLayout_account_search);
        viewPager2 = findViewById(R.id.view_pager_account_search);
        usernameProfilo = findViewById(R.id.profile_username_account_search);
        emailProfilo = findViewById(R.id.email_text_account_search);
    }

    private void setUpActivity(ControllerAccount controllerAccount) {
        setUpActivityUI();

        usernameProfilo.setText(controllerAccount.getUser().getUsername());
        emailProfilo.setText(controllerAccount.getUser().getEmail());

        AdapterPathFragment adapterPathFragment = new AdapterPathFragment(getSupportFragmentManager(),getLifecycle(), controllerAccount);
        viewPager2.setAdapter(adapterPathFragment);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    if(position == 0){
                        tab.setText("Paths");
                    }else if(position == 1){
                        tab.setText("Photo");
                    }else if(position == 2){
                        tab.setText("Compilations");
                    }else{;
                        tab.setText("Reviews");
                    }
            }
        }).attach();
    }

}
