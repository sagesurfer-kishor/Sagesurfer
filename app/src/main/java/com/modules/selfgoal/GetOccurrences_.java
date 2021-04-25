package com.modules.selfgoal;

import android.content.Context;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.datetime.GetTime;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 24/03/2018
 *         Last Modified on
 */
/*
* This class is used to get occurrences of goal
* It make use of start and end date with other units
*/

class GetOccurrences_ {

    private static final String TAG = GetOccurrences_.class.getSimpleName();

    static int getOccurrences(String frequency, String occurrences, String start_date,
                              String end_date, boolean isAdvance, boolean checkOne, String valueOne, Context _context) {
        if (occurrences.trim().length() <= 0) {
            occurrences = "1";
        }
        if (valueOne.trim().length() <= 0 || valueOne.equalsIgnoreCase("")) {
            valueOne = "1";
        }

        if (frequency.equalsIgnoreCase(_context.getResources().getString(R.string.daily))) {
            if (!isAdvance) {
                checkOne = false;
            }
            long days;
            if (checkOne) {
                days = Compare.getWorkingDays(start_date, end_date);
            } else {
                days = GetTime.dayDifference(start_date, end_date);
            }
            days = days + 1;
            if (valueOne.equalsIgnoreCase("1")) {
                return (int) days;
            } else {
                int count = Integer.parseInt(valueOne);
                return Math.round(days / (long) count);
            }
        }
        if (frequency.equalsIgnoreCase(_context.getResources().getString(R.string.weekly))) {
            long days = GetTime.dayDifference(start_date, end_date);
            long mod = days % 7;
            long div = days / 7;
            if (mod > 0) {
                div = div + 1;
            }
            if (div == 0 && mod == 0) {
                div = 1;
            }
            return (int) div;
        }
        return Integer.parseInt(occurrences);
    }
}
