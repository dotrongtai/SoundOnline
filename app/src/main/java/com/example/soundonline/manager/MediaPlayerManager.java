package com.example.soundonline.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class MediaPlayerManager {
    public static MediaPlayer mediaPlayer;
    public static boolean isPrepared = false;
    public static String currentTitle = "";
    public static String currentArtist = "";
    public static String currentUploader = "";
    public static String currentUrl = "";
    public static String currentImage = "";
    public static String currentSongId = "";

    public static void play(Context context, String url, String title, String artist, String uploader, String image,String songId) {
        Log.d("MediaPlayerManager", "Attempting to play: " + url);
        stop(); // Dừng nếu có bài đang phát

        currentTitle = title;
        currentArtist = artist;
        currentUploader = uploader;
        currentUrl = url;
        currentImage = image;
        currentSongId = songId;

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(mp -> {
                isPrepared = true;
                mediaPlayer.start();
                Log.d("MediaPlayerManager", "Playback started for: " + title);
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MediaPlayerManager", "MediaPlayer error: what=" + what + ", extra=" + extra);
                Toast.makeText(context, "Không thể phát nhạc: " + what, Toast.LENGTH_SHORT).show();
                isPrepared = false;
                return true;
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                Log.d("MediaPlayerManager", "Playback completed");
                isPrepared = false;
            });
            mediaPlayer.prepareAsync();
            Log.d("MediaPlayerManager", "Preparing async for: " + url);
        } catch (IOException e) {
            Log.e("MediaPlayerManager", "IOException in setDataSource: " + e.getMessage());
            Toast.makeText(context, "Lỗi tải tệp âm thanh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            isPrepared = false;
        }
    }

    public static void pause() {
        if (mediaPlayer != null && isPrepared && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Log.d("MediaPlayerManager", "Playback paused");
        } else {
            Log.w("MediaPlayerManager", "Cannot pause: mediaPlayer=" + mediaPlayer + ", isPrepared=" + isPrepared);
        }
    }

    public static void resume() {
        if (mediaPlayer != null && isPrepared && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Log.d("MediaPlayerManager", "Playback resumed");
        } else {
            Log.w("MediaPlayerManager", "Cannot resume: mediaPlayer=" + mediaPlayer + ", isPrepared=" + isPrepared);
        }
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPrepared = false;
            Log.d("MediaPlayerManager", "MediaPlayer stopped and released");
        }
    }

    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static boolean isMediaPrepared() {
        return isPrepared;
    }
}