package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.Choice;
import com.example.pplki18.grouptravelplanner.data.Message;
import com.example.pplki18.grouptravelplanner.utils.Chat.ChatViewHolder;
import com.example.pplki18.grouptravelplanner.utils.ChoiceViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityPollChoiceTest extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference pollRef;
    private DatabaseReference choiceRef;
    DatabaseReference pollVoterRef;
    private DatabaseReference getPollChoiceVoterRef;

    private RecyclerView choicesRecyclerView;
    private TextView pollQuestion;
    private ImageButton btn_done;
    private List<Choice> choiceList;

    private String pollID;
    private String pollTopic;

    FirebaseRecyclerAdapter<Choice, ChoiceViewHolder> adapter;
    FirebaseRecyclerOptions<Choice> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_choice_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

        options = new FirebaseRecyclerOptions.Builder<Choice>()
                .setQuery(choiceRef, Choice.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Choice, ChoiceViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChoiceViewHolder holder, int position, @NonNull Choice model) {
                Log.d("BINDING_CHOICE", model.getChoiceID());
                holder.bind(model);
            }

            @NonNull
            @Override
            public ChoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ChoiceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_poll_choice, parent, false), pollID);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        choicesRecyclerView.setAdapter(adapter);
    }

    private void init() {
        // TODO voters list
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        choicesRecyclerView = findViewById(R.id.rv);
        pollQuestion = findViewById(R.id.poll_question);
        pollTopic = getIntent().getStringExtra("pollQuestion");
        pollQuestion.setText(pollTopic);
        btn_done = findViewById(R.id.btn_done_poll_choice);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), InHomeActivity.class);
                myIntent.putExtra("fragment", "group");
                startActivity(myIntent);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        choicesRecyclerView.setLayoutManager(linearLayoutManager);

        pollID = getIntent().getStringExtra("pollID");

        choiceRef = firebaseDatabase.getReference().child("polls").child(pollID).child("choiceList");
//        TODO poll

    }

    private void getPollChoices(final PollCallback pollCallback) {
        pollRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> choiceList = new ArrayList<>();
                for (DataSnapshot choice: dataSnapshot.getChildren()){
                    String c = choice.getValue(String.class);
//                    Log.d("REFERENCE_CHOICE", choice.getRef().toString());
                    Log.v("CHOICE", c);
                    choiceList.add(c);
                }
                pollCallback.onCallback(choiceList);
                Log.v("CHOICE SIZE", choiceList.size() + "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPollMapSets(final PollMapCallback pollMapCallback) {
        choiceRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d("OUTER_REFERENCE_MAP", dataSnapshot.getRef().toString());
                List<Long> voteList = new ArrayList<>();
                for (DataSnapshot set: dataSnapshot.getChildren()){
                    Long voteValue = (Long) set.getValue();
//                    Log.d("REFERENCE_MAP", set.getRef().toString());
                    voteList.add(voteValue);
                    Log.v("CHOICE", voteValue.toString());

                }
                pollMapCallback.onCallback(voteList);
                Log.v("MAP SIZE", voteList.size() + "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void votePlus(final String choice) {
        choiceRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot val : dataSnapshot.getChildren()) {
                    if (choice.equals(val.getKey())) {
                        Long plus = Long.parseLong(val.getValue().toString()) + 1;
                        choiceRef.child(choice).setValue(plus);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Toast.makeText(this, "PRESSED BUTTON", Toast.LENGTH_SHORT).show();
    }

    private List<String> getAlreadyVotedList() {
        List<String> votees = new ArrayList<>();


        return  votees;
    }

    private interface PollCallback {
        void onCallback(List<String> list);
    }

    private interface PollMapCallback{
        void onCallback(List<Long> list);
    }

}
