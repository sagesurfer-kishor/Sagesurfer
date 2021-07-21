package com.api;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface ApiService {


    @POST("/api/login")
    Call<String> getServerLogin(@Body String body);

    @POST("/phase3/mobile_mhaw_appointment.php")
    Call<JsonElement> addAppointment(@Body HashMap<String, Object> body);


}
