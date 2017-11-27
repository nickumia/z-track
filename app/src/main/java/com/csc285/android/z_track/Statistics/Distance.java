package com.csc285.android.z_track.Statistics;

/**
 * Created by nicku on 11/17/2017.
 */

public class Distance extends Statistics {

    private float totalDistance = 0;

    public float getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(float totalDistance) {
        this.totalDistance = totalDistance;
    }

    public void updateDistance(float part){
        this.totalDistance += part;
    }
}
