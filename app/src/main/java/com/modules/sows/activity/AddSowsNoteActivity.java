package com.modules.sows.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.modules.caseload.werhope.fragment.TimeHourPickerFragment;
import com.modules.sows.model.SowsNotes;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.AddGoalPreferences;
import com.storage.preferences.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.RequestBody;

public class AddSowsNoteActivity extends AppCompatActivity implements View.OnClickListener, TimeHourPickerFragment.TimeHourPickerFragmentInterface {
    private static final String TAG = AddSowsNoteActivity.class.getSimpleName();
    public static AddSowsNoteActivity addProgressNoteActivity;
    private SowsNotes sowsNotesModel;
    private EditText  editNoteEditText, subjectEditText, mTextViewTitle;
    private TextView totalTime, Time, dateTxt, mEnteredCount, mRemainingCount;
    private Button  submitBtn;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private Calendar calendar;
    private String date = "", noteUpdate, mTitle;
    private String titleName, subject, selectDate, selectTime, selectDuration, selectNote;
    private String selectFirstTime = "", selectSecondTime = "";
    private InputFilter filter;
    private int wordsLength, defineWordLength = 1000, wordsLengthRemaining;

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_sows_note_add_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        addProgressNoteActivity = this;
        AddGoalPreferences.initialize(AddSowsNoteActivity.this);

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

        // get data through Intent which we have to pass from previous activity
        Intent data = getIntent();

