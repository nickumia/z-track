package com.csc285.android.z_track.Statistics;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by nicku on 11/26/2017.
 */

public class LocationA extends Statistics {

    private Location current = new Location("dum");
    private Location start = new Location("dum");
    private Location end = new Location("dum");
    private ArrayList<Location> markers = new ArrayList<>();

    public LocationA(){
        markers.add(start);
    }

    public Location getCurrent() {
        return current;
    }

    public void setCurrent(Location current) {
        this.current = current;
    }

    public Location getStart() {
        return start;
    }

    public void setStart(Location start) {
        this.start = start;
    }

    public Location getEnd() {
        return end;
    }

    public void setEnd(Location end) {
        this.end = end;
    }

    public ArrayList<Location> getMarkers() {
        return markers;
    }

    public void setMarkers(Location markers) {
        this.markers.add(markers);
    }
}
