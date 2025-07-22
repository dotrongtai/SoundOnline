package com.example.soundonline.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log; // For logging potential parsing errors
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soundonline.R;
import com.example.soundonline.model.History;
import com.example.soundonline.model.Sound;
import com.example.soundonline.presentation.player.PlayerActivity;

import java.text.ParseException; // For parsing String to Date
import java.text.SimpleDateFormat;
import java.util.Date; // For Date object
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<History> items;
    private Context context;

    public HistoryAdapter(Context context, List<History> items) {
        this.context = context;
        this.items = items;
    }

    public void updateData(List<History> newData) {
        this.items = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listening_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History item = items.get(position);

        // This null check for 'item' itself is usually not needed if getItemCount() is correct.
        // If your list can genuinely contain null History objects, then keep it.
        // Otherwise, it indicates an issue with how 'items' list is populated.
        // For now, I'll keep it but recommend ensuring 'items' doesn't contain nulls.
        if (item == null) {
            holder.titleTextView.setText("Dữ liệu lịch sử không hợp lệ"); // More descriptive
            holder.artistTextView.setText("");
            holder.playCountTextView.setText("");
            holder.durationTextView.setText("");
            holder.trackImage.setImageResource(R.drawable.img); // Default image
            holder.itemView.setOnClickListener(null);
            holder.btnPlay.setOnClickListener(null);
            holder.btnPlay.setVisibility(View.GONE); // Hide button if no data
            return;
        }

        Sound song = item.getSound();

        if (song != null) {
            holder.titleTextView.setText(song.getTitle());
            holder.artistTextView.setText(song.getArtistName());

            // --- Handle playedAt (String to Date parsing) ---
            SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()); // Example: ISO 8601 format
            SimpleDateFormat outputSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String formattedPlayedAt = "N/A";
            if (item.getPlayedAt() != null && !item.getPlayedAt().isEmpty()) {
                try {
                    Date playedDate = inputSdf.parse(item.getPlayedAt());
                    formattedPlayedAt = outputSdf.format(playedDate);
                } catch (ParseException e) {
                    Log.e("HistoryAdapter", "Error parsing playedAt date: " + item.getPlayedAt(), e);
                    // Fallback to the original string or another default if parsing fails
                    formattedPlayedAt = item.getPlayedAt() + " (Invalid Format)";
                }
            }
            holder.playCountTextView.setText(formattedPlayedAt);
            // --- End playedAt handling ---


            holder.durationTextView.setText(String.valueOf(item.getPlayDuration()) + "s");

            Glide.with(context)
                    .load(song.getCoverImageUrl() != null ? song.getCoverImageUrl() : R.drawable.img)
                    .error(R.drawable.img) // Error placeholder for Glide
                    .into(holder.trackImage);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("title", song.getTitle());
                intent.putExtra("image", song.getCoverImageUrl());
                intent.putExtra("audioUrl", song.getFileUrl());
                intent.putExtra("artist", song.getArtistName());
                intent.putExtra("uploader", song.getUploaderName());
                context.startActivity(intent);
            });

            // Keep btnPlay listener if it has specific action or just duplicates itemView click
            holder.btnPlay.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("title", song.getTitle());
                intent.putExtra("image", song.getCoverImageUrl());
                intent.putExtra("audioUrl", song.getFileUrl());
                intent.putExtra("artist", song.getArtistName());
                intent.putExtra("uploader", song.getUploaderName());
                context.startActivity(intent);
            });
            holder.btnPlay.setVisibility(View.VISIBLE); // Ensure button is visible
        } else {
            // Handle the case where 'sound' is null
            holder.titleTextView.setText("Bài hát không khả dụng");
            holder.artistTextView.setText("Thông tin bài hát bị thiếu");
            holder.playCountTextView.setText("Ngày phát: " + (item.getPlayedAt() != null ? item.getPlayedAt() : "N/A")); // Display raw string if sound is null
            holder.durationTextView.setText("Thời lượng: N/A");
            holder.trackImage.setImageResource(R.drawable.img); // Set default image
            holder.itemView.setOnClickListener(null); // Disable click action if no sound
            holder.btnPlay.setOnClickListener(null); // No click action
            holder.btnPlay.setVisibility(View.GONE); // Hide the button
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView trackImage;
        TextView titleTextView;
        TextView artistTextView;
        TextView playCountTextView;
        TextView durationTextView;
        Button btnPlay;

        ViewHolder(View itemView) {
            super(itemView);
            trackImage = itemView.findViewById(R.id.imgCover);
            titleTextView = itemView.findViewById(R.id.tvTitle);
            artistTextView = itemView.findViewById(R.id.tvArtist);
            playCountTextView = itemView.findViewById(R.id.tvPlayedAt);
            durationTextView = itemView.findViewById(R.id.tvDuration);
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }
    }
}