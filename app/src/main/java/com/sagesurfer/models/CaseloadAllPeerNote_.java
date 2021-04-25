package com.sagesurfer.models;

import com.google.gson.annotations.SerializedName;
import com.sagesurfer.constant.General;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 9/10/2019.
 */
public class CaseloadAllPeerNote_ implements Serializable {

    @SerializedName(General.START)
    private long start;

    @SerializedName(General.COUNT)
    private long count;

    private ArrayList<CaseloadPeerNote_> caseloadPeerNotes = new ArrayList<>();

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public ArrayList<CaseloadPeerNote_> getCaseloadPeerNotes() {
        return caseloadPeerNotes;
    }

    public void setCaseloadPeerNotes(ArrayList<CaseloadPeerNote_> caseloadPeerNotes) {
        this.caseloadPeerNotes = caseloadPeerNotes;
    }
}
