package com.modules.caseload.mhaw.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
 import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.modules.caseload.mhaw.adapter.MhawAmendmentlistAdapter;
import com.modules.caseload.mhaw.model.MhawAmendmentList;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.MhawAmendmentList_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

public class MhawAmendmentListActivity extends AppCompatActivity implements MhawAmendmentlistAdapter.MhawAmendmentAdapterListener, MhawAmendmentlistAdapter.MhawAmendmentAdapterListenerEdit {

    private static final String TAG = MhawAmendmentListActivity.class.getSimpleName();
    public ArrayList<MhawAmendmentList> progressList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private RecyclerView recyclerView;
    private int progressID;
    private FloatingActionButton createProgressNote;
    private int AddedById, FinalizeNoteId;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mhaw_amendment_list);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

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
        titleText.setText(getResources().getString(R.string.amendments));

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            progressID = extra.getInt("progressNoteId");
            AddedById = extra.getInt("AddedById");
            FinalizeNoteId = extra.getInt("FinalizeNoteId");
            /*Log.e("progressID", "" + progressID);
            Log.e("AddedById", "" + AddedById);
            Log.e("FinalizeNoteId", "" + FinalizeNoteId);
            Log.e("UserId", "" + Preferences.get(General.USER_ID));*/
        }

        initUI();

    }

    private void initUI() {

        recyclerView = (RecyclerView) findViewById(R.id.swipe_refresh_layout_recycler_view);
        recyclerView.setBackgroundColor(this.getResources().getColor(R.color.screen_background));
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);


        createProgressNote = (FloatingActionButton) findViewById(R.id.swipe_refresh_layout_recycler_view_float);
        createProgressNote.setImageResource(R.drawable.ic_add_white);

        errorText = (TextView) findViewById(R.id.swipe_refresh_recycler_view_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.swipe_refresh_recycler_view__error_icon);
        errorLayout = (LinearLayout) findViewById(R.id.swipe_refresh_recycler_view_error_layout);

//        Log.e("ApiCall", String.valueOf(progressID));
//        amendmentListAPICalled(String.valueOf(progressID));

        if (Preferences.get(General.USER_ID).equalsIgnoreCase(String.valueOf(AddedById)) && FinalizeNoteId == 1) {
            createProgressNote.setVisibility(View.VISIBLE);
        } else {
            createProgressNote.setVisibility(View.GONE);
        }

        createProgressNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MhawAmendmentListActivity.this, MhawAddAmendmentActivity.class);
                intent.putExtra("progressNoteId", progressID);
                startActivity(intent);
            }
        });

    }


    @SuppressLint("SetTextI18n")
    private void amendmentListAPICalled(String ID) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_AMENDMENT_LIST_MHAW);
        requestMap.put(General.ID, ID);

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("ADResponse", response);
                if (response != null) {
                    progressList = MhawAmendmentList_.parseProgressList(response, Actions_.GET_AMENDMENT_LIST_MHAW, this, TAG);
                    if (progressList.size() > 0) {
                        if (progressList.get(0).getStatus() == 1) {
                            showError(false, 1);
//                            notesCount.setText(progressList.size() + " Notes Found");
                            /*ArrayList can be Bind And Set into Adapter*/
                            MhawAmendmentlistAdapter progressListAdapter = new MhawAmendmentlistAdapter(this, progressList, this, this);
                            recyclerView.setAdapter(progressListAdapter);
                        } else {
//                            notesCount.setText("0 Note Found");
                            showError(true, progressList.get(0).getStatus());
                        }
                    } else {
                        showError(true, progressList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("errorADListActivity", e.getMessage());
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


    private void deleteProgressNoteAPI(int pos) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE_AMENDMENT_MHAW);
        requestMap.put(General.ID, String.valueOf(progressList.get(pos).getId()));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonDeleteNote = jsonObject.getAsJsonObject(Actions_.DELETE_AMENDMENT_MHAW);
                    if (jsonDeleteNote.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(this, jsonDeleteNote.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        amendmentListAPICalled(String.valueOf(progressID));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNoteDetailsLayoutClicked(int position, MhawAmendmentList progressNote) {
        deleteProgressNoteAPI(position);
    }

    @Override
    public void onNoteDetailsLayoutClickedEdit(int position, MhawAmendmentList progressNote) {
        Intent intent = new Intent(MhawAmendmentListActivity.this, MhawAddAmendmentActivity.class);
        intent.putExtra("amendmentId", progressList.get(position).getId());
        intent.putExtra(General.NOTE_DETAILS, progressNote);
        intent.putExtra(Actions_.UPDATE_NOTE, Actions_.UPDATE_NOTE);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        amendmentListAPICalled(String.valueOf(progressID));
    }
}
