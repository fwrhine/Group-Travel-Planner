package com.example.pplki18.grouptravelplanner.utils;

import com.android.volley.VolleyError;

public interface VolleyResponseListener {
    void onError(VolleyError error);

    void onResponse(Object response);
}