package com.example.natour.activities;

import android.graphics.Color;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour.R;
import com.example.natour.dao.CompilationDao;
import com.example.natour.dao.PhotoDao;
import com.example.natour.dao.ReviewDao;
import com.example.natour.data.NaPath;
import com.example.natour.data.Photo;
import com.example.natour.data.User;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.dialogs.AddPathToCompilationDialog;
import com.example.natour.utils.dialogs.ErrorDialog;
import com.example.natour.utils.cardadapter.ReviewCardAdapter;
import com.example.natour.utils.dialogs.ReviewDialog;
import com.example.natour.utils.TypeOfError;

import de.hdodenhof.circleimageview.CircleImageView;

public class PathActivity extends AppCompatActivity {

    private static final String TAG = "PathActivity";
    private Button saveGpx;
    private Button openMap;
    private Button writeReview;
    private TextView difficultText;
    private TextView durataText;
    private TextView startPoint;
    private TextView description;
    private CircleImageView accessibilitaView;
    private TextView reviewsSize;
    private TextView namePathText;
    private TextView regioneText;
    private TextView nazioneText;
    private RecyclerView recyclerRecensioni;
    private ProgressBar progressBarReviews;
    private ProgressBar progressBarPhoto;
    private NaPath naPath;
    private ReviewCardAdapter adapterReview;
    private ViewFlipper slider;
    private Button addToCompilationButton;
    private User user;
    private Button addSightsButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);
        setUpActivity();
        Bundle extras;
        if(savedInstanceState == null){
            extras = getIntent().getExtras();
            if(extras == null){
                //Error message, (dovrebbe provare a rientrare)
            }else{
                setNaPath(extras.getParcelable("Path"));
                setUser(extras.getParcelable("User"));
            }
        }
        

        setUpPath();
        setUpButton();
    }

    private void setUser(User user){
        this.user = user;
    }

    private void setNaPath(NaPath naPath){
        this.naPath = naPath;
    }

    private void setUpActivity(){
        slider = findViewById(R.id.slider);
        openMap = findViewById(R.id.open_map);
        writeReview = findViewById(R.id.write_review);
        difficultText = findViewById(R.id.difficult_text);
        durataText = findViewById(R.id.duration_text);
        startPoint = findViewById(R.id.start_point_text);
        description = findViewById(R.id.description_text);
        accessibilitaView = findViewById(R.id.accessible);
        namePathText = findViewById(R.id.title_path_review);
        recyclerRecensioni = findViewById(R.id.recycler_view_path);
        progressBarReviews = findViewById(R.id.progress_bar_reviews);
        progressBarPhoto = findViewById(R.id.progress_bar_photos);
        reviewsSize = findViewById(R.id.review_number);
        addToCompilationButton = findViewById(R.id.add_to_compilation_button);
        nazioneText = findViewById(R.id.nazione_text);
        regioneText = findViewById(R.id.regione_text);
        addSightsButton = findViewById(R.id.add_sights_button);
    }

    private void setUpPath(){
        if(naPath != null){
            difficultText.setText(String.valueOf(naPath.getDifficultyEnum())); // Non lo prende a causa del parser
            durataText.setText(String.valueOf(naPath.getDurata()));
            startPoint.setText(String.valueOf(naPath.getStartPath()));
            description.setText(naPath.getDescrizione());
            nazioneText.setText(naPath.getCountry());
            regioneText.setText(naPath.getRegion());
            if(!naPath.isAccessibilitaDisabili()){
                accessibilitaView.setCircleBackgroundColor(Color.RED);
            }else{
                accessibilitaView.setCircleBackgroundColor(Color.GREEN);
            }
            String titleText = naPath.getPathName() + "-" + naPath.getEmailCreatore();
            namePathText.setText(titleText);
            progressBarReviews.setVisibility(View.VISIBLE);

            new ReviewDao().reqReviews(naPath, PathActivity.this,
                    reviews -> {
                        progressBarReviews.setVisibility(View.INVISIBLE);
                        if (reviews != null) {
                            adapterReview = new ReviewCardAdapter(PathActivity.this, reviews);
                            recyclerRecensioni.setAdapter(adapterReview);
                            recyclerRecensioni.setLayoutManager(new LinearLayoutManager(PathActivity.this));
                            reviewsSize.setText(String.valueOf(reviews.size()));
                        }else{
                            //setta Testo di "nessuna recensione"
                            reviewsSize.setText(0);
                        }
                    }, new FailListener() {
                        @Override
                        public void onAuthFail(String msg) {
                            progressBarReviews.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onDBFail(String msg) {
                            progressBarReviews.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onLocalProblem(String msg) {
                            progressBarReviews.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onJSONException(String msg) {
                            progressBarReviews.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onHTTPError(String msg) {
                            progressBarReviews.setVisibility(View.INVISIBLE);

                        }
                    });
            progressBarPhoto.setVisibility(View.VISIBLE);
            new PhotoDao().getPhotosByPath(naPath, PathActivity.this,
                    photos -> {
                        progressBarPhoto.setVisibility(View.INVISIBLE);
                        if(!photos.isEmpty()){
                            for (Photo photo:
                                    photos) {
                                new PhotoDao().getPhotoInBitmap(photo.getS3_key(), PathActivity.this,
                                        bitmap -> {
                                            ImageView imageView = new ImageView(PathActivity.this);
                                            imageView.setImageBitmap(bitmap);
                                            slider.addView(imageView);
                                            slider.setFlipInterval(3000);
                                            slider.startFlipping();
                                        }, new FailListener() {
                                            @Override
                                            public void onAuthFail(String msg) {
                                                progressBarPhoto.setVisibility(View.INVISIBLE);
                                            }

                                            @Override
                                            public void onDBFail(String msg) {
                                                progressBarPhoto.setVisibility(View.INVISIBLE);
                                            }

                                            @Override
                                            public void onLocalProblem(String msg) {
                                                progressBarPhoto.setVisibility(View.INVISIBLE);
                                            }

                                            @Override
                                            public void onJSONException(String msg) {
                                                progressBarPhoto.setVisibility(View.INVISIBLE);
                                            }

                                            @Override
                                            public void onHTTPError(String msg) {
                                                progressBarPhoto.setVisibility(View.INVISIBLE);
                                            }
                                        });
                            }
                        }else{
                            ImageView imageView = new ImageView((PathActivity.this));
                            imageView.setImageResource(R.drawable.image_logo);
                            slider.addView(imageView);
                        }

                    }, new FailListener() {
                        @Override
                        public void onAuthFail(String msg) {
                            progressBarPhoto.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onDBFail(String msg) {
                            progressBarPhoto.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onLocalProblem(String msg) {
                            progressBarPhoto.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onJSONException(String msg) {
                            progressBarPhoto.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onHTTPError(String msg) {
                            progressBarPhoto.setVisibility(View.INVISIBLE);
                        }
                    });
        }
    }

    private void setUpButton(){
        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSeeMap = new Intent(PathActivity.this, SeeMapActivity.class);
                intentSeeMap.putExtra("naPath", naPath);
                startActivity(intentSeeMap);
            }
        });
        writeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //write review and push to db
                ReviewDialog reviewDialog = new ReviewDialog(naPath);
                reviewDialog.show(getSupportFragmentManager(), TAG);
            }
        });
        addToCompilationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new CompilationDao().getCompilationsByEmail(user.getEmail(), PathActivity.this,
                            compilations -> {
                                AddPathToCompilationDialog addPathToCompilationDialog = new AddPathToCompilationDialog(naPath, compilations);
                                addPathToCompilationDialog.show(getSupportFragmentManager(), TAG);
                            },  new FailListener() {
                                @Override
                                public void onAuthFail(String msg) {
                                    new ErrorDialog(PathActivity.this).startDialog(TypeOfError.AUTH_ERROR.toString(), msg);
                                }

                                @Override
                                public void onDBFail(String msg) {
                                    new ErrorDialog(PathActivity.this).startDialog(TypeOfError.DB_ERROR.toString(), msg);
                                }

                                @Override
                                public void onLocalProblem(String msg) {
                                    new ErrorDialog(PathActivity.this).startDialog(TypeOfError.LOCAL_PROBLEM.toString(), msg);
                                }

                                @Override
                                public void onJSONException(String msg) {
                                    new ErrorDialog(PathActivity.this).startDialog(TypeOfError.JSON_EXCEPTION.toString(), msg);
                                }

                                @Override
                                public void onHTTPError(String msg) {
                                    new ErrorDialog(PathActivity.this).startDialog(TypeOfError.HTTP_ERROR.toString(), msg);
                                }
                            });
            }
        });
        addSightsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PathActivity.this, AddSightByExternalUserActivity.class);
                intent.putExtra("naPath", naPath);
                startActivity(intent);
            }
        });

    }
}
