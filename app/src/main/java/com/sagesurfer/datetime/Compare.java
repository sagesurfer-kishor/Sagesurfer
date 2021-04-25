package com.sagesurfer.datetime;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

/*
* This is date comparator class
* compare date is today's or not
* compare date is tomorrow or not
* compare date is yesterday or not
*/

public class Compare {

    @SuppressLint("SimpleDateFormat")
    public static boolean isToday(String today, String selected_date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        try {
            Date date2 = dateFormat.parse(selected_date);
            Date date1 = dateFormat.parse(today);
            calendar1.setTime(date1);
            calendar2.setTime(date2);
            int result = calendar1.compareTo(calendar2);
            if (result == 0) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressLint("SimpleDateFormat")
    public static boolean isTomorrow(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        return isToday(dateFormat.format(tomorrow), date);
    }

    @SuppressLint("SimpleDateFormat")
    public static boolean isYesterday(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();
        return isToday(dateFormat.format(yesterday), date);
    }

    // check task due date validity
    @SuppressLint("SimpleDateFormat")
    public static int dueDateTask(String today, int mYear, String selected_date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        Date date1 = dateFormat.parse(today);
        Date date2 = dateFormat.parse(selected_date);

        calendar1.setTime(date1);
        calendar2.setTime(date2);

        int result = calendar1.compareTo(calendar2);

        int startYear = calendar1.get(Calendar.YEAR);
        if (mYear > (startYear + 2)) {
            result = 1;
        }
        return result;
    }

    // check validity of start date across the application
    @SuppressLint("SimpleDateFormat")
    public static int startDate(String today, String selected_date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        Date date1 = dateFormat.parse(today);
        Date date2 = dateFormat.parse(selected_date);

        calendar1.setTime(date1);
        calendar2.setTime(date2);

        return calendar1.compareTo(calendar2);
    }

    // valid end date across the application
    @SuppressLint("SimpleDateFormat")
    public static int validEndDate(String endDate, String startDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM");
        Date end = null;
        String newEnd;
        Date start = null;
        String newStart;
        try {
            end = dateFormat.parse(endDate);
            start = dateFormat.parse(startDate);
            newEnd = format.format(end);
            newStart = format.format(start);
            end = format.parse(newEnd);
            start = format.parse(newStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        int endYear = cal.get(Calendar.YEAR);
        int endMonth = cal.get(Calendar.MONTH);
        int endDay = cal.get(Calendar.DAY_OF_MONTH);
        if (endMonth < 12) {
            endMonth = endMonth + 1;
        }

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);
        int startYear = startCal.get(Calendar.YEAR);
        int startMonth = startCal.get(Calendar.MONTH);
        int startDay = startCal.get(Calendar.DAY_OF_MONTH);
        if (startMonth < 12) {
            startMonth = startMonth + 1;
        }

        if (endYear < startYear) {
            return 0;
        }
        if (endYear > startYear) {
            return 1;
        }
        if (endMonth < startMonth) {
            return 0;
        }
        if (endMonth > startMonth) {
            return 1;
        }
        if (endDay < startDay) {
            return 0;
        }
        return 1;
    }

    // check if selected date reside in given date range
    @SuppressLint("SimpleDateFormat")
    public static boolean dateInRange(String start, String end, String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date a = dateFormat.parse(start);
        Date b = dateFormat.parse(end);   // assume these are set to something
        Date d = dateFormat.parse(date);
        return a.compareTo(d) * d.compareTo(b) >= 0;
    }

    @SuppressLint("SimpleDateFormat")
    public static String dayOfWeekShort(){
        SimpleDateFormat sdf = new SimpleDateFormat("EE");
        Date d = new Date();
        return sdf.format(d);
    }

    // get days between two dates
    @SuppressLint("SimpleDateFormat")
    public static int getWorkingDays(String start_date, String end_date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = dateFormat.parse(start_date);
            endDate = dateFormat.parse(end_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        int workDays = 0;

        //Return 0 if start and end are the same
        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            return 0;
        }

        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(endDate);
            endCal.setTime(startDate);
        }

        do {
            //excluding start date
            startCal.add(Calendar.DAY_OF_MONTH, 1);
            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                    startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                ++workDays;
            }
        } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date

        return workDays;
    }
}
