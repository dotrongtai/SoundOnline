package com.example.soundonline.presentation.library;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.soundonline.R;
import com.example.soundonline.model.User;
import com.example.soundonline.network.ApiService;
import com.example.soundonline.presentation.auth.Login;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ProfileActivity extends AppCompatActivity {

    @Inject
    ApiService apiService;

    private TextView tvUsername, tvEmail, tvFirstName, tvLastName, tvDateOfBirth, tvGender, tvCountry, tvBio;
    private ImageView ivAvatar;
    private Button btnEditProfile, btnChangePassword, btnBack;
    private int userId;
    private static final int EDIT_PROFILE_REQUEST = 1; // Mã request cho EditProfileActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        tvGender = findViewById(R.id.tvGender);
        tvCountry = findViewById(R.id.tvCountry);
        tvBio = findViewById(R.id.tvBio);
        ivAvatar = findViewById(R.id.ivAvatar);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnBack = findViewById(R.id.btnBack);

        // Lấy userId và token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        String token = prefs.getString("jwt_token", "");
        Log.d("ProfileActivity", "userId: " + userId + ", token: " + (token.isEmpty() ? "empty" : "present (" + token.length() + " chars)"));

        // Kiểm tra userId và token
        if (userId == -1) {
            Log.e("ProfileActivity", "Invalid userId: " + userId);
            Toast.makeText(this, "Không tìm thấy userId. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }
        if (token.isEmpty()) {
            Log.e("ProfileActivity", "No token found");
            Toast.makeText(this, "Không tìm thấy token. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        // Xử lý sự kiện nút
        btnBack.setOnClickListener(v -> finish());
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            intent.putExtra("user_id", userId);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST);
        });
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        // Gọi API
        fetchUserProfile();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == RESULT_OK) {
            Log.d("ProfileActivity", "EditProfileActivity returned RESULT_OK, refreshing profile");
            fetchUserProfile();
        }
    }

    private void fetchUserProfile() {
        Call<User> call = apiService.getUser(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("ProfileActivity", "API URL: " + call.request().url());
                Log.d("ProfileActivity", "Request Headers: " + call.request().headers());
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    Log.d("ProfileActivity", "User fetched (userId=" + userId + "): " + user.getUsername());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = user.getDateOfBirth() != null ? dateFormat.format(user.getDateOfBirth()) : "";
                    runOnUiThread(() -> {
                        tvUsername.setText(user.getUsername() != null ? user.getUsername() : "");
                        tvEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                        tvFirstName.setText(user.getFirstName() != null ? user.getFirstName() : "");
                        tvLastName.setText(user.getLastName() != null ? user.getLastName() : "");
                        tvDateOfBirth.setText(formattedDate);
                        tvGender.setText(user.getGender() != null ? user.getGender() : "");
                        tvCountry.setText(user.getCountry() != null ? user.getCountry() : "");
                        tvBio.setText(user.getBio() != null ? user.getBio() : "");
                        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty() && !user.getAvatarUrl().startsWith("blob:")) {
                            Glide.with(ProfileActivity.this)
                                    .load(user.getAvatarUrl())
                                    .error(R.drawable.img)
                                    .placeholder(R.drawable.img)
                                    .into(ivAvatar);
                        } else {
                            ivAvatar.setImageResource(R.drawable.img);
                            Log.w("ProfileActivity", "Invalid or null avatarUrl: " + user.getAvatarUrl());
                        }
                    });
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (IOException e) {
                        Log.e("ProfileActivity", "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e("ProfileActivity", "Error (userId=" + userId + "): " + response.code() + ", Message: " + response.message() + ", Body: " + errorBody);
                    runOnUiThread(() -> {
                        if (response.code() == 401) {
                            Toast.makeText(ProfileActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ProfileActivity.this, Login.class));
                            finish();
                        } else if (response.code() == 404) {
                            Toast.makeText(ProfileActivity.this, "Không tìm thấy người dùng với ID: " + userId, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Lỗi tải thông tin người dùng: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ProfileActivity", "API connection error (userId=" + userId + "): " + t.getMessage());
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}