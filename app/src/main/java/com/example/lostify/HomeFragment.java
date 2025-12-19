package com.example.lostify;

import android.content.Context;
import android.content.Intent;
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

    // --- UI Components ---
    private View btnReportLost, btnReportFound;
    private ViewPager2 bannerViewPager;
    private TabLayout tabLayoutIndicator;
    private EditText etSearch;
    private ImageView btnClear;
    private View layoutNoData;

    // --- RecyclerView Components ---
    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private ArrayList<ReportItem> reportList;

    // --- Auto-Slider Logic ---
    private Handler sliderHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ==========================================
        // 1. RECYCLERVIEW & DUMMY DATA SETUP
        // ==========================================
        recyclerView = view.findViewById(R.id.recyclerViewReports);
        layoutNoData = view.findViewById(R.id.layoutNoData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reportList = new ArrayList<>();

        // Adding dummy data for demonstration
        reportList.add(new ReportItem("Bagpack", "Library, 2nd Floor", "2 hrs ago", "LOST", R.drawable.bagpack, "Black backpack with a Dell laptop and notebooks."));
        reportList.add(new ReportItem("Calculator", "Main Auditorium", "Yesterday", "FOUND", R.drawable.calculator, "Casio Scientific Calculator found on last row."));
        reportList.add(new ReportItem("Keys", "Cafeteria Table 4", "3 hrs ago", "LOST", R.drawable.banner1, "Bunch of 3 keys with Spider-Man keychain."));
        reportList.add(new ReportItem("ID Card", "Sports Complex", "Today", "FOUND", R.drawable.banner2, "Student ID belonging to 'Ali Khan'."));
        reportList.add(new ReportItem("Blue Bottle", "Computer Lab", "1 hr ago", "LOST", R.drawable.bagpack, "Blue metal bottle with 'Coding' sticker."));
        reportList.add(new ReportItem("Wallet", "Canteen", "Yesterday", "LOST", R.drawable.calculator, "Brown leather wallet with Driving License."));

        reportAdapter = new ReportAdapter(reportList);
        recyclerView.setAdapter(reportAdapter);

        // Hide keyboard when scrolling the list
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    hideKeyboard(view);
                }
            }
        });

        // ==========================================
        // 2. SEARCH FILTER LOGIC
        // ==========================================
        etSearch = view.findViewById(R.id.etSearch);
        btnClear = view.findViewById(R.id.btnClear);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                btnClear.setVisibility(query.length() > 0 ? View.VISIBLE : View.GONE);

                // Filter list and show 'No Data' layout if no results found
                if (reportAdapter != null) {
                    boolean isListEmpty = reportAdapter.filterList(query);
                    recyclerView.setVisibility(isListEmpty ? View.GONE : View.VISIBLE);
                    layoutNoData.setVisibility(isListEmpty ? View.VISIBLE : View.GONE);
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
        // 3. NAVIGATION (REPORTING ACTIVITIES)
        // ==========================================
        btnReportLost = view.findViewById(R.id.btn_report_lost);
        btnReportFound = view.findViewById(R.id.btn_report_found);

        btnReportLost.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ReportLostActivity.class));
        });

        btnReportFound.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ReportFoundActivity.class));
        });

        // ==========================================
        // 4. BANNER SLIDER (VIEWPAGER2) LOGIC
        // ==========================================
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        tabLayoutIndicator = view.findViewById(R.id.tabLayoutIndicator);

        List<Integer> sliderImages = new ArrayList<>();
        sliderImages.add(R.drawable.banner1);
        sliderImages.add(R.drawable.banner2);
        sliderImages.add(R.drawable.banner3);

        BannerAdapter bannerAdapter = new BannerAdapter(sliderImages);
        bannerViewPager.setAdapter(bannerAdapter);

        // Link TabLayout with ViewPager2 for dot indicators
        new TabLayoutMediator(tabLayoutIndicator, bannerViewPager, (tab, position) -> {}).attach();

        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000); // 3 seconds delay
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

    // Auto-scroll logic for the banner
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerViewPager != null && bannerViewPager.getAdapter() != null) {
                int currentItem = bannerViewPager.getCurrentItem();
                int totalItems = bannerViewPager.getAdapter().getItemCount();

                if (totalItems > 0) {
                    bannerViewPager.setCurrentItem(currentItem < totalItems - 1 ? currentItem + 1 : 0);
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