package com.example.android.popularmovies.util;


import android.content.Context;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class MoviesParser {

    private static final String ID = "id";
    private static final String MOVIE_TITLE = "original_title";
    private static final String POSTER_PATH = "poster_path";
    private static final String RELEASE_DATE = "release_date";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String OVERVIEW = "overview";
    private static final String MOVIE_LIST = "results";

    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEW_CONTENT = "content";
    private static final String REVIEW_URL = "url";

    private static final String TRAILER_ISO_639_1 = "iso_639_1";
    private static final String TRAILER_ISO_3166_1 = "iso_3166_1";
    private static final String TRAILER_KEY = "key";
    private static final String TRAILER_NAME = "name";
    private static final String TRAILER_SITE = "site";
    private static final String TRAILER_SIZE = "size";
    private static final String TRAILER_TYPE = "type";

    public static List<Movie> getMoviesFromJson(String moviesJson) throws JSONException {

        List<Movie> movies = new ArrayList<>();

        if (moviesJson != null) {

            JSONObject response = new JSONObject(moviesJson);
            JSONArray moviesArray = response.getJSONArray(MOVIE_LIST);

            for (int i = 0; i < moviesArray.length(); i++) {

                JSONObject movieJson = moviesArray.getJSONObject(i);

                Integer movieId = movieJson.getInt(ID);
                String title = movieJson.getString(MOVIE_TITLE);
                String posterPath = movieJson.getString(POSTER_PATH);

                Movie movie = new Movie(movieId, title, posterPath);
                movies.add(movie);
            }
        }

        return movies;
    }

    public static Movie getMovieFromJson(String movieJson) throws JSONException {

        Movie movie = null;

        if (movieJson != null) {

            JSONObject response = new JSONObject(movieJson);

            Integer movieId = response.getInt(ID);
            String title = response.getString(MOVIE_TITLE);
            String posterPath = response.getString(POSTER_PATH);
            String releaseDate = response.getString(RELEASE_DATE);
            String voteAverage = response.getString(VOTE_AVERAGE);
            String overview = response.getString(OVERVIEW);

            movie = new Movie(movieId, title, posterPath, overview, voteAverage, releaseDate);

        }

        return movie;
    }

    public static List<Review> getReviewsFromJson(String reviewsJson) throws JSONException {

        List<Review> reviews = new ArrayList<>();

        if (reviewsJson != null) {

            JSONObject response = new JSONObject(reviewsJson);
            JSONArray reviewsArray = response.getJSONArray(MOVIE_LIST);

            for (int i = 0; i < reviewsArray.length(); i++) {

                JSONObject reviewJson = reviewsArray.getJSONObject(i);

                String id = reviewJson.getString(ID);
                String author = reviewJson.getString(REVIEW_AUTHOR);
                String content = reviewJson.getString(REVIEW_CONTENT);
                String url = reviewJson.getString(REVIEW_URL);

                Review review = new Review(id, author, content, url);
                reviews.add(review);
            }
        }

        return reviews;
    }

    public static List<Trailer> getTrailersFromJson(String trailersJson) throws JSONException {

        List<Trailer> trailers = new ArrayList<>();

        if (trailersJson != null) {

            JSONObject response = new JSONObject(trailersJson);
            JSONArray trailersArray = response.getJSONArray(MOVIE_LIST);

            for (int i = 0; i < trailersArray.length(); i++) {

                JSONObject trailerJson = trailersArray.getJSONObject(i);

                String id = trailerJson.getString(ID);
                String iso_639_1 = trailerJson.getString(TRAILER_ISO_639_1);
                String iso_3166_1 = trailerJson.getString(TRAILER_ISO_3166_1);
                String key = trailerJson.getString(TRAILER_KEY);
                String name = trailerJson.getString(TRAILER_NAME);
                String site = trailerJson.getString(TRAILER_SITE);
                Integer size = trailerJson.getInt(TRAILER_SIZE);
                String type = trailerJson.getString(TRAILER_TYPE);

                Trailer trailer = new Trailer(id, iso_639_1, iso_3166_1, key, name, site, size, type);
                trailers.add(trailer);
            }
        }

        return trailers;
    }

}
