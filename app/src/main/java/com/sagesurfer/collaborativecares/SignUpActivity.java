package com.sagesurfer.collaborativecares;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.constant.Warnings;
import com.sagesurfer.interfaces.SignUpInterface;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.logger.Logger;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.secure.KeyMaker_;
import com.sagesurfer.secure._Base64;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.SignUpPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.RequestBody;


/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 16/03/2018
 *         Last Modified on
 */

public class SignUpActivity extends AppCompatActivity implements SignUpInterface {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    @BindView(R.id.textview_activitytoolbar_title)
    TextView textViewActivityToolbarTitle;
    @BindView(R.id.framelayout_signup)
    FrameLayout signupFrameLayout;
    @BindView(R.id.webview_signup)
    WebView webViewSignUp;

    private boolean isBack = true;
    private boolean isSign = false;
    private String error = Warnings.INTERNAL_ERROR_OCCURRED;

    Toolbar toolbar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_sign_up_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        SignUpPreferences.initialize(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        textViewActivityToolbarTitle.setText(getApplicationContext().getResources().getString(R.string.create_an_account));
        /*viewPager = (ViewPager) findViewById(R.id.sign_goal_pager);
        viewPager.addOnPageChangeListener(onPageChange);

        nextButton = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setText(this.getResources().getString(R.string.next));
        nextButton.setOnClickListener(this);*/

        Uri data = getIntent().getData();
        if (data != null) {
            List<String> params = data.getPathSegments();
            if (params.get(0).equalsIgnoreCase("signuptest.php")) {
                isSign = true;
                signupFrameLayout.setVisibility(View.VISIBLE);
                //viewPager.setVisibility(View.VISIBLE);
                SignUpPreferences.save(General.URL, data.getQuery(), TAG);
                SignUpPreferences.save(General.DOMAIN, "https://" + data.getHost() + General.INSATNCE_NAME, TAG);
                new GetDetails().execute();
                textViewActivityToolbarTitle.setText(getApplicationContext().getResources().getString(R.string.create_an_account));
                //nextButton.setVisibility(View.VISIBLE);
            } else if (params.get(0).equalsIgnoreCase("activateUser.php")) {
                isSign = false;
                signupFrameLayout.setVisibility(View.GONE);
                String url = "https://" + data.getHost();
                String query = data.getQuery();
                if (query == null || query.equalsIgnoreCase("null")) {
                    url = url + "/" + params.get(0);
                } else {
                    url = url + "/" + params.get(0) + "?" + data.getQuery();
                }
                url = url + "&d=a";

                textViewActivityToolbarTitle.setText(getApplicationContext().getResources().getString(R.string.sagesurfer));
                //nextButton.setVisibility(View.GONE);
                webViewSignUp.addJavascriptInterface(new WebAppInterface(getApplicationContext()), "Android");
                webViewSignUp.getSettings().setJavaScriptEnabled(true);
                webViewSignUp.getSettings().setLoadsImagesAutomatically(true);
                webViewSignUp.getSettings().setBuiltInZoomControls(true);
                webViewSignUp.loadUrl(url);
            } else {
                isSign = false;
                signupFrameLayout.setVisibility(View.GONE);
                String url = "https://" + data.getHost();
                String query = data.getQuery();
                if (query == null || query.equalsIgnoreCase("null")) {
                    url = url + "/" + params.get(0);
                } else {
                    url = url + "/" + params.get(0) + "?" + data.getQuery();
                }
                textViewActivityToolbarTitle.setText(getApplicationContext().getResources().getString(R.string.sagesurfer));
                //nextButton.setVisibility(View.GONE);
                webViewSignUp.addJavascriptInterface(new WebAppInterface(getApplicationContext()), "Android");
                webViewSignUp.getSettings().setJavaScriptEnabled(true);
                webViewSignUp.getSettings().setLoadsImagesAutomatically(true);
                webViewSignUp.getSettings().setBuiltInZoomControls(true);
                webViewSignUp.loadUrl(url);
            }
        }
    }

