package com.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class TokenInterceptor implements Interceptor {

    String token;

    public TokenInterceptor(String token) {
        this.token = token;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        Request newRequest=chain.request().newBuilder()
                .header("Authorization","Bearer "+ token)
                .build();


        return chain.proceed(newRequest);
    }
}