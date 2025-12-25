package com.example.lostify;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private Context context;
    private List<InboxModel> originalList; // Search ke liye backup
    private List<InboxModel> displayList;  // Jo screen par dikhega

    public InboxAdapter(Context context, List<InboxModel> list) {
        this.context = context;
        this.originalList = new ArrayList<>(list);
        this.displayList = list;
    }

    // 🔴 NEW: Data update method
    public void updateData(List<InboxModel> newList) {
        this.originalList = new ArrayList<>(newList);
        this.displayList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inbox_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InboxModel model = displayList.get(position);

        holder.tvName.setText(model.getUserName());
        holder.tvLastMsg.setText(model.getLastMessage());
        holder.tvTime.setText(model.getTime());

        // 🔴 CLICK ACTION: Open ChatActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("receiverId", model.getUserId()); // Other person's ID
            intent.putExtra("receiverName", model.getUserName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    // 🔴 SEARCH FILTER LOGIC
    public void filterList(String query) {
        if (query.isEmpty()) {
            displayList = new ArrayList<>(originalList);
        } else {
            List<InboxModel> filtered = new ArrayList<>();
            for (InboxModel item : originalList) {
                if (item.getUserName().toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(item);
                }
            }
            displayList = filtered;
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLastMsg, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvLastMsg = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}