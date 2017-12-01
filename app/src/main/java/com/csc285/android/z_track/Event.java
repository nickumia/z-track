package com.csc285.android.z_track;

import com.csc285.android.z_track.Statistics.Distance;
import com.csc285.android.z_track.Statistics.Elevation;
import com.csc285.android.z_track.Statistics.Location;
import com.csc285.android.z_track.Statistics.Pace;
import com.csc285.android.z_track.Statistics.Statistics;
import com.csc285.android.z_track.Statistics.Time;
import com.csc285.android.z_track.Statistics.Velocity;

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
    private ArrayList<Statistics> mStats = new ArrayList<>();

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

        Statistics stat7 = new Location();
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


}
