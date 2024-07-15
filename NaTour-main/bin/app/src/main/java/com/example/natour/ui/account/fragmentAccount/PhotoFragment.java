package com.example.natour.ui.account.fragmentAccount;

import android.graphics.Bitmap;
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
import com.example.natour.dao.PhotoDao;
import com.example.natour.data.Photo;
import com.example.natour.data.User;
import com.example.natour.interfacce.FailListener;
import com.example.natour.utils.cardadapter.PhotoCardAdapter;

import java.util.ArrayList;

public class PhotoFragment extends Fragment {

    private static final String TAG = "PhotoFragment";
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView noResultText;
    private ArrayList<Bitmap> photosInBitmap = new ArrayList<>();
    private User user;

    public PhotoFragment() {}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = this.getArguments().getParcelable("User");
        setUpFragmentUI(view);
        setUpFragment(user);
    }


    private void setUpFragmentUI(View view) {
        progressBar = view.findViewById(R.id.progress_bar_photo_fragment);
        recyclerView = view.findViewById(R.id.recycler_view_photo_fragment);
        noResultText = view.findViewById(R.id.no_photo_text_fragment_photo);
    }


    private void setUpFragment(User user) {
        Log.d(TAG, "setUpFragment: starting ...");
        progressBar.setVisibility(View.VISIBLE);
        new PhotoDao().getPhotosByEmail(user.getEmail(), getContext(),
                photos -> {
                    if(photos.isEmpty()){
                        noResultText.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }else{
                        for (Photo photo:
                                photos) {
                            new PhotoDao().getPhotoInBitmap(photo.getS3_key(), getActivity(),
                                    bitmap -> {
                                        photosInBitmap.add(bitmap);
                                        PhotoCardAdapter photoCardAdapter = new PhotoCardAdapter(getActivity(), photosInBitmap);
                                        recyclerView.setAdapter(photoCardAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    }, new FailListener() {
                                        @Override
                                        public void onAuthFail(String msg) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }

                                        @Override
                                        public void onDBFail(String msg) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }

                                        @Override
                                        public void onLocalProblem(String msg) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }

                                        @Override
                                        public void onJSONException(String msg) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }

                                        @Override
                                        public void onHTTPError(String msg) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                }, new FailListener() {
                    @Override
                    public void onAuthFail(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onDBFail(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onLocalProblem(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onJSONException(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onHTTPError(String msg) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }


}
