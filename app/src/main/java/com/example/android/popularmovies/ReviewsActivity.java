package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.popularmovies.adapter.MovieAdapter;
import com.example.android.popularmovies.adapter.ReviewAdapter;
import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.util.MoviesParser;
import com.example.android.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReviewsActivity extends AppCompatActivity {

    public static final String REVIEWS_LOADED = "reviewsLoaded";

    private RecyclerView recyclerView;
    private TextView errorMessageDisplay;

    private ReviewAdapter reviewAdapter;

    List<Review> reviewsLoaded = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);
        errorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display_reviews);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Intent intent = getIntent();

        if (intent.hasExtra(REVIEWS_LOADED)) {
            reviewsLoaded = (List) intent.getParcelableArrayListExtra(REVIEWS_LOADED);
        } else {
            showErrorMessage();
        }

        reviewAdapter = new ReviewAdapter(reviewsLoaded);
        recyclerView.setAdapter(reviewAdapter);

        loadReviews();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(REVIEWS_LOADED, (ArrayList<? extends Parcelable>) reviewsLoaded);
        super.onSaveInstanceState(outState);
    }

    private void loadReviews() {
        showReviewsView();
    }

    private void showReviewsView() {
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessageDisplay.setVisibility(View.VISIBLE);
    }
}
