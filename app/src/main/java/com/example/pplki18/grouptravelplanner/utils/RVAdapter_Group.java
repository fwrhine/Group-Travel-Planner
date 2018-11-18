package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.pplki18.grouptravelplanner.InGroupActivity;
import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter_Group extends RecyclerView.Adapter<RVAdapter_Group.GroupViewHolder>{

    private final List<Group> groups;
    private final Context context;

    public RVAdapter_Group(List<Group> groups, Context context){
        this.groups = groups;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_group, viewGroup, false);
        return new GroupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupViewHolder groupViewHolder, int i) {
        final Group group = groups.get(i);
        groupViewHolder.groupName.setText(group.getGroup_name());

        if(!group.getGroup_image_url().equals("none")) {
            Uri uri = Uri.parse(group.group_image_url);

            GlideApp.with(groupViewHolder.groupImage.getContext())
                    .load(uri)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .centerCrop()
                    .apply(new RequestOptions().override(300, 300))
                    .apply(RequestOptions.circleCropTransform())
                    .into(groupViewHolder.groupImage);
        }
        else{
            groupViewHolder.groupImage.setImageResource(R.mipmap.ic_launcher);
        }

        final List<ImageView> memberImageViews = new ArrayList<>();
        memberImageViews.add(groupViewHolder.memberImg1);
        memberImageViews.add(groupViewHolder.memberImg2);
        memberImageViews.add(groupViewHolder.memberImg3);
        memberImageViews.add(groupViewHolder.memberImg4);

        final int[] index = {0};
        for(String memberID : group.getFirstMembers()){
            final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(memberID);
            userRef.child("fullName").getParent().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user != null && !user.getPhotoUrl().equals("none")){
                        Uri uri = Uri.parse(user.photoUrl);

                        GlideApp.with(memberImageViews.get(index[0]).getContext())
                                .load(uri)
                                .placeholder(R.mipmap.ic_launcher_round)
                                .error(R.mipmap.ic_launcher_round)
                                .centerCrop()
                                .apply(new RequestOptions().override(300, 300))
                                .apply(RequestOptions.circleCropTransform())
                                .into(memberImageViews.get(index[0]));
                    }
                    else{
                        memberImageViews.get(index[0]).setImageResource(R.drawable.user_pic);
                    }
                    index[0]++;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        groupViewHolder.itemView.findViewById(R.id.cv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "CLICKED " + groupViewHolder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, InGroupActivity.class);
                intent.putExtra("group", group);
                context.startActivity(intent);
            }
        });

    }

// --Commented out by Inspection START (11/12/18, 1:09 PM):
//    public void insertGroup(Group group) {
//        groups.add(group);
//    }
// --Commented out by Inspection STOP (11/12/18, 1:09 PM)

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        final CardView cardView;
        final TextView groupName;
        final ImageView groupImage;
        final ImageView memberImg1;
        final ImageView memberImg2;
        final ImageView memberImg3;
        final ImageView memberImg4;

        GroupViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cv);
            groupName = itemView.findViewById(R.id.group_name);
            groupImage = itemView.findViewById(R.id.group_image);
            memberImg1 = itemView.findViewById(R.id.user_image1);
            memberImg2 = itemView.findViewById(R.id.user_image2);
            memberImg3 = itemView.findViewById(R.id.user_image3);
            memberImg4 = itemView.findViewById(R.id.user_image4);
        }

    }
}

