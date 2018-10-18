package com.example.pplki18.grouptravelplanner.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.pplki18.grouptravelplanner.BookPlaneFragment;
import com.example.pplki18.grouptravelplanner.BookTrainFragment;


import com.example.pplki18.grouptravelplanner.Fragment_CustomEvent;
import com.example.pplki18.grouptravelplanner.Fragment_PlaceList;
import com.google.android.gms.maps.model.LatLng;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private String region;
    private LatLng region_coor;
    private Bundle bundle;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, String region, LatLng region_coor, Bundle bundle) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.region = region;
        this.region_coor = region_coor;
        this.bundle = bundle;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                Fragment_PlaceList tab1 = newPlaceListInstance("restaurants");
                return tab1;
            case 1:
                Fragment_PlaceList tab2 = newPlaceListInstance("attractions");
                return tab2;
            case 2: return new BookPlaneFragment();
            case 3: return new BookTrainFragment();
            case 4: Fragment_CustomEvent tab3 = newCustomEventInstance();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "Restaurant";
            case 1: return "Attraction";
            case 2: return "Airplane";
            case 3: return "Train";
            case 4: return "Custom";
            default: return null;
        }
    }

    private Fragment_PlaceList newPlaceListInstance(String type) {
        Fragment_PlaceList myFragment = new Fragment_PlaceList();

        Bundle args = new Bundle();
        args.putString("QUERY", type);
        args.putString("REGION", region);
        args.putString("LATITUDE", String.valueOf(region_coor.latitude));
        args.putString("LONGITUDE", String.valueOf(region_coor.longitude));

        int plan_id = bundle.getInt("plan_id");
        String date = bundle.getString("date");
        args.putInt("plan_id", plan_id);
        args.putString("date", date);

        myFragment.setArguments(args);

        return myFragment;
    }

    private Fragment_CustomEvent newCustomEventInstance() {
        Fragment_CustomEvent myFragment = new Fragment_CustomEvent();

        Bundle args = new Bundle();

        int plan_id = bundle.getInt("plan_id");
        String date = bundle.getString("date");
        args.putInt("plan_id", plan_id);
        args.putString("date", date);

        myFragment.setArguments(args);

        return myFragment;
    }

}
