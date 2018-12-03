package com.example.pplki18.grouptravelplanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.data.Plan;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Suggest;
import com.example.pplki18.grouptravelplanner.utils.Suggestion;
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

public class Fragment_SuggestionList extends Fragment {

    private static final String TAG = "Fragment_SuggestionList";

    private DatabaseReference groupRef;
    private DatabaseReference suggestRef;
    private DatabaseReference groupPlanRef;
    private DatabaseReference planRef;

    private RecyclerView recyclerViewSuggestion;

    private FloatingActionButton new_suggestion_button;
    private ProgressBar progressBar;

    private List<String> suggestionIDs = new ArrayList<>();
    private List<Suggestion> suggestions = new ArrayList<>();

    private RVAdapter_Suggest adapter;
    private Intent myIntent;
    private Group group;

    private AlertDialog alertDialog1;
    private AlertDialog alertDialog2;

    private List<Plan> plans = new ArrayList<>();
    private List<String> planIds = new ArrayList<>();
    private List<String> planNames = new ArrayList<>();
    private List<String> dateList = new ArrayList<>();

    private List<String> planIdTemps = new ArrayList<>();

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
        suggestRef = firebaseDatabase.getReference().child("suggestions");

        groupPlanRef = firebaseDatabase.getReference().child("groups").child(group.getGroup_id()).child("plans");
        planRef = firebaseDatabase.getReference().child("plans");

        getAllGroupPlanId(new PlanIDCallback() {
            @Override
            public void onCallback(List<String> list) {
                planIds = list;
            }
        });

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
                CreateAlertDialogWithRadioButtonGroup();
            }
        });
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
            progressBar.setVisibility(View.VISIBLE);

            getAllPlan(new PlanCallback() {
                @Override
                public void onCallback(List<Plan> list) {
                    plans = list;
                }
            });

            getAllSuggestion(new SuggestionCallback() {
                @Override
                public void onCallback(List<Suggestion> list) {
                    suggestions = list;
                    progressBar.setVisibility(View.INVISIBLE);
                    adapter = new RVAdapter_Suggest(suggestions, group, getActivity());
                    recyclerViewSuggestion.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });
    }

    private void populatePlanRecyclerView() {
        Log.d(TAG, "populatePlanRecyclerView: Displaying list of plans in the ListView.");

        getAllPlan(new PlanCallback() {
            @Override
            public void onCallback(List<Plan> list) {
                plans = list;
            }
        });

        getAllSuggestion(new SuggestionCallback() {
            @Override
            public void onCallback(List<Suggestion> list) {
                suggestions = list;
                Log.d("SUGGESTION-LIST", list.size()+"");
                adapter = new RVAdapter_Suggest(suggestions, group, getActivity());
                Log.d("ADAPTER-COUNT", adapter.getItemCount()+"");
                recyclerViewSuggestion.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getAllSuggestion(final SuggestionCallback callback) {
        suggestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                suggestions.clear();
                Log.d("REFERENCE", dataSnapshot.getRef().toString());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Suggestion suggestion = postSnapshot.getValue(Suggestion.class);
                    assert suggestion != null;
                    if(suggestionIDs.contains(suggestion.getSuggestion_id())){
                        suggestions.add(suggestion);
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

    private void getAllPlan(final PlanCallback callback) {
        planRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                plans.clear();
                Log.d("REFERENCE", dataSnapshot.getRef().toString());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Plan plan = postSnapshot.getValue(Plan.class);
                    assert plan != null;
                    if(planIds.contains(plan.getPlan_id())){
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

    private void getAllGroupPlanId(final PlanIDCallback callback) {
        groupPlanRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                planIds.clear();
                Log.d("REFERENCE", dataSnapshot.getRef().toString());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String planId = postSnapshot.getValue(String.class);
                    assert planId != null;
                    planIds.add(planId);
                }
                callback.onCallback(planIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CreateAlertDialogWithRadioButtonGroup(){
        planNames.clear();
        planIdTemps.clear();
        for (int i = 0; i < plans.size(); i++) {
            planNames.add(plans.get(i).getPlan_name());
            planIdTemps.add(plans.get(i).getPlan_id());
        }

        final CharSequence[] values = planNames.toArray(new CharSequence[planNames.size()]);

        if (values.length == 0) {
            Toast.makeText(Fragment_SuggestionList.this.getActivity(), "There are currently no plans to give a suggestion to",
                    Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            builder.setTitle("Select Your Choice Plan");

            builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int item) {

                    switch(item)
                    {
                        default:
                            myIntent.putExtra("suggest_to_plan_id", planIdTemps.get(item));
                            Log.d("PLAN-ID-TEMP", planIdTemps.get(item));
                            myIntent.putExtra("suggest_to_plan_name", values[item]);
                            selectDate(plans.get(item));
                            break;
                    }
                }
            });
            alertDialog1 = builder.create();
            alertDialog1.show();
        }
    }

    private void selectDate(Plan plan) {
        alertDialog1.dismiss();
        dateList.clear();
        String strFirstDate = plan.getPlan_start_date();
        String strSecondDate = plan.getPlan_end_date();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("d MMMM yyyy", Locale.US);

        try {
            Date startDate = dateFormatter.parse(strFirstDate);
            Date endDate = dateFormatter.parse(strSecondDate);

            final Calendar startCal = Calendar.getInstance();
            final Calendar endCal = Calendar.getInstance();

            startCal.setTime(startDate);
            endCal.setTime(endDate);

            Long dateDiff = endCal.getTime().getTime() - startCal.getTime().getTime();

            dateList.add(dateFormatter.format(startCal.getTime()));

            int diffDays =  (int) (dateDiff / (24* 1000 * 60 * 60));

            if (diffDays != 0) {
                for (int i = 0; i < diffDays; i++) {
                    startCal.add(Calendar.DATE, 1);
                    dateList.add(dateFormatter.format(startCal.getTime()));
                }
            }

            final CharSequence[] values = dateList.toArray(new CharSequence[dateList.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            builder.setTitle("Select Your Date");

            builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int item) {
                    switch(item)
                    {
                        default:
                            myIntent.putExtra("suggest_to_plan_date", values[item]);

                            Fragment_SuggestionList.this.startActivity(myIntent);
                            break;
                    }
                    alertDialog2.dismiss();
                }
            });
            alertDialog2 = builder.create();
            alertDialog2.show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewSuggestion = Objects.requireNonNull(getView()).findViewById(R.id.rv_suggestion_list);
        recyclerViewSuggestion.setLayoutManager(linearLayoutManager);
        progressBar = Objects.requireNonNull(getView()).findViewById(R.id.progress_loader);
        new_suggestion_button = getView().findViewById(R.id.fab_add_suggestion);
        myIntent = new Intent(getActivity(), ChooseEventActivity.class);
        myIntent.putExtra("prev_fragment", "Fragment_SuggestionList");

        Intent i = Objects.requireNonNull(getActivity()).getIntent();
        group = i.getParcelableExtra("group");

        myIntent.putExtra("group_id", group.getGroup_id());

        Log.d(TAG, "Init Suggestion List");
    }

    private interface SuggestionIDCallback {
        void onCallback(List<String> list);
    }

    private interface SuggestionCallback {
        void onCallback(List<Suggestion> list);
    }

    private interface PlanIDCallback {
        void onCallback(List<String> list);
    }

    private interface PlanCallback {
        void onCallback(List<Plan> list);
    }
}
