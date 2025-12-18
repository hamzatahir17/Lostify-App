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

    // 1. Variables Declare karein
    private EditText otp1, otp2, otp3, otp4;
    private MaterialButton btnVerify;
    private TextView tvResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // 2. XML IDs ko Java se Connect karein
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        btnVerify = findViewById(R.id.btnVerifyOTP);
        tvResend = findViewById(R.id.tvResend);

        // 3. Login Screen se aaya hua Email receive karein (Optional)
        String userEmail = getIntent().getStringExtra("userEmail");
        if (userEmail != null) {
            Toast.makeText(this, "Code sent to: " + userEmail, Toast.LENGTH_LONG).show();
        }

        // 4. Cursor Movement Logic (Jadoo ✨)
        setupOTPInputs();

        // 5. Verify Button ka Kaam
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Saare numbers ko jod kar aik string banayen
                String code = otp1.getText().toString().trim() +
                        otp2.getText().toString().trim() +
                        otp3.getText().toString().trim() +
                        otp4.getText().toString().trim();

                if (code.length() < 4) {
                    Toast.makeText(OtpActivity.this, "Please enter valid 4-digit code", Toast.LENGTH_SHORT).show();
                } else {
                    // --- SUCCESS LOGIC ---

                    Toast.makeText(OtpActivity.this, "Verification Successful!", Toast.LENGTH_SHORT).show();

                    // 1. Home Screen (MainActivity) par jane ka intent
                    Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                    startActivity(intent);

                    // 2. Pichli screens (Login/OTP) ko khatam kr den
                    // Taake user Home se Back dabaye to wapis Login par na jaye
                    finishAffinity();
                }
            }
        });
    }

    // Yeh function cursor ko agle dabbay mein bhejta hai
    private void setupOTPInputs() {
        // Box 1 ke liye listener
        otp1.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) { otp2.requestFocus(); } // 1 likha to 2 pe jao
            }
            public void afterTextChanged(Editable s) {}
        });

        // Box 2 ke liye listener
        otp2.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) { otp3.requestFocus(); } // 1 likha to 3 pe jao
            }
            public void afterTextChanged(Editable s) {}
        });

        // Box 3 ke liye listener
        otp3.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) { otp4.requestFocus(); } // 1 likha to 4 pe jao
            }
            public void afterTextChanged(Editable s) {}
        });
    }
}