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
                // Return to the Settings screen
                finish();
            }
        });

        // --- Contact Us Logic ---
        // Using View type to support both TextView or CardView clicks
        View btnContact = findViewById(R.id.btnContact);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmailSupport();
            }
        });

        // --- Terms & Conditions Logic ---
        TextView btnTerms = findViewById(R.id.btnTerms);
        btnTerms.setOnClickListener(v ->
                Toast.makeText(HelpActivity.this, "Soon...", Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * Opens the device's default email application to contact support
     */
    private void openEmailSupport() {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            // "mailto:" protocol ensures only email apps handle this intent
            intent.setData(Uri.parse("mailto:support@lostify.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Help Needed - Lostify");

            startActivity(intent);
        } catch (Exception e) {
            // Error handling if no email client is installed
            Toast.makeText(HelpActivity.this, "No email app found.", Toast.LENGTH_SHORT).show();
        }
    }
}