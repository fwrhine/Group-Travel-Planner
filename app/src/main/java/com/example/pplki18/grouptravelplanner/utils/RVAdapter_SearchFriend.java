package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter_SearchFriend extends RecyclerView.Adapter<RVAdapter_SearchFriend.SearchFriendViewHolder> {

    private List<User> friends;
    private List<String> friendIDs = new ArrayList<>();
    private Context context;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference userRef;

    public RVAdapter_SearchFriend(List<User> friends, Context context){
        this.friends = friends;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseUser = firebaseAuth.getCurrentUser();
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.userRef = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());
        this.context = context;

        getAllFriendIDs(new SearchFriendCallback() {
            @Override
            public void onCallback(List<String> list) {
                friendIDs = list;
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public SearchFriendViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_row, viewGroup, false);
        SearchFriendViewHolder pvh = new SearchFriendViewHolder(v);

        pvh.itemView.findViewById(R.id.add_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CLICKED", "TRUE");
                addFriend(friends.get(i).getId());
                updateFriendSession(friends.get(i).getId());
                Toast.makeText(context, "Friend added!", Toast.LENGTH_SHORT);
            }
        });

        return pvh;
    }

    @Override
    public void onBindViewHolder(SearchFriendViewHolder searchFriendViewHolder, int i) {
        final User friend = friends.get(i);
        searchFriendViewHolder.tvUsername.setText("@"+friends.get(i).getUsername());
        searchFriendViewHolder.tvFullname.setText(friends.get(i).getFullName());

        if(!friend.getPhotoUrl().equals("none")) {
            Uri uri = Uri.parse(friend.getPhotoUrl());

            GlideApp.with(searchFriendViewHolder.tvDisplayPicture.getContext())
                    .load(uri)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .centerCrop()
                    .apply(new RequestOptions().override(300, 300))
                    .apply(RequestOptions.circleCropTransform())
                    .into(searchFriendViewHolder.tvDisplayPicture);
        }
        else{
            searchFriendViewHolder.tvDisplayPicture.setImageResource(R.mipmap.ic_launcher);
        }
    }

    private void getAllFriendIDs(final SearchFriendCallback searchFriendCallback){
        userRef.child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendIDs.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String groupId = postSnapshot.getValue(String.class); // String of groupID
                    friendIDs.add(groupId);
                }
                searchFriendCallback.onCallback(friendIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addFriend(final String newFriendID){
        final DatabaseReference friendsRef = userRef.child("friends");
        final List<String> friends = new ArrayList<>();

        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friends.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String obtainedID = postSnapshot.getValue(String.class);
                    if(!obtainedID.equals(newFriendID)){
                        friends.add(obtainedID);
                    }
                }
                Log.d("FRIEND_ID", newFriendID);
                friends.add(newFriendID);
                friendsRef.setValue(friends);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateFriendSession(String friendId){
        // TODO: Masukin friendID yang baru di-add ke dalam session
    }

    public static class SearchFriendViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvFullname;
        private ImageView tvDisplayPicture;
        private Button addFriendButton;

        SearchFriendViewHolder(View itemView) {
            super(itemView);
            tvUsername = (TextView) itemView.findViewById(R.id.username);
            tvFullname = (TextView) itemView.findViewById(R.id.fullname);
            tvDisplayPicture = (ImageView) itemView.findViewById(R.id.profile_image);
            addFriendButton = (Button) itemView.findViewById(R.id.add_friend);
        }
    }

    private interface SearchFriendCallback {
        void onCallback(List<String> list);
    }
}

