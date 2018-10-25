package com.example.mp3service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class ForegroundService extends Service {
    private MyMusic myMusic;
    private Notification notification;
    public ForegroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myMusic = new MyMusic(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getAction().equals(Utils.PRE_ACTION)){
            Log.d("status", Utils.PRE_ACTION);
            myMusic.pre();
        } else if (intent.getAction().equals(Utils.PLAY_ACTION)){
            myMusic.play();
            if (myMusic.isPlaying()){

            } else {

            }
        } else if (intent.getAction().equals(Utils.NEXT_ACTION)){
            myMusic.next();
        }
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Utils.START_ACTION)){
            Intent notifyIntent = new Intent(this, MainActivity.class);
            notifyIntent.setAction(Utils.MAIN_ACTION);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, 0);

            Intent nextIntent = new Intent(this, ForegroundService.class);
            nextIntent.setAction(Utils.NEXT_ACTION);
            PendingIntent pendingNextIntent = PendingIntent.getActivity(this, 0, nextIntent, 0);

            Intent playIntent = new Intent(this, ForegroundService.class);
            nextIntent.setAction(Utils.PLAY_ACTION);
            PendingIntent pendingPlayIntent = PendingIntent.getActivity(this, 0, playIntent, 0);

            Intent prevIntent = new Intent(this, ForegroundService.class);
            nextIntent.setAction(Utils.PRE_ACTION);
            PendingIntent pendingPreIntent = PendingIntent.getActivity(this, 0, prevIntent, 0);

            notification = new Notification.Builder(this)
                    .setContentTitle("Music Player")
                    .setContentText("My Music")
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .addAction(R.drawable.ic_pre, "Prev", pendingPreIntent)
                    .addAction(R.drawable.ic_play, "Play", pendingPlayIntent)
                    .addAction(R.drawable.ic_next, "Next", pendingNextIntent)
                    .build();
            startForeground(Utils.ID, notification);
            myMusic.playMedia();
        } else if (intent.getAction().equals(Utils.PRE_ACTION)){
            Log.d("status", Utils.PRE_ACTION);
            myMusic.pre();
        } else if (intent.getAction().equals(Utils.PLAY_ACTION)){
            myMusic.play();
            if (myMusic.isPlaying()){

            } else {

            }
        } else if (intent.getAction().equals(Utils.NEXT_ACTION)){
            myMusic.next();
        } else if (intent.getAction().equals(Utils.STOP_ACTION)){
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        myMusic.stop();
        super.onDestroy();
    }
}
