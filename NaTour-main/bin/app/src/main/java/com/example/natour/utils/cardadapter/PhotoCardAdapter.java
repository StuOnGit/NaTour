package com.example.natour.utils.cardadapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour.R;

import java.util.ArrayList;

public class PhotoCardAdapter extends  RecyclerView.Adapter<PhotoCardAdapter.ViewHolder>{
    Activity activity;
    ArrayList<Bitmap> photosBitmap;

    public PhotoCardAdapter(Activity activity, ArrayList<Bitmap> photosBitmap){
        this.activity = activity;
        this.photosBitmap = photosBitmap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity.getApplicationContext());
        View view = inflater.inflate(R.layout.photo_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image.setImageBitmap(photosBitmap.get(position));
    }

    @Override
    public int getItemCount() {
        return photosBitmap.size();
    }

    public void clear() {
        int size = photosBitmap.size();
        photosBitmap.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageViewPhoto);
        }
    }
}
