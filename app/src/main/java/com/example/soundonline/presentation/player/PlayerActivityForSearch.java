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
import com.example.soundonline.model.Sound;
import com.example.soundonline.presentation.playlist.commentActivity;
import java.util.concurrent.TimeUnit;

public class PlayerActivityForSearch extends AppCompatActivity {

    private ImageView imageBackground;
    private TextView textTitle, textArtist, textUploader, textCurrentTime, textDuration;
    private SeekBar seekBar;
    private ImageButton btnPlay, btnDown, btnComment;

    private Handler handler = new Handler();
    private Runnable updateSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playmusic);

        // Ánh xạ View
        imageBackground = findViewById(R.id.imageBackground);
        textTitle = findViewById(R.id.textTitle);
        textArtist = findViewById(R.id.textArtist);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textDuration = findViewById(R.id.textDuration);
        seekBar = findViewById(R.id.seekBar);
        btnPlay = findViewById(R.id.btnPlay);
        btnDown = findViewById(R.id.btnDown);
        btnComment = findViewById(R.id.btnComment);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        Sound sound = (Sound) intent.getSerializableExtra("sound");
        // Khi gửi Intent
        intent.putExtra("soundId", sound.getSoundId());

        int soundId = intent.getIntExtra("soundId", -1);

        btnComment.setOnClickListener(v -> {
            Intent commentIntent = new Intent(PlayerActivityForSearch.this, commentActivity.class);
            commentIntent.putExtra("soundId", soundId); // send soundId
            startActivity(commentIntent);
        });

        String title = intent.getStringExtra("title");
        String imageUrl = intent.getStringExtra("image");
        String audioUrl = intent.getStringExtra("audioUrl");
        String artist = intent.getStringExtra("artist");
        String uploader = intent.getStringExtra("uploader");

        // Nếu có Sound object, override các giá trị
        if (sound != null) {
            title = sound.getTitle();
            imageUrl = sound.getCoverImageUrl();
            audioUrl = sound.getFileUrl();
            artist = sound.getArtistName();
            uploader = sound.getUploaderName();
        }

        // Kiểm tra dữ liệu hợp lệ
        if (title == null || imageUrl == null || audioUrl == null) {
            finish();
            return;
        }

        // Gán dữ liệu ra UI
        textTitle.setText(title);
        textArtist.setText(artist != null ? artist : "Unknown Artist");
        Glide.with(this).load(imageUrl).into(imageBackground);

        // Phát nhạc
        String soundIdStr = String.valueOf(sound.getSoundId()); // ép int sang String
        MediaPlayerManager.play(this, audioUrl, title, artist, uploader, imageUrl, soundIdStr);

        // Xử lý Play/Pause
        btnPlay.setOnClickListener(v -> {
            if (MediaPlayerManager.isPlaying()) {
                MediaPlayerManager.pause();
                btnPlay.setImageResource(R.drawable.ic_play_arrow);
            } else {
                MediaPlayerManager.resume();
                btnPlay.setImageResource(R.drawable.ic_pause);
            }
        });

        btnDown.setOnClickListener(v -> finish());

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

        // Xử lý SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MediaPlayer mp = MediaPlayerManager.getMediaPlayer();
                if (fromUser && mp != null) {
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
