package com.example.lostify;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date; // ⚠️ Zaroori Import

public class ReportModel {
    private String userId;
    private String itemName;
    private String category;
    private String description;
    private String location;
    private String date; // Manual string date (e.g. "12/12/2025")
    private String time; // Manual string time (e.g. "10:30 PM")
    private String imageUrl;
    private String status;

    // 🔴 CHANGE: 'long' ki jagah 'Date' use karein
    @ServerTimestamp // Yeh annotation automatic time handle karegi
    private Date timestamp;

    // Empty Constructor (Firebase ke liye Zaroori hai)
    public ReportModel() {
    }

    // Constructor with all fields
    public ReportModel(String userId, String itemName, String category, String description, String location, String date, String time, String imageUrl, String status) {
        this.userId = userId;
        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.location = location;
        this.date = date;
        this.time = time;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    // --- GETTERS AND SETTERS ---

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // 🔴 Updated Getter/Setter for Timestamp
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}