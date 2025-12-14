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
        View view = LayoutInflater.from(context).inflate(R.layout.item_inbox_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InboxModel model = list.get(position);

        holder.tvName.setText(model.getUserName());
        holder.tvLastMsg.setText(model.getLastMessage());
        holder.tvTime.setText(model.getTime());

        // CLICK EVENT: Jab user list mein kisi naam par click kare
        holder.itemView.setOnClickListener(v -> {
            // Hum Naya ChatFragment kholenge
            ChatFragment chatFragment = new ChatFragment();

            // Yahan hum Agli screen ko data bhej sakte hain (Future use ke liye)
            // Bundle args = new Bundle();
            // args.putString("targetUserId", model.getUserId());
            // chatFragment.setArguments(args);

            // Fragment Replace Logic
            AppCompatActivity activity = (AppCompatActivity) context;
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, chatFragment) // Note: ID check kr lena apni MainActivity ki
                    .addToBackStack(null) // Taake back button dabane se wapis list par aye
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
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