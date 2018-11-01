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

public class RVAdapter_Hotel extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Hotel> hotels;
    private ClickListener listener;
    private Context context;
    private boolean isLoadingAdded = false;

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    public RVAdapter_Hotel(Context context){
        this.context = context;
        hotels = new ArrayList<>();
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
                View view = inflater.inflate(R.layout.row_hotel, viewGroup, false);
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
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {

        switch (getItemViewType(i)) {
            case ITEM:
                PlaceViewHolder placeViewHolder = (PlaceViewHolder) viewHolder;
                placeViewHolder.hotelName.setText(hotels.get(i).getName());
                placeViewHolder.hotelAddress.setText(hotels.get(i).getRegion());
                placeViewHolder.hotelRating.setText(String.valueOf(hotels.get(i).getRating()) + "/5");
                placeViewHolder.addIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.addImageOnClick(v, viewHolder.getAdapterPosition());
                    }
                });

                break;
            case LOADING:
//                Do nothing
                break;
        }
    }

    @Override
    public int getItemCount() {
        return hotels == null ? 0 : hotels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == hotels.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

//    @Override
//    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    private void add(Hotel hotel) {
        hotels.add(hotel);
        notifyItemInserted(hotels.size() - 1);
    }

    public void addAll(List<Hotel> hotels) {
        for (Hotel hotel : hotels) {
            add(hotel);
        }
    }

    public List<Hotel> getAll() {
        return hotels;
    }

    private void remove(Hotel hotel) {
        int position = hotels.indexOf(hotel);
        if (position > -1) {
            hotels.remove(position);
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
        hotels.add(new Hotel());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = hotels.size() - 1;
        Hotel item = getItem(position);

        if (item != null) {
            hotels.remove(position);
            notifyItemRemoved(position);
        }
    }

    private Hotel getItem(int position) {
        return hotels.get(position);
    }

//    private void getPhoto(final PlaceViewHolder placeViewHolder, String photo_reference) {
//        // Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(context);
//        String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photoreference="
//                + photo_reference + "&key=" + context.getString(R.string.api_key);
//
//
//        // Request an image response from the provided URL.
//        ImageRequest imageRequest = new ImageRequest(url,
//                new Response.Listener<Bitmap>() {
//                    @Override
//                    public void onResponse(Bitmap response) {
//                        placeViewHolder.placeImage.setImageBitmap(response);
//                    }
//                },  0, 0,  ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
//                new Response.ErrorListener() {
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("PHOTO REQUEST", error.toString());
//                    }
//                });
//
//        // Add the request to the RequestQueue.
//        queue.add(imageRequest);
//    }


    /*
   View Holders
   _________________________________________________________________________________________________
    */

    public static class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cardView;
        private TextView hotelName;
        private TextView hotelAddress;
        private TextView hotelRating;
        private ImageView hotelImage;
        private ImageView addIcon;
        private WeakReference<ClickListener> listenerRef;


        PlaceViewHolder(View itemView,  ClickListener listener) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cv);
            hotelName = (TextView)itemView.findViewById(R.id.hotel_name);
            hotelAddress = (TextView)itemView.findViewById(R.id.hotel_address);
            hotelRating = (TextView)itemView.findViewById(R.id.hotel_rating);
            hotelImage = (ImageView)itemView.findViewById(R.id.place_image);
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
