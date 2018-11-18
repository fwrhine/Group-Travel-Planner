package com.example.pplki18.grouptravelplanner;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_GroupPlanList extends Fragment {

    private LinearLayout top_layout;
    private TextView warning;
    private FloatingActionButton fab_add_plan;
    private Bundle bundle;
    private String type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_plan_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    public void init() {
        findViewById();
        bundle = getArguments();
        type = bundle.getString("type");

        if (type.equals("past")) {
            top_layout.setVisibility(View.GONE);
            fab_add_plan.setVisibility(View.GONE);
            warning.setText("You have never finish a plan. Go make one!");
        }
    }

    public void findViewById() {
        top_layout = getView().findViewById(R.id.top_layout);
        warning = getView().findViewById(R.id.warning);
        fab_add_plan = getView().findViewById(R.id.fab_add_plan);
    }

}
