package com.example.lostify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageView sendButton, btnBack, imgToolbarProfile;
    private TextView tvToolbarName;

    private ChatAdapter chatAdapter;
    private List<ChatModel> chatList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String senderId, receiverId;
    private RequestQueue requestQueue;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private ListenerRegistration seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            senderId = auth.getCurrentUser().getUid();
        }

        requestQueue = Volley.newRequestQueue(this);
        receiverId = getIntent().getStringExtra("receiverId");

        if (receiverId == null || receiverId.isEmpty()) {
            Toast.makeText(this, "User Error!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_button);
        btnBack = findViewById(R.id.btnBack);
        imgToolbarProfile = findViewById(R.id.imgToolbarProfile);
        tvToolbarName = findViewById(R.id.tvToolbarName);

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);

        chatRecyclerView.setAdapter(chatAdapter);

        loadReceiverDetails();
        loadMessages();
        seenMessage();

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString();
            if (TextUtils.isEmpty(message)) return;
            sendMessage(message);
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
            intent.putExtra("OPEN_INBOX", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void seenMessage() {
        seenListener = db.collection("chats")
                .whereEqualTo("receiverId", senderId)
                .whereEqualTo("senderId", receiverId)
                .whereEqualTo("seen", false)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("seen", true);
                            dc.getDocument().getReference().update(hashMap);
                        }
                    }
                });
    }

    private void loadReceiverDetails() {
        if (receiverId == null) return;

        tvToolbarName.setText("Loading...");

        db.collection("users").document(receiverId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) return;

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        if (name == null || name.isEmpty()) {
                            name = documentSnapshot.getString("username");
                        }

                        if (name != null && !name.isEmpty()) {
                            tvToolbarName.setText(name);
                        } else {
                            tvToolbarName.setText("User");
                        }

                        String receiverImage = documentSnapshot.getString("profileImage");
                        if (receiverImage != null && !receiverImage.isEmpty()) {
                            chatAdapter.setReceiverProfileImage(receiverImage);

                            if (!isFinishing() && !isDestroyed()) {
                                try {
                                    Glide.with(ChatActivity.this)
                                            .load(receiverImage)
                                            .placeholder(R.drawable.placeholder_loading)
                                            .into(imgToolbarProfile);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
    }

    private void sendMessage(String message) {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("senderId", senderId);
        messageMap.put("receiverId", receiverId);
        messageMap.put("messageText", message);
        messageMap.put("timestamp", new Date().getTime());
        messageMap.put("seen", false);

        messageEditText.setText("");

        db.collection("chats").add(messageMap)
                .addOnSuccessListener(documentReference -> getTokenAndSendNotification(message));
    }

    private void loadMessages() {
        db.collection("chats")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            ChatModel chat = dc.getDocument().toObject(ChatModel.class);
                            if ((chat.getSenderId() != null && chat.getReceiverId() != null) &&
                                    ((chat.getSenderId().equals(senderId) && chat.getReceiverId().equals(receiverId)) ||
                                            (chat.getSenderId().equals(receiverId) && chat.getReceiverId().equals(senderId)))) {
                                chatList.add(chat);
                                chatAdapter.notifyItemInserted(chatList.size() - 1);
                                chatRecyclerView.scrollToPosition(chatList.size() - 1);
                            }
                        }
                    }
                });
    }

    private void getTokenAndSendNotification(String message) {
        db.collection("users").document(receiverId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String token = documentSnapshot.getString("fcmToken");
                        if (token != null && !token.isEmpty()) {
                            sendV1Notification(token, message);
                        }
                    }
                });
    }

    private void sendV1Notification(String deviceToken, String message) {
        executor.execute(() -> {
            try {
                InputStream stream = ChatActivity.this.getAssets().open("service-account.json");
                GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                        .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));
                credentials.refreshIfExpired();
                String accessToken = credentials.getAccessToken().getTokenValue();

                JSONObject json = new JSONObject();
                JSONObject messageObj = new JSONObject();
                JSONObject dataObj = new JSONObject();

                dataObj.put("title", "New Message");
                dataObj.put("body", message);
                dataObj.put("type", "chat");
                dataObj.put("senderId", senderId);

                messageObj.put("token", deviceToken);
                messageObj.put("data", dataObj);

                json.put("message", messageObj);

                runOnUiThread(() -> sendVolleyRequest(json, accessToken));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Notification Error: File Not Found or Auth Failed", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void sendVolleyRequest(JSONObject jsonBody, String accessToken) {
        String projectId = "lostify-2e249";
        String url = "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {},
                error -> {}
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };
        requestQueue.add(request);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (seenListener != null) seenListener.remove();
    }
}