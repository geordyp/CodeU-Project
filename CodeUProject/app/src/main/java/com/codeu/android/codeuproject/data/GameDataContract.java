package com.codeu.android.codeuproject.data;

/**
 * Created by geordywilliams on 8/9/15.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

public class GameDataContract {

    public static final String CONTENT_AUTHORITY = "com.codeu.android.codeuproject";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_GAME = "game";
    public static final String PATH_RATING = "rating";

    public static final class RatingEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RATING).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RATING;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RATING;

        public static final String TABLE_NAME = "rating";
        public static final String COLUMN_REVIEWER = "reviewer";
        public static final String COLUMN_REVIEWER_ID = "reviewer_id";
        public static final String COLUMN_GAME = "game";
        public static final String COLUMN_GAME_ID = "game_id";
        public static final String COLUMN_SCORE = "score";

        public static Uri buildRatingUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class GameEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GAME).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GAME;

        public static final String TABLE_NAME = "games";
        public static final String COLUMN_GAME_NAME = "name";
        public static final String COLUMN_GAME_ID = "game_id";
        public static final String COLUMN_DECK = "deck";
        public static final String COLUMN_SITE_URL = "site_detail_url";
        public static final String COLUMN_RELEASE_DATE = "original_release_date";

        public static Uri buildGameUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildGameWithName(String gameName) {

            return CONTENT_URI.buildUpon().appendPath(gameName).build();
        }

        public static String getGameNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

}
