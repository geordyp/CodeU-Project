package com.codeu.android.codeuproject.data;

/**
 * Created by geordywilliams on 8/10/15.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.codeu.android.codeuproject.data.GameDataContract.GameEntry;

public class GameDataDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 12;

    static final String DATABASE_NAME = "gameData.db";

    public GameDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_GAME_TABLE = "CREATE TABLE " + GameEntry.TABLE_NAME + " (" +
                GameEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                GameEntry.COLUMN_GAME_ID + " INTEGER NOT NULL, " +
                GameEntry.COLUMN_GAME_NAME + " TEXT NOT NULL, " +
                GameEntry.COLUMN_DECK + " TEXT NOT NULL, " +
                GameEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                GameEntry.COLUMN_PLATFORMS + " TEXT NOT NULL, " +
                GameEntry.COLUMN_IMAGE + " TEXT, " +
                GameEntry.COLUMN_GENRES + " TEXT, " +
                GameEntry.COLUMN_DEVELOPERS + " TEXT, " +
                GameEntry.COLUMN_PUBLISHERS + " TEXT, " +
                GameEntry.COLUMN_SIMILAR_GAMES + " TEXT, " +

                " UNIQUE (" + GameEntry.COLUMN_GAME_ID + ", " +
                GameEntry.COLUMN_GAME_NAME + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_GAME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GameEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
