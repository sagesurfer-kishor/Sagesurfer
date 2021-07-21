package com.modules.appointment.activity;

import com.api.ApiClient;
import com.api.ApiService;
import com.base.BaseActivity;
import com.google.gson.JsonElement;
import com.sagesurfer.collaborativecares.R;
import android.os.Bundle;
import java.util.HashMap;
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

        mApiService = ApiClient.getClient(getApplicationContext(), "https://mhaw.sagesurfer.com/phase3/")
                .create(ApiService.class);

        showDialog();

        HashMap<String, Object> parameter = new HashMap<>();

        parameter.put("search", "");
        parameter.put("t", "6362337564a881096228");
        parameter.put("user_id", "464");
        parameter.put("fetch_date", "2020-11-28");
        parameter.put("domain_code", "sage027");
        parameter.put("action", "get_appointment");
        parameter.put("k", "ajhoITA5dlJiSA");
        parameter.put("tz", getTimeZone());
        parameter.put("imei", getIMEI());
        parameter.put("modelno", getModel());
        parameter.put("debug", "1");


        Call<JsonElement> call = mApiService.mobile_mhaw_appointment(parameter);


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