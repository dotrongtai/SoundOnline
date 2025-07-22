package com.example.soundonline.presentation.library;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.HistoryAdapter;
import com.example.soundonline.R;
import com.example.soundonline.model.History;
import com.example.soundonline.network.User.UserHistoryResponse;
import com.example.soundonline.network.ApiService;
import com.example.soundonline.presentation.main.MainActivity;

import java.util.List;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ListeningHistoryActivity extends ComponentActivity {

    @Inject
    ApiService apiService;

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening_history);

        // Setup toolbar
        ImageButton backButton = findViewById(R.id.btnBack);
        TextView titleView = findViewById(R.id.tvTitle);
        titleView.setText("Listening History");

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recycler_view_listening_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(this, null);
        recyclerView.setAdapter(adapter);

        // Get userId
        userId = getUserIdFromPreferences();
        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem lịch sử nghe.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Fetch data
        fetchUserHistory();

        // Handle back
        backButton.setOnClickListener(v -> onBackPressed());

        // Bottom navigation
        setupBottomNavigation();
    }

    private int getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("auth", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }

    private void fetchUserHistory() {
        apiService.getUserHistory(userId).enqueue(new Callback<List<History>>() {
            @Override
            public void onResponse(Call<List<History>> call, Response<List<History>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<History> historyItems = response.body();
                    if (!historyItems.isEmpty()) {
                        adapter.updateData(historyItems);
                    } else {
                        Toast.makeText(ListeningHistoryActivity.this, "Không có lịch sử nghe nào.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ListeningHistoryActivity.this, "Không thể tải lịch sử nghe.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<History>> call, Throwable t) {
                Toast.makeText(ListeningHistoryActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupBottomNavigation() {
        findViewById(R.id.btnHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            Toast.makeText(this, "Search feature chưa được hỗ trợ.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnLibrary).setOnClickListener(v -> {
            // Current screen
        });

        findViewById(R.id.btnCategory).setOnClickListener(v -> {
            Toast.makeText(this, "Category feature chưa được hỗ trợ.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnPlaylist).setOnClickListener(v -> {
            Toast.makeText(this, "Playlist feature chưa được hỗ trợ.", Toast.LENGTH_SHORT).show();
        });
    }
}
