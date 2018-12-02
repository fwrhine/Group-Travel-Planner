package com.example.pplki18.grouptravelplanner;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.data.User;
import com.example.pplki18.grouptravelplanner.utils.Chat.MemberHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class GroupMemberActivity extends AppCompatActivity {

    private RecyclerView memberRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Group group;

    private FirebaseDatabase firebaseDatabase;
    private Query memberQuery;
    private DatabaseReference userRef;

    private FirebaseRecyclerOptions<User> options;
    private FirebaseRecyclerAdapter<User, MemberHolder> fbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);
        init();
        firebaseDatabase = FirebaseDatabase.getInstance();
        memberQuery = firebaseDatabase.getReference().child("groups").child(group.getGroup_id()).child("members");
        userRef = firebaseDatabase.getReference().child("users");

        Log.d("memberQuery", memberQuery.getRef().getRef().toString());
        Log.d("userRef", userRef.getRef().toString());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        memberRecyclerView.setLayoutManager(linearLayoutManager);

        options = new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(memberQuery.getRef(), userRef, User.class)
                .setLifecycleOwner(this)
                .build();

        fbAdapter = new FirebaseRecyclerAdapter<User, MemberHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MemberHolder holder, int position, @NonNull User model) {
                Log.d("Binding_model_for ", model.getId());
                holder.bind(model);
            }

            @NonNull
            @Override
            public MemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MemberHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_group_friend, parent, false), group.getGroup_id());
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        fbAdapter.startListening();
        Log.d("Adapter_size", fbAdapter.getItemCount() + "");
        memberRecyclerView.setAdapter(fbAdapter);
    }

    private void init() {
        memberRecyclerView = findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
        group = getIntent().getParcelableExtra("group");
    }
}
