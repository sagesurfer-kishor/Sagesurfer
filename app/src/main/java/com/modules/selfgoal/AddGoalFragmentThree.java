package com.modules.selfgoal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.interfaces.AddGoalActivityInterface;
import com.sagesurfer.library.ArrayOperations;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.AddGoalPreferences;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.RequestBody;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 24/03/2018
 * Last Modified on
 */

public class AddGoalFragmentThree extends Fragment implements View.OnClickListener {

    private static final String TAG = AddGoalFragmentThree.class.getSimpleName();

    @BindView(R.id.textview_addgoalactivity_bubble_two)
    TextView textViewAddGoalActivityBubbleTwo;
    @BindView(R.id.relativelayout_fragmentaddgoalthree_milestone)
    RelativeLayout relativeLayoutFragmentAddGoalThreeMilestone;
    @BindView(R.id.edittext_fragmentaddgoalthree_milestonetext)
    EditText editTextFragmentAddGoalThreeMilestoneText;
    @BindView(R.id.textview_fragmentaddgoalthree_milestonedate)
    TextView textViewFragmentAddGoalThreeMilestoneDate;
    @BindView(R.id.button_fragmentaddgoalthree_milestoneadd)
    Button buttonFragmentAddGoalThreeMilestoneAdd;
    @BindView(R.id.linearlayout_fragmentaddgoalthree_milestoneitem)
    LinearLayout linearLayoutFragmentAddGoalThreeMilestoneItem;
    @BindView(R.id.textview_fragmentaddgoalthree_notifyfrequency)
    TextView textViewFragmentAddGoalThreeNotifyFrequency;
    @BindView(R.id.linearlayout_fragmentaddgoalthree_frequencyweek)
    LinearLayout linearLayoutFragmentAddGoalThreeFrequencyWeek;
    @BindView(R.id.checkedtextview_fragmentaddgoalthree_weekmon)
    CheckedTextView checkedTextViewFragmentAddGoalThreeWeekMon;
    @BindView(R.id.checkedtextview_fragmentaddgoalthree_weektue)
    CheckedTextView checkedTextViewFragmentAddGoalThreeWeekTue;
    @BindView(R.id.checkedtextview_fragmentaddgoalthree_weekwed)
    CheckedTextView checkedTextViewFragmentAddGoalThreeWeekWed;
    @BindView(R.id.checkedtextview_fragmentaddgoalthree_weekthu)
    CheckedTextView checkedTextViewFragmentAddGoalThreeWeekThu;
    @BindView(R.id.checkedtextview_fragmentaddgoalthree_weekfri)
    CheckedTextView checkedTextViewFragmentAddGoalThreeWeekFri;
    @BindView(R.id.checkedtextview_fragmentaddgoalthree_weeksat)
    CheckedTextView checkedTextViewFragmentAddGoalThreeWeekSat;
    @BindView(R.id.checkedtextview_fragmentaddgoalthree_weeksun)
    CheckedTextView checkedTextViewFragmentAddGoalThreeWeekSun;
    @BindView(R.id.textview_fragmentaddgoalthree_notifyat)
    TextView textViewFragmentAddGoalThreeNotifyAt;
    @BindView(R.id.checkedtextview_fragmentaddgoalthree_creategoalspecificday)
    CheckedTextView checkedTextViewFragmentAddGoalCreateGoalSpecificDay;

    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String mileStoneDate = "", start_date = "", end_date = "", frequency_notify = "";
    private ArrayList<String> milestoneNameArray, milestoneDateArray, milestoneIdArray, deleteId;

