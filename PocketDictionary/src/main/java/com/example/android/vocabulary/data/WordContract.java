package com.example.android.vocabulary.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Nadina on 19.04.2017.
 */

public class WordContract {

    public static final String AUTHORITY = "com.example.android.vocabulary";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_WORDS = "words";


    public static final class WordEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORDS).build();

        public static final String TABLE_NAME = "words";

        public static final String COLUMN_LANGUAGES = "languages";

        public static final String COLUMN_WORD = "word";

        public static final String COLUMN_TRANSLATE = "translate";
    }

}
