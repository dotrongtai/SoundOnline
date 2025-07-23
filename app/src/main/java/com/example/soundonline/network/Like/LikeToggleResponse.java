package com.example.soundonline.network.Like;

public class LikeToggleResponse {
    private boolean isLiked;
    private String message;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
