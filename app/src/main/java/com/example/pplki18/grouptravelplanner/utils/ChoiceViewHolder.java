package com.example.pplki18.grouptravelplanner.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.Choice;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChoiceViewHolder extends RecyclerView.ViewHolder {

    private String pollID;
    private String userID;

    private TextView choiceName, voterNum;
    private Button voteButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference pollRef;


    public ChoiceViewHolder(View itemView, String pollID) {
        super(itemView);

        this.pollID = pollID;
        choiceName = itemView.findViewById(R.id.poll_choice);
        voterNum = itemView.findViewById(R.id.poll_choice_num);
        voteButton = itemView.findViewById(R.id.poll_choice_increment);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        pollRef = firebaseDatabase.getReference().child("polls").child(pollID);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    public void bind(final Choice choice) {
        choiceName.setText(choice.getName());
        if (choice.getVoters() != null) {
            voterNum.setText(choice.getVoters().size() + "");
        } else {
            voterNum.setText("0");
        }
        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vote(choice.getChoiceID());
            }
        });
    }

    public void vote(final String choiceID) {
        pollRef.child("voters").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // if the user has already voted and want to change the vote
                if(dataSnapshot.hasChild(userID)) {
                    Log.d("DATA_REF", dataSnapshot.getRef().toString());
                    changeVote(choiceID);
                } else {
                    pollRef.child("voters").child(userID).setValue(true);
                    firstTimeVote(choiceID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void firstTimeVote(String choiceID) {
        final DatabaseReference choiceRef = pollRef.child("choiceList").child(choiceID).child("voters");
        choiceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                choiceRef.child(userID).setValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void changeVote(final String choiceID) {
        pollRef.child("choiceList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("voters").hasChild(userID)) {
                        // check if the user chooses the same choice
                        if(choiceID.equals(snapshot.getKey())) {
                            continue;
                        } else {
                            snapshot.child("voters").child(userID).getRef().removeValue();
                            firstTimeVote(choiceID);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
