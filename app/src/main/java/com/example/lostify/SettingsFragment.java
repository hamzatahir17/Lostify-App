package com.example.lostify;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_setting, container, false);


        RelativeLayout btnAccount = view.findViewById(R.id.btnAccount);
        RelativeLayout btnNotifications = view.findViewById(R.id.btnNotifications);
        RelativeLayout btnPrivacy = view.findViewById(R.id.btnPrivacy);
        RelativeLayout btnHelp = view.findViewById(R.id.btnHelp);



        // --- Account Button ---
        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Account Clicked", Toast.LENGTH_SHORT).show();


            }
        });

        // --- Notifications Button ---
        btnNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Notifications Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Privacy Button ---
        btnPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Privacy Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Help Button ---
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Help Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}