package com.example.soundonline.presentation.library;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.SoundAdminAdapter;
import com.example.soundonline.R;
import com.example.soundonline.network.Sound.SoundAdminResponse;
import com.example.soundonline.network.Sound.UpdateSoundRequest;
import com.example.soundonline.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ManageSoundsActivity extends AppCompatActivity implements SoundAdminAdapter.OnSoundActionListener {

    private RecyclerView rvSounds;
    private SoundAdminAdapter adapter;
    private List<SoundAdminResponse> soundList;

    @Inject
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_sounds);

        // Khởi tạo RecyclerView
        rvSounds = findViewById(R.id.rvSounds);
        rvSounds.setLayoutManager(new LinearLayoutManager(this));
        soundList = new ArrayList<>();
        adapter = new SoundAdminAdapter(this, apiService, soundList, this);
        rvSounds.setAdapter(adapter);

        // Tải danh sách bài hát
        loadAllSounds();
    }

    // Tải tất cả bài hát từ API
    private void loadAllSounds() {
        apiService.getAllSounds().enqueue(new Callback<List<SoundAdminResponse>>() {
            @Override
            public void onResponse(Call<List<SoundAdminResponse>> call, Response<List<SoundAdminResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    soundList.clear();
                    soundList.addAll(response.body());
                    adapter.updateSounds(soundList);
                } else {
                    Toast.makeText(ManageSoundsActivity.this, "Lỗi khi tải danh sách bài hát: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SoundAdminResponse>> call, Throwable t) {
                Toast.makeText(ManageSoundsActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditSound(SoundAdminResponse sound) {
        Toast.makeText(this, "Chỉnh sửa bài hát: " + sound.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSoundHidden(SoundAdminResponse sound) {
        Toast.makeText(this, "Đã ẩn bài hát: " + sound.getTitle(), Toast.LENGTH_SHORT).show();
    }
}