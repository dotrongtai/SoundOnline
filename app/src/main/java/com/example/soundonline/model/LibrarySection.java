package com.example.soundonline.model;

public class LibrarySection {
    private String title;
    private boolean hasSeeAll;

    public LibrarySection(String title, boolean hasSeeAll) {
        this.title = title;
        this.hasSeeAll = hasSeeAll;
    }

    public String getTitle() {
        return title;
    }

    public boolean hasSeeAll() {
        return hasSeeAll;
    }
}
