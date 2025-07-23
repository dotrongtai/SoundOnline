package com.example.soundonline.presentation.player;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import com.example.soundonline.R;
import com.example.soundonline.network.ApiService;
import com.example.soundonline.network.Sound.UploadSoundRequest;
import com.example.soundonline.network.Sound.UploadSoundResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import javax.inject.Inject;
import android.util.Log;

@AndroidEntryPoint
public class UploadSoundActivity extends ComponentActivity {

    @Inject
    ApiService apiService;

    private TextInputEditText etTitle, etArtistName, etFileUrl, etCoverImageUrl;
    private TextInputLayout tilTitle, tilArtistName, tilFileUrl, tilCoverImageUrl;
    private Button btnSubmit, btnCancel;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_sound);

        // Lấy userId từ Intent
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ view
        tilTitle = findViewById(R.id.tilTitle);
        tilArtistName = findViewById(R.id.tilArtistName);
        tilFileUrl = findViewById(R.id.tilFileUrl);
        tilCoverImageUrl = findViewById(R.id.tilCoverImageUrl);
        etTitle = findViewById(R.id.etTitle);
        etArtistName = findViewById(R.id.etArtistName);
        etFileUrl = findViewById(R.id.etFileUrl);
        etCoverImageUrl = findViewById(R.id.etCoverImageUrl);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);

        // Xử lý nút Hủy
        btnCancel.setOnClickListener(v -> finish());

        // Xử lý nút Tải lên
        btnSubmit.setOnClickListener(v -> {
            // Reset lỗi
            tilTitle.setError(null);
            tilArtistName.setError(null);
            tilFileUrl.setError(null);

            String title = etTitle.getText().toString().trim();
            String artistName = etArtistName.getText().toString().trim();
            String fileUrl = etFileUrl.getText().toString().trim();
            String coverImageUrl = etCoverImageUrl.getText().toString().trim();

            // Kiểm tra các trường bắt buộc
            boolean isValid = true;
            if (title.isEmpty()) {
                tilTitle.setError("Vui lòng nhập tiêu đề bài hát");
                isValid = false;
            }
            if (artistName.isEmpty()) {
                tilArtistName.setError("Vui lòng nhập tên nghệ sĩ");
                isValid = false;
            }
            if (fileUrl.isEmpty()) {
                tilFileUrl.setError("Vui lòng nhập URL file");
                isValid = false;
            }

            if (!isValid) {
                return;
            }

            // Tạo UploadSoundRequest
            UploadSoundRequest request = new UploadSoundRequest(
                    title,
                    artistName,
                    fileUrl,
                    coverImageUrl.isEmpty() ? null : coverImageUrl,
                    userId,
                    null,
                    1,
                    0,
                    null,
                    true,
                    false 
            );

            // Gọi API uploadSound
            apiService.uploadSound(request).enqueue(new Callback<UploadSoundResponse>() {
                @Override
                public void onResponse(Call<UploadSoundResponse> call, Response<UploadSoundResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UploadSoundResponse uploadResponse = response.body();
                        Toast.makeText(UploadSoundActivity.this, uploadResponse.getMessage() + ". Bài hát đang chờ duyệt.", Toast.LENGTH_LONG).show();
                        Log.d("UploadSoundActivity", "Upload successful, SoundId: " + uploadResponse.getSoundId());
                        finish(); // Quay lại MainActivity sau khi tải lên thành công
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Lỗi không xác định";
                            Log.e("UploadSoundActivity", "Upload failed: " + errorBody);
                            Toast.makeText(UploadSoundActivity.this, "Tải lên thất bại: " + errorBody, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("UploadSoundActivity", "Error reading error body: " + e.getMessage());
                            Toast.makeText(UploadSoundActivity.this, "Tải lên thất bại", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadSoundResponse> call, Throwable t) {
                    Log.e("UploadSoundActivity", "Upload error: " + t.getMessage());
                    Toast.makeText(UploadSoundActivity.this, "Lỗi kết nối khi tải lên: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}