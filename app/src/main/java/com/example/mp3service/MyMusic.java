package com.example.mp3service;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MyMusic implements MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
    //    private ArrayList<String> listMP3;
    public ArrayList<Song> listSong;
    public ArrayList<File> listFile;
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
//        listSong = listAsset("");
        listFile = getAllFile();
        listSong = getListSong();
        Log.d("list song", String.valueOf(listSong.size()));
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

//    private ArrayList<Song> listAsset(String path) {
//        Song song = new Song();
//        ArrayList<Song> songArrayList = song.createData();
//        ArrayList<String> listFileMP3 = new ArrayList<>();
//        String [] list;
//        try {
//            list = context.getAssets().list(path);
//            if (list.length > 0) {
//                Log.d("length", String.valueOf(list.length));
//                for (int i = 0; i < list.length; i++){
//                    if (list[i].endsWith(".mp3")){
//                        listFileMP3.add(list[i]);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (listFileMP3.size() > 0){
//            for (int i = 0; i < listFileMP3.size(); i++){
//                songArrayList.get(i).setFileName(listFileMP3.get(i));
//            }
//        }
//
//        return songArrayList;
//    }

    public void playMedia(){
        stopPlaying();
        playSound(context, listSong.get(index).getFileName());
    }

    public void playSound(final Context context, final String fileName) {
        EventBus.getDefault().post(new CompleteEvent(true));
        seek = 0;
        mediaPlayer = new MediaPlayer();
//        try {
//            AssetFileDescriptor afd = context.getAssets().openFd(fileName);
//            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
//            afd.close();
//            mediaPlayer.prepare();
//        } catch (final Exception e) {
//            e.printStackTrace();
//        }
        mediaPlayer = MediaPlayer.create(context, Uri.parse(fileName));
        mediaPlayer.start();
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    private Song getSongbySource(String source){
        Song song = new Song();
        byte[] art;
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
        metaRetriver.setDataSource(context, Uri.parse(source));
//        try {
            String title = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String genre = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
            String album = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String duration = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            art = metaRetriver.getEmbeddedPicture();
            Bitmap songImage;
            if (art != null) {
                songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
            } else {
                songImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_media);
            }
            if (title != null)
                song.setTitle(title);
            else
                song.setTitle("Unknown Title");
            if (artist != null)
                song.setArtist(artist);
            else
                song.setArtist("Unknown Artist");
            if (genre != null )
                song.setGenre(genre);
            else
                song.setGenre("Unknown Genre");
            if (album != null)
                song.setAlbum(album);
            else
                song.setAlbum("Unknown Album");
            if (duration != null)
                song.setDuration(duration);
            else
                song.setDuration("xx:xx");
            song.setImage(songImage);
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//            song.setTitle("Unknown Title");
//            song.setArtist("Unknown Artist");
//            song.setGenre("Unknown Genre");
//            song.setAlbum("Unknown Album");
//            song.setDuration("xx:xx");
//            song.setImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_play));
//        }
        song.setFileName("");
        return song;
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        index++;
        playMedia();
        Log.d("status","Complete");
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
//            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.seekTo(seek);
            Log.d("Seek", String.valueOf(seek));
        }
    }



    public void next() {
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

    public ArrayList<File> getAllFile(){
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        Log.d("root", root.getPath().toString());
        ArrayList<File> inFiles = new ArrayList<>();
        File list[] = root.listFiles();
        Log.d("length", String.valueOf(list.length));
        for (int i = 0; i < list.length; i++)
        {
            File temp_file = new File(list[i].getAbsolutePath(),list[i].getName());
            if (temp_file.listFiles() != null)
            {
                Log.d("List file", "not empty");
            }
            else
            {
                if (list[i].getName().toLowerCase().contains(".mp3"))
                {
                    inFiles.add(list[i]);
                }
            }
        }
        return inFiles;
    }

    public ArrayList<Song> getListSong(){
        ArrayList<Song> listSong = new ArrayList<>();
        for (int i = 0; i < listFile.size(); i++){
            File file = listFile.get(i);
            Song song = getSongbySource(file.getAbsolutePath());
            song.setFileName(file.getAbsolutePath());
            listSong.add(song);
        }
        return  listSong;
        //String title, String artist, Bitmap image, String duration, String fileName
    }

    public void shuffleList() {
        long seed = System.nanoTime();
        Collections.shuffle(listSong, new Random(seed));
        index = 0;
        playMedia();
    }
}
