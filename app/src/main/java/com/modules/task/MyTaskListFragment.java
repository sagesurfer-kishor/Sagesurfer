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

public class MyTaskListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, TaskListAdapter.OnItemClickListener, View.OnClickListener {

    private static final String TAG = MyTaskListFragment.class.getSimpleName();
    private ArrayList<Task_> taskList;

    private FloatingActionButton fb_create_task;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeMenuListView listView;

    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    private DatabaseRetrieve_ databaseRetrieve_;
    private Activity activity;
    private TaskListAdapter taskListAdapter;
    private MainActivityInterface mainActivityInterface;

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

        fb_create_task = (FloatingActionButton) view.findViewById(R.id.fab);
        fb_create_task.setVisibility(View.VISIBLE);
        fb_create_task.setOnClickListener(this);

        listView = (SwipeMenuListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(0);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        taskListAdapter = new TaskListAdapter(activity, taskList, this);
        listView.setAdapter(taskListAdapter);
        listView.setDividerHeight(0);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.task_list));
        mainActivityInterface.setToolbarBackgroundColor();
        fb_create_task.setBackgroundTintList(ColorStateList.valueOf(GetColor.getHomeIconBackgroundColorColorParse(true)));

        if (taskList.size()>0){
            taskList.clear();
        }
        // fetch home task from local database and add it to list adapter
        taskList.addAll(databaseRetrieve_.retrieveTask(0));
        taskListAdapter.notifyDataSetChanged();

        if (taskList.size() <= 0) {
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        //mainActivityInterface.hideRevealView();
        swipeRefreshLayout.setRefreshing(true);
        fetchTask();
    }

    @Override
    public void onItemClickListener(Task_ task_) {
        if (task_ != null) {
            Intent detailsIntent = new Intent(activity.getApplicationContext(), TaskDetailsActivity.class);
            detailsIntent.putExtra(General.TASK_LIST, task_);
           // startActivity(detailsIntent, ActivityTransition.moveToNextAnimation(activity.getApplicationContext()));
            startActivity(detailsIntent);
            activity.overridePendingTransition(0, 0);
        }
    }

    // make network call to fetch all home task
    private void fetchTask() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.TASK_LIST);
        requestMap.put(General.LAST_DATE, "0");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.ALERT_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);

                ArrayList<Task_> tempTaskArrayList = Alerts_.parseTask(response, Actions_.TASK_LIST, General.TASK_LIST, activity.getApplicationContext(), TAG);
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    // insert newly received task to local database
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
                Preferences.save(General.IS_FROM_TEAM_TASK, false);
                Intent sosIntent = new Intent(activity.getApplicationContext(), CreateTaskActivity.class);
              //startActivity(sosIntent, ActivityTransition.moveToNextAnimation(activity.getApplicationContext()));
                startActivity(sosIntent);
                activity.overridePendingTransition(0, 0);
                break;
        }
    }
}
