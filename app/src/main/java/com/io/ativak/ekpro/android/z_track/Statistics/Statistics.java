package com.io.ativak.ekpro.android.z_track.Statistics;

/**
 * Created by nick on 11/17/2017.
 *
 */

public class Statistics {

    private Integer id;
    private String units;
    private int idx;

    public Statistics(){
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }
}