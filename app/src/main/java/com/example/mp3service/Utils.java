package com.example.mp3service;

public class Utils {
    public static final String MAIN_ACTION = "MAIN";
    public static final String START_ACTION = "START";
    public static final String STOP_ACTION = "STOP";
    public static final String PAUSE_ACTION = "PAUSE";
    public static final String PLAY_ACTION = "PLAY";
    public static final String NEXT_ACTION = "NEXT";
    public static final String PRE_ACTION = "PREVIOUS";
    public static final String SHUFFLE_ACTION = "SHUFFLE";
    public static final int ID = 111;
    public static final int NOTIFICATION = 999;

    public static String durationToTimer(String duration) {
        long milliseconds = Long.parseLong(duration);
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
}
