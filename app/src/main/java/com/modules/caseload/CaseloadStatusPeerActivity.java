package com.modules.caseload;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowSnack;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;

/**
 * Created by Monika on 8/20/2018.
 */

public class CaseloadStatusPeerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CaseloadStatusPeerActivity.class.getSimpleName();

    @BindView(R.id.caseload_status_title)
    TextView textViewToolbarTitle;
    @BindView(R.id.textview_post)
    TextView textViewPost;
    @BindView(R.id.radiogroup_status)
    RadioGroup radioGroupStatus;
    @BindView(R.id.radiobutton_inprocess)
    RadioButton radioButtonInProcess;
    @BindView(R.id.radiobutton_emerging)
    RadioButton radioButtonEmerging;
    @BindView(R.id.radiobutton_supportneeded)
    RadioButton radioButtonSupportNeeded;
    /*@BindView(R.id.edittext_status_message)
    EditText editTextStatusMessage;*/

    Toolbar toolbar;
    private String statusType = "";
    private int statusTypeValue = 0;
    private EditText editTextStatusMessage;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_caseload_status);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.caseload_progress_note_toolbar);
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

        textViewPost.setOnClickListener(this);

        Intent data = getIntent();
        if (data != null && data.hasExtra(General.STATUS)) {
            statusType = data.getStringExtra(General.STATUS);
            if (statusType.equalsIgnoreCase(getResources().getString(R.string.worsening)) || statusType.equalsIgnoreCase(getResources().getString(R.string.support_needed))) { //support_needed
                radioButtonSupportNeeded.setChecked(true);
                statusTypeValue = 3;
                showStatusCommentDialog(3);
            } else if (statusType.equalsIgnoreCase(getResources().getString(R.string.improving)) || statusType.equalsIgnoreCase(getResources().getString(R.string.emerging))) { //emerging
                radioButtonEmerging.setChecked(true);
                statusTypeValue = 2;
                showStatusCommentDialog(2);
            } else {//stable or in process
                radioButtonInProcess.setChecked(true);
                statusTypeValue = 1;
                showStatusCommentDialog(1);
            }
        } else {
            onBackPressed();
        }

        radioGroupStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radiobutton_supportneeded:
                        if (statusTypeValue != 3)
                            showStatusCommentDialog(3);
                        break;
                    case R.id.radiobutton_emerging:
                        if (statusTypeValue != 2)
                            showStatusCommentDialog(2);
                        break;
                    case R.id.radiobutton_inprocess:
                        if (statusTypeValue != 1)
                            showStatusCommentDialog(1);
                        break;
                }
            }
        });
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
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        textViewToolbarTitle.setText(getResources().getString(R.string.status));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_post:
                /*if(validate()) {
                    int status = postCaseloadStatus();
                    if (status == 1) {
                        onBackPressed();
                    }
                }*/
                int status = postCaseloadStatus();
                if (status == 1) {
                    onBackPressed();
                }
                break;
        }
    }

    //open Status Comment Dialog
    private void showStatusCommentDialog(final int selectedStatus) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_caseload_peer_status);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        editTextStatusMessage = (EditText) dialog.findViewById(R.id.edittext_status_message);
        final Button buttonYes = (Button) dialog.findViewById(R.id.button_yes);
        final Button buttonNo = (Button) dialog.findViewById(R.id.button_no);
        final TextView titleTxt = (TextView) dialog.findViewById(R.id.textview_message);

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021))) {
            titleTxt.setText("Are you sure you want to change status for this guest?");
            editTextStatusMessage.setHint("Reason to change status of guest");
        }

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (statusType.equalsIgnoreCase(getResources().getString(R.string.worsening)) || statusType.equalsIgnoreCase(getResources().getString(R.string.support_needed))) { //support_needed
                    statusTypeValue = 3;
                    radioButtonSupportNeeded.setChecked(true);
                } else if (statusType.equalsIgnoreCase(getResources().getString(R.string.improving)) || statusType.equalsIgnoreCase(getResources().getString(R.string.emerging))) { //emerging
                    statusTypeValue = 2;
                    radioButtonEmerging.setChecked(true);
                } else {//stable or in process
                    statusTypeValue = 1;
                    radioButtonInProcess.setChecked(true);
                }
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTypeValue = selectedStatus;
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean validate() {
        if (editTextStatusMessage.getText().toString().trim().length() <= 0) {
            ShowSnack.viewWarning(editTextStatusMessage, "Status Message is compulsory", getApplicationContext());
            return false;
        }
        return true;
    }

    //make network call to fetch all consumers for Care Cordinator/Clinician
    private int postCaseloadStatus() {
        int status = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.SET_STATUS_DROPDOWN);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.STATUS, "" + statusTypeValue);
        requestMap.put(General.STATUS_MSG, editTextStatusMessage.getText().toString().trim());

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.ACTIVITY_REVIEW_API;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    if (Error_.oauth(response, CaseloadStatusPeerActivity.this) == 13) {
                        status = 13;
                        GetErrorResources.showError(status, CaseloadStatusPeerActivity.this);
                        return 13;
                    }
                    JsonObject jsonObject = GetJson_.getJson(response);
                    status = jsonObject.get(General.STATUS).getAsInt();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GetErrorResources.showError(status, CaseloadStatusPeerActivity.this);
        return status;
    }
}