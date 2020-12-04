package com.modules.task;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Task_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.storage.database.operations.DatabaseInsert_;
import com.storage.database.operations.DatabaseRetrieve_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Monika on 7/4/2018.
 */

public class TeamTaskListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, TaskListAdapter.OnItemClickListener, View.OnClickListener {

    private static final String TAG = TeamTaskListActivity.class.getSimpleName();
    private ArrayList<Task_> taskList;

    private FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeMenuListView listView;

    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    private DatabaseRetrieve_ databaseRetrieve_;
    private TaskListAdapter taskListAdapter;
    String lastDate = "0";
    Toolbar toolbar;

    @SuppressLint({"InflateParams", "RestrictedApi"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_team_task_list);

        toolbar = (Toolbar) findViewById(R.id.tasklist_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        databaseRetrieve_ = new DatabaseRetrieve_(getApplicationContext());

        taskList = new ArrayList<>();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        /*if(Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))){
            if (SosValidation.checkId(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                    || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.GONE);
            }
        } else {
            if (SosValidation.checkId(Integer.parseInt(Preferences.get(General.ROLE_ID))) || SosValidation.checkParentId(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.GONE);
            }
        }*/


        /*if([AppManager isCurruntUserHasPermissionToCreateNewTask] || [AppManager sharedManager].curruntLoginUser.nUserId == self.objSelectedGroup.nModeratorId || [AppManager sharedManager].curruntLoginUser.nUserId == self.objSelectedGroup.nOwnerId) {
            self.btnCreateNewTask.hidden = NO;
        }
    else {
            self.btnCreateNewTask.hidden = YES;
        }*/



        //Changed after discussion with Sagar and Nirmal as Sagar have implemented owner_id and isModerator on backend for all instances
        if (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
                || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1") || General.isCurruntUserHasPermissionToCreateNewTask()) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

        listView = (SwipeMenuListView) findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(0);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        taskListAdapter = new TaskListAdapter(TeamTaskListActivity.this, taskList, this);
        listView.setAdapter(taskListAdapter);

        errorText = (TextView) findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) findViewById(R.id.linealayout_error);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        fab.setBackgroundTintList(ColorStateList.valueOf(GetColor.getHomeIconBackgroundColorColorParse(true)));

        lastDate = "0";
        onRefresh();
    }

    @Override
    public void onRefresh() {
        ////mainActivityInterface.hideRevealView();
        swipeRefreshLayout.setRefreshing(true);
        showError(false, 1);
        fetchTask();
    }

    @Override
    public void onItemClickListener(Task_ task_) {
        if (task_ != null) {
            Intent detailsIntent = new Intent(getApplicationContext(), TaskDetailsActivity.class);
            detailsIntent.putExtra(General.TASK_LIST, task_);
            startActivity(detailsIntent);
            overridePendingTransition(0, 0);
        }
    }

    private void fetchTask() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.TASKLIST);
        requestMap.put(General.LAST_DATE, lastDate);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DATA;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, TeamTaskListActivity.this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, TeamTaskListActivity.this, this);
                taskList = Alerts_.parseTask(response, Actions_.TASKLIST, General.GROUP_TASK_LIST, getApplicationContext(), TAG);
                if (taskList.size() > 0) {
                    if (taskList.get(0).getStatus() == 1) {
                        saveTask(taskList);
                        taskListAdapter = new TaskListAdapter(TeamTaskListActivity.this, taskList, this);
                        listView.setAdapter(taskListAdapter);
                    } else {
                        showError(true, taskList.get(0).getStatus());
                    }
                } else {
                    showError(true, 12);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (taskList.size() <= 0) {
            showError(true, 2);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private void saveTask(final ArrayList<Task_> list) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                DatabaseInsert_ databaseInsert_ = new DatabaseInsert_(getApplicationContext());
                for (int i = 0; i < list.size(); i++) {
                    databaseInsert_.insertTask(list.get(i));
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Preferences.save(General.IS_FROM_TEAM_TASK, true);
                Intent intent = new Intent(getApplicationContext(), CreateTaskActivity.class);
                //startActivity(intent, ActivityTransition.moveToNextAnimation(getApplicationContext()));
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
        }
    }
}
