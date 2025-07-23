package com.example.soundonline.presentation.library;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soundonline.R;
import com.example.soundonline.network.Sound.SoundAdminResponse;
import com.example.soundonline.network.Sound.UpdateSoundRequest;
import com.example.soundonline.network.ApiService;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class EditSoundActivity extends AppCompatActivity {

    private TextView tvSoundId;
    private EditText etSoundName, etArtistName, etCategoryId, etAlbumId, etDuration, etFileUrl, etCoverImageUrl, etLyrics, etPlayCount, etLikeCount, etUploadedBy;
    private CheckBox cbIsActive, cbIsPublic;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    private SoundAdminResponse sound;

    @Inject
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sound_admin);

        // Ánh xạ view
        tvSoundId = findViewById(R.id.tvSoundId);
        etSoundName = findViewById(R.id.etSoundName);
        etArtistName = findViewById(R.id.etArtistName);
        etCategoryId = findViewById(R.id.etCategoryId);
        etAlbumId = findViewById(R.id.etAlbumId);
        etDuration = findViewById(R.id.etDuration);
        etFileUrl = findViewById(R.id.etFileUrl);
        etCoverImageUrl = findViewById(R.id.etCoverImageUrl);
        etLyrics = findViewById(R.id.etLyrics);
        etPlayCount = findViewById(R.id.etPlayCount);
        etLikeCount = findViewById(R.id.etLikeCount);
        etUploadedBy = findViewById(R.id.etUploadedBy);
        cbIsActive = findViewById(R.id.cbIsActive);
        cbIsPublic = findViewById(R.id.cbIsPublic);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        // Lấy dữ liệu bài hát từ Intent
        sound = (SoundAdminResponse) getIntent().getSerializableExtra("sound");
        if (sound == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu bài hát", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị dữ liệu hiện tại
        tvSoundId.setText("ID: " + sound.getSoundId());
        etSoundName.setText(sound.getTitle());
        etArtistName.setText(sound.getArtistName());
        etCategoryId.setText(sound.getCategoryId() != null ? String.valueOf(sound.getCategoryId()) : "");
        etAlbumId.setText(sound.getAlbumId() != null ? String.valueOf(sound.getAlbumId()) : "");
        etDuration.setText(String.valueOf(sound.getDuration()));
        etFileUrl.setText(sound.getFileUrl());
        etCoverImageUrl.setText(sound.getCoverImageUrl());
        etLyrics.setText(sound.getLyrics());
        etPlayCount.setText(sound.getPlayCount() != null ? String.valueOf(sound.getPlayCount()) : "");
        etLikeCount.setText(sound.getLikeCount() != null ? String.valueOf(sound.getLikeCount()) : "");
        etUploadedBy.setText(sound.getUploadedBy() != null ? String.valueOf(sound.getUploadedBy()) : "");
        cbIsActive.setChecked(sound.getIsActive() != null ? sound.getIsActive() : false);
        cbIsPublic.setChecked(sound.getIsPublic() != null ? sound.getIsPublic() : false);

        // Xử lý nút Lưu
        btnSave.setOnClickListener(v -> saveSound());

        // Xử lý nút Hủy
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveSound() {
        // Lấy dữ liệu từ giao diện
        String soundName = etSoundName.getText().toString().trim();
        String artistName = etArtistName.getText().toString().trim();
        String categoryIdStr = etCategoryId.getText().toString().trim();
        String albumIdStr = etAlbumId.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();
        String fileUrl = etFileUrl.getText().toString().trim();
        String coverImageUrl = etCoverImageUrl.getText().toString().trim();
        String lyrics = etLyrics.getText().toString().trim();
        String playCountStr = etPlayCount.getText().toString().trim();
        String likeCountStr = etLikeCount.getText().toString().trim();
        String uploadedByStr = etUploadedBy.getText().toString().trim();
        boolean isActive = cbIsActive.isChecked();
        boolean isPublic = cbIsPublic.isChecked();

        // Validate dữ liệu
        if (soundName.isEmpty()) {
            etSoundName.setError("Tên bài hát không được để trống");
            return;
        }
        if (durationStr.isEmpty()) {
            etDuration.setError("Thời lượng không được để trống");
            return;
        }

        Integer categoryId = categoryIdStr.isEmpty() ? null : Integer.parseInt(categoryIdStr);
        Integer albumId = albumIdStr.isEmpty() ? null : Integer.parseInt(albumIdStr);
        Integer duration = Integer.parseInt(durationStr);
        Integer playCount = playCountStr.isEmpty() ? null : Integer.parseInt(playCountStr);
        Integer likeCount = likeCountStr.isEmpty() ? null : Integer.parseInt(likeCountStr);
        Integer uploadedBy = uploadedByStr.isEmpty() ? null : Integer.parseInt(uploadedByStr);

        // Tạo UpdateSoundRequest
        UpdateSoundRequest request = new UpdateSoundRequest();
        request.setSoundName(soundName);
        request.setArtistName(artistName.isEmpty() ? null : artistName);
        request.setCategoryId(categoryId);
        request.setAlbumId(albumId);
        request.setDuration(duration);
        request.setFileUrl(fileUrl.isEmpty() ? null : fileUrl);
        request.setCoverImageUrl(coverImageUrl.isEmpty() ? null : coverImageUrl);
        request.setLyrics(lyrics.isEmpty() ? null : lyrics);
        request.setPlayCount(playCount);
        request.setLikeCount(likeCount);
        request.setIsActive(isActive);
        request.setIsPublic(isPublic);
        request.setUploadedBy(uploadedBy);

        // Gọi API updateSound
        progressBar.setVisibility(View.VISIBLE);
        apiService.updateSound(sound.getSoundId(), request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(EditSoundActivity.this, "Đã cập nhật bài hát: " + sound.getTitle(), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditSoundActivity.this, "Lỗi khi cập nhật bài hát: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditSoundActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
