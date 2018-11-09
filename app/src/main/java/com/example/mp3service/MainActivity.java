package com.example.mp3service;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputType;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.GLAudioVisualizationView;
import com.cleveroad.audiovisualization.VisualizerDbmHandler;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity{

    private Unbinder unbinder;
    private AudioVisualization audioVisualization;
    private boolean isShuffle = false;
    private boolean isPlaying = false;
    private boolean isStart = false;

    @BindView(R.id.ibNext)
    ImageButton ibNext;

    @BindView(R.id.ibPre)
    ImageButton ibPre;

    @BindView(R.id.ibShuffle)
    ImageButton ibShuffle;

    @BindView(R.id.ibPlay)
    ImageButton ibPlay;

    @BindView(R.id.ibLoop)
    ImageButton ibLoop;

    @BindView(R.id.visualizer_view)
    GLAudioVisualizationView visualizerView;

    @BindView(R.id.tvSongTitle)
    AppCompatTextView tvSongTitle;

    @BindView(R.id.ivSongImage)
    AppCompatImageView ivSongImage;

    @BindView(R.id.tvDuration)
    AppCompatTextView tvDuration;

    @BindView(R.id.tvCurrentDuration)
    AppCompatTextView tvCurrentDuration;

    @BindView(R.id.seekBar)
    AppCompatSeekBar seekBar;

    @BindView(R.id.tvArtist)
    AppCompatTextView tvArtist;

    @OnClick(R.id.ibShuffle)
    void shuffle(){
//        if (isShuffle)
//            isShuffle = false;
//        else {
//            isShuffle = true;
//            ibShuffle.setBackgroundColor(Color.GRAY);
//        }
        Intent shuffleIntent = new Intent(MainActivity.this, ForegroundService.class);
        shuffleIntent.setAction(Utils.SHUFFLE_ACTION);
        startService(shuffleIntent);
    }

    @OnClick(R.id.ibPlay)
    void start(){
        int status;
        if (!isStart){
            Intent startIntent = new Intent(MainActivity.this, ForegroundService.class);
            startIntent.setAction(Utils.START_ACTION);
            startService(startIntent);
            isStart = true;
            isPlaying = true;
            status = 1;
            ibPlay.setImageResource(R.drawable.ic_pause);
        } else {
            if (isPlaying) {
                isPlaying = false;
                status = 0;
                ibPlay.setImageResource(R.drawable.ic_play);
            } else {
                isPlaying = true;
                status = 1;
                ibPlay.setImageResource(R.drawable.ic_pause);
            }
        }
        EventBus.getDefault().post(new SongStatusEvent(status));
    }

    void stop(){
        Intent stopIntent = new Intent(MainActivity.this, ForegroundService.class);
        stopIntent.setAction(Utils.STOP_ACTION);
        startService(stopIntent);
    }
    @OnClick(R.id.ibNext)
    void next(){
        Intent nextIntent = new Intent(MainActivity.this, ForegroundService.class);
        nextIntent.setAction(Utils.NEXT_ACTION);
        startService(nextIntent);
    }

    @OnClick(R.id.ibPre)
    void pre(){
        Intent preIntent = new Intent(MainActivity.this, ForegroundService.class);
        preIntent.setAction(Utils.PRE_ACTION);
        startService(preIntent);
    }

    @OnClick(R.id.ibTimer)
    void setTimer(){
        new MaterialDialog.Builder(this)
                .title(R.string.title_dialog)
                .content(R.string.content_dialog)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .positiveText("OK")
                .negativeText("Cancel")
                .input("Minutes", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (Integer.parseInt(input.toString()) <= 1440) {
                            Log.d("dialog", "valid");
                            EventBus.getDefault().post(new TimerEvent(Integer.parseInt(input.toString())));
                        } else {
                            timerInvalidDialog();
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void timerInvalidDialog() {
        new MaterialDialog.Builder(this)
                .title("Timer")
                .content("Your input time must be less than 24 hours")
                .negativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        audioVisualization = (AudioVisualization) visualizerView;
        VisualizerDbmHandler vizualizerHandler = DbmHandler.Factory.newVisualizerHandler(this, 0);
        audioVisualization.linkTo(vizualizerHandler);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSongEvent(SongEvent event) {
        tvSongTitle.setText(event.song.getTitle());
        tvArtist.setText(event.song.getArtist());
        ivSongImage.setImageBitmap(event.song.getImage());
        tvDuration.setText(Utils.durationToTimer(event.song.getDuration()));
        seekBar.setMax(Integer.parseInt(event.song.getDuration()));
//        BitmapDrawable imageLayer = new BitmapDrawable(getResources(), event.song.getImage());
//        Drawable[] layers = {imageLayer, getResources().getDrawable(R.drawable.circle)};
//        LayerDrawable splash_test = new LayerDrawable(layers);
//        ivSongImage.setBackgroundDrawable(splash_test);
//        RotateAnimation rotateAnimation = new RotateAnimation(0, 360f,
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//
//        rotateAnimation.setInterpolator(new LinearInterpolator());
//        rotateAnimation.setDuration(3000);
//        rotateAnimation.setRepeatCount(Animation.INFINITE);

//        ivSongImage.startAnimation(rotateAnimation);
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDurationEvent(DurationEvent event){
        tvCurrentDuration.setText(Utils.durationToTimer(event.currentDuration));
        seekBar.setProgress(event.currentDurationInt);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotiStatusEvent(NotiStatusEvent event){
        if (event.status == 1){
            ibPlay.setImageResource(R.drawable.ic_pause);
        } else {
            ibPlay.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        audioVisualization.onResume();
    }

    @Override
    public void onPause() {
        audioVisualization.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        stop();
    }
}
