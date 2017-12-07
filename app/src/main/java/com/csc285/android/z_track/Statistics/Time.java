package com.csc285.android.z_track.Statistics;

import android.os.SystemClock;

/**
 * Created by nicku on 11/17/2017.
 */

public class Time extends Statistics {

    private long officialSTime = 0L;
    private long startTime = 0L;
    private long endTime = 0L;
    private long startTimeOLD = 0L;
    private long endTimeOLD = 0L;
    private long deltaTime = 0L;
    private int timeMilli = 0;
    private int timeSeconds = 0;
    private int timeMinutes = 0;
    private int timeHours = 0;

    public String getQTime(){
        return Long.toString(deltaTime);
    }

    public void setQTime(String q){
        deltaTime = Long.valueOf(q);
    }

    public void updateTime(){
        setTimeSeconds((int) ((getCurrentTime()-getStartTime()+getDeltaTime()) / 1000));
        setTimeMinutes(getTimeSeconds() / 60);
        setTimeSeconds(getTimeSeconds() % 60);
        setTimeMilli((int) ((getCurrentTime()-getStartTime()+getDeltaTime()) % 1000));
    }

    public long getCurrentTime() {
        return SystemClock.uptimeMillis();
    }

    public long getOfficialSTime() {
        return officialSTime;
    }

    public void setOfficialSTime() {
        this.officialSTime = SystemClock.uptimeMillis();
    }

    public void setOfficialSTime(long sT){
        this.officialSTime = sT;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime() {
        this.startTimeOLD = this.startTime;
        this.startTime = SystemClock.uptimeMillis();
    }

    public int getTimeSeconds() {
        return timeSeconds;
    }

    public void setTimeSeconds(int seconds) {
        this.timeSeconds = seconds;
    }

    public int getTimeMinutes() {
        return timeMinutes;
    }

    public void setTimeMinutes(int minutes) {
        this.timeMinutes = minutes;
    }

    public int getTimeHours() {
        return timeHours;
    }

    public void setTimeHours(int timeHours) {
        this.timeHours = timeHours;
    }

    public int getTimeMilli() {
        return timeMilli;
    }

    public void setTimeMilli(int milli) {
        this.timeMilli = milli;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime() {
        this.endTimeOLD = this.endTime;
        this.endTime = SystemClock.uptimeMillis();
    }

    public void setEndTime(long eT){
        this.endTime = eT;
    }

    public long getDeltaTime() {
        return deltaTime;
    }

    private long getOldDeltaTime(){
        return this.endTimeOLD - this.startTimeOLD;
    }

    public void addDeltaTime() {
        if (getOldDeltaTime() - (getEndTime() - getStartTime()) != 0 ||
                (getOldDeltaTime() - (getCurrentTime() - getStartTime()) < 1)) {
            this.deltaTime += getEndTime() - getStartTime();
        }
    }

    public void resetDeltaTime() {
        this.deltaTime = 0L;
    }
}
