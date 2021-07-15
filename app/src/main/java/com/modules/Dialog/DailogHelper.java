package com.modules.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.modules.appointment.adapter.ServiceStaffListAdapter;
import com.modules.appointment.model.Appointment_;
import com.modules.appointment.model.Staff;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Appointments_;
import com.storage.preferences.Preferences;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Kailash Karankal on 2/13/2020.
 */
public class DailogHelper {
    private static final String TAG = DailogHelper.class.getSimpleName();
    private ArrayList<Appointment_> appointmentArrayList = new ArrayList<>();
    private ServiceStaffListAdapter serviceStaffListAdapter;
    private Activity activity;


    public void appointmentReminderDialog(String appointment_id, Activity activity) {
        activity = activity;
        View view = activity.getLayoutInflater().inflate(R.layout.appointment_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP;

        Switch switchCurrentReminder = view.findViewById(R.id.switch_reminder);
        final TextView onOffTxt = view.findViewById(R.id.on_off_txt);
        TextView clientNameTxt = view.findViewById(R.id.client_name);
        TextView descriptionTxt = view.findViewById(R.id.description_txt);
        TextView dateTxt = view.findViewById(R.id.date_txt);
        TextView startTimeTxt = view.findViewById(R.id.start_time);
        TextView endTimeTxt = view.findViewById(R.id.end_time);
        TextView durationTxt = view.findViewById(R.id.duration_txt);
        TextView appointmentStatus = view.findViewById(R.id.appoint_status_txt);
        ImageView close = view.findViewById(R.id.close_icon);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_swiperefresh);

        final Activity finalActivity = activity;
        switchCurrentReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onOffTxt.setText("YES");
                    pushSettingAPI(finalActivity, "1");
                } else {
                    onOffTxt.setText("NO");
                    pushSettingAPI(finalActivity, "2");
                }
            }
        });


        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.VIEW_DETAILS);
        requestMap.put(General.ID, appointment_id);

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    appointmentArrayList = Appointments_.appointmentList(response, Actions_.VIEW_DETAILS, activity, TAG);
                    if (appointmentArrayList.size() > 0) {
                        if (appointmentArrayList.get(0).getStatus() == 1) {
                            for (int i = 0; i < appointmentArrayList.size(); i++) {
                                clientNameTxt.setText("Appointment with " + appointmentArrayList.get(i).getClient_name());
                                descriptionTxt.setText(appointmentArrayList.get(i).getDescription());
                                dateTxt.setText(GetTime.dateCaps(appointmentArrayList.get(i).getDate()));

                                addServiceLayout(appointmentArrayList.get(i).getServices(), recyclerView);

                                setAppointmentDuration(startTimeTxt, endTimeTxt, durationTxt, appointmentArrayList.get(i).getStart_time().substring(0, 5), appointmentArrayList.get(i).getEnd_time().substring(0, 5));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void pushSettingAPI(Activity activity, String yesNoValue) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.REMINDER_SETTING_YES_NO);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.YES_NO, yesNoValue);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject object = jsonObject.getJSONObject(Actions_.REMINDER_SETTING_YES_NO);
                    Toast.makeText(activity, object.getString("msg"), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addServiceLayout(ArrayList<Staff> services, RecyclerView recyclerView) {
        serviceStaffListAdapter = new ServiceStaffListAdapter(activity, services, activity);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(serviceStaffListAdapter);
    }

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
//        String hours = GetCounters.checkDigit(Math.toIntExact(Math.abs((start.getTime() - end.getTime()) / (60 * 60 * 1000) % 24)));
        //  String minutes = GetCounters.checkDigit(Math.toIntExact(Math.abs((start.getTime() - end.getTime()) / (60 * 1000) % 60)));
        startTime.setText(_12HourSDF.format(start));
        endTime.setText(_12HourSDF.format(end));
        // appointmentTime.setText(hours + ":" + minutes + " Hrs.");
    }
}
