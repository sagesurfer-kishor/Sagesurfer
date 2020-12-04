package com.modules.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.modules.appointment.fragments.AppointmentFragment;
import com.modules.planner.DailyPlannerFragment;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.storage.preferences.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Kailash Karankal
 */

public class CustomCalendarWeekAdapter extends BaseAdapter {

    private final Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static ViewHolder mHolder;
    private String currentDate;
    private boolean isFutureWeekDate = false;

    private static class ViewHolder {
        ImageView mSelectedDayImageView, mSavedDataImageView;
        TextView mDateTextView, mWeekDayTextView;
    }

    // Constructor
    public CustomCalendarWeekAdapter(Context context) {
        mContext = context;
        isFutureWeekDate = false;
    }

    public CustomCalendarWeekAdapter(Context context, boolean isFutureDate) {
        mContext = context;
        isFutureWeekDate=isFutureDate;
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.calendar_day_item_layout, parent, false);

            mHolder = new ViewHolder();

            if (convertView != null) {
                mHolder.mWeekDayTextView = (TextView) convertView.findViewById(R.id.material_calendar_weeek_day);
                mHolder.mSelectedDayImageView = (ImageView) convertView.findViewById(R.id.material_calendar_selected_day);
                mHolder.mSavedDataImageView = (ImageView) convertView.findViewById(R.id.material_calendar_saved_data);
                mHolder.mDateTextView = (TextView) convertView.findViewById(R.id.material_calendar_day);

                mHolder.mWeekDayTextView.setVisibility(View.VISIBLE);
                convertView.setTag(mHolder);
            }
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        if (mHolder.mSelectedDayImageView != null) {
            GridView gridView = (GridView) parent;
            if (gridView.isItemChecked(position)) {
                Animation feedBackAnimation = AnimationUtils.loadAnimation(mContext, R.anim.selected_day_feedback);
                mHolder.mSelectedDayImageView.setVisibility(View.VISIBLE);
                if (feedBackAnimation != null) {
                    mHolder.mSelectedDayImageView.startAnimation(feedBackAnimation);
                }
            } else {
                mHolder.mSelectedDayImageView.setVisibility(View.INVISIBLE);
            }
        }

        if (mHolder.mDateTextView != null) {
            setCalendarDay(position, parent);
        }

        //setSavedEvent(position, parent);

        return convertView;
    }


    private void setCalendarDay(int position, ViewGroup parent) {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
        //String currentDate = "";

        int i = 6-position;
        if(isFutureWeekDate) {
            i = position;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        try {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date myDate = dateFormat.parse(currentDate);
            currentDate = (String) DateFormat.format("dd", myDate);
            calendar = Calendar.getInstance();
            calendar.setTime(myDate);

            if(isFutureWeekDate) {
                calendar.add(Calendar.DAY_OF_YEAR, i);
            }
            else {
                calendar.add(Calendar.DAY_OF_YEAR, -i);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date newDate = calendar.getTime();
        String date = (String) DateFormat.format("dd", newDate);
        calendar.setTime(newDate);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        mHolder.mDateTextView.setText(date);
        mHolder.mDateTextView.setTypeface(null, Typeface.NORMAL);

        if (CustomCalendar.mCurrentDay != -1) {
            if (currentDate.equalsIgnoreCase(date)) {
                mHolder.mDateTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                mHolder.mDateTextView.setTypeface(null, Typeface.BOLD);
            } else {
                mHolder.mDateTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                mHolder.mDateTextView.setTypeface(null, Typeface.NORMAL);
            }
        } else {
            mHolder.mDateTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
            mHolder.mDateTextView.setTypeface(null, Typeface.NORMAL);
        }

        switch (dayOfWeek) {
            case 1:
                mHolder.mWeekDayTextView.setText(mContext.getResources().getString(R.string.sun));
                mHolder.mWeekDayTextView.setTypeface(null, Typeface.BOLD);
                break;

            case 2:
                mHolder.mWeekDayTextView.setText(mContext.getResources().getString(R.string.mon));
                mHolder.mWeekDayTextView.setTypeface(null, Typeface.BOLD);
                break;

            case 3:
                mHolder.mWeekDayTextView.setText(mContext.getResources().getString(R.string.tue));
                mHolder.mWeekDayTextView.setTypeface(null, Typeface.BOLD);
                break;

            case 4:
                mHolder.mWeekDayTextView.setText(mContext.getResources().getString(R.string.wed));
                mHolder.mWeekDayTextView.setTypeface(null, Typeface.BOLD);
                break;

            case 5:
                mHolder.mWeekDayTextView.setText(mContext.getResources().getString(R.string.thu));
                mHolder.mWeekDayTextView.setTypeface(null, Typeface.BOLD);
                break;

            case 6:
                mHolder.mWeekDayTextView.setText(mContext.getResources().getString(R.string.fri));
                mHolder.mWeekDayTextView.setTypeface(null, Typeface.BOLD);
                break;

            case 7:
                mHolder.mWeekDayTextView.setText(mContext.getResources().getString(R.string.sat));
                mHolder.mWeekDayTextView.setTypeface(null, Typeface.BOLD);
                break;

            default:
                break;
        }

        setSavedEvent(position, date, parent);
    }

    private void setSavedEvent(int position, String date, ViewGroup parent) {
        if (Preferences.getBoolean(Actions_.APPOINTMENT_COUNTER)) {
            if (CustomCalendar.mFirstDay != -1 && AppointmentFragment.mSavedEventDays != null && AppointmentFragment.mSavedEventDays.size() > 0) {
                for (int i = 0; i < AppointmentFragment.mSavedEventDays.size(); i++) {
                    int savedEventPosition = AppointmentFragment.mSavedEventDays.get(i);
                    if (Integer.parseInt(date) == savedEventPosition) {
                        mHolder.mSavedDataImageView.setVisibility(View.VISIBLE);
                        mHolder.mSavedDataImageView.setImageResource(R.drawable.calendar_yellow_circle);
                        break;
                    } else {
                        mHolder.mDateTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                        mHolder.mDateTextView.setTypeface(null, Typeface.NORMAL);
                        mHolder.mSavedDataImageView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        } else if (CustomCalendar.mFirstDay != -1 && DailyPlannerFragment.mSavedEventDays != null && DailyPlannerFragment.mSavedEventDays.size() > 0) {
            for (int i = 0; i < DailyPlannerFragment.mSavedEventDays.size(); i++) {
                int savedEventPosition = DailyPlannerFragment.mSavedEventDays.get(i);
                if (Integer.parseInt(date) == savedEventPosition) {
                    mHolder.mSavedDataImageView.setVisibility(View.VISIBLE);
                    break;
                } else {
                    if (!currentDate.equalsIgnoreCase(date)) {
                        mHolder.mDateTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                        mHolder.mDateTextView.setTypeface(null, Typeface.NORMAL);
                        mHolder.mSavedDataImageView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }

        GridView gridView = (GridView) parent;
        if (gridView.isItemChecked(position)) {
            mHolder.mDateTextView.setTextColor(mContext.getResources().getColor(R.color.white));
        }
    }
}
