package com.example.lostify;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ReportLostActivity extends AppCompatActivity {

    private EditText etDate, etTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_lost);

        // ==========================================
        // 1. HEADER SETUP
        // ==========================================
        // Initialize custom back button and handle click event
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to the previous screen
                finish();
            }
        });

        // ==========================================
        // 2. SPINNER SETUP
        // ==========================================
        Spinner spinner = findViewById(R.id.spinnerCategory);
        String[] categories = {"Electronics", "Books", "Bags", "Clothing", "Wallet/Keys", "Others"};

        // Populate the spinner with categories using a default layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(adapter);

        // ==========================================
        // 3. DATE & TIME PICKER LOGIC
        // ==========================================
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);

        // --- Handle Date Selection ---
        etDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Display DatePickerDialog to ensure standard date format input
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ReportLostActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        // Display formatted date (Month +1 because Java months are 0-indexed)
                        etDate.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        // --- Handle Time Selection ---
        etTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Display TimePickerDialog with 12-hour format logic
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    ReportLostActivity.this,
                    (view, hourOfDay, minute1) -> {
                        String amPm;
                        int hour12;

                        // Convert 24-hour time to 12-hour format
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                            hour12 = (hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
                        } else {
                            amPm = "AM";
                            hour12 = (hourOfDay == 0) ? 12 : hourOfDay;
                        }

                        // Format minutes to always show two digits (e.g., 05 instead of 5)
                        String formattedMinute = String.format("%02d", minute1);
                        etTime.setText(hour12 + ":" + formattedMinute + " " + amPm);
                    },
                    hour, minute, false); // Set is24HourView to false for AM/PM
            timePickerDialog.show();
        });
    }
}