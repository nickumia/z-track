package com.csc285.android.z_track.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.location.Location;

import com.csc285.android.z_track.Event;
import com.csc285.android.z_track.Statistics.Elevation;
import com.csc285.android.z_track.Statistics.LocationA;
import com.csc285.android.z_track.Statistics.Velocity;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.UUID;

/**
 *
 * Created by nick on 10/31/2017.
 */

public class EventCursorWrapper extends CursorWrapper
{
    public EventCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Event getEventD() {
        String uuidString = getString(getColumnIndex(EventDbSchema.EventTable.Cols.UUID));
        String a_type = getString(getColumnIndex(EventDbSchema.EventTable.Cols.A_TYPE));
        long date = getLong(getColumnIndex(EventDbSchema.EventTable.Cols.DATE));
//        long time = getInt(getColumnIndex(EventDbSchema.EventTable.Cols.TIME));
        float distance = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.DISTANCE));
        float pace = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.PACE));
//        float elevation = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.ELEVATION));
        float top_velocity = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.TOP_VELOCITY));
        float avg_velocity = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.AVG_VELOCITY));
        double start_location_lat = getDouble(getColumnIndex(EventDbSchema.EventTable.Cols.START_LOC_LAT));
        double start_location_lon = getDouble(getColumnIndex(EventDbSchema.EventTable.Cols.START_LOC_LON));
        double end_location_lat = getDouble(getColumnIndex(EventDbSchema.EventTable.Cols.END_LOC_LAT));
        double end_location_lon = getDouble(getColumnIndex(EventDbSchema.EventTable.Cols.END_LOC_LON));
//        float marker_location_lat = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.MARKER_LOC));
        int time_m = getInt(getColumnIndex(EventDbSchema.EventTable.Cols.TIME_M));
        int time_s = getInt(getColumnIndex(EventDbSchema.EventTable.Cols.TIME_S));
        int time_ms = getInt(getColumnIndex(EventDbSchema.EventTable.Cols.TIME_MS));


        Event event = new Event(UUID.fromString(uuidString));
        event.setmDate(new Date(date));
        event.setAcType(a_type);
        event.setmStats(distance, EventDbSchema.EventTable.Cols.DISTANCE);
        event.setmStats(pace, EventDbSchema.EventTable.Cols.PACE);
        event.setmStats(top_velocity, EventDbSchema.EventTable.Cols.TOP_VELOCITY);
        event.setmStats(avg_velocity, EventDbSchema.EventTable.Cols.AVG_VELOCITY);
        event.setmStats(start_location_lat, EventDbSchema.EventTable.Cols.START_LOC_LAT);
        event.setmStats(start_location_lon, EventDbSchema.EventTable.Cols.START_LOC_LON);
        event.setmStats(end_location_lat, EventDbSchema.EventTable.Cols.END_LOC_LAT);
        event.setmStats(end_location_lon, EventDbSchema.EventTable.Cols.END_LOC_LON);
        event.setmStats(time_m, EventDbSchema.EventTable.Cols.TIME_M);
        event.setmStats(time_s, EventDbSchema.EventTable.Cols.TIME_S);
        event.setmStats(time_ms, EventDbSchema.EventTable.Cols.TIME_MS);

        return event;
    }

    public Velocity getVelocity() {
//        String uuidString = getString(getColumnIndex(EventDbSchema.VelocityTable.Cols.UUID));
        int num = getInt(getColumnIndex(EventDbSchema.VelocityTable.Cols.NUM));
        float velo = getFloat(getColumnIndex(EventDbSchema.VelocityTable.Cols.VEL));
        double heading = getDouble(getColumnIndex(EventDbSchema.VelocityTable.Cols.HEADING));

        // Array of floats

        Velocity vel = new Velocity();
        vel.setVelocities(velo, num);
        vel.setHeading(heading, num);

        return vel;
    }

    public Elevation getElevation() {
//        String uuidString = getString(getColumnIndex(EventDbSchema.ElevationTable.Cols.UUID));
        int num = getInt(getColumnIndex(EventDbSchema.ElevationTable.Cols.NUM));
        double elev = getDouble(getColumnIndex(EventDbSchema.ElevationTable.Cols.ELEVATION));
        // Array of floats

        Elevation ele = new Elevation();
        ele.setElevation(elev, num);

        return ele;
    }

    public LocationA getMarkers() {
//        String uuidString = getString(getColumnIndex(EventDbSchema.MarkerTable.Cols.UUID));
        double lat = getDouble(getColumnIndex(EventDbSchema.MarkerTable.Cols.LATITUDE));
        double lon = getDouble(getColumnIndex(EventDbSchema.MarkerTable.Cols.LONGITUDE));
        String photo = getString(getColumnIndex(EventDbSchema.MarkerTable.Cols.PHOTO));
        String info = getString(getColumnIndex(EventDbSchema.MarkerTable.Cols.INFO));
        // Array of doubles
        // Array of doubles

        LocationA loc = new LocationA();
        Location a = new Location("dum");
        a.setLatitude(lat);
        a.setLongitude(lon);
        loc.addMarkers(a, info, photo);

        return loc;
    }

    public LocationA getPath() {
        String uuidString = getString(getColumnIndex(EventDbSchema.PathTable.Cols.UUID));
        int num = getInt(getColumnIndex(EventDbSchema.PathTable.Cols.NUM));
        double lat = getDouble(getColumnIndex(EventDbSchema.PathTable.Cols.LATITUDE));
        double lon = getDouble(getColumnIndex(EventDbSchema.PathTable.Cols.LONGITUDE));
        // Array of doubles
        // Array of doubles

        LocationA loc = new LocationA();
        LatLng a = new LatLng(lat, lon);
        loc.addToPath(a, num);

        return loc;
    }

    public Event getSharedRoute(){
        String uuidString = getString(getColumnIndex(EventDbSchema.SharingTable.Cols.UUID));
        long date = getLong(getColumnIndex(EventDbSchema.SharingTable.Cols.DATE));
        int visited = getInt(getColumnIndex(EventDbSchema.SharingTable.Cols.VISITED));
        float distance = getFloat(getColumnIndex(EventDbSchema.SharingTable.Cols.DISTANCE));
        double start_location_lat = getDouble(getColumnIndex(EventDbSchema.SharingTable.Cols.START_LOC_LAT));
        double start_location_lon = getDouble(getColumnIndex(EventDbSchema.SharingTable.Cols.START_LOC_LON));

        Event event = new Event(UUID.fromString(uuidString));
        event.setmDate(new Date(date));
        event.setVisited(visited);
        event.setmStats(distance, EventDbSchema.EventTable.Cols.DISTANCE);
        event.setmStats(start_location_lat, EventDbSchema.EventTable.Cols.START_LOC_LAT);
        event.setmStats(start_location_lon, EventDbSchema.EventTable.Cols.START_LOC_LON);

        return event;
    }
}
