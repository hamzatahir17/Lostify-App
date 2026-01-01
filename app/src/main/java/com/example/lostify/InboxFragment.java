package com.example.lostify;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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


    private List<InboxModel> masterList = new ArrayList<>();

    private Map<String, InboxModel> partnerMap = new HashMap<>();
    private FirebaseFirestore db;
    private String currentUserId;
    private EditText etSearch;

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
        etSearch = view.findViewById(R.id.etSearchChats);

        adapter = new InboxAdapter(getContext(), conversationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadConversations();
        setupSearchListener();
    }

    private void loadConversations() {
        db.collection("chats")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        ChatModel chat = dc.getDocument().toObject(ChatModel.class);

                        if (chat.getSenderId().equals(currentUserId) || chat.getReceiverId().equals(currentUserId)) {
                            String partnerId = chat.getSenderId().equals(currentUserId) ?
                                    chat.getReceiverId() : chat.getSenderId();

                            String msg = chat.getMessageText();
                            String time = formatTime(chat.getTimestamp());
                            long ts = chat.getTimestamp();
                            String senderId = chat.getSenderId();
                            boolean isSeen = chat.isSeen();

                            if (partnerMap.containsKey(partnerId)) {
                                InboxModel existing = partnerMap.get(partnerId);

                                if (ts >= existing.getRawTimestamp()) {
                                    existing.lastMessage = msg;
                                    existing.time = time;
                                    existing.senderId = senderId;
                                    existing.setRawTimestamp(ts);
                                    existing.seen = isSeen;

                                    sortAndRefresh();
                                }
                            } else {
                                InboxModel newItem = new InboxModel(partnerId, "Loading...", msg, time, null, senderId, ts, isSeen);
                                partnerMap.put(partnerId, newItem);


                                conversationList.add(newItem);
                                masterList.add(newItem);

                                fetchPartnerDetails(partnerId, newItem);
                            }
                        }
                    }
                });
    }

    private void fetchPartnerDetails(String partnerId, InboxModel model) {
        db.collection("users").document(partnerId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        model.userName = doc.getString("name");
                        model.image = doc.getString("profileImage");
                    } else {
                        model.userName = "Unknown User";
                        model.image = null;
                    }


                    sortAndRefresh();
                });
    }


    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        ArrayList<InboxModel> filteredList = new ArrayList<>();

        for (InboxModel item : masterList) {

            if (item.userName.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);
    }

    private void sortAndRefresh() {
        if (getActivity() != null) {

            Collections.sort(masterList, (o1, o2) -> Long.compare(o2.getRawTimestamp(), o1.getRawTimestamp()));


            String searchText = etSearch.getText().toString();

            getActivity().runOnUiThread(() -> {
                if (searchText.isEmpty()) {

                    conversationList.clear();
                    conversationList.addAll(masterList);
                    adapter.filterList(conversationList);
                } else {

                    filter(searchText);
                }
            });
        }
    }

    private String formatTime(long timestamp) {
        if (timestamp <= 0) return "Just now";
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(timestamp));
    }
}