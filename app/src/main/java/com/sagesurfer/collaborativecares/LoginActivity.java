package com.sagesurfer.collaborativecares;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputFilter;
import android.text.Spanned;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.utils.AppLog;
import com.airbnb.lottie.LottieAnimationView;


import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.firebase.MessagingService;
/*
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
*/
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.modules.selfcare.ListItem;
import com.sagesurfer.callbacks.AuthorizationCallbacks;
import com.sagesurfer.callbacks.TokenCallbacks;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.constant.Oauth;
import com.sagesurfer.constant.Quote;
import com.sagesurfer.constant.Warnings;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.directory.DirectoryList;
import com.sagesurfer.directory.MakeDirectory;
import com.sagesurfer.download.ImageDownloader;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.models.RegisterUser;
import com.sagesurfer.models.Server_;
import com.sagesurfer.models.UserInfo_;
import com.sagesurfer.network.AppConfig;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.oauth.Authorize;
import com.sagesurfer.oauth.Token;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.Login_;
import com.sagesurfer.parser.Servers_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformLoginTask;
import com.sagesurfer.tasks.PerformServerTask;
import com.sagesurfer.validator.LoginValidator;
import com.storage.preferences.Preferences;
import com.storage.preferences.StorageHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import screen.messagelist.NetworkCall_;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AuthorizationCallbacks, TokenCallbacks {
    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.edittext_login_user_name)
    EditText editTextUserName;
    @BindView(R.id.edittext_login_user_password)
    EditText editTextUserPassword;
    @BindView(R.id.edittext_login_server_code)
    EditText editTextServerCode;
    @BindView(R.id.button_login)
    Button buttonLogin;
    @BindView(R.id.textview_login_forgot_password)
    TextView textViewForgotPassword;
    @BindView(R.id.saveLoginCheckBox)
    CheckBox saveLoginDetailsCheckBox;

    private ArrayList<Server_> serverList;
    private String start_time = "0";
    private UserInfo_ userInfo;
    private ProgressDialog progressDialog;
    private LottieAnimationView animationView;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private LinearLayout newUserRegistration;
    private ArrayList<RegisterUser> registerUserArrayList;
    private ArrayAdapter<String> reasonAdapter;
    private String selectedReason = "";
    private String msg, is_sagesurfer;
    private AlertDialog registrarAlert;
    private boolean reasonBoxOpen = false;
    private Boolean yesState = false, noState = false;
    private AlertDialog alertDialog;
    private String blockCharacterSet = ".?@;:'#~!£$%^&*()_+-=¬`,./<>";
    private String errorMsg;
    String firebaseToken;
    private PopupWindow popupWindow = new PopupWindow();

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        Preferences.initialize(getApplicationContext());

        progressDialog = new ProgressDialog(LoginActivity.this);
        serverList = new ArrayList<>();

        textViewForgotPassword.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);

        if (saveLogin) {
            editTextUserName.setText(loginPreferences.getString("username", ""));
            editTextServerCode.setText(loginPreferences.getString("server_code", ""));
            saveLoginDetailsCheckBox.setChecked(false);
        }

        //Setting vecotr images to TextInputEditText
        VectorDrawableCompat userNameDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_user_name, editTextUserName.getContext().getTheme());
        editTextUserName.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, userNameDrawable, null);

        VectorDrawableCompat passwordDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_user_password, editTextUserPassword.getContext().getTheme());
        editTextUserPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, passwordDrawable, null);

        VectorDrawableCompat serverCodeDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_server_code, editTextServerCode.getContext().getTheme());
        editTextServerCode.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, serverCodeDrawable, null);

        //setting OnFocusChangeListener
        editTextUserName.setOnFocusChangeListener(onFocusChange);
        editTextUserPassword.setOnFocusChangeListener(onFocusChange);
        editTextServerCode.setOnFocusChangeListener(onFocusChange);

        toggleLogin(false);

        animationView = findViewById(R.id.animation_view);
        animationView.setAnimation("genericLoader.json");
        animationView.loop(true);

        newUserRegistration = findViewById(R.id.register_layout);
        newUserRegistration.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.button_login:
                final String user_name = editTextUserName.getText().toString();
                final String password = editTextUserPassword.getText().toString();
                String code = editTextServerCode.getText().toString();

                if (saveLoginDetailsCheckBox.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("username", user_name);
                    loginPrefsEditor.putString("server_code", code);
                    loginPrefsEditor.commit();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }

                /*if (isValid(user_name, password, code)) {
                    animationView.setVisibility(View.VISIBLE);
                    animationView.playAnimation();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doLogin(v,user_name, password);
                        }
                    }, 2000);
                }*/


                if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                    if (code.equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                            code.equalsIgnoreCase(getResources().getString(R.string.sage031))) {
                        if (isValid(user_name, password, code)) {
                            animationView.setVisibility(View.VISIBLE);
                            animationView.playAnimation();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    doLogin(v, user_name, password);
                                }
                            }, 2000);
                        }
                    } else {
                        Toast.makeText(this, "Code Invalid", Toast.LENGTH_SHORT).show();
                    }

                } else {
//                    if(BuildConfig.FLAVOR.equalsIgnoreCase("senjam")){
                    if (code.equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                            code.equalsIgnoreCase(getResources().getString(R.string.sage031))) {
                        Toast.makeText(this, "Code Invalid", Toast.LENGTH_SHORT).show();
                    } else {
                        if (isValid(user_name, password, code)) {
                            animationView.setVisibility(View.VISIBLE);
                            animationView.playAnimation();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    doLogin(v, user_name, password);
                                }
                            }, 2000);
                        }
                    }
                }

                break;

            case R.id.textview_login_forgot_password:
                if (serverList == null || serverList.size() <= 0) {
                    ShowSnack.viewWarning(v, "Server_ Unavailable", getApplicationContext());
                } else {
                    forgotIntent();
                }
                break;

            case R.id.register_layout:
                registerDialogActivity();
                break;
        }
    }

    private void registerDialogActivity() {
        View view = getLayoutInflater().inflate(R.layout.dialog_sign_up_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        registrarAlert = builder.create();
        registrarAlert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView closeImg = view.findViewById(R.id.close_icon);

        final EditText fullName = view.findViewById(R.id.edittext_signup_name);
        final EditText emailAddress = view.findViewById(R.id.edittext_signup_email);
        final EditText companyName = view.findViewById(R.id.edittext_signup_company_name);
        final EditText signUpReason = view.findViewById(R.id.edittext_signupreason);

        final RadioGroup yesNoRadioGroup = view.findViewById(R.id.radio_group);
        final RadioButton yesRadioBtn = view.findViewById(R.id.radiobutton_yes);
        final RadioButton noRadioBtn = view.findViewById(R.id.radiobutton_no);

        final Spinner reasonSpinner = view.findViewById(R.id.yes_spinner_reason);
        final Spinner noReasonSpinner = view.findViewById(R.id.no_spinner_reason);

        Button submitBtn = view.findViewById(R.id.button_signup);

        yesNoRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radiobutton_yes) {
                    reasonSpinner.setVisibility(View.VISIBLE);
                    noReasonSpinner.setVisibility(View.GONE);
                } else if (checkedId == R.id.radiobutton_no) {
                    reasonSpinner.setVisibility(View.GONE);
                    noReasonSpinner.setVisibility(View.VISIBLE);
                }
            }
        });

        ArrayList<String> reason = new ArrayList<>();
        reason.add("Select a reason for access");
        reason.add("I never received the invitation");
        reason.add("I don't know,how to invite myself");
        reason.add("I want to evaluate the platform");
        reason.add("I want to take part in a research project");
        reason.add("Other");

        reasonAdapter = new ArrayAdapter<String>(this, R.layout.drop_down_selected_text_item_layout, reason);
        reasonAdapter.setDropDownViewResource(R.layout.drop_down_text_item_layout);
        reasonSpinner.setAdapter(reasonAdapter);

        reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedReason = reasonSpinner.getSelectedItem().toString().trim();
                if (selectedReason.equalsIgnoreCase("Other")) {
                    signUpReason.setVisibility(View.VISIBLE);
                    reasonBoxOpen = true;
                } else {
                    signUpReason.setVisibility(View.GONE);
                    reasonBoxOpen = false;
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> noreason = new ArrayList<>();
        noreason.add("Select a reason for access");
        noreason.add("I want to evaluate the platform");
        noreason.add("I want to take part in a research project");
        noreason.add("Other");

        reasonAdapter = new ArrayAdapter<String>(this, R.layout.drop_down_selected_text_item_layout, noreason);
        reasonAdapter.setDropDownViewResource(R.layout.drop_down_text_item_layout);
        noReasonSpinner.setAdapter(reasonAdapter);

        noReasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedReason = noReasonSpinner.getSelectedItem().toString();
                if (selectedReason.equalsIgnoreCase("Other")) {
                    signUpReason.setVisibility(View.VISIBLE);
                    reasonBoxOpen = true;
                } else {
                    signUpReason.setVisibility(View.GONE);
                    reasonBoxOpen = false;
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarAlert.dismiss();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidation(fullName.getText().toString(), emailAddress.getText().toString(),
                        companyName.getText().toString(), signUpReason.getText().toString())) {

                    yesState = yesRadioBtn.isChecked();
                    noState = noRadioBtn.isChecked();

                    if (yesState) {
                        is_sagesurfer = "1";
                    } else if (noState) {
                        is_sagesurfer = "0";
                    }

                    userRegistration(fullName.getText().toString().trim(), emailAddress.getText().toString().trim(),
                            companyName.getText().toString().trim(), signUpReason.getText().toString().trim());
                }
            }
        });

        fullName.setFilters(new InputFilter[]{filter});

        registrarAlert.show();
    }

    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    private void userRegistration(final String fullname, final String email, final String company_name, final String reason) {

        String url = "https://design.sagesurfer.com/phase3/" + Urls_.MOBILE_USER_REGISTRATION;

        RequestBody postBody = null;

        if (selectedReason.equalsIgnoreCase("Other")) {
            postBody = new FormBody.Builder()
                    .add(General.ACTION, Actions_.REGISTER_USER)
                    .add(General.FULL_NAME, fullname)
                    .add(General.EMAIL, email)
                    .add(General.COMPANY, company_name)
                    .add("is_sagesurfer", is_sagesurfer)
                    .add("reason", selectedReason)
                    .add("other", reason)
                    .add("k", "cC5kZiFQQmt6OA==")
                    .add("t", "72662546730e46102ba8")
                    .add("tz", DeviceInfo.getTimeZone())
                    .build();
        } else {
            postBody = new FormBody.Builder()
                    .add(General.ACTION, Actions_.REGISTER_USER)
                    .add(General.FULL_NAME, fullname)
                    .add(General.EMAIL, email)
                    .add(General.COMPANY, company_name)
                    .add("is_sagesurfer", is_sagesurfer)
                    .add("reason", selectedReason)
                    .add("other", "")
                    .add("k", "cC5kZiFQQmt6OA==")
                    .add("t", "72662546730e46102ba8")
                    .add("tz", DeviceInfo.getTimeZone())
                    .build();
        }

        try {
            postRequest(url, postBody);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    registrarAlert.dismiss();
                }
            }, 1000);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void postRequest(String postUrl, RequestBody postBody) throws IOException {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();

                JsonArray jsonArray = GetJson_.getArray(responseData, Actions_.REGISTER_USER);
                if (jsonArray != null) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<RegisterUser>>() {
                    }.getType();

                    registerUserArrayList = gson.fromJson(GetJson_.getArray(responseData, Actions_.REGISTER_USER).toString(), listType);

                    if (registerUserArrayList.size() > 0) {
                        msg = registerUserArrayList.get(0).getMsg();
                    }
                }

            }
        });

        Toast toast = Toast.makeText(LoginActivity.this, "Mail sent", Toast.LENGTH_LONG);
        toast.show();
    }

    //Checking if all fields are valid or not
    private boolean isValidation(String full_name, String email, String company_name, String reason) {
        if (full_name.trim().length() <= 0 || !LoginValidator.isUsername(full_name)) {
            ShowToast.toast("Full Name : Min 5 char required", this);
            return false;
        }
        if (email.trim().length() <= 0 || !LoginValidator.isEmail(email)) {
            ShowToast.toast("Please enter valid email id", this);
            return false;
        }
        if (company_name.trim().length() <= 0 || !LoginValidator.isUsername(company_name)) {
            ShowToast.toast("Company Name : Min 5 char required", this);
            return false;
        }

        if (selectedReason.equalsIgnoreCase("") || selectedReason.equalsIgnoreCase("Select a reason for access")) {
            ShowToast.toast("Please select reason for access", this);
            return false;
        }

        if (reasonBoxOpen) {
            if (reason == null || reason.trim().length() <= 0) {
                ShowToast.toast("Please provide other reason for access", this);
                return false;
            }
            if (reason.length() < 6 || reason.length() > 140) {
                ShowToast.toast("Min 6 Char Required\nMax 140 Char allowed", this);
                return false;
            }

        }

        return true;
    }

    private final View.OnFocusChangeListener onFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.edittext_login_user_name:
                    /*editTextUserName.getBackground().setAlpha(180);
                    editTextUserPassword.getBackground().setAlpha(80);
                    editTextServerCode.getBackground().setAlpha(80);*/
                    break;

                case R.id.edittext_login_user_password:
                    /*editTextUserName.getBackground().setAlpha(80);
                    editTextUserPassword.getBackground().setAlpha(180);
                    editTextServerCode.getBackground().setAlpha(80);*/
                    break;

                case R.id.edittext_login_server_code:
                   /* editTextUserName.getBackground().setAlpha(80);
                    editTextUserPassword.getBackground().setAlpha(80);
                    editTextServerCode.getBackground().setAlpha(180);*/
                    break;
            }
        }
    };

    //Enabling-Disabling login button, if server connection made successfully
    private void toggleLogin(boolean isAvailable) {
        if (isAvailable) {
            buttonLogin.setEnabled(true);
            buttonLogin.setBackgroundResource(R.drawable.blue_rounded);
        } else {
            buttonLogin.setEnabled(false);
            buttonLogin.setBackgroundResource(R.drawable.disable_color_rounded_rectangle);
        }
    }

    //Checking if all fields are valid or not
    private boolean isValid(String user_name, String password, String code) {
        if (user_name.trim().length() <= 0 || !LoginValidator.isUsername(user_name)) {
            editTextUserName.setError(this.getResources().getString(R.string.invalid_input));
            return false;
        }
        if (password.trim().length() <= 0 || !LoginValidator.isPassword(password)) {
            editTextUserPassword.setError(this.getResources().getString(R.string.invalid_input));
            return false;
        }
        if (code.trim().length() <= 0 || !LoginValidator.isCode(code)) {
            editTextServerCode.setError(this.getResources().getString(R.string.invalid_input));
            return false;
        }
        if (code.equalsIgnoreCase("sage018") || code.equalsIgnoreCase("sage019")) {
            editTextServerCode.setError(this.getResources().getString(R.string.invalid_server_code));
            return false;
        }
        if (!checkCode(code)) {
            editTextServerCode.setError(this.getResources().getString(R.string.invalid_server_code));
            return false;
        }
        return true;
    }

    //Performing login- logging in with the username, password, if user login success than check for oAuth
    private void doLogin(View v, String user_name, String password) {
        int status = 12;
        int roleId = 0;
        String response = null;
        AppLog.i(TAG, "doLogin: entered ------------------->");
        try {
            response = new PerformLoginTask(user_name, password, start_time, this).execute().get();
            AppLog.e("doLogin : ", "Login response " + response);

            /*JSONObject jsonObject=new JSONObject(response);
            if(jsonObject.has("drawer")){
                Toast.makeText(this, "drawer object found", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show();
            }*/
            if (response != null) {
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
                if (jsonObject.has("drawer")) {
                    if (jsonObject.has("details")) {
                        JsonObject object = jsonObject.get("details").getAsJsonObject();
                        if (object.has(General.STATUS)) {
                            status = object.get(General.STATUS).getAsInt();
                            roleId = object.get(General.ROLE_ID).getAsInt();
                            if (status == 5) {

                                if (object.get("error") != null) {
                                    errorMsg = object.get("error").getAsString();
                                }
                            }

                            if (object.has(General.LANDING_QUESTIONS)) {
                                int landing_questions = object.get(General.LANDING_QUESTIONS).getAsInt();
                                Preferences.save(General.IS_LANDING_QUESTION_FILLED, true);
                                if (landing_questions == 0) { //landing questions not filled
                                    Preferences.save(General.IS_FORM_SYNC_LANDING_QUESTION, false);
                                    Preferences.save(General.IS_LANDING_QUESTION_FILLED, false);
                                }
                            }

                            if (object.has(General.SHOW_BEHAVIORAL)) {
                                int show_behavioural = object.get(General.SHOW_BEHAVIORAL).getAsInt();
                                Preferences.save(General.SHOW_BEHAVIOURAL_FILLED, false);
                                if (show_behavioural == 0) { //show behavioural not filled
                                    Preferences.save(General.SHOW_BEHAVIOURAL_FILLED, true);
                                }
                            }

                            if (object.has(General.IS_RESET_PASSWORD)) {
                                int is_reset_password = object.get(General.IS_RESET_PASSWORD).getAsInt();
                                Preferences.save(General.IS_RESET_PASSWORD_FILLED, false);
                                if (is_reset_password == 0) {
                                    Preferences.save(General.IS_RESET_PASSWORD_FILLED, true);
                                }
                            }

                            if (object.has(General.SHOW_GOAL_POPUP)) {
                                int show_goal = object.get(General.SHOW_GOAL_POP).getAsInt();
                                Preferences.save(General.SHOW_GOAL_FILLED, false);
                                if (show_goal == 0) { //show goal popup
                                    Preferences.save(General.SHOW_GOAL_FILLED, true);
                                }
                            }

                            if (object.has(General.SHOW_APPOINTMENT_POPUP)) {
                                int show_appointment_popup = object.get(General.SHOW_APPOINTMENT_POPUP).getAsInt();
                                Preferences.save(General.SHOW_APPOINTMENT_FILLED, false);
                                Preferences.save(General.APP_ID, object.get(General.APP_ID).getAsInt());
                                Preferences.save(General.IS_APPOINTMENT_ADD, object.get(General.IS_APPOINTMENT_ADD).getAsInt());
                                if (show_appointment_popup == 0) { //show show_appointment_popup
                                    Preferences.save(General.SHOW_APPOINTMENT_FILLED, true);
                                }
                            }

                            Preferences.save(General.CLINICIAN_ID, 0);
                            Preferences.save(General.CLINICIAN_USER_NAME, "");
                            if (object.has(General.CLINICIAN_USER_NAME)) {
                                String strClinicianUserName = object.get(General.CLINICIAN_USER_NAME).getAsString();
                                int nClinicianID = object.get(General.CLINICIAN_ID).getAsInt();
                                Preferences.save(General.CLINICIAN_ID, nClinicianID);
                                Preferences.save(General.CLINICIAN_USER_NAME, strClinicianUserName);
                            }

                            if (object.has(General.IS_SHOW_SOWS)) {
                                int show_daily_dosing = object.get(General.IS_SHOW_SOWS).getAsInt();
                                Preferences.save(General.SHOW_SOWS_FILLED, false);
                                if (show_daily_dosing == 0) { //show daily dosing10 Question Popup
                                    Preferences.save(General.SHOW_SOWS_FILLED, true);
                                }
                            }

                            if (object.has(General.SHOW_DAILY_DOSAGE)) {
                                int show_daily_dosing = object.get(General.SHOW_DAILY_DOSAGE).getAsInt();
                                String sejman_goal_id = String.valueOf(object.get(General.SENJAM_GOAL_ID).getAsInt());
                                Preferences.save(General.SHOW_DAILY_DOSAGE_FILLED, false);
                                if (show_daily_dosing == 0) { //show daily dosing compliance dialog
                                    Preferences.save(General.SHOW_DAILY_DOSAGE_FILLED, true);
                                    Preferences.save(General.SENJAM_GOAL_ID, sejman_goal_id);
                                }
                            }


                            if (object.has(General.SHOW_PRIVACY_POPUP)) {
                                int show_daily_dosing = object.get(General.SHOW_PRIVACY_POPUP).getAsInt();
                                String privacyUrl = object.get(General.PRIVACY_URL).getAsString();
                                Preferences.save(General.SHOW_PRIVACY_POPUP_FILLED, false);
                                if (show_daily_dosing == 0) { //show Privacy Popup
                                    Preferences.save(General.SHOW_PRIVACY_POPUP_FILLED, true);
                                    Preferences.save(General.PRIVACY_URL, privacyUrl);
                                }
                            }

                            if (object.has(General.SHOW_ONE_TIME_SURVEY)) {
                                int show_daily_dosing = object.get(General.SHOW_ONE_TIME_SURVEY).getAsInt();
                                Preferences.save(General.SHOW_ONE_TIME_SURVEY_FILLED, false);
                                if (show_daily_dosing == 0) { //show One Time Survey Popup
                                    Preferences.save(General.SHOW_ONE_TIME_SURVEY_FILLED, true);
                                }
                            }

                            if (object.has(General.SHOW_DAILY_SURVEY)) {
                                int show_daily_survey = object.get(General.SHOW_DAILY_SURVEY).getAsInt();
                                Preferences.save(General.SHOW_DAILY_SURVEY_FILLED, false);
                                if (show_daily_survey == 0) { //show Daily Survey Popup
                                    Preferences.save(General.SHOW_DAILY_SURVEY_FILLED, true);
                                }
                            }


//                            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026))
//                                    || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027)) &&
//                                    (Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_ComplianceAdministrator) || Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_SystemAdministrator)) || Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_ClinicalApplicationsAdministrator)){
//
//                                popUpForManageRelationShip(v);
//                            }

                        }


                        if (CheckRole.isYouth(roleId)) {
                            if (jsonObject.has("quote")) {
                                JsonObject quoteJsonObject = jsonObject.get("quote").getAsJsonObject();

                                if (quoteJsonObject.has(General.STATUS)) {
                                    int statusQuote = quoteJsonObject.get(General.STATUS).getAsInt();
                                    if (statusQuote == 1) {
                                        Preferences.save(Quote.QUOTE_ID, quoteJsonObject.get(General.QUOTE_ID).getAsInt());
                                        String quoteName = quoteJsonObject.get(General.QUOTE_NAME).getAsString();
                                        Preferences.save(Quote.QUOTE_NAME, quoteName);
                                        Preferences.save(Quote.QUOTE_AUTHER_NAME, quoteJsonObject.get(General.AUTHER_NAME).getAsString());
                                        Preferences.save(Quote.QUOTE_IMAGE_PATH, quoteJsonObject.get(General.IMAGE_PATH).getAsString());
                                    } else {
                                        Preferences.save(Quote.QUOTE_NAME, "");
                                        Preferences.save(Quote.QUOTE_AUTHER_NAME, "");

                                    }
                                }
                            }
                        }

                    } else {
                        status = 11;
                    }
                } else if (jsonObject.has("details")) {
                    JsonObject object = jsonObject.get("details").getAsJsonObject();
                    if (status == 5) {
                        if (object.get("error") != null) {
                            errorMsg = object.get("error").getAsString();
                        }
                    }
                    if (object.has(General.STATUS)) {
                        status = object.get(General.STATUS).getAsInt();

                        if (object.has(General.LANDING_QUESTIONS)) {
                            int landing_questions = object.get(General.LANDING_QUESTIONS).getAsInt();
                            Preferences.save(General.IS_LANDING_QUESTION_FILLED, true);
                            if (landing_questions == 0) { //landing questions not filled
                                Preferences.save(General.IS_FORM_SYNC_LANDING_QUESTION, false);
                                Preferences.save(General.IS_LANDING_QUESTION_FILLED, false);
                            }
                        }

                        if (object.has(General.SHOW_BEHAVIORAL)) {
                            int show_behavioural = object.get(General.SHOW_BEHAVIORAL).getAsInt();
                            Preferences.save(General.SHOW_BEHAVIOURAL_FILLED, false);
                            if (show_behavioural == 0) { //show behavioural not filled
                                Preferences.save(General.SHOW_BEHAVIOURAL_FILLED, true);
                            }
                        }

                        if (object.has(General.IS_RESET_PASSWORD)) {
                            int is_reset_password = object.get(General.IS_RESET_PASSWORD).getAsInt();
                            Preferences.save(General.IS_RESET_PASSWORD_FILLED, false);
                            if (is_reset_password == 0) {
                                Preferences.save(General.IS_RESET_PASSWORD_FILLED, true);
                            }
                        }

                        if (object.has(General.SHOW_GOAL_POPUP)) {
                            int show_goal = object.get(General.SHOW_GOAL_POP).getAsInt();
                            Preferences.save(General.SHOW_GOAL_FILLED, false);
                            if (show_goal == 0) { //show goal popup
                                Preferences.save(General.SHOW_GOAL_FILLED, true);
                            }
                        }

                        if (object.has(General.IS_SHOW_SOWS)) {
                            int show_daily_dosing = object.get(General.IS_SHOW_SOWS).getAsInt();
                            Preferences.save(General.SHOW_SOWS_FILLED, false);
                            if (show_daily_dosing == 0) { //show daily dosing 10 Question PopUp
                                Preferences.save(General.SHOW_SOWS_FILLED, true);
                            }
                        }

                        if (object.has(General.SHOW_APPOINTMENT_POPUP)) {
                            int show_appointment_popup = object.get(General.SHOW_APPOINTMENT_POPUP).getAsInt();
                            Preferences.save(General.SHOW_APPOINTMENT_FILLED, false);
                            Preferences.save(General.APP_ID, object.get(General.APP_ID).getAsInt());
                            Preferences.save(General.IS_APPOINTMENT_ADD, object.get(General.IS_APPOINTMENT_ADD).getAsInt());
                            if (show_appointment_popup == 0) { //show show_appointment_popup
                                Preferences.save(General.SHOW_APPOINTMENT_FILLED, true);
                            }
                        }

                        if (object.has(General.SHOW_DAILY_DOSAGE)) {
                            int show_daily_dosing = object.get(General.SHOW_DAILY_DOSAGE).getAsInt();
                            String sejman_goal_id = String.valueOf(object.get(General.SENJAM_GOAL_ID).getAsInt());
                            Preferences.save(General.SHOW_DAILY_DOSAGE_FILLED, false);
                            if (show_daily_dosing == 0) { //show daily dosing compliance
                                Preferences.save(General.SHOW_DAILY_DOSAGE_FILLED, true);
                                Preferences.save(General.SENJAM_GOAL_ID, sejman_goal_id);
                            }
                        }


                        if (object.has(General.SHOW_PRIVACY_POPUP)) {
                            int show_daily_dosing = object.get(General.SHOW_PRIVACY_POPUP).getAsInt();
                            String privacyUrl = object.get(General.PRIVACY_URL).getAsString();
                            Preferences.save(General.SHOW_PRIVACY_POPUP_FILLED, false);
                            if (show_daily_dosing == 0) { //show Privacy Popup
                                Preferences.save(General.SHOW_PRIVACY_POPUP_FILLED, true);
                                Preferences.save(General.PRIVACY_URL, privacyUrl);
                            }
                        }

                        if (object.has(General.SHOW_ONE_TIME_SURVEY)) {
                            int show_daily_dosing = object.get(General.SHOW_ONE_TIME_SURVEY).getAsInt();
                            Preferences.save(General.SHOW_ONE_TIME_SURVEY_FILLED, false);
                            if (show_daily_dosing == 0) { //show One Time Survey Popup
                                Preferences.save(General.SHOW_ONE_TIME_SURVEY_FILLED, true);
                            }
                        }

                        if (object.has(General.SHOW_DAILY_SURVEY)) {
                            int show_daily_survey = object.get(General.SHOW_DAILY_SURVEY).getAsInt();
                            Preferences.save(General.SHOW_DAILY_SURVEY_FILLED, false);
                            if (show_daily_survey == 0) { //show Daily Survey Popup
                                Preferences.save(General.SHOW_DAILY_SURVEY_FILLED, true);
                            }
                        }

                    }

                } else {
                    status = 11;
                }
            }
        } catch (InterruptedException | ExecutionException | IllegalStateException e) {
            e.printStackTrace();
        }
        if (status == 1) {
            userInfo = Login_.userInfoParser(response, "details", getApplicationContext());
            Preferences.save(Oauth.CLIENT_ID, userInfo.getClientId());
            Preferences.save(Oauth.CLIENT_SECRET, userInfo.getClientSecret());

            if (userInfo != null && userInfo.getUserId() != null) {
                Authorize authorize = new Authorize(this);
                authorize.getAuthorized(userInfo.getClientId(), userInfo.getClientSecret(), Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, ""), this);
            }
            return;
        }
        showResponses(status);
    }

    public void popUpForManageRelationShip(View v) {
        try {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View customView = inflater.inflate(R.layout.popup_manage_relationship_layout, null);
            popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            popupWindow.setOutsideTouchable(false);
//            popupWindow.setBackgroundDrawable(null);
            popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            popupWindow.isShowing();

            TextView mTxtSelectStaff, mTxtSelectSupervisor;
            TextView mBtnCancel, mBtnAssignment;
            LinearLayout mLinearBtnCancel, mLinearBtnAssignment, mLinearTxtSupervisor;

            mTxtSelectSupervisor = customView.findViewById(R.id.txt_select_supervisor);
            mLinearTxtSupervisor = customView.findViewById(R.id.linear_txt_title_supervisor);
            mTxtSelectStaff = customView.findViewById(R.id.txt_select_staff);
            mBtnCancel = customView.findViewById(R.id.btn_cancel);
            mLinearBtnCancel = customView.findViewById(R.id.linear_btn_cancel);
            mLinearBtnAssignment = customView.findViewById(R.id.linear_btn_assignment);
            mBtnAssignment = customView.findViewById(R.id.btn_assignment);

//            getSupervisorListData();

            // mTxtSelectStaff.setText(strName);

            mLinearTxtSupervisor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Intent intent = new Intent(LoginActivity.this, TeamPeerSupervisorListActivity.class);
//                    intent.putExtra("invitationsArrayList", invitationsArrayList);
//                    intent.putExtra("invitation", selectedSupervisor);
//
//                    startActivityForResult(intent, SUPERVISOR_REQUEST_CODE);

                }
            });

            mBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

            mBtnAssignment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    showAddProgressNoteDialog(getResources().getString(R.string.are_you_sure_you_want_to_add_this_assignment));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Showing login response in snackbar
    private void showResponses(int status) {
        animationView.setVisibility(View.GONE);
        String message = null;
        if (status == 1) {
            message = "Login was successful";
        } else if (status == 3) {
            status = 2;
            message = getApplicationContext().getResources().getString(R.string.username_password_not_match);
        } else if (status == 5 && (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage015")
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage021"))) {
            if (errorMsg != null) {
                showDialog(errorMsg);
            }
        } else if (status == 20 || status == 21) {
            message = getApplicationContext().getResources().getString(R.string.invalid_login);
        } else {
            status = 2;
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, buttonLogin, getApplicationContext());
    }

    private void showDialog(String errorMsg) {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.error_popup, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(dialogView);

        alertDialog = builder.create();

        final TextView okBtn = dialogView.findViewById(R.id.ok_btn);
        final TextView error = dialogView.findViewById(R.id.message_txt);
        error.setText(errorMsg);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    //Checking code validity and storing domain details in preferences
    private boolean checkCode(String code) {
        for (Server_ server : serverList) {
            if (server.getKey().equalsIgnoreCase(code)) {
                AppLog.e(TAG, "DomainCodeBeforeSaving: " + code);
                AppLog.e(TAG, "DomainURLMain: " + server.getDomainUrl());
                Preferences.save(General.DOMAIN, server.getDomainUrl());//server.getDomainUrl());
                Preferences.save(General.DOMAIN_CODE, code);
                SharedPreferences domainUrlPref = getSharedPreferences("domainUrlPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = domainUrlPref.edit();
                editor.putString(General.DOMAIN, "" + server.getDomainUrl());
                editor.apply();
//                screen.messagelist.Preferences.save(General.DOMAIN, server.getDomainUrl());
                Preferences.save(General.CHAT_URL, server.getChatUrl());
                AppLog.e(TAG, "DomainCodeFromPreferences: " + Preferences.get(General.DOMAIN_CODE));
                return true;
            }
        }
        return false;
    }

    //Fetching server list from web for checking if valid server code entered or not on login

    private void getServers() {
        String response = null;
        try {
            response = new PerformServerTask(this).execute(getApplicationContext()).get();
//            Log.e(TAG, "getServers: "+response.toString() );
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("instances");
            for (int i = 0; i < jsonArray.length(); i++) {
                AppLog.i(TAG, "getServers: Instance " + jsonArray.get(i));
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
            AppLog.e(TAG, "getServers: catch" + e.getMessage());
        }
        if (response != null) {

            serverList = Servers_.parse(response, getApplicationContext(), TAG);
            if (serverList.size() > 0) {
                toggleLogin(true);
            } else {
                ShowSnack.buttonWarning(buttonLogin, this.getResources().getString(R.string.servers_unavailable), getApplicationContext());
            }
        } else {
            ShowSnack.buttonWarning(buttonLogin, Warnings.AUTHENTICATION_FAILED, getApplicationContext());
        }
    }

    //onSuccessful oAuth token generation, store user details and move to HomeRecentUpdates_ page
    private void moveNext() {
        SharedPreferences loginSessionCheck = getSharedPreferences("loginSessionCheck", MODE_PRIVATE);
        SharedPreferences.Editor editor = loginSessionCheck.edit();
        editor.putString(General.IS_LOGIN, "1");
        editor.apply();
        Preferences.save(General.IS_LOGIN, 1);
        MakeDirectory.makeDirectories();
        saveData();
        // download profile image
        ImageDownloader.getImage(userInfo.getImage(), getApplicationContext());

        // for showing WeRHope slash screen
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
            Intent weRHopeIntent = new Intent(LoginActivity.this, WeRHopeSplashAcivity.class);
            startActivity(weRHopeIntent);
            finish();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mainIntent();
                }
            }, 5000);

        } else {
            mainIntent();
        }
    }

    //start MainActivity, onSuccessful oAuth token generation
    private void mainIntent() {
        //For CC only, on login, by default store caseloadSettingArrayList and set events = 0, plan =1 as true for showing events, plan in caseload list.
        ArrayList<ListItem> caseloadSettingArrayList = new ArrayList<>();
        int role_id = Integer.parseInt(Preferences.get(General.ROLE_ID));
        if (CheckRole.isCoordinator(role_id)) {
            for (int i = 0; i < 7; i++) {
                ListItem settingListItem = new ListItem();
                settingListItem.setId("" + i);
                if (Integer.parseInt(settingListItem.getId()) == 0 || Integer.parseInt(settingListItem.getId()) == 1) {
                    settingListItem.setSelected(true);
                } else {
                    settingListItem.setSelected(false);
                }
                caseloadSettingArrayList.add(settingListItem);
            }
            Gson gson = new Gson();
            String settingListString = gson.toJson(caseloadSettingArrayList);
            Preferences.save(General.SETTING_LIST, settingListString);
        }

        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    //onClick of forgot password, move to ForgotPasswordActivity
    private void forgotIntent() {
        Intent forgotIntent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        forgotIntent.putExtra(General.INSTANCE_KEY, serverList);
        startActivity(forgotIntent);
    }

    //Storing user data in preferences onSuccess login, onSuccessful oAuth token generation
    private void saveData() {
        String password = editTextUserPassword.getText().toString();
        Preferences.save(General.PASSWORD, password);
        Preferences.save(General.USER_ID, userInfo.getUserId());

        SharedPreferences UserInfoForUIKitPref = getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = UserInfoForUIKitPref.edit();
        editor.putString(General.USER_ID, userInfo.getUserId());
        editor.putString(General.DOMAIN_CODE, Preferences.get(General.DOMAIN_CODE));
        editor.putString(General.TIMEZONE, DeviceInfo.getTimeZone());
        editor.putString(General.COMET_CHAT_ID, userInfo.getComet_chat_id());
        editor.apply();
        AppLog.e(TAG, "saveData: screen.messagelist.Preferences UserId " + userInfo.getUserId());
        AppLog.e(TAG, "saveData: screen.messagelist.Preferences cometchat UserId " + userInfo.getComet_chat_id());
        screen.messagelist.Preferences.initialize(getApplicationContext());
        screen.messagelist.Preferences.save(General.USER_ID, userInfo.getUserId());
        screen.messagelist.Preferences.save(General.TIMEZONE, DeviceInfo.getTimeZone());
        screen.messagelist.Preferences.save(General.DOMAIN_CODE, Preferences.get(General.DOMAIN_CODE));
        AppLog.e(TAG, "saveData: screen.messagelist.Preferences UserId " + screen.messagelist.Preferences.get(General.USER_ID));
        AppLog.e(TAG, "saveData: screen.messagelist.Preferences timezone " + screen.messagelist.Preferences.get(General.TIMEZONE));
        AppLog.e(TAG, "saveData: screen.messagelist.Preferences Domain code " + screen.messagelist.Preferences.get(General.DOMAIN_CODE));
        Preferences.save(General.TIMEZONE, DeviceInfo.getTimeZone());
        Preferences.save(General.NAME, userInfo.getName());
        Preferences.save(General.FIRST_NAME, userInfo.getFirstName());
        Preferences.save(General.LAST_NAME, userInfo.getLastName());
        Preferences.save(General.USERNAME, userInfo.getUsername());
        Preferences.save(General.EMAIL, userInfo.getEmail());
        Preferences.save(General.ROLE, userInfo.getRole());
        AppLog.e(TAG, "User Role is " + userInfo.getRole() + " Id = " + userInfo.getRoleId());
        Preferences.save(General.ROLE_ID, userInfo.getRoleId());
        Preferences.save(General.URL_IMAGE, userInfo.getImage());
        Preferences.save(General.LOCAL_IMAGE, DirectoryList.DIR_MY_PIC);
        Preferences.save(General.PROFILE_IMAGE, userInfo.getImage()); //monika added on 05/02/19 as profile image not changing on logout also(new user login)
        Preferences.save(General.GROUP_ID, userInfo.getGroupId());
        Preferences.save(General.GROUP_NAME, userInfo.getGroupName());
        Preferences.save(General.IS_REVIEWER, userInfo.getIsReviewer());
        Preferences.save(General.API_KEY, "636c75cdc7316319545ee9cce125e3a1");
        Preferences.save(General.LAST_UPDATED, (System.currentTimeMillis() + (60 * 1000)));
        Preferences.save(General.MOOD_REMINDER_STATUS, userInfo.getMood_reminder_status());
        Preferences.save(General.MOBILE_SETTING, userInfo.getMood_setting());
        Preferences.save(General.ANDROID_OS_VERSION, System.getProperty("os.version"));
        Preferences.save(General.CITY_ID, userInfo.getCity());
        Preferences.save(General.STATE_ID, userInfo.getState());
        Preferences.save(General.COUNTRY_ID, userInfo.getCountry());
        Preferences.save(General.BIRTDATE, userInfo.getDob());
        Preferences.save(General.USER_COMETCHAT_ID, userInfo.getComet_chat_id());

        if (userInfo.getRole().equalsIgnoreCase("care coordinator")) {
            Preferences.save(General.IS_CC, 1);
        } else if (userInfo.getRole().equalsIgnoreCase("moderator")) {
            Preferences.save(General.IS_MODERATOR, 1);
        } else if (userInfo.getRole().equalsIgnoreCase("Case Manager")) {
            Preferences.save(General.IS_CASE_MANAGER, 1);
        } else if (userInfo.getRole().equalsIgnoreCase("Student")) {
            Preferences.save(General.IS_STUDENT, 1);
        }
        AppLog.e(TAG, "saveData: is_case_manager " + Preferences.get(General.IS_CASE_MANAGER));

        if (userInfo.getLati_longi() != null) {
            Preferences.save(General.LATI_LONGI, userInfo.getLati_longi());
        } else {
            Preferences.save(General.LATI_LONGI, 0);
        }
        if (userInfo.getLati_longi() != null) {
            Preferences.save(General.PROFILE_LOCATION, userInfo.getProfile_location());
        } else {
            Preferences.save(General.PROFILE_LOCATION, 0);
        }

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage013")
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage016")
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage021")) {
            Preferences.save(General.IS_COMETCHAT_LOGIN_SUCCESS, true);
        } else {
            try {
                cometChatLogin(userInfo.getComet_chat_id());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cometChatLogin(String Uid) {
        if (CometChat.getLoggedInUser() == null) {
            CometChat.login(Uid, AppConfig.AppDetails.AUTH_KEY, new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    AppLog.e(TAG, "Login Successful : " + user.toString());
                    CometChat.getLoggedInUser().setUid(Uid);
                    Preferences.save(General.IS_COMETCHAT_LOGIN_SUCCESS, true);
                    MessagingService.subscribeUserNotification(user.getUid());
                    AppLog.e(TAG, "subscribe : " + user.getUid());
                    registerPushNotificationToken();
                }

                @Override
                public void onError(CometChatException e) {
                    AppLog.d(TAG, "Login failed with exception: " + e.getMessage());
                    Preferences.save(General.IS_COMETCHAT_LOGIN_SUCCESS, false);
                }
            });
        }
    }

    private void registerPushNotificationToken() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AppLog.i(TAG, "registerPushNotificationToken : "+Preferences.get("regId"));
//                RegisterFirebaseTokenToServer(Preferences.get("regId"));
                RegisterFirebaseTokenToServer(StorageHelper.geTOKEN(LoginActivity.this));
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

        /*FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        firebaseToken = task.getResult();
                        Log.i(TAG, "onComplete: token " + firebaseToken);
                    }
                });*/

        CometChat.registerTokenForPushNotification(Preferences.get("regId"), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
                AppLog.e(TAG, "registerPushNotification onSuccess: token register successfully");
            }

            @Override
            public void onError(CometChatException e) {
                AppLog.e("onErrorPN: ", e.getMessage());
                AppLog.e(TAG, "registerPushNotification onSuccess: token register successfully");
            }
        });
    }


    private void RegisterFirebaseTokenToServer(String token) {

        SharedPreferences UserInfoForUIKitPref = getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(screen.messagelist.General.USER_ID, "");
        SharedPreferences domainUrlPref = getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String DomainURL = domainUrlPref.getString(screen.messagelist.General.DOMAIN, "");

        String url = screen.messagelist.Urls_.MOBILE_FIREBASE_REGISTER;

        AppLog.i(TAG, "RegisterFirebaseTokenToServer: Token at submit :"+token);

        AppLog.i(TAG, "RegisterFirebaseTokenToServer: " + DomainURL + " -> " + url +" Token :"+token);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(screen.messagelist.General.FIREBASE_ID, "" + token);
        requestMap.put(screen.messagelist.General.USER_ID, "" + UserId);

        RequestBody requestBody = NetworkCall_.make(requestMap, DomainURL + url, TAG, this);

        try {
            if (requestBody != null) {
                String response = NetworkCall_.post(DomainURL + url, requestBody, TAG, this);
                if (response != null) {
                    AppLog.i(TAG, "RegisterFirebaseTokenToServer:  response " + response);
                } else {
                    AppLog.i(TAG, "RegisterFirebaseTokenToServer:  null  ");
                }
            } else {
                AppLog.i(TAG, "RegisterFirebaseTokenToServer:  null2");
            }
        } catch (Exception e) {
            AppLog.i(TAG, "RegisterFirebaseTokenToServer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        start_time = GetTime.getChatTimestamp();
        new CountDownTimer(100, 5000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                getServers();
            }
        }.start();
    }

    //oAuth token- Authorization Success callback
    @Override
    public void authorizationSuccessCallback(JSONObject jsonObject) {
        Token token = new Token(this);
        token.getToken(Preferences.get(Oauth.CLIENT_ID),
                Preferences.get(Oauth.CLIENT_SECRET),
                Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, ""), this);
    }

    //oAuth token- Authorization failed callback
    @Override
    public void authorizationFailCallback(JSONObject jsonObject) {
        showResponses(13);
    }

    //oAuth- Authorization token generated successfully callback
    @Override
    public void tokenSuccessCallback(JSONObject jsonObject) {
        showResponses(1);
        moveNext();
    }

    //oAuth- Authorization token generation failed callback
    @Override
    public void tokenFailCallback(JSONObject jsonObject) {
        showResponses(13);
    }
}
