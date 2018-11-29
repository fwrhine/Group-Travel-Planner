package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.Flight;

import java.util.ArrayList;

public class FlightAdapter extends ArrayAdapter<Flight> {
    public FlightAdapter(Context context, ArrayList<Flight> arrayList) {
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Flight flight = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_book_flight, parent, false);
        }
        // Lookup view for data population
        TextView tvAirlineName = convertView.findViewById(R.id.airlineName);
        TextView tvFlightNum = convertView.findViewById(R.id.flightNumber);
        TextView tvDepartCity = convertView.findViewById(R.id.departCity);
        TextView tvArriveCity = convertView.findViewById(R.id.arriveCity);
        TextView tvDepartTime = convertView.findViewById(R.id.departTime);
        TextView tvArriveTime = convertView.findViewById(R.id.arriveTime);
        TextView tvPrice = convertView.findViewById(R.id.price);

        // Populate the data into the template view using the data object
        assert flight != null;
        tvAirlineName.setText(flight.getAirlineName() + " ");
        tvFlightNum.setText(" " + flight.getFlightNumber());
        tvDepartCity.setText(flight.getDepartureCity());
        tvArriveCity.setText(flight.getArrivalCity());
        tvDepartTime.setText(flight.getDepartureTime());
        tvArriveTime.setText(flight.getArrivalTime());
        tvPrice.setText(flight.getPrice());

        // Return the completed view to render on screen
        return convertView;
    }

}
