package com.example.pplki18.grouptravelplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.example.pplki18.grouptravelplanner.BookPlaneFragment;
import com.example.pplki18.grouptravelplanner.BookTrainFragment;


import com.example.pplki18.grouptravelplanner.Fragment_CustomEvent;
import com.example.pplki18.grouptravelplanner.Fragment_PlaceList;
import com.google.android.gms.maps.model.LatLng;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String region;
    LatLng region_coor;
    Bundle bundle;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, String region, LatLng region_coor, Bundle bundle) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.region = region;
        this.region_coor = region_coor;
        this.bundle = bundle;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        int plan_id = bundle.getInt("plan_id");
        args.putInt("plan_id", plan_id);

        switch (position) {

            case 0:
                Fragment_PlaceList tab1 = newInstance("restaurants");
                return tab1;
            case 1:
                Fragment_PlaceList tab2 = newInstance("attractions");
                return tab2;
            case 2:
                BookPlaneFragment myFragment = new BookPlaneFragment();
                myFragment.setArguments(bundle);
                return myFragment;
            case 3:
                BookTrainFragment myFragment2 = new BookTrainFragment();
                myFragment2.setArguments(bundle);
                return myFragment2;
            case 4: Fragment_CustomEvent tab3 = new Fragment_CustomEvent();
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

    public Fragment_PlaceList newInstance(String type) {
        Fragment_PlaceList myFragment = new Fragment_PlaceList();

        Bundle args = new Bundle();
        args.putString("QUERY", type);
        args.putString("REGION", region);
        args.putString("LATITUDE", String.valueOf(region_coor.latitude));
        args.putString("LONGITUDE", String.valueOf(region_coor.longitude));

        int plan_id = bundle.getInt("plan_id");
        String date = bundle.getString("date");
//        Log.d("PLANID", plan_id+ "");
        args.putInt("plan_id", plan_id);
        args.putString("date", date);

        myFragment.setArguments(args);

        return myFragment;
    }

}
