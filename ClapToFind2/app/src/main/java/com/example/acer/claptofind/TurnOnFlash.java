package com.example.acer.claptofind;

import android.hardware.Camera;


public class TurnOnFlash {
    Camera mCamera;
    Camera.Parameters mParams;
    int delay = 100;
    boolean on = false;

    public TurnOnFlash(){
        mCamera = Camera.open();
        mParams = mCamera.getParameters();
    }
    /*public void blinkFlash(boolean checkFlash){
        for(int i=0; i< 5; i++){
            if(!checkFlash){
                turnOff();
                break;
            }
            //toggleFlashLight();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        turnOff();
    }
*/

    public void turnOn() {
        if (mCamera != null) {
            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mParams);
            mCamera.startPreview();
            on = true;
        }
    }

    public void turnOff() {

        if (mCamera != null) {
            //mParams = mCamera.getParameters();
            if (mParams.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                mParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(mParams);
                mCamera.stopPreview();
            }
        }
        on = false;
    }

    /*public void toggleFlashLight() {
        if (!on) {
            turnOn();
        } else {
            turnOff();
        }
    }*/
}
