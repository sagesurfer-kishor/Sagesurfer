package com.modules.caseload;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
 import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.Config;
import com.firebase.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessaging;
import com.modules.selfgoal.TimeHourPickerDialogFragment;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.Broadcast;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.GoalDetailsInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.services.CounterService;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.AddGoalPreferences;
import com.storage.preferences.Preferences;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;

/**
 * Created by Monika on 7/24/2018.
 */

public class PeerAddNoteActivity extends AppCompatActivity implements View.OnClickListener, GoalDetailsInterface {
    private static final String TAG = PeerAddNoteActivity.class.getSimpleName();

    @BindView(R.id.caseload_add_note_title)
    TextView titleToolbarText;
    @BindView(R.id.linearlayout_save_draft)
    LinearLayout linearLayoutSaveDraft;
    @BindView(R.id.imageview_save_draft)
    AppCompatImageView imageViewSaveDraft;
    @BindView(R.id.imageview_toolbar_save)
    AppCompatImageView imageViewSave;
    @BindView(R.id.edittext_peer_subject)
    EditText editTextSubject;
    @BindView(R.id.textview_peer_note_date)
    TextView dateText;
    @BindView(R.id.textview_peer_note_time)
    TextView timeText;
    @BindView(R.id.textview_peer_note_duration)
    TextView durationText;
    @BindView(R.id.spinner_type_of_contact)
    Spinner spinnerTypeOfContact;
    @BindView(R.id.edittext_peer_next_steps_notes)
    EditText editTextNextStepsNotes;
    @BindView(R.id.edittext_peer_resources_needed)
    EditText editTextResourcesNeeded;
    @BindView(R.id.edittext_peer_note)
    EditText editTextPeerNote;

    private LinearLayout startDate;

