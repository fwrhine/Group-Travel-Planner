package com.example.pplki18.grouptravelplanner;

import android.content.Context;
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

import com.example.pplki18.grouptravelplanner.utils.RVAdapter_PollChoices;

import java.util.List;

import static android.content.ContentValues.TAG;

public class Fragment_PollChoices extends Fragment implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerViewGroup;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton fab;
    private List<Poll> pollList;
    Reminder r;
    private Context context;
    long i;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poll_choices, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();

        context = getActivity().getApplicationContext();

        recyclerViewGroup.setHasFixedSize(true);
        recyclerViewGroup.setLayoutManager(linearLayoutManager);

        populateReminderRecyclerView();
    }

    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populateReminderRecyclerView() {
        Log.d(TAG, "populateGroupRecyclerView: Displaying list of groups in the ListView.");

        pollList = getAllPollChoices();
        RVAdapter_PollChoices adapter = new RVAdapter_PollChoices(pollList, getActivity());
        recyclerViewGroup.setAdapter(adapter);
    }

    private List<Poll> getAllPollChoices() {
        //TODO get poll choices list
        return  null;
    }

    private void init() {
        recyclerViewGroup = (RecyclerView) getView().findViewById(R.id.rv2);
        linearLayoutManager = new LinearLayoutManager(getActivity());
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

}