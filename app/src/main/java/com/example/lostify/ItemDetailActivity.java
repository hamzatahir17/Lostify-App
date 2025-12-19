package com.example.lostify;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;

public class ItemDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // --- 1. TOOLBAR & NAVIGATION SETUP ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable back arrow and hide default title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Handle back button click via System Dispatcher
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Initialize UI components
        ImageView detailImage = findViewById(R.id.detailImage);
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvStatus = findViewById(R.id.tvDetailStatus);
        TextView tvLocation = findViewById(R.id.tvDetailLocation);
        TextView tvTime = findViewById(R.id.tvDetailDate);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);
        MaterialButton btnContact = findViewById(R.id.btnContact);

        // --- 2. RETRIEVE DATA FROM INTENT ---
        // Extract data passed from the ReportAdapter
        String title = getIntent().getStringExtra("ITEM_TITLE");
        String location = getIntent().getStringExtra("ITEM_LOCATION");
        String time = getIntent().getStringExtra("ITEM_TIME");
        String status = getIntent().getStringExtra("ITEM_STATUS");
        String description = getIntent().getStringExtra("ITEM_DESC");
        int imageResId = getIntent().getIntExtra("ITEM_IMAGE", R.drawable.bagpack);

        // --- 3. BIND DATA TO UI ---
        tvTitle.setText(title);
        tvLocation.setText("📍 " + location);
        tvTime.setText("📅 " + time);
        tvDescription.setText(description);
        tvStatus.setText(status);
        detailImage.setImageResource(imageResId);

        // Dynamic styling for Status Badge based on item type (LOST/FOUND)
        if (status != null && status.equals("FOUND")) {
            tvStatus.setTextColor(Color.parseColor("#388E3C")); // Green Text
            tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9")); // Light Green Background
        } else {
            tvStatus.setTextColor(Color.parseColor("#D32F2F")); // Red Text
            tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE")); // Light Red Background
        }

        // --- 4. CONTACT LOGIC ---
        btnContact.setOnClickListener(v -> {
            // Navigate to ChatActivity and pass the receiver's name
            Intent intent = new Intent(ItemDetailActivity.this, ChatActivity.class);
            intent.putExtra("receiverName", "Ali Ahmed"); // Placeholder name
            startActivity(intent);
        });
    }
}