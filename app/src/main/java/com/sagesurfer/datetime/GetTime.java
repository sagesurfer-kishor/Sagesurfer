package com.sagesurfer.datetime;

import android.annotation.SuppressLint;
import android.util.Log;

import com.sagesurfer.library.Age;
import com.sagesurfer.library.AgeCalculator;
import com.sagesurfer.library.GetCounters;
import com.utils.AppLog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

/*
 * This class is used to converts timestamp in milliseconds to desire date time format
 */

public class GetTime {
    private static final String TAG = "GetTime";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getChatTimestamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        return tsLong.toString();
    }

    public static String wallTime(long time) {
        long now = System.currentTimeMillis();
        if (time < 1000000000000L) {
            time *= 1000;
        }
       /* if (time > now || time <= 0) {
            AppLog.i(TAG, "wallTime:  time > now || time <= 0 and time = "+time +" and now = "+now);
            return "now";
        }*/
        AppLog.i(TAG,"now "+now +" time "+time);
        final long diff = now - time;

        if (diff < MINUTE_MILLIS) {
            AppLog.i(TAG, "wallTime: diff < MINUTE_MILLIS "+diff);
            return "now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            AppLog.i(TAG, "wallTime: diff < 2 * MINUTE_MILLIS "+diff);
            return "1 min";
        } else if (diff < 50 * MINUTE_MILLIS) {
            AppLog.i(TAG, "wallTime: diff < 50 * MINUTE_MILLIS "+diff);
            return diff / MINUTE_MILLIS + " min";
        } else if (diff < 90 * MINUTE_MILLIS) {
            AppLog.i(TAG, "diff < 90 * MINUTE_MILLIS "+diff);
            return "1 hr";
        } else if (diff < 24 * HOUR_MILLIS) {
            AppLog.i(TAG, "wallTime: diff < 24 * HOUR_MILLIS "+diff);
            return diff / HOUR_MILLIS + " hr";
        } else if (diff < 48 * HOUR_MILLIS) {
            AppLog.i(TAG, "wallTime: diff < 24 * HOUR_MILLIS "+diff);
            return "1 day";
        } else if ((diff / DAY_MILLIS) > 3) {
            AppLog.i(TAG, "wallTime: diff < MINUTE_MILLIS "+diff);
            return getDate(time);
        } else {
            AppLog.i(TAG, "wallTime: diff / DAY_MILLIS) > 3 "+diff);
            return diff / DAY_MILLIS + " days";
        }
    }

    public static String dueTime(long time) {
        long now = System.currentTimeMillis();
        if (time < 1000000000000L) {
            time *= 1000;
        }

        if (time > now || time <= 0) {
            return "due now";
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Due now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "Due in 1 min";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "Due in " + diff / MINUTE_MILLIS + " min";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "Due in 1 hr";
        } else if (diff < 24 * HOUR_MILLIS) {
            return "Due in " + diff / HOUR_MILLIS + " hr";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Due in 1 day";
        } else if ((diff / DAY_MILLIS) > 3) {
            return getDate(time);
        } else {
            return "Due in " + diff / DAY_MILLIS + " days";
        }
    }

    public static Date addDay(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, i);
        return cal.getTime();
    }

    @SuppressLint("SimpleDateFormat")
    public static String todaysDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDate(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
        Date result_date = new Date(time);
        return sdf.format(result_date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTodayEeeMm(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        SimpleDateFormat formatFrom = new SimpleDateFormat("EEE, MMM dd yyyy HH:mm:ss");
        Date result_date = new Date(time);
        return formatFrom.format(result_date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTodayMm(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        SimpleDateFormat formatFrom = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        Date result_date = new Date(time);
        return formatFrom.format(result_date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTodayMonthYear(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        SimpleDateFormat formatFrom = new SimpleDateFormat("EEEE, MMM dd yyyy");
        Date result_date = new Date(time);
        return formatFrom.format(result_date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getMmDdYyyy(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        SimpleDateFormat formatFrom = new SimpleDateFormat("yyyy-MM-dd");
        Date result_date = new Date(time);
        return formatFrom.format(result_date);
    }


    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDateYyyyMmDd() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        return sdfDate.format(now);
    }

    public static int currentTimeSpan() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            return 1;
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            return 2;
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            return 3;
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            return 4;
        }
        return 1;
    }

    @SuppressLint("SimpleDateFormat")
    public static long getDateTimestamp(String date_string) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        long dateInMillis = 0;
        try {
            Date date = formatter.parse(date_string);
            dateInMillis = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateInMillis / 1000;
    }

    public static String dateDifference(String start_date, String end_date, String frequency) {
        return age(start_date, end_date, frequency);
    }

    @SuppressLint("SimpleDateFormat")
    public static long dayDifference(String start_date, String end_date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;
        Date endDate;
        try {
            startDate = dateFormat.parse(start_date);
            endDate = dateFormat.parse(end_date);
            long diff = endDate.getTime() - startDate.getTime();
            return diff / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static String age(String start_date, String end_date, String frequency) {
        long days = dayDifference(start_date, end_date);
        Age age = AgeCalculator.ageCalculate(days);
        int day = age.getDays();
        int month = age.getMonths();
        int year = age.getYears();
        day = day + 1;
        StringBuilder stringBuilder = new StringBuilder();
        if (year > 0) {
            if (year == 1) {
                stringBuilder.append(year).append(" Year");
            } else {
                stringBuilder.append(year).append(" Years");
            }
            if (month > 0) {
                stringBuilder.append(", ");
            }
        }
        if (month > 0) {
            if (month == 1) {
                stringBuilder.append(month).append(" Month");
            } else {
                if (month >= 2 && frequency.equalsIgnoreCase("month")) {
                    day = 1;
                }
                stringBuilder.append(month).append(" Months");
            }
            if (day > 0) {
                stringBuilder.append(", ");
            }
        }
        if (day > 0) {
            if (day == 1) {
                stringBuilder.append(day).append(" Day");
            } else {
                stringBuilder.append(day).append(" Days");
            }
        }
        return stringBuilder.toString();
    }


    public static String getSpecificDay(int count, int day, int month_count) {
        Calendar cacheCalendar = Calendar.getInstance();
        int today = cacheCalendar.get(Calendar.DATE);
        cacheCalendar.set(Calendar.DAY_OF_WEEK, day);
        cacheCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, count);
        int final_date = cacheCalendar.get(Calendar.DATE);
        if (month_count > 0) {
            if (final_date < today) {
                cacheCalendar.set(Calendar.MONTH, cacheCalendar.get(Calendar.MONTH) + (month_count + 1));
                cacheCalendar.set(Calendar.DAY_OF_WEEK, day);
                cacheCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, count);
                cacheCalendar.set(Calendar.YEAR, cacheCalendar.get(Calendar.YEAR));
                return cacheCalendar.get(Calendar.YEAR) + "-" + GetCounters.checkDigit(cacheCalendar.get(Calendar.MONTH) + 1)
                        + "-" + GetCounters.checkDigit(cacheCalendar.get(Calendar.DATE));
            } else {
                cacheCalendar.set(Calendar.MONTH, cacheCalendar.get(Calendar.MONTH) + month_count);
                cacheCalendar.set(Calendar.DAY_OF_WEEK, day);
                cacheCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, count);
                cacheCalendar.set(Calendar.YEAR, cacheCalendar.get(Calendar.YEAR));
                return cacheCalendar.get(Calendar.YEAR) + "-" + GetCounters
                        .checkDigit(cacheCalendar.get(Calendar.MONTH) + 1)
                        + "-" + GetCounters.checkDigit(cacheCalendar.get(Calendar.DATE));

            }
        } else {
            if (final_date < today) {
                cacheCalendar.set(Calendar.MONTH, cacheCalendar.get(Calendar.MONTH) + (month_count + 1));
                cacheCalendar.set(Calendar.DAY_OF_WEEK, day);
                cacheCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, count);
                cacheCalendar.set(Calendar.YEAR, cacheCalendar.get(Calendar.YEAR));
                return cacheCalendar.get(Calendar.YEAR) + "-" + GetCounters
                        .checkDigit(cacheCalendar.get(Calendar.MONTH) + 1)
                        + "-" + GetCounters.checkDigit(cacheCalendar.get(Calendar.DATE));
            } else {
                cacheCalendar.set(Calendar.MONTH, cacheCalendar.get(Calendar.MONTH) + month_count);
                cacheCalendar.set(Calendar.YEAR, cacheCalendar.get(Calendar.YEAR));
                return cacheCalendar.get(Calendar.YEAR) + "-" + GetCounters
                        .checkDigit(cacheCalendar.get(Calendar.MONTH) + 1)
                        + "-" + GetCounters.checkDigit(cacheCalendar.get(Calendar.DATE));
            }
        }
    }

   /* @SuppressLint("SimpleDateFormat")
    public static String monthDdYyyy(String original_date) {
        Log.e(GetTime.class.getSimpleName(), "original_date: " + original_date);
        if (original_date == null) {
            return "";
        }
        if (original_date.trim().length() <= 0) {
            return "";
        }
        SimpleDateFormat originalFormat = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat convertFormat = new SimpleDateFormat("MMM dd, yyyy");
        try {
            Date originalDate = originalFormat.parse(original_date);
            return convertFormat.format(originalDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return original_date;
    }*/

    @SuppressLint("SimpleDateFormat")
    public static String month_DdYyyy(String original_date) {
        //Log.e(GetTime.class.getSimpleName(), "original_date: " + original_date);
        if (original_date == null) {
            return "";
        }
        if (original_date.trim().length() <= 0) {
            return "";
        }
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat convertFormat = new SimpleDateFormat("MMM dd, yyyy");
        try {
            Date originalDate = originalFormat.parse(original_date);
            return convertFormat.format(originalDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return original_date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String yy_mm_dd(String original_date) {
        //Log.e(GetTime.class.getSimpleName(), "original_date: " + original_date);
        if (original_date == null) {
            return "";
        }
        if (original_date.trim().length() <= 0) {
            return "";
        }
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date originalDate = originalFormat.parse(original_date);
            return convertFormat.format(originalDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return original_date;
    }


    public static String getCurrentTime() {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String time = simpleDateFormat.format(calender.getTime());
        return time;
    }

    public static String dateValue(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = android.text.format.DateFormat.format("MMM dd, yyyy ", cal).toString();
        return date;
    }

    public static String getDateTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = android.text.format.DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String dateCaps(String dateValue) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = spf.parse(dateValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("MMM dd, yyyy");
        String date = spf.format(newDate);
        return date;
    }
}
