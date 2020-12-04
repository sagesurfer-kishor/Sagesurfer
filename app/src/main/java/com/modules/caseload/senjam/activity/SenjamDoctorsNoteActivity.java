package com.modules.caseload.senjam.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.modules.caseload.senjam.adapter.SenjamDoctorNoteListAdapter;
import com.modules.caseload.senjam.model.SenjamListModel;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.SenjamDoctorNoteList_;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

public class SenjamDoctorsNoteActivity extends AppCompatActivity implements View.OnClickListener, SenjamDoctorNoteListAdapter.SenjamDoctorNoteListAdapterListener, SenjamDoctorNoteListAdapter.SenjamEditButtonListener, SenjamDoctorNoteListAdapter.SenjamDeleteButtonListener {
    private static final String TAG = SenjamDoctorsNoteActivity.class.getSimpleName();
    public ArrayList<SenjamListModel> senjamListModelArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private FloatingActionButton createDoctorNote;
    private LinearLayoutManager mLinearLayoutManager;
    private TextInputEditText editTextSearch;
    private LinearLayout mLinearLayoutAddDoctorNote;
    private Dialog dialog;
    private SenjamDoctorNoteListAdapter senjamDoctorNoteListAdapter;
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

        setContentView(R.layout.activity_senjam_doctors_note);
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
        titleText.setText(getResources().getString(R.string.doctors_note));

        initUI();

        editTextSearch.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                searchText = editTextSearch.getText().toString().trim();

                //Api call for search list and search will be Manage from Api
                noteProgressListAPICalled(searchText);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }

    // Variable Declaration Function
    private void initUI() {
        recyclerView = (RecyclerView) findViewById(R.id.swipe_refresh_layout_recycler_view);
        recyclerView.setBackgroundColor(this.getResources().getColor(R.color.screen_background));
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);

        errorText = (TextView) findViewById(R.id.swipe_refresh_recycler_view_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.swipe_refresh_recycler_view__error_icon);
        errorLayout = (LinearLayout) findViewById(R.id.swipe_refresh_recycler_view_error_layout);

        createDoctorNote = (FloatingActionButton) findViewById(R.id.swipe_refresh_layout_recycler_view_float);
        createDoctorNote.setImageResource(R.drawable.ic_add_white);
        createDoctorNote.setOnClickListener(this);

        editTextSearch = findViewById(R.id.edittext_search);

        mLinearLayoutAddDoctorNote = findViewById(R.id.linear_add_layout);

        mLinearLayoutAddDoctorNote.setOnClickListener(this);
        createDoctorNote.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Called API for display list of doctor notes
        noteProgressListAPICalled("");
    }

    @SuppressLint("SetTextI18n")
    private void noteProgressListAPICalled(String searchText) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROGRESS_NOTE_SENJAM);
        requestMap.put(General.PATIENT_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.SEARCH, searchText);
//        Log.e("requestMap", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
//                Log.e("PNResponse", "" + response);
                if (response != null) {
                    senjamListModelArrayList = SenjamDoctorNoteList_.parseDoctorSowsList(response, Actions_.GET_PROGRESS_NOTE_SENJAM, this, TAG);
                    if (senjamListModelArrayList.size() > 0) {
                        if (senjamListModelArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);

                            // ArrayList Bind Here And Set into Adapter
                            senjamDoctorNoteListAdapter = new SenjamDoctorNoteListAdapter(this, senjamListModelArrayList, this, this, this);
                            recyclerView.setAdapter(senjamDoctorNoteListAdapter);

                        } else {
                            showError(true, senjamListModelArrayList.get(0).getStatus());
                        }
                    } else {
                        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
//                        showError(true, progressList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("errorPNListActivity", e.getMessage());
            }
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, this));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swipe_refresh_layout_recycler_view_float:
                Intent addProgressNoteIntent = new Intent(this, AddDoctorsNoteActivity.class);
                startActivity(addProgressNoteIntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // Click Event For Doctors Notes Detail Activity
    @Override
    public void onNoteDetailsLayoutClicked(SenjamListModel senjamListModel) {
        Intent progressNoteDetails = new Intent(this, SenjamDoctorsNoteDetailsActivity.class);
        progressNoteDetails.putExtra(General.NOTE_DETAILS, senjamListModel);
        progressNoteDetails.putExtra("patientName", Preferences.get(General.CONSUMER_NAME));
        startActivity(progressNoteDetails);
    }

    // This Function or Click Listener will be Applied When Clinician Clicked on Edit Button it will redirect in Edit Screen
    @Override
    public void onSenjamEditButtonClickedListener(SenjamListModel senjamListModel, int position) {
        Intent addDoctorNote = new Intent(this, AddDoctorsNoteActivity.class);
        addDoctorNote.putExtra(General.NOTE_DETAILS, senjamListModel);
        addDoctorNote.putExtra(Actions_.UPDATE_DOCTOR_NOTE, Actions_.UPDATE_DOCTOR_NOTE);
        startActivity(addDoctorNote);
    }


    // This Function or Click Listener will be Applied When Clinician Clicked on Delete Button it will Shows Delete Dialog
    @Override
    public void onSenjamDeleteButtonClickedListener(SenjamListModel senjamListModel, int position) {

        // Shows Delete Dialog
        showDeleteDoctorNoteDialog(senjamListModel.getId(), position);
    }

    // Delete Dialog Function
    @SuppressLint("SetTextI18n")
    private void showDeleteDoctorNoteDialog(final String ID, final int pos) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_peer_note_delete);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView textViewMsg = (TextView) dialog.findViewById(R.id.textview_msg);
        final Button buttonYes = (Button) dialog.findViewById(R.id.button_yes);
        final ImageButton buttonClose = (ImageButton) dialog.findViewById(R.id.button_close);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Api call for Delete Doctor Notes
                deleteProgressNoteAPI(ID, pos);
            }
        });

        dialog.show();
    }


    // Function for Api calling Delete Doctor Notes
    private void deleteProgressNoteAPI(String ID, int position) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE_PROGRESS_NOTE_SENJAM);
        requestMap.put(General.ID, ID);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        Log.e("RequestMapDelete", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                Log.e("DeleteSenjamResponse", response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonDeleteNote = jsonObject.getAsJsonObject(Actions_.DELETE_PROGRESS_NOTE_SENJAM);
                    if (jsonDeleteNote.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(this, jsonDeleteNote.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        senjamListModelArrayList.remove(position);
                        senjamDoctorNoteListAdapter.notifyDataSetChanged();
//                        onBackPressed();
                    } else {
                        Toast.makeText(this, jsonDeleteNote.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
