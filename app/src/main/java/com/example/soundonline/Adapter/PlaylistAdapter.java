package com.example.soundonline.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soundonline.R;
import com.example.soundonline.model.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private Context context;
    private List<Playlist> playlists;

    public PlaylistAdapter(Context context, List<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);

        // Chỉ cần gọi 1 lần
        Glide.with(context)
                .load(playlist.getCoverImageUrl())
                .error(R.drawable.img) // Ảnh mặc định nếu load lỗi
                .into(holder.playlistImage);

        holder.playlistName.setText(playlist.getPlaylistName());
        holder.playlistDescription.setText(playlist.getDescription());
        holder.totalTrack.setText("Tổng bài hát: " + playlist.getTotalTracks());
    }


    @Override
    public int getItemCount() {
        return playlists != null ? playlists.size() : 0;
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView playlistImage;
        TextView playlistName,playlistDescription,totalTrack;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistImage = itemView.findViewById(R.id.imagePlaylist);
            playlistName = itemView.findViewById(R.id.textPlaylistName);
            playlistDescription = itemView.findViewById(R.id.textPlaylistDescription);
            totalTrack = itemView.findViewById(R.id.textTotalTrack);
        }
    }
}
