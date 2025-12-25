package com.example.lostify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    RecyclerView chatRecyclerView;
    EditText messageEditText;
    ImageView sendButton;

    ChatAdapter chatAdapter;
    List<ChatModel> messageList;

    private String currentUserId;
    private String receiverId; // 🔴 Jisko message bhejna hai
    private FirebaseFirestore db;

    public ChatFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Activity se Receiver ID receive karein
        if (getArguments() != null) {
            receiverId = getArguments().getString("receiverId");
        }
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        chatRecyclerView = view.findViewById(R.id.chat_recycler_view);
        messageEditText = view.findViewById(R.id.message_edit_text);
        sendButton = view.findViewById(R.id.send_button);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(), messageList); // Adapter setup

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true); // Latest message neeche dikhega
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        // 🔴 Send Button Click
        sendButton.setOnClickListener(v -> {
            String messageText = messageEditText.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
            }
        });

        // 🔴 Listen for Messages
        listenForMessages();
    }

    private void sendMessage(String messageText) {
        long timestamp = System.currentTimeMillis();
        // Create Object with Receiver ID
        ChatModel newMessage = new ChatModel(currentUserId, receiverId, messageText, timestamp);

        db.collection("chats").add(newMessage)
                .addOnSuccessListener(doc -> messageEditText.setText("")) // Clear Box
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to send", Toast.LENGTH_SHORT).show());
    }

    private void listenForMessages() {
        db.collection("chats")
                .orderBy("timestamp", Query.Direction.ASCENDING) // Time ke hisaab se sort
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            ChatModel m = dc.getDocument().toObject(ChatModel.class);

                            // 🔴 Filter Logic: Kya ye message mere aur uske beech hai?
                            if ((m.getSenderId().equals(currentUserId) && m.getReceiverId().equals(receiverId)) ||
                                    (m.getSenderId().equals(receiverId) && m.getReceiverId().equals(currentUserId))) {

                                messageList.add(m);
                                chatAdapter.notifyItemInserted(messageList.size() - 1);
                                chatRecyclerView.scrollToPosition(messageList.size() - 1);
                            }
                        }
                    }
                });
    }
}