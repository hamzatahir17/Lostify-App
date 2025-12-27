package com.example.lostify;

public class ChatModel {
    private String senderId;
    private String receiverId;
    private String messageText;
    private long timestamp;
    private boolean seen;

    public ChatModel() { }

    public ChatModel(String senderId, String receiverId, String messageText, long timestamp, boolean seen) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }
}