package com.example.soundonline.network.Playlist;

public class AddSoundToPlaylistRequest {
    private int playlistTrackId;
    private int playlistId;
    private int soundId;
    private int triggerOn;
    private String addedAt;
    private String message;

    public AddSoundToPlaylistRequest(int playlistId, int soundId) {
        this.playlistId = playlistId;
        this.soundId = soundId;
    }

    // Getter + Setter (có thể generate bằng IDE)
}