    @SuppressLint("StaticFieldLeak")
    public static AddGoalFragmentThree goalFragmentStepThree;
    private Activity activity;
    private BroadcastReceiver receiver;
    private AddGoalActivityInterface addGoalActivityInterface;
    private Unbinder unbinder;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            addGoalActivityInterface = (AddGoalActivityInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AddGoalActivityInterface");
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.fragment_add_goal_three, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();
        goalFragmentStepThree = this;

        milestoneNameArray = new ArrayList<>();
        milestoneDateArray = new ArrayList<>();
        milestoneIdArray = new ArrayList<>();
        deleteId = new ArrayList<>();

        textViewAddGoalActivityBubbleTwo.setOnClickListener(this);
        textViewFragmentAddGoalThreeMilestoneDate.setOnClickListener(this);
        textViewFragmentAddGoalThreeNotifyAt.setOnClickListener(this);
        textViewFragmentAddGoalThreeNotifyFrequency.setOnClickListener(this);
        checkedTextViewFragmentAddGoalCreateGoalSpecificDay.setOnClickListener(this);
        buttonFragmentAddGoalThreeMilestoneAdd.setOnClickListener(this);

        /*VectorDrawableCompat milestoneDateDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_angle_arrow_down, textViewFragmentAddGoalThreeMilestoneDate.getContext().getTheme());
        textViewFragmentAddGoalThreeMilestoneDate.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, milestoneDateDrawable, null);

        VectorDrawableCompat frequencyDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_angle_arrow_down, textViewFragmentAddGoalThreeNotifyFrequency.getContext().getTheme());
        textViewFragmentAddGoalThreeNotifyFrequency.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, frrequencyDrawable, null);

        VectorDrawableCompat notifyAtDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_angle_arrow_down, textViewFragmentAddGoalThreeNotifyAt.getContext().getTheme());
        textViewFragmentAddGoalThreeNotifyAt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, notifyAtDrawable, null);*/
        Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        checkedTextViewFragmentAddGoalThreeWeekSun.setOnClickListener(this);
        checkedTextViewFragmentAddGoalThreeWeekSat.setOnClickListener(this);
        checkedTextViewFragmentAddGoalThreeWeekFri.setOnClickListener(this);
        checkedTextViewFragmentAddGoalThreeWeekThu.setOnClickListener(this);
        checkedTextViewFragmentAddGoalThreeWeekWed.setOnClickListener(this);
        checkedTextViewFragmentAddGoalThreeWeekTue.setOnClickListener(this);
        checkedTextViewFragmentAddGoalThreeWeekMon.setOnClickListener(this);

        activity.registerReceiver(receiver, new IntentFilter("2"));
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(General.CHECK_IN)) {
                    if (intent.getStringExtra(General.POSITION).equalsIgnoreCase("2")) {
                        saveData(true);
                    }
                }
            }
        };
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (receiver != null)
            activity.unregisterReceiver(receiver);
    }

    // set milestone view from shared preferences
    private void setMileStone() {
        if (AddGoalPreferences.contains(General.MILESTONE_DATE) && AddGoalPreferences.contains(General.MILESTONE)) {
            String milestone = AddGoalPreferences.get(General.MILESTONE);
            String milestone_date = AddGoalPreferences.get(General.MILESTONE_DATE);
            if (milestone != null && milestone_date != null) {
                if (milestone.trim().length() <= 0 || milestone_date.trim().length() <= 0) {
                    return;
                }
                ArrayList<String> mile_list = new ArrayList<>(
                        Arrays.asList(AddGoalPreferences.get(General.MILESTONE).split(",")));
                ArrayList<String> mile_date_list = new ArrayList<>(
                        Arrays.asList(AddGoalPreferences.get(General.MILESTONE_DATE).split(",")));
                ArrayList<String> mile_id_list = new ArrayList<>(
                        Arrays.asList(AddGoalPreferences.get(General.MILESTONE_ID).split(",")));
                milestoneNameArray = mile_list;
                milestoneDateArray = mile_date_list;
                milestoneIdArray = mile_id_list;
                if (milestoneDateArray.size() > 0 && milestoneNameArray.size() > 0) {
                    for (int i = 0; i < milestoneDateArray.size(); i++) {
                        addLayout(milestoneIdArray.get(i), milestoneNameArray.get(i),
                                milestoneDateArray.get(i), true, i);
                    }
                }
            }
        }
    }

    // set notify frequency from shared preferences
    private void setNotifyFrequency() {
        String frequency = AddGoalPreferences.get(General.FREQUENCY);
        if (frequency != null) {
            if (frequency.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.hour))) {
                frequency_notify = activity.getApplicationContext().getResources().getString(R.string.hour);
                textViewFragmentAddGoalThreeNotifyFrequency.setText(frequency_notify);
                textViewFragmentAddGoalThreeNotifyFrequency.setEnabled(false);
            }
            if (frequency.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.weekly))) {
                frequency_notify = activity.getApplicationContext().getResources().getString(R.string.weekly);
                textViewFragmentAddGoalThreeNotifyFrequency.setText(frequency_notify);
                textViewFragmentAddGoalThreeNotifyFrequency.setEnabled(false);
            }
            if (frequency.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.daily))) {
                frequency_notify = activity.getApplicationContext().getResources().getString(R.string.daily);
                textViewFragmentAddGoalThreeNotifyFrequency.setEnabled(true);
                textViewFragmentAddGoalThreeNotifyFrequency.setText("");
            }
            textViewFragmentAddGoalThreeNotifyAt.setText("");
            toggleFrequencyLayout();
        }
    }

    public void setTime(String time) {
        textViewFragmentAddGoalThreeNotifyAt.setText(time);
    }

    // open frequency selector pop up
    private void showFrequency() {
        final PopupMenu popup = new PopupMenu(activity, textViewFragmentAddGoalThreeNotifyFrequency);
        popup.getMenuInflater().inflate(R.menu.menu_add_goal_freq_notify, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menuitem_add_goal_frequency_select) {
                    frequency_notify = "";
                } else {
                    frequency_notify = item.getTitle().toString();
                }
                popup.dismiss();
                textViewFragmentAddGoalThreeNotifyFrequency.setText(frequency_notify);
                toggleFrequencyLayout();
                return true;
            }
        });
        popup.show();
    }

    // open week/week-day selector based on notify frequency
    private void toggleFrequencyLayout() {
        if (frequency_notify.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.hour))) {
            textViewFragmentAddGoalThreeNotifyAt.setHint(activity.getApplicationContext().getResources().getString(R.string.select_hours));
            linearLayoutFragmentAddGoalThreeFrequencyWeek.setVisibility(View.GONE);
            toggleTime();
        } else if (frequency_notify.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.daily))) {
            textViewFragmentAddGoalThreeNotifyAt.setHint(activity.getApplicationContext().getResources().getString(R.string.select_hours));
            linearLayoutFragmentAddGoalThreeFrequencyWeek.setVisibility(View.GONE);
        } else if (frequency_notify.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.weekly))) {
            linearLayoutFragmentAddGoalThreeFrequencyWeek.setVisibility(View.VISIBLE);
            String[] todayList = {Compare.dayOfWeekShort()};
            setWeekSelection(todayList);
        } else {
            linearLayoutFragmentAddGoalThreeFrequencyWeek.setVisibility(View.GONE);
        }
    }

    private void toggleTime() {
        textViewFragmentAddGoalThreeNotifyAt.setText("");
        if (frequency_notify.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.hour))) {
            textViewFragmentAddGoalThreeNotifyAt.setHint(activity.getApplicationContext().getResources().getString(R.string.select_hours));
        } else {
            textViewFragmentAddGoalThreeNotifyAt.setHint(activity.getApplicationContext().getResources().getString(R.string.select_minutes));
        }
    }

    // retrieve selected days from week
    private boolean isDayCheck(int _id, boolean isReset) {
        int[] dayCheckId = {
                R.id.checkedtextview_fragmentaddgoalthree_weeksun,
                R.id.checkedtextview_fragmentaddgoalthree_weeksat,
                R.id.checkedtextview_fragmentaddgoalthree_weekfri,
                R.id.checkedtextview_fragmentaddgoalthree_weekthu,
                R.id.checkedtextview_fragmentaddgoalthree_weekwed,
                R.id.checkedtextview_fragmentaddgoalthree_weektue,
                R.id.checkedtextview_fragmentaddgoalthree_weekmon
        };
        CheckedTextView[] dayCheck = {
                checkedTextViewFragmentAddGoalThreeWeekSun, checkedTextViewFragmentAddGoalThreeWeekSat, checkedTextViewFragmentAddGoalThreeWeekFri, checkedTextViewFragmentAddGoalThreeWeekThu, checkedTextViewFragmentAddGoalThreeWeekWed, checkedTextViewFragmentAddGoalThreeWeekTue, checkedTextViewFragmentAddGoalThreeWeekMon};
        if (isReset) {
            for (CheckedTextView aDayCheck : dayCheck) {
                aDayCheck.setChecked(true);
            }
            return false;
        }
        int i = 0;
        for (int id : dayCheckId) {
            if (id == _id) {
                if (dayCheck[i].isChecked()) {
                    dayCheck[i].setChecked(false);
                } else {
                    dayCheck[i].setChecked(true);
                }
                return true;
            }
            i++;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (isDayCheck(v.getId(), false)) {
            return;
        }
        switch (v.getId()) {
            case R.id.textview_addgoalactivity_bubble_two:
                addGoalActivityInterface.moveToNext(true, 1);
                break;
            case R.id.textview_fragmentaddgoalthree_notifyfrequency:
                showFrequency();
                break;
            case R.id.textview_fragmentaddgoalthree_notifyat:
                if (frequency_notify.trim().length() <= 0) {
                    ShowSnack.viewWarning(textViewFragmentAddGoalThreeNotifyFrequency, "Select measurement frequency first",
                            activity.getApplicationContext());
                } else {
                    openDialog();
                }
                break;
            case R.id.button_fragmentaddgoalthree_milestoneadd:
                final String milestone_name = editTextFragmentAddGoalThreeMilestoneText.getText().toString().trim();
                if (milestone_name.length() <= 0) {
                    editTextFragmentAddGoalThreeMilestoneText.setError("Enter Valid milestone");
                    return;
                }
                if (mileStoneDate == null || mileStoneDate.length() <= 0) {
                    ShowToast.toast("Enter Valid Milestone Date", activity.getApplicationContext());
                    return;
                }
                editTextFragmentAddGoalThreeMilestoneText.setText("");
                textViewFragmentAddGoalThreeMilestoneDate.setText("");
                //textViewFragmentAddGoalThreeMilestoneDate.setHint(activity.getApplicationContext().getResources().getString(R.string.date));
                addLayout("0", milestone_name, mileStoneDate, false, 1);
                break;
            case R.id.checkedtextview_fragmentaddgoalthree_creategoalspecificday:
                if (checkedTextViewFragmentAddGoalCreateGoalSpecificDay.isChecked()) {
                    checkedTextViewFragmentAddGoalCreateGoalSpecificDay.setChecked(false);
                } else {
                    checkedTextViewFragmentAddGoalCreateGoalSpecificDay.setChecked(true);
                }
                break;
            case R.id.textview_fragmentaddgoalthree_milestonedate:
                //textViewFragmentAddGoalThreeMilestoneDate.setText(activity.getApplicationContext().getResources().getString(R.string.date));
                DatePickerDialog milestoneDatePicker = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                mileStoneDate = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                try {
                                    int result = compareDate(mYear + "-" + (mMonth + 1) + "-" + mDay,
                                            mileStoneDate);
                                    if (result == 1) {
                                        mileStoneDate = null;
                                        textViewFragmentAddGoalThreeMilestoneDate.setText("");
                                        //textViewFragmentAddGoalThreeMilestoneDate.setHint(activity.getApplicationContext().getResources().getString(R.string.date));
                                        ShowToast.toast("Invalid Date", activity.getApplicationContext());
                                    } else {
                                        if (Compare.dateInRange(start_date, end_date, mileStoneDate)) {
                                            textViewFragmentAddGoalThreeMilestoneDate.setText(GetTime.month_DdYyyy(mileStoneDate));
                                        } else {
                                            mileStoneDate = null;
                                            //textViewFragmentAddGoalThreeMilestoneDate.setHint(activity.getApplicationContext().getResources().getString(R.string.date));
                                            textViewFragmentAddGoalThreeMilestoneDate.setText("");
                                            ShowToast.toast("Date out of range",
                                                    activity.getApplicationContext());
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                milestoneDatePicker.show();
                break;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private int compareDate(String today, String selected_date) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        Date date1 = dateFormat.parse(today);
        Date date2 = dateFormat.parse(selected_date);

        calendar1.setTime(date1);
        calendar2.setTime(date2);

        return calendar1.compareTo(calendar2);
    }

    // enable/disable milestone layout
    private void toggleMilestoneLayout() {
        if (milestoneNameArray.size() == 10) {//Nirmal Bug report 10_11July_2018, size changed from 99 to 10, like on web platform
            relativeLayoutFragmentAddGoalThreeMilestone.setVisibility(View.GONE);
        } else {
            relativeLayoutFragmentAddGoalThreeMilestone.setVisibility(View.VISIBLE);
        }
    }

    private boolean isRemoved(String id) {
        for (String _id : deleteId) {
            if (!id.equalsIgnoreCase("0")) {
                if (id.equalsIgnoreCase(_id)) {
                    return true;
                }
            }
        }
        return false;
    }

    // add milestone row item to layout
    private void addLayout(String id, String milestone, String date, boolean isOld, int position) {

        if (isRemoved(id)) {
            return;
        }
        final View layout2 = LayoutInflater.from(activity).inflate(R.layout.self_goal_details_milestone_item,
                linearLayoutFragmentAddGoalThreeMilestoneItem, false);
        TextView nameTag = (TextView) layout2.findViewById(R.id.selfgoaldetails_milestone_item_name);
        TextView dateTag = (TextView) layout2.findViewById(R.id.selfgoaldetails_milestone_item_date);
        ImageView cancel = (ImageView) layout2.findViewById(R.id.selfgoaldetails_milestone_item_close);

        if (isOld) {
            cancel.setTag(position);
        } else {
            milestoneNameArray.add(milestone);
            milestoneDateArray.add(date);
            milestoneIdArray.add(id);
            cancel.setTag(milestoneDateArray.size() - 1);
        }

        nameTag.setText(milestone);
        if (AddGoalPreferences.contains(General.ACTION) && !AddGoalPreferences.get(
                General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.add_goal))) {
            dateTag.setText(GetTime.month_DdYyyy(date));
        } else {
            dateTag.setText(GetTime.month_DdYyyy(date));
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                if (position >= milestoneIdArray.size()) {
                    position = position - 1;
                }
                deleteId.add(milestoneIdArray.get(position));
                milestoneNameArray.remove(position);
                milestoneDateArray.remove(position);
                milestoneIdArray.remove(position);
                linearLayoutFragmentAddGoalThreeMilestoneItem.removeViewAt(position);
                toggleMilestoneLayout();
            }
        });
        linearLayoutFragmentAddGoalThreeMilestoneItem.addView(layout2);
        toggleMilestoneLayout();
    }

    // open time picker dialog for month
    @SuppressLint("CommitTransaction")
    private void openDialog() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment frag = getFragmentManager().findFragmentByTag(General.TIMESTAMP);
        if (frag != null) {
            fragmentTransaction.remove(frag);
        }
        fragmentTransaction.addToBackStack(null);
        TimeHourPickerDialogFragment dialogFrag = new TimeHourPickerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(General.FREQUENCY, frequency_notify);
        bundle.putString(General.POSITION, "3");

        if (frequency_notify.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.hour))) {
            bundle.putBoolean(General.SHOW_UNIT, false);
        }

        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager(), General.TIMESTAMP);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.registerReceiver(receiver, new IntentFilter("2"));
    }

    // save required data to shared preferences
    private void saveData(boolean isCreate) {
        AddGoalPreferences.initialize(getActivity().getApplicationContext());
        AddGoalPreferences.save(General.REQUEST_SEND, checkedTextViewFragmentAddGoalCreateGoalSpecificDay.isChecked());

        String milestone_names = "";
        if (milestoneNameArray.size() >= 0) {
            milestone_names = ArrayOperations.stringListToString(milestoneNameArray);
        }
        String milestone_dates = "";
        if (milestoneDateArray.size() >= 0) {
            milestone_dates = ArrayOperations.stringListToString(milestoneDateArray);
        }
        String milestone_id = "";
        if (milestoneIdArray.size() >= 0) {
            milestone_id = ArrayOperations.stringListToString(milestoneIdArray);
        }

        String delete_milestone_id = "";
        if (deleteId.size() >= 0) {
            delete_milestone_id = ArrayOperations.stringListToString(deleteId);
        }
        int notify = 1;
        if (!checkedTextViewFragmentAddGoalCreateGoalSpecificDay.isChecked()) {
            notify = 0;
        }
        AddGoalPreferences.save(General.MILESTONE, milestone_names, TAG);
        AddGoalPreferences.save(General.MILESTONE_DATE, milestone_dates, TAG);
        AddGoalPreferences.save(General.MILESTONE_ID, milestone_id, TAG);
        AddGoalPreferences.save("del_mile_id", delete_milestone_id, TAG);
        if (frequency_notify.trim().length() > 0) {
            AddGoalPreferences.save("checked_noti", getSelectedWeek(), TAG);
        } else {
            AddGoalPreferences.save("checked_noti", "0", TAG);
        }
        AddGoalPreferences.save(General.NOTE, "" + notify, TAG);
        addGoalActivityInterface.createGoal(isCreate);
    }

    // get selected frequency units in desired format
    private String getSelectedWeek() {
        if (frequency_notify.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.hour))) {
            return "hour";
        } else if (frequency_notify.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.daily))) {
            return "day";
        }
        ArrayList<String> dayList = new ArrayList<>();
        if (checkedTextViewFragmentAddGoalThreeWeekMon.isChecked()) {
            dayList.add(activity.getApplicationContext().getResources().getString(R.string.mon));
        }
        if (checkedTextViewFragmentAddGoalThreeWeekTue.isChecked()) {
            dayList.add(activity.getApplicationContext().getResources().getString(R.string.tue));
        }
        if (checkedTextViewFragmentAddGoalThreeWeekWed.isChecked()) {
            dayList.add(activity.getApplicationContext().getResources().getString(R.string.wed));
        }
        if (checkedTextViewFragmentAddGoalThreeWeekThu.isChecked()) {
            dayList.add(activity.getApplicationContext().getResources().getString(R.string.thu));
        }
        if (checkedTextViewFragmentAddGoalThreeWeekFri.isChecked()) {
            dayList.add(activity.getApplicationContext().getResources().getString(R.string.fri));
        }
        if (checkedTextViewFragmentAddGoalThreeWeekSat.isChecked()) {
            dayList.add(activity.getApplicationContext().getResources().getString(R.string.sat));
        }
        if (checkedTextViewFragmentAddGoalThreeWeekSun.isChecked()) {
            dayList.add(activity.getApplicationContext().getResources().getString(R.string.sun));
        }
        if (dayList.size() > 0) {
            return "week-" + ArrayOperations.stringListToString(dayList).replaceAll(",",
                    "-").toLowerCase();
        }
        return "week-" + GetTime.todaysDate().toLowerCase();
    }

    // make network call to fetch goal milestone
    private void getMilestoneList() {
        linearLayoutFragmentAddGoalThreeMilestoneItem.removeAllViews();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.MILESTONE_LIST);
        requestMap.put(General.ID, AddGoalPreferences.get(General.ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        String response;
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                JSONObject jsonObject = new JSONObject(response);
                if (response != null && !response.equalsIgnoreCase("13")) {
                    JSONArray milestoneArray = jsonObject.getJSONArray(Actions_.MILESTONE_LIST);
                    for (int i = 0; i < milestoneArray.length(); i++) {
                        JSONObject object = milestoneArray.getJSONObject(i);
                        if (object.getInt(General.STATUS) == 1) {
                            String id = object.getString(General.ID);
                            String name = object.getString(General.NAME);
                            String date = object.getString("date");
                            addLayout(id, name, date, false, i);
                        }
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ShowToast.toast(activity.getApplicationContext().getResources().getString(R.string.internal_error_occurred),
                activity.getApplicationContext());
    }

    private void setData() {
        if (!AddGoalPreferences.get(General.ACTION).equalsIgnoreCase("reschedule")) {
            if (milestoneIdArray.size() <= 0) {
                getMilestoneList();
            }
        }
        if (AddGoalPreferences.get(General.ACTION).equalsIgnoreCase("edit")) {
            if (AddGoalPreferences.get("notify_at").trim().length() > 0) {
                String[] time = AddGoalPreferences.get("notify_at").split(":");
                if (!time[0].equalsIgnoreCase("00") && time[1].equalsIgnoreCase("00")) {
                    String unit = getTimeUnit(Integer.parseInt(time[0]));
                    AddGoalPreferences.save(General.NOTIFY_MINUTE, time[1], TAG);
                    AddGoalPreferences.save(General.NOTIFY_HOUR, "" + hrTime(Integer.parseInt(time[0]), unit), TAG);
                    AddGoalPreferences.save(General.NOTIFY_UNIT, unit, TAG);
                    setTime(hrTime(Integer.parseInt(time[0]), unit) + ":" + time[1] + " " + unit);
                }
            }
            if (!AddGoalPreferences.get("notify_frequency").equalsIgnoreCase("0")) {
                String[] notify_frequency = AddGoalPreferences.get("notify_frequency").split("-");
                frequency_notify = getFrequency(notify_frequency[0]);
                textViewFragmentAddGoalThreeNotifyFrequency.setText(frequency_notify);
                toggleFrequencyLayout();
                setWeekSelection(notify_frequency);
            }
        }
    }

    // set week selection to respective check boxes based on previous choices
    private void setWeekSelection(String[] notify_frequency) {
        for (String aNotify_frequency : notify_frequency) {
            if (activity.getApplicationContext().getResources().getString(R.string.mon).equalsIgnoreCase(aNotify_frequency)) {
                checkedTextViewFragmentAddGoalThreeWeekMon.setChecked(true);
            }
            if (activity.getApplicationContext().getResources().getString(R.string.tue).equalsIgnoreCase(aNotify_frequency)) {
                checkedTextViewFragmentAddGoalThreeWeekTue.setChecked(true);
            }
            if (activity.getApplicationContext().getResources().getString(R.string.wed).equalsIgnoreCase(aNotify_frequency)) {
                checkedTextViewFragmentAddGoalThreeWeekWed.setChecked(true);
            }
            if (activity.getApplicationContext().getResources().getString(R.string.thu).equalsIgnoreCase(aNotify_frequency)) {
                checkedTextViewFragmentAddGoalThreeWeekThu.setChecked(true);
            }
            if (activity.getApplicationContext().getResources().getString(R.string.fri).equalsIgnoreCase(aNotify_frequency)) {
                checkedTextViewFragmentAddGoalThreeWeekFri.setChecked(true);
            }
            if (activity.getApplicationContext().getResources().getString(R.string.sat).equalsIgnoreCase(aNotify_frequency)) {
                checkedTextViewFragmentAddGoalThreeWeekSat.setChecked(true);
            }
            if (activity.getApplicationContext().getResources().getString(R.string.sun).equalsIgnoreCase(aNotify_frequency)) {
                checkedTextViewFragmentAddGoalThreeWeekSun.setChecked(true);
            }
        }
    }

    //get desired frequency name based on selections
    private String getFrequency(String _frequency) {
        if (_frequency.equalsIgnoreCase("day") || _frequency.equalsIgnoreCase("daily")) {
            return activity.getApplicationContext().getResources().getString(R.string.daily);
        }
        if (_frequency.equalsIgnoreCase("hour") || _frequency.equalsIgnoreCase("hourly")) {
            return activity.getApplicationContext().getResources().getString(R.string.hour);
        }
        if (_frequency.equalsIgnoreCase("week") || _frequency.equalsIgnoreCase("weekly")) {
            return activity.getApplicationContext().getResources().getString(R.string.weekly);
        }
        return _frequency;
    }

    //convert 24 hr time to 12 hrs
    private int hrTime(int time, String unit) {
        if (unit.trim().equalsIgnoreCase("PM")) {
            return time - 12;
        }
        return time;
    }

    private String getTimeUnit(int progress) {
        String unit;
        if (progress > 0 && progress <= 12) {
            unit = "AM";
            if (progress == 12) {
                unit = "PM";
            }
        } else {
            unit = "PM";
        }
        return unit;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            if (activity != null) {
                saveData(false);
            }
        } else {
            AddGoalPreferences.initialize(activity.getApplicationContext());
            start_date = AddGoalPreferences.get(General.START_DATE);
            end_date = AddGoalPreferences.get(General.END_DATE);
            activity.registerReceiver(receiver, new IntentFilter("2"));
            if (AddGoalPreferences.contains(General.ACTION) && !AddGoalPreferences.get(General.ACTION).equalsIgnoreCase("reschedule")) {
                linearLayoutFragmentAddGoalThreeMilestoneItem.removeAllViews();
                setMileStone();
            }
            if (AddGoalPreferences.contains(General.ACTION) && AddGoalPreferences.get(General.ACTION).equalsIgnoreCase("reschedule")) {
                AddGoalPreferences.removeKey(General.MILESTONE_ID, TAG);
                AddGoalPreferences.removeKey(General.MILESTONE, TAG);
                AddGoalPreferences.removeKey(General.MILESTONE_DATE, TAG);
            }
            if (AddGoalPreferences.contains(General.ACTION) && !AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                    .getResources().getString(R.string.add_goal))) {
                setData();
            }
            if (AddGoalPreferences.contains(General.ACTION) && AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                    .getResources().getString(R.string.add_goal))) {
                setNotifyFrequency();
            }
        }
    }
}

