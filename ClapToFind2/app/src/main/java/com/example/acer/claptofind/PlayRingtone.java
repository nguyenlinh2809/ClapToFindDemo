package com.example.acer.claptofind;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by ACER on 10/29/2017.
 */

public class PlayRingtone {
    Context context;
    MediaPlayer mediaPlayer;
    public PlayRingtone(Context context){
        this.context = context;
        mediaPlayer = MediaPlayer.create(context, R.raw.ringtone);
        mediaPlayer.setLooping(true);
    }
    public void playSong(){
        if(mediaPlayer!=null && (!mediaPlayer.isPlaying())){
            mediaPlayer.start();
        }
    }
    public void stopSong(){
        if(mediaPlayer.isPlaying() && mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

    }
}
