package com.modules.caseload;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.directory.DirectoryList;
import com.sagesurfer.download.DownloadFile;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.CaseloadProgressNote_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CaseloadParser_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;

/**
 * Created by Monika on 7/10/2018.
 */

public class ProgressNoteDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ProgressNoteDetailsActivity.class.getSimpleName();

    @BindView(R.id.caseload_peer_note_title)
    TextView textViewTitle;
    @BindView(R.id.imageview_edit)
    ImageView imageViewEdit;
    @BindView(R.id.imageview_delete)
    ImageView imageViewDelete;
    @BindView(R.id.imageview_download)
    ImageView imageViewDownload;
    @BindView(R.id.linearlayout_peer_note_details)
    LinearLayout linearLayoutPeerNoteDetails;
    @BindView(R.id.linearlayout_progress_note_details)
    LinearLayout linearLayoutProgressNoteDetails;
    @BindView(R.id.textview_progress_note_name)
    TextView nameTextView;
    @BindView(R.id.textview_progress_note_date)
    TextView dateTextView;
    @BindView(R.id.textview_progress_note_subjective)
    TextView subjectiveTextView;
    @BindView(R.id.textview_progress_note_objective)
    TextView objectiveTextView;
    @BindView(R.id.textview_progress_note_assessment)
    TextView assessmentTextView;
    @BindView(R.id.textview_progress_note_plan)
    TextView planTextView;

    public ArrayList<CaseloadProgressNote_> caseloadProgressNoteArrayList = new ArrayList<>();

    public String name = "";
    public long id;
    Toolbar toolbar;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_caseload_peer_note_details);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.caseload_peer_note_toolbar);
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

        imageViewEdit.setVisibility(View.GONE);
        imageViewDelete.setVisibility(View.GONE);
        imageViewDownload.setVisibility(View.VISIBLE);
        linearLayoutPeerNoteDetails.setVisibility(View.GONE);
        linearLayoutProgressNoteDetails.setVisibility(View.VISIBLE);

        imageViewDownload.setOnClickListener(this);

        subjectiveTextView.setMovementMethod(new ScrollingMovementMethod());
        objectiveTextView.setMovementMethod(new ScrollingMovementMethod());
        assessmentTextView.setMovementMethod(new ScrollingMovementMethod());
        planTextView.setMovementMethod(new ScrollingMovementMethod());

        Intent data = getIntent();
        if (data != null) {
            if (data.hasExtra(General.ID)) {
                id = data.getLongExtra(General.ID, 0);
                name = data.getStringExtra(General.NAME);
            }
        } else {
            onBackPressed();
        }
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
        textViewTitle.setText(getResources().getString(R.string.progress_note));
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));

        geProgressNoteDetails();
    }

    //make network call to fetch all consumers for Care Cordinator/Clinician
    private void geProgressNoteDetails() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROGRESS_NOTE);
        requestMap.put(General.PROG_ID, String.valueOf(id));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    caseloadProgressNoteArrayList = CaseloadParser_.parseProgressNote(response, Actions_.GET_PROGRESS_NOTE, getApplicationContext(), TAG);
                    if (caseloadProgressNoteArrayList.size() > 0) {
                        if (caseloadProgressNoteArrayList.get(0).getStatus() == 1) {
                            nameTextView.setText("By User: " + caseloadProgressNoteArrayList.get(0).getBy_user());
                            dateTextView.setText("Posted On: " + caseloadProgressNoteArrayList.get(0).getDate());
                            subjectiveTextView.setText("Subjective: " + caseloadProgressNoteArrayList.get(0).getSubjective());
                            objectiveTextView.setText("Objective: " + caseloadProgressNoteArrayList.get(0).getObjective());
                            assessmentTextView.setText("Assessment: " + caseloadProgressNoteArrayList.get(0).getAssessment());
                            planTextView.setText("Plan: " + caseloadProgressNoteArrayList.get(0).getPlan());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_download:
                DownloadFile downloadFile = new DownloadFile();
                downloadFile.download(caseloadProgressNoteArrayList.get(0).getId(), caseloadProgressNoteArrayList.get(0).getUrl(),
                        FileOperations.getFileName(caseloadProgressNoteArrayList.get(0).getUrl()), DirectoryList.DIR_NOTES, this);
                break;
        }
    }
}