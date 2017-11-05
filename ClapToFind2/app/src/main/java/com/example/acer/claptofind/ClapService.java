package com.example.acer.claptofind;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;

/**
 * Created by ACER on 11/5/2017.
 */

public class ClapService extends Service {
    boolean isStart, swRingTone, swFlash, swVibration;

    PlayRingtone playRingtone;
    Vibration vibration;
    TurnOnFlash turnOnFlash;

    AudioDispatcher audio;
    PercussionOnsetDetector detector;
    Thread mThread;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playRingtone = new PlayRingtone(getApplicationContext());
        vibration = new Vibration(getApplicationContext());
        turnOnFlash = new TurnOnFlash();
        audio = startListen();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle bundle = intent.getBundleExtra(MainActivity.BUNDLE);
        if(bundle != null){
            isStart = bundle.getBoolean(MainActivity.IS_START);
            swRingTone = bundle.getBoolean(MainActivity.SW_RINGTONE);
            swFlash = bundle.getBoolean(MainActivity.SW_FLASH);
            swVibration = bundle.getBoolean(MainActivity.SW_VIBRATION);

            if(isStart){
                mThread = new Thread(audio);
                mThread.start();
            }

        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopListen();
    }


    public AudioDispatcher startListen() {
        AudioDispatcher mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        double threshold = 8;
        double sensitivity = 40;
        PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                new OnsetHandler() {

                    @Override
                    public void handleOnset(double time, double salience) {
                        stopListen();
                        Log.d("Clap", "Clap detected!");
                        startNotification(swRingTone, swFlash, swVibration);

                    }
                }, sensitivity, threshold);
        mDispatcher.addAudioProcessor(mPercussionDetector);
        this.detector = mPercussionDetector;
        return mDispatcher;
    }
    public void stopListen() {
        if (audio != null) {
            audio.removeAudioProcessor(detector);
            if (mThread != null) {
                mThread.interrupt();
            }
            audio.stop();
            Log.d("Stop", "Stop");
        } else {
            Toast.makeText(this, "Already stop", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void startNotification(boolean isRingTone, boolean isFlash, boolean isVibration){
        if(isRingTone && isStart){
            playRingtone.playSong();
        }
        if(isFlash){
            turnOnFlash.blinkFlash(isStart);
        }
        if(isVibration){
            vibration.vibrate();
        }

    }
    public void stopNotification(){
        if(isStart==false){
            playRingtone.stopSong();
            vibration.stopVibrate();
        }
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage("FOUND ME?");
        builder.setTitle("Click YES to finish notification");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setCancelable(false);
        builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                stopNotification();
                audio = startListen();
                mThread = new Thread(audio);
                mThread.start();
            }
        });
        builder.show();

    }
}
