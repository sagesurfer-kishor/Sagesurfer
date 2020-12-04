package com.modules.caseload;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.CaseloadProgressNote_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CaseloadParser_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;

/**
 * Created by Monika on 10/11/2018.
 */

public class ProgressAddNoteActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = PeerAddNoteActivity.class.getSimpleName();

    @BindView(R.id.caseload_add_note_title)
    TextView textViewTitle;
    @BindView(R.id.textview_toolbar_save)
    TextView saveText;
    @BindView(R.id.imageview_toolbar_save)
    AppCompatImageView imageViewSave;
    @BindView(R.id.linearlayout_add_peer_note)
    LinearLayout linearLayoutAddPeerNote;
    @BindView(R.id.linearlayout_add_progress_note)
    LinearLayout linearLayoutAddProgressNote;
    @BindView(R.id.textview_progress_note_name)
    TextView nameText;
    @BindView(R.id.textview_progress_note_id)
    TextView noteIdText;
    @BindView(R.id.textview_progress_note_date)
    TextView dateText;
    @BindView(R.id.edittext_progress_note_subjective)
    EditText editTextSubjective;
    @BindView(R.id.edittext_progress_note_objective)
    EditText editTextObjective;
    @BindView(R.id.edittext_progress_note_assessment)
    EditText editTextAssessment;
    @BindView(R.id.edittext_progress_note_plan)
    EditText editTextPlan;

    Toolbar toolbar;
    private int sYear, sMonth, sDay;
    public String date = "";
    private int mYear = 0, mMonth = 0, mDay = 0;
    private Calendar calendar;

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
                onBackPressed();
            }
        });

        saveText.setVisibility(View.GONE);
        imageViewSave.setVisibility(View.VISIBLE);

        imageViewSave.setOnClickListener(this);
        dateText.setOnClickListener(this);
        linearLayoutAddPeerNote.setVisibility(View.GONE);
        linearLayoutAddProgressNote.setVisibility(View.VISIBLE);

        nameText.setText(Preferences.get(General.CONSUMER_NAME));
        noteIdText.setText(Preferences.get(General.CONSUMER_ID));

        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        textViewTitle.setText(getResources().getString(R.string.add_progress_note));
        //toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_progress_note_date:
                DatePickerDialog datePickerDialog = new DatePickerDialog(ProgressAddNoteActivity.this,
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
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;

            case R.id.imageview_toolbar_save:
                if (validate()) {
                    addProgressNote();
                }
                break;
        }
    }

    private boolean validate() {
        if (date.length() <= 0) {
            ShowSnack.viewWarning(dateText, "Date is compulsory", getApplicationContext());
            return false;
        }
        if (editTextSubjective.getText().toString().trim().length() <= 0) {
            ShowSnack.viewWarning(editTextSubjective, "Subjective is compulsory", getApplicationContext());
            return false;
        }
        if (editTextObjective.getText().toString().trim().length() <= 0) {
            ShowSnack.viewWarning(editTextObjective, "Objective is compulsory", getApplicationContext());
            return false;
        }
        if (editTextAssessment.getText().toString().trim().length() <= 0) {
            ShowSnack.viewWarning(editTextAssessment, "Assessment is compulsory", getApplicationContext());
            return false;
        }
        if (editTextPlan.getText().toString().trim().length() <= 0) {
            ShowSnack.viewWarning(editTextPlan, "Plan is compulsory", getApplicationContext());
            return false;
        }
        return true;
    }

    //make network call to add a note
    private void addProgressNote() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ADD_PROGRESS_NOTE);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.POSTED_DATE, dateText.getText().toString().trim());
        requestMap.put(General.SUBJECTIVE, editTextSubjective.getText().toString().trim());
        requestMap.put(General.OBJECTIVE, editTextObjective.getText().toString().trim());
        requestMap.put(General.ASSESSMENT, editTextAssessment.getText().toString().trim());
        requestMap.put(General.PLAN, editTextAssessment.getText().toString().trim());

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    ArrayList<CaseloadProgressNote_> caseloadProgressNoteArrayList = CaseloadParser_.parseProgressNote(response, Actions_.ADD_PROGRESS_NOTE, getApplicationContext(), TAG);
                    if (caseloadProgressNoteArrayList.size() > 0) {
                        if (caseloadProgressNoteArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                        } else {
                            showError(true, caseloadProgressNoteArrayList.get(0).getStatus());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            ShowToast.successful(GetErrorResources.getMessage(status, getApplicationContext()), getApplicationContext());
        } else {
            ShowToast.successful(getResources().getString(R.string.successful), getApplicationContext());
            onBackPressed();
        }
    }
}
