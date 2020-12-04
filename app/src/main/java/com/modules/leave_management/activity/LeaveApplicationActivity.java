package com.modules.leave_management.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.modules.leave_management.models.Leave;
import com.modules.leave_management.models.LeaveManagement;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowSnack;
import com.storage.preferences.Preferences;

import java.util.Calendar;
import java.util.HashMap;

import okhttp3.RequestBody;

public class LeaveApplicationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LeaveApplicationActivity.class.getSimpleName();
    private AppCompatImageView postButton;
    private EditText reasonEditText;
    private TextView startDate, endDate;
    private ImageView startDateImg, endDateImg;
    private String leaveReason, startDateValue, endDateValue;
    private Leave leave;
    private LeaveManagement leaveManagement;
    private String leaveUpdate = "";
    private boolean leaveSelection = false;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private Calendar calendar;
    private int sYear, sMonth, sDay;
    private String start_date = "", end_date = "";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_leave_application);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_cancel_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);

        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        initUI();

        Intent data = getIntent();
        if (data != null && data.hasExtra(General.LEAVE_DATA)) {
            titleText.setPadding(50, 0, 0, 0);
            titleText.setText(getResources().getString(R.string.edit_application));

            leaveUpdate = data.getStringExtra(General.LEAVE_UPDATE);
            leaveSelection = data.getBooleanExtra(General.LEAVE_UPDATE_SUPERVISOR, true);
            leave = (Leave) data.getSerializableExtra(General.LEAVE_DATA);

            setData(leave);

        } else if (data != null && data.hasExtra(General.LEAVE_COACH_DATA)) {
            titleText.setPadding(50, 0, 0, 0);
            titleText.setText(getResources().getString(R.string.edit_application));

            leaveUpdate = data.getStringExtra(General.LEAVE_UPDATE);
            leaveSelection = data.getBooleanExtra(General.LEAVE_UPDATE_SUPERVISOR, false);
            leaveManagement = (LeaveManagement) data.getSerializableExtra(General.LEAVE_COACH_DATA);

            setDataCoach(leaveManagement);

        } else {
            leaveUpdate = "";
            titleText.setPadding(50, 0, 0, 0);
            titleText.setText(this.getResources().getString(R.string.leave_application));
        }
    }

    private void setData(Leave leaveManagement) {
        startDate.setText(leaveManagement.getFrom_date());
        endDate.setText(leaveManagement.getTo_date());
        reasonEditText.setText(leaveManagement.getReason());
    }

    private void setDataCoach(LeaveManagement leaveManagement) {
        startDate.setText(leaveManagement.getFrom_date());
        endDate.setText(leaveManagement.getTo_date());
        reasonEditText.setText(leaveManagement.getReason());
    }

    private void initUI() {
        reasonEditText = (EditText) findViewById(R.id.reason_edit_txt);
        startDate = (TextView) findViewById(R.id.start_date_txt);
        endDate = (TextView) findViewById(R.id.end_date_txt);
        startDateImg = (ImageView) findViewById(R.id.start_date_icon);
        endDateImg = (ImageView) findViewById(R.id.end_date_icon);
        startDateImg.setOnClickListener(this);
        endDateImg.setOnClickListener(this);

        if (Preferences.get(General.ROLE_ID).equals("6")) {
            startDateImg.setClickable(true);
            endDateImg.setClickable(true);
        } else {
            startDateImg.setClickable(false);
            endDateImg.setClickable(true);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_date_icon:
                DatePickerDialog datePickerDialog = new DatePickerDialog(LeaveApplicationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                start_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                try {

                                    if (end_date.equals("")) {
                                        startDate.setText(start_date);
                                    } else {
                                        int result = Compare.validEndDate(start_date, endDate.getText().toString().trim());
                                        if (result == 1) {
                                            start_date = null;
                                            startDate.setText(null);
                                            ShowSnack.textViewWarning(endDate, "Invalid Date", LeaveApplicationActivity.this);
                                        } else {
                                            startDate.setText(start_date);
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_WEEK, -6);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                break;

            case R.id.end_date_icon:

                DatePickerDialog datePickerDialog1 = new DatePickerDialog(LeaveApplicationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                end_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                try {

                                    int result = Compare.validEndDate(end_date, startDate.getText().toString().trim());
                                    if (result == 1) {
                                        endDate.setText(end_date);
                                    } else {
                                        end_date = null;
                                        endDate.setText(null);
                                        ShowSnack.textViewWarning(endDate, "Invalid Date", LeaveApplicationActivity.this);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DAY_OF_WEEK, -6);
                datePickerDialog1.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog1.show();
                break;

            case R.id.imageview_toolbar_save:
                leaveReason = reasonEditText.getText().toString().trim();
                startDateValue = startDate.getText().toString().trim();
                endDateValue = endDate.getText().toString().trim();

                if (LeaveValidation(leaveReason, startDateValue, endDateValue, v)) {
                    if (leaveUpdate.equalsIgnoreCase(General.LEAVE_UPDATE)) {
                        createNewLeaveApplication(leaveReason, Actions_.UPDATE_LEAVE);
                    } else {
                        createNewLeaveApplication(leaveReason, Actions_.ADD_LEAVE);
                    }
                }

                break;
        }
    }

    private void createNewLeaveApplication(String leaveReason, String action) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.FROM_DATE, startDateValue);
        requestMap.put(General.TO_DATE, endDateValue);
        requestMap.put(General.DESCRIPTION, leaveReason);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        if (action.equals(Actions_.UPDATE_LEAVE)) {
            if (leaveSelection) {
                requestMap.put(General.ID, String.valueOf(leave.getId()));
            } else {
                requestMap.put(General.ID, String.valueOf(leaveManagement.getId()));
            }
        }

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_LEAVE_MANAGEMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddLeave = jsonObject.getAsJsonObject(action);
                    if (jsonAddLeave.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(LeaveApplicationActivity.this, jsonAddLeave.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(LeaveApplicationActivity.this, jsonAddLeave.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private boolean LeaveValidation(String leaveReason, String startDate, String endDate, View view) {
        if (startDate == null || startDate.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please select Start Date", getApplicationContext());
            return false;
        }

        if (endDate == null || endDate.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please select End Date", getApplicationContext());
            return false;
        }

        if (leaveReason == null || leaveReason.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Reason: Min 4 char required ", getApplicationContext());
            return false;
        }

        if (leaveReason.length() < 4 || leaveReason.length() > 150) {
            ShowSnack.viewWarning(view, "Reason: Min 4 char required", getApplicationContext());
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

