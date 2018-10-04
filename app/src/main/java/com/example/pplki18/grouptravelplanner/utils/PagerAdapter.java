package com.example.pplki18.grouptravelplanner.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.pplki18.grouptravelplanner.BookPlaneFragment;
import com.example.pplki18.grouptravelplanner.BookTrainFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    public PagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new BookPlaneFragment();
            case 1: return new BookTrainFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "Airplane";
            case 1: return "Train";
            default: return null;
        }
    }
}
