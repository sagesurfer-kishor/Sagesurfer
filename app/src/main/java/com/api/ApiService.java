package com.api;


import com.google.gson.JsonArray;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface ApiService {


    @POST("/api/login")
    Call<String> getServerLogin(@Body String body);


}
