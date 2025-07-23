package com.example.soundonline.presentation.playlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.SoundAdapter;
import com.example.soundonline.R;
import com.example.soundonline.model.Sound;
import com.example.soundonline.network.ApiService;
import com.example.soundonline.network.Search.SearchResponse;
import com.example.soundonline.presentation.main.MainActivity;
import com.example.soundonline.presentation.player.PlayerActivity;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class  searchActivity extends ComponentActivity {
    @Inject
    ApiService apiService;
    private RecyclerView rvSearch; // RecyclerView để hiển thị kết quả tìm kiếm
    private SoundAdapter soundAdapter; // Adapter để hiển thị danh sách âm thanh
    Button btnPlay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search); // Gắn layout vào activity
        rvSearch = findViewById(R.id.rvSearchResults); // Khởi tạo RecyclerView từ layout
        rvSearch.setLayoutManager(new LinearLayoutManager(this)); // Cài đặt layout manager
        fetchSounds();
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = etSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                fetchSounds(); // Reload original list if search is empty or only spaces
            } else {
                searchSongs(keyword);
            }
            return true;
        });



    }
    private void searchSongs(String keyword) {
        apiService.search(keyword, "sound").enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Sound> soundList = response.body().getSounds();
                    soundAdapter = new SoundAdapter(searchActivity.this, soundList);
                    rvSearch.setAdapter(soundAdapter);
                    soundAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Log.e("searchSongs", "Lỗi kết nối API", t);
            }
        });
    }
    // Thêm log kiểm tra số lượng dữ liệu và dùng biến soundAdapter toàn cục
    private void fetchSounds() {
        apiService.getSounds().enqueue(new Callback<List<Sound>>() {
            @Override
            public void onResponse(Call<List<Sound>> call, Response<List<Sound>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Sound> soundList = response.body();
                    Log.d("fetchSounds", "Số lượng: " + soundList.size());
                    soundAdapter = new SoundAdapter(searchActivity.this, soundList);
                    rvSearch.setAdapter(soundAdapter);
                    soundAdapter.notifyDataSetChanged();


                } else {
                    Log.e("fetchSounds", "Lỗi phản hồi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Sound>> call, Throwable t) {
                Log.e("fetchSounds", "Lỗi kết nối API", t);
            }
        });
    }

}
