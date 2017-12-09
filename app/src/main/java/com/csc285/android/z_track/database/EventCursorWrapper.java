package com.csc285.android.z_track.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.csc285.android.z_track.Event;
import com.csc285.android.z_track.Statistics.Elevation;
import com.csc285.android.z_track.Statistics.LocationA;
import com.csc285.android.z_track.Statistics.Velocity;

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

    public Event getEvent() {
        String uuidString = getString(getColumnIndex(EventDbSchema.EventTable.Cols.UUID));
        String a_type = getString(getColumnIndex(EventDbSchema.EventTable.Cols.A_TYPE));
        long date = getLong(getColumnIndex(EventDbSchema.EventTable.Cols.DATE));
//        long time = getInt(getColumnIndex(EventDbSchema.EventTable.Cols.TIME));
        float distance = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.DISTANCE));
        float pace = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.PACE));
        float elevation = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.ELEVATION));
        float top_velocity = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.TOP_VELOCITY));
        float velocity = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.VELOCITY));
        double start_location_lat = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.START_LOC_LAT));
        double start_location_lon = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.START_LOC_LON));
        double end_location_lat = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.END_LOC_LAT));
        double end_location_lon = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.END_LOC_LON));
        float marker_location_lat = getFloat(getColumnIndex(EventDbSchema.EventTable.Cols.MARKER_LOC));
        int time_m = getInt(getColumnIndex(EventDbSchema.EventTable.Cols.TIME_M));
        int time_s = getInt(getColumnIndex(EventDbSchema.EventTable.Cols.TIME_S));
        int time_ms = getInt(getColumnIndex(EventDbSchema.EventTable.Cols.TIME_MS));

        Event event = new Event(UUID.fromString(uuidString));
        event.setmDate(new Date(date));
        event.setAcType(a_type);
//        event.setmTime(time);
        event.setmStats(distance, EventDbSchema.EventTable.Cols.DISTANCE);
        event.setmStats(pace, EventDbSchema.EventTable.Cols.PACE);
//        event.setmStats(elevation, EventDbSchema.EventTable.Cols.ELEVATION);
        event.setmStats(top_velocity, EventDbSchema.EventTable.Cols.TOP_VELOCITY);
//        event.setmStats(velocity, EventDbSchema.EventTable.Cols.VELOCITY);
        event.setmStats(start_location_lat, EventDbSchema.EventTable.Cols.START_LOC_LAT);
        event.setmStats(start_location_lon, EventDbSchema.EventTable.Cols.START_LOC_LON);
        event.setmStats(end_location_lat, EventDbSchema.EventTable.Cols.END_LOC_LAT);
        event.setmStats(end_location_lon, EventDbSchema.EventTable.Cols.END_LOC_LON);
//        event.setmStats(marker_location_lat, EventDbSchema.EventTable.Cols.MARKER_LOC);
        event.setmStats(time_m, EventDbSchema.EventTable.Cols.TIME_M);
        event.setmStats(time_s, EventDbSchema.EventTable.Cols.TIME_S);
        event.setmStats(time_ms, EventDbSchema.EventTable.Cols.TIME_MS);

        return event;
    }

    public Velocity getVelocity() {
        String uuidString = getString(getColumnIndex(EventDbSchema.VelocityTable.Cols.UUID));
        // Array of floats

        Velocity vel = new Velocity();

        return vel;
    }

    public Elevation getElevation() {
        String uuidString = getString(getColumnIndex(EventDbSchema.ElevationTable.Cols.UUID));
        // Array of floats

        Elevation ele = new Elevation();

        return ele;
    }

    public LocationA getMarkers() {
        String uuidString = getString(getColumnIndex(EventDbSchema.MarkerTable.Cols.UUID));
        String uuidString2 = getString(getColumnIndex(EventDbSchema.MarkerTable.Cols.UUID));
        // Array of doubles
        // Array of doubles

        LocationA loc = new LocationA();

        return loc;
    }
}
