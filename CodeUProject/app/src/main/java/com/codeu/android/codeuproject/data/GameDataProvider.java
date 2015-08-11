package com.codeu.android.codeuproject.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.media.Rating;
import android.net.Uri;

/**
 * Created by geordywilliams on 8/10/15.
 */
public class GameDataProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private GameDataDbHelper mOpenHelper;

    static final int GAME = 100;
    static final int GAME_WITH_NAME = 101;
    static final int RATING = 300;

    private static final SQLiteQueryBuilder sGameQueryBuilder;

    static{
        sGameQueryBuilder = new SQLiteQueryBuilder();
    }

    //name = ?
    private static final String sGameSelection =
            GameDataContract.GameEntry.COLUMN_GAME_NAME + " = ? ";

    private Cursor getGameByName(
            Uri uri, String[] projection, String sortOrder) {
        String gameName = GameDataContract.GameEntry.getGameNameFromUri(uri);

        return sGameQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                //sLocationSettingAndDaySelection,
                sGameSelection,
                //new String[]{locationSetting, Long.toString(date)},
                new String[]{gameName},
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = GameDataContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, GameDataContract.PATH_GAME, GAME);
        matcher.addURI(authority, GameDataContract.PATH_GAME + "/*", GAME_WITH_NAME);

        matcher.addURI(authority, GameDataContract.PATH_RATING, RATING);
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
            case GAME_WITH_NAME:
                return GameDataContract.GameEntry.CONTENT_ITEM_TYPE;
            case GAME:
                return GameDataContract.GameEntry.CONTENT_TYPE;
            case RATING:
                return GameDataContract.RatingEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "game/*"
            case GAME_WITH_NAME:
            {
                retCursor = getGameByName(uri, projection, sortOrder);
                break;
            }
            // "game"
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
            // "rating"
            case RATING: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        GameDataContract.RatingEntry.TABLE_NAME,
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
                    returnUri = GameDataContract.GameEntry.buildGameUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case RATING: {
                long _id = db.insert(GameDataContract.GameEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = GameDataContract.RatingEntry.buildRatingUri(_id);
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
            case RATING:
                rowsDeleted = db.delete(
                        GameDataContract.RatingEntry.TABLE_NAME, selection, selectionArgs);
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
            case RATING:
                rowsUpdated = db.update(GameDataContract.RatingEntry.TABLE_NAME, values, selection,
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
            case RATING:
                db.beginTransaction();
                returnCount = 0;
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
