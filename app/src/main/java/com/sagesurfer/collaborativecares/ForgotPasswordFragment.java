package com.sagesurfer.collaborativecares;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sagesurfer.captcha.TextCaptcha;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.models.Server_;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.secure.KeyMaker_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.validator.LoginValidator;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ForgotPasswordFragment.class.getSimpleName();

    @BindView(R.id.imageview_forgot_password_fragment_back)
    ImageView imageViewBack;
    @BindView(R.id.textview_forgot_password_fragment_title)
    TextView textViewTitle;
    @BindView(R.id.textview_forgot_password_fragment_submit)
    TextView textViewSubmit;
    @BindView(R.id.button_forgot_password_fragment_captcha_reset)
    Button buttonCaptchaReset;
    @BindView(R.id.linearlayout_forgot_password_support)
    LinearLayout linearLayoutSupport;
    @BindView(R.id.linearlayout_forgot_password_other)
    LinearLayout linearLayoutOther;
    @BindView(R.id.imageview_forgot_password_fragment_captcha)
    ImageView imageViewCaptcha;
    @BindView(R.id.textview_forgot_password_fragment_dob)
    TextView textViewDOB;
    @BindView(R.id.textview_forgot_password_fragment_security_question)
    TextView textViewSecurityQuestion;
    @BindView(R.id.edittext_forgot_password_fragment_username)
    EditText editTextUsername;
    @BindView(R.id.edittext_forgot_password_fragment_email)
    EditText editTextEmail;
    @BindView(R.id.edittext_forgot_password_fragment_reason)
    EditText editTextReason;
    @BindView(R.id.edittext_forgot_password_fragment_message)
    EditText editTextMessage;
    @BindView(R.id.edittext_forgot_password_fragment_registered_email)
    EditText editTextRegisteredEmail;
    @BindView(R.id.edittext_forgot_password_fragment_security_answer)
    EditText editTextSecurityAnswer;
    @BindView(R.id.edittext_forgot_password_fragment_captcha_answer)
    EditText editCaptchaAnswer;
    @BindView(R.id.edittext_forgot_password_fragment_code)
    EditText editTextCode;
    @BindView(R.id.edittext_forgot_password_fragment_server_code)
    EditText editTextServerCode;

    private String title = "";
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String date_of_birth = "";
    private ArrayList<Server_> instanceList;

    private TextCaptcha textCaptcha;
    private Activity activity;
    private Unbinder unbinder;
    private MainActivityInterface mainActivityInterface;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //mainActivityInterface = (MainActivityInterface) activity;
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password_layout, null);
        unbinder = ButterKnife.bind(this, view);

        activity = getActivity();

        Bundle data = getArguments();
        if (data.containsKey(General.INSTANCE_KEY)) {
            title = data.getString(General.TITLE);
            instanceList = (ArrayList<Server_>) data.getSerializable(General.INSTANCE_KEY);
        } else {
            removeFragment();
        }

        VectorDrawableCompat dobDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_angle_arrow_down_blue, textViewDOB.getContext().getTheme());
        textViewDOB.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, dobDrawable, null);

        imageViewBack.setOnClickListener(this);
        textViewTitle.setText(title);
        textViewSubmit.setOnClickListener(this);
        buttonCaptchaReset.setOnClickListener(this);
        textViewDOB.setOnClickListener(this);

        editTextRegisteredEmail.setOnFocusChangeListener(onFocusChange);
        editTextCode.setOnFocusChangeListener(onFocusChange);
        editTextServerCode.setOnFocusChangeListener(onFocusChange);

        toggleLayout();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //mainActivityInterface.setMainTitle(title);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private final View.OnFocusChangeListener onFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getId() == R.id.edittext_forgot_password_fragment_registered_email) {
                if (!hasFocus) {
                    if (editTextRegisteredEmail != null) {
                        if (editTextRegisteredEmail.getText().length() > 0) {
                            String email = editTextRegisteredEmail.getText().toString().trim();
                            if (LoginValidator.isEmail(email)) {
                                new GetQuestion().execute(email);
                            } else {
                                editTextRegisteredEmail.setError("Invalid Email");
                            }
                        }
                    }
                }
            } else if (v.getId() == R.id.edittext_forgot_password_fragment_server_code) {
                if (editTextServerCode != null) {
                    if (editTextServerCode.getText().length() > 0) {
                        String code = editTextServerCode.getText().toString().trim();
                        if (!LoginValidator.isCode(code) || !checkCode(code)) {
                            editTextServerCode.setError(activity.getResources().getString(R.string.invalid_server_code));
                        }
                        if (code.equalsIgnoreCase("sage018") || code.equalsIgnoreCase("sage019")) {
                            editTextServerCode.setError(activity.getResources().getString(R.string.invalid_server_code));
                        }
                    }
                }
            } else if (v.getId() == R.id.edittext_forgot_password_fragment_code) {
                if (editTextCode != null) {
                    if (editTextCode.getText().length() > 0) {
                        String code = editTextCode.getText().toString().trim();
                        if (!LoginValidator.isCode(code) || !checkCode(code)) {
                            editTextCode.setError(activity.getResources().getString(R.string.invalid_server_code));
                        }
                        if (code.equalsIgnoreCase("sage018") || code.equalsIgnoreCase("sage019")) {
                            editTextCode.setError(activity.getResources().getString(R.string.invalid_server_code));
                        }
                    }
                }
            }
        }
    };

    // set captcha code to the field
    private void setCaptchaImage(boolean isReset) {
        if (isReset) {
            imageViewCaptcha.setImageBitmap(null);
        }
        textCaptcha = new TextCaptcha(600, 150, 4, TextCaptcha.TextOptions.NUMBERS_AND_LETTERS);
        imageViewCaptcha.setImageBitmap(textCaptcha.getImage());
    }

    // toggle fields based on choice user's choice
    private void toggleLayout() {
        if (title.equalsIgnoreCase(activity.getApplicationContext().getResources()
                .getString(R.string.contact_to_support_team))) {
            linearLayoutSupport.setVisibility(View.VISIBLE);
            linearLayoutOther.setVisibility(View.GONE);
            textViewSubmit.setText(activity.getApplicationContext().getResources().getString(R.string.send));
        } else {
            textViewSubmit.setText(activity.getApplicationContext().getResources().getString(R.string.submit));
            linearLayoutSupport.setVisibility(View.GONE);
            linearLayoutOther.setVisibility(View.VISIBLE);
            if (title.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.forgot_password))) {
                textViewDOB.setVisibility(View.GONE);
                editTextUsername.setVisibility(View.VISIBLE);
            } else {
                textViewDOB.setVisibility(View.VISIBLE);
                editTextUsername.setVisibility(View.GONE);
            }
            setCaptchaImage(true);
        }
    }

    // check if server code is available or not
    private boolean checkCode(String code) {
        for (Server_ server : instanceList) {
            if (server.getKey().equals(code)) {
                Preferences.save(General.DOMAIN, server.getDomainUrl());
                Preferences.save(General.DOMAIN_CODE, code);
                Preferences.save(General.CHAT_URL, server.getChatUrl());
                return true;
            }
        }
        return false;
    }

    // make com.sagesurfer.network call for other option
    private void submitOther() {
        String email = editTextRegisteredEmail.getText().toString().trim();
        String answer = editTextSecurityAnswer.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String code = editTextServerCode.getText().toString().trim();
        String action;
        if (!LoginValidator.isCode(code) || !checkCode(code)) {
            editTextServerCode.setError(activity.getResources().getString(R.string.invalid_server_code));
            return;
        }
        if (code.equalsIgnoreCase("sage018") || code.equalsIgnoreCase("sage019")) {
            editTextServerCode.setError(activity.getResources().getString(R.string.invalid_server_code));
            return;
        }

        if (!LoginValidator.isEmail(email)) {
            editTextRegisteredEmail.setError("Invalid Email");
            return;
        }
        if (title.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.forgot_password))) {
            if (username.length() <= 0 || username.length() <= 5) {
                editTextUsername.setError("Invalid username");
                return;
            }
            action = General.PASSWORD;
        } else {
            if (date_of_birth.trim().length() <= 0) {
                ShowSnack.viewWarning(textViewDOB, "Invalid date of birth", activity.getApplicationContext());
                return;
            }
            action = General.USERNAME;
        }
        if (answer.length() <= 0) {
            editTextSecurityAnswer.setError("Invalid answer");
            return;
        }
        if (!textCaptcha.checkAnswer(editCaptchaAnswer.getText().toString().trim())) {
            editCaptchaAnswer.setError("Captcha doesn't match");
            setCaptchaImage(true);
            return;
        }
        new OtherAction(email, answer, username, action).execute();
    }

    // make com.sagesurfer.network call with support option
    private void submitSupport() {
        String email = editTextEmail.getText().toString().trim();
        String reason = editTextReason.getText().toString().trim();
        String message = editTextMessage.getText().toString().trim();
        String code = editTextCode.getText().toString().trim();
        if (!LoginValidator.isCode(code) || !checkCode(code)) {
            editTextCode.setError(activity.getResources().getString(R.string.invalid_server_code));
            return;
        }
        if (code.equalsIgnoreCase("sage018") || code.equalsIgnoreCase("sage019")) {
            editTextCode.setError(activity.getResources().getString(R.string.invalid_server_code));
            return;
        }
        if (!LoginValidator.isEmail(email)) {
            editTextEmail.setError("Invalid Email");
            return;
        }
        if (reason.length() <= 0 || reason.length() < 3) {
            editTextReason.setError("Provide valid reason");
            return;
        }
        if (message.length() <= 0 || message.length() < 3) {
            editTextMessage.setError("Enter Valid Message");
            return;
        }
        new SupportAction(email, reason, message).execute();
    }

    private void removeFragment() {
        hideKeyboard();
        getFragmentManager().popBackStack();
    }

    // hide soft keyboard
    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_forgot_password_fragment_back:
                removeFragment();
                break;
            case R.id.button_forgot_password_fragment_captcha_reset:
                setCaptchaImage(true);
                break;
            case R.id.textview_forgot_password_fragment_dob:
                Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                date_of_birth = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                try {
                                    int result = compareDate(mYear + "-" + (mMonth + 1) + "-" + mDay, date_of_birth);
                                    if (result == 1) {
                                        textViewDOB.setText(GetTime.month_DdYyyy(date_of_birth));
                                    } else {
                                        date_of_birth = null;
                                        textViewDOB.setText("");
                                        ShowToast.toast("Invalid Date of Birth", activity.getApplicationContext());
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePicker.show();
                break;
            case R.id.textview_forgot_password_fragment_submit:
                if (title.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.contact_to_support_team))) {
                    submitSupport();
                } else {
                    submitOther();
                }
                break;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private int compareDate(String today, String selected_date) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        Date date1 = dateFormat.parse(today);
        Date date2 = dateFormat.parse(selected_date);

        calendar1.setTime(date1);
        calendar2.setTime(date2);

        return calendar1.compareTo(calendar2);
    }

    // make background com.sagesurfer.network call
    @SuppressLint("StaticFieldLeak")
    private class OtherAction extends AsyncTask<Void, Void, Integer> {
        final String email;
        final String answer;
        final String username;
        final String action;
        String error = "";

        OtherAction(String email, String answer, String username, String action) {
            this.email = email;
            this.answer = answer;
            this.username = username;
            this.action = action;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 12;
            HashMap<String, String> keyMap = KeyMaker_.getKey();
            RequestBody loginBody = new FormBody.Builder()
                    .add(General.KEY, keyMap.get(General.KEY))
                    .add(General.TOKEN, keyMap.get(General.TOKEN))
                    .add(General.ACTION, action)
                    .add(General.EMAIL, email)
                    .add(General.USERNAME, username)
                    .add("dob", date_of_birth)
                    .add("answer", answer)
                    .add(General.IMEI, DeviceInfo.getDeviceId(activity))
                    .add(General.VERSION, DeviceInfo.getVersion())
                    .add(General.MODELNO, DeviceInfo.getDeviceName()).build();
            try {
                String response = MakeCall.post(Preferences.get(General.DOMAIN) + Urls_.MOBILE_FORGOT_PASSWORD, loginBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(action)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(action);
                        JSONObject object = jsonArray.getJSONObject(0);
                        if (object.has(General.STATUS)) {
                            result = object.getInt(General.STATUS);
                            if (result != 1) {
                                error = object.getString(General.ERROR);
                            }
                            if (result == 1) {
                                error = object.getString(General.MSG);
                            }
                        } else {
                            result = 11;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case 1:
                    ShowToast.showLongerDurationToast(error, activity.getApplicationContext());
                    ForgotPasswordActivity.forgotPasswordActivity.onBackPressed();
                    break;
                case 2:
                    ShowToast.showLongerDurationToast(activity.getApplicationContext().getResources().getString(R.string.action_failed), activity.getApplicationContext());
                    break;
                case 3:
                    ShowSnack.viewWarning_One(textViewDOB, error, activity.getApplicationContext());
                    break;
                case 11:
                    ShowSnack.viewWarning(textViewDOB, activity.getApplicationContext().getResources()
                            .getString(R.string.internal_error_occurred), activity.getApplicationContext());
                    break;
                case 12:
                    ShowSnack.viewWarning(textViewDOB, activity.getApplicationContext().getResources()
                            .getString(R.string.network_error_occurred), activity.getApplicationContext());
                    break;
                default:
                    ShowSnack.viewWarning(textViewDOB, "Unknown Error Code: " + result, activity.getApplicationContext());
                    break;
            }
        }
    }

    // make background com.sagesurfer.network call
    @SuppressLint("StaticFieldLeak")
    private class SupportAction extends AsyncTask<Void, Void, Integer> {
        final String email;
        final String reason;
        final String message;
        String error = "";

        SupportAction(String email, String reason, String message) {
            this.email = email;
            this.reason = reason;
            this.message = message;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 12;
            HashMap<String, String> keyMap = KeyMaker_.getKey();
            RequestBody loginBody = new FormBody.Builder()
                    .add(General.KEY, keyMap.get(General.KEY))
                    .add(General.TOKEN, keyMap.get(General.TOKEN))
                    .add(General.ACTION, Actions_.SUPPORT)
                    .add(General.EMAIL, email)
                    .add(General.MESSAGE, message)
                    .add("reason", reason)
                    .add(General.IMEI, DeviceInfo.getDeviceId(activity))
                    .add(General.VERSION, DeviceInfo.getVersion())
                    .add(General.MODELNO, DeviceInfo.getDeviceName()).build();
            try {
                String response = MakeCall.post(Preferences.get(General.DOMAIN) + Urls_.MOBILE_FORGOT_PASSWORD, loginBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(Actions_.SUPPORT)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Actions_.SUPPORT);
                        JSONObject object = jsonArray.getJSONObject(0);
                        if (object.has(General.STATUS)) {
                            result = object.getInt(General.STATUS);
                            if (result != 1) {
                                error = object.getString(General.ERROR);
                            }
                            if (result == 1) {
                                error = object.getString(General.MSG);
                            }
                        } else {
                            result = 11;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case 1:
                    ShowToast.toast(error, activity.getApplicationContext());
                    ForgotPasswordActivity.forgotPasswordActivity.onBackPressed();
                    break;
                case 2:
                    ShowToast.toast(activity.getApplicationContext().getResources().getString(R.string.action_failed), activity.getApplicationContext());
                    break;
                case 3:
                    ShowSnack.viewWarning(textViewDOB, error, activity.getApplicationContext());
                    break;
                case 11:
                    ShowSnack.viewWarning(textViewDOB, activity.getApplicationContext().getResources()
                            .getString(R.string.internal_error_occurred), activity.getApplicationContext());
                    break;
                case 12:
                    ShowSnack.viewWarning(textViewDOB, activity.getApplicationContext().getResources()
                            .getString(R.string.network_error_occurred), activity.getApplicationContext());
                    break;
                default:
                    ShowSnack.viewWarning(textViewDOB, "Unknown Error Code: " + result, activity.getApplicationContext());
                    break;
            }
        }
    }

    // make background com.sagesurfer.network call
    @SuppressLint("StaticFieldLeak")
    private class GetQuestion extends AsyncTask<String, Void, Integer> {
        String question = activity.getApplicationContext().getResources()
                .getString(R.string.security_question_user_specified_security_question);
        String sub_question = "";

        @Override
        protected Integer doInBackground(String... params) {
            int result = 12;
            HashMap<String, String> keyMap = KeyMaker_.getKey();
            RequestBody loginBody = new FormBody.Builder()
                    .add(General.KEY, keyMap.get(General.KEY))
                    .add(General.TOKEN, keyMap.get(General.TOKEN))
                    .add(General.ACTION, "get_question")
                    .add(General.EMAIL, params[0])
                    .add(General.IMEI, DeviceInfo.getDeviceId(activity))
                    .add(General.VERSION, DeviceInfo.getVersion())
                    .add(General.MODELNO, DeviceInfo.getDeviceName()).build();
            try {
                String response = MakeCall.post(Preferences.get(General.DOMAIN) + Urls_.MOBILE_FORGOT_PASSWORD, loginBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("get_question")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("get_question");
                        JSONObject object = jsonArray.getJSONObject(0);
                        if (object.has(General.STATUS)) {
                            result = object.getInt(General.STATUS);
                            if (result == 1) {
                                sub_question = object.getString("question");
                            }
                        } else {
                            result = 11;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case 1:
                    question = "Security Question : " + sub_question;
                    break;
                case 2:
                    editTextRegisteredEmail.setError("Email is not registered with us");
                    break;
                case 11:
                    ShowSnack.viewWarning(textViewDOB, activity.getApplicationContext().getResources()
                            .getString(R.string.internal_error_occurred), activity.getApplicationContext());
                    break;
                case 12:
                    ShowSnack.viewWarning(textViewDOB, activity.getApplicationContext().getResources()
                            .getString(R.string.network_error_occurred), activity.getApplicationContext());
                    break;
                default:
                    ShowSnack.viewWarning(textViewDOB, "Unknown Error Code: " + result, activity.getApplicationContext());
                    break;
            }
            textViewSecurityQuestion.setText(question);
        }
    }
}