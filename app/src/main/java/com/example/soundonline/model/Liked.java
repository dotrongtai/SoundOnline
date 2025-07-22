package com.example.soundonline.model;

public class Liked {
    private int likeId;
    private int userId;
    private Sound sound;
    private String likedAt;

    public Liked(int userId, Sound sound) {
        this.userId = userId;
        this.sound = sound;
    }

    public int getLikeId() {
        return likeId;
    }

    public void setLikeId(int likeId) {
        this.likeId = likeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public String getLikedAt() {
        return likedAt;
    }

    public void setLikedAt(String likedAt) {
        this.likedAt = likedAt;
    }
}
