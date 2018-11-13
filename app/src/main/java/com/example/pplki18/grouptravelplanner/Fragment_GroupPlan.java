package com.example.pplki18.grouptravelplanner;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.utils.PagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_GroupPlan extends Fragment {

    private TabLayout tabLayout;

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
        findViewById();
        setTabLayout();
    }

    public void findViewById() {
        tabLayout = getView().findViewById(R.id.plan_tab_layout);
    }

    public void setTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setTag("CURRENT"));
        tabLayout.addTab(tabLayout.newTab().setTag("PAST"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

//        final PagerAdapter adapter = new PagerAdapter
//                (getSupportFragmentManager(), tabLayout.getTabCount(),
//                        sessionManager.getCurrentRegion(), regionCoor, plan_bundle);
//
//        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(2);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }

}
