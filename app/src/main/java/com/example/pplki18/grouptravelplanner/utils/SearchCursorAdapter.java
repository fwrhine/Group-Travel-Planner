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

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.search_row, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvUsername = (TextView) view.findViewById(R.id.username);
        TextView tvFullname = (TextView) view.findViewById(R.id.fullname);
        // Extract properties from cursor
        String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
        String fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
        // Populate fields with extracted properties
        tvUsername.setText(String.valueOf(username));
        tvFullname.setText(String.valueOf(fullname));
    }
}
