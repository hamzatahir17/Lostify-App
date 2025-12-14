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
import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends Fragment {

    RecyclerView recyclerView;
    EditText searchBar;
    ConversationAdapter adapter;
    List<ConversationModel> conversationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // CHANGE: Ab hum naya layout use kar rahe hain
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ID ab naye layout wali use hogi
        recyclerView = view.findViewById(R.id.recyclerInbox);
        searchBar = view.findViewById(R.id.etSearchChats);

        conversationList = new ArrayList<>();

        // Dummy Data
        conversationList.add(new ConversationModel("2", "Ali Ahmed", "Thanks for the help!", "10:30 AM"));
        conversationList.add(new ConversationModel("3", "Library Admin", "Is this your bag?", "Yesterday"));
        conversationList.add(new ConversationModel("4", "Hamza", "Ok, coming.", "Mon"));

        adapter = new ConversationAdapter(getContext(), conversationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // (Optional) Search Logic: Future mein yahan filter ka code ayega
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Future: Filter list based on 's'
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}