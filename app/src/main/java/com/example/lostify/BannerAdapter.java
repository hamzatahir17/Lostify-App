package com.example.lostify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    // Images ki list store karne ke liye variable
    private List<Integer> imageList;

    // Constructor: Jab hum adapter banayenge to list yahan pass hogi
    public BannerAdapter(List<Integer> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Step A: XML layout (item_banner) ko Java view mein convert karna
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        // Step B: Image ko set karna
        // List se image ID utha kar ImageView par lagana
        holder.imageView.setImageResource(imageList.get(position));
    }

    @Override
    public int getItemCount() {
        // Total images kitni hain batana
        return imageList.size();
    }

    // ViewHolder Class: Yeh view ke elements ko pakad kar rakhta hai
    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_banner.xml mein jo ImageView ki ID hai, wo yahan likhein
            imageView = itemView.findViewById(R.id.bannerImage);
        }
    }
}