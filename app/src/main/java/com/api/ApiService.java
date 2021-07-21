package com.api;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface ApiService {

    @POST("/api/login")
    Call<String> getServerLogin(@Body String body);

    @POST("mobile_mhaw_appointment.php")
    Call<JsonElement> mobile_mhaw_appointment(@Body RequestBody body);

    Call<String> getServerLogin2(@Body RequestBody body);


}
