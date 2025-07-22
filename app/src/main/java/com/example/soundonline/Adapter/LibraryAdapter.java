package com.example.soundonline.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.R;
import com.example.soundonline.model.LibrarySection;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {
    private List<LibrarySection> sections;
    private OnItemClickListener listener;
    public LibraryAdapter(List<LibrarySection> sections) {
        this.sections = sections;
    }
    public interface OnItemClickListener {
        void onItemClick(LibrarySection section);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_library_section, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LibrarySection section = sections.get(position);
        holder.titleTextView.setText(section.getTitle());
        holder.seeAllTextView.setVisibility(section.hasSeeAll() ? View.VISIBLE : View.GONE);

        // 👉 Gắn sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(section);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView seeAllTextView;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.section_title);
            seeAllTextView = itemView.findViewById(R.id.see_all);
        }
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }


}
