package com.example.pplki18.grouptravelplanner.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.EventDetailActivity;
import com.example.pplki18.grouptravelplanner.PlaceActivity;
import com.example.pplki18.grouptravelplanner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RVAdapter_Suggest extends RecyclerView.Adapter<RVAdapter_Suggest.SuggestionViewHolder> {

    private List<Suggestion> suggestions;
    private Context context;
    private LayoutInflater mLayoutInflater;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;

    public RVAdapter_Suggest(List<Suggestion> suggestions, Context context) {
        this.suggestions = suggestions;
        this.context = context;

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        mLayoutInflater = LayoutInflater.from(context);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_suggestion, viewGroup, false);
        return new SuggestionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SuggestionViewHolder holder, int position) {
        Suggestion suggestion = suggestions.get(position);

        holder.eventName.setText(suggestion.getTitle());
        holder.eventDesc.setVisibility(View.GONE);

        if (suggestion.getDescription() != null) {
            holder.eventDesc.setVisibility(View.VISIBLE);
            String desc = suggestion.getDescription();
            if (desc.length() > 45) desc = desc.substring(0,42) + "...";
            holder.eventDesc.setText(desc);
        }

        setEventDetailOnClick(holder, position);
        //setCardViewLongClick(holder, position, suggestion.getTitle());
    }

    public void setEventDetailOnClick(final SuggestionViewHolder holder, final int position) {
        holder.eventDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Suggestion aSuggest = suggestions.get(position);
                String type = aSuggest.getType();
                if (type.equals("restaurants") || type.equals("attractions")) {
                    setEventDetailOne(suggestions, position);
                } else {
                    setEventDetailTwo(suggestions, position);
                }
            }
        });
    }

    public void setEventDetailOne(List<Suggestion> events, int pos) {
        Intent myIntent = new Intent(context, PlaceActivity.class);
        Suggestion anEvent = events.get(pos);

        myIntent.putExtra("PLACE_ID", anEvent.getQuery_id());
        myIntent.putExtra("event_id", anEvent.getEvent_id());
        myIntent.putExtra("type", anEvent.getType());

        if (anEvent.getDescription() != null) {
            myIntent.putExtra("description", anEvent.getDescription());
        } else {
            myIntent.putExtra("description", "");
        }

        ((Activity) context).startActivityForResult(myIntent, 5);

    }

    public void setEventDetailTwo(List<Suggestion> events, int pos) {
        Intent myIntent = new Intent(context, EventDetailActivity.class);
        Suggestion anEvent = events.get(pos);

        myIntent.putExtra("event", anEvent);

        ((Activity) context).startActivityForResult(myIntent, 5);
    }
/*
    public void setCardViewLongClick(final SuggestionViewHolder holder, final int position,
                                     final String title) {
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.cardView);
                //inflating menu from xml resource
                popup.inflate(R.menu.event_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        AlertDialog box;
                        switch (item.getItemId()) {
                            case R.id.delete_event:
                                box = deleteConfirmation(position, title);
                                box.show();
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
                return true;
            }
        });
    }

    private AlertDialog deleteConfirmation(final int position, String title) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete " + title + "?")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deleteEvent(suggestions.get(position));
                        suggestions.remove(position);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    private void deleteEvent(Suggestion event) {

        if (event.getEvent_id() != null){
            deleteHelper(event, new DeleteEventCallback() {
                @Override
                public void onCallback() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void deleteHelper(Suggestion event, final DeleteEventCallback callback){
        //TODO: CHECK
        //String plan_id = event.getPlan_id();
        String groupId = event.getGroup_id();
        final String event_id = event.getEvent_id();
        final DatabaseReference planRef = firebaseDatabase.getReference().child("groups").child(groupId).child("events").child(event_id);
        planRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                planRef.removeValue();
                final DatabaseReference eventRef = firebaseDatabase.getReference().child("events").child(event_id);
                eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        eventRef.removeValue();
                        callback.onCallback();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
*/
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView eventName;
        TextView eventDesc;
        TextView eventDetail;   // TEXT BUTTON

        SuggestionViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cv_event);
            eventName = itemView.findViewById(R.id.item_title);
            eventDesc = itemView.findViewById(R.id.item_desc_detail);
            eventDetail = itemView.findViewById(R.id.item_detail);
        }
    }

    private interface DeleteEventCallback {
        void onCallback();
    }
}

