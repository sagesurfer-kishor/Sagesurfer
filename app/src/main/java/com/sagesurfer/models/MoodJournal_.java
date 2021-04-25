package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Monika on 11/13/2018.
 */

public class MoodJournal_ implements Serializable {
    @SerializedName(General.DATA)
    private ArrayList<MoodJournalData_> data;

    public ArrayList<MoodJournalData_> getData() {
        return data;
    }

    public void setData(ArrayList<MoodJournalData_> data) {
        this.data = data;
    }
}
