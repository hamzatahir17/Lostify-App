package com.example.lostify;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.lostify.BuildConfig;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReportFoundActivity extends AppCompatActivity {

    private EditText etItemName, etDescription, etLocation, etDate, etTime;
    private Spinner spinnerCategory;
    private ImageView ivSelectedImage;
    private Button btnUploadImage, btnSubmit;
    private Uri imageUri;
    private static final int IMAGE_REQ = 1;
    private ProgressDialog progressDialog;

    // Keys ab BuildConfig se aayengi
    private static final String CLOUD_NAME = BuildConfig.CLOUDINARY_CLOUD_NAME;
    private static final String API_KEY = BuildConfig.CLOUDINARY_API_KEY;
    private static final String API_SECRET = BuildConfig.CLOUDINARY_API_SECRET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_found);

        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", CLOUD_NAME);
            config.put("api_key", API_KEY);
            config.put("api_secret", API_SECRET);
            MediaManager.init(this, config);
        } catch (IllegalStateException e) { }

        etItemName = findViewById(R.id.etItemName);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSubmit = findViewById(R.id.btnSubmit);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Report...");
        progressDialog.setCancelable(false);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        String[] categories = {"Electronics", "Books", "Bags", "Clothing", "Wallet/Keys", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);
        setupPickers();

        btnUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE_REQ);
        });

        btnSubmit.setOnClickListener(v -> {
            if (validateFields()) { uploadToCloudinary(); }
        });
    }

    private void setupPickers() {
        etDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) -> etDate.setText(d + "/" + (m + 1) + "/" + y),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
        etTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, h, m) -> {
                String amPm = (h >= 12) ? "PM" : "AM";
                int h12 = (h > 12) ? h - 12 : (h == 0 ? 12 : h);
                etTime.setText(String.format("%d:%02d %s", h12, m, amPm));
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQ && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ivSelectedImage.setImageURI(imageUri);
            ivSelectedImage.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateFields() {
        if (etItemName.getText().toString().isEmpty()) { etItemName.setError("Required"); return false; }
        if (imageUri == null) { Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show(); return false; }
        return true;
    }

    private void uploadToCloudinary() {
        progressDialog.show();
        MediaManager.get().upload(imageUri).callback(new UploadCallback() {
            @Override public void onStart(String requestId) {}
            @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
            @Override public void onSuccess(String requestId, Map resultData) {
                saveDataToFirestore((String) resultData.get("secure_url"));
            }
            @Override public void onError(String requestId, ErrorInfo error) {
                progressDialog.dismiss();
                Toast.makeText(ReportFoundActivity.this, "Error: " + error.getDescription(), Toast.LENGTH_SHORT).show();
            }
            @Override public void onReschedule(String requestId, ErrorInfo error) {}
        }).dispatch();
    }

    private void saveDataToFirestore(String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "Guest";
        ReportModel report = new ReportModel(userId, etItemName.getText().toString(), spinnerCategory.getSelectedItem().toString(),
                etDescription.getText().toString(), etLocation.getText().toString(), etDate.getText().toString(),
                etTime.getText().toString(), imageUrl, "FOUND");

        db.collection("FoundItems").add(report).addOnSuccessListener(doc -> {
            progressDialog.dismiss();
            finish();
        }).addOnFailureListener(e -> progressDialog.dismiss());
    }
}