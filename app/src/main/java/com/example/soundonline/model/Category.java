package com.example.soundonline.model;

import java.util.Date;
import java.util.List;

public class Category {
    private int categoryId;
    private String categoryName;
    private String description;
    private String imageUrl;
    private Boolean isActive;
    private Date createdAt;
    private List<Sound> sounds;

    public Category() {
    }

    public Category(int categoryId, String categoryName, String description, String imageUrl, Boolean isActive, Date createdAt, List<Sound> sounds) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.sounds = sounds;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<Sound> getSounds() {
        return sounds;
    }

    public void setSounds(List<Sound> sounds) {
        this.sounds = sounds;
    }
}
