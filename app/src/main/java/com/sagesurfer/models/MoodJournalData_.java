package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.util.ArrayList;

/**
 * Created by Monika on 11/14/2018.
 */

public class MoodJournalData_ {
    @SerializedName(General.MOOD)
    private ArrayList<MoodJournalDataMood_> mood;

    public ArrayList<MoodJournalDataMood_> getMood() {
        return mood;
    }

    public void setMood(ArrayList<MoodJournalDataMood_> mood) {
        this.mood = mood;
    }
}
