package com.sagesurfer.library;

import java.util.Calendar;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class AgeCalculator {

    private static final String TAG = AgeCalculator.class.getSimpleName();

    public static Age ageCalculate(long days) {
        long y = 0;
        if (days > Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR)) {
            y = days / 365;
        }
        days = days % 365;
        long month = days / 30;
        long m = 0;
        if (month > 0) {
            for (int i = 0; i < month; i++) {
                Calendar calendar = Calendar.getInstance();
                days = days - calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                calendar.add(Calendar.MONTH, i + 1);
                m++;
            }
        }
        return new Age((int) days, (int) m, (int) y);
    }
}