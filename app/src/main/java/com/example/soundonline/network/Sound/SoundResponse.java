package com.example.soundonline.network.Sound;

import com.example.soundonline.model.Sound;

import java.util.List;

public class SoundResponse {
    private boolean success;
    private String message;
    private List<Sound> data;

    // Getter & Setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Sound> getData() {
        return data;
    }

    public void setData(List<Sound> data) {
        this.data = data;
    }
}
