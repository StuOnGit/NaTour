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
import com.example.natour.dao.CompilationDao;
import com.example.natour.data.User;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.cardadapter.CompilationCardAdapter;
import com.example.natour.utils.dialogs.ErrorDialog;
import com.example.natour.utils.TypeOfError;

public class CompilationFragment extends Fragment {

    private static final String TAG = "CompilationFragment";

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView noResultText;
    private User user;
    public CompilationFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compilation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = this.getArguments().getParcelable("User");
        setUpFragmentUI(view);
        setUpFragment(user);
    }

    private void setUpFragmentUI(View view){
        progressBar = view.findViewById(R.id.progress_bar_compilation_fragment);
        recyclerView = view.findViewById(R.id.recycler_view_compilation_fragment);
        noResultText = view.findViewById(R.id.no_compilation_text_fragment_compilation);
    }

    private void setUpFragment(User user){
        Log.d(TAG, "setUpFragment: starting ...");
        progressBar.setVisibility(View.VISIBLE);
        // new CompilationDao().reqCompilationsWithEmail(user.getEmail()...
        new CompilationDao().getCompilationsByEmail(user.getEmail(), requireActivity(),
                compilations -> {
                    if(compilations.isEmpty()){
                        noResultText.setVisibility(View.VISIBLE);
                    }else{
                        noResultText.setVisibility(View.INVISIBLE);
                    }
                    Log.d(TAG, "getCompilationsByEmail..." + String.valueOf(compilations.size()));
                    progressBar.setVisibility(View.INVISIBLE);
                    CompilationCardAdapter compilationCardAdapter = new CompilationCardAdapter(requireActivity(), compilations);
                    recyclerView.setAdapter(compilationCardAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                }, new FailListener() {
                    @Override
                    public void onAuthFail(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                        new ErrorDialog(getActivity()).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                    }

                    @Override
                    public void onDBFail(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                        new ErrorDialog(getActivity()).startDialog(TypeOfError.DB_ERROR.toString(), msg);
                    }

                    @Override
                    public void onLocalProblem(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                        new ErrorDialog(getActivity()).startDialog(TypeOfError.LOCAL_PROBLEM.toString(), msg);
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
                });
    }
}
