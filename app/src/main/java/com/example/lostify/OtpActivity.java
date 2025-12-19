package com.example.lostify;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class OtpActivity extends AppCompatActivity {

    // UI Components for OTP input
    private EditText otp1, otp2, otp3, otp4;
    private MaterialButton btnVerify;
    private TextView tvResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // Initialize UI components
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        btnVerify = findViewById(R.id.btnVerifyOTP);
        tvResend = findViewById(R.id.tvResend);

        // Retrieve the email address passed from the Login screen
        String userEmail = getIntent().getStringExtra("userEmail");
        if (userEmail != null) {
            Toast.makeText(this, "Code sent to: " + userEmail, Toast.LENGTH_LONG).show();
        }

        // Initialize the focus movement logic between input boxes
        setupOTPInputs();

        // Verification button logic
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Concatenate individual digits into a single string
                String code = otp1.getText().toString().trim() +
                        otp2.getText().toString().trim() +
                        otp3.getText().toString().trim() +
                        otp4.getText().toString().trim();

                if (code.length() < 4) {
                    Toast.makeText(OtpActivity.this, "Please enter valid 4-digit code", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OtpActivity.this, "Verification Successful!", Toast.LENGTH_SHORT).show();

                    // Navigate to MainActivity
                    Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                    startActivity(intent);

                    // Clear the activity stack so the user cannot navigate back to the OTP screen
                    finishAffinity();
                }
            }
        });
    }

    /**
     * Handles automatic focus movement to the next EditText when a digit is entered.
     */
    private void setupOTPInputs() {
        otp1.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) { otp2.requestFocus(); }
            }
            public void afterTextChanged(Editable s) {}
        });

        otp2.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) { otp3.requestFocus(); }
            }
            public void afterTextChanged(Editable s) {}
        });

        otp3.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) { otp4.requestFocus(); }
            }
            public void afterTextChanged(Editable s) {}
        });
    }
}