package com.modules.appointment.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.modules.appointment.adapter.CancelAppointmentReasonsAdapter;
import com.modules.appointment.adapter.ServiceStaffListAdapter;
import com.modules.appointment.model.AppointmentReason_;
import com.modules.appointment.model.Appointment_;
import com.modules.appointment.model.Staff;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetSelected;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Appointments_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import okhttp3.RequestBody;

public class AppointmentDetailsActivity extends AppCompatActivity implements View.OnClickListener, CancelAppointmentReasonsAdapter.CancelAppointmentReasonsAdapterListener {
    private static final String TAG = AppointmentDetailsActivity.class.getSimpleName();
    private Toolbar toolbar;
    private Appointment_ appointment;
    private ImageView imageViewEdit, imageViewDelete;
    private TextView clientNameTxt, descriptionTxt, dateTxt, startTimeTxt, endTimeTxt, durationTxt, appointmentStatus, txtEmail, txtPhoneNo;
    private Button showLessBtn, showMoreBtn, rescheduleBtn, rescheduleOneBtn, cancelAppointment;
    private LinearLayout viewMoreLayout, buttonsLayout;
    private RecyclerView recyclerView, recyclerViewOne;
    private ServiceStaffListAdapter serviceStaffListAdapter;
    private ArrayList<Appointment_> appointmentArrayList = new ArrayList<>();
    ArrayList<AppointmentReason_> appointmentReasonList = new ArrayList<>();
    private ArrayList<Staff> staffArrayList = new ArrayList<>(), otherStaffArrayList = new ArrayList<>();
    private String cancel_reason_id = "0";
    private EditText editTextOther;
    private boolean showIcon = false;
    private int isAppointmentAttend, nAppointmentStatus;
    private Handler handler;

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

        setContentView(R.layout.activity_appointment_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.journal_toolbar);
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

        TextView titleText = (TextView) findViewById(R.id.caseload_peer_note_title);
        titleText.setText(this.getResources().getString(R.string.appointment_details));
        titleText.setPadding(50, 0, 0, 0);

        initUI();

        Intent data = getIntent();
        if (data.hasExtra(General.APPOINTMENT)) {
            appointment = (Appointment_) data.getSerializableExtra(General.APPOINTMENT);
            showIcon = data.getBooleanExtra("showIcon", true);
            setAppointmentDetailsData();
//            appointmentDetailsAPI(appointment.getId());
        } else {
            onBackPressed();
        }

        // set appointment details data


