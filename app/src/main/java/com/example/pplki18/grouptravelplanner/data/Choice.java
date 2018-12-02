package com.example.pplki18.grouptravelplanner.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Choice {
    private String choiceID;
    private HashMap<String, Boolean> voters;
    private String name;

    public Choice(){
    }

    public Choice(String name) {
        this.voters = new HashMap<>();
        this.name = name;

    }

    public String getChoiceID() {
        return choiceID;
    }

    public void setChoiceID(String choiceID) {
        this.choiceID = choiceID;
    }

    public HashMap<String, Boolean> getVoters(){
        return this.voters;
    }

    public String getName() {
        return name;
    }

    public void setVoters(HashMap<String, Boolean> voters) {
        this.voters = voters;
    }

    public void setName(String name) {
        this.name = name;
    }
}
