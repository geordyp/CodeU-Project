package com.codeu.android.codeuproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
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

import com.codeu.android.codeuproject.FetchGameDataTask;

/**
 * Created by geordywilliams on 8/3/15.
 * Fetching game data and displaying it as a layout.
 */
public class GameFragment extends Fragment {
    ArrayAdapter<String> mGameAdapter;
    List<String> list;
    //Context context;

    //public GameFragment(Context c) {
    //    context = c;
    //}
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
                String gameData = mGameAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), RecommendationActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, gameData);
                //Log.d("hey,listen", gameData);
                startActivity(intent);
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