        // showHideButtons();
        // buttonVisibility();
    }

    // All Variable Declaration Function
    private void initUI() {
        imageViewEdit = (ImageView) findViewById(R.id.imageview_edit);
        imageViewDelete = (ImageView) findViewById(R.id.imageview_delete);
        imageViewEdit.setVisibility(View.VISIBLE);
        imageViewDelete.setVisibility(View.VISIBLE);
        imageViewEdit.setOnClickListener(this);
        imageViewDelete.setOnClickListener(this);

        clientNameTxt = (TextView) findViewById(R.id.client_name);
        descriptionTxt = (TextView) findViewById(R.id.description_txt);
        dateTxt = (TextView) findViewById(R.id.date_txt);
        startTimeTxt = (TextView) findViewById(R.id.start_time);
        endTimeTxt = (TextView) findViewById(R.id.end_time);
        durationTxt = (TextView) findViewById(R.id.duration_txt);
        appointmentStatus = (TextView) findViewById(R.id.appoint_status_txt);
        viewMoreLayout = (LinearLayout) findViewById(R.id.view_more_layout);
        buttonsLayout = (LinearLayout) findViewById(R.id.btn_layout);
        showLessBtn = (Button) findViewById(R.id.view_less_btn);
        showMoreBtn = (Button) findViewById(R.id.view_more_btn);
        rescheduleBtn = (Button) findViewById(R.id.reschedule_btn);
        rescheduleOneBtn = (Button) findViewById(R.id.reschedule_one_btn);
        cancelAppointment = (Button) findViewById(R.id.cancel_appointment_btn);
        txtEmail = (TextView) findViewById(R.id.email_txt);
        txtPhoneNo = (TextView) findViewById(R.id.phone_no_txt);

        showLessBtn.setOnClickListener(this);
        showMoreBtn.setOnClickListener(this);
        rescheduleBtn.setOnClickListener(this);
        rescheduleOneBtn.setOnClickListener(this);
        cancelAppointment.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_swiperefresh);
        recyclerViewOne = (RecyclerView) findViewById(R.id.recyclerview_swiperefresh_one);
    }

    // Called Function For Set Appointment Details
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setAppointmentDetailsData() {
        clientNameTxt.setText(appointment.getClient_name());
        descriptionTxt.setText(appointment.getDescription());
        dateTxt.setText(GetTime.dateCaps(appointment.getDate()));
        txtEmail.setText(appointment.getEmail());
        if(appointment.getPhone().isEmpty()){
            txtPhoneNo.setText("N/A");
        }else {
            txtPhoneNo.setText(appointment.getPhone());
        }


        if (appointment.getApp_status() == 1) {
            appointmentStatus.setText("Confirmed");
            appointmentStatus.setTextColor(this.getResources().getColor(R.color.self_goal_green));
        } else if (appointment.getApp_status() == 2) {
            appointmentStatus.setText("Rescheduled");
            appointmentStatus.setTextColor(this.getResources().getColor(R.color.busy));
        } else if (appointment.getApp_status() == 3) {
            appointmentStatus.setText("Canceled");
            appointmentStatus.setTextColor(this.getResources().getColor(R.color.busy));
        } else {
            appointmentStatus.setText("Completed");
            appointmentStatus.setTextColor(this.getResources().getColor(R.color.self_goal_green));
        }

        setAppointmentDuration(startTimeTxt, endTimeTxt, durationTxt, appointment.getStart_time().substring(0, 5), appointment.getEnd_time().substring(0, 5));

    }

    // Called Function For button Hide and Show
    private void buttonVisibility() {
       /* if (CheckRole.isCA_CAA(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            buttonsLayout.setVisibility(View.VISIBLE);
        } else {
            buttonsLayout.setVisibility(View.GONE);
        }*/
        // if(isLoginUserCanCancelThisAppointment] && [self isLoginUserCanRescheduleThisAppointment]){

        if (isLoginUserCanCancelThisAppointment() && isLoginUserCanRescheduleThisAppointment()) {
            //show both buttons
            cancelAppointment.setVisibility(View.VISIBLE);
            rescheduleBtn.setVisibility(View.VISIBLE);
        } else if (!isLoginUserCanCancelThisAppointment() && isLoginUserCanRescheduleThisAppointment()) {
            // resch show
            cancelAppointment.setVisibility(View.GONE);
            rescheduleBtn.setVisibility(View.VISIBLE);
        } else if (isLoginUserCanCancelThisAppointment() && !isLoginUserCanRescheduleThisAppointment()) {
            // cancel button show
            cancelAppointment.setVisibility(View.VISIBLE);
            rescheduleBtn.setVisibility(View.GONE);
        } else {
            //hide both buttons
            cancelAppointment.setVisibility(View.GONE);
            rescheduleBtn.setVisibility(View.GONE);
        }

        // Edit Button Logic

        if ((CheckRole.isCA_CAA(Integer.parseInt(Preferences.get(General.ROLE_ID)))) ||
                (appointmentArrayList.get(0).getAdded_by_id() == Integer.parseInt(Preferences.get(General.USER_ID)))) {

            imageViewDelete.setVisibility(View.VISIBLE);

            imageViewEdit.setVisibility(View.VISIBLE);
            if (isAppointmentAttend == 1) {
                imageViewEdit.setVisibility(View.GONE);
            } else if (nAppointmentStatus == General.APPOINTMENT_STATUS_CANCELED) {
                imageViewEdit.setVisibility(View.GONE);
            } else if (nAppointmentStatus == General.APPOINTMENT_STATUS_COMPLETED) {
                imageViewEdit.setVisibility(View.GONE);
            }
        } else {

            imageViewDelete.setVisibility(View.GONE);
            imageViewEdit.setVisibility(View.GONE);
        }

    }

    // Check user is Staff member for Appointment
    private boolean isLoginUserIsInStaffMemberForThisAppointment() {

        boolean isLoginUserMember = false;
        int nLoginUserId = Integer.parseInt(Preferences.get(General.USER_ID));
        if (otherStaffArrayList.size() > 0) {
            for (int i = 0; i < otherStaffArrayList.size(); i++) {
                if (nLoginUserId == otherStaffArrayList.get(i).getId()) {
                    isLoginUserMember = true;
                    break;
                }
            }
        } else {
            for (int i = 0; i < staffArrayList.size(); i++) {
                Log.e(" Login UserId", "" + nLoginUserId);
                Log.e(" StaffMember UserId", "" + staffArrayList.get(i).getId());
                if (nLoginUserId == staffArrayList.get(i).getId()) {
                    isLoginUserMember = true;
                    break;
                }
            }
        }

        Log.e("Login User is as staff:", "" + isLoginUserMember);

        return isLoginUserMember;
    }

    // Check user is able to cancel Appointment
    private boolean isLoginUserCanCancelThisAppointment() {

        Date dateOfEndAppointment = null;
        Date dateCurrent = new Date();
        String strEndAppointment = appointment.getDate() + " " + appointment.getEnd_time();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dateOfEndAppointment = format.parse(strEndAppointment);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean hasPermission = false;
        if ((isLoginUserIsInStaffMemberForThisAppointment() || CheckRole.isCA_CAA(Integer.parseInt(Preferences.get(General.ROLE_ID)))) && dateCurrent.before(dateOfEndAppointment)) {

            hasPermission = true;
            if (isAppointmentAttend == 1) {
                hasPermission = false;
            } else if (nAppointmentStatus == General.APPOINTMENT_STATUS_CANCELED) {
                hasPermission = false;
            } else if (nAppointmentStatus == General.APPOINTMENT_STATUS_COMPLETED) {
                hasPermission = false;
            }
        }

        return hasPermission;
    }

    // check user is able to Reschedule Appointment
    private boolean isLoginUserCanRescheduleThisAppointment() {

        Date dateOfEndAppointment = null;
        Date dateCurrent = new Date();
        String strEndAppointment = appointment.getDate() + " " + appointment.getEnd_time();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dateOfEndAppointment = format.parse(strEndAppointment);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean hasPermission = false;
        if (isLoginUserIsInStaffMemberForThisAppointment() || CheckRole.isCA_CAA(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {

            hasPermission = true;
            if (isAppointmentAttend == 1) {
                hasPermission = false;
            } else if (nAppointmentStatus == General.APPOINTMENT_STATUS_CANCELED) {
                hasPermission = false;
            } else if (nAppointmentStatus == General.APPOINTMENT_STATUS_COMPLETED) {
                hasPermission = false;
            } else if ((nAppointmentStatus == General.APPOINTMENT_STATUS_CONFIRMED || nAppointmentStatus == General.APPOINTMENT_STATUS_RESCHEDULED) &&
                    dateCurrent.after(dateOfEndAppointment)) {
                hasPermission = false;
            }
        }

        return hasPermission;
    }

    private void showHideButtons() {
        if ((CheckRole.isCA_CAA(Integer.parseInt(Preferences.get(General.ROLE_ID)))) || (appointmentArrayList.get(0).getAdded_by_id() == Integer.parseInt(Preferences.get(General.USER_ID)))) {
            imageViewDelete.setVisibility(View.VISIBLE);

            if (showIcon) {
                rescheduleOneBtn.setVisibility(View.VISIBLE);
                buttonsLayout.setVisibility(View.GONE);
            } else {
                rescheduleOneBtn.setVisibility(View.GONE);
                buttonsLayout.setVisibility(View.VISIBLE);
            }

            if ((appointmentArrayList.get(0).getApp_status() < 3 && appointmentArrayList.get(0).getIsAppointmentAttend() == 0)) {
                imageViewEdit.setVisibility(View.VISIBLE);
            } else {
                imageViewEdit.setVisibility(View.GONE);
            }
        } else {
            imageViewDelete.setVisibility(View.GONE);
            if (showIcon) {
                rescheduleOneBtn.setVisibility(View.VISIBLE);
                buttonsLayout.setVisibility(View.GONE);
            } else {
                rescheduleOneBtn.setVisibility(View.GONE);
                buttonsLayout.setVisibility(View.VISIBLE);
            }
        }

        int is_Staff = 0;
        for (int i = 0; i < staffArrayList.size(); i++) {
            if (staffArrayList.get(i).getId() == Integer.parseInt(Preferences.get(General.USER_ID))) {
                is_Staff = 1;
            }
        }

        for (int j = 0; j < appointmentArrayList.size(); j++) {
            if (appointmentArrayList.get(j).getOtherStaffs().size() > 0 && (appointmentArrayList.get(j).getOtherStaffs().get(0).getId() == Integer.parseInt(Preferences.get(General.USER_ID)))) {
                is_Staff = 1;
            }
        }

        if (((CheckRole.isCA_CAA(Integer.parseInt(Preferences.get(General.ROLE_ID)))) || is_Staff == 1) &&
                (appointmentArrayList.get(0).getApp_status() < 3 && appointmentArrayList.get(0).getIsAppointmentAttend() == 0)) {
            if (showIcon) {
                rescheduleOneBtn.setVisibility(View.VISIBLE);
                buttonsLayout.setVisibility(View.GONE);
            } else {
                rescheduleOneBtn.setVisibility(View.GONE);
                buttonsLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (showIcon) {
                rescheduleOneBtn.setVisibility(View.VISIBLE);
                buttonsLayout.setVisibility(View.GONE);
            } else {
                rescheduleOneBtn.setVisibility(View.GONE);
                buttonsLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    // Called for set Time Duration for Appointment
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setAppointmentDuration(TextView startTime, TextView endTime, TextView appointmentTime, String start_time, String end_time) {
        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");

        Date start = null;
        try {
            start = _24HourSDF.parse(start_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date end = null;
        try {
            end = _24HourSDF.parse(end_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String hours = GetCounters.checkDigit(Math.toIntExact(Math.abs((start.getTime() - end.getTime()) / (60 * 60 * 1000) % 24)));
        String minutes = GetCounters.checkDigit(Math.toIntExact(Math.abs((start.getTime() - end.getTime()) / (60 * 1000) % 60)));
        startTime.setText(_12HourSDF.format(start));
        endTime.setText(_12HourSDF.format(end));
        appointmentTime.setText(hours + ":" + minutes + " Hrs.");
    }

    // Clicked Function
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_edit:
                callCreateAppointment(appointmentArrayList);
                break;

            case R.id.imageview_delete:
                showCancelAppointmentDialogBox(getResources().getString(R.string.are_you_sure_you_want_to_delete_this_appointment), true);
                break;

            case R.id.view_less_btn:
                viewMoreLayout.setVisibility(View.GONE);
                showLessBtn.setVisibility(View.GONE);
                showMoreBtn.setVisibility(View.VISIBLE);
                break;

            case R.id.view_more_btn:
                viewMoreLayout.setVisibility(View.VISIBLE);
                showLessBtn.setVisibility(View.VISIBLE);
                showMoreBtn.setVisibility(View.GONE);
                break;

            case R.id.reschedule_btn:
                callRescheduleAppointment(appointmentArrayList);
                break;

            case R.id.reschedule_one_btn:
                callRescheduleAppointment(appointmentArrayList);
                break;

            case R.id.cancel_appointment_btn:
                showCancelAppointmentDialogBox(getResources().getString(R.string.are_you_sure_you_want_to_cancel_this_appointment), false);
                break;

        }
    }

    // Called to Create Appointment
    private void callCreateAppointment(ArrayList<Appointment_> appointmentList) {
        Intent appointmentDetails = new Intent(getApplicationContext(), CreateAppointmentActivity.class);
        appointmentDetails.putExtra(Actions_.UPDATE_APPOINTMENT, Actions_.EDIT_APPOINTMENT);
        appointmentDetails.putExtra(General.APPOINTMENT, appointmentList.get(0));
        startActivity(appointmentDetails);
    }

    // Called to Reschedule Appointment
    private void callRescheduleAppointment(ArrayList<Appointment_> appointmentList) {
        Intent appointmentDetails = new Intent(getApplicationContext(), CreateAppointmentActivity.class);
        appointmentDetails.putExtra(Actions_.RESCHEDULE_APPOINTMENT, Actions_.RESCHEDULE_APPOINTMENT);
        appointmentDetails.putExtra(General.APPOINTMENT_RESCHEDULE, appointmentList.get(0));
        startActivity(appointmentDetails);
    }

    // Called when you have not edit or reschedule appointment
    private void showCancelAppointmentDialogBox(String message, final boolean isDelete) {
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
                dialog.dismiss();
                if (isDelete) {
                    deleteAppointmentAPI();
                } else {
                    openCancelAppointmentDialog();
                }
            }
        });

        dialog.show();
    }

    // Called for Cancel Appointment DialogBox
    private void openCancelAppointmentDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cancel_appointment_dialog);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;

        final RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view);
        editTextOther = (EditText) dialog.findViewById(R.id.editview_other);
        final Button buttonSubmit = (Button) dialog.findViewById(R.id.button_submit);
        final Button buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);

        CancelAppointmentReasonsAdapter cancelAppointmentReasonsAdapter = new CancelAppointmentReasonsAdapter(this, getApplicationContext(), appointmentReasonList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cancelAppointmentReasonsAdapter);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.CANCEL_APPOINTMENT_REASON);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {

                    appointmentReasonList = new ArrayList<>();

                    AppointmentReason_ appointmentReason = new AppointmentReason_();
                    appointmentReason.setId(0);
                    appointmentReason.setReason("Other");
                    appointmentReason.setStatus(1);
                    appointmentReasonList.add(appointmentReason);

                    appointmentReasonList.addAll(Appointments_.appointmentReasonList(response, Actions_.CANCEL_APPOINTMENT_REASON, getApplicationContext(), TAG));
                    Collections.reverse(appointmentReasonList);

                    if (appointmentReasonList.size() > 0) {
                        cancelAppointmentReasonsAdapter = new CancelAppointmentReasonsAdapter(this, getApplicationContext(), appointmentReasonList, this);
                        recyclerView.setAdapter(cancelAppointmentReasonsAdapter);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel_reason_id = GetSelected.cancelAppointment(appointmentReasonList);

                if (cancel_reason_id.equals("0")) {
                    String otherReason = editTextOther.getText().toString().trim();
                    if (otherReason.length() > 0) {
                        cancelAppointmentAPI(Actions_.CANCEL_APPOINTMENT, otherReason);
                        dialog.dismiss();
                    } else {
                        ShowToast.toast("Please enter other reason", getApplicationContext());
                    }
                } else {
                    cancelAppointmentAPI(Actions_.CANCEL_APPOINTMENT, "");
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    // Api Called for cancel Appointment
    private void cancelAppointmentAPI(String action, String otherReason) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.ID, String.valueOf(appointment.getId()));

        if (cancel_reason_id.equals("0")) {
            requestMap.put(General.REASON, "-1");
            requestMap.put(General.DESCRIPTION_OTHER, otherReason);
        } else {
            requestMap.put(General.REASON, cancel_reason_id);
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    final JsonObject jsonAddJournal = jsonObject.getAsJsonObject(action);
                    String message;
                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        /* message = this.getResources().getString(R.string.successful);*/
                        message = jsonAddJournal.get(General.MSG).getAsString();
                        /*SubmitSnackResponse.showSnack(jsonAddJournal.get(General.STATUS).getAsInt(), message, getApplicationContext());*/

                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                Toast.makeText(AppointmentDetailsActivity.this, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                            }
                        }, 1000);
                        finish();
                        /*Toast.makeText(AppointmentDetailsActivity.this,jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();*/
                        /*onBackPressed();*/
                        /* finish();*/
                    } else {
                        /*message = this.getResources().getString(R.string.action_failed);*/
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                Toast.makeText(AppointmentDetailsActivity.this, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                            }
                        }, 1000);
                        finish();
                        /*onBackPressed();*/

                    }
