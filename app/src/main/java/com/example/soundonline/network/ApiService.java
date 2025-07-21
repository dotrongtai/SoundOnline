package com.example.soundonline.network;

import com.example.soundonline.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<LoginResponse> register(@Body RegisterRequest registerRequest);

    @GET("users/{id}")
    Call<User> getUserProfile(@Path("id") int id);

    @PUT("users/{id}")
    Call<User> updateUserProfile(@Path("id") int id, @Body UpdateProfileRequest updateProfileRequest);

    @GET("users/{id}/likes")
    Call<List<Liked>> getUserLikedTracks(@Path("id") int userId);

    @GET("playlists/user/{userId}")
    Call<List<Playlist>> getUserPlaylists(@Path("userId") int userId);

    // ✅ NEW: Get trending categories
    @GET("categories/trending")
    Call<List<Category>> getTrendingCategories();
}
