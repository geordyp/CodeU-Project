package com.codeu.android.codeuproject;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.codeu.android.codeuproject.data.GameDataContract.GameEntry;

/**
 * Created by geordywilliams on 8/3/15.
 * Fetching game data and displaying it as a layout.
 */
public class GameFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int GAME_DATA_LOADER = 0;

    private static final String[] GAME_DATA_COLUMNS = {
            GameEntry.TABLE_NAME + "." + GameEntry._ID,
            GameEntry.COLUMN_GAME_ID,
            GameEntry.COLUMN_GAME_NAME,
            GameEntry.COLUMN_DECK,
            GameEntry.COLUMN_RELEASE_DATE,
            GameEntry.COLUMN_PLATFORMS,
            GameEntry.COLUMN_IMAGE,
            GameEntry.COLUMN_GENRES,
            GameEntry.COLUMN_DEVELOPERS,
            GameEntry.COLUMN_PUBLISHERS,
            GameEntry.COLUMN_SIMILAR_GAMES
    };

    static final int COL_IND_GAME_DATA_ID = 0;
    static final int COL_IND_GAME_ID = 1;
    static final int COL_IND_GAME_NAME = 2;
    static final int COL_IND_DECK = 3;
    static final int COL_IND_RELEASE_DATE = 4;
    static final int COL_IND_PLATFORMS = 5;
    static final int COL_IND_IMAGE = 6;
    static final int COL_IND_GENRES = 7;
    static final int COL_IND_DEVELOPERS = 8;
    static final int COL_IND_PUBLISHERS = 9;
    static final int COL_IND_SIMILAR_GAMES = 10;

    GiantBombAdapter mGiantBombAdapter;

    public GameFragment() {}

    public interface Callback {

        public void onItemSelected(Uri info);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.gamefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateGameData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mGiantBombAdapter = new GiantBombAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_game);
        listView.setAdapter(mGiantBombAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null) {

                    ((Callback) getActivity()).
                            onItemSelected(GameEntry.buildGameWithID(cursor.getString(COL_IND_GAME_ID)));

                    //Intent intent = new Intent(getActivity(), DetailActivity.class)
                      //      .setData(GameEntry.buildGameWithID(cursor.getString(COL_IND_GAME_ID)));
                    //startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(GAME_DATA_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateGameData() {

        CharSequence text = "Fetching game releases from the last 2 weeks.";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getActivity(), text, duration);
        toast.show();

        FetchGameDataTask gameDataTask = new FetchGameDataTask(getActivity());
        gameDataTask.execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri gameUri = GameEntry.buildGameUri();

        return new CursorLoader(getActivity(),
                gameUri,
                GAME_DATA_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mGiantBombAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mGiantBombAdapter.swapCursor(null);
    }
}
