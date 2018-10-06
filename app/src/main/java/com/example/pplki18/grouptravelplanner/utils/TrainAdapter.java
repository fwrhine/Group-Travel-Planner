package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;

import java.util.ArrayList;

public class TrainAdapter extends ArrayAdapter<Train> {
    public TrainAdapter(Context context, ArrayList<Train> arrayList) {
        super(context, 0, arrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Train user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_book_train, parent, false);
        }
        // Lookup view for data population
        TextView tvTrainName = (TextView) convertView.findViewById(R.id.trainName);
        TextView tvDepartCity = (TextView) convertView.findViewById(R.id.departCity);
        TextView tvArriveCity = (TextView) convertView.findViewById(R.id.arriveCity);
        TextView tvDepartTime = (TextView) convertView.findViewById(R.id.departTime);
        TextView tvArriveTime = (TextView) convertView.findViewById(R.id.arriveTime);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.price);

        // Populate the data into the template view using the data object
        tvTrainName.setText(user.trainName);
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
