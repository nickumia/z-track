package com.csc285.android.z_track.Statistics;

import android.location.Location;

import com.csc285.android.z_track.MainActivity;

/**
 * Class to track total distance traveled
 */

public class Distance extends Statistics {

    private float totalDistanceEN = 0;
    private float totalDistanceSI = 0;

    public float getTotalDistance() {
        if (MainActivity.UNIT.equals("SI")) {
            return totalDistanceSI;
        } else {
            return totalDistanceEN;
        }
    }

    public void setTotalDistance(float totalDistance) {
        if (MainActivity.UNIT.equals("SI")) {
            this.totalDistanceSI = totalDistance;
        } else {
            this.totalDistanceEN = totalDistance;
        }
    }

    public void updateDistance(float part){
        if (MainActivity.UNIT.equals("SI")) {
            this.totalDistanceSI = part;
        } else {
            this.totalDistanceEN = part;
        }
    }

    // Algorithm to calculate distance between two GeoPoints
    // Adapted from :
    // https://www.movable-type.co.uk/scripts/latlong.html
    public double  getDistanceFromLatLonInKm(Location a, Location b) {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(b.getLatitude()-a.getLatitude());  // deg2rad below
        double dLon = deg2rad(b.getLongitude()-a.getLongitude());
        double aa = Math.sin(dLat/2) *
                Math.sin(dLat/2) +
                Math.cos(deg2rad(a.getLongitude())) *
                Math.cos(deg2rad(b.getLatitude())) *
                Math.sin(dLon/2) *
                Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(aa), Math.sqrt(1-aa));
        return R * c; // Distance in km
    }

    double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }
}
