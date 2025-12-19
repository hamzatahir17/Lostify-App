package com.example.lostify;

public class InboxModel {
    String userId;
    String userName;
    String lastMessage;
    String time;

    public InboxModel(String userId, String userName, String lastMessage, String time) {
        this.userId = userId;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.time = time;
    }

    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getLastMessage() { return lastMessage; }
    public String getTime() { return time; }
}