package com.example.soundonline.presentation.player;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.soundonline.R;
import com.example.soundonline.manager.MediaPlayerManagerForHistory;
import com.example.soundonline.model.History;
import com.example.soundonline.network.History.AddHistoryRequest;
import com.example.soundonline.network.Like.LikeToggleResponse;
import com.example.soundonline.model.Liked;
import com.example.soundonline.model.Sound;
import com.example.soundonline.network.ApiService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class PlayerForHistory extends AppCompatActivity {

    private ImageView imageBackground;
    private TextView textTitle, textArtist, textCurrentTime, textDuration;
    private SeekBar seekBar;
    private ImageButton btnPlay, btnDown, btnLike;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    @Inject
    ApiService apiService;
    private boolean isSongLiked = false;
    private int songId;
    private String audioUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playmusic);

        // Ánh xạ view
        imageBackground = findViewById(R.id.imageBackground);
        textTitle = findViewById(R.id.textTitle);
        textArtist = findViewById(R.id.textArtist);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textDuration = findViewById(R.id.textDuration);
        seekBar = findViewById(R.id.seekBar);
        btnPlay = findViewById(R.id.btnPlay);
        btnDown = findViewById(R.id.btnDown);
        btnLike = findViewById(R.id.btnLike);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String imageUrl = intent.getStringExtra("image");
        audioUrl = intent.getStringExtra("audioUrl");
        String artist = intent.getStringExtra("artist");
        songId = intent.getIntExtra("songId", -1); // Change to getIntExtra with default value -1

        if (title == null || imageUrl == null || audioUrl == null || songId == -1) {
            Log.e("PlayerForHistory", "Missing required intent extras: title=" + title + ", imageUrl=" + imageUrl + ", audioUrl=" + audioUrl + ", songId=" + songId);
            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Gán dữ liệu lên UI
        textTitle.setText(title);
        textArtist.setText(artist != null ? artist : "Unknown Artist");
        Glide.with(this).load(imageUrl).into(imageBackground);

        // Xử lý nhận playlist
        ArrayList<Sound> playlist = (ArrayList<Sound>) getIntent().getSerializableExtra("playlist");
        if (playlist != null) {
            MediaPlayerManagerForHistory.playPlaylistSequentially(this, playlist);
        }

        // Bắt đầu phát nhạc
        Log.d("PlayerForHistory", "Starting playback with audioUrl: " + audioUrl);
        if (!audioUrl.equals(MediaPlayerManagerForHistory.currentUrl)) {
            Log.d("PlayerForHistory", "Audio URL changed, restarting MediaPlayer with new song");
            MediaPlayerManagerForHistory.stop(); // Dừng bài cũ
            MediaPlayerManagerForHistory.play(this, audioUrl, title, artist, null, imageUrl, songId);

            // Gọi API lưu lịch sử nghe
            int userId = getSharedPreferences("auth", MODE_PRIVATE).getInt("user_id", -1);
            if (userId != -1 && songId != -1) {
                AddHistoryRequest historyRequest = new AddHistoryRequest(songId, 0, false);
                apiService.addHistory(historyRequest).enqueue(new Callback<History>() {
                    @Override
                    public void onResponse(Call<History> call, Response<History> response) {
                        if (response.isSuccessful()) {
                            Log.d("PlayerForHistory", "Lưu lịch sử nghe thành công");
                        } else {
                            Log.e("PlayerForHistory", "Lỗi khi lưu lịch sử nghe: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<History> call, Throwable t) {
                        Log.e("PlayerForHistory", "Lỗi khi gọi API addHistory: " + t.getMessage());
                    }
                });
            }
        } else if (!MediaPlayerManagerForHistory.isPlaying()) {
            MediaPlayerManagerForHistory.resume();
        }

        btnPlay.setImageResource(R.drawable.ic_pause); // Cập nhật icon

        // Kiểm tra trạng thái thích ban đầu
        if (songId != -1) {
            checkLikeStatus(songId);
        } else {
            Log.w("PlayerForHistory", "songId is invalid, disabling like button");
            btnLike.setEnabled(false);
        }

        // Cập nhật SeekBar
        handler.postDelayed(updateSeekBar = new Runnable() {
            @Override
            public void run() {
                MediaPlayer mp = MediaPlayerManagerForHistory.getMediaPlayer();
                if (mp != null && mp.isPlaying()) {
                    int current = mp.getCurrentPosition();
                    int duration = mp.getDuration();
                    seekBar.setMax(duration);
                    seekBar.setProgress(current);
                    textCurrentTime.setText(formatTime(current));
                    textDuration.setText(formatTime(duration));
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);

        // SeekBar listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MediaPlayer mp = MediaPlayerManagerForHistory.getMediaPlayer();
                if (fromUser && mp != null && mp.isPlaying()) {
                    mp.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void checkLikeStatus(int songId) {
        int userId = getSharedPreferences("auth", MODE_PRIVATE).getInt("user_id", -1);
        Log.d("PlayerForHistory", "Checking like status with userId: " + userId + ", songId: " + songId);
        if (userId == -1) {
            Log.w("PlayerForHistory", "No userId, disabling like button");
            btnLike.setEnabled(false);
            Toast.makeText(this, "Vui lòng đăng nhập để thích bài hát", Toast.LENGTH_SHORT).show();
            return;
        }
        // Since we only use @POST("Like"), set default like state to false
        isSongLiked = false;
        btnLike.setImageResource(R.drawable.ic_heart);
        Log.d("PlayerForHistory", "Default like status set to: " + isSongLiked);
    }

    private void toggleLike(int songId) {
        int userId = getSharedPreferences("auth", MODE_PRIVATE).getInt("user_id", -1);
        Log.d("PlayerForHistory", "Toggling like with userId: " + userId + ", songId: " + songId);
        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để thích bài hát", Toast.LENGTH_SHORT).show();
            return;
        }

        Sound sound = new Sound(songId);
        Liked likedRequest = new Liked(userId, sound);

        apiService.toggleLike(likedRequest).enqueue(new Callback<LikeToggleResponse>() {
            @Override
            public void onResponse(Call<LikeToggleResponse> call, Response<LikeToggleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isSongLiked = response.body().isLiked();
                    btnLike.setImageResource(isSongLiked ? R.drawable.ic_heart : R.drawable.ic_heart);
                    Toast.makeText(PlayerForHistory.this,
                            isSongLiked ? "Đã thích bài hát!" : "Đã bỏ thích bài hát!",
                            Toast.LENGTH_SHORT).show();
                    Log.d("PlayerForHistory", "Like toggled successfully, new status: " + isSongLiked);
                } else {
                    Log.e("PlayerForHistory", "Failed to toggle like: HTTP " + response.code());
                    Toast.makeText(PlayerForHistory.this, "Không thể toggle like: HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LikeToggleResponse> call, Throwable t) {
                Log.e("PlayerForHistory", "Failed to toggle like: " + t.getMessage());
                Toast.makeText(PlayerForHistory.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatTime(int millis) {
        return String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBar);
    }


    private int currentTrackIndex = 0;
    private ArrayList<String> currentPlaylist = new ArrayList<>();

    private void playPlaylist(ArrayList<String> playlist) {
        currentPlaylist = playlist;
        currentTrackIndex = 0;
        playCurrentTrack();

        MediaPlayerManagerForHistory.setOnCompletionListener(mp-> {
            currentTrackIndex++;
            if (currentTrackIndex < currentPlaylist.size()) {
                playCurrentTrack();
            } else {
                Toast.makeText(this, "Đã phát hết danh sách", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playCurrentTrack() {
        String url = currentPlaylist.get(currentTrackIndex);
        MediaPlayerManagerForHistory.stop();
        MediaPlayerManagerForHistory.play(this, url, "Bài hát #" + (currentTrackIndex + 1), "", null, null, -1);
    }



}
