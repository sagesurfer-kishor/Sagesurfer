package com.modules.motivation.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.modules.motivation.adapter.WellnessSelecteToolkitItemAdapter;
import com.modules.motivation.model.ToolKitData;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.ToolkitParser_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.RequestBody;

public class WellnessSelectedItemsActivity extends AppCompatActivity {
    private static final String TAG = WellnessSelectedItemsActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ListView listView;
    private LinearLayout errorLayout;
    private TextView errorText, dateText, wellnessItem;
    private AppCompatImageView errorIcon, editToolkitImage;
    private ArrayList<ToolKitData> toolkitArrayList, toolkitIdArrayList, editKitArrayList;
    private ArrayList<String> idsArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout toolkitItemLayout;
    private String toolkitIds,dateTime;
    private int selectId=0;
    private Long timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_wellness_selected_items);

        editKitArrayList = new ArrayList<ToolKitData>();
        toolkitIdArrayList = new ArrayList<ToolKitData>();
        idsArrayList = new ArrayList<>();

        getIntentData();

        setInitailization();
    }

    private void getIntentData() {
        Intent data = getIntent();
        if (data != null) {
            toolkitIds = data.getStringExtra("toolkit_ids");
            dateTime = data.getStringExtra("datetime");
            selectId = data.getIntExtra("id",0);

            idsArrayList.clear();
            String[] ids = toolkitIds.split(","); //1,4,7,5
            for (int i = 0; i < ids.length; i++) {
                idsArrayList.add(ids[i].replace(" ", ""));
            }

            timeStamp = data.getLongExtra("time_stamp", 0);
        }

    }

    @SuppressLint("RestrictedApi")
    private void setInitailization() {
        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.back_btn);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setPadding(90, 0, 0, 0);
        titleText.setText(this.getResources().getString(R.string.toolkit_selected_items));

        listView = (ListView) findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(3);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        errorText = (TextView) findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.imageview_error_icon);
        errorLayout = (LinearLayout) findViewById(R.id.linealayout_error);
        toolkitItemLayout = findViewById(R.id.add_toolkit_layout);

        dateText = (TextView) findViewById(R.id.date_toolkit);
        wellnessItem = (TextView) findViewById(R.id.label_id);
        editToolkitImage = findViewById(R.id.edit_toolkit);

        //dateText.setText(getDate(timeStamp));
        dateText.setText(dateTime);

        editToolkitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editToolkitData();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSelectedToolkitData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    //  make network call to fetch selected toolkit data
    @SuppressLint("LongLogTag")
    private void getSelectedToolkitData() {
        int status = 11;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_TOOLKIT_LIST);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.GET_TOOLKIT_LIST;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {

                    toolkitArrayList = ToolkitParser_.parseGetToolkit(response, Actions_.GET_TOOLKIT_LIST, this, TAG);

                    if (toolkitArrayList.size() > 0) {
                        if (toolkitArrayList.get(0).getStatus() == 1) {

                            if (toolkitArrayList != null && toolkitArrayList.size() > 0) {
                                for (int i = 0; i < toolkitArrayList.size(); i++) {

                                    for (int j = 0; j < idsArrayList.size(); j++) {

                                        if (idsArrayList.get(j).equals(String.valueOf(toolkitArrayList.get(i).getId()))) {
                                            toolkitIdArrayList.add(toolkitArrayList.get(i));
                                            break;
                                        }
                                    }
                                }
                            }

                            showError(false, 1);

                            WellnessSelecteToolkitItemAdapter wellnessToolkitAdapter = new WellnessSelecteToolkitItemAdapter(this, toolkitIdArrayList);
                            listView.setAdapter(wellnessToolkitAdapter);

                        } else {
                            showError(true, toolkitArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(true, 2);
                    }
                } else {
                    showError(true, 11);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showError(true, status);
        }
    }

    private void editToolkitData() {
        finish();
        Intent editToolkitIntent = new Intent(this, AddToolkitItemAcivity.class);
        editToolkitIntent.putExtra("editToolkit", true);
        editToolkitIntent.putExtra("edit_toolkit_list", toolkitArrayList);
        editToolkitIntent.putExtra("toolkit_ids", toolkitIds);
        editToolkitIntent.putExtra("id", selectId);
        editToolkitIntent.putExtra("date_time", timeStamp);
        startActivity(editToolkitIntent);

      /*  HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.EDIT_TOOLKIT);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.GET_TOOLKIT_LIST;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    editKitArrayList = ToolkitParser_.parseGetToolkit(response, Actions_.EDIT_TOOLKIT, this, TAG);
                    if (editKitArrayList.size() > 0) {
                        if (editKitArrayList.get(0).getStatus() == 1) {
                            Bundle bundleAnimation = ActivityOptions.makeCustomAnimation
                                    (this, R.anim.animation_one, R.anim.animation_two).toBundle();
                            Intent editToolkitIntent = new Intent(this, AddToolkitItemAcivity.class);
                            editToolkitIntent.putExtra("editBoolean", true);
                            editToolkitIntent.putExtra("edit_toolkit_list", editKitArrayList);
                            startActivity(editToolkitIntent, bundleAnimation);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }


    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            toolkitItemLayout.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, this));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            toolkitItemLayout.setVisibility(View.VISIBLE);
        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }

}
