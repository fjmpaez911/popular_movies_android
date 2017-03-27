package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.android.popularmovies.data.MoviesContract;
import com.example.android.popularmovies.data.MoviesDbHelper;
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
    public final static String EXTRA_MOVIE_FAVORITE = "movieFavorite";
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
    private ImageView favoriteStar;

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
        favoriteStar = (ImageView) findViewById(R.id.favorite);

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_LOADED)) {
            movie = savedInstanceState.getParcelable(MOVIE_LOADED);
            showMovieView();
            loadMovieInViews();
        } else {
            Intent intent = getIntent();

            if (intent.hasExtra(EXTRA_MOVIE)) {
                Integer movieId = (Integer) intent.getSerializableExtra(EXTRA_MOVIE);

                if (intent.hasExtra(EXTRA_MOVIE_FAVORITE)) {
                    Movie favoriteMovie = getFavoriteMovie(movieId);
                    loadFavoriteMovie(favoriteMovie);
                } else {
                    loadMovie(movieId);
                }
            }

            scrollView.fullScroll(ScrollView.FOCUS_UP);
            scrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            }, 600);
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

    public void markAsFavorite (View view) {
        if (movie.getFavoriteFlag() == 0) {
            movie.setFavoriteFlag(1);
            favoriteStar.setImageResource(R.drawable.star_favorite);
            saveMovie();
        } else {
            movie.setFavoriteFlag(0);
            favoriteStar.setImageResource(R.drawable.star_favorite_disable);
            deleteMovie();
        }
    }

    private void loadMovieInViews () {
        String posterPath = movie.getPosterPath();
        String posterUrl = getResources().getString(R.string.movie_db_base_url_poster) + posterPath;
        Picasso.with(this).load(posterUrl).into(poster);

        if (movie.getFavoriteFlag() == 1) {
            favoriteStar.setImageResource(R.drawable.star_favorite);
        } else {
            favoriteStar.setImageResource(R.drawable.star_favorite_disable);
        }

        originalTitle.setText(movie.getOriginalTitle());
        releaseDate.setText(movie.getReleaseDate());
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

    private void loadFavoriteMovie(Movie movie) {
        showMovieView();
        loadingIndicator.setVisibility(View.VISIBLE);
        loadMovie(movie);
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
            loadMovie(movieLoaded);
        }
    }

    private void loadMovie(Movie movieLoaded) {
        loadingIndicator.setVisibility(View.INVISIBLE);

        if (movieLoaded != null) {
            showMovieView();
            movie = movieLoaded;
            loadMovieInViews();
        } else {
            showErrorMessage();
        }
    }

    private void deleteMovie() {

        getContentResolver().delete(MoviesContract.MoviesEntry.CONTENT_URI,
                MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] {String.valueOf(movie.getId())}
        );

        getContentResolver().delete(MoviesContract.ReviewsEntry.CONTENT_URI,
                MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] {String.valueOf(movie.getId())}
        );

        getContentResolver().delete(MoviesContract.TrailersEntry.CONTENT_URI,
                MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] {String.valueOf(movie.getId())}
        );
    }

    private void saveMovie() {

        ContentValues cvMovie = new ContentValues();

        cvMovie.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movie.getId());
        cvMovie.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movie.getOriginalTitle());
        cvMovie.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
        cvMovie.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        cvMovie.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        cvMovie.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        cvMovie.put(MoviesContract.MoviesEntry.COLUMN_IS_FAVORITE, 1);

        Uri uri = getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, cvMovie);

        if (uri != null) {

            if (movie.getReviews() != null && movie.getReviews().size() > 0) {
                for (Review review : movie.getReviews()) {

                    ContentValues cvReview = new ContentValues();

                    cvReview.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_ID, review.getId());
                    cvReview.put(MoviesContract.ReviewsEntry.COLUMN_AUTHOR, review.getAuthor());
                    cvReview.put(MoviesContract.ReviewsEntry.COLUMN_CONTENT, review.getContent());
                    cvReview.put(MoviesContract.ReviewsEntry.COLUMN_URL, review.getUrl());
                    cvReview.put(MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID, movie.getId());

                    getContentResolver().insert(MoviesContract.ReviewsEntry.CONTENT_URI, cvReview);
                }
            }

            if (movie.getTrailers() != null && movie.getTrailers().size() > 0) {
                for (Trailer trailer : movie.getTrailers()) {

                    ContentValues cvTrailer = new ContentValues();

                    cvTrailer.put(MoviesContract.TrailersEntry.COLUMN_TRAILER_ID, trailer.getId());
                    cvTrailer.put(MoviesContract.TrailersEntry.COLUMN_ISO_639_1, trailer.getIso_639_1());
                    cvTrailer.put(MoviesContract.TrailersEntry.COLUMN_ISO_3166_1, trailer.getIso_3166_1());
                    cvTrailer.put(MoviesContract.TrailersEntry.COLUMN_KEY, trailer.getKey());
                    cvTrailer.put(MoviesContract.TrailersEntry.COLUMN_NAME, trailer.getName());
                    cvTrailer.put(MoviesContract.TrailersEntry.COLUMN_SITE, trailer.getSite());
                    cvTrailer.put(MoviesContract.TrailersEntry.COLUMN_SIZE, trailer.getSize());
                    cvTrailer.put(MoviesContract.TrailersEntry.COLUMN_TYPE, trailer.getType());
                    cvTrailer.put(MoviesContract.TrailersEntry.COLUMN_MOVIE_ID, movie.getId());

                    getContentResolver().insert(MoviesContract.TrailersEntry.CONTENT_URI, cvTrailer);
                }
            }
        }
    }

    private Movie getFavoriteMovie(int movieId) {

        Movie movie = new Movie();

        Cursor cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] {String.valueOf(movieId)},
                MoviesContract.MoviesEntry._ID
        );

        cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            movie.setId(cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID)));
            movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH)));
            movie.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_OVERVIEW)));
            movie.setVoteAverage(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE)));
            movie.setFavoriteFlag(cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_IS_FAVORITE)));
        }

        movie.setReviews(getFavoriteMovieReviews(movie.getId()));
        movie.setTrailers(getFavoriteMovieTrailers(movie.getId()));

        return movie;
    }

    private List<Review> getFavoriteMovieReviews(int movieId) {

        List<Review> reviews = new ArrayList<>();

        Cursor cursor = getContentResolver().query(MoviesContract.ReviewsEntry.CONTENT_URI,
                null,
                MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] {String.valueOf(movieId)},
                MoviesContract.ReviewsEntry._ID
        );

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Review review = new Review();

            review.setId(cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewsEntry.COLUMN_REVIEW_ID)));
            review.setAuthor(cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewsEntry.COLUMN_AUTHOR)));
            review.setContent(cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewsEntry.COLUMN_CONTENT)));
            review.setUrl(cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewsEntry.COLUMN_URL)));

            reviews.add(review);

            cursor.moveToNext();
        }

        return reviews;
    }

    private List<Trailer> getFavoriteMovieTrailers(int movieId) {

        List<Trailer> trailers = new ArrayList<>();

        Cursor cursor = getContentResolver().query(MoviesContract.TrailersEntry.CONTENT_URI,
                null,
                MoviesContract.TrailersEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] {String.valueOf(movieId)},
                MoviesContract.TrailersEntry._ID
        );

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Trailer trailer = new Trailer();

            trailer.setId(cursor.getString(cursor.getColumnIndex(MoviesContract.TrailersEntry.COLUMN_TRAILER_ID)));
            trailer.setIso_639_1(cursor.getString(cursor.getColumnIndex(MoviesContract.TrailersEntry.COLUMN_ISO_639_1)));
            trailer.setIso_3166_1(cursor.getString(cursor.getColumnIndex(MoviesContract.TrailersEntry.COLUMN_ISO_3166_1)));
            trailer.setKey(cursor.getString(cursor.getColumnIndex(MoviesContract.TrailersEntry.COLUMN_KEY)));
            trailer.setName(cursor.getString(cursor.getColumnIndex(MoviesContract.TrailersEntry.COLUMN_NAME)));
            trailer.setSite(cursor.getString(cursor.getColumnIndex(MoviesContract.TrailersEntry.COLUMN_SITE)));
            trailer.setSize(cursor.getInt(cursor.getColumnIndex(MoviesContract.TrailersEntry.COLUMN_SIZE)));
            trailer.setType(cursor.getString(cursor.getColumnIndex(MoviesContract.TrailersEntry.COLUMN_TYPE)));

            trailers.add(trailer);

            cursor.moveToNext();
        }

        return trailers;
    }
}
