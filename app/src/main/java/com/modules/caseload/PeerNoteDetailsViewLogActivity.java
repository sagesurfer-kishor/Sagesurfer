package com.modules.caseload;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sagesurfer.adapters.CaseloadNoteViewLogListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.CaseloadPeerNoteViewLog_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CaseloadParser_;
import com.storage.preferences.Preferences;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;

/**
 * Created by Monika on 10/29/2018.
 */

public class PeerNoteDetailsViewLogActivity extends AppCompatActivity {
    private static final String TAG = PeerNoteDetailsViewLogActivity.class.getSimpleName();

    @BindView(R.id.caseload_peer_note_title)
    TextView textViewTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.linearlayout_error)
    LinearLayout lineaLayoutError;
    @BindView(R.id.textview_error_message)
    TextView textViewErrorMessage;
    @BindView(R.id.imageview_error)
    ImageView imageViewError;

    private Long note_id;
    private String note_subject;
    Toolbar toolbar;
    ArrayList<CaseloadPeerNoteViewLog_> viewLogArrayList = new ArrayList<>();
    public CaseloadNoteViewLogListAdapter caseloadNoteViewLogListAdapter;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_caseload_peer_note_view_logs);
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

        Intent data = getIntent();
        if (data != null) {
            if (data.hasExtra(General.NOTE_ID)) {
                note_id = data.getLongExtra(General.NOTE_ID, 0);
                note_subject = data.getStringExtra(General.NOTE_SUBJECT);
            }
        } else {
            onBackPressed();
        }

        caseloadNoteViewLogListAdapter = new CaseloadNoteViewLogListAdapter(this, getApplicationContext(), viewLogArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(caseloadNoteViewLogListAdapter);
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
        textViewTitle.setText(getResources().getString(R.string.view_log) + "- " + note_subject);
        getViewLog();
    }

    //make network call to fetch all logs for Peer Note
    private void getViewLog() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.VIEW_LOG);
        requestMap.put(General.NOTE_ID, String.valueOf(note_id));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.ADD_NOTES_API;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    viewLogArrayList = CaseloadParser_.parseViewLog(response, General.DATA, getApplicationContext(), TAG);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 1) {
                        if (viewLogArrayList.size() > 0) {
                            caseloadNoteViewLogListAdapter = new CaseloadNoteViewLogListAdapter(this, getApplicationContext(), viewLogArrayList);
                            recyclerView.setAdapter(caseloadNoteViewLogListAdapter);
                            showError(false, 1);
                        } else {
                            showError(true, 2);
                        }
                    } else {
                        showError(true, jsonObject.getInt(General.STATUS));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            lineaLayoutError.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            textViewErrorMessage.setText(GetErrorResources.getMessage(status, getApplicationContext()));
            imageViewError.setImageResource(GetErrorResources.getIcon(status));
        } else {
            lineaLayoutError.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        }
    }

}
