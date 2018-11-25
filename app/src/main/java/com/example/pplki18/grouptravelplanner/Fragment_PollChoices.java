package com.example.pplki18.grouptravelplanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.data.User;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_PollChoices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Fragment_PollChoices extends Fragment implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    DatabaseReference userRef;
    DatabaseReference pollChoiceRef;
    DatabaseReference pollChoiceMapRef;
    StorageReference storageReference;
    private RecyclerView recyclerViewGroup;
    private LinearLayoutManager linearLayoutManager;
    private List<Poll> pollList;
    private HashMap<String, Integer> pollMap;
    private Context context;
    long i;
    private String pollID;

    private HashMap<String, Integer> pollChoices = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poll_choices, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        // TODO pollID = intent.getIntent().getStringExtra("id");
        pollID = "something";
        if (firebaseUser == null) {
            Intent errorIntent = new Intent(getActivity(), LoginActivity.class);
            Fragment_PollChoices.this.startActivity(errorIntent);
            getActivity().finish();
        }
        else {
            pollChoiceRef = firebaseDatabase.getReference().child("polls").child(pollID).child("choiceList");
            pollChoiceMapRef = firebaseDatabase.getReference().child("polls").child(pollID).child("choiceMap");
            storageReference = FirebaseStorage.getInstance().getReference();

            context = getActivity().getApplicationContext();

            recyclerViewGroup.setHasFixedSize(true);
            recyclerViewGroup.setLayoutManager(linearLayoutManager);

            populateReminderRecyclerView();
        }
    }

    private void populateReminderRecyclerView() {
        pollList = getAllPollChoices();
        pollMap = getAllPollMapSets();
        RVAdapter_PollChoices adapter = new RVAdapter_PollChoices(pollList, pollMap, getActivity());
        recyclerViewGroup.setAdapter(adapter);
    }

    public List<Poll> getAllPollChoices() {
        //TODO get poll choices list
        return  null;
    }

    public HashMap<String, Integer> getAllPollMapSets() {
        return pollChoices;
    }

    public void getPollChoices(final PollChoiceCallback pollCallback) {
        pollChoiceRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("OUTER_REFERENCE", dataSnapshot.getRef().toString());
                List<String> choiceList = new ArrayList<>();
                for (DataSnapshot choice: dataSnapshot.getChildren()){
                    String c = choice.getValue(String.class);
                    Log.d("REFERENCE", choice.getRef().toString());
                    Log.v("CHOICE MAPAAAA", c);
                    choiceList.add(c);
                }
                pollCallback.onCallback(choiceList);
                Log.v("CHOICE MAPAAAA", choiceList.size() + "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getPollMapSets(final PollMapCallback pollCallback) {
        pollChoiceMapRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("OUTER_REFERENCE", dataSnapshot.getRef().toString());
                List<HashMap<String, Integer>> mapList = new ArrayList<>();
                for (DataSnapshot set: dataSnapshot.getChildren()){
                    HashMap<String, Integer> map = set.getValue(HashMap.class);
                    Log.d("REFERENCE", set.getRef().toString());
                    mapList.add(map);
                }
                pollCallback.onCallback(mapList);
                Log.v("CHOICE MAPAAAA", mapList.size() + "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void init() {
        recyclerViewGroup = (RecyclerView) getView().findViewById(R.id.rv2);
        linearLayoutManager = new LinearLayoutManager(getActivity());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private interface PollChoiceCallback{
        void onCallback(List<String> list);
    }

    private interface PollMapCallback{
        void onCallback(List<HashMap<String, Integer>> list);
    }

}