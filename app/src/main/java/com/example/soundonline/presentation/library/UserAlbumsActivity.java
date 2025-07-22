package com.example.soundonline.presentation.library;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton; // Make sure this is imported if using ImageButton
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.AlbumAdapter;
import com.example.soundonline.R;
import com.example.soundonline.model.Album;
import com.example.soundonline.network.ApiService; // Assume you have an ApiService
import com.example.soundonline.presentation.main.MainActivity; // For home button

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject; // For Dagger Hilt
import dagger.hilt.android.AndroidEntryPoint; // For Dagger Hilt

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint // Add this if you are using Dagger Hilt for dependency injection
public class UserAlbumsActivity extends AppCompatActivity {

    @Inject // Inject ApiService if using Dagger Hilt
    ApiService apiService;

    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_albums); // Ensure this matches the XML

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerUserAlbums); // ID from activity_user_albums.xml
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        albumAdapter = new AlbumAdapter(this, new ArrayList<>()); // Initialize with empty list
        recyclerView.setAdapter(albumAdapter);

        // Get userId from SharedPreferences
        userId = getUserIdFromPreferences();
        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem album của bạn.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class)); // Go back to main or login
            finish();
            return;
        }

        // Fetch user's albums
        fetchAlbums();

        // Setup Header and Back Button
        setupHeader();

        // Set up bottom navigation
        setupBottomNavigation();
    }

    private int getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }

    private void fetchAlbums() {
        apiService.getAlbums().enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Album> albums = response.body();
                    Log.d("MainActivity", "Số album: " + albums.size());
                    albumAdapter = new AlbumAdapter(UserAlbumsActivity.this, albums);
                    recyclerView.setAdapter(albumAdapter);

                } else {
                    Log.e("MainActivity", "Lỗi album: " + response.code() + ", Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {
                Log.e("UserAlbumsActivity", "Lỗi kết nối API album: " + t.getMessage(), t);
            }
        });
    }

    private void setupHeader() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish()); // Go back to the previous activity
        // You can also set a custom title if needed, or update the existing tvTitle
        // TextView tvTitle = findViewById(R.id.tvTitle);
        // tvTitle.setText("Album của tôi"); // Or whatever appropriate title
    }

    private void setupBottomNavigation() {
        // Implement logic for bottom navigation buttons
        findViewById(R.id.btnHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            // startActivity(new Intent(this, SearchActivity.class));
            // finish();
            Toast.makeText(this, "Search Clicked", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnLibrary).setOnClickListener(v -> {
            // Already on Library section, do nothing or refresh if needed.
            Toast.makeText(this, "Library Clicked (Already here)", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnCategory).setOnClickListener(v -> {
            // startActivity(new Intent(this, CategoryActivity.class));
            // finish();
            Toast.makeText(this, "Category Clicked", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnPlaylist).setOnClickListener(v -> {
            // startActivity(new Intent(this, PlaylistActivity.class));
            // finish();
            Toast.makeText(this, "Playlist Clicked", Toast.LENGTH_SHORT).show();
        });
    }
}