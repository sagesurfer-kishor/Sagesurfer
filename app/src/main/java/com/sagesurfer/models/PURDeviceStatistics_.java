package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;

/**
 * Created by Monika on 8/6/2018.
 */

//PlatformUsageReportDeviceStatistics_
public class PURDeviceStatistics_ implements Serializable {
    @SerializedName(General.MOB_USAGE_LAST_MONTH)
    private long mob_usage_last_month;

    @SerializedName(General.TOT_MOB_USAGE)
    private long tot_mob_usage;

    @SerializedName(General.DAILY_TIME_PLATFORM)
    private float daily_time_platform;

    @SerializedName(General.AVG_TIME_TEAM)
    private float avg_time_team;

    @SerializedName(General.TIME_SPEND_PER_TEAM)
    private float time_spend_per_team;

    @SerializedName(General.STATUS)
    private int status;

    public long getMob_usage_last_month() {
        return mob_usage_last_month;
    }

    public void setMob_usage_last_month(long mob_usage_last_month) {
        this.mob_usage_last_month = mob_usage_last_month;
    }

    public long getTot_mob_usage() {
        return tot_mob_usage;
    }

    public void setTot_mob_usage(long tot_mob_usage) {
        this.tot_mob_usage = tot_mob_usage;
    }

    public float getDaily_time_platform() {
        return daily_time_platform;
    }

    public void setDaily_time_platform(float daily_time_platform) {
        this.daily_time_platform = daily_time_platform;
    }

    public float getAvg_time_team() {
        return avg_time_team;
    }

    public void setAvg_time_team(float avg_time_team) {
        this.avg_time_team = avg_time_team;
    }

    public float getTime_spend_per_team() {
        return time_spend_per_team;
    }

    public void setTime_spend_per_team(float time_spend_per_team) {
        this.time_spend_per_team = time_spend_per_team;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
