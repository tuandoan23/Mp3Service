package com.example.mp3service;

public class Song {
    private String title;
    private String composer;
    private String artist;
    private String duration;
    private String fileName;


    public Song() {
    }

    public Song(String title, String composer, String artist, String duration, String fileName) {

        this.title = title;
        this.composer = composer;
        this.artist = artist;
        this.duration = duration;
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
