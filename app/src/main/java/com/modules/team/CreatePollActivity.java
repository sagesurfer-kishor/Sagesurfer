package com.modules.team;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.secure._Base64;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.RequestBody;


/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 18-09-2017
 *         Last Modified on 14-12-2017
 */

public class CreatePollActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CreatePollActivity.class.getSimpleName();
    private String start_time = "0";
    private int mYear = 0, mMonth = 0, mDay = 0;
    private String expiryDate = "", publishDate = "";

    private LinearLayout linearLayoutAddOption, linearLayoutOptionThree, linearLayoutOptionFour, linearLayoutOptionFive;
    private EditText questionBox, optionOneBox, optionTwoBox, optionThreeBox, optionFourBox, optionFiveBox;
    private TextView publishDateText, expiryDateText;
    private AppCompatImageView imageViewAddMoreOption;

    Toolbar toolbar;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.create_poll_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        //toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.screen_background));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_cancel_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        calendar = Calendar.getInstance();
        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        //titleText.setTextColor(getResources().getColor(R.color.text_color_primary));
        titleText.setText(this.getResources().getString(R.string.create_poll));

        AppCompatImageView postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        /*int color = Color.parseColor("#a5a5a5"); //text_color_tertiary
        postButton.setColorFilter(color);
        postButton.setImageResource(R.drawable.vi_check_white);*/
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);

        questionBox = (EditText) findViewById(R.id.create_poll_question);

        linearLayoutAddOption = (LinearLayout) findViewById(R.id.linearlayout_add_option);
        linearLayoutOptionThree = (LinearLayout) findViewById(R.id.linearlayout_option_three);
        linearLayoutOptionFour = (LinearLayout) findViewById(R.id.linearlayout_option_four);
        linearLayoutOptionFive = (LinearLayout) findViewById(R.id.linearlayout_option_five);

        optionOneBox = (EditText) findViewById(R.id.create_poll_option_one);
        optionTwoBox = (EditText) findViewById(R.id.create_poll_option_two);
        optionThreeBox = (EditText) findViewById(R.id.create_poll_option_three);
        optionFourBox = (EditText) findViewById(R.id.create_poll_option_four);
        optionFiveBox = (EditText) findViewById(R.id.create_poll_option_five);

        imageViewAddMoreOption = (AppCompatImageView) findViewById(R.id.imageview_add_more_option);
        imageViewAddMoreOption.setOnClickListener(this);
        publishDateText = (TextView) findViewById(R.id.create_poll_publish_date);
        publishDateText.setOnClickListener(this);
        expiryDateText = (TextView) findViewById(R.id.create_poll_expiry_date);
        expiryDateText.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.imageview_toolbar_save:
                hideKeyboard();
                String question = questionBox.getText().toString().trim();
                String optionOne = optionOneBox.getText().toString().trim();
                String optionTwo = optionTwoBox.getText().toString().trim();
                String optionThree = optionThreeBox.getText().toString().trim();
                String optionFour = optionFourBox.getText().toString().trim();
                String optionFive = optionFiveBox.getText().toString().trim();
                boolean validity = validate(question, optionOne, optionTwo, optionThree, optionFour, optionFive, v);

                if (validity) {
                    createPoll(question, getOptions(optionOne, optionTwo, optionThree,
                            optionFour, optionFive));
                }
                break;
            case R.id.create_poll_expiry_date:
                if (publishDate == null || publishDate.trim().length() <= 0) {
                    ShowSnack.viewWarning(v, "Enter Publish Date First", getApplicationContext());
                    return;
                }
                DatePickerDialog createPickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                expiryDate = year + "-" + GetCounters.checkDigit(monthOfYear) + "-" + GetCounters.checkDigit(dayOfMonth);
                                int result = Compare.validEndDate(expiryDate, publishDate);
                                if (result == 1) {
                                    expiryDateText.setText(expiryDate);
                                } else {
                                    expiryDate = null;
                                    expiryDateText.setText(null);
                                    ShowSnack.textViewWarning(expiryDateText, getApplicationContext().getResources()
                                            .getString(R.string.invalid_date), getApplicationContext());

                                }
                            }
                        }, mYear, mMonth, mDay);
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_WEEK, 1); //add 1 days from now
                createPickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                createPickerDialog.show();
                break;

            case R.id.imageview_add_more_option:
                if(linearLayoutOptionThree.getVisibility() == View.GONE) {
                    linearLayoutOptionThree.setVisibility(View.VISIBLE);
                } else {
                    if (linearLayoutOptionFour.getVisibility() == View.GONE) {
                        linearLayoutOptionFour.setVisibility(View.VISIBLE);
                    } else {
                        if (linearLayoutOptionFive.getVisibility() == View.GONE) {
                            linearLayoutOptionFive.setVisibility(View.VISIBLE);
                            linearLayoutAddOption.setVisibility(View.GONE);
                        }
                    }
                }
                break;

            case R.id.create_poll_publish_date:
                expiryDate = null;
                expiryDateText.setText(null);
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                publishDate = year + "-" + GetCounters.checkDigit(monthOfYear) + "-" + GetCounters.checkDigit(dayOfMonth);
                                try {
                                    /*int result = compareDate(mYear + "-" + (mMonth + 1) + "-" + mDay, publishDate);
                                    if (result == 0) {
                                        publishDateText.setText(publishDate);
                                    } else {
                                        publishDate = null;
                                        publishDateText.setText(null);
                                        ShowSnack.textViewWarning(publishDateText, getApplicationContext().getResources()
                                                .getString(R.string.invalid_date), getApplicationContext());
                                    }*/
                                    publishDateText.setText(publishDate);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0,0);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        start_time = GetTime.getChatTimestamp();
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        Preferences.initialize(getApplicationContext());
    }

    // validate all input data for proper values and length
    private boolean validate(String questionString, String optionOne, String optionTwo, String optionThree, String optionFour, String optionFive, View view) {
        if (questionString.length() <= 0 || questionString.length() < 6) {
            questionBox.setError("Invalid Question\nMin 6 char required\nMax 250 char allowed");
            return false;
        }
        if (optionOne.length() <= 0 && optionTwo.length() <= 0 && optionThree.length() <= 0 && optionFour.length() <= 0 && optionFive.length() <= 0) {
            ShowSnack.viewWarning(view, "Please provide min two options", getApplicationContext());
            return false;
        }
        if (optionOne.length() <= 0 || optionTwo.length() <= 0) {
            ShowSnack.viewWarning(view, "Please provide min two options", getApplicationContext());
            return false;
        }

        if (optionOne.length() > 0 && optionOne.length() < 2) {
            optionOneBox.setError(this.getResources().getString(R.string.poll_option_error));
            return false;
        }
        if (optionTwo.length() > 0 && optionTwo.length() < 2) {
            optionTwoBox.setError(this.getResources().getString(R.string.poll_option_error));
            return false;
        }
        if (optionThree.length() > 0 && optionThree.length() < 2) {
            optionThreeBox.setError(this.getResources().getString(R.string.poll_option_error));
            return false;
        }
        if (optionFour.length() > 0 && optionFour.length() < 2) {
            optionFourBox.setError(this.getResources().getString(R.string.poll_option_error));
            return false;
        }
        if (optionFive.length() > 0 && optionFive.length() < 2) {
            optionFiveBox.setError(this.getResources().getString(R.string.poll_option_error));
            return false;
        }

        if (publishDate == null) {
            ShowSnack.viewWarning(view, "Please provide valid publish date", getApplicationContext());
            return false;
        }
        if (expiryDate == null) {
            ShowSnack.viewWarning(view, "Please provide valid expiry Date", getApplicationContext());
            return false;
        }
        if (publishDate.length() <= 0 && expiryDate != null) {
            ShowSnack.viewWarning(view, "Please enter publish date first", getApplicationContext());
        }
        if (publishDate.isEmpty() || expiryDate.isEmpty() || publishDate.length() <= 0 || expiryDate.length() <= 0
                || publishDate == null || expiryDate == null) {
            ShowSnack.viewWarning(view, "Please provide valid dates", getApplicationContext());
            return false;
        } else {
            int result = 0;
            try {
                result = compareDate(publishDate, expiryDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (result == 0 || result == 1) {
                ShowSnack.viewWarning(view, "Please provide valid dates", getApplicationContext());
                return false;
            }
        }

        return true;
    }

    // make string of all options entered to one string
    private String getOptions(String optionOne, String optionTwo, String optionThree,
                              String optionFour, String optionFive) {
        String options = "";

        if (optionOne.length() >= 2) {
            options = optionOne;
        }
        if (optionTwo.length() >= 2) {
            options = options + "," + optionTwo;
        }
        if (optionThree.length() >= 2) {
            options = options + "," + optionThree;
        }
        if (optionFour.length() >= 2) {
            options = options + "," + optionFour;
        }
        if (optionFive.length() >= 2) {
            options = options + "," + optionFive;
        }
        return options;
    }

    // make network call to create new poll
    private void createPoll(String questions, String options) {
        int status = 12;
        String info = DeviceInfo.get(this);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.POST_POLL);
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.TEAM_LIST, Preferences.get(General.TEAM_ID));
        requestMap.put(General.USER_LIST, "");
        requestMap.put(General.START_TIME, start_time);
        requestMap.put(General.END_TIME, GetTime.getChatTimestamp());
        requestMap.put(General.INFO, _Base64.encode(info));
        requestMap.put(General.IP, DeviceInfo.getDeviceMAC(this));
        requestMap.put(General.PUBLISHED_DATE, publishDate);
        requestMap.put(General.EXPIRES_ON, expiryDate);
        requestMap.put(General.POLL_QUESTION, questions);
        requestMap.put(General.POLL_OPTIONS, options);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DETAILS_PAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.POST_WALL);
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        if (object.has(General.STATUS)) {
                            status = object.get(General.STATUS).getAsInt();
                        } else {
                            status = 11;
                        }
                    } else {
                        status = 11;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(status, publishDateText);
    }

    private void showResponses(int status, View view) {
        String message = this.getResources().getString(R.string.action_failed);
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
        }
        SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
        if (status == 1) {
            onBackPressed();
        }
    }

    // compare date with today's date
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

    // hide soft keyboard
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
