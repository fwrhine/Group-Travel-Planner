package com.example.pplki18.grouptravelplanner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.List;

public class ActivityPollChoiceTest extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference pollRef;
    DatabaseReference pollMapRef;

    private TextView choice1;
    private TextView choice1num;
    private TextView choice2;
    private TextView choice2num;
    private Button btn_increment1;
    private Button btn_increment2;
    private List<String> choiceList;
    private List<Long> voteList;

    String choice1fb;
    private String choice2fb;

    Long votes1;
    private Long votes2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_choice_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

// ref = new Firebase(FIREBASE_URL);
// Root URL

        getPollChoices(new PollCallback() {
            @Override
            public void onCallback(List<String> list) {
                Log.v("CHOICE", list.size() + "");
                Log.v("CHOICE", list.toString() + "");
                Log.v("CHOICE MAP1", choiceList.toString() + "");
                choiceList = list;
                choice2fb = choiceList.get(0);
                choice2.setText(choice2fb);
                getPollMapSets(new PollMapCallback() {
                    @Override
                    public void onCallback(List<Long> list) {
                        Log.v("MAP", list.size() + "");
                        Log.v("MAP", list.toString() + "");
                        voteList = list;
                        votes2 = voteList.get(0);
                        choice2num.setText(votes2.toString());
                        btn_increment2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                votePlus(choiceList.get(0));
                            }
                        });
                    }
                });
            }
        });

        btn_increment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_increment2.setEnabled(false);
            }
        });



    }

//    private void collectPollChoices(Map<String,Object> poll) {
//
//        List<String> pollChoices = new ArrayList<>();
//
//        //iterate through each user, ignoring their UID
//        for (Map.Entry<String, Object> entry : poll.entrySet()){
//
//            //Get user voteValue
//            Map singlePoll = (Map) entry.getValue();
//            pollChoices.add(singlePoll.get())
//            Log.v("CHOICE MAP3", singlePoll.toString());
//
//
//            //Get poll field and append to list
//            for (Map.Entry<String, Object> entry2 : poll.entrySet()){
//                //Get user voteValue
//                Map pollChoice = (Map) entry.getValue();
//                //Get phone field and append to list
//                pollMap.add((HashMap<String, Integer>) pollChoice);
//                Log.v("CHOICE MAP4", pollMap.toString());
//
//            }
//        }
//
//    }

    private void init() {
        choice1 = (TextView) findViewById(R.id.poll_choice_test);
        choice2 = (TextView) findViewById(R.id.poll_choice_test2);
        choice1num = (TextView) findViewById(R.id.poll_choice_num_test);
        choice2num = (TextView) findViewById(R.id.poll_choice_num_test2);
        btn_increment1 = (Button) findViewById(R.id.poll_choice_increment_test);
        btn_increment2 = (Button) findViewById(R.id.poll_choice_increment_test2);

        choiceList = new ArrayList<>();
        voteList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        pollRef = firebaseDatabase.getReference().child("polls").child("-LRbMoWjE-TjcB1lOfI4").child("choiceList");
        pollMapRef = firebaseDatabase.getReference().child("polls").child("-LRbMoWjE-TjcB1lOfI4").child("choiceMap");
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
        pollMapRef.addValueEventListener(new ValueEventListener() {

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
        pollMapRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot val : dataSnapshot.getChildren()) {
                    if (choice.equals(val.getKey())) {
                        Long plus = Long.parseLong(val.getValue().toString()) + 1;
                        pollMapRef.child(choice).setValue(plus);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Toast.makeText(this, "PRESSED BUTTON", Toast.LENGTH_SHORT).show();
    }

//    public void votePlus(final String choice) {
//        pollMapRef.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                voteList.clear();
//                for (DataSnapshot val : dataSnapshot.getChildren()) {
//                    if (choice.equals(val.getKey())) {
//                        Long value = val.getValue(Long.class) + 1;
//                        voteList.add(value);
//                    }
//                    else{
//                        Long value = val.getValue(Long.class);
//                        voteList.add(value);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        Toast.makeText(this, "PRESSED BUTTON", Toast.LENGTH_SHORT).show();
//    }

    private interface PollCallback {
        void onCallback(List<String> list);
    }

    private interface PollMapCallback{
        void onCallback(List<Long> list);
    }

}
