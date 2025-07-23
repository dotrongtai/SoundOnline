package com.example.soundonline.presentation.playlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.SoundAdapter;
import com.example.soundonline.Adapter.SoundAddAdapter;
import com.example.soundonline.R;
import com.example.soundonline.model.Sound;
import com.example.soundonline.network.ApiService;
import com.example.soundonline.network.Playlist.CheckAvailableForPlaylistRequest;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class playlistAddToTrack extends ComponentActivity {
    RecyclerView rvSoundTrack; // RecyclerView để hiển thị danh sách track trong playlist
    private SoundAddAdapter soundAddAdapter; // Adapter để hiển thị danh sách track
    @Inject
    ApiService apiService; // Inject ApiService nếu cần thiết
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_add); // Gắn layout vào activity
        // Lấy playlistId từ Intent
        int playlistId = getIntent().getIntExtra("playlistId", -1); // -1 là giá trị mặc định nếu không có
        String playlistIdString = String.valueOf(playlistId);
        rvSoundTrack = findViewById(R.id.rvSoundTrack);
        rvSoundTrack.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false));
        int userId = getUserIdFromPreferences();
        fetchTracksForPlaylist(playlistId, userId);


    }

    private int getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }

    private void fetchTracksForPlaylist(int playlistId, int userId) {
        CheckAvailableForPlaylistRequest request = new CheckAvailableForPlaylistRequest(playlistId, userId);
        apiService.checkAvailableForPlaylist(request).enqueue(new Callback<List<Sound>>() {
            @Override
            public void onResponse(Call<List<Sound>> call, Response<List<Sound>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Sound> sounds = response.body();
                    soundAddAdapter = new SoundAddAdapter(playlistAddToTrack.this, apiService, sounds, playlistId);
                    rvSoundTrack.setAdapter(soundAddAdapter);
                } else {
                    Log.e("playlistAddToTrack", "API failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Sound>> call, Throwable t) {
                Log.e("playlistAddToTrack", "API error", t);
            }
        });
    }





}
