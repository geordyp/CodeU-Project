package com.codeu.android.codeuproject;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by geordywilliams on 8/3/15.
 * Fetching game data and displaying it as a layout.
 */
public class GameFragment extends Fragment {
    ArrayAdapter<String> mGameAdapter;
    Context context;

    public GameFragment(Context c) {
        context = c;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Allows fragment to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.gamefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            CharSequence text = "Nothing happened and nothing was supposed to happen.";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            //FetchGameDataTask gameDataTask = new FetchGameDataTask();
            //gameDataTask.execute();
            //return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Dummy data
        String[] data = {
                "Game 1",
                "Game 2",
                "Game 3",
                "Game 4",
                "Game 5",
                "Game 6"
        };
        List<String> gameList = new ArrayList<String>(Arrays.asList(data));

        mGameAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_game,
                R.id.list_item_game_textview,
                gameList);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_game);
        listView.setAdapter(mGameAdapter);

        // Get a reference to the Shuffle Button, and set up click listener.
        final Button button = (Button) rootView.findViewById(R.id.shuffle_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FetchGameDataTask gameDataTask = new FetchGameDataTask();
                gameDataTask.execute();
            }
        });

        return rootView;
    }

    public class FetchGameDataTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchGameDataTask.class.getSimpleName();

        /**
         * Take the string representing the game data in JSON format and
         * pull out the data we need to construct the strings needed for the wireframes.
         */
        private String[] getGameDataFromJson(String giantBombJsonStr) throws JSONException {
            // The JSON objects to be extracted
            final String GB_RESULTS = "results";
            final String GB_ID = "id";
            final String GB_NAME = "name";

            JSONObject giantBombDataJson = new JSONObject(giantBombJsonStr);
            JSONArray gameArray = giantBombDataJson.getJSONArray(GB_RESULTS);

            Random r = new Random();
            String[] resultStrs = new String[10];
            for (int i = 0; i < 10; i++) {
                String name;
                int id;

                JSONObject game = gameArray.getJSONObject(r.nextInt(100));

                name = game.getString(GB_NAME);
                id = game.getInt(GB_ID);

                resultStrs[i] = id + " - " + name;
            }

            for (String s:resultStrs) {
                Log.v(LOG_TAG, "Giant Bomb entry: " + s);
            }

            return resultStrs;
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
                        .appendQueryParameter(FIELD_LIST_PARAM, field_list_name + "," + field_list_id)
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

                Log.v(LOG_TAG, "Giant Bomb string: " + gameDataJsonStr);
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
                mGameAdapter.clear();
                for (String gameDataStr:result) {
                    mGameAdapter.add(gameDataStr);
                }
            }
        }
    }
}
