package com.example.lostify;

import android.app.Dialog;
import android.content.Intent;
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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ItemDetailActivity extends AppCompatActivity {

    private ImageView detailImage;
    private TextView tvTitle, tvStatus, tvLocation, tvTime, tvDescription, tvCategory;
    private MaterialButton btnContact;


    private String imageUrl, ownerId, itemTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        setupToolbar();
        initViews();
        getAndSetData();
        setupClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void initViews() {
        detailImage = findViewById(R.id.detailImage);
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvTime = findViewById(R.id.tvDetailDate);
        tvDescription = findViewById(R.id.tvDetailDescription);
        // tvCategory = findViewById(R.id.tvDetailCategory);
        btnContact = findViewById(R.id.btnContact);
    }

    private void getAndSetData() {

        itemTitle = getIntent().getStringExtra("ITEM_NAME");
        String location = getIntent().getStringExtra("ITEM_LOCATION");
        String date = getIntent().getStringExtra("ITEM_DATE");
        String time = getIntent().getStringExtra("ITEM_TIME");
        String status = getIntent().getStringExtra("ITEM_STATUS");
        String description = getIntent().getStringExtra("DESCRIPTION");
        String category = getIntent().getStringExtra("CATEGORY");
        imageUrl = getIntent().getStringExtra("ITEM_IMAGE_URL");
        ownerId = getIntent().getStringExtra("USER_ID");


        tvTitle.setText(itemTitle);
        tvLocation.setText("📍 " + location);


        String dateTimeDisplay = "📅 " + date;
        if(time != null && !time.isEmpty()) {
            dateTimeDisplay += " | " + time;
        }
        tvTime.setText(dateTimeDisplay);

        tvDescription.setText(description);


        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_loading)
                    .error(R.drawable.placeholder_loading)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(detailImage);
        } else {
            detailImage.setImageResource(R.drawable.placeholder_loading);
        }


        if (status != null && status.equalsIgnoreCase("FOUND")) {
            tvStatus.setText("FOUND");
            tvStatus.setTextColor(Color.parseColor("#388E3C")); // Green
            tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
            btnContact.setText("Claim Item");
        } else {
            tvStatus.setText("LOST");
            tvStatus.setTextColor(Color.parseColor("#D32F2F")); // Red
            tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
            btnContact.setText("Contact Owner");
        }
    }

    private void setupClickListeners() {

        detailImage.setOnClickListener(v -> {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                showFullImageDialog(imageUrl);
            }
        });


        btnContact.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser == null) {
                Toast.makeText(this, "Please Login to Chat", Toast.LENGTH_SHORT).show();
                return;
            }

            if (ownerId == null || ownerId.isEmpty() || ownerId.equals("Guest")) {
                Toast.makeText(this, "Cannot contact guest user", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentUser.getUid().equals(ownerId)) {
                Toast.makeText(this, "You posted this item yourself!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Start Chat
            Intent intent = new Intent(ItemDetailActivity.this, ChatActivity.class);
            intent.putExtra("receiverId", ownerId);
            intent.putExtra("receiverName", itemTitle);
            startActivity(intent);
        });
    }

    private void showFullImageDialog(String imageUrl) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        FrameLayout container = new FrameLayout(this);
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        container.setBackgroundColor(Color.BLACK);

        ImageView fullScreenImage = new ImageView(this);
        fullScreenImage.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fullScreenImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Glide.with(this).load(imageUrl).into(fullScreenImage);

        ImageView closeButton = new ImageView(this);
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        closeButton.setColorFilter(Color.WHITE);
        int padding = dpToPx(12);
        closeButton.setPadding(padding, padding, padding, padding);

        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        closeParams.gravity = Gravity.TOP | Gravity.END;
        closeParams.setMargins(0, dpToPx(16), dpToPx(16), 0);
        closeButton.setLayoutParams(closeParams);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        container.addView(fullScreenImage);
        container.addView(closeButton);
        dialog.setContentView(container);
        dialog.show();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}