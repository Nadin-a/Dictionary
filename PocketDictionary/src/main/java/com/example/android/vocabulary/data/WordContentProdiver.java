package com.example.android.vocabulary.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by Nadina on 19.04.2017.
 */

public class WordContentProdiver extends ContentProvider {

    public static final int WORDS = 100;
    public static final int WORDS_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(WordContract.AUTHORITY, WordContract.PATH_WORDS, WORDS);
        uriMatcher.addURI(WordContract.AUTHORITY, WordContract.PATH_WORDS + "/#", WORDS_WITH_ID);

        return uriMatcher;
    }

    private WordDbHelper mWordDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mWordDbHelper = new WordDbHelper(context);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mWordDbHelper.getReadableDatabase();
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case WORDS: {
                retCursor = db.query(WordContract.WordEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mWordDbHelper.getWritableDatabase();

        Uri returnUri;



        switch (sUriMatcher.match(uri)) {
            case WORDS: {
                long id = db.insert(WordContract.WordEntry.TABLE_NAME, null, contentValues);
                Log.d("====","insert " + contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(WordContract.WordEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mWordDbHelper.getWritableDatabase();

        int taskDeleted;

        switch (sUriMatcher.match(uri)) {
            case WORDS_WITH_ID: {
                String id = uri.getPathSegments().get(1);
                taskDeleted = db.delete(WordContract.WordEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        if (taskDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return taskDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
