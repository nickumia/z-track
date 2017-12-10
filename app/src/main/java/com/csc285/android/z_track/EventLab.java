package com.csc285.android.z_track;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.csc285.android.z_track.Statistics.Distance;
import com.csc285.android.z_track.Statistics.Elevation;
import com.csc285.android.z_track.Statistics.LocationA;
import com.csc285.android.z_track.Statistics.Pace;
import com.csc285.android.z_track.Statistics.Time;
import com.csc285.android.z_track.Statistics.Velocity;
import com.csc285.android.z_track.database.EventBaseHelper;
import com.csc285.android.z_track.database.EventCursorWrapper;
import com.csc285.android.z_track.database.EventDbSchema;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Singleton holding current Event
 */

public class EventLab {
    
    private static EventLab sStatisticsLab;
    private Event tempEvent;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private Integer[] Labels = {
            R.string.activity_item_time,
            R.string.activity_item_pace,
            R.string.activity_item_topspeed,
            R.string.activity_item_avgspeed,
            R.string.activity_item_distance,
            R.string.activity_item_elevation,
            R.string.activity_item_location
    };

    public static EventLab get(Context context) {
        if (sStatisticsLab == null) {
            sStatisticsLab = new EventLab(context);
        }
        return sStatisticsLab;
    }
    private EventLab(Context context) {

        mContext = context.getApplicationContext();
        mDatabase = new EventBaseHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(Event event) {
        ContentValues values = new ContentValues();
        values.put(EventDbSchema.EventTable.Cols.UUID, event.getmId().toString());
        values.put(EventDbSchema.EventTable.Cols.DATE, event.getmDate().getTime());
        values.put(EventDbSchema.EventTable.Cols.A_TYPE, event.getAcType());

        Distance d = (Distance) event.getStat(R.string.activity_item_distance);
        values.put(EventDbSchema.EventTable.Cols.DISTANCE, d.getTotalDistance());

        Pace p = (Pace) event.getStat(R.string.activity_item_pace);
        values.put(EventDbSchema.EventTable.Cols.PACE, p.getPace());

        Velocity v = (Velocity) event.getStat(R.string.activity_item_topspeed);
        values.put(EventDbSchema.EventTable.Cols.TOP_VELOCITY, v.getTopVelocity());
        values.put(EventDbSchema.EventTable.Cols.AVG_VELOCITY, v.getAvgVelocity());

        LocationA l = (LocationA) event.getStat(R.string.activity_item_location);
        values.put(EventDbSchema.EventTable.Cols.START_LOC_LAT, l.getStart().getLatitude());
        values.put(EventDbSchema.EventTable.Cols.START_LOC_LON, l.getStart().getLongitude());
        values.put(EventDbSchema.EventTable.Cols.END_LOC_LAT, l.getEnd().getLatitude());
        values.put(EventDbSchema.EventTable.Cols.END_LOC_LON, l.getEnd().getLongitude());

        Time t = (Time) event.getStat(R.string.activity_item_time);
        values.put(EventDbSchema.EventTable.Cols.TIME_M, t.getOfficialTimeM());
        values.put(EventDbSchema.EventTable.Cols.TIME_S, t.getOfficialTimeS());
        values.put(EventDbSchema.EventTable.Cols.TIME_MS, t.getOfficialTimeMS());

        return values;
    }

    private static ContentValues getElevationContentValues(Event event, int i, double elev) {
        ContentValues values = new ContentValues();
        values.put(EventDbSchema.ElevationTable.Cols.UUID, event.getmId().toString());
        values.put(EventDbSchema.ElevationTable.Cols.NUM, i);
        values.put(EventDbSchema.ElevationTable.Cols.ELEVATION, elev);

        return values;
    }

    private static ContentValues getVelocityContentValues(Event event, int i, float vel) {
        ContentValues values = new ContentValues();
        values.put(EventDbSchema.VelocityTable.Cols.UUID, event.getmId().toString());
        values.put(EventDbSchema.VelocityTable.Cols.NUM, i);
        values.put(EventDbSchema.VelocityTable.Cols.VEL, vel);

        return values;
    }

    private static ContentValues getMarkerContentValues(Event event, double lat, double lon, String pName, String info) {
        ContentValues values = new ContentValues();
        values.put(EventDbSchema.MarkerTable.Cols.UUID, event.getmId().toString());
        values.put(EventDbSchema.MarkerTable.Cols.LATITUDE, lat);
        values.put(EventDbSchema.MarkerTable.Cols.LONGITUDE, lon);
        values.put(EventDbSchema.MarkerTable.Cols.PHOTO, pName);
        values.put(EventDbSchema.MarkerTable.Cols.INFO, info);

        return values;
    }

    private static ContentValues getPathContentValues(Event event, int i, double lat, double lon) {
        ContentValues values = new ContentValues();
        values.put(EventDbSchema.PathTable.Cols.UUID, event.getmId().toString());
        values.put(EventDbSchema.PathTable.Cols.NUM, i);
        values.put(EventDbSchema.PathTable.Cols.LATITUDE, lat);
        values.put(EventDbSchema.PathTable.Cols.LONGITUDE, lon);

        return values;
    }

    void addEvent(Event c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(EventDbSchema.EventTable.NAME, null, values);

        Elevation e = (Elevation) c.getStat(R.string.activity_item_elevation);
        for(int i = 0; i<e.getElevation().size(); i++) {
            ContentValues val = getElevationContentValues(c, i, e.getElevation().get(i));
            mDatabase.insert(EventDbSchema.ElevationTable.NAME, null, val);
        }

        Velocity v = (Velocity) c.getStat(R.string.activity_item_avgspeed);
        for(int i = 0; i<v.getVelocities().size(); i++) {
            ContentValues val = getVelocityContentValues(c, i, v.getVelocities().get(i));
            mDatabase.insert(EventDbSchema.VelocityTable.NAME, null, val);
        }

        LocationA l = (LocationA) c.getStat(R.string.activity_item_location);
        for(int i = 0; i<l.getMarkers().size(); i++) {
            Location lT = l.getMarkers().get(i);
            ContentValues val = getMarkerContentValues(c, lT.getLatitude(), lT.getLongitude(), c.getPhotoFilename(i), l.getMarkerTitles().get(i));
            mDatabase.insert(EventDbSchema.MarkerTable.NAME, null, val);
        }

        LocationA l2 = (LocationA) c.getStat(R.string.activity_item_location);
        for(int i = 0; i<l2.getPath().size(); i++) {
            LatLng lT = l.getPath().get(i);
            ContentValues val = getPathContentValues(c, i, lT.latitude, lT.longitude);
            mDatabase.insert(EventDbSchema.PathTable.NAME, null, val);
        }
    }

    void updateEvent(Event event) {
        // Update Based on multiple columns
        // https://stackoverflow.com/questions/32152966/update-sqlite-with-multiple-column-values
        String uuidString = event.getmId().toString();
        ContentValues values = getContentValues(event);
        mDatabase.update(EventDbSchema.EventTable.NAME, values,
                EventDbSchema.EventTable.Cols.UUID + " = ?",
                new String[] { uuidString });

        Elevation e = (Elevation) event.getStat(R.string.activity_item_elevation);
        for(int i = 0; i<e.getElevation().size(); i++) {
            ContentValues val = getElevationContentValues(event, i, e.getElevation().get(i));
            mDatabase.update(EventDbSchema.ElevationTable.NAME, val,
                    EventDbSchema.ElevationTable.Cols.UUID + " = ? AND " + EventDbSchema.ElevationTable.Cols.NUM + " = ?",
                    new String[] { uuidString, Integer.toString(i) });
        }

        Velocity v = (Velocity) event.getStat(R.string.activity_item_avgspeed);
        for(int i = 0; i<v.getVelocities().size(); i++) {
            ContentValues val = getVelocityContentValues(event, i, v.getVelocities().get(i));
            mDatabase.update(EventDbSchema.VelocityTable.NAME, val,
                    EventDbSchema.VelocityTable.Cols.UUID + " = ? AND " + EventDbSchema.VelocityTable.Cols.NUM + " = ?",
                    new String[] { uuidString, Integer.toString(i) });
        }

        LocationA l = (LocationA) event.getStat(R.string.activity_item_location);
        for(int i = 0; i<l.getMarkers().size(); i++) {
            Location lT = l.getMarkers().get(i);
            ContentValues val = getMarkerContentValues(event, lT.getLatitude(), lT.getLongitude(), event.getPhotoFilename(i), l.getMarkerTitles().get(i));
            mDatabase.update(EventDbSchema.MarkerTable.NAME, val,
                    EventDbSchema.MarkerTable.Cols.UUID + " = ?" ,
                    new String[] { uuidString });
        }

        LocationA l2 = (LocationA) event.getStat(R.string.activity_item_location);
        for(int i = 0; i<l2.getPath().size(); i++) {
            LatLng lT = l.getPath().get(i);
            ContentValues val = getPathContentValues(event, i, lT.latitude, lT.longitude);
            mDatabase.update(EventDbSchema.PathTable.NAME, val,
                    EventDbSchema.PathTable.Cols.UUID + " = ? AND " + EventDbSchema.PathTable.Cols.NUM + " = ?",
                    new String[] { uuidString, Integer.toString(i) });
        }

    }

    boolean Exists(String id)
    {
        String query = "SELECT " + EventDbSchema.EventTable.Cols.UUID +
                " FROM " + EventDbSchema.EventTable.NAME +
                " WHERE " + EventDbSchema.EventTable.Cols.UUID + " =?";
        Cursor cursor = mDatabase.rawQuery(query, new String[]{id});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    List<Event> getEvents() {
        List<Event> events = new ArrayList<>();
        EventCursorWrapper cursor = queryEvents(EventDbSchema.EventTable.NAME, null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UUID id = cursor.getEventD().getmId();
                events.add(getEvent(id));
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return events;
    }

    File getPhotoFile(Event event, int idx) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, event.getPhotoFilename(idx));
    }

    Event getEvent(UUID id) {
        Event event = new Event(id);
        EventCursorWrapper cursor = queryEvents(
                EventDbSchema.EventTable.NAME,
                EventDbSchema.EventTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            event = cursor.getEventD();
        } finally {
            cursor.close();
        }

        EventCursorWrapper cursorVel = queryEvents(
                EventDbSchema.VelocityTable.NAME,
                EventDbSchema.VelocityTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursorVel.getCount() != 0) {
                cursorVel.moveToFirst();
                event.setmStats(cursorVel.getVelocity(), R.string.activity_item_speed);
            }
        } finally {
            cursor.close();
        }

        EventCursorWrapper cursorElev = queryEvents(
                EventDbSchema.VelocityTable.NAME,
                EventDbSchema.VelocityTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursorElev.getCount() != 0) {
                cursorElev.moveToFirst();
                event.setmStats(cursorElev.getElevation(), R.string.activity_item_elevation);
            }
        } finally {
            cursor.close();
        }

        EventCursorWrapper cursorMark = queryEvents(
                EventDbSchema.MarkerTable.NAME,
                EventDbSchema.MarkerTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursorMark.getCount() != 0) {
                cursorMark.moveToFirst();
                event.setmStats(cursorMark.getMarkers(), R.string.activity_item_markers);
            }
        } finally {
            cursor.close();
        }

        EventCursorWrapper cursorPath = queryEvents(
                EventDbSchema.PathTable.NAME,
                EventDbSchema.PathTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursorPath.getCount() != 0) {
                cursorPath.moveToFirst();
                event.setmStats(cursorPath.getPath(), R.string.activity_item_path);
            }
        } finally {
            cursor.close();
        }

        return event;
    }

    private EventCursorWrapper queryEvents(String tableName, String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                tableName,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );
        return new EventCursorWrapper(cursor);
    }


    Event getTempEvent() {
        return tempEvent;
    }

    void setTempEvent(Event tempEvent) {
        this.tempEvent = tempEvent;
    }
}
