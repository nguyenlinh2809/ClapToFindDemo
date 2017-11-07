package com.example.acer.claptofind;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 5678;
    private static final String CAMERA_PERMISSION = "android.permission.CAMERA";
    private static final String MICROPHONE_PERMISSION = "android.permission.RECORD_AUDIO";
    public static final String IS_START = "is_start";
    public static final String SW_RINGTONE = "sw_ringtone";
    public static final String SW_FLASH = "sw_flash";
    public static final String SW_VIBRATION = "sw_vibration";
    public static final String BUNDLE = "bundle";


    Switch swRingtone, swFlash, swVibration;
    ImageButton imbtnToggle;

    ShareReferencesManager setting;

    boolean isStart;

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
        swFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(checkPermission(CAMERA_PERMISSION)){
                        return;
                    }else {
                        requestPermissions(new String[]{CAMERA_PERMISSION}, PERMISSION_CODE);
                    }
                }

            }
        });

    }

    private void doStart() {
        if((!swFlash.isChecked()) && (!swRingtone.isChecked()) && (!swVibration.isChecked())){
            if(!isStart){
                Toast.makeText(this, "Must be choose one!", Toast.LENGTH_SHORT).show();
                return;
            }

        }
        if (!isStart) {
            if(!checkPermission(MICROPHONE_PERMISSION)){
                requestPermissions(new String[]{MICROPHONE_PERMISSION}, PERMISSION_CODE);
            }else {
                isStart = true;
                imbtnToggle.setImageResource(R.drawable.image_button_on);
                Intent intent = new Intent(MainActivity.this, ClapService.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(IS_START, isStart);
                bundle.putBoolean(SW_RINGTONE, swRingtone.isChecked());
                bundle.putBoolean(SW_FLASH, swFlash.isChecked());
                bundle.putBoolean(SW_VIBRATION, swVibration.isChecked());
                intent.putExtra(BUNDLE, bundle);
                Log.d("service_status", isStart+" "+ swRingtone.isChecked()+" "+swFlash.isChecked()+" "+swVibration.isChecked());
                startService(intent);
                Toast.makeText(this, "Service is started!", Toast.LENGTH_SHORT).show();
            }

        } else {
            isStart = false;
            imbtnToggle.setImageResource(R.drawable.image_button_off);
            Intent intent = new Intent(MainActivity.this, ClapService.class);
            stopService(intent);
            Toast.makeText(this, "Service is stopped!", Toast.LENGTH_SHORT).show();

        }
    }


    private void addControls() {
        swRingtone = (Switch) findViewById(R.id.swPlayRingtone);
        swFlash = (Switch) findViewById(R.id.swFlash);
        swVibration = (Switch) findViewById(R.id.swVibrate);
        imbtnToggle = (ImageButton) findViewById(R.id.imbtnToggle);
        setting = new ShareReferencesManager(this);
        isStart = setting.getIsOnStatus();
    }


    @Override
    protected void onResume() {
        super.onResume();
        swRingtone.setChecked(setting.getRingtoneStatus());
        swFlash.setChecked(setting.getFlashStatus());
        swVibration.setChecked(setting.getVibrationStatus());
        isStart = setting.getIsOnStatus();
        if (isStart) {
            imbtnToggle.setImageResource(R.drawable.image_button_on);
        } else imbtnToggle.setImageResource(R.drawable.image_button_off);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setting.saveSetting(swRingtone.isChecked(), swFlash.isChecked(), swVibration.isChecked(), isStart);
    }

    public boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }else return true;
        }return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (requestCode == PERMISSION_CODE)) {
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "This app need permission to run, please restart and accept permission!", Toast.LENGTH_SHORT).show();
            isStart = false;
            imbtnToggle.setImageResource(R.drawable.image_button_off);
        }
    }
}

