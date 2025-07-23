package com.example.soundonline.presentation.library;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.UserAdapter;
import com.example.soundonline.R;
import com.example.soundonline.model.User;
import com.example.soundonline.network.ApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class UserManagementActivity extends AppCompatActivity {

    @Inject
    ApiService apiService;

    private RecyclerView rvUsers;
    private Button btnAddUser, btnBack;
    private UserAdapter userAdapter;
    private List<User> userList;
    private static final int CREATE_USER_REQUEST = 1;
    private static final int EDIT_USER_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        rvUsers = findViewById(R.id.rvUsers);
        btnAddUser = findViewById(R.id.btnAddUser);
        btnBack = findViewById(R.id.btnBack);

        // Thiết lập RecyclerView
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this::onEditUser, this::onDeleteUser);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(userAdapter);

        // Xử lý sự kiện nút
        btnAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateEditUserActivity.class);
            startActivityForResult(intent, CREATE_USER_REQUEST);
        });

        btnBack.setOnClickListener(v -> finish());

        // Lấy danh sách người dùng
        fetchUsers();
    }

    private void fetchUsers() {
        Call<List<User>> call = apiService.getAllUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Log.d("UserManagementActivity", "API URL: " + call.request().url());
                Log.d("UserManagementActivity", "Request Headers: " + call.request().headers());
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());
                    userAdapter.notifyDataSetChanged();
                    Log.d("UserManagementActivity", "Fetched " + userList.size() + " users");
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (IOException e) {
                        Log.e("UserManagementActivity", "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e("UserManagementActivity", "Error: " + response.code() + ", Message: " + response.message() + ", Body: " + errorBody);
                    runOnUiThread(() -> Toast.makeText(UserManagementActivity.this, "Lỗi tải danh sách người dùng: " + response.code(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("UserManagementActivity", "API connection error: " + t.getMessage());
                runOnUiThread(() -> Toast.makeText(UserManagementActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void onEditUser(User user) {
        Intent intent = new Intent(this, CreateEditUserActivity.class);
        intent.putExtra("user_id", user.getUserId());
        intent.putExtra("username", user.getUsername());
        intent.putExtra("email", user.getEmail());
        intent.putExtra("firstName", user.getFirstName());
        intent.putExtra("lastName", user.getLastName());
        intent.putExtra("dateOfBirth", user.getDateOfBirth() != null ? user.getDateOfBirth().getTime() : 0);
        intent.putExtra("gender", user.getGender());
        intent.putExtra("country", user.getCountry());
        intent.putExtra("bio", user.getBio());
        intent.putExtra("isActive", user.getIsActive());
        intent.putExtra("isAdmin", user.getIsAdmin());
        startActivityForResult(intent, EDIT_USER_REQUEST);
    }

    private void onDeleteUser(User user) {
        Call<Void> call = apiService.deleteUser(user.getUserId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("UserManagementActivity", "Delete API URL: " + call.request().url());
                if (response.isSuccessful()) {
                    userList.remove(user);
                    userAdapter.notifyDataSetChanged();
                    runOnUiThread(() -> Toast.makeText(UserManagementActivity.this, "Xóa người dùng thành công", Toast.LENGTH_SHORT).show());
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (IOException e) {
                        Log.e("UserManagementActivity", "Error reading errorBody: " + e.getMessage());
                    }
                    Log.e("UserManagementActivity", "Delete error (userId=" + user.getUserId() + "): " + response.code() + ", Message: " + response.message() + ", Body: " + errorBody);
                    runOnUiThread(() -> Toast.makeText(UserManagementActivity.this, "Lỗi xóa người dùng: " + response.code(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("UserManagementActivity", "Delete API connection error: " + t.getMessage());
                runOnUiThread(() -> Toast.makeText(UserManagementActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == CREATE_USER_REQUEST || requestCode == EDIT_USER_REQUEST) && resultCode == RESULT_OK) {
            Log.d("UserManagementActivity", "User created/updated, refreshing list");
            fetchUsers();
        }
    }
}