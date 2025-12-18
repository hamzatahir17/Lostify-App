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

    // Variables
    private EditText etEmail;
    private MaterialButton btnGetOTP;
    private TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make sure aapka XML file name yahi hai
        setContentView(R.layout.activity_login_screen);

        // 1. IDs Connect karna
        etEmail = findViewById(R.id.etEmailLogin);
        btnGetOTP = findViewById(R.id.btnGetOTP);
        tvSignUp = findViewById(R.id.tvSignUp);

        // 2. "Get OTP" Button Logic
        btnGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();

                // Validation: Check karein email khali to nahi
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email is Required!");
                    return;
                }

                // User ko feedback dein
                Toast.makeText(LoginScreen.this, "Otp sending ...", Toast.LENGTH_SHORT).show();

                // OTP Screen par shift hon
                Intent intent = new Intent(LoginScreen.this, OtpActivity.class);
                intent.putExtra("userEmail", email); // Email agle page par bhejen
                startActivity(intent);
            }
        });

        // 3. "Sign Up" Link Logic (Create Account Page par jane ke liye)
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}