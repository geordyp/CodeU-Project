package com.codeu.android.codeuproject.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by geordywilliams on 8/10/15.
 */
public class GameDataProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private GameDataDbHelper mOpenHelper;

    static final int GAME = 100;
    static final int GAME_WITH_ID = 101;

    private static final SQLiteQueryBuilder sGameQueryBuilder;

    static{
        sGameQueryBuilder = new SQLiteQueryBuilder();

        sGameQueryBuilder.setTables(
                GameDataContract.GameEntry.TABLE_NAME);
    }

    //id = ?
    private static final String sGameSelection =
            GameDataContract.GameEntry.TABLE_NAME + "." +
                    GameDataContract.GameEntry.COLUMN_GAME_ID + " = ? ";

    private Cursor getGameById(
            Uri uri, String[] projection, String sortOrder) {

        long gameId = GameDataContract.GameEntry.getGameIdFromUri(uri);

        return sGameQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sGameSelection,
                new String[]{Long.toString(gameId)},
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = GameDataContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, GameDataContract.PATH_GAME, GAME);
        matcher.addURI(authority, GameDataContract.PATH_GAME + "/*", GAME_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new GameDataDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case GAME_WITH_ID:
                return GameDataContract.GameEntry.CONTENT_ITEM_TYPE;
            case GAME:
                return GameDataContract.GameEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            //"game/*"
            case GAME_WITH_ID:
            {
                retCursor = getGameById(uri, projection, sortOrder);
                break;
            }
            //"game"
            case GAME: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        GameDataContract.GameEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case GAME: {
                long _id = db.insert(GameDataContract.GameEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = GameDataContract.GameEntry.buildGameUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsDeleted;

        if (null == selection) selection = "1";

        switch (match) {
            case GAME:
                rowsDeleted = db.delete(
                        GameDataContract.GameEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case GAME:
                rowsUpdated = db.update(GameDataContract.GameEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GAME:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(GameDataContract.GameEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
