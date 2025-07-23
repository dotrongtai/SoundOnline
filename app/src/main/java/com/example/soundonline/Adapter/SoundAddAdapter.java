package com.example.soundonline.Adapter;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soundonline.R;
import com.example.soundonline.model.Sound;
import com.example.soundonline.model.Track;
import com.example.soundonline.network.ApiService;
import com.example.soundonline.network.Playlist.AddSoundToPlaylistRequest;
import com.example.soundonline.network.Playlist.AddSoundToPlaylistResponse;
import com.example.soundonline.network.Playlist.AddTrackResponse;
import com.example.soundonline.network.Playlist.AddTrackToPlaylistRequest;
import com.example.soundonline.presentation.player.PlayerActivity;
import com.example.soundonline.presentation.player.PlayerActivityForSearch;
import com.example.soundonline.presentation.playlist.PlaylistTrackActivity;
import com.example.soundonline.presentation.playlist.playlistActivity;
import com.example.soundonline.presentation.playlist.playlistAddToTrack;
import com.example.soundonline.presentation.playlist.playlistAddToTrack;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SoundAddAdapter extends RecyclerView.Adapter<SoundAddAdapter.SoundViewHolder> {

    private Context context;
    private List<Sound> soundList;
    ApiService apiService;
    private int playlistId; // ID của playlist để thêm track

    public SoundAddAdapter(Context context, ApiService apiService, List<Sound> soundList, int playlistId) {
        this.context = context;
        this.soundList = soundList;
        this.apiService = apiService;
        this.playlistId = playlistId;
    }

    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sound_add, parent, false);
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
        holder.btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(context, playlistAddToTrack.class);
            intent.putExtra("sound", sound); // Truyền Sound (implements Serializable)
            context.startActivity(intent);
            addSoundToPlaylist(playlistId, sound.getSoundId());
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
        Button btnAdd;

        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCover = itemView.findViewById(R.id.imageCover);
            textTitle = itemView.findViewById(R.id.textTitle);
            textArtist = itemView.findViewById(R.id.textArtist);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
    private void addSoundToPlaylist(int playlistId, int soundId) {
        AddSoundToPlaylistRequest request = new AddSoundToPlaylistRequest(playlistId, soundId);

        apiService.addSoundToPlaylist(request).enqueue(new Callback<AddSoundToPlaylistResponse>() {
            @Override
            public void onResponse(Call<AddSoundToPlaylistResponse> call, Response<AddSoundToPlaylistResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Đã thêm nhạc vào playlist!", Toast.LENGTH_SHORT).show();

                    // Trở về playlistActivity sau khi thêm thành công
                    Intent intent = new Intent(context, playlistActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).finish();
                    }
                } else {
                    Log.e("AddTrack", "API error: " + response.message());
                    Toast.makeText(context, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddSoundToPlaylistResponse> call, Throwable throwable) {
                Log.e("AddTrack", "Lỗi kết nối: " + throwable.getMessage());
                Toast.makeText(context, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
