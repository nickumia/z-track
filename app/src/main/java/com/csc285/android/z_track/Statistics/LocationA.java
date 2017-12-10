package com.csc285.android.z_track.Statistics;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Class to track location events
 */

public class LocationA extends Statistics {

    private Location current = new Location("dum");
    private Location start = new Location("dum");
    private Location end = new Location("dum");
    private ArrayList<Location> markers = new ArrayList<>();
    private ArrayList<String> markers_photo = new ArrayList<>();
    private ArrayList<LatLng> path = new ArrayList<>();
    private ArrayList<String> marker_time = new ArrayList<>();

    public LocationA(){
        current.setLatitude(0);
        current.setLongitude(0);
        start.setLatitude(0);
        start.setLongitude(0);
        end.setLatitude(0);
        end.setLongitude(0);
//        markers.add(start);
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
        if (start != null) {
            this.start = start;
//            markers.add(start);
        }
    }

    public void setStart(double lat, double lon){
        start.setLatitude(lat);
        start.setLongitude(lon);
    }

    public Location getEnd() {
        return end;
    }

    public void setEnd(Location end) {
        this.end = end;
    }

    public void setEnd(double lat, double lon){
        end.setLatitude(lat);
        end.setLongitude(lon);
    }

    public ArrayList<Location> getMarkers() {
        return markers;
    }

    public ArrayList<String> getMarkerTitles() {return marker_time; }

    public void addMarkers(Location markers, String time, String photo) {
        this.markers.add(markers);
        this.marker_time.add(time);
        this.markers_photo.add(photo);
    }

    public ArrayList<String> getMarkers_photo() {
        return markers_photo;
    }

    public void setMarkers_photo(String markers_photo) {
        this.markers_photo.add(markers_photo);
    }

    public ArrayList<LatLng> getPath() {
        return path;
    }

    public void addToPath(LatLng pathPoint) {
        this.path.add(pathPoint);
    }

    public void addToPath(LatLng pathPoint, int i) {
        this.path.add(i, pathPoint);
    }
}
