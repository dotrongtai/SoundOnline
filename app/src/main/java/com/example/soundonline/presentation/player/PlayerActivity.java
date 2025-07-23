package com.example.soundonline.presentation.player;

import android.annotation.SuppressLint;
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
import com.example.soundonline.manager.MediaPlayerManager;
import com.example.soundonline.model.History;
import com.example.soundonline.network.History.AddHistoryRequest;
import com.example.soundonline.network.Like.LikeToggleResponse;
import com.example.soundonline.model.Liked;
import com.example.soundonline.model.Sound;
import com.example.soundonline.network.ApiService;
import com.example.soundonline.presentation.playlist.commentActivity;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class PlayerActivity extends AppCompatActivity {

    private ImageView imageBackground;
    private TextView textTitle, textArtist, textCurrentTime, textDuration;
    private SeekBar seekBar;
    private ImageButton btnPlay, btnDown, btnLike;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    @Inject
    ApiService apiService;
    private boolean isSongLiked = false;
    private String songId;
    private String audioUrl;
    ImageButton btnComment;

    @SuppressLint("WrongViewCast")
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
        btnComment = findViewById(R.id.btnComment);


        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String imageUrl = intent.getStringExtra("image");
        audioUrl = intent.getStringExtra("audioUrl");
        String artist = intent.getStringExtra("artist");
        songId = intent.getStringExtra("songId");

        // Nhận dữ liệu từ Intent
        Sound sound = (Sound) intent.getSerializableExtra("sound");
        int soundId = intent.getIntExtra("soundId", -1); // -1 là giá trị mặc định nếu null
        // Khi gửi Intent
        btnComment.setOnClickListener(v -> {
            Intent commentIntent = new Intent(PlayerActivity.this, commentActivity.class);
            commentIntent.putExtra("soundId", soundId); // send soundId
            startActivity(commentIntent);
        });

        if (title == null || imageUrl == null || audioUrl == null) {
            Log.e("PlayerActivity", "Missing required intent extras: title=" + title + ", imageUrl=" + imageUrl + ", audioUrl=" + audioUrl + ", songId=" + songId);
            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Gán dữ liệu lên UI
        textTitle.setText(title);
        textArtist.setText(artist != null ? artist : "Unknown Artist");
        Glide.with(this).load(imageUrl).into(imageBackground);

        // Bắt đầu phát nhạc
        Log.d("PlayerActivity", "Starting playback with audioUrl: " + audioUrl);
        if (!audioUrl.equals(MediaPlayerManager.currentUrl)) {
            Log.d("PlayerActivity", "Audio URL changed, restarting MediaPlayer with new song");
            MediaPlayerManager.stop(); // Dừng bài cũ
            MediaPlayerManager.play(this, audioUrl, title, artist, null, imageUrl, songId);
        } else if (!MediaPlayerManager.isPlaying()) {
            MediaPlayerManager.resume();
        }

        btnPlay.setImageResource(R.drawable.ic_pause); // cập nhật icon

        // Kiểm tra trạng thái thích ban đầu
        if (songId != null) {
            checkLikeStatus(songId);
        } else {
            Log.w("PlayerActivity", "songId is null, disabling like button");
            btnLike.setEnabled(false);
        }

        // Xử lý nút Play/Pause
        btnPlay.setOnClickListener(v -> {
            if (MediaPlayerManager.isPlaying()) {
                MediaPlayerManager.pause();
                btnPlay.setImageResource(R.drawable.ic_play_arrow);
                Log.d("PlayerActivity", "Paused playback");
            } else {
                if (MediaPlayerManager.getMediaPlayer() == null || !MediaPlayerManager.isMediaPrepared()) {
                    Log.d("PlayerActivity", "MediaPlayer null or not prepared, restarting playback");
                    MediaPlayerManager.play(this, audioUrl, title, artist, null, imageUrl, songId);

                    //gọi API lưu lịch sử nghe:
                    int userId = getSharedPreferences("auth", MODE_PRIVATE).getInt("user_id", -1);
                    if (userId != -1 && songId != null) {
                        try {
                            int soundIdInt = Integer.parseInt(songId);
                            AddHistoryRequest historyRequest = new AddHistoryRequest(soundIdInt, 0, false);
                            apiService.addHistory(historyRequest).enqueue(new Callback<History>() {
                                @Override
                                public void onResponse(Call<History> call, Response<History> response) {
                                    if (response.isSuccessful()) {
                                        Log.d("PlayerActivity", "Lưu lịch sử nghe thành công");
                                    } else {
                                        Log.e("PlayerActivity", "Lỗi khi lưu lịch sử nghe: " + response.code());
                                    }
                                }

                                @Override
                                public void onFailure(Call<History> call, Throwable t) {
                                    Log.e("PlayerActivity", "Lỗi khi gọi API addHistory: " + t.getMessage());
                                }
                            });
                        } catch (NumberFormatException e) {
                            Log.e("PlayerActivity", "Không thể parse songId: " + songId);
                        }
                    }

                } else {
                    MediaPlayerManager.resume();
                    Log.d("PlayerActivity", "Resumed playback");
                }
                btnPlay.setImageResource(R.drawable.ic_pause);
            }
        });

        // Xử lý nút Back
        btnDown.setOnClickListener(v -> finish());

        // Xử lý nút Like
        btnLike.setOnClickListener(v -> {
            if (songId != null) {
                toggleLike(songId);
            } else {
                Toast.makeText(this, "Không thể thích bài hát: Thiếu ID", Toast.LENGTH_SHORT).show();
            }
        });

        // Cập nhật SeekBar
        handler.postDelayed(updateSeekBar = new Runnable() {
            @Override
            public void run() {
                MediaPlayer mp = MediaPlayerManager.getMediaPlayer();
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
                MediaPlayer mp = MediaPlayerManager.getMediaPlayer();
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

    private void checkLikeStatus(String songId) {
        int userId = getSharedPreferences("auth", MODE_PRIVATE).getInt("user_id", -1);
        Log.d("PlayerActivity", "Checking like status with userId: " + userId + ", songId: " + songId);
        if (userId == -1) {
            Log.w("PlayerActivity", "No userId, disabling like button");
            btnLike.setEnabled(false);
            Toast.makeText(this, "Vui lòng đăng nhập để thích bài hát", Toast.LENGTH_SHORT).show();
            return;
        }
        // Since we only use @POST("Like"), set default like state to false
        isSongLiked = false;
        btnLike.setImageResource(R.drawable.ic_heart);
        Log.d("PlayerActivity", "Default like status set to: " + isSongLiked);
    }

    private void toggleLike(String songId) {
        int userId = getSharedPreferences("auth", MODE_PRIVATE).getInt("user_id", -1);
        Log.d("PlayerActivity", "Toggling like with userId: " + userId + ", songId: " + songId);
        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để thích bài hát", Toast.LENGTH_SHORT).show();
            return;
        }

        int songIdInt;
        try {
            songIdInt = Integer.parseInt(songId);
        } catch (NumberFormatException e) {
            Log.e("PlayerActivity", "Invalid songId: " + songId);
            Toast.makeText(this, "ID bài hát không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Sound sound = new Sound(songIdInt);
        Liked likedRequest = new Liked(userId, sound);

        apiService.toggleLike(likedRequest).enqueue(new Callback<LikeToggleResponse>() {
            @Override
            public void onResponse(Call<LikeToggleResponse> call, Response<LikeToggleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isSongLiked = response.body().isLiked();
                    btnLike.setImageResource(isSongLiked ? R.drawable.ic_heart : R.drawable.ic_heart);
                    Toast.makeText(PlayerActivity.this,
                            isSongLiked ? "Đã thích bài hát!" : "Đã bỏ thích bài hát!",
                            Toast.LENGTH_SHORT).show();
                    Log.d("PlayerActivity", "Like toggled successfully, new status: " + isSongLiked);
                } else {
                    Log.e("PlayerActivity", "Failed to toggle like: HTTP " + response.code());
                    Toast.makeText(PlayerActivity.this, "Không thể toggle like: HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LikeToggleResponse> call, Throwable t) {
                Log.e("PlayerActivity", "Failed to toggle like: " + t.getMessage());
                Toast.makeText(PlayerActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
}