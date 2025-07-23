 package com.example.soundonline.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soundonline.R;
import com.example.soundonline.network.Sound.SoundAdminResponse;
import com.example.soundonline.network.Sound.UpdateSoundRequest;
import com.example.soundonline.network.ApiService;
import com.example.soundonline.presentation.library.EditSoundActivity;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SoundAdminAdapter extends RecyclerView.Adapter<SoundAdminAdapter.SoundViewHolder> {

    private Context context;
    private List<SoundAdminResponse> soundList;
    private OnSoundActionListener actionListener;
    private ApiService apiService;

    // Callback interface để xử lý sự kiện chỉnh sửa và ẩn
    public interface OnSoundActionListener {
        void onEditSound(SoundAdminResponse sound);
        void onSoundHidden(SoundAdminResponse sound);
    }

    @Inject
    public SoundAdminAdapter(@ApplicationContext Context context, ApiService apiService, List<SoundAdminResponse> soundList, OnSoundActionListener listener) {
        this.context = context;
        this.apiService = apiService;
        this.soundList = soundList;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sound_admin, parent, false);
        return new SoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {
        SoundAdminResponse sound = soundList.get(position);

        // Hiển thị tên bài hát
        holder.tvTitle.setText(sound.getTitle());

        // Hiển thị tên người tải lên
        String uploaderName = sound.getUploaderName() != null ? sound.getUploaderName() : "Unknown";
        holder.tvUploadedBy.setText(uploaderName);

        // Tải ảnh bìa bằng Glide
        Glide.with(context)
                .load(sound.getCoverImageUrl())
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(holder.imgSound);

        // Xử lý nút Chỉnh sửa
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditSoundActivity.class);
            intent.putExtra("sound", sound);
            context.startActivity(intent);
            if (actionListener != null) {
                actionListener.onEditSound(sound);
            }
        });

        // Xử lý nút Ẩn
        holder.btnHide.setOnClickListener(v -> hideSound(sound, position));
    }

    @Override
    public int getItemCount() {
        return soundList != null ? soundList.size() : 0;
    }


    // Cập nhật danh sách bài hát
    public void updateSounds(List<SoundAdminResponse> newSoundList) {
        this.soundList = newSoundList;
        notifyDataSetChanged();
    }

    // Ẩn bài hát bằng API updateSound
    private void hideSound(SoundAdminResponse sound, int position) {
        UpdateSoundRequest request = new UpdateSoundRequest();
        request.setIsActive(false);
        apiService.updateSound(sound.getSoundId(), request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Đã ẩn bài hát: " + sound.getTitle(), Toast.LENGTH_SHORT).show();
                    if (actionListener != null) {
                        actionListener.onSoundHidden(sound);
                    }
                    soundList.remove(position);
                    notifyItemRemoved(position);
                } else {
                    Toast.makeText(context, "Lỗi khi ẩn bài hát: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class SoundViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSound;
        TextView tvTitle, tvUploadedBy;
        Button btnEdit, btnHide;

        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSound = itemView.findViewById(R.id.imgSound);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvUploadedBy = itemView.findViewById(R.id.tvUploadedBy);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnHide = itemView.findViewById(R.id.btnHide);
        }
    }
}
