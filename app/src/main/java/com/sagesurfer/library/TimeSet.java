package com.sagesurfer.library;

import android.annotation.SuppressLint;

import java.text.DateFormat;
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
* convert time in milliseconds to date and time and vise versa
*/

public class TimeSet {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getWallTime(long time) {
        long now = System.currentTimeMillis();
        if (time < 1000000000000L) {
            time *= 1000;
        }
        if (time > now || time <= 0) {
            return "now";
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1min";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "min";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1hr";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hr";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "1day";
        } else if ((diff / DAY_MILLIS) > 3) {
            return getDateWithTime(time);
        } else {
            return diff / DAY_MILLIS + "days";
        }
    }

    public static String getChatTime(long time) {
        long now = System.currentTimeMillis();
        if (time < 1000000000000L) {
            time *= 1000;
        }
        if (time > now || time <= 0) {
            return null;
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1min";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "min";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1hr";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hr";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "1day";
        } else if ((diff / DAY_MILLIS) > 3) {
            return getDateWithTime(time);
        } else {
            return diff / DAY_MILLIS + "days";
        }
    }

    public static String getPollTime(long time) {
        long now = System.currentTimeMillis();
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long diff = now - time;

        if (time > now || time <= 0) {
            diff = diff * -1;
            if (diff < MINUTE_MILLIS) {
                return "expire now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "expire now";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return "expire in " + diff / MINUTE_MILLIS + "min";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "expire in 1 hr";
            } else if (diff < 24 * HOUR_MILLIS) {
                return "expire in " + diff / HOUR_MILLIS + "hr";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "expire in " + "1day";
            } else if ((diff / DAY_MILLIS) > 3) {
                return getDate(time);
            } else {
                return diff / DAY_MILLIS + "days";
            }
        }

        if (diff < MINUTE_MILLIS) {
            return "now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "now";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "min";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1 hr";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + "hr";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "1 d";
        } else if ((diff / DAY_MILLIS) > 3) {
            return getDate(time);
        } else {
            return diff / DAY_MILLIS + "days";
        }
    }


    public static String getAlertTime(long time, boolean isDate) {
        long now = System.currentTimeMillis();
        if (time < 1000000000000L) {
            time *= 1000;
        }
        if (time > now || time <= 0) {
            return "now";
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "now";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "min";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1hr";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + "hr";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "1day";
        } else if ((diff / DAY_MILLIS) > 3) {
            if (isDate) {
                return getDate(time);
            } else {
                return getDateWithTime(time);
            }
        } else {
            return diff / DAY_MILLIS + "days";
        }
    }

    public static String getTaskTime(String srcDate) {
        if (srcDate.equalsIgnoreCase("N/A")) {
            return srcDate;
        } else {
            long diff = getDayDifference(srcDate);
            if (diff <= 0) {
                return getDate(Long.parseLong(srcDate));
            } else if (diff <= 3 && diff > 0) {
                return diff + " d";
            } else {
                return getDate(Long.parseLong(srcDate));
            }
        }
    }

    public static String getMothFormat(String srcDate) {
        return getDateWithTime(Long.parseLong(srcDate));
    }

    private static long getDayDifference(String srcDate) {
        long diff = Long.parseLong(srcDate) - getCurrentDateStamp();
        return diff / (24 * 60 * 60 * 1000);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateWithTime(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
        Date result_date = new Date(time);
        return sdf.format(result_date);
    }

    @SuppressLint("SimpleDateFormat")
    private static String getDate(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
        Date result_date = new Date(time);
        String str = sdf.format(result_date);
        String one = str.substring(0, 11);
        return one.split(" ", 1)[0];
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCareDate(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date result_date = new Date(time);
        return sdf.format(result_date);
    }

    public static String getChatTimestamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        return tsLong.toString();
    }

    @SuppressLint("SimpleDateFormat")
    private static long getCurrentDateStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date now = new Date();
        String strDate = sdf.format(now);
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        long dateInMillis = date.getTime();
        return dateInMillis / 1000;
    }

    @SuppressLint("SimpleDateFormat")
    public static String loginDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date now = new Date();
        return sdf.format(now);
    }

    @SuppressLint("SimpleDateFormat")
    public static long loginDifference(String oldDate) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        Date d1;
        Date d2;
        long diffSeconds = 0;
        try {
            d1 = format.parse(oldDate);
            d2 = format.parse(loginDate());

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffMinutes > 0) {
                return 32;
            }
            if (diffHours > 0) {
                return 32;
            }
            if (diffDays > 0) {
                return 32;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return diffSeconds;
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
    public static String dayOfWeekShort() {
        SimpleDateFormat sdf = new SimpleDateFormat("EE");
        Date d = new Date();
        return sdf.format(d);
    }

    @SuppressLint("SimpleDateFormat")
    public static String todayDateMonthDayYear() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
        return df.format(c.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTodayEeeMm() {
        SimpleDateFormat formatFrom = new SimpleDateFormat("EEE, MMM dd yyyy");
        Calendar c = Calendar.getInstance();
        return formatFrom.format(c.getTime());
    }

    public static Date getYesterdayDate() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(getYesterdayDate());
    }
}
