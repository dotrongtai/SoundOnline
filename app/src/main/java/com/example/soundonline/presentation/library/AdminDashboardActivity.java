package com.example.soundonline.presentation.library;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.R;
import com.example.soundonline.Adapter.StatisticAdapter;
import com.example.soundonline.model.Statistic;
import com.example.soundonline.network.StatisticsResponse;
import com.example.soundonline.presentation.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button btnManageUsers, btnBack, btnHome, btnManageSounds;
    private RecyclerView rvStatistics;
    private StatisticAdapter statisticAdapter;
    private List<Statistic> statisticList;
    private OkHttpClient client;
    private static final String BASE_URL = "http://10.0.2.2:7146/api/admin/statistics"; // URL API cho emulator
    private static final String PREFS_NAME = "auth"; // Sửa thành "auth" để khớp với Login.java
    private static final String TOKEN_KEY = "jwt_token"; // Key lưu token

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);
        rvStatistics = findViewById(R.id.rvStatistics);
        btnManageSounds=findViewById(R.id.btnManageSounds);
        // Thiết lập RecyclerView
        statisticList = new ArrayList<>();
        statisticAdapter = new StatisticAdapter(statisticList);
        rvStatistics.setLayoutManager(new GridLayoutManager(this, 2));
        rvStatistics.setAdapter(statisticAdapter);

        // Khởi tạo OkHttpClient
        client = new OkHttpClient();

        // Gọi API để lấy số liệu thống kê
        fetchStatistics();

        // Xử lý sự kiện nút
        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserManagementActivity.class);
            startActivity(intent);
        });
        btnManageSounds.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageSoundsActivity.class);
            intent.putExtra("user_id", getIntent().getIntExtra("user_id", -1));
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user_id", getIntent().getIntExtra("user_id", -1));
            startActivity(intent);
        });
    }

    private void fetchStatistics() {
        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, "");
        Log.d("AdminDashboardActivity", "Token from SharedPreferences: " + token); // Log để kiểm tra token
        if (token.isEmpty()) {
            runOnUiThread(() -> Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show());
            return;
        }

        // Tạo request với header Authorization
        Request request = new Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Accept", "application/json")
                .build();

        // Gọi API bất đồng bộ
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AdminDashboardActivity", "Lỗi kết nối API: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(AdminDashboardActivity.this, "Lỗi khi tải số liệu thống kê: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Log.d("AdminDashboardActivity", "Dữ liệu API: " + responseData);
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        StatisticsResponse stats = new StatisticsResponse();
                        stats.setCategoryCount(jsonObject.getInt("categoryCount"));
                        stats.setSoundCount(jsonObject.getInt("soundCount"));
                        stats.setUserCount(jsonObject.getInt("userCount"));
                        stats.setCommentCount(jsonObject.getInt("commentCount"));

                        // Cập nhật danh sách thống kê
                        runOnUiThread(() -> {
                            statisticList.clear();
                            statisticList.add(new Statistic("Người Dùng", stats.getUserCount()));
                            statisticList.add(new Statistic("Bài Hát", stats.getSoundCount()));
                            statisticList.add(new Statistic("Thể Loại", stats.getCategoryCount()));
                            statisticList.add(new Statistic("Bình Luận", stats.getCommentCount()));
                            statisticAdapter.notifyDataSetChanged();
                        });
                    } catch (JSONException e) {
                        Log.e("AdminDashboardActivity", "Lỗi phân tích JSON: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(AdminDashboardActivity.this, "Lỗi phân tích dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                } else {
                    Log.e("AdminDashboardActivity", "Phản hồi không thành công: " + response.code());
                    runOnUiThread(() -> Toast.makeText(AdminDashboardActivity.this, "Lỗi khi tải số liệu thống kê: " + response.code(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}