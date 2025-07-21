package com.example.soundonline.presentation.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.AlbumAdapter;
import com.example.soundonline.Adapter.LikeAdapter;
import com.example.soundonline.Adapter.PlaylistAdapter;
import com.example.soundonline.Adapter.TrendingCategoryAdapter;
import com.example.soundonline.R;
import com.example.soundonline.manager.MediaPlayerManager;
import com.example.soundonline.model.Album;
import com.example.soundonline.model.Category;
import com.example.soundonline.model.Liked;
import com.example.soundonline.model.Playlist;
import com.example.soundonline.network.Album.AlbumsResponse;
import com.example.soundonline.network.ApiService;
import com.example.soundonline.presentation.auth.Login;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class MainActivity extends ComponentActivity {

    @Inject
    ApiService apiService;

    private RecyclerView rvTrending, rvPlaylist, rvFavorite, rvAlbum;
    private PlaylistAdapter playlistAdapter;
    private LikeAdapter likeAdapter;
    private TrendingCategoryAdapter trendingCategoryAdapter;
    private AlbumAdapter albumAdapter;
    private int userId;
    private Button btnLogin;
    private TextView tvPlaylistTitle, tvLikedTitle;
    private Button btnLogout;
    private LinearLayout miniPlayer;
    private TextView miniTitle;
    private ImageButton btnMiniPlay;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogout = findViewById(R.id.btnLogout);

        // Ánh xạ view
        rvTrending = findViewById(R.id.rvTrending);
        rvPlaylist = findViewById(R.id.rvPlaylist);
        rvFavorite = findViewById(R.id.rvFavorite);
        rvAlbum = findViewById(R.id.rvAlbum);
        btnLogin = findViewById(R.id.btnLogin);
        tvPlaylistTitle = findViewById(R.id.tvPlaylistTitle);
        tvLikedTitle = findViewById(R.id.tvLikedTitle);
        miniPlayer = findViewById(R.id.miniPlayer);
        miniTitle = findViewById(R.id.miniPlayerTitle);
        btnMiniPlay = findViewById(R.id.miniPlayerPlayPause);

        updateMiniPlayer(); // Gọi sau khi ánh xạ

        // Cài đặt layout manager
        rvTrending.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvPlaylist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFavorite.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvAlbum.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Lấy userId từ SharedPreferences
        userId = getUserIdFromPreferences();

        if (userId == -1) {
            // Chưa đăng nhập
            Toast.makeText(this, "Vui lòng đăng nhập để sử dụng đầy đủ tính năng.", Toast.LENGTH_SHORT).show();
            btnLogin.setVisibility(View.VISIBLE); // Hiển thị nút Login
            btnLogin.setOnClickListener(v -> {
                startActivity(new Intent(this, Login.class));
                finish();
            });

            // Ẩn các phần phụ thuộc userId
            tvPlaylistTitle.setVisibility(View.GONE);
            rvPlaylist.setVisibility(View.GONE);
            tvLikedTitle.setVisibility(View.GONE);
            rvFavorite.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);

            // Chỉ gọi các API không phụ thuộc userId
            fetchAlbums();
            fetchTrendingCategories();
        } else {
            // Đã đăng nhập
            btnLogin.setVisibility(View.GONE); // Ẩn nút Login
            tvPlaylistTitle.setVisibility(View.VISIBLE);
            rvPlaylist.setVisibility(View.VISIBLE);
            tvLikedTitle.setVisibility(View.VISIBLE);
            rvFavorite.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);

            btnLogout.setOnClickListener(v -> {
                SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                Toast.makeText(MainActivity.this, "Đã đăng xuất.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            });


            fetchUserPlaylists(userId);
            fetchTrendingCategories();
            fetchLikedItems(userId);
            fetchAlbums();
        }
    }

    private int getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }
    private void updateMiniPlayer() {
        if (MediaPlayerManager.getMediaPlayer() != null) {
            miniPlayer.setVisibility(View.VISIBLE);
            miniTitle.setText("🎵 " + MediaPlayerManager.currentTitle);

            // Cập nhật icon Play/Pause
            if (MediaPlayerManager.isPlaying()) {
                btnMiniPlay.setImageResource(R.drawable.ic_pause);
            } else {
                btnMiniPlay.setImageResource(R.drawable.ic_play_arrow);
            }

            // Sự kiện Play/Pause
            btnMiniPlay.setOnClickListener(v -> {
                if (MediaPlayerManager.isPlaying()) {
                    MediaPlayerManager.pause();
                    btnMiniPlay.setImageResource(R.drawable.ic_play_arrow);
                } else {
                    MediaPlayerManager.resume();
                    btnMiniPlay.setImageResource(R.drawable.ic_pause);
                }
            });

            // Click Mini Player để mở lại PlayerActivity
            miniPlayer.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, com.example.soundonline.presentation.player.PlayerActivity.class);
                intent.putExtra("title", MediaPlayerManager.currentTitle);
                intent.putExtra("audioUrl", MediaPlayerManager.currentUrl);
                intent.putExtra("artist", MediaPlayerManager.currentArtist);
                intent.putExtra("uploader", MediaPlayerManager.currentUploader);
                intent.putExtra("image", MediaPlayerManager.currentImage);
                startActivity(intent);
            });
        } else {
            miniPlayer.setVisibility(View.GONE);
        }
    }

    private void fetchUserPlaylists(int userId) {
        apiService.getUserPlaylists(userId).enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Playlist> playlists = response.body();
                    Log.d("MainActivity", "Số playlist: " + playlists.size());
                    playlistAdapter = new PlaylistAdapter(MainActivity.this, playlists);
                    rvPlaylist.setAdapter(playlistAdapter);
                } else {
                    Log.e("MainActivity", "Lỗi playlist: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.e("MainActivity", "Lỗi kết nối playlist", t);
            }
        });
    }

    private void fetchLikedItems(int userId) {
        apiService.getUserLikedTracks(userId).enqueue(new Callback<List<Liked>>() {
            @Override
            public void onResponse(Call<List<Liked>> call, Response<List<Liked>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Liked> likedItems = response.body();
                    Log.d("MainActivity", "Số like: " + likedItems.size());
                    likeAdapter = new LikeAdapter(MainActivity.this, likedItems);
                    rvFavorite.setAdapter(likeAdapter);
                } else {
                    Log.e("MainActivity", "Lỗi likedItems: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Liked>> call, Throwable t) {
                Log.e("MainActivity", "Lỗi likedItems", t);
            }
        });
    }

    private void fetchAlbums() {
        apiService.getAlbums().enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Album> albums = response.body();
                    Log.d("MainActivity", "Số album: " + albums.size());
                    albumAdapter = new AlbumAdapter(MainActivity.this, albums);
                    rvAlbum.setAdapter(albumAdapter);

                } else {
                    Log.e("MainActivity", "Lỗi album: " + response.code() + ", Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {
                Log.e("MainActivity", "Lỗi kết nối API album: " + t.getMessage(), t);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateMiniPlayer(); // Cập nhật MiniPlayer khi quay lại
    }

    private void fetchTrendingCategories() {
        apiService.getTrendingCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    Log.d("MainActivity", "Số trending: " + categories.size());
                    trendingCategoryAdapter = new TrendingCategoryAdapter(MainActivity.this, categories);
                    rvTrending.setAdapter(trendingCategoryAdapter);
                } else {
                    Log.e("MainActivity", "Lỗi trending: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("MainActivity", "Lỗi trending", t);
            }
        });
    }
}