package com.example.lostify;

import android.app.Dialog;
import android.content.Intent; // Required for Intent
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth; // Required for Auth check
import com.google.firebase.auth.FirebaseUser;

public class ItemDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // --- TOOLBAR & NAVIGATION ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // --- INITIALIZE VIEWS ---
        ImageView detailImage = findViewById(R.id.detailImage);
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvStatus = findViewById(R.id.tvDetailStatus);
        TextView tvLocation = findViewById(R.id.tvDetailLocation);
        TextView tvTime = findViewById(R.id.tvDetailDate);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);
        MaterialButton btnContact = findViewById(R.id.btnContact);

        // --- RETRIEVE DATA ---
        String title = getIntent().getStringExtra("ITEM_TITLE");
        String location = getIntent().getStringExtra("ITEM_LOCATION");
        String time = getIntent().getStringExtra("ITEM_TIME");
        String status = getIntent().getStringExtra("ITEM_STATUS");
        String description = getIntent().getStringExtra("ITEM_DESC");
        String imageUrl = getIntent().getStringExtra("ITEM_IMAGE_URL");

        // 🔴 NEW: Retrieve Owner ID passed from ReportAdapter
        String ownerId = getIntent().getStringExtra("OWNER_ID");

        // --- BIND DATA ---
        tvTitle.setText(title);
        tvLocation.setText("📍 " + location);
        tvTime.setText("📅 " + time);
        tvDescription.setText(description);

        // --- IMAGE LOGIC (GLIDE) ---
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(detailImage);

            // Click Listener for Full Screen View
            detailImage.setOnClickListener(v -> showFullImageDialog(imageUrl));

        } else {
            detailImage.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // --- STATUS COLOR LOGIC ---
        if (status != null && status.equals("FOUND")) {
            tvStatus.setText("FOUND");
            tvStatus.setTextColor(Color.parseColor("#388E3C"));
            tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
            btnContact.setText("Claim Item"); // Found item hai to "Claim" likha aaye
        } else {
            tvStatus.setText("LOST");
            tvStatus.setTextColor(Color.parseColor("#D32F2F"));
            tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
            btnContact.setText("Contact Owner"); // Lost item hai to "Contact" likha aaye
        }

        // --- 🔴 UPDATED CONTACT LOGIC (Start Chat) ---
        btnContact.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            // 1. Check if user is logged in
            if (currentUser == null) {
                Toast.makeText(this, "Please Login to Chat", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Check if Owner ID exists
            if (ownerId == null || ownerId.isEmpty() || ownerId.equals("Guest")) {
                Toast.makeText(this, "Cannot contact guest user", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Prevent chatting with yourself
            if (currentUser.getUid().equals(ownerId)) {
                Toast.makeText(this, "You posted this item yourself!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. Start Chat Activity
            Intent intent = new Intent(ItemDetailActivity.this, ChatActivity.class);
            intent.putExtra("receiverId", ownerId); // Pass Owner ID to Chat
            intent.putExtra("receiverName", title); // Show Item Name as Chat Title
            startActivity(intent);
        });
    }

    /**
     * Shows full-screen image with a Close (X) button
     */
    private void showFullImageDialog(String imageUrl) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        // 1. Create a Container
        FrameLayout container = new FrameLayout(this);
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        container.setBackgroundColor(Color.BLACK);

        // 2. Create the Main Full Screen Image View
        ImageView fullScreenImage = new ImageView(this);
        fullScreenImage.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        fullScreenImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(this).load(imageUrl).into(fullScreenImage);

        // 3. Create the Close Button (X Icon)
        ImageView closeButton = new ImageView(this);
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        closeButton.setColorFilter(Color.WHITE);

        int padding = dpToPx(12);
        closeButton.setPadding(padding, padding, padding, padding);

        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        closeParams.gravity = Gravity.TOP | Gravity.END;
        int margin = dpToPx(16);
        closeParams.setMargins(0, margin, margin, 0);
        closeButton.setLayoutParams(closeParams);

        closeButton.setOnClickListener(v -> dialog.dismiss());

        // 4. Add views to container
        container.addView(fullScreenImage);
        container.addView(closeButton);

        // 5. Show the dialog
        dialog.setContentView(container);
        dialog.show();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}