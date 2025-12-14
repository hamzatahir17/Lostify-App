package com.example.lostify;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private ArrayList<ReportItem> originalList;
    private ArrayList<ReportItem> displayList;

    public ReportAdapter(ArrayList<ReportItem> list) {
        this.originalList = list;
        this.displayList = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        // 1. Current Item nikalo (Jo screen par dikhana hai)
        ReportItem item = displayList.get(position);

        // 2. Data Set karo
        holder.tvTitle.setText(item.getTitle());
        holder.tvLocation.setText(item.getLocation());
        holder.tvTime.setText(item.getTime());
        holder.tvStatus.setText(item.getStatus());
        holder.itemImage.setImageResource(item.getImageResId());

        // 3. Status Color Logic
        if (item.getStatus().equals("LOST")) {
            holder.statusCard.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
            holder.tvStatus.setTextColor(Color.parseColor("#D32F2F"));
        } else {
            holder.statusCard.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.tvStatus.setTextColor(Color.parseColor("#388E3C"));
        }

        // --- NEW: CLICK LISTENER ADDED HERE 🟢 ---
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ItemDetailActivity.class);

            // Data pass kar rahe hain agli screen ko
            intent.putExtra("ITEM_TITLE", item.getTitle());
            intent.putExtra("ITEM_LOCATION", item.getLocation());
            intent.putExtra("ITEM_TIME", item.getTime());
            intent.putExtra("ITEM_STATUS", item.getStatus());
            intent.putExtra("ITEM_IMAGE", item.getImageResId());

            // Note: Make sure ReportItem.java mein getDescription() ka method ho
            intent.putExtra("ITEM_DESC", item.getDescription());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    public boolean filterList(String query) {
        ArrayList<ReportItem> filteredList = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            String filterPattern = query.toLowerCase().trim();
            for (ReportItem item : originalList) {
                if (item.getTitle().toLowerCase().trim().contains(filterPattern) ||
                        item.getLocation().toLowerCase().trim().contains(filterPattern)) {
                    filteredList.add(item);
                }
            }
        }
        displayList = filteredList;
        notifyDataSetChanged();
        return displayList.isEmpty();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvTime, tvStatus;
        ImageView itemImage;
        CardView statusCard;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            itemImage = itemView.findViewById(R.id.itemImage);
            statusCard = itemView.findViewById(R.id.statusCard);
        }
    }
}