package com.example.pplki18.grouptravelplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.pplki18.grouptravelplanner.InGroupActivity;
import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.data.User;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter_Group extends RecyclerView.Adapter<RVAdapter_Group.GroupViewHolder>{

    List<Group> groups;
    Context context;

    public RVAdapter_Group(List<Group> groups, Context context){
        this.groups = groups;
        this.context = context;
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
    public void onBindViewHolder(final GroupViewHolder groupViewHolder, final int i) {
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
                Toast.makeText(context, "CLICKED " + i, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, InGroupActivity.class);
                intent.putExtra("group", group);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void insertGroup(Group group) {
        groups.add(group);
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView groupName;
        ImageView groupImage;
        ImageView memberImg1;
        ImageView memberImg2;
        ImageView memberImg3;
        ImageView memberImg4;

        GroupViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cv);
            groupName = (TextView)itemView.findViewById(R.id.group_name);
            groupImage = (ImageView)itemView.findViewById(R.id.group_image);
            memberImg1 = (ImageView) itemView.findViewById(R.id.user_image1);
            memberImg2 = (ImageView) itemView.findViewById(R.id.user_image2);
            memberImg3 = (ImageView) itemView.findViewById(R.id.user_image3);
            memberImg4 = (ImageView) itemView.findViewById(R.id.user_image4);
        }

    }
}

