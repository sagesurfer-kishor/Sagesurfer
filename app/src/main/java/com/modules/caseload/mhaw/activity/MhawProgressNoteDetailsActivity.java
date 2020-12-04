package com.modules.caseload.mhaw.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.modules.caseload.mhaw.model.MhawProgressList;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.MhawProgressList_;
import com.storage.preferences.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.RequestBody;

public class MhawProgressNoteDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MhawProgressNoteDetailsActivity.class.getSimpleName();
    private MhawProgressList progressList;
    private Toolbar toolbar;
    private TextView mTxtTitleName, mTxtLocationValue, mTxtProvidedValue, mTxtGoalValue, mTxtObjValue;
    private TextView mTxtProgressValue, mTxtReasonValue, mTxtBarriersValue, mTxtHomeWorkValue, mTxtMeetingValue, mTxtFinalizeValue;
    private ImageView imageViewEdit, imageViewDelete;
    private TextView date, duration, attendance, weeklyTheme, notes, reason;
    private LinearLayout reasonLayout;
    private Long refId;
    private ArrayList<MhawProgressList> progressArryList;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_mhaw_progress_note_details);

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

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setText(getResources().getString(R.string.view_progress_note));

        initUI();

        Intent data = getIntent();
        if (data.hasExtra(General.NOTE_DETAILS)) {
            progressList = (MhawProgressList) data.getSerializableExtra(General.NOTE_DETAILS);
            callDetailsAPI(Long.valueOf(progressList.getId()));
//            setData(progressList);
        } else if (data.hasExtra(General.NOTE_FROM_NOTIFICATION)) {
            refId = data.getLongExtra(General.NOTE_FROM_NOTIFICATION, 0);
            callDetailsAPI(refId);
        } else {
            onBackPressed();
        }



    }

    private void callDetailsAPI(Long refId) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_VIEW_PROGRESS_NOTE_MHAW);
        requestMap.put(General.ID, String.valueOf(refId));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        Log.e("RequestMap",requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("MhawDetailResponse", response);
                if (response != null) {
                    progressArryList = MhawProgressList_.parseProgressList(response, Actions_.GET_VIEW_PROGRESS_NOTE_MHAW, this, TAG);
                    progressList = progressArryList.get(0);
                    setData(progressArryList.get(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initUI() {
//        moodType = (TextView) findViewById(R.id.mood_type);
        mTxtTitleName = (TextView) findViewById(R.id.txt_title_name);
        mTxtLocationValue = (TextView) findViewById(R.id.txt_location_value);
        mTxtProvidedValue = (TextView) findViewById(R.id.txt_provided_value);
        mTxtGoalValue = (TextView) findViewById(R.id.txt_goal_value);
        mTxtObjValue = (TextView) findViewById(R.id.txt_obj_value);
        mTxtProgressValue = (TextView) findViewById(R.id.txt_progress_value);
        mTxtReasonValue = (TextView) findViewById(R.id.txt_reason_value);
        mTxtBarriersValue = (TextView) findViewById(R.id.txt_barriers_value);
        mTxtHomeWorkValue = (TextView) findViewById(R.id.txt_homework_value);
        mTxtMeetingValue = (TextView) findViewById(R.id.txt_meeting_value);
        mTxtFinalizeValue = (TextView) findViewById(R.id.txt_finalize_value);
//        moodImage = (ImageView) findViewById(R.id.mood_image);
        reasonLayout = (LinearLayout) findViewById(R.id.reason_layout);

        imageViewEdit = (ImageView) findViewById(R.id.imageview_edit);
        imageViewDelete = (ImageView) findViewById(R.id.imageview_delete);
        imageViewEdit.setOnClickListener(this);
        imageViewDelete.setOnClickListener(this);

       /* if (CheckRole.EDDOIcon(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            imageViewEdit.setVisibility(View.VISIBLE);
            imageViewDelete.setVisibility(View.VISIBLE);
        } else {
            imageViewEdit.setVisibility(View.GONE);
            imageViewDelete.setVisibility(View.GONE);
        }*/

    }

    @SuppressLint("SetTextI18n")
    private void setData(MhawProgressList progressList) {
//        moodType.setText(ChangeCase.toTitleCase(progressList.getMood()));
//        postedDate.setText(getDate(progressList.getPosted_date()));
//        addedBy.setText(ChangeCase.toTitleCase(progressList.getAdded_by_name()) + " (" + ChangeCase.toTitleCase(progressList.getAdded_by_role()) + ")");
//        date.setText(dateCaps(progressList.getDate()));
//        duration.setText(progressList.getTotal_time().substring(0, 5));
//        attendance.setText(ChangeCase.toTitleCase(progressList.getAttendance()));
//        notes.setText(ChangeCase.toTitleCase(progressList.getNotes()));
//        weeklyTheme.setText(ChangeCase.toTitleCase(progressList.getWeekly_theme()));
//
//        if (progressList.getAttendance().equalsIgnoreCase("Declined Session")) {
//            reasonLayout.setVisibility(View.VISIBLE);
//            reason.setText(progressList.getReason());
//        } else {
//            reasonLayout.setVisibility(View.GONE);
//        }

        //moodType.setTextColor(Color.parseColor(progressList.getMood_color()));

//        Glide.with(this)
//                .load(progressList.getMood_image())
//                .thumbnail(0.5f)
//                .transition(withCrossFade())
//                .apply(new RequestOptions()
//                        .placeholder(GetThumbnails.userIcon(progressList.getMood_image())))
//                .into(moodImage);

        /*This condition is for show or hide delete and edit icon as per User Roll */
        if (progressList.getAdded_by_id() == Integer.parseInt(Preferences.get(General.USER_ID))) {
            if (progressList.getFinalize_note() == 1) {
                imageViewEdit.setVisibility(View.GONE);
                imageViewDelete.setVisibility(View.VISIBLE);
            } else {
                imageViewEdit.setVisibility(View.VISIBLE);
                imageViewDelete.setVisibility(View.VISIBLE);
            }

        }else {
            imageViewEdit.setVisibility(View.GONE);
            imageViewDelete.setVisibility(View.GONE);
        }

        mTxtTitleName.setText(ChangeCase.toTitleCase(progressList.getTitle()));
        mTxtLocationValue.setText(ChangeCase.toTitleCase(progressList.getService_address()));
        mTxtProvidedValue.setText(ChangeCase.toTitleCase(progressList.getService()));
        mTxtGoalValue.setText(ChangeCase.toTitleCase(progressList.getGoal_address()));
        mTxtObjValue.setText(ChangeCase.toTitleCase(progressList.getObj_address()));
        mTxtProgressValue.setText(ChangeCase.toTitleCase(progressList.getProgress_address()));
        mTxtReasonValue.setText(ChangeCase.toTitleCase(progressList.getReason()));
        mTxtBarriersValue.setText(ChangeCase.toTitleCase(progressList.getBarriers_presented()));
        mTxtHomeWorkValue.setText(ChangeCase.toTitleCase(progressList.getHome_work()));
        mTxtMeetingValue.setText(dateCaps(progressList.getMeeting_date()) + ", " + getTime(ChangeCase.toSentenceCase(progressList.getMeeting_time())));

        if (progressList.getFinalize_note() == 1) {
            mTxtFinalizeValue.setText(ChangeCase.toTitleCase("Finalize"));
        } else {
            mTxtFinalizeValue.setText(ChangeCase.toTitleCase("Open"));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }

    private String getTime(String time) {
//        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date newdate = null;
        try {
            newdate = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String formattedDate = sdf.format(newdate);
        return formattedDate;
    }

    @SuppressLint("SimpleDateFormat")
    private String dateCaps(String dateValue) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = spf.parse(dateValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("MMM dd, yyyy");
        String date = spf.format(newDate);
        return date;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_edit:
                Intent noteEdit = new Intent(this, MhawAddProgressNoteActivity.class);
                noteEdit.putExtra(Actions_.UPDATE_PROGRESS_NOTE, progressList);
                noteEdit.putExtra(Actions_.UPDATE_NOTE, Actions_.UPDATE_NOTE);
                startActivity(noteEdit);
                finish();
                break;

            case R.id.imageview_delete:
                deleteProgressNoteAPI();
                break;
        }
    }

    private void deleteProgressNoteAPI() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE_PROGRESS_NOTE_MHAW);
        requestMap.put(General.ID, String.valueOf(progressList.getId()));
        requestMap.put(General.USER_ID, Preferences.get(General.NOTE_USER_ID));
        requestMap.put(General.YOUTH_ID, "25");

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonDeleteNote = jsonObject.getAsJsonObject(Actions_.DELETE_PROGRESS_NOTE_MHAW);
                    if (jsonDeleteNote.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(this, jsonDeleteNote.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
