package com.example.soundonline.presentation.library;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log; // Import Log
import android.view.View; // Import View
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.HistoryAdapter;
import com.example.soundonline.R;
import com.example.soundonline.model.History;
import com.example.soundonline.network.ApiService;
import com.example.soundonline.presentation.main.MainActivity;
import com.example.soundonline.presentation.player.PlayerActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    private boolean isSelectionMode = false; // Keep track of selection mode in activity as well

    private ImageButton btnDeleteHistory; // Reference to the delete button

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening_history);

        // Setup toolbar
        ImageButton backButton = findViewById(R.id.btnBack);
        TextView titleView = findViewById(R.id.tvTitle);
        ImageButton btnPlayRandom = findViewById(R.id.btnPlayRandom);
        ImageButton btnPlayAll = findViewById(R.id.btnPlayAll);
        btnDeleteHistory = findViewById(R.id.btnDeleteHistory); // Initialize the button
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

        // Handle back button
        backButton.setOnClickListener(v -> {
            if (isSelectionMode) {
                exitSelectionMode(); // Exit selection mode if active
            } else {
                onBackPressed(); // Go back as usual
            }
        });

        // Play Random
        btnPlayRandom.setOnClickListener(v -> {
            List<History> historyList = adapter.getCurrentData();
            if (historyList == null || historyList.isEmpty()) {
                Toast.makeText(this, "Danh sách trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<String> audioUrls = new ArrayList<>();
            for (History h : historyList) {
                if (h.getSound() != null && h.getSound().getFileUrl() != null) {
                    audioUrls.add(h.getSound().getFileUrl());
                }
            }

            // Shuffle playlist
            Collections.shuffle(audioUrls);

            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("playMode", "playlist");
            intent.putStringArrayListExtra("playlist", audioUrls);
            startActivity(intent);
        });

        // Delete history button logic
        btnDeleteHistory.setOnClickListener(v -> {
            if (isSelectionMode) {
                // If in selection mode, prompt for deletion
                Set<Integer> selectedIds = adapter.getSelectedIds();
                if (selectedIds.isEmpty()) {
                    Toast.makeText(this, "Chọn bài để xóa", Toast.LENGTH_SHORT).show();
                    return;
                }

                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc muốn xóa các bài đã chọn?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            deleteHistories(selectedIds);
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            } else {
                // If not in selection mode, enter selection mode
                enterSelectionMode();
            }
        });


        // Play All
        btnPlayAll.setOnClickListener(v -> {
            List<History> historyList = adapter.getCurrentData();
            if (historyList == null || historyList.isEmpty()) {
                Toast.makeText(this, "Danh sách trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi danh sách sang PlayerActivity
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("playMode", "playlist");

            ArrayList<String> audioUrls = new ArrayList<>();
            for (History h : historyList) {
                if (h.getSound() != null) {
                    audioUrls.add(h.getSound().getFileUrl());
                }
            }
            intent.putStringArrayListExtra("playlist", audioUrls);
            startActivity(intent);
        });

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

    // Function to delete histories via API
    private void deleteHistories(Set<Integer> ids) {
        apiService.deleteHistories(getUserIdFromPreferences(), new ArrayList<>(ids)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ListeningHistoryActivity.this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                    exitSelectionMode(); // Exit selection mode after deletion
                    fetchUserHistory(); // Reload data
                } else {
                    // Log the error details
                    Log.e("DeleteHistory", "Xóa dữ liệu thất bại. Mã lỗi: " + response.code());
                    try {
                        // Attempt to read the error body for more details from the server
                        if (response.errorBody() != null) {
                            Log.e("DeleteHistory", "Thông báo lỗi từ server: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        Log.e("DeleteHistory", "Lỗi đọc errorBody: " + e.getMessage());
                    }
                    Toast.makeText(ListeningHistoryActivity.this, "Lỗi xóa dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ListeningHistoryActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to enter selection mode
    private void enterSelectionMode() {
        isSelectionMode = true;
        adapter.setSelectionMode(true);
        // You might want to change the UI of the delete button here, e.g., to "Done" or "Cancel"
        // For example, change its icon or text
        btnDeleteHistory.setImageResource(R.drawable.ic_delete); // Example: change to a checkmark icon
        // Or hide other buttons if they conflict with selection mode
        findViewById(R.id.btnPlayRandom).setVisibility(View.GONE);
        findViewById(R.id.btnPlayAll).setVisibility(View.GONE);
    }

    // Method to exit selection mode
    private void exitSelectionMode() {
        isSelectionMode = false;
        adapter.setSelectionMode(false);
        // Reset the delete button UI
        btnDeleteHistory.setImageResource(R.drawable.ic_delete); // Example: change back to delete icon
        // Show other buttons again
        findViewById(R.id.btnPlayRandom).setVisibility(View.VISIBLE);
        findViewById(R.id.btnPlayAll).setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (isSelectionMode) {
            exitSelectionMode(); // Exit selection mode if active
        } else {
            super.onBackPressed(); // Perform default back action
        }
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