// File: app/src/main/java/com/example/soundonline/network/Search/SearchResponse.java
package com.example.soundonline.network.Search;

import com.example.soundonline.model.Sound;
import java.util.List;

public class SearchResponse {
    private List<Sound> sounds;

    public List<Sound> getSounds() {
        return sounds;
    }

    public void setSounds(List<Sound> sounds) {
        this.sounds = sounds;
    }
}