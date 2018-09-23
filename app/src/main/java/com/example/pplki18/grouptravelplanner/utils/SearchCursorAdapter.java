package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;

public class SearchCursorAdapter extends CursorAdapter {
    public SearchCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.search_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvUsername = (TextView) view.findViewById(R.id.username);
        TextView tvFullname = (TextView) view.findViewById(R.id.fullname);
        //ImageView tvDisplayPicture = (ImageView) view.findViewById(R.id.image);

        String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
        String fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
        //String displayPicture = cursor.getString(cursor.getColumnIndexOrThrow("display_picture"));

        tvUsername.setText("@" + String.valueOf(username));
        tvFullname.setText(String.valueOf(fullname));
        //tvDisplayPicture.setImageIcon(Icon.createWithContentUri(displayPicture));
    }
}
