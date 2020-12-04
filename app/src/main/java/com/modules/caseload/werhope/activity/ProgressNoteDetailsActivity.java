package com.modules.caseload.werhope.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.modules.caseload.werhope.model.ProgressList;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.ProgressList_;
import com.storage.preferences.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ProgressNoteDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ProgressNoteDetailsActivity.class.getSimpleName();
    private ProgressList progressList;
    private Toolbar toolbar;
    private TextView moodType, postedDate, addedBy;
    private ImageView moodImage, imageViewEdit, imageViewDelete;
    private TextView date, duration, attendance, weeklyTheme, notes, reason;
    private LinearLayout reasonLayout;
    private Long refId;
    private ArrayList<ProgressList> progressArryList;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_progress_note_details);

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
            progressList = (ProgressList) data.getSerializableExtra(General.NOTE_DETAILS);
            setData(progressList);
        } else if (data.hasExtra(General.NOTE_FROM_NOTIFICATION)) {
            refId = data.getLongExtra(General.NOTE_FROM_NOTIFICATION, 0);
            callDetailsAPI(refId);
        } else {
            onBackPressed();
        }

    }

    private void callDetailsAPI(Long refId) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROGRESS_NOTE_DETAILS);
        requestMap.put(General.ID, String.valueOf(refId));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    progressArryList = ProgressList_.parseProgressList(response, Actions_.GET_PROGRESS_NOTE_DETAILS, this, TAG);
                    progressList = progressArryList.get(0);
                    setData(progressArryList.get(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initUI() {
        moodType = (TextView) findViewById(R.id.mood_type);
        reason = (TextView) findViewById(R.id.reason_txt);
        postedDate = (TextView) findViewById(R.id.posted_date_txt);
        addedBy = (TextView) findViewById(R.id.added_by_txt);
        date = (TextView) findViewById(R.id.date_txt);
        duration = (TextView) findViewById(R.id.durartion_txt);
        attendance = (TextView) findViewById(R.id.attendance_txt);
        weeklyTheme = (TextView) findViewById(R.id.weekly_theme);
        notes = (TextView) findViewById(R.id.note_deatails);
        moodImage = (ImageView) findViewById(R.id.mood_image);
        reasonLayout = (LinearLayout) findViewById(R.id.reason_layout);

        imageViewEdit = (ImageView) findViewById(R.id.imageview_edit);
        imageViewDelete = (ImageView) findViewById(R.id.imageview_delete);
        imageViewEdit.setOnClickListener(this);
        imageViewDelete.setOnClickListener(this);

        if (CheckRole.EDDOIcon(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            imageViewEdit.setVisibility(View.VISIBLE);
            imageViewDelete.setVisibility(View.VISIBLE);
        } else {
            imageViewEdit.setVisibility(View.GONE);
            imageViewDelete.setVisibility(View.GONE);
        }

    }

    @SuppressLint("SetTextI18n")
    private void setData(ProgressList progressList) {
        moodType.setText(ChangeCase.toTitleCase(progressList.getMood()));
        postedDate.setText(getDate(progressList.getPosted_date()));
        addedBy.setText(ChangeCase.toTitleCase(progressList.getAdded_by_name()) + " (" + ChangeCase.toTitleCase(progressList.getAdded_by_role()) + ")");
        date.setText(dateCaps(progressList.getDate()));
        duration.setText(progressList.getTotal_time().substring(0, 5));
        attendance.setText(ChangeCase.toTitleCase(progressList.getAttendance()));
        notes.setText(ChangeCase.toTitleCase(progressList.getNotes()));
        weeklyTheme.setText(ChangeCase.toTitleCase(progressList.getWeekly_theme()));

        if (progressList.getAttendance().equalsIgnoreCase("Declined Session")) {
            reasonLayout.setVisibility(View.VISIBLE);
            reason.setText(progressList.getReason());
        } else {
            reasonLayout.setVisibility(View.GONE);
        }

        //moodType.setTextColor(Color.parseColor(progressList.getMood_color()));

        Glide.with(this)
                .load(progressList.getMood_image())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(progressList.getMood_image())))
                .into(moodImage);

        if (progressList.getAdded_by_id() == Integer.parseInt(Preferences.get(General.USER_ID))) {
            imageViewEdit.setVisibility(View.VISIBLE);
            imageViewDelete.setVisibility(View.VISIBLE);
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
        String date = DateFormat.format("MMM dd, yyyy", cal).toString();
        return date;
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
                Intent noteEdit = new Intent(this, AddProgressNoteActivity.class);
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
        requestMap.put(General.ACTION, Actions_.DELETE_PROGRESS_NOTE);
        requestMap.put(General.ID, String.valueOf(progressList.getId()));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonDeleteNote = jsonObject.getAsJsonObject(Actions_.DELETE_PROGRESS_NOTE);
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
