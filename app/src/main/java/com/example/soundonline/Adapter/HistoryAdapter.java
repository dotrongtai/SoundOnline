package com.example.soundonline.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox; // Import CheckBox
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soundonline.R;
import com.example.soundonline.model.History;
import com.example.soundonline.presentation.player.PlayerActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
    private List<History> historyList;
    private Set<Integer> selectedIds = new HashSet<>();
    private boolean isSelectionMode = false; // New flag for selection mode

    public HistoryAdapter(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList != null ? historyList : new ArrayList<>();
    }

    public void updateData(List<History> newHistoryList) {
        this.historyList = newHistoryList != null ? newHistoryList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<History> getCurrentData() {
        return historyList;
    }

    // Method to toggle selection mode
    public void setSelectionMode(boolean selectionMode) {
        if (this.isSelectionMode != selectionMode) {
            this.isSelectionMode = selectionMode;
            if (!isSelectionMode) { // Clear selections if exiting selection mode
                selectedIds.clear();
            }
            notifyDataSetChanged(); // Rebind all views to show/hide checkboxes
        }
    }

    public Set<Integer> getSelectedIds() {
        return selectedIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_listening_history, parent, false); // Assuming item_history is your layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History history = historyList.get(position);

        if (history.getSound() != null) {
            holder.tvTitle.setText(history.getSound().getTitle());
            holder.tvArtist.setText(history.getSound().getArtistName());
            holder.tvDuration.setText("Duration: " + formatDuration(history.getSound().getDuration()));

            Glide.with(context)
                    .load(history.getSound().getCoverImageUrl())
                    .placeholder(R.drawable.img) // default image
                    .error(R.drawable.img) // error image
                    .into(holder.imgCover);

            // Handle Play button click
            holder.btnPlay.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("playMode", "single");
                intent.putExtra("audioUrl", history.getSound().getFileUrl());
                context.startActivity(intent);
            });
        }

        holder.tvPlayedAt.setText("Played on: " + history.getPlayedAt());

        // Checkbox visibility and state
        if (isSelectionMode) {
            holder.checkboxSelect.setVisibility(View.VISIBLE);
            holder.checkboxSelect.setChecked(selectedIds.contains(history.getHistoryId()));
        } else {
            holder.checkboxSelect.setVisibility(View.GONE);
            holder.checkboxSelect.setChecked(false); // Reset checked state
        }

        // Handle checkbox click
        holder.checkboxSelect.setOnClickListener(v -> {
            if (holder.checkboxSelect.isChecked()) {
                selectedIds.add(history.getHistoryId());
            } else {
                selectedIds.remove(history.getHistoryId());
            }
        });

        // Handle item click for selection (optional, can be long press or a dedicated button)
        // For simplicity, let's make the whole item toggle the checkbox
        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                holder.checkboxSelect.setChecked(!holder.checkboxSelect.isChecked());
                if (holder.checkboxSelect.isChecked()) {
                    selectedIds.add(history.getHistoryId());
                } else {
                    selectedIds.remove(history.getHistoryId());
                }
            } else {
                // Regular play logic if not in selection mode
                if (history.getSound() != null) {
                    Intent intent = new Intent(context, PlayerActivity.class);
                    intent.putExtra("playMode", "single");
                    intent.putExtra("audioUrl", history.getSound().getFileUrl());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvTitle, tvArtist, tvPlayedAt, tvDuration;
        Button btnPlay;
        CheckBox checkboxSelect; // Declare CheckBox

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            tvPlayedAt = itemView.findViewById(R.id.tvPlayedAt);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            checkboxSelect = itemView.findViewById(R.id.checkbox_select); // Initialize CheckBox
        }
    }

    private String formatDuration(int durationInSeconds) {
        int minutes = durationInSeconds / 60;
        int seconds = durationInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}