package com.csc285.android.z_track.Statistics;

import java.util.ArrayList;

/**
 * Created by nicku on 11/26/2017.
 */

public class Location extends Statistics {

    private Location current;
    private Location start;
    private Location end;
    private ArrayList<Location> markers;

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
