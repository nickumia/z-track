package com.csc285.android.z_track;

import com.csc285.android.z_track.Statistics.Distance;
import com.csc285.android.z_track.Statistics.Elevation;
import com.csc285.android.z_track.Statistics.LocationA;
import com.csc285.android.z_track.Statistics.Pace;
import com.csc285.android.z_track.Statistics.Statistics;
import com.csc285.android.z_track.Statistics.Time;
import com.csc285.android.z_track.Statistics.Velocity;
import com.csc285.android.z_track.database.EventDbSchema;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by nicku on 11/27/2017.
 *
 * Event
 *      ID      :   Unique Identifier
 *      Date    :   MM/DD/YYYY
 *      Time    :   Start Time
 *      Stats   :
 *          ID      :   Unique Identifier (Time, Distance, Pace ...)
 *          Units   :   Units
 *          Idx     :   Index of ID in stats array
 *          ...     :   Unique Statistic Information
 *
 */

public class Event {

    private UUID mId;
    private Date mDate;
    private long mTime;
    private String acType = "misc";
    private ArrayList<Statistics> mStats = new ArrayList<>();
    private ArrayList<String> markerPhotoFileNames = new ArrayList<>();

    static double slat, slon, elat, elon = -1;

    Event(){
        this(UUID.randomUUID());
    }

    public Event(UUID id) {
        mId = id;
        mDate = new Date();

        Statistics stat1 = new Distance();
        stat1.setId(R.string.activity_item_distance);
        stat1.setIdx(1);

        Statistics stat2 = new Elevation();
        stat2.setId(R.string.activity_item_elevation);
        stat2.setIdx(4);

        Statistics stat3 = new Pace();
        stat3.setId(R.string.activity_item_pace);
        stat3.setIdx(3);

        Statistics stat4 = new Time();
        stat4.setId(R.string.activity_item_time);
        stat4.setIdx(0);

        Statistics stat5 = new Velocity();
        stat5.setId(R.string.activity_item_topspeed);
        stat5.setIdx(2);

        Statistics stat6 = new Velocity();
        stat6.setId(R.string.activity_item_avgspeed);
        stat6.setIdx(2);

        Statistics stat7 = new LocationA();
        stat7.setId(R.string.activity_item_location);
        stat7.setIdx(5);

        mStats.add(stat4);
        mStats.add(stat3);
        mStats.add(stat5);
        mStats.add(stat6);
        mStats.add(stat1);
        mStats.add(stat2);
        mStats.add(stat7);
    }

    public UUID getmId() {
        return mId;
    }

    public void setmId(UUID mName) {
        this.mId = mName;
    }

    public String getAcType() {
        return acType;
    }

    public void setAcType(String acType) {
        this.acType = acType;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public long getmTime() {
        return mTime;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }

    public ArrayList<Statistics> getmStats() {
        return mStats;
    }

    public Statistics getStat(Integer id) {
        for (Statistics stat : mStats) {
            if (stat.getId().equals(id)) {
                return stat;
            }
        }
        return null;
    }

    public void setmStats(Object stat, String type){
        for (Statistics s : mStats){
            if (s instanceof Distance) {
                if (type.equals(EventDbSchema.EventTable.Cols.DISTANCE)) {
                    ((Distance) s).setTotalDistance((float) stat);
                }
            }

            if (s instanceof Pace) {
                if (type.equals(EventDbSchema.EventTable.Cols.PACE)) {
                    ((Pace) s).setPace((float) stat);
                }
            }

            if (s instanceof Velocity) {
                if (type.equals(EventDbSchema.EventTable.Cols.TOP_VELOCITY)) {
                    ((Velocity) s).setTopVelocity((float) stat);
                }

            }

            if (s instanceof LocationA) {
                if (type.equals(EventDbSchema.EventTable.Cols.START_LOC_LAT)) {
                    slat = (double) stat;
                }
                if (type.equals(EventDbSchema.EventTable.Cols.START_LOC_LON)) {
                    slon = (double) stat;
                }
                if (slat != -1 && slon != -1){
                    ((LocationA) s).setStart(slat, slon);
                }

                if (type.equals(EventDbSchema.EventTable.Cols.END_LOC_LAT)) {
                    elat = (double) stat;
                }
                if (type.equals(EventDbSchema.EventTable.Cols.END_LOC_LON)) {
                    elon = (double) stat;
                }
                if (elat != -1 && elon != -1){
                    ((LocationA) s).setEnd(elat, elon);
                }
            }

            if (s instanceof Time){
                if (type.equals(EventDbSchema.EventTable.Cols.TIME_M)){
                    ((Time) s).setOfficialTimeM((int) stat);
                }
                if (type.equals(EventDbSchema.EventTable.Cols.TIME_S)){
                    ((Time) s).setOfficialTimeS((int) stat);
                }
                if (type.equals(EventDbSchema.EventTable.Cols.TIME_MS)){
                    ((Time) s).setOfficialTimeMS((int) stat);
                }
            }
        }
    }

    public void setmStats(Object stat, int type){
        for (Statistics s : mStats){
            if (s instanceof Distance) {
                if (type == R.string.activity_item_distance) {
                    ((Distance) s).setTotalDistance(((Distance) stat).getTotalDistance());
                }
            }

            if (s instanceof Pace) {
                if (type == R.string.activity_item_pace) {
                    ((Pace) s).setPace(((Pace) stat).getPace());
                }
            }

            if (s instanceof Velocity) {
                if (type == R.string.activity_item_topspeed) {
                    ((Velocity) s).setTopVelocity(((Velocity) stat).getTopVelocity());
                }
//                if (type == R.string.activity_item_topspeed) {
//                    ((Velocity) s).setAvgVelocity((float) stat);
//                }
            }

            // EventDbSchema.EventTable.Cols.START_LOC_LAT
            if (s instanceof LocationA) {
                if (type == R.string.activity_item_location){
                    ((LocationA) s).setStart(((LocationA) stat).getStart());
                    ((LocationA) s).setEnd(((LocationA) stat).getEnd());
                }
            }

            //(EventDbSchema.EventTable.Cols.START_TIME)
            if (s instanceof Time){
                if (type == R.string.activity_item_time){
                    ((Time) s).setOfficialSTime(((Time) stat).getOfficialSTime());
                    ((Time) s).setOfficialTime(((Time) stat).getOfficialTime());
                    ((Time) s).setEndTime(((Time) stat).getEndTime());
                }
            }
        }
    }

    public void addPhotoFilename(int idx) {
        String a = "IMG_" + getmId().toString() + idx + ".jpg";
        markerPhotoFileNames.add(idx,a);
    }

    public String getPhotoFilename(int idx) {
        return markerPhotoFileNames.get(idx);
    }

    public int getPhotoSize(){
        return markerPhotoFileNames.size();
    }

    public ArrayList<String> getMarkers(){
        return markerPhotoFileNames;
    }


}
