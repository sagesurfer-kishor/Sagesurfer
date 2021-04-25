package com.modules.sows;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.modules.caseload.senjam.model.SenjamListModel;
import com.modules.sows.activity.AddSowsNoteActivity;
import com.modules.sows.activity.SenjamSowsDetailsActivity;
import com.modules.sows.activity.SenjamSowsNoteDetailsActivity;
import com.modules.sows.adapter.SenjamSowsAdapter;
import com.modules.sows.adapter.SenjamSowsNoteListAdapter;
import com.modules.sows.model.SowsNotes;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.SenjamDoctorNoteList_;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

public class SenjamSowsActivity extends AppCompatActivity implements SenjamSowsAdapter.SenjamSowsAdapterListener, SenjamSowsNoteListAdapter.SenjamSowsNoteListAdapterListener, View.OnClickListener {
    private static final String TAG = SenjamSowsActivity.class.getSimpleName();
    public ArrayList<SowsNotes> sowsNotesArrayList = new ArrayList<>();
    private RecyclerView recyclerViewSows, recyclerViewNotes;
    public LinearLayoutManager mLinearLayoutManager;
    public LinearLayout mLinearLayoutSows, mLinearLayoutNotes, mLinearLayoutSowsNotesText;
    public TextView mTextViewSows, mTextViewNotes, errorText;
    public LinearLayout mLinearLayoutSearch, mLinearLayoutRecycleNotes, errorLayout;
    public CardView mCardViewSows;
    public int isClickedOnSowsOrNotes = 0;
    public EditText mEditTExtSearch;
    public FloatingActionButton createProgressNote;
    private AppCompatImageView errorIcon;
    private String mId = "";
    private String searchText;


    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_senjam_sows_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
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
        titleText.setPadding(100, 0, 0, 0);
        titleText.setText(getResources().getString(R.string.notes));

        initUI();

        // Condition Checked if user is patient then we have to pass patient_id as User_id and
        // if Login with Clinician than pass patient_id
        if (Preferences.get(General.ROLE_ID).equals(General.USER_ROLL_SENJAM_PATIENT)) {
            mId = Preferences.get(General.USER_ID);
        } else {
            mId = Preferences.get(General.CONSUMER_ID);
        }


        // Search Logic implemented here
        mEditTExtSearch.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                searchText = mEditTExtSearch.getText().toString().trim();

                // Api call when user can search from list
                sowsNotesListAPICalled(mId, searchText);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

//        ClickListener();
//
//        isCurrentUserPatient();
//        isCurrentUserClinician();

//        sowsProgressListAPICalled();


    }

    // Variable Declaration Function
    private void initUI() {

        mLinearLayoutSows = findViewById(R.id.linear_txt_sows);
        mLinearLayoutNotes = findViewById(R.id.linear_txt_notes);
        mLinearLayoutSearch = findViewById(R.id.linearlayout_search);
        mLinearLayoutRecycleNotes = findViewById(R.id.linear_layout_notes);
        mLinearLayoutSowsNotesText = findViewById(R.id.linear_sows_notes_txt_layout);
        mEditTExtSearch = findViewById(R.id.edittext_search);
        mCardViewSows = findViewById(R.id.cardView);
        mTextViewSows = findViewById(R.id.txt_sows);
        mTextViewNotes = findViewById(R.id.txt_notes);

        errorText = (TextView) findViewById(R.id.swipe_refresh_recycler_view_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.swipe_refresh_recycler_view__error_icon);
        errorLayout = (LinearLayout) findViewById(R.id.swipe_refresh_recycler_view_error_layout);

        recyclerViewSows = findViewById(R.id.layout_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSows.setLayoutManager(mLinearLayoutManager);

        recyclerViewNotes = findViewById(R.id.layout_recycler_view_notes);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewNotes.setLayoutManager(mLinearLayoutManager);


        mLinearLayoutSows.setBackground(getResources().getDrawable(R.drawable.selector_sows));
        mTextViewSows.setTextColor(getResources().getColor(R.color.white1));


        createProgressNote = (FloatingActionButton) findViewById(R.id.swipe_refresh_layout_recycler_view_float);
        createProgressNote.setImageResource(R.drawable.ic_add_white);
        createProgressNote.setOnClickListener(this);

        if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) &&
                Preferences.get(General.ROLE_ID).equals(General.USER_ROLL_SENJAM_PATIENT)) {
            createProgressNote.setVisibility(View.VISIBLE);
        } else {
            createProgressNote.setVisibility(View.GONE);
        }
    }

//    /*this function is to check Login with patient*/
//    public boolean isCurrentUserPatient() {
//
//        boolean hasPermissionShowHideLayout = false;
//
//        if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
//                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) &&
//                Preferences.get(General.ROLE_ID).equals(General.USER_ROLL_SENJAM_PATIENT)) {
//
//            mLinearLayoutSowsNotesText.setVisibility(View.GONE);
//            mLinearLayoutSearch.setVisibility(View.VISIBLE);
//            mLinearLayoutRecycleNotes.setVisibility(View.VISIBLE);
//            mCardViewSows.setVisibility(View.GONE);
//
//            hasPermissionShowHideLayout =true;
//
//
//        }
//
//        return hasPermissionShowHideLayout;
//    }
//
//    /*this function is to check Login with patient*/
//    public boolean isCurrentUserClinician() {
//
//        boolean hasPermissionShowHideLayout = false;
//
//        if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
//                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) &&
//                Preferences.get(General.ROLE_ID).equals(General.USER_ROLL_SENJAM_CLINICIAN)) {
//
//            if (isClickedOnSowsOrNotes == 0){
//                /*Show Sows Layout*/
//                mLinearLayoutSearch.setVisibility(View.GONE);
//                mLinearLayoutRecycleNotes.setVisibility(View.GONE);
//                mCardViewSows.setVisibility(View.VISIBLE);
//            }else {
//                /*Show Notes Layout*/
//                mLinearLayoutSearch.setVisibility(View.VISIBLE);
//                mLinearLayoutRecycleNotes.setVisibility(View.VISIBLE);
//                mCardViewSows.setVisibility(View.GONE);
//
//            }
//
//            hasPermissionShowHideLayout =true;
//
//
//        }
//        return hasPermissionShowHideLayout;
//    }

