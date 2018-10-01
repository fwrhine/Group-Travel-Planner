package com.example.pplki18.grouptravelplanner.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.pplki18.grouptravelplanner.Fragment_EntertainmentList;
import com.example.pplki18.grouptravelplanner.Fragment_RestaurantList;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment_RestaurantList tab1 = new Fragment_RestaurantList();
                return tab1;
            case 1:
                Fragment_EntertainmentList tab2 = new Fragment_EntertainmentList();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
