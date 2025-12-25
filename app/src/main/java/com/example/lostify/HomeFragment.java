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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
    private ArrayList<ReportModel> reportList;

    // --- Firebase ---
    private FirebaseFirestore db;

    // --- Auto-Slider Logic ---
    private Handler sliderHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // ==========================================
        // 1. RECYCLERVIEW SETUP
        // ==========================================
        recyclerView = view.findViewById(R.id.recyclerViewReports);
        layoutNoData = view.findViewById(R.id.layoutNoData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize list and adapter
        reportList = new ArrayList<>();
        reportAdapter = new ReportAdapter(reportList);
        recyclerView.setAdapter(reportAdapter);

        // Fetch Real Data from Firestore (Lost + Found)
        fetchItems();

        // Hide keyboard on scroll
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

                if (reportAdapter != null) {
                    boolean isListEmpty = reportAdapter.filterList(query);
                    updateNoDataView(isListEmpty);
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
        // 3. NAVIGATION
        // ==========================================
        btnReportLost = view.findViewById(R.id.btn_report_lost);
        btnReportFound = view.findViewById(R.id.btn_report_found);

        btnReportLost.setOnClickListener(v -> startActivity(new Intent(getActivity(), ReportLostActivity.class)));
        btnReportFound.setOnClickListener(v -> startActivity(new Intent(getActivity(), ReportFoundActivity.class)));

        // ==========================================
        // 4. BANNER SLIDER
        // ==========================================
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        tabLayoutIndicator = view.findViewById(R.id.tabLayoutIndicator);

        List<Integer> sliderImages = new ArrayList<>();
        sliderImages.add(R.drawable.banner1);
        sliderImages.add(R.drawable.banner2);
        sliderImages.add(R.drawable.banner3);

        BannerAdapter bannerAdapter = new BannerAdapter(sliderImages);
        bannerViewPager.setAdapter(bannerAdapter);

        new TabLayoutMediator(tabLayoutIndicator, bannerViewPager, (tab, position) -> {}).attach();

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

    /**
     * Fetches data from both 'LostItems' and 'FoundItems' collections in real-time
     */
    private void fetchItems() {
        String[] collections = {"LostItems", "FoundItems"};

        for (String colName : collections) {
            db.collection(colName)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) return;

                            if (value != null) {
                                // 🔴 Fix Duplicates: Har collection update par relevant data refresh
                                for (DocumentSnapshot doc : value.getDocuments()) {
                                    ReportModel item = doc.toObject(ReportModel.class);
                                    if (item != null) {

                                        // Duplicate check logic
                                        boolean alreadyExists = false;
                                        int indexToUpdate = -1;

                                        for (int i = 0; i < reportList.size(); i++) {
                                            // Agar same UserID aur ItemName ho to matlab wahi item hai
                                            if (reportList.get(i).getItemName().equals(item.getItemName()) &&
                                                    reportList.get(i).getUserId().equals(item.getUserId())) {
                                                alreadyExists = true;
                                                indexToUpdate = i;
                                                break;
                                            }
                                        }

                                        if (alreadyExists) {
                                            reportList.set(indexToUpdate, item); // Update existing
                                        } else {
                                            reportList.add(item); // Add new
                                        }
                                    }
                                }

                                if (reportAdapter != null) {
                                    reportAdapter.updateData(reportList);
                                }
                                updateNoDataView(reportList.isEmpty());
                            }
                        }
                    });
        }
    }

    private void updateNoDataView(boolean isEmpty) {
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        layoutNoData.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
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