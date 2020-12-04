package com.modules.caseload.senjam.activity;

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
import android.text.TextUtils;
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
import com.modules.caseload.senjam.model.SenjamListModel;
import com.modules.caseload.werhope.fragment.TimeHourPickerFragment;
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

public class AddDoctorsNoteActivity extends AppCompatActivity implements View.OnClickListener, TimeHourPickerFragment.TimeHourPickerFragmentInterface {
    private static final String TAG = AddDoctorsNoteActivity.class.getSimpleName();
    public static AddDoctorsNoteActivity addProgressNoteActivity;
    private SenjamListModel senjamListModel;
    private EditText  editNoteEditText, subjectEditText;
    private TextView totalTime, startTime, Time, dateTxt, mEnteredCount, mRemainingCount;
    private Button submitBtn;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String date = "", noteUpdate;
    private String patientName,  subject, selectDate, selectTime, selectNote;
    private String selectFirstTime = "", selectSecondTime = "";
    private TextView mPatientNameEditText;
    private InputFilter filter;
    private int wordsLength, defineWordLength = 500, wordsLengthRemaining;

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_add_doctor_progress_note);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        addProgressNoteActivity = this;
        AddGoalPreferences.initialize(AddDoctorsNoteActivity.this);

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

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        initUI();


        // Get data from previous activity
        // Below Condition is Identify that Clinician is coming Form Edit Or Add Click
        // On that Bases the Activity title and Data will be Set
        Intent data = getIntent();
        if (data != null && data.hasExtra(General.NOTE_DETAILS)) {
            titleText.setPadding(20, 0, 0, 0);
            titleText.setText(getResources().getString(R.string.edit_doctor_note));
            senjamListModel = (SenjamListModel) data.getSerializableExtra(General.NOTE_DETAILS);
            noteUpdate = data.getStringExtra(Actions_.UPDATE_DOCTOR_NOTE);

            // Data will be set for Edit Doctor Notes
            setUpdatedDoctorNoteData(senjamListModel);
        } else {
            noteUpdate = "";
            titleText.setPadding(50, 0, 0, 0);
            titleText.setText(getResources().getString(R.string.add_doctor_note));
        }
    }

    // Variable Declaration Function
    private void initUI() {
        mPatientNameEditText =  findViewById(R.id.patient_name_txt);
        subjectEditText = (EditText) findViewById(R.id.edit_subject_txt);
        dateTxt = (TextView) findViewById(R.id.select_date_txt);
        Time = (TextView) findViewById(R.id.select_time_txt);
        mEnteredCount = (TextView) findViewById(R.id.txt_count_entered);
        mRemainingCount = (TextView) findViewById(R.id.txt_count_remaining);
        editNoteEditText = (EditText) findViewById(R.id.desc_edit_txt);

        dateTxt.setOnClickListener(this);
        Time.setOnClickListener(this);
        submitBtn = (Button) findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(this);

        mPatientNameEditText.setText(Preferences.get(General.CONSUMER_NAME));

        // description Edit Text Click Listener
        editNoteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                wordsLength = countWords(s.toString());// words.length;
                // i1 == 0 means a new word is going to start
                if (i1 == 0 && wordsLength >= 500) {
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

    /*PreFills the data of the note In-case user does edit note*/
    private void setUpdatedDoctorNoteData(SenjamListModel senjamList) {
        mPatientNameEditText.setText(Preferences.get(General.CONSUMER_NAME));
        subjectEditText.setText(senjamList.getSubject());
        dateTxt.setText(senjamList.getDate());
        Time.setText(getTimeInAmPM(senjamList.getTime()));
        editNoteEditText.setText(senjamList.getDescription());

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
                c.add(Calendar.DAY_OF_WEEK, -1);
                Date result = c.getTime();
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMinDate(result.getTime());
                datePickerDialog.show();
                break;
            case R.id.select_time_txt:
                openChoiceDialog(false);
                break;
            case R.id.submit_btn: 
                patientName = mPatientNameEditText.getText().toString().trim();
                subject = subjectEditText.getText().toString().trim();
                selectDate = dateTxt.getText().toString().trim();
                selectTime = Time.getText().toString().trim();
                selectNote = editNoteEditText.getText().toString().trim();

                if (noteUpdate.equalsIgnoreCase(Actions_.UPDATE_DOCTOR_NOTE)) {
                    if (patientName.equalsIgnoreCase(Preferences.get(General.CONSUMER_NAME))
                            && subject.equalsIgnoreCase(senjamListModel.getSubject())
                            && selectDate.equalsIgnoreCase(senjamListModel.getDate())) {
                        apiConditionFunction(Actions_.EDIT_PROGRESS_NOTE_SENJAM);
                    } else {
                        if (ProgressNoteValidation(patientName, subject, selectDate, selectTime, selectNote, v)) {
                            apiConditionFunction(Actions_.EDIT_PROGRESS_NOTE_SENJAM);
                        }
                    }
                } else {
                    if (ProgressNoteValidation(patientName, subject, selectDate, selectTime, selectNote, v)) {
                        apiConditionFunction(Actions_.ADD_PROGRESS_NOTE_SENJAM);
                    }
                }

                break;
        }
    }

    private String getTimeInAmPM(String time) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date newdate = null;
        try {
            newdate = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf =  new SimpleDateFormat("hh:mm a");
        String formattedDate = sdf.format(newdate);
        return formattedDate;
    }

    private String getTimeInSecondFormat(String time) {
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        Date newdate = null;
        try {
            newdate = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf =  new SimpleDateFormat("HH:mm:ss");
        String formattedDate = sdf.format(newdate);
        return formattedDate;
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


    /*this function is decide which action will be pass in Api*/
    public void apiConditionFunction(String action){
        selectTime = getTimeInSecondFormat(Time.getText().toString().trim());
        if (action.equals(Actions_.ADD_PROGRESS_NOTE_SENJAM)) {
            /*Api called ADD Action */
            addUpdateProgressNoteAPICalled(action, patientName, subject, selectDate, selectTime, selectNote);
        } else if (action.equals(Actions_.EDIT_PROGRESS_NOTE_SENJAM)) {
            /*Api called Edit Action*/
            addUpdateProgressNoteAPICalled(action, patientName, subject, selectDate, selectTime, selectNote);
        } else {
            onBackPressed();
        }
    }

    /*Validation Function */
    private boolean ProgressNoteValidation(String patientName, String subject, String selectDate, String selectTime, String selectNote, View view) {

        if (TextUtils.isEmpty(patientName)){
            ShowSnack.viewWarning(view, "Please provide Patient Name", getApplicationContext());
            return false;
        }

        if (subject.equalsIgnoreCase("") || subject.trim().length() <= 0) {
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

        if (selectTime.equalsIgnoreCase("") || selectTime.equalsIgnoreCase("Select Time")) {
            ShowSnack.viewWarning(view, "Please select time", getApplicationContext());
            return false;
        }

        if (selectNote.equalsIgnoreCase("") || selectNote.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please Provide Description", getApplicationContext());
            return false;
        }

        if (selectNote.length() < 4 || selectNote.length() > 500) {
            ShowSnack.viewWarning(view, "Note:Min 4 char required", getApplicationContext());
            return false;
        }

        return true;
    }

    @SuppressLint("SetTextI18n")
    private void addUpdateProgressNoteAPICalled(String action,String patientName, String subject, String selectDate, String selectTime, String selectNote) {
        HashMap<String, String> requestMap = new HashMap<>();

        if (action.equals(Actions_.ADD_PROGRESS_NOTE_SENJAM)) {
            requestMap.put(General.ACTION, action);
//            requestMap.put(General.PATIENT_ID, Preferences.get(General.USER_ID));
        } else {
            requestMap.put(General.ACTION, action);
            requestMap.put(General.ID, String.valueOf(senjamListModel.getId()));
        }
        requestMap.put(General.PATIENT_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.SUBJECT, subject);
        requestMap.put(General.DATE, selectDate);
        requestMap.put(General.TIME, selectTime);
        requestMap.put(General.DESCRIPTION, selectNote);
        Log.e("requestParams",requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                Log.e("Add_Update_Response",response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddProgressNote;

                    if (action.equals(Actions_.ADD_PROGRESS_NOTE_SENJAM)) {
                        jsonAddProgressNote = jsonObject.getAsJsonObject(Actions_.ADD_PROGRESS_NOTE_SENJAM);
                    } else {
                        jsonAddProgressNote = jsonObject.getAsJsonObject(Actions_.EDIT_PROGRESS_NOTE_SENJAM);
                    }

                    if (jsonAddProgressNote.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(this, jsonAddProgressNote.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }else {
                        Toast.makeText(this, jsonAddProgressNote.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
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