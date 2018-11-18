package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.InHomeActivity;
import com.example.pplki18.grouptravelplanner.Poll;
import com.example.pplki18.grouptravelplanner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RVAdapter_PollChoices extends RecyclerView.Adapter<RVAdapter_PollChoices.PollChoiceViewHolder>{

        List<Poll> pollChoiceList;
        Context context;

        FirebaseDatabase firebaseDatabase;
        FirebaseAuth firebaseAuth;
        FirebaseUser firebaseUser;
        DatabaseReference pollRef;

    public RVAdapter_PollChoices(List<Poll> pollChoiceList, Context context){
            this.pollChoiceList = pollChoiceList;
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return pollChoiceList.size();
        }

        @NonNull
        @Override
        public RVAdapter_PollChoices.PollChoiceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_poll_choice, viewGroup, false);
            RVAdapter_PollChoices.PollChoiceViewHolder rvh = new RVAdapter_PollChoices.PollChoiceViewHolder(v);
            return rvh;
        }

        @Override
        public void onBindViewHolder(final RVAdapter_PollChoices.PollChoiceViewHolder pollChoiceViewHolder, int i) {

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            firebaseDatabase = FirebaseDatabase.getInstance();
            pollRef = firebaseDatabase.getReference().child("polls");

            String choice = pollChoiceList.get(i).getChoiceList().get(i);
            Integer choiceVal = pollChoiceList.get(i).getChoiceMap().get(choice);

            pollChoiceViewHolder.choiceText.setText(choice);
            pollChoiceViewHolder.choiceVal.setText(choiceVal);

            pollChoiceViewHolder.choicePlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vote(pollChoiceViewHolder);
                    //go back to chat??
                }
            });
        }

        public void goBackToList(){
            Intent myIntent = new Intent(this.context , InHomeActivity.class);
            myIntent.putExtra("fragment", "reminder");
            context.startActivity(myIntent);
        }

        public void vote(PollChoiceViewHolder viewHolder) {
            String choice = viewHolder.choiceText.getText().toString();
            Integer newVal = Integer.parseInt(viewHolder.choiceVal.getText().toString());
            String id = viewHolder.id.getText().toString();
            viewHolder.choiceVal.setText(newVal++);
            //TODO save votes to firebase??
            saveTofirebase(choice, newVal, id);
        }

    private void saveTofirebase(final String choice, final Integer newValue, final String idfirebase){
        final List<Poll> pollList = new ArrayList<>();

        pollRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Integer> newSet = new HashMap<String, Integer>();
                pollRef.child(idfirebase).child("choiceMap").child(choice).setValue(newValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public static class PollChoiceViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;
            TextView choiceText;
            TextView choiceVal;
            TextView id;
            Button choicePlus;
//        Context context;

            PollChoiceViewHolder(View itemView) {
                super(itemView);
//            context = itemView.getContext();
                cardView = (CardView)itemView.findViewById(R.id.cv_row_poll_choice);
                choiceText = (TextView)itemView.findViewById(R.id.poll_choiceText);
                choiceVal = (TextView)itemView.findViewById(R.id.poll_choice_num);
                choicePlus = (Button)itemView.findViewById(R.id.poll_choice_increment);
                id = (TextView)itemView.findViewById(R.id.poll_id);

            }

        }
    }
