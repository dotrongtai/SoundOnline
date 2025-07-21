package com.example.soundonline.model;

import java.util.List;

public class Album {
    private int albumId;
    private String albumTitle;
    private String artistName;
    private String coverImageUrl;
    private String releaseDate;
    private String genre;
    private Integer totalTracks;
    private Integer duration; // in seconds (or minutes depending on API)
    private String createdAt;

    private List<Sound> sounds; // assuming you have a Sound model

    // Constructors
    public Album() {
    }

    public Album(int albumId, String albumTitle, String artistName, String coverImageUrl,
                 String releaseDate, String genre, Integer totalTracks, Integer duration,
                 String createdAt, List<Sound> sounds) {
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.artistName = artistName;
        this.coverImageUrl = coverImageUrl;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.totalTracks = totalTracks;
        this.duration = duration;
        this.createdAt = createdAt;
        this.sounds = sounds;
    }

    // Getters and Setters
    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getTotalTracks() {
        return totalTracks;
    }

    public void setTotalTracks(Integer totalTracks) {
        this.totalTracks = totalTracks;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Sound> getSounds() {
        return sounds;
    }

    public void setSounds(List<Sound> sounds) {
        this.sounds = sounds;
    }
}
