package com.modules.admin;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.sagesurfer.collaborativecares.R;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 19-08-2017
 * Last Modified on 13-12-2017
 */
/*
* This class is created to give appropriate drawable based on invitation status at individual
*/

class InviteAnalysis {

    static Drawable getCircle(String color, Context context) {
        if (color.equalsIgnoreCase("green_circle")) {
            return context.getResources().getDrawable(R.drawable.sos_attended_circle);
        }
        if (color.equalsIgnoreCase("yellow_circle")) {
            return context.getResources().getDrawable(R.drawable.yellow_circle);
        }
        if (color.equalsIgnoreCase("red_circle")) {
            return context.getResources().getDrawable(R.drawable.sos_delivered_circle);
        }
        return context.getResources().getDrawable(R.drawable.sos_grey_circle);
    }

    static int getLine(String color, Context context) {
        if (color.equalsIgnoreCase("green_circle")) {
            return context.getResources().getColor(R.color.sos_attending);
        }
        if (color.equalsIgnoreCase("yellow_circle")) {
            return context.getResources().getColor(R.color.invite_yellow);
        }
        if (color.equalsIgnoreCase("red_circle")) {
            return context.getResources().getColor(R.color.sos_delivered);
        }
        return context.getResources().getColor(R.color.sos_grey);
    }
}
