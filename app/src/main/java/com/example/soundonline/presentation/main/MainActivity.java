package com.example.soundonline.presentation.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

    private RecyclerView rvTrending, rvPlaylist,rvFavorite, rvAlbum;
    private PlaylistAdapter playlistAdapter;
    private LikeAdapter likeAdapter;
    private TrendingCategoryAdapter trendingCategoryAdapter;
    private AlbumAdapter albumAdapter;
    private int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ view
        rvTrending = findViewById(R.id.rvTrending);
        rvPlaylist = findViewById(R.id.rvPlaylist);
        rvFavorite = findViewById(R.id.rvFavorite);
        rvAlbum = findViewById(R.id.rvAlbum);
        // Cài đặt layout manager
        rvTrending.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvPlaylist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFavorite.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)); // Hiển thị like dọc
        rvAlbum.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        // Lấy userId từ SharedPreferences
        userId = getUserIdFromPreferences();

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy userId. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        fetchUserPlaylists(userId);
        fetchTrendingCategories();
        fetchLikedItems(userId);
        fetchAlbums();
        ;
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
