package com.modules.selfgoal;

import android.content.Context;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.ArrayOperations;
import com.storage.preferences.AddGoalPreferences;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 24/03/2018
 *         Last Modified on
 */
/*
* This class is make frequency unit based on frequency and time
*/

class FrequencyUnit_ {

    private static final String TAG = FrequencyUnit_.class.getSimpleName();

    static String getFrequencyUnit(String frequency, String time, Context _context) {
        String frequency_unit = "";
        if (frequency.equalsIgnoreCase("minute")) {
            return time + "," + "0";
        } else if (frequency.equalsIgnoreCase(_context.getResources().getString(R.string.hour))) {
            return time + "," + "0";
        } else if (frequency.equalsIgnoreCase(_context.getResources().getString(R.string.daily))) {
            if (AddGoalPreferences.getBoolean(_context.getApplicationContext().getResources().getString(R.string.daily))) {
                return "EVERY_WEEKDAY";
            } else {
                return AddGoalPreferences.get("day_count");
            }
        } else if (frequency.equalsIgnoreCase(_context.getResources().getString(R.string.weekly))) {
            String selected_week = AddGoalPreferences.get("week");
            if (selected_week.trim().length() <= 0) {
                String[] daysArray = _context.getResources().getStringArray(R.array.add_goal_days_short);
                return "1," + ArrayOperations.stringListToString(new ArrayList<>(Arrays.asList(daysArray))).replaceAll(",", "-");
            }
            return "1," + selected_week.replaceAll(",", "-");
        } else if (frequency.equalsIgnoreCase(_context.getResources().getString(R.string.month))) {
            return AddGoalPreferences.get("month");
        }
        return frequency_unit;
    }
}
