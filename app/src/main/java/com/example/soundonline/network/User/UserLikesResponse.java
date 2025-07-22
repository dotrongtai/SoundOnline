package com.example.soundonline.network.User;

import com.example.soundonline.model.Sound;

import java.util.List;

public class UserLikesResponse {
    private List<Sound> sounds;

    public List<Sound> getSounds() {
        return sounds;
    }

    public void setSounds(List<Sound> sounds) {
        this.sounds = sounds;
    }
}
