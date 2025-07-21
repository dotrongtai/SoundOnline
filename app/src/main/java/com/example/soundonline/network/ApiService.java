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

    // Auth
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<LoginResponse> register(@Body RegisterRequest registerRequest);

    @GET("Auth/google")
    Call<GoogleAuthResponse> googleAuth();

    // User
    @GET("users/{id}")
    Call<User> getUserProfile(@Path("id") int id);

    @PUT("users/{id}")
    Call<User> updateUserProfile(@Path("id") int id, @Body UpdateProfileRequest updateProfileRequest);

    @GET("users/{id}/likes")
    Call<List<Liked>> getUserLikedTracks(@Path("id") int userId);

    @GET("users/{id}/followers")
    Call<FollowersResponse> getFollowers(@Path("id") int id);

    @GET("users/{id}/following")
    Call<FollowingResponse> getFollowing(@Path("id") int id);

    @GET("users/{id}/history")
    Call<UserHistoryResponse> getUserHistory(@Path("id") int id);

    // Admin
    @GET("admin/users")
    Call<UsersResponse> getUsers();

    @PUT("admin/users/{id}/status")
    Call<UserStatusResponse> updateUserStatus(@Path("id") int id, @Body UpdateUserStatusRequest request);

    @PUT("admin/sounds/{id}/moderation")
    Call<ModerateSoundResponse> moderateSound(@Path("id") int id, @Body ModerateSoundRequest request);

    // Album
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

    // Category
    @GET("categories")
    Call<CategoriesResponse> getCategories();

    @GET("categories/trending")
    Call<List<Category>> getTrendingCategories();

    @GET("categories/{id}/sounds")
    Call<CategorySoundsResponse> getCategorySounds(@Path("id") int id);

    // Comment
    @POST("Comments")
    Call<CreateCommentResponse> createComment(@Body CreateCommentRequest request);

    @GET("Comments/sound/{soundId}")
    Call<SoundCommentsResponse> getSoundComments(@Path("soundId") int soundId);

    @PUT("Comments/{commentId}")
    Call<UpdateCommentResponse> updateComment(@Path("commentId") int commentId, @Body UpdateCommentRequest request);

    @DELETE("Comments/{commentId}")
    Call<Void> deleteComment(@Path("commentId") int commentId);

    // Follow
    @POST("follows")
    Call<FollowResponse> follow(@Body FollowRequest request);

    // History
    @POST("history")
    Call<AddHistoryResponse> addHistory(@Body AddHistoryRequest request);

    // Home
    @GET("home")
    Call<HomeResponse> getHome();

    // Library
    @GET("library/my-tracks")
    Call<MyTracksResponse> getMyTracks();

    // Like
    @POST("likes")
    Call<LikeResponse> like(@Body LikeRequest request);

    // Playlist
    @POST("Playlists")
    Call<CreatePlaylistResponse> createPlaylist(@Body CreatePlaylistRequest request);

    @POST("Playlists/{playlistId}/tracks")
    Call<AddTrackResponse> addTrackToPlaylist(@Path("playlistId") String playlistId, @Body AddTrackToPlaylistRequest request);

    @DELETE("Playlists/{playlistId}/tracks")
    Call<Void> removeTrackFromPlaylist(@Path("playlistId") String playlistId, @Body RemoveTrackFromPlaylistRequest request);

    @DELETE("Playlists/{playlistId}")
    Call<Void> deletePlaylist(@Path("playlistId") int playlistId);

    @GET("playlists/user/{userId}")
    Call<List<Playlist>> getUserPlaylists(@Path("userId") int userId);

    // Search
    @GET("search")
    Call<SearchResponse> search(@Query("query") String query, @Query("type") String type);

    // Sound
    @GET("sounds/{id}")
    Call<SoundResponse> getSound(@Path("id") int id);

    @POST("sounds/{id}/play")
    Call<PlaySoundResponse> playSound(@Path("id") int id);

    // Uncomment to implement upload if needed in the future
    // @Multipart
    // @POST("sounds")
    // Call<UploadSoundResponse> uploadSound(...);
}
