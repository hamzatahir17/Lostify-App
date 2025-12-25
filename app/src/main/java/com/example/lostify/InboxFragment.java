package com.example.lostify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InboxFragment extends Fragment {

    private RecyclerView recyclerView;
    private InboxAdapter adapter;
    private List<InboxModel> conversationList = new ArrayList<>();
    private Map<String, InboxModel> partnerMap = new HashMap<>();
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUserId = (FirebaseAuth.getInstance().getCurrentUser() != null) ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        recyclerView = view.findViewById(R.id.recyclerInbox);
        adapter = new InboxAdapter(getContext(), conversationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadConversations();
    }

    private void loadConversations() {
        // Query sorted by timestamp descending
        db.collection("chats")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        ChatModel chat = dc.getDocument().toObject(ChatModel.class);

                        // Check if I am involved
                        if (chat.getSenderId().equals(currentUserId) || chat.getReceiverId().equals(currentUserId)) {
                            String partnerId = chat.getSenderId().equals(currentUserId) ?
                                    chat.getReceiverId() : chat.getSenderId();

                            String msg = chat.getMessageText();
                            String time = formatTime(chat.getTimestamp());
                            long ts = chat.getTimestamp();

                            //  CASE: Document ADDED or MODIFIED
                            if (dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {

                                if (partnerMap.containsKey(partnerId)) {
                                    // 1. Purana data nikaal kar update karo
                                    InboxModel existing = partnerMap.get(partnerId);

                                    // Update content only if this message is newer
                                    if (ts >= existing.getRawTimestamp()) {
                                        existing.lastMessage = msg;
                                        existing.time = time;
                                        existing.setRawTimestamp(ts);
                                        sortAndRefresh();
                                    }
                                } else {
                                    // 2. Naya user hai toh fetch karo
                                    InboxModel newItem = new InboxModel(partnerId, "Loading...", msg, time);
                                    newItem.setRawTimestamp(ts);

                                    partnerMap.put(partnerId, newItem);
                                    conversationList.add(newItem);

                                    fetchPartnerName(partnerId, newItem);
                                }
                            }
                        }
                    }
                });
    }

    private void fetchPartnerName(String partnerId, InboxModel model) {
        db.collection("users").document(partnerId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        model.userName = doc.getString("name");
                    } else {
                        model.userName = "Unknown User";
                    }
                    sortAndRefresh();
                });
    }

    private void sortAndRefresh() {
        //  CRITICAL FIX: WhatsApp style sorting (Latest on Top)
        Collections.sort(conversationList, (o1, o2) -> Long.compare(o2.getRawTimestamp(), o1.getRawTimestamp()));
        adapter.notifyDataSetChanged();
    }

    private String formatTime(long timestamp) {
        if (timestamp <= 0) return "Just now";
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(timestamp));
    }
}