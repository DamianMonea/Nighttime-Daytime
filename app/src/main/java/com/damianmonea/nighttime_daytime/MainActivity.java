package com.damianmonea.nighttime_daytime;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mySensorManager;
    private Sensor mSensorLight;
    private TextView label;
    private boolean Nighttime;
    private ConstraintLayout myLayout;
    private TransitionDrawable bgTransition;
    private MediaPlayer nighttime1, daytime1, nighttime2, daytime2, nighttime3, daytime3;
    private Random randomizer = new Random();

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

        nighttime1 = MediaPlayer.create(this, R.raw.nighttime1);
        daytime1 = MediaPlayer.create(this, R.raw.daytime1);
        nighttime2 = MediaPlayer.create(this, R.raw.nighttime2);
        daytime2 = MediaPlayer.create(this, R.raw.daytime2);
        nighttime3 = MediaPlayer.create(this, R.raw.nighttime3);
        daytime3 = MediaPlayer.create(this, R.raw.daytime3);
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
        int index;
        if( event.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            currentLux = event.values[0];
        }
        if(currentLux < 50 && Nighttime == false){
            Nighttime = true;
            index = randomizer.nextInt(3);
            switch(index){
                case 0: nighttime1.start(); break;
                case 1: nighttime2.start(); break;
                case 2: nighttime3.start(); break;
            }
            label.setText(R.string.nighttime);
            label.setTextColor(Color.WHITE);
            bgTransition.startTransition(300);

        }
        if(currentLux > 50 && Nighttime == true){
            Nighttime = false;
            index = randomizer.nextInt(3);
            switch(index){
                case 0: daytime1.start(); break;
                case 1: daytime2.start(); break;
                case 2: daytime3.start(); break;
            }
            label.setText(R.string.daytime);
            label.setTextColor(Color.BLACK);
            bgTransition.reverseTransition(300);

        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy){

    }
}
