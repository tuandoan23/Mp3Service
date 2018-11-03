package com.example.mp3service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ForegroundService extends Service {
    private MyMusic myMusic;
    private Notification notification;
    private NotificationManager nm;
    private PendingIntent pendingIntent;
    private PendingIntent pendingNextIntent;
    private PendingIntent pendingPreIntent;
    private PendingIntent pendingPlayIntent;
    private PendingIntent pendingPauseIntent;
    private PendingIntent pendingShuffleIntent;
    private Notification.Action playAction;
    private Notification.Action pauseAction;
    public ForegroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myMusic = new MyMusic(this);
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        EventBus.getDefault().register(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null){
            Log.d("intent", "NULL");
        } else {
            initPendingIntent();

            playAction = new Notification.Action(R.drawable.ic_play, "", pendingPlayIntent);
            pauseAction = new Notification.Action(R.drawable.ic_pause, "", pendingPauseIntent);

            notification = showNotification(pauseAction);
            nm.notify(Utils.NOTIFICATION, notification);

            handleAction(intent);
        }
        return START_STICKY;
    }

    private void initPendingIntent() {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setAction(Utils.MAIN_ACTION);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getService(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, ForegroundService.class);
        nextIntent.setAction(Utils.NEXT_ACTION);
        pendingNextIntent = PendingIntent.getService(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(this, ForegroundService.class);
        playIntent.setAction(Utils.PLAY_ACTION);
        pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, ForegroundService.class);
        pauseIntent.setAction(Utils.PAUSE_ACTION);
        pendingPauseIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent prevIntent = new Intent(this, ForegroundService.class);
        prevIntent.setAction(Utils.PRE_ACTION);
        pendingPreIntent = PendingIntent.getService(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent shuffleIntent = new Intent(this, ForegroundService.class);
        shuffleIntent.setAction(Utils.SHUFFLE_ACTION);
        pendingPreIntent = PendingIntent.getService(this, 0, shuffleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void handleAction(Intent intent) {
        if (intent.getAction().equals(Utils.START_ACTION)) {
            startForeground(Utils.NOTIFICATION, notification);
            myMusic.playMedia();
        } else if (intent.getAction().equals(Utils.PRE_ACTION)) {
            myMusic.pre();
            nm.notify(Utils.NOTIFICATION, showNotification(pauseAction));
        } else if (intent.getAction().equals(Utils.PLAY_ACTION)) {
            myMusic.playResume();
            notification.actions[1] = pauseAction;
            nm.notify(Utils.NOTIFICATION, showNotification(pauseAction));
        } else if (intent.getAction().equals(Utils.PAUSE_ACTION)){
            myMusic.pause();
            notification.actions[1] = playAction;
            nm.notify(Utils.NOTIFICATION, showNotification(playAction));
        } else if (intent.getAction().equals(Utils.NEXT_ACTION)) {
            myMusic.next();
            nm.notify(Utils.NOTIFICATION, showNotification(pauseAction));
        } else if (intent.getAction().equals(Utils.STOP_ACTION)) {
            stopForeground(true);
            stopSelf();
        } else if (intent.getAction().equals(Utils.SHUFFLE_ACTION)){
            myMusic.shuffleList();
        }
    }

    private Notification showNotification(Notification.Action action){
        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setContentTitle(myMusic.getCurrentSong().getTitle())
                .setContentText(myMusic.getCurrentSong().getArtist())
                .setSubText(myMusic.getCurrentSong().getAlbum())
                .setSmallIcon(R.drawable.ic_play)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setLargeIcon(myMusic.getCurrentSong().getImage())
                .addAction(R.drawable.ic_pre, "", pendingPreIntent)
                .addAction(action)
                .addAction(R.drawable.ic_next, "", pendingNextIntent);
        return notificationBuilder.build();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCompleteEvent(CompleteEvent event) {
        if (event.isComplete){
            nm.notify(Utils.NOTIFICATION, showNotification(pauseAction));
        }
    };

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        nm.cancel(Utils.NOTIFICATION);
        EventBus.getDefault().unregister(this);
        myMusic.stop();
        super.onDestroy();
    }
}
