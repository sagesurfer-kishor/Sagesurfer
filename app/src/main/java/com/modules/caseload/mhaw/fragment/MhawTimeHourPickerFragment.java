package com.modules.caseload.mhaw.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.modules.selfgoal.AddGoalFragmentThree;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.storage.preferences.AddGoalPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MhawTimeHourPickerFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = MhawTimeHourPickerFragment.class.getSimpleName();

    @BindView(R.id.imageview_fragmentselfgoal_timepickerdialog_cancel)
    ImageView imageViewFragmentSelfGoalTimePickerDialogCancel;
    @BindView(R.id.textview_fragmentselfgoal_timehourpickerdialog_hour)
    TextView textViewFragmentSelfGoalTimePickerDialogHour;
    @BindView(R.id.textview_fragmentselfgoal_timehourpickerdialog_colon)
    TextView textViewFragmentSelfGoalTimePickerDialogColon;
    @BindView(R.id.textview_fragmentselfgoal_timehourpickerdialog_minute)
    TextView textViewFragmentSelfGoalTimePickerDialogMinute;
    @BindView(R.id.textview_fragmentselfgoal_timehourpickerdialog_unit)
    TextView textViewFragmentSelfGoalTimePickerDialogUnit;
    @BindView(R.id.linearlayout_fragmentselfgoal_timehourpickerdialog_hour)
    LinearLayout linearLayoutFragmentSelfGoalTimePickerDialogHour;
    @BindView(R.id.textview_fragmentselfgoal_timehourpickerdialog_hour_tag)
    TextView textViewFragmentSelfGoalTimePickerDialogHourTag;
    @BindView(R.id.seekbar_fragmentselfgoal_timehourpickerdialog_hour)
    SeekBar seekBarFragmentSelfGoalTimePickerDialogHour;
    @BindView(R.id.linearlayout_fragmentselfgoal_timehourpickerdialog_minute)
    LinearLayout linearLayoutFragmentSelfGoalTimePickerDialogMinute;
    @BindView(R.id.seekbar_fragmentselfgoal_timehourpickerdialog_minute)
    SeekBar seekBarFragmentSelfGoalTimePickerDialogMinute;
    @BindView(R.id.button_fragmentselfgoal_timehourpickerdialog_done)
    Button buttonFragmentSelfGoalTimePickerDialogDone;
    @BindView(R.id.linearlayout_timepicker)
    LinearLayout linearLayoutTimePicker;
    @BindView(R.id.button_timepicker_now)
    Button buttonTimePickerNow;
    @BindView(R.id.button_timepicker_done)
    Button buttonTimePickerDone;

    private String hour = "01", minute = "15", unit = "AM";
    private String frequency = "";
    private Boolean isNotify = false, isDetails = false, isShow = false, isPeerAdd = false, isNowVisible = false, isUnitVisible = true, isMood = false;

    private MhawTimeHourPickerFragmentInterface goalDetailsInterface;
    private Unbinder unbinder;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            goalDetailsInterface = (MhawTimeHourPickerFragmentInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement GoalDetailsInterface");
        }
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.fragment_self_goal_time_hour_picker_dialog, null);
        unbinder = ButterKnife.bind(this, view);

        Bundle data = getArguments();
        if (data.containsKey(General.FREQUENCY)) {
            frequency = data.getString(General.FREQUENCY);
        } else {
            dismiss();
        }
        if (data.containsKey(General.POSITION)) {
            isNotify = true;
        }

        if (data.containsKey(General.DESCRIPTION)) {
            isDetails = true;
        }
        if (data.containsKey(General.IS_FROM_CASELOAD)) {
            isPeerAdd = true;
        }

        isShow = data.getBoolean("show");

        if (data.containsKey(General.SHOW_UNIT)) {
            isUnitVisible = false;
        }
        if (data.containsKey(General.IS_NOW_VISIBLE)) {
            isNowVisible = data.getBoolean(General.IS_NOW_VISIBLE);
            buttonFragmentSelfGoalTimePickerDialogDone.setVisibility(View.GONE);
            linearLayoutTimePicker.setVisibility(View.VISIBLE);
        }
        if (data.containsKey(General.IS_FROM_MOOD)) {
            isMood = data.getBoolean(General.IS_FROM_MOOD);
        }

        imageViewFragmentSelfGoalTimePickerDialogCancel.setOnClickListener(this);
        buttonFragmentSelfGoalTimePickerDialogDone.setOnClickListener(this);
        buttonTimePickerDone.setOnClickListener(this);
        buttonTimePickerNow.setOnClickListener(this);
        seekBarFragmentSelfGoalTimePickerDialogHour.setOnSeekBarChangeListener(onSeek);

        seekBarFragmentSelfGoalTimePickerDialogMinute.setOnSeekBarChangeListener(onSeek);

        if (frequency.equalsIgnoreCase("Hour") || frequency.equalsIgnoreCase("Hourly")) {
            minute = "00";
            seekBarFragmentSelfGoalTimePickerDialogMinute.setMax(1);
            if (isPeerAdd) {
                textViewFragmentSelfGoalTimePickerDialogHourTag.setText(getActivity().getApplicationContext().getResources().getString(R.string.start_time));
                linearLayoutFragmentSelfGoalTimePickerDialogMinute.setVisibility(View.VISIBLE);
                linearLayoutFragmentSelfGoalTimePickerDialogHour.setVisibility(View.VISIBLE);
                if (isUnitVisible) {
                    textViewFragmentSelfGoalTimePickerDialogUnit.setVisibility(View.VISIBLE);
                } else {
                    textViewFragmentSelfGoalTimePickerDialogUnit.setVisibility(View.GONE);
                }
                minute = "00";
                seekBarFragmentSelfGoalTimePickerDialogMinute.setMax(59);
            }
        } else if (frequency.equalsIgnoreCase("Minute")) {
            textViewFragmentSelfGoalTimePickerDialogHour.setVisibility(View.GONE);
            textViewFragmentSelfGoalTimePickerDialogColon.setVisibility(View.GONE);
            linearLayoutFragmentSelfGoalTimePickerDialogHour.setVisibility(View.GONE);
        } else if (isDetails || isMood) {
            textViewFragmentSelfGoalTimePickerDialogHourTag.setText(getActivity().getApplicationContext().getResources().getString(R.string.start_time));
            linearLayoutFragmentSelfGoalTimePickerDialogMinute.setVisibility(View.VISIBLE);
            linearLayoutFragmentSelfGoalTimePickerDialogHour.setVisibility(View.VISIBLE);
            if (isUnitVisible) {
                textViewFragmentSelfGoalTimePickerDialogUnit.setVisibility(View.VISIBLE);
            } else {
                textViewFragmentSelfGoalTimePickerDialogUnit.setVisibility(View.GONE);
            }
            minute = "00";
            hour = "12";
            seekBarFragmentSelfGoalTimePickerDialogHour.setMax(23);
            seekBarFragmentSelfGoalTimePickerDialogMinute.setMax(59);
        } else {
            textViewFragmentSelfGoalTimePickerDialogHourTag.setText(getActivity().getApplicationContext().getResources().getString(R.string.start_time));
            minute = "00";
            hour = "12";
            linearLayoutFragmentSelfGoalTimePickerDialogMinute.setVisibility(View.GONE);
            seekBarFragmentSelfGoalTimePickerDialogHour.setMax(23);
            if (isUnitVisible) {
                textViewFragmentSelfGoalTimePickerDialogUnit.setVisibility(View.VISIBLE);
            } else {
                textViewFragmentSelfGoalTimePickerDialogUnit.setVisibility(View.GONE);
            }
        }
        if (!isNotify) {
            minute = "00";
            if (AddGoalPreferences.contains(General.START_MINUTE)) {
                minute = AddGoalPreferences.get(General.START_MINUTE);
            }
            if (AddGoalPreferences.contains(General.START_HOUR)) {
                hour = AddGoalPreferences.get(General.START_HOUR);
            }
            if (AddGoalPreferences.contains(General.TIME_UNIT)) {
                unit = AddGoalPreferences.get(General.TIME_UNIT);
            }
        } else {
            if (AddGoalPreferences.contains(General.NOTIFY_MINUTE)) {
                minute = AddGoalPreferences.get(General.NOTIFY_MINUTE);
            }
            if (AddGoalPreferences.contains(General.NOTIFY_HOUR)) {
                hour = AddGoalPreferences.get(General.NOTIFY_HOUR);
            }
            if (AddGoalPreferences.contains(General.NOTIFY_UNIT)) {
                unit = AddGoalPreferences.get(General.NOTIFY_UNIT);
            }
        }

        textViewFragmentSelfGoalTimePickerDialogMinute.setText(minute);
        seekBarFragmentSelfGoalTimePickerDialogHour.setProgress(getHourSelected());
        seekBarFragmentSelfGoalTimePickerDialogMinute.setProgress(getMinuteSelected());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // get selected hour from time selector
    private int getHourSelected() {
        int hours = Integer.parseInt(hour);
        if (frequency.equalsIgnoreCase("Hour") || frequency.equalsIgnoreCase("Hourly") ||
                frequency.equalsIgnoreCase("Minute")) {
            if (hours == 12) {
                return 0;
            }
            if (hours != 0) {
                return hours - 1;
            }
        } else {
            if (unit.equalsIgnoreCase("am")) {
                if (hours == 12) {
                    return 0;
                } else {
                    return hours;
                }
            } else {
                if (hours == 12) {
                    return 12;
                } else {
                    return 12 + 1;
                }
            }
        }
        return 0;
    }

    // get selected minute from time selector
    private int getMinuteSelected() {
        int minutes = Integer.parseInt(minute);
        if (minutes == 15) {
            return 0;
        }
        if (minutes == 30) {
            return 1;
        }
        if (minutes == 45) {
            return 2;
        }
        if ((frequency.equalsIgnoreCase("Hour") || frequency.equalsIgnoreCase("Hourly")) && minutes == 0) {
            return 0;
        }
        return 10;
    }

    // get selected minute from time selector
    private int getMinutes(int progress) {
        if ((frequency.equalsIgnoreCase("Hour") || frequency.equalsIgnoreCase("Hourly"))
                || (!isNotify && isDetails && isPeerAdd)) { //Add Peer Note Time
            if (progress == 0) {
                return 0;
            } else {
                //return 30;
                if (isPeerAdd) {
                    return progress;
                } else {
                    return 30;
                }
            }
        } else if (frequency.equalsIgnoreCase("Minute")) {
            if (progress == 0) {
                return 15;
            } else if (progress == 1) {
                return 30;
            } else if (progress == 2) {
                return 45;
            } else {
                return 15;
            }
        }
        if (isMood) { //Add Mood Time
            return progress;
        }
        if (isDetails) {
            return progress + 1;
        }
        return 0;
    }

    // get selected hour from time selector in 12hrs format
    private int hrTime(int progress) {
        if (progress == 0) {
            return 12;
        }
        if (progress > 0 && progress <= 12) {
            unit = "AM";
            if (progress == 12) {
                unit = "PM";
            }
            return progress;
        } else {
            unit = "PM";
            return progress - 12;
        }
    }

    // get values from seek bar positions
    private final SeekBar.OnSeekBarChangeListener onSeek = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.seekbar_fragmentselfgoal_timehourpickerdialog_hour:
                    if (frequency.equalsIgnoreCase("Hour") || frequency.equalsIgnoreCase("Hourly")) {
                        hour = GetCounters.checkDigit(progress + 1);
                    } else {
                        hour = GetCounters.checkDigit(hrTime(progress));
                        if (isUnitVisible) {
                            textViewFragmentSelfGoalTimePickerDialogUnit.setText(unit);
                        } else {
                            textViewFragmentSelfGoalTimePickerDialogUnit.setText("");
                        }
                    }
                    textViewFragmentSelfGoalTimePickerDialogHour.setText(hour);
                    break;
                case R.id.seekbar_fragmentselfgoal_timehourpickerdialog_minute:
                    minute = "" + getMinutes(progress);
                    if (minute.equalsIgnoreCase("0")) {
                        minute = "00";
                    }
                    textViewFragmentSelfGoalTimePickerDialogMinute.setText(GetCounters.checkDigit(Integer.parseInt(minute)));
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        assert dialog.getWindow() != null;
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d.getWindow() != null) {
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        d.setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_fragmentselfgoal_timepickerdialog_cancel:
                dismiss();
                break;
            case R.id.button_fragmentselfgoal_timehourpickerdialog_done:
                if (isNotify) {
                    if (!isUnitVisible) {
                        unit = "";
                    }
                    AddGoalFragmentThree.goalFragmentStepThree.setTime(hour + ":" + minute + " " + unit);
                    AddGoalPreferences.save(General.NOTIFY_MINUTE, minute, TAG);
                    AddGoalPreferences.save(General.NOTIFY_HOUR, hour, TAG);
                    AddGoalPreferences.save(General.NOTIFY_UNIT, unit, TAG);
                }
                if (!isNotify && !isDetails && isPeerAdd) { //Add Peer Note Duration
                    AddGoalPreferences.save(General.START_MINUTE, GetCounters.checkDigit(Integer.parseInt(minute)), TAG);
                    AddGoalPreferences.save(General.START_HOUR, hour, TAG);
                    AddGoalPreferences.save(General.TIME_UNIT, unit, TAG);
                }
                if (!isNotify && isDetails && isPeerAdd) { //Add Peer Note Time
                    AddGoalPreferences.save(General.START_MINUTE, GetCounters.checkDigit(Integer.parseInt(minute)), TAG);
                    AddGoalPreferences.save(General.START_HOUR, hour, TAG);
                    AddGoalPreferences.save(General.TIME_UNIT, unit, TAG);
                }

                if (isShow) {
                    goalDetailsInterface.setStartTime(hour + ":" + GetCounters.checkDigit(Integer.parseInt(minute)) + ":00", unit);
                } else {
                    goalDetailsInterface.setEndTime(hour + ":" + GetCounters.checkDigit(Integer.parseInt(minute)) + ":00", unit);
                }
                dismiss();
                break;

            case R.id.button_timepicker_done:
                if (isShow) {
                    goalDetailsInterface.setStartTime(hour + ":" + GetCounters.checkDigit(Integer.parseInt(minute)) + ":00", unit);
                } else {
                    goalDetailsInterface.setEndTime(hour + ":" + GetCounters.checkDigit(Integer.parseInt(minute)) + ":00", unit);
                }
                dismiss();
                break;

            case R.id.button_timepicker_now:
                if (isDetails) {
                    Calendar calender = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                    String time = simpleDateFormat.format(calender.getTime());

                    String currentString = time;
                    String[] separated = currentString.split(" ");
                    separated[0] = separated[0].trim();
                    separated[1] = separated[1].trim();

                    if (isShow) {
                        goalDetailsInterface.setStartTime(separated[0] + ":00 ", separated[1]);
                    } else {
                        goalDetailsInterface.setEndTime(separated[0] + ":00 ", separated[1]);
                    }
                }
                dismiss();
                break;
        }
    }

    public interface MhawTimeHourPickerFragmentInterface {
        void setStartTime(String time, String unit);

        void setEndTime(String time, String unit);
    }
}
