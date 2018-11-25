package com.example.pplki18.grouptravelplanner;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.data.Plan;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Plan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_GroupPlanList extends Fragment {
    private static final String TAG = "Fragment_GroupPlanList";

    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private DatabaseReference groupRef;
    private DatabaseReference planRef;

    private RecyclerView recyclerViewGroupPlan;
    private LinearLayoutManager linearLayoutManager;
    private ProgressBar progressBar;
    private LinearLayout top_layout;
    private TextView warning, warning1, planName, planDate, planOverview, planCreated;
    private CardView cardView;
    private ImageButton planMenuButton;
    private LinearLayout included;
    private FloatingActionButton fab_add_plan;
    private String type;
    private Group group;

    private Date today;
    private SimpleDateFormat dateFormatter;
    private Plan currentPlan;
    private List<String> planIDs = new ArrayList<>();
    private List<Plan> plans = new ArrayList<>();
    private List<String> eventIDs = new ArrayList<>();

    private RVAdapter_Plan adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_plan_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        type = bundle.getString("type");
        group = bundle.getParcelable("group");

        today = Calendar.getInstance().getTime();
        dateFormatter = new SimpleDateFormat("d MMMM yyyy", Locale.US);
        try {
            today = dateFormatter.parse(dateFormatter.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        groupRef = firebaseDatabase.getReference().child("groups").child(group.getGroup_id()).child("plans");
        planRef = firebaseDatabase.getReference().child("plans");

        init();

        progressBar.setVisibility(View.VISIBLE);
        getPlanIDs(new PlanIDCallback() {
            @Override
            public void onCallback(List<String> list) {
                planIDs = list;
                populatePlanRecyclerView();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        recyclerViewGroupPlan.setHasFixedSize(true);
        recyclerViewGroupPlan.setLayoutManager(linearLayoutManager);
    }

    public void init() {
        findViewById();

        Log.d("CREATORID", group.getCreator_id());

        if (!group.getCreator_id().equals(firebaseUser.getUid())) {
            fab_add_plan.setVisibility(View.GONE);
        } else {
            warning.setText("You don\'t have any plan in progress...\nCreate a plan now!");
            setCreatePlanButton();
        }

        if (type.equals("past")) {
            top_layout.setVisibility(View.GONE);
            fab_add_plan.setVisibility(View.GONE);
            warning1.setText("You have never finished a plan. Go make one!");
        } else {
            setCurrentPlan();
        }
    }

    public void setCurrentPlan() {
        if (currentPlan == null) {
            included.setVisibility(View.GONE);
            warning.setVisibility(View.VISIBLE);
        } else {
            included.setVisibility(View.VISIBLE);
            warning.setVisibility(View.GONE);

            String total_day_str;
            int total_day = currentPlan.getPlan_total_days();
            if (total_day == 1 || total_day == 0) {
                total_day_str = "(" + total_day + " day trip)";
            } else {
                total_day_str = "(" + total_day + " days trip)";
            }

            String dateString = currentPlan.getPlan_start_date() + " - "
                    + currentPlan.getPlan_end_date() + "\n" + total_day_str;
            String createdString = "Modified: " + currentPlan.getPlan_modified() + " / "
                    + "Created: " + currentPlan.getPlan_created();

            planName.setText(currentPlan.getPlan_name());
            planDate.setText(dateString);
            planOverview.setText(currentPlan.getPlan_overview());
            planCreated.setText(createdString);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), EditPlanActivity.class);
                    intent.putExtra("plan_id", currentPlan.getPlan_id());
                    intent.putExtra("plan_name", currentPlan.getPlan_name());
                    intent.putExtra("plan_date_start", currentPlan.getPlan_start_date());
                    intent.putExtra("plan_date_end", currentPlan.getPlan_end_date());
                    intent.putExtra("plan_total_days", currentPlan.getPlan_total_days());

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("group", group);
                    bundle.putString("ACTIVITY", "Fragment_GroupPlanList");
                    intent.putExtra("bundle", bundle);

                    startActivity(intent);
                }
            });

            planMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(getActivity(), planMenuButton);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.plan_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            AlertDialog box;
                            switch (item.getItemId()) {
                                case R.id.rename_plan:
                                    box = renameDialog(currentPlan.getPlan_name());
                                    box.show();
                                    break;
                                case R.id.delete_plan:
                                    box = deleteConfirmation();
                                    box.show();
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });

            if (!group.getCreator_id().equals(firebaseUser.getUid())) {
                planMenuButton.setVisibility(View.GONE);
            }
        }

        if (!plans.isEmpty()) {
            warning1.setVisibility(View.GONE);
        }
    }

    public void setPastPlan() {
        warning1.setVisibility(View.GONE);
    }

    private AlertDialog renameDialog(String name) {
        final EditText edtText = new EditText(getActivity());
        return new AlertDialog.Builder(getActivity())
                //set message, title, and icon
                .setTitle("Rename \"" + name + "\"?")
                .setMessage("Insert new name below!")
                .setView(edtText)
                .setPositiveButton("Rename", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your renaming code
                        String new_name = edtText.getText().toString();
                        planName.setText(new_name);
                        renamePlan(new_name);
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
    }

    public void renamePlan(String new_name) {
        currentPlan.setPlan_name(new_name);
        planRef = firebaseDatabase.getReference().child("plans").child(currentPlan.getPlan_id());
        planRef.child("plan_name").setValue(new_name);
    }

    private AlertDialog deleteConfirmation() {
        return new AlertDialog.Builder(getActivity())
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete \"" + currentPlan.getPlan_name() + "\"?")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deletePlan(currentPlan);
                        currentPlan = null;
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .create();
    }

    private void deletePlan(Plan plan) {

        if (plan.getPlan_id() != null) {
            deleteHelper(plan, new DeleteEventCallback() {
                @Override
                public void onCallback() {
                    included.setVisibility(View.GONE);
                    warning.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void deleteHelper(Plan plan, final DeleteEventCallback callback) {

        final String plan_id = plan.getPlan_id();
        planRef = firebaseDatabase.getReference().child("plans").child(plan_id);
        final DatabaseReference ref = firebaseDatabase.getReference().child("groups").child(group.getGroup_id()).child("plans");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (Objects.equals(postSnapshot.getValue(String.class), plan_id)) {
                        ref.child(Objects.requireNonNull(postSnapshot.getKey())).removeValue();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getAllEventIDs(new EventIdCallback() {
            @Override
            public void onCallback(List<String> list) {
                eventIDs = list;

                planRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (String event_id : eventIDs) {
                            final DatabaseReference eventRef = firebaseDatabase.getReference().child("events").child(event_id);
                            eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    eventRef.removeValue();
                                    callback.onCallback();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        planRef.removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private interface DeleteEventCallback {
        void onCallback();
    }

    private void getAllEventIDs(final EventIdCallback userIdCallback){
        planRef.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventIDs.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String eventId = postSnapshot.getValue(String.class); // String of eventID
                    eventIDs.add(eventId);
                }
                userIdCallback.onCallback(eventIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private interface EventIdCallback{
        void onCallback(List<String> list);
    }

    public void findViewById() {
        top_layout = getView().findViewById(R.id.top_layout);
        warning = getView().findViewById(R.id.warning);
        warning1 = getView().findViewById(R.id.warning1);
        included = getView().findViewById(R.id.included_row_plan);
        fab_add_plan = getView().findViewById(R.id.fab_add_plan);

        cardView = getView().findViewById(R.id.cv_plan);
        planName = getView().findViewById(R.id.plan_name);
        planDate = getView().findViewById(R.id.plan_date);
        planOverview = getView().findViewById(R.id.plan_overview);
        planCreated = getView().findViewById(R.id.plan_created);
        planMenuButton = getView().findViewById(R.id.plan_menu_button);

        progressBar = getView().findViewById(R.id.progress_loader);
        recyclerViewGroupPlan = getView().findViewById(R.id.rv_group_plan_list);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
    }

    public void setCreatePlanButton() {
        fab_add_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), CreateNewPlanActivity.class);
                myIntent.putExtra("init_name", setPlanName());
                myIntent.putParcelableArrayListExtra("plans", (ArrayList<? extends Parcelable>) plans);
                myIntent.putExtra("group_id", group.getGroup_id());
                myIntent.putExtra("ACTIVITY_GROUP", "Fragment_GroupPlanList");
                startActivity(myIntent);
            }
        });
    }

    private int setPlanName() {
        if (plans != null) {
            return plans.size()+1;
        } else {
            return 1;
        }
    }

    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populatePlanRecyclerView() {
        Log.d(TAG, "populateGroupPlanRecyclerView: Displaying list of plans in the ListView.");
        getAllPlans(new PlanCallback() {
            @Override
            public void onCallback(List<Plan> list) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("group", group);
                bundle.putString("ACTIVITY", "Fragment_GroupPlanList");

                adapter = new RVAdapter_Plan(list, getActivity(), bundle);
                recyclerViewGroupPlan.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                setCurrentPlan();

                if (!list.isEmpty() && type.equals("past")) setPastPlan();
            }
        });
    }

    /*
     * Get all group plans
     * */
    private void getAllPlans(final PlanCallback callback) {

        planRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                plans.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Plan plan = postSnapshot.getValue(Plan.class); // Plan Objects
                    if (planIDs.contains(plan.getPlan_id())){
                        try {
                            Date start_date = dateFormatter.parse(plan.getPlan_start_date());
                            Date end_date = dateFormatter.parse(plan.getPlan_end_date());
                            if (today.getTime() >= start_date.getTime() &&
                                    today.getTime() <= end_date.getTime()) {
                                currentPlan = plan;
                                setCurrentPlan();
                            } else {
                                if (type.equals("past")) {
                                    if (end_date.getTime() < today.getTime()) {
                                        plans.add(plan);
                                    }
                                } else {
                                    if (start_date.getTime() > today.getTime()) {
                                        plans.add(plan);
                                    }
                                }
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
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
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                planIDs.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String planId = postSnapshot.getValue(String.class); // String of groupID
//                    Log.d("PLAN_ID", planId);
                    planIDs.add(planId);
                }
                callback.onCallback(planIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private interface PlanIDCallback {
        void onCallback(List<String> list);
    }

    private interface PlanCallback {
        void onCallback(List<Plan> list);
    }

}
