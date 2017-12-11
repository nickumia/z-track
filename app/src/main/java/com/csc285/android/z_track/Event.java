package com.csc285.android.z_track;

import com.csc285.android.z_track.Statistics.Distance;
import com.csc285.android.z_track.Statistics.Elevation;
import com.csc285.android.z_track.Statistics.LocationA;
import com.csc285.android.z_track.Statistics.Pace;
import com.csc285.android.z_track.Statistics.Rating;
import com.csc285.android.z_track.Statistics.Statistics;
import com.csc285.android.z_track.Statistics.Time;
import com.csc285.android.z_track.Statistics.Velocity;
import com.csc285.android.z_track.Statistics.Visit;
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
    private int visited = 0;
    private int rating = 0;
    private boolean scenic = false;
    private boolean shareMarkers = false;
    private ArrayList<Statistics> mStats = new ArrayList<>();
    private ArrayList<Statistics> mReview = new ArrayList<>();
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

//        Statistics stat9 = new Velocity();
//        stat9.setId(R.string.activity_item_speed);
//        stat9.setIdx(2);

        Statistics stat7 = new LocationA();
        stat7.setId(R.string.activity_item_location);
        stat7.setIdx(5);

        Statistics stat8 = new Velocity();
        stat8.setId(R.string.activity_item_heading);
        stat8.setIdx(6);

        Statistics stat12 = new Visit();
        stat12.setId(R.string.visit_title);
        stat12.setIdx(0);

        Statistics stat13 = new Rating();
        stat13.setId(R.string.rating_title);
        stat13.setIdx(0);

        Statistics stat14 = new Time();
        stat14.setId(R.string.activity_item_times);
        stat14.setIdx(0);

//        Statistics stat10 = new LocationA();
//        stat10.setId(R.string.activity_item_markers);
//        stat10.setIdx(5);
//
//        Statistics stat11 = new LocationA();
//        stat11.setId(R.string.activity_item_path);
//        stat11.setIdx(5);

        mStats.add(stat4);
        mStats.add(stat3);
        mStats.add(stat5);
        mStats.add(stat6);
        mStats.add(stat1);
        mStats.add(stat2);
        mStats.add(stat7);
        mStats.add(stat8);
//        mStats.add(stat9);
//        mStats.add(stat10);
//        mStats.add(stat11);

        mReview.add(stat7);
        mReview.add(stat14);
        mReview.add(stat12);
        mReview.add(stat4);
        mReview.add(stat1);
        mReview.add(stat13);

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

    public int getVisited() {
        return visited;
    }

    public void setVisited(int visited) {
        this.visited = visited;
    }

    public boolean isScenic() {
        return scenic;
    }

    public void setScenic(boolean scenic) {
        this.scenic = scenic;
    }

    public boolean isShareMarkers() {
        return shareMarkers;
    }

    public void setShareMarkers(boolean shareMarkers) {
        this.shareMarkers = shareMarkers;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
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

    public ArrayList<Statistics> getmReview() {
        return mReview;
    }

    public Statistics getStat(Integer id) {
        for (Statistics stat : mStats) {
            if (stat.getId().equals(id)) {
                return stat;
            }
        }
        return null;
    }

    public Statistics getReview(Integer id){
        for (Statistics stat : mReview) {
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

    public void setmReview(Object stat, int type) {
        for (Statistics s : mReview){
            if (s instanceof LocationA){
                if (type == R.string.activity_item_location)
                ((LocationA) s).setStart(((LocationA) stat).getStart());
            }

            if (s instanceof Distance){
                if (type == R.string.activity_item_distance) {
                    ((Distance) s).setTotalDistance(((Distance) stat).getTotalDistance());
                }
            }

            if (s instanceof Time) {
                if (type == R.string.activity_item_time || type == R.string.activity_item_times) {
                    ((Time) s).setOfficialSTime(((Time) stat).getOfficialSTime());
                    ((Time) s).setOfficialTime(((Time) stat).getOfficialTime());
                    ((Time) s).setEndTime(((Time) stat).getEndTime());
                }
            }

            if (s instanceof Rating) {
                if (type == R.string.rating_title){
                    ((Rating) s).setRating((float)(stat));
                }
            }

            if (s instanceof Visit) {
                if (type == R.string.visit_title){
                    ((Visit) s).setVisited(((Visit) stat).getVisited());
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
                if (type == R.string.activity_item_avgspeed) {
                    ((Velocity) s).setAvgVelocity(((Velocity) stat).getAvgVelocity());
                    ((Velocity) s).setVelocities(((Velocity) stat).getVelocities());
                }
                if (type == R.string.activity_item_heading) {
                    ((Velocity) s).setHeading(((Velocity) stat).getHeading());
                }
            }

            // EventDbSchema.EventTable.Cols.START_LOC_LAT
            if (s instanceof LocationA) {
                if (type == R.string.activity_item_location){
                    ((LocationA) s).setStart(((LocationA) stat).getStart());
                    ((LocationA) s).setEnd(((LocationA) stat).getEnd());
                    ((LocationA) s).addMarkers(
                            ((LocationA) stat).getMarkers(),
                            ((LocationA) stat).getMarkerTitles(),
                            ((LocationA) stat).getMarkers_photo());
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

            //System.out.println(mTracking.getMarkers_photo());
        }
    }

    public void setmStats(Object stat, int type, int i) {
        for (Statistics s : mStats) {
            if (s instanceof Velocity) {
                if (type == (R.string.activity_item_avgspeed)) {
                    if (i < ((Velocity) stat).getVelocities().size()) {
                        ((Velocity) s).setVelocities(((Velocity) stat).getVelocities().get(i), i);
                    }
                }
                if (type == R.string.activity_item_heading) {
                    if (i < ((Velocity) stat).getHeading().size()) {
                        ((Velocity) s).setHeading(((Velocity) stat).getHeading().get(i), i);
                    }
                }
            }

            if (s instanceof Elevation) {
                if (type == (R.string.activity_item_elevation)) {
                    ((Elevation) s).setElevation(((Elevation) stat).getElevation().get(i),i);
                }
            }

            if (s instanceof LocationA) {
                if (type == (R.string.activity_item_location)) {
                    ((LocationA) s).addMarkers(
                            ((LocationA) stat).getMarkers().get(i),
                            ((LocationA) stat).getMarkerTitles().get(i),
                            ((LocationA) stat).getMarkers_photo().get(i)
                    );
//                    ((LocationA) s).addToPath(
//                            ((LocationA) stat).getPath().get(i),i);
                }

                if (type == (R.string.activity_item_path)) {
                    ((LocationA) s).addToPath(
                            ((LocationA) stat).getPath().get(i),i);
                }
            }
        }
    }

    public void addPhotoFilename(int idx) {
        String a = "IMG_" + getmId().toString() + idx + ".jpg";
        markerPhotoFileNames.add(idx,a);
    }

    public String getPhotoFilename(int idx) {
        if (markerPhotoFileNames.size() != 0){
            return markerPhotoFileNames.get(idx);
        } else {
            return "";
        }
    }

    public int getPhotoSize(){
        return markerPhotoFileNames.size();
    }

    public ArrayList<String> getMarkers(){
        return markerPhotoFileNames;
    }


}
