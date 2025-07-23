package com.example.soundonline.network.Sound;

import com.google.gson.annotations.SerializedName;

public class UpdateSoundRequest {
    @SerializedName("soundName")
    private String soundName;

    @SerializedName("artistName")
    private String artistName;

    @SerializedName("categoryId")
    private Integer categoryId;

    @SerializedName("albumId")
    private Integer albumId;

    @SerializedName("duration")
    private Integer duration;

    @SerializedName("fileUrl")
    private String fileUrl;

    @SerializedName("coverImageUrl")
    private String coverImageUrl;

    @SerializedName("lyrics")
    private String lyrics;

    @SerializedName("playCount")
    private Integer playCount;

    @SerializedName("likeCount")
    private Integer likeCount;

    @SerializedName("isPublic")
    private Boolean isPublic;

    @SerializedName("isActive")
    private Boolean isActive;

    @SerializedName("uploadedBy")
    private Integer uploadedBy;

    // Constructor mặc định
    public UpdateSoundRequest() {
    }

    // Getters và Setters
    public String getSoundName() { return soundName; }
    public void setSoundName(String soundName) { this.soundName = soundName; }
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Integer getAlbumId() { return albumId; }
    public void setAlbumId(Integer albumId) { this.albumId = albumId; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
    public String getLyrics() { return lyrics; }
    public void setLyrics(String lyrics) { this.lyrics = lyrics; }
    public Integer getPlayCount() { return playCount; }
    public void setPlayCount(Integer playCount) { this.playCount = playCount; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Integer getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(Integer uploadedBy) { this.uploadedBy = uploadedBy; }
}