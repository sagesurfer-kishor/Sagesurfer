package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.appointment.model.AppointmentReason_;
import com.modules.appointment.model.Appointment_;
import com.modules.appointment.model.Staff;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kailash Karankal on 2/4/2020.
 */
public class Appointments_ {
    public static ArrayList<Appointment_> appointmentList(String response, String action, Context _context, String TAG) {
        ArrayList<Appointment_> appointmentArrayList = new ArrayList<>();
        if (response == null) {
            Appointment_ appointment_ = new Appointment_();
            appointment_.setStatus(11);
            appointmentArrayList.add(appointment_);
            return appointmentArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Appointment_ appointment_ = new Appointment_();
            appointment_.setStatus(13);
            appointmentArrayList.add(appointment_);
            return appointmentArrayList;
        }

        if (Error_.noData(response, action, _context) == 2) {
            Appointment_ appointment_ = new Appointment_();
            appointment_.setStatus(2);
            appointmentArrayList.add(appointment_);
            return appointmentArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Appointment_>>() {
            }.getType();
            appointmentArrayList = gson.fromJson(GetJson_.getArray(response, action)
                    .toString(), listType);
        }
        return appointmentArrayList;
    }

    public static ArrayList<AppointmentReason_> appointmentReasonList(String response, String action, Context _context, String TAG) {
        ArrayList<AppointmentReason_> appointmentReasonArrayList = new ArrayList<>();
        if (response == null) {
            AppointmentReason_ appointmentReason_ = new AppointmentReason_();
            appointmentReason_.setStatus(11);
            appointmentReasonArrayList.add(appointmentReason_);
            return appointmentReasonArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            AppointmentReason_ appointmentReason_ = new AppointmentReason_();
            appointmentReason_.setStatus(13);
            appointmentReasonArrayList.add(appointmentReason_);
            return appointmentReasonArrayList;
        }

        if (Error_.noData(response, action, _context) == 2) {
            AppointmentReason_ appointmentReason_ = new AppointmentReason_();
            appointmentReason_.setStatus(2);
            appointmentReasonArrayList.add(appointmentReason_);
            return appointmentReasonArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<AppointmentReason_>>() {
            }.getType();
            appointmentReasonArrayList = gson.fromJson(GetJson_.getArray(response, action)
                    .toString(), listType);
        }
        return appointmentReasonArrayList;
    }

    public static ArrayList<Staff> clientNameList(String response, String action, Context _context, String TAG) {
        ArrayList<Staff> staffArrayList = new ArrayList<>();
        if (response == null) {
            Staff staff = new Staff();
            staff.setStatus(11);
            staffArrayList.add(staff);
            return staffArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Staff staff = new Staff();
            staff.setStatus(13);
            staffArrayList.add(staff);
            return staffArrayList;
        }

        if (Error_.noData(response, action, _context) == 2) {
            Staff staff = new Staff();
            staff.setStatus(2);
            staffArrayList.add(staff);
            return staffArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Staff>>() {
            }.getType();
            staffArrayList = gson.fromJson(GetJson_.getArray(response, action)
                    .toString(), listType);
        }
        return staffArrayList;
    }
}
