package com.example.soundonline.model;

import java.util.Date;

public class History {
    private int historyId ;
    private String playedAt;
    private int playDuration;
    private boolean completedPlay;
    private Sound sound;


    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public String getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(String playedAt) {
        this.playedAt = playedAt;
    }

    public int getPlayDuration() {
        return playDuration;
    }

    public void setPlayDuration(int playDuration) {
        this.playDuration = playDuration;
    }

    public boolean isCompletedPlay() {
        return completedPlay;
    }

    public void setCompletedPlay(boolean completedPlay) {
        this.completedPlay = completedPlay;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }
}
