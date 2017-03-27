package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 4;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " ( " +
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE, " +
                MoviesContract.MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_IS_FAVORITE+ " INTEGER NOT NULL DEFAULT 0" +
                " )";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + MoviesContract.ReviewsEntry.TABLE_NAME + " ( " +
                MoviesContract.ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.ReviewsEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                MoviesContract.ReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MoviesContract.ReviewsEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                MoviesContract.ReviewsEntry.COLUMN_URL + " TEXT NOT NULL, " +
                MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL" +
                " )";

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + MoviesContract.TrailersEntry.TABLE_NAME + " ( " +
                MoviesContract.TrailersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.TrailersEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_ISO_639_1 + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_ISO_3166_1 + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_SITE + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_SIZE + " INTEGER NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL" +
                " )";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.TrailersEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.ReviewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);

        onCreate(db);
    }
}
