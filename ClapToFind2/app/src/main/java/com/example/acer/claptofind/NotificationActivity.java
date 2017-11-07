package com.example.acer.claptofind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class NotificationActivity extends AppCompatActivity {
    public static String MY_ACTION = "my_action";
    public static String MY_RECEIVER = "my_receiver";
    PlayRingtone playRingtone;
    Vibration vibration;
    TurnOnFlash turnOnFlash;

    boolean receiver = false;
    boolean checkFlash = true;
    boolean isRingTone, isFlash, isVibration = false;

    Button btnYes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        addControls();
        addEvents();

    }

    private void addEvents() {
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiver = true;
                checkFlash = false;
                stopNotification(isRingTone, isVibration, isFlash);
                Intent intent = new Intent();
                intent.setAction(MY_ACTION);
                intent.putExtra(MY_RECEIVER, receiver);
                sendBroadcast(intent);
                finish();
            }
        });
    }

    private void addControls() {
        btnYes = (Button) findViewById(R.id.btnYes);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        isRingTone = bundle.getBoolean(MainActivity.SW_RINGTONE);
        isFlash = bundle.getBoolean(MainActivity.SW_FLASH);
        isVibration = bundle.getBoolean(MainActivity.SW_VIBRATION);
        if(isFlash){
            turnOnFlash = new TurnOnFlash();
        }
        playRingtone = new PlayRingtone(getApplicationContext());
        vibration = new Vibration(getApplicationContext());
        startNotification(isRingTone, isFlash, isVibration);
    }

    public void startNotification(boolean isRingTone, boolean isFlash, boolean isVibration){
        if(isRingTone){
            playRingtone.playSong();
        }
        if(isFlash){
            turnOnFlash.turnOn();
        }
        if(isVibration){
            vibration.vibrate();
        }

    }
    public void stopNotification(boolean isRingTone, boolean isVibration, boolean isFlash){
        if(isRingTone){
            playRingtone.stopSong();
        }
        if(isVibration){
            vibration.stopVibrate();
        }
        if(isFlash && (!checkFlash)){
            turnOnFlash.turnOff();
        }
    }


}
