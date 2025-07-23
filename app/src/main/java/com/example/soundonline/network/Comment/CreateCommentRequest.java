package com.example.soundonline.network.Comment;

public class CreateCommentRequest {
    private int soundId;
    private int userId;
    private String commentText;
    private Integer parentCommentId; // Use Integer to allow null

    public CreateCommentRequest(int soundId, int userId, String commentText, Integer parentCommentId) {
        this.soundId = soundId;
        this.userId = userId;
        this.commentText = commentText;
        this.parentCommentId = parentCommentId;
    }

    // Getters and setters (optional, if needed)
}