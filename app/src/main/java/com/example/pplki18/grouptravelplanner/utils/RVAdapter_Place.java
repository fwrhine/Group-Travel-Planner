package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.pplki18.grouptravelplanner.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RVAdapter_Place extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Place> places;
    private ClickListener listener;
    private Context context;
    private boolean isLoadingAdded = false;

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    public RVAdapter_Place(Context context){
        this.context = context;
        places = new ArrayList<>();
    }

    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (i) {
            case ITEM:
                View view = inflater.inflate(R.layout.row_place, viewGroup, false);
                viewHolder = new PlaceViewHolder(view, listener);
                break;
            case LOADING:
                Log.d("DISPLAY", "LOADING");
                view = inflater.inflate(R.layout.row_progress, viewGroup, false);
                viewHolder = new LoadingViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {

        switch (getItemViewType(i)) {
            case ITEM:
                PlaceViewHolder placeViewHolder = (PlaceViewHolder) viewHolder;
                placeViewHolder.placeName.setText(places.get(i).getName());
                placeViewHolder.placeAddress.setText(places.get(i).getAddress());
                placeViewHolder.placeRating.setText(String.valueOf(places.get(i).getRating()) + "/5");
                placeViewHolder.addIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.addImageOnClick(v, i);
                    }
                });

                if (places.get(i).getPhoto() != null) {
                    getPhoto(placeViewHolder, places.get(i).getPhoto());
                }
                break;
            case LOADING:
//                Do nothing
                break;
        }
    }

    @Override
    public int getItemCount() {
        return places == null ? 0 : places.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == places.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

//    @Override
//    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    private void add(Place place) {
        places.add(place);
        notifyItemInserted(places.size() - 1);
    }

    public void addAll(List<Place> places) {
        for (Place place : places) {
            add(place);
        }
    }

    private void remove(Place city) {
        int position = places.indexOf(city);
        if (position > -1) {
            places.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        places.add(new Place());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = places.size() - 1;
        Place item = getItem(position);

        if (item != null) {
            places.remove(position);
            notifyItemRemoved(position);
        }
    }

    private Place getItem(int position) {
        return places.get(position);
    }

    private void getPhoto(final PlaceViewHolder placeViewHolder, String photo_reference) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photoreference="
                + photo_reference + "&key=" + context.getString(R.string.api_key);


        // Request an image response from the provided URL.
        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        placeViewHolder.placeImage.setImageBitmap(response);
                    }
                },  0, 0,  ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.e("PHOTO REQUEST", error.toString());
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(imageRequest);
    }


    /*
   View Holders
   _________________________________________________________________________________________________
    */

    public static class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cardView;
        private TextView placeName;
        private TextView placeAddress;
        private TextView placeRating;
        private ImageView placeImage;
        private ImageView addIcon;
        private WeakReference<ClickListener> listenerRef;


        PlaceViewHolder(View itemView,  ClickListener listener) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cv);
            placeName = (TextView)itemView.findViewById(R.id.place_name);
            placeAddress = (TextView)itemView.findViewById(R.id.place_address);
            placeRating = (TextView)itemView.findViewById(R.id.place_rating);
            placeImage = (ImageView)itemView.findViewById(R.id.place_image);
            addIcon = (ImageView)itemView.findViewById(R.id.ic_add);

            listenerRef = new WeakReference<>(listener);

            cardView.setOnClickListener(this);
        }

        // onClick Listener for view
        @Override
        public void onClick(View v) {
            listenerRef.get().cardViewOnClick(v, getAdapterPosition());
        }

    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {

        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface ClickListener {
        void cardViewOnClick(View v, int position);
        void addImageOnClick(View v, int position);
    }
}

