package com.io.ativak.ekpro.android.z_track.RecordSensor;

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by nicku on 12/1/2017.
 */

public class Compass extends RecordSensor {

    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    public Compass(){}
    public Compass(SensorManager sm, SensorEventListener sel){

    }
}
