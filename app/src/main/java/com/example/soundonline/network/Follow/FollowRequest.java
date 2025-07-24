package com.example.soundonline.network.Follow;

public class FollowRequest {
    private int followingId;

    public FollowRequest(int followingId) {
        this.followingId = followingId;
    }

    public int getFollowingId() {
        return followingId;
    }

    public void setFollowingId(int followingId) {
        this.followingId = followingId;
    }
}



