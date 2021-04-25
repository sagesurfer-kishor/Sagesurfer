package com.modules.sows.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.modules.caseload.senjam.model.SenjamListModel;
import com.modules.sows.adapter.SenjamSowsDetailsAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

public class SenjamSowsDetailsActivity extends AppCompatActivity implements SenjamSowsDetailsAdapter.SenjamSowsDetailsAdapterListener {
    private static final String TAG = SenjamSowsDetailsActivity.class.getSimpleName();
    public ArrayList<SenjamListModel> senjamListModel = new ArrayList<>();
    private RecyclerView recyclerView;
    private SenjamListModel listModel;
    private LinearLayoutManager mLinearLayoutManager;
    private String sowsID;
    private long refId;


    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_senjam_sows_details);
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
        titleText.setText(getResources().getString(R.string.sows_details));

        initUI();

        // get Data from previous Activity
        // condition will be define sow list activity or it will click from notification and redirect here
        Intent data = getIntent();
        if (data.hasExtra(General.SOWS_DETAILS)) {
            listModel = (SenjamListModel) data.getSerializableExtra(General.SOWS_DETAILS);
            assert listModel != null;
            sowsID = listModel.getId();
            getSowsListAPICalled(sowsID);
        }else if (data.hasExtra(General.SOWS_FROM_NOTIFICATION)) {
            refId = data.getLongExtra(General.SOWS_FROM_NOTIFICATION, 0);
            getSowsListAPICalled(String.valueOf(refId));
        }

    }

    private void initUI() {
        recyclerView = findViewById(R.id.layout_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);

    }


    @SuppressLint("SetTextI18n")
    private void getSowsListAPICalled(String sowsID) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_SOWS_DETAILS_SENJAM);
        requestMap.put(General.SOW_ID, sowsID);
        Log.e("requestMap", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
//                Log.e("DetailsResponse", response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonArray jsonObjectSenjamDetail;
                    JsonObject objectQueAns = null;
                    jsonObjectSenjamDetail = jsonObject.getAsJsonArray(Actions_.GET_SOWS_DETAILS_SENJAM);
                    objectQueAns = jsonObjectSenjamDetail.get(0).getAsJsonObject();
                    JsonArray array = objectQueAns.getAsJsonArray(Actions_.QUES_ANS);
                    senjamListModel = new Gson().fromJson(array.toString(), new TypeToken<ArrayList<SenjamListModel>>(){}.getType());
                    if (objectQueAns.get(General.STATUS).getAsInt() == 1) {

                        // ArrayList will be bind here and set into SenjamSows Adapter
                        SenjamSowsDetailsAdapter senjamSowsAdapter = new SenjamSowsDetailsAdapter(this, senjamListModel, this);
                        recyclerView.setAdapter(senjamSowsAdapter);
                    }else {
                        Toast.makeText(this, objectQueAns.get(General.ERROR).getAsString(), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("errorPNListActivity", e.getMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onNoteDetailsLayoutClicked(SenjamListModel senjamListModel) {

    }
}
