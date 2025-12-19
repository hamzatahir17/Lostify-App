package com.example.lostify;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Configure Toolbar and Back Button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Set receiver name from Intent extras as the Toolbar title
            String name = getIntent().getStringExtra("receiverName");
            if (name != null) getSupportActionBar().setTitle(name);
        }

        // Initialize Fragment only if the activity is newly created
        if (savedInstanceState == null) {
            ChatFragment chatFragment = new ChatFragment();

            // Load ChatFragment into the container
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, chatFragment);
            transaction.commit();
        }
    }

    // Handle back button click on the Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}