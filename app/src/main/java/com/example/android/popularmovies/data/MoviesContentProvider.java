package com.example.android.popularmovies.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MoviesContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;
    public static final int REVIEWS = 200;
    public static final int REVIEWS_WITH_ID = 201;
    public static final int TRAILERS = 300;
    public static final int TRAILERS_WITH_ID = 301;

    private static final UriMatcher sUriMatcher = builDUriMatcher();

    private MoviesDbHelper dbHelper;

    private static UriMatcher builDUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);

        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_REVIEWS + "/#", REVIEWS_WITH_ID);

        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_TRAILERS, TRAILERS);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_TRAILERS + "/#", TRAILERS_WITH_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase mDB = dbHelper.getReadableDatabase();

        String id = null;
        String mSelection = null;
        String[] mSelectionArgs = null;


        Cursor cursor = null;

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                cursor = mDB.query(MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case MOVIES_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = MoviesContract.MoviesEntry._ID + " = ?";
                mSelectionArgs = new String[] {String.valueOf(id)};

                cursor = mDB.query(MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case REVIEWS:
                cursor = mDB.query(MoviesContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case REVIEWS_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = MoviesContract.ReviewsEntry._ID + " = ?";
                mSelectionArgs = new String[] {String.valueOf(id)};

                cursor = mDB.query(MoviesContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case TRAILERS:
                cursor = mDB.query(MoviesContract.TrailersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case TRAILERS_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = MoviesContract.TrailersEntry._ID + " = ?";
                mSelectionArgs = new String[] {String.valueOf(id)};

                cursor = mDB.query(MoviesContract.TrailersEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase mDB = dbHelper.getWritableDatabase();

        Uri resultUri = null;

        long rowID;

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                rowID = mDB.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, values);
                if (rowID > 0) {
                    resultUri = ContentUris.withAppendedId(MoviesContract.MoviesEntry.CONTENT_URI, rowID);
                }
                break;

            case REVIEWS:
                rowID = mDB.insert(MoviesContract.ReviewsEntry.TABLE_NAME, null, values);
                if (rowID > 0) {
                    resultUri = ContentUris.withAppendedId(MoviesContract.ReviewsEntry.CONTENT_URI, rowID);
                }
                break;

            case TRAILERS:
                rowID = mDB.insert(MoviesContract.TrailersEntry.TABLE_NAME, null, values);
                if (rowID > 0) {
                    resultUri = ContentUris.withAppendedId(MoviesContract.TrailersEntry.CONTENT_URI, rowID);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        if (resultUri == null) {
            throw new SQLException("Failed to insert row into " + uri);
        } else {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase mDB = dbHelper.getWritableDatabase();

        int rowsAffected = 0;

        mDB.delete(MoviesContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                rowsAffected = mDB.delete(MoviesContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case REVIEWS:
                rowsAffected = mDB.delete(MoviesContract.ReviewsEntry.TABLE_NAME, selection, selectionArgs);

                break;

            case TRAILERS:
                rowsAffected = mDB.delete(MoviesContract.TrailersEntry.TABLE_NAME, selection, selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsAffected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
