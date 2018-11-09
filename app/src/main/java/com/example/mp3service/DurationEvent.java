package com.example.mp3service;

public class DurationEvent {
    public final String currentDuration;
    public final int currentDurationInt;

    public DurationEvent(String currentDuration, int currentDurationInt) {
        this.currentDuration = currentDuration;
        this.currentDurationInt = currentDurationInt;
    }
}
