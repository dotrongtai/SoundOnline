package com.example.soundonline.presentation.playlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.PlaylistAdapter;
import com.example.soundonline.R;
import com.example.soundonline.model.Playlist;
import com.example.soundonline.network.ApiService;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class playlistActivity extends ComponentActivity {

    @Inject
    ApiService apiService;
    private RecyclerView rvPlaylist;
    private PlaylistAdapter playlistAdapter;
    private Button btnAddPlaylist;
    private int userId;

    private static final int CREATE_PLAYLIST_REQUEST_CODE = 1001; // ✅ Thêm requestCode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        btnAddPlaylist = findViewById(R.id.btAdd);
        rvPlaylist = findViewById(R.id.rvPlaylist);
        rvPlaylist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        btnAddPlaylist.setOnClickListener(v -> {
            Intent intent = new Intent(playlistActivity.this, createPlaylist.class);
            startActivityForResult(intent, CREATE_PLAYLIST_REQUEST_CODE); // ✅ Dùng startActivityForResult
        });

        userId = getUserIdFromPreferences();
        fetchUserPlaylists(userId);
    }

    private int getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }

    private void fetchUserPlaylists(int userId) {
        apiService.getUserPlaylists(userId).enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Playlist> playlists = response.body();
                    Log.d("PlaylistActivity", "Số playlist: " + playlists.size());

                    playlistAdapter = new PlaylistAdapter(playlistActivity.this, playlists);
                    rvPlaylist.setAdapter(playlistAdapter);
                } else {
                    Log.e("PlaylistActivity", "Lỗi playlist: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.e("PlaylistActivity", "Lỗi kết nối playlist", t);
            }
        });
    }

    // ✅ Bắt kết quả trả về sau khi tạo playlist thành công
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_PLAYLIST_REQUEST_CODE && resultCode == RESULT_OK) {
            fetchUserPlaylists(userId); // 🔁 Làm mới danh sách playlist
        }
    }
}
