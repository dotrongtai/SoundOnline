package com.example.soundonline.model;

public class Following {
    private int userId;
    private String username;
    private String avatarurl;
    private String bio;
    private boolean isFollowing = true; // <-- THÊM DÒNG NÀY

    public Following(int userId, String username, String avatarurl, String bio, boolean isFollowing) {
        this.userId = userId;
        this.username = username;
        this.avatarurl = avatarurl;
        this.bio = bio;
        this.isFollowing = isFollowing;
    }

    public Following() {}

    // Getters và Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarurl() {
        return avatarurl;
    }

    public void setAvatarurl(String avatarurl) {
        this.avatarurl = avatarurl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }
}
