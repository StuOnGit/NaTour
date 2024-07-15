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
import com.example.natour.dao.ReviewDao;
import com.example.natour.data.User;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.dialogs.ErrorDialog;
import com.example.natour.utils.cardadapter.ReviewCardAdapter;
import com.example.natour.utils.TypeOfError;

public class ReviewFragment extends Fragment {

    private static final String TAG = "ReviewFragment";
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView noResultText;
    private  User user;
    public ReviewFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = this.getArguments().getParcelable("User");
        setUpFragmentUI(view);
        setUpFragment(user);
    }

    private void setUpFragmentUI(View view){
        progressBar = view.findViewById(R.id.progress_bar_review_fragment);
        recyclerView = view.findViewById(R.id.recycler_view_review_fragment);
        noResultText = view.findViewById(R.id.no_reviews_text_fragment_review);
    }

    private void setUpFragment(User user){
        Log.d(TAG, "setUpFragment: starting ...");
        progressBar.setVisibility(View.VISIBLE);
        System.out.println("Get reviews with email: " + user.getEmail());
        new ReviewDao().reqReviewsWithEmail(user.getEmail(), getActivity(),
                reviews -> {
                    if(reviews.isEmpty()){
                        noResultText.setVisibility(View.VISIBLE);
                    }else{
                        noResultText.setVisibility(View.INVISIBLE);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    ReviewCardAdapter reviewCardAdapter = new ReviewCardAdapter(getActivity(), reviews);
                    recyclerView.setAdapter(reviewCardAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                }, new FailListener() {
                    @Override
                    public void onAuthFail(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                        new ErrorDialog(getActivity()).startDialog(TypeOfError.AUTH_ERROR.toString(),msg);
                    }

                    @Override
                    public void onDBFail(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                        new ErrorDialog(getActivity()).startDialog(TypeOfError.DB_ERROR.toString(),msg);
                    }

                    @Override
                    public void onLocalProblem(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                        new ErrorDialog(getActivity()).startDialog(TypeOfError.LOCAL_PROBLEM.toString(),msg);
                    }

                    @Override
                    public void onJSONException(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                        new ErrorDialog(getActivity()).startDialog(TypeOfError.JSON_EXCEPTION.toString(),msg);
                    }

                    @Override
                    public void onHTTPError(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                        new ErrorDialog(getActivity()).startDialog(TypeOfError.HTTP_ERROR.toString(),msg);
                    }
                });
    }
}
