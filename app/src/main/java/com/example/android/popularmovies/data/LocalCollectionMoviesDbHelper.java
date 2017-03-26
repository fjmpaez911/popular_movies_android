package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalCollectionMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 2;

    public LocalCollectionMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + LocalCollectionMoviesContract.LocalCollectionMoviesEntry.TABLE_NAME + " ( " +
                LocalCollectionMoviesContract.LocalCollectionMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesEntry.COLUMN_IS_FAVORITE+ " INTEGER NOT NULL DEFAULT 0" +
                " )";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.TABLE_NAME + " ( " +
                LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_URL + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL" +
                " )";

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.TABLE_NAME + " ( " +
                LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_ISO_639_1 + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_ISO_3166_1 + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_SITE + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_SIZE + " INTEGER NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL" +
                " )";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + LocalCollectionMoviesContract.LocalCollectionMoviesTrailersEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LocalCollectionMoviesContract.LocalCollectionMoviesReviewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LocalCollectionMoviesContract.LocalCollectionMoviesEntry.TABLE_NAME);

        onCreate(db);
    }
}
