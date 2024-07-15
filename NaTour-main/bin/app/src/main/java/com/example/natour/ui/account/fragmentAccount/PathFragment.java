package com.example.natour.ui.account.fragmentAccount;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour.R;
import com.example.natour.dao.PathDao;
import com.example.natour.data.User;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.TypeOfError;
import com.example.natour.utils.cardadapter.PathCardAdapter;
import com.example.natour.utils.dialogs.ErrorDialog;

public class PathFragment extends Fragment {
    private static final String TAG = "PathFragment";
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView noResultText;
    private User user;
    public PathFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_path, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragmentUI(view);
        user = this.getArguments().getParcelable("User");
        setUpFragment(user);

    }

    private void setUpFragmentUI(View view){
        progressBar = view.findViewById(R.id.progress_bar_path_fragment);
        recyclerView = view.findViewById(R.id.recycler_view_path_fragment);
        noResultText = view.findViewById(R.id.no_paths_text_fragment_path);
    }

    private void setUpFragment(User user){
        Log.d(TAG, "setUpFragment: starting ...");
        progressBar.setVisibility(View.VISIBLE);
        new PathDao().getPathsWithEmail(user.getEmail(), getContext(),
                naPaths -> {
                    if(naPaths.isEmpty()){
                        noResultText.setVisibility(View.VISIBLE);
                    }else{
                        noResultText.setVisibility(View.INVISIBLE);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    PathCardAdapter pathCardAdapter = new PathCardAdapter(getActivity(), naPaths, user);
                    recyclerView.setAdapter(pathCardAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                }, new FailListener() {
                    @Override
                    public void onAuthFail(String msg) {
                        Log.d(TAG, "onAuthFail: " + msg);
                        new ErrorDialog(getActivity()).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                    }

                    @Override
                    public void onDBFail(String msg) {
                        Log.d(TAG, "onDBFail: "+ msg);
                        new ErrorDialog(getActivity()).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                    }

                    @Override
                    public void onLocalProblem(String msg) {
                        Log.d(TAG, "onLocalProblem: " + msg);
                        new ErrorDialog(getActivity()).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                    }

                    @Override
                    public void onJSONException(String msg) {
                        Log.d(TAG, "onJSONException: " + msg);
                        new ErrorDialog(getActivity()).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                    }

                    @Override
                    public void onHTTPError(String msg) {
                        Log.d(TAG, "onHTTPError: " + msg);
                        new ErrorDialog(getActivity()).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                    }
                });


    }
}
