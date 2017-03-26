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
import android.widget.Toast;

import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.data.LocalCollectionMoviesContract;
import com.example.android.popularmovies.data.LocalCollectionMoviesDbHelper;
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
    private boolean isInFavoriteCollection = false;

    private TrailerAdapter trailerAdapter;

    private SQLiteDatabase mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        LocalCollectionMoviesDbHelper dbHelper = new LocalCollectionMoviesDbHelper(this);
        mDB = dbHelper.getWritableDatabase();

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
            isInFavoriteCollection = savedInstanceState.getBoolean(EXTRA_MOVIE_FAVORITE);
            showMovieView();
            loadMovieInViews();
        } else {
            Intent intent = getIntent();

            if (intent.hasExtra(EXTRA_MOVIE)) {
                Integer movieId = (Integer) intent.getSerializableExtra(EXTRA_MOVIE);

                if (intent.hasExtra(EXTRA_MOVIE_FAVORITE)) {
                    isInFavoriteCollection = true;
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
        outState.putBoolean(EXTRA_MOVIE_FAVORITE, isInFavoriteCollection);
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
        } else {
            movie.setFavoriteFlag(0);
            favoriteStar.setImageResource(R.drawable.star_favorite_disable);
        }
        updateMovie();
    }

    private void loadMovieInViews () {
        String posterPath = movie.getPosterPath();
        String posterUrl = getResources().getString(R.string.movie_db_base_url_poster) + posterPath;
        Picasso.with(this).load(posterUrl).into(poster);

        if (isInFavoriteCollection) {
            if (movie.getFavoriteFlag() == 1) {
                favoriteStar.setImageResource(R.drawable.star_favorite);
            } else {
                favoriteStar.setImageResource(R.drawable.star_favorite_disable);
            }
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
            boolean isLoaded = loadMovie(movieLoaded);
            boolean savingLocalCollection =  Boolean.valueOf(getResources().getString(R.string.enable_saving_local_collection));
            if (isLoaded && savingLocalCollection) {
                saveMovie();
            }
        }
    }

    private boolean loadMovie(Movie movieLoaded) {
        loadingIndicator.setVisibility(View.INVISIBLE);

        if (movieLoaded != null) {
            showMovieView();
            movie = movieLoaded;
            loadMovieInViews();
            return true;
        } else {
            showErrorMessage();
        }
        return false;
    }

    private void updateMovie() {

        ContentValues cvMovie = new ContentValues();

        cvMovie.put(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_IS_FAVORITE, movie.getFavoriteFlag());

        mDB.update(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.TABLE_NAME,
                cvMovie,
                LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] {String.valueOf(movie.getId())}
        );
    }

    // This method is only for storing the favorite movies collection
    private void saveMovie() {

        ContentValues cvMovie = new ContentValues();

        cvMovie.put(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_MOVIE_ID, movie.getId());
        cvMovie.put(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_TITLE, movie.getOriginalTitle());
        cvMovie.put(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
        cvMovie.put(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        cvMovie.put(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        cvMovie.put(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());

        mDB.insert(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.TABLE_NAME, null, cvMovie);

        if (movie.getReviews() != null && movie.getReviews().size() > 0) {
            for (Review review : movie.getReviews()) {

                ContentValues cvReview = new ContentValues();

                cvReview.put(LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_REVIEW_ID, review.getId());
                cvReview.put(LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_AUTHOR, review.getAuthor());
                cvReview.put(LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_CONTENT, review.getContent());
                cvReview.put(LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_URL, review.getUrl());
                cvReview.put(LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_MOVIE_ID, movie.getId());

                mDB.insert(LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.TABLE_NAME, null, cvReview);
            }
        }

        if (movie.getTrailers() != null && movie.getTrailers().size() > 0) {
            for (Trailer trailer : movie.getTrailers()) {

                ContentValues cvTrailer = new ContentValues();

                cvTrailer.put(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_TRAILER_ID, trailer.getId());
                cvTrailer.put(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_ISO_639_1, trailer.getIso_639_1());
                cvTrailer.put(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_ISO_3166_1, trailer.getIso_3166_1());
                cvTrailer.put(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_KEY, trailer.getKey());
                cvTrailer.put(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_NAME, trailer.getName());
                cvTrailer.put(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_SITE, trailer.getSite());
                cvTrailer.put(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_SIZE, trailer.getSize());
                cvTrailer.put(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_TYPE, trailer.getType());
                cvTrailer.put(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_MOVIE_ID, movie.getId());

                mDB.insert(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.TABLE_NAME, null, cvTrailer);
            }
        }

        Toast.makeText(this, "Movie saved in local collection!", Toast.LENGTH_LONG).show();
    }

    private Movie getFavoriteMovie(int movieId) {

        Movie movie = new Movie();

        Cursor cursor = mDB.query(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.TABLE_NAME,
                null,
                LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] {String.valueOf(movieId)},
                null,
                null,
                LocalCollectionMoviesContract.LocalCollectionMoviesEntry._ID
        );

        cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            movie.setId(cursor.getInt(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_MOVIE_ID)));
            movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_TITLE)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_POSTER_PATH)));
            movie.setOverview(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_OVERVIEW)));
            movie.setVoteAverage(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_VOTE_AVERAGE)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_RELEASE_DATE)));
            movie.setFavoriteFlag(cursor.getInt(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_IS_FAVORITE)));
        }

        movie.setReviews(getFavoriteMovieReviews(movie.getId()));
        movie.setTrailers(getFavoriteMovieTrailers(movie.getId()));

        return movie;
    }

    private List<Review> getFavoriteMovieReviews(int movieId) {

        List<Review> reviews = new ArrayList<>();

        Cursor cursor = mDB.query(LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.TABLE_NAME,
                null,
                LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] {String.valueOf(movieId)},
                null,
                null,
                LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry._ID
        );

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Review review = new Review();

            review.setId(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_REVIEW_ID)));
            review.setAuthor(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_AUTHOR)));
            review.setContent(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_CONTENT)));
            review.setUrl(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_URL)));

            reviews.add(review);

            cursor.moveToNext();
        }

        return reviews;
    }

    private List<Trailer> getFavoriteMovieTrailers(int movieId) {

        List<Trailer> trailers = new ArrayList<>();

        Cursor cursor = mDB.query(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.TABLE_NAME,
                null,
                LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] {String.valueOf(movieId)},
                null,
                null,
                LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry._ID
        );

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Trailer trailer = new Trailer();

            trailer.setId(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_TRAILER_ID)));
            trailer.setIso_639_1(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_ISO_639_1)));
            trailer.setIso_3166_1(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_ISO_3166_1)));
            trailer.setKey(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_KEY)));
            trailer.setName(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_NAME)));
            trailer.setSite(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_SITE)));
            trailer.setSize(cursor.getInt(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_SIZE)));
            trailer.setType(cursor.getString(cursor.getColumnIndex(LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_TYPE)));

            trailers.add(trailer);

            cursor.moveToNext();
        }

        return trailers;
    }
}
