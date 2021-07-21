package com.sagesurfer.network;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sagesurfer.collaborativecares.LoginActivity;
import com.sagesurfer.constant.General;
import com.sagesurfer.constant.Oauth;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.logger.Logger;
import com.sagesurfer.models.Token_;
import com.sagesurfer.oauth.RefreshToken;
import com.sagesurfer.secure.KeyMaker_;
import com.sagesurfer.secure.UrlEncoder_;
import com.sagesurfer.tasks.PerformLogoutTask;
import com.storage.preferences.OauthPreferences;
import com.storage.preferences.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

@SuppressWarnings("ConstantConditions")
public class NetworkCall_ {
    private static ProgressDialog pDialog;

    public static void showDialog(Context _context) {
        try {
            pDialog = new ProgressDialog(_context);
            pDialog.setCancelable(false);
            pDialog.setMessage("Loading...");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void hideDialog() {
        try {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static final String TAG = NetworkCall_.class.getSimpleName();
    private static RefreshToken refreshToken;

    public static RequestBody make(HashMap<String, String> map, String url, String tag, Context _context) {
        /*String device_id = "";
        TelephonyManager tManager = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        if (tManager != null) {
            device_id = tManager.getDeviceId();
        }*/
        //HashMap<String, String> requestMap = new HashMap<>();
        //String deviceId = Settings.Secure.getString(_context.getContentResolver(), Settings.Secure.ANDROID_ID);

        map.put(General.IMEI, DeviceInfo.getDeviceId(_context));
        refreshToken = new RefreshToken(_context);

        Request request = new Request.Builder()
                .url(url)
                .post(makeBody(map))
                .tag(tag)
                .build();
        String body = bodyToString(request);

        Logger.debug(tag, url + "?" + body, _context);
        String token = getToken(_context);
        if (token != null) {
            return finalBody(body, token);
        }
        return null;
    }

    public static RequestBody make(HashMap<String, String> map, String url, String tag,
                                   Context _context, Activity activity) {
        //HashMap<String, String> requestMap = new HashMap<>();
        /*String deviceID = DeviceInfo.getDeviceId(activity);
        String deviceMAC = DeviceInfo.getDeviceMAC(activity);
        String deviceIDMEI = DeviceInfo.getImei(activity);
        String deviceId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);*/
        map.put(General.IMEI, DeviceInfo.getDeviceId(activity));
        map.put(General.VERSION, DeviceInfo.getVersion());
        map.put(General.MODELNO, DeviceInfo.getDeviceName());
        refreshToken = new RefreshToken(_context);
        Request request = new Request.Builder()
                .url(url)
                .post(makeBody(map))
                .tag(tag)
                .build();

        String body = bodyToString(request);
        Log.e(TAG, tag + " make: request body " + body);
        Logger.debug(tag, url + "?" + body, _context);
        String token = getToken(_context);
        if (token != null) {
            return finalBody(body, token);
        }
        return null;
    }

    public static RequestBody make_one(HashMap<String, String> map, String url, String tag,
                                       Context _context, Activity activity) {
        map.put(General.IMEI, DeviceInfo.getDeviceId(activity));
        Request request = new Request.Builder()
                .url(url)
                .post(makeBody(map))
                .tag(tag)
                .build();
        String body = bodyToString(request);
        Logger.debug(tag, "" + url + "?" + body, _context);

        return null;
    }

    public static String post(String url, RequestBody formBody, String tag, Context _context, Activity activity) throws Exception {
        try {

            MyCall myCall = new MyCall(url, formBody, tag, _context, activity);
            return myCall.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static final class MyCall extends AsyncTask<Void, Void, String> {
        final String url;
        final String tag;
        final RequestBody formBody;
        final Context _context;
        final Activity activity;

        MyCall(String url, RequestBody formBody, String tag, Context _context, Activity activity) {
            this.url = url;
            this.formBody = formBody;
            this.tag = tag;
            this._context = _context;
            this.activity = activity;
        }

        @Override
        protected String doInBackground(Void... params) {
            //OkHttpClient client = new OkHttpClient();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .tag(tag)
                    .build();
            Logger.debug(tag, url + "?" + bodyToString(request), _context);
            String s = url + "?" + bodyToString(request);

            try {
                Response response = client.newCall(request).execute();
                String res = response.body().string();
                Log.e(TAG, "doInBackground: " + res);
                if (res.trim().length() > 4) {
                    Log.e(TAG, "doInBackground: teamsResponse" + response);
                    JsonElement jelement = new JsonParser().parse(res);

                    if (jelement != null) {
                        JsonObject jsonObject1 = jelement.getAsJsonObject();
                        JSONObject jsonObject = new JSONObject(res);

                        Logger.debug(tag, res, _context);
                        if (response.isSuccessful()) {
                            if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 13) {
                                return "13";
                            } else if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 21) {
                                PerformLogoutTask.logout(activity);
                            } else if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 20) {
                                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + my_packagename));
                                final String appPackageName = _context.getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    _context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    _context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            } else if (jsonObject1.has("details")) {
                                try {
                                    JsonObject object = jsonObject1.get("details").getAsJsonObject();
                                    //JsonArray  jarray = jsonObject1.get("details").getAsJsonArray();
                                    //JsonObject object = jarray.get(0).getAsJsonObject();
                                    if (object.has(General.STATUS) && object.get(General.STATUS).getAsInt() == 21) {
                                        //PerformLogoutTask.logout(activity);
                                        HashMap<String, String> keyMap = KeyMaker_.getKey();
                                        RequestBody logoutBody = new FormBody.Builder()
                                                .add(General.USER_ID, Preferences.get(General.USER_ID))
                                                .add(General.KEY, keyMap.get(General.KEY))
                                                .add(General.TOKEN, keyMap.get(General.TOKEN))
                                                .build();
                                        String responseLogout = MakeCall.post(Preferences.get(General.DOMAIN) + "/" + Urls_.LOGOUT_URL, logoutBody, TAG, activity.getApplicationContext(), activity);
                                        if (responseLogout != null) {


                                            Preferences.save(General.IS_LOGIN, 0);
                                            Preferences.removeKey("regId");
                                            Preferences.removeKey(General.GROUP_NAME);
                                            Preferences.removeKey(General.GROUP_ID);
                                            Preferences.removeKey(General.OWNER_ID);
                                            Preferences.save(General.IS_COMETCHAT_LOGIN_SUCCESS, false);

                                       /* SugarRecord.deleteAll(OneOnOneMessage.class);
                                        SugarRecord.deleteAll(Groups.class);
                                        SugarRecord.deleteAll(Conversation.class);
                                        SugarRecord.deleteAll(GroupMessage.class);
                                        SugarRecord.deleteAll(models.Status.class);
                                        SugarRecord.deleteAll(Contact.class);
                                        SugarRecord.deleteAll(Bot.class);*/

                                            Intent loginIntent = new Intent(activity.getApplicationContext(), LoginActivity.class);
                                            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            activity.startActivity(loginIntent);
                                            activity.finish();
                                        }
                                    } else if (object.has(General.STATUS) && object.get(General.STATUS).getAsInt() == 20) {
                                        final String appPackageName = _context.getPackageName(); // getPackageName() from Context or Activity object
                                        try {
                                            _context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            _context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        }
                                    } else {
                                        return res;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                return res;
                            }
                        } else {
                            if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 13) {
                                return "13";
                            } else {
                                return null;
                            }
                        }
                    }
                } else {
                    return null;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static String post(String url, RequestBody formBody, String tag, Context _context) throws Exception {
        MyCallCounter myCall = new MyCallCounter(url, formBody, tag, _context);
        return myCall.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
    }

    @SuppressLint("StaticFieldLeak")
    private static final class MyCallCounter extends AsyncTask<Void, Void, String> {
        final String url;
        final String tag;
        final RequestBody formBody;
        final Context _context;

        MyCallCounter(String url, RequestBody formBody, String tag, Context _context) {
            this.url = url;
            this.formBody = formBody;
            this.tag = tag;
            this._context = _context;
        }

        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .tag(tag)
                    .build();
            Logger.debug(tag, url + "?" + bodyToString(request), _context);
            String s = url + "?" + bodyToString(request);

            try {
                Response response = client.newCall(request).execute();
                String res = response.body().string();
                if (res.trim().length() > 0) {
                    JSONObject jsonObject = new JSONObject(res);
                    Logger.debug(tag, res, _context);
                    if (response.isSuccessful()) {
                        if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 13) {
                            return "13";
                        } /*else if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 13) {
                            PerformLogoutTask.logout(activity);
                        } else if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 13) {
                            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + my_packagename));
                            final String appPackageName = _context.getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                _context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                _context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }*/ else {
                            return res;
                        }
                    } else {
                        if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 13) {
                            return "13";
                        } else {
                            return null;
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // make call to fetch oauth token
    private static String getToken(Context _context) {
        String access_token = null;
        Token_ token;
        OauthPreferences.initialize(_context);
        String i = OauthPreferences.get(Oauth.EXPIRES_AT);
        try {
            if (System.currentTimeMillis() >= Long.parseLong(OauthPreferences.get(Oauth.EXPIRES_AT))) {
                token = refreshToken.getRefreshToken(Preferences.get(Oauth.CLIENT_ID),
                        Preferences.get(Oauth.CLIENT_SECRET),
                        Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, ""),
                        _context);

                if (token.getStatus() == 1) {
                    access_token = token.getAccessToken();
                } else if (token.getStatus() == 12) {
                    getToken(_context);
                }/* else {
                 PerformLogoutTask.logout(activity);
            }*/
            } else {
                access_token = OauthPreferences.get(Oauth.ACCESS_TOKEN);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return access_token;
    }

    // make final url with encrypted parameters and agent name
    private static RequestBody finalBody(String mainBody, String token) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder
                .add("akujs", UrlEncoder_.encrypt(mainBody))
                .add("d", "a")
                .add(Oauth.ACCESS_TOKEN, token);
        return formBuilder.build();
    }

    // convert encrypted final body to normal string
    private static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    // make com.sagesurfer.network call body with user parameters with remaining common parameters
    // token, key, timezone and user id
    private static RequestBody makeBody(HashMap<String, String> map) {
        HashMap<String, String> keyMap = KeyMaker_.getKey();
        map.put(General.TOKEN, keyMap.get(General.TOKEN));
        map.put(General.KEY, keyMap.get(General.KEY));
        map.put(General.USER_ID, Preferences.get(General.USER_ID)); //logged in user id
        map.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        map.put(General.TIMEZONE_SERVER, Preferences.get(General.TIMEZONE_SERVER));
        map.put(General.DOMAIN_CODE, Preferences.get(General.DOMAIN_CODE));
        Set keys = map.keySet();
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Object object : keys) {
            String key = (String) object;
            String value = map.get(key);
            formBuilder.add(key, value);
        }
        return formBuilder.build();
    }
}