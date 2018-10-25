package com.example.mp3service;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;

public class MyMusic {
    private MediaPlayer mediaPlayer;
    private ArrayList<String> listMP3;
    public static int index = 0;
    private Context context;
    private int seek = 0;

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public MyMusic(Context context){
        this.context = context;
        listMP3 = listAssetFiles("");
    }

    private ArrayList<String> listAssetFiles(String path) {
        ArrayList<String> listFile = new ArrayList<>();
        String [] list;
        try {
            list = context.getAssets().list(path);
            if (list.length > 0) {
                for (int i = 0; i < list.length; i++){
                    if (list[i].endsWith(".mp3")){
                        listFile.add(list[i]);
                    }
                }
            }
        } catch (IOException e) {
            return listFile;
        }

        return listFile;
    }

    public void playMedia(){
        stopPlaying();
        playSound(context, listMP3.get(index));
    }

    public void playSound(final Context context, final String fileName) {
        seek = 0;
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(fileName);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void play(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            seek = mediaPlayer.getCurrentPosition();
        } else {
            mediaPlayer.seekTo(seek);
        }
    }

    public void next() {
        if (index < listMP3.size() - 1){
            index++;
        } else {
            index = 0;
        }
        playMedia();
    }

    public void pre() {
        if (index > 0){
            index--;
        } else {
            index = listMP3.size()-1;
        }
        playMedia();
    }

    public void stop(){
        mediaPlayer.stop();
    }
}
