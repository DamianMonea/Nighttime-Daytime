package com.damianmonea.nighttime_daytime;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Random;

public class MyService extends Service implements SensorEventListener{
    private SensorManager mySensorManager;
    private Sensor mSensorLight;
    private MediaPlayer nighttime1, daytime1, nighttime2, daytime2, nighttime3, daytime3;
    private Random randomizer = new Random();
    private boolean Nighttime, firstTime;
    private long lastTime, currentTime, timeDifference;
    private int index;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorLight = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        firstTime = true;

        nighttime1 = MediaPlayer.create(this, R.raw.nighttime1);
        daytime1 = MediaPlayer.create(this, R.raw.daytime1);
        nighttime2 = MediaPlayer.create(this, R.raw.nighttime2);
        daytime2 = MediaPlayer.create(this, R.raw.daytime2);
        nighttime3 = MediaPlayer.create(this, R.raw.nighttime3);
        daytime3 = MediaPlayer.create(this, R.raw.daytime3);
        if(mSensorLight != null){
            mySensorManager.registerListener(this, mSensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        }

        currentTime = System.currentTimeMillis();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mySensorManager.unregisterListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float currentLux = 0;
        lastTime = currentTime;
        currentTime = System.currentTimeMillis();
        timeDifference = currentTime - lastTime;
        if( event.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            currentLux = event.values[0];
        }
        if(firstTime == true){
            firstTime = false;
            if (currentLux < 50) {
                Nighttime = true;
            }
            else
            {
                Nighttime = false;
            }
        }
        if(currentLux < 50 && Nighttime == false && timeDifference > 30){
            Nighttime = true;
            index = randomizer.nextInt(3);
            if(firstTime == true)
                firstTime = false;
            switch(index){
                case 0: nighttime1.start(); break;
                case 1: nighttime2.start(); break;
                case 2: nighttime3.start(); break;
            }
        }
        if(currentLux > 50 && Nighttime == true && timeDifference > 30){
            Nighttime = false;
            if(firstTime == true)
                firstTime = false;
            index = randomizer.nextInt(3);
            switch(index){
                case 0: daytime1.start(); break;
                case 1: daytime2.start(); break;
                case 2: daytime3.start(); break;
            }
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy){

    }
}
