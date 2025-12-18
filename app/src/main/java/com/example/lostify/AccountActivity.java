package com.example.lostify;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // 1. Back Button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish()); // Activity band kar ke wapis bhej dega

        // 2. Update Button
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            // Yahan baad mein Database update ka code aayega
            Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });

        // 3. Logout Button
        TextView tvLogout = findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(v -> {
            // Yahan Logout ka logic aayega
            Toast.makeText(this, "Logging Out...", Toast.LENGTH_SHORT).show();
        });
    }
}