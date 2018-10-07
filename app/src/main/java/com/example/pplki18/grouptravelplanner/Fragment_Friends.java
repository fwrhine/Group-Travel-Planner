package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.data.User;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Friend;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class Fragment_Friends extends Fragment {

//    private Button to_search_friend;    // TEMP - nopal

    private static final String TAG = "ListFriendActivity";

    private RecyclerView recyclerViewGroup;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton to_search_friend;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference userRef;

    List<User> friends = new ArrayList<>();
    List<String> friendIDs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Chat");
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userRef = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());

        init();
        setAddFriendButton();

        recyclerViewGroup.setHasFixedSize(true);
        recyclerViewGroup.setLayoutManager(linearLayoutManager);

        progressBar.setVisibility(View.VISIBLE);
        getAllFriendIDs(new FriendIdCallback() {
            @Override
            public void onCallback(List<String> list) {
                friendIDs = list;
                populateFriendRecyclerView();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    // TEMP - nopal
    public void setAddFriendButton() {
        to_search_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("TEMP","To Search Friend");
                Intent myIntent = new Intent(getActivity(), SearchBarActivity.class);
                Fragment_Friends.this.startActivity(myIntent);
            }
        });
    }


    private void init() {
        toolbar = getView().findViewById(R.id.toolbar);
        recyclerViewGroup = (RecyclerView) getView().findViewById(R.id.rv2);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        to_search_friend = getView().findViewById(R.id.to_search_friend);
        progressBar = getView().findViewById(R.id.progress_loader);
    }

    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populateFriendRecyclerView() {
        Log.d(TAG, "populateFriendRecyclerView: Displaying list of friends in the ListView.");

        //get data and append to list
        getAllFriends(new FriendCallback() {
            @Override
            public void onCallback(List<User> list) {
                Log.d("NUM_OF_FRIEND", list.size() + ".");
                RVAdapter_Friend adapter = new RVAdapter_Friend(list, getActivity());
                recyclerViewGroup.setAdapter(adapter);
            }
        });
    }

    public void getAllFriendIDs(final FriendIdCallback friendIdCallback){
        userRef.child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendIDs.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String groupId = postSnapshot.getValue(String.class); // String of groupID
                    friendIDs.add(groupId);
                }
                friendIdCallback.onCallback(friendIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getAllFriends(final FriendCallback friendCallback){
        firebaseDatabase.getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friends.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    User friend = postSnapshot.getValue(User.class); // User Objects
                    if(friendIDs.contains(friend.getId())){
                        friends.add(friend);
                    }
                }
                friendCallback.onCallback(friends);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private interface FriendIdCallback{
        void onCallback(List<String> list);
    }

    private interface FriendCallback{
        void onCallback(List<User> list);
    }
}
