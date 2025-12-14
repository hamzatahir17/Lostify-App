package com.example.lostify;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat); // Step 1 wali file

        // Back Button (Toolbar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Title set karein (jo piche se aaya hai)
            String name = getIntent().getStringExtra("receiverName");
            if (name != null) getSupportActionBar().setTitle(name);
        }

        // --- PROFESSIONAL LOGIC: FRAGMENT LOAD KARNA ---
        if (savedInstanceState == null) {
            // 1. Aapka purana ChatFragment create karein
            ChatFragment chatFragment = new ChatFragment();

            // 2. Agar data bhejna ho (Optional)
            // Bundle args = new Bundle();
            // args.putString("userId", getIntent().getStringExtra("receiverId"));
            // chatFragment.setArguments(args);

            // 3. Fragment ko Container mein daal dein
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, chatFragment);
            transaction.commit();
        }
    }

    // Back button dabane par activity band honi chahiye
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}