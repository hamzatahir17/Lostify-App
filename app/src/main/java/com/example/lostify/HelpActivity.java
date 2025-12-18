package com.example.lostify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // --- Back Button Logic ---
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Wapis Settings par bhejne ke liye
            }
        });

        // --- Contact Us Logic (UPDATED: Ab ye Email App kholega) ---
        // Maine View use kiya hai taake agar ye TextView ho ya CardView, dono par chale
        View btnContact = findViewById(R.id.btnContact);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmailSupport();
            }
        });

        // --- Terms & Conditions Logic (SAME: "Soon...") ---
        TextView btnTerms = findViewById(R.id.btnTerms);
        btnTerms.setOnClickListener(v ->
                Toast.makeText(HelpActivity.this, "Soon...", Toast.LENGTH_SHORT).show()
        );
    }

    // Email kholne ka function
    private void openEmailSupport() {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            // "mailto:" ka matlab hai seedha email app khulega
            intent.setData(Uri.parse("mailto:support@lostify.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Help Needed - Lostify");

            startActivity(intent);
        } catch (Exception e) {
            // Agar phone mein koi email app na ho
            Toast.makeText(HelpActivity.this, "No email app found.", Toast.LENGTH_SHORT).show();
        }
    }
}