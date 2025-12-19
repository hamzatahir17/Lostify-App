package com.example.lostify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        // Initialize UI components
        ImageView btnBack = findViewById(R.id.btnBack);
        CardView cardEmail = findViewById(R.id.cardEmailUs);

        // Handle back button click to return to the previous screen
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Trigger email intent when the contact card is clicked
        cardEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmailApp();
            }
        });
    }

    /**
     * Creates and starts an Intent to open the device's email client.
     */
    private void openEmailApp() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        // "mailto:" ensures only email clients handle this intent
        intent.setData(Uri.parse("mailto:support@lostify.com"));

        // Optional: Pre-fill the email subject line
        intent.putExtra(Intent.EXTRA_SUBJECT, "Help Needed - Lostify");

        try {
            startActivity(intent);
        } catch (Exception e) {
            // Handle cases where no email application is installed
            Toast.makeText(ContactUsActivity.this, "No email app found.", Toast.LENGTH_SHORT).show();
        }
    }
}