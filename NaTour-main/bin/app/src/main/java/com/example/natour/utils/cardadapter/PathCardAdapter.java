package com.example.natour.utils.cardadapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour.activities.PathActivity;
import com.example.natour.R;
import com.example.natour.data.NaPath;
import com.example.natour.data.User;

import java.util.ArrayList;

public class PathCardAdapter extends RecyclerView.Adapter<PathCardAdapter.ViewHolder> {

    Activity activity;
    ArrayList<NaPath> paths;
    User user;

    public PathCardAdapter(Activity activity, ArrayList<NaPath> paths, User user){
        this.activity = activity;
        this.paths = paths;
        this.user = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity.getApplicationContext());
        View view = inflater.inflate(R.layout.path_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.title.setText(paths.get(position).getPathName());
            holder.description.setText(paths.get(position).getDescrizione());
//            holder.image.setImageResource(paths.get(position).getPhotos().get(1).getImage());


//            holder.title.setText(title[position]);
//            holder.description.setText(description[position]);
            holder.image.setImageResource(R.drawable.image_logo);
            holder.setPath(paths.get(position));
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView title, description;
        private ImageView image;
        private NaPath path;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.path_name_home);
            description = itemView.findViewById(R.id.path_description_home);
            image = itemView.findViewById(R.id.path_image_home);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(activity, PathActivity.class);
                intent.putExtra("Path", path);
                intent.putExtra("User", user);
                activity.startActivity(intent);
                Toast.makeText(activity.getApplicationContext(), title.getText(), Toast.LENGTH_SHORT).show();
            });
        }

        private void setPath(NaPath path){
            this.path = path;
        }

    }


}
