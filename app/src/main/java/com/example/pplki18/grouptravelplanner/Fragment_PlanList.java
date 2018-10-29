package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.PlanContract.PlanEntry;
import com.example.pplki18.grouptravelplanner.utils.Plan;
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

        getPlanIDs(new PlanIDCallback() {
            @Override
            public void onCallback(List<String> list) {
                planIDs = list;
                populatePlanRecyclerView();
            }
        });
    }

    private void setCreatePlanButton() {
        new_plan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Create New Plan",
//                        Toast.LENGTH_LONG).show();
//                int plan_id = createNewPlan();
//                myIntent.putExtra("plan_id", plan_id);
                setPlanName();
                Fragment_PlanList.this.startActivity(myIntent);
            }
        });
    }

    private void setPlanName() {
        //TODO: FIREBASE
//        SQLiteDatabase db = databaseHelper.getReadableDatabase();
//
//        String query = "SELECT * FROM " + PlanEntry.TABLE_NAME + " WHERE " + PlanEntry.COL_USER_ID +
//                " = " + user.get(SessionManager.KEY_ID) + " AND " + PlanEntry.COL_PLAN_NAME +
//                " LIKE \'New Plan (%)\' ";
//
//        Cursor cursor = db.rawQuery(query, null);
//        String planName;
//        int idx;
//
//        if(cursor.getCount() > 0) {
//            cursor.moveToLast();
////            idx = cursor.getCount() + 1;
//            String temp = cursor.getString(cursor.getColumnIndex(PlanEntry.COL_PLAN_NAME));
//            idx = Integer.parseInt(temp.substring(10,temp.length()-1)) + 1;
//            planName = "New Plan (" + idx + ")";
//            cursor.close();
//        } else {
//            planName = "New Plan (1)";
//            cursor.close();
//        }
//
//        myIntent.putExtra("plan_name", planName);
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(PlanEntry.COL_PLAN_NAME, planName);
//        contentValues.put(PlanEntry.COL_USER_ID, user.get(SessionManager.KEY_ID));
//        long plan_id = db.insert(PlanEntry.TABLE_NAME, null, contentValues);

//        return (int)plan_id;
//        getLastId();
    }

    public void getLastId() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        long lastId = -1;
        String query = "SELECT " + PlanEntry._ID + " FROM " + PlanEntry.TABLE_NAME + " ORDER BY " +
                PlanEntry._ID + " DESC limit 1";

        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            lastId = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        c.close();
        myIntent.putExtra("plan_id", lastId+1);
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        Log.d("RESUME", "masuk resume");
        super.onResume();
//        plans = getAllPlans();
//        adapter = new RVAdapter_Plan(plans, getActivity());
//        recyclerViewPlan.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
    }

    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populatePlanRecyclerView() {
        Log.d(TAG, "populatePlanRecyclerView: Displaying list of plans in the ListView.");
        getAllPlans(new PlanCallback() {
            @Override
            public void onCallback(List<Plan> list) {
                adapter = new RVAdapter_Plan(plans, getActivity());

                recyclerViewPlan.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /*
     * Get all groups
     * */
    private void getAllPlans(final PlanCallback callback) {
        //TODO: FIREBASE
//
//        String selectQuery = " SELECT * FROM " + PlanEntry.TABLE_NAME + " WHERE " +
//                PlanEntry.COL_USER_ID + " = " + user.get(SessionManager.KEY_ID);
//
//        SQLiteDatabase db = databaseHelper.getReadableDatabase();
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        if (c.moveToFirst()) {
//            do {
//                Plan plan = new Plan();
//                plan.setPlan_name((c.getString(c.getColumnIndex(PlanEntry.COL_PLAN_NAME))));
//                plan.setPlan_overview(c.getString(c.getColumnIndex(PlanEntry.COL_DESCRIPTION)));
//                plan.setPlan_start_date((c.getString(c.getColumnIndex(PlanEntry.COL_START_DAY))));
//                plan.setPlan_end_date((c.getString(c.getColumnIndex(PlanEntry.COL_END_DAY))));
//                plan.setPlan_total_days((c.getInt(c.getColumnIndex(PlanEntry.COL_TOTAL_DAY))));
//                plan.setPlan_id((c.getInt(c.getColumnIndex(PlanEntry._ID))));
//
//                // adding to plan list
//                plans.add(plan);
//            } while (c.moveToNext());
//        }
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
