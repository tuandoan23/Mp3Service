package com.example.mp3service;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class MyMusic implements MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
//    private ArrayList<String> listMP3;
    public ArrayList<Song> listSong;
    private MediaMetadataRetriever metaRetriver;
    public static int index = 0;
    private Context context;
    private int seek = 0;

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public MyMusic(Context context){
        this.context = context;
//        listMP3 = listAssetFiles("");
        listSong = listAsset("");
    }
    private ArrayList<String> listAssetFiles(String path) {
        ArrayList<String> listFile = new ArrayList<>();
        String [] list;
        byte[] art;
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
            e.printStackTrace();
        }

        return listFile;
    }

    private ArrayList<Song> listAsset(String path) {
        ArrayList<Song> songArrayList = new ArrayList<>();
        String [] list;
        byte[] art;
        try {
            list = context.getAssets().list(path);
            if (list.length > 0) {
                for (int i = 0; i < list.length; i++){
                    if (list[i].endsWith(".mp3")){
                        Song song = new Song("Title " + i, "Composer " + i, "Artist " + i, "Du " + i , list[i]);
                        songArrayList.add(song);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return songArrayList;
    }

    public void playMedia(){
        stopPlaying();
//        playSound(context, listMP3.get(index));
        playSound(context, listSong.get(index).getFileName());
    }

    public void playSound(final Context context, final String fileName) {
        Log.d("filename", fileName);
        seek = 0;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnSeekCompleteListener(this);
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(fileName);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
            metaData();
//            byte[] art;
//            MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
//            metaRetriver.setDataSource(afd.getFileDescriptor());
//            try {
//                art = metaRetriver.getEmbeddedPicture();
//                Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
//                Log.d("album", metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
//                Log.d("artist", metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
//                Log.d("genre", metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
////                            album_art.setImageBitmap(songImage);
////                            album.setText(metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
////                            artist.setText(metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
////                            genre.setText(metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
//            } catch (Exception e)
//            {
//                Log.d("exception", "e");
////                            album_art.setBackgroundColor(Color.GRAY);
////                            album.setText("Unknown Album");
////                            artist.setText("Unknown Artist");
////                            genre.setText("Unknown Genre");
//            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(this);
    }

    private void metaData(){
        byte[] art;
        String source = "file:///android_asset/girlslikeyou.mp3";
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
        metaRetriver.setDataSource(context, Uri.parse(source));
        try {
            art = metaRetriver.getEmbeddedPicture();
            Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
            Log.d("album", metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            Log.d("artist", metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            Log.d("genre", metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
//                            album_art.setImageBitmap(songImage);
//                            album.setText(metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
//                            artist.setText(metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
//                            genre.setText(metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
        } catch (Exception e)
        {
            Log.d("exception", "e");
//                            album_art.setBackgroundColor(Color.GRAY);
//                            album.setText("Unknown Album");
//                            artist.setText("Unknown Artist");
//                            genre.setText("Unknown Genre");
        }
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        index++;
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        mp.start();
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void pause(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            seek = mediaPlayer.getCurrentPosition();
        }
    }

    public void playResume(){
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(seek);
            Log.d("Seek", String.valueOf(seek));
        }
    }



    public void next() {
//        if (index < listMP3.size() - 1){
        if (index < listSong.size() - 1){
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
//            index = listMP3.size()-1;
            index = listSong.size() - 1;
        }
        playMedia();
    }

    public void stop(){
        mediaPlayer.stop();
    }

    public Song getCurrentSong(){
        return listSong.get(index);
    }
}
