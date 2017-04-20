package com.example.android.vocabulary.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nadina on 19.04.2017.
 */

public class WordDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wordsDb.db";

    private static final int VERSION = 1;

    public WordDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE = "CREATE TABLE " + WordContract.WordEntry.TABLE_NAME + " (" +
                WordContract.WordEntry._ID + " INTEGER PRIMARY KEY, " +
                WordContract.WordEntry.COLUMN_LANGUAGES + " TEXT NOT NULL, " +
                WordContract.WordEntry.COLUMN_WORD + " TEXT NOT NULL, " +
                WordContract.WordEntry.COLUMN_TRANSLATE + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WordContract.WordEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
