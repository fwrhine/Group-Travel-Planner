package com.example.pplki18.grouptravelplanner.data;

import java.util.HashMap;

public class Message {

    private String messageId;
    private String senderId;
    private String text;
    private String photoUrl;
    private Long time;
    private HashMap<String, Boolean> read_by;

    public Message() {
    }

    public Message(String messageId, String senderId, String text, String photoUrl, Long time) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.text = text;
        this.photoUrl = photoUrl;
        this.time = time;
        this.read_by = new HashMap<>();
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    public HashMap<String, Boolean> getRead_by() {
        return read_by;
    }

    public void setRead_by(HashMap<String, Boolean> read_by) {
        this.read_by = read_by;
    }
}
