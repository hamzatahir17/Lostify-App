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
    InboxAdapter adapter;
    List<InboxModel> conversationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the updated inbox layout
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        recyclerView = view.findViewById(R.id.recyclerInbox);
        searchBar = view.findViewById(R.id.etSearchChats);

        conversationList = new ArrayList<>();

        // Temporary Dummy Data for UI testing
        conversationList.add(new InboxModel("2", "Ali Ahmed", "Thanks for the help!", "10:30 AM"));
        conversationList.add(new InboxModel("3", "Library Admin", "Is this your bag?", "Yesterday"));
        conversationList.add(new InboxModel("4", "Hamza", "Ok, coming.", "Mon"));

        // Setup RecyclerView with a Linear Layout Manager and Adapter
        adapter = new InboxAdapter(getContext(), conversationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Listener for search/filtering logic (Implementation pending)
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO: Add search filter logic here
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}