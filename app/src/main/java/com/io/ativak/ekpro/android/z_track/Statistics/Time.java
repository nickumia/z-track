package com.io.ativak.ekpro.android.z_track.Statistics;

import android.os.SystemClock;

/**
 * Created by nicku on 11/17/2017.
 */

public class Time extends Statistics {

    private long offcialSTime = 0L;
    private int officialTimeM = 0;
    private int officialTimeS = 0;
    private int officialTimeMS = 0;
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

    public int[] getOfficialTime() {
        return new int[]{officialTimeM, officialTimeS, officialTimeMS};
    }

    public void setOfficialTime() {
        officialTimeM = timeMinutes;
        officialTimeS = timeSeconds;
        officialTimeMS = timeMilli;
    }

    public void setOfficialTime(int t[]) {
        officialTimeM = t[0];
        officialTimeS = t[1];
        officialTimeMS = t[2];
    }

    public void setOfficialTimeM(int t) {
        officialTimeM = t;
    }

    public void setOfficialTimeS(int t) {
        officialTimeS = t;
    }

    public void setOfficialTimeMS(int t) {
        officialTimeMS = t;
    }

    public int getOfficialTimeM() {
        return officialTimeM;
    }

    public int getOfficialTimeS() {
        return officialTimeS;
    }

    public int getOfficialTimeMS() {
        return officialTimeMS;
    }

    public long getOfficialSTime() {
        return offcialSTime;
    }

    public void setOfficialSTime(long s) {
        this.offcialSTime = s;
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
