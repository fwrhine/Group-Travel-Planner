package com.example.pplki18.grouptravelplanner.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.Activity_CreateReminder;
import com.example.pplki18.grouptravelplanner.Activity_EditReminder;
import com.example.pplki18.grouptravelplanner.Fragment_Reminder;
import com.example.pplki18.grouptravelplanner.InHomeActivity;
import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.Reminder;
import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.ReminderContract;

import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class RVAdapter_Reminder extends RecyclerView.Adapter<RVAdapter_Reminder.ReminderViewHolder>{

    List<Reminder> reminderList;
    Context context;
    DatabaseHelper myDb;
    SessionManager sessionManager;

    public RVAdapter_Reminder(List<Reminder> reminderList, Context context){
        this.reminderList = reminderList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    @Override
    public RVAdapter_Reminder.ReminderViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_reminder, viewGroup, false);
        ReminderViewHolder rvh = new ReminderViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(final RVAdapter_Reminder.ReminderViewHolder reminderViewHolder, int i) {
        //TRYING TO USE CALENDAR EVENT TABLE
        String destinationText = reminderList.get(i).getDestination() + " at ";
        String dateText = reminderList.get(i).getDate().toString().substring(0, 16);

        reminderViewHolder.destination.setText(destinationText);
        reminderViewHolder.date.setText(dateText);
        reminderViewHolder.eventid.setText(reminderList.get(i).getEventID().toString());
//        reminderViewHolder.alarmchannel.setText(reminderList.get(i).getAlarmChannel());

        final long eventID = Long.parseLong(reminderList.get(i).getEventID().toString());
/*        Calendar cal = Calendar.getInstance();
        Uri eventUri = CalendarContract.Events.CONTENT_URI;
        CalendarContract.Events._ID;
        eventUri.*/

        reminderViewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRemTest(eventID);
            }
        });

        reminderViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // method to remove from calendar
                ((InHomeActivity) context).deleteEventFromCalendar(eventID);
                goBackToList();
                Toast.makeText(context, "Deleted event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goBackToList(){
        Intent myIntent = new Intent(this.context , InHomeActivity.class);
        myIntent.putExtra("fragment", "reminder");
        context.startActivity(myIntent);
    }

    public void editRemTest(Long eventID) {
        // go to edit page
        Intent myIntent = new Intent(this.context, Activity_EditReminder.class);
        myIntent.putExtra("event_id", eventID);
        context.startActivity(myIntent);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView destination;
        TextView date;
        TextView eventid;
        TextView alarmchannel;
        Button delete;
        Button edit;
//        Context context;

        ReminderViewHolder(View itemView) {
            super(itemView);
//            context = itemView.getContext();
            cardView = (CardView)itemView.findViewById(R.id.cvReminder);
            destination = (TextView)itemView.findViewById(R.id.destination_adap);
            date = (TextView)itemView.findViewById(R.id.date_adap);
            eventid = (TextView)itemView.findViewById(R.id.event_id2);
            alarmchannel = (TextView)itemView.findViewById(R.id.alarmchannel);
            delete = (Button)itemView.findViewById(R.id.remove_notification);
            edit = (Button)itemView.findViewById(R.id.edit_notification);

        }

    }
}

