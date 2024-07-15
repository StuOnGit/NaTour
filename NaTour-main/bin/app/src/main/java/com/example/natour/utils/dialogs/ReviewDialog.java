package com.example.natour.utils.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.natour.R;
import com.example.natour.application.NaTourApplication;
import com.example.natour.dao.ReviewDao;
import com.example.natour.data.NaPath;
import com.example.natour.data.Review;
import com.example.natour.data.User;
import com.example.natour.enumeration.ValutazioneEnum;
import com.example.natour.interfacce.FailListener;

public class ReviewDialog extends DialogFragment {
    private static final String TAG = "ReviewDialog";

    private ImageView close;
    private EditText reviewText;
    private Button confirmButton;
    private Review review;
    private ProgressBar progressBar;
    private RatingBar ratingBar;
    private User userLogged;
    public ReviewDialog(NaPath naPath) {
        this.review = new Review(naPath);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
         return inflater.inflate(R.layout.review_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setCancelable(true);
        setUpDialog(view);
        setUpButtons();
    }

    private void setUpDialog(View view){
        close = view.findViewById(R.id.close_insert_review);
        reviewText = view.findViewById(R.id.insert_review_text);
        confirmButton = view.findViewById(R.id.confirm_insert_review_button);
        progressBar = view.findViewById(R.id.progress_bar_insert_review);
        ratingBar = view.findViewById(R.id.rating_bar_insert_review);
        userLogged = ((NaTourApplication)getActivity().getApplication()).user;
        ratingBar.setRating(1.0f);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                if(rating<1.0f)
                    ratingBar.setRating(1.0f);
            }
        });
    }

    private void setUpButtons(){
        close.setOnClickListener(v -> dismiss());
        confirmButton.setOnClickListener(v -> {

            float valutazione = ratingBar.getRating();
            ValutazioneEnum valutazioneEnum = ValutazioneEnum.UNA_STELLA;
            if(valutazione <= 1){
                valutazioneEnum = ValutazioneEnum.UNA_STELLA;
            }else if(valutazione <= 2){
                valutazioneEnum = ValutazioneEnum.DUE_STELLE;
            }else if(valutazione <= 3){
                valutazioneEnum = ValutazioneEnum.TRE_STELLE;
            }else if(valutazione <= 4){
                valutazioneEnum = ValutazioneEnum.QUATTRO_STELLE;
            }else if(valutazione <= 5){
                valutazioneEnum = ValutazioneEnum.CINQUE_STELLE;
            }
            review.setValutazione(valutazioneEnum);
            review.setDescription(reviewText.getText().toString());
            review.setEmailUtente(userLogged.getEmail());
            progressBar.setVisibility(View.VISIBLE);
            new ReviewDao().insertReview(review, getContext(),
                    onSuccess -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Review Done", Toast.LENGTH_SHORT);
                        dismiss();
                    }, new FailListener() {
                        @Override
                        public void onAuthFail(String msg) {
                            progressBar.setVisibility(View.INVISIBLE);
                            dismiss();
                        }

                        @Override
                        public void onDBFail(String msg) {
                            progressBar.setVisibility(View.INVISIBLE);
                            dismiss();
                        }

                        @Override
                        public void onLocalProblem(String msg) {
                            progressBar.setVisibility(View.INVISIBLE);
                            dismiss();
                        }

                        @Override
                        public void onJSONException(String msg) {
                            progressBar.setVisibility(View.INVISIBLE);
                            dismiss();
                        }

                        @Override
                        public void onHTTPError(String msg) {
                            progressBar.setVisibility(View.INVISIBLE);
                            dismiss();
                        }
                    });
        });
    }
}
