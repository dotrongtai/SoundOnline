package com.example.soundonline.network.User;

import com.example.soundonline.model.History;

import java.util.List;

public class UserHistoryResponse {
    private List<History> history;

    public List<History> getHistory() {
        return history;
    }

    public void setHistory(List<History> history) {
        this.history = history;
    }
}