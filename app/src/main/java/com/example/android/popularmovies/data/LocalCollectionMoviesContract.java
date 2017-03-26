package com.example.android.popularmovies.data;

import android.provider.BaseColumns;

public class LocalCollectionMoviesContract {

    private LocalCollectionMoviesContract() {
    }

    public static final class LocalCollectionMoviesEntry implements BaseColumns {

        public static final String TABLE_NAME = "local_collections_movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_IS_FAVORITE = "favorite";

    }

    public static final class LocalCollectionMoviesReviewsEntry implements BaseColumns {

        public static final String TABLE_NAME = "local_collections_movies_reviews";
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_MOVIE_ID = "movie_id";

    }

    public static final class LocalCollectionMoviesTrailersEntry implements BaseColumns {

        public static final String TABLE_NAME = "local_collections_movies_trailers";
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
