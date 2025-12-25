package com.example.lostify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth; // Import zaroori hai
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private Context context;
    private List<ChatModel> messageList;
    private String currentUserId;

    public ChatAdapter(Context context, List<ChatModel> messageList) {
        this.context = context;
        this.messageList = messageList;
        // Current User ID Firebase se lein
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel message = messageList.get(position);
        // Agar Sender ID meri ID hai, to SENT view dikhao
        if (message.getSenderId() != null && message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModel message = messageList.get(position);
        if (holder.getClass() == SentMessageViewHolder.class) {
            ((SentMessageViewHolder) holder).messageText.setText(message.getMessageText());
        } else {
            ((ReceivedMessageViewHolder) holder).messageText.setText(message.getMessageText());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        SentMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text_sent);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text_received);
        }
    }
}