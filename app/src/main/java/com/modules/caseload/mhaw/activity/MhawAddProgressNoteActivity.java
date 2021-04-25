package com.modules.caseload.mhaw.activity;

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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.modules.caseload.mhaw.fragment.MhawTimeHourPickerFragment;
import com.modules.caseload.mhaw.model.MhawProgressList;
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

public class MhawAddProgressNoteActivity extends AppCompatActivity implements View.OnClickListener, MhawTimeHourPickerFragment.MhawTimeHourPickerFragmentInterface {
    private static final String TAG = MhawAddProgressNoteActivity.class.getSimpleName();
    public static MhawAddProgressNoteActivity addProgressNoteActivity;
    private MhawProgressList progressList;

    private EditText EdtHcbsStaffTitle, EdtLocationSerAdd, EdtServProvided, EdtGoalTitle, EdtObjectiveTitle, EdtHcbsProgressGoalObj, EdtHcbsReason, EdtHcbsBarrier, EdtHcbsHomeWork;
    private RadioGroup radioGroup;
    private TextView totalTime, startTime, endTime, dateTxt;
    private LinearLayout reasonlayout;
    private Button cancelBtn, submitBtn;
    private boolean reasonSelected = false;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private Calendar calendar;
    private String date = "", noteUpdate;
    private String edtHcbsStaffTitle, edtLocationSerAdd, edtServProvided, edtGoalTitle, edtObjectiveTitle, edtHcbsProgressGoalObj, edtHcbsReason,
            edtHcbsBarrier, edtHcbsHomeWork, selectDate, selectEndTime, selectDuration, selectNote;
    private String selectFirstTime = "", selectSecondTime = "";
    int selectedId;
    RadioButton finalizeradioButton;
    private int number = 0;

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_mhaw_add_progress_note);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        addProgressNoteActivity = this;
        AddGoalPreferences.initialize(MhawAddProgressNoteActivity.this);

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
            progressList = (MhawProgressList) data.getSerializableExtra(Actions_.UPDATE_PROGRESS_NOTE);
            noteUpdate = data.getStringExtra(Actions_.UPDATE_NOTE);
            setUpdatedNotePregressData(progressList);
        } else {
            noteUpdate = "";
            titleText.setPadding(50, 0, 0, 0);
            titleText.setText(getResources().getString(R.string.add_progress_note));
        }
    }

    private void initUI() {

        //Element initialise for all form fields
        EdtHcbsStaffTitle = (EditText) findViewById(R.id.hcbs_staff_title_edt);
        EdtLocationSerAdd = (EditText) findViewById(R.id.location_serv_add__edt);
        EdtServProvided = (EditText) findViewById(R.id.serv_provided_edt);
        EdtGoalTitle = (EditText) findViewById(R.id.hcbs_goal_title_edt);
        EdtObjectiveTitle = (EditText) findViewById(R.id.hcbs_obj_title_edt);
        EdtHcbsProgressGoalObj = (EditText) findViewById(R.id.hcbs_progress_goal_obj_edt);
        EdtHcbsReason = (EditText) findViewById(R.id.hcbs_reason_edt);
        EdtHcbsBarrier = (EditText) findViewById(R.id.hcbs_barrier_edt);
        EdtHcbsHomeWork = (EditText) findViewById(R.id.hcbs_homework_edt);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        dateTxt = (TextView) findViewById(R.id.select_date_txt);
        endTime = (TextView) findViewById(R.id.select_end_time_txt);

        dateTxt.setOnClickListener(this);
        endTime.setOnClickListener(this);
        reasonlayout = (LinearLayout) findViewById(R.id.reason_layout);

        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        submitBtn = (Button) findViewById(R.id.submit_btn);
        cancelBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

    }

    private void setUpdatedNotePregressData(MhawProgressList progressList) {
        //Prefill data in edit mode
        EdtHcbsStaffTitle.setText(progressList.getTitle());
        EdtLocationSerAdd.setText(progressList.getService());
        EdtServProvided.setText(progressList.getService_address());
        EdtGoalTitle.setText(progressList.getGoal_address());
        EdtObjectiveTitle.setText(progressList.getObj_address());
        EdtHcbsProgressGoalObj.setText(progressList.getProgress_address());
        EdtHcbsReason.setText(progressList.getReason());
        EdtHcbsBarrier.setText(progressList.getBarriers_presented());
        EdtHcbsHomeWork.setText(progressList.getHome_work());

        dateTxt.setText(progressList.getMeeting_date());
        endTime.setText(progressList.getMeeting_time());
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
            case R.id.select_end_time_txt:
                openChoiceDialog(false);
                break;
            case R.id.cancel_btn:
                showAddProgressNoteDialog(getResources().getString(R.string.are_you_sure_you_want_to_cancel_this_progress_note), "Cancel");
                break;
            case R.id.submit_btn:
                edtHcbsStaffTitle = EdtHcbsStaffTitle.getText().toString().trim();
                edtLocationSerAdd = EdtLocationSerAdd.getText().toString().trim();
                edtServProvided = EdtServProvided.getText().toString().trim();
                edtGoalTitle = EdtGoalTitle.getText().toString().trim();
                edtObjectiveTitle = EdtObjectiveTitle.getText().toString().trim();
                edtHcbsProgressGoalObj = EdtHcbsProgressGoalObj.getText().toString().trim();
                edtHcbsReason = EdtHcbsReason.getText().toString().trim();
                edtHcbsBarrier = EdtHcbsBarrier.getText().toString().trim();
                edtHcbsHomeWork = EdtHcbsHomeWork.getText().toString().trim();
                selectDate = dateTxt.getText().toString().trim();
                selectedId = radioGroup.getCheckedRadioButtonId();
                Log.e("selectedId", "" + selectedId);
                finalizeradioButton = (RadioButton) findViewById(selectedId);
                if (selectedId == -1) {
                    Toast.makeText(MhawAddProgressNoteActivity.this, "Nothing selected", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(MhawAddProgressNoteActivity.this, finalizeradioButton.getText(), Toast.LENGTH_SHORT).show();
                    String name = String.valueOf(finalizeradioButton.getText());
                    Log.e("name", name);
                    if (name.equalsIgnoreCase("Yes")) {
                        number = 1;
                    } else {
                        number = 2;
                    }
                }
                selectEndTime = endTime.getText().toString().trim();
                if (noteUpdate.equalsIgnoreCase(Actions_.UPDATE_NOTE)) {
                    if (ProgressNoteValidation(edtHcbsStaffTitle, edtLocationSerAdd, edtServProvided, edtGoalTitle, edtObjectiveTitle, edtHcbsProgressGoalObj, edtHcbsReason, edtHcbsBarrier, edtHcbsHomeWork, v)) {
                        showAddProgressNoteDialog(getResources().getString(R.string.are_you_sure_you_want_to_update_this_progress_note), Actions_.EDIT_PROGRESS_NOTE_MHAW);
                    }
                } else {
                    if (ProgressNoteValidation(edtHcbsStaffTitle, edtLocationSerAdd, edtServProvided, edtGoalTitle, edtObjectiveTitle, edtHcbsProgressGoalObj, edtHcbsReason, edtHcbsBarrier, edtHcbsHomeWork, v)) {
                        showAddProgressNoteDialog(getResources().getString(R.string.are_you_sure_you_want_to_submit_this_progress_note), Actions_.ADD_PROGRESS_NOTE_MHAW);
                    }
                }

                break;
        }
    }

    @SuppressLint("CommitTransaction")
    private void openChoiceDialog(boolean show) {
        Bundle bundle = new Bundle();
        MhawTimeHourPickerFragment dialogFrag = new MhawTimeHourPickerFragment();
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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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

                if (action.equals(Actions_.ADD_PROGRESS_NOTE_MHAW)) {
                    addUpdateProgressNoteAPICalled(action, edtHcbsStaffTitle, edtLocationSerAdd, edtServProvided, edtGoalTitle, edtObjectiveTitle, edtHcbsProgressGoalObj, edtHcbsReason, edtHcbsBarrier, edtHcbsHomeWork, String.valueOf(number));
                    dialog.dismiss();
                } else if (action.equals(Actions_.EDIT_PROGRESS_NOTE_MHAW)) {
                    addUpdateProgressNoteAPICalled(action, edtHcbsStaffTitle, edtLocationSerAdd, edtServProvided, edtGoalTitle, edtHcbsProgressGoalObj, edtHcbsProgressGoalObj, edtHcbsReason, edtHcbsBarrier, edtHcbsHomeWork, String.valueOf(number));
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    onBackPressed();
                }
            }
        });

        dialog.show();
    }

    private boolean ProgressNoteValidation(String title, String address, String service, String goalAddress, String objectAddress, String progress, String reason, String barriersPresented, String homeWork, View view) {
//        if (moodScale.equalsIgnoreCase("") || moodScale.equalsIgnoreCase("Select Mood")) {
//            ShowSnack.viewWarning(view, "Please select mood", getApplicationContext());
//            return false;
//        }

        if (title.length() < 3 || title.trim().length() > 30) {
            ShowSnack.viewWarning(view, "Min 3 chars required", getApplicationContext());
            return false;
        }

        if (address.length() < 3 || address.length() > 30) {
            ShowSnack.viewWarning(view, "Min 3 chars required", getApplicationContext());
            return false;
        }

        if (service.length() < 3 || service.length() > 30) {
            ShowSnack.viewWarning(view, "Min 3 chars required", getApplicationContext());
            return false;
        }

        if (goalAddress.length() < 3 || goalAddress.length() > 300) {
            ShowSnack.viewWarning(view, "Min 3 chars required", getApplicationContext());
            return false;
        }

        if (objectAddress.length() < 3 || objectAddress.length() > 300) {
            ShowSnack.viewWarning(view, "Min 3 chars required", getApplicationContext());
            return false;
        }

        if (progress.length() < 3 || progress.length() > 300) {
            ShowSnack.viewWarning(view, "Min 3 chars required", getApplicationContext());
            return false;
        }

        if (reason.length() < 3 || reason.length() > 300) {
            ShowSnack.viewWarning(view, "Min 3 chars required", getApplicationContext());
            return false;
        }

        if (barriersPresented.length() < 3 || barriersPresented.length() > 300) {
            ShowSnack.viewWarning(view, "Min 3 chars required", getApplicationContext());
            return false;
        }

        if (homeWork.length() < 3 || homeWork.length() > 300) {
            ShowSnack.viewWarning(view, "Min 3 chars required", getApplicationContext());
            return false;
        }

        if (selectDate.equalsIgnoreCase("") || selectDate.equalsIgnoreCase("Select Date")) {
            ShowSnack.viewWarning(view, "Please select date", getApplicationContext());
            return false;
        }


        if (selectEndTime.equalsIgnoreCase("") || selectEndTime.equalsIgnoreCase("--Select--")) {
            ShowSnack.viewWarning(view, "Please select time", getApplicationContext());
            return false;
        }

        return true;
    }

    @SuppressLint("SetTextI18n")
    private void addUpdateProgressNoteAPICalled(String action, String title, String address, String service, String goalAddress, String objectAddress, String progress, String reason, String barriersPresented, String homeWork, String finalize) {
        HashMap<String, String> requestMap = new HashMap<>();

        if (action.equals(Actions_.ADD_PROGRESS_NOTE_MHAW)) {
            requestMap.put(General.ACTION, action);
        } else {
            requestMap.put(General.ACTION, action);
            requestMap.put(General.ID, String.valueOf(progressList.getId()));
        }

        requestMap.put(General.USER_ID, Preferences.get(General.NOTE_USER_ID));
        requestMap.put(General.YOUTH_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.TITLE, title);
        requestMap.put(General.ADDRESS, address);
        requestMap.put(General.SERVICES, service);
        requestMap.put(General.GOAL_ADDRESSED, goalAddress);
        requestMap.put(General.OBJ_ADDRESSED, objectAddress);
        requestMap.put(General.PROGRESS, progress);
        requestMap.put(General.REASON, reason);
        requestMap.put(General.BARRIERS, barriersPresented);
        requestMap.put(General.HOMEWORK, homeWork);
        requestMap.put(General.DATE, selectDate);
        requestMap.put(General.TIME, selectEndTime);
        requestMap.put(General.FINALIZE_NOTE, finalize);

        Log.e("requestMapParameter",requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                Log.e("AddProgressNoteResponse", response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddProgressNote;

                    if (action.equals(Actions_.ADD_PROGRESS_NOTE_MHAW)) {
                        jsonAddProgressNote = jsonObject.getAsJsonObject(Actions_.ADD_PROGRESS_NOTE_MHAW);
                    } else {
                        jsonAddProgressNote = jsonObject.getAsJsonObject(Actions_.EDIT_PROGRESS_NOTE_MHAW);
                    }

                    if (jsonAddProgressNote.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(this, jsonAddProgressNote.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error", e.getMessage());
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
