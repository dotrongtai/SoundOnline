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
import com.example.soundonline.model.Album;
import com.example.soundonline.presentation.library.UserAlbumDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private Context context;
    private List<Album> albumList;

    public AlbumAdapter(Context context, List<Album> albumList) {
        this.context = context;
        this.albumList = albumList != null ? albumList : new ArrayList<>();
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_album, parent, false); // Use appropriate layout
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albumList.get(position);

        // Bind data
        holder.textAlbumTitle.setText(album.getAlbumTitle());
        holder.textArtistName.setText(album.getArtistName() != null ? album.getArtistName() : "Unknown Artist");
        holder.textGenre.setText(album.getGenre() != null ? album.getGenre() : "Unknown Genre");

        // Load cover image
        Glide.with(context)
                .load(album.getCoverImageUrl())
                .error(R.drawable.img)
                .into(holder.imageAlbumCover);

        // Handle click to open album details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserAlbumDetailActivity.class);
            intent.putExtra("albumId", album.getAlbumId()); // Pass albumId as int
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void updateAlbums(List<Album> newAlbums) {
        this.albumList = newAlbums != null ? newAlbums : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAlbumCover;
        TextView textAlbumTitle;
        TextView textArtistName;
        TextView textGenre;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAlbumCover = itemView.findViewById(R.id.imageAlbumCover);
            textAlbumTitle = itemView.findViewById(R.id.textAlbumTitle);
            textArtistName = itemView.findViewById(R.id.textArtistName);
            textGenre = itemView.findViewById(R.id.textGenre);
        }
    }
}