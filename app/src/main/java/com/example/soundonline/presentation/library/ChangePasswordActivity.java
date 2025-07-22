package com.example.soundonline.presentation.library;

import android.content.Context;
import android.content.Intent;
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
import com.example.soundonline.network.ApiService;
import com.example.soundonline.presentation.auth.Login;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ChangePasswordActivity extends AppCompatActivity {

    @Inject
    ApiService apiService;

    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnSave, btnBack;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        // Lấy userId từ Intent hoặc SharedPreferences
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
            userId = prefs.getInt("user_id", -1);
        }
        Log.d("ChangePasswordActivity", "userId: " + userId);

        // Kiểm tra userId và token
        SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt_token", "");
        Log.d("ChangePasswordActivity", "token: " + (token.isEmpty() ? "empty" : "present (" + token.length() + " chars)"));
        if (userId == -1 || token.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin đăng nhập. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        // Xử lý sự kiện nút
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (oldPassword.isEmpty()) {
            etOldPassword.setError("Vui lòng nhập mật khẩu cũ");
            etOldPassword.requestFocus();
            return;
        }
        if (newPassword.isEmpty()) {
            etNewPassword.setError("Vui lòng nhập mật khẩu mới");
            etNewPassword.requestFocus();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            etConfirmPassword.requestFocus();
            return;
        }

        // Tạo request body
        Map<String, String> request = new HashMap<>();
        request.put("oldPassword", oldPassword);
        request.put("newPassword", newPassword);
        request.put("confirmPassword", confirmPassword);
        Log.d("ChangePasswordActivity", "Request body: " + request.toString());

        // Gọi API
        Call<Map<String, Object>> call = apiService.changePassword(request);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Log.d("ChangePasswordActivity", "API URL: " + call.request().url());
                Log.d("ChangePasswordActivity", "Request Headers: " + call.request().headers());
                if (response.isSuccessful() && response.body() != null) {
                    String message = (String) response.body().get("message");
                    runOnUiThread(() -> {
                        Toast.makeText(ChangePasswordActivity.this, message != null ? message : "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (IOException e) {
                        Log.e("ChangePasswordActivity", "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e("ChangePasswordActivity", "Error: " + response.code() + ", Message: " + response.message() + ", Body: " + errorBody);
                    runOnUiThread(() -> {
                        if (response.code() == 401) {
                            Toast.makeText(ChangePasswordActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ChangePasswordActivity.this, Login.class));
                            finish();
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Lỗi đổi mật khẩu: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("ChangePasswordActivity", "API connection error: " + t.getMessage());
                runOnUiThread(() -> Toast.makeText(ChangePasswordActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}