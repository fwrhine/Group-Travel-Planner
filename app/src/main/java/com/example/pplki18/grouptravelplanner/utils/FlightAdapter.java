package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;

import java.util.ArrayList;

public class FlightAdapter extends ArrayAdapter<Flight> {
    public FlightAdapter(Context context, ArrayList<Flight> arrayList) {
        super(context, 0, arrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Flight user = (Flight) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_book_flight, parent, false);
        }
        // Lookup view for data population
        TextView tvAirlineName = (TextView) convertView.findViewById(R.id.airlineName);
        TextView tvFlightNum = (TextView) convertView.findViewById(R.id.flightNumber);
        TextView tvDepartCity = (TextView) convertView.findViewById(R.id.departCity);
        TextView tvArriveCity = (TextView) convertView.findViewById(R.id.arriveCity);
        TextView tvDepartTime = (TextView) convertView.findViewById(R.id.departTime);
        TextView tvArriveTime = (TextView) convertView.findViewById(R.id.arriveTime);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.price);

        // Populate the data into the template view using the data object
        tvAirlineName.setText(user.airlineName);
        tvFlightNum.setText(user.flightNumber);
        tvDepartCity.setText(user.departureCity);
        tvArriveCity.setText(user.arrivalCity);
        tvDepartTime.setText(user.departureTime);
        tvArriveCity.setText(user.arrivalCity);
        tvArriveTime.setText(user.arrivalTime);
        tvPrice.setText(user.price);

        // Return the completed view to render on screen
        return convertView;
    }

}
