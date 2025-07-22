package com.example.soundonline.network;

import com.example.soundonline.model.*;
import com.example.soundonline.network.Admin.*;
import com.example.soundonline.network.Album.*;
import com.example.soundonline.network.Auth.*;
import com.example.soundonline.network.Category.*;
import com.example.soundonline.network.Comment.*;
import com.example.soundonline.network.Follow.*;
import com.example.soundonline.network.History.*;
import com.example.soundonline.network.Home.*;
import com.example.soundonline.network.Library.*;
import com.example.soundonline.network.Like.*;
import com.example.soundonline.network.Playlist.*;
import com.example.soundonline.network.Search.*;
import com.example.soundonline.network.Sound.*;
import com.example.soundonline.network.User.*;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<LoginResponse> register(@Body RegisterRequest registerRequest);

    @GET("Users/{id}") // Sửa thành uppercase /Users để khớp với Postman
    Call<User> getUser(@Path("id") int userId);

    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") int userId, @Body User user);

    @POST("users/change-password")
    Call<Map<String, Object>> changePassword(@Body Map<String, String> request);

    @GET("users/{id}/likes")
    Call<List<Liked>> getUserLikedTracks(@Path("id") int userId);

    @GET("playlists/user/{userId}")
    Call<List<Playlist>> getUserPlaylists(@Path("userId") int userId);

    // ✅ NEW: Get trending categories
    @GET("categories/trending")
    Call<List<Category>> getTrendingCategories();

    // Auth Endpoints


    @GET("Auth/google")
    Call<GoogleAuthResponse> googleAuth();

    // Admin Endpoints
    @GET("admin/users")
    Call<UsersResponse> getUsers();

    @PUT("admin/users/{id}/status")
    Call<UserStatusResponse> updateUserStatus(@Path("id") int id, @Body UpdateUserStatusRequest request);

    @PUT("admin/sounds/{id}/moderation")
    Call<ModerateSoundResponse> moderateSound(@Path("id") int id, @Body ModerateSoundRequest request);

    // Album Endpoints
    @POST("Albums")
    Call<CreateAlbumResponse> createAlbum(@Body CreateAlbumRequest request);

    @GET("Albums")
    Call<List<Album>> getAlbums();

    @GET("Albums/{albumId}")
    Call<AlbumResponse> getAlbum(@Path("albumId") int albumId);

    @PUT("Albums/{albumId}")
    Call<UpdateAlbumResponse> updateAlbum(@Path("albumId") int albumId, @Body UpdateAlbumRequest request);

    @DELETE("Albums/{albumId}")
    Call<Void> deleteAlbum(@Path("albumId") int albumId);

    // Category Endpoints
    @GET("categories")
    Call<CategoriesResponse> getCategories();


    @GET("categories/{id}/sounds")
    Call<CategorySoundsResponse> getCategorySounds(@Path("id") int id);

    // Comment Endpoints
    @POST("Comments")
    Call<CreateCommentResponse> createComment(@Body CreateCommentRequest request);

    @GET("Comments/sound/{soundId}")
    Call<SoundCommentsResponse> getSoundComments(@Path("soundId") int soundId);

    @PUT("Comments/{commentId}")
    Call<UpdateCommentResponse> updateComment(@Path("commentId") int commentId, @Body UpdateCommentRequest request);

    @DELETE("Comments/{commentId}")
    Call<Void> deleteComment(@Path("commentId") int commentId);

    // Follow Endpoints
    @POST("follows")
    Call<FollowResponse> follow(@Body FollowRequest request);

    // History Endpoints
    @POST("history")
    Call<AddHistoryResponse> addHistory(@Body AddHistoryRequest request);

    // Home Endpoint
    @GET("home")
    Call<HomeResponse> getHome();

    // Library Endpoint
    @GET("library/my-tracks")
    Call<MyTracksResponse> getMyTracks();

    // Like Endpoint
    @POST("likes")
    Call<LikeResponse> like(@Body LikeRequest request);

    // Playlist Endpoints
    @POST("Playlists")
    Call<CreatePlaylistResponse> createPlaylist(@Body CreatePlaylistRequest request);

    @POST("Playlists/{playlistId}/tracks")
    Call<AddTrackResponse> addTrackToPlaylist(@Path("playlistId") String playlistId, @Body AddTrackToPlaylistRequest request);

    @DELETE("Playlists/{playlistId}/tracks")
    Call<Void> removeTrackFromPlaylist(@Path("playlistId") String playlistId, @Body RemoveTrackFromPlaylistRequest request);

    @DELETE("Playlists/{playlistId}")
    Call<Void> deletePlaylist(@Path("playlistId") int playlistId);


    // Search Endpoint
    @GET("search")
    Call<SearchResponse> search(@Query("query") String query, @Query("type") String type);

    // Sound Endpoints
//    @Multipart
//    @POST("Sounds")
//    Call<UploadSoundResponse> uploadSound(
//            @Part("Title") String title,
//            @Part("ArtistName") String artistName,
//            @Part("AlbumId") int albumId,
//            @Part("CategoryId") int categoryId,
//            @Part("Duration") int duration,
//            @Part("File") RequestBody file,
//            @Part("CoverImageUrl") String coverImageUrl,
//            @Part("Lyrics") String lyrics,
//            @Part("UploadedBy") int uploadedBy,
//            @Part("IsPublic") boolean isPublic
//    );

    @GET("sounds/{id}")
    Call<SoundResponse> getSound(@Path("id") int id);

    @POST("sounds/{id}/play")
    Call<PlaySoundResponse> playSound(@Path("id") int id);

    // User Endpoints
    @GET("users/{id}/followers")
    Call<FollowersResponse> getFollowers(@Path("id") int id);

    @GET("users/{id}/following")
    Call<FollowingResponse> getFollowing(@Path("id") int id);

    @GET("users/{id}/history")
    Call<UserHistoryResponse> getUserHistory(@Path("id") int id);


}