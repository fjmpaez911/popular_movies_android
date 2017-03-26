package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private static final String TAG = ReviewAdapter.class.getSimpleName();

    List<Review> reviews;
    Context context;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }


    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView reviewAuthor;
        public final TextView reviewContent;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            reviewAuthor = (TextView) view.findViewById(R.id.review_author);
            reviewContent = (TextView) view.findViewById(R.id.review_content);
        }
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        context = viewGroup.getContext();

        int layoutIdForListItem = R.layout.review;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder reviewAdapterViewHolder, int position) {
        reviewAdapterViewHolder.reviewAuthor.setText(reviews.get(position).getAuthor());
        reviewAdapterViewHolder.reviewContent.setText(reviews.get(position).getContent());

        Log.v(TAG, "Getting items");
    }

    @Override
    public int getItemCount() {
        if (reviews != null) {
            return reviews.size();
        }
        return 0;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

}
