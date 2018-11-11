package com.example.pplki18.grouptravelplanner;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

@SuppressLint("Registered")
public class Activity_CreatePoll extends AppCompatActivity {

    private EditText topicInput;
    private EditText choiceInput;
    private TextView choiceText;
    private Button btnAddChoice;
    private ImageButton btndone;
    private ArrayList<String> choiceList;
    private String choices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        init();
        btnAddChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Starts the function below
                String newChoice = choiceText.getText().toString();
                choiceList.add(newChoice);
                choices += newChoice + " ";
                choiceText.setText(choices);
            }
        });

        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Starts the function below
                @SuppressWarnings("unused") Poll newPoll = new Poll(choiceList);
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
    }
}
