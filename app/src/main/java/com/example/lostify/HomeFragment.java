package com.example.lostify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private Button btnReportLost, btnReportFound;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize buttons
        btnReportLost = view.findViewById(R.id.btn_report_lost);
        btnReportFound = view.findViewById(R.id.btn_report_found);

        // Button Click Listeners
        btnReportLost.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Report Lost Item clicked", Toast.LENGTH_SHORT).show()
        );

        btnReportFound.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Report Found Item clicked", Toast.LENGTH_SHORT).show()
        );

        return view;
    }
}
