package com.example.soundonline.Adapter;

import android.content.Context;
import android.util.Log;
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
import com.example.soundonline.model.Following;
import com.example.soundonline.network.ApiService;
import com.example.soundonline.network.Follow.FollowRequest;
import com.example.soundonline.network.Follow.FollowResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.FollowingViewHolder> {

    private final Context context;
    private List<Following> followings;
    private final ApiService apiService;

    public FollowingAdapter(Context context, List<Following> followings, ApiService apiService) {
        this.context = context;
        this.followings = followings;
        this.apiService = apiService;
    }

    @NonNull
    @Override
    public FollowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_following, parent, false);
        return new FollowingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowingViewHolder holder, int position) {
        Following following = followings.get(position);

        holder.textUsername.setText(following.getUsername());
        holder.textBio.setText(following.getBio());

        Glide.with(context)
                .load(following.getAvatarurl())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(holder.imageAvatar);

        updateFollowButtonText(holder.btnFollow, following.isFollowing());

        holder.btnFollow.setOnClickListener(v -> {
            boolean wasFollowing = following.isFollowing();
            boolean isNowFollowing = !wasFollowing;

            // Optimistic update
            following.setFollowing(isNowFollowing);
            notifyItemChanged(position);

            FollowRequest request = new FollowRequest(following.getUserId());

            apiService.follow(request).enqueue(new Callback<FollowResponse>() {
                @Override
                public void onResponse(@NonNull Call<FollowResponse> call, @NonNull Response<FollowResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        boolean updatedStatus = response.body().isFollowing();
                        following.setFollowing(updatedStatus);
                        notifyItemChanged(position);
                        String msg = updatedStatus ? "Đã theo dõi " : "Đã bỏ theo dõi ";
                        Toast.makeText(context, msg + following.getUsername(), Toast.LENGTH_SHORT).show();
                    } else {
                        // Revert UI on failure
                        following.setFollowing(wasFollowing);
                        notifyItemChanged(position);
                        Log.e("Adapter", "API error: " + response.code());
                        Toast.makeText(context, "Lỗi cập nhật theo dõi!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FollowResponse> call, @NonNull Throwable t) {
                    following.setFollowing(wasFollowing);
                    notifyItemChanged(position);
                    Log.e("Adapter", "Network error: " + t.getMessage(), t);
                    Toast.makeText(context, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return followings != null ? followings.size() : 0;
    }

    private void updateFollowButtonText(Button button, boolean isFollowing) {
        button.setText(isFollowing ? "Đang theo dõi" : "Theo dõi");
    }

    public void updateData(List<Following> newList) {
        this.followings = newList;
        notifyDataSetChanged();
    }

    static class FollowingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAvatar;
        TextView textUsername, textBio;
        Button btnFollow;

        public FollowingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.imgAvatar);
            textUsername = itemView.findViewById(R.id.tvUsername);
            textBio = itemView.findViewById(R.id.textBio);
            btnFollow = itemView.findViewById(R.id.btnFollow);
        }
    }
}
