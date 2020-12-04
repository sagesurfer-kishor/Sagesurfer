package com.modules.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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

import com.modules.appointment.fragments.AppointmentFragment;
import com.modules.planner.DailyPlannerFragment;
import com.modules.selfgoal.LogBookActivity;
import com.modules.selfgoal.LogBookDotsModel;
import com.modules.selfgoal.werhope_self_goal.activity.GoalLogBookActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.storage.preferences.Preferences;

import java.util.ArrayList;

/**
 * @author Kailash Karankal
 */

public class CustomCalendarAdapter extends BaseAdapter {
    private final Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static ViewHolder mHolder;
    private final int mWeekDayNames = 7;
    private final int mGridViewIndexOffset = 1;
    private ArrayList<LogBookDotsModel> mlogBookDotsModelArrayList;

    private static class ViewHolder {
        ImageView mSelectedDayImageView, mSavedDataImageView;
        TextView mTextView;
    }

    // Constructor
    public CustomCalendarAdapter(Context context) {
        mContext = context;
    }

    public CustomCalendarAdapter(Context context, ArrayList<LogBookDotsModel> logBookDotsModelArrayList) {
        mContext = context;
        mlogBookDotsModelArrayList = logBookDotsModelArrayList;
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
                mHolder.mSavedDataImageView = (ImageView) convertView.findViewById(R.id.material_calendar_saved_data);
                mHolder.mTextView = (TextView) convertView.findViewById(R.id.material_calendar_day);
                mHolder.mSavedDataImageView.setVisibility(View.INVISIBLE);
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

        mHolder.mSavedDataImageView.setVisibility(View.INVISIBLE);
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
                    mHolder.mTextView.setText(String.valueOf(position - (mWeekDayNames - mGridViewIndexOffset) - CustomCalendar.mFirstDay));
                    mHolder.mTextView.setTypeface(null, Typeface.NORMAL);

                    if (CustomCalendar.mCurrentDay != -1) {
                        int startingPosition = mWeekDayNames - mGridViewIndexOffset + CustomCalendar.mFirstDay;
                        int currentDayPosition = startingPosition + CustomCalendar.mCurrentDay;

                        if (position == currentDayPosition) {
                            mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                            mHolder.mTextView.setTypeface(null, Typeface.BOLD);
                        } else {
                            mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                            mHolder.mTextView.setTypeface(null, Typeface.NORMAL);
                        }
                    } else {
                        mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                        mHolder.mTextView.setTypeface(null, Typeface.NORMAL);
                    }
                }
                break;
        }
    }

    private void setSavedEvent(int position, ViewGroup parent) {
        if (position > mWeekDayNames - mGridViewIndexOffset + CustomCalendar.mFirstDay) {
            if (Preferences.getBoolean(General.IS_FROM_CASELOAD)) {
                if (CustomCalendar.mFirstDay != -1 && CalenderActivity.mSavedEventDays != null && CalenderActivity.mSavedEventDays.size() > 0) {
                    int startingPosition = mWeekDayNames - mGridViewIndexOffset + CustomCalendar.mFirstDay;
                    if (position > startingPosition) {
                        for (int i = 0; i < CalenderActivity.mSavedEventDays.size(); i++) {
                            int savedEventPosition = startingPosition + CalenderActivity.mSavedEventDays.get(i);
                            if (position == savedEventPosition) {
                                mHolder.mSavedDataImageView.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                        mHolder.mTextView.setTypeface(null, Typeface.NORMAL);
                        mHolder.mSavedDataImageView.setVisibility(View.INVISIBLE);
                    }
                }
            } else if (Preferences.getBoolean(Actions_.GET_LOG)) {
                if (CustomCalendar.mFirstDay != -1 && GoalLogBookActivity.completeDays != null && GoalLogBookActivity.completeDays.size() > 0) {
                    int startingPosition = mWeekDayNames - mGridViewIndexOffset + CustomCalendar.mFirstDay;
                    if (position > startingPosition) {
                        for (int i = 0; i < GoalLogBookActivity.completeDays.size(); i++) {
                            int savedEventPosition = startingPosition + GoalLogBookActivity.completeDays.get(i);
                            if (position == savedEventPosition) {
                                mHolder.mSavedDataImageView.setVisibility(View.VISIBLE);
                                mHolder.mSavedDataImageView.setImageResource(R.drawable.cc_status_online);
                            }
                        }
                    } else {
                        mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                        mHolder.mTextView.setTypeface(null, Typeface.NORMAL);
                        mHolder.mSavedDataImageView.setVisibility(View.INVISIBLE);
                    }
                }


                if (CustomCalendar.mFirstDay != -1 && GoalLogBookActivity.missedDays != null && GoalLogBookActivity.missedDays.size() > 0) {
                    int startingPosition = mWeekDayNames - mGridViewIndexOffset + CustomCalendar.mFirstDay;
                    if (position > startingPosition) {
                        for (int i = 0; i < GoalLogBookActivity.missedDays.size(); i++) {
                            int savedEventPosition = startingPosition + GoalLogBookActivity.missedDays.get(i);
                            if (position == savedEventPosition) {
                                mHolder.mSavedDataImageView.setVisibility(View.VISIBLE);
                                mHolder.mSavedDataImageView.setImageResource(R.drawable.cc_status_busy);
                            }
                        }
                    } else {
                        mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                        mHolder.mTextView.setTypeface(null, Typeface.NORMAL);
                        mHolder.mSavedDataImageView.setVisibility(View.INVISIBLE);
                    }
                }

                if (CustomCalendar.mFirstDay != -1 && GoalLogBookActivity.pendingDays != null && GoalLogBookActivity.pendingDays.size() > 0) {
                    int startingPosition = mWeekDayNames - mGridViewIndexOffset + CustomCalendar.mFirstDay;
                    if (position > startingPosition) {
                        for (int i = 0; i < GoalLogBookActivity.pendingDays.size(); i++) {
                            int savedEventPosition = startingPosition + GoalLogBookActivity.pendingDays.get(i);
                            if (position == savedEventPosition) {
                                mHolder.mSavedDataImageView.setVisibility(View.VISIBLE);
                                mHolder.mSavedDataImageView.setImageResource(R.drawable.offline_circle);
                            }
                        }
                    } else {
                        mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                        mHolder.mTextView.setTypeface(null, Typeface.NORMAL);
                        mHolder.mSavedDataImageView.setVisibility(View.INVISIBLE);
                    }
                }

            }else if (Preferences.getBoolean(Actions_.GET_LOGBOOK_DOTS)){

                if (CustomCalendar.mFirstDay != -1 && mlogBookDotsModelArrayList != null && mlogBookDotsModelArrayList.size() > 0) {
                    int startingPosition = mWeekDayNames - mGridViewIndexOffset + CustomCalendar.mFirstDay;
                    if (position > startingPosition) {
                        for (int i = 0; i < mlogBookDotsModelArrayList.size(); i++) {
                            int savedEventPosition = startingPosition + Integer.parseInt(mlogBookDotsModelArrayList.get(i).getDate());
                            if (position == savedEventPosition) {
                                mHolder.mSavedDataImageView.setVisibility(View.VISIBLE);
                                mHolder.mSavedDataImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_log_dot));
                                int color = Color.parseColor(mlogBookDotsModelArrayList.get(i).getColor());
                                mHolder.mSavedDataImageView.setColorFilter(color);
                            }
                        }
                    } else {
                        mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                        mHolder.mTextView.setTypeface(null, Typeface.NORMAL);
                        mHolder.mSavedDataImageView.setVisibility(View.INVISIBLE);
                    }
                }

            }
            else if (Preferences.getBoolean(General.IS_FROM_DAILYPLANNER)) {
                if (CustomCalendar.mFirstDay != -1 && DailyPlannerFragment.mSavedEventDays != null && DailyPlannerFragment.mSavedEventDays.size() > 0) {
                    int startingPosition = mWeekDayNames - mGridViewIndexOffset + CustomCalendar.mFirstDay;
                    if (position > startingPosition) {
                        for (int i = 0; i < DailyPlannerFragment.mSavedEventDays.size(); i++) {
                            int savedEventPosition = startingPosition + DailyPlannerFragment.mSavedEventDays.get(i);
                            if (position == savedEventPosition) {
                                mHolder.mSavedDataImageView.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                        mHolder.mTextView.setTypeface(null, Typeface.NORMAL);
                        mHolder.mSavedDataImageView.setVisibility(View.INVISIBLE);
                    }
                }
            } else if (Preferences.getBoolean(Actions_.APPOINTMENT_COUNTER)) {
                if (CustomCalendar.mFirstDay != -1 && AppointmentFragment.mSavedEventDays != null && AppointmentFragment.mSavedEventDays.size() > 0) {
                    int startingPosition = mWeekDayNames - mGridViewIndexOffset + CustomCalendar.mFirstDay;
                    if (position > startingPosition) {
                        for (int i = 0; i < AppointmentFragment.mSavedEventDays.size(); i++) {
                            int savedEventPosition = startingPosition + AppointmentFragment.mSavedEventDays.get(i);
                            if (position == savedEventPosition) {
                                mHolder.mSavedDataImageView.setVisibility(View.VISIBLE);
                                mHolder.mSavedDataImageView.setImageResource(R.drawable.calendar_yellow_circle);
                            }
                        }
                    } else {
                        mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                        mHolder.mTextView.setTypeface(null, Typeface.NORMAL);
                        mHolder.mSavedDataImageView.setVisibility(View.INVISIBLE);
                    }
                }
            } else {
                if (CustomCalendar.mFirstDay != -1 && CalendarFragment.mSavedEventDays != null && CalendarFragment.mSavedEventDays.size() > 0) {
                    int startingPosition = mWeekDayNames - mGridViewIndexOffset + CustomCalendar.mFirstDay;
                    if (position > startingPosition) {
                        for (int i = 0; i < CalendarFragment.mSavedEventDays.size(); i++) {
                            int savedEventPosition = startingPosition + CalendarFragment.mSavedEventDays.get(i);
                            if (position == savedEventPosition) {
                                mHolder.mSavedDataImageView.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.text_color_primary));
                        mHolder.mTextView.setTypeface(null, Typeface.NORMAL);
                        mHolder.mSavedDataImageView.setVisibility(View.INVISIBLE);
                    }
                }
            }

            GridView gridView = (GridView) parent;
            if (gridView.isItemChecked(position)) {
                mHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.white));
            }
        }
    }
}
