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

    public void addMarkers(ArrayList<Location> markers, ArrayList<String> time, ArrayList<String> photo) {
        this.markers = markers;
        this.marker_time = time;
        this.markers_photo = photo;
    }

    public ArrayList<String> getMarkers_photo() {
        return markers_photo;
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

// If location address is needed
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//
//        try {
//            addresses = geocoder.getFromLocation(latitude, longitude, 1);
//        } catch (IOException e) {
//            //  Auto-generated catch block
//            e.printStackTrace();
//        }
//        String cityName = addresses.get(0).getAddressLine(0);
//        String stateName = addresses.get(0).getAddressLine(1);
//        String countryName = addresses.get(0).getAddressLine(2);
//
//        String[] splittedStateName = stateName.split(",");
//        requiredArea = splittedStateName[2];
//        Log.d("iFocus", "The value of required area is " + requiredArea);
//
//        city = addresses.get(0).getLocality();
//        area = addresses.get(0).getSubLocality();
//        String adminArea = addresses.get(0).getAdminArea();
//        String premises = addresses.get(0).getPremises();
//        String subAdminArea = addresses.get(0).getSubAdminArea();
//        String featureName = addresses.get(0).getFeatureName();
//        String phone = addresses.get(0).getPhone();
//        country = addresses.get(0).getCountryName();
//
//        SharedPreferences sharedPreferences = getSharedPreferences("MyValues", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("CITY", cityName);
//        editor.putString("STATE", stateName);
//        editor.putString("COUNTRY", countryName);
//        editor.commit();
//
//        TextView mapTitle = (TextView) findViewById(R.id.textViewTitle);
//
//        if (requiredArea != "" && city != "" && country != "") {
//            title = mLastUpdateTime.concat(", " + requiredArea).concat(", " + city).concat(", " + country);
//        }
//        else {
//            title = mLastUpdateTime.concat(", " + area).concat(", " + city).concat(", " + country);
//        }
//        mapTitle.setText(title);

//        drawPath();// newly added