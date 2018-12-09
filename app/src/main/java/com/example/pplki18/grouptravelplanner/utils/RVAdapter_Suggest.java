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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.EventDetailActivity;
import com.example.pplki18.grouptravelplanner.PlaceActivity;
import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.pplki18.grouptravelplanner.data.Group;

public class RVAdapter_Suggest extends RecyclerView.Adapter<RVAdapter_Suggest.SuggestionViewHolder> {

    private List<Suggestion> suggestions;
    private Context context;

    private DatabaseReference planRef;
    private List<String> eventIDs = new ArrayList<>();

    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private Group group;

    public RVAdapter_Suggest(List<Suggestion> suggestions, Group group, Context context) {
        this.suggestions = suggestions;
        this.group = group;
        this.context = context;

        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_suggestion, viewGroup, false);
        return new SuggestionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        Suggestion suggestion = suggestions.get(position);

        holder.suggestName.setText(suggestion.getTitle());
        holder.suggestDesc.setVisibility(View.GONE);

        if (suggestion.getDescription() != null) {
            holder.suggestDesc.setVisibility(View.VISIBLE);
            String desc = suggestion.getDescription();
            if (desc.length() > 45) desc = desc.substring(0,42) + "...";
            holder.suggestDesc.setText(desc);
        }

        setSuggestDetailOnClick(holder, position);
        setCardViewLongClick(holder, position, suggestion.getTitle());
    }

    private void setSuggestDetailOnClick(final SuggestionViewHolder holder, final int position) {
        holder.suggestDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Suggestion aSuggest = suggestions.get(position);
                String type = aSuggest.getType();
                if (type.equals("restaurants") || type.equals("attractions")) {
                    setSuggestDetailOne(suggestions, position);
                } else {
                    setSuggestDetailTwo(suggestions, position);
                }
            }
        });
    }

    private void setSuggestDetailOne(List<Suggestion> suggestions, int pos) {
        Intent myIntent = new Intent(context, PlaceActivity.class);
        Suggestion aSuggest = suggestions.get(pos);

        myIntent.putExtra("PLACE_ID", aSuggest.getQuery_id());
        myIntent.putExtra("event_id", aSuggest.getSuggestion_id());
        myIntent.putExtra("type", aSuggest.getType());

        if (aSuggest.getDescription() != null) {
            myIntent.putExtra("description", aSuggest.getDescription());
        } else {
            myIntent.putExtra("description", "");
        }

        ((Activity) context).startActivityForResult(myIntent, 5);
    }

    private void setSuggestDetailTwo(List<Suggestion> suggestions, int pos) {
        Intent myIntent = new Intent(context, EventDetailActivity.class);
        Suggestion aSuggest = suggestions.get(pos);

        myIntent.putExtra("prev_fragment", "Fragment_SuggestionList");
        myIntent.putExtra("event", aSuggest);

        ((Activity) context).startActivityForResult(myIntent, 5);
    }

    private void setCardViewLongClick(final SuggestionViewHolder holder, final int position, final String title) {
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.cardView);

                String userId = firebaseUser.getUid();
                String groupLeaderId = group.getCreator_id();

                //inflating menu from xml resource
                if (userId.equals(groupLeaderId)) {
                    popup.inflate(R.menu.event_menu_leader);
                }

                else {
                    popup.inflate(R.menu.event_menu);
                }

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
                            case R.id.add_suggest:
                                box = addConfirmation(position, title);
                                box.show();
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
        return new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete " + title + "?")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deleteSuggest(suggestions.get(position));
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
    }

    private void deleteSuggest(Suggestion suggestion) {
        if (suggestion.getSuggestion_id() != null){
            deleteHelper(suggestion, new DeleteSuggestionCallback() {
                @Override
                public void onCallback() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void deleteHelper(Suggestion suggestion, final DeleteSuggestionCallback callback){
        String groupId = suggestion.getGroup_id();
        final String event_id = suggestion.getSuggestion_id();
        final DatabaseReference suggestionRef = firebaseDatabase.getReference().child("groups").child(groupId).child("suggestion");
        suggestionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //suggestionRef.removeValue();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    if (Objects.equals(postSnapshot.getValue(String.class), event_id)) {
                        suggestionRef.child(Objects.requireNonNull(postSnapshot.getKey())).removeValue();
                        break;
                    }
                }
                final DatabaseReference eventRef = firebaseDatabase.getReference().child("suggestions").child(event_id);
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

    private AlertDialog addConfirmation(final int position, String title) {
        return new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Add")
                .setMessage("Do you want to add " + title + " to your plan?")

                .setPositiveButton("Add", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        addSuggest(suggestions.get(position));
                        //suggestions.remove(position);
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
    }

    private void addSuggest(Suggestion suggestion) {
        if (suggestion.getSuggestion_id() != null){
            addHelper(suggestion, new AddSuggestionCallback() {
                @Override
                public void onCallback() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void addHelper(final Suggestion suggestion, final AddSuggestionCallback callback){
        String planId = suggestion.getPlan_id();

        String placeName = suggestion.getTitle();
        String eventDate = suggestion.getPlan_date();
        String startTime = suggestion.getTime_start();
        String endTime = suggestion.getTime_end();
        String type = suggestion.getType();

        Event anEvent = new Event(placeName, eventDate, startTime, endTime, type);

        anEvent.setQuery_id(suggestion.getQuery_id());
        anEvent.setLocation(suggestion.getLocation());
        anEvent.setWebsite(suggestion.getWebsite());
        anEvent.setPhone(suggestion.getPhone());
        anEvent.setRating(suggestion.getRating());

        final DatabaseReference eventRef = firebaseDatabase.getReference().child("events");

        final String eventId = eventRef.push().getKey();
        anEvent.setEvent_id(eventId);
        anEvent.setPlan_id(planId);
        anEvent.setCreator_id(firebaseUser.getUid());
        eventRef.child(eventId).setValue(anEvent);

        planRef = firebaseDatabase.getReference().child("plans").child(planId).child("events");

        getAllEventIDs(new EventIdCallback() {
            @Override
            public void onCallback(List<String> list) {
                eventIDs = list;
                eventIDs.add(eventId);

                planRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        planRef.setValue(eventIDs);

                        callback.onCallback();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        deleteSuggest(suggestion);

        Toast.makeText(context, "Suggestion has been saved into a group plan!",
                Toast.LENGTH_LONG).show();
    }

    private void getAllEventIDs(final EventIdCallback userIdCallback){
        planRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventIDs.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String eventId = postSnapshot.getValue(String.class); // String of groupID
                    eventIDs.add(eventId);
                }
                userIdCallback.onCallback(eventIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView suggestName;
        TextView suggestDesc;
        TextView suggestDetail;   // TEXT BUTTON

        SuggestionViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cv_suggest);
            suggestName = itemView.findViewById(R.id.item_title);
            suggestDesc = itemView.findViewById(R.id.item_desc_detail);
            suggestDetail = itemView.findViewById(R.id.item_detail);
        }
    }

    private interface DeleteSuggestionCallback {
        void onCallback();
    }

    private interface AddSuggestionCallback {
        void onCallback();
    }

    private interface EventIdCallback{
        void onCallback(List<String> list);
    }
}