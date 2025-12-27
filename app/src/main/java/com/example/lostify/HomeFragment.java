package com.example.lostify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private View btnReportLost, btnReportFound;
    private ViewPager2 bannerViewPager;
    private TabLayout tabLayoutIndicator;
    private EditText etSearch;
    private ImageView btnClear;
    private View layoutNoData;

    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private ArrayList<ReportModel> masterList = new ArrayList<>();

    private List<ReportModel> lostList = new ArrayList<>();
    private List<ReportModel> foundList = new ArrayList<>();

    private ListenerRegistration lostListener;
    private ListenerRegistration foundListener;

    private FirebaseFirestore db;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();

        initViews(view);
        setupRecyclerView();
        setupSearchLogic();
        setupBanner();
        setupClickListeners();

        startRealtimeUpdates();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewReports);
        layoutNoData = view.findViewById(R.id.layoutNoData);
        etSearch = view.findViewById(R.id.etSearch);
        btnClear = view.findViewById(R.id.btnClear);

        btnReportLost = view.findViewById(R.id.btn_report_lost);
        btnReportFound = view.findViewById(R.id.btn_report_found);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        tabLayoutIndicator = view.findViewById(R.id.tabLayoutIndicator);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reportAdapter = new ReportAdapter(new ArrayList<>());
        recyclerView.setAdapter(reportAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    hideKeyboard();
                }
            }
        });
    }

    private void setupSearchLogic() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                boolean isSearching = query.length() > 0;

                btnClear.setVisibility(isSearching ? View.VISIBLE : View.GONE);
                toggleHomeElements(!isSearching);

                if (reportAdapter != null) {
                    boolean isEmpty = reportAdapter.filterList(query);
                    updateNoDataView(isEmpty);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                hideKeyboard();
                return true;
            }
            return false;
        });

        btnClear.setOnClickListener(v -> {
            etSearch.setText("");
            hideKeyboard();
            toggleHomeElements(true);
        });
    }

    private void toggleHomeElements(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        bannerViewPager.setVisibility(visibility);
        tabLayoutIndicator.setVisibility(visibility);
        btnReportLost.setVisibility(visibility);
        btnReportFound.setVisibility(visibility);
    }

    private void setupBanner() {
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
    }

    private void setupClickListeners() {
        btnReportLost.setOnClickListener(v -> startActivity(new Intent(getActivity(), ReportLostActivity.class)));
        btnReportFound.setOnClickListener(v -> startActivity(new Intent(getActivity(), ReportFoundActivity.class)));
    }

    private void startRealtimeUpdates() {
        if (lostListener != null && foundListener != null) return;

        lostListener = db.collection("LostItems").addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;
            lostList.clear();
            lostList.addAll(value.toObjects(ReportModel.class));
            mergeAndSortLists();
        });

        foundListener = db.collection("FoundItems").addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;
            foundList.clear();
            foundList.addAll(value.toObjects(ReportModel.class));
            mergeAndSortLists();
        });
    }

    private void mergeAndSortLists() {
        masterList.clear();
        masterList.addAll(lostList);
        masterList.addAll(foundList);

        Collections.sort(masterList, (o1, o2) -> {
            String date1 = o1.getDate() + " " + o1.getTime();
            String date2 = o2.getDate() + " " + o2.getTime();
            return date2.compareTo(date1);
        });

        reportAdapter.updateData(masterList);
        updateNoDataView(masterList.isEmpty());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (lostListener != null) lostListener.remove();
        if (foundListener != null) foundListener.remove();
        lostListener = null;
        foundListener = null;
    }

    private void updateNoDataView(boolean isEmpty) {
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        layoutNoData.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void hideKeyboard() {
        if (getActivity() == null) return;
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
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
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);

        if (etSearch != null && etSearch.length() > 0) {
            etSearch.setText("");
            etSearch.clearFocus();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}