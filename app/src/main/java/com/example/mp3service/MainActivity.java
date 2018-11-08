package com.example.mp3service;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;

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

    @BindView(R.id.btnNext)
    AppCompatButton btnNext;

    @BindView(R.id.btnPrev)
    AppCompatButton btnPrev;

    @OnClick(R.id.btnShuffle)
    void shuffle(){
        Intent shuffleIntent = new Intent(MainActivity.this, ForegroundService.class);
        shuffleIntent.setAction(Utils.SHUFFLE_ACTION);
        startService(shuffleIntent);
    }

    @OnClick(R.id.btnStart)
    void start(){
        btnStop.setEnabled(true);
        btnNext.setEnabled(true);
        btnPrev.setEnabled(true);
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
        btnStop.setEnabled(false);
        btnNext.setEnabled(false);
        btnPrev.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
