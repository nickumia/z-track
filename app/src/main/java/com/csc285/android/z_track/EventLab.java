package com.csc285.android.z_track;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csc285.android.z_track.Statistics.Distance;
import com.csc285.android.z_track.Statistics.Elevation;
import com.csc285.android.z_track.Statistics.LocationA;
import com.csc285.android.z_track.Statistics.Pace;
import com.csc285.android.z_track.Statistics.Time;
import com.csc285.android.z_track.Statistics.Velocity;
import com.csc285.android.z_track.database.EventBaseHelper;
import com.csc285.android.z_track.database.EventCursorWrapper;
import com.csc285.android.z_track.database.EventDbSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by nicku on 11/17/2017.
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
            R.string.activity_item_elevation
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
        values.put(EventDbSchema.EventTable.Cols.UUID,
                event.getmId().toString());
        values.put(EventDbSchema.EventTable.Cols.DATE,
                event.getmDate().getTime());

        Distance d = (Distance) event.getStat(R.string.activity_item_distance);
        values.put(EventDbSchema.EventTable.Cols.DISTANCE,
                d.getTotalDistance());

        Pace p = (Pace) event.getStat(R.string.activity_item_pace);
        values.put(EventDbSchema.EventTable.Cols.PACE,
                p.getPace());

        Elevation e = (Elevation) event.getStat(R.string.activity_item_elevation);
        values.put(EventDbSchema.EventTable.Cols.ELEVATION,
                e.getElevation());

        Velocity v = (Velocity) event.getStat(R.string.activity_item_topspeed);
        values.put(EventDbSchema.EventTable.Cols.TOP_VELOCITY,
                v.getTopVelocity());
        values.put(EventDbSchema.EventTable.Cols.VELOCITY,
                v.getTopVelocity());

        LocationA l = (LocationA) event.getStat(R.string.activity_item_location);
        values.put(EventDbSchema.EventTable.Cols.START_LOC_LAT,
                l.getStart().getLatitude());
        values.put(EventDbSchema.EventTable.Cols.START_LOC_LON,
                l.getStart().getLongitude());
        values.put(EventDbSchema.EventTable.Cols.END_LOC_LAT,
                l.getEnd().getLatitude());
        values.put(EventDbSchema.EventTable.Cols.END_LOC_LON,
                l.getEnd().getLongitude());
//        values.put(EventDbSchema.EventTable.Cols.MARKER_LOC,
//                l.getMarkers().get(0).getLongitude());

        Time t = (Time) event.getStat(R.string.activity_item_time);
        values.put(EventDbSchema.EventTable.Cols.START_TIME,
                t.getOfficialSTime());
        values.put(EventDbSchema.EventTable.Cols.END_TIME,
                t.getEndTime());

        return values;
    }

    void addEvent(Event c) {
//        mCrimes.add(c);
        ContentValues values = getContentValues(c);
        mDatabase.insert(EventDbSchema.EventTable.NAME, null, values);
    }

    public void updateEvent(Event event) {
        String uuidString = event.getmId().toString();
        ContentValues values = getContentValues(event);
        mDatabase.update(EventDbSchema.EventTable.NAME, values,
                EventDbSchema.EventTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    public List<Event> getEvents() {
        List<Event> crimes = new ArrayList<>();
        EventCursorWrapper cursor = queryEvents(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getEvent());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public File getPhotoFile(Event event, int idx) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir,
                event.getPhotoFilename(idx));
    }

    public Event getEvent(UUID id) {
        EventCursorWrapper cursor = queryEvents(
                EventDbSchema.EventTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getEvent();
        } finally {
            cursor.close();
        }
    }

    private EventCursorWrapper queryEvents(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                EventDbSchema.EventTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );
        return new EventCursorWrapper(cursor);
    }

    public Event getTempEvent() {
        return tempEvent;
    }

    public void setTempEvent(Event tempEvent) {
        this.tempEvent = tempEvent;
    }
}
