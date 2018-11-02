package com.example.mp3service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ForegroundService extends Service {
    private MyMusic myMusic;
    private Notification notification;
    private Notification.Action notiAction;
    private final IBinder mBinder = new LocalBinder();
    private NotificationManager nm;
    public ForegroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myMusic = new MyMusic(this);
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public class LocalBinder extends Binder {
        ForegroundService getService() {
            return ForegroundService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null){
            Log.d("intent", "NULL");
        } else {
            Log.d("intent action", intent.getAction());
            Intent notifyIntent = new Intent(this, MainActivity.class);
            notifyIntent.setAction(Utils.MAIN_ACTION);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent nextIntent = new Intent(this, ForegroundService.class);
            nextIntent.setAction(Utils.NEXT_ACTION);
            PendingIntent pendingNextIntent = PendingIntent.getService(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent playIntent = new Intent(this, ForegroundService.class);
            playIntent.setAction(Utils.PLAY_ACTION);
            PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent pauseIntent = new Intent(this, ForegroundService.class);
            pauseIntent.setAction(Utils.PAUSE_ACTION);
            PendingIntent pendingPauseIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent prevIntent = new Intent(this, ForegroundService.class);
            prevIntent.setAction(Utils.PRE_ACTION);
            PendingIntent pendingPreIntent = PendingIntent.getService(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Action playAction = new Notification.Action(R.drawable.ic_play, "", pendingPlayIntent);
            Notification.Action pauseAction = new Notification.Action(R.drawable.ic_pause, "", pendingPauseIntent);
//            if (notification == null) {
            Log.d("Title", myMusic.getCurrentSong().getTitle());
            notification = new Notification.Builder(this)
                    .setContentTitle(myMusic.getCurrentSong().getTitle())
                    .setContentText(myMusic.getCurrentSong().getArtist())
                    .setSubText(myMusic.getCurrentSong().getComposer())
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setStyle(new Notification.MediaStyle().setShowActionsInCompactView(1))
                    .addAction(R.drawable.ic_pre, "", pendingPreIntent)
                    .addAction(pauseAction)
//                        .addAction(playAction)
                    .addAction(R.drawable.ic_next, "", pendingNextIntent)
                    .build();
//            }
//            nm.notify(Utils.NOTIFICATION, notification);
            if (intent.getAction().equals(Utils.START_ACTION)) {
                startForeground(Utils.ID, notification);
                myMusic.playMedia();
//                metaData();
            } else if (intent.getAction().equals(Utils.PRE_ACTION)) {
                myMusic.pre();
            } else if (intent.getAction().equals(Utils.PLAY_ACTION)) {
                Log.d("stt", "1");
                myMusic.playResume();
                notification.actions[1] = pauseAction;
            } else if (intent.getAction().equals(Utils.PAUSE_ACTION)){
                Log.d("stt","2");
                myMusic.pause();
                notification.actions[1] = playAction;
            } else if (intent.getAction().equals(Utils.NEXT_ACTION)) {
                myMusic.next();
            } else if (intent.getAction().equals(Utils.STOP_ACTION)) {
                stopForeground(true);
                stopSelf();
            }
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        nm.cancel(Utils.NOTIFICATION);
        myMusic.stop();
        super.onDestroy();
    }

//    private void metaData(){
//        byte[] art;
//        String source = "/data/data/" + context.getPackageName() + "/databases/";
//        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
//        metaRetriver.setDataSource("file:///android_asset/buonkhongem.mp3");
//        try {
//            art = metaRetriver.getEmbeddedPicture();
//            Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
//            Log.d("album", metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
//            Log.d("artist", metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
//            Log.d("genre", metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
////                            album_art.setImageBitmap(songImage);
////                            album.setText(metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
////                            artist.setText(metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
////                            genre.setText(metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
//        } catch (Exception e)
//        {
//            Log.d("exception", "e");
////                            album_art.setBackgroundColor(Color.GRAY);
////                            album.setText("Unknown Album");
////                            artist.setText("Unknown Artist");
////                            genre.setText("Unknown Genre");
//        }
//    }
}
