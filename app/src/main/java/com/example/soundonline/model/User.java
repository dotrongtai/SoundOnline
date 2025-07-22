package com.example.soundonline.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class User {
    @SerializedName("userId")
    private int userId;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("dateOfBirth")
    private Date dateOfBirth;

    @SerializedName("gender")
    private String gender;

    @SerializedName("country")
    private String country;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("bio")
    private String bio;

    @SerializedName("isActive")
    private Boolean isActive;

    @SerializedName("isAdmin")
    private Boolean isAdmin;

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("updatedAt")
    private Date updatedAt;

    @SerializedName("lastLoginAt")
    private Date lastLoginAt;

    @SerializedName("token")
    private String token;

    // Constructor cho đăng nhập/đăng ký
    public User(int userId, String username, String email, String token, List<String> roles) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.token = token;
        this.isAdmin = roles != null && roles.contains("Admin");
    }

    // Constructor đầy đủ
    public User(int userId, String username, String email, String password, String firstName, String lastName,
                Date dateOfBirth, String gender, String country, String avatarUrl, String bio,
                Boolean isActive, Boolean isAdmin, Date createdAt, Date updatedAt, Date lastLoginAt, String token) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.country = country;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.isActive = isActive;
        this.isAdmin = isAdmin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLoginAt = lastLoginAt;
        this.token = token;
    }

    // Getter và Setter
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public Date getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(Date lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}