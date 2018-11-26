package com.example.pplki18.grouptravelplanner.data;

public class PollMessage {
    private String senderId;
    private String topicText;
    private String photoUrl;
    private Long time;

    private String pollID;

    public PollMessage() {
    }

    public PollMessage(String senderId, String topicText, String photoUrl, Long time) {
        this.senderId = senderId;
        this.topicText = topicText;
        this.photoUrl = photoUrl;
        this.time = time;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String gettopicText() {
        return topicText;
    }

    public void settopicText(String topicText) {
        this.topicText = topicText;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getPollID(){
        return pollID;
    }

    public void setPollID(String id) {
        this.pollID = id;
    }
}


