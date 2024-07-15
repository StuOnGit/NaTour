package com.example.natour.ui.account;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.natour.application.NaTourApplication;
import com.example.natour.R;
import com.example.natour.controllers.ControllerAccount;
import com.example.natour.data.User;
import com.example.natour.ui.account.adapter.AdapterPathFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "AccountFragment";
    private TabLayout tabLayout;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static final int REQUEST_TAKE_PHOTO = 2;
    private String mParam1;
    private String mParam2;


    private User user;
    private ViewPager2 viewPager2;
    private TextView usernameProfile;
    private TextView emailProfilo;
    private ControllerAccount controllerAccount;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starting...");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void setUpActivityUI(View view){
        tabLayout = view.findViewById(R.id.tabLayout_account);
        viewPager2 = view.findViewById(R.id.view_pager_account);
        usernameProfile = view.findViewById(R.id.profile_username_account);
        emailProfilo = view.findViewById(R.id.email_text_account);
    }

    private void setUpActivity( View view) {
        setUpActivityUI(view);

        usernameProfile.setText(user.getUsername());
        emailProfilo.setText(user.getEmail());
        controllerAccount = new ControllerAccount();
        controllerAccount.setUser(user);
        AdapterPathFragment adapterPathFragment = new AdapterPathFragment(getActivity().getSupportFragmentManager(), getLifecycle(), controllerAccount);
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(savedInstanceState == null){
            user = ((NaTourApplication) getActivity().getApplication()).user;
            System.out.println("User logged: " + user.getName());
            setUpActivity(view);
        }
    }

}