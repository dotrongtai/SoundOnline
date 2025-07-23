package com.example.soundonline.presentation.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.soundonline.R;
import com.example.soundonline.model.User;
import com.example.soundonline.network.ApiService;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class EditProfileActivity extends AppCompatActivity {

    @Inject
    ApiService apiService;

    private EditText etUsername, etEmail, etFirstName, etLastName, etDateOfBirth, etGender, etCountry, etBio;
    private Button btnSave, btnCancel;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etGender = findViewById(R.id.etGender);
        etCountry = findViewById(R.id.etCountry);
        etBio = findViewById(R.id.etBio);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Lấy userId từ Intent
        userId = getIntent().getIntExtra("user_id", -1);
        Log.d("EditProfileActivity", "userId: " + userId);

        if (userId == -1) {
            Log.e("EditProfileActivity", "Invalid userId: " + userId);
            Toast.makeText(this, "Không tìm thấy userId.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Xử lý sự kiện nút
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveProfile());

        // Lấy thông tin hồ sơ hiện tại
        fetchUserProfile();
    }

    private void fetchUserProfile() {
        Call<User> call = apiService.getUser(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("EditProfileActivity", "API URL: " + call.request().url());
                Log.d("EditProfileActivity", "Request Headers: " + call.request().headers());
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    Log.d("EditProfileActivity", "User fetched (userId=" + userId + "): " + user.getUsername());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = user.getDateOfBirth() != null ? dateFormat.format(user.getDateOfBirth()) : "";
                    runOnUiThread(() -> {
                        etUsername.setText(user.getUsername() != null ? user.getUsername() : "");
                        etEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                        etFirstName.setText(user.getFirstName() != null ? user.getFirstName() : "");
                        etLastName.setText(user.getLastName() != null ? user.getLastName() : "");
                        etDateOfBirth.setText(formattedDate);
                        etGender.setText(user.getGender() != null ? user.getGender() : "");
                        etCountry.setText(user.getCountry() != null ? user.getCountry() : "");
                        etBio.setText(user.getBio() != null ? user.getBio() : "");
                    });
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (IOException e) {
                        Log.e("EditProfileActivity", "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e("EditProfileActivity", "Error (userId=" + userId + "): " + response.code() + ", Message: " + response.message() + ", Body: " + errorBody);
                    runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, "Lỗi tải thông tin người dùng: " + response.code(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("EditProfileActivity", "API connection error (userId=" + userId + "): " + t.getMessage());
                runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void saveProfile() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String dob = etDateOfBirth.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String bio = etBio.getText().toString().trim();

        Date dateOfBirth = null;
        if (!dob.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                dateOfBirth = dateFormat.parse(dob);
            } catch (ParseException e) {
                Log.e("EditProfileActivity", "Invalid date format: " + e.getMessage());
                Toast.makeText(this, "Định dạng ngày sinh không hợp lệ (yyyy-MM-dd).", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Tạo đối tượng User với constructor đầy đủ
        User user = new User(
                userId,           // userId
                username,         // username
                email,            // email
                firstName,        // firstName
                lastName,         // lastName
                null,             // password (không thay đổi trong edit profile)
                dateOfBirth,      // dateOfBirth
                gender,           // gender
                country,          // country
                null,             // avatarUrl (không hỗ trợ upload ảnh trong giao diện này)
                bio,              // bio
                true,             // isActive (giả định mặc định là true)
                false,            // isAdmin (giả định mặc định là false)
                null,             // createdAt (không cần gửi)
                null,             // updatedAt (không cần gửi)
                null,             // lastLogin (không cần gửi)
                "User"            // role (giả định mặc định là "User")
        );

        Call<User> call = apiService.updateUser(userId, user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("EditProfileActivity", "Update API URL: " + call.request().url());
                Log.d("EditProfileActivity", "Update Request Headers: " + call.request().headers());
                if (response.isSuccessful()) {
                    Log.d("EditProfileActivity", "Profile updated successfully (userId=" + userId + ")");
                    runOnUiThread(() -> {
                        Toast.makeText(EditProfileActivity.this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // Gửi kết quả về ProfileActivity
                        finish();
                    });
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (IOException e) {
                        Log.e("EditProfileActivity", "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e("EditProfileActivity", "Update error (userId=" + userId + "): " + response.code() + ", Message: " + response.message() + ", Body: " + errorBody);
                    runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, "Lỗi cập nhật hồ sơ: " + response.code(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("EditProfileActivity", "Update API connection error (userId=" + userId + "): " + t.getMessage());
                runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}