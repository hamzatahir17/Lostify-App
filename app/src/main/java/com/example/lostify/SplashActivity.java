package com.example.lostify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 3 Seconds (3000 milliseconds) ka delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Splash se Login Screen par jane ka rasta
                Intent intent = new Intent(SplashActivity.this, LoginScreen.class);
                startActivity(intent);

                // Splash activity ko band kar dein taake user 'Back' dabaye to wapis splash na aaye
                finish();
            }
        }, 2000);
    }
}