//    private void ClickListener() {
//
//        mLinearLayoutSows.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isClickedOnSowsOrNotes = 0;
//                mLinearLayoutSows.setBackground(getResources().getDrawable(R.drawable.selector_sows));
//                mLinearLayoutNotes.setBackground(getResources().getDrawable(R.drawable.selector_white_notes));
//                mTextViewSows.setTextColor(getResources().getColor(R.color.white1));
//                mTextViewNotes.setTextColor(getResources().getColor(R.color.colorPrimary));
////                isCurrentUserClinician();
//
//            }
//        });
//
//        mLinearLayoutNotes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isClickedOnSowsOrNotes = 1;
//                mLinearLayoutNotes.setBackground(getResources().getDrawable(R.drawable.selector_notes));
//                mLinearLayoutSows.setBackground(getResources().getDrawable(R.drawable.selector_white_sows));
//                mTextViewSows.setTextColor(getResources().getColor(R.color.colorPrimary));
//                mTextViewNotes.setTextColor(getResources().getColor(R.color.white1));
////                isCurrentUserClinician();
////                notesProgressListAPICalled();
//            }
//        });
//    }

    /*Api called for Sows List*//*
    @SuppressLint("SetTextI18n")
    private void sowsProgressListAPICalled() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_SOWS_SENJAM);
        requestMap.put(General.PATIENT_ID, Preferences.get(General.CONSUMER_ID));
        Log.e("requestMap", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("PNResponse", response);
                if (response != null) {
                    senjamListModelArrayList = SenjamDoctorNoteList_.parseDoctorSowsList(response, Actions_.GET_SOWS_SENJAM, this, TAG);
                    if (senjamListModelArrayList.size() > 0) {
                        if (senjamListModelArrayList.get(0).getStatus() == 1) {
                            SenjamSowsAdapter senjamSowsAdapter = new SenjamSowsAdapter(this, senjamListModelArrayList, this);
                            recyclerViewSows.setAdapter(senjamSowsAdapter);
                        } else {
                            Toast.makeText(this, "" + senjamListModelArrayList.get(0).getStatus(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("errorPNListActivity", e.getMessage());
            }
        }
    }*/

    /*Api called for Notes List*/
    @SuppressLint("SetTextI18n")
    private void sowsNotesListAPICalled(String ID, String searchText) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_NOTE_SENJAM);
        requestMap.put(General.PATIENT_ID, ID);
        requestMap.put(General.SEARCH, searchText);
        Log.e("requestMap", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("getSowsNotesResponse", response);
                if (response != null) {
                    sowsNotesArrayList = SenjamDoctorNoteList_.parseSowsNoteList(response, Actions_.GET_NOTE_SENJAM, this, TAG);
                    if (sowsNotesArrayList.size() > 0) {
                        if (sowsNotesArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            /*ArrayList Bind Here and set to adapter*/
                            SenjamSowsNoteListAdapter senjamSowsNoteListAdapter = new SenjamSowsNoteListAdapter(this, sowsNotesArrayList, this);
                            recyclerViewNotes.setAdapter(senjamSowsNoteListAdapter);
                        } else {
                            showError(true, sowsNotesArrayList.get(0).getStatus());
//                            Toast.makeText(this, "" + sowsNotesArrayList.get(0).getStatus(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        showError(true, 0);
//                        Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Log.e("errorSowsNotes", "" + e.getMessage());
            }
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            recyclerViewNotes.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, this));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            recyclerViewNotes.setVisibility(View.VISIBLE);
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

        // Api called for Note List
        sowsNotesListAPICalled(mId, "");
    }

    // This Click Listener is called when user click on note list and it will redirect into Note Detail Screen
    @Override
    public void onNoteDetailsLayoutClicked(SenjamListModel senjamListModel) {
        Intent intent = new Intent(SenjamSowsActivity.this, SenjamSowsDetailsActivity.class);
        intent.putExtra(General.SOWS_DETAILS, senjamListModel);
        startActivity(intent);
    }

    // This Click Listener is called when user click on sows list and it will redirect into Sows Detail Screen
    @Override
    public void onSowsNoteDetailsLayoutClicked(SowsNotes senjamListModel) {
        Intent intent = new Intent(SenjamSowsActivity.this, SenjamSowsNoteDetailsActivity.class);
        intent.putExtra(General.SOWS_DETAILS, senjamListModel);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // floating button click Listener
            // it will redirect into Add Sows or Note Activity
            case R.id.swipe_refresh_layout_recycler_view_float:
                Intent addProgressNoteIntent = new Intent(this, AddSowsNoteActivity.class);
                startActivity(addProgressNoteIntent);
                break;

        }
    }
}
