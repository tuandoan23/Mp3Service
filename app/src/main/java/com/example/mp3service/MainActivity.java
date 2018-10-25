package com.example.mp3service;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;

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

    @BindView(R.id.tvName)
    AppCompatTextView tvName;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
    }
}
