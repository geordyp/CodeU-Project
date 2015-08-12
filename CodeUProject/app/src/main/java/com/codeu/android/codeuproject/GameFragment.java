package com.codeu.android.codeuproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.codeu.android.codeuproject.data.GameDataContract;

import java.util.ArrayList;

/**
 * Created by geordywilliams on 8/3/15.
 * Fetching game data and displaying it as a layout.
 */
public class GameFragment extends Fragment {
    ArrayAdapter<String> mGameAdapter;
    private int mPosition = ListView.INVALID_POSITION;
    //Context context;

    //public GameFragment(Context c) {
    //    context = c;
    //}

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public GameFragment() {}

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
            //CharSequence text = "Nothing happened and nothing was supposed to happen.";
            //int duration = Toast.LENGTH_LONG;
            //Toast toast = Toast.makeText(context, text, duration);
            //toast.show();

            //FetchGameDataTask gameDataTask = new FetchGameDataTask();
            //gameDataTask.execute();
            //return true;
            
            updateGameData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mGameAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_game,
                R.id.list_item_game_textview,
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_game);
        listView.setAdapter(mGameAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                /**Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    ((Callback) getActivity())
                            .onItemSelected(GameDataContract.GameEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                            ));
                }
                mPosition = position; */

                // OLD WAY, AKA NOT USING A CURSOR
                String gameData = mGameAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class) // HERE!
                        .putExtra(Intent.EXTRA_TEXT, gameData);
                startActivity(intent);
            }
        });

        // Get a reference to the Shuffle Button, and set up click listener.
        final Button button = (Button) rootView.findViewById(R.id.shuffle_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateGameData();
            }
        });

        return rootView;
    }
    
    private void updateGameData() {
        FetchGameDataTask gameDataTask = new FetchGameDataTask(getActivity(), mGameAdapter);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        gameDataTask.execute();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        updateGameData();
    }
}