    /*private void toggleNext(int position) {
        if (position == 0) {
            nextButton.setText(this.getResources().getString(R.string.next));
        } else {
            nextButton.setText(this.getResources().getString(R.string.done));
        }
    }*/

    /*private final ViewPager.OnPageChangeListener onPageChange = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            isBack = position == 0;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };*/

    private void successDialog(String title, String sub_title) {
        final Dialog dialog = new Dialog(SignUpActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sign_up_success_layout);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView title_text = (TextView) dialog.findViewById(R.id.sign_success_text_sub_one);
        TextView sub_title_text = (TextView) dialog.findViewById(R.id.sign_success_text_sub_two);
        title_text.setText(title);
        sub_title_text.setText(sub_title);
        Button okButton = (Button) dialog.findViewById(R.id.sign_success_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                System.exit(0);
            }
        });
        dialog.show();
    }


    /*@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_activitytoolbar_post:
                int position = viewPager.getCurrentItem();
                if (position == 1) {
                    sendMoveBroadcast();
                    return;
                }
                sendSaveBroadcast();
                break;
        }
    }*/

    // get time zone of user's
    private static String getTimeZone() {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        return tz.getID();
    }

    // get user's device if
    @SuppressLint("HardwareIds")
    private String getDeviceId() {
        return Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    // make background network call to fetch decrypted details from  encrypt
    @SuppressLint("StaticFieldLeak")
    private class GetDetails extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            return getAccountDetails();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer != 1) {
                ShowToast.internalErrorOccurred(getApplicationContext());
            } else {
                /*SignUpPagerAdapter mPagerAdapter = new SignUpPagerAdapter(getApplicationContext(), getSupportFragmentManager());
                viewPager.setAdapter(mPagerAdapter);*/

                /*SignUpFragment signupFragment = new SignUpFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment fragment = getFragmentManager().findFragmentByTag(TAG);
                if (fragment != null) {
                    getFragmentManager().beginTransaction().remove(fragment).commit();
                }
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                ft.replace(R.id.framelayout_signup, signupFragment, TAG);
                ft.commit();*/

                /*SignUpFragment signupFragment = new SignUpFragment();

                Fragment f = getFragmentManager().findFragmentByTag(TAG);
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.slide_up,
                                R.animator.slide_down,
                                R.animator.slide_up,
                                R.animator.slide_down)
                        .add(R.id.framelayout_signup, signupFragment).addToBackStack(null).commit();*/

              Fragment fragment = new SignUpFragment();
                if (fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                    ft.replace(R.id.framelayout_signup, fragment, TAG);
                    ft.commit();
                }
            }
        }
    }

    // get decrypted user information from given encrypt
    private int getAccountDetails() {
        String encode = SignUpPreferences.get(General.URL);
        Logger.error(TAG, "encode: " + encode, getApplicationContext());
        HashMap<String, String> keyMap = KeyMaker_.getKey();
        RequestBody loginBody = new FormBody.Builder()
                .add(General.KEY, keyMap.get(General.KEY))
                .add(General.TOKEN, keyMap.get(General.TOKEN))
                .add(General.ACTION, "encode")
                .add("encode", encode)
                .build();
        try {
            String response = MakeCall.post(SignUpPreferences.get(General.DOMAIN)
                    + Urls_.MOBILE_SIGN_UP, loginBody, TAG, getApplicationContext(), this);
            if (response != null) {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("encode")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("encode");
                    JSONObject object = jsonArray.getJSONObject(0);
                    SignUpPreferences.save(General.EMAIL, object.getString(General.EMAIL), TAG);
                    SignUpPreferences.save(General.ROLE, object.getString(General.ROLE), TAG);
                    SignUpPreferences.save(General.ROLE_ID, object.getString(General.ROLE_ID), TAG);
                    SignUpPreferences.save(General.FIRST_NAME, object.getString("first_name"), TAG);
                    return 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 12;
    }

    // make network call in background create account
    @SuppressLint("StaticFieldLeak")
    private class Create extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            return createAccount();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == 1) {
                SignUpPreferences.clear(TAG);
                successDialog("Check you email for verification link", "We will see you soon!!!");
            } else if (integer == 2) {
                ShowToast.toast(error, getApplicationContext());
            } else {
                ShowToast.toast(error, getApplicationContext());
            }
        }
    }

    // make network call create account
    private int createAccount() {
        String fist_name = SignUpPreferences.get(General.FIRST_NAME);
        String last_name = SignUpPreferences.get(General.LAST_NAME);
        String date_of_birth = SignUpPreferences.get(General.START_DATE);
        String email = SignUpPreferences.get(General.EMAIL);
        String user_name = SignUpPreferences.get(General.USERNAME);
        String password = SignUpPreferences.get(General.PASSWORD);
        String question = SignUpPreferences.get(General.POLL_QUESTION);
        String answer = SignUpPreferences.get(General.POLL_ANSWER);
        String role_id = SignUpPreferences.get(General.ROLE_ID);
        String role = SignUpPreferences.get(General.ROLE);
        String encode = SignUpPreferences.get(General.URL);

        HashMap<String, String> keyMap = KeyMaker_.getKey();
        String info = DeviceInfo.get(SignUpActivity.this);

        RequestBody loginBody = new FormBody.Builder()
                .add(General.KEY, keyMap.get(General.KEY))
                .add(General.TOKEN, keyMap.get(General.TOKEN))
                .add(General.TIMEZONE, getTimeZone())
                .add(General.DEVICE, "a")
                .add(General.INFO, _Base64.encode(info))
                .add(General.UID, getDeviceId())
                .add(General.IP, DeviceInfo.getDeviceMAC(SignUpActivity.this))
                .add(General.ACTION, Actions_.SIGN_UP)
                .add(General.FIRST_NAME, fist_name)
                .add(General.LAST_NAME, last_name)
                .add(General.EMAIL, email)
                .add(General.USERNAME, user_name)
                .add(General.PASSWORD, password)
                .add(General.ROLE_ID, role_id)
                .add(General.ROLE, role)
                .add("question", question)
                .add("answer", answer)
                .add("date_of_birth", date_of_birth)
                .add("encode", encode)
                .build();
        try {
            String response = MakeCall.post(SignUpPreferences.get(General.DOMAIN)
                    + Urls_.MOBILE_SIGN_UP, loginBody, TAG, getApplicationContext(), this);
            if (response != null) {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("sign_up")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("sign_up");
                    JSONObject object = jsonArray.getJSONObject(0);
                    if (object.has(General.STATUS)) {
                        int result = object.getInt(General.STATUS);
                        if (result != 1 && object.has(General.ERROR)) {
                            error = object.getString(General.ERROR);
                        }
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 12;
    }

    /*private void sendMoveBroadcast() {
        int current_position = viewPager.getCurrentItem();
        if (current_position == 1) {
            Intent intent = new Intent();
            intent.setAction(General.ACTION);
            intent.putExtra(General.ACTION, "1");
            sendBroadcast(intent);
        }
    }

    private void sendSaveBroadcast() {
        int current_position = viewPager.getCurrentItem();
        if (current_position == 0) {
            Intent intent = new Intent();
            intent.setAction(General.MESSAGE);
            intent.putExtra(General.MESSAGE, "1");
            sendBroadcast(intent);
        }
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!isSign) {
            finish();
            System.exit(0);
        } else {
            this.overridePendingTransition(0,0);
            finish();
        }
        /*int current_position = viewPager.getCurrentItem();

        if (current_position == 1) {
            viewPager.setCurrentItem(0, true);
            return;
        }
        if (current_position == 0 && isBack) {
            this.overridePendingTransition(0,0);
            finish();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        /*if (viewPager != null)
            viewPager.getCurrentItem();*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putString(General.POSITION, "" + viewPager.getCurrentItem());
    }

    @Override
    public void doSignUp() {
        new Create().execute();
    }

    /*@Override
    public void moveNext() {
        viewPager.setCurrentItem(1, true);
        toggleNext(1);
    }*/

    // close application from javascript
    public class WebAppInterface {
        final Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void startNewActivity() {
            //Log.e(TAG,"startNewActivity");
            successDialog("Your Sagesurfer account is activated", "We will see you soon!!!");
        }
    }
}
