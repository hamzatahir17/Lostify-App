package com.example.lostify;

import android.content.Context;
import android.content.Intent;            // NEW IMPORT for switching screens
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // --- Variables ---
    private View btnReportLost, btnReportFound;
    private ViewPager2 bannerViewPager;
    private TabLayout tabLayoutIndicator;

    // Search Variables
    private EditText etSearch;
    private ImageView btnClear;
    private View layoutNoData;

    // RecyclerView Variables
    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private ArrayList<ReportItem> reportList;

    private Handler sliderHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ==========================================
        // 1. RECYCLER VIEW & DATA SETUP
        // ==========================================
        recyclerView = view.findViewById(R.id.recyclerViewReports);
        layoutNoData = view.findViewById(R.id.layoutNoData);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reportList = new ArrayList<>();

// 1. Bagpack
        reportList.add(new ReportItem(
                "Bagpack",
                "Library, 2nd Floor",
                "2 hrs ago",
                "LOST",
                R.drawable.bagpack,
                "I lost my black backpack near the history section table. It contains a Dell laptop and two blue notebooks. Please contact if found."
        ));

// 2. Calculator
        reportList.add(new ReportItem(
                "Calculator",
                "Main Auditorium",
                "Yesterday",
                "FOUND",
                R.drawable.calculator,
                "Found a Casio Scientific Calculator on the last row seat. Please claim it from the admin office."
        ));

// 3. Keys
        reportList.add(new ReportItem(
                "Keys",
                "Cafeteria Table 4",
                "3 hrs ago",
                "LOST",
                R.drawable.banner1,
                "A bunch of 3 keys with a Spider-Man keychain. Lost it while having lunch."
        ));

// 4. ID Card
        reportList.add(new ReportItem(
                "ID Card",
                "Sports Complex",
                "Today",
                "FOUND",
                R.drawable.banner2,
                "Found a Student ID Card belonging to 'Ali Khan'. Currently submitted to the security guard."
        ));

// 5. Blue Bottle
        reportList.add(new ReportItem(
                "Blue Bottle",
                "Computer Lab",
                "1 hr ago",
                "LOST",
                R.drawable.bagpack,
                "My blue metal water bottle was left on the desk in Lab 3. It has a sticker of 'Coding' on it."
        ));

// 6. Wallet
        reportList.add(new ReportItem(
                "Wallet",
                "Canteen",
                "Yesterday",
                "LOST",
                R.drawable.calculator,
                "Brown leather wallet lost somewhere near the canteen. Contains my Driving License and some cash."
        ));
        reportAdapter = new ReportAdapter(reportList);
        recyclerView.setAdapter(reportAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    hideKeyboard(view);
                }
            }
        });


        // ==========================================
        // 2. SEARCH BAR LOGIC
        // ==========================================
        etSearch = view.findViewById(R.id.etSearch);
        btnClear = view.findViewById(R.id.btnClear);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();

                if (query.length() > 0) {
                    btnClear.setVisibility(View.VISIBLE);
                } else {
                    btnClear.setVisibility(View.GONE);
                }

                if (reportAdapter != null) {
                    boolean isListEmpty = reportAdapter.filterList(query);
                    if (isListEmpty) {
                        recyclerView.setVisibility(View.GONE);
                        layoutNoData.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        layoutNoData.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClear.setOnClickListener(v -> {
            etSearch.setText("");
            hideKeyboard(view);
        });


        // ==========================================
        // 3. BUTTONS LOGIC (UPDATED WITH INTENTS)
        // ==========================================
        btnReportLost = view.findViewById(R.id.btn_report_lost);
        btnReportFound = view.findViewById(R.id.btn_report_found);

        // Jab "I Lost Something" par click ho -> ReportLostActivity khole
        btnReportLost.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReportLostActivity.class);
            startActivity(intent);
        });

        // Jab "I Found Something" par click ho -> ReportFoundActivity khole
        btnReportFound.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReportFoundActivity.class);
            startActivity(intent);
        });


        // ==========================================
        // 4. BANNER SLIDER LOGIC
        // ==========================================
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        tabLayoutIndicator = view.findViewById(R.id.tabLayoutIndicator);

        List<Integer> sliderImages = new ArrayList<>();
        sliderImages.add(R.drawable.banner1);
        sliderImages.add(R.drawable.banner2);
        sliderImages.add(R.drawable.banner3);

        BannerAdapter bannerAdapter = new BannerAdapter(sliderImages);
        bannerViewPager.setAdapter(bannerAdapter);

        new TabLayoutMediator(tabLayoutIndicator, bannerViewPager,
                (tab, position) -> {}).attach();

        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });

        return view;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        etSearch.clearFocus();
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerViewPager != null) {
                int currentItem = bannerViewPager.getCurrentItem();
                int totalItems = bannerViewPager.getAdapter() != null ? bannerViewPager.getAdapter().getItemCount() : 0;

                if (totalItems > 0) {
                    if (currentItem < totalItems - 1) {
                        bannerViewPager.setCurrentItem(currentItem + 1);
                    } else {
                        bannerViewPager.setCurrentItem(0);
                    }
                }
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }
}