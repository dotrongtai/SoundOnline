package com.example.soundonline.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soundonline.R;
import com.example.soundonline.model.Sound;
import com.example.soundonline.presentation.player.PlayerForHistory;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailAdapter extends RecyclerView.Adapter<AlbumDetailAdapter.SoundViewHolder> {

    private Context context;
    private List<Sound> soundList;

    public AlbumDetailAdapter(Context context, List<Sound> soundList) {
        this.context = context;
        this.soundList = soundList != null ? soundList : new ArrayList<>();
    }

    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_album_detail, parent, false);
        return new SoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {
        Sound sound = soundList.get(position);

        // Bind data to views
        holder.textAlbumTitle.setText(sound.getTitle());
        holder.textArtistName.setText(sound.getArtistName() != null ? sound.getArtistName() : "Unknown Artist");
        holder.textGenre.setText("Song"); // Since genre is not in Sound model, use placeholder or modify Sound model if needed

        // Load cover image
        Glide.with(context)
                .load(sound.getCoverImageUrl())
                .error(R.drawable.img) // Fallback image
                .into(holder.imageAlbumCover);


        // Handle click to play song
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerForHistory.class);
            intent.putExtra("title", sound.getTitle());
            intent.putExtra("image", sound.getCoverImageUrl());
            intent.putExtra("audioUrl", sound.getFileUrl());
            intent.putExtra("artist", sound.getArtistName());
            intent.putExtra("songId", sound.getSoundId()); // Pass soundId as int
            intent.putExtra("playlist", new ArrayList<>(soundList)); // Pass entire playlist
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return soundList.size();
    }

    public void updateSounds(List<Sound> newSounds) {
        this.soundList = newSounds != null ? newSounds : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class SoundViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAlbumCover;
        TextView textAlbumTitle;
        TextView textArtistName;
        TextView textGenre;

        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAlbumCover = itemView.findViewById(R.id.imageAlbumCover);
            textAlbumTitle = itemView.findViewById(R.id.textAlbumTitle);
            textArtistName = itemView.findViewById(R.id.textArtistName);
            textGenre = itemView.findViewById(R.id.textGenre);
        }
    }
}