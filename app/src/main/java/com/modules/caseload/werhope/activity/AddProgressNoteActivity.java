package com.modules.caseload.werhope.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
 import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.modules.caseload.werhope.fragment.TimeHourPickerFragment;
import com.modules.caseload.werhope.model.ProgressList;
import com.modules.mood.MoodModel;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.MoodParser_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.AddGoalPreferences;
import com.storage.preferences.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.RequestBody;

public class AddProgressNoteActivity extends AppCompatActivity implements View.OnClickListener, TimeHourPickerFragment.TimeHourPickerFragmentInterface {
    private static final String TAG = AddProgressNoteActivity.class.getSimpleName();
    public static AddProgressNoteActivity addProgressNoteActivity;
    private ProgressList progressList;
    private int item_selection = 1, isAttendanceSelected = 1;
    private EditText studentNameEditText, editNoteEditText, weeklyThemeEditText, reasonEditText;
    private TextView totalTime, startTime, endTime, dateTxt, moodScaleSpinner, attendanceSpinner;
    private LinearLayout reasonlayout;
    private Button cancelBtn, submitBtn;
    private boolean reasonSelected = false;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private Calendar calendar;
    private String date = "", noteUpdate;
    private String studentName, moodScale, weeklyTheme, attendance, reason, selectDate,
            selectStartTime, selectEndTime, selectDuration, selectNote;
    private String selectFirstTime = "", selectSecondTime = "";

