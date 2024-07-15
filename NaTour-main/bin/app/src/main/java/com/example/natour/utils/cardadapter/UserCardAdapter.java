package com.example.natour.utils.cardadapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour.activities.ProfileActivity;
import com.example.natour.R;
import com.example.natour.data.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserCardAdapter extends RecyclerView.Adapter<UserCardAdapter.ViewHolder> {

    ArrayList<User> users;
    Context context;

    public UserCardAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.search_result_card, parent, false);
        return new UserCardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCardAdapter.ViewHolder holder, int position) {
            // Cosi dovrebbe essere implementato
        // holder.circleUserImage.setImageResource(new PhotoDao().getPhoto(users.get(position).getEmail()));
            holder.circleUserImage.setImageResource(R.drawable.ic_account_black);
            holder.title.setText(users.get(position).getName());
            holder.email.setText(users.get(position).getEmail());
            holder.setUser(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void clear() {
        int size = users.size();
        users.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView circleUserImage;
        private TextView title, email;
        private User user;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.result_title);
            email = itemView.findViewById(R.id.email_result);
            circleUserImage = itemView.findViewById(R.id.result_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Aprire l'account dell'utente
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("User", user);
                    context.startActivity(intent);
                }
            });
        }

        public void setUser(User user){
            this.user = user;
        }
    }
}
