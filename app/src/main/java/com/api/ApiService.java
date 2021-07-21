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

    @POST("mobile_mhaw_appointment.php")
    Call<JsonElement> mobile_mhaw_appointment(@Body HashMap<String, Object> body);


}
