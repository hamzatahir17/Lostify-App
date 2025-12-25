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

public class LoginScreen extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvSignUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // 1. INITIALIZE FIREBASE (Sabse Pehle)
        mAuth = FirebaseAuth.getInstance();

        // 2. AUTO-LOGIN CHECK (Yahan shift kar diya)
        // Check karein agar user pehle se login hai
        if (mAuth.getCurrentUser() != null) {
            // User login hai -> Home Screen par bhejo
            Intent intent = new Intent(LoginScreen.this, MainActivity.class);
            startActivity(intent);
            finish(); // Login screen band karo
            return;   // Code yahi rok do, neeche wala code run na ho
        }

        // 3. Connect Views (Agar user login nahi hai to yeh chalega)
        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    etEmail.setError("Required!");
                    return;
                }

                loginUser(email, password);
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginScreen.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginScreen.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Error";
                            Toast.makeText(LoginScreen.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}