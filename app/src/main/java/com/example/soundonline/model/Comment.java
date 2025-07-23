package com.example.soundonline.model;

import java.util.List;

public class Comment {
    private int commentId;
    private int soundId;
    private int userId;
    private String username;
    private String commentText;
    private Integer parentCommentId;
    private String createdAt;
    private String updatedAt;
    private List<Comment> childComments;

    public Comment() {
    }

    public Comment(int commentId, int soundId, int userId, String username, String commentText,
                   Integer parentCommentId, String createdAt, String updatedAt, List<Comment> childComments) {
        this.commentId = commentId;
        this.soundId = soundId;
        this.userId = userId;
        this.username = username;
        this.commentText = commentText;
        this.parentCommentId = parentCommentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.childComments = childComments;
    }

    // Getter và Setter
    public int getCommentId() { return commentId; }
    public void setCommentId(int commentId) { this.commentId = commentId; }

    public int getSoundId() { return soundId; }
    public void setSoundId(int soundId) { this.soundId = soundId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public Integer getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Integer parentCommentId) { this.parentCommentId = parentCommentId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public List<Comment> getChildComments() { return childComments; }
    public void setChildComments(List<Comment> childComments) { this.childComments = childComments; }
}
