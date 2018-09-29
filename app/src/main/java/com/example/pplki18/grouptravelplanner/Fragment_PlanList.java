package com.example.pplki18.grouptravelplanner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.utils.Plan;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Plan;

import java.util.ArrayList;
import java.util.List;


public class Fragment_PlanList extends Fragment {

    private static final String TAG = "Fragment_PlanList";

    DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewPlan;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton new_plan_button;
    private Toolbar toolbar;
    DatabaseHelper myDb;

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

        recyclerViewPlan.setHasFixedSize(true);
        recyclerViewPlan.setLayoutManager(linearLayoutManager);

        populatePlanRecyclerView();
    }

    // TEMP - nopal
    public void setCreatePlanButton() {
        new_plan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Create New Plan",
                        Toast.LENGTH_LONG).show();
//                Intent myIntent = new Intent(getActivity(), CreatePlanActivity.class);
//                Fragment_PlanList.this.startActivity(myIntent);
            }
        });
    }


    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populatePlanRecyclerView() {
        Log.d(TAG, "populatePlanRecyclerView: Displaying list of plans in the ListView.");

        //get data and append to list
        List<Plan> plans = getAllPlans();
        RVAdapter_Plan adapter = new RVAdapter_Plan(plans, getActivity());
        recyclerViewPlan.setAdapter(adapter);
    }

    /*
     * Get all groups
     * */
    public List<Plan> getAllPlans() {
        List<Plan> plans = new ArrayList<Plan>();

        Plan plan = new Plan();
        plan.setPlan_name("Sample Plan");
        plan.setPlan_start_date("Mon, 1 Oct");
        plan.setPlan_end_date("Mon, 8 Oct");
        plan.setPlan_total_days(7);
        plan.setPlan_overview("Mon, 1 Oct (09:00): Soekarno-Hatta International Airport\n" + "...");
        plan.setPlan_modified("Sep 29, 2018");
        plan.setPlan_created("Sep 29, 2018");
        plans.add(plan);

        Plan plan2 = new Plan();
        plan2.setPlan_name("Sample Plan 2");
        plan2.setPlan_start_date("Mon, 1 Oct");
        plan2.setPlan_end_date("Mon, 8 Oct");
        plan2.setPlan_total_days(7);
        plan2.setPlan_overview("Mon, 1 Oct (09:00): Soekarno-Hatta International Airport\n" + "...");
        plan2.setPlan_modified("Sep 29, 2018");
        plan2.setPlan_created("Sep 29, 2018");
        plans.add(plan2);
//        String selectQuery = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + "  u, " +
//                FriendsContract.FriendsEntry.TABLE_NAME + "  f" +
//                " WHERE " + "f." + FriendsContract.FriendsEntry.COL_FRIEND_ID + " = " + "u." +
//                UserContract.UserEntry._ID;
//
//        Log.e("FRIENDS", selectQuery);
//
//        SQLiteDatabase db = databaseHelper.getReadableDatabase();
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to list
//        if (c.moveToFirst()) {
//            do {
//                Friend plan = new Friend();
//                plan.setFriend_username((c.getString(c.getColumnIndex(UserContract.UserEntry.COL_USERNAME))));
//                plan.setUser_plan_image((c.getBlob(c.getColumnIndex(UserContract.UserEntry.COL_PICTURE))));
//
//                // adding to group list
//                plans.add(plan);
//            } while (c.moveToNext());
//        }

        return plans;
    }

    private void init() {
        recyclerViewPlan = (RecyclerView) getView().findViewById(R.id.rv_plan_list);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        databaseHelper = new DatabaseHelper(this.getActivity());
        new_plan_button = getView().findViewById(R.id.fab_add_plan);
    }
}
