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
import com.example.soundonline.model.Album;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private Context context;
    private List<Album> albums;

    public AlbumAdapter(Context context, List<Album> albums) {
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albums.get(position);

        Glide.with(context)
                .load(album.getCoverImageUrl())
                .error(R.drawable.img) // ảnh mặc định nếu lỗi
                .into(holder.albumCover);

        holder.albumTitle.setText(album.getAlbumTitle());
        holder.artistName.setText(album.getArtistName());
        holder.genre.setText(album.getGenre() != null ? album.getGenre() : "Chưa rõ thể loại");
    }

    @Override
    public int getItemCount() {
        return albums != null ? albums.size() : 0;
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView albumCover;
        TextView albumTitle, artistName, genre;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumCover = itemView.findViewById(R.id.imageAlbumCover);
            albumTitle = itemView.findViewById(R.id.textAlbumTitle);
            artistName = itemView.findViewById(R.id.textArtistName);
            genre = itemView.findViewById(R.id.textGenre);
        }
    }
}
