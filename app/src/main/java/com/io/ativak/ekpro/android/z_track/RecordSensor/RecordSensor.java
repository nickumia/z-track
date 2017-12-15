package com.io.ativak.ekpro.android.z_track.RecordSensor;

import java.util.ArrayList;

/**
 * Created by nick on 12/1/2017.
 *
 * Sensor Fusion Attempt
 */

public class RecordSensor {

    private long lastUpdate;
    private float avg = 0;
    private ArrayList<Float> sources = new ArrayList<>();

    public float getAvg() {
        return avg;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setValue(Float value) {
        this.sources.add(value);
        if (this.sources.size() > 10){
            this.sources.remove(0);
            averageValues();
        }
    }

    public void averageValues(){
        for (Float a : this.sources){
            avg += a;
        }

        avg /= 10;
    }



}
