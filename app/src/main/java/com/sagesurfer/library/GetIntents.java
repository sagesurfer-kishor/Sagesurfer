package com.sagesurfer.library;

import android.content.Context;
import android.content.Intent;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

/*
* This class return intent respective to menu id
*/

public class GetIntents {

    public static Intent get(int id, Context context) {
        switch (id) {
            /*case 22:
                return new Intent(context, CreateSosActivity.class);
            case 30:
                return new Intent(context, PostAnnouncementActivity.class);
            case 34:
                return new Intent(context, MoodActivity.class);*/
            default:
                return null;
        }
    }
}
