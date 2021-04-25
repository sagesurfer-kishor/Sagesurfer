package com.sagesurfer.network;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.sagesurfer.logger.Logger;
import com.sagesurfer.secure.UrlEncoder_;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

@SuppressWarnings("ConstantConditions")
public class MakeCall {

    private static final OkHttpClient client = new OkHttpClient();
    public static String post(String url, RequestBody formBody, String TAG, Context _context, Activity activity) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .tag(TAG)
                .build();
        Logger.debug(TAG, url + "?" + bodyToString(request), _context);

        Request request_new = new Request.Builder()
                .url(url)
                .post(convertBody(bodyToString(request)))
                .tag(TAG)
                .build();

        Log.e(TAG, "post: getInstancesRequest"+request_new.toString() );

        Logger.debug(TAG, url + "?" + bodyToString(request_new), _context);

        Log.d(TAG, url + "?" + bodyToString(request_new) );

        String s = url + "?" + bodyToString(request_new);

        Response response = client.newCall(request_new).execute();
        if (response != null) {
            String res = response.body().string();
            Logger.debug(TAG, "Response -> " + res, _context);
            if (response.isSuccessful()) {
                return res;
            }
        }
        return null;
    }

    // make normal call without encrypting parameters
    public static void postNormal(String url, RequestBody formBody, String TAG, Context context)
            throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .tag(TAG)
                .build();
        Logger.error(TAG, url + "?" + bodyToString(request), context);
        Response response = client.newCall(request).execute();
        String res = response.body().string();
        Logger.error(TAG, res, context);
    }

    // add encrypted parameters to one key and make call with device details
    private static RequestBody convertBody(String params) {
        params = UrlEncoder_.encrypt(params);
        return new FormBody.Builder()
                .add("akujs", params)
                .add("d", "a")
                .build();
    }

    // convert encrypted parameters to normal string
    public static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
