// File: app/src/main/java/com/example/soundonline/network/Playlist/CreatePlaylistRequest.java
package com.example.soundonline.network.Playlist;

public class CreatePlaylistRequest {
    private int userId;
    private String playlistName;
    private String description;
    private String coverImageUrl;
    private boolean isPublic;

    public CreatePlaylistRequest(int userId, String playlistName, String description, String coverImageUrl, boolean isPublic) {
        this.userId = userId;
        this.playlistName = playlistName;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.isPublic = isPublic;
    }

    // Getter và Setter cho tất cả các trường
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getPlaylistName() { return playlistName; }
    public void setPlaylistName(String playlistName) { this.playlistName = playlistName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public boolean isPublic() { return isPublic; }
    public void setIsPublic(boolean isPublic) { this.isPublic = isPublic; }
}