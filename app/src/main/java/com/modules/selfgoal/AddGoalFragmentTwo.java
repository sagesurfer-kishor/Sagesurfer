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
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.interfaces.AddGoalActivityInterface;
import com.sagesurfer.library.ArrayOperations;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.AddGoalPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 24/03/2018
 * Last Modified on
 */

public class AddGoalFragmentTwo extends Fragment implements View.OnClickListener {
    private static final String TAG = AddGoalFragmentTwo.class.getSimpleName();

    @BindView(R.id.textview_addgoalactivity_bubble_one)
    TextView textViewAddGoalActivityBubbleOne;
    @BindView(R.id.textview_addgoalactivity_bubble_three)
    TextView textViewAddGoalActivityBubbleThree;
    @BindView(R.id.textview_fragmentaddgoaltwo_sentence)
    TextView textViewFragmentAddGoalTwoSentence;
    @BindView(R.id.textview_fragmentaddgoaltwo_type)
    TextView textViewFragmentAddGoalTwoType;
    @BindView(R.id.radiobutton_fragmentaddgoaltwo_option)
    RadioButton radioButtonFragmentAddGoalTwoOption;
    @BindView(R.id.radiobutton_fragmentaddgoaltwo_count)
    RadioButton radioButtonFragmentAddGoalTwoCount;
    @BindView(R.id.textview_count_mandatory)
    TextView textViewCountMandatory;
    @BindView(R.id.linearlayout_fragmentaddgoaltwo_parameter)
    LinearLayout linearLayoutFragmentAddGoalTwoParameter;
    @BindView(R.id.linearlayout_fragmentaddgoaltwo_parameter_count)
    LinearLayout linearLayoutFragmentAddGoalTwoParameterCount;
    @BindView(R.id.edittext_fragmentaddgoaltwo_count)
    EditText editTextFragmentAddGoalTwoCount;
    @BindView(R.id.textview_fragmentaddgoaltwo_countunit)
    TextView textViewFragmentAddGoalTwoCountUnit;
    @BindView(R.id.linearlayout_unit_other)
    LinearLayout linearLayoutUnitOther;
    @BindView(R.id.editview_fragmentaddgoaltwo_unit_other)
    EditText editTextFragmentAddGoalTwoUnitOther;
    @BindView(R.id.imageview_other_add)
    AppCompatImageView imageViewOtherAdd;
    @BindView(R.id.textview_fragmentaddgoaltwo_frequency)
    TextView textViewFragmentAddGoalTwoFrequency;
    @BindView(R.id.button_fragmentaddgoaltwo_advance)
    Button buttonFragmentAddGoalTwoAdvance;
    @BindView(R.id.linearlayout_fragmentaddgoaltwo_frequency)
    LinearLayout linearLayoutFragmentAddGoalTwoFrequency;
    @BindView(R.id.textview_fragmentaddgoaltwo_numberpicker)
    TextView textViewFragmentAddGoalTwoNumberPicker;
    @BindView(R.id.textview_validate_numberpicker)
    TextView textViewValidateNumberPicker;
    @BindView(R.id.linearlayout_fragmentaddgoaltwo_frequencydays)
    LinearLayout linearLayoutFragmentAddGoalTwoFrequencyDays;
    @BindView(R.id.radiobutton_fragmentaddgoaltwo_specificdaycheck)
    RadioButton radioButtonFragmentAddGoalTwoSpecificDayCheck;
    @BindView(R.id.edittext_fragmentaddgoaltwo_specificdaycount)
    EditText editTextFragmentAddGoalTwoSpecificDayCount;
    @BindView(R.id.radiobutton_fragmentaddgoaltwo_everydaycheck)
    RadioButton radioButtonFragmentAddGoalTwoEveryDayCheck;
    @BindView(R.id.linaerlayout_fragmentaddgoaltwo_frequencyweek)
    LinearLayout linaerLayoutFragmentAddGoalTwoFrequencyWeek;
    @BindView(R.id.checkedtextview_fragmentaddgoaltwo_weekmon)
    CheckedTextView checkedTextViewFragmentAddGoalTwoWeekMon;
    @BindView(R.id.checkedtextview_fragmentaddgoaltwo_weektue)
    CheckedTextView checkedTextViewFragmentAddGoalTwoWeekTue;
    @BindView(R.id.checkedtextview_fragmentaddgoaltwo_weekwed)
    CheckedTextView checkedTextViewFragmentAddGoalTwoWeekWed;
    @BindView(R.id.checkedtextview_fragmentaddgoaltwo_weekthu)
    CheckedTextView checkedTextViewFragmentAddGoalTwoWeekThu;
    @BindView(R.id.checkedtextview_fragmentaddgoaltwo_weekfri)
    CheckedTextView checkedTextViewFragmentAddGoalTwoWeekFri;
    @BindView(R.id.checkedtextview_fragmentaddgoaltwo_weeksat)
    CheckedTextView checkedTextViewFragmentAddGoalTwoWeekSat;
    @BindView(R.id.checkedtextview_fragmentaddgoaltwo_weeksun)
    CheckedTextView checkedTextViewFragmentAddGoalTwoWeekSun;
    @BindView(R.id.linearlayout_fragmentaddgoaltwo_frequencymonth)
    LinearLayout linearLayoutFragmentAddGoalTwoFrequencyMonth;
    @BindView(R.id.radiobutton_fragmentaddgoaltwo_frequencymonthday)
    RadioButton radioButtonFragmentAddGoalTwoFrequencyMonthDay;
    @BindView(R.id.edittext_fragmentaddgoaltwo_frequencymonthdaycount)
    EditText editTextFragmentAddGoalTwoFrequencyMonthDayCount;
    @BindView(R.id.edittext_fragmentaddgoaltwo_frequencymonthcount)
    EditText editTextFragmentAddGoalTwoFrequencyMonthCount;
    @BindView(R.id.radiobutton_fragmentaddgoaltwo_frequencymonththe)
    RadioButton radioButtonFragmentAddGoalTwoFrequencyMonthThe;
    @BindView(R.id.spinner_fragmentaddgoaltwo_frequencymonthsequence)
    Spinner spinnerFragmentAddGoalTwoFrequencyMonthSequence;
    @BindView(R.id.spinner_fragmentaddgoaltwo_frequencymonthweek)
    Spinner spinnerFragmentAddGoalTwoFrequencyMonthWeek;
    @BindView(R.id.edittext_fragmentaddgoaltwo_frequencymonthcounttwo)
    EditText editTextFragmentAddGoalTwoFrequencyMonthCountTwo;
    @BindView(R.id.linearlayout_fragmentaddgoaltwo_occurrences)
    LinearLayout linearLayoutFragmentAddGoalTwoOccurrences;

    @BindView(R.id.edittext_fragmentaddgoaltwo_occurrence)
    EditText editTextFragmentAddGoalTwoOccurrence;

    @BindView(R.id.textview_fragmentaddgoaltwo_occurrencetime)
    TextView textViewFragmentAddGoalTwoOccurrenceTime;

    @BindView(R.id.linearlayout_fragmentaddgoaltwo_date)
    LinearLayout linearLayoutFragmentAddGoalTwoDate;

    @BindView(R.id.textview_fragmentaddgoaltwo_startdate)
    TextView textViewFragmentAddGoalTwoStartDate;

    @BindView(R.id.textview_fragmentaddgoaltwo_enddate)
    TextView textViewFragmentAddGoalTwoEndDate;

