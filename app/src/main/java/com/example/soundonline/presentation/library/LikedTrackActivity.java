package com.example.soundonline.presentation.library;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.LikeAdapter; // Reuse the LikeAdapter from MainActivity
import com.example.soundonline.Adapter.LikedTracksAdapter;
import com.example.soundonline.R;
import com.example.soundonline.model.Liked; // Use Liked model consistent with MainActivity
import com.example.soundonline.network.ApiService;
import com.example.soundonline.presentation.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class LikedTrackActivity extends ComponentActivity {

    @Inject
    ApiService apiService;

    private RecyclerView recyclerView;
    private LikedTracksAdapter adapter; // Reuse LikeAdapter for consistency
    private int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_track);

        // Setup toolbar
        ImageButton backButton = findViewById(R.id.btnBack);
        TextView titleView = findViewById(R.id.tvTitle);
        titleView.setText("Liked Tracks");

        // Initialize views
        recyclerView = findViewById(R.id.recyclerLikedTracks);
        adapter = new LikedTracksAdapter(this, new ArrayList<>()); // Khởi tạo với list rỗng
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        // Get userId from SharedPreferences
        userId = getUserIdFromPreferences();
        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem các bài hát đã thích.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Fetch liked tracks
        fetchLikedTracks();

// Handle back
        backButton.setOnClickListener(v -> onBackPressed());

        // Set up bottom navigation
        setupBottomNavigation();
    }

    private int getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }

    private void fetchLikedTracks() {
        apiService.getUserLikedTracks(userId).enqueue(new Callback<List<Liked>>() {
            @Override
            public void onResponse(Call<List<Liked>> call, Response<List<Liked>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Liked> likedTracks = response.body();
                    if (likedTracks != null && !likedTracks.isEmpty()) {
                        adapter.updateData(likedTracks); // Update adapter with fetched data
                    } else {
                        Toast.makeText(LikedTrackActivity.this, "Không có bài hát nào được thích.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LikedTrackActivity.this, "Không thể tải danh sách bài hát đã thích.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Liked>> call, Throwable t) {
                Toast.makeText(LikedTrackActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        findViewById(R.id.btnHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            Toast.makeText(this, "Search feature chưa được hỗ trợ.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnLibrary).setOnClickListener(v -> {
            // Current screen
        });

        findViewById(R.id.btnCategory).setOnClickListener(v -> {
            Toast.makeText(this, "Category feature chưa được hỗ trợ.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnPlaylist).setOnClickListener(v -> {
            Toast.makeText(this, "Playlist feature chưa được hỗ trợ.", Toast.LENGTH_SHORT).show();
        });
    }
}