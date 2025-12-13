package com.example.lostify;

import android.app.DatePickerDialog; // Import
import android.app.TimePickerDialog; // Import
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;      // Import
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;           // Import

public class ReportFoundActivity extends AppCompatActivity {

    // Variables declare karein
    private EditText etDate, etTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_found);

        // ==========================================
        // 1. TOOLBAR SETUP
        // ==========================================
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // ==========================================
        // 2. SPINNER SETUP
        // ==========================================
        Spinner spinner = findViewById(R.id.spinnerCategory);
        String[] categories = {"Electronics", "Books", "Bags", "Clothing", "Wallet/Keys", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(adapter);

        // ==========================================
        // 3. DATE & TIME PICKER LOGIC (New Code)
        // ==========================================
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);

        // --- DATE PICKER ---
        etDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ReportFoundActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        etDate.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        // --- TIME PICKER ---
        etTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    ReportFoundActivity.this,
                    (view, hourOfDay, minute1) -> {
                        String amPm;
                        int hour12;

                        if (hourOfDay >= 12) {
                            amPm = "PM";
                            hour12 = (hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
                        } else {
                            amPm = "AM";
                            hour12 = (hourOfDay == 0) ? 12 : hourOfDay;
                        }

                        String formattedMinute = String.format("%02d", minute1);
                        etTime.setText(hour12 + ":" + formattedMinute + " " + amPm);
                    },
                    hour, minute, false);
            timePickerDialog.show();
        });
    }
}