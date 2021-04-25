package com.modules.task;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetSelected;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.models.Task_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.Users_;
import com.sagesurfer.secure._Base64;
import com.sagesurfer.selectors.MultiUserSelectorDialog;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.RequestBody;


/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 08-09-2017
 *         Last Modified on 14-12-2017
 */

public class CreateTaskActivity extends AppCompatActivity implements View.OnClickListener, MultiUserSelectorDialog.SelectedUsers,
        OwnerSelectorDialog.GetOwner, TaskListSelectorDialog.GetTask {

    private static final String TAG = CreateTaskActivity.class.getSimpleName();
    //private ArrayList<Teams_> teamsArrayList;
    private ArrayList<Friends_> al_friends, al_members, al_ownerList;
    private ArrayList<Task_> al_task;
    private boolean isTeam = false;
    private int group_id = 0, owner_id = 0, task_id = 0;
    private String user_id = "0", group_name = "", start_time = "0";
    private String due_date = "0000-00-00", priority = "Low";
    private int mYear = 0, mMonth = 0, mDay = 0;
    //private RadioButton homeRadio, teamRadio;
    private TextView tv_team_name, tv_teamSelected, tv_ownerLabel, ownerText, tv_assigned, tv_date, tv_priority;
    private EditText et_title, et_description;
    private AppCompatImageView iv_addTaskList;
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
        setContentView(R.layout.create_task_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //teamsArrayList = new ArrayList<>();
        al_ownerList = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
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
        TextView tv_title = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        //tv_title.setTextColor(getResources().getColor(R.color.text_color_primary));
        tv_title.setText(this.getResources().getString(R.string.create_task));
        AppCompatImageView postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        /*int color = Color.parseColor("#a5a5a5"); //text_color_tertiary
        postButton.setColorFilter(color);
        postButton.setImageResource(R.drawable.vi_check_white);*/
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);
        /*homeRadio = (RadioButton) findViewById(R.id.create_task_home_radio);
        homeRadio.setOnClickListener(this);
        teamRadio = (RadioButton) findViewById(R.id.create_task_team_radio);
        teamRadio.setOnClickListener(this);*/
        tv_team_name = (TextView) findViewById(R.id.create_task_team_selector_label);
        tv_teamSelected = (TextView) findViewById(R.id.tv_team_name_for_task);
        tv_teamSelected.setOnClickListener(this);
        tv_ownerLabel = (TextView) findViewById(R.id.create_task_owner_label);
        ownerText = (TextView) findViewById(R.id.create_task_owner);
        ownerText.setOnClickListener(this);
        tv_assigned = (TextView) findViewById(R.id.tv_task_assigned_to);
        tv_assigned.setOnClickListener(this);
        tv_date = (TextView) findViewById(R.id.tv_task_due_date);
        tv_date.setOnClickListener(this);
        tv_priority = (TextView) findViewById(R.id.tv_task_priority);
        tv_priority.setOnClickListener(this);
        et_description = (EditText) findViewById(R.id.tv_task_description);
        et_title = (EditText) findViewById(R.id.tv_task_title);
        iv_addTaskList = (AppCompatImageView) findViewById(R.id.create_task_add_task);
        iv_addTaskList.setOnClickListener(this);
        //Log.e(TAG, "Group name "+Preferences.get(General.GROUP_NAME));
        //Log.e(TAG, "Group id "+Preferences.get(General.GROUP_ID));

    }

    private void showResponses(int status, View view) {
        String message;
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
        } else {
            status = 2;
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
        if (status == 1) {
            onBackPressed();
        }
    }

    /*//Open team selector dialog
    @SuppressLint("CommitTransaction")
    private void openTeamSelector() {
        Bundle bundle = new Bundle();
        DialogFragment dialogFrag = new SingleTeamSelectorDialog();
        bundle.putSerializable(General.TEAM_LIST, teamsArrayList);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getSupportFragmentManager().beginTransaction(), General.TEAM_LIST);
    }*/

    // make UI changes based on radio button selected (team/home task)
    private void toggleRadio() {
        isTeam = Preferences.getBoolean(General.IS_FROM_TEAM_TASK);
        tv_teamSelected.setText("");
        tv_assigned.setText("");
        ownerText.setText("");
        al_ownerList.clear();
        toggleOwner();
        user_id = "0";
        owner_id = 0;
        task_id = 0;
        if (isTeam) {
            /*teamRadio.setChecked(true);
            homeRadio.setChecked(false);
            teamSelected.setHint(this.getResources().getString(R.string.select_team));*/
            tv_team_name.setText(this.getResources().getString(R.string.team));
            iv_addTaskList.setVisibility(View.GONE);

            group_id = Integer.parseInt(Preferences.get(General.TEAM_ID));
            tv_teamSelected.setText(""+Preferences.get(General.TEAM_NAME));
            getMembers();
        } else {
            /*teamRadio.setChecked(false);
            homeRadio.setChecked(true);*/
            tv_teamSelected.setText(""+Preferences.get(General.TEAM_NAME));
            tv_team_name.setText(this.getResources().getString(R.string.select_task_list));
            iv_addTaskList.setVisibility(View.VISIBLE);
            group_id = 0;
            group_name = "";
        }
    }

    private void toggleOwner() {
        if (al_ownerList.size() > 0) {
            tv_ownerLabel.setVisibility(View.VISIBLE);
            ownerText.setVisibility(View.VISIBLE);
        } else {
            tv_ownerLabel.setVisibility(View.GONE);
            ownerText.setVisibility(View.GONE);
        }
    }

    // show priority dialog to select
    private void showPriority(final View view) {
        final PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.task_priority_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                priority = item.getTitle().toString();
                tv_priority.setText(priority);
                popup.dismiss();
                return true;
            }
        });
        popup.show();
    }

    //make network call to fetch task list before creating task
    private void getTaskList() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_TASK_FRIEND);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TASK_LIST;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    al_friends = Users_.parseTaskFriends(response, "friend_list", getApplicationContext(), TAG);
                    al_task = Alerts_.parseTaskList(response, getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //getTeams();
    }

    /*// fetch all team list
    private void getTeams() {
        teamsArrayList = PerformGetTeamsTask.get(Actions_.TASK_LIST, getApplicationContext(), TAG);
    }*/

    //open user selector dialog
    @SuppressLint("CommitTransaction")
    private void openUsersSelector(ArrayList<Friends_> list) {
        Bundle bundle = new Bundle();
        android.app.DialogFragment dialogFrag = new MultiUserSelectorDialog();
        bundle.putSerializable(Actions_.MY_FRIENDS, list);
        bundle.putBoolean(General.IS_FROM_TEAM_TASK, isTeam);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager().beginTransaction(), Actions_.MY_FRIENDS);
    }

    // open task list selector dialog
    @SuppressLint("CommitTransaction")
    private void openTaskSelector() {
        Bundle bundle = new Bundle();
        TaskListSelectorDialog dialogFrag = new TaskListSelectorDialog();
        bundle.putSerializable(General.TASK_LIST, al_task);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getSupportFragmentManager().beginTransaction(), General.TASK_LIST);
    }

    // open selected users list in dialog to chose owner from it
    @SuppressLint("CommitTransaction")
    private void openOwnerSelector() {
        Bundle bundle = new Bundle();
        OwnerSelectorDialog ownerSelectorDialog = new OwnerSelectorDialog();
        bundle.putSerializable(General.USER_LIST, al_ownerList);
        ownerSelectorDialog.setArguments(bundle);
        ownerSelectorDialog.show(getSupportFragmentManager().beginTransaction(), General.USER_LIST);
    }

    // make network call to fetch users from server
    private void getMembers() {
        if (al_members != null) {
            al_members.clear();
        } else {
            al_members = new ArrayList<>();
        }
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GROUP_FRIENDS);
        requestMap.put(General.GROUP_ID, "" + group_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_USERS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    al_members = Users_.parseTaskFriends(response, Actions_.GROUP_FRIENDS, getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // validate all fields for proper data
    private boolean validate(String task_title, String task_description) {
        if (isTeam && group_id == 0) {
            ShowSnack.textViewWarning(tv_teamSelected, this.getResources().getString(R.string.please_select_team), getApplicationContext());
            return false;
        }
        if (!isTeam && task_id == 0) {
            ShowSnack.textViewWarning(tv_teamSelected, "Please select task list", getApplicationContext());
            return false;
        }
        if (task_title == null) {
            et_title.setError("Invalid Task List Name");
            return false;
        }
        if (task_title.length() < 3) {
            et_title.setError("Min 3 char required");
            return false;
        }
        if (task_title.length() > 50) {
            et_title.setError("Max 50 char required");
            return false;
        }
        if (task_description.length() < 3 && task_description.length() > 0) {
            et_description.setError("Min 3 char required");
            return false;
        }
        if (task_description.length() > 500) {
            et_description.setError("Max 500 char required");
            return false;
        }
        return true;
    }

    // validate task list name for validity
    private void createTaskList(String task_list, EditText inputBox, Dialog dialog) {
        if (task_list == null) {
            inputBox.setError("Invalid Task List Name");
            return;
        }
        if (task_list.length() < 3) {
            inputBox.setError("Min 3 char required");
            return;
        }
        if (task_list.length() > 150) {
            inputBox.setError("Max 150 char required");
            return;
        }

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ADD_TASK_LIST);
        requestMap.put("list_name", task_list);
        requestMap.put(General.DOMAIN_CODE, Preferences.get(General.DOMAIN_CODE));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TASK_LIST;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    if (Error_.oauth(response, getApplicationContext()) == 13) {
                        ShowSnack.viewWarning(iv_addTaskList, this.getResources().getString(R.string.network_error_occurred), getApplicationContext());
                        return;
                    }
                    JsonObject jsonObject = GetJson_.getObject(response, Actions_.ADD_TASK_LIST);
                    if (jsonObject != null) {
                        if (jsonObject.get(General.STATUS).getAsInt() == 1) {
                            /*Task_ task_ = new Task_();
                            task_.setStatus(1);
                            task_.setId(jsonObject.get(General.ID).getAsInt());
                            task_.setName(jsonObject.get(General.NAME).getAsString());
                            taskArrayList.add(task_);*/
                            ShowToast.successful("Task list added", getApplicationContext());
                        } else {
                            ShowSnack.viewWarning(iv_addTaskList, this.getResources().getString(R.string.action_failed), getApplicationContext());
                        }
                    }
                } else {
                    ShowSnack.viewWarning(iv_addTaskList, this.getResources().getString(R.string.network_error_occurred), getApplicationContext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dialog.dismiss();
    }

    private void createTaskDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView) dialog.findViewById(R.id.delete_confirmation_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);
        subTitle.setVisibility(View.GONE);
        final EditText inputBox = (EditText) dialog.findViewById(R.id.delete_confirmation_input_box);
        inputBox.setVisibility(View.VISIBLE);
        title.setText(this.getResources().getString(R.string.create_task_list));

        TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTaskList(inputBox.getText().toString().trim(), inputBox, dialog);
            }
        });

        AppCompatImageButton cancelButton = (AppCompatImageButton) dialog.findViewById
                (R.id.delete_confirmation_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void dueDateConfirmation(final String task_title, final String task_description) {
        final Dialog dialog = new Dialog(CreateTaskActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView) dialog.findViewById(R.id.delete_confirmation_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);
        subTitle.setText(this.getResources().getString(R.string.task_due_date_warning));
        final EditText inputBox = (EditText) dialog.findViewById(R.id.delete_confirmation_input_box);
        inputBox.setVisibility(View.GONE);
        title.setText(getApplicationContext().getResources().getString(R.string.action_confirmation));

        TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask(task_title, task_description);
                dialog.dismiss();
            }
        });

        AppCompatImageButton cancelButton = (AppCompatImageButton) dialog.findViewById(
                R.id.delete_confirmation_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //make network call to create task
    private void createTask(String task_title, String task_description) {
        int result = 11;
        String info = DeviceInfo.get(this);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.START_TIME, start_time);
        requestMap.put(General.END_TIME, GetTime.getChatTimestamp());
        requestMap.put(General.INFO, _Base64.encode(info));
        requestMap.put(General.IP, DeviceInfo.getDeviceMAC(this));
        requestMap.put(General.ACTION, Actions_.POST_TASKLIST);
        requestMap.put(General.TITLE, task_title);
        requestMap.put(General.DESCRIPTION, task_description);
        requestMap.put(General.DUE_DATE, due_date);
        requestMap.put(General.PRIORITY, priority);
        requestMap.put("taf", "");
        requestMap.put(General.GROUP_ID, "" + group_id);
        requestMap.put("list_id", "" + task_id);
        requestMap.put("visible_to_mem", "Yes");
        requestMap.put(General.ASSIGNED_TO, user_id);
        requestMap.put(General.TASK_OWNER, "" + owner_id);
        requestMap.put(General.STATUS, "Assigned");
        requestMap.put(General.DOMAIN_CODE, Preferences.get(General.DOMAIN_CODE));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TASK_LIST;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    if (Error_.oauth(response, getApplicationContext()) == 13) {
                        result = 13;
                        showResponses(result, tv_teamSelected);
                        return;
                    }
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject != null) {
                        if (jsonObject.has(General.STATUS)) {
                            result = jsonObject.get(General.STATUS).getAsInt();
                        }
                    }
                } else {
                    result = 12;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(result, tv_teamSelected);
    }

    @Override
    public void onStart() {
        super.onStart();
        start_time = GetTime.getChatTimestamp();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        toggleRadio();
        getTaskList();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putSerializable(General.TEAM_LIST, teamsArrayList);
        outState.putBoolean(General.IS_SELECTED, isTeam);
        outState.putInt(General.GROUP_ID, group_id);
        outState.putString(General.GROUP_NAME, group_name);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            //teamsArrayList = (ArrayList<Teams_>) savedInstanceState.getSerializable(General.TEAM_LIST);
            isTeam = savedInstanceState.getBoolean(General.IS_SELECTED, false);
            group_id = savedInstanceState.getInt(General.GROUP_ID, 0);
            if (group_id != 0) {
                group_name = savedInstanceState.getString(General.GROUP_NAME);
                tv_teamSelected.setText(group_name);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_toolbar_save:
                String title = et_title.getText().toString().trim();
                String description = et_description.getText().toString().trim();
                if (validate(title, description)) {
                    if (due_date.trim().length() <= 0 || due_date.equalsIgnoreCase("0000-00-00")) {
                        dueDateConfirmation(title, description);
                    } else {
                        createTask(title, description);
                    }
                }
                break;

            case R.id.create_task_owner:
                if (al_ownerList != null && al_ownerList.size() > 0) {
                    openOwnerSelector();
                }
                break;

            case R.id.tv_task_assigned_to:
                if (isTeam) {
                    if (al_members != null && al_members.size() > 0) {
                        openUsersSelector(al_members);
                    } else {
                        ShowSnack.viewWarning(v, this.getResources().getString(R.string.teams_unavailable), getApplicationContext());
                    }
                } else {
                    if (al_friends != null && al_friends.size() > 0) {
                        openUsersSelector(al_friends);
                    } else {
                        ShowSnack.viewWarning(v, this.getResources().getString(R.string.friends_unavailable), getApplicationContext());
                    }
                }
                break;

            case R.id.tv_team_name_for_task:
                if (isTeam) {
                    if (group_id == 0) {
                    //if (teamsArrayList == null || teamsArrayList.size() <= 0) {
                        ShowSnack.viewWarning(v, this.getResources().getString(R.string.teams_unavailable), getApplicationContext());
                        return;
                    }
                    //openTeamSelector();
                } else {
                    getTaskList();
                    if (al_task != null && al_task.size() > 0) {
                        openTaskSelector();
                    }
                }
                break;
            /*case R.id.create_task_team_radio:
                isTeam = true;
                toggleRadio();
                break;
            case R.id.create_task_home_radio:
                isTeam = false;
                toggleRadio();
                break;*/
            case R.id.tv_task_due_date:
                Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                due_date = "0000-00-00";
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                due_date = year + "-"
                                        + GetCounters.checkDigit(monthOfYear) + "-"
                                        + GetCounters.checkDigit(dayOfMonth);
                                try {
                                    int result = Compare.dueDateTask(mYear + "-"
                                            + (mMonth + 1) + "-" + mDay, year, due_date);
                                    if (result == 1) {
                                        due_date = "0000-00-00";
                                        tv_date.setText("");
                                        ShowSnack.textViewWarning(tv_date, getApplicationContext()
                                                .getResources().getString(R.string.invalid_date), getApplicationContext());
                                    } else {
                                        tv_date.setText(due_date);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;

            case R.id.tv_task_priority:
                showPriority(v);
                break;

            case R.id.create_task_add_task:
                createTaskDialog();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    /*@Override
    public void getChoice(Teams_ teams_, boolean isSelected, int menu_id) {
        if (isSelected) {
            group_id = teams_.getId();
            group_name = teams_.getName();
            teamSelected.setText(group_name);
            getMembers();
        }
    }*/

    @Override
    public void selectedUsers(ArrayList<Friends_> users_arrayList, String selfCareContentId, boolean isSelected) {
        if (isTeam) {
            al_members = users_arrayList;
        } else {
            al_friends = users_arrayList;
        }
        if (isSelected) {
            user_id = GetSelected.wallUsers(users_arrayList);
            tv_assigned.setText(GetSelected.wallUsersName(users_arrayList));
        }
        al_ownerList = GetSelected.getSelectedUserList(users_arrayList);
        if (al_ownerList.size() > 0) {
            owner_id = al_ownerList.get(0).getUserId();
            ownerText.setText(al_ownerList.get(0).getName());
        }
        toggleOwner();
    }

    @Override
    public void getOwner(Friends_ friends_, boolean isSelected) {
        if (isSelected) {
            owner_id = friends_.getUserId();
            ownerText.setText(friends_.getName());
        }
    }

    @Override
    public void getSelectedTask(Task_ task_, boolean isSelected) {
        if (isSelected) {
            task_id = task_.getId();
            tv_teamSelected.setText(task_.getName());
        }
    }
}
