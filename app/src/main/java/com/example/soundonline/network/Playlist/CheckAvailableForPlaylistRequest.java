package com.example.soundonline.network.Playlist;

public class CheckAvailableForPlaylistRequest {
    int playlistId;
    int userId;
    public CheckAvailableForPlaylistRequest(int playlistId, int userId) {
        this.playlistId = playlistId;
        this.userId = userId;
    }
}
