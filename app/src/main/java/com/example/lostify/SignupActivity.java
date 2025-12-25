package com.example.lostify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    // UI Components
    private EditText etFullName, etEmail, etPassword;
    private MaterialButton btnSignup;
    private TextView tvLoginLink;

    // Firebase Instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // --- 1. INITIALIZE FIREBASE ---
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // --- 2. CONNECT VIEWS ---
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmailSignup);
        etPassword = findViewById(R.id.etPassword);

        btnSignup = findViewById(R.id.btnSignup);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // --- 3. SIGN UP BUTTON LOGIC ---
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // --- INPUT VALIDATION ---
                if (TextUtils.isEmpty(name)) {
                    etFullName.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password is required");
                    return;
                }
                if (password.length() < 6) {
                    etPassword.setError("Password must be at least 6 characters");
                    return;
                }

                // Proceed to register user in Firebase
                registerUser(name, email, password);
            }
        });

        // --- 4. LOGIN LINK LOGIC ---
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Return to Login Activity
            }
        });
    }

    /**
     * Creates a new account in Firebase Authentication
     */
    private void registerUser(String name, String email, String password) {
        Toast.makeText(this, "Creating Account...", Toast.LENGTH_SHORT).show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Authentication successful, now save additional details to Firestore
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                saveUserToFirestore(user.getUid(), name, email);
                            }
                        } else {
                            // Handle errors
                            String error = task.getException() != null ? task.getException().getMessage() : "Unknown Error";
                            Toast.makeText(SignupActivity.this, "Registration Failed: " + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Saves user details to Firestore Database
     */
    private void saveUserToFirestore(String uid, String name, String email) {
        // Create a data map
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", uid);

        // 🔴 CHANGE: Key must be "name" (not fullName) for Inbox Logic
        userMap.put("name", name);

        userMap.put("email", email);
        userMap.put("createdAt", System.currentTimeMillis());

        // Save to "users" collection
        db.collection("users").document(uid).set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();

                            // Navigate to Home Screen
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SignupActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}