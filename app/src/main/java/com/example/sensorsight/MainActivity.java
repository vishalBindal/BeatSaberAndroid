package com.example.sensorsight;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import android.view.KeyEvent;

public class MainActivity extends AppCompatActivity {

    Sensor rotationVectorSensor, gyroSensor;
    SensorManager sensorManager;
    float[] rotationMatrix = new float[16];
    float[] orientations = new float[3];
    float[] angVelocity = new float[3];
    float meanAzimuth;
    float length;
    boolean setMean;
    float netAngularVelocity;
    TextView text1, text2, text3, text4, text5, text6;
    float x, y;
    String azimuth, pitch, roll;
    long lastTime, deltaTime ;
    float[] lastAngVelocity = new float[3];
    float angAcc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(rotationListener, rotationVectorSensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(gyroListener, gyroSensor, sensorManager.SENSOR_DELAY_GAME);
        //SENSOR_DELAY_GAME can be changed accordingly to adjust time interval between sensor
        //readings

        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        text5 = findViewById(R.id.text5);
        text6 = findViewById(R.id.text6);

        meanAzimuth = 0;
        length = 20;
        //changing this would scale x,y proportionally
        setMean = false;
        lastTime = SystemClock.elapsedRealtime();
        for(int i=0;i<3;i++) lastAngVelocity[i] = 0;
    }

    SensorEventListener rotationListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.getOrientation(rotationMatrix, orientations);

            // orientations[0] = azimuth, orientations[1]= pitch , orientations[2] = roll
            //Units = rad
            x = length * (float) Math.sin(orientations[0] - meanAzimuth);
            //Since azimuth is angle from the plane perpendicular to ground and along North,
            // azimuth - meanAzimuth gives angle moved from mean position along horizontal
            // So x = const * sin of that angle
            y = length * (float) Math.sin(orientations[1]);
            //Since pitch is angle from plane parallel to ground,
            // y = const * sin of that angle
            y = y * (-1);
            // To make y positive when phone is tilted upwards
            //Units of x and y = unknown ( depend on units of variable "length")

            azimuth = "Azimuth: " + String.format("%.1f", Math.toDegrees(orientations[0] - meanAzimuth));
            pitch = "Pitch: " + String.format("%.1f", Math.toDegrees(orientations[1]));
            roll = "Roll: " + String.format("%.1f", Math.toDegrees(orientations[2]));

            if (setMean) {

                text1.setText(azimuth);
                text2.setText(pitch);
                text3.setText(roll);

                text4.setText("x: " + String.format("%.1f", x));
                text5.setText("y: " + String.format("%.1f", y));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    SensorEventListener gyroListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            angVelocity[0] = event.values[0];
            angVelocity[1] = event.values[1];
            angVelocity[2] = event.values[2];

            netAngularVelocity = (float) Math.sqrt(Math.pow(angVelocity[0], 2) +
                    Math.pow(angVelocity[1], 2) + Math.pow(angVelocity[2], 2));
            //resultant of angular velocity across all 3 axis
            //Units: rad/s

            deltaTime = SystemClock.elapsedRealtime() - lastTime;
            //t2=current time, change in time = t2 - t1
            // Value of deltaTime is approximately 20 (ms) for SENSOR_DELAY_GAME
            lastTime = SystemClock.elapsedRealtime();
            //updating t1 : t1=t2=current time

            if(deltaTime!=0) {
                angAcc = (float) (Math.sqrt(Math.pow(angVelocity[0] - lastAngVelocity[0], 2) +
                        Math.pow(angVelocity[1] - lastAngVelocity[1], 2)
                        + Math.pow(angVelocity[2] - lastAngVelocity[2], 2))) / deltaTime;
                angAcc *= 1000;
            }
            // calculating net angular acceleration using
            // (change in ang velocity) / (change in time)
            // Units : rad/(s^2)
            else
                angAcc = 0;

            System.arraycopy(angVelocity, 0, lastAngVelocity, 0, 3);
            //updating lastAngVelocity to current values

            if (netAngularVelocity >= 3 || angAcc>50) {
                getWindow().getDecorView().setBackgroundColor(Color.RED);
                text6.setText("SWING!!");
            }
            // The values 3 and 50 may be changed to adjust sensitivity
            else {
                getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                text6.setText("");
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            // setting mean value of Azimuth when either of volume keys are pressed
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    meanAzimuth = orientations[0];
                    setMean = true;
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    meanAzimuth = orientations[0];
                    setMean = true;
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }


    public void onResume() {
        super.onResume();
        sensorManager.registerListener(rotationListener, rotationVectorSensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(gyroListener, gyroSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(rotationListener);
        sensorManager.unregisterListener(gyroListener);
    }


}
