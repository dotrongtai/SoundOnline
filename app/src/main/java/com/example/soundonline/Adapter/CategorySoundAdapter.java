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
import com.example.soundonline.model.Sound;

import java.util.List;
import java.util.Locale;

public class CategorySoundAdapter extends RecyclerView.Adapter<CategorySoundAdapter.SoundViewHolder> {

    private Context context;
    private List<Sound> sounds;

    public CategorySoundAdapter(Context context, List<Sound> sounds) {
        this.context = context;
        this.sounds = sounds;
    }

    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_sound, parent, false);
        return new SoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {
        Sound sound = sounds.get(position);

        Glide.with(context)
                .load(sound.getCoverImageUrl())
                .error(R.drawable.img)
                .into(holder.soundImage);

        holder.soundTitle.setText(sound.getTitle());
        holder.soundArtist.setText(sound.getArtistName());
        holder.soundDuration.setText(formatDuration(sound.getDuration()));
    }

    private String formatDuration(int duration) {
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    @Override
    public int getItemCount() {
        return sounds != null ? sounds.size() : 0;
    }

    public static class SoundViewHolder extends RecyclerView.ViewHolder {
        ImageView soundImage;
        TextView soundTitle, soundArtist, soundDuration;

        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);
            soundImage = itemView.findViewById(R.id.imageCategorySound);
            soundTitle = itemView.findViewById(R.id.textCategorySoundTitle);
            soundArtist = itemView.findViewById(R.id.textCategorySoundArtist);
            soundDuration = itemView.findViewById(R.id.textCategorySoundDuration);
        }
    }
}