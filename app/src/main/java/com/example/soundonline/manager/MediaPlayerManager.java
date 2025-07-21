package com.example.soundonline.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class MediaPlayerManager {
    private static MediaPlayer mediaPlayer;
    private static boolean isPrepared = false;
    public static String currentTitle = "";
    public static String currentArtist = "";
    public static String currentUploader = "";
    public static String currentUrl = "";
    public static String currentImage = "";

    public static void play(Context context, String url, String title, String artist, String uploader, String image) {
        stop(); // Dừng nếu có bài đang phát

        currentTitle = title;
        currentArtist = artist;
        currentUploader = uploader;
        currentUrl = url;
        currentImage = image;

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(mp -> {
                isPrepared = true;
                mediaPlayer.start();
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void pause() {
        if (mediaPlayer != null && isPrepared && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public static void resume() {
        if (mediaPlayer != null && isPrepared && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPrepared = false;
        }
    }

    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}