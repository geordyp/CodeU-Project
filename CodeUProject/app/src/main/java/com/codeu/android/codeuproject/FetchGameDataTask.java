package com.codeu.android.codeuproject;

/**
 * Created by geordywilliams on 8/10/15.
 */

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.codeu.android.codeuproject.data.GameDataContract;
import com.codeu.android.codeuproject.data.GameDataContract.GameEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class FetchGameDataTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchGameDataTask.class.getSimpleName();

    private ArrayAdapter<String> mGiantBombAdapter;
    private final Context mContext;

    public FetchGameDataTask(Context context, ArrayAdapter<String> giantBombAdapter) {
        mContext = context;
        mGiantBombAdapter = giantBombAdapter;
    }

    private boolean DEBUG = true;

    private long addRating(String reviewer, String game, int score) {
        long ratingId;

        int reviewer_id = hash(reviewer);
        int game_id = hash(game);

        ContentValues ratingValues = new ContentValues();
        ratingValues.put(GameDataContract.RatingEntry.COLUMN_REVIEWER, reviewer);
        ratingValues.put(GameDataContract.RatingEntry.COLUMN_REVIEWER_ID, reviewer_id);
        ratingValues.put(GameDataContract.RatingEntry.COLUMN_GAME, game);
        ratingValues.put(GameDataContract.RatingEntry.COLUMN_GAME_ID, game_id);
        ratingValues.put(GameDataContract.RatingEntry.COLUMN_SCORE, score);

        Uri insertedUri = mContext.getContentResolver().insert(
                GameEntry.CONTENT_URI,
                ratingValues);

        ratingId = ContentUris.parseId(insertedUri);

        return ratingId;
    }

    private long addGame(int gameApiId, String name, String deck, String releaseDate, String siteUrl) {
        long gameId;

        ContentValues gameValues = new ContentValues();
        gameValues.put(GameEntry.COLUMN_GAME_ID, gameApiId);
        gameValues.put(GameEntry.COLUMN_GAME_NAME, name);
        gameValues.put(GameEntry.COLUMN_DECK, deck);
        gameValues.put(GameEntry.COLUMN_RELEASE_DATE, releaseDate);
        gameValues.put(GameEntry.COLUMN_SITE_URL, siteUrl);

        Uri insertedUri = mContext.getContentResolver().insert(
                GameEntry.CONTENT_URI,
                gameValues);

        gameId = ContentUris.parseId(insertedUri);

        return gameId;
    }

    String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
        String[] resultStrs = new String[cvv.size()];
        for (int i = 0; i < cvv.size(); i++) {
            ContentValues gameValues = cvv.elementAt(i);
            resultStrs[i] = gameValues.getAsString(GameEntry.COLUMN_GAME_ID) +
                    " - " + gameValues.getAsString(GameEntry.COLUMN_GAME_NAME);
        }
        return resultStrs;
    }

    public static int hash(String s) {
        int h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = 31 * h + s.charAt(i);
        }
        return h;
    }

    /**
     * Take the string representing the game data in JSON format and
     * pull out the data we need to construct the strings needed for the wireframes.
     */
    private String[] getGameDataFromJson(String giantBombJsonStr) throws JSONException {
        // The JSON objects to be extracted
        final String GB_RESULTS = "results";
        final String GB_ID = "id";
        final String GB_NAME = "name";
        final String GB_DECK = "deck";
        final String GB_SITE_URL = "site_detail_url";
        final String GB_RELEASE_DATE = "original_release_date";

        try {
            JSONObject giantBombDataJson = new JSONObject(giantBombJsonStr);
            JSONArray gameArray = giantBombDataJson.getJSONArray(GB_RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(gameArray.length());

            for (int i = 0; i < gameArray.length(); i++) {
                int id;
                String name;
                String deck;
                String siteUrl;
                String releaseDate;

                JSONObject game = gameArray.getJSONObject(i);

                id = game.getInt(GB_ID);
                name = game.getString(GB_NAME);
                deck = game.getString(GB_DECK);
                siteUrl = game.getString(GB_SITE_URL);
                releaseDate = game.getString(GB_RELEASE_DATE);

                ContentValues gameValues = new ContentValues();

                gameValues.put(GameEntry.COLUMN_GAME_ID, id);
                gameValues.put(GameEntry.COLUMN_GAME_NAME, name);
                gameValues.put(GameEntry.COLUMN_DECK, deck);
                gameValues.put(GameEntry.COLUMN_RELEASE_DATE, releaseDate);
                gameValues.put(GameEntry.COLUMN_SITE_URL, siteUrl);

                cVVector.add(gameValues);
            }

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(GameEntry.CONTENT_URI, cvArray);
            }

            /*
            String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";
+            Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
+                    locationSetting, System.currentTimeMillis());
             */
            String sortOrder = GameEntry.COLUMN_GAME_NAME + " ASC";
            Uri gameDataUri = GameEntry.buildGameUri(System.currentTimeMillis());

            Cursor cur = mContext.getContentResolver().query(gameDataUri,
                    null, null, null, sortOrder);

            cVVector = new Vector<ContentValues>(cur.getCount());

            if ( cur.moveToFirst() ) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cVVector.add(cv);
                } while (cur.moveToNext());
            }

            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + cVVector.size() + " Inserted");

            String[] resultStrs = convertContentValuesToUXFormat(cVVector);
            return resultStrs;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String[] doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string
        String gameDataJsonStr = null;

        // profile key needed to access GiantBomb api
        String key = "beda8b0843a1ac3465493df4018ddf26041ab6c4";
        String format = "json";
        // give us the name and id of each game you return
        String field_list_name = "name";
        String field_list_id = "id";
        String field_list_deck = "deck";
        String field_list_url = "site_detail_url";
        String field_list_release_date = "original_release_date";
        String limit = "100";

        try {
            final String GIANTBOMB_BASE_URL = "http://www.giantbomb.com/api/games/?";
            final String KEY_PARAM = "api_key";
            final String FORMAT_PARAM = "format";
            final String FIELD_LIST_PARAM = "field_list";
            final String LIMIT_PARAM = "limit";
            final String OFFSET_PARAM = "offset";

            Uri builtUri = Uri.parse(GIANTBOMB_BASE_URL).buildUpon()
                    .appendQueryParameter(KEY_PARAM, key)
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(FIELD_LIST_PARAM, field_list_name + "," +
                            field_list_id + "," +
                            field_list_deck + "," +
                            field_list_url + "," +
                            field_list_release_date)
                    .appendQueryParameter(LIMIT_PARAM, limit)
                    .build();

            URL url = new URL(builtUri.toString());

            // Creating the request to GiantBomb and opening the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Reading the input stream into a string
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            gameDataJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // Code didn't get the game data
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getGameDataFromJson(gameDataJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        if (result != null) {
            mGiantBombAdapter.clear();
            for (String gameDataStr:result) {
                mGiantBombAdapter.add(gameDataStr);
            }
        }
    }
}