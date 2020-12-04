package com.modules.appointment.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.modules.appointment.dialog.ServiceSelectorDialog;
import com.modules.appointment.model.Appointment_;
import com.modules.appointment.model.Staff;
import com.modules.caseload.werhope.fragment.TimeHourPickerFragment;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetSelected;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Appointments_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.validator.LoginValidator;
import com.storage.preferences.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import okhttp3.RequestBody;

public class CreateAppointmentActivity extends AppCompatActivity implements View.OnClickListener, TimeHourPickerFragment.TimeHourPickerFragmentInterface,
        ServiceSelectorDialog.SelectedServices {
    private static final String TAG = CreateAppointmentActivity.class.getSimpleName();
    private EditText selectClientName, selectEmailId, dateTxt, startTime, endTime, phoneNumber, appointmentDesc, teamName, serviceName, staffMember, otherStaffMember;
    private TextView totalTime, appointmentStatus;
    private LinearLayout otherStaffLayout;
    private Button cancelBtn, submitBtn;
    private String selectClient, selectEmail, selectPhone, selectAppDesc, selectDate, selectStartTime, selectEndTime, selectDuration;
    private String selectTeam, selectService, selectStaff;
    private String selectFirstTime = "", selectSecondTime = "";
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private Calendar calendar;
    private ArrayList<Staff> clientArrayList = new ArrayList<>();
    private ArrayList<Staff> serviceArrayList = new ArrayList<>(), staffArrayList = new ArrayList<>(), otherStaffArrayList = new ArrayList<>();
    private String date = "", updateAppointment = "";
    private Appointment_ appointment;
    private int id = 0, group_id = 0, staff_Id = 0;
    private int MODE_STAFF = 1;
    private int MODE_OTHER_STAFF = 2;
    private int STAFF_REQUEST_CODE = 11, OTHER_STAFF_REQUEST_CODE = 12;
    private Staff selectedStaff, selectedOtherStaff;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        //Load Main XML design file
        setContentView(R.layout.activity_create_appointment);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
        if (data != null && data.hasExtra(General.APPOINTMENT)) {
            //Edit Mode
            titleText.setPadding(80, 0, 0, 0);
            titleText.setText(getResources().getString(R.string.edit_appointment));
            selectClientName.setEnabled(false);
            updateAppointment = data.getStringExtra(Actions_.UPDATE_APPOINTMENT);
            appointment = (Appointment_) data.getSerializableExtra(General.APPOINTMENT);
            setUpdatedAppointmentData(appointment);
        } else if (data != null && data.hasExtra(General.APPOINTMENT_RESCHEDULE)) {
            //Reschedule Mode
            titleText.setText(getResources().getString(R.string.reschedule_appointment));
            selectClientName.setEnabled(false);
            phoneNumber.setEnabled(false);
            appointmentDesc.setEnabled(false);
            serviceName.setEnabled(false);
            updateAppointment = data.getStringExtra(Actions_.RESCHEDULE_APPOINTMENT);
            appointment = (Appointment_) data.getSerializableExtra(General.APPOINTMENT_RESCHEDULE);
            setUpdatedAppointmentData(appointment);
        } else {
            //Create Mode
            updateAppointment = "";
            titleText.setPadding(100, 0, 0, 0);
            titleText.setText(getResources().getString(R.string.add_appointment));
        }

    }

    // All Variable Declaration Function
    private void initUI() {
        selectClientName = (EditText) findViewById(R.id.select_client_name);
        selectEmailId = (EditText) findViewById(R.id.email_id_txt);
        phoneNumber = (EditText) findViewById(R.id.phone_no_txt);
        appointmentDesc = (EditText) findViewById(R.id.appointment_desc_txt);
        dateTxt = (EditText) findViewById(R.id.select_date_txt);
        startTime = (EditText) findViewById(R.id.select_start_time_txt);
        endTime = (EditText) findViewById(R.id.select_end_time_txt);
        totalTime = (TextView) findViewById(R.id.duration_txt);

        selectClientName.setOnClickListener(this);
        dateTxt.setOnClickListener(this);
        endTime.setOnClickListener(this);
        startTime.setOnClickListener(this);

        teamName = (EditText) findViewById(R.id.team_name_txt);
        serviceName = (EditText) findViewById(R.id.services_name_txt);
        staffMember = (EditText) findViewById(R.id.staff_member_txt);
        otherStaffMember = (EditText) findViewById(R.id.other_staff_member_txt);
        otherStaffLayout = (LinearLayout) findViewById(R.id.other_staff_layout);
        appointmentStatus = (TextView) findViewById(R.id.appoint_status_txt);

        serviceName.setOnClickListener(this);
        staffMember.setOnClickListener(this);
        otherStaffMember.setOnClickListener(this);

        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        submitBtn = (Button) findViewById(R.id.submit_btn);
        cancelBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    // Called When Updated Data Can be Displayed
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUpdatedAppointmentData(Appointment_ appointment) {
        serviceArrayList = appointment.getServices();
        staffArrayList = appointment.getStaff();
        otherStaffArrayList = appointment.getOtherStaffs();
        for (int i = 0; i < staffArrayList.size(); i++) {
            if (staffArrayList.get(i).getName().equalsIgnoreCase("other")) {
                otherStaffLayout.setVisibility(View.VISIBLE);
            } else {
                otherStaffLayout.setVisibility(View.GONE);
            }
        }


        selectClientName.setText(appointment.getClient_name());
        teamName.setText(appointment.getGroup_name());
        selectEmailId.setText(appointment.getEmail());
        phoneNumber.setText(appointment.getPhone());
        appointmentDesc.setText(appointment.getDescription());

        dateTxt.setText(appointment.getDate());

        String strStartDate = appointment.getStart_time();
        String strEndDate = appointment.getEnd_time();
        Date dateStart = new Date();
        Date dateEnd = new Date();

        SimpleDateFormat formatold = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat formatnew = new SimpleDateFormat("hh:mm:ss a");
        SimpleDateFormat formatnewdisplay = new SimpleDateFormat("hh:mm a");

        try {
            dateStart = formatold.parse(strStartDate);
            dateEnd = formatold.parse(strEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String strTimeStart = formatnew.format(dateStart);
        String strTimeEnd = formatnew.format(dateEnd);
        selectFirstTime = strTimeStart;
        selectSecondTime = strTimeEnd;
/*
        selectFirstTime = appointment.getStart_time() + " AM";
        selectSecondTime = appointment.getEnd_time() + " AM";
*/
        startTime.setText(formatnewdisplay.format(dateStart));
        endTime.setText(formatnewdisplay.format(dateEnd));


        setTime(appointment.getStart_time() + " AM", appointment.getEnd_time() + " AM");

        ArrayList<String> serviceList = new ArrayList<String>();
        for (int i = 0; i < serviceArrayList.size(); i++) {
            serviceList.add(serviceArrayList.get(i).getName());
        }
        serviceName.setText(toCSV(serviceList));

        ArrayList<String> staffNameList = new ArrayList<String>();
        for (int i = 0; i < staffArrayList.size(); i++) {
            staffNameList.add(staffArrayList.get(i).getName());
            staff_Id = staffArrayList.get(i).getId();
        }

        staffMember.setText(toCSV(staffNameList));

        ArrayList<String> otherStaffNameList = new ArrayList<String>();
        for (int i = 0; i < otherStaffArrayList.size(); i++) {
            otherStaffNameList.add(otherStaffArrayList.get(i).getName());
        }
        otherStaffMember.setText(toCSV(otherStaffNameList));

        group_id = appointment.getGroup_id();
        id = appointment.getId();

        //Call API display the service list
        fetchAppointmentServiceNameAPI(General.GET_SERVICES);

        //Call API display the staff list
        fetchStaffNameAPI(General.GET_STAFF, appointment.getGroup_id(), appointment.getClient_id());
    }

    // Clicked Events for this Activity
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_client_name:
                fetchAppointmentClientNameAPI(General.GET_CLIENTS);
                Intent intent = new Intent(CreateAppointmentActivity.this, ClientNameListActivity.class);
                intent.putExtra("staffsArrayList", clientArrayList);
                intent.putExtra("staff", selectedStaff);
                intent.putExtra("Mode", MODE_STAFF);
                startActivityForResult(intent, STAFF_REQUEST_CODE);
                break;
            case R.id.services_name_txt:
                //Open Services list to select Service
                if (serviceArrayList != null && serviceArrayList.size() > 0) {
                    openServiceSelector(General.GET_SERVICES, serviceArrayList);
                } else {
                    ShowToast.successful("Service list unavailable", this);
                }
                break;
            case R.id.staff_member_txt:
                //Open Staff list to select Staff Member
                if (staffArrayList != null && staffArrayList.size() > 0) {
                    openServiceSelector(General.GET_STAFF, staffArrayList);
                } else {
                    ShowToast.successful("Staff list unavailable", this);
                }
                break;
            case R.id.other_staff_member_txt:
                /*if (otherStaffArrayList != null && otherStaffArrayList.size() > 0) {
                    openServiceSelector(General.GET_OTHER_STAFF_DATA, otherStaffArrayList);
                } else {
                    ShowToast.successful("Other Staff list unavailable", this);
                }*/
                //Open Client list to select client
                Intent clientIntent = new Intent(CreateAppointmentActivity.this, ClientNameListActivity.class);
                clientIntent.putExtra("otherStaffArrayList", otherStaffArrayList);
                clientIntent.putExtra("otherStaff", selectedOtherStaff);
                clientIntent.putExtra("Mode", MODE_OTHER_STAFF);
                startActivityForResult(clientIntent, OTHER_STAFF_REQUEST_CODE);

                break;
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
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
                break;
            case R.id.select_start_time_txt:
                openChoiceDialog(true);
                break;
            case R.id.select_end_time_txt:
                openChoiceDialog(false);
                break;
            case R.id.cancel_btn:
                showAddAppointmentDialog(getResources().getString(R.string.are_you_sure_you_want_to_cancel), "Cancel");
                break;
            case R.id.submit_btn:
                selectClient = selectClientName.getText().toString().trim();
                selectEmail = selectEmailId.getText().toString().trim();
                selectPhone = phoneNumber.getText().toString().trim();
                selectAppDesc = appointmentDesc.getText().toString().trim();
                selectDate = dateTxt.getText().toString().trim();
                selectStartTime = startTime.getText().toString().trim();
                selectEndTime = endTime.getText().toString().trim();
                selectDuration = totalTime.getText().toString().trim();
                selectTeam = teamName.getText().toString().trim();
                selectService = serviceName.getText().toString().trim();
                selectStaff = staffMember.getText().toString().trim();

                if (updateAppointment.equalsIgnoreCase(Actions_.EDIT_APPOINTMENT)) {
                    if (selectDate.equalsIgnoreCase(appointment.getDate())
                            && selectStartTime.equalsIgnoreCase(appointment.getStart_time())) {
                        showAddAppointmentDialog(getResources().getString(R.string.edit_not_changed_anything_submit_msg), Actions_.EDIT_APPOINTMENT);
                    } else if (selectDate.equalsIgnoreCase(appointment.getDate())
                            || selectStartTime.equalsIgnoreCase(appointment.getStart_time())) {
                        if (AppointmenteValidation(selectClient, selectEmail, selectPhone, selectAppDesc, selectDate, selectStartTime, selectEndTime, selectService, selectStaff, v)) {
                            showAddAppointmentDialog(getResources().getString(R.string.are_you_sure_you_want_to_update_this_appointment), Actions_.EDIT_APPOINTMENT);
                        }
                    } else {
                        if (AppointmenteValidation(selectClient, selectEmail, selectPhone, selectAppDesc, selectDate, selectStartTime, selectEndTime, selectService, selectStaff, v)) {
                            showAddAppointmentDialog(getResources().getString(R.string.are_you_sure_you_want_to_update_this_appointment), Actions_.EDIT_APPOINTMENT);
                        }
                    }
                } else if (updateAppointment.equalsIgnoreCase(Actions_.RESCHEDULE_APPOINTMENT)) {
                    if (selectDate.equalsIgnoreCase(appointment.getDate())
                            && selectStartTime.equalsIgnoreCase(appointment.getStart_time())) {
                        showAddAppointmentDialog(getResources().getString(R.string.edit_not_changed_anything_submit_msg), Actions_.RESCHEDULE_APPOINTMENT);
                    } else if (selectDate.equalsIgnoreCase(appointment.getDate())
                            || selectStartTime.equalsIgnoreCase(appointment.getStart_time())) {
                        if (AppointmenteValidation(selectClient, selectEmail, selectPhone, selectAppDesc, selectDate, selectStartTime, selectEndTime, selectService, selectStaff, v)) {
                            showAddAppointmentDialog(getResources().getString(R.string.are_you_sure_you_want_to_update_this_appointment_reschedule), Actions_.RESCHEDULE_APPOINTMENT);
                        }
                    } else {
                        if (AppointmenteValidation(selectClient, selectEmail, selectPhone, selectAppDesc, selectDate, selectStartTime, selectEndTime, selectService, selectStaff, v)) {
                            showAddAppointmentDialog(getResources().getString(R.string.are_you_sure_you_want_to_update_this_appointment_reschedule), Actions_.RESCHEDULE_APPOINTMENT);
                        }
                    }
                } else {
                    if (AppointmenteValidation(selectClient, selectEmail, selectPhone, selectAppDesc, selectDate, selectStartTime, selectEndTime, selectService, selectStaff, v)) {
                        showAddAppointmentDialog(getResources().getString(R.string.are_you_sure_you_want_to_submit_this_appointment), Actions_.ADD_APPOINTMENT);
                    }
                }
                break;
        }
    }

    // Api Called when clicked on Service , Staff member and other Staff Member
    private void openServiceSelector(String action, ArrayList<Staff> clientArrayList) {
        Bundle bundle = new Bundle();
        ServiceSelectorDialog dialogFrag = new ServiceSelectorDialog();

        if (action.equalsIgnoreCase(General.GET_SERVICES)) {
            bundle.putSerializable(General.GET_SERVICES, clientArrayList);
        } else if (action.equalsIgnoreCase(General.GET_STAFF)) {
            bundle.putSerializable(General.GET_STAFF, clientArrayList);
        } else {
            bundle.putSerializable(General.GET_OTHER_STAFF_DATA, clientArrayList);
        }

        bundle.putString(General.ACTION, action);
        dialogFrag.setArguments(bundle);

        if (action.equalsIgnoreCase(General.GET_SERVICES)) {
            dialogFrag.show(getSupportFragmentManager().beginTransaction(), General.GET_SERVICES);
        } else if (action.equalsIgnoreCase(General.GET_STAFF)) {
            dialogFrag.show(getSupportFragmentManager().beginTransaction(), General.GET_STAFF);
        } else {
            dialogFrag.show(getSupportFragmentManager().beginTransaction(), General.GET_OTHER_STAFF_DATA);
        }
    }

    // Called For Appointment Validation
    private boolean AppointmenteValidation(String selectClient, String email, String selectPhone, String selectAppDesc, String selectDate, String selectStartTime, String selectEndTime, String selectService, String selectStaff, View view) {


        if (selectClient == null || selectClient.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please select client", getApplicationContext());
            return false;
        }

        if (!LoginValidator.isEmail(email)) {
            ShowToast.toast("Please enter a valid email address", getApplicationContext());
            return false;
        }

//        if (selectPhone == null || selectPhone.trim().length() <= 0) {
//            ShowSnack.viewWarning(view, "Please provide phone number", getApplicationContext());
//            return false;
//        }

        if (selectAppDesc == null || selectAppDesc.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please provide description", getApplicationContext());
            return false;
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

        if (selectService == null || selectService.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please select service", getApplicationContext());
            return false;
        }

        if (selectStaff == null || selectStaff.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please select staff member", getApplicationContext());
            return false;
        }

        Date dateCurrent = new Date();
        Date dateOfEndAppointment = null;

        String strAppDateTime = selectDate + " " + selectEndTime;
        SimpleDateFormat formatold = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        try {
            dateOfEndAppointment = formatold.parse(strAppDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dateCurrent.after(dateOfEndAppointment)) {
            ShowSnack.viewWarning(view, "Please select future time", getApplicationContext());
            return false;
        }


        return true;
    }

    // Called When you not edit anything and then also you submit appointment
    private void showAddAppointmentDialog(String message, final String action) {
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
                if (action.equals(Actions_.ADD_APPOINTMENT) || action.equals(Actions_.EDIT_APPOINTMENT) || action.equals(Actions_.RESCHEDULE_APPOINTMENT)) {
                    addAppointmentAPICalled(action, selectEmail, selectPhone, selectAppDesc, selectDate, selectStartTime, selectEndTime, selectService, selectStaff);
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    onBackPressed();
                }
            }
        });

        dialog.show();
    }

    // Called When Clicked to Select Time Duration for Appointment
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // Called for  Start Time
    @RequiresApi(api = Build.VERSION_CODES.N)
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

    // Called for  End Time
    @RequiresApi(api = Build.VERSION_CODES.N)
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

    // Called for Date Selection
    @RequiresApi(api = Build.VERSION_CODES.N)
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

    // Called for Set Time
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setTime(String selectFirstTime, String selectSecondTime) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(selectFirstTime);
            date2 = format.parse(selectSecondTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String hours = GetCounters.checkDigit(Math.toIntExact(Math.abs((date1.getTime() - date2.getTime()) / (60 * 60 * 1000) % 24)));
        String minutes = GetCounters.checkDigit(Math.toIntExact(Math.abs((date1.getTime() - date2.getTime()) / (60 * 1000) % 60)));

        totalTime.setText(hours + ":" + minutes + " Hr(s).");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("Activity", "The activity has finished");
        if (resultCode == STAFF_REQUEST_CODE) {
            selectedStaff = (Staff) data.getExtras().getSerializable("staff");

            selectClientName.setText(selectedStaff.getName());
            teamName.setText(selectedStaff.getGroup_name());
            selectEmailId.setText(selectedStaff.getEmail());
            phoneNumber.setText(selectedStaff.getPhone());

            group_id = selectedStaff.getGroup_id();
            id = selectedStaff.getId();

            //Call API display the service list
            fetchAppointmentServiceNameAPI(General.GET_SERVICES);

            //Call API display the staff list
            fetchStaffNameAPI(General.GET_STAFF, selectedStaff.getGroup_id(), id);
        } else if (resultCode == OTHER_STAFF_REQUEST_CODE) {
            selectedOtherStaff = (Staff) data.getExtras().getSerializable("otherStaff");
            otherStaffMember.setText(selectedOtherStaff.getName());
        }
    }

    // Api Called for Client Name List
    private void fetchAppointmentClientNameAPI(String action) {
        clientArrayList.clear();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                Log.e("ResponseStaffClient", response);
                if (response != null) {
                    clientArrayList = Appointments_.clientNameList(response, action, getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Api Called for Service Name List
    private void fetchAppointmentServiceNameAPI(String action) {
        Intent data = getIntent();

        if (data != null && (data.hasExtra(General.APPOINTMENT) || data.hasExtra(General.APPOINTMENT_RESCHEDULE))) {

        } else {
            serviceArrayList.clear();
        }

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {

                    if (data != null && (data.hasExtra(General.APPOINTMENT) || data.hasExtra(General.APPOINTMENT_RESCHEDULE))) {
                        ArrayList<Staff> serviceArrayListAll = Appointments_.clientNameList(response, action, getApplicationContext(), TAG);
                        for (int i = 0; i < serviceArrayListAll.size(); i++) {
                            Staff objStaffMain = serviceArrayListAll.get(i);
                            for (int j = 0; j < serviceArrayList.size(); j++) {
                                Staff objStaffOld = serviceArrayList.get(j);
                                if (objStaffOld.getId() == objStaffMain.getId()) {
                                    objStaffMain.setSelected(true);
                                    break;
                                }
                            }
                        }
                        serviceArrayList = serviceArrayListAll;
                    } else {
                        serviceArrayList = Appointments_.clientNameList(response, action, getApplicationContext(), TAG);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Api Called for Staff Name List
    private void fetchStaffNameAPI(String action, int group_id, int client_id) {
        Intent data = getIntent();
        if (data != null && (data.hasExtra(General.APPOINTMENT) || data.hasExtra(General.APPOINTMENT_RESCHEDULE))) {

        } else {
            staffArrayList.clear();
        }
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, String.valueOf(group_id));
        requestMap.put(General.CLIENT_ID, String.valueOf(client_id));
        Log.e("fetchStaffNameAPI", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                Log.e("fetchStaffNameAPI", response);
                if (response != null) {
                    ArrayList<Staff> staffArrayListAll = new ArrayList<>();
                    Staff staff = new Staff();
                    staff.setId(-1);
                    staff.setName("Other");
                    staff.setStatus(1);
                    staffArrayListAll.add(staff);

                    staffArrayListAll.addAll(Appointments_.clientNameList(response, action, getApplicationContext(), TAG));

                    /*String staffName = selectClientName.getText().toString();
                    for (int i = 0; i < staffArrayListAll.size(); i++) {
                        if (!TextUtils.isEmpty(staffName) && staffArrayListAll.get(i).getName().equalsIgnoreCase(staffName)){
                            Log.d("DATTAAAAA", ""+staffArrayListAll.get(i));
                            staffArrayListAll.remove(i);
                            //staffArrayListAll.remove(staffName);
                            break;
                        }
                    }*/


                    Collections.reverse(staffArrayListAll);

                    if (data != null && (data.hasExtra(General.APPOINTMENT) || data.hasExtra(General.APPOINTMENT_RESCHEDULE))) {
                        for (int i = 0; i < staffArrayListAll.size(); i++) {
                            Staff objStaffMain = staffArrayListAll.get(i);
                            for (int j = 0; j < staffArrayList.size(); j++) {
                                Staff objStaffOld = staffArrayList.get(j);
                                if (objStaffOld.getId() == objStaffMain.getId()) {
                                    objStaffMain.setSelected(true);
                                    break;
                                }
                            }
                        }
                    }
                    staffArrayList = staffArrayListAll;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Api Called for Create Appointment
    private void addAppointmentAPICalled(String action, String selectEmail, String selectPhone, String selectAppDesc, String selectDate, String selectStartTime, String selectEndTime, String selectService, String selectStaff) {

        String strStartDate = selectStartTime;
        String strEndDate = selectEndTime;
        Date dateStart = new Date();
        Date dateEnd = new Date();

        SimpleDateFormat formatold = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat formatnew = new SimpleDateFormat("HH:mm:ss");

        try {
            dateStart = formatold.parse(strStartDate);
            dateEnd = formatold.parse(strEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String strTimeStart = formatnew.format(dateStart);
        String strTimeEnd = formatnew.format(dateEnd);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);

        if (action.equals(Actions_.EDIT_APPOINTMENT) || action.equals(Actions_.RESCHEDULE_APPOINTMENT)) {
            requestMap.put(General.ID, String.valueOf(appointment.getId()));
        }

        if (action.equals(Actions_.EDIT_APPOINTMENT) || action.equals(Actions_.ADD_APPOINTMENT)) {
            requestMap.put(General.CLIENT_ID, String.valueOf(id));
            requestMap.put(General.GROUP_ID, String.valueOf(group_id));
            requestMap.put(General.EMAIL, selectEmail);
            requestMap.put(General.PHONE, selectPhone);
            requestMap.put(General.DESCRIPTION, selectAppDesc);
            requestMap.put(General.DATE, selectDate);
            /*requestMap.put(General.START_TIME, selectFirstTime.substring(0, 8));
            requestMap.put(General.END_TIME, selectSecondTime.substring(0, 8));*/
            requestMap.put(General.START_TIME, strTimeStart);
            requestMap.put(General.END_TIME, strTimeEnd);
            requestMap.put(General.SERVICES, GetSelected.getIds(serviceArrayList));
        } else {
            requestMap.put(General.DATE, selectDate);
            requestMap.put(General.START_TIME, strTimeStart);
            requestMap.put(General.END_TIME, strTimeEnd);
        }

        if (staff_Id == -1) {
            requestMap.put(General.STAFF_MEMBERS, "-1");
            //requestMap.put(General.OTHER_STAFF_MEMBERS, GetSelected.getIds(otherStaffArrayList));

            if (selectedOtherStaff.getId() != 0)
                requestMap.put(General.OTHER_STAFF_MEMBERS, String.valueOf(selectedOtherStaff.getId()));
        } else {
            requestMap.put(General.STAFF_MEMBERS, GetSelected.getIds(staffArrayList));
            requestMap.put(General.OTHER_STAFF_MEMBERS, "0");
        }

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                Log.e("ResponseAppp", response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddProgressNote;

                    if (action.equals(Actions_.ADD_APPOINTMENT)) {
                        jsonAddProgressNote = jsonObject.getAsJsonObject(Actions_.ADD_APPOINTMENT);
                        Toast.makeText(this, jsonAddProgressNote.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else if (action.equals(Actions_.EDIT_APPOINTMENT)) {
                        jsonAddProgressNote = jsonObject.getAsJsonObject(Actions_.EDIT_APPOINTMENT);
                        Toast.makeText(this, jsonAddProgressNote.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        jsonAddProgressNote = jsonObject.getAsJsonObject(Actions_.RESCHEDULE_APPOINTMENT);
                        Toast.makeText(this, jsonAddProgressNote.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                        /*finish();*/
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ErrorCAA", e.getMessage());
//                Toast.makeText(this,e.getMessage() , Toast.LENGTH_LONG).show();
            }
        }
    }

    // Called For Selected Service Displayed in TextView
    @Override
    public void selectedServices(ArrayList<Staff> serviceArrayList, boolean isSelected, String action) {
        if (action.equalsIgnoreCase(General.GET_SERVICES)) {
            serviceArrayList = serviceArrayList;
            ArrayList<String> staffNameList = new ArrayList<String>();
            for (int i = 0; i < serviceArrayList.size(); i++) {
                if (serviceArrayList.get(i).isSelected()) {
                    staffNameList.add(serviceArrayList.get(i).getName());
                }
            }
            serviceName.setText(toCSV(staffNameList));
        } else if (action.equalsIgnoreCase(General.GET_STAFF)) {
            staffArrayList = serviceArrayList;
            ArrayList<String> staffNameList = new ArrayList<String>();
            for (int i = 0; i < serviceArrayList.size(); i++) {
                if (serviceArrayList.get(i).isSelected()) {
                    staffNameList.add(serviceArrayList.get(i).getName());
                    staff_Id = serviceArrayList.get(i).getId();
                    if (serviceArrayList.get(i).getName().equals("Other")) {
                        //Call API display the other staff list
                        fetchOtherStaffNameAPI(General.GET_OTHER_STAFF_DATA, group_id, id);
                        otherStaffLayout.setVisibility(View.VISIBLE);
                    } else {
                        otherStaffLayout.setVisibility(View.GONE);
                    }
                }
            }

            staffMember.setText(toCSV(staffNameList));

        } else {
            otherStaffArrayList = serviceArrayList;
            ArrayList<String> staffNameList = new ArrayList<String>();
            for (int i = 0; i < serviceArrayList.size(); i++) {
                if (serviceArrayList.get(i).isSelected()) {
                    staffNameList.add(serviceArrayList.get(i).getName());
                }
            }
            otherStaffMember.setText(toCSV(staffNameList));
        }
    }

    // Api Called for OtherStaff Name List
    private void fetchOtherStaffNameAPI(String action, int group_id, int client_id) {
        otherStaffArrayList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, String.valueOf(group_id));
        requestMap.put(General.CLIENT_ID, String.valueOf(client_id));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    otherStaffArrayList = Appointments_.clientNameList(response, action, getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Called for Converting string to CSV
    public static String toCSV(ArrayList<String> staffNameList) {
        String result = "";
        if (staffNameList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : staffNameList) {
                sb.append(s).append(",");
            }
            result = sb.deleteCharAt(sb.length() - 1).toString();
        }
        return result;
    }

}