    public String date = "", subject = "", time = "", type_of_contact = "", duration = "", next_steps = "", resources_needed = "", notes = "", action = "";
    Toolbar toolbar;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String hour = "01", minute = "00", unit = "AM";
    public static PeerAddNoteActivity peerAddNoteActivity;
    private Calendar calendar;
    private boolean isTimeSelected = false;
    private long note_id = 0;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Intent intent;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_caseload_peer_add_note);
        ButterKnife.bind(this);

        peerAddNoteActivity = this;
        AddGoalPreferences.initialize(PeerAddNoteActivity.this);
        toolbar = (Toolbar) findViewById(R.id.caseload_peer_add_note_toolbar);
        //toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.screen_background));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_cancel_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        int color = Color.parseColor("#a5a5a5"); //text_color_tertiary
        imageViewSaveDraft.setColorFilter(color);
        imageViewSaveDraft.setImageResource(R.drawable.vi_save_file);

        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        ArrayAdapter<CharSequence> adapterTypeOfContact = ArrayAdapter.createFromResource(getApplicationContext(), R.array.peer_type_of_contact, R.layout.drop_down_selected_text_item_layout);
        adapterTypeOfContact.setDropDownViewResource(R.layout.drop_down_text_item_layout);
        spinnerTypeOfContact.setAdapter(adapterTypeOfContact);

        startDate = findViewById(R.id.time_dialog);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTimeSelected = true;
                openChoiceDialog();
            }
        });

        Intent data = getIntent();
        if (data != null && data.hasExtra(Actions_.EDIT)) {
            action = data.getStringExtra(Actions_.EDIT);
            note_id = data.getLongExtra(General.NOTE_ID, 0);
            subject = data.getStringExtra(General.SUBJECT);
            date = data.getStringExtra(General.DATE);
            time = data.getStringExtra(General.TIME);
            type_of_contact = data.getStringExtra(General.CONTACT_TYPE);
            type_of_contact = getTypeofContact(type_of_contact);
            duration = data.getStringExtra(General.DURATION);
            next_steps = data.getStringExtra(General.NEXT_STEP_NOTES);
            resources_needed = data.getStringExtra(General.RESOURCES_NEEDED);
            notes = data.getStringExtra(General.NOTES);

            titleToolbarText.setText(getResources().getString(R.string.edit_note));
            editTextSubject.setText(subject);
            dateText.setText(date);
            timeText.setText(time.substring(0, 5) + " " + time.substring(9, 11));
            spinnerTypeOfContact.setSelection(Integer.valueOf(type_of_contact) - 1);
            durationText.setText(duration.substring(0, 5));
            editTextNextStepsNotes.setText(next_steps);
            editTextResourcesNeeded.setText(resources_needed);
            editTextPeerNote.setText(notes);

            imageViewSave.setOnClickListener(this);

            if (action.equalsIgnoreCase(Actions_.GET_REJECTED)) {
                editTextSubject.setFocusable(false);
                editTextSubject.setEnabled(false);
                dateText.setFocusable(false);
                dateText.setEnabled(false);
                timeText.setFocusable(false);
                timeText.setEnabled(false);
                spinnerTypeOfContact.setClickable(false);
                spinnerTypeOfContact.setEnabled(false);
                spinnerTypeOfContact.setFocusable(false);
                durationText.setFocusable(false);
                durationText.setEnabled(false);
            } else if (action.equalsIgnoreCase(Actions_.GET_DRAFT)) {
                linearLayoutSaveDraft.setVisibility(View.VISIBLE);
                linearLayoutSaveDraft.setOnClickListener(this);
                dateText.setOnClickListener(this);
                timeText.setOnClickListener(this);
                durationText.setOnClickListener(this);
            }
        } else {
            titleToolbarText.setText(getResources().getString(R.string.add_note));
            linearLayoutSaveDraft.setVisibility(View.VISIBLE);
            linearLayoutSaveDraft.setOnClickListener(this);
            imageViewSave.setOnClickListener(this);
            dateText.setOnClickListener(this);
            timeText.setOnClickListener(this);
            durationText.setOnClickListener(this);
            AddGoalPreferences.save(General.START_MINUTE, minute, TAG);
            AddGoalPreferences.save(General.START_HOUR, hour, TAG);
            AddGoalPreferences.save(General.TIME_UNIT, unit, TAG);
            spinnerTypeOfContact.setOnItemSelectedListener(onItemSelected);
        }

        editTextNextStepsNotes.setHorizontallyScrolling(false);
        editTextNextStepsNotes.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    editTextNextStepsNotes.clearFocus();
                    editTextResourcesNeeded.requestFocus();
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editTextNextStepsNotes.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        editTextResourcesNeeded.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    editTextResourcesNeeded.clearFocus();
                    editTextPeerNote.requestFocus();
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editTextResourcesNeeded.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        editTextPeerNote.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editTextPeerNote.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        editTextNextStepsNotes.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    editTextNextStepsNotes.clearFocus();
                    editTextResourcesNeeded.requestFocus();
                    return true;
                }
                return false;
            }
        });
        editTextResourcesNeeded.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    editTextResourcesNeeded.clearFocus();
                    editTextPeerNote.requestFocus();
                    return true;
                }
                return false;
            }
        });
        editTextPeerNote.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        intent = new Intent(this, CounterService.class);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                assert intent.getAction() != null;
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {

                    if (!Preferences.getBoolean(General.IS_PUSH_NOTIFICATION_SENT)) {

                        NotificationUtils.clearNotifications(getApplicationContext());

                        for (Map.Entry<String, List<String>> entry : Config.mapOfPosts.entrySet()) {
                            String key = entry.getKey();
                            Preferences.save(General.IS_PUSH_NOTIFICATION_SENT, true);
                            showNotificationMessage(entry.getValue().get(0), entry.getValue().get(1), entry.getValue().get(2), entry.getKey());
                        }
                    }
                }
            }
        };
    }

    // show notification if push is received
    private void showNotificationMessage(String title, String message, String type, String timestamp) {
        Intent resultIntent = new Intent(getApplicationContext(), PeerAddNoteActivity.class);
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        resultIntent.putExtra(General.MESSAGE, message);
        resultIntent.putExtra(General.TIMESTAMP, timestamp);
        resultIntent.putExtra(General.TITLE, title);
        resultIntent.putExtra(General.TYPE, type);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timestamp, type, resultIntent);
    }


    private final AdapterView.OnItemSelectedListener onItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerTypeOfContact.setSelection(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (action.equalsIgnoreCase(Actions_.GET_REJECTED) || action.equalsIgnoreCase(Actions_.GET_DRAFT)) {
            Intent detailsIntent = new Intent(getApplicationContext(), CaseloadProgressNoteActivity.class);
            startActivity(detailsIntent);
            overridePendingTransition(0, 0);
        } else {
            this.overridePendingTransition(0, 0);
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startService(intent);
        registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Broadcast.COUNTER_BROADCAST));
        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        unregisterReceiver(mRegistrationBroadcastReceiver);
        stopService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_peer_note_date:
                DatePickerDialog datePickerDialog = new DatePickerDialog(PeerAddNoteActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                try {
                                    dateText.setText(date);
                                    checkTime();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
                break;

            case R.id.textview_peer_note_time:
                isTimeSelected = true;
                openChoiceDialog();
                break;

            case R.id.textview_peer_note_duration:
                isTimeSelected = false;
                openChoiceDialog();
                break;

            case R.id.linearlayout_save_draft:
                if (action.equalsIgnoreCase(Actions_.GET_DRAFT)) {
                    String contactType = getTypeofContact(spinnerTypeOfContact.getSelectedItem().toString());
                    String subjectOne = editTextSubject.getText().toString().trim();
                    String dateOne = dateText.getText().toString().trim();
                    String timeOne = timeText.getText().toString().trim();
                    String durationOne = durationText.getText().toString().trim();
                    String next_stepsOne = editTextNextStepsNotes.getText().toString().trim();
                    String resources_neededOne = editTextResourcesNeeded.getText().toString().trim();
                    String notesOne = editTextPeerNote.getText().toString().trim();

                    if (subjectOne.equalsIgnoreCase(subject) && dateOne.equalsIgnoreCase(date) /*&& timeOne.equalsIgnoreCase(time)
                            && durationOne.equalsIgnoreCase(duration)*/
                            && contactType.equalsIgnoreCase(type_of_contact)
                            && next_stepsOne.equalsIgnoreCase(next_steps)
                            && resources_neededOne.equalsIgnoreCase(resources_needed)
                            && notesOne.equalsIgnoreCase(notes)) {
                        showEditNotChangedDialog(getResources().getString(R.string.edit_not_changed_anything_draft_msg), Actions_.DRAFT_UPDATE);
                    } else if (subjectOne.equalsIgnoreCase(subject) || dateOne.equalsIgnoreCase(date) || timeOne.equalsIgnoreCase(time)
                            || durationOne.equalsIgnoreCase(duration)
                            || contactType.equalsIgnoreCase(type_of_contact)
                            || next_stepsOne.equalsIgnoreCase(next_steps)
                            || resources_neededOne.equalsIgnoreCase(resources_needed)
                            || notesOne.equalsIgnoreCase(notes)) {

                        if (validate()) {
                            addPeerNoteCall(Actions_.DRAFT_UPDATE);
                        }
                    }
                } else {
                    if (validate()) {
                        addPeerNote(Actions_.SAVE_DRAFT);
                    }
                }
                break;

            case R.id.imageview_toolbar_save:
                if (action.equalsIgnoreCase(Actions_.GET_REJECTED) || action.equalsIgnoreCase(Actions_.GET_DRAFT)) {
                    String contactType = getTypeofContact(spinnerTypeOfContact.getSelectedItem().toString());
                    if (/*editTextSubject.getText().toString().trim().equalsIgnoreCase(subject)
                            && dateText.getText().toString().trim().equalsIgnoreCase(date)
                            || timeText.getText().toString().trim().equalsIgnoreCase(time)
                            || durationText.getText().toString().trim().equalsIgnoreCase(duration)
                            && contactType.equalsIgnoreCase(type_of_contact)
                            &&*/ editTextNextStepsNotes.getText().toString().trim().equalsIgnoreCase(next_steps)
                            && editTextResourcesNeeded.getText().toString().trim().equalsIgnoreCase(resources_needed)
                            && editTextPeerNote.getText().toString().trim().equalsIgnoreCase(notes)) {
                        showEditNotChangedDialog(getResources().getString(R.string.edit_not_changed_anything_submit_msg), Actions_.REJECTED_UPDATE);
                    } else if (/*editTextSubject.getText().toString().trim().equalsIgnoreCase(subject)
                            && dateText.getText().toString().trim().equalsIgnoreCase(date)
                            && timeText.getText().toString().trim().equalsIgnoreCase(time)
                            && durationText.getText().toString().trim().equalsIgnoreCase(duration)
                            && contactType.equalsIgnoreCase(type_of_contact)
                            &&*/ editTextNextStepsNotes.getText().toString().trim().equalsIgnoreCase(next_steps)
                            || editTextResourcesNeeded.getText().toString().trim().equalsIgnoreCase(resources_needed)
                            || editTextPeerNote.getText().toString().trim().equalsIgnoreCase(notes)) {
                        if (validate()) {
                            addPeerNoteCall(Actions_.REJECTED_UPDATE);
                        }
                    } else {
                        if (validate()) {
                            addPeerNoteCall(Actions_.REJECTED_UPDATE);
                        }
                    }
                } else {
                    if (validate()) {
                        showEditNotChangedDialog(getResources().getString(R.string.are_you_sure_you_want_to_submit_this_note), Actions_.SAVE_NOTES);
                    }
                }
                break;
        }
    }

    public void setTime(String _hour, String _minute, String _unit) {
        String time = _hour + ":" + _minute;
        if (isTimeSelected) {
            time = time + ":" + "00" + " " + _unit;
            timeText.setText(time);
            checkTime();
        } else {
            durationText.setText(time);
        }
    }

    public void checkTime() {
        String currentUnit;
        Calendar c = Calendar.getInstance();
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        int currentYear = c.get(Calendar.YEAR);
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMin = c.get(Calendar.MINUTE);
        int ds = c.get(Calendar.AM_PM);
        if (ds == 0)
            currentUnit = "AM";
        else
            currentUnit = "PM";
        String currentDateTime = currentYear + "-" + currentMonth + "-" + currentDay + " " + currentHour + ":" + currentMin + ":" + "00" + " " + currentUnit;
        if (dateText.getText().toString().length() > 0) {
            try {
                if (timeText.getText().toString().length() > 0) {
                    String dateTime = dateText.getText().toString().trim() + " " + timeText.getText().toString().trim();
                    Date time1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa").parse(dateTime); //startTime
                    Date time2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa").parse(currentDateTime); //endTime
                    if (time1.before(time2)) {
                        timeText.setError(null);
                    } else if (time1.after(time2)) {
                        timeText.setError(this.getResources().getString(R.string.please_do_not_add_future_time));
                    } else {
                        timeText.setError(null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {//2018-11-1 12:11:00 AM,2018-11-1 06:11:00 PM  2018-11-01 12:11:00 PM
                String dateTime = currentYear + "-" + currentMonth + "-" + currentDay + " " + timeText.getText().toString().trim();
                Date time1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa").parse(dateTime); //startTime
                Date time2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa").parse(currentDateTime); //endTime
                if (time1.after(time2)) {
                    timeText.setError(this.getResources().getString(R.string.please_do_not_add_future_time));
                } else if (time1.before(time2)) {
                    timeText.setError(this.getResources().getString(R.string.please_do_not_add_future_time));
                } else {
                    timeText.setError(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("CommitTransaction")
    private void openChoiceDialog() {
        Bundle bundle = new Bundle();
        TimeHourPickerDialogFragment dialogFrag = new TimeHourPickerDialogFragment();
        if (isTimeSelected) {
            bundle.putString(General.FREQUENCY, "details");
            bundle.putString(General.DESCRIPTION, "1");
            bundle.putString(General.IS_FROM_CASELOAD, "time");
        } else {
            bundle.putString(General.FREQUENCY, "Hour");
            bundle.putString(General.IS_FROM_CASELOAD, "duration");
            bundle.putString(General.SHOW_UNIT, "false");
        }

        dialogFrag.setArguments(bundle);
        dialogFrag.show(getSupportFragmentManager().beginTransaction(), General.TIMESTAMP);
    }

    private boolean validate() {
        if (editTextSubject.getText().toString().trim().length() <= 0) {
            ShowSnack.viewWarning(editTextSubject, getResources().getString(R.string.subject_is_compulsory), getApplicationContext());
            return false;
        }
        if (date.length() <= 0) {
            ShowSnack.viewWarning(dateText, getResources().getString(R.string.date_is_compulsory), getApplicationContext());
            return false;
        }

        if (timeText == null || timeText.getText().toString().trim().length() <= 0 || timeText.getError() != null) {
            ShowSnack.viewWarning(timeText, getResources().getString(R.string.please_do_not_add_future_time), getApplicationContext());
            return false;
        }

        if (durationText.getText().toString().trim().length() <= 0) {
            ShowSnack.viewWarning(durationText, getResources().getString(R.string.duration_is_compulsory), getApplicationContext());
            return false;
        }
        if (spinnerTypeOfContact.getSelectedItem().toString().equalsIgnoreCase(getResources().getString(R.string.select_type_of_contact))) {
            ShowSnack.viewWarning(spinnerTypeOfContact, getResources().getString(R.string.type_of_contact_is_compulsory), getApplicationContext());
            return false;
        }
        if (editTextNextStepsNotes.getText().toString().trim().length() <= 0) {
            ShowSnack.viewWarning(editTextNextStepsNotes, getResources().getString(R.string.next_steps_are_compulsory), getApplicationContext());
            return false;
        }
        if (editTextResourcesNeeded.getText().toString().trim().length() <= 0) {
            ShowSnack.viewWarning(editTextResourcesNeeded, getResources().getString(R.string.resources_needed_is_compulsory), getApplicationContext());
            return false;
        }
        if (editTextPeerNote.getText().toString().trim().length() <= 0) {
            ShowSnack.viewWarning(editTextPeerNote, getResources().getString(R.string.note_is_compulsory), getApplicationContext());
            return false;
        }

        return true;
    }

    //make network call to delete a note
    private void addPeerNote(String action) {
        String[] time = timeText.getText().toString().trim().split(" ");
        String contact = getTypeofContact(spinnerTypeOfContact.getSelectedItem().toString());
        String note = editTextPeerNote.getText().toString().trim();

        HashMap<String, String> requestMap = new HashMap<>();
        if (action.equalsIgnoreCase(Actions_.REJECTED_UPDATE) || action.equalsIgnoreCase(Actions_.DRAFT_UPDATE)) {
            requestMap.put(General.NOTE_ID, String.valueOf(note_id));
        }
        requestMap.put(General.ACTION, action);
        requestMap.put(General.LOGIN_USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.CUST_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.SUBJECT, editTextSubject.getText().toString().trim());
        requestMap.put(General.TIMESTAMP, dateText.getText().toString().trim());
        requestMap.put(General.TIME, time[0]);
        requestMap.put(General.CONTACT, contact);
        requestMap.put(General.DURATION, durationText.getText().toString().trim());
        requestMap.put(General.NEXT_STEP_NOTES, editTextNextStepsNotes.getText().toString().trim());
        requestMap.put(General.RESOURCES_NEEDED, editTextResourcesNeeded.getText().toString().trim());
        requestMap.put(General.NOTE, note);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.ADD_NOTES_API;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 1) {
                        showError(false, 1, action);
                    } else if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 4) { //status=4 for Mature Mentor
                        showError(false, 4, action);
                    } else {
                        showError(true, jsonObject.getInt(General.STATUS), "");
                    }
                } else {
                    showError(true, 11, "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showError(boolean isError, int status, String action) {
        if (isError) {
            ShowToast.successful(GetErrorResources.getMessage(status, getApplicationContext()), getApplicationContext());
        } else {
            if (action.equalsIgnoreCase(Actions_.GET_DRAFT) || action.equalsIgnoreCase(Actions_.SAVE_DRAFT) || action.equalsIgnoreCase(Actions_.DRAFT_UPDATE)) {
                ShowToast.successful(getResources().getString(R.string.note_saved_as_draft_successfully), getApplicationContext());
            } else {
                if (status == 1) {
                    ShowToast.successful(getResources().getString(R.string.note_submitted_for_approval_successfully), getApplicationContext());
                } else {
                    ShowToast.successful(getResources().getString(R.string.note_submitted_for_successfully), getApplicationContext());
                }
            }
            onBackPressed();
        }
    }

    @Override
    public void setCountTime(String time, String unit) {
        timeText.setText(time.substring(0, 5) + " " + unit);
    }

    @Override
    public void setDurationTime(String time) {
        durationText.setText(time);
    }

    public String getTypeofContact(String type_of_contact) {
        if (type_of_contact != null) {
            if (type_of_contact.equalsIgnoreCase(getResources().getString(R.string.face_to_face_meeting))) {
                return "1";
            } else if (type_of_contact.equalsIgnoreCase(getResources().getString(R.string.phone_conversation))) {
                return "2";
            } else if (type_of_contact.equalsIgnoreCase(getResources().getString(R.string.appointment_support))) {
                return "3";
            } else if (type_of_contact.equalsIgnoreCase(getResources().getString(R.string.community_resource_engagement))) {
                return "4";
            } else if (type_of_contact.equalsIgnoreCase(getResources().getString(R.string.community_event))) {
                return "5";
            } else if (type_of_contact.equalsIgnoreCase(getResources().getString(R.string.sagesurfer))) {
                return "6";
            } else if (type_of_contact.equalsIgnoreCase(getResources().getString(R.string.text))) {
                return "7";
            } else if (type_of_contact.equalsIgnoreCase(getResources().getString(R.string.email))) {
                return "8";
            }
        }
        return "0";
    }

    //open Reject Comment Dialog with available reasons to a reject note
    private void showEditNotChangedDialog(String message, final String action) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_peer_note_edit);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView textViewMsg = (TextView) dialog.findViewById(R.id.textview_msg);
        final Button buttonYes = (Button) dialog.findViewById(R.id.button_yes);
        final ImageButton buttonCancel = (ImageButton) dialog.findViewById(R.id.button_close);

        textViewMsg.setText(message);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                addPeerNoteCall(action);
            }
        });

        dialog.show();
    }

    public void addPeerNoteCall(String action) {
        addPeerNote(action);
    }
}
