package com.modules.caseload.observer;

import android.content.Context;

import java.util.Observable;

public class AppObserver extends Observable {
    private int nStatusType;
    Context context;

    public AppObserver(Context context) {
        this.context = context;
    }

    public void setValue(int nStatusTyp) {
        this.nStatusType = nStatusTyp;
        setChanged();
        notifyObservers();
    }

    public int getValue() {
        return nStatusType;
    }
}

