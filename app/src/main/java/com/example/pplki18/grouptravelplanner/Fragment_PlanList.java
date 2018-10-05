package com.example.pplki18.grouptravelplanner;

import android.content.ContentValues;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.EventContract;
import com.example.pplki18.grouptravelplanner.data.PlanContract;
import com.example.pplki18.grouptravelplanner.data.PlanContract.PlanEntry;
import com.example.pplki18.grouptravelplanner.data.UserContract;
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

    public void setCreatePlanButton() {
        new_plan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Create New Plan",
//                        Toast.LENGTH_LONG).show();
                int plan_id = createNewPlan();
                Intent myIntent = new Intent(getActivity(), CreateNewPlanActivity.class);
                myIntent.putExtra("plan_id", plan_id);
                Fragment_PlanList.this.startActivity(myIntent);
            }
        });
    }

    public int createNewPlan() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String query = "SELECT * FROM " + PlanEntry.TABLE_NAME + " WHERE user_id = "
                + UserContract.UserEntry._ID;

        String query2 = "SELECT * FROM " + PlanEntry.TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        String planName;
        int idx = 0;
        int last_id = 0;
        if(cursor.getCount() > 0) {
            idx = cursor.getCount() + 1;
            planName = "New Plan (" + idx + ")";
            cursor.close();
        } else {
            planName = "New Plan";
            cursor.close();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlanEntry.COL_PLAN_NAME, planName);
        db.insert(PlanEntry.TABLE_NAME, null, contentValues);

        cursor = db.rawQuery(query2, null);
        if(cursor.getCount() > 0) {
            cursor.moveToLast();
            last_id = cursor.getColumnIndex(PlanEntry._ID);
            cursor.close();
        } else {
            cursor.close();
        }

        return last_id;
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

        return plans;
    }

    private void init() {
        recyclerViewPlan = (RecyclerView) getView().findViewById(R.id.rv_plan_list);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        databaseHelper = new DatabaseHelper(this.getActivity());
        new_plan_button = getView().findViewById(R.id.fab_add_plan);
    }
}
