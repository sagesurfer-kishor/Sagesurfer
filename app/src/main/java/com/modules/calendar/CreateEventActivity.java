
package com.modules.calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.datetime.TimePicker;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.library.GetSelected;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.models.Location_;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.Events_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.secure._Base64;
import com.sagesurfer.selectors.LocationSelectorDialog;
import com.sagesurfer.selectors.MultiUserSelectorDialog;
import com.sagesurfer.selectors.SingleTeamSelectorDialog;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformGetTeamsTask;
import com.sagesurfer.tasks.PerformGetUsersTask;
import com.storage.preferences.Preferences;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

/**
 * @author Kailash Karankal
 * Created on 08-09-2019
 */

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener,
        SingleTeamSelectorDialog.GetChoice, MultiUserSelectorDialog.SelectedUsers, LocationSelectorDialog.GetChoice {
    private static final String TAG = CreateEventActivity.class.getSimpleName();
    private String start_time = "0", group_name = "", user_name = "", user_id = "0", location_name = "", startDate, endDate, startTime, endTime;
    private int group_id = 0, location_id = 0;
    private ArrayList<Teams_> teamArrayList;
    private ArrayList<Friends_> friendsArrayList;
    private ArrayList<Location_> locationArrayList;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private boolean isFromPlanner = false;

    private TextView selectTeamLabel, tv_selectedTeam, selectParticipants, startDateText, endDateText, textViewLocation;
    private EditText et_title, et_description;
    private Spinner sp_startTimeText, sp_endTimeText;
    private AppCompatImageView imageViewAddTeam, imageViewAddParticipant, imageViewAddLocation;
    private LinearLayout linearLayoutTeam;
    private int startTimePosition = 0, tempStartTimePosition = 0;
    private Toolbar toolbar;

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));
        setContentView(R.layout.create_event_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
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

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setText(this.getResources().getString(R.string.create_event));

        AppCompatImageView postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);

        Intent data = getIntent();
        if (data != null) {
            if (data.hasExtra(General.IS_FROM_PLANNER)) {
                isFromPlanner = data.getBooleanExtra(General.IS_FROM_PLANNER, false);
            }
        }

        linearLayoutTeam = (LinearLayout) findViewById(R.id.linearlayout_team);
        selectTeamLabel = (TextView) findViewById(R.id.create_event_select_team_label);
        tv_selectedTeam = (TextView) findViewById(R.id.create_event_select_team);

        tv_selectedTeam.setOnClickListener(this);
        if (isFromPlanner) {
            linearLayoutTeam.setVisibility(View.GONE);
            selectTeamLabel.setVisibility(View.GONE);
            tv_selectedTeam.setVisibility(View.GONE);
        }

        selectParticipants = (TextView) findViewById(R.id.create_event_select_participant);
        selectParticipants.setOnClickListener(this);

        startDateText = (TextView) findViewById(R.id.create_event_start_date);
        startDateText.setOnClickListener(this);
        endDateText = (TextView) findViewById(R.id.create_event_end_date);
        endDateText.setOnClickListener(this);

        sp_startTimeText = (Spinner) findViewById(R.id.create_event_start_time);
        sp_endTimeText = (Spinner) findViewById(R.id.create_event_end_time);

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerStartTimeArrayAdapter = new ArrayAdapter<String>(this, R.layout.drop_down_selected_text_item_layout, TimePicker.time12()) {
            @Override
            public boolean isEnabled(int position) {
                Date currentDateTime = new Date();
                Date spinnerDateTime = new Date();
                String fromSelectedDateString = startDateText.getText().toString(), dateDateString = null;

                try {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/M/yyyy hh:mm aa");
                    String currentDateTimeString = formatter1.format(calendar.getTime()); //18/3/2019 03:15 pm
                    currentDateTime = formatter1.parse(currentDateTimeString); //Mon Mar 18 15:26:00 GMT+05:30 2019

                    SimpleDateFormat formatter2 = new SimpleDateFormat("dd/M/yyyy");
                    String currentDateString = formatter2.format(calendar.getTime());
                    final String spinnerTimeString = TimePicker.time12().get(position);
                    String spinnerDateTimeString = currentDateString + " " + spinnerTimeString; //18/3/2019 12:00 AM
                    spinnerDateTime = formatter1.parse(spinnerDateTimeString); //Mon Mar 18 00:00:00 GMT+05:30 2019

                    SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy-M-dd");
                    dateDateString = formatter3.format(calendar.getTime());

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (fromSelectedDateString.equals(dateDateString)) {
                    if (currentDateTime.before(spinnerDateTime)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                Date currentDateTime = new Date();
                Date spinnerDateTime = new Date();

                String fromSelectedDateString = startDateText.getText().toString(), dateDateString = null;
                try {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/M/yyyy hh:mm aa");
                    String currentDateTimeString = formatter1.format(calendar.getTime()); //18/3/2019 03:26 pm
                    currentDateTime = formatter1.parse(currentDateTimeString); //Mon Mar 18 15:26:00 GMT+05:30 2019

                    SimpleDateFormat formatter2 = new SimpleDateFormat("dd/M/yyyy");
                    String currentDateString = formatter2.format(calendar.getTime());
                    final String spinnerTimeString = TimePicker.time12().get(position);
                    String spinnerDateTimeString = currentDateString + " " + spinnerTimeString; //18/3/2019 12:00 AM
                    spinnerDateTime = formatter1.parse(spinnerDateTimeString); //Mon Mar 18 00:00:00 GMT+05:30 2019

                    SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy-M-dd");
                    dateDateString = formatter3.format(calendar.getTime());

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (fromSelectedDateString.equals(dateDateString)) {
                    if (currentDateTime.before(spinnerDateTime)) {
                        tv.setTextColor(Color.BLACK);
                    } else {
                        tv.setTextColor(Color.GRAY);
                    }
                } else {
                    tv.setTextColor(Color.BLACK);
                }

                return view;
            }
        };

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerEndTimeArrayAdapter = new ArrayAdapter<String>(this, R.layout.drop_down_selected_text_item_layout, TimePicker.time12()) {
            @Override
            public boolean isEnabled(int position) {

                Date currentDateTime = new Date();
                Date spinnerDateTime = new Date();
                String fromSelectedDateString = endDateText.getText().toString(), dateDateString = null;

                try {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/M/yyyy hh:mm aa");
                    String currentDateTimeString = formatter1.format(calendar.getTime()); //18/3/2019 03:15 pm
                    currentDateTime = formatter1.parse(currentDateTimeString); //Mon Mar 18 15:26:00 GMT+05:30 2019

                    SimpleDateFormat formatter2 = new SimpleDateFormat("dd/M/yyyy");
                    String currentDateString = formatter2.format(calendar.getTime());
                    final String spinnerTimeString = TimePicker.time12().get(position);
                    String spinnerDateTimeString = currentDateString + " " + spinnerTimeString; //18/3/2019 12:00 AM
                    spinnerDateTime = formatter1.parse(spinnerDateTimeString); //Mon Mar 18 00:00:00 GMT+05:30 2019

                    SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy-M-dd");
                    dateDateString = formatter3.format(calendar.getTime());

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (fromSelectedDateString.equals(dateDateString)) {
                    if (currentDateTime.before(spinnerDateTime)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }

               /* if (position > startTimePosition) {
                    return true;
                } else {
                    return false;
                }*/
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position > startTimePosition) {
                    tv.setTextColor(Color.BLACK);
                } else {
                    tv.setTextColor(Color.GRAY);
                }
                return view;
            }
        };

        spinnerStartTimeArrayAdapter.setDropDownViewResource(R.layout.drop_down_text_item_layout);
        spinnerEndTimeArrayAdapter.setDropDownViewResource(R.layout.drop_down_text_item_layout);
        sp_startTimeText.setAdapter(spinnerStartTimeArrayAdapter);
        sp_endTimeText.setAdapter(spinnerEndTimeArrayAdapter);

        AdapterView.OnItemSelectedListener startTimeSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View container, int position, long id) {
                startTimePosition = position;
                tempStartTimePosition = position;
                sp_endTimeText.setSelection(startTimePosition + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        };

        // Setting ItemClick Handler for Spinner Widget
        sp_startTimeText.setOnItemSelectedListener(startTimeSelectedListener);

        if (data != null && data.hasExtra(General.TEAM_ID)) {
            group_id = Integer.parseInt(data.getStringExtra(General.TEAM_ID));
        }
        for (int i = 0; i < TimePicker.time12().size(); i++) {
            Date currentDateTime = new Date();
            Date spinnerDateTime = new Date();
            try {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat formatter1 = new SimpleDateFormat("dd/M/yyyy hh:mm aa");
                String currentDateTimeString = formatter1.format(calendar.getTime()); //18/3/2019 03:26 pm
                currentDateTime = formatter1.parse(currentDateTimeString); //Mon Mar 18 15:26:00 GMT+05:30 2019

                SimpleDateFormat formatter2 = new SimpleDateFormat("dd/M/yyyy");
                String currentDateString = formatter2.format(calendar.getTime());
                final String spinnerTimeString = TimePicker.time12().get(i);
                String spinnerDateTimeString = currentDateString + " " + spinnerTimeString; //18/3/2019 12:00 AM
                spinnerDateTime = formatter1.parse(spinnerDateTimeString); //Mon Mar 18 00:00:00 GMT+05:30 2019
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (currentDateTime.before(spinnerDateTime)) {
                startTimePosition = i;
                tempStartTimePosition = i;
                sp_startTimeText.setSelection(i);
                sp_endTimeText.setSelection(i + 1);
                break;
            }
        }

        et_title = (EditText) findViewById(R.id.create_event_title);
        et_description = (EditText) findViewById(R.id.create_event_description);

        textViewLocation = (TextView) findViewById(R.id.textview_createevent_location);
        textViewLocation.setOnClickListener(this);

        int color = Color.parseColor("#ffffff"); //white
        imageViewAddTeam = (AppCompatImageView) findViewById(R.id.imageview_createevent_add_team);
        imageViewAddTeam.setColorFilter(color);
        imageViewAddTeam.setImageResource(R.drawable.vi_add_task_list_p);
        imageViewAddTeam.setOnClickListener(this);

        imageViewAddParticipant = (AppCompatImageView) findViewById(R.id.imageview_createevent_add_participant);
        imageViewAddParticipant.setColorFilter(color);
        imageViewAddParticipant.setImageResource(R.drawable.vi_add_task_list_p);
        imageViewAddParticipant.setOnClickListener(this);

        imageViewAddLocation = (AppCompatImageView) findViewById(R.id.imageview_createevent_addlocation);
        imageViewAddLocation.setColorFilter(color);
        imageViewAddLocation.setImageResource(R.drawable.vi_add_task_list_p);
        imageViewAddLocation.setOnClickListener(this);
    }

    // Validate all field for data before posting it
    private boolean validate(String title, String description) {
        startTime = TimePicker.time24().get(sp_startTimeText.getSelectedItemPosition());
        endTime = TimePicker.time24().get(sp_endTimeText.getSelectedItemPosition());

        if (title == null || title.length() < 3) {
            et_title.setError("Invalid Event Name \nMin Char allowed is 3");
            return false;
        }
        if (title.length() > 70) {
            et_title.setError("Invalid Event Name \nMax Char allowed is 250");
            return false;
        }
        if (description == null || description.length() < 3) {
            et_description.setError("Invalid Event Description \nMin Char allowed is 3");
            return false;
        }
        if (description.length() > 1000) {
            et_description.setError("Invalid Event Description \nMax Char allowed is 1000");
            return false;
        }

        if (startDate == null || startDate.trim().length() <= 0) {
            ShowSnack.textViewWarning(startDateText, "Enter Valid Start Date", getApplicationContext());
            return false;
        }
        if (endDate == null || endDate.trim().length() <= 0) {
            ShowSnack.textViewWarning(startDateText, "Enter Valid End Date", getApplicationContext());
            return false;
        }
        if (startTime == null || startTime.trim().length() <= 0) {
            ShowSnack.textViewWarning(startDateText, "Enter Valid Start Time", getApplicationContext());
            return false;
        }
        if (endTime == null || endTime.trim().length() <= 0) {
            ShowSnack.textViewWarning(startDateText, "Enter Valid End Time", getApplicationContext());
            return false;
        }

        if (sp_startTimeText.getSelectedItemPosition() == sp_endTimeText.getSelectedItemPosition()) {
            ShowSnack.textViewWarning(startDateText, "Please Enter Valid Event Time", getApplicationContext());
            return false;
        }
        if (sp_startTimeText.getSelectedItemPosition() > sp_endTimeText.getSelectedItemPosition()) {
            ShowSnack.textViewWarning(startDateText, "Please Enter Valid Event Time", getApplicationContext());
            return false;
        }
        return true;
    }

    // Get time in separate hour and minute format
    private static String[] getTime(String input) {
        input = input.replaceAll(" AM", "").replaceAll(" PM", "");
        return input.split(":");
    }

    // Make network call to post/create new event
    private void postEvent(String title, String description) {
        int result = 12;
        String[] startArray, endArray, tStartArray, tEndArray;
        String info = DeviceInfo.get(CreateEventActivity.this);
        startArray = startDate.split("-");
        endArray = endDate.split("-");
        tStartArray = getTime(startTime);
        tEndArray = getTime(endTime);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ADD_EVENT);
        requestMap.put(General.GROUP_ID, "" + group_id);
        requestMap.put(General.PARTICIPANT, user_id);
        requestMap.put(General.EVENT_NAME, title);
        requestMap.put(General.EVENT_DESCRIPTION, description);
        requestMap.put(General.START_YEAR, startArray[0]);
        requestMap.put(General.START_MONTH, GetCounters.checkDigit(Integer.parseInt(startArray[1])));
        requestMap.put(General.START_DAY, GetCounters.checkDigit(Integer.parseInt(startArray[2])));
        requestMap.put(General.START_HOUR, tStartArray[0]);
        requestMap.put(General.START_MINUTE, tStartArray[1]);
        requestMap.put(General.END_YEAR, endArray[0]);
        requestMap.put(General.END_MONTH, GetCounters.checkDigit(Integer.parseInt(endArray[1])));
        requestMap.put(General.END_DAY, GetCounters.checkDigit(Integer.parseInt(endArray[2])));
        requestMap.put(General.END_HOUR, tEndArray[0]);
        requestMap.put(General.END_MINUTE, tEndArray[1]);
        requestMap.put(General.START_TIME, start_time);
        requestMap.put(General.END_TIME, GetTime.getChatTimestamp());
        requestMap.put(General.INFO, _Base64.encode(info));
        requestMap.put(General.IP, DeviceInfo.getDeviceMAC(CreateEventActivity.this));
        requestMap.put(General.LOCATION, String.valueOf(location_id));
        requestMap.put(General.DOMAIN_CODE, Preferences.get(General.DOMAIN_CODE));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DETAILS_PAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    Log.e(TAG, "postEvent: addEvent"+response +" request "+requestBody);
                    if (Error_.oauth(response, getApplicationContext()) == 13) {
                        result = 13;
                    } else {
                        JsonObject object = GetJson_.getJson(response);
                        if (object != null && object.has(General.STATUS)) {
                            result = object.get(General.STATUS).getAsInt();
                        } else {
                            result = 11;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(result, startDateText);
    }

    private void showResponses(int status, View view) {
        String message;
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
        } else {
            status = 2;
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
        if (status == 1) {
            onBackPressed();
        }
    }

    // Open teams selector dialog
    @SuppressLint("CommitTransaction")
    private void openTeamSelector() {
        user_id = "0";
        user_name = "";
        selectParticipants.setText("");

        Bundle bundle = new Bundle();
        DialogFragment dialogFrag = new SingleTeamSelectorDialog();
        bundle.putSerializable(General.TEAM_LIST, teamArrayList);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getSupportFragmentManager().beginTransaction(), General.TEAM_LIST);
    }

    // Open users selector dialog
    @SuppressLint("CommitTransaction")
    private void openUsersSelector() {
        group_id = 0;
        group_name = "";
        tv_selectedTeam.setText("");

        Bundle bundle = new Bundle();
        android.app.DialogFragment dialogFrag = new MultiUserSelectorDialog();
        bundle.putSerializable(Actions_.MY_FRIENDS, friendsArrayList);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager().beginTransaction(), Actions_.MY_FRIENDS);
    }

    // Confirmation dialog to switch between team and personal event creation
    private void confirmationDialog(final int type) {
        //type => 1: team, 2:friends
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView title = (TextView) dialog.findViewById(R.id.delete_confirmation_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);
        if (type == 1) {
            subTitle.setText(this.getResources().getString(R.string.create_event_participant));
        } else {
            subTitle.setText(this.getResources().getString(R.string.create_event_team));
        }
        title.setText(getApplicationContext().getResources().getString(R.string.action_confirmation));

        TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (type == 1) {
                    openTeamSelector();
                } else {
                    openUsersSelector();
                }
            }
        });

        AppCompatImageButton cancelButton = (AppCompatImageButton) dialog.findViewById(R.id.delete_confirmation_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        start_time = GetTime.getChatTimestamp();
    }

    @Override
    protected void onResume() {
        //toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        if (!isFromPlanner) {
            if (teamArrayList == null || teamArrayList.size() <= 0) {
                fetchTeamDetails();
            }
        }
        if (friendsArrayList == null || friendsArrayList.size() <= 0) {
            String shared_to_ids = "";
            String my_group_id = String.valueOf(group_id);
            if(group_id > 0){
                friendsArrayList = PerformGetUsersTask.get(Actions_.MY_GROUP_FRIENDS, my_group_id, getApplicationContext(), TAG, this);
            }else {
                friendsArrayList = PerformGetUsersTask.get(Actions_.MY_FRIENDS, shared_to_ids, getApplicationContext(), TAG, this);
            }
        }
        if (locationArrayList == null || locationArrayList.size() <= 0) {
            getLocations();
        }
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_toolbar_save:
                String title = et_title.getText().toString().trim();
                String description = et_description.getText().toString().trim();
                if (validate(title, description)) {
                    postEvent(title, description);
                }
                break;
            case R.id.imageview_createevent_add_team:
                if (teamArrayList == null || teamArrayList.size() <= 0) {
                    ShowSnack.viewWarning(v, this.getResources().getString(R.string.teams_unavailable), getApplicationContext());
                } else {
                    if (user_id.equalsIgnoreCase("0")) {
                        openTeamSelector();
                    } else {
                        confirmationDialog(1);
                    }
                }
                break;
            case R.id.imageview_createevent_add_participant:
                if (friendsArrayList == null || friendsArrayList.size() <= 0) {
                    ShowSnack.viewWarning(v, this.getResources().getString(R.string.friends_unavailable), getApplicationContext());
                } else {
                    if (group_id != 0) {
                        confirmationDialog(2);
                    } else {
                        openUsersSelector();
                    }
                }
                break;
            case R.id.create_event_start_date:
                //open date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                String date_selected = year + "-" + GetCounters.checkDigit(monthOfYear) + "-" + GetCounters.checkDigit(dayOfMonth);
                                try {
                                    endDateText.setText("");
                                    endDate = null;
                                    int result = Compare.startDate(mYear + "-" + (mMonth + 1) + "-" + mDay, date_selected);
                                    if (result == 1) {
                                        startDateText.setText("");
                                        startDate = null;
                                        ShowSnack.textViewWarning(startDateText, getApplicationContext().getResources()
                                                .getString(R.string.invalid_date), getApplicationContext());
                                    } else {
                                        startDate = date_selected;
                                        startDateText.setText(startDate);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.create_event_end_date:
                if (startDate == null || startDate.trim().length() <= 0) {
                    ShowSnack.textViewWarning(endDateText, "Enter Start Date First", getApplicationContext());
                    return;
                }
                DatePickerDialog createPickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                String selected_date = year + "-" + GetCounters.checkDigit(monthOfYear) + "-" + GetCounters.checkDigit(dayOfMonth);
                                int result = Compare.validEndDate(selected_date, startDate);
                                if (result == 1) {
                                    endDate = selected_date;
                                    endDateText.setText(endDate);
                                    int days = Compare.getWorkingDays(startDate, selected_date);
                                    if (days > 0) {
                                        startTimePosition = 0;
                                        sp_endTimeText.setSelection(0);
                                    } else {
                                        startTimePosition = tempStartTimePosition;
                                        sp_endTimeText.setSelection(startTimePosition + 1);
                                    }
                                } else {
                                    endDateText.setText("");
                                    endDate = null;
                                    ShowSnack.textViewWarning(endDateText, getApplicationContext().getResources().getString(R.string.invalid_date), getApplicationContext());
                                }
                            }
                        }, mYear, mMonth, mDay);
                createPickerDialog.show();
                break;
            case R.id.create_event_start_time:
                if (startDate == null || startDate.trim().length() <= 0) {
                    ShowSnack.textViewWarning(endDateText, "Enter Start Date First", getApplicationContext());
                    return;
                }
                break;

            case R.id.create_event_end_time:
                if (startDate == null || startDate.trim().length() <= 0) {
                    ShowSnack.textViewWarning(endDateText, "Enter Start Date First", getApplicationContext());
                    return;
                }
                break;

            case R.id.textview_createevent_location:
                if (locationArrayList != null && locationArrayList.size() > 0) {
                    openLocationSelector();
                } else {
                    ShowSnack.viewWarning(v, this.getResources().getString(R.string.location_unavailable), getApplicationContext());
                }
                break;

            case R.id.imageview_createevent_addlocation:
                openLocationDialog();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        overridePendingTransition(0, 0);
        finish();
    }

    // Get ans set selected teams from users list
    @Override
    public void getChoice(Teams_ teams_, boolean isSelected, int menu_id) {
        if (isSelected) {
            group_id = teams_.getId();
            group_name = teams_.getName();
            tv_selectedTeam.setText(group_name);
        }
    }

    // Get ans set selected users from users list
    @Override
    public void selectedUsers(ArrayList<Friends_> users_arrayList, String selfCareContentId, boolean isSelected) {
        friendsArrayList = users_arrayList;
        if (isSelected) {
            user_id = GetSelected.wallUsers(users_arrayList);
            user_name = GetSelected.wallUsersName(users_arrayList);
            selectParticipants.setText(user_name);
        }
    }

    // make network call to fetch team at a glance
    private void fetchTeamDetails() {
        try {
            teamArrayList = PerformGetTeamsTask.getNormalTeams(Actions_.TEAM_DATA, this, TAG, true, this);

            user_id = "0";
            user_name = "";
            selectParticipants.setText("");

            if(teamArrayList.size()>0) {
                group_id = teamArrayList.get(0).getId();
                group_name = teamArrayList.get(0).getName();
                tv_selectedTeam.setText(group_name);
                for (int i=0; i<teamArrayList.size();i++) {
                    Teams_ objTeam = teamArrayList.get(i);
                    if(String.valueOf(objTeam.getId()).equals(Preferences.get(General.GROUP_ID))) {
                        group_id = objTeam.getId();
                        group_name = objTeam.getName();
                        tv_selectedTeam.setText(group_name);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // make network call to prefetch location list
    private void getLocations() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_LOCATION);
        requestMap.put(General.USER_ID, String.valueOf(user_id));
        requestMap.put(General.GROUP_ID, String.valueOf(group_id));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CALENDAR;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this.getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this.getApplicationContext(), this);
                if (response != null) {
                    locationArrayList = Events_.parseGetLocations(response, Actions_.GET_LOCATION, this.getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getChoice(Location_ location_, boolean isSelected, int menu_id) {
        if (isSelected) {
            location_id = location_.getId();
            location_name = location_.getName();
            textViewLocation.setText(location_name);
        }
    }

    // Open users selector dialog
    @SuppressLint("CommitTransaction")
    private void openLocationSelector() {
        getLocations();

        Bundle bundle = new Bundle();
        DialogFragment dialogFrag = new LocationSelectorDialog();
        bundle.putSerializable(General.LOCATION, locationArrayList);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getSupportFragmentManager().beginTransaction(), General.LOCATION);
    }

    private void openLocationDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_location);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextInputEditText editTextLocationName = (TextInputEditText) dialog.findViewById(R.id.edittext_location_name);
        final TextInputEditText editTextLocationDescription = (TextInputEditText) dialog.findViewById(R.id.edittext_location_description);

        TextView okButton = (TextView) dialog.findViewById(R.id.textview_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextLocationName.length() == 0) {
                    editTextLocationName.setError("Invalid Location Name");
                    //return;
                } else if (editTextLocationName.length() < 3) {
                    editTextLocationName.setError("Min 3 char required");
                    //return;
                } else if (editTextLocationName.length() > 150) {
                    editTextLocationName.setError("Max 150 char required");
                    //return;
                } else {
                    addLocations(editTextLocationName.getText().toString().trim(), editTextLocationDescription.getText().toString().trim(), dialog);
                }
            }
        });

        AppCompatImageButton cancelButton = (AppCompatImageButton) dialog.findViewById(R.id.imageview_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // make network call to prefetch location list
    private void addLocations(String locationName, String locationDescription, Dialog dialog) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ADD_LOCATION);
        requestMap.put(General.USER_ID, String.valueOf(user_id));
        requestMap.put(General.GROUP_ID, String.valueOf(group_id));
        requestMap.put(General.NAME, locationName);
        requestMap.put(General.DESCRIPTION, locationDescription);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CALENDAR;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this.getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this.getApplicationContext(), this);
                if (response != null) {
                    if (response == null) {
                        ShowToast.toast(GetErrorResources.getMessage(12, getApplicationContext()), getApplicationContext());
                        return;
                    }
                    if (Error_.oauth(response, CreateEventActivity.this) == 13) {
                        ShowToast.toast(GetErrorResources.getMessage(13, getApplicationContext()), getApplicationContext());
                        return;
                    }
                    if (Error_.noData(response, Actions_.ADD_LOCATION, CreateEventActivity.this) == 2) {
                        ShowToast.toast(GetErrorResources.getMessage(2, getApplicationContext()), getApplicationContext());
                        return;
                    }
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.ADD_LOCATION);
                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Location_>>() {
                        }.getType();
                        ArrayList<Location_> addLocationArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.ADD_LOCATION).toString(), listType);
                        if (addLocationArrayList.get(0).getStatus() == 1) {
                            if (addLocationArrayList.get(0).getId() > 0) {
                                location_id = addLocationArrayList.get(0).getId();
                                textViewLocation.setText(locationName);
                                ShowToast.toast(getResources().getString(R.string.location_added_successfully), getApplicationContext());
                                getLocations();
                            }
                        }
                    } else {
                        ShowToast.toast(GetErrorResources.getMessage(11, getApplicationContext()), getApplicationContext());
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dialog.dismiss();
    }
}
