package com.example.lostify;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat); // Ensure isme FrameLayout ho

        String receiverId = getIntent().getStringExtra("receiverId"); // 🔴 Get ID
        String receiverName = getIntent().getStringExtra("receiverName");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (receiverName != null) getSupportActionBar().setTitle(receiverName);
        }

        if (savedInstanceState == null) {
            ChatFragment chatFragment = new ChatFragment();

            // 🔴 Pass ID to Fragment
            Bundle args = new Bundle();
            args.putString("receiverId", receiverId);
            chatFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, chatFragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}