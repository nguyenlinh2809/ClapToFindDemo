package com.example.acer.claptofind;


import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_CODE = 100;
    private static final int RECORD_AUDIO_CODE = 101;
    private static final String CAMERA_PERMISSION = "android.permission.CAMERA";
    private static final String MICROPHONE_PERMISSION = "android.permission.RECORD_AUDIO";

    Switch swRingtone, swFlash, swVibration;
    ImageButton imbtnToggle;

    ShareReferencesManager setting;

    boolean isStart;
    AudioDispatcher audio;
    PercussionOnsetDetector detector;
    Thread mThread;

    PlayRingtone playRingtone;
    Vibration vibrate;
    TurnOnFlash turnOnFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
    }

    private void addEvents() {
        imbtnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doStart();
            }
        });
    }

    private void doStart() {
        if(isStart == false){
            playRingtone = new PlayRingtone(this);

            imbtnToggle.setImageResource(R.drawable.image_button_on);
            isStart = true;
            audio = startListen();
            mThread = new Thread(audio);
            mThread.start();

        }else{
            imbtnToggle.setImageResource(R.drawable.image_button_off);
            isStart = false;
            //stopListen();
            stopNotification();

        }
    }


    private void addControls() {
        swRingtone = (Switch) findViewById(R.id.swPlayRingtone);
        swFlash = (Switch) findViewById(R.id.swFlash);
        swVibration = (Switch) findViewById(R.id.swVibrate);
        imbtnToggle = (ImageButton) findViewById(R.id.imbtnToggle);
        setting = new ShareReferencesManager(this);
        isStart = setting.getIsOnStatus();

        vibrate = new Vibration(getApplicationContext());
        turnOnFlash = new TurnOnFlash();
    }

    public AudioDispatcher startListen() {
        AudioDispatcher mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        double threshold = 8;
        double sensitivity = 60;
        PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                new OnsetHandler() {

                    @Override
                    public void handleOnset(double time, double salience) {
                        Log.d("Clap", "Clap detected!");
                        startNotification(swRingtone.isChecked(), swFlash.isChecked(), swVibration.isChecked());
                    }
                }, sensitivity, threshold);
        mDispatcher.addAudioProcessor(mPercussionDetector);
        this.detector = mPercussionDetector;
        return mDispatcher;
    }

    public void startNotification(boolean isRingTone, boolean isFlash, boolean isVibration){
        if(isRingTone && isStart){
            playRingtone.playSong();
        }
        if(isFlash){
            turnOnFlash.blinkFlash(isStart);
        }
        if(isVibration){
            vibrate.vibrate();
        }
        stopListen();

    }
    public void stopNotification(){
        if(isStart==false){
            playRingtone.stopSong();
            vibrate.stopVibrate();
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        swRingtone.setChecked(setting.getRingtoneStatus());
        swFlash.setChecked(setting.getFlashStatus());
        swVibration.setChecked(setting.getVibrationStatus());
        isStart = setting.getIsOnStatus();
        if(isStart){
            imbtnToggle.setImageResource(R.drawable.image_button_on);
        }else imbtnToggle.setImageResource(R.drawable.image_button_off);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setting.saveSetting(swRingtone.isChecked(), swFlash.isChecked(), swVibration.isChecked(), isStart);
    }
}

