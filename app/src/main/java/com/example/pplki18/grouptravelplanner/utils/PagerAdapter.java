package com.example.pplki18.grouptravelplanner.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.pplki18.grouptravelplanner.BookHotelFragment;
import com.example.pplki18.grouptravelplanner.BookPlaneFragment;
import com.example.pplki18.grouptravelplanner.BookTrainFragment;


import com.example.pplki18.grouptravelplanner.Fragment_CustomEvent;
import com.example.pplki18.grouptravelplanner.Fragment_PlaceList;
import com.google.android.gms.maps.model.LatLng;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private final int mNumOfTabs;
    private final String region;
    private final LatLng region_coordinates;
    private final Bundle bundle;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, String region, LatLng region_coordinates, Bundle bundle) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.region = region;
        this.region_coordinates = region_coordinates;
        this.bundle = bundle;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        int plan_id = bundle.getInt("plan_id");
        args.putInt("plan_id", plan_id);

        switch (position) {

            case 0:
                return newPlaceListInstance("restaurants");
            case 1:
                return newPlaceListInstance("attractions");
            case 2:
                BookHotelFragment tab3 = new BookHotelFragment();
                tab3.setArguments(bundle);
                return tab3;
            case 3:
                BookPlaneFragment tab4 = new BookPlaneFragment();
                tab4.setArguments(bundle);
                return tab4;
            case 4:
                BookTrainFragment tab5 = new BookTrainFragment();
                tab5.setArguments(bundle);
                return tab5;
            case 5:
                Fragment_CustomEvent tab6 = new Fragment_CustomEvent();
                tab6.setArguments(bundle);
                return tab6;
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
            case 2: return "Hotel";
            case 3: return "Airplane";
            case 4: return "Train";
            case 5: return "Custom";
            default: return null;
        }
    }

    private Fragment_PlaceList newPlaceListInstance(String type) {
        Fragment_PlaceList myFragment = new Fragment_PlaceList();

        Bundle args = new Bundle();
        args.putString("QUERY", type);
        args.putString("REGION", region);
        args.putString("LATITUDE", String.valueOf(region_coordinates.latitude));
        args.putString("LONGITUDE", String.valueOf(region_coordinates.longitude));

        int plan_id = bundle.getInt("plan_id");
        String date = bundle.getString("date");
        args.putInt("plan_id", plan_id);
        args.putString("date", date);

        myFragment.setArguments(args);

        return myFragment;
    }

}