        // this condition is work if user click on edit or add button
        // if user click on edit button it will be set tile to Edit Note else button click on Add then it will set title to Add note
        if (data != null && data.hasExtra(General.NOTE_DETAILS)) {
            titleText.setPadding(20, 0, 0, 0);
            titleText.setText(getResources().getString(R.string.edit_note));
            sowsNotesModel = (SowsNotes) data.getSerializableExtra(General.NOTE_DETAILS);
            noteUpdate = data.getStringExtra(Actions_.UPDATE_SOWS_NOTE);
            mTitle = data.getStringExtra("title");
            setUpdatedNoteData(sowsNotesModel);
        } else {
            noteUpdate = "";
            titleText.setPadding(50, 0, 0, 0);
            titleText.setText(getResources().getString(R.string.add_note_hint));
        }
    }

    // Variable Declaration Function
    private void initUI() {

        mTextViewTitle = findViewById(R.id.title_txt);
        subjectEditText = (EditText) findViewById(R.id.subject_txt);
        dateTxt = (TextView) findViewById(R.id.select_date_txt);
        Time = (TextView) findViewById(R.id.select_time_txt);
        editNoteEditText = (EditText) findViewById(R.id.desc_txt);

        mEnteredCount = (TextView) findViewById(R.id.txt_count_entered);
        mRemainingCount = (TextView) findViewById(R.id.txt_count_remaining);

        dateTxt.setOnClickListener(this);
        Time.setOnClickListener(this);

        submitBtn = (Button) findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(this);

        mTextViewTitle.setText(mTitle);

        editNoteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {


            }


            // In this we have to count word which user have was written in Description
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                wordsLength = countWords(s.toString());// words.length;
                // i1 == 0 means a new word is going to start
                if (i1 == 0 && wordsLength >= 1000) {
                    setCharLimit(editNoteEditText, editNoteEditText.getText().length());
                } else {
                    removeFilter(editNoteEditText);
                }
                mEnteredCount.setText(String.valueOf(wordsLength));
                wordsLengthRemaining = defineWordLength - Integer.parseInt(mEnteredCount.getText().toString());
                mRemainingCount.setText(String.valueOf(wordsLengthRemaining));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }

    private void setCharLimit(EditText et, int max) {
        filter = new InputFilter.LengthFilter(max);
        et.setFilters(new InputFilter[] { filter });
    }

    private void removeFilter(EditText et) {
        if (filter != null) {
            et.setFilters(new InputFilter[0]);
            filter = null;
        }
    }

    // set Updated data Function
    // when user come to edit this function will set data on that screen
    private void setUpdatedNoteData(SowsNotes sowsNotesList) {
        mTextViewTitle.setText(sowsNotesList.getTitle());
        subjectEditText.setText(sowsNotesList.getSubject());
        dateTxt.setText(sowsNotesList.getDate());
        Time.setText(getTimeInAmPM(sowsNotesList.getTime()));
        editNoteEditText.setText(sowsNotesList.getDescription());
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

            case R.id.select_time_txt:
                openChoiceDialog(false);
                break;

            case R.id.submit_btn:
                titleName = mTextViewTitle.getText().toString().trim();
                subject = subjectEditText.getText().toString().trim();
                selectDate = dateTxt.getText().toString().trim();
                selectTime = Time.getText().toString().trim();
                selectNote = editNoteEditText.getText().toString().trim();

                if (noteUpdate.equalsIgnoreCase(Actions_.UPDATE_SOWS_NOTE)) {
                    if (titleName.equalsIgnoreCase(sowsNotesModel.getTitle())
                            && subject.equalsIgnoreCase(sowsNotesModel.getSubject())
                            && selectDate.equalsIgnoreCase(sowsNotesModel.getDate())) {
                        apiConditionFunction(Actions_.UPDATE_JOURNAL);
                    } else {
                        if (ProgressNoteValidation(titleName, subject, selectDate, selectTime, selectNote, v)) {
                            apiConditionFunction(Actions_.UPDATE_JOURNAL);
                        }
                    }
                } else {
                    if (ProgressNoteValidation(titleName, subject, selectDate, selectTime, selectNote, v)) {
                        apiConditionFunction(Actions_.ADD_JOURNAL);
                    }
                }

                break;
        }
    }


    private String getTimeInAmPM(String time) {
//        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date newdate = null;
        try {
            newdate = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        assert newdate != null;
        return sdf.format(newdate);
    }

    private String getTimeInSecondFormat(String time) {
//        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        Date newdate = null;
        try {
            newdate = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        assert newdate != null;
        return sdf.format(newdate);
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

    /*This function is check that which action we have to call */
    public void apiConditionFunction(String action) {

        selectTime = getTimeInSecondFormat(Time.getText().toString().trim());

        if (action.equals(Actions_.ADD_JOURNAL)) {
            addUpdateSowsNoteAPICalled(action, titleName, subject, selectDate, selectTime, selectNote);
        } else if (action.equals(Actions_.UPDATE_JOURNAL)) {
            addUpdateSowsNoteAPICalled(action, titleName, subject, selectDate, selectTime, selectNote);
        } else {
            onBackPressed();
        }
    }

    /*This Function is for Validation all Fields*/
    private boolean ProgressNoteValidation(String patientName, String subject, String selectDate, String selectTime, String description, View view) {


        /*if (TextUtils.isEmpty(patientName)) {
            ShowSnack.viewWarning(view, "Please provide title", getApplicationContext());
            return false;
        }*/

        if (subject == null || subject.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please provide subject", getApplicationContext());
            return false;
        }

        if (subject.length() < 4 || subject.length() > 500) {
            ShowSnack.viewWarning(view, "subject :Min 4 char required", getApplicationContext());
            return false;
        }


        if (selectDate.equalsIgnoreCase("") || selectDate.equalsIgnoreCase("Select Date")) {
            ShowSnack.viewWarning(view, "Please select date", getApplicationContext());
            return false;
        }

        if (selectTime.equalsIgnoreCase("") || selectTime.equalsIgnoreCase("--Select--")) {
            ShowSnack.viewWarning(view, "Please select time", getApplicationContext());
            return false;
        }

        if (description == null || description.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please provide description", getApplicationContext());
            return false;
        }

        if (description.length() < 4 || description.length() > 1000) {
            ShowSnack.viewWarning(view, "Note:Min 4 char required", getApplicationContext());
            return false;
        }

        return true;
    }

    /*Api call For Add and Edit Data*/
    @SuppressLint("LongLogTag")
    private void addUpdateSowsNoteAPICalled(String action, String title, String subject, String selectDate, String selectTime, String desc) {
        HashMap<String, String> requestMap = new HashMap<>();

        if (action.equals(Actions_.ADD_JOURNAL)) {
            requestMap.put(General.ACTION, action);
        } else {
            requestMap.put(General.ACTION, action);
            requestMap.put(General.ID, String.valueOf(sowsNotesModel.getId()));
        }
        requestMap.put(General.TITLE, title);
        requestMap.put(General.SUBJECT, subject);
        requestMap.put(General.DESC, desc);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.TAGS, "");
        requestMap.put(General.LATITUDE, "");
        requestMap.put(General.LONGITUDE, "");
        requestMap.put(General.LINK, "");
        requestMap.put(General.ATTACHMENTS, "");
        requestMap.put(General.IS_FAV, "");
        requestMap.put(General.DATE, selectDate);
        requestMap.put(General.TIME, selectTime);

        Log.e("sowsNotesAddUpdateResponse",""+requestMap.toString());

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_JOURNALING;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                Log.e("sowsNotesAddUpdateResponse",response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddJournal;

                    if (action.equals(Actions_.ADD_JOURNAL)) {
                        jsonAddJournal = jsonObject.getAsJsonObject(Actions_.ADD_JOURNAL);
                    } else {
                        jsonAddJournal = jsonObject.getAsJsonObject(Actions_.UPDATE_JOURNAL);
                    }

                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(this, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }else {
                        Toast.makeText(this, jsonAddJournal.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
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
//        startTime.setText(time.substring(0, 5) + " " + unit);

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
        Time.setText(time.substring(0, 5) + " " + unit);

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

    @SuppressLint("SetTextI18n")
    private void setTime(String selectFirstTime, String selectSecondTime) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a");
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
