package com.csc285.android.z_track.Statistics;

import java.util.ArrayList;

/**
 * Created by nicku on 11/17/2017.
 */

public class Elevation extends Statistics {

    private ArrayList<Float> elevation = new ArrayList<>();
    private float elevationRate = 0;

    public ArrayList<Float> getElevation() {
        return elevation;
    }

    public void setElevation(float elevation, int i) {
        this.elevation.add(i, elevation);
    }

    public float getElevationRate() {
        return elevationRate;
    }

    public void setElevationRate(float elevationRate) {
        this.elevationRate = elevationRate;
    }
}
