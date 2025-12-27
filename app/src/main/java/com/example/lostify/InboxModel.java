package com.example.lostify;

public class InboxModel {
    public String partnerId;
    public String userName;
    public String lastMessage;
    public String time;
    public String image;
    public String senderId;
    public boolean seen;
    private long rawTimestamp;

    public InboxModel() { }

    public InboxModel(String partnerId, String userName, String lastMessage, String time, String image, String senderId, long rawTimestamp, boolean seen) {
        this.partnerId = partnerId;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.time = time;
        this.image = image;
        this.senderId = senderId;
        this.rawTimestamp = rawTimestamp;
        this.seen = seen;
    }

    public long getRawTimestamp() { return rawTimestamp; }
    public void setRawTimestamp(long rawTimestamp) { this.rawTimestamp = rawTimestamp; }
}