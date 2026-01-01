package com.example.lostify;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class OtpActivity extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4;
    private MaterialButton btnVerify;
    private TextView tvResend;
    private String generatedOTP, name, email, pass;
    private boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        btnVerify = findViewById(R.id.btnVerifyOTP);
        tvResend = findViewById(R.id.tvResend);

        name = getIntent().getStringExtra("userName");
        email = getIntent().getStringExtra("userEmail");
        pass = getIntent().getStringExtra("userPass");
        isLogin = getIntent().getBooleanExtra("isLogin", false);

        generateAndSendOTP();
        setupOTPInputs();
        startTimer();

        btnVerify.setOnClickListener(v -> {
            String enteredCode = otp1.getText().toString() + otp2.getText().toString() +
                    otp3.getText().toString() + otp4.getText().toString();

            if (enteredCode.equals(generatedOTP)) {
                performAuth();
            } else {
                Toast.makeText(this, "Invalid code", Toast.LENGTH_SHORT).show();
            }
        });

        tvResend.setOnClickListener(v -> {
            generateAndSendOTP();
            startTimer();
        });
    }

    private void generateAndSendOTP() {
        generatedOTP = String.format("%04d", new Random().nextInt(10000));
        sendEmail(generatedOTP);
    }

    private void sendEmail(String otp) {
        new Thread(() -> {
            final String username = BuildConfig.EMAIL_USERNAME;
            final String password = BuildConfig.EMAIL_PASSWORD;
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setSubject("Lostify - Verification Code");
                message.setText("Your verification code is: " + otp);
                Transport.send(message);
                runOnUiThread(() -> Toast.makeText(this, "OTP sent to email", Toast.LENGTH_SHORT).show());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void performAuth() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (isLogin) {
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(authResult -> navigateMain())
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(authResult -> {
                        String uid = authResult.getUser().getUid();
                        Map<String, Object> user = new HashMap<>();
                        user.put("uid", uid);
                        user.put("name", name);
                        user.put("email", email);
                        user.put("createdAt", System.currentTimeMillis());
                        FirebaseFirestore.getInstance().collection("users").document(uid).set(user)
                                .addOnSuccessListener(aVoid -> navigateMain());
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private void navigateMain() {
        Intent intent = new Intent(OtpActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startTimer() {
        new CountDownTimer(30000, 1000) {
            public void onTick(long millis) { tvResend.setText("Resend in 00:" + millis / 1000); tvResend.setEnabled(false); }
            public void onFinish() { tvResend.setText("Resend Code"); tvResend.setEnabled(true); }
        }.start();
    }

    private void setupOTPInputs() {
        otp1.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { if (s.length() == 1) otp2.requestFocus(); }
            public void afterTextChanged(Editable s) {}
        });
        otp2.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { if (s.length() == 1) otp3.requestFocus(); else if (s.length() == 0) otp1.requestFocus(); }
            public void afterTextChanged(Editable s) {}
        });
        otp3.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { if (s.length() == 1) otp4.requestFocus(); else if (s.length() == 0) otp2.requestFocus(); }
            public void afterTextChanged(Editable s) {}
        });
        otp4.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { if (s.length() == 0) otp3.requestFocus(); }
            public void afterTextChanged(Editable s) {}
        });
    }
}