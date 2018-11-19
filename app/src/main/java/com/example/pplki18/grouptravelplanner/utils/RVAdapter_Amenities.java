package com.example.pplki18.grouptravelplanner.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.User;

import java.lang.ref.WeakReference;
import java.util.List;

public class RVAdapter_Amenities extends RecyclerView.Adapter<RVAdapter_Amenities.AmenityViewHolder>{

    private final List<String> amenities;

    public RVAdapter_Amenities(List<String> amenities){
        this.amenities = amenities;
    }

    @Override
    public int getItemCount() {
        return amenities.size();
    }

    @Override
    public AmenityViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_amenities, viewGroup, false);
        return new AmenityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AmenityViewHolder amenityViewHolder, int i) {
        amenityViewHolder.amenity.setText(amenities.get(i));
    }

    public static class AmenityViewHolder extends RecyclerView.ViewHolder {
        private final TextView amenity;

        AmenityViewHolder(View itemView) {
            super(itemView);
            amenity = (TextView)itemView.findViewById(R.id.amenity);
        }
    }
}

