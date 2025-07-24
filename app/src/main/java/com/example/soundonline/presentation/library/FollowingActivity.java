package com.example.soundonline.presentation.library;

import android.os.Bundle;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.FollowingAdapter;
import com.example.soundonline.R;
import com.example.soundonline.model.Following;
import com.example.soundonline.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.widget.ImageButton;
import android.widget.Toast;

@AndroidEntryPoint
public class FollowingActivity extends AppCompatActivity {

    @Inject
    ApiService apiService;

    private RecyclerView recyclerView;
    private FollowingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        recyclerView = findViewById(R.id.recyclerViewFollowing);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FollowingAdapter(this, new ArrayList<>(),apiService);
        recyclerView.setAdapter(adapter);

        int userId = getSharedPreferences("auth", MODE_PRIVATE).getInt("user_id", -1);
        if (userId != -1) {
            apiService.getFollowing(userId).enqueue(new Callback<List<Following>>() {
                @Override
                public void onResponse(Call<List<Following>> call, Response<List<Following>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        adapter.updateData(response.body());
                    } else {
                        Toast.makeText(FollowingActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Following>> call, Throwable t) {
                    Toast.makeText(FollowingActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        setupHeader();
    }
    private void setupHeader() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish()); // Go back to the previous activity
        // You can also set a custom title if needed, or update the existing tvTitle
        // TextView tvTitle = findViewById(R.id.tvTitle);
        // tvTitle.setText("Album của tôi"); // Or whatever appropriate title
    }
}
