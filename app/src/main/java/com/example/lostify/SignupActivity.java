package com.example.lostify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class SignupActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword;
    private MaterialButton btnSignup;
    private TextView tvLoginLink;
    private ImageView btnEyeSignup;
    private boolean isVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmailSignup);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        btnEyeSignup = findViewById(R.id.btnEyeSignup);

        btnEyeSignup.setOnClickListener(v -> {
            int selection = etPassword.getSelectionEnd();

            if (isVisible) {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnEyeSignup.setAlpha(0.5f);
            } else {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnEyeSignup.setAlpha(1.0f);
            }
            isVisible = !isVisible;

            etPassword.setSelection(selection);

            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
            }
        });

        btnSignup.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                etPassword.setError("Password must be at least 6 characters");
                return;
            }

            Intent intent = new Intent(SignupActivity.this, OtpActivity.class);
            intent.putExtra("userName", name);
            intent.putExtra("userEmail", email);
            intent.putExtra("userPass", password);
            intent.putExtra("isLogin", false);
            startActivity(intent);
        });

        tvLoginLink.setOnClickListener(v -> finish());
    }
}