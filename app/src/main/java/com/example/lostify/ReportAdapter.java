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
        // Inflate the custom item layout for each row
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        // Retrieve data for the current position
        ReportItem item = displayList.get(position);

        // Bind data to UI components
        holder.tvTitle.setText(item.getTitle());
        holder.tvLocation.setText(item.getLocation());
        holder.tvTime.setText(item.getTime());
        holder.tvStatus.setText(item.getStatus());
        holder.itemImage.setImageResource(item.getImageResId());

        // Apply dynamic styling based on item status (LOST vs FOUND)
        if (item.getStatus().equals("LOST")) {
            holder.statusCard.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
            holder.tvStatus.setTextColor(Color.parseColor("#D32F2F"));
        } else {
            holder.statusCard.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.tvStatus.setTextColor(Color.parseColor("#388E3C"));
        }

        // Handle item click navigation to ItemDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ItemDetailActivity.class);

            // Pass item details to the destination activity using Intent Extras
            intent.putExtra("ITEM_TITLE", item.getTitle());
            intent.putExtra("ITEM_LOCATION", item.getLocation());
            intent.putExtra("ITEM_TIME", item.getTime());
            intent.putExtra("ITEM_STATUS", item.getStatus());
            intent.putExtra("ITEM_IMAGE", item.getImageResId());
            intent.putExtra("ITEM_DESC", item.getDescription());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    /**
     * Filters the list based on search query and updates the UI
     */
    public boolean filterList(String query) {
        ArrayList<ReportItem> filteredList = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            String filterPattern = query.toLowerCase().trim();
            for (ReportItem item : originalList) {
                // Search by title or location
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

    // ViewHolder class to cache UI references
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