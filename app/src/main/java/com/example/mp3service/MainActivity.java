package com.example.mp3service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.widget.RemoteViews;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.btnStart)
    AppCompatButton btnStart;

    @BindView(R.id.btnStop)
    AppCompatButton btnStop;

    @OnClick(R.id.btnShuffle)
    void shuffle(){
        Intent shuffleIntent = new Intent(MainActivity.this, ForegroundService.class);
        shuffleIntent.setAction(Utils.SHUFFLE_ACTION);
        startService(shuffleIntent);
    }

    @OnClick(R.id.btnStart)
    void start(){
        Intent startIntent = new Intent(MainActivity.this, ForegroundService.class);
        startIntent.setAction(Utils.START_ACTION);
        startService(startIntent);
    }

    @OnClick(R.id.btnStop)
    void stop(){
        Intent stopIntent = new Intent(MainActivity.this, ForegroundService.class);
        stopIntent.setAction(Utils.STOP_ACTION);
        startService(stopIntent);
    }
    @OnClick(R.id.btnNext)
    void next(){
        Intent nextIntent = new Intent(MainActivity.this, ForegroundService.class);
        nextIntent.setAction(Utils.NEXT_ACTION);
        startService(nextIntent);
    }

    @OnClick(R.id.btnPrev)
    void pre(){
        Intent preIntent = new Intent(MainActivity.this, ForegroundService.class);
        preIntent.setAction(Utils.PRE_ACTION);
        startService(preIntent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
