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

        // --- 1. VIEWS INITIALIZE ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Back arrow enable karna
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        // Back button dabane par wapis jana
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        ImageView detailImage = findViewById(R.id.detailImage);
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvStatus = findViewById(R.id.tvDetailStatus);
        TextView tvLocation = findViewById(R.id.tvDetailLocation);
        TextView tvTime = findViewById(R.id.tvDetailDate);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);
        MaterialButton btnContact = findViewById(R.id.btnContact);

        // --- 2. DATA RECEIVE KARNA (Intent se) ---
        // Ye wahi keys hain jo humne Adapter mein use ki thin
        String title = getIntent().getStringExtra("ITEM_TITLE");
        String location = getIntent().getStringExtra("ITEM_LOCATION");
        String time = getIntent().getStringExtra("ITEM_TIME");
        String status = getIntent().getStringExtra("ITEM_STATUS");
        String description = getIntent().getStringExtra("ITEM_DESC");
        int imageResId = getIntent().getIntExtra("ITEM_IMAGE", R.drawable.bagpack);

        // --- 3. DATA SET KARNA ---
        tvTitle.setText(title);
        tvLocation.setText("📍 " + location);
        tvTime.setText("📅 " + time);
        tvDescription.setText(description);
        tvStatus.setText(status);
        detailImage.setImageResource(imageResId);

        // Status ka color set karna
        if (status != null && status.equals("FOUND")) {
            tvStatus.setTextColor(Color.parseColor("#388E3C")); // Green Text
            tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9")); // Light Green Bg
        } else {
            tvStatus.setTextColor(Color.parseColor("#D32F2F")); // Red Text
            tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE")); // Light Red Bg
        }

        // --- 4. CONTACT BUTTON CLICK ---
        btnContact.setOnClickListener(v -> {
            // Chat Screen par jana
            Intent intent = new Intent(ItemDetailActivity.this, ChatActivity.class);
            // Agli screen ko naam bhejna (Example: "Ali Ahmed")
            intent.putExtra("receiverName", "Ali Ahmed");
            startActivity(intent);
        });
    }
}