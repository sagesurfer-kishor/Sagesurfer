package com.sagesurfer.library;

import android.app.Activity;
import android.content.Context;

import com.sagesurfer.constant.General;
import com.sagesurfer.logger.Logger;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.secure.KeyMaker_;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Monika M(monikam@sagesurfer.com)
 * Created on 4/3/2018
 * Last Modified on 4/3/2018
 */

public class FileUpload {

    private static final String TAG = FileUpload.class.getSimpleName();

    public static String uploadFile(String file_path, String file_name, String user_id, String url,
                                    String action, Context context, Activity activity) throws Exception {
        OkHttpClient client = new OkHttpClient();
        File file = new File(file_path);
        String contentType = file.toURL().openConnection().getContentType();
        RequestBody fileBody = RequestBody.create(MediaType.parse(contentType), file);
        HashMap<String, String> keyMap = KeyMaker_.getKey();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(General.USER_ID, user_id)
                .addFormDataPart(General.ACTION, action)
                .addFormDataPart(General.TOKEN, keyMap.get(General.TOKEN))
                .addFormDataPart(General.KEY, keyMap.get(General.KEY))
                .addFormDataPart("userfile", file_name, fileBody)
                .addFormDataPart(General.IMEI, DeviceInfo.getDeviceId(activity))
                .build();
        Request request_new = new Request.Builder()
                .url(url)
                .post(requestBody)
                .tag(TAG)
                .build();
        String s = url + "?" + MakeCall.bodyToString(request_new);
        Response response = client.newCall(request_new).execute();
        String output = response.body().string();
        Logger.error(TAG, "Response: " + output, context);
        return output;
    }
}
