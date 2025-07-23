package com.example.soundonline.network.Sound;


import com.google.gson.annotations.SerializedName;


import java.io.Serializable;
public class SoundAdminResponse implements Serializable {
    @SerializedName("soundId")
    private int soundId;

    @SerializedName("title")
    private String title;

    @SerializedName("artistName")
    private String artistName;

    @SerializedName("albumId")
    private Integer albumId;

    @SerializedName("categoryId")
    private Integer categoryId;

    @SerializedName("duration")
    private int duration;

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

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("uploaderName")
    private String uploaderName;

    @SerializedName("album")
    private Object album; // Có thể là null hoặc cần triển khai Serializable nếu là đối tượng phức hợp

    @SerializedName("category")
    private Object category; // Có thể là null hoặc cần triển khai Serializable nếu là đối tượng phức hợp

    // Constructor mặc định
    public SoundAdminResponse() {
    }

    // Getters và Setters
    public int getSoundId() { return soundId; }
    public void setSoundId(int soundId) { this.soundId = soundId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public Integer getAlbumId() { return albumId; }
    public void setAlbumId(Integer albumId) { this.albumId = albumId; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
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
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUploaderName() { return uploaderName; }
    public void setUploaderName(String uploaderName) { this.uploaderName = uploaderName; }
    public Object getAlbum() { return album; }
    public void setAlbum(Object album) { this.album = album; }
    public Object getCategory() { return category; }
    public void setCategory(Object category) { this.category = category; }
}