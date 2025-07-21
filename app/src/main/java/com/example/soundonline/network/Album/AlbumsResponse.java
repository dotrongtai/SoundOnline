package com.example.soundonline.network.Album;

import com.example.soundonline.model.Album;

import java.util.List;

public class AlbumsResponse {
    private List<Album> data;

    public List<Album> getData() {
        return data;
    }

    public void setData(List<Album> data) {
        this.data = data;
    }
}
