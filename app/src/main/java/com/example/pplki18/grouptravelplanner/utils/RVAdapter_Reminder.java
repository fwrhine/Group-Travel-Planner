package com.example.pplki18.grouptravelplanner.utils;

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
        reminderViewHolder.destination.setText(reminderList.get(i).getDestination());
        reminderViewHolder.date.setText((reminderList.get(i).getDate()));
        reminderViewHolder.eventid.setText(reminderList.get(i).getEventID().toString());
//        reminderViewHolder.alarmchannel.setText(reminderList.get(i).getAlarmChannel());


/*        Calendar cal = Calendar.getInstance();
        Uri eventUri = CalendarContract.Events.CONTENT_URI;
        CalendarContract.Events._ID;
        eventUri.*/

        reminderViewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRem(reminderViewHolder);
            }
        });

        reminderViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO method to remove from calendar
                long eventID = Long.parseLong(reminderViewHolder.eventid.getText().toString());
                if(context instanceof Activity_CreateReminder){
                    ((Activity_CreateReminder)context).deleteEventFromCalendar(eventID);
                }
                Toast.makeText(context, "Deleted event", Toast.LENGTH_SHORT).show();
                //====================================
//                removeRem(reminderViewHolder);
            }
        });
    }

    public void removeRem(ReminderViewHolder viewHolder) {
        myDb = new DatabaseHelper(context);
        SQLiteDatabase db = myDb.getReadableDatabase();
        sessionManager = new SessionManager(context);
        HashMap<String, String> user = sessionManager.getUserDetails();

        String where = ReminderContract.ReminderEntry.COL_EVENT_ID + " = " + viewHolder.eventid.getText().toString() +
                " AND " + ReminderContract.ReminderEntry.COL_USER_ID + " = " + user.get(SessionManager.KEY_ID);
        db.delete(ReminderContract.ReminderEntry.TABLE_NAME, where, null );
        Log.d("RemoveRem", "Removed Friend");
        Toast.makeText(context, "Removed friend", Toast.LENGTH_SHORT).show();
    }

    /** user defined method to delete the event based on id*/
    private int deleteEventFromCalendar(ContentResolver cr, long id){
        Uri eventUri = Uri.parse("content://com.android.calendar/events");  // or
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(eventUri, id);
        int rows = cr.delete(deleteUri, null, null);
// System.out.println("Rows deleted: " + rows);
        return rows;
    }


    public void editRem(ReminderViewHolder viewHolder) {
        //TODO go to edit page

        Intent myIntent = new Intent(this.context, Activity_EditReminder.class);
        Long id = Long.parseLong(viewHolder.eventid.getText().toString());
        myIntent.putExtra("event_id", id);
        context.startActivity(myIntent);
/*        String evId = viewHolder.eventid.getText().toString();
        long id = Long.parseLong(evId);

        long rowID = 760;
        Uri uri = ContentUris.withAppendedId(
                CalendarContract.Events.CONTENT_URI, rowID);
        Intent intent = new Intent(Intent.ACTION_EDIT, uri);
        // Modify the calendar event details
        Calendar startTime = Calendar.getInstance();
        startTime.set(2014, 2, 13, 0, 30);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                startTime.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
        // Use the Calendar application to edit the event.
        startActivity(intent);
        Toast.makeText(this, "Editing done", Toast.LENGTH_SHORT).show();*/
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

