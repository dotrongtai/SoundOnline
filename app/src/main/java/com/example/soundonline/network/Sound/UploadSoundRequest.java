package com.example.soundonline.network.Sound;

public class UploadSoundRequest {
    private String title;
    private String artistName;
    private String fileUrl;
    private String coverImageUrl;
    private Integer userId;
    private Integer albumId;
    private Integer categoryId;
    private int duration;
    private String lyrics;
    private boolean isPublic;
    private boolean isActive;

    // Constructor
    public UploadSoundRequest(String title, String artistName, String fileUrl, String coverImageUrl,
                              Integer userId, Integer albumId, Integer categoryId, int duration,
                              String lyrics, boolean isPublic, boolean isActive) {
        this.title = title;
        this.artistName = artistName;
        this.fileUrl = fileUrl;
        this.coverImageUrl = coverImageUrl;
        this.userId = userId;
        this.albumId = albumId;
        this.categoryId = categoryId;
        this.duration = duration;
        this.lyrics = lyrics;
        this.isPublic = isPublic;
        this.isActive = false; // Default to false, pending approval
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}