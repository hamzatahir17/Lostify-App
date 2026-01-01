package com.example.lostify;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.messaging.FirebaseMessaging;

public class NotificationsActivity extends AppCompatActivity {

    MaterialSwitch switchFound, switchChat, switchUpdate;
    ImageView btnBack;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        btnBack = findViewById(R.id.btnBack);
        switchFound = findViewById(R.id.switchFound);
        switchChat = findViewById(R.id.switchChat);
        switchUpdate = findViewById(R.id.switchUpdate);

        sharedPreferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        switchFound.setChecked(sharedPreferences.getBoolean("notify_found", true));
        switchChat.setChecked(sharedPreferences.getBoolean("notify_chat", true));
        switchUpdate.setChecked(sharedPreferences.getBoolean("notify_update", false));

        switchFound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("notify_found", isChecked);
            editor.apply();

            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("all_reports");
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("all_reports");
            }
        });

        switchChat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("notify_chat", isChecked);
            editor.apply();
        });

        switchUpdate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("notify_update", isChecked);
            editor.apply();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}