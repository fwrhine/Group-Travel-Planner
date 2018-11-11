package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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

public class Fragment_SuggestionList extends Fragment {

    private static final String TAG = "Fragment_SuggestionList";

    private DatabaseReference userRef;

    private FloatingActionButton new_suggestion_button;
    private ProgressBar progressBar;
    private List<String> suggestionIDs = new ArrayList<>();
    private Intent myIntent;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Objects.requireNonNull(getActivity()).setTitle("Suggestion List");
        return inflater.inflate(R.layout.fragment_suggestion_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        setCreateSuggestionButton();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        userRef = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid()).child("plans");

        progressBar.setVisibility(View.VISIBLE);
        getSuggestionIDs(new SuggestionIDCallback() {
            @Override
            public void onCallback(List<String> list) {
                suggestionIDs = list;
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setCreateSuggestionButton() {
        new_suggestion_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment_SuggestionList.this.startActivity(myIntent);
            }
        });
    }

    private void getSuggestionIDs(final SuggestionIDCallback callback) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                suggestionIDs.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String suggestionId = postSnapshot.getValue(String.class); // String of groupID
                    Log.d("SUGGESTION_ID", suggestionId);
                    suggestionIDs.add(suggestionId);
                }
                callback.onCallback(suggestionIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        progressBar = Objects.requireNonNull(getView()).findViewById(R.id.progress_loader);
        new_suggestion_button = getView().findViewById(R.id.fab_add_suggestion);
        myIntent = new Intent(getActivity(), TempChooseEventActivity.class);
        myIntent.putExtra("prev_fragment", "Fragment_SuggestionList");

        Log.d(TAG, "Init Suggestion List");
    }

    private interface SuggestionIDCallback {
        void onCallback(List<String> list);
    }
}
