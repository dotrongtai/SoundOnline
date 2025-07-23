 package com.example.soundonline.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast; // Example: for showing a click

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.R;
import com.example.soundonline.model.Sound; // Import your Sound model

import java.util.List;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.SoundViewHolder> {

    private Context context;
    private List<Sound> sounds;

    public SoundAdapter(Context context, List<Sound> sounds) {
        this.context = context;
        this.sounds = sounds;
    }

    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sound, parent, false); // Assuming you have item_sound.xml
        return new SoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {
        Sound sound = sounds.get(position);
        holder.soundTitle.setText(sound.getTitle());
        holder.soundArtist.setText(sound.getArtistName());
        // You can also format and display duration here if needed
        // holder.soundDuration.setText(formatDuration(sound.getDuration()));

        holder.itemView.setOnClickListener(v -> {
            // Handle song click (e.g., start playback)
            Toast.makeText(context, "Playing: " + sound.getTitle(), Toast.LENGTH_SHORT).show();
            // You would typically start a service or play the sound here
        });
    }

    @Override
    public int getItemCount() {
        return sounds != null ? sounds.size() : 0;
    }

    public static class SoundViewHolder extends RecyclerView.ViewHolder {
        TextView soundTitle;
        TextView soundArtist;
        // TextView soundDuration; // If you add duration to your item_sound.xml

        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);
            soundTitle = itemView.findViewById(R.id.textSoundTitle); // Assuming ID in item_sound.xml
            soundArtist = itemView.findViewById(R.id.textSoundArtist); // Assuming ID in item_sound.xml
            // soundDuration = itemView.findViewById(R.id.textSoundDuration);
        }
    }

    // Helper method to format duration (optional)
    private String formatDuration(int durationInSeconds) {
        int minutes = durationInSeconds / 60;
        int seconds = durationInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}