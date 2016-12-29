package com.codeu.android.codeuproject;

/**
 * Created by geordywilliams on 8/10/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

public class FetchGameDataTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = "hey,listen";

    private final Context mContext;

    public FetchGameDataTask(Context context) {
        mContext = context;
    }

    /**
     * Take the string representing the game data in JSON format and
     * pull out the data we need to construct the strings needed for the wireframes.
     */
    private void getGameDataFromJson(List<JSONObject> giantBombJsonList) throws JSONException {

        // The JSON objects to be extracted from list
        final String GB_RESULTS = "results";
        final String GB_ID = "id";
        final String GB_NAME = "name";
        final String GB_DECK = "deck";
        final String GB_RELEASE_DATE = "original_release_date";
        final String GB_IMAGE = "image";
        final String GB_PLATFORMS = "platforms";
        final String GB_ICON_URL = "icon_url";

        // The JSON objects to be extracted from item
        final String GB_GENRES = "genres";
        final String GB_DEVELOPERS = "developers";
        final String GB_PUBLISHERS = "publishers";
        final String GB_SIMILAR_GAMES = "similar_games";

        try {

            // for each of the JSONs in list of GiantBomb JSONs
            for (JSONObject giantBombJson : giantBombJsonList) {

                JSONArray gameArray = giantBombJson.getJSONArray(GB_RESULTS);
                Vector<ContentValues> cVVector = new Vector<>(gameArray.length());

                int game_id;
                String game_name;
                String game_image;
                String game_deck;
                String game_releaseDate;
                String game_platformList;
                for (int i = 0; i < gameArray.length(); i++) {

                    JSONObject game = gameArray.getJSONObject(i);

                    game_id = game.getInt(GB_ID);
                    game_name = game.getString(GB_NAME);
                    game_deck = game.getString(GB_DECK);
                    game_releaseDate = game.getString(GB_RELEASE_DATE);

                    JSONArray platforms = game.getJSONArray(GB_PLATFORMS);
                    game_platformList = getJsonArrayItems(platforms);

                    // may be null
                    JSONObject images;
                    try {
                        images = game.getJSONObject(GB_IMAGE);
                        game_image = images.getString(GB_ICON_URL);
                    } catch(JSONException e) {
                        game_image = null;
                    }

                    /*
                        from here we make item queries to get
                        further information on a single game
                     */

                    // profile key needed to access GiantBomb api
                    String key = "beda8b0843a1ac3465493df4018ddf26041ab6c4";
                    String format = "json";
                    // field_list narrows down the details returned
                    String field_list_genres = "genres";
                    String field_list_developers = "developers";
                    String field_list_publishers = "publishers";
                    String field_list_similar_games = "similar_games";

                    final String GIANTBOMB_GAME_BASE_URL = "http://www.giantbomb.com/api/game/" + game_id + "/?";
                    final String KEY_PARAM = "api_key";
                    final String FORMAT_PARAM = "format";
                    final String FIELD_LIST_PARAM = "field_list";

                    Uri builtUri = Uri.parse(GIANTBOMB_GAME_BASE_URL).buildUpon()
                            .appendQueryParameter(KEY_PARAM, key)
                            .appendQueryParameter(FORMAT_PARAM, format)
                            .appendQueryParameter(FIELD_LIST_PARAM, field_list_genres + "," +
                                    field_list_developers + "," +
                                    field_list_publishers + "," +
                                    field_list_similar_games)
                            .build();

                    URL url = new URL(builtUri.toString());

                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;

                    // Creating the request to GiantBomb and opening the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Reading the input stream into a string
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) return;
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) buffer.append(line + "\n");

                    String game_genresList = null;
                    String game_developersList = null;
                    String game_publishersList = null;
                    String game_similarGamesList = null;
                    if (buffer.length() > 0) {
                        String gameDataJsonStr = buffer.toString();

                        JSONObject giantBombDataJson = new JSONObject(gameDataJsonStr);
                        JSONObject gameInfo = giantBombDataJson.getJSONObject(GB_RESULTS);

                        // all of these may be null
                        JSONArray genresArray;
                        try {
                            genresArray = gameInfo.getJSONArray(GB_GENRES);
                            game_genresList = getJsonArrayItems(genresArray);
                        } catch(JSONException e) {
                            game_genresList = null;
                        }
                        JSONArray developersArray;
                        try {
                            developersArray = gameInfo.getJSONArray(GB_DEVELOPERS);
                            game_developersList = getJsonArrayItems(developersArray);
                        } catch(JSONException e) {
                            game_developersList = null;
                        }
                        JSONArray publisherArray;
                        try {
                            publisherArray = gameInfo.getJSONArray(GB_PUBLISHERS);
                            game_publishersList = getJsonArrayItems(publisherArray);
                        } catch(JSONException e) {
                            game_publishersList = null;
                        }
                        JSONArray similarGamesArray;
                        try {
                            similarGamesArray = gameInfo.getJSONArray(GB_SIMILAR_GAMES);
                            game_similarGamesList = getJsonArrayItems(similarGamesArray);
                        } catch(JSONException e) {
                            game_similarGamesList = null;
                        }
                    }

                    ContentValues gameValues = new ContentValues();
                    gameValues.put(GameEntry.COLUMN_GAME_ID, game_id);
                    gameValues.put(GameEntry.COLUMN_GAME_NAME, game_name);
                    gameValues.put(GameEntry.COLUMN_DECK, game_deck);
                    gameValues.put(GameEntry.COLUMN_RELEASE_DATE, game_releaseDate);
                    gameValues.put(GameEntry.COLUMN_PLATFORMS, game_platformList.toString());
                    gameValues.put(GameEntry.COLUMN_IMAGE, game_image);
                    gameValues.put(GameEntry.COLUMN_PUBLISHERS, game_publishersList);
                    gameValues.put(GameEntry.COLUMN_GENRES, game_genresList);
                    gameValues.put(GameEntry.COLUMN_DEVELOPERS, game_developersList);
                    gameValues.put(GameEntry.COLUMN_SIMILAR_GAMES, game_similarGamesList);

                    cVVector.add(gameValues);
                }

                int inserted = 0;
                // add to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(GameEntry.CONTENT_URI, cvArray);
                }

                Log.d(LOG_TAG, "FetchGameDataTask Complete. " + inserted + " Inserted");

            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    // takes a JSONArray and breaks it into a comma delimited StringBuilder
    public String getJsonArrayItems(JSONArray arr) {
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < arr.length(); i++) {
                sb.append(arr.getJSONObject(i).getString("name") + ", ");
            }
            sb.delete(sb.length()-2, sb.length());

            return sb.toString();
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Void doInBackground(String... params) {

        // ArrayList of all the JSONs we'll receive
        List<JSONObject> gameDataJsonList = new ArrayList<>();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // profile key needed to access GiantBomb api
        String key = "beda8b0843a1ac3465493df4018ddf26041ab6c4";
        String format = "json";

        // field_list narrows down the details returned
        String field_list_id = "id";
        String field_list_name = "name";
        String field_list_deck = "deck";
        String field_list_image = "image";
        String field_list_platforms = "platforms";
        String field_list_release_date = "original_release_date";
        String field_list_total_results = "number_of_total_results";

        final String GIANTBOMB_GAMES_BASE_URL = "http://www.giantbomb.com/api/games/?";
        final String KEY_PARAM = "api_key";
        final String FORMAT_PARAM = "format";
        final String FIELD_LIST_PARAM = "field_list";
        final String FIELD_FILTER_PARAM = "filter";
        final String PLATFORM_FILTER_PS4 = "146";
        final String PLATFORM_FILTER_XB1 = "145";
        final String PLATFORM_FILTER_WIIU = "139";

        int dayCount = 0;   // keep track of the days we're querying for
        try {

            // we only want the last 14 days worth of released games
            while (dayCount < 15) {

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                cal.add(Calendar.DATE, -dayCount);  // takes the calendar back by whatever the count is
                String day = dateFormat.format(cal.getTime());

                Uri builtUri = Uri.parse(GIANTBOMB_GAMES_BASE_URL).buildUpon()
                        .appendQueryParameter(KEY_PARAM, key)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(FIELD_LIST_PARAM, field_list_id + "," +
                                field_list_name + "," +
                                field_list_deck + "," +
                                field_list_image + "," +
                                field_list_platforms + "," +
                                field_list_release_date)
                        .appendQueryParameter(FIELD_FILTER_PARAM,
                                field_list_release_date + ":" + day + " 00:00:00," +
                                        field_list_platforms + ":" +
                                        PLATFORM_FILTER_PS4 + "," +
                                        PLATFORM_FILTER_XB1 + "," +
                                        PLATFORM_FILTER_WIIU)
                        .build();

                URL url = new URL(builtUri.toString());

                // Creating the request to GiantBomb and opening the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Reading the input stream into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) return null;
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) buffer.append(line + "\n");

                if (buffer.length() == 0) return null;
                String jsonStr = buffer.toString();

                JSONObject giantBombData = new JSONObject(jsonStr);
                String gamesFound = giantBombData.getString(field_list_total_results);
                // no need passing empty JSONs
                if (Integer.parseInt(gamesFound) > 0) {
                    gameDataJsonList.add(giantBombData);
                }

                dayCount++;
            }
            getGameDataFromJson(gameDataJsonList);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
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
        
        return null;

    }
}