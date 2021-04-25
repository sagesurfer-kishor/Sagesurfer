package com.modules.calendar;

import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.modules.planner.DailyPlannerFragment;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.storage.preferences.Preferences;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 19-07-2017
 * Last Modified on 13-12-2017
 */


public class CustomCalendar {

    // Variables
    public static int mMonth = -1;
    public static int mYear = -1;
    public static int mCurrentDay = -1;
    public static int mCurrentMonth = -1;
    public static int mCurrentYear = -1;
    public static int mFirstDay = -1;
    public static int mNumDaysInMonth = -1;


    public static void getInitialCalendarInfo() {
        Calendar cal = Calendar.getInstance();

        if (cal != null) {
            mNumDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            mMonth = cal.get(Calendar.MONTH);
            mYear = cal.get(Calendar.YEAR);

            mCurrentDay = cal.get(Calendar.DAY_OF_MONTH);
            mCurrentMonth = mMonth;
            mCurrentYear = mYear;
            getFirstDay(mMonth, mYear);
        }
    }

    private static void refreshCalendar(TextView monthTextView, GridView calendarGridView, CustomCalendarAdapter materialCalendarAdapter, int month, int year) {

        checkCurrentDay(month, year);
        getNumDayInMonth(month, year);
        getFirstDay(month, year);

        if (monthTextView != null) {
            String monthName = getMonthName(month) + " " + year;
            monthTextView.setText(monthName);
        }

        // Clear Saved Events ListView count when changing calendars
        /*if(Preferences.getBoolean(General.IS_FROM_MOOD_CALENDAR)) {
            if (MoodCalendarFragment.mSavedEventsAdapter != null) {
                MoodCalendarFragment.mNumEventsOnDay = -1;
                MoodCalendarFragment.mSavedEventsAdapter.notifyDataSetChanged();
            }

            MoodCalendarFragment.getCounters();
        } else*/
        if (Preferences.getBoolean(General.IS_FROM_DAILYPLANNER)) {
            /*if (DailyPlannerFragment.mSavedEventsAdapter != null) {
                DailyPlannerFragment.mNumEventsOnDay = -1;
                DailyPlannerFragment.mSavedEventsAdapter.notifyDataSetChanged();
            }*/

            DailyPlannerFragment.getCounters("month");
        } else if (Preferences.getBoolean(General.IS_FROM_CASELOAD)) {
            if (CalenderActivity.mSavedEventsAdapter != null) {
                CalenderActivity.mNumEventsOnDay = -1;
                CalenderActivity.mSavedEventsAdapter.notifyDataSetChanged();
            }

            CalenderActivity.getCounters();
        } else if (Preferences.getBoolean(Actions_.GET_LOG)) {
            if (CalenderActivity.mSavedEventsAdapter != null) {
                CalenderActivity.mNumEventsOnDay = -1;
                CalenderActivity.mSavedEventsAdapter.notifyDataSetChanged();
            }
        } else if (Preferences.getBoolean(Actions_.APPOINTMENT_COUNTER)) {
            if (CalenderActivity.mSavedEventsAdapter != null) {
                CalenderActivity.mNumEventsOnDay = -1;
                CalenderActivity.mSavedEventsAdapter.notifyDataSetChanged();
            }
        } else if (Preferences.getBoolean(Actions_.GET_LOGBOOK_DOTS)) {
            if (CalenderActivity.mSavedEventsAdapter != null) {
                CalenderActivity.mNumEventsOnDay = -1;
                CalenderActivity.mSavedEventsAdapter.notifyDataSetChanged();
            }
        } else {
            if (CalendarFragment.mSavedEventsAdapter != null) {
                CalendarFragment.mNumEventsOnDay = -1;
                CalendarFragment.mSavedEventsAdapter.notifyDataSetChanged();
            }

            CalendarFragment.getCounters();
        }

        if (materialCalendarAdapter != null) {
            if (calendarGridView != null) {
                calendarGridView.setItemChecked(calendarGridView.getCheckedItemPosition(), false);
            }

            materialCalendarAdapter.notifyDataSetChanged();
        }
    }

    private static String getMonthName(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

    private static void checkCurrentDay(int month, int year) {
        if (month == mCurrentMonth && year == mCurrentYear) {
            Calendar cal = java.util.Calendar.getInstance();
            mCurrentDay = cal.get(Calendar.DAY_OF_MONTH);
        } else {
            mCurrentDay = -1;
        }
    }

    private static void getNumDayInMonth(int month, int year) {
        Calendar cal = Calendar.getInstance();
        if (cal != null) {
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.YEAR, year);
            mNumDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
    }

    private static void getFirstDay(int month, int year) {
        Calendar cal = Calendar.getInstance();
        if (cal != null) {
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.DAY_OF_MONTH, 1);

            switch (cal.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY:
                    mFirstDay = 0;
                    break;
                case Calendar.MONDAY:
                    mFirstDay = 1;
                    break;
                case Calendar.TUESDAY:
                    mFirstDay = 2;
                    break;
                case Calendar.WEDNESDAY:
                    mFirstDay = 3;
                    break;
                case Calendar.THURSDAY:
                    mFirstDay = 4;
                    break;
                case Calendar.FRIDAY:
                    mFirstDay = 5;
                    break;
                case Calendar.SATURDAY:
                    mFirstDay = 6;
                    break;
                default:
                    break;
            }
        }
    }

