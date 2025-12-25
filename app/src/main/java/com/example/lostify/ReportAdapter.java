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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private ArrayList<ReportModel> originalList;
    private ArrayList<ReportModel> displayList;

    public ReportAdapter(ArrayList<ReportModel> list) {
        this.originalList = new ArrayList<>(list);
        this.displayList = new ArrayList<>(list);
    }

    public void updateData(ArrayList<ReportModel> newList) {
        this.originalList = new ArrayList<>(newList);
        this.displayList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportModel item = displayList.get(position);

        holder.tvTitle.setText(item.getItemName());
        holder.tvLocation.setText(item.getLocation());
        holder.tvTime.setText(item.getDate() + " " + item.getTime());

        if ("FOUND".equalsIgnoreCase(item.getStatus())) {
            holder.tvStatus.setText("FOUND");
            holder.statusCard.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvStatus.setText("LOST");
            holder.statusCard.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
            holder.tvStatus.setTextColor(Color.parseColor("#D32F2F"));
        }

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .timeout(60000)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.itemImage);
        } else {
            holder.itemImage.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // 4. Handle Click (Navigate to Details)
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ItemDetailActivity.class);

            intent.putExtra("ITEM_TITLE", item.getItemName());
            intent.putExtra("ITEM_LOCATION", item.getLocation());
            intent.putExtra("ITEM_TIME", item.getDate());
            intent.putExtra("ITEM_STATUS", item.getStatus());
            intent.putExtra("ITEM_IMAGE_URL", item.getImageUrl());
            intent.putExtra("ITEM_DESC", item.getDescription());

            // 🔴 SABSE ZAROORI LINE (Fixes Guest User Error):
            // Yeh Owner ki UID detail screen ko bhej raha hai
            intent.putExtra("OWNER_ID", item.getUserId());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    public boolean filterList(String query) {
        ArrayList<ReportModel> filteredList = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            String filterPattern = query.toLowerCase().trim();
            for (ReportModel item : originalList) {
                if (item.getItemName().toLowerCase().contains(filterPattern) ||
                        item.getLocation().toLowerCase().contains(filterPattern)) {
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