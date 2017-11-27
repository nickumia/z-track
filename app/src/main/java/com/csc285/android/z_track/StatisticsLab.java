package com.csc285.android.z_track;

import android.content.Context;

import com.csc285.android.z_track.Statistics.Distance;
import com.csc285.android.z_track.Statistics.Elevation;
import com.csc285.android.z_track.Statistics.Pace;
import com.csc285.android.z_track.Statistics.Statistics;
import com.csc285.android.z_track.Statistics.Time;
import com.csc285.android.z_track.Statistics.Velocity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicku on 11/17/2017.
 */

public class StatisticsLab {
    private static StatisticsLab sStatisticsLab;
    private List<Statistics> mStats;

    private Integer[] Labels = {
            R.string.activity_item_time,
            R.string.activity_item_pace,
            R.string.activity_item_topspeed,
            R.string.activity_item_avgspeed,
            R.string.activity_item_elevation
    };

    public static StatisticsLab get(Context context) {
        if (sStatisticsLab == null) {
            sStatisticsLab = new StatisticsLab(context);
        }
        return sStatisticsLab;
    }
    private StatisticsLab(Context context) {
        mStats = new ArrayList<>();

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

        mStats.add(stat4);
        mStats.add(stat3);
        mStats.add(stat5);
        mStats.add(stat6);
        mStats.add(stat1);
        mStats.add(stat2);
    }

    public List<Statistics> getmStats() {
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
