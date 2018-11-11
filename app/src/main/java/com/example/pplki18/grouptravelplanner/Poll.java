package com.example.pplki18.grouptravelplanner;

import java.util.ArrayList;
import java.util.HashMap;

public class Poll {

    private ArrayList<String> voters;
    private ArrayList<String> alreadyVoted;
    private ArrayList<String> choiceList;
    private HashMap<String, Integer> choiceMap;

    public Poll(ArrayList<String> list){
        this.choiceList = list;
        this.alreadyVoted = new ArrayList<String>();
        for (int ii = 0; ii < this.choiceList.size(); ii++){
            choiceMap.put(choiceList.get(ii), 0);
        }
    }

    //Getter

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

    //Setter
    public void setVoters(ArrayList<String> voters) {
        this.voters = voters;
    }

    public void setAlreadyVoted(ArrayList<String> alreadyVoted) {
        this.alreadyVoted = alreadyVoted;
    }

}