    ArrayList<MoodModel> moodModelArrayList = new ArrayList<>();
    String moodID;

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_add_progress_note);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        addProgressNoteActivity = this;
        AddGoalPreferences.initialize(AddProgressNoteActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);

        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        initUI();

        Intent data = getIntent();
        if (data != null && data.hasExtra(Actions_.UPDATE_PROGRESS_NOTE)) {
            titleText.setPadding(20, 0, 0, 0);
            titleText.setText(getResources().getString(R.string.edit_progress_note));
            progressList = (ProgressList) data.getSerializableExtra(Actions_.UPDATE_PROGRESS_NOTE);
            noteUpdate = data.getStringExtra(Actions_.UPDATE_NOTE);
            setUpdatedNotePregressData(progressList);
        } else {
            noteUpdate = "";
            titleText.setPadding(50, 0, 0, 0);
            titleText.setText(getResources().getString(R.string.add_progress_note));
        }

        moodListAPICalled();
    }

    private void initUI() {
        studentNameEditText = (EditText) findViewById(R.id.student_name_txt);
        weeklyThemeEditText = (EditText) findViewById(R.id.weekly_theme_txt);
        moodScaleSpinner = (TextView) findViewById(R.id.mood_scale_spinner);
        attendanceSpinner = (TextView) findViewById(R.id.attendance_spinner);
        reasonEditText = (EditText) findViewById(R.id.reason_edit_txt);
        dateTxt = (TextView) findViewById(R.id.select_date_txt);
        startTime = (TextView) findViewById(R.id.select_start_time_txt);
        endTime = (TextView) findViewById(R.id.select_end_time_txt);
        totalTime = (TextView) findViewById(R.id.duration_txt);
        editNoteEditText = (EditText) findViewById(R.id.note_edit_txt);

        dateTxt.setOnClickListener(this);
        endTime.setOnClickListener(this);
        startTime.setOnClickListener(this);
        attendanceSpinner.setOnClickListener(this);
        moodScaleSpinner.setOnClickListener(this);

        reasonlayout = (LinearLayout) findViewById(R.id.reason_layout);

        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        submitBtn = (Button) findViewById(R.id.submit_btn);
        cancelBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        studentNameEditText.setText(Preferences.get(General.CONSUMER_NAME));
    }

    private void setUpdatedNotePregressData(ProgressList progressList) {
        studentNameEditText.setText(Preferences.get(General.CONSUMER_NAME));
        weeklyThemeEditText.setText(progressList.getWeekly_theme());
        attendanceSpinner.setText(progressList.getAttendance());

        if (progressList.getAttendance().equals("Declined Session")) {
            reasonlayout.setVisibility(View.VISIBLE);
            reasonEditText.setText(progressList.getReason());
        }
        dateTxt.setText(progressList.getDate());
        endTime.setText(progressList.getStart_time());
        startTime.setText(progressList.getEnd_time());
        totalTime.setText(progressList.getTotal_time());
        editNoteEditText.setText(progressList.getNotes());

        selectFirstTime = progressList.getStart_time();
        selectSecondTime = progressList.getEnd_time();

        if (progressList.getMood().equals(getResources().getString(R.string.happy))) {
            moodScaleSpinner.setText(getResources().getString(R.string.happy));
            moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_happy));
        } else if (progressList.getMood().equals(getResources().getString(R.string.anxious))) {
            moodScaleSpinner.setText(getResources().getString(R.string.anxious));
            moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_anxious));
        } else if (progressList.getMood().equals(getResources().getString(R.string.sad))) {
            moodScaleSpinner.setText(getResources().getString(R.string.sad));
            moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_sad));
        } else if (progressList.getMood().equals(getResources().getString(R.string.bored))) {
            moodScaleSpinner.setText(getResources().getString(R.string.bored));
            moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_bored));
        } else if (progressList.getMood().equals(getResources().getString(R.string.fearful))) {
            moodScaleSpinner.setText(getResources().getString(R.string.fearful));
            moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_fearful));
        } else if (progressList.getMood().equals(getResources().getString(R.string.confused))) {
            moodScaleSpinner.setText(getResources().getString(R.string.confused));
            moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_confused));
        } else if (progressList.getMood().equals(getResources().getString(R.string.frustrated))) {
            moodScaleSpinner.setText(getResources().getString(R.string.frustrated));
            moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_frustrated));
        } else if (progressList.getMood().equals(getResources().getString(R.string.angry))) {
            moodScaleSpinner.setText(getResources().getString(R.string.angry));
            moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_angry));
        } else if (progressList.getMood().equals(getResources().getString(R.string.excited))) {
            moodScaleSpinner.setText(getResources().getString(R.string.excited));
            moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_excited));
        }
        moodID = String.valueOf(progressList.getMood_id());

        submitBtn.setText(getResources().getString(R.string.update));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_date_txt:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                try {
                                    dateTxt.setText(date);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_WEEK, -14);
                Date result = c.getTime();
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMinDate(result.getTime());
                datePickerDialog.show();
                break;
            case R.id.select_start_time_txt:
                openChoiceDialog(true);
                break;
            case R.id.select_end_time_txt:
                openChoiceDialog(false);
                break;
            case R.id.mood_scale_spinner:
                showMoodPopupMenu(v);
                break;
            case R.id.attendance_spinner:
                showAttendancePopupMenu(v);
                break;
            case R.id.cancel_btn:
                showAddProgressNoteDialog(getResources().getString(R.string.are_you_sure_you_want_to_cancel_this_progress_note), "Cancel");
                break;
            case R.id.submit_btn:
                studentName = studentNameEditText.getText().toString().trim();
                moodScale = moodScaleSpinner.getText().toString().trim();
                weeklyTheme = weeklyThemeEditText.getText().toString().trim();
                attendance = attendanceSpinner.getText().toString().trim();
                reason = reasonEditText.getText().toString().trim();
                selectDate = dateTxt.getText().toString().trim();
                selectStartTime = startTime.getText().toString().trim();
                selectEndTime = endTime.getText().toString().trim();
                selectDuration = totalTime.getText().toString().trim();
                selectNote = editNoteEditText.getText().toString().trim();

                if (noteUpdate.equalsIgnoreCase(Actions_.UPDATE_NOTE)) {
                    if (studentName.equalsIgnoreCase(Preferences.get(General.CONSUMER_NAME))
                            && moodScale.equalsIgnoreCase(progressList.getMood())
                            && weeklyTheme.equalsIgnoreCase(progressList.getWeekly_theme())
                            && selectDate.equalsIgnoreCase(progressList.getDate())
                            && selectStartTime.equalsIgnoreCase(progressList.getStart_time())) {
                        showAddProgressNoteDialog(getResources().getString(R.string.edit_not_changed_anything_submit_msg), Actions_.EDIT_PROGRESS_NOTE_WER_NEW);
                    } else if (studentName.equalsIgnoreCase(Preferences.get(General.CONSUMER_NAME))
                            || moodScale.equalsIgnoreCase(progressList.getMood())
                            || weeklyTheme.equalsIgnoreCase(progressList.getWeekly_theme())
                            || selectDate.equalsIgnoreCase(progressList.getDate())
                            || selectStartTime.equalsIgnoreCase(progressList.getStart_time())) {
                        if (ProgressNoteValidation(moodScale, weeklyTheme, reason, selectDate, selectStartTime, selectEndTime, selectNote, v)) {
                            showAddProgressNoteDialog(getResources().getString(R.string.are_you_sure_you_want_to_update_this_progress_note), Actions_.EDIT_PROGRESS_NOTE_WER_NEW);
                        }
                    } else {
                        if (ProgressNoteValidation(moodScale, weeklyTheme, reason, selectDate, selectStartTime, selectEndTime, selectNote, v)) {
                            showAddProgressNoteDialog(getResources().getString(R.string.are_you_sure_you_want_to_update_this_progress_note), Actions_.EDIT_PROGRESS_NOTE_WER_NEW);
                        }
                    }
                } else {
                    if (ProgressNoteValidation(moodScale, weeklyTheme, reason, selectDate, selectStartTime, selectEndTime, selectNote, v)) {
                        showAddProgressNoteDialog(getResources().getString(R.string.are_you_sure_you_want_to_submit_this_progress_note), Actions_.ADD_PROGRESS_NOTE_WER_NEW);
                    }
                }

                break;
        }
    }

    @SuppressLint("CommitTransaction")
    private void openChoiceDialog(boolean show) {
        Bundle bundle = new Bundle();
        TimeHourPickerFragment dialogFrag = new TimeHourPickerFragment();
        bundle.putString(General.FREQUENCY, "details");
        bundle.putString(General.DESCRIPTION, "1");
        bundle.putString(General.IS_FROM_CASELOAD, "time");
        bundle.putBoolean("show", show);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getSupportFragmentManager().beginTransaction(), General.TIMESTAMP);
    }

    @SuppressLint("SetTextI18n")
    private void showAddProgressNoteDialog(String message, final String action) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_peer_note_edit);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView textViewMsg = (TextView) dialog.findViewById(R.id.textview_msg);
        final Button buttonYes = (Button) dialog.findViewById(R.id.button_yes);
        final ImageButton buttonClose = (ImageButton) dialog.findViewById(R.id.button_close);
        textViewMsg.setText(message);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (action.equals(Actions_.ADD_PROGRESS_NOTE_WER_NEW)) {
                    addUpdateProgressNoteAPICalled(action, moodScale, weeklyTheme, attendance, reason, selectDate, selectStartTime, selectEndTime, selectDuration, selectNote);
                    dialog.dismiss();
                } else if (action.equals(Actions_.EDIT_PROGRESS_NOTE_WER_NEW)) {
                    addUpdateProgressNoteAPICalled(action, moodScale, weeklyTheme, attendance, reason, selectDate, selectStartTime, selectEndTime, selectDuration, selectNote);
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    onBackPressed();
                }
            }
        });

        dialog.show();
    }

    private boolean ProgressNoteValidation(String moodScale, String weeklyTheme, String selectReason, String selectDate, String selectStartTime, String selectEndTime, String selectNote, View view) {
        if (moodScale.equalsIgnoreCase("") || moodScale.equalsIgnoreCase("Select Mood")) {
            ShowSnack.viewWarning(view, "Please select mood", getApplicationContext());
            return false;
        }

        if (weeklyTheme == null || weeklyTheme.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please provide weekly theme", getApplicationContext());
            return false;
        }

        if (weeklyTheme.length() < 4 || weeklyTheme.length() > 500) {
            ShowSnack.viewWarning(view, "Weekly theme :Min 4 char required", getApplicationContext());
            return false;
        }


        if (reasonSelected) {
            if (selectReason == null || selectReason.trim().length() <= 0) {
                ShowSnack.viewWarning(view, "Please select reason", getApplicationContext());
                return false;
            }

            if (selectReason.length() < 4 || selectReason.length() > 500) {
                ShowSnack.viewWarning(view, "Reason:Min 4 char required", getApplicationContext());
                return false;
            }

        }

        if (selectDate.equalsIgnoreCase("") || selectDate.equalsIgnoreCase("Select Date")) {
            ShowSnack.viewWarning(view, "Please select date", getApplicationContext());
            return false;
        }

        if (selectStartTime.equalsIgnoreCase("") || selectStartTime.equalsIgnoreCase("--Select--")) {
            ShowSnack.viewWarning(view, "Please select start time", getApplicationContext());
            return false;
        }

        if (selectEndTime.equalsIgnoreCase("") || selectEndTime.equalsIgnoreCase("--Select--")) {
            ShowSnack.viewWarning(view, "Please select end time", getApplicationContext());
            return false;
        }

        if (selectNote == null || selectNote.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please provide notes details", getApplicationContext());
            return false;
        }

        if (selectNote.length() < 4 || selectNote.length() > 500) {
            ShowSnack.viewWarning(view, "Note:Min 4 char required", getApplicationContext());
            return false;
        }

        return true;
    }

    private void showAttendancePopupMenu(View view) {
        final PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_attendance, popup.getMenu());
        MenuItem itemPresent = popup.getMenu().findItem(R.id.present_attendance);
        MenuItem itemAbsent = popup.getMenu().findItem(R.id.absent_attendance);
        MenuItem itemDeclined = popup.getMenu().findItem(R.id.declined_attendance);

        if (isAttendanceSelected == 1) {
            itemPresent.setChecked(true);
        }
        if (isAttendanceSelected == 2) {
            itemAbsent.setChecked(true);
        }
        if (isAttendanceSelected == 3) {
            itemDeclined.setChecked(true);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.present_attendance:
                        item.setChecked(!item.isChecked());
                        attendanceSpinner.setText("Present");
                        reasonlayout.setVisibility(View.GONE);
                        reasonSelected = false;
                        isAttendanceSelected = 1;
                        popup.dismiss();
                        break;
                    case R.id.absent_attendance:
                        item.setChecked(!item.isChecked());
                        attendanceSpinner.setText("Absent");
                        reasonlayout.setVisibility(View.GONE);
                        isAttendanceSelected = 2;
                        reasonSelected = false;
                        popup.dismiss();
                        break;
                    case R.id.declined_attendance:
                        item.setChecked(!item.isChecked());
                        attendanceSpinner.setText("Declined Session");
                        reasonlayout.setVisibility(View.VISIBLE);
                        reasonSelected = true;
                        isAttendanceSelected = 3;
                        popup.dismiss();
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    /*private void showMoodPopupMenu(View view) {
        final PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_mood_data_sort, popup.getMenu());
        MenuItem selectMood = popup.getMenu().findItem(R.id.select_mood);
        MenuItem happy = popup.getMenu().findItem(R.id.happy_mood);
        MenuItem anxious = popup.getMenu().findItem(R.id.anxious_mood);
        MenuItem sad = popup.getMenu().findItem(R.id.sad_mood);
        MenuItem bored = popup.getMenu().findItem(R.id.bored_mood);
        MenuItem confused = popup.getMenu().findItem(R.id.fearful_confused);
        MenuItem frustrated = popup.getMenu().findItem(R.id.fearful_frustrated);
        MenuItem angry = popup.getMenu().findItem(R.id.fearful_angry);
        MenuItem excited = popup.getMenu().findItem(R.id.fearful_excited);
        MenuItem fearfull = popup.getMenu().findItem(R.id.fearful_mood);
        fearfull.setVisible(false);

        SpannableString s = new SpannableString(selectMood.getTitle().toString());
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
        selectMood.setTitle(s);

        SpannableString s1 = new SpannableString(happy.getTitle().toString());
        s1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mood_happy)), 0, s1.length(), 0);
        happy.setTitle(s1);

        SpannableString s2 = new SpannableString(anxious.getTitle().toString());
        s2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mood_anxious)), 0, s2.length(), 0);
        anxious.setTitle(s2);

        SpannableString s3 = new SpannableString(sad.getTitle().toString());
        s3.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mood_sad)), 0, s3.length(), 0);
        sad.setTitle(s3);

        SpannableString s4 = new SpannableString(bored.getTitle().toString());
        s4.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mood_bored)), 0, s4.length(), 0);
        bored.setTitle(s4);

        SpannableString s5 = new SpannableString(confused.getTitle().toString());
        s5.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mood_confused)), 0, s5.length(), 0);
        confused.setTitle(s5);

        SpannableString s6 = new SpannableString(frustrated.getTitle().toString());
        s6.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mood_frustrated)), 0, s6.length(), 0);
        frustrated.setTitle(s6);

        SpannableString s7 = new SpannableString(angry.getTitle().toString());
        s7.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mood_angry)), 0, s7.length(), 0);
        angry.setTitle(s7);

        SpannableString s8 = new SpannableString(excited.getTitle().toString());
        s8.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mood_excited)), 0, s8.length(), 0);
        excited.setTitle(s8);

        if (item_selection == 1) {
            selectMood.setChecked(true);
        }
        if (item_selection == 2) {
            happy.setChecked(true);
        }
        if (item_selection == 3) {
            anxious.setChecked(true);
        }

        if (item_selection == 4) {
            sad.setChecked(true);
        }
        if (item_selection == 5) {
            bored.setChecked(true);
        }
        if (item_selection == 6) {
            confused.setChecked(true);
        }

        if (item_selection == 7) {
            frustrated.setChecked(true);
        }

        if (item_selection == 8) {
            angry.setChecked(true);
        }

        if (item_selection == 9) {
            excited.setChecked(true);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.select_mood:
                        item.setChecked(!item.isChecked());
                        moodScaleSpinner.setText("Select Mood");
                        moodScaleSpinner.setTextColor(getResources().getColor(R.color.black));
                        item_selection = 1;
                        popup.dismiss();
                        break;
                    case R.id.happy_mood:
                        item.setChecked(!item.isChecked());
                        moodScaleSpinner.setText(getResources().getString(R.string.happy));
                        moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_happy));
                        item_selection = 2;
                        popup.dismiss();
                        break;

                    case R.id.anxious_mood:
                        item.setChecked(!item.isChecked());
                        moodScaleSpinner.setText(getResources().getString(R.string.anxious));
                        moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_anxious));
                        item_selection = 3;
                        popup.dismiss();
                        break;

                    case R.id.sad_mood:
                        item.setChecked(!item.isChecked());
                        moodScaleSpinner.setText(getResources().getString(R.string.sad));
                        moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_sad));
                        item_selection = 4;
                        popup.dismiss();
                        break;

                    case R.id.bored_mood:
                        item.setChecked(!item.isChecked());
                        moodScaleSpinner.setText(getResources().getString(R.string.bored));
                        moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_bored));
                        item_selection = 5;
                        popup.dismiss();
                        break;

                    case R.id.fearful_confused:
                        item.setChecked(!item.isChecked());
                        moodScaleSpinner.setText(getResources().getString(R.string.confused));
                        moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_confused));
                        item_selection = 6;
                        popup.dismiss();
                        break;

                    case R.id.fearful_frustrated:
                        item.setChecked(!item.isChecked());
                        moodScaleSpinner.setText(getResources().getString(R.string.frustrated));
                        moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_frustrated));
                        item_selection = 7;
                        popup.dismiss();
                        break;

                    case R.id.fearful_angry:
                        item.setChecked(!item.isChecked());
                        moodScaleSpinner.setText(getResources().getString(R.string.angry));
                        moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_angry));
                        item_selection = 8;
                        popup.dismiss();
                        break;

                    case R.id.fearful_excited:
                        item.setChecked(!item.isChecked());
                        moodScaleSpinner.setText(getResources().getString(R.string.excited));
                        moodScaleSpinner.setTextColor(getResources().getColor(R.color.mood_excited));
                        item_selection = 9;
                        popup.dismiss();
                        break;
                }
                return true;
            }
        });
        popup.show();
    }*/
    private void showMoodPopupMenu(View view) {
        MenuItem addedMenuItem = null;
        final PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_mood_data_sort, popup.getMenu());

        for (int i = 0 ; i < moodModelArrayList.size() ; i++){
            MoodModel moodM = moodModelArrayList.get(i);
            addedMenuItem = popup.getMenu().add(R.id.group, moodModelArrayList.get(i).getId(), Menu.NONE, moodModelArrayList.get(i).getName());
            SpannableString s = new SpannableString(addedMenuItem.getTitle().toString());
            s.setSpan(new ForegroundColorSpan(Color.parseColor(moodModelArrayList.get(i).getColorName())), 0, s.length(), 0);
            addedMenuItem.setTitle(s);
            if(moodM.isSelected()) {
                addedMenuItem.setChecked(true);
            }

            popup.getMenu().setGroupCheckable(R.id.group, true, true);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                    menuItem.setChecked(!menuItem.isChecked());
                    for (int i = 0 ; i < moodModelArrayList.size() ; i++){
                            MoodModel moodM = moodModelArrayList.get(i);
                           if(menuItem.getTitle().toString().equals(moodM.getName())) {
                               moodM.setSelected(menuItem.isChecked());
                               moodID = String.valueOf(moodM.getId());
                               moodScaleSpinner.setText(moodM.getName());
                               moodScaleSpinner.setTextColor(Color.parseColor(moodM.getColorName()));
                               break;
                           }
                        }

                    popup.dismiss();

                return true;
            }
        });
        popup.show();
    }

    public void moodListAPICalled()
    {

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_MOODS);

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MOOD_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("moodListResponse", response);
                if (response != null) {
                    moodModelArrayList = MoodParser_.parseMoodList(response, Actions_.GET_MOODS, this, TAG);
                    if (moodModelArrayList.size() > 0) {
                        if (moodModelArrayList.get(0).getStatus() == 1) {
                            MoodModel moodModel = new MoodModel();
                            moodModel.setId(0);
                            moodModel.setName("Select Mood");
                            moodModel.setColorName("#000000");
                            moodModelArrayList.add(0,moodModel);
                        } else {
                            Log.e("ErrorMood",""+moodModelArrayList.get(0).getStatus());
                        }
                    } else {
//                        showError(true, progressList.get(0).getStatus());
                        Log.e("ErrorMood_1",""+moodModelArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("errorPNListActivity", e.getMessage());
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void addUpdateProgressNoteAPICalled(String action, String moodScale, String weeklyTheme, String selectAttendance, String selectReason, String selectDate, String selectStartTime, String selectEndTime, String selectTotalTime, String selectNote) {
        HashMap<String, String> requestMap = new HashMap<>();

        if (action.equals(Actions_.ADD_PROGRESS_NOTE_WER_NEW)) {
            requestMap.put(General.ACTION, action);
        } else {
            requestMap.put(General.ACTION, action);
            requestMap.put(General.ID, String.valueOf(progressList.getId()));
        }

        requestMap.put(General.STUD_ID, Preferences.get(General.NOTE_USER_ID));

        /*if (moodScale.equals(getResources().getString(R.string.happy))) {
            requestMap.put(General.MOOD, "1");
        } else if (moodScale.equals(getResources().getString(R.string.anxious))) {
            requestMap.put(General.MOOD, "4");
        } else if (moodScale.equals(getResources().getString(R.string.sad))) {
            requestMap.put(General.MOOD, "6");
        } else if (moodScale.equals(getResources().getString(R.string.bored))) {
            requestMap.put(General.MOOD, "9");
        } else if (moodScale.equals(getResources().getString(R.string.fearful))) {
            requestMap.put(General.MOOD, "10");
        } else if (moodScale.equals(getResources().getString(R.string.angry))) {
            requestMap.put(General.MOOD, "11");
        } else if (moodScale.equals(getResources().getString(R.string.frustrated))) {
            requestMap.put(General.MOOD, "12");
        } else if (moodScale.equals(getResources().getString(R.string.excited))) {
            requestMap.put(General.MOOD, "13");
        } else if (moodScale.equals(getResources().getString(R.string.confused))) {
            requestMap.put(General.MOOD, "14");

        }*/
        requestMap.put(General.MOOD, moodID);
        requestMap.put(General.WEEKLY_THEME, weeklyTheme);
        requestMap.put(General.ATTENDANCE, selectAttendance);
        requestMap.put(General.REASON, selectReason);
        requestMap.put(General.DATE, selectDate);
        requestMap.put(General.START_TIME, selectStartTime);
        requestMap.put(General.END_TIME, selectEndTime);
        requestMap.put(General.TOTAL_TIME, selectTotalTime);
        requestMap.put(General.NOTES, selectNote);
        Log.e("requestParams",requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddProgressNote;

                    if (action.equals(Actions_.ADD_PROGRESS_NOTE_WER_NEW)) {
                        jsonAddProgressNote = jsonObject.getAsJsonObject(Actions_.ADD_PROGRESS_NOTE_WER_NEW);
                    } else {
                        jsonAddProgressNote = jsonObject.getAsJsonObject(Actions_.EDIT_PROGRESS_NOTE_WER_NEW);
                    }

                    if (jsonAddProgressNote.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(this, jsonAddProgressNote.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void setStartTime(String time, String unit) {
        selectFirstTime = time + " " + unit;
        startTime.setText(time.substring(0, 5) + " " + unit);

        if (selectFirstTime.equalsIgnoreCase(selectSecondTime)) {
            ShowToast.successful("End time must be greater than start time", this);
            totalTime.setText("");
        } else {
            if (selectSecondTime.length() > 1) {
                setTime(selectFirstTime, selectSecondTime);
            }
        }

    }

    @Override
    public void setEndTime(String time, String unit) {
        selectSecondTime = time + " " + unit;
        endTime.setText(time.substring(0, 5) + " " + unit);

        if (selectFirstTime.equalsIgnoreCase(selectSecondTime)) {
            ShowToast.successful("End time must be greater than start time", this);
            totalTime.setText("");
        } else {
            date(selectFirstTime, selectSecondTime);
        }
    }

    private void date(String selectFirstTime, String selectSecondTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
            Date start = sdf.parse(selectFirstTime);
            Date end = sdf.parse(selectSecondTime);

            if (start.after(end)) {
                ShowToast.successful("End time must be greater than start time", this);
            } else {
                setTime(selectFirstTime, selectSecondTime);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setTime(String selectFirstTime, String selectSecondTime) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a");
        Date date1 = null;
        try {
            date1 = format.parse(selectFirstTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = format.parse(selectSecondTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = date2.getTime() - date1.getTime();
        totalTime.setText(String.valueOf(Math.abs(difference / (60 * 60 * 1000) % 24)) + ":" + String.valueOf(Math.abs(difference / (60 * 1000) % 60)));
    }

}
