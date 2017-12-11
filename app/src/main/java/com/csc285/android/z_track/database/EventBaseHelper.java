package com.csc285.android.z_track.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * Created by nick on 10/31/2017.
 */

public class EventBaseHelper extends SQLiteOpenHelper
{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "eventBase.db";

    public EventBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + EventDbSchema.EventTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                EventDbSchema.EventTable.Cols.UUID + ", " +
                EventDbSchema.EventTable.Cols.DATE + ", " +
                EventDbSchema.EventTable.Cols.A_TYPE + ", " +
//                EventDbSchema.EventTable.Cols.TIME + ", " +
                EventDbSchema.EventTable.Cols.DISTANCE + ", " +
                EventDbSchema.EventTable.Cols.PACE + ", " +
//                EventDbSchema.EventTable.Cols.ELEVATION + ", " +
                EventDbSchema.EventTable.Cols.TOP_VELOCITY + ", " +
                EventDbSchema.EventTable.Cols.AVG_VELOCITY + ", " +
                EventDbSchema.EventTable.Cols.START_LOC_LAT + ", " +
                EventDbSchema.EventTable.Cols.START_LOC_LON + ", " +
                EventDbSchema.EventTable.Cols.END_LOC_LAT + ", " +
                EventDbSchema.EventTable.Cols.END_LOC_LON + ", " +
//                EventDbSchema.EventTable.Cols.MARKER_LOC + ", " +
                EventDbSchema.EventTable.Cols.TIME_M + ", " +
                EventDbSchema.EventTable.Cols.TIME_S + ", " +
                EventDbSchema.EventTable.Cols.TIME_MS +
                ")"
        );

        db.execSQL("create table " + EventDbSchema.VelocityTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                EventDbSchema.VelocityTable.Cols.UUID + ", " +
                EventDbSchema.VelocityTable.Cols.NUM + ", " +
                EventDbSchema.VelocityTable.Cols.VEL + ", " +
                EventDbSchema.VelocityTable.Cols.HEADING +
//                EventDbSchema.VelocityTable.Cols.PACE +
                ")"
        );

        db.execSQL("create table " + EventDbSchema.ElevationTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                EventDbSchema.ElevationTable.Cols.UUID + ", " +
                EventDbSchema.ElevationTable.Cols.NUM + ", " +
                EventDbSchema.ElevationTable.Cols.ELEVATION +
                ")"
        );

        db.execSQL("create table " + EventDbSchema.MarkerTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                EventDbSchema.MarkerTable.Cols.UUID + ", " +
                EventDbSchema.MarkerTable.Cols.LATITUDE + ", " +
                EventDbSchema.MarkerTable.Cols.LONGITUDE + ", " +
                EventDbSchema.MarkerTable.Cols.PHOTO + ", " +
                EventDbSchema.MarkerTable.Cols.INFO +
                ")"
        );

        db.execSQL("create table " + EventDbSchema.PathTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                EventDbSchema.PathTable.Cols.UUID + ", " +
                EventDbSchema.PathTable.Cols.NUM + ", " +
                EventDbSchema.PathTable.Cols.LATITUDE + ", " +
                EventDbSchema.PathTable.Cols.LONGITUDE +
                ")"
        );

        db.execSQL("create table " + EventDbSchema.SharingTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                EventDbSchema.SharingTable.Cols.UUID + ", " +
                EventDbSchema.SharingTable.Cols.DATE + ", " +
                EventDbSchema.SharingTable.Cols.DISTANCE + ", " +
                EventDbSchema.SharingTable.Cols.VISITED + ", " +
                EventDbSchema.SharingTable.Cols.RATING + ", " +
                EventDbSchema.SharingTable.Cols.START_LOC_LAT + ", " +
                EventDbSchema.SharingTable.Cols.START_LOC_LON +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}