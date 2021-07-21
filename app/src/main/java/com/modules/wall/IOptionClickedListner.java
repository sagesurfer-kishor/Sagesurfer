package com.modules.wall;

import android.app.Activity;

import androidx.fragment.app.FragmentActivity;

public interface IOptionClickedListner {
    public void onEditClicked(Feed_ feed_, Activity activity, String position);
    public void onDeleteClicked(Feed_ feed_, FragmentActivity activity, String position);
    public void onCancelClicked();
}
