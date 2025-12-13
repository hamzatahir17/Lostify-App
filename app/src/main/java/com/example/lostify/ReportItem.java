package com.example.lostify;

public class ReportItem {
    // Variables
    private String title;
    private String location;
    private String time;
    private String status; // "LOST" or "FOUND"
    private int imageResId; // Image (e.g., R.drawable.bagpack)

    // Constructor
    public ReportItem(String title, String location, String time, String status, int imageResId) {
        this.title = title;
        this.location = location;
        this.time = time;
        this.status = status;
        this.imageResId = imageResId;
    }

    // Getters
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public int getImageResId() { return imageResId; }
}