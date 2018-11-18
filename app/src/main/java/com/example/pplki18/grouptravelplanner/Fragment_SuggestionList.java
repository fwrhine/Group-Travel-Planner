package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.utils.Event;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Plan;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Suggest;
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

    private DatabaseReference groupRef;
    private DatabaseReference suggestRef;
    private RecyclerView recyclerViewSuggestion;

    private FloatingActionButton new_suggestion_button;
    private ProgressBar progressBar;
    private List<String> suggestionIDs = new ArrayList<>();
    private List<Event> suggestions = new ArrayList<>();
    private RVAdapter_Suggest adapter;
    private Intent myIntent;
    private Group group;

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
        groupRef = firebaseDatabase.getReference().child("groups").child(group.getGroup_id()).child("suggestion");
        suggestRef = firebaseDatabase.getReference().child("events");

        progressBar.setVisibility(View.VISIBLE);
        getSuggestionIDs(new SuggestionIDCallback() {
            @Override
            public void onCallback(List<String> list) {
                suggestionIDs = list;
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        populatePlanRecyclerView();
    }

    private void setCreateSuggestionButton() {
        new_suggestion_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment_SuggestionList.this.startActivity(myIntent);
            }
        });
    }

    private void populatePlanRecyclerView() {
        Log.d(TAG, "populatePlanRecyclerView: Displaying list of plans in the ListView.");
        getAllSuggestion(new SuggestionCallback() {
            @Override
            public void onCallback(List<Event> list) {
                adapter = new RVAdapter_Suggest(suggestions, getActivity());  // TODO should I add another adapter???

                recyclerViewSuggestion.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getAllSuggestion(final SuggestionCallback callback) {
        //TODO: FIREBASE

        suggestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                suggestions.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Event event = postSnapshot.getValue(Event.class); // TODO What is postSnapshot?
                    if(suggestionIDs.contains(event.getEvent_id())){
                        suggestions.add(event);
                    }
                }
                callback.onCallback(suggestions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSuggestionIDs(final SuggestionIDCallback callback) {
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
        recyclerViewSuggestion = getView().findViewById(R.id.rv_suggestion_list);
        progressBar = Objects.requireNonNull(getView()).findViewById(R.id.progress_loader);
        new_suggestion_button = getView().findViewById(R.id.fab_add_suggestion);
        myIntent = new Intent(getActivity(), ChooseEventActivity.class);
        myIntent.putExtra("prev_fragment", "Fragment_SuggestionList");

        Intent i = getActivity().getIntent();
        group = i.getParcelableExtra("group");

        myIntent.putExtra("group_id", group.getGroup_id());

        Log.d(TAG, "Init Suggestion List");
    }

    private interface SuggestionIDCallback {
        void onCallback(List<String> list);
    }

    private interface SuggestionCallback {
        void onCallback(List<Event> list);
    }
}
