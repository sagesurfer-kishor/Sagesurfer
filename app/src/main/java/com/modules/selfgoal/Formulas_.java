package com.modules.selfgoal;


/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 24/03/2018
 *         Last Modified on
 */
/*
* This class is make day,week or month counting formula
* It make uses of occurrences and day
*/

class Formulas_ {

    private static final String TAG = Formulas_.class.getSimpleName();

    static int getDayCount(String occurrence, String day) {
        int count;
        if (occurrence == null || occurrence.length() <= 0 || occurrence.equalsIgnoreCase("0")) {
            occurrence = "1";
        }
        if (day == null || day.length() <= 0 || day.equalsIgnoreCase("0")) {
            day = "1";
        }
        int _occurrence = Integer.parseInt(occurrence);
        int _day = Integer.parseInt(day);
        if (_day == 1 && _occurrence == 1) {
            count = 0;
        } else if (_day == 1 && _occurrence > 1) {
            count = (_occurrence - 1);
        } else {
            int _count = _day * _occurrence;
            if (_day > 1 && _occurrence == 1) {
                _count = 0;
            } else {
                _count = _count - _day;
            }
            count = _count;
        }
        return count;
    }

    static int getWeekCount(String occurrence) {
        if (occurrence == null || occurrence.length() <= 0 || occurrence.equalsIgnoreCase("0")) {
            return 6;
        }
        int _occurrence = Integer.parseInt(occurrence);
        return ((_occurrence * 7) - 1);
    }

    static int getMonthCount(String occurrence, String month) {
        if (occurrence == null || occurrence.length() <= 0 || occurrence.equalsIgnoreCase("0")) {
            return 1;
        }
        if (month == null || month.length() <= 0 || month.equalsIgnoreCase("0")) {
            month = "1";
        }
        int _occurrence = Integer.parseInt(occurrence);
        int _month = Integer.parseInt(month);
        int _count = 1;
        if (_occurrence == 1) {
            return 0;
        }
        if (_occurrence > 1) {
            int m = _month * _occurrence;
            _count = m - _month;
        }
        return _count;
    }
}
