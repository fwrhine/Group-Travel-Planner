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

import com.example.pplki18.grouptravelplanner.data.Choice;
import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.data.Message;
import com.example.pplki18.grouptravelplanner.data.Poll;
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
    private ArrayList<String> choiceNames;
    private ArrayList<Choice> choiceList;
    private String choices;
    private ArrayList<String> voters;
    //    private Group group;
    private String groupID;
    private Integer choiceNum;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference pollRef;
    private DatabaseReference messageDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        init();
        btnAddChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Starts the function below
                String newChoice = choiceInput.getText().toString();
                if (!choiceNames.contains(newChoice)) {
                    choiceNames.add(newChoice);
                    Choice choice = new Choice(choiceInput.getText().toString());
                    choiceList.add(choice);
                    if (choiceNum < 10) {
                        if (choiceText.equals("")) {
                            choices += newChoice;
                            choiceNum++;
                        } else {
                            choices += newChoice + "\n";
                            choiceNum++;
                        }
                    }

                    else {
                        Toast.makeText(getApplicationContext(), "Max number of choices", Toast.LENGTH_SHORT).show();
                    }
                    choiceText.setText(choices);
                    choiceInput.setText("");

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
                    newPoll.setGroupID(groupID);

                    String pollKey = pollRef.push().getKey();
                    newPoll.setPollID(pollKey);

                    addPoll(newPoll, pollKey);


//========================

                    String msgKey = messageDatabaseReference.push().getKey();
                    Message message = new Message(msgKey, firebaseUser.getUid(), topic, null, System.currentTimeMillis(), pollKey);
                    messageDatabaseReference.child(msgKey).setValue(message);

                    //================================

                    Toast.makeText(getApplicationContext(), "Created Poll", Toast.LENGTH_SHORT).show();

                    Intent myIntent = new Intent(Activity_CreatePoll.this , InHomeActivity.class);
                    myIntent.putExtra("fragment", "group");
                    startActivity(myIntent);
                }

                else {
                    Toast.makeText(getApplicationContext(), "Write a topic and at least 2 " +
                            "choices must be made", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(Activity_CreatePoll.this , InHomeActivity.class);
                    myIntent.putExtra("fragment", "group");
                    startActivity(myIntent);
                }
            }
        });
    }

    private void pushChoices(String pollID) {
        final DatabaseReference choiceRef = pollRef.child(pollID).child("choiceList");
        choiceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i=0; i<choiceList.size(); i++) {
                    String choiceKey = choiceRef.push().getKey();
                    Choice choice = choiceList.get(i);
                    choice.setChoiceID(choiceKey);
                    choiceRef.child(choiceKey).setValue(choice);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        choiceNames = new ArrayList<>();
        choices = "";
        choiceNum = 0;

        Intent myIntent = getIntent();
        voters = myIntent.getStringArrayListExtra("voters");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        pollRef = firebaseDatabase.getReference().child("polls");

        groupID = getIntent().getStringExtra("groupID");
        messageDatabaseReference = firebaseDatabase.getReference().child("messages").child(groupID);


    }

    private void addPoll(final Poll newPoll, final String pollKey){
        final List<Poll> pollList = new ArrayList<>();

        pollRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pollRef.child(pollKey).setValue(newPoll);
                pushChoices(pollKey);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
