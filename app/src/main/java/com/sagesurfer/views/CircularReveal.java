package com.sagesurfer.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
 import android.view.View;
import android.view.ViewAnimationUtils;

import androidx.annotation.RequiresApi;

import com.sagesurfer.collaborativecares.R;

/**
 * @author Monika M(monikam@sagesurfer.com)
 * Created on 4/3/2018
 * Last Modified on 4/3/2018
 */

public class CircularReveal {

    @SuppressLint("PrivateResource")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void circleReveal(int viewID, int posFromRight, boolean containsOverflow,
                                    final boolean isShow, Activity activity) {
        final View myView = activity.findViewById(viewID);

        int width = myView.getWidth();

        if (posFromRight > 0)
            width -= (posFromRight * activity.getApplicationContext().getResources()
                    .getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (activity.getApplicationContext().getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);
        if (containsOverflow)
            width -= activity.getApplicationContext().getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx = width;
        int cy = myView.getHeight() / 2;

        Animator anim;
        if (isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, (float) width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float) width, 0);

        anim.setDuration((long) 220);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if (isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();


    }
}
