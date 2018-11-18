package com.example.pplki18.grouptravelplanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Poll {

    private String id;
    private ArrayList<String> voters;
    private ArrayList<String> alreadyVoted;
    private ArrayList<String> choiceList;
    private HashMap<String, Integer> choiceMap;
    private String pollQuestion;

    public Poll(ArrayList<String> list){
        this.choiceList = list;
        this.alreadyVoted = new ArrayList<String>();
        for (int ii = 0; ii < this.choiceList.size(); ii++){
            choiceMap.put(choiceList.get(ii), 0);
        }
    }

    public Poll(){

    }

    //Getter
    public String getId() {
        return id;
    }

    public ArrayList<String> getVoters() {
        return voters;
    }

    public ArrayList<String> getAlreadyVoted() {
        return alreadyVoted;
    }

    public ArrayList<String> getChoiceList() {
        return choiceList;
    }

    public HashMap<String, Integer> getChoiceMap() {
        return choiceMap;
    }

    public String getPollQuestion(){
        return pollQuestion;
    }
    //Setter
    public void setId(String newID) {
        this.id = newID;
    }

    public void setVoters(ArrayList<String> voters) {
        this.voters = voters;
    }

    public void setAlreadyVoted(ArrayList<String> alreadyVoted) {
        this.alreadyVoted = alreadyVoted;
    }

    public void setPollQuestion(String question){
        this.pollQuestion = question;
    }

    public void setChoiceList(ArrayList<String> list) {
        this.choiceList = list;
    }
}
