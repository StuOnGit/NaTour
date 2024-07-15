package com.example.natour.utils.cardadapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour.R;
import com.example.natour.data.NaPath;

import java.util.ArrayList;

public class PathCompilationCardAdapter extends  RecyclerView.Adapter<PathCompilationCardAdapter.ViewHolder> {

    Activity activity;
    ArrayList<NaPath> paths;

    public PathCompilationCardAdapter(Activity activity, ArrayList<NaPath> paths) {
        this.activity = activity;
        this.paths = paths;
    }

    @NonNull
    @Override
    public PathCompilationCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity.getApplicationContext());
        View view = inflater.inflate(R.layout.path_card, parent, false);
        return new PathCompilationCardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PathCompilationCardAdapter.ViewHolder holder, int position) {
        holder.title.setText(paths.get(position).getPathName());
        holder.description.setText(paths.get(position).getDescrizione());
        holder.image.setImageResource(R.drawable.image_logo);
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    public void clear() {
        int size = paths.size();
        paths.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, description;
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.path_name_home);
            description = itemView.findViewById(R.id.path_description_home);
            image = itemView.findViewById(R.id.path_image_home);
        }
    }
}
