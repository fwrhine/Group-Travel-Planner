package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;

import java.lang.ref.WeakReference;
import java.util.List;

public class RVAdapter_Place extends RecyclerView.Adapter<RVAdapter_Place.PlaceViewHolder>{

    List<Place> places;
    private final ClickListener listener;

    public RVAdapter_Place(List<Place> places, ClickListener listener){
        this.places = places;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_place, viewGroup, false);
        PlaceViewHolder pvh = new PlaceViewHolder(v, listener);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder placeViewHolder, int i) {
        placeViewHolder.placeName.setText(places.get(i).getName());
        placeViewHolder.placeAddress.setText(places.get(i).getAddress());
        placeViewHolder.placeRating.setText(String.valueOf(places.get(i).getRating()));

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cardView;
        private TextView placeName;
        private TextView placeAddress;
        private TextView placeRating;
        private WeakReference<ClickListener> listenerRef;


        PlaceViewHolder(View itemView,  ClickListener listener) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cv);
            placeName = (TextView)itemView.findViewById(R.id.place_name);
            placeAddress = (TextView)itemView.findViewById(R.id.place_address);
            placeRating = (TextView)itemView.findViewById(R.id.place_rating);
            listenerRef = new WeakReference<>(listener);

            cardView.setOnClickListener(this);
        }

        // onClick Listener for view
        @Override
        public void onClick(View v) {
            listenerRef.get().onClick(v, getAdapterPosition());
        }

    }

    public interface ClickListener {
        void onClick(View v, int position);
    }
}

