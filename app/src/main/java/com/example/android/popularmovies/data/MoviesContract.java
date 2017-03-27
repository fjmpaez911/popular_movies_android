package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {

    public static final String AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = MoviesEntry.TABLE_NAME;
    public static final String PATH_REVIEWS = ReviewsEntry.TABLE_NAME;
    public static final String PATH_TRAILERS = TrailersEntry.TABLE_NAME;

    private MoviesContract() {
    }

    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_IS_FAVORITE = "favorite";

    }

    public static final class ReviewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_MOVIE_ID = "movie_id";

    }

    public static final class TrailersEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        public static final String TABLE_NAME = "trailers";
        public static final String COLUMN_TRAILER_ID = "trailer_id";
        public static final String COLUMN_ISO_639_1 = "iso_639_1";
        public static final String COLUMN_ISO_3166_1 = "iso_3166_1";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_MOVIE_ID = "movie_id";

    }
}
