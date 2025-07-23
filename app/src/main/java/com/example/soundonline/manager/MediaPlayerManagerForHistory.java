package com.example.soundonline.manager;



import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.example.soundonline.model.Sound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MediaPlayerManagerForHistory {
    public static MediaPlayer mediaPlayer;
    public static boolean isPrepared = false;
    public static String currentTitle = "";
    public static String currentArtist = "";
    public static String currentUploader = "";
    public static String currentUrl = "";
    public static String currentImage = "";
    public static int currentSongId;

    private static MediaPlayer.OnCompletionListener onCompletionListener;
    private static List<Sound> playlist = new ArrayList<>();
    private static int currentIndex = 0;
    private static OnTrackChangedListener trackChangedListener;

    public static void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        onCompletionListener = listener;
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(mp -> {
                isPrepared = false;
                if (onCompletionListener != null) onCompletionListener.onCompletion(mp);
            });
        }
    }

    public static void play(Context context, String url, String title, String artist, String uploader, String image, int songId) {
        stop();

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
                mp.start();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                isPrepared = false;
                if (onCompletionListener != null) onCompletionListener.onCompletion(mp);
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                isPrepared = false;
                Toast.makeText(context, "Lỗi phát nhạc", Toast.LENGTH_SHORT).show();
                return true;
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            isPrepared = false;
            Toast.makeText(context, "Lỗi tải bài hát", Toast.LENGTH_SHORT).show();
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

    public static boolean isMediaPrepared() {
        return isPrepared;
    }

    public static void playPlaylistSequentially(Context context, List<Sound> items) {
        if (items == null || items.isEmpty()) return;
        playlist = items;
        currentIndex = 0;
        playCurrentInPlaylist(context);
    }

    private static void playCurrentInPlaylist(Context context) {
        if (currentIndex >= playlist.size()) {
            stop();
            return;
        }

        Sound item = playlist.get(currentIndex);
        play(context, item.getFileUrl(), item.getTitle(), item.getArtistName(), item.getUploaderName(), item.getCoverImageUrl(), item.getSoundId());

        if (trackChangedListener != null) {
            trackChangedListener.onTrackChanged(currentIndex);
        }

        setOnCompletionListener(mp -> {
            currentIndex++;
            playCurrentInPlaylist(context);
        });
    }

    public interface OnTrackChangedListener {
        void onTrackChanged(int index);
    }

    public static void setOnTrackChangedListener(OnTrackChangedListener listener) {
        trackChangedListener = listener;
    }



}
