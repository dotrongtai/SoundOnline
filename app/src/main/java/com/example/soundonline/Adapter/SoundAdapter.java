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
import com.example.soundonline.model.Sound;
import com.example.soundonline.presentation.player.PlayerActivity;
import com.example.soundonline.presentation.player.PlayerActivityForSearch;

import java.util.List;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.SoundViewHolder> {

    private Context context;
    private List<Sound> soundList;

    public SoundAdapter(Context context, List<Sound> soundList) {
        this.context = context;
        this.soundList = soundList;
    }

    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sound, parent, false);
        return new SoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {
        Sound sound = soundList.get(position);

        holder.textTitle.setText(sound.getTitle());
        holder.textArtist.setText(sound.getArtistName());

        Glide.with(context)
                .load(sound.getCoverImageUrl())
                .error(R.drawable.img) // fallback image
                .into(holder.imageCover);

        // Gán sự kiện click nút "Play"
        holder.btnPlay.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerActivityForSearch.class);
            intent.putExtra("sound", sound); // Truyền Sound (implements Serializable)
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return soundList != null ? soundList.size() : 0;
    }

    public static class SoundViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCover;
        TextView textTitle, textArtist;
        Button btnPlay;

        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCover = itemView.findViewById(R.id.imageCover);
            textTitle = itemView.findViewById(R.id.textTitle);
            textArtist = itemView.findViewById(R.id.textArtist);
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }
    }
}
