package com.modules.caseload.mhaw.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.modules.caseload.mhaw.model.MhawAmendmentList;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowSnack;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import okhttp3.RequestBody;

public class MhawAddAmendmentActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MhawAddProgressNoteActivity.class.getSimpleName();
    private EditText mEdtAmendmentsDesc;
    private Button mBtnCancel, mBtnSubmit;
    private MhawAmendmentList mhawAmendmentList;
    private String mEdtAmendmentsString, mUpdateString;
    private int progressID, amendmentID;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mhaw_amendment_add);


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

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
        titleText.setText(getResources().getString(R.string.add_amendments));

        initUi();

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            progressID = extra.getInt("progressNoteId");
            amendmentID = extra.getInt("amendmentId");
           /* Log.e("progressID", "" + progressID);
            Log.e("amendmentId", "" + amendmentID);*/
        }

        Intent data = getIntent();
        if (data.hasExtra(General.NOTE_DETAILS)) {
            titleText.setPadding(100, 0, 0, 0);
            titleText.setText(getResources().getString(R.string.edit_amendments));
            mhawAmendmentList = (MhawAmendmentList) data.getSerializableExtra(General.NOTE_DETAILS);
            mUpdateString = data.getStringExtra(Actions_.UPDATE_NOTE);
            setData(mhawAmendmentList);
        } else {
            mUpdateString = "";
            titleText.setPadding(100, 0, 0, 0);
            titleText.setText(getResources().getString(R.string.add_amendments));
        }


    }

    private void initUi() {
        mEdtAmendmentsDesc = findViewById(R.id.appointment_desc_edt);

        mBtnCancel = (Button) findViewById(R.id.cancel_btn);
        mBtnSubmit = (Button) findViewById(R.id.submit_btn);
        mBtnCancel.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
    }


    private void setData(MhawAmendmentList progressList) {

        mEdtAmendmentsDesc.setText(ChangeCase.toTitleCase(progressList.getDescription()));
        mBtnSubmit.setText(getResources().getString(R.string.update));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                showAddAmendmentDialog(getResources().getString(R.string.are_you_sure_you_want_to_cancel_amendment), "Cancel");
                break;
            case R.id.submit_btn:
                mEdtAmendmentsString = mEdtAmendmentsDesc.getText().toString();
                if (mUpdateString.equalsIgnoreCase(Actions_.UPDATE_NOTE)) {
                    if (AmendmentValidation(mEdtAmendmentsString, v)) {
                        showAddAmendmentDialog(getResources().getString(R.string.are_you_sure_you_want_to_update_this_amendments_note), Actions_.EDIT_AMENDMENT_MHAW);
                    }
                } else {
                    if (AmendmentValidation(mEdtAmendmentsString, v)) {
                        showAddAmendmentDialog(getResources().getString(R.string.are_you_sure_you_want_to_submit_this_amendments_note), Actions_.ADD_AMENDMENT_MHAW);
                    }
                }
        }

    }


    private boolean AmendmentValidation(String title, View view) {

        if (title.length() == 0) {
            ShowSnack.viewWarning(view, "Please enter amendment", getApplicationContext());
            return false;
        }


        return true;
    }


    @SuppressLint("SetTextI18n")
    private void showAddAmendmentDialog(String message, final String action) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_peer_note_edit);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView textViewMsg = (TextView) dialog.findViewById(R.id.textview_msg);
        final Button buttonYes = (Button) dialog.findViewById(R.id.button_yes);
        final ImageButton buttonClose = (ImageButton) dialog.findViewById(R.id.button_close);
        textViewMsg.setText(message);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (action.equals(Actions_.ADD_AMENDMENT_MHAW)) {
                    addUpdateAmendmentAPICalled(action, mEdtAmendmentsString, String.valueOf(progressID));
                    dialog.dismiss();
                    onBackPressed();
                } else if (action.equals(Actions_.EDIT_AMENDMENT_MHAW)) {
                    addUpdateAmendmentAPICalled(action, mEdtAmendmentsString, String.valueOf(amendmentID));
                    dialog.dismiss();
                    onBackPressed();
                } else {
                    dialog.dismiss();
                    onBackPressed();
                }
            }
        });

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void addUpdateAmendmentAPICalled(String action, String description, String id) {
        HashMap<String, String> requestMap = new HashMap<>();

        if (action.equals(Actions_.ADD_AMENDMENT_MHAW)) {
            requestMap.put(General.ACTION, action);
            requestMap.put(General.ID, id);
        } else {
            requestMap.put(General.ACTION, action);
            requestMap.put(General.ID, id);
        }

        requestMap.put(General.USER_ID, Preferences.get(General.NOTE_USER_ID));
        requestMap.put(General.DESCRIPTION, description);


        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                Log.e("AddAmendmentResponse", response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddProgressNote;

                    if (action.equals(Actions_.ADD_AMENDMENT_MHAW)) {
                        jsonAddProgressNote = jsonObject.getAsJsonObject(Actions_.ADD_AMENDMENT_MHAW);
                    } else {
                        jsonAddProgressNote = jsonObject.getAsJsonObject(Actions_.EDIT_AMENDMENT_MHAW);
                    }

                    if (jsonAddProgressNote.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(this, jsonAddProgressNote.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("errorAddAmen", e.getMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
