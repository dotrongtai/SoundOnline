package com.example.soundonline.presentation.player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.soundonline.R;
import com.example.soundonline.manager.MediaPlayerManager;

import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {

    private ImageView imageBackground;
    private TextView textTitle, textArtist, textUploader, textCurrentTime, textDuration;
    private SeekBar seekBar;
    private ImageButton btnPlay,btnDown;

    private Handler handler = new Handler();
    private Runnable updateSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playmusic);

        // Ánh xạ view
        imageBackground = findViewById(R.id.imageBackground);
        textTitle = findViewById(R.id.textTitle);
        textArtist = findViewById(R.id.textArtist);
        textUploader = findViewById(R.id.textUploader);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textDuration = findViewById(R.id.textDuration);
        seekBar = findViewById(R.id.seekBar);
        btnPlay = findViewById(R.id.btnPlay);
        btnDown = findViewById(R.id.btnDown);
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String imageUrl = intent.getStringExtra("image");
        String audioUrl = intent.getStringExtra("audioUrl");
        String artist = intent.getStringExtra("artist");
        String uploader = intent.getStringExtra("uploader");

        if (title == null || imageUrl == null || audioUrl == null) {
            finish();
            return;
        }

        // Gán dữ liệu ra UI
        textTitle.setText(title);
        textArtist.setText(artist != null ? artist : "Unknown Artist");
        textUploader.setText((uploader != null && !uploader.trim().isEmpty()) ? "Upload by: " + uploader : "Upload by: Unknown");
        Glide.with(this).load(imageUrl).into(imageBackground);

        // Bắt đầu phát nhạc
        MediaPlayerManager.play(this, audioUrl, title, artist, uploader, imageUrl);


        // Xử lý nút Play/Pause
        btnPlay.setOnClickListener(v -> {
            if (MediaPlayerManager.isPlaying()) {
                MediaPlayerManager.pause();
                btnPlay.setImageResource(R.drawable.ic_play_arrow);
            } else {
                MediaPlayerManager.resume();
                btnPlay.setImageResource(R.drawable.ic_pause);
            }
        }

        );
        btnDown.setOnClickListener(v->{
            finish();
        });

        // Theo dõi quá trình phát để cập nhật SeekBar
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
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
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
