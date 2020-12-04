package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Monika on 6/12/2018.
 */

public class TeamCountersAll_ implements Serializable {

    @SerializedName(General.COUNTER)
    private List<TeamCounters_> counter;

    public List<TeamCounters_> getCounter() {
        return counter;
    }

    public void setCounter(List<TeamCounters_> counter) {
        this.counter = counter;
    }
}
