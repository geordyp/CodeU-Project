package com.codeu.android.codeuproject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeu.android.codeuproject.data.GameDataContract.GameEntry;

/**
 * Created by geordywilliams on 8/4/15.
 */
public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    public static class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

        private static final int DETAIL_LOADER = 0;

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

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_detail, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }

            return new CursorLoader(
                    getActivity(),
                    intent.getData(),
                    GAME_DATA_COLUMNS,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (!data.moveToFirst()) {return;}

            TextView detailTextView = (TextView) getView().findViewById(R.id.detail_text);
            detailTextView.setText(data.getString(COL_IND_GAME_NAME));
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) { }
    }
}
