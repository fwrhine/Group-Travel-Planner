package com.example.pplki18.grouptravelplanner;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.utils.GroupPlanPagerAdapter;
import com.example.pplki18.grouptravelplanner.utils.PagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_GroupPlan extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Plan");
        return inflater.inflate(R.layout.fragment_group_plan, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    public void init() {
        bundle = getActivity().getIntent().getExtras();

        findViewById();
        setTabLayout();
    }

    public void findViewById() {
        tabLayout = getView().findViewById(R.id.plan_tab_layout);
        viewPager = getView().findViewById(R.id.group_plan_pager);
    }

    public void setTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("CURRENT"));
        tabLayout.addTab(tabLayout.newTab().setText("PAST"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        GroupPlanPagerAdapter adapter = new GroupPlanPagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount(), bundle);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}
