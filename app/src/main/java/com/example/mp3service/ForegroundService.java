package com.example.mp3service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.time.Duration;
import java.util.Calendar;

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
    private final Handler timerHandler = new Handler();
    private final Handler notificationHandler = new Handler();
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
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent == null){
            Log.d("intent", "NULL");
        } else {
            initPendingIntent();
            playAction = new Notification.Action(R.drawable.ic_play, "", pendingPlayIntent);
            pauseAction = new Notification.Action(R.drawable.ic_pause, "", pendingPauseIntent);
            notification = showNotification(pauseAction, "00:00");
            nm.notify(Utils.NOTIFICATION, notification);
            handleAction(intent);
            notificationHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int currentPosition = myMusic.getCurrentPosition();
                    if (myMusic.isPlaying()){
                        nm.notify(Utils.NOTIFICATION, showNotification(pauseAction, Utils.durationToTimer(String.valueOf(currentPosition))));
                    } else {
                        nm.notify(Utils.NOTIFICATION, showNotification(playAction, Utils.durationToTimer(String.valueOf(currentPosition))));
                    }
                    EventBus.getDefault().post(new DurationEvent(String.valueOf(currentPosition), currentPosition));
                    notificationHandler.postDelayed(this, 1000);
                }
            }, 1000);
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
        pendingShuffleIntent = PendingIntent.getService(this, 0, shuffleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void handleAction(Intent intent) {
        if (intent.getAction().equals(Utils.START_ACTION)) {
            startForeground(Utils.NOTIFICATION, notification);
            myMusic.playMedia();
        } else if (intent.getAction().equals(Utils.PRE_ACTION)) {
            myMusic.pre();
            nm.notify(Utils.NOTIFICATION, showNotification(pauseAction, "00:00"));
        } else if (intent.getAction().equals(Utils.PLAY_ACTION)) {
            EventBus.getDefault().post(new NotiStatusEvent(1));
            myMusic.playResume();
            nm.notify(Utils.NOTIFICATION, showNotification(pauseAction, String.valueOf(myMusic.getCurrentPosition())));
        } else if (intent.getAction().equals(Utils.PAUSE_ACTION)){
            EventBus.getDefault().post(new NotiStatusEvent(0));
            myMusic.pause();
            nm.notify(Utils.NOTIFICATION, showNotification(playAction, String.valueOf(myMusic.getCurrentPosition())));
        } else if (intent.getAction().equals(Utils.NEXT_ACTION)) {
            myMusic.next();
            nm.notify(Utils.NOTIFICATION, showNotification(pauseAction, "00:00"));
        } else if (intent.getAction().equals(Utils.STOP_ACTION)) {
            stopService();
        } else if (intent.getAction().equals(Utils.SHUFFLE_ACTION)){
            myMusic.shuffleList();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSongStatusEvent(SongStatusEvent event) {
        if (event.status == 1){
            myMusic.playResume();
            nm.notify(Utils.NOTIFICATION, showNotification(pauseAction, String.valueOf(myMusic.getCurrentPosition())));
        } else {
            myMusic.pause();
            nm.notify(Utils.NOTIFICATION, showNotification(playAction, String.valueOf(myMusic.getCurrentPosition())));
        }
    };

    private Notification showNotification(Notification.Action action, String timer){
        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setContentTitle(myMusic.getCurrentSong().getTitle())
                .setContentText(myMusic.getCurrentSong().getArtist())
                .setSubText(timer + " | " + Utils.durationToTimer(myMusic.getCurrentSong().getDuration()))
                .setSmallIcon(R.drawable.ic_play)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(R.drawable.ic_pre, "", pendingPreIntent)
                .addAction(action)
                .addAction(R.drawable.ic_next, "", pendingNextIntent)
                .setStyle(new Notification.MediaStyle().setShowActionsInCompactView(1))
                .setLargeIcon(myMusic.getCurrentSong().getImage());
        return notificationBuilder.build();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCompleteEvent(CompleteEvent event) {
        if (event.isComplete && !myMusic.mediaIsNull()){
            nm.notify(Utils.NOTIFICATION, showNotification(pauseAction, Utils.durationToTimer(String.valueOf(myMusic.getCurrentPosition()))));
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerEvent(final TimerEvent event) {
        final Calendar timeEnd = getTimeEnd(event.timer);
        timerHandler.removeCallbacksAndMessages(null);
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Calendar now = Calendar.getInstance();
                if (now.getTime().getTime() > timeEnd.getTime().getTime()){
                    stopService();
                }
                timerHandler.postDelayed(this, 1000);
            }
        }, 1000);
    };

    public Calendar getTimeEnd(int timer){
        int hours;
        int minutes;
        if (timer >= 60){
            hours = timer/60;
            minutes = timer - hours* 60;
        } else {
            hours = 0;
            minutes = timer;
        }
        Calendar now = Calendar.getInstance();
        Calendar tmp = (Calendar) now.clone();
        tmp.add(Calendar.HOUR_OF_DAY, hours);
        tmp.add(Calendar.MINUTE, minutes);
        Calendar timeEnd = tmp;
        return  timeEnd;
    }

    @Override
    public void onDestroy() {
        stopService();
        super.onDestroy();
    }

    public void stopService(){
        stopForeground(true);
        stopSelf();
        timerHandler.removeCallbacksAndMessages(null);
        notificationHandler.removeCallbacksAndMessages(null);
        nm.cancel(Utils.NOTIFICATION);
        EventBus.getDefault().unregister(this);
        myMusic.stop();
    }
}
