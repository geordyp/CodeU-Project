package com.codeu.android.codeuproject;

import android.database.Cursor;
import android.net.Uri;
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

            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

            DetailFragment frag = new DetailFragment();
            frag.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.game_detail_container, frag)
                    .commit();
        }
    }

    public static class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

        private static final int DETAIL_LOADER = 0;
        static final String DETAIL_URI = "URI";
        private Uri mUri;

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

            Bundle arguments = getArguments();
            if (arguments != null) {
                mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
            }

            return inflater.inflate(R.layout.fragment_detail, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            //Intent intent = getActivity().getIntent();
            //if (intent == null || intent.getData() == null) {
            //    return null;
            //}

            if (null != mUri) {

                return new CursorLoader(
                        getActivity(),
                        mUri,
                        GAME_DATA_COLUMNS,
                        null,
                        null,
                        null);
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (!data.moveToFirst()) {return;}

            TextView mNameView = (TextView) getView().findViewById(R.id.game_name_textview);
            mNameView.setText(data.getString(COL_IND_GAME_NAME));

            //ImageView mGameIconView = (ImageView) getView().findViewById(R.id.game_icon);

            TextView mDateView = (TextView) getView().findViewById(R.id.game_date_textview);
            String date = data.getString(COL_IND_RELEASE_DATE);
            String[] yearMonth = date.split("-");
            String[] dayTime = yearMonth[2].split(" ");
            date = yearMonth[1] + "/" + dayTime[0];
            mDateView.setText(date);

            TextView mGenreView = (TextView) getView().findViewById(R.id.game_genre_textview);
            mGenreView.setText(data.getString(COL_IND_GENRES));

            TextView mDeckView = (TextView) getView().findViewById(R.id.game_deck_textview);
            mDeckView.setText(data.getString(COL_IND_DECK));

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) { }
    }
}
