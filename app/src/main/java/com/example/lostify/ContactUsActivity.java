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

        // 1. Views dhoondna
        ImageView btnBack = findViewById(R.id.btnBack);
        CardView cardEmail = findViewById(R.id.cardEmailUs);

        // 2. Back Button Logic
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 3. Email Card Logic (Seedha Gmail Kholega)
        cardEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmailApp();
            }
        });
    }

    private void openEmailApp() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        // "mailto:" lagane se phone samajh jata hai ke sirf email app kholna hai
        intent.setData(Uri.parse("mailto:support@lostify.com"));

        // Optional: Subject pehle se likha hua aa jaye
        intent.putExtra(Intent.EXTRA_SUBJECT, "Help Needed - Lostify");

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(ContactUsActivity.this, "No email app found.", Toast.LENGTH_SHORT).show();
        }
    }
}