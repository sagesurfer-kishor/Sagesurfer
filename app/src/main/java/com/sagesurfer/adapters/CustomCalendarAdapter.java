package com.sagesurfer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.modules.calendar.CalenderActivity;
import com.modules.calendar.CustomCalendar;
import com.sagesurfer.collaborativecares.R;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 19-07-2017
 * Last Modified on 13-12-2017
 */

public class CustomCalendarAdapter extends BaseAdapter {

    private final Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static ViewHolder mHolder;

    private final int mWeekDayNames = 7;
    private final int mGridViewIndexOffset = 1;

    private static class ViewHolder {
        ImageView mSelectedDayImageView;
        TextView mTextView;
    }

    // Constructor
    CustomCalendarAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        if (CustomCalendar.mFirstDay != -1 && CustomCalendar.mNumDaysInMonth != -1) {
            return mWeekDayNames + CustomCalendar.mFirstDay + CustomCalendar.mNumDaysInMonth;
        }
        return mWeekDayNames;
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
                mHolder.mSelectedDayImageView = (ImageView) convertView.findViewById(R.id.material_calendar_selected_day);
                mHolder.mTextView = (TextView) convertView.findViewById(R.id.material_calendar_day);
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

        if (mHolder.mTextView != null) {
            setCalendarDay(position);
        }

        setSavedEvent(position, parent);

        return convertView;
    }


    private void setCalendarDay(int position) {
        if (position <= mWeekDayNames - mGridViewIndexOffset + CustomCalendar.mFirstDay) {
            mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
        } else {
            mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_tertiary));
        }

        switch (position) {
            case 0:
                mHolder.mTextView.setText(mContext.getResources().getString(R.string.sun));
                mHolder.mTextView.setTypeface(null, Typeface.BOLD);
                break;

            case 1:
                mHolder.mTextView.setText(mContext.getResources().getString(R.string.mon));
                mHolder.mTextView.setTypeface(null, Typeface.BOLD);
                break;

            case 2:
                mHolder.mTextView.setText(mContext.getResources().getString(R.string.tue));
                mHolder.mTextView.setTypeface(null, Typeface.BOLD);
                break;

            case 3:
                mHolder.mTextView.setText(mContext.getResources().getString(R.string.wed));
                mHolder.mTextView.setTypeface(null, Typeface.BOLD);
                break;

            case 4:
                mHolder.mTextView.setText(mContext.getResources().getString(R.string.thu));
                mHolder.mTextView.setTypeface(null, Typeface.BOLD);
                break;

            case 5:
                mHolder.mTextView.setText(mContext.getResources().getString(R.string.fri));
                mHolder.mTextView.setTypeface(null, Typeface.BOLD);
                break;

            case 6:
                mHolder.mTextView.setText(mContext.getResources().getString(R.string.sat));
                mHolder.mTextView.setTypeface(null, Typeface.BOLD);
                break;

            default:
                if (position < mWeekDayNames + CustomCalendar.mFirstDay) {
                    mHolder.mTextView.setText("");
                    mHolder.mTextView.setTypeface(Typeface.DEFAULT);
                } else {
                    mHolder.mTextView.setText(String.valueOf(position - (mWeekDayNames - mGridViewIndexOffset) -
                            CustomCalendar.mFirstDay));
                    mHolder.mTextView.setTypeface(null, Typeface.NORMAL);

                    if (CustomCalendar.mCurrentDay != -1) {
                        int startingPosition = mWeekDayNames - mGridViewIndexOffset + CustomCalendar.mFirstDay;
                        int currentDayPosition = startingPosition + CustomCalendar.mCurrentDay;

                        if (position == currentDayPosition) {
                            mHolder.mTextView.setTextColor(mContext.getResources().getColor(
                                    R.color.colorPrimary));
                            mHolder.mTextView.setTypeface(null, Typeface.BOLD);
                        } else {
                            mHolder.mTextView.setTextColor(mContext.getResources().getColor(
                                    R.color.text_color_tertiary));
                            mHolder.mTextView.setTypeface(null, Typeface.NORMAL);
                        }
                    } else {
                        mHolder.mTextView.setTextColor(mContext.getResources().getColor(
                                R.color.text_color_tertiary));
                        mHolder.mTextView.setTypeface(null, Typeface.NORMAL);
                    }
                }
                break;
        }
    }

    private void setSavedEvent(int position, ViewGroup parent) {
        if (position > mWeekDayNames - mGridViewIndexOffset + CustomCalendar.mFirstDay) {
            if (CustomCalendar.mFirstDay != -1 && CalenderActivity.mSavedEventDays != null && CalenderActivity.mSavedEventDays.size() > 0) {
                int startingPosition = mWeekDayNames - mGridViewIndexOffset + CustomCalendar.mFirstDay;
                if (position > startingPosition) {
                    for (int i = 0; i < CalenderActivity.mSavedEventDays.size(); i++) {
                        int savedEventPosition = startingPosition + CalenderActivity.mSavedEventDays.get(i);
                        if (position == savedEventPosition) {
                            mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.calendar_day_color));
                            mHolder.mTextView.setTypeface(null, Typeface.BOLD);
                        }
                    }
                } else {
                    mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_tertiary));
                    mHolder.mTextView.setTypeface(null, Typeface.NORMAL);
                }
            }
            GridView gridView = (GridView) parent;
            if (gridView.isItemChecked(position)) {
                mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.white));
            }
        }
    }
}
