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
import com.example.soundonline.model.Liked;
import com.example.soundonline.model.Sound;
import com.example.soundonline.R;

import java.util.List;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.LikeViewHolder> {

    private Context context;
    private List<Liked> likedSongs;

    public LikeAdapter(Context context, List<Liked> likedSongs) {
        this.context = context;
        this.likedSongs = likedSongs;
    }

    @NonNull
    @Override
    public LikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_like_song, parent, false);
        return new LikeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikeViewHolder holder, int position) {
        Liked liked = likedSongs.get(position);
        Sound song = liked.getSound(); // 🔁 lấy ra bài hát

        holder.txtSongTitle.setText(song.getTitle());

        Glide.with(context)
                .load(song.getCoverImageUrl())
                .error(R.drawable.img)
                .into(holder.imgSong);
    }

    @Override
    public int getItemCount() {
        return likedSongs.size();
    }

    public static class LikeViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong;
        TextView txtSongTitle;

        public LikeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imgSong);
            txtSongTitle = itemView.findViewById(R.id.txtSongTitle);
        }
    }
}
