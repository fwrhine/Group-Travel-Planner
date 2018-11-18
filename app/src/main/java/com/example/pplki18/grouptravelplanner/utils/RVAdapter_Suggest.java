package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.Fragment_SuggestionList;
import com.example.pplki18.grouptravelplanner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RVAdapter_Suggest extends RecyclerView.Adapter<RVAdapter_Suggest.ViewHolder> {
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference eventRef;
    StorageReference storageReference;

    List<Event> suggestions;
    Context context;

    public RVAdapter_Suggest(List<Event> suggestions, Context context) {
        this.suggestions = suggestions;
        this.context = context;

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        eventRef = firebaseDatabase.getReference().child("events");
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_suggestion, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Event event = suggestions.get(i);

        viewHolder.eventName.setText(event.getTitle());
        viewHolder.eventDesc.setText(event.getDescription());

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Fragment_SuggestionList.class);
                intent.putExtra("suggestion_id", event.getEvent_id());
                intent.putExtra("suggestion_name", viewHolder.eventName.getText().toString());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView eventName;
        TextView eventDesc;
        TextView eventDetail;   // TEXT BUTTON

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cv_event);
            eventName = itemView.findViewById(R.id.item_title);
            eventDesc = itemView.findViewById(R.id.item_desc_detail);
            eventDetail = itemView.findViewById(R.id.item_detail);
        }

    }
}

