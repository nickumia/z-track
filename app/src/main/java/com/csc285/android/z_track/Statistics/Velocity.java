package com.csc285.android.z_track.Statistics;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class to track velocity and heading
 */

public class Velocity extends Statistics{

    private float topVelocity = 0;
    private float avgVelocity = 0;
    private ArrayList<Double> heading = new ArrayList<>();
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

    public void calculateAvg(){
        float sum = 0;
        for(int i=0; i< velocity.size(); i++){
            sum += velocity.get(i);
        }
        this.avgVelocity = sum/velocity.size();
    }

    public ArrayList<Float> getVelocities() {
        return velocity;
    }

    public void setVelocities(float velocity, int i) {
        try {
            this.velocity.add(i, velocity);
        } catch (IndexOutOfBoundsException ie){
            this.velocity.add(velocity);
        }
    }

    public void setVelocity(float vel)  {
        this.velocity.add(vel);
        this.topVelocity = Collections.max(this.velocity);
    }

    public void setVelocities(ArrayList<Float> velocity) {
        this.velocity = velocity;
    }

    public ArrayList<Double> getHeading() {
        return heading;
    }

    public double getLatestHeading() {
        if (heading.size() > 0){
            return heading.get(heading.size()-1);
        } else {
            return 0.0;
        }
    }

    public void setHeading(ArrayList<Double> h){
        this.heading = h;
    }

    public void setHeading(double heading) {
        this.heading.add(heading);
    }

    public void setHeading(double heading, int i){
        try {
            this.heading.add(i, heading);
        } catch (IndexOutOfBoundsException ie) {
            this.heading.add(heading);
        }
    }

}