    // Call in View.OnClickListener for Previous ImageView
    public static void previousOnClick(ImageView previousImageView, TextView monthTextView, GridView calendarGridView, CustomCalendarAdapter materialCalendarAdapter) {
        if (previousImageView != null && mMonth != -1 && mYear != -1) {
            previousMonth(monthTextView, calendarGridView, materialCalendarAdapter);
        }
    }

    // Call in View.OnClickListener for Next ImageView
    public static void nextOnClick(ImageView nextImageView, TextView monthTextView, GridView calendarGridView, CustomCalendarAdapter materialCalendarAdapter) {
        if (nextImageView != null && mMonth != -1 && mYear != -1) {
            nextMonth(monthTextView, calendarGridView, materialCalendarAdapter);
        }
    }

    private static void previousMonth(TextView monthTextView, GridView calendarGridView, CustomCalendarAdapter materialCalendarAdapter) {
        if (mMonth == 0) {
            mMonth = 11;
            mYear = mYear - 1;
        } else {
            mMonth = mMonth - 1;
        }

        refreshCalendar(monthTextView, calendarGridView, materialCalendarAdapter, mMonth, mYear);
    }

    private static void nextMonth(TextView monthTextView, GridView calendarGridView, CustomCalendarAdapter materialCalendarAdapter) {
        if (mMonth == 11) {
            mMonth = 0;
            mYear = mYear + 1;
        } else {
            mMonth = mMonth + 1;
        }

        refreshCalendar(monthTextView, calendarGridView, materialCalendarAdapter, mMonth, mYear);
    }

    // Call in View.OnClickListener for Previous ImageView
    public static void previousOnClick(ImageView previousImageView, TextView monthTextView) {
        if (previousImageView != null && mMonth != -1 && mYear != -1) {
            previousMonth(monthTextView);
        }
    }

    // Call in View.OnClickListener for Next ImageView
    public static void nextOnClick(ImageView nextImageView, TextView monthTextView) {
        if (nextImageView != null && mMonth != -1 && mYear != -1) {
            nextMonth(monthTextView);
        }
    }

    private static void previousMonth(TextView monthTextView) {
        if (mMonth == 0) {
            mMonth = 11;
            mYear = mYear - 1;
        } else {
            mMonth = mMonth - 1;
        }

        checkCurrentDay(mMonth, mYear);
        getNumDayInMonth(mMonth, mYear);
        getFirstDay(mMonth, mYear);

        if (monthTextView != null) {
            String monthName = getMonthName(mMonth) + " " + mYear;
            monthTextView.setText(monthName);
        }
    }

    private static void nextMonth(TextView monthTextView) {
        if (mMonth == 11) {
            mMonth = 0;
            mYear = mYear + 1;
        } else {
            mMonth = mMonth + 1;
        }

        checkCurrentDay(mMonth, mYear);
        getNumDayInMonth(mMonth, mYear);
        getFirstDay(mMonth, mYear);

        if (monthTextView != null) {
            String monthName = getMonthName(mMonth) + " " + mYear;
            monthTextView.setText(monthName);
        }
    }

    // Call in GridView.onItemClickListener for custom Calendar GirdView
    public static void selectCalendarDay(CustomCalendarAdapter materialCalendarAdapter, int position) {
        int weekPositions = 6;
        int noneSelectablePositions = weekPositions + mFirstDay;

        if (position > noneSelectablePositions) {
            getSelectedDate(position, mMonth, mYear);

            if (materialCalendarAdapter != null) {
                materialCalendarAdapter.notifyDataSetChanged();
            }
        }
    }

    // Call in GridView.onItemClickListener for custom Calendar GirdView
    public static void selectCalendarDay(CustomCalendarWeekAdapter materialCalendarWeekAdapter, int position) {
        getSelectedDate(6 - position, mMonth, mYear);

        if (materialCalendarWeekAdapter != null) {
            materialCalendarWeekAdapter.notifyDataSetChanged();
        }
    }

    private static void getSelectedDate(int selectedPosition, int month, int year) {
        int weekPositions = 6;
        int dateNumber = selectedPosition - weekPositions - mFirstDay;
    }
}

