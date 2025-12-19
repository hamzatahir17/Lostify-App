package com.example.lostify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class LoginScreen extends AppCompatActivity {

    // UI Components
    private EditText etEmail;
    private MaterialButton btnGetOTP;
    private TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Initialize UI components by their XML IDs
        etEmail = findViewById(R.id.etEmailLogin);
        btnGetOTP = findViewById(R.id.btnGetOTP);
        tvSignUp = findViewById(R.id.tvSignUp);

        // Logic for "Get OTP" button click
        btnGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();

                // Input Validation: Ensure the email field is not empty
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email is Required!");
                    return;
                }

                Toast.makeText(LoginScreen.this, "Sending OTP...", Toast.LENGTH_SHORT).show();

                // Navigate to OtpActivity and pass the email address for verification
                Intent intent = new Intent(LoginScreen.this, OtpActivity.class);
                intent.putExtra("userEmail", email);
                startActivity(intent);
            }
        });

        // Navigation to the Signup screen
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}