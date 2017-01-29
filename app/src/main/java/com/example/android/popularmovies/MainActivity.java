package com.example.android.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.adapter.MovieAdapter;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.util.MoviesParser;
import com.example.android.popularmovies.util.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String MOVIES_LOADED = "moviesLoaded";

    private RecyclerView recyclerView;
    private TextView errorMessageDisplay;
    private ProgressBar loadingIndicator;

    private MovieAdapter movieAdapter;

    List<Movie> moviesLoaded = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        errorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        loadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        GridLayoutManager gridLayoutManager;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            gridLayoutManager = new GridLayoutManager(this, 3);
        }
        else{
            gridLayoutManager = new GridLayoutManager(this, 5);
        }

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIES_LOADED)) {
            moviesLoaded = savedInstanceState.getParcelableArrayList(MOVIES_LOADED);
        }

        movieAdapter = new MovieAdapter(this, moviesLoaded);
        recyclerView.setAdapter(movieAdapter);

        loadMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_LOADED, (ArrayList<? extends Parcelable>) moviesLoaded);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_popular) {
            movieAdapter.setMovies(null);
            moviesLoaded = null;
            String endpoint = getResources().getString(R.string.movie_db_popular);
            loadMovies(endpoint);
            return true;
        } else if (id == R.id.action_rated) {
            movieAdapter.setMovies(null);
            moviesLoaded = null;
            String endpoint = getResources().getString(R.string.movie_db_top_rated);
            loadMovies(endpoint);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Integer movieId) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movieId);

        startActivity(intent);
    }

    private void loadMovies() {
        String endpoint = getResources().getString(R.string.movie_db_popular);
        loadMovies(endpoint);
    }

    private void loadMovies(String endpoint) {
        showMoviesView();
        String url = getResources().getString(R.string.movie_db_base_url) + endpoint;

        if (moviesLoaded == null || moviesLoaded.size() == 0) {
            new FetchMoviesTask().execute(url);
        }
    }

    private void showMoviesView() {
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

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

                List<Movie> movies = MoviesParser
                        .getMoviesFromJson(MainActivity.this, jsonResponse);

                return movies;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            loadingIndicator.setVisibility(View.INVISIBLE);

            if (movies != null) {
                showMoviesView();
                movieAdapter.setMovies(movies);
                moviesLoaded = movies;
            } else {
                showErrorMessage();
            }
        }
    }
}
