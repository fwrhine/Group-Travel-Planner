package com.example.pplki18.grouptravelplanner;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class TravelApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
