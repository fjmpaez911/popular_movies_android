package com.example.android.popularmovies.util;


import android.content.Context;

import com.example.android.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class MoviesParser {

    public static List<Movie> getMoviesFromJson(Context context, String moviesJson) throws JSONException {

        final String MOVIE_LIST = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "original_title";
        final String POSTER_PATH = "poster_path";

        List<Movie> movies = new ArrayList<>();

        if (moviesJson != null) {

            JSONObject response = new JSONObject(moviesJson);
            JSONArray moviesArray = response.getJSONArray(MOVIE_LIST);

            for (int i = 0; i < moviesArray.length(); i++) {

                JSONObject movieJson = moviesArray.getJSONObject(i);

                Integer movieId = movieJson.getInt(MOVIE_ID);
                String title = movieJson.getString(MOVIE_TITLE);
                String posterPath = movieJson.getString(POSTER_PATH);

                Movie movie = new Movie(movieId, title, posterPath);
                movies.add(movie);
            }
        }

        return movies;
    }

    public static Movie getMovieFromJson(Context context, String movieJson) throws JSONException {

        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "original_title";
        final String POSTER_PATH = "poster_path";
        final String RELEASE_DATE = "release_date";
        final String VOTE_AVERAGE = "vote_average";
        final String OVERVIEW = "overview";

        Movie movie = null;

        if (movieJson != null) {

            JSONObject response = new JSONObject(movieJson);

            Integer movieId = response.getInt(MOVIE_ID);
            String title = response.getString(MOVIE_TITLE);
            String posterPath = response.getString(POSTER_PATH);
            String releaseDate = response.getString(RELEASE_DATE);
            String voteAverage = response.getString(VOTE_AVERAGE);
            String overview = response.getString(OVERVIEW);

            movie = new Movie(movieId, title, posterPath, overview, voteAverage, releaseDate);

        }

        return movie;
    }
}
