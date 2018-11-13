package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.Train;

import java.util.ArrayList;

public class TrainAdapter extends ArrayAdapter<Train> {
    public TrainAdapter(Context context, ArrayList<Train> arrayList) {
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Train train = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_book_train, parent, false);
        }
        // Lookup view for data population
        TextView tvTrainName = convertView.findViewById(R.id.trainName);
        TextView tvDepartCity = convertView.findViewById(R.id.departCity);
        TextView tvArriveCity = convertView.findViewById(R.id.arriveCity);
        TextView tvDepartTime = convertView.findViewById(R.id.departTime);
        TextView tvArriveTime = convertView.findViewById(R.id.arriveTime);
        TextView tvPrice = convertView.findViewById(R.id.price);

        // Populate the data into the template view using the data object
        assert train != null;
        tvTrainName.setText(train.getTrainName());
        tvDepartCity.setText(train.getDepartureCity());
        tvArriveCity.setText(train.getArrivalCity());
        tvDepartTime.setText(train.getDepartureTime());
        tvArriveTime.setText(train.getArrivalTime());
        tvPrice.setText(train.getPrice());

        // Return the completed view to render on screen
        return convertView;
    }

}
