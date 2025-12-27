package com.example.lostify;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private Context context;
    private List<InboxModel> inboxList;
    private String currentUserId;

    public InboxAdapter(Context context, List<InboxModel> inboxList) {
        this.context = context;
        this.inboxList = inboxList;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inbox_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InboxModel model = inboxList.get(position);

        holder.tvUserName.setText(model.userName);
        holder.tvTime.setText(model.time);

        if (model.senderId != null && model.senderId.equals(currentUserId)) {
            holder.tvLastMessage.setText("You: " + model.lastMessage);
            holder.tvLastMessage.setTypeface(null, Typeface.NORMAL);
            holder.tvLastMessage.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        } else {
            holder.tvLastMessage.setText(model.lastMessage);

            if (model.seen) {
                holder.tvLastMessage.setTypeface(null, Typeface.NORMAL);
                holder.tvLastMessage.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            } else {
                holder.tvLastMessage.setTypeface(null, Typeface.BOLD);
                holder.tvLastMessage.setTextColor(context.getResources().getColor(android.R.color.black));
            }
        }

        if (model.image != null && !model.image.isEmpty()) {
            Glide.with(context)
                    .load(model.image)
                    .circleCrop()
                    .placeholder(R.drawable.placeholder_loading)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.imgProfile);
        } else {
            holder.imgProfile.setImageResource(R.drawable.placeholder_loading);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("receiverId", model.partnerId);
            intent.putExtra("receiverName", model.userName);
            intent.putExtra("receiverImage", model.image);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return inboxList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile;
        TextView tvUserName, tvLastMessage, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}