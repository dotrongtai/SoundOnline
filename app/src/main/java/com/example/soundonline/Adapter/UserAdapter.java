package com.example.soundonline.Adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.R;
import com.example.soundonline.model.User;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private final OnUserActionListener editListener;
    private final OnUserActionListener deleteListener;

    public interface OnUserActionListener {
        void onUserAction(User user);
    }

    public UserAdapter(List<User> userList, OnUserActionListener editListener, OnUserActionListener deleteListener) {
        this.userList = userList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUsername.setText("Tên đăng nhập: " + (user.getUsername() != null ? user.getUsername() : ""));
        holder.tvEmail.setText("Email: " + (user.getEmail() != null ? user.getEmail() : ""));
        holder.tvName.setText("Tên: " + (user.getFirstName() != null ? user.getFirstName() + " " + (user.getLastName() != null ? user.getLastName() : "") : ""));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        holder.tvDateOfBirth.setText("Ngày sinh: " + (user.getDateOfBirth() != null ? dateFormat.format(user.getDateOfBirth()) : ""));
        holder.tvIsAdmin.setText("Admin: " + (user.getIsAdmin() != null && user.getIsAdmin() ? "Có" : "Không"));

        holder.btnEdit.setOnClickListener(v -> editListener.onUserAction(user));
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa người dùng " + (user.getUsername() != null ? user.getUsername() : "") + "?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        deleteListener.onUserAction(user);
                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                    .setCancelable(true)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvEmail, tvName, tvDateOfBirth, tvIsAdmin;
        Button btnEdit, btnDelete;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvName = itemView.findViewById(R.id.tvName);
            tvDateOfBirth = itemView.findViewById(R.id.tvDateOfBirth);
            tvIsAdmin = itemView.findViewById(R.id.tvIsAdmin);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}