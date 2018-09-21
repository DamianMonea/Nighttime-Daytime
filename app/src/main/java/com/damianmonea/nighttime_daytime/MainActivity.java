package com.damianmonea.nighttime_daytime;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mySensorManager;
    private Sensor mSensorLight;
    private TextView label;
    private boolean Nighttime, canChangeStatus, firstTime;
    private ConstraintLayout myLayout;
    private TransitionDrawable bgTransition;
    private MediaPlayer nighttime1, daytime1, nighttime2, daytime2, nighttime3, daytime3;
    private Random randomizer = new Random();
    private Window window;
    private float brightness;
    private int index;
    private long lastTime, currentTime, timeDifference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myLayout = findViewById(R.id.constraintLayout);
        label = (TextView) findViewById(R.id.textView);
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorLight = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        bgTransition = (TransitionDrawable) myLayout.getBackground();
        bgTransition.reverseTransition(1);

        if(mSensorLight == null){
            label.setText(R.string.sensor_error);
        }
        Nighttime = true;
        canChangeStatus = true;
        firstTime = true;

        nighttime1 = MediaPlayer.create(this, R.raw.nighttime1);
        daytime1 = MediaPlayer.create(this, R.raw.daytime1);
        nighttime2 = MediaPlayer.create(this, R.raw.nighttime2);
        daytime2 = MediaPlayer.create(this, R.raw.daytime2);
        nighttime3 = MediaPlayer.create(this, R.raw.nighttime3);
        daytime3 = MediaPlayer.create(this, R.raw.daytime3);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canChangeStatus = true;
            window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        currentTime = System.currentTimeMillis();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mSensorLight != null){
            mySensorManager.registerListener(this, mSensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mySensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float currentLux = 0;
        brightness = event.values[0];
        lastTime = currentTime;
        currentTime = System.currentTimeMillis();
        timeDifference = currentTime - lastTime;
        if( event.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            currentLux = event.values[0];
        }
        if(currentLux < 50 && Nighttime == false && timeDifference > 30){
            Nighttime = true;
            index = randomizer.nextInt(3);
            if(firstTime == true)
                firstTime = false;
            if(canChangeStatus == true){
                window.setStatusBarColor(Color.WHITE);
            }
            switch(index){
                case 0: nighttime1.start(); break;
                case 1: nighttime2.start(); break;
                case 2: nighttime3.start(); break;
            }
            label.setText(R.string.nighttime);
            label.setTextColor(Color.WHITE);
            bgTransition.startTransition(300);

        }
        if(currentLux > 50 && Nighttime == true && timeDifference > 30){
            Nighttime = false;
            if(firstTime == true)
                firstTime = false;
            index = randomizer.nextInt(3);
            if(canChangeStatus == true){
                window.setStatusBarColor(Color.BLACK);
            }
            switch(index){
                case 0: daytime1.start(); break;
                case 1: daytime2.start(); break;
                case 2: daytime3.start(); break;
            }
            label.setText(R.string.daytime);
            label.setTextColor(Color.BLACK);
            bgTransition.reverseTransition(300);
        }
        if(currentLux < 50 && Nighttime == true && firstTime == true){
            Nighttime = true;
            firstTime = false;
            index = randomizer.nextInt(3);
            if(canChangeStatus == true){
                window.setStatusBarColor(Color.WHITE);
            }
            switch(index){
                case 0: nighttime1.start(); break;
                case 1: nighttime2.start(); break;
                case 2: nighttime3.start(); break;
            }
            label.setText(R.string.nighttime);
            label.setTextColor(Color.WHITE);
            bgTransition.startTransition(300);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        startService(new Intent(this, MyService.class));
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        stopService(new Intent(this, MyService.class));
    }
}
