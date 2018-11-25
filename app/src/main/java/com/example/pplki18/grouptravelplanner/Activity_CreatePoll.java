package com.example.pplki18.grouptravelplanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("Registered")
public class Activity_CreatePoll extends AppCompatActivity {

    private EditText topicInput;
    private EditText choiceInput;
    private TextView choiceText;
    private Button btnAddChoice;
    private ImageButton btndone;
    private ArrayList<String> choiceList;
    private String choices;
    private ArrayList<String> voters;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference pollRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        init();
//        choiceText.setText("dummy");
        btnAddChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Starts the function below
                String newChoice = choiceInput.getText().toString();
                if (!choiceList.contains(newChoice)) {
                    choiceList.add(newChoice);
                    if (choiceText.equals("")){
                        choices += newChoice;
                    }
                    else {
                        choices += ", " + newChoice;
                    }
                    choiceText.setText(choices);
//                    Intent intent = getIntent();
//                    intent.putExtra("topic", topicInput.getText());
//                    finish();
//                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Added choice", Toast.LENGTH_SHORT).show();
                    Log.v("CHOICE LIST", choiceList.toString());

                }
            }
        });

        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Starts the function below
                String topic = topicInput.getText().toString();
                if (choiceList.size() > 1 && topic.length() != 0) {
                    Poll newPoll = new Poll();
                    newPoll.setPollQuestion(topic);
                    newPoll.setChoiceList(choiceList);
                    newPoll.setVoters(voters);
                    HashMap<String, Integer> choiceMap = new HashMap<>();

                    for (String x : choiceList) {
                        choiceMap.put(x, 0);
                    }
                    newPoll.setChoiceMap(choiceMap);
//                ArrayList<String> voters = new ArrayList<>() ;
//                voters.add("DUMMY1");
//                voters.add("DUMMY2");
//                List<String> alreadyVoted = new ArrayList<>();
//                ArrayList<String> choiceList = new ArrayList<>();
//                choiceList.add("CHOICE1");
//                choiceList.add("CHOICE2");
//                HashMap<String, Integer> choiceMap = new HashMap<>();
//                String key0 = choiceList.get(0);
//                String key1 = choiceList.get(1);
//                choiceMap.put(key0, 0);
//                choiceMap.put(key1, 0);
//                newPoll.setVoters(voters);
//                newPoll.setChoiceList(choiceList);
//                newPoll.setPollQuestion("TESTING");

                    String pollKey = pollRef.push().getKey();
                    addPoll(newPoll, pollKey);
                    newPoll.setId(pollKey);
                    //TODO add poll message to the group chat
                    Toast.makeText(getApplicationContext(), "Created Poll", Toast.LENGTH_SHORT).show();

                Intent myIntent = new Intent(Activity_CreatePoll.this , InGroupActivity.class);
                myIntent.putExtra("fragment", "group");
                startActivity(myIntent);
                }

                else {
//                    Toast.makeText(getApplicationContext(), "Write a topic and at least 2 " +
//                            "choices must be made", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(Activity_CreatePoll.this , InHomeActivity.class);
//                    myIntent.putExtra("fragment", "group");
                    startActivity(myIntent);
                }
            }
        });
    }


    private void init() {
        topicInput = (EditText) findViewById(R.id.poll_topicText);
        choiceInput = (EditText) findViewById(R.id.poll_choiceText);
        choiceText = (TextView) findViewById(R.id.poll_choicesList);
        btnAddChoice = (Button) findViewById(R.id.btn_poll_add_choice);
        btndone = (ImageButton) findViewById(R.id.btn_done_poll);
        choiceList = new ArrayList<>();
        choices = "";

        Intent myIntent = getIntent();
        voters = myIntent.getStringArrayListExtra("voters");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        pollRef = firebaseDatabase.getReference().child("polls");    }

    private void addPoll(final Poll newPoll, final String pollKey){
        final List<Poll> pollList = new ArrayList<>();

        pollRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pollRef.child(pollKey).setValue(newPoll);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
