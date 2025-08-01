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
import com.example.soundonline.network.ApiService;
import com.example.soundonline.presentation.auth.Login;
import com.example.soundonline.presentation.library.CategoryActivity;
import com.example.soundonline.presentation.library.LibraryActivity;
import com.example.soundonline.presentation.library.ProfileActivity;
import com.example.soundonline.presentation.player.UploadSoundActivity;
import com.example.soundonline.presentation.playlist.playlistActivity;
import com.example.soundonline.presentation.playlist.searchActivity;

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
    private Button btnLogin, btnCategory, btnProfile, btnLogout, btnUpload;
    private TextView tvPlaylistTitle, tvLikedTitle, miniTitle;
    private LinearLayout miniPlayer;
    private ImageButton btnMiniPlay;
    private Button btnSearch, btnPlaylist;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ view
        rvTrending = findViewById(R.id.rvTrending);
        rvPlaylist = findViewById(R.id.rvPlaylist);
        rvFavorite = findViewById(R.id.rvFavorite);
        rvAlbum = findViewById(R.id.rvAlbum);
        btnCategory = findViewById(R.id.btnCategory);
        btnProfile = findViewById(R.id.btnProfile);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);
        btnUpload = findViewById(R.id.btnUpload);
        tvPlaylistTitle = findViewById(R.id.tvPlaylistTitle);
        tvLikedTitle = findViewById(R.id.tvLikedTitle);
        miniPlayer = findViewById(R.id.miniPlayer);
        miniTitle = findViewById(R.id.miniPlayerTitle);
        btnMiniPlay = findViewById(R.id.miniPlayerPlayPause);
        btnPlaylist = findViewById(R.id.btnPlaylist); // Nút để chuyển đến playlist
        btnPlaylist.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, playlistActivity.class);
            startActivity(intent);
        });
        btnSearch = findViewById(R.id.btnSearch); // Nút để chuyển đến tìm kiếm
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, searchActivity.class);
            startActivity(intent);
        });

        // Cài đặt layout manager
        rvTrending.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvPlaylist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFavorite.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvAlbum.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Lấy userId và token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
        userId = getUserIdFromPreferences();
        String token = prefs.getString("jwt_token", "");
        Log.d("MainActivity", "Received userId: " + userId + ", token: " + token);

        // Xử lý sự kiện click
        btnCategory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            if (userId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Login.class));
            } else {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }
        });

        btnUpload.setOnClickListener(v -> {
            if (userId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập để tải lên bài hát", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Login.class));
            } else {
                Intent intent = new Intent(MainActivity.this, UploadSoundActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, Login.class));
        });

        btnLogout.setOnClickListener(v -> {
            // Dừng nhạc
            MediaPlayerManager.stop();
            // Xóa SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            // Cập nhật trạng thái không đăng nhập
            userId = -1;
            updateUINonLoggedIn();
            Toast.makeText(MainActivity.this, "Đã đăng xuất.", Toast.LENGTH_SHORT).show();
        });

        // Cập nhật UI dựa trên trạng thái đăng nhập
        updateUI();

        // Cài đặt bottom navigation
        setupBottomNavigation();

        // Cập nhật mini player
        updateMiniPlayer();
    }

    private void updateUI() {
        if (userId == -1) {
            updateUINonLoggedIn();
        } else {
            updateUILoggedIn();
        }
    }

    private void updateUINonLoggedIn() {
        btnLogin.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.GONE);
        btnUpload.setVisibility(View.GONE);
        tvPlaylistTitle.setVisibility(View.GONE);
        rvPlaylist.setVisibility(View.GONE);
        tvLikedTitle.setVisibility(View.GONE);
        rvFavorite.setVisibility(View.GONE);
        fetchAlbums();
        fetchTrendingCategories();
    }

    private void updateUILoggedIn() {
        btnLogin.setVisibility(View.GONE);
        btnLogout.setVisibility(View.VISIBLE);
        btnUpload.setVisibility(View.VISIBLE);
        tvPlaylistTitle.setVisibility(View.VISIBLE);
        rvPlaylist.setVisibility(View.VISIBLE);
        tvLikedTitle.setVisibility(View.VISIBLE);
        rvFavorite.setVisibility(View.VISIBLE);
        fetchUserPlaylists(userId);
        fetchTrendingCategories();
        fetchLikedItems(userId);
        fetchAlbums();
    }

    private int getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }

    private void updateMiniPlayer() {
        if (MediaPlayerManager.getMediaPlayer() != null) {
            miniPlayer.setVisibility(View.VISIBLE);
            miniTitle.setText("🎵 " + MediaPlayerManager.currentTitle);

            if (MediaPlayerManager.isPlaying()) {
                btnMiniPlay.setImageResource(R.drawable.ic_pause);
            } else {
                btnMiniPlay.setImageResource(R.drawable.ic_play_arrow);
            }

            btnMiniPlay.setOnClickListener(v -> {
                if (MediaPlayerManager.isPlaying()) {
                    MediaPlayerManager.pause();
                    btnMiniPlay.setImageResource(R.drawable.ic_play_arrow);
                } else {
                    MediaPlayerManager.resume();
                    btnMiniPlay.setImageResource(R.drawable.ic_pause);
                }
            });

            miniPlayer.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, com.example.soundonline.presentation.player.PlayerActivity.class);
                intent.putExtra("title", MediaPlayerManager.currentTitle);
                intent.putExtra("audioUrl", MediaPlayerManager.currentUrl);
                intent.putExtra("artist", MediaPlayerManager.currentArtist);
                intent.putExtra("uploader", MediaPlayerManager.currentUploader);
                intent.putExtra("image", MediaPlayerManager.currentImage);
                intent.putExtra("songId", MediaPlayerManager.currentSongId);
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
                    if (playlists.size() > 0) {
                        playlistAdapter = new PlaylistAdapter(MainActivity.this, playlists);
                        rvPlaylist.setAdapter(playlistAdapter);
                        rvPlaylist.setVisibility(View.VISIBLE);
                        tvPlaylistTitle.setVisibility(View.VISIBLE);
                    } else {
                        rvPlaylist.setVisibility(View.GONE);
                        tvPlaylistTitle.setVisibility(View.GONE);
                    }
                } else {
                    Log.e("MainActivity", "Lỗi playlist: " + response.code());
                    rvPlaylist.setVisibility(View.GONE);
                    tvPlaylistTitle.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.e("MainActivity", "Lỗi kết nối playlist", t);
                rvPlaylist.setVisibility(View.GONE);
                tvPlaylistTitle.setVisibility(View.GONE);
            }
        });
    }

    private void fetchLikedItems(int userId) {
        apiService.getUserLikedTracks(userId).enqueue(new Callback<List<Liked>>() {
            @Override
            public void onResponse(Call<List<Liked>> call, Response<List<Liked>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Liked> likedItems = response.body();
                    if (likedItems.size() > 0) {
                        likeAdapter = new LikeAdapter(MainActivity.this, likedItems);
                        rvFavorite.setAdapter(likeAdapter);
                        rvFavorite.setVisibility(View.VISIBLE);
                        tvLikedTitle.setVisibility(View.VISIBLE);
                    } else {
                        rvFavorite.setVisibility(View.GONE);
                        tvLikedTitle.setVisibility(View.GONE);
                    }
                } else {
                    Log.e("MainActivity", "Lỗi likedItems: " + response.code());
                    rvFavorite.setVisibility(View.GONE);
                    tvLikedTitle.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Liked>> call, Throwable t) {
                Log.e("MainActivity", "Lỗi likedItems", t);
                rvFavorite.setVisibility(View.GONE);
                tvLikedTitle.setVisibility(View.GONE);
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
        updateMiniPlayer();
        if (userId != -1) {
            fetchLikedItems(userId); // Làm mới danh sách yêu thích
        }
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

    private void setupBottomNavigation() {
        findViewById(R.id.btnLibrary).setOnClickListener(v -> {
            startActivity(new Intent(this, LibraryActivity.class));
            finish();
        });
    }
}