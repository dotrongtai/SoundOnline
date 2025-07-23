package com.example.soundonline.presentation.playlist;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.CommentAdapter;
import com.example.soundonline.R;
import com.example.soundonline.model.Comment;
import com.example.soundonline.network.ApiService;
import com.example.soundonline.network.Comment.CreateCommentResponse;
import com.example.soundonline.network.Comment.SoundCommentsResponse;
import com.example.soundonline.network.Comment.CreateCommentRequest;
import java.util.List;
import android.content.Intent;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class commentActivity extends ComponentActivity {
    private RecyclerView rvComment;
    private CommentAdapter commentAdapter;
    private EditText etComment;
    private ImageButton btnSend;
    private int userId;
    @Inject
    ApiService apiService;

    @SuppressLint("WrongViewCast")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Intent intent = getIntent();
        int soundId = intent.getIntExtra("soundId", -1);
        Log.d("CommentActivity", "Sound ID nhận được: " + soundId);
        userId = getUserIdFromPreferences();
        rvComment = findViewById(R.id.rvComments);
        rvComment.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        fetchComment(soundId);
        etComment = findViewById(R.id.etComment);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> {
            String commentText = etComment.getText().toString().trim();
            if (!commentText.isEmpty()) {
                CreateCommentRequest request = new CreateCommentRequest(soundId, userId, commentText, null);
                apiService.createComment(request).enqueue(new Callback<CreateCommentResponse>() {
                    @Override
                    public void onResponse(Call<CreateCommentResponse> call, Response<CreateCommentResponse> response) {
                        if (response.isSuccessful()) {
                            etComment.setText("");
                            fetchComment(soundId);
                        } else {
                            Log.e("CommentActivity", "Failed to post comment: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateCommentResponse> call, Throwable t) {
                        Log.e("CommentActivity", "Error posting comment", t);
                    }
                });
            }
        });

    }

    private void fetchComment(int soundId) {
        apiService.getSoundComments(soundId).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    commentAdapter = new CommentAdapter(commentActivity.this, response.body());
                    rvComment.setAdapter(commentAdapter);
                } else {
                    Log.e("CommentActivity", "Lỗi khi lấy comment: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e("CommentActivity", "Lỗi kết nối API", t);
            }
        });
    }
    private int getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }
}

