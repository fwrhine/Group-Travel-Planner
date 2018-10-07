package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.Group;
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

public class Fragment_GroupList extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ListGroupActivity";

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference userRef;
    DatabaseReference groupRef;
    StorageReference storageReference;

    DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewGroup;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private List<String> groupIDs = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_group_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userRef = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());
        groupRef = firebaseDatabase.getReference().child("groups");
        storageReference = FirebaseStorage.getInstance().getReference();

        init();

        //FAB: when clicked, open create new group interface
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("fab", "FAB Clicked");
                Intent myIntent = new Intent(getActivity(), Activity_CreateNewGroup.class);
                Fragment_GroupList.this.startActivity(myIntent);
            }
        });


        recyclerViewGroup.setHasFixedSize(true);
        recyclerViewGroup.setLayoutManager(linearLayoutManager);

        progressBar.setVisibility(View.VISIBLE);

        // TODO: TIME COMPLEXITY bakal meledak kalo jumlah group+user banyak
        getAllGroupIDs(new UserIdCallback() {
            @Override
            public void onCallback(List<String> list) {
                groupIDs = list;
                populateGroupRecyclerView();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populateGroupRecyclerView() {
        Log.d(TAG, "populateGroupRecyclerView: Displaying list of groups in the ListView.");

        //get data and append to list
        getAllGroups(new GroupCallback() {
            @Override
            public void onCallback(List<Group> list) {
                Log.d("NUM_OF_GROUP", list.size() + ".");
                RVAdapter_Group adapter = new RVAdapter_Group(list, getActivity());
                recyclerViewGroup.setAdapter(adapter);
            }
        });

    }

    /*
     * Get all groups
     * */
    public void getAllGroups(final GroupCallback groupCallback){
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groups.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Group group = postSnapshot.getValue(Group.class); // Group Objects
                        if(groupIDs.contains(group.getGroup_id())){
                            groups.add(group);
                        }
                }
                groupCallback.onCallback(groups);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        recyclerViewGroup = (RecyclerView) getView().findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        databaseHelper = new DatabaseHelper(getActivity());
        fab = getView().findViewById(R.id.fab);
        progressBar = getView().findViewById(R.id.progress_loader);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void getAllGroupIDs(final UserIdCallback userIdCallback){
        userRef.child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupIDs.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String groupId = postSnapshot.getValue(String.class); // String of groupID
                    groupIDs.add(groupId);
                }
                userIdCallback.onCallback(groupIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private interface UserIdCallback {
        void onCallback(List<String> list);
    }
    private interface GroupCallback{
        void onCallback(List<Group> list);
    }
}
