package com.example.lostify;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatActivity extends AppCompatActivity {

    private ImageView btnBack, imgToolbarProfile;
    private TextView tvToolbarName;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = FirebaseFirestore.getInstance();

        btnBack = findViewById(R.id.btnBack);
        imgToolbarProfile = findViewById(R.id.imgToolbarProfile);
        tvToolbarName = findViewById(R.id.tvToolbarName);

        String receiverId = getIntent().getStringExtra("receiverId");
        String receiverName = getIntent().getStringExtra("receiverName");

        tvToolbarName.setText(receiverName);
        btnBack.setOnClickListener(v -> finish());

        if (receiverId != null) {
            db.collection("users").document(receiverId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String imageUrl = "";
                        if (documentSnapshot.exists()) {
                            imageUrl = documentSnapshot.getString("profileImage");
                            String name = documentSnapshot.getString("name");

                            if (name != null && !name.isEmpty()) {
                                tvToolbarName.setText(name);
                            }

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(imageUrl)
                                        .circleCrop()
                                        .placeholder(R.drawable.placeholder_loading)
                                        .transition(DrawableTransitionOptions.withCrossFade(500))
                                        .into(imgToolbarProfile);
                            }
                        }
                        loadChatFragment(receiverId, imageUrl);
                    })
                    .addOnFailureListener(e -> loadChatFragment(receiverId, ""));
        }
    }

    private void loadChatFragment(String receiverId, String receiverImage) {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            ChatFragment chatFragment = new ChatFragment();
            Bundle args = new Bundle();
            args.putString("receiverId", receiverId);
            args.putString("receiverImage", receiverImage);
            chatFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, chatFragment);
            transaction.commit();
        }
    }
}