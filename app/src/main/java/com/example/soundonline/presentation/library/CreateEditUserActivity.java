package com.example.soundonline.presentation.library;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class CreateEditUserActivity extends AppCompatActivity {

    @Inject
    ApiService apiService;

    private EditText etUsername, etEmail, etPassword, etFirstName, etLastName, etDateOfBirth, etGender, etCountry, etBio;
    private CheckBox cbIsActive;
    private Button btnSave, btnCancel;
    private int userId = -1;
    private Date selectedDateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_edit_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etGender = findViewById(R.id.etGender);
        etCountry = findViewById(R.id.etCountry);
        etBio = findViewById(R.id.etBio);
        cbIsActive = findViewById(R.id.cb_is_active);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Đặt etDateOfBirth thành chỉ đọc
        etDateOfBirth.setKeyListener(null);
        etDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        // Kiểm tra nếu là chỉnh sửa người dùng
        Intent intent = getIntent();
        if (intent.hasExtra("user_id")) {
            userId = intent.getIntExtra("user_id", -1);
            etUsername.setText(intent.getStringExtra("username"));
            etEmail.setText(intent.getStringExtra("email"));
            etFirstName.setText(intent.getStringExtra("firstName"));
            etLastName.setText(intent.getStringExtra("lastName"));
            long dobMillis = intent.getLongExtra("dateOfBirth", 0);
            if (dobMillis != 0) {
                selectedDateOfBirth = new Date(dobMillis);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                etDateOfBirth.setText(dateFormat.format(selectedDateOfBirth));
            }
            etGender.setText(intent.getStringExtra("gender"));
            etCountry.setText(intent.getStringExtra("country"));
            etBio.setText(intent.getStringExtra("bio"));
            cbIsActive.setChecked(intent.getBooleanExtra("isActive", true));
            etPassword.setEnabled(false); // Không cho chỉnh sửa mật khẩu khi cập nhật
        }

        // Xử lý sự kiện nút
        btnSave.setOnClickListener(v -> saveUser());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDateOfBirth != null) {
            calendar.setTime(selectedDateOfBirth);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay);
                    selectedDateOfBirth = selectedCalendar.getTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    etDateOfBirth.setText(dateFormat.format(selectedDateOfBirth));
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void saveUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String bio = etBio.getText().toString().trim();
        boolean isActive = cbIsActive.isChecked();

        // Kiểm tra dữ liệu đầu vào
        if (username.isEmpty() || email.isEmpty() || (userId == -1 && password.isEmpty())) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ tên đăng nhập, email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra độ dài mật khẩu khi tạo mới
        if (userId == -1 && password.length() <= 6) {
            etPassword.setError("Mật khẩu phải có hơn 6 ký tự");
            etPassword.requestFocus();
            Toast.makeText(this, "Mật khẩu phải có hơn 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng email cơ bản
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            Toast.makeText(this, "Vui lòng nhập email hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra username
        if (username.length() < 3) {
            etUsername.setError("Tên đăng nhập phải có ít nhất 3 ký tự");
            etUsername.requestFocus();
            Toast.makeText(this, "Tên đăng nhập phải có ít nhất 3 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra token
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", "");
        if (token.isEmpty()) {
            Log.e("CreateEditUserActivity", "No JWT token found");
            Toast.makeText(this, "Lỗi: Không tìm thấy token xác thực", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d("CreateEditUserActivity", "JWT token: " + token);

        User user = new User(
                userId == -1 ? 0 : userId,
                username,
                email,
                userId == -1 ? password : null,
                firstName,
                lastName,
                selectedDateOfBirth,
                gender,
                country,
                null,
                bio,
                isActive,
                false,
                null,
                null,
                null,
                null
        );

        Log.d("CreateEditUserActivity", "Sending user data: " + user.toString());
        Log.d("CreateEditUserActivity", "Password length: " + password.length() + ", Password: " + password);
        if (userId == -1) {
            Log.d("CreateEditUserActivity", "JSON body: " + new com.google.gson.Gson().toJson(user));
        }

        Call<User> call = userId == -1 ? apiService.createUser(user) : apiService.updateUserById(userId, user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("CreateEditUserActivity", "API URL: " + call.request().url());
                Log.d("CreateEditUserActivity", "Request Headers: " + call.request().headers());
                Log.d("CreateEditUserActivity", "Response code: " + response.code());
                if (response.isSuccessful()) {
                    Log.d("CreateEditUserActivity", (userId == -1 ? "Created" : "Updated") + " user successfully: " + response.body());
                    runOnUiThread(() -> {
                        Toast.makeText(CreateEditUserActivity.this, userId == -1 ? "Tạo người dùng thành công" : "Cập nhật người dùng thành công", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                } else {
                    final String errorMessage = response.errorBody() != null ? getErrorMessage(response) : "No error body";
                    Log.e("CreateEditUserActivity", "Error: " + response.code() + ", Message: " + response.message() + ", Body: " + errorMessage);
                    runOnUiThread(() -> Toast.makeText(CreateEditUserActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("CreateEditUserActivity", "API connection error: " + t.getMessage());
                runOnUiThread(() -> Toast.makeText(CreateEditUserActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }

            private String getErrorMessage(Response<User> response) {
                try {
                    return response.errorBody().string();
                } catch (IOException e) {
                    Log.e("CreateEditUserActivity", "Error reading errorBody: " + e.getMessage());
                    return "Không thể đọc phản hồi từ server";
                }
            }
        });
    }
}