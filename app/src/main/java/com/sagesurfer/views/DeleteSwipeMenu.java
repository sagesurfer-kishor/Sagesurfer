package com.sagesurfer.views;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;

import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.sagesurfer.collaborativecares.R;

/**
 * Created by girish on 14/08/16.
 */
public class DeleteSwipeMenu implements SwipeMenuCreator {

    private Activity activity;

    public DeleteSwipeMenu(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void create(com.baoyz.swipemenulistview.SwipeMenu menu) {
        SwipeMenuItem deleteItem = new SwipeMenuItem(activity.getApplicationContext());
        deleteItem.setBackground(new ColorDrawable(activity.getApplicationContext()
                .getResources().getColor(R.color.colorPrimaryDark)));
        deleteItem.setWidth(dp2px(80));
        deleteItem.setIcon(R.drawable.ic_delete_white);
        menu.addMenuItem(deleteItem);

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                activity.getResources().getDisplayMetrics());
    }
}
