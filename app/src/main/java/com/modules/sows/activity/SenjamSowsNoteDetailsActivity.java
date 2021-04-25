package com.modules.sows.activity;

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
import com.modules.caseload.senjam.model.SenjamListModel;
import com.modules.sows.model.SowsNotes;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.SenjamDoctorNoteList_;
import com.storage.preferences.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.RequestBody;

public class SenjamSowsNoteDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = SenjamSowsNoteDetailsActivity.class.getSimpleName();
    private SenjamListModel senjamListModel;
    private Toolbar toolbar;
    private ImageView moodImage, imageViewEdit, imageViewDelete;
    private TextView mTextViewDate, mTextViewTime, mTextViewPatientName, mTextViewSubject, mTextViewDescription, mTextViewTitle, mTextViewLastDate, mTextViewAddedDate;
    private LinearLayout reasonLayout;
    private Long refId;
    public ArrayList<SowsNotes> sowsNotesArrayList = new ArrayList<>();
    private SowsNotes listModel;
    private String mPatientName;
    private String sowsID;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_sows_note_detail);

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
        titleText.setText(getResources().getString(R.string.notes_details));

        initUI();

        // get Data from previous Activity
        // condition will be define sow list activity called Api for note Deyails
        Intent data = getIntent();
        if (data.hasExtra(General.SOWS_DETAILS)) {
            listModel = (SowsNotes) data.getSerializableExtra(General.SOWS_DETAILS);
            assert listModel != null;
            sowsID = listModel.getId();
            getNoteDetailsAPI(sowsID);
        }
//        else if (data.hasExtra(General.SOWS_FROM_NOTIFICATION)) {
//            refId = data.getLongExtra(General.SOWS_FROM_NOTIFICATION, 0);
//            noteProgressListAPICalled(String.valueOf(refId));
//        }
        else {
            onBackPressed();
        }

    }

    /*Api call for Note Details*/
    @SuppressLint("LongLogTag")
    private void getNoteDetailsAPI(String ID) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_JOURNAL_DETAILS);
        requestMap.put(General.ID, ID);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        Log.e("RequestMap",requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_JOURNALING;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("DoctorNotesDetailsResponse",response);
                if (response != null) {
                        sowsNotesArrayList = SenjamDoctorNoteList_.parseSowsNoteList(response, Actions_.GET_JOURNAL_DETAILS, this, TAG);
                    if (sowsNotesArrayList.get(0).getStatus() == 1){
                        listModel = sowsNotesArrayList.get(0);

                        // Notes Details Data will be set Here if Status will be 1
                        setData(sowsNotesArrayList.get(0));
                    }else{
                        Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Variable Declaration Function
    private void initUI() {
        mTextViewDate = (TextView) findViewById(R.id.select_date_txt);
        mTextViewTime = (TextView) findViewById(R.id.select_time_txt);
        mTextViewSubject = (TextView) findViewById(R.id.subject_txt);
        mTextViewDescription = (TextView) findViewById(R.id.desc_txt);

        mTextViewAddedDate = (TextView) findViewById(R.id.txt_added_date);
        mTextViewLastDate = (TextView) findViewById(R.id.txt_last_date);
        mTextViewTitle = (TextView) findViewById(R.id.title_txt);


        moodImage = (ImageView) findViewById(R.id.mood_image);
        reasonLayout = (LinearLayout) findViewById(R.id.reason_layout);

        imageViewEdit = (ImageView) findViewById(R.id.imageview_edit);
        imageViewDelete = (ImageView) findViewById(R.id.imageview_delete);
        imageViewEdit.setOnClickListener(this);
        imageViewDelete.setOnClickListener(this);


        mEditDeleteShowHideFunction();

    }

    // Note Details Data Set Function
    @SuppressLint("SetTextI18n")
    private void setData(SowsNotes senjamList) {
        mTextViewTitle.setText(senjamList.getTitle());
        mTextViewSubject.setText(senjamList.getSubject());
        mTextViewDate.setText(senjamList.getDate());
        mTextViewTime.setText(getTime(senjamList.getTime()));
        mTextViewDescription.setText(senjamList.getDescription());
        mTextViewAddedDate.setText(getDate(senjamList.getDb_add_date()));
        mTextViewLastDate.setText(getDate(senjamList.getLast_updated()));


       mEditDeleteShowHideFunction();

    }

    /*This Function is for Show hide Edit delete Icon*/
    private void mEditDeleteShowHideFunction() {

        /*in this condition if patient is login then we have to show Edit Delete Icon else hide Edit delete Icon */
        if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) &&
                Preferences.get(General.ROLE_ID).equals(General.USER_ROLL_SENJAM_PATIENT)) {

            imageViewEdit.setVisibility(View.VISIBLE);
            imageViewDelete.setVisibility(View.VISIBLE);
        }else {
            imageViewEdit.setVisibility(View.GONE);
            imageViewDelete.setVisibility(View.GONE);
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
        String date = DateFormat.format("MMM dd, yyyy, hh:mm:ss a", cal).toString();
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
                Intent noteEdit = new Intent(this, AddSowsNoteActivity.class);
                noteEdit.putExtra(General.NOTE_DETAILS, listModel);
                noteEdit.putExtra("title", sowsNotesArrayList.get(0).getTitle());
                noteEdit.putExtra(Actions_.UPDATE_SOWS_NOTE, Actions_.UPDATE_SOWS_NOTE);
                startActivity(noteEdit);
                finish();
                break;

            case R.id.imageview_delete:
                /*When delete icon click this below function call*/
                deleteSowsNoteAPI();
                break;
        }
    }

    /*Api call for Delete Note*/
    private void deleteSowsNoteAPI() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE_JOURNAL);
        requestMap.put(General.ID, String.valueOf(listModel.getId()));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_JOURNALING;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                Log.e("DeleteSenjamResponse",response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonDeleteNote = jsonObject.getAsJsonObject(Actions_.DELETE_JOURNAL);
                    if (jsonDeleteNote.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(this, jsonDeleteNote.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }else {
                        Toast.makeText(this, jsonDeleteNote.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
