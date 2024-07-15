package com.example.natour.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour.application.NaTourApplication;
import com.example.natour.R;
import com.example.natour.dao.PathDao;
import com.example.natour.data.User;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.dialogs.ErrorDialog;
import com.example.natour.utils.cardadapter.PathCardAdapter;
import com.example.natour.utils.SharedViewModel;
import com.example.natour.utils.TypeOfError;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private final String TAG = "HomeFragment";


    RecyclerView recyclerView;
    private SharedViewModel sharedViewModel;
    private ProgressBar progressBar;
    private TextView noPathsResultText;
    private User user;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starting");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starting");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progress_bar_home);
        noPathsResultText = view.findViewById(R.id.no_paths_home_result_text);
        recyclerView = view.findViewById(R.id.recycler_view_home);
        setUpFragment();
        user = ((NaTourApplication) getActivity().getApplication()).user;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setUpFragment() {
        Log.d(TAG, "setUpFragment: starting ...");
        progressBar.setVisibility(View.VISIBLE);
        new PathDao().reqPathsMostVoted(10, getContext(),
                naPaths -> {
                    if(!naPaths.isEmpty()){
                        noPathsResultText.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        PathCardAdapter adapter = new PathCardAdapter(getActivity(),naPaths,user );
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
                    }else {
                        noPathsResultText.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }, new FailListener() {
                    @Override
                    public void onAuthFail(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                        noPathsResultText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onDBFail(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                        noPathsResultText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLocalProblem(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                        noPathsResultText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onJSONException(String msg) {
                        ErrorDialog errorDialog = new ErrorDialog(getActivity());
                        errorDialog.startDialog(TypeOfError.JSON_EXCEPTION.toString(), msg);
                        progressBar.setVisibility(View.INVISIBLE);
                        noPathsResultText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onHTTPError(String msg) {
                        ErrorDialog errorDialog = new ErrorDialog(getActivity());
                        errorDialog.startDialog(TypeOfError.HTTP_ERROR.toString(), msg);
                        progressBar.setVisibility(View.INVISIBLE);
                        noPathsResultText.setVisibility(View.VISIBLE);
                    }
                });

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }



}