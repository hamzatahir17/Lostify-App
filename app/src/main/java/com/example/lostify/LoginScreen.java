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
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvSignUp;
    private ImageView btnEyeLogin;
    private boolean isVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginScreen.this, MainActivity.class));
            finish();
            return;
        }

        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        btnEyeLogin = findViewById(R.id.btnEyeLogin);

        btnEyeLogin.setOnClickListener(v -> {

            int selection = etPassword.getSelectionEnd();

            if (isVisible) {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnEyeLogin.setAlpha(0.5f);
            } else {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnEyeLogin.setAlpha(1.0f);
            }
            isVisible = !isVisible;

            etPassword.setSelection(selection);

            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
            }
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Required!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(LoginScreen.this, OtpActivity.class);
            intent.putExtra("userEmail", email);
            intent.putExtra("userPass", password);
            intent.putExtra("isLogin", true);
            startActivity(intent);
        });

        tvSignUp.setOnClickListener(v -> startActivity(new Intent(LoginScreen.this, SignupActivity.class)));
    }
}