package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.User;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter_Friend extends RecyclerView.Adapter<RVAdapter_Friend.FriendViewHolder>{

    List<User> friends;
    Context context;

    public RVAdapter_Friend(List<User> friends, Context context){
        this.friends = friends;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_friend, viewGroup, false);
        FriendViewHolder pvh = new FriendViewHolder(v);

        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int i) {
        User friend = friends.get(i);
        holder.friendName.setText(friend.getFullName());
        if(!friend.getPhotoUrl().equals("none")) {
            Uri uri = Uri.parse(friend.getPhotoUrl());

            GlideApp.with(holder.friendImage.getContext())
                    .load(uri)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .centerCrop()
                    .apply(new RequestOptions().override(300, 300))
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.friendImage);
        }
        else {
            holder.friendImage.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView friendImage;
        TextView friendName;

        FriendViewHolder(View itemView){
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cv);
            friendImage = (ImageView) itemView.findViewById(R.id.friend_image);
            friendName = (TextView) itemView.findViewById(R.id.friend_name);
        }
    }

}
