package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.util.MoviesParser;
import com.example.android.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    public final static String EXTRA_MOVIE = "movieId";

    private TextView errorMessageDisplay;
    private ProgressBar loadingIndicator;
    private ScrollView scrollView;

    private ImageView poster;
    private TextView originalTitle;
    private TextView releaseDate;
    private TextView voteAverage;
    private TextView overview;

    private Movie movie;

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

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_MOVIE)) {
            Integer movieId = (Integer) intent.getSerializableExtra(EXTRA_MOVIE);
            loadMovie(movieId);
        }

    }

    private void loadMovieInViews () {
        String posterPath = movie.getPosterPath();
        String posterUrl = getResources().getString(R.string.movie_db_base_url_poster) + posterPath;
        Picasso.with(this).load(posterUrl).into(poster);

        originalTitle.setText(movie.getOriginalTitle());
        releaseDate.setText(movie.getRealeseDate());
        voteAverage.setText(movie.getVoteAverage());
        overview.setText(movie.getOverview());
    }

    private void loadMovie(Integer movieId) {
        showMovieView();
        String url = getResources().getString(R.string.movie_db_base_url) + getResources().getString(R.string.movie_db_movie_detail) + movieId;
        new FetchMovieTask().execute(url);
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

            String url = params[0];
            URL requestUrl = NetworkUtils.buildUrl(url, apiKeyParam, apiKeyValue);

            try {
                String jsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(requestUrl);

                Movie movie = MoviesParser
                        .getMovieFromJson(MovieDetailActivity.this, jsonResponse);

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
