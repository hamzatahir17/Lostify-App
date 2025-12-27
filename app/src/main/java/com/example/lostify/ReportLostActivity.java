package com.example.lostify;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportLostActivity extends AppCompatActivity {

    private EditText etItemName, etDescription, etLocation, etDate, etTime;
    private Spinner spinnerCategory;
    private Button btnSubmit, btnUploadImage;
    private ImageView ivSelectedImage;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private Uri imageUri;
    private ProgressDialog progressDialog;

    // Keys secured via BuildConfig
    private static final String CLOUD_NAME = BuildConfig.CLOUDINARY_CLOUD_NAME;
    private static final String API_KEY = BuildConfig.CLOUDINARY_API_KEY;
    private static final String API_SECRET = BuildConfig.CLOUDINARY_API_SECRET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_lost);

        initCloudinary();
        initViews();
        setupPickers();
        setupDescriptionScroll();
        setupPhotoPicker(); //  Modern Picker Logic

        // Database Init
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Submit Button
        btnSubmit.setOnClickListener(v -> {
            if (validateFields()) {
                if (imageUri != null) {
                    uploadToCloudinary(imageUri);
                } else {
                    // Agar bina image ke allow karna hai (Optional)
                    saveDataToFirestore(null);
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
        etItemName = findViewById(R.id.etItemName);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnUploadImage = findViewById(R.id.btnUploadImage);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting Report...");
        progressDialog.setCancelable(false);

        // Setup Spinner
        String[] categories = {
                "Select Category", "Mobile Phones", "Laptops/Tablets", "Electronics (Other)",
                "Wallet/Purse", "Keys", "Documents/IDs", "Bags/Luggage", "Clothing/Shoes",
                "Jewelry/Watches", "Books/Stationery", "Pets", "Sports Gear", "Others"
        };
        spinnerCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));
    }

    private void setupPhotoPicker() {
        //  PRO: Android Photo Picker (No Permission Crash)
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        ivSelectedImage.setVisibility(View.VISIBLE);
                        btnUploadImage.setText("Change Image");

                        //  PRO: Glide handles large bitmaps safely
                        Glide.with(this)
                                .load(uri)
                                .transform(new CenterCrop(), new RoundedCorners(16))
                                .into(ivSelectedImage);

                        // Persist Permission (Safety check)
                        try {
                            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        btnUploadImage.setOnClickListener(v ->
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build())
        );
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupDescriptionScroll() {
        etDescription.setOnTouchListener((v, event) -> {
            if (etDescription.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
            return false;
        });
    }

    private void setupPickers() {
        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) ->
                    etDate.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", d, m + 1, y)),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        etTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, h, m) -> {
                String amPm = (h >= 12) ? "PM" : "AM";
                int h12 = (h > 12) ? h - 12 : (h == 0 ? 12 : h);
                etTime.setText(String.format(Locale.getDefault(), "%02d:%02d %s", h12, m, amPm));
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
        });
    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(etItemName.getText())) { etItemName.setError("Required"); return false; }
        if (TextUtils.isEmpty(etDescription.getText())) { etDescription.setError("Required"); return false; }
        if (TextUtils.isEmpty(etLocation.getText())) { etLocation.setError("Required"); return false; }
        if (TextUtils.isEmpty(etDate.getText())) { Toast.makeText(this, "Select Date", Toast.LENGTH_SHORT).show(); return false; }
        if (spinnerCategory.getSelectedItemPosition() == 0) { Toast.makeText(this, "Select Category", Toast.LENGTH_SHORT).show(); return false; }


        if (imageUri == null) {
            Toast.makeText(this, "Please attach an image for better reach", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void uploadToCloudinary(Uri uri) {
        progressDialog.show();
        MediaManager.get().upload(uri).callback(new UploadCallback() {
            @Override public void onSuccess(String requestId, Map resultData) {
                saveDataToFirestore(resultData.get("secure_url").toString());
            }
            @Override public void onError(String requestId, ErrorInfo error) {
                progressDialog.dismiss();
                Toast.makeText(ReportLostActivity.this, "Upload Error: " + error.getDescription(), Toast.LENGTH_SHORT).show();
            }
            @Override public void onStart(String requestId) {}
            @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
            @Override public void onReschedule(String requestId, ErrorInfo error) {}
        }).dispatch();
    }

    private void saveDataToFirestore(String imageUrl) {
        if(!progressDialog.isShowing()) progressDialog.show();

        String userId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : "Guest";

        ReportModel report = new ReportModel(
                userId,
                etItemName.getText().toString().trim(),
                spinnerCategory.getSelectedItem().toString(),
                etDescription.getText().toString().trim(),
                etLocation.getText().toString().trim(),
                etDate.getText().toString(),
                etTime.getText().toString(),
                imageUrl,
                "LOST" // Status set to LOST
        );

        db.collection("LostItems").add(report).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(this, "Report Submitted Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to submit. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}