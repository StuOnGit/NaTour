package com.example.natour.utils.cardadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour.R;
import com.example.natour.data.Review;

import java.util.ArrayList;

public class ReviewCardAdapter extends RecyclerView.Adapter<ReviewCardAdapter.ViewHolder> {

    ArrayList<Review> reviews;
    Context context;

    public ReviewCardAdapter(Context context, ArrayList<Review> reviews){
        this.context = context;
        this.reviews = reviews;
    }
    @NonNull
    @Override
    public ReviewCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.review_card, parent, false);
        return new ReviewCardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewCardAdapter.ViewHolder holder, int position) {
            Review review = reviews.get(position);
            holder.setReview(reviews.get(position));
            holder.description.setText(review.getDescription());
            holder.ratingBar.setIsIndicator(true);

            holder.emailUser.setText("-" +review.getEmailUtente());
            holder.namePathAndUser.setText("Itinerario:" + review.getPath().getPathName()+"("+review.getPath().getEmailCreatore()+")");
            holder.ratingBar.setRating(review.getValutazione().ordinal() + 1);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void clear() {
        int size = reviews.size();
        reviews.clear();
        notifyItemRangeRemoved(0, size);
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        private Review review;
        private TextView description;
        private TextView emailUser;
        private RatingBar ratingBar;
        private TextView namePathAndUser;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.review_description);
            emailUser = itemView.findViewById(R.id.review_email);
            ratingBar = itemView.findViewById(R.id.stars_review);
            namePathAndUser = itemView.findViewById(R.id.text_namePath_review_card);
        }

        public void setReview(Review review){
            this.review = review;
        }
    }
}
