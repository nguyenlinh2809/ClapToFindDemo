package com.example.acer.claptofind;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ACER on 10/30/2017.
 */

public class ShareReferencesManager {
    public static String SHAREREFERENCES_NAME = "setting";
    public static String RINGTONE_STATUS = "ringtone";
    public static String FLASH_STATUS = "flash";
    public static String VIBRATION_STATUS = "vibration";
    public static String ISON_STATUS = "ison";
    Context context;
    SharedPreferences sharedPreferences;
    public ShareReferencesManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHAREREFERENCES_NAME, Context.MODE_PRIVATE);
    }
    public void saveSetting(boolean ringTone, boolean flash, boolean vibration, boolean isStart){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(RINGTONE_STATUS, ringTone);
        editor.putBoolean(FLASH_STATUS, flash);
        editor.putBoolean(VIBRATION_STATUS, vibration);
        editor.putBoolean(ISON_STATUS, isStart);
        editor.apply();
    }

    public boolean getRingtoneStatus(){
        return sharedPreferences.getBoolean(RINGTONE_STATUS, false);
    }
    public boolean getFlashStatus(){
        return sharedPreferences.getBoolean(FLASH_STATUS, false);
    }
    public boolean getVibrationStatus(){
        return sharedPreferences.getBoolean(VIBRATION_STATUS, false);
    }
    public boolean getIsOnStatus(){
        return sharedPreferences.getBoolean(ISON_STATUS, false);
    }
}
