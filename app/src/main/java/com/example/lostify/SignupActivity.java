package com.example.lostify;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SignupActivity extends AppCompatActivity {

    // Variables (Phone hata diya)
    private EditText etFullName, etEmail;
    private MaterialButton btnSignup;
    private TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 1. IDs Connect karna
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmailSignup);
        // Phone wali line remove kar di

        btnSignup = findViewById(R.id.btnSignup);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // 2. "Sign Up" Button Logic
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                // Phone string remove kar di

                // Validation: Phone check hata diya
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
                    Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Success Message
                Toast.makeText(SignupActivity.this, "Account Created for " + name, Toast.LENGTH_SHORT).show();
            }
        });

        // 3. "Log In" Link Logic
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}