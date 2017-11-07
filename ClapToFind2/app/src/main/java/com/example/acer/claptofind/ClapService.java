package com.example.acer.claptofind;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;

/**
 * Created by ACER on 11/5/2017.
 */

public class ClapService extends Service {
    NotificationManager notificationManager;
    public static int NOTIFICATION_ID = 1234;
    boolean isStart, swRingTone, swFlash, swVibration;

    AudioDispatcher audio;
    PercussionOnsetDetector detector;
    Thread mThread;

    ConfirmReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        audio = startListen();

        initReceiver();
    }

    private void initReceiver() {
        receiver = new ConfirmReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NotificationActivity.MY_ACTION);
        registerReceiver(receiver, intentFilter);
    }

    public void showNotification(){
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(android.R.drawable.ic_delete);
        builder.setContentText("Clap to find your phone!");
        builder.setContentTitle("Clap to find");

        Intent intent = new Intent(ClapService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());

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
        showNotification();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        notificationManager.cancel(NOTIFICATION_ID);
    }


    public AudioDispatcher startListen() {
        AudioDispatcher mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        double threshold = 8;
        double sensitivity = 70;
        PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                new OnsetHandler() {

                    @Override
                    public void handleOnset(double time, double salience) {
                        stopListen();
                        Log.d("Clap", "Clap detected!");
                        Intent intent = new Intent(ClapService.this, NotificationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(MainActivity.IS_START, isStart);
                        bundle.putBoolean(MainActivity.SW_RINGTONE, swRingTone);
                        bundle.putBoolean(MainActivity.SW_FLASH, swFlash);
                        bundle.putBoolean(MainActivity.SW_VIBRATION, swVibration);
                        intent.putExtras(bundle);
                        //initReceiver();
                        startActivity(intent);

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


    public class ConfirmReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(NotificationActivity.MY_ACTION)){
                boolean recei = intent.getBooleanExtra(NotificationActivity.MY_RECEIVER, false);
                if(recei){
                    audio = startListen();
                    mThread = new Thread(audio);
                    mThread.start();
                }
            }
        }
    }

}
