package com.example.natour.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.natour.application.NaTourApplication;
import com.example.natour.R;
import com.example.natour.controllers.ControllerSearch;
import com.example.natour.dao.PathDao;
import com.example.natour.dao.UserDao;
import com.example.natour.data.NaPath;
import com.example.natour.data.User;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.dialogs.ErrorDialog;
import com.example.natour.utils.dialogs.FilterDialog;
import com.example.natour.utils.NaPathFilters;
import com.example.natour.utils.cardadapter.PathCardAdapter;
import com.example.natour.utils.TypeOfError;
import com.example.natour.utils.cardadapter.UserCardAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final int PATHS_TAB = 0;
    public static final int USERS_TAB = 1;

    private RecyclerView recyclerView;
    private EditText searchBar;
    private TabLayout tabLayout;
    private TextView noResultTextView;
    private UserCardAdapter adapterUser = null;
    private PathCardAdapter adapterPath = null;
    private ProgressBar progressBar;
    private Button filterButton;
    private ArrayList<User> users_result = new ArrayList<>();
    private ArrayList<NaPath> paths_result = new ArrayList<>();
    private ArrayList<NaPath> paths_DB_result = new ArrayList<>();
    NaPathFilters naPathFilters;

    private String mParam1;
    private String mParam2;
    private User user;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.tab_layout_search);
        searchBar = view.findViewById(R.id.search_bar);
        recyclerView = view.findViewById(R.id.recycler_view_search);
        noResultTextView = view.findViewById(R.id.no_result_text);
        progressBar = view.findViewById(R.id.progressBarSearch);
        filterButton = view.findViewById(R.id.filter_button);
        naPathFilters = new NaPathFilters();
        enableSearching();
        settingUpTab();
        setUpButtons();
        user = ((NaTourApplication) getActivity().getApplication()).user;
    }

    private void setUpButtons() {
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setText("");
                new PathDao().reqRegionAndCountry(getActivity(),
                        resultCountries -> {
                            FilterDialog filterDialog = new FilterDialog(naPathFilters, resultCountries);
                            filterDialog.show(getActivity().getSupportFragmentManager(), "FragmentFilterDialog");
                        }, new FailListener() {
                            @Override
                            public void onAuthFail(String msg) {
                                // LASCIATO VUOTO CONSAPEVOLMENTE
                            }

                            @Override
                            public void onDBFail(String msg) {
                                // LASCIATO VUOTO CONSAPEVOLMENTE
                            }

                            @Override
                            public void onLocalProblem(String msg) {
                                // LASCIATO VUOTO CONSAPEVOLMENTE
                            }

                            @Override
                            public void onJSONException(String msg) {
                                new ErrorDialog(getActivity()).startDialog(TypeOfError.JSON_EXCEPTION.toString(), msg);
                            }

                            @Override
                            public void onHTTPError(String msg) {
                                new ErrorDialog(getActivity()).startDialog(TypeOfError.HTTP_ERROR.toString(), msg);
                            }
                        });

            }
        });
    }
    private void enableSearching(){
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable sequenceChanged) {
                String data = sequenceChanged.toString().trim();
                int filterTab = tabLayout.getSelectedTabPosition();
                if (filterTab == USERS_TAB){
                    filterButton.setVisibility(View.INVISIBLE);
                    if(!data.isEmpty()){
                        if(data.length() == 1){ //primo carattere inserito
                            progressBar.setVisibility(View.VISIBLE);
                            new UserDao().search(data, getContext(),
                                    onSuccess -> {
                                        progressBar.setVisibility(View.INVISIBLE);

                                        users_result = onSuccess;
                                        if(users_result.isEmpty()){
                                            noResultTextView.setVisibility(View.VISIBLE);
                                        }else{
                                            noResultTextView.setVisibility(View.INVISIBLE);
                                        }
                                        adapterUser = new UserCardAdapter(requireActivity(), users_result);
                                        recyclerView.setAdapter(adapterUser);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    }, new FailListener() {
                                        @Override
                                        public void onAuthFail(String msg) {
                                            // LASCIATO VUOTO CONSAPEVOLMENTE
                                        }

                                        @Override
                                        public void onDBFail(String msg) {
                                            // LASCIATO VUOTO CONSAPEVOLMENTE
                                        }

                                        @Override
                                        public void onLocalProblem(String msg) {
                                            // LASCIATO VUOTO CONSAPEVOLMENTE
                                        }

                                        @Override
                                        public void onJSONException(String msg) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            new ErrorDialog(getActivity()).startDialog(TypeOfError.JSON_EXCEPTION.toString(), msg);
                                        }

                                        @Override
                                        public void onHTTPError(String msg) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            new ErrorDialog(getActivity()).startDialog(TypeOfError.HTTP_ERROR.toString(), msg);
                                        }
                                    }
                            );
                        }else{
                            if(users_result != null){
                                if (!users_result.isEmpty()){
                                    ArrayList<User> user_searched = new ControllerSearch().searchUserWith(data, users_result);
                                    adapterUser = new UserCardAdapter(getActivity(), user_searched);
                                    recyclerView.setAdapter(adapterUser);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    if(user_searched.isEmpty()){
                                        noResultTextView.setVisibility(View.VISIBLE);
                                    }else{
                                        noResultTextView.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                        }
                    }else{
                        if(adapterUser != null){
                            adapterUser.clear();
                        }
                    }

                }else if(filterTab == PATHS_TAB){
                    filterButton.setVisibility(View.VISIBLE);
                    if(!data.isEmpty()){
                        if(data.length() == 1){ //primo carattere inserito
                            progressBar.setVisibility(View.VISIBLE);  // || (paths_result.size() < result_max_number && paths_DB_result.size() > paths_result.size()
                            new PathDao().search(data, getContext(),
                                    onSuccess -> {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        paths_DB_result = onSuccess;
                                        paths_result = new ControllerSearch().searchPathWith(data, onSuccess, naPathFilters);
                                        adapterPath = new PathCardAdapter(requireActivity(), paths_result, user);
                                        recyclerView.setAdapter(adapterPath);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                        if(paths_result.isEmpty()){
                                            noResultTextView.setVisibility(View.VISIBLE);
                                        }else{
                                            noResultTextView.setVisibility(View.INVISIBLE);
                                        }
                                    }, new FailListener() {
                                        @Override
                                        public void onAuthFail(String msg) {
                                            // LASCIATO VUOTO CONSAPEVOLMENTE
                                        }

                                        @Override
                                        public void onDBFail(String msg) {
                                            // LASCIATO VUOTO CONSAPEVOLMENTE
                                        }

                                        @Override
                                        public void onLocalProblem(String msg) {
                                                // LASCIATO VUOTO CONSAPEVOLMENTE
                                        }

                                        @Override
                                        public void onJSONException(String msg) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            new ErrorDialog(getActivity()).startDialog(TypeOfError.JSON_EXCEPTION.toString(), msg);
                                        }

                                        @Override
                                        public void onHTTPError(String msg) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            new ErrorDialog(getActivity()).startDialog(TypeOfError.HTTP_ERROR.toString(), msg);
                                        }
                                    }
                            );
                        }else{
                            if(paths_result != null){
                                if (!paths_result.isEmpty()){
                                    ArrayList<NaPath> path_searched = new ControllerSearch().searchPathWith(data, paths_result, naPathFilters);
                                    adapterPath = new PathCardAdapter(getActivity(), path_searched, user);
                                    recyclerView.setAdapter(adapterPath);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                }
                            }
                        }
                    }else{
                        if(adapterPath != null){
                            adapterPath.clear();
                            recyclerView.setAdapter(adapterPath);
                        }
                    }
                }

            }
        });

    }

    private void settingUpTab() {

        tabLayout.addTab(tabLayout.newTab().setText(R.string.paths));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.users));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                System.out.println("Clearing the adapter..");
                clear();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                System.out.println("Clearing the adapter..");
               ;clear();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                clear();
            }
        });

    }

    public void clear(){
        if(adapterUser != null){
            adapterUser.clear();
        }
        if(adapterPath != null){
            adapterPath.clear();
        }
        searchBar.setText("");
        noResultTextView.setVisibility(View.INVISIBLE);
    }

}