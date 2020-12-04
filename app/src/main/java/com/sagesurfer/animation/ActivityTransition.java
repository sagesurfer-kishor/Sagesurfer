package com.sagesurfer.animation;

import android.app.ActivityOptions;
import android.content.Context;
import android.os.Bundle;

import com.sagesurfer.collaborativecares.R;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

/*
 * This class contain animation for activity transition that is animation which user can see
 * when user move from one page to another
 */

public class ActivityTransition {

    public static Bundle moveToNextAnimation(Context _context) {
        return ActivityOptions.makeCustomAnimation(_context, R.anim.animation_one, R.anim.animation_two).toBundle();
    }
}
