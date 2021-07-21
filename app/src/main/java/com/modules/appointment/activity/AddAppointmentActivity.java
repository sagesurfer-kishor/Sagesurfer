package com.modules.appointment.activity;

import com.api.ApiClient;
import com.api.ApiService;
import com.base.BaseActivity;
import com.google.gson.JsonElement;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.storage.preferences.Preferences;

import android.os.Bundle;
import java.util.HashMap;

import okhttp3.RequestBody;
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
        }
    }

    private void getAppointmentList() {

        mApiService = ApiClient.getClient(getApplicationContext(), Preferences.get(General.DOMAIN) + "/")
                .create(ApiService.class);

        showDialog();

        HashMap<String, String> parameter = new HashMap<>();

        parameter.put("search", "");
        parameter.put("fetch_date", "2020-11-28");
        parameter.put("action", "get_appointment");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody body= NetworkCall_.make(parameter,url,TAG,AddAppointmentActivity.this);


        Call<JsonElement> call = mApiService.mobile_mhaw_appointment(body);


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