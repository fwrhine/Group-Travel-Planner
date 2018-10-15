package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.Reminder;

import java.util.List;

public class RVAdapter_Reminder extends RecyclerView.Adapter<RVAdapter_Reminder.ReminderViewHolder>{

    List<Reminder> reminderList;
    Context context;

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
    public void onBindViewHolder(RVAdapter_Reminder.ReminderViewHolder reminderViewHolder, int i) {
        reminderViewHolder.destination.setText(reminderList.get(i).getDestination());
        reminderViewHolder.date.setText((reminderList.get(i).getDate()));

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView destination;
        TextView date;

        ReminderViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cvReminder);
            destination = (TextView)itemView.findViewById(R.id.destination_adap);
            date = (TextView)itemView.findViewById(R.id.date_adap);

        }

    }
}

