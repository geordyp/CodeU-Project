package com.codeu.android.codeuproject;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by geordywilliams on 8/15/15.
 */
public class GiantBombAdapter extends CursorAdapter {

    public GiantBombAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {

        String date = cursor.getString(GameFragment.COL_IND_RELEASE_DATE);

        String[] yearMonth = date.split("-");
        String[] dayTime = yearMonth[2].split(" ");

        date = yearMonth[1] + "/" + dayTime[0];

        return cursor.getString(GameFragment.COL_IND_GAME_NAME) + " - " + date + "\n" +
                cursor.getString(GameFragment.COL_IND_PLATFORMS);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_game, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view;
        tv.setText(convertCursorRowToUXFormat(cursor));
    }
}
