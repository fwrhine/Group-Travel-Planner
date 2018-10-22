package com.example.pplki18.grouptravelplanner.utils;

import java.util.ArrayList;
import java.util.List;

public class Plan {

    String plan_id;
    String userId;
    String plan_name;
    String plan_start_date;
    String plan_end_date;
    int plan_total_days;
    String plan_overview;
    String plan_modified;
    String plan_created;
    List<Event> events;

    //constructor
    public Plan() {
    }

    public Plan(String plan_id, String plan_name, String userId) {
        this.plan_id = plan_id;
        this.plan_name = plan_name;
        this.userId = userId;
        events = new ArrayList<>();
//        this.user_image = user_image;

    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public String getPlan_start_date() {
        return plan_start_date;
    }

    public void setPlan_start_date(String plan_start_date) {
        this.plan_start_date = plan_start_date;
    }

    public String getPlan_end_date() {
        return plan_end_date;
    }

    public void setPlan_end_date(String plan_end_date) {
        this.plan_end_date = plan_end_date;
    }

    public int getPlan_total_days() {
        return plan_total_days;
    }

    public void setPlan_total_days(int plan_total_days) {
        this.plan_total_days = plan_total_days;
    }

    public String getPlan_overview() {
        return plan_overview;
    }

    public void setPlan_overview(String plan_overview) {
        this.plan_overview = plan_overview;
    }

    public String getPlan_modified() {
        return plan_modified;
    }

    public void setPlan_modified(String plan_modified) {
        this.plan_modified = plan_modified;
    }

    public String getPlan_created() {
        return plan_created;
    }

    public void setPlan_created(String plan_created) {
        this.plan_created = plan_created;
    }
}
