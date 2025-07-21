package com.example.soundonline.model;

// domain/model/Track.java
public class Track {
    private int id;
    private String title;
    private String artistName;
    private int duration;
    private String fileUrl;
    private String coverImageUrl;

    public Track(int id, String title, String artistName, int duration, String fileUrl, String coverImageUrl) {
        this.id = id;
        this.title = title;
        this.artistName = artistName;
        this.duration = duration;
        this.fileUrl = fileUrl;
        this.coverImageUrl = coverImageUrl;
    }

    // Getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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
}
