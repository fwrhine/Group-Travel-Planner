package com.example.pplki18.grouptravelplanner.utils;

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

import java.util.List;

public class RVAdapter_Group extends RecyclerView.Adapter<RVAdapter_Group.GroupViewHolder>{

    List<Group> groups;

    public RVAdapter_Group(List<Group> groups){
        this.groups = groups;
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_group, viewGroup, false);
        GroupViewHolder pvh = new GroupViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(GroupViewHolder groupViewHolder, int i) {
        groupViewHolder.groupName.setText(groups.get(i).getGroup_name());

        byte[] byteArray = groups.get(i).getGroup_image();
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        groupViewHolder.groupImage.setImageBitmap(bmp);

        groupViewHolder.groupMembers.setText(groups.get(i).getGroup_members().toString());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView groupName;
        TextView groupMembers;
        ImageView groupImage;

        GroupViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cv);
            groupName = (TextView)itemView.findViewById(R.id.group_name);
            groupMembers = (TextView) itemView.findViewById(R.id.group_members);
            groupImage = (ImageView)itemView.findViewById(R.id.group_image);
        }

    }
}
