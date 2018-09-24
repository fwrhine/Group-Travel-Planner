package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.example.pplki18.grouptravelplanner.LoginActivity;

import java.util.HashMap;

/**
 * Class that stores and loads session data
 * for other activities that needs a quick reference
 * of the user's information (e.g. username, isLoggedIn).
 */
public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    public static final String PREF_NAME = "mypref";

    // User id
    public static final String KEY_ID = "id";

    // User name
    public static final String KEY_USERNAME = "username";

    // User full name
    public static final String KEY_FULLNAME = "fullname";

    // Log-in status
    public static final String IS_LOGIN = "isLoggedIn";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // Gender
    public static final String KEY_GENDER = "gender";

    // Phone number
    public static final String KEY_PHONE = "phone_no";

    // Birthday
    public static final String KEY_BIRTHDAY = "birthday";

    // Trip status
    public static final String IS_ON_TRIP = "isOnTrip";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String id, String name, String email, String gender,
                                   String phone_no, String birthday){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing user id in pref
        editor.putString(KEY_ID, id);

        // Storing name in pref
        editor.putString(KEY_USERNAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // Storing gender in pref
        editor.putString(KEY_GENDER, gender);

        // Storing phone number in pref
        editor.putString(KEY_PHONE, phone_no);

        // Storing birthday in pref
        editor.putString(KEY_BIRTHDAY, birthday);

        // commit changes
        editor.commit();
    }

    /**
     * Set user status ON TRIP
     */
    public void setOnTrip(boolean status){
        // Storing on trip value
        editor.putBoolean(IS_ON_TRIP, status);
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void goToLogin(){
        // user is not logged in redirect him to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }


    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user id
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // user name
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        // user full name
        user.put(KEY_FULLNAME, pref.getString(KEY_FULLNAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // user gender
        user.put(KEY_GENDER, pref.getString(KEY_GENDER, null));

        // user phone number
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));

        // user birthday
        user.put(KEY_BIRTHDAY, pref.getString(KEY_BIRTHDAY, null));

        // user trip status (TYPE IS STRING)
        user.put(IS_ON_TRIP, pref.getString(IS_ON_TRIP, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Quick check for trip status
     * **/
    // Get Login State
    public boolean isOnTrip(){
        return pref.getBoolean(IS_ON_TRIP, false);
    }

    public void setFullName(String fullname){
        editor.putString(KEY_FULLNAME, fullname);
        editor.commit();
    }

    public void updateSession(String key, String value) {
        editor.putString(key, value);
        // commit changes
        editor.commit();
    }
}
