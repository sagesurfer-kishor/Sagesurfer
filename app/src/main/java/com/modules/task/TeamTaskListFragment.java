package com.modules.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.modules.team.TeamDetailsActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
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
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 17-07-2017
 *         Last Modified on 14-12-2017
 */

public class TeamTaskListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, TaskListAdapter.OnItemClickListener, View.OnClickListener {

    private static final String TAG = TeamTaskListFragment.class.getSimpleName();
    private ArrayList<Task_> taskList;

    private FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeMenuListView listView;

    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    private DatabaseRetrieve_ databaseRetrieve_;
    private Activity activity;
    private TaskListAdapter adapter_taskList;
    private MainActivityInterface mainActivityInterface;
    String lastDate = "0";
    private AppCompatImageView postButton;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.list_view_layout, null);

        activity = getActivity();
        databaseRetrieve_ = new DatabaseRetrieve_(activity.getApplicationContext());

        taskList = new ArrayList<>();

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
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
        //Changed after discussion with Sagar and Nirmal as Sagar have implemented owner_id and isModerator on backend for all instances
        /*if (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
                || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }*/

        if (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
                || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1") || General.isCurruntUserHasPermissionToTeamTalkActions()) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

        listView = (SwipeMenuListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(0);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        adapter_taskList = new TaskListAdapter(activity, taskList, this);
        listView.setAdapter(adapter_taskList);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);
        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.task_list));
        mainActivityInterface.setToolbarBackgroundColor();
        fab.setBackgroundTintList(ColorStateList.valueOf(GetColor.getHomeIconBackgroundColorColorParse(true)));

        lastDate = "0";
        onRefresh();

        try{
            ((TeamDetailsActivity) getActivity()).inviteButtonSetVisibility();
        }catch (Exception e){
            e.printStackTrace();
        }
        /*if (taskList.size() > 0) {
            taskList.clear();
        }
        taskList.addAll(databaseRetrieve_.retrieveTask(1));
        taskListAdapter.notifyDataSetChanged();*/

        /*if (taskList.size() <= 0) {
            lastDate = "0";
            onRefresh();
        }*/ /*else {
            lastDate = databaseRetrieve_.retrieveUpdateLog(General.GROUP_TASK_LIST);
            onRefresh();
        }*/
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
            Intent detailsIntent = new Intent(activity.getApplicationContext(), TaskDetailsActivity.class);
            detailsIntent.putExtra(General.TASK_LIST, task_);
            startActivity(detailsIntent);
            activity.overridePendingTransition(0, 0);
        }
    }

    private void fetchTask() {
        HashMap<String, String> requestMap = new HashMap<>();
        /*requestMap.put(General.ACTION, Actions_.TASK_LIST_GROUP);
        requestMap.put(General.LAST_DATE, databaseRetrieve_.retrieveUpdateLog(General.GROUP_TASK_LIST));
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.ALERT_URL;*/
        requestMap.put(General.ACTION, Actions_.TASKLIST);
        requestMap.put(General.LAST_DATE, lastDate);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DATA;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                taskList = Alerts_.parseTask(response, Actions_.TASKLIST, General.GROUP_TASK_LIST, activity.getApplicationContext(), TAG);
                if (taskList.size() > 0) {
                    if (taskList.get(0).getStatus() == 1) {
                        saveTask(taskList);
                        adapter_taskList = new TaskListAdapter(activity, taskList, this);
                        listView.setAdapter(adapter_taskList);
                    } else {
                        showError(true, taskList.get(0).getStatus());
                    }
                } else {
                    showError(true, 12);
                }
                /*ArrayList<Task_> tempTaskArrayList = Alerts_.parseTask(response, Actions_.TASKLIST, General.GROUP_TASK_LIST, activity.getApplicationContext(), TAG);
                if (tempTaskArrayList.size() > 0) {
                    if (tempTaskArrayList.get(0).getStatus() == 1) {
                        saveTask(tempTaskArrayList);
                        taskList.addAll(tempTaskArrayList);
                        taskListAdapter.notifyDataSetChanged();
                    } else {
                        showError(true, tempTaskArrayList.get(0).getStatus());
                    }
                } else {
                    showError(true, 12);
                }*/

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
            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
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
                DatabaseInsert_ databaseInsert_ = new DatabaseInsert_(activity.getApplicationContext());
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
                Intent intent = new Intent(activity.getApplicationContext(), CreateTaskActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(0, 0);
                break;
        }
    }
}
