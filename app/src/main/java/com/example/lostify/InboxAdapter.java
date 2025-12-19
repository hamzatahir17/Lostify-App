package com.example.lostify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private Context context;
    private List<InboxModel> list;

    public InboxAdapter(Context context, List<InboxModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the single row layout for the inbox
        View view = LayoutInflater.from(context).inflate(R.layout.item_inbox_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InboxModel model = list.get(position);

        // Map data from the model to the UI components
        holder.tvName.setText(model.getUserName());
        holder.tvLastMsg.setText(model.getLastMessage());
        holder.tvTime.setText(model.getTime());

        // Handle item click to open a conversation
        holder.itemView.setOnClickListener(v -> {
            ChatFragment chatFragment = new ChatFragment();

            // Replace the current fragment with ChatFragment
            AppCompatActivity activity = (AppCompatActivity) context;
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, chatFragment)
                    .addToBackStack(null) // Allows the user to navigate back to the inbox list
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ViewHolder class to initialize and store UI references
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