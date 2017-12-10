package com.csc285.android.z_track.Statistics;

import com.csc285.android.z_track.MainActivity;
import com.google.android.gms.maps.model.LatLng;

/**
 * Class to track total distance traveled
 */

public class Distance extends Statistics {

    private double totalDistanceEN = 0;
    private double totalDistanceSI = 0;

    public void clear(){
        totalDistanceEN = 0;
        totalDistanceSI = 0;
    }

    public double getTotalDistance() {
        if (MainActivity.UNIT.equals("SI")) {
            return totalDistanceSI;
        } else {
            return totalDistanceEN;
        }
    }

    public void setTotalDistance(double totalDistance) {
        if (MainActivity.UNIT.equals("SI")) {
            this.totalDistanceSI = totalDistance;
        } else {
            this.totalDistanceEN = totalDistance;
        }
    }

    public void updateDistance(double part){
        if (MainActivity.UNIT.equals("SI")) {
            this.totalDistanceSI = part;
        } else {
            this.totalDistanceEN = part;
        }
    }

    // Algorithm to calculate distance between two GeoPoints
    // Adapted from :
    // https://www.movable-type.co.uk/scripts/latlong.html
    public double  getDistanceFromLatLonInKm(LatLng a, LatLng b) {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(b.latitude-a.latitude);  // deg2rad below
        double dLon = deg2rad(b.longitude-a.longitude);
        double aa = Math.sin(dLat/2) *
                Math.sin(dLat/2) +
                Math.cos(deg2rad(a.longitude)) *
                Math.cos(deg2rad(b.latitude)) *
                Math.sin(dLon/2) *
                Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(aa), Math.sqrt(1-aa));
        return R * c; // Distance in km
    }

    public double kmToMi(double num){
        return 0.621371*num;
    }

    double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }
}
