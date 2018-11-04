package com.example.pplki18.grouptravelplanner.data;

public class Message {

    private String senderId;
    private String text;
    private String photoUrl;
    private Long time;

    public Message() {
    }

    public Message(String senderId, String text, String photoUrl, Long time) {
        this.senderId = senderId;
        this.text = text;
        this.photoUrl = photoUrl;
        this.time = time;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
