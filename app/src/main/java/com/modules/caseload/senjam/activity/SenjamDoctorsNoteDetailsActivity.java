package com.modules.caseload.senjam.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.modules.caseload.senjam.model.SenjamListModel;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class SenjamDoctorsNoteDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = SenjamDoctorsNoteDetailsActivity.class.getSimpleName();
    private SenjamListModel senjamListModel;
    private Toolbar toolbar;
    private ImageView moodImage, imageViewEdit, imageViewDelete;
    private TextView mTextViewDate, mTextViewTime, mTextViewPatientName, mTextViewSubject, mTextViewDescription, time;
    private LinearLayout reasonLayout;
    private Long refId;
    private ArrayList<SenjamListModel> senjamListModelArrayList;
    private String mPatientName;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_add_doctor_progress_note_detail);

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
        titleText.setText(getResources().getString(R.string.view_doctor_note));

        initUI();


        // Get Data using Intent which will be passed From Previous Activity
        Intent data = getIntent();
        if (data.hasExtra(General.NOTE_DETAILS)) {
            senjamListModel = (SenjamListModel) data.getSerializableExtra(General.NOTE_DETAILS);
            mPatientName = data.getStringExtra("patientName");

            // this function will called for set Data
            setData(senjamListModel);
        }
        else {
            onBackPressed();
        }

    }

    // Variable Declaration Function
    private void initUI() {
        mTextViewDate = (TextView) findViewById(R.id.select_date_txt);
        mTextViewTime = (TextView) findViewById(R.id.select_time_txt);
        mTextViewPatientName = (TextView) findViewById(R.id.patient_name_txt);
        mTextViewSubject = (TextView) findViewById(R.id.subject_txt);
        mTextViewDescription = (TextView) findViewById(R.id.desc_txt);
        moodImage = (ImageView) findViewById(R.id.mood_image);
        reasonLayout = (LinearLayout) findViewById(R.id.reason_layout);
        imageViewEdit = (ImageView) findViewById(R.id.imageview_edit);
        imageViewDelete = (ImageView) findViewById(R.id.imageview_delete);
        imageViewEdit.setOnClickListener(this);
        imageViewDelete.setOnClickListener(this);
        imageViewEdit.setVisibility(View.VISIBLE);
        imageViewDelete.setVisibility(View.GONE);

    }

    // This  Function to set Data in Doctor Note Detail Screen
    @SuppressLint("SetTextI18n")
    private void setData(SenjamListModel senjamList) {
        mTextViewSubject.setText(senjamList.getSubject());
        mTextViewDate.setText(senjamList.getDate());
        mTextViewTime.setText(getTime(senjamList.getTime()));
        mTextViewDescription.setText(senjamList.getDescription());
        mTextViewPatientName.setText(mPatientName);
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

    // This Function will be Used For Time Format in Detail Screen
    private String getTime(String time) {
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

    // Click Event Function
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // this will called when clinician click on edit button and it will be Redirect into Edit Doctor note Activity
            case R.id.imageview_edit:
                Intent noteEdit = new Intent(this, AddDoctorsNoteActivity.class);
                noteEdit.putExtra(General.NOTE_DETAILS, senjamListModel);
                noteEdit.putExtra(Actions_.UPDATE_DOCTOR_NOTE, Actions_.UPDATE_DOCTOR_NOTE);
                startActivity(noteEdit);
                finish();
                break;

        }
    }
}
