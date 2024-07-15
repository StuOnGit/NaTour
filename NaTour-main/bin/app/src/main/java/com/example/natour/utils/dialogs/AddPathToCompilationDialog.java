package com.example.natour.utils.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour.R;
import com.example.natour.dao.CompilationDao;
import com.example.natour.data.Compilation;
import com.example.natour.data.NaPath;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.TypeOfError;
import com.example.natour.utils.cardadapter.AddPathCompilationCardAdapter;

import java.util.ArrayList;

public class AddPathToCompilationDialog extends DialogFragment {
    private static final String TAG = "AddPathToCompilationDialog";

    private RecyclerView recyclerView;
    private Button confirmButton;
    private ArrayList<Compilation> compilations;
    private ArrayList<Compilation> compilationsWithoutPath;
    private NaPath naPath;
    public AddPathToCompilationDialog(NaPath naPath,ArrayList<Compilation> compilations){
        this.compilations = compilations;
        this.naPath = naPath;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
         return inflater.inflate(R.layout.add_to_compilation_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setCancelable(true);
        setUpDialog(view);
        setUpButtons();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        compilationsWithoutPath = getCompilationWithoutThePath(compilations);
        AddPathCompilationCardAdapter addPathCompilationCardAdapter = new AddPathCompilationCardAdapter(getActivity(), compilationsWithoutPath);
        recyclerView.setAdapter(addPathCompilationCardAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private ArrayList<Compilation> getCompilationWithoutThePath(ArrayList<Compilation> compilationsToCheck) {
        ArrayList<Compilation> compilationsWithoutPath = new ArrayList<>();
        for (int i = 0; i < compilationsToCheck.size(); i++) {
            int compilationWithoutPathIndex = i;
            ArrayList<NaPath> pathsToCheck = compilationsToCheck.get(i).getPaths();
            boolean isInThere = false;
            int j = 0;
            while(!isInThere && j < pathsToCheck.size()){
                NaPath percorso = pathsToCheck.get(j);
                System.out.println(percorso.getPathName());
                System.out.println(naPath.getPathName());

                System.out.println(percorso.getEmailCreatore());
                System.out.println(naPath.getEmailCreatore());
                if((percorso.getPathName().equals(naPath.getPathName())) && (percorso.getEmailCreatore().equals(naPath.getEmailCreatore()))){
                    System.out.println("sono entrato");
                    isInThere = true;
                }
                j++;
            }
            if(!isInThere){
                compilationsWithoutPath.add(compilations.get(compilationWithoutPathIndex));
            }
        }
        System.out.println("Compilations Size: "  + compilationsWithoutPath.size());
        return compilationsWithoutPath;
    }

    private void setUpButtons() {
        setCancelable(true);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPathCompilationCardAdapter adapter =(AddPathCompilationCardAdapter) recyclerView.getAdapter();
                ArrayList<Compilation> compilationsChecked = getCompilationsChecked(compilationsWithoutPath,adapter.getChecked());
                for (Compilation compilation:
                     compilationsChecked) {

                    new CompilationDao().insertCompilationPathRel(naPath, compilation, getActivity(),
                            response -> {
                                Log.d(TAG, response);
                            }, new FailListener() {
                                @Override
                                public void onAuthFail(String msg) {
                                    new ErrorDialog(getActivity()).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                                }

                                @Override
                                public void onDBFail(String msg) {
                                    new ErrorDialog(getActivity()).startDialog(TypeOfError.DB_ERROR.toString(), msg);
                                }

                                @Override
                                public void onLocalProblem(String msg) {
                                    new ErrorDialog(getActivity()).startDialog(TypeOfError.LOCAL_PROBLEM.toString(), msg);
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
                dismiss();
            }
        });
    }

    ArrayList<Compilation> getCompilationsChecked(ArrayList<Compilation> compilations, boolean [] checked){
        ArrayList<Compilation> compilationsChecked = new ArrayList<>();
        for (int i = 0; i < compilations.size(); i++){
            if(checked[i]){
                compilationsChecked.add(compilations.get(i));
            }
        }
        return compilationsChecked;
    }

    private void setUpDialog(View view){
        recyclerView = view.findViewById(R.id.recycler_view_add_to_compilation_dialog);
        confirmButton = view.findViewById(R.id.add_path_to_compilations_button);
    }
}
