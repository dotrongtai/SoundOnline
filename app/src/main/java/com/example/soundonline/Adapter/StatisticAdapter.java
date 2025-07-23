package com.example.soundonline.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.R;
import com.example.soundonline.model.Statistic;

import java.util.List;

public class StatisticAdapter extends RecyclerView.Adapter<StatisticAdapter.StatisticViewHolder> {

    private List<Statistic> statistics;

    public StatisticAdapter(List<Statistic> statistics) {
        this.statistics = statistics;
    }

    @NonNull
    @Override
    public StatisticViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_statistic, parent, false);
        return new StatisticViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticViewHolder holder, int position) {
        Statistic statistic = statistics.get(position);
        holder.tvStatisticName.setText(statistic.getName());
        holder.tvStatisticValue.setText(String.valueOf(statistic.getValue()));
    }

    @Override
    public int getItemCount() {
        return statistics.size();
    }

    static class StatisticViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatisticName, tvStatisticValue;

        public StatisticViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatisticName = itemView.findViewById(R.id.tvStatisticName);
            tvStatisticValue = itemView.findViewById(R.id.tvStatisticValue);
        }
    }
}