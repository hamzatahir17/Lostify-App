package com.example.lostify;

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

        // --- Contact Us Logic ---
        TextView btnContact = findViewById(R.id.btnContact);
        btnContact.setOnClickListener(v ->
                Toast.makeText(HelpActivity.this, "Contacting Support...", Toast.LENGTH_SHORT).show()
        );

        // --- Terms & Conditions Logic ---
        TextView btnTerms = findViewById(R.id.btnTerms);
        btnTerms.setOnClickListener(v ->
                Toast.makeText(HelpActivity.this, "Opening Terms...", Toast.LENGTH_SHORT).show()
        );
    }
}