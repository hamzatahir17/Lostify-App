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

public class ReportFoundActivity extends AppCompatActivity {

    private EditText etDate, etTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_found);

        // ==========================================
        // 1. HEADER SETUP
        // ==========================================
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close current activity and return to the previous screen
                finish();
            }
        });

        // ==========================================
        // 2. SPINNER SETUP (Category Selection)
        // ==========================================
        Spinner spinner = findViewById(R.id.spinnerCategory);
        String[] categories = {"Electronics", "Books", "Bags", "Clothing", "Wallet/Keys", "Others"};

        // Use default Android dropdown layout for the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(adapter);

        // ==========================================
        // 3. DATE & TIME PICKER LOGIC
        // ==========================================
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);

        // --- Setup Date Picker Dialog ---
        etDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ReportFoundActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Month is 0-based in Java, so add 1 for display
                        etDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        // --- Setup Time Picker Dialog ---
        etTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    ReportFoundActivity.this,
                    (view, hourOfDay, selectedMinute) -> {
                        // Logic to convert 24-hour format to 12-hour (AM/PM)
                        String amPm = (hourOfDay >= 12) ? "PM" : "AM";
                        int hour12 = (hourOfDay > 12) ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);

                        // Ensure minutes are displayed with leading zeros (e.g., 05 instead of 5)
                        String formattedMinute = String.format("%02d", selectedMinute);
                        etTime.setText(hour12 + ":" + formattedMinute + " " + amPm);
                    },
                    hour, minute, false); // Set to false for 12-hour format
            timePickerDialog.show();
        });
    }
}