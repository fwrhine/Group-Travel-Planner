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

import com.example.pplki18.grouptravelplanner.data.Choice;
import com.example.pplki18.grouptravelplanner.utils.ChoiceViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityPollChoiceTest extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference choiceRef;
    private RecyclerView choicesRecyclerView;
    private TextView pollQuestion;
    private ImageButton btn_done;
    private String pollID;
    private String pollTopic;

    private FirebaseRecyclerAdapter<Choice, ChoiceViewHolder> adapter;
    private FirebaseRecyclerOptions<Choice> options;

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

    }



}
