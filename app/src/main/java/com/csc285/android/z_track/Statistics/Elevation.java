package com.csc285.android.z_track.Statistics;

import java.util.ArrayList;

/**
 * Class to hold Elevation stats
 */

public class Elevation extends Statistics {

    private ArrayList<Double> elevation = new ArrayList<>();
    private float elevationRate = 0;

    public ArrayList<Double> getElevation() {
        return elevation;
    }

    public Double getLatestElevation() {
        if (elevation.size() > 0) {
            return elevation.get(elevation.size() - 1);
        } else {
            return 0.0;
        }
    }

    public void setElevation(double elevation, int i) {
        this.elevation.add(i, elevation);
    }

    public void setElevation(double elevation) {
        this.elevation.add(elevation);
    }

    public float getElevationRate() {
        return elevationRate;
    }

    public void setElevationRate(float elevationRate) {
        this.elevationRate = elevationRate;
    }
}
