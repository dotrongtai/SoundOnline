package com.example.soundonline.presentation.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soundonline.R;
import com.example.soundonline.network.ApiService;
import com.example.soundonline.network.LoginResponse;
import com.example.soundonline.network.RegisterRequest;

import java.util.regex.Pattern;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint // THÊM DÒNG NÀY
public class Register extends AppCompatActivity {

    private EditText edtUsername, edtEmail, edtPassword, edtFirstName, edtLastName;
    private Button btnRegister;

    @Inject
    ApiService apiService;

    private static final Pattern NO_SPECIAL_CHARACTERS = Pattern.compile("^[a-zA-Z0-9]+$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.editUsername);
        edtEmail = findViewById(R.id.editEmail);
        edtPassword = findViewById(R.id.editPassword);
        edtFirstName = findViewById(R.id.editFirstName);
        edtLastName = findViewById(R.id.editLastName);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            if (validateInput()) {
                RegisterRequest request = new RegisterRequest(
                        edtUsername.getText().toString().trim(),
                        edtEmail.getText().toString().trim(),
                        edtPassword.getText().toString(),
                        edtFirstName.getText().toString().trim(),
                        edtLastName.getText().toString().trim()
                );

                apiService.register(request).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(Register.this, "Bạn đã đăng ký tài khoản thành công", Toast.LENGTH_LONG).show();
                            finish(); // Quay lại LoginActivity
                        } else {
                            Toast.makeText(Register.this, "Đăng ký thất bại", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(Register.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private boolean validateInput() {
        String username = edtUsername.getText().toString();
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();

        if (TextUtils.isEmpty(username.trim())) {
            edtUsername.setError("Vui lòng nhập tên đăng nhập");
            return false;
        }
        if (username.contains(" ")) {
            edtUsername.setError("Tên đăng nhập không được chứa dấu cách");
            return false;
        }
        if (!NO_SPECIAL_CHARACTERS.matcher(username).matches()) {
            edtUsername.setError("Tên đăng nhập chỉ được chứa chữ và số");
            return false;
        }

        if (TextUtils.isEmpty(email.trim())) {
            edtEmail.setError("Vui lòng nhập email");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        }
        if (password.contains(" ")) {
            edtPassword.setError("Mật khẩu không được chứa dấu cách");
            return false;
        }
        if (!NO_SPECIAL_CHARACTERS.matcher(password).matches()) {
            edtPassword.setError("Mật khẩu chỉ được chứa chữ và số");
            return false;
        }
        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu phải từ 6 ký tự");
            return false;
        }

        if (TextUtils.isEmpty(firstName.trim())) {
            edtFirstName.setError("Vui lòng nhập họ");
            return false;
        }
        if (TextUtils.isEmpty(lastName.trim())) {
            edtLastName.setError("Vui lòng nhập tên");
            return false;
        }

        return true;
    }
}
