package com.example.soundonline.model;

public class Statistic {
    private String name;
    private int value;

    public Statistic(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}