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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageView sendButton;
    private ChatAdapter chatAdapter;
    private List<ChatModel> messageList;
    private String currentUserId;
    private String receiverId;
    private String receiverImage;
    private FirebaseFirestore db;


    private ListenerRegistration seenListener;

    public ChatFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            receiverId = getArguments().getString("receiverId");
            receiverImage = getArguments().getString("receiverImage");
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
        chatAdapter = new ChatAdapter(getContext(), messageList, receiverImage);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> {
            String messageText = messageEditText.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
            }
        });

        listenForMessages();
    }

    private void sendMessage(String messageText) {
        long timestamp = System.currentTimeMillis();
        ChatModel newMessage = new ChatModel(currentUserId, receiverId, messageText, timestamp, false);

        db.collection("chats").add(newMessage)
                .addOnSuccessListener(doc -> messageEditText.setText(""))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to send", Toast.LENGTH_SHORT).show());
    }

    private void listenForMessages() {

        seenListener = db.collection("chats")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            ChatModel m = dc.getDocument().toObject(ChatModel.class);

                            if ((m.getSenderId().equals(currentUserId) && m.getReceiverId().equals(receiverId)) ||
                                    (m.getSenderId().equals(receiverId) && m.getReceiverId().equals(currentUserId))) {

                                messageList.add(m);
                                chatAdapter.notifyItemInserted(messageList.size() - 1);
                                chatRecyclerView.scrollToPosition(messageList.size() - 1);


                                if (m.getSenderId().equals(receiverId) && !m.isSeen()) {
                                    dc.getDocument().getReference().update("seen", true);
                                }
                            }
                        }
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (seenListener != null) {
            seenListener.remove();
        }
    }
}