//                    SubmitSnackResponse.showSnack(jsonObject.getInt(General.STATUS), message, getApplicationContext());
                }
                /*onBackPressed();*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Api Called for Appointment Details
    @SuppressLint("NewApi")
    private void appointmentDetailsAPI(int id) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.VIEW_DETAILS);
        requestMap.put(General.ID, String.valueOf(id));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                Log.e("appointmentDetailApi", response);
                if (response != null) {
                    appointmentArrayList = Appointments_.appointmentList(response, Actions_.VIEW_DETAILS, getApplicationContext(), TAG);
                    if (appointmentArrayList.size() > 0) {
                        if (appointmentArrayList.get(0).getStatus() == 1) {
                            for (int i = 0; i < appointmentArrayList.size(); i++) {
                                addServiceLayout(appointmentArrayList.get(i).getServices());
                                if (appointmentArrayList.get(i).getStaff().get(i).getName().equalsIgnoreCase("other")) {
                                    addStaffLayout(appointmentArrayList.get(i).getOtherStaffs());
                                } else {
                                    addStaffLayout(appointmentArrayList.get(i).getStaff());
                                }


                                isAppointmentAttend = appointmentArrayList.get(i).getIsAppointmentAttend();
                                nAppointmentStatus = appointmentArrayList.get(i).getApp_status();
                                appointment = appointmentArrayList.get(i);
                                setAppointmentDetailsData();
                            }

                            //For Edit, Delete, Reschedule, Cancel appointment button show/hide logic
                            buttonVisibility();
                            setAppointmentDetailsData();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void addStaffLayout(ArrayList<Staff> staff) {
        staffArrayList = staff;
        serviceStaffListAdapter = new ServiceStaffListAdapter(this, staff, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerViewOne.setLayoutManager(mLayoutManager);
        recyclerViewOne.setAdapter(serviceStaffListAdapter);
    }

    private void addServiceLayout(ArrayList<Staff> services) {
        serviceStaffListAdapter = new ServiceStaffListAdapter(this, services, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(serviceStaffListAdapter);
    }

    //Api Called for Delete Appointment
    private void deleteAppointmentAPI() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE_APPOINTMENT);
        requestMap.put(General.ID, String.valueOf(appointment.getId()));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddJournal = jsonObject.getAsJsonObject(Actions_.DELETE_APPOINTMENT);
                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(AppointmentDetailsActivity.this, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AppointmentDetailsActivity.this, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                    }
                    onBackPressed();
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
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        appointmentDetailsAPI(appointment.getId());
    }

    // Called When to Show or Hide otherStaffMember Field
    @Override
    public void onOtherClicked(boolean show) {
        if (show) {
            editTextOther.setVisibility(View.GONE);
        } else {
            editTextOther.setVisibility(View.VISIBLE);
        }
    }
}
