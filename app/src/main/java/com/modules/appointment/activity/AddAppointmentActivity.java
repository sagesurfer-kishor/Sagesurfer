package com.modules.appointment.activity;

import com.base.BaseActivity;
import com.sagesurfer.collaborativecares.R;
import android.os.Bundle;

public class AddAppointmentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);
        initwidget();
    }

    void initwidget()
    {
        setTitle("Add Appointment");

    }

    @Override
    public void onButtonEvent(int code) {

    }
}