package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.User;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_SearchFriend;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
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
import java.util.HashMap;
import java.util.List;

public class SearchBarActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    Toolbar toolbar;
    RecyclerView searchItems;
    LinearLayoutManager linearLayoutManager;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userRef;
    private DatabaseReference groupRef;
    private StorageReference storageReference;

    List<User> result = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userRef = firebaseDatabase.getReference().child("users");
        groupRef = firebaseDatabase.getReference().child("groups");
        storageReference = FirebaseStorage.getInstance().getReference();

        myDb = new DatabaseHelper(this);
        searchItems = (RecyclerView) findViewById(R.id.list);

        SearchView searchView = (SearchView) findViewById(R.id.search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Add Friend");

        linearLayoutManager = new LinearLayoutManager(this);
        searchItems.setHasFixedSize(true);
        searchItems.setLayoutManager(linearLayoutManager);

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        final String currUsername = user.get(SessionManager.KEY_USERNAME);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals(null)) {
                    searchData(newText.toString(), new FriendCallback() {
                        @Override
                        public void onCallback(List<User> list) {
                            Log.d("LIST_SIZE", list.size() + ".");
                            RVAdapter_SearchFriend adapter = new RVAdapter_SearchFriend(list, SearchBarActivity.this);
                            searchItems.setAdapter(adapter);
                        }
                    });

                    Log.d("QUERY", "START_SEARCH");
                }
                return true;
            }
        });
    }

    public void searchData(final String query, final FriendCallback friendCallback) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                result.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    if(user.getUsername().toLowerCase().equals(query.toLowerCase()) && !user.getId().equals(firebaseUser.getUid())){
                        result.add(user);
                        Log.d("FRIEND_USERNAME", user.getUsername());
                    }
                }
                friendCallback.onCallback(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void setUpToolbar() {
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent intent = new Intent(SearchBarActivity.this, Activity_InHome.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
                        onBackPressed();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_home,
//                                new Fragment_Friends()).commit();
                    }
                }
        );
    }

    private interface FriendCallback{
        void onCallback(List<User> list);
    }


}
