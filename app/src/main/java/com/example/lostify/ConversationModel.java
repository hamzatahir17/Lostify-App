package com.example.lostify;

public class ConversationModel {
    String userId;      // Jisse baat ho rahi hai uski ID
    String userName;    // Uska naam
    String lastMessage; // Aakhri message
    String time;

    public ConversationModel(String userId, String userName, String lastMessage, String time) {
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