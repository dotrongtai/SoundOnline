package com.example.soundonline.presentation.playlist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.SoundAdapter;
import com.example.soundonline.Adapter.SoundAddAdapter;
import com.example.soundonline.R;
import com.example.soundonline.model.Sound;
import com.example.soundonline.model.Track;
import com.example.soundonline.network.ApiService;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@AndroidEntryPoint
public class PlaylistTrackActivity extends ComponentActivity {
    RecyclerView rvSoundTrack; // RecyclerView để hiển thị danh sách track trong playlist
    @Inject
    ApiService apiService;
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_track); // Gắn layout vào activity
        // Lấy playlistId từ Intent
        int playlistId = getIntent().getIntExtra("playlistId", -1); // -1 là giá trị mặc định nếu không có
        String playlistIdString = String.valueOf(playlistId);

        rvSoundTrack = findViewById(R.id.rvSoundTrack); // Ánh xạ RecyclerView
        rvSoundTrack.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        fetchPlaylistTracks(playlistId);
    }

    private void fetchPlaylistTracks(int playlistId) {
        apiService.getPlaylistTracks(playlistId).enqueue(new Callback<List<Sound>>() {
            @Override
            public void onResponse(Call<List<Sound>> call, Response<List<Sound>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Sound> soundList = response.body();
                    Log.d("fetchSounds", "Số lượng: " + soundList.size());
                    SoundAdapter SoundAddAdapter = new SoundAdapter(PlaylistTrackActivity.this, soundList);
                    rvSoundTrack.setAdapter(SoundAddAdapter);
                    SoundAddAdapter.notifyDataSetChanged();
                } else {
                    Log.e("fetchSounds", "Lỗi phản hồi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Sound>> call, Throwable t) {
                Log.e("fetchSounds", "Lỗi kết nối API", t);
            }
        });
    }

}
