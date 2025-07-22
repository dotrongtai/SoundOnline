package com.example.soundonline.presentation.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import com.example.soundonline.R;
import com.example.soundonline.network.LoginRequest;
import com.example.soundonline.network.LoginResponse;
import com.example.soundonline.network.ApiService;
import com.example.soundonline.presentation.main.MainActivity;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class Login extends ComponentActivity {

    @Inject
    ApiService apiService;

    private EditText edtEmail, edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.et_email);
        edtPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(view -> performLogin());
    }

    private void performLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);
        Log.d("Login", "Login request: email=" + email);
        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d("Login", "API URL: " + call.request().url());
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d("Login", "Login successful: userId=" + loginResponse.getUserId() + ", token=" + loginResponse.getToken());
                    saveLoginInfo(loginResponse);
                    Toast.makeText(Login.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (IOException e) {
                        Log.e("Login", "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e("Login", "Login failed: " + response.code() + ", Message: " + response.message() + ", Body: " + errorBody);
                    runOnUiThread(() -> Toast.makeText(Login.this, "Đăng nhập thất bại: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("Login", "API connection error: " + t.getMessage());
                runOnUiThread(() -> Toast.makeText(Login.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void saveLoginInfo(LoginResponse response) {
        SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("jwt_token", response.getToken());
        editor.putInt("user_id", response.getUserId());
        editor.putString("roles", String.join(",", response.getRoles()));
        editor.apply();
        Log.d("Login", "Saved to SharedPreferences: user_id=" + response.getUserId() + ", jwt_token=" + response.getToken() + ", roles=" + String.join(",", response.getRoles()));
    }
}