package com.example.pplki18.grouptravelplanner.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.pplki18.grouptravelplanner.Fragment_CustomEvent;
import com.example.pplki18.grouptravelplanner.Fragment_PlaceList;
import com.google.android.gms.maps.model.LatLng;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String region;
    LatLng region_coor;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, String region, LatLng region_coor) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.region = region;
        this.region_coor = region_coor;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment_PlaceList tab1 = newInstance("restaurants");
                return tab1;
            case 1:
                Fragment_PlaceList tab2 = newInstance("attractions");
                return tab2;
            case 2:
                Fragment_CustomEvent tab3 = new Fragment_CustomEvent();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public Fragment_PlaceList newInstance(String type) {
        Fragment_PlaceList myFragment = new Fragment_PlaceList();

        Bundle args = new Bundle();
        args.putString("QUERY", type);
        args.putString("REGION", region);
        args.putString("LATITUDE", String.valueOf(region_coor.latitude));
        args.putString("LONGITUDE", String.valueOf(region_coor.longitude));

        myFragment.setArguments(args);

        return myFragment;
    }
}
