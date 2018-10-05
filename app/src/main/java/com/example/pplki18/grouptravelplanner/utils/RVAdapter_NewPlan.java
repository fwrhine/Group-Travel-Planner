package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;
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

        String time_start = format.format(event.getTime_start());

        String timeString = time_start + " - " + format.format(event.getTime_end()) +
                " (" + event.getTotal_time() + ")";
        holder.eventTime.setText(time_start);
        holder.eventTitle.setText(event.getTitle());
        holder.eventTimeDetail.setText(timeString);

//        if(timeLineModel.getStatus() == OrderStatus.INACTIVE) {
//            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
//        } else if(timeLineModel.getStatus() == OrderStatus.ACTIVE) {
//            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorPrimary));
//        } else {
//            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.colorPrimary));
//        }
//
//        if(!timeLineModel.getDate().isEmpty()) {
//            holder.mDate.setVisibility(View.VISIBLE);
//            holder.mDate.setText(DateTimeUtils.parseDateTime(timeLineModel.getDate(), "yyyy-MM-dd HH:mm", "hh:mm a, dd-MMM-yyyy"));
//        }
//        else
//            holder.mDate.setVisibility(View.GONE);
//
//        holder.mMessage.setText(timeLineModel.getMessage());
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

        NewPlanViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cv_event);
            eventTitle = (TextView) itemView.findViewById(R.id.item_title);
            eventTime = (TextView) itemView.findViewById(R.id.item_time);
            eventTimeDetail = (TextView) itemView.findViewById(R.id.item_time_detail);
        }
    }

}
