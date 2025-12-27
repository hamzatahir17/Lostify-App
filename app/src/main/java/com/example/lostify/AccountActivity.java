package com.example.lostify;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    private ImageView ivProfile, btnBack;
    private TextView tvChangePhoto, tvRemovePhoto, tvLogout;
    private EditText etName, etEmail;
    private MaterialButton btnSave;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Uri localImageUri;
    private String serverImageUrl;

    private boolean isImageChanged = false;
    private boolean isImageRemoved = false;

    private ProgressDialog progressDialog;

    // Keys from BuildConfig
    private static final String CLOUD_NAME = BuildConfig.CLOUDINARY_CLOUD_NAME;
    private static final String API_KEY = BuildConfig.CLOUDINARY_API_KEY;
    private static final String API_SECRET = BuildConfig.CLOUDINARY_API_SECRET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // --- Init Cloudinary ---
        initCloudinary();

        // --- Init Firebase ---
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // --- Hooks ---
        initViews();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Profile...");
        progressDialog.setCancelable(false);

        // --- Load Data ---
        loadUserProfile();

        // --- Back Button ---
        btnBack.setOnClickListener(v -> finish());

        // --- Logout Logic ---
        tvLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(AccountActivity.this, LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });


        ActivityResultLauncher<PickVisualMediaRequest> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        localImageUri = uri;
                        isImageChanged = true;
                        isImageRemoved = false;

                        //  Glide Optimization: Circle Crop for Profile
                        Glide.with(this)
                                .load(uri)
                                .circleCrop() // Makes image round
                                .placeholder(R.drawable.placeholder_loading)
                                .into(ivProfile);
                    } else {
                        // User closed picker without selecting
                        Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Launch the modern Photo Picker
        tvChangePhoto.setOnClickListener(v ->
                imagePickerLauncher.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build())
        );



        // --- Remove Photo Logic ---
        tvRemovePhoto.setOnClickListener(v -> {
            ivProfile.setImageResource(R.drawable.placeholder_loading);
            isImageRemoved = true;
            isImageChanged = false;
            localImageUri = null;
            serverImageUrl = null;
            Toast.makeText(this, "Photo Removed. Click 'Update' to save.", Toast.LENGTH_SHORT).show();
        });

        // --- Full Screen View Logic ---
        ivProfile.setOnClickListener(v -> handleFullScreenView());

        // --- Save Button Logic ---
        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                if (isImageChanged && localImageUri != null) {
                    uploadImageAndSave();
                } else if (isImageRemoved) {
                    updateFirestore(""); // Remove image
                } else {
                    updateFirestore(null); // Just update text
                }
            }
        });
    }

    private void initCloudinary() {
        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", CLOUD_NAME);
            config.put("api_key", API_KEY);
            config.put("api_secret", API_SECRET);
            MediaManager.init(this, config);
        } catch (IllegalStateException e) {
            // Already initialized
        }
    }

    private void initViews() {
        ivProfile = findViewById(R.id.ivProfile);
        btnBack = findViewById(R.id.btnBack);
        tvChangePhoto = findViewById(R.id.tvChangePhoto);
        tvRemovePhoto = findViewById(R.id.tvRemovePhoto);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSave);
        tvLogout = findViewById(R.id.tvLogout);
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            serverImageUrl = documentSnapshot.getString("profileImage");

                            etName.setText(name);
                            etEmail.setText(email);

                            if (serverImageUrl != null && !serverImageUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(serverImageUrl)
                                        .circleCrop() //  Consistent styling
                                        .placeholder(R.drawable.placeholder_loading)
                                        .transition(DrawableTransitionOptions.withCrossFade(500))
                                        .into(ivProfile);
                            } else {
                                ivProfile.setImageResource(R.drawable.placeholder_loading);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show());
        }
    }

    private void handleFullScreenView() {
        if (isImageRemoved) {
            Toast.makeText(this, "No photo to view", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isImageChanged && localImageUri != null) {
            showFullImageDialog(localImageUri);
        } else if (serverImageUrl != null && !serverImageUrl.isEmpty()) {
            showFullImageDialog(serverImageUrl);
        } else {
            Toast.makeText(this, "No profile photo set", Toast.LENGTH_SHORT).show();
        }
    }

    private void showFullImageDialog(Object imageSource) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        FrameLayout container = new FrameLayout(this);
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        container.setBackgroundColor(Color.BLACK);

        ImageView fullScreenImage = new ImageView(this);
        fullScreenImage.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fullScreenImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        //  MEMORY FIX: Resize large images to avoid OOM crash
        Glide.with(this)
                .load(imageSource)
                .override(1080, 1920)
                .placeholder(R.drawable.placeholder_loading)
                .into(fullScreenImage);

        // Close Button Logic
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

    private boolean validateInputs() {
        if (TextUtils.isEmpty(etName.getText())) {
            etName.setError("Name required");
            return false;
        }
        return true;
    }

    private void uploadImageAndSave() {
        progressDialog.setMessage("Uploading Image...");
        progressDialog.show();

        MediaManager.get().upload(localImageUri).callback(new UploadCallback() {
            @Override public void onStart(String requestId) {}
            @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
            @Override public void onSuccess(String requestId, Map resultData) {
                String secureUrl = (String) resultData.get("secure_url");
                serverImageUrl = secureUrl;
                updateFirestore(secureUrl);
            }
            @Override public void onError(String requestId, ErrorInfo error) {
                progressDialog.dismiss();
                Toast.makeText(AccountActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
            }
            @Override public void onReschedule(String requestId, ErrorInfo error) {}
        }).dispatch();
    }

    private void updateFirestore(String newImageUrl) {
        progressDialog.setMessage("Saving Profile...");
        if (!progressDialog.isShowing()) progressDialog.show();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String newName = etName.getText().toString().trim();
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newName);
            updates.put("email", etEmail.getText().toString().trim());

            if (newImageUrl != null) {
                updates.put("profileImage", newImageUrl);
            } else if (isImageRemoved) {
                updates.put("profileImage", ""); // Explicitly clear it in DB
            }

            db.collection("users").document(user.getUid())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(newName)
                                .build();
                        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            isImageChanged = false;
                            isImageRemoved = false;
                            Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}