package com.example.lostify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    RecyclerView chatRecyclerView;
    EditText messageEditText;
    ImageView sendButton;

    ChatAdapter chatAdapter;
    List<ChatModel> messageList;

    private String currentUserId = "user1";

    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatRecyclerView = view.findViewById(R.id.chat_recycler_view);
        messageEditText = view.findViewById(R.id.message_edit_text);
        sendButton = view.findViewById(R.id.send_button);

        messageList = new ArrayList<>();

        chatAdapter = new ChatAdapter(getContext(), messageList, currentUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> {
            String messageText = messageEditText.getText().toString().trim();

            if (!messageText.isEmpty()) {
                long timestamp = System.currentTimeMillis();
                ChatModel newMessage = new ChatModel(currentUserId, messageText, timestamp);

                messageList.add(newMessage);
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                chatRecyclerView.scrollToPosition(messageList.size() - 1);
                messageEditText.setText("");
            }
        });

        loadDummyData();
    }

    private void loadDummyData() {
        long time = System.currentTimeMillis();

        messageList.add(new ChatModel("user2", "Hello, I found your backpack.", time - 20000));
        messageList.add(new ChatModel("user1", "That's great! Where did you find it?", time - 10000));
        messageList.add(new ChatModel("user2", "I found it near the library.", time - 5000));
        messageList.add(new ChatModel("user1", "Okay, I am on my way.", time));

        chatAdapter.notifyDataSetChanged();
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
    }
}