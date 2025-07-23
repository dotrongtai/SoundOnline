package com.example.soundonline.presentation.playlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.ComponentActivity;

import com.example.soundonline.R;
import com.example.soundonline.network.ApiService;
import com.example.soundonline.network.Playlist.CreatePlaylistRequest;
import com.example.soundonline.network.Playlist.CreatePlaylistResponse;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class createPlaylist extends ComponentActivity {
    @Inject
    ApiService apiService;
    EditText etPlaylistName, etPlaylistDescription;
    Button btnCreatePlaylist;
    private int userId;
    private String coverImageUrl = null;
    private boolean isPublic = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);

        etPlaylistName = findViewById(R.id.etPlaylistName);
        etPlaylistDescription = findViewById(R.id.etPlaylistDescription);
        btnCreatePlaylist = findViewById(R.id.btAddPlaylist);
        userId = getUserIdFromPreferences();

        btnCreatePlaylist.setOnClickListener(v -> {
            String playlistName = etPlaylistName.getText().toString().trim();
            String playlistDescription = etPlaylistDescription.getText().toString().trim();

            if (playlistName.isEmpty()) {
                Toast.makeText(this, "Please enter a playlist name", Toast.LENGTH_SHORT).show();
                return;
            }

            CreatePlaylistRequest request = new CreatePlaylistRequest(userId, playlistName, playlistDescription, coverImageUrl, isPublic);
            apiService.createPlaylist(request).enqueue(new Callback<CreatePlaylistResponse>() {
                @Override
                public void onResponse(Call<CreatePlaylistResponse> call, Response<CreatePlaylistResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(createPlaylist.this, "Playlist created successfully", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // ➕ THÊM DÒNG NÀY
                        finish();             // 🔙 QUAY VỀ ACTIVITY TRƯỚC
                    } else {
                        Toast.makeText(createPlaylist.this, "Failed to create playlist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CreatePlaylistResponse> call, Throwable t) {
                    Toast.makeText(createPlaylist.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private int getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }
}
