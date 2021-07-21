package com.modules.appointment.activity;

import com.api.ApiClient;
import com.api.ApiService;
import com.base.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAppointmentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);
        initwidget();
    }

    void initwidget() {


        setTitle("Add Appointment");

        if (isInternetAvailable(this)) {
            getAppointmentList();
        } else {
            NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(AddAppointmentActivity.this).build();
            noInternetDialog.showDialog();
        }
    }

    private void getAppointmentList() {

        mApiService = ApiClient.getClient(getApplicationContext(), "https://mhaw.sagesurfer.com/")
                .create(ApiService.class);

        showDialog();

        HashMap<String, Object> parameter = new HashMap<>();


        parameter.put("search", "");
        parameter.put("t", "");
        parameter.put("user_id", "464");
        parameter.put("fetch_date", "2020-11-28");
        parameter.put("tz", "Asia/Kolkata");
        parameter.put("domain_code", "sage027");
        parameter.put("action", "get_appointment");
        parameter.put("imei", "1234");
        parameter.put("modelno", "Samsung");
        parameter.put("k", "ajhoITA5dlJiSA");
        parameter.put("debug", "1");


        Call<JsonElement> call = mApiService.addAppointment(parameter);


        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                hideDialog();


            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                hideDialog();
            }
        });
    }


    @Override
    public void onButtonEvent(int code) {

    }
}