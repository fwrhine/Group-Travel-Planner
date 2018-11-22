package com.example.pplki18.grouptravelplanner.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.example.pplki18.grouptravelplanner.Fragment_GroupPlanList;
import com.example.pplki18.grouptravelplanner.data.Group;

public class GroupPlanPagerAdapter extends FragmentPagerAdapter {
    private int mNumOfTabs;
    private Bundle bundle;

    public GroupPlanPagerAdapter(FragmentManager fm, int NumOfTabs, Bundle bundle) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.bundle = bundle;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                Fragment_GroupPlanList tab1 = newGroupPlanListInstance("current");
                return tab1;
            case 1:
                Fragment_GroupPlanList tab2 = newGroupPlanListInstance("past");
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "Current";
            case 1: return "Past";
            default: return null;
        }
    }

    private Fragment_GroupPlanList newGroupPlanListInstance(String type) {
        Fragment_GroupPlanList myFragment = new Fragment_GroupPlanList();

        Group group = bundle.getParcelable("group");
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putParcelable("group", group);
        myFragment.setArguments(args);

        return myFragment;
    }

}
