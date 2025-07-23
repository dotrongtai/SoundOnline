package com.example.soundonline.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soundonline.R;
import com.example.soundonline.model.Playlist;
import com.example.soundonline.presentation.playlist.PlaylistTrackActivity;

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

    // Trong PlaylistAdapter.java

// ... (các phần khác của lớp)

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);

        Glide.with(context)
                .load(playlist.getCoverImageUrl())
                .error(R.drawable.img)
                .into(holder.playlistImage);

        holder.playlistName.setText(playlist.getPlaylistName());
        holder.playlistDescription.setText(playlist.getDescription());
        holder.totalTrack.setText("Tổng bài hát: " + playlist.getTotalTracks());

        // Xử lý sự kiện click vào ảnh
        holder.playlistImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlaylistTrackActivity.class);
            // Chỉ truyền playlistId (giả sử playlistId là kiểu int hoặc long)
            // Nếu playlistId là kiểu String, hãy dùng intent.putExtra("playlistId", playlist.getPlaylistId());
            intent.putExtra("playlistId", playlist.getPlaylistId()); // << SỬA Ở ĐÂY

            context.startActivity(intent);
        });
        holder.btnAdd.setOnClickListener(v -> {
            // Xử lý sự kiện click nút "Thêm vào Playlist"
            // Bạn có thể mở một Activity mới để thêm bài hát vào Playlist
            Intent intent = new Intent(context, com.example.soundonline.presentation.playlist.playlistAddToTrack.class);
            intent.putExtra("playlistId", playlist.getPlaylistId()); // Truyền playlistId
            context.startActivity(intent);
        });

    }

// ... (các phần khác của lớp)




    @Override
    public int getItemCount() {
        return playlists != null ? playlists.size() : 0;
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView playlistImage;
        TextView playlistName,playlistDescription,totalTrack;
        Button btnPlay, btnAdd;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistImage = itemView.findViewById(R.id.imagePlaylist);
            playlistName = itemView.findViewById(R.id.textPlaylistName);
            playlistDescription = itemView.findViewById(R.id.textPlaylistDescription);
            totalTrack = itemView.findViewById(R.id.textTotalTrack);
            btnAdd = itemView.findViewById(R.id.btnPlaylist);
            // Inside onBindViewHolder

        }
    }
}
