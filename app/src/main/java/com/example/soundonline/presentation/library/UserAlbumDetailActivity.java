package com.example.soundonline.presentation.library;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soundonline.R;
import com.example.soundonline.Adapter.SoundAdapter;
import com.example.soundonline.model.Album;
import com.example.soundonline.model.Sound;
import com.example.soundonline.network.ApiService; // Import your ApiService

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject; // For Dagger Hilt
import dagger.hilt.android.AndroidEntryPoint; // For Dagger Hilt

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint // Add this if you are using Dagger Hilt for dependency injection
public class UserAlbumDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ALBUM_ID = "extra_album_id"; // New key for passing Album ID

    @Inject // Inject ApiService if using Dagger Hilt
    ApiService apiService;

    private ImageView imgDetailAlbumCover;
    private TextView tvDetailAlbumTitle;
    private TextView tvDetailArtistName;
    private TextView tvDetailGenre;
    private TextView tvHeaderTitle;

    private RecyclerView recyclerAlbumSongs;
    private SoundAdapter soundAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_album_detail);

        // Initialize Views
        imgDetailAlbumCover = findViewById(R.id.imgDetailAlbumCover);
        tvDetailAlbumTitle = findViewById(R.id.tvDetailAlbumTitle);
        tvDetailArtistName = findViewById(R.id.tvDetailArtistName);
        tvDetailGenre = findViewById(R.id.tvDetailGenre);
        tvHeaderTitle = findViewById(R.id.tvAlbumDetailTitle);
        ImageButton btnBack = findViewById(R.id.btnBack);

        recyclerAlbumSongs = findViewById(R.id.recyclerAlbumSongs);
        recyclerAlbumSongs.setLayoutManager(new LinearLayoutManager(this));

        // Setup Back Button
        btnBack.setOnClickListener(v -> finish());

        // Get Album ID from Intent
        int albumId = getIntent().getIntExtra(EXTRA_ALBUM_ID, -1);

        if (albumId != -1) {
            // Fetch album details using the ID
            fetchAlbumDetails(albumId);
        } else {
            Toast.makeText(this, "Không tìm thấy ID album.", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if no album ID
        }
    }

    private void fetchAlbumDetails(int albumId) {
        apiService.getAlbum(albumId).enqueue(new Callback<Album>() { // Call the API with albumId
            @Override
            public void onResponse(Call<Album> call, Response<Album> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Album album = response.body();
                    // Set data to views
                    Glide.with(UserAlbumDetailActivity.this)
                            .load(album.getCoverImageUrl())
                            .error(R.drawable.img)
                            .into(imgDetailAlbumCover);

                    tvDetailAlbumTitle.setText(album.getAlbumTitle());
                    tvDetailArtistName.setText(album.getArtistName());
                    tvDetailGenre.setText(album.getGenre() != null ? album.getGenre() : "Chưa rõ thể loại");
                    tvHeaderTitle.setText(album.getAlbumTitle());

                    // Populate the RecyclerView for songs
                    List<Sound> soundsInAlbum = album.getSounds();
                    if (soundsInAlbum != null && !soundsInAlbum.isEmpty()) {
                        soundAdapter = new SoundAdapter(UserAlbumDetailActivity.this, soundsInAlbum);
                        recyclerAlbumSongs.setAdapter(soundAdapter);
                    } else {
                        Toast.makeText(UserAlbumDetailActivity.this, "Không có bài hát nào trong album này.", Toast.LENGTH_SHORT).show();
                        soundAdapter = new SoundAdapter(UserAlbumDetailActivity.this, new ArrayList<>()); // Set an empty adapter
                        recyclerAlbumSongs.setAdapter(soundAdapter);
                    }

                } else {
                    Log.e("UserAlbumDetailActivity", "Lỗi tải chi tiết album: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(UserAlbumDetailActivity.this, "Lỗi khi tải thông tin album.", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity on error
                }
            }

            @Override
            public void onFailure(Call<Album> call, Throwable t) {
                Log.e("UserAlbumDetailActivity", "Lỗi kết nối API chi tiết album: " + t.getMessage(), t);
                Toast.makeText(UserAlbumDetailActivity.this, "Lỗi kết nối mạng.", Toast.LENGTH_SHORT).show();
                finish(); // Close activity on network error
            }
        });
    }
}