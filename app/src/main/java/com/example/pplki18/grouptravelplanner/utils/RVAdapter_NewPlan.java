package com.example.pplki18.grouptravelplanner.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.PlaceActivity;
import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.EventContract;
import com.github.vipulasri.timelineview.TimelineView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class RVAdapter_NewPlan extends RecyclerView.Adapter<RVAdapter_NewPlan.NewPlanViewHolder> {

    private List<Event> events;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public RVAdapter_NewPlan(List<Event> events, Context context) {
        this.events = events;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public NewPlanViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;

        view = mLayoutInflater.inflate(R.layout.item_timeline, viewGroup, false);

        return new NewPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewPlanViewHolder holder, int position) {

        Event event = events.get(position);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        String time_start = event.getTime_start();
        String timeString = time_start + " - " + event.getTime_end() +
                " (" + event.getTotal_time() + ")";
        if(event.getType() != null) {
            if (event.getType().equals("restaurants")) {
                holder.eventIcon.setMarker(mContext.getDrawable(R.drawable.ic_restaurant_black));
            } else if (event.getType().equals("attractions")) {
                holder.eventIcon.setMarker(mContext.getDrawable(R.drawable.ic_sunny_black));
            }
        }

        holder.eventTime.setText(time_start);
        holder.eventTitle.setText(event.getTitle());
        holder.eventTimeDetail.setText(timeString);

        setEventDetailOnClick(holder, position);
        setCardViewLongClick(holder, position, event.getTitle());
    }

    public void setEventDetailOnClick(final NewPlanViewHolder holder, final int position) {
        holder.eventDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PlaceActivity.class);
                Event anEvent = events.get(position);
                intent.putExtra("PLACE_ID", anEvent.getQuery_id());
                Log.d("DATE", anEvent.getDate());
                intent.putExtra("ACTIVITY", "PlanActivity");
                intent.putExtra("date", anEvent.getDate());
                intent.putExtra("time_start", anEvent.getTime_start());
                intent.putExtra("time_end", anEvent.getTime_end());
                intent.putExtra("duration", anEvent.getTotal_time());
                intent.putExtra("event_id", anEvent.getEvent_id());
                intent.putExtra("type", anEvent.getType());
                mContext.startActivity(intent);
            }
        });
    }

    public void setCardViewLongClick(final NewPlanViewHolder holder, final int position,
                                     final String title) {
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.cardView);
                //inflating menu from xml resource
                popup.inflate(R.menu.event_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        AlertDialog box;
                        switch (item.getItemId()) {
                            case R.id.delete_event:
                                box = deleteConfirmation(position, title);
                                box.show();
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
                return true;
            }
        });
    }

    private AlertDialog deleteConfirmation(final int position, String title) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete " + title + "?")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deleteEvent(events.get(position));
                        events.remove(position);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    private void deleteEvent(Event event) {
        DatabaseHelper myDb = new DatabaseHelper(mContext);
        SQLiteDatabase db = myDb.getWritableDatabase();

        if(event.getEvent_id() != 0) {
            String deleteQuery = "DELETE FROM " + EventContract.EventEntry.TABLE_NAME + " WHERE " +
                    EventContract.EventEntry._ID + " = " + event.getEvent_id();
            db.execSQL(deleteQuery);
            db.close();
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (events !=null? events.size():0);
    }

    public static class NewPlanViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView eventTitle;
        TextView eventTimeDetail;
        TextView eventTime;
        TextView eventDetail;
        TimelineView eventIcon;

        NewPlanViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cv_event);
            eventTitle = (TextView) itemView.findViewById(R.id.item_title);
            eventTime = (TextView) itemView.findViewById(R.id.item_time);
            eventTimeDetail = (TextView) itemView.findViewById(R.id.item_time_detail);
            eventDetail = (TextView) itemView.findViewById(R.id.item_detail);
            eventIcon = (TimelineView) itemView.findViewById(R.id.time_marker);
        }
    }

}