    public static AddGoalFragmentTwo goalFragmentStepTwo;
    private boolean isCounting = false, isEveryday = false, isMonthDay = true, advanceActive = false;
    private String unit = "", frequency = "", start_date = "", end_date = "", mainCount = "0",
            hour = "0", goal_type = "0", hour_time = "0";
    private ArrayList<String> unitList;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;

    private AddGoalActivityInterface addGoalActivityInterface;
    private Activity activity;
    private BroadcastReceiver receiver;
    private Calendar calendar;
    private Unbinder unbinder;
    private TextView textViewActivityToolbarPost;

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

        View view = inflater.inflate(R.layout.fragment_add_goal_two, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();
        goalFragmentStepTwo = this;

        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        textViewActivityToolbarPost = (TextView) view.findViewById(R.id.textview_activitytoolbar_post);
        unitList = new ArrayList<>();

        editTextFragmentAddGoalTwoSpecificDayCount.addTextChangedListener(textWatcher);

        editTextFragmentAddGoalTwoFrequencyMonthDayCount = (EditText) view.findViewById(R.id.edittext_fragmentaddgoaltwo_frequencymonthdaycount);
        editTextFragmentAddGoalTwoFrequencyMonthDayCount.addTextChangedListener(textWatcher);
        editTextFragmentAddGoalTwoFrequencyMonthDayCount.setOnFocusChangeListener(onFocusChange);

        editTextFragmentAddGoalTwoFrequencyMonthCount.addTextChangedListener(textWatcher);
        editTextFragmentAddGoalTwoFrequencyMonthCount.setOnFocusChangeListener(onFocusChange);

        editTextFragmentAddGoalTwoFrequencyMonthCountTwo.addTextChangedListener(textWatcher);
        editTextFragmentAddGoalTwoFrequencyMonthCountTwo.setOnFocusChangeListener(onFocusChange);

        editTextFragmentAddGoalTwoCount.addTextChangedListener(textWatcher);

        editTextFragmentAddGoalTwoOccurrence.addTextChangedListener(textWatcher);
        editTextFragmentAddGoalTwoOccurrence.setOnFocusChangeListener(onFocusChange);

        final ArrayAdapter<CharSequence> weekAdapter = ArrayAdapter.createFromResource(activity, R.array.add_goal_days, R.layout.simple_item_layout);
        weekAdapter.setDropDownViewResource(R.layout.simple_item_layout);
        spinnerFragmentAddGoalTwoFrequencyMonthWeek.setAdapter(weekAdapter);
        spinnerFragmentAddGoalTwoFrequencyMonthWeek.setOnItemSelectedListener(onItemSelected);

        final ArrayAdapter<CharSequence> sequenceAdapter = ArrayAdapter.createFromResource(activity, R.array.add_goal_sequence, R.layout.simple_item_layout);
        sequenceAdapter.setDropDownViewResource(R.layout.simple_item_layout);
        spinnerFragmentAddGoalTwoFrequencyMonthSequence.setAdapter(sequenceAdapter);
        spinnerFragmentAddGoalTwoFrequencyMonthSequence.setOnItemSelectedListener(onItemSelected);

        textViewAddGoalActivityBubbleOne.setOnClickListener(this);
        textViewAddGoalActivityBubbleThree.setOnClickListener(this);
        textViewFragmentAddGoalTwoFrequency.setOnClickListener(this);
        textViewFragmentAddGoalTwoCountUnit.setOnClickListener(this);
        textViewFragmentAddGoalTwoNumberPicker.setOnClickListener(this);
        textViewFragmentAddGoalTwoOccurrenceTime.setOnClickListener(this);
        textViewFragmentAddGoalTwoStartDate.setOnClickListener(this);
        textViewFragmentAddGoalTwoEndDate.setOnClickListener(this);
        textViewFragmentAddGoalTwoSentence.setText("");

        checkedTextViewFragmentAddGoalTwoWeekSun.setOnClickListener(this);
        checkedTextViewFragmentAddGoalTwoWeekSat.setOnClickListener(this);
        checkedTextViewFragmentAddGoalTwoWeekFri.setOnClickListener(this);
        checkedTextViewFragmentAddGoalTwoWeekThu.setOnClickListener(this);
        checkedTextViewFragmentAddGoalTwoWeekWed.setOnClickListener(this);
        checkedTextViewFragmentAddGoalTwoWeekTue.setOnClickListener(this);
        checkedTextViewFragmentAddGoalTwoWeekMon.setOnClickListener(this);

        buttonFragmentAddGoalTwoAdvance.setOnClickListener(this);
        imageViewOtherAdd.setOnClickListener(this);

        activity.registerReceiver(receiver, new IntentFilter("1"));
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(General.CHECK_IN)) {
                    if (intent.getStringExtra(General.CHECK_IN).equalsIgnoreCase("1")) {
                        if (validToMove()) {
                            saveData();
                            addGoalActivityInterface.moveToNext(true, 2);
                        }
                    }
                }
            }
        };

        AddGoalPreferences.initialize(activity.getApplicationContext());

        if (AddGoalPreferences.contains(General.ACTION)
                && AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.add_goal))) {
            clearTimePreferences();
            textViewFragmentAddGoalTwoFrequency.setEnabled(true);
        } else if (AddGoalPreferences.contains(General.ACTION)
                && AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.edit))) {
            textViewFragmentAddGoalTwoFrequency.setEnabled(false);
        }
        setData();

        toggleGoalType();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.registerReceiver(receiver, new IntentFilter("1"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        activity.unregisterReceiver(receiver);
    }

    private final AdapterView.OnItemSelectedListener onItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String occurrences = editTextFragmentAddGoalTwoOccurrence.getText().toString().trim();
            String month = editTextFragmentAddGoalTwoFrequencyMonthCountTwo.getText().toString().trim();
            int day = spinnerFragmentAddGoalTwoFrequencyMonthWeek.getSelectedItemPosition() + 1;
            int week = spinnerFragmentAddGoalTwoFrequencyMonthSequence.getSelectedItemPosition() + 1;
            end_date = GetTime.getSpecificDay(week, day, Formulas_.getMonthCount(occurrences, month));

            textViewFragmentAddGoalTwoEndDate.setText(GetTime.month_DdYyyy(end_date));

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final View.OnFocusChangeListener onFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.edittext_fragmentaddgoaltwo_occurrence:
                    if (!hasFocus) {
                        if (editTextFragmentAddGoalTwoOccurrence != null) {
                            String occurrences = editTextFragmentAddGoalTwoOccurrence.getText().toString().trim();
                            if (occurrences.length() <= 0) {
                                editTextFragmentAddGoalTwoOccurrence.setText("1");
                            }
                        }
                    }
                    break;
                case R.id.edittext_fragmentaddgoaltwo_specificdaycount:
                    if (!hasFocus) {
                        if (editTextFragmentAddGoalTwoSpecificDayCount != null) {
                            String count_day = editTextFragmentAddGoalTwoSpecificDayCount.getText().toString().trim();
                            if (count_day.length() <= 0) {
                                editTextFragmentAddGoalTwoSpecificDayCount.setText("1");
                            }
                        }
                    }
                    break;
                case R.id.edittext_fragmentaddgoaltwo_frequencymonthcount:
                    if (!hasFocus) {
                        if (editTextFragmentAddGoalTwoFrequencyMonthCount != null) {
                            String count_month = editTextFragmentAddGoalTwoFrequencyMonthCount.getText().toString().trim();
                            if (count_month.length() <= 0) {
                                editTextFragmentAddGoalTwoFrequencyMonthCount.setText("1");
                            }
                        }
                    }
                    break;
                case R.id.edittext_fragmentaddgoaltwo_frequencymonthdaycount:
                    if (!hasFocus) {
                        if (editTextFragmentAddGoalTwoFrequencyMonthDayCount != null) {
                            String count_day = editTextFragmentAddGoalTwoFrequencyMonthDayCount.getText().toString().trim();
                            if (count_day.length() <= 0) {
                                editTextFragmentAddGoalTwoFrequencyMonthDayCount.setText("1");
                            }
                        }
                    }
                    break;
                case R.id.edittext_fragmentaddgoaltwo_frequencymonthcounttwo:
                    if (!hasFocus) {
                        if (editTextFragmentAddGoalTwoFrequencyMonthCountTwo != null) {
                            String month_count = editTextFragmentAddGoalTwoFrequencyMonthCountTwo.getText().toString().trim();
                            if (month_count.length() <= 0) {
                                editTextFragmentAddGoalTwoFrequencyMonthCountTwo.setText("1");
                            }
                        }
                    }
                    break;
            }
            setSentenceText();
        }
    };

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s != null && s.toString().trim().length() > 0) {
                if (s.toString().equalsIgnoreCase("0")) {
                    resetToOne(s);
                }
                if (editTextFragmentAddGoalTwoFrequencyMonthDayCount.getText().hashCode() == s.hashCode()) {
                    int day_of_month = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
                    if (Integer.parseInt(s.toString()) > day_of_month) {
                        resetToOne(s);
                    }
                }
                String occurrences = editTextFragmentAddGoalTwoOccurrence.getText().toString().trim();
                if (editTextFragmentAddGoalTwoOccurrence.getText().hashCode() == s.hashCode()) {
                    end_date = getEndDate();
                } else if (editTextFragmentAddGoalTwoSpecificDayCount.getText().hashCode() == s.hashCode()) {
                    String day = editTextFragmentAddGoalTwoSpecificDayCount.getText().toString().trim();
                    end_date = getEndDateWithCount(Formulas_.getDayCount(occurrences, day));
                } else if (editTextFragmentAddGoalTwoFrequencyMonthCount.getText().hashCode() == s.hashCode()) {
                    end_date = getEndDate();
                } else if (editTextFragmentAddGoalTwoFrequencyMonthDayCount.getText().hashCode() == s.hashCode()) {
                    end_date = getEndDate();
                } else if (editTextFragmentAddGoalTwoFrequencyMonthCountTwo.getText().hashCode() == s.hashCode()) {
                    end_date = getEndDate();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (AddGoalPreferences.contains(General.ACTION) && AddGoalPreferences.get(
                    General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                    .getResources().getString(R.string.edit))) {
                end_date = AddGoalPreferences.get(General.END_DATE);
            }
            textViewFragmentAddGoalTwoEndDate.setText(GetTime.month_DdYyyy(end_date));
            setSentenceText();
        }
    };

    @OnClick({R.id.radiobutton_fragmentaddgoaltwo_count, R.id.radiobutton_fragmentaddgoaltwo_option,
            R.id.radiobutton_fragmentaddgoaltwo_specificdaycheck, R.id.radiobutton_fragmentaddgoaltwo_everydaycheck,
            R.id.radiobutton_fragmentaddgoaltwo_frequencymonththe, R.id.radiobutton_fragmentaddgoaltwo_frequencymonthday})
    public void onRadioButtonClicked(RadioButton radioButton) {
        // Is the button now checked?
        boolean checked = radioButton.isChecked();

        // Check which radio button was clicked
        switch (radioButton.getId()) {
            case R.id.radiobutton_fragmentaddgoaltwo_count:
                isCounting = true;
                textViewCountMandatory.setVisibility(View.VISIBLE);
                toggleGoalType();
                break;
            //textViewFragmentAddGoalTwoFrequency
            case R.id.radiobutton_fragmentaddgoaltwo_option:
                isCounting = false;
                textViewCountMandatory.setVisibility(View.GONE);
                toggleGoalType();
                break;

            case R.id.radiobutton_fragmentaddgoaltwo_specificdaycheck:
                if (!AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                        .getResources().getString(R.string.edit))) {
                    isEveryday = false;
                    toggleDayFrequency();
                }
                break;

            case R.id.radiobutton_fragmentaddgoaltwo_everydaycheck:
                if (!AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                        .getResources().getString(R.string.edit))) {
                    isEveryday = true;
                    toggleDayFrequency();
                }
                break;

            case R.id.radiobutton_fragmentaddgoaltwo_frequencymonththe:
                isMonthDay = false;
                toggleMonthCheck();
                break;

            case R.id.radiobutton_fragmentaddgoaltwo_frequencymonthday:
                isMonthDay = true;
                toggleMonthCheck();
                break;
        }
    }

    private void resetToOne(CharSequence sequence) {
        if (editTextFragmentAddGoalTwoOccurrence.getText().hashCode() == sequence.hashCode()) {
            editTextFragmentAddGoalTwoOccurrence.setText("1");
        } else if (editTextFragmentAddGoalTwoSpecificDayCount.getText().hashCode() == sequence.hashCode()) {
            editTextFragmentAddGoalTwoSpecificDayCount.setText("1");
        } else if (editTextFragmentAddGoalTwoFrequencyMonthDayCount.getText().hashCode() == sequence.hashCode()) {
            editTextFragmentAddGoalTwoFrequencyMonthDayCount.setText("1");
        }
    }

    private String getCountOne() {
        String count = "1";
        if (frequency.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.daily))) {
            if (radioButtonFragmentAddGoalTwoEveryDayCheck.isChecked()) {
                count = "100";
            } else {
                count = editTextFragmentAddGoalTwoSpecificDayCount.getText().toString().trim();
            }
        } else if (frequency.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.month))) {
            if (isMonthDay) {
                count = editTextFragmentAddGoalTwoFrequencyMonthCount.getText().toString().trim();
            } else {
                count = editTextFragmentAddGoalTwoFrequencyMonthCountTwo.getText().toString().trim();
            }
        }
        if (count.equalsIgnoreCase("null") || count.length() <= 0) {
            count = "1";
        }
        return count;
    }

    private void setSentenceText() {
        if (editTextFragmentAddGoalTwoFrequencyMonthCountTwo != null || editTextFragmentAddGoalTwoOccurrence != null || textViewFragmentAddGoalTwoSentence != null) {
            String main_count = editTextFragmentAddGoalTwoCount.getText().toString().trim();
            String occurrences = editTextFragmentAddGoalTwoOccurrence.getText().toString().trim();
            if (occurrences.length() <= 0) {
                occurrences = "1";
            }
//            String name = AddGoalPreferences.get(General.NAME);
            textViewFragmentAddGoalTwoSentence.setText(CreateSentence_.make_sentence(goal_type, main_count, unit, frequency, start_date, end_date, getCountOne(), occurrences));
        }
    }

    private boolean validToMove() {
        if (isCounting) {
            mainCount = editTextFragmentAddGoalTwoCount.getText().toString();
            if (mainCount.trim().length() <= 0) {
                editTextFragmentAddGoalTwoCount.setError("Compulsory Field");
                return false;
            }
        }
        if (start_date.length() <= 0 || end_date.length() <= 0) {
            ShowSnack.viewWarning(textViewFragmentAddGoalTwoStartDate, "Start and End date is compulsory", activity.getApplicationContext());
            return false;
        }
        if (frequency.length() <= 0) {
            ShowSnack.viewWarning(textViewFragmentAddGoalTwoFrequency, "Frequency is compulsory", activity.getApplicationContext());
            return false;
        }
        if (frequency.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.hour))) {
            if (hour.equalsIgnoreCase("0") || hour.trim().length() <= 0) {
                ShowSnack.viewWarning(textViewFragmentAddGoalTwoStartDate, "Invalid Hours", activity.getApplicationContext());
                return false;
            }
        }
        if (frequency.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.daily)) || frequency.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.month)) || frequency.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.weekly))) {
            String occur = editTextFragmentAddGoalTwoOccurrence.getText().toString();
            if (occur.length() <= 0) {
                ShowSnack.viewWarning(textViewFragmentAddGoalTwoFrequency, "Enter valid occurrence", activity.getApplicationContext());
                return false;
            }
            if (frequency.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.weekly))) {
                String selected_day = getSelectedWeek();
                if (selected_day.length() <= 0) {
                    ShowSnack.viewWarning(textViewFragmentAddGoalTwoFrequency, "Please select at least one day", activity.getApplicationContext());
                    return false;
                }
            }
            if (hour_time.equalsIgnoreCase("0") || hour_time.trim().length() <= 0) {
                ShowSnack.viewWarning(textViewFragmentAddGoalTwoFrequency, "Enter valid time", activity.getApplicationContext());
                return false;
            }
        }
        return true;
    }

    private String getSelectedWeek() {
        ArrayList<String> dayList = new ArrayList<>();
        if (checkedTextViewFragmentAddGoalTwoWeekMon.isChecked()) {
            dayList.add(activity.getApplicationContext().getResources().getString(R.string.mon));
        }
        if (checkedTextViewFragmentAddGoalTwoWeekTue.isChecked()) {
            dayList.add(activity.getApplicationContext().getResources().getString(R.string.tue));
        }
        if (checkedTextViewFragmentAddGoalTwoWeekWed.isChecked()) {
            dayList.add(activity.getApplicationContext().getResources().getString(R.string.wed));
        }
        if (checkedTextViewFragmentAddGoalTwoWeekThu.isChecked()) {
            dayList.add(activity.getApplicationContext().getResources().getString(R.string.thu));
        }
        if (checkedTextViewFragmentAddGoalTwoWeekFri.isChecked()) {
            dayList.add(activity.getApplicationContext().getResources().getString(R.string.fri));
        }
        if (checkedTextViewFragmentAddGoalTwoWeekSat.isChecked()) {
            dayList.add(activity.getApplicationContext().getResources().getString(R.string.sat));
        }
        if (checkedTextViewFragmentAddGoalTwoWeekSun.isChecked()) {
            dayList.add(activity.getApplicationContext().getResources().getString(R.string.sun));
        }
        if (dayList.size() > 0) {
            return ArrayOperations.stringListToString(dayList);
        }
        return "";
    }

    private int getFrequencyMenu() {
        if (unit.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.hour))) {
            return R.menu.menu_add_goal_freq_hour;
        }
        if (unit.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.daily)) || unit.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.weekly))) {
            return R.menu.menu_add_goal_freq_day;
        }
        return R.menu.menu_add_goal_frequency;
    }

    private void showFrequency() {
        final PopupMenu popup = new PopupMenu(activity, textViewFragmentAddGoalTwoFrequency);
        popup.getMenuInflater().inflate(getFrequencyMenu(), popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menuitem_add_goal_frequency_select) {
                    frequency = "";
                } else {
                    frequency = item.getTitle().toString();
                }
                popup.dismiss();
                textViewFragmentAddGoalTwoFrequency.setText(frequency);
                clearTimePreferences();
                toggleFrequencyLayout();
                return true;
            }
        });
        popup.show();
    }

    private void toggleDayFrequency() {
        if (AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.edit))) {
            if (isEveryday) {
                radioButtonFragmentAddGoalTwoEveryDayCheck.setChecked(true);
                radioButtonFragmentAddGoalTwoSpecificDayCheck.setChecked(false);
                radioButtonFragmentAddGoalTwoEveryDayCheck.setEnabled(true);
                radioButtonFragmentAddGoalTwoSpecificDayCheck.setChecked(false);
            } else {
                radioButtonFragmentAddGoalTwoEveryDayCheck.setChecked(false);
                radioButtonFragmentAddGoalTwoSpecificDayCheck.setChecked(true);
                radioButtonFragmentAddGoalTwoEveryDayCheck.setEnabled(false);
                radioButtonFragmentAddGoalTwoSpecificDayCheck.setChecked(true);
            }
            editTextFragmentAddGoalTwoSpecificDayCount.setEnabled(false);
        } else {
            editTextFragmentAddGoalTwoSpecificDayCount.setEnabled(true);
            if (isEveryday) {
                radioButtonFragmentAddGoalTwoEveryDayCheck.setChecked(true);
                radioButtonFragmentAddGoalTwoSpecificDayCheck.setChecked(false);
                editTextFragmentAddGoalTwoSpecificDayCount.setEnabled(false);
                editTextFragmentAddGoalTwoSpecificDayCount.setText("1");
            } else {
                radioButtonFragmentAddGoalTwoEveryDayCheck.setChecked(false);
                radioButtonFragmentAddGoalTwoSpecificDayCheck.setChecked(true);
                editTextFragmentAddGoalTwoSpecificDayCount.setEnabled(true);
                editTextFragmentAddGoalTwoSpecificDayCount.setText("1");
                editTextFragmentAddGoalTwoSpecificDayCount.setSelection(editTextFragmentAddGoalTwoSpecificDayCount.getText().toString().length());
            }
        }
        setDates();
    }

    private void showUnit() {
        linearLayoutUnitOther.setVisibility(View.GONE);
        final PopupMenu popup = new PopupMenu(activity, textViewFragmentAddGoalTwoCountUnit);
        popup.getMenuInflater().inflate(R.menu.menu_add_goal_unit, popup.getMenu());
        for (int i = 0; i < unitList.size(); i++) {
            popup.getMenu().add(unitList.get(i));
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menuitem_add_goal_unit_select) {
                    unit = "";
                } else //noinspection StatementWithEmptyBody
                    if (item.getItemId() == R.id.menuitem_add_goal_unit_other) {
                        linearLayoutUnitOther.setVisibility(View.VISIBLE);
                        //addUnit();
                    } else {
                        unit = item.getTitle().toString();
                    }
                popup.dismiss();
                textViewFragmentAddGoalTwoCountUnit.setText(unit);
                if (unit.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.weekly)) || unit.equalsIgnoreCase(activity.getApplicationContext()
                        .getResources().getString(R.string.daily))) {
                    frequency = activity.getApplicationContext().getResources().getString(R.string.weekly);
                    textViewFragmentAddGoalTwoFrequency.setText(frequency);
                } else {
                    frequency = "";
                    textViewFragmentAddGoalTwoFrequency.setText(frequency);
                }
                if (AddGoalPreferences.contains(General.ACTION) && AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                        .getResources().getString(R.string.add_goal))) {
                    clearTimePreferences();
                }
                toggleFrequencyLayout();
                toggleAdvance();
                return true;
            }
        });
        popup.show();
    }

    private void addUnit() {
        String otherString = editTextFragmentAddGoalTwoUnitOther.getText().toString().trim();
        if (otherString.length() != 0) {
            unitList.add(otherString);
            textViewFragmentAddGoalTwoCountUnit.setText(otherString);
            editTextFragmentAddGoalTwoUnitOther.setText("");
            linearLayoutUnitOther.setVisibility(View.GONE);
            unit = otherString;
            if (unit.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.weekly)) || unit.equalsIgnoreCase(activity.getApplicationContext()
                    .getResources().getString(R.string.daily))) {
                frequency = activity.getApplicationContext().getResources().getString(R.string.weekly);
                textViewFragmentAddGoalTwoFrequency.setText(frequency);
            } else {
                frequency = "";
                textViewFragmentAddGoalTwoFrequency.setText(frequency);
            }
            if (AddGoalPreferences.contains(General.ACTION) && AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                    .getResources().getString(R.string.add_goal))) {
                clearTimePreferences();
            }
            toggleFrequencyLayout();
            toggleAdvance();
        }
    }

    private void toggleFrequencyLayout() {
        if (AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.add_goal))) {
            resetField();
        }
        if (frequency.length() <= 0) {
            linearLayoutFragmentAddGoalTwoFrequency.setVisibility(View.GONE);
            return;
        }
        linearLayoutFragmentAddGoalTwoFrequency.setVisibility(View.VISIBLE);
        if (frequency.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.hour))) {
            textViewFragmentAddGoalTwoNumberPicker.setVisibility(View.VISIBLE);
            textViewValidateNumberPicker.setVisibility(View.VISIBLE);
            textViewFragmentAddGoalTwoNumberPicker.setHint(activity.getApplicationContext().getResources().getString(R.string.select_hours));
            linearLayoutFragmentAddGoalTwoFrequencyDays.setVisibility(View.GONE);
            linaerLayoutFragmentAddGoalTwoFrequencyWeek.setVisibility(View.GONE);
            linearLayoutFragmentAddGoalTwoFrequencyMonth.setVisibility(View.GONE);
            linearLayoutFragmentAddGoalTwoOccurrences.setVisibility(View.GONE);
            toggleTime();
        } else if (frequency.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.daily))) {
            textViewFragmentAddGoalTwoNumberPicker.setVisibility(View.GONE);
            textViewValidateNumberPicker.setVisibility(View.GONE);
            linearLayoutFragmentAddGoalTwoFrequencyDays.setVisibility(View.GONE);
            linaerLayoutFragmentAddGoalTwoFrequencyWeek.setVisibility(View.GONE);
            linearLayoutFragmentAddGoalTwoFrequencyMonth.setVisibility(View.GONE);
            linearLayoutFragmentAddGoalTwoOccurrences.setVisibility(View.GONE);
            linearLayoutFragmentAddGoalTwoOccurrences.setVisibility(View.VISIBLE);
            editTextFragmentAddGoalTwoOccurrence.setText("1");
        } else if (frequency.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.weekly))) {
            linearLayoutFragmentAddGoalTwoFrequencyDays.setVisibility(View.GONE);
            textViewFragmentAddGoalTwoNumberPicker.setVisibility(View.GONE);
            textViewValidateNumberPicker.setVisibility(View.GONE);
            linaerLayoutFragmentAddGoalTwoFrequencyWeek.setVisibility(View.GONE);
            linearLayoutFragmentAddGoalTwoFrequencyMonth.setVisibility(View.GONE);
            linearLayoutFragmentAddGoalTwoOccurrences.setVisibility(View.VISIBLE);
            editTextFragmentAddGoalTwoOccurrence.setText("1");
        }
        setDates();
        if (AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.add_goal))) {
            advanceActive = false;
            toggleAdvance();
        }
    }

    private void setDates() {
        if (AddGoalPreferences.contains(General.ACTION) && AddGoalPreferences.get(
                General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.add_goal))) {
            start_date = GetTime.todaysDate();
            if (frequency.equalsIgnoreCase(activity.getApplicationContext()
                    .getResources().getString(R.string.hour))) {
                end_date = GetTime.todaysDate();
            } else {
                end_date = getEndDate();
            }
            textViewFragmentAddGoalTwoStartDate.setText(GetTime.month_DdYyyy(start_date));
            textViewFragmentAddGoalTwoEndDate.setText(GetTime.month_DdYyyy(end_date));

        } else {

            start_date = AddGoalPreferences.get(General.START_DATE);
            end_date = AddGoalPreferences.get(General.END_DATE);

            textViewFragmentAddGoalTwoStartDate.setText(GetTime.month_DdYyyy(start_date));
            textViewFragmentAddGoalTwoEndDate.setText(GetTime.month_DdYyyy(end_date));

        }
        setSentenceText();
    }

    @SuppressLint("SimpleDateFormat")
    private String getEndDateWithCount(int count) {
        if (start_date.length() <= 0) {
            start_date = GetTime.todaysDate();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date input_date;
        try {
            input_date = formatter.parse(start_date);
            return formatter.format(GetTime.addDay(input_date, count));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return start_date;
    }

    @SuppressLint("SimpleDateFormat")
    private String getEndDate() {
        if (start_date.length() <= 0) {
            start_date = GetTime.todaysDate();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date input_date = null;
        try {
            input_date = formatter.parse(start_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String occurrences = editTextFragmentAddGoalTwoOccurrence.getText().toString().trim();
        if (frequency.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.daily))) {
            String day = editTextFragmentAddGoalTwoSpecificDayCount.getText().toString();
            return getEndDateWithCount(Formulas_.getDayCount(occurrences, day));
        } else if (frequency.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.weekly))) {
            return formatter.format(GetTime.addDay(input_date, Formulas_.getWeekCount(occurrences)));
        }
        return start_date;
    }

    private void toggleParameterLayout() {
        if (AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.add_goal))) {
            frequency = "";
            textViewFragmentAddGoalTwoFrequency.setText(frequency);
            clearTimePreferences();
        }
        if (isCounting) {
            linearLayoutFragmentAddGoalTwoParameterCount.setVisibility(View.VISIBLE);
            if (AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                    .getResources().getString(R.string.add_goal))) {
                unit = "";
                editTextFragmentAddGoalTwoCount.setText("");
            }
            textViewFragmentAddGoalTwoCountUnit.setText(unit);
        } else {
            linearLayoutFragmentAddGoalTwoParameterCount.setVisibility(View.GONE);
        }
        linearLayoutFragmentAddGoalTwoFrequencyMonth.setVisibility(View.GONE);
        linearLayoutFragmentAddGoalTwoFrequencyDays.setVisibility(View.GONE);
        linaerLayoutFragmentAddGoalTwoFrequencyWeek.setVisibility(View.GONE);
        textViewFragmentAddGoalTwoNumberPicker.setVisibility(View.GONE);
        textViewValidateNumberPicker.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void resetField() {
        editTextFragmentAddGoalTwoSpecificDayCount.setText("1");
        isDayCheck(1, true);
        editTextFragmentAddGoalTwoFrequencyMonthDayCount.setText("20");
        editTextFragmentAddGoalTwoOccurrence.setText("");

        if (frequency.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.daily))
                || frequency.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.weekly))) {

        } else {
            textViewFragmentAddGoalTwoOccurrenceTime.setText("");
        }

        editTextFragmentAddGoalTwoFrequencyMonthCount.setText("1");
        editTextFragmentAddGoalTwoFrequencyMonthCountTwo.setText("1");
    }

    private void toggleGoalType() {
        if (AddGoalPreferences.contains(General.ACTION) && !AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.reschedule_goal))) {
            if (isCounting) {
                goal_type = "0";
                radioButtonFragmentAddGoalTwoCount.setChecked(true);
                radioButtonFragmentAddGoalTwoOption.setChecked(false);
            } else {
                goal_type = "1";
                radioButtonFragmentAddGoalTwoCount.setChecked(false);
                radioButtonFragmentAddGoalTwoOption.setChecked(true);
            }
        }
        toggleParameterLayout();
    }

    private void toggleAdvanceLayout() {
        if (advanceActive) {
            if (frequency.equalsIgnoreCase(activity.getApplicationContext()
                    .getResources().getString(R.string.daily))) {
                linearLayoutFragmentAddGoalTwoFrequencyDays.setVisibility(View.VISIBLE);
                linaerLayoutFragmentAddGoalTwoFrequencyWeek.setVisibility(View.GONE);
                linearLayoutFragmentAddGoalTwoFrequencyMonth.setVisibility(View.GONE);
                if (AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                        .getResources().getString(R.string.edit))) {
                    String frequency_sub_unit_days = AddGoalPreferences.get("frequency_sub_unit_days");
                    if (frequency_sub_unit_days.trim().length() <= 0) {
                        editTextFragmentAddGoalTwoSpecificDayCount.setText(AddGoalPreferences.get("frequency_unit"));
                        editTextFragmentAddGoalTwoSpecificDayCount.setEnabled(false);
                    } else {
                        isEveryday = true;
                        toggleDayFrequency();
                    }
                }
            } else if (frequency.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.weekly))) {
                linearLayoutFragmentAddGoalTwoFrequencyDays.setVisibility(View.GONE);
                linaerLayoutFragmentAddGoalTwoFrequencyWeek.setVisibility(View.VISIBLE);
                linearLayoutFragmentAddGoalTwoFrequencyMonth.setVisibility(View.GONE);
                if (AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.edit))) {
                    String[] frequency_sub_unit_days = AddGoalPreferences.get("frequency_sub_unit_days").split("-");
                    setWeekSelection(frequency_sub_unit_days);
                }
            } else if (frequency.equalsIgnoreCase(activity.getApplicationContext()
                    .getResources().getString(R.string.month))) {
                linearLayoutFragmentAddGoalTwoFrequencyDays.setVisibility(View.GONE);
                linaerLayoutFragmentAddGoalTwoFrequencyWeek.setVisibility(View.GONE);
                linearLayoutFragmentAddGoalTwoFrequencyMonth.setVisibility(View.VISIBLE);
                toggleMonthCheck();
            }
        } else {
            editTextFragmentAddGoalTwoSpecificDayCount.setText("1");
            editTextFragmentAddGoalTwoFrequencyMonthDayCount.setText(String.valueOf(mDay));
            editTextFragmentAddGoalTwoFrequencyMonthCount.setText("1");
            editTextFragmentAddGoalTwoFrequencyMonthCountTwo.setText("1");
            isEveryday = false;
            toggleDayFrequency();
            isDayCheck(1, true);
            isMonthDay = true;
            toggleMonthCheck();
            linearLayoutFragmentAddGoalTwoFrequencyDays.setVisibility(View.GONE);
            linaerLayoutFragmentAddGoalTwoFrequencyWeek.setVisibility(View.GONE);
            linearLayoutFragmentAddGoalTwoFrequencyMonth.setVisibility(View.GONE);
        }
        setDates();
    }

    private void clearTimePreferences() {
        if (AddGoalPreferences.contains(General.START_MINUTE)) {
            AddGoalPreferences.removeKey(General.START_MINUTE, TAG);
        }
        if (AddGoalPreferences.contains(General.START_HOUR)) {
            AddGoalPreferences.removeKey(General.START_HOUR, TAG);
        }
        if (AddGoalPreferences.contains(General.UNITS)) {
            AddGoalPreferences.removeKey(General.UNITS, TAG);
        }
        hour_time = "0";
    }

    private void toggleTime() {
        textViewFragmentAddGoalTwoNumberPicker.setText("");
        if (frequency.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.hour))) {
            textViewFragmentAddGoalTwoNumberPicker.setHint(activity.getApplicationContext().getResources().getString(R.string.select_hours));
        } else {
            textViewFragmentAddGoalTwoNumberPicker.setHint(activity.getApplicationContext().getResources().getString(R.string.select_minutes));
        }
    }

    private void toggleMonthCheck() {
        editTextFragmentAddGoalTwoFrequencyMonthDayCount.setText(GetCounters.checkDigit(mDay));
        spinnerFragmentAddGoalTwoFrequencyMonthWeek.setSelection(calendar.get(Calendar.DAY_OF_WEEK) - 1);
        spinnerFragmentAddGoalTwoFrequencyMonthSequence.setSelection(calendar.get(Calendar.WEEK_OF_MONTH) - 1);

        if (isMonthDay) {
            radioButtonFragmentAddGoalTwoFrequencyMonthDay.setChecked(true);
            radioButtonFragmentAddGoalTwoFrequencyMonthThe.setChecked(false);
            editTextFragmentAddGoalTwoFrequencyMonthDayCount.setEnabled(true);
            editTextFragmentAddGoalTwoFrequencyMonthDayCount.requestFocus();
            editTextFragmentAddGoalTwoFrequencyMonthCount.setEnabled(true);
            spinnerFragmentAddGoalTwoFrequencyMonthSequence.setEnabled(false);
            spinnerFragmentAddGoalTwoFrequencyMonthWeek.setEnabled(false);
            editTextFragmentAddGoalTwoFrequencyMonthCountTwo.setEnabled(false);
            editTextFragmentAddGoalTwoFrequencyMonthCountTwo.setText("1");
        } else {
            radioButtonFragmentAddGoalTwoFrequencyMonthDay.setChecked(false);
            radioButtonFragmentAddGoalTwoFrequencyMonthThe.setChecked(true);
            editTextFragmentAddGoalTwoFrequencyMonthDayCount.setEnabled(false);
            editTextFragmentAddGoalTwoFrequencyMonthCount.setEnabled(false);
            spinnerFragmentAddGoalTwoFrequencyMonthSequence.setEnabled(true);
            spinnerFragmentAddGoalTwoFrequencyMonthWeek.setEnabled(true);
            editTextFragmentAddGoalTwoFrequencyMonthCountTwo.setEnabled(true);
            editTextFragmentAddGoalTwoFrequencyMonthCountTwo.requestFocus();
            editTextFragmentAddGoalTwoFrequencyMonthCount.setText("1");
        }
    }

    private void isDayCheck(int _id, boolean isReset) {
        if (AddGoalPreferences.contains(General.ACTION) && AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.edit))) {
            return;
        }
        int[] dayCheckId = {
                R.id.checkedtextview_fragmentaddgoaltwo_weeksun,
                R.id.checkedtextview_fragmentaddgoaltwo_weeksat,
                R.id.checkedtextview_fragmentaddgoaltwo_weekfri,
                R.id.checkedtextview_fragmentaddgoaltwo_weekthu,
                R.id.checkedtextview_fragmentaddgoaltwo_weekwed,
                R.id.checkedtextview_fragmentaddgoaltwo_weektue,
                R.id.checkedtextview_fragmentaddgoaltwo_weekmon
        };
        CheckedTextView[] specificDayCheck = {
                checkedTextViewFragmentAddGoalTwoWeekSun, checkedTextViewFragmentAddGoalTwoWeekSat, checkedTextViewFragmentAddGoalTwoWeekFri, checkedTextViewFragmentAddGoalTwoWeekThu, checkedTextViewFragmentAddGoalTwoWeekWed, checkedTextViewFragmentAddGoalTwoWeekTue, checkedTextViewFragmentAddGoalTwoWeekMon};
        if (isReset) {
            for (CheckedTextView aDayCheck : specificDayCheck) {
                aDayCheck.setChecked(true);
            }
            return;
        }
        int i = 0;
        for (int id : dayCheckId) {
            if (id == _id) {
                if (specificDayCheck[i].isChecked()) {
                    specificDayCheck[i].setChecked(false);
                } else {
                    specificDayCheck[i].setChecked(true);
                }
                return;
            }
            i++;
        }
    }

    @SuppressLint("CommitTransaction")
    private void openChoiceDialog() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment frag = getFragmentManager().findFragmentByTag(General.TIMESTAMP);
        if (frag != null) {
            fragmentTransaction.remove(frag);
        }
        fragmentTransaction.addToBackStack(null);
        TimeHourPickerDialogFragment dialogFrag = new TimeHourPickerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(General.FREQUENCY, frequency);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager(), General.TIMESTAMP);
    }

    public void setTime(String _hour, String _minute, String _unit) {
        //Log.e(TAG, "setTime() _hour: " + _hour + " _minute: " + _minute + " _unit: " + _unit);
        String time;
        String timeOne = _hour + ":" + _minute + ":" + _unit;
        if (frequency.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.hour))) {
            time = GetCounters.checkDigit(Integer.parseInt(_hour)) + ":" + GetCounters.checkDigit(Integer.parseInt(_minute));
            hour = _hour;
        } else {
            // time = convertTime(_hour) + ":" + _minute + " " + _unit;
            time = timeOne;
            hour_time = convertTime(_hour) + ":" + _minute + " " + _unit;
        }
        if (frequency.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.hour))) {
            textViewFragmentAddGoalTwoNumberPicker.setText(time);
        } else {
            textViewFragmentAddGoalTwoOccurrenceTime.setText(timeOne);
        }
        setSentenceText();
    }

    private String convertTime(String hour_) {
        if (unit.equalsIgnoreCase("pm")) {
            int _hour = Integer.parseInt(hour_);
            if (_hour != 12) {
                return "" + GetCounters.checkDigit((_hour + 12));
            }
        } else {
            int _hour = Integer.parseInt(hour_);
            if (_hour == 12) {
                return "00";
            }
        }
        return GetCounters.checkDigit(Integer.parseInt(hour_));
    }

    private void toggleAdvance() {
        if (frequency.length() <= 0) {
            advanceActive = false;
            buttonFragmentAddGoalTwoAdvance.setText(activity.getApplicationContext().getResources().getString(R.string.advance_option));
            buttonFragmentAddGoalTwoAdvance.setBackgroundResource(R.drawable.disable_color_rounded_rectangle);
            buttonFragmentAddGoalTwoAdvance.setEnabled(false);
            return;
        }
        if (frequency.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.hour))) {
            advanceActive = false;
            buttonFragmentAddGoalTwoAdvance.setText(activity.getApplicationContext().getResources().getString(R.string.advance_option));
            buttonFragmentAddGoalTwoAdvance.setBackgroundResource(R.drawable.disable_color_rounded_rectangle);
            buttonFragmentAddGoalTwoAdvance.setEnabled(false);
        } else {
            buttonFragmentAddGoalTwoAdvance.setBackgroundResource(R.drawable.primary_rounded_rectangle);
            buttonFragmentAddGoalTwoAdvance.setEnabled(true);
        }
        if (advanceActive) {
            buttonFragmentAddGoalTwoAdvance.setText(activity.getApplicationContext().getResources().getString(R.string.hide));
            toggleAdvanceLayout();
        } else {
            buttonFragmentAddGoalTwoAdvance.setText(activity.getApplicationContext().getResources().getString(R.string.advance_option));
            toggleAdvanceLayout();
        }
    }

    @Override
    public void onClick(View v) {
        if (frequency.equalsIgnoreCase(activity.getApplicationContext().getString(R.string.weekly))) {
            isDayCheck(v.getId(), false);
        }
        editTextFragmentAddGoalTwoOccurrence.clearFocus();
        switch (v.getId()) {
            case R.id.textview_addgoalactivity_bubble_one:
                addGoalActivityInterface.moveToNext(true, 0);
                break;
            case R.id.textview_addgoalactivity_bubble_three:
                if (validToMove()) {
                    saveData();
                    addGoalActivityInterface.moveToNext(true, 2);
                }
                break;
            case R.id.textview_fragmentaddgoaltwo_countunit:
                showUnit();
                break;
            case R.id.imageview_other_add:
                addUnit();
                break;
            case R.id.textview_fragmentaddgoaltwo_frequency:
                if (!unit.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.weekly))) {
                    showFrequency();
                }
                break;
            case R.id.textview_fragmentaddgoaltwo_numberpicker:
                openChoiceDialog();
                break;
            case R.id.textview_fragmentaddgoaltwo_occurrencetime:
                openChoiceDialog();
                break;
            case R.id.button_fragmentaddgoaltwo_advance:
                advanceActive = !advanceActive;
                toggleAdvance();
                break;
            case R.id.spinner_fragmentaddgoaltwo_frequencymonthsequence:
                ShowToast.toast("Sequence", activity.getApplicationContext());
                break;
            case R.id.spinner_fragmentaddgoaltwo_frequencymonthweek:
                ShowToast.toast("Week", activity.getApplicationContext());
                break;
            case R.id.textview_fragmentaddgoaltwo_startdate:
                end_date = "";
                textViewFragmentAddGoalTwoEndDate.setText(end_date);

                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                start_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                try {
                                    int result = Compare.startDate(mYear + "-" + (mMonth + 1) + "-" + mDay, start_date);
                                    if (result == 1) {
                                        start_date = "";
                                        textViewFragmentAddGoalTwoStartDate.setText(start_date);
                                        textViewFragmentAddGoalTwoStartDate.setHint(activity.getApplicationContext()
                                                .getResources().getString(R.string.start_date));
                                        ShowToast.toast("Invalid Date", activity.getApplicationContext());
                                    } else {
                                        textViewFragmentAddGoalTwoStartDate.setText(GetTime.month_DdYyyy(start_date));
                                        if (frequency.trim().length() > 0) {
                                            end_date = getEndDate();
                                            textViewFragmentAddGoalTwoEndDate.setText(GetTime.month_DdYyyy(end_date));
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;

            case R.id.textview_fragmentaddgoaltwo_enddate:

                if (start_date.length() <= 0) {
                    ShowToast.toast("Select Start Date First", activity.getApplicationContext());
                    return;
                }

                DatePickerDialog endDate = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                end_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                try {
                                    int result = Compare.startDate(mYear + "-" + (mMonth + 1) + "-" + mDay, end_date);
                                    if (result == 1) {
                                        end_date = "";
                                        textViewFragmentAddGoalTwoEndDate.setText(end_date);
                                        textViewFragmentAddGoalTwoEndDate.setHint(activity.getApplicationContext()
                                                .getResources().getString(R.string.end_date));
                                        ShowToast.toast("Invalid Date", activity.getApplicationContext());
                                    } else {
                                        textViewFragmentAddGoalTwoEndDate.setText(GetTime.month_DdYyyy(end_date));
                                        //setOccurrencesBox();
                                        setSentenceText();
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                endDate.show();
                break;
        }
    }

    private void setOccurrencesBox() {
        String valueOne = editTextFragmentAddGoalTwoSpecificDayCount.getText().toString().trim();
        String occurrences = editTextFragmentAddGoalTwoOccurrence.getText().toString().trim();
        int occurrences_ = GetOccurrences_.getOccurrences(frequency, occurrences, start_date,
                end_date, advanceActive, isEveryday, valueOne, activity.getApplicationContext());
        if (!occurrences.equalsIgnoreCase("" + occurrences_)) {
            editTextFragmentAddGoalTwoOccurrence.setText(String.valueOf(occurrences_));
        }
    }

    //
    private void setData() {
        if (AddGoalPreferences.contains(General.ACTION) && !AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.add_goal))) {
            textViewCountMandatory.setVisibility(View.GONE);
            frequency = ChangeCase.toSentenceCase(AddGoalPreferences.get(General.FREQUENCY));
            textViewFragmentAddGoalTwoFrequency.setText(frequency);
            unit = AddGoalPreferences.get(General.UNITS);
            textViewFragmentAddGoalTwoCountUnit.setText(unit);
            mainCount = AddGoalPreferences.get(General.COUNT);
            editTextFragmentAddGoalTwoCount.setText(mainCount);
            if (AddGoalPreferences.get(General.GOAL_TYPE).equalsIgnoreCase("0")) {
                isCounting = true;
                radioButtonFragmentAddGoalTwoOption.setChecked(false);
                radioButtonFragmentAddGoalTwoOption.setEnabled(false);
                radioButtonFragmentAddGoalTwoCount.setChecked(true);
            } else {
                advanceActive = false;
                isCounting = false;
                radioButtonFragmentAddGoalTwoOption.setChecked(true);
                radioButtonFragmentAddGoalTwoCount.setChecked(false);
                radioButtonFragmentAddGoalTwoCount.setEnabled(false);
            }
            if (AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                    .getResources().getString(R.string.edit))) {
                radioButtonFragmentAddGoalTwoOption.setEnabled(false);
                radioButtonFragmentAddGoalTwoCount.setEnabled(false);
                textViewFragmentAddGoalTwoStartDate.setEnabled(false);
                textViewFragmentAddGoalTwoCountUnit.setEnabled(false);
            } else {
                textViewFragmentAddGoalTwoStartDate.setEnabled(true);
                textViewFragmentAddGoalTwoCountUnit.setEnabled(true);
            }

            toggleFrequencyLayout();
            advanceActive = true;
            toggleAdvance();

            setTime(AddGoalPreferences.get(General.START_HOUR), AddGoalPreferences.get(General.START_MINUTE), AddGoalPreferences.get(General.TIME_UNIT));

            if (AddGoalPreferences.contains(General.ACTION) && !AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                    .getResources().getString(R.string.add_goal))) {
                editTextFragmentAddGoalTwoOccurrence.setText(AddGoalPreferences.get("occurrences"));
            }
            if (AddGoalPreferences.contains(General.ACTION) && AddGoalPreferences.get(
                    General.ACTION).equalsIgnoreCase(activity.getApplicationContext()
                    .getResources().getString(R.string.edit))) {
                editTextFragmentAddGoalTwoOccurrence.setEnabled(false);
                textViewFragmentAddGoalTwoOccurrenceTime.setEnabled(false);
                buttonFragmentAddGoalTwoAdvance.setEnabled(false);
            }
            setSentenceText();
        }
    }

    //set week check box selection based on data
    private void setWeekSelection(String[] frequency_sub_unit_days) {
        for (String aNotify_frequency : frequency_sub_unit_days) {
            if (activity.getApplicationContext().getResources().getString(R.string.mon).equalsIgnoreCase(aNotify_frequency)) {
                checkedTextViewFragmentAddGoalTwoWeekMon.setChecked(true);
            }
            if (activity.getApplicationContext().getResources().getString(R.string.tue).equalsIgnoreCase(aNotify_frequency)) {
                checkedTextViewFragmentAddGoalTwoWeekTue.setChecked(true);
            }
            if (activity.getApplicationContext().getResources().getString(R.string.wed).equalsIgnoreCase(aNotify_frequency)) {
                checkedTextViewFragmentAddGoalTwoWeekWed.setChecked(true);
            }
            if (activity.getApplicationContext().getResources().getString(R.string.thu).equalsIgnoreCase(aNotify_frequency)) {
                checkedTextViewFragmentAddGoalTwoWeekThu.setChecked(true);
            }
            if (activity.getApplicationContext().getResources().getString(R.string.fri).equalsIgnoreCase(aNotify_frequency)) {
                checkedTextViewFragmentAddGoalTwoWeekFri.setChecked(true);
            }
            if (activity.getApplicationContext().getResources().getString(R.string.sat).equalsIgnoreCase(aNotify_frequency)) {
                checkedTextViewFragmentAddGoalTwoWeekSat.setChecked(true);
            }
            if (activity.getApplicationContext().getResources().getString(R.string.sun).equalsIgnoreCase(aNotify_frequency)) {
                checkedTextViewFragmentAddGoalTwoWeekSun.setChecked(true);
            }
        }
    }

    // save page data to shared preferences
    private void saveData() {
        AddGoalPreferences.initialize(getActivity().getApplicationContext());
        AddGoalPreferences.save(General.GOAL_TYPE, isCounting);
        AddGoalPreferences.save(General.COUNT, mainCount, TAG);
        AddGoalPreferences.save("occurrences", editTextFragmentAddGoalTwoOccurrence.getText().toString().trim(), TAG);
        AddGoalPreferences.save(General.UNITS, ChangeCase.toSentenceCase(unit), TAG);
        AddGoalPreferences.save(General.FREQUENCY, frequency, TAG);
        AddGoalPreferences.save(General.START_DATE, start_date, TAG);
        AddGoalPreferences.save(General.END_DATE, end_date, TAG);
        AddGoalPreferences.save(activity.getApplicationContext().getResources().getString(R.string.daily), isEveryday);

        if (frequency.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.daily)) && !isEveryday) {
            String day_count = editTextFragmentAddGoalTwoSpecificDayCount.getText().toString().trim();
            if (day_count.length() <= 0) {
                day_count = "1";
            }
            AddGoalPreferences.save("day_count", day_count, TAG);
        }
        if (frequency.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.weekly))) {
            AddGoalPreferences.save("week", getSelectedWeek(), TAG);
        }

        if (frequency.equalsIgnoreCase(activity.getApplicationContext()
                .getResources().getString(R.string.month))) {
            if (isMonthDay) {
                String day = editTextFragmentAddGoalTwoFrequencyMonthDayCount.getText().toString().trim();
                String count = editTextFragmentAddGoalTwoFrequencyMonthCount.getText().toString().trim();
                AddGoalPreferences.save("month", day + "-" + count + ",MONTH_DAY", TAG);
            } else {
                String month = editTextFragmentAddGoalTwoFrequencyMonthCountTwo.getText().toString().trim();
                String day_month = spinnerFragmentAddGoalTwoFrequencyMonthWeek.getSelectedItem().toString();
                String week_month = spinnerFragmentAddGoalTwoFrequencyMonthSequence.getSelectedItem().toString();
                AddGoalPreferences.save("month", week_month + "-" + day_month.toLowerCase() + "-" + month + ",MONTH_WEEKDAY", TAG);
            }
        }
        setDates();
    }
}
