package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.pplki18.grouptravelplanner.old_stuff.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.old_stuff.PlanContract.PlanEntry;
import com.example.pplki18.grouptravelplanner.data.Plan;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Plan;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Fragment_PlanList extends Fragment {

    private static final String TAG = "Fragment_PlanList";

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userRef;
    private DatabaseReference planRef;
    private DatabaseHelper databaseHelper;

    private RecyclerView recyclerViewPlan;
    private ProgressBar progressBar;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton new_plan_button;
    private SessionManager session;
    private HashMap<String, String> user;
    private List<String> planIDs = new ArrayList<>();
    private List<Plan> plans = new ArrayList<>();
    private RVAdapter_Plan adapter;
    private DatabaseHelper myDb;
    private Intent myIntent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Plan List");
        return inflater.inflate(R.layout.fragment_plan_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        setCreatePlanButton();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userRef = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid()).child("plans");
        planRef = firebaseDatabase.getReference().child("plans");

        recyclerViewPlan.setHasFixedSize(true);
        recyclerViewPlan.setLayoutManager(linearLayoutManager);

        progressBar.setVisibility(View.VISIBLE);
        getPlanIDs(new PlanIDCallback() {
            @Override
            public void onCallback(List<String> list) {
                planIDs = list;
                populatePlanRecyclerView();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setCreatePlanButton() {
        new_plan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlanName();
                myIntent.putParcelableArrayListExtra("plans", (ArrayList<? extends Parcelable>) plans);
                Fragment_PlanList.this.startActivity(myIntent);
            }
        });
    }

    private void setPlanName() {
        if (plans != null) {
            myIntent.putExtra("init_name", plans.size()+1);
        } else {
            myIntent.putExtra("init_name", 1);
        }
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        Log.d("RESUME", "masuk resumeeee");
        super.onResume();
        this.onActivityCreated(null);
    }

    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populatePlanRecyclerView() {
        Log.d(TAG, "populatePlanRecyclerView: Displaying list of plans in the ListView.");
        getAllPlans(new PlanCallback() {
            @Override
            public void onCallback(List<Plan> list) {
                adapter = new RVAdapter_Plan(list, getActivity());

                recyclerViewPlan.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /*
     * Get all plans
     * */
    private void getAllPlans(final PlanCallback callback) {
        planRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                plans.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Plan plan = postSnapshot.getValue(Plan.class); // Plan Objects
                    if(planIDs.contains(plan.getPlan_id())){
                        plans.add(plan);
                    }
                }
                Collections.sort(plans);
                callback.onCallback(plans);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPlanIDs(final PlanIDCallback callback) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                planIDs.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String planId = postSnapshot.getValue(String.class); // String of groupID
                    Log.d("PLAN_ID", planId);
                    planIDs.add(planId);
                }
                callback.onCallback(planIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        recyclerViewPlan = getView().findViewById(R.id.rv_plan_list);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        databaseHelper = new DatabaseHelper(this.getActivity());
        new_plan_button = getView().findViewById(R.id.fab_add_plan);
        progressBar = getView().findViewById(R.id.progress_loader);
        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUserDetails();
        myIntent = new Intent(getActivity(), CreateNewPlanActivity.class);
    }

    private interface PlanIDCallback {
        void onCallback(List<String> list);
    }
    private interface PlanCallback {
        void onCallback(List<Plan> list);
    }
}
