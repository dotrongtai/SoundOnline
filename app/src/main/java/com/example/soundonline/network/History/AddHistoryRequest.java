package com.example.soundonline.network.History;

public class AddHistoryRequest {
    private int soundId;
    private int playDuration;
    private boolean completedPlay;

    public AddHistoryRequest(int soundId, int playDuration, boolean completedPlay) {
        this.soundId = soundId;
        this.playDuration = playDuration;
        this.completedPlay = completedPlay;
    }

    public int getSoundId() {
        return soundId;
    }

    public void setSoundId(int soundId) {
        this.soundId = soundId;
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
}
