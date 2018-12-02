package com.example.pplki18.grouptravelplanner;

import com.example.pplki18.grouptravelplanner.data.Choice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Poll {

    private String pollID;
    private List<Choice> choiceList;
    private String pollQuestion;
    private String groupID;
    private HashMap<String, Boolean> voters;

    public Poll(String pollID, String topic, String groupID){
        this.pollID = pollID;
        this.choiceList = new ArrayList<>();
        this.pollQuestion = topic;
        this.groupID = groupID;
        this.voters = new HashMap<>();
    }

    public Poll(){}

    //Getter
    public String getPollID() {
        return pollID;
    }

    public List<Choice> getChoiceList() {
        return choiceList;
    }

    public String getPollQuestion(){
        return pollQuestion;
    }

    public String getGroupID() { return groupID; }

    //Setter
    public void setPollID(String newID) {
        this.pollID = newID;
    }

    public void setChoiceList(List<Choice> list) {
        this.choiceList = list;
    }

    public void setPollQuestion(String question){
        this.pollQuestion = question;
    }

    public void setGroupID(String groupID) { this.groupID = groupID; }

    public HashMap<String, Boolean> getVoters() {
        return voters;
    }

    public void setVoters(HashMap<String, Boolean> voters) {
        this.voters = voters;
    }
}
