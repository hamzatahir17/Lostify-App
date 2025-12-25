package com.example.lostify;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

// ✅ SAHI IMPORT
import com.example.lostify.BuildConfig;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReportLostActivity extends AppCompatActivity {

    private EditText etItemName, etDescription, etLocation, etDate, etTime;
    private Spinner spinnerCategory;
    private Button btnSubmit, btnUploadImage;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Uri imageUri;
    private boolean isImageSelected = false;

    // Keys secured via BuildConfig
    private static final String CLOUD_NAME = BuildConfig.CLOUDINARY_CLOUD_NAME;
    private static final String API_KEY = BuildConfig.CLOUDINARY_API_KEY;
    private static final String API_SECRET = BuildConfig.CLOUDINARY_API_SECRET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_lost);

        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", CLOUD_NAME);
            config.put("api_key", API_KEY);
            config.put("api_secret", API_SECRET);
            MediaManager.init(this, config);
        } catch (IllegalStateException e) { }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etItemName = findViewById(R.id.etItemName);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnUploadImage = findViewById(R.id.btnUploadImage);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        String[] categories = {"Electronics", "Books", "Bags", "Clothing", "Wallet/Keys", "Others"};
        spinnerCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));

        setupPickers();

        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        imageUri = result;
                        isImageSelected = true;
                        btnUploadImage.setText("Image Selected ✅");
                    }
                });

        btnUploadImage.setOnClickListener(v -> mGetContent.launch("image/*"));

        btnSubmit.setOnClickListener(v -> {
            if (validateFields()) {
                if (isImageSelected) uploadToCloudinary(imageUri);
                else saveDataToFirestore(null);
            }
        });
    }

    private void uploadToCloudinary(Uri uri) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();

        MediaManager.get().upload(uri).callback(new UploadCallback() {
            @Override public void onSuccess(String requestId, Map resultData) {
                pd.dismiss();
                saveDataToFirestore(resultData.get("secure_url").toString());
            }
            @Override public void onError(String requestId, ErrorInfo error) {
                pd.dismiss();
                Toast.makeText(ReportLostActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
            @Override public void onStart(String requestId) {}
            @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
            @Override public void onReschedule(String requestId, ErrorInfo error) {}
        }).dispatch();
    }

    private void saveDataToFirestore(String imageUrl) {
        String userId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : "Guest";
        ReportModel report = new ReportModel(userId, etItemName.getText().toString(), spinnerCategory.getSelectedItem().toString(),
                etDescription.getText().toString(), etLocation.getText().toString(), etDate.getText().toString(),
                etTime.getText().toString(), imageUrl, "LOST");

        db.collection("LostItems").add(report).addOnCompleteListener(task -> finish());
    }

    private void setupPickers() {
        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) -> etDate.setText(d + "/" + (m + 1) + "/" + y),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
        etTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, h, m) -> etTime.setText(h + ":" + m),
                    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
        });
    }

    private boolean validateFields() {
        return !TextUtils.isEmpty(etItemName.getText());
    }
}