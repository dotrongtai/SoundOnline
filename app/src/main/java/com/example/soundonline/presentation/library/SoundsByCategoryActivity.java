package com.example.soundonline.presentation.library;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.CategorySoundAdapter;
import com.example.soundonline.R;
import com.example.soundonline.model.Sound;

import org.json.JSONArray;
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

public class SoundsByCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategorySoundAdapter soundAdapter;
    private List<Sound> soundList;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sounds_by_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView titleTextView = findViewById(R.id.textCategoryTitle);
        recyclerView = findViewById(R.id.recyclerViewSounds);
        Button btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        soundList = new ArrayList<>();
        soundAdapter = new CategorySoundAdapter(this, soundList);
        recyclerView.setAdapter(soundAdapter);

        int categoryId = getIntent().getIntExtra("category_id", -1);
        String categoryName = getIntent().getStringExtra("category_name");
        titleTextView.setText(categoryName);

        btnBack.setOnClickListener(v -> finish());

        client = new OkHttpClient();
        fetchSoundsByCategory(categoryId);
    }

    private void fetchSoundsByCategory(int categoryId) {
        String url = "http://10.0.2.2:7146/api/categories/" + categoryId + "/sounds";
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SoundsByCategoryActivity", "Lỗi kết nối API: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(SoundsByCategoryActivity.this, "Lỗi khi tải danh sách bài hát: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("SoundsByCategoryActivity", "Dữ liệu API: " + responseData);
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        soundList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Sound sound = new Sound(jsonObject.getInt("soundId"));
                            sound.setSoundId(jsonObject.getInt("soundId"));
                            sound.setTitle(jsonObject.getString("title"));
                            sound.setArtistName(jsonObject.getString("artistName"));
                            sound.setAlbumId(jsonObject.isNull("albumId") ? null : jsonObject.getInt("albumId"));
                            sound.setCategoryId(jsonObject.isNull("categoryId") ? null : jsonObject.getInt("categoryId"));
                            sound.setDuration(jsonObject.getInt("duration"));
                            sound.setFileUrl(jsonObject.getString("fileUrl"));
                            sound.setCoverImageUrl(jsonObject.getString("coverImageUrl"));
                            sound.setLyrics(jsonObject.isNull("lyrics") ? null : jsonObject.getString("lyrics"));
                            sound.setPlayCount(jsonObject.isNull("playCount") ? null : jsonObject.getInt("playCount"));
                            sound.setLikeCount(jsonObject.isNull("likeCount") ? null : jsonObject.getInt("likeCount"));
                            sound.setIsPublic(jsonObject.isNull("isPublic") ? null : jsonObject.getBoolean("isPublic"));
                            sound.setIsActive(jsonObject.isNull("isActive") ? null : jsonObject.getBoolean("isActive"));
                            sound.setUploadedBy(jsonObject.isNull("uploadedBy") ? null : jsonObject.getInt("uploadedBy"));
                            sound.setCreatedAt(jsonObject.isNull("createdAt") ? null : jsonObject.getString("createdAt"));
                            soundList.add(sound);
                        }
                        runOnUiThread(() -> soundAdapter.notifyDataSetChanged());
                    } catch (JSONException e) {
                        Log.e("SoundsByCategoryActivity", "Lỗi phân tích JSON: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(SoundsByCategoryActivity.this, "Lỗi phân tích dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                } else {
                    Log.e("SoundsByCategoryActivity", "Phản hồi không thành công: " + response.code());
                    runOnUiThread(() -> Toast.makeText(SoundsByCategoryActivity.this, "Lỗi khi tải danh sách bài hát: " + response.code(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}