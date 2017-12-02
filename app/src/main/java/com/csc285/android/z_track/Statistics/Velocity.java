package com.csc285.android.z_track.Statistics;

import java.util.ArrayList;

/**
 * Created by nicku on 11/17/2017.
 */

public class Velocity extends Statistics{

    private float topVelocity = 0;
    private float avgVelocity = 0;

    private ArrayList<Float> velocity = new ArrayList<>();

    public float getTopVelocity() {
        return topVelocity;
    }

    public void setTopVelocity(float topVelocity) {
        this.topVelocity = topVelocity;
    }

    public float getAvgVelocity() {
        return avgVelocity;
    }

    public void setAvgVelocity(float avgVelocity) {
        this.avgVelocity = avgVelocity;
    }

    public ArrayList<Float> getVelocity() {
        return velocity;
    }

    public void setVelocity(ArrayList<Float> velocity) {
        this.velocity = velocity;
    }
}
