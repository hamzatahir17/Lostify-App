package com.example.lostify;

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

    private ArrayList<ReportItem> originalList; // Asli data
    private ArrayList<ReportItem> displayList;  // Wo data jo screen par dikh raha hai

    public ReportAdapter(ArrayList<ReportItem> list) {
        this.originalList = list;
        this.displayList = new ArrayList<>(list); // Shuru mein sab kuch dikhao
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportItem item = displayList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvLocation.setText(item.getLocation());
        holder.tvTime.setText(item.getTime());
        holder.tvStatus.setText(item.getStatus());
        holder.itemImage.setImageResource(item.getImageResId());

        // Status ke hisab se rang badalna (Red for Lost, Green for Found)
        if (item.getStatus().equals("LOST")) {
            holder.statusCard.setCardBackgroundColor(Color.parseColor("#FFEBEE")); // Light Red
            holder.tvStatus.setTextColor(Color.parseColor("#D32F2F")); // Dark Red
        } else {
            holder.statusCard.setCardBackgroundColor(Color.parseColor("#E8F5E9")); // Light Green
            holder.tvStatus.setTextColor(Color.parseColor("#388E3C")); // Dark Green
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    // --- SEARCH FILTER LOGIC (UPDATED) ---
    // Ab yeh method 'boolean' return karega (True = List Empty, False = Data Hai)
    public boolean filterList(String query) {
        // Nayi temporary list banayi taake purani kharab na ho
        ArrayList<ReportItem> filteredList = new ArrayList<>();

        // Agar query khali hai ya null hai
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(originalList); // Sab wapis dikhao
        } else {
            // Text ko lowercase karo aur extra spaces hatao
            String filterPattern = query.toLowerCase().trim();

            for (ReportItem item : originalList) {
                // Title ya Location mein check karo
                if (item.getTitle().toLowerCase().trim().contains(filterPattern) ||
                        item.getLocation().toLowerCase().trim().contains(filterPattern)) {
                    filteredList.add(item);
                }
            }
        }

        // List update karo
        displayList = filteredList;
        notifyDataSetChanged();

        // Agar list khali hai to TRUE bhejega, taake Fragment "No Data" show kare
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