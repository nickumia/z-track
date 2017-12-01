package com.csc285.android.z_track;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csc285.android.z_track.database.EventBaseHelper;
import com.csc285.android.z_track.database.EventCursorWrapper;
import com.csc285.android.z_track.database.EventDbSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by nicku on 11/17/2017.
 */

public class EventLab {
    private static EventLab sStatisticsLab;

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

    public List<Event> getEvents() {
        List<Event> crimes = new ArrayList<>();
        EventCursorWrapper cursor = queryCrimes(null,
                null);
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

    public Event getEvent(UUID id) {
        EventCursorWrapper cursor = queryCrimes(
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

    private EventCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
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
}
