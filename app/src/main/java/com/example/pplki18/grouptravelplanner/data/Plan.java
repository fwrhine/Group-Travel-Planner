package com.example.pplki18.grouptravelplanner.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Plan implements Parcelable {

    private String plan_id;
    private String creatorId;
    private String plan_name;
    private String plan_start_date;
    private String plan_end_date;
    private int plan_total_days;
    private String plan_overview;
    private String plan_modified;
    private String plan_created;
    private List<String> events;

    //constructor
    public Plan() {
    }

    public Plan(String plan_id, String plan_name, String creatorId) {
        this.plan_id = plan_id;
        this.plan_name = plan_name;
        this.creatorId = creatorId;
        events = new ArrayList<>();
//        this.user_image = user_image;

    }

    protected Plan(Parcel in) {
        plan_id = in.readString();
        creatorId = in.readString();
        plan_name = in.readString();
        plan_start_date = in.readString();
        plan_end_date = in.readString();
        plan_total_days = in.readInt();
        plan_overview = in.readString();
        plan_modified = in.readString();
        plan_created = in.readString();
        events = in.createStringArrayList();
    }

    public static final Creator<Plan> CREATOR = new Creator<Plan>() {
        @Override
        public Plan createFromParcel(Parcel in) {
            return new Plan(in);
        }

        @Override
        public Plan[] newArray(int size) {
            return new Plan[size];
        }
    };

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
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

    public List<String> getEvents() {
        return this.events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(plan_id);
        parcel.writeString(creatorId);
        parcel.writeString(plan_name);
        parcel.writeString(plan_start_date);
        parcel.writeString(plan_end_date);
        parcel.writeInt(plan_total_days);
        parcel.writeString(plan_overview);
        parcel.writeString(plan_modified);
        parcel.writeString(plan_created);
        parcel.writeStringList(events);
    }
}
