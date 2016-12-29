package com.codeu.android.codeuproject.data;

/**
 * Created by geordywilliams on 8/9/15.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class GameDataContract {

    public static final String CONTENT_AUTHORITY = "com.codeu.android.codeuproject";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_GAME = "game";

    public static final class GameEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GAME).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GAME;

        public static final String TABLE_NAME = "games";
        public static final String COLUMN_GAME_ID = "id";
        public static final String COLUMN_GAME_NAME = "name";
        public static final String COLUMN_DECK = "deck";
        public static final String COLUMN_RELEASE_DATE = "original_release_date";
        public static final String COLUMN_PLATFORMS = "platforms";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_GENRES = "genres";
        public static final String COLUMN_DEVELOPERS = "developers";
        public static final String COLUMN_PUBLISHERS = "publishers";
        public static final String COLUMN_SIMILAR_GAMES = "similar_games";

        public static Uri buildGameUri() {
            return CONTENT_URI;
        }

        public static Uri buildGameWithID(String gameId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(gameId).build();
        }

        public static long getGameIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

}
