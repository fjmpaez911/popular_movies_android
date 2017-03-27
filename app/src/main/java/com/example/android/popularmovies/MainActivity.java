package com.example.android.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;

import com.example.android.popularmovies.adapter.MovieAdapter;
import com.example.android.popularmovies.data.MoviesContract;
import com.example.android.popularmovies.data.MoviesDbHelper;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.util.MoviesParser;
import com.example.android.popularmovies.util.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String MOVIES_LOADED = "moviesLoaded";
    private static final String MOVIES_FAVORITES = "moviesFavorites";

    private RecyclerView recyclerView;
    private TextView errorMessageDisplay;
    private ProgressBar loadingIndicator;

    private MovieAdapter movieAdapter;

    List<Movie> moviesLoaded = new ArrayList<>();

    private SQLiteDatabase mDB;

    private Boolean favorites = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MoviesDbHelper dbHelper = new MoviesDbHelper(this);
        mDB = dbHelper.getWritableDatabase();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        errorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        loadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        GridLayoutManager gridLayoutManager;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            gridLayoutManager = new GridLayoutManager(this, 2);
        }
        else{
            gridLayoutManager = new GridLayoutManager(this, 3);
        }

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIES_LOADED) && savedInstanceState.containsKey(MOVIES_FAVORITES) ) {
            moviesLoaded = savedInstanceState.getParcelableArrayList(MOVIES_LOADED);
            favorites = savedInstanceState.getBoolean(MOVIES_FAVORITES);
        }

        movieAdapter = new MovieAdapter(this, moviesLoaded);
        recyclerView.setAdapter(movieAdapter);

        loadMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_LOADED, (ArrayList<? extends Parcelable>) moviesLoaded);
        outState.putBoolean(MOVIES_FAVORITES, favorites);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (favorites) {
            movieAdapter.setMovies(null);
            moviesLoaded = null;
            loadMoviesLocalCollection();
        }

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

        favorites = false;

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
        } else if (id == R.id.action_favorites) {
            movieAdapter.setMovies(null);
            moviesLoaded = null;
            loadMoviesLocalCollection();
            favorites = true;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Integer movieId) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movieId);

        if (favorites) {
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_FAVORITE, movieId);
        }

        startActivity(intent);
    }

    private void loadMoviesLocalCollection() {

        showMoviesView();

        if (moviesLoaded == null || moviesLoaded.size() == 0) {
            loadingIndicator.setVisibility(View.VISIBLE);
            List<Movie> movies = getFavoritesMovies();
            if (movies.isEmpty()) {
                loadingIndicator.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "The local collection is empty.", Toast.LENGTH_LONG).show();
            } else {
                loadMovies(movies);
            }
        }
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
                        .getMoviesFromJson(jsonResponse);

                return movies;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            loadMovies(movies);
        }
    }

    private void loadMovies(List<Movie> movies) {
        loadingIndicator.setVisibility(View.INVISIBLE);

        if (movies != null) {
            showMoviesView();
            movieAdapter.setMovies(movies);
            moviesLoaded = movies;
        } else {
            showErrorMessage();
        }
    }

    private List<Movie> getFavoritesMovies() {

        List<Movie> movies = new ArrayList<>();

        Cursor cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                MoviesContract.MoviesEntry._ID
        );

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Movie movie = new Movie();

            movie.setId(cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID)));
            movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH)));
            movie.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_OVERVIEW)));
            movie.setVoteAverage(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE)));

            movies.add(movie);

            cursor.moveToNext();
        }

        return movies;
    }
}
