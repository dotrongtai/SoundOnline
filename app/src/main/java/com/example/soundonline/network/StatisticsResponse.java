package com.example.soundonline.network;

public class StatisticsResponse {
    private int categoryCount;
    private int soundCount;
    private int userCount;
    private int commentCount;

    public int getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(int categoryCount) {
        this.categoryCount = categoryCount;
    }

    public int getSoundCount() {
        return soundCount;
    }

    public void setSoundCount(int soundCount) {
        this.soundCount = soundCount;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}