package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

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

public class MovieDetailActivity extends AppCompatActivity {

    public final static String EXTRA_MOVIE = "movieId";
    private static final String MOVIE_LOADED = "movieLoaded";

    private TextView errorMessageDisplay;
    private ProgressBar loadingIndicator;
    private ScrollView scrollView;

    private ImageView poster;
    private TextView originalTitle;
    private TextView releaseDate;
    private TextView voteAverage;
    private TextView overview;
    private Button reviews;
    private GridView trailers;

    private Movie movie;

    private TrailerAdapter trailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        errorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display_movie);
        loadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator_movie);
        scrollView = (ScrollView) findViewById(R.id.sv_movie_detail);

        poster = (ImageView) findViewById(R.id.iv_movie_detail);
        originalTitle = (TextView) findViewById(R.id.original_title);
        releaseDate = (TextView) findViewById(R.id.release_date);
        voteAverage = (TextView) findViewById(R.id.vote_average);
        overview = (TextView) findViewById(R.id.overview);
        reviews = (Button) findViewById(R.id.reviews);
        trailers = (GridView) findViewById(R.id.trailers);

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_LOADED)) {
            movie = savedInstanceState.getParcelable(MOVIE_LOADED);
            showMovieView();
            loadMovieInViews();
        } else {
            Intent intent = getIntent();

            if (intent.hasExtra(EXTRA_MOVIE)) {
                Integer movieId = (Integer) intent.getSerializableExtra(EXTRA_MOVIE);
                loadMovie(movieId);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MOVIE_LOADED, movie);
        super.onSaveInstanceState(outState);
    }

    public void showReviews (View view) {
        Intent intent = new Intent(this, ReviewsActivity.class);
        intent.putParcelableArrayListExtra(ReviewsActivity.REVIEWS_LOADED, (ArrayList) movie.getReviews());

        startActivity(intent);
    }

    private void loadMovieInViews () {
        String posterPath = movie.getPosterPath();
        String posterUrl = getResources().getString(R.string.movie_db_base_url_poster) + posterPath;
        Picasso.with(this).load(posterUrl).into(poster);

        originalTitle.setText(movie.getOriginalTitle());
        releaseDate.setText(movie.getRealeseDate());
        voteAverage.setText(movie.getVoteAverage());
        overview.setText(movie.getOverview());

        Integer numReviews = movie.getReviews().size();
        String reviewsInfo = "View " + numReviews + " reviews...";
        reviews.setText(reviewsInfo);
        reviews.setEnabled(numReviews > 0 ? true : false);

        trailerAdapter = new TrailerAdapter(this, movie.getTrailers());
        trailers.setAdapter(trailerAdapter);

        trailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = movie.getTrailers().get(position).getKey();

                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + key));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            }
        });

    }

    private void loadMovie(Integer movieId) {
        showMovieView();

        String urlMovieDetails = getResources().getString(R.string.movie_db_base_url) + getResources().getString(R.string.movie_db_movie_detail) + movieId;

        String endpointMovieReviews = getResources().getString(R.string.movie_db_movie_reviews).replace(getResources().getString(R.string.pattern_movie_id), movieId.toString());
        String urlMovieReviews = getResources().getString(R.string.movie_db_base_url) + endpointMovieReviews;

        String endpointMovieTrailers = getResources().getString(R.string.movie_db_movie_trailers).replace(getResources().getString(R.string.pattern_movie_id), movieId.toString());
        String urlMovieTrailers = getResources().getString(R.string.movie_db_base_url) + endpointMovieTrailers;

        new FetchMovieTask().execute(urlMovieDetails, urlMovieReviews, urlMovieTrailers);
    }

    private void showMovieView() {
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        scrollView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        scrollView.setVisibility(View.INVISIBLE);
        errorMessageDisplay.setVisibility(View.VISIBLE);
    }


    public class FetchMovieTask extends AsyncTask<String, Void, Movie> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String apiKeyParam = getResources().getString(R.string.movie_db_api_key_param);
            String apiKeyValue = getResources().getString(R.string.movie_db_api_key_value);

            String urlMovieDetails = params[0];
            String urlMovieReviews = params[1];
            String urlMovieTrailers = params[2];

            URL requestUrlMovieDetails = NetworkUtils.buildUrl(urlMovieDetails, apiKeyParam, apiKeyValue);
            URL requestUrlMovieReviews = NetworkUtils.buildUrl(urlMovieReviews, apiKeyParam, apiKeyValue);
            URL requestUrlMovieTrailers = NetworkUtils.buildUrl(urlMovieTrailers, apiKeyParam, apiKeyValue);

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrlMovieDetails);
                Movie movie = MoviesParser
                        .getMovieFromJson(jsonResponse);

                jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrlMovieReviews);
                List<Review> reviews = MoviesParser
                        .getReviewsFromJson(jsonResponse);

                jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrlMovieTrailers);
                List<Trailer> trailers = MoviesParser
                        .getTrailersFromJson(jsonResponse);

                movie.setReviews(reviews);
                movie.setTrailers(trailers);

                return movie;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie movieLoaded) {
            loadingIndicator.setVisibility(View.INVISIBLE);

            if (movieLoaded != null) {
                showMovieView();
                movie = movieLoaded;
                loadMovieInViews();
            } else {
                showErrorMessage();
            }
        }
    }
}
