package com.modules.notification;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.modules.announcement.AnnouncementDetailsActivity;
import com.modules.appointment.activity.AppointmentDetailsActivity;
import com.modules.appointment.model.Appointment_;
import com.modules.assessment.FormShowActivity;
import com.modules.beahivoural_survey.fragment.BeahivouralSurveyFragment;
import com.modules.calendar.EventDetailsActivity;
import com.modules.calendar.InviteListActivity;
import com.modules.caseload.CaseloadFragment;
import com.modules.caseload.PeerNoteDetailsActivity;
import com.modules.caseload.werhope.activity.ProgressNoteDetailsActivity;
import com.modules.fms.FileSharingOperations;
import com.modules.fms.FileSharing_;
import com.modules.journaling.activity.JournalDetailsActivity;
import com.modules.journaling.model.Journal_;
import com.modules.leave_management.fragment.LeaveListingFragment;
import com.modules.messageboard.MessageBoardDetailsActivity;
import com.modules.mood.CCMoodActivity;
import com.modules.mood.MoodFragment_;
import com.modules.onetime_dailysurvey.activity.DailySurveyDetailsActivity;
import com.modules.onetime_dailysurvey.activity.OnTimeSurveyDetailsActivity;
import com.modules.selfcare.SelfCareDetailsActivity;
import com.modules.selfcare.SelfCareFragment;
import com.modules.selfcare.UploadCareDetailsActivity;
import com.modules.selfgoal.SelfGoalDetailsActivity;
import com.modules.selfgoal.SelfGoalMainFragment;
import com.modules.sows.activity.SenjamSowsDetailsActivity;
import com.modules.sows.activity.SenjamSowsNoteDetailsActivity;
import com.modules.sows.model.SowsNotes;
import com.modules.task.TaskDetailsActivity;
import com.modules.team.PollListFragment;
import com.modules.team.TeamDetailsActivity;
import com.modules.teamtalk.activity.TalkDetailsActivity;
import com.modules.teamtalk.model.TeamTalk_;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.directory.DirectoryList;
import com.sagesurfer.download.DownloadFile;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.library.NotificationTypeDetector;
import com.sagesurfer.models.Announcement_;
import com.sagesurfer.models.CareUploaded_;
import com.sagesurfer.models.CaseloadPeerNote_;
import com.sagesurfer.models.Content_;
import com.sagesurfer.models.Event_;
import com.sagesurfer.models.Goal_;
import com.sagesurfer.models.MessageBoard_;
import com.sagesurfer.models.Task_;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.sagesurfer.parser.Notifications_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformGetTeamsTask;
import com.sagesurfer.tasks.PerformReadTask;
import com.sagesurfer.views.TextWatcherExtended;
import com.sagesurfer.webservices.Teams;
import com.storage.database.constants.TableList_;
import com.storage.database.operations.DatabaseUpdate_;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.RequestBody;

/**
 * @author Kailash Karankal
 */

public class NotificationsFragment extends Fragment {
    private ListView listView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private MainActivityInterface mainActivityInterface;
    private Activity activity;
    private ArrayList<Notification> notificationList = new ArrayList<>();
    private ArrayList<Notification> searchNotificationList;
    private static final String TAG = NotificationsFragment.class.getSimpleName();
    private ArrayList<FileSharing_> fileArrayList = new ArrayList<>();
    private PopupWindow popupWindow = new PopupWindow();
    private CardView cardViewActionsSearch;
    private EditText editTextSearch;
    private ImageButton imageButtonSetting, notificationFilterButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NotificationAdapter notificationAdapter;
    //notification filter part
    private ArrayList<String> notificationFilterIds;
    private ListView filterlistView;
    private ArrayList<Notification> notificationFilterList = new ArrayList<>();
    private NotificationFilterAdapter notificationFilterAdapter;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String date = "", end_date = "";
    private String lastWeek = "", lastMonth = "";
    private String mCurrentDate;
    private SimpleDateFormat sdfDate;
    private Date CurrentDate, StartDate;


    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mainActivityInterface = (MainActivityInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement MainActivityInterface");
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.list_view_layout, null);

        activity = getActivity();

        Preferences.initialize(activity.getApplicationContext());
        FloatingActionButton createButton = (FloatingActionButton) view.findViewById(R.id.fab_listview);
        createButton.setVisibility(View.GONE);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchNotification();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(10);
        listView.setPadding(10, 0, 10, 10);
        listView.setOnItemClickListener(onItemClick);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);
        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        cardViewActionsSearch = (CardView) view.findViewById(R.id.cardview_actions);
        imageButtonSetting = (ImageButton) view.findViewById(R.id.imagebutton_setting);
        notificationFilterButton = (ImageButton) view.findViewById(R.id.notification_filter);
        notificationFilterButton.setVisibility(View.VISIBLE);

        listView.setOnItemClickListener(onItemClick);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        notificationSrearchFunctionality(view);
        notificationFilterData();

        sdfDate = new SimpleDateFormat("MMM dd, yyyy");
        Date now = new Date();
        mCurrentDate = sdfDate.format(now);
        return view;
    }

    private void notificationFilterData() {
        notificationFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    View customView = inflater.inflate(R.layout.dialog_notification_filter, null);
                    popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    popupWindow.isShowing();

                    final SwipeRefreshLayout swipeRefreshLayout;
                    final LinearLayout dateLayout, dateSelectionLayout;
                    final TextView clearSelection, clearDateSelection;
                    final TextView startDate, endDate;
                    final ImageView imageviewSave, closeNotificationFilterDialog;
                    final Calendar calendar;
                    final CheckBox lastWeekCheckBox, lastMonthCheckBox;

                    filterlistView = (ListView) customView.findViewById(R.id.swipe_filter_listview);
                    filterlistView.setDividerHeight(3);

                    swipeRefreshLayout = customView.findViewById(R.id.swipe_refresh_layout);
                    swipeRefreshLayout.setEnabled(false);

                    dateLayout = customView.findViewById(R.id.date_layout);
                    dateSelectionLayout = customView.findViewById(R.id.date_selection);
                    lastWeekCheckBox = customView.findViewById(R.id.check_box_week);
                    lastMonthCheckBox = customView.findViewById(R.id.check_box_month);
                    clearSelection = customView.findViewById(R.id.clear_selection);
                    clearDateSelection = customView.findViewById(R.id.clear_selection_date);
                    imageviewSave = customView.findViewById(R.id.imageview_toolbar_save);
                    closeNotificationFilterDialog = customView.findViewById(R.id.imageview_back);
                    startDate = customView.findViewById(R.id.start_date_txt);
                    endDate = customView.findViewById(R.id.end_date_txt);

                    fetchModuleTypeOfNotification();

                    notificationFilterIds = new ArrayList<String>();

                    clearDateSelection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lastWeekCheckBox.setChecked(false);
                            lastMonthCheckBox.setChecked(false);
                            startDate.setText("");
                            endDate.setText("");
                        }
                    });

                    clearSelection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int size = notificationFilterList.size();
                            for (int i = 0; i < size; i++) {
                                Notification notification = notificationFilterList.get(i);
                                notification.setSelected(false);
                            }
                            notificationFilterAdapter.notifyDataSetChanged();
                        }
                    });

                    dateSelectionLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dateLayout.setVisibility(View.VISIBLE);
                            lastWeekCheckBox.setChecked(false);
                            lastMonthCheckBox.setChecked(false);
                        }
                    });

                    calendar = Calendar.getInstance();
                    mYear = calendar.get(Calendar.YEAR);
                    mMonth = calendar.get(Calendar.MONTH);
                    mDay = calendar.get(Calendar.DAY_OF_MONTH);

                    startDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                            monthOfYear = (monthOfYear + 1);
                                            sDay = dayOfMonth;
                                            sMonth = monthOfYear;
                                            sYear = year;

                                            date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                            try {
                                                startDate.setText(date);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, mYear, mMonth, mDay);
                            Calendar c = Calendar.getInstance();
                            c.add(Calendar.DAY_OF_WEEK, -6);
                            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                            datePickerDialog.show();
                        }
                    });

                    endDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatePickerDialog datePickerDialog1 = new DatePickerDialog(activity,
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                            monthOfYear = (monthOfYear + 1);
                                            sDay = dayOfMonth;
                                            sMonth = monthOfYear;
                                            sYear = year;

                                            end_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                            try {
                                                int result = Compare.validEndDate(end_date, date);
                                                if (result == 1) {
                                                    endDate.setText(end_date);
                                                } else {
                                                    end_date = null;
                                                    endDate.setText(null);
                                                    ShowSnack.textViewWarning(endDate, activity.getResources()
                                                            .getString(R.string.invalid_date), activity);

                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }, mYear, mMonth, mDay);
                            Calendar c1 = Calendar.getInstance();
                            c1.add(Calendar.DAY_OF_WEEK, -6);
                            datePickerDialog1.getDatePicker().setMaxDate(System.currentTimeMillis());
                            datePickerDialog1.show();
                        }
                    });


                    if (Preferences.getBoolean("last_week")) {
                        lastWeekCheckBox.setChecked(true);
                    } else {
                        lastWeekCheckBox.setChecked(false);
                    }

                    lastWeekCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if (checked) {
                                lastWeek = "last_week";
                                lastMonthCheckBox.setChecked(false);
                                Preferences.save("last_week", true);
                                startDate.setText("");
                                endDate.setText("");
                            } else {
                                lastWeek = "";
                                Preferences.save("last_week", false);
                            }
                        }
                    });

                    if (Preferences.getBoolean("last_month")) {
                        lastMonthCheckBox.setChecked(true);
                    } else {
                        lastMonthCheckBox.setChecked(false);
                    }

                    lastMonthCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if (checked) {
                                lastMonth = "last_month";
                                lastWeekCheckBox.setChecked(false);
                                Preferences.save("last_month", true);
                                startDate.setText("");
                                endDate.setText("");
                            } else {
                                lastMonth = "";
                                Preferences.save("last_month", false);
                            }
                        }
                    });

                    imageviewSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            filterModuleWiseNotification();
                        }
                    });

                    closeNotificationFilterDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fetchModuleTypeOfNotification() {
        if (notificationFilterList.size() > 0) {
            notificationFilterAdapter = new NotificationFilterAdapter(activity, notificationFilterList);
            filterlistView.setAdapter(notificationFilterAdapter);
        } else {
            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put(General.ACTION, Actions_.NOTIFICATION_FILTER);
            requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

            String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_YOUTH_OPERATIONS_URL;

            RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
            if (requestBody != null) {
                try {
                    String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                    if (response != null) {
                        notificationFilterList = Notifications_.parseNotificationData(response, activity.getApplicationContext(), TAG);
                        if (notificationFilterList.size() > 0) {
                            if (notificationFilterList.get(0).getStatus() == 1) {
                                notificationFilterAdapter = new NotificationFilterAdapter(activity, notificationFilterList);
                                filterlistView.setAdapter(notificationFilterAdapter);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void filterModuleWiseNotification() {
        notificationList.clear();
        showError(true, 20);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.NOTIFICATION);

        String ids = getIds();

        if (ids.equals("")) {
            if (!date.equals("") && !end_date.equals("")) {
                requestMap.put(General.DATE_TYPE, "date");
                requestMap.put(General.START_DATE, date);
                requestMap.put(General.END_DATE, end_date);
            } else if (lastWeek.equals("last_week")) {
                requestMap.put(General.DATE_TYPE, lastWeek);
            } else if (lastMonth.equals("last_month")) {
                requestMap.put(General.DATE_TYPE, lastMonth);
            } else {
                requestMap.put(General.DATE_TYPE, "0");
            }
        } else {
            requestMap.put("filter", ids);
            if (!date.equals("") && !end_date.equals("")) {
                requestMap.put(General.DATE_TYPE, "date");
                requestMap.put(General.START_DATE, date);
                requestMap.put(General.END_DATE, end_date);
            } else if (lastWeek.equals("last_week")) {
                requestMap.put(General.DATE_TYPE, lastWeek);
            } else if (lastMonth.equals("last_month")) {
                requestMap.put(General.DATE_TYPE, lastMonth);
            } else {
                requestMap.put(General.DATE_TYPE, "0");
            }
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_YOUTH_OPERATIONS_URL;
        Log.e("urlurlurl", url);
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.e("notificationResponse", response);
                if (response != null) {
                    notificationList = Notifications_.parseSpams(response, activity, TAG);
                    if (notificationList.size() > 0) {
                        if (notificationList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            notificationAdapter = new NotificationAdapter(activity, notificationList);
                            listView.setAdapter(notificationAdapter);
                            notificationAdapter.notifyDataSetChanged();
                            popupWindow.dismiss();
                        } else {
                            showError(true, notificationList.get(0).getStatus());
                            popupWindow.dismiss();
                        }
                    } else {
                        showError(true, 2);
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ModuleWiseNotification", e.getMessage());
            }
        }
        showError(true, 11);
    }

    private String getIds() {
        notificationFilterIds.clear();
        if (notificationFilterList != null && notificationFilterList.size() > 0) {
            for (int i = 0; i < notificationFilterList.size(); i++) {
                if (notificationFilterList.get(i).getSelected()) {
                    notificationFilterIds.add(String.valueOf(notificationFilterList.get(i).getId()));
                }
            }
        }
        return notificationFilterIds.toString()
                .replace("[", "")
                .replace("]", "").trim();
    }


    private void notificationSrearchFunctionality(View view) {
        cardViewActionsSearch.setVisibility(View.VISIBLE);
        editTextSearch = (EditText) view.findViewById(R.id.edittext_search);
        imageButtonSetting.setVisibility(View.GONE);
        view = (View) view.findViewById(R.id.view_part);
        view.setVisibility(View.GONE);


        editTextSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    editTextSearch.clearFocus();
                    InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                performSearch();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }

    public void performSearch() {
        searchNotificationList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (Notification notification : notificationList) {
            if (notification.getModule() != null && notification.getModule().toLowerCase().contains(searchText.toLowerCase())) {
                searchNotificationList.add(notification);
            }
        }
        if (searchNotificationList.size() > 0) {
            showError(false, 1);
            NotificationAdapter notificationAdapter = new NotificationAdapter(activity, searchNotificationList);
            listView.setAdapter(notificationAdapter);
            notificationAdapter.notifyDataSetChanged();
            notificationList = searchNotificationList;
        } else {
            showError(true, 2);
        }
    }


    private final AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            boolean isShowErrorMsg = true;
            if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 1) { //Messageboard
                Preferences.save(General.GROUP_ID, notificationList.get(position).getGroup_id());
                String response = Teams.getMessageboardDetails(TAG, activity);
                ArrayList<MessageBoard_> messageBoardArrayList = Alerts_.parseMessageBoard(response, Actions_.GET_MESSAGEBOARD, activity.getApplicationContext(), TAG);
                if (messageBoardArrayList.size() > 0) {
                    if (messageBoardArrayList.get(0).getStatus() == 1) {
                        for (int i = 0; i < messageBoardArrayList.size(); i++) {
                            if (notificationList.get(position).getRef_id() == messageBoardArrayList.get(i).getId()) {
                                isShowErrorMsg = false;
                                Intent detailsIntent = new Intent(activity.getApplicationContext(), MessageBoardDetailsActivity.class);
                                detailsIntent.putExtra(General.MESSAGEBOARD, messageBoardArrayList.get(i));
                                startActivity(detailsIntent);
                                activity.overridePendingTransition(0, 0);
                            }
                        }
                    }
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 2) {//Announcement
                Preferences.save(General.GROUP_ID, notificationList.get(position).getGroup_id());
                String response = Teams.getAnnouncement(TAG, activity);
                ArrayList<Announcement_> announcementList = Alerts_.parseAnnouncement(response, Actions_.ANNOUNCEMENT, activity.getApplicationContext(), TAG);
                if (announcementList.size() > 0) {
                    if (announcementList.get(0).getStatus() == 1) {
                        for (int i = 0; i < announcementList.size(); i++) {
                            if (notificationList.get(position).getRef_id() == announcementList.get(i).getId()) {
                                isShowErrorMsg = false;
                                Intent detailsIntent = new Intent(activity.getApplicationContext(), AnnouncementDetailsActivity.class);
                                detailsIntent.putExtra(Actions_.ANNOUNCEMENT, announcementList.get(i));
                                startActivity(detailsIntent);
                                activity.overridePendingTransition(0, 0);
                            }
                        }
                    }
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 3) { //FMS
                Preferences.save(General.GROUP_ID, notificationList.get(position).getGroup_id());
                fileArrayList = Teams.getFMS(TAG, activity);
                if (fileArrayList.size() > 0) {
                    if (fileArrayList.get(0).getStatus() == 1) {
                        for (int i = 0; i < fileArrayList.size(); i++) {
                            if (notificationList.get(position).getRef_id() == fileArrayList.get(i).getId() && fileArrayList.get(i).isFile()) {
                                isShowErrorMsg = false;
                                updateFMSReadRecord(fileArrayList.get(i).getId());
                                if (fileArrayList.get(i).getPermission() != 1) {
                                    initiatePopupWindow(view, i);
                                }
                            }
                        }
                    }
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 7) { //TeamTalk
                Preferences.save(General.GROUP_ID, notificationList.get(position).getGroup_id());
                String response = Teams.getTeamTalk(TAG, activity);
                ArrayList<TeamTalk_> teamTalkList = Alerts_.parseTalk(response, Actions_.TEAMTALK, activity.getApplicationContext(), TAG);
                if (teamTalkList.size() > 0) {
                    if (teamTalkList.get(0).getStatus() == 1) {
                        for (int i = 0; i < teamTalkList.size(); i++) {
                            if (notificationList.get(position).getRef_id() == teamTalkList.get(i).getId()) {
                                isShowErrorMsg = false;
                                Intent detailsIntent = new Intent(activity.getApplicationContext(), TalkDetailsActivity.class);
                                detailsIntent.putExtra(Actions_.TEAM_TALK, teamTalkList.get(i));
                                startActivity(detailsIntent);
                            }
                        }
                    }
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 9) { //Poll

                if (notificationList.get(position).getIs_delete() == 0) {

                    isShowErrorMsg = false;
                    Preferences.save(General.TEAM_ID, notificationList.get(position).getGroup_id());
                    Fragment fragment = new PollListFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    Fragment oldFragment = fragmentManager.findFragmentByTag(Actions_.GET_PROFILE_DATA);
                    if (oldFragment != null) {
                        ft.remove(oldFragment);
                    }
                    oldFragment = fragmentManager.findFragmentByTag(Actions_.GET_PROFILE_DATA);
                    if (oldFragment != null) {
                        ft.remove(oldFragment);
                    }
                    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                    ft.replace(R.id.app_bar_main_container, fragment, Actions_.GET_PROFILE_DATA);
                    ft.commit();
                    PerformReadTask.readAlert_One("" + notificationList.get(position).getRef_id(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);
                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 10) { //Group Invitation
                isShowErrorMsg = false;
                Log.i(TAG, "onItemClick: group invitation");
                Intent detailsIntent = new Intent(activity.getApplicationContext(), InviteListActivity.class);
                detailsIntent.putExtra(General.TEAM_ID, String.valueOf(notificationList.get(position).getGroup_id()));
                startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 11) { //My invitation
                isShowErrorMsg = false;
                Intent detailsIntent = new Intent(activity.getApplicationContext(), FriendInvitation.class);
                detailsIntent.putExtra(General.TEAM_ID, notificationList.get(position).getAdded_by());
                detailsIntent.putExtra("added_by_id", notificationList.get(position).getAdded_by_id());
                detailsIntent.putExtra("group_name", notificationList.get(position).getGroup_name());
                detailsIntent.putExtra("time_stamp", notificationList.get(position).getTimestamp());
                detailsIntent.putExtra("profile_img", notificationList.get(position).getProfile());
                startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 30) { //team invitation
                isShowErrorMsg = false;
                Intent detailsIntent = new Intent(activity.getApplicationContext(), TeamInvitation.class);
                detailsIntent.putExtra(General.TEAM_ID, notificationList.get(position).getAdded_by());
                detailsIntent.putExtra("added_by_id", notificationList.get(position).getAdded_by_id());
                detailsIntent.putExtra("group_name", notificationList.get(position).getGroup_name());
                detailsIntent.putExtra("time_stamp", notificationList.get(position).getTimestamp());
                detailsIntent.putExtra("profile_img", notificationList.get(position).getProfile());
                detailsIntent.putExtra("ref_id", notificationList.get(position).getRef_id());
                detailsIntent.putExtra("group_id", notificationList.get(position).getGroup_id());
                startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 37) { //team_request_decline
                isShowErrorMsg = false;
                PerformReadTask.readAlert_One("" + notificationList.get(position).getRef_id(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 38) { //team_request_accept
                isShowErrorMsg = false;
                PerformReadTask.readAlert_One("" + notificationList.get(position).getRef_id(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 39) { //friend_request_decline
                isShowErrorMsg = false;
                PerformReadTask.readAlert_One("" + notificationList.get(position).getRef_id(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 40) { //friend_request_accept
                isShowErrorMsg = false;
                PerformReadTask.readAlert_One("" + notificationList.get(position).getRef_id(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 41) { //share_selfcare
                isShowErrorMsg = false;
                Fragment fragment = new SelfCareFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                Fragment oldFragment = fragmentManager.findFragmentByTag(TAG);
                if (oldFragment != null) {
                    ft.remove(oldFragment);
                }
                oldFragment = fragmentManager.findFragmentByTag(TAG);
                if (oldFragment != null) {
                    ft.remove(oldFragment);
                }
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                ft.replace(R.id.app_bar_main_container, fragment, TAG);
                ft.commit();
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 12) { //TaskList
                ArrayList<Task_> taskList = new ArrayList<>();
                Preferences.save(General.GROUP_ID, notificationList.get(position).getGroup_id());
                if (notificationList.get(position).getGroup_id() == 0) {
                    taskList = Teams.getMyTaskList(TAG, activity);
                } else {
                    taskList = Teams.getGroupTaskList(TAG, activity);
                }
                if (taskList.size() > 0) {
                    if (taskList.get(0).getStatus() == 1) {
                        for (int i = 0; i < taskList.size(); i++) {
                            if (notificationList.get(position).getRef_id() == taskList.get(i).getId()) {
                                isShowErrorMsg = false;
                                Intent detailsIntent = new Intent(activity.getApplicationContext(), TaskDetailsActivity.class);
                                detailsIntent.putExtra(Actions_.TASK_LIST, taskList.get(i));
                                startActivity(detailsIntent);
                                activity.overridePendingTransition(0, 0);
                            }
                        }
                    }
                }
                PerformReadTask.readAlert_One("" + notificationList.get(position).getRef_id(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 17) { //Upload Selfcare for reviewer
                boolean action = false;
                ArrayList<CareUploaded_> careUploadedArrayList = Teams.getSelfCareUploader(Actions_.RE_PENDING, TAG, activity);
                if (careUploadedArrayList.size() > 0) {
                    if (careUploadedArrayList.get(0).getStatus() == 1) {
                        for (int i = 0; i < careUploadedArrayList.size(); i++) {
                            if (notificationList.get(position).getRef_id() == careUploadedArrayList.get(i).getId()) {
                                action = true;
                                isShowErrorMsg = false;
                                Intent detailsIntent = new Intent(activity.getApplicationContext(), UploadCareDetailsActivity.class);
                                detailsIntent.putExtra(Actions_.GET_DATA, careUploadedArrayList.get(i));
                                detailsIntent.putExtra(General.ACTION, Actions_.RE_PENDING);
                                startActivity(detailsIntent);
                                activity.overridePendingTransition(0, 0);
                            }
                        }
                        if (!action) {
                            careUploadedArrayList = Teams.getSelfCareUploader(Actions_.RE_APPROVAL, TAG, activity);
                            if (careUploadedArrayList.size() > 0) {
                                if (careUploadedArrayList.get(0).getStatus() == 1) {
                                    for (int i = 0; i < careUploadedArrayList.size(); i++) {
                                        if (notificationList.get(position).getRef_id() == careUploadedArrayList.get(i).getId()) {
                                            action = true;
                                            isShowErrorMsg = false;
                                            Intent detailsIntent = new Intent(activity.getApplicationContext(), UploadCareDetailsActivity.class);
                                            detailsIntent.putExtra(Actions_.GET_DATA, careUploadedArrayList.get(i));
                                            detailsIntent.putExtra(General.ACTION, Actions_.RE_APPROVAL);
                                            startActivity(detailsIntent);
                                        }
                                    }
                                }
                            }
                        }
                        if (!action) {
                            careUploadedArrayList = Teams.getSelfCareUploader(Actions_.RE_REJECTED, TAG, activity);
                            if (careUploadedArrayList.size() > 0) {
                                if (careUploadedArrayList.get(0).getStatus() == 1) {
                                    for (int i = 0; i < careUploadedArrayList.size(); i++) {
                                        if (notificationList.get(position).getRef_id() == careUploadedArrayList.get(i).getId()) {
                                            action = true;
                                            isShowErrorMsg = false;
                                            Intent detailsIntent = new Intent(activity.getApplicationContext(), UploadCareDetailsActivity.class);
                                            detailsIntent.putExtra(Actions_.GET_DATA, careUploadedArrayList.get(i));
                                            detailsIntent.putExtra(General.ACTION, Actions_.RE_REJECTED);
                                            startActivity(detailsIntent);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 18) { //Comment Selfcare
                ArrayList<Content_> contentArrayList = Teams.getSelfCare(TAG, activity);
                if (contentArrayList.size() > 0) {
                    if (contentArrayList.get(0).getStatus() == 1) {
                        for (int i = 0; i < contentArrayList.size(); i++) {
                            if (notificationList.get(position).getRef_id() == contentArrayList.get(i).getId()) {
                                isShowErrorMsg = false;
                                Intent detailsIntent = new Intent(activity.getApplicationContext(), SelfCareDetailsActivity.class);
                                detailsIntent.putExtra(Actions_.GET_DATA, contentArrayList.get(i));
                                startActivity(detailsIntent);
                            }
                        }
                    }
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 19) { //Decline Selfcare
                ArrayList<CareUploaded_> careUploadedArrayList = Teams.getSelfCareUploader(Actions_.REJECTED, TAG, activity);
                if (careUploadedArrayList.size() > 0) {
                    if (careUploadedArrayList.get(0).getStatus() == 1) {
                        for (int i = 0; i < careUploadedArrayList.size(); i++) {
                            if (notificationList.get(position).getRef_id() == careUploadedArrayList.get(i).getId()) {
                                isShowErrorMsg = false;
                                Intent detailsIntent = new Intent(activity.getApplicationContext(), UploadCareDetailsActivity.class);
                                detailsIntent.putExtra(Actions_.GET_DATA, careUploadedArrayList.get(i));
                                detailsIntent.putExtra(General.ACTION, Actions_.REJECTED);
                                startActivity(detailsIntent);
                            }
                        }
                    }
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 20) { //Approve Selfcare
                ArrayList<CareUploaded_> careUploadedArrayList = Teams.getSelfCareUploader(Actions_.APPROVAL, TAG, activity);
                if (careUploadedArrayList.size() > 0) {
                    if (careUploadedArrayList.get(0).getStatus() == 1) {
                        for (int i = 0; i < careUploadedArrayList.size(); i++) {
                            if (notificationList.get(position).getRef_id() == careUploadedArrayList.get(i).getId()) {
                                isShowErrorMsg = false;
                                Intent detailsIntent = new Intent(activity.getApplicationContext(), UploadCareDetailsActivity.class);
                                detailsIntent.putExtra(Actions_.GET_DATA, careUploadedArrayList.get(i));
                                detailsIntent.putExtra(General.ACTION, Actions_.APPROVAL);
                                activity.startActivity(detailsIntent);
                            }
                        }
                    }
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 21 //Event
                    || NotificationTypeDetector.getType(notificationList.get(position).getType()) == 25 //Accept Event
                    || NotificationTypeDetector.getType(notificationList.get(position).getType()) == 26) { //Decline Event
                ArrayList<Event_> eventArrayList = Teams.getEventDetails(TAG, String.valueOf(notificationList.get(position).getRef_id()), activity);
                if (eventArrayList.size() > 0) {
                    if (eventArrayList.get(0).getStatus() == 1) {
                        isShowErrorMsg = false;
                        Intent addIntent = new Intent(activity.getApplicationContext(), EventDetailsActivity.class);
                        addIntent.putExtra(Actions_.GET_EVENTS, eventArrayList.get(0));
                        activity.startActivity(addIntent);
                    }
                }
                PerformReadTask.readAlert_One("" + notificationList.get(position).getRef_id(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);

            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 22) { //Self Goal
                ArrayList<Goal_> goalArrayList = Teams.getGoalDetails(TAG, String.valueOf(notificationList.get(position).getRef_id()), activity);
                if (goalArrayList.size() > 0) {
                    if (goalArrayList.get(0).getStatus() == 1) {
                        isShowErrorMsg = false;
                        Intent addIntent = new Intent(activity.getApplicationContext(), SelfGoalDetailsActivity.class);
                        addIntent.putExtra(Actions_.MY_GOAL, goalArrayList.get(0));
                        activity.startActivity(addIntent);
                    }
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 23 //Added Notes
                    || NotificationTypeDetector.getType(notificationList.get(position).getType()) == 27 //Approved Notes
                    || NotificationTypeDetector.getType(notificationList.get(position).getType()) == 28 //Rejected Notes
                    || NotificationTypeDetector.getType(notificationList.get(position).getType()) == 29) { //Updated Notes
                ArrayList<CaseloadPeerNote_> caseloadPeerNoteArrayList = Teams.getPeerCaseloadNoteDetails(TAG, String.valueOf(notificationList.get(position).getRef_id()), activity);
                if (caseloadPeerNoteArrayList.size() > 0) {
                    if (caseloadPeerNoteArrayList.get(0).getStatus() == 1) {
                        isShowErrorMsg = false;
                        Preferences.save(General.CONSUMER_ID, caseloadPeerNoteArrayList.get(0).getConsumer_id());
                        Intent detailsIntent = new Intent(activity, PeerNoteDetailsActivity.class);
                        detailsIntent.putExtra(Actions_.NOTES, caseloadPeerNoteArrayList);
                        activity.startActivity(detailsIntent);
                    }
                }
                PerformReadTask.readAlert_One("" + notificationList.get(position).getRef_id(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);

            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 24) { //Mood
                if (notificationList.get(position).getIs_delete() == 0) {
                    if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                        isShowErrorMsg = false;
                        Bundle bundle = new Bundle();
                        Preferences.save(General.MODULE_ID, 34);
                        mainActivityInterface.toggleAdd(false);
                        mainActivityInterface.setMoodToolbar(51);
                        Preferences.save(General.TEAM_ID, notificationList.get(position).getGroup_id());
                        Fragment fragment = new MoodFragment_();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        Fragment oldFragment = fragmentManager.findFragmentByTag(TAG);
                        if (oldFragment != null) {
                            ft.remove(oldFragment);
                        }
                        oldFragment = fragmentManager.findFragmentByTag(TAG);
                        if (oldFragment != null) {
                            ft.remove(oldFragment);
                        }
                        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                        ft.replace(R.id.app_bar_main_container, fragment, TAG);
                        ft.commit();
                    } else {
                        isShowErrorMsg = false;
                        Preferences.save(General.CONSUMER_ID, notificationList.get(position).getConsumer_id());
                        Intent detailsIntent = new Intent(activity.getApplicationContext(), CCMoodActivity.class);
                        startActivity(detailsIntent);
                    }

                    PerformReadTask.readAlert_Two("" + notificationList.get(position).getRef_id(), "" + notificationList.get(position).getId(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);
                    // PerformReadTask.readAlert_One("" + notificationList.get(position).getRef_id(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);
                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 34) { //Assessment
                isShowErrorMsg = false;
                //0 : not filled; 1 : filled
                if (notificationList.get(position).getIs_filled() == 0) {
                    Intent detailsIntent = new Intent(activity.getApplicationContext(), FormShowActivity.class);
                    detailsIntent.putExtra(General.ASSESSMENT_RECORD_ID, String.valueOf(notificationList.get(position).getRef_id()));
                    detailsIntent.putExtra("assessment_form_name", String.valueOf(notificationList.get(position).getDescription()));
                    startActivity(detailsIntent);
                } else {
                    SubmitSnackResponse.showSnack(2, "Form has already been filled.", activity.getApplicationContext());
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 42 //add_journal
                    || NotificationTypeDetector.getType(notificationList.get(position).getType()) == 43 //update_journal
                    || NotificationTypeDetector.getType(notificationList.get(position).getType()) == 44) {//delete_journal

                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage030)) ||
                        Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage031))) {
                    if (notificationList.get(position).getIs_delete() == 0) {
//                        if (CheckRole.isWerHope(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                        ArrayList<SowsNotes> sowsNotesArrayList = Teams.getNoteDetails(TAG, String.valueOf(notificationList.get(position).getRef_id()), activity);
                        if (sowsNotesArrayList.size() > 0) {
                            if (sowsNotesArrayList.get(0).getStatus() == 1) {
                                isShowErrorMsg = false;
                                Intent noteDetails = new Intent(activity.getApplicationContext(), SenjamSowsNoteDetailsActivity.class);
                                noteDetails.putExtra(General.SOWS_DETAILS, sowsNotesArrayList.get(0));
//                                    journalDetails.putExtra("details", false);
                                startActivity(noteDetails);
                            }
                        }
//                        }
                        PerformReadTask.readAlert_Two("" + notificationList.get(position).getRef_id(), "" + notificationList.get(position).getId(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);
                    } else {
                        PerformReadTask.readAlert_Two("" + notificationList.get(position).getRef_id(), "" + notificationList.get(position).getId(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);
                        Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                        fetchNotification();
                    }
                } else {
                    if (notificationList.get(position).getIs_delete() == 0) {
                        if (CheckRole.isWerHope(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                            ArrayList<Journal_> journalArrayList = Teams.getJournalDetails(TAG, String.valueOf(notificationList.get(position).getRef_id()), activity);
                            if (journalArrayList.size() > 0) {
                                if (journalArrayList.get(0).getStatus() == 1) {
                                    isShowErrorMsg = false;
                                    Intent journalDetails = new Intent(activity.getApplicationContext(), JournalDetailsActivity.class);
                                    journalDetails.putExtra(General.JOURNAL, journalArrayList.get(0));
                                    journalDetails.putExtra("details", false);
                                    startActivity(journalDetails);
                                }
                            }
                        }
                        PerformReadTask.readAlert_Two("" + notificationList.get(position).getRef_id(), "" + notificationList.get(position).getId(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);
                    } else {
                        PerformReadTask.readAlert_Two("" + notificationList.get(position).getRef_id(), "" + notificationList.get(position).getId(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);
                        Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                        fetchNotification();
                    }
                }

            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 45) { //share_selfcare
                isShowErrorMsg = false;
                Fragment fragment = new SelfGoalMainFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                Fragment oldFragment = fragmentManager.findFragmentByTag(TAG);
                if (oldFragment != null) {
                    ft.remove(oldFragment);
                }
                oldFragment = fragmentManager.findFragmentByTag(TAG);
                if (oldFragment != null) {
                    ft.remove(oldFragment);
                }
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                ft.replace(R.id.app_bar_main_container, fragment, TAG);
                ft.commit();
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 46) { //bhs
                isShowErrorMsg = false;
                Preferences.save(General.NOTIFICATION_BHS_ID, notificationList.get(position).getAdded_by_id());
                Preferences.save(General.NOTIFICATION_BHS_REF_ID, notificationList.get(position).getRef_id());
                Fragment fragment = new BeahivouralSurveyFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                Fragment oldFragment = fragmentManager.findFragmentByTag(TAG);
                if (oldFragment != null) {
                    ft.remove(oldFragment);
                }
                oldFragment = fragmentManager.findFragmentByTag(TAG);
                if (oldFragment != null) {
                    ft.remove(oldFragment);
                }
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                ft.replace(R.id.app_bar_main_container, fragment, TAG);
                ft.commit();
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 47) { //assign_student
                isShowErrorMsg = false;
                PerformReadTask.readAlert_Two("" + notificationList.get(position).getRef_id(), "" + notificationList.get(position).getId(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);
                fetchNotification();
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 48) { //student_reassignment
                isShowErrorMsg = false;
                fetchNotification();
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 49) { //Leave Management
                isShowErrorMsg = false;
                Preferences.save(General.NOTIFICATION_COACH_ID, notificationList.get(position).getAdded_by_id());
                Preferences.save(General.NOTIFICATION_LEAVE_ID, notificationList.get(position).getRef_id());
                Fragment fragment = new LeaveListingFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                Fragment oldFragment = fragmentManager.findFragmentByTag(TAG);
                if (oldFragment != null) {
                    ft.remove(oldFragment);
                }
                oldFragment = fragmentManager.findFragmentByTag(TAG);
                if (oldFragment != null) {
                    ft.remove(oldFragment);
                }
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                ft.replace(R.id.app_bar_main_container, fragment, TAG);
                ft.commit();
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 46) { //BHS
                isShowErrorMsg = false;

                if (notificationList.get(position).getIs_filled() == 0) {
                } else {
                    SubmitSnackResponse.showSnack(2, "Form has already been filled.", activity.getApplicationContext());
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 50) { //unset goal
                isShowErrorMsg = false;
                fetchNotification();
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 51) {
                //progress_note

                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage023") ||
                        Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage025")) {

                    if (notificationList.get(position).getIs_delete() == 0) {
                        isShowErrorMsg = false;
                        Intent progressNoteDetails = new Intent(activity.getApplicationContext(), ProgressNoteDetailsActivity.class);
                        progressNoteDetails.putExtra(General.NOTE_FROM_NOTIFICATION, notificationList.get(position).getRef_id());
                        startActivity(progressNoteDetails);
                    } else {
                        Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                    }

                } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")) {

                    if (notificationList.get(position).getIs_delete() == 0) {
                        PerformReadTask.readAlert_One("" + notificationList.get(position).getRef_id(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);

                        if (isShowErrorMsg) {
                            fetchNotification();
                        }

                    } else {
                        Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                    }

                } else {

                }


            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 52 &&
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage023") ||
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage025")) { //edit_progress_note
                if (notificationList.get(position).getIs_delete() == 0) {
                    isShowErrorMsg = false;
                    Intent progressNoteDetails = new Intent(activity.getApplicationContext(), ProgressNoteDetailsActivity.class);
                    progressNoteDetails.putExtra(General.NOTE_FROM_NOTIFICATION, notificationList.get(position).getRef_id());
                    startActivity(progressNoteDetails);
                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 53) { //delete_progress_note
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 54) { //delete_goal
                if (notificationList.get(position).getIs_delete() == 0) {
                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 55) { //edit_leave
                if (notificationList.get(position).getIs_delete() == 0) {
                    isShowErrorMsg = false;
                    Preferences.save(General.NOTIFICATION_COACH_ID, notificationList.get(position).getAdded_by_id());
                    Preferences.save(General.NOTIFICATION_LEAVE_ID, notificationList.get(position).getRef_id());
                    Fragment fragment = new LeaveListingFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    Fragment oldFragment = fragmentManager.findFragmentByTag(TAG);
                    if (oldFragment != null) {
                        ft.remove(oldFragment);
                    }
                    oldFragment = fragmentManager.findFragmentByTag(TAG);
                    if (oldFragment != null) {
                        ft.remove(oldFragment);
                    }
                    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                    ft.replace(R.id.app_bar_main_container, fragment, TAG);
                    ft.commit();
                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 56) { //delete_leave
                if (notificationList.get(position).getIs_delete() == 0) {
                    isShowErrorMsg = false;
                    Preferences.save(General.NOTIFICATION_COACH_ID, notificationList.get(position).getAdded_by_id());
                    Preferences.save(General.NOTIFICATION_LEAVE_ID, notificationList.get(position).getRef_id());
                    Fragment fragment = new LeaveListingFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    Fragment oldFragment = fragmentManager.findFragmentByTag(TAG);
                    if (oldFragment != null) {
                        ft.remove(oldFragment);
                    }
                    oldFragment = fragmentManager.findFragmentByTag(TAG);
                    if (oldFragment != null) {
                        ft.remove(oldFragment);
                    }
                    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                    ft.replace(R.id.app_bar_main_container, fragment, TAG);
                    ft.commit();
                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 57) { //platform_youth_message
                if (notificationList.get(position).getIs_delete() == 0) {

                    HashMap<String, String> requestMap = new HashMap<>();
                    requestMap.put(General.ACTION, Actions_.CLIENT_OUTREACH);
                    requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
                    requestMap.put(General.MSG_ID, String.valueOf(notificationList.get(position).getRef_id()));

                    String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_DETAILS_CALL;
                    RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
                    if (requestBody != null) {
                        try {
                            String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray clientOutReach = jsonObject.getJSONArray("client_outreach");

                                for (int i = 0; i < clientOutReach.length(); i++) {
                                    JSONObject object = clientOutReach.getJSONObject(i);
                                    if (object.getString("status").equals("1")) {
                                        showClientOtReachMessage(object.getString("subject"), object.getString("message"));
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    isShowErrorMsg = false;
                    fetchNotification();
                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 58) { //add_appointment
                if (notificationList.get(position).getIs_delete() == 0) {
                    ArrayList<Appointment_> appointmentArrayList = Teams.getAppointmentDetails(TAG, String.valueOf(notificationList.get(position).getRef_id()), activity);
                    if (appointmentArrayList.size() > 0) {
                        if (appointmentArrayList.get(0).getStatus() == 1) {
                            isShowErrorMsg = false;
                            Intent appointmentDetails = new Intent(activity, AppointmentDetailsActivity.class);
                            appointmentDetails.putExtra("showIcon", true);
                            appointmentDetails.putExtra(General.APPOINTMENT, appointmentArrayList.get(0));
                            startActivity(appointmentDetails);
                        }
                    }
                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 59) { //updated_appointment
                if (notificationList.get(position).getIs_delete() == 0) {
                    ArrayList<Appointment_> appointmentArrayList = Teams.getAppointmentDetails(TAG, String.valueOf(notificationList.get(position).getRef_id()), activity);
                    if (appointmentArrayList.size() > 0) {
                        if (appointmentArrayList.get(0).getStatus() == 1) {
                            isShowErrorMsg = false;
                            Intent appointmentDetails = new Intent(activity, AppointmentDetailsActivity.class);
                            appointmentDetails.putExtra("showIcon", true);
                            appointmentDetails.putExtra(General.APPOINTMENT, appointmentArrayList.get(0));
                            startActivity(appointmentDetails);
                        }
                    }
                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 60) { //cancel_appointment
                if (notificationList.get(position).getIs_delete() == 0) {

                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 61) { //rescheduled_appointment
                if (notificationList.get(position).getIs_delete() == 0) {
                    ArrayList<Appointment_> appointmentArrayList = Teams.getAppointmentDetails(TAG, String.valueOf(notificationList.get(position).getRef_id()), activity);
                    if (appointmentArrayList.size() > 0) {
                        if (appointmentArrayList.get(0).getStatus() == 1) {
                            isShowErrorMsg = false;
                            Intent appointmentDetails = new Intent(activity, AppointmentDetailsActivity.class);
                            appointmentDetails.putExtra("showIcon", true);
                            appointmentDetails.putExtra(General.APPOINTMENT, appointmentArrayList.get(0));
                            startActivity(appointmentDetails);
                        }
                    }
                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 62) { //delete_appointment
                if (notificationList.get(position).getIs_delete() == 0) {

                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 63) { //peer_supervisor_notification
                if (notificationList.get(position).getIs_delete() == 0) {
                    Preferences.save(General.GROUP_ID, notificationList.get(position).getGroup_id());
                    ArrayList<Teams_> teamsArrayList = PerformGetTeamsTask.get(Actions_.TEAM_DATA, activity, TAG, true, activity);
                    if (teamsArrayList.size() > 0) {
                        if (teamsArrayList.get(0).getStatus() == 1) {
                            isShowErrorMsg = false;
                            Preferences.save(General.BANNER_IMG, teamsArrayList.get(0).getBanner());
                            Preferences.save(General.TEAM_ID, teamsArrayList.get(0).getId());
                            Preferences.save(General.TEAM_NAME, teamsArrayList.get(0).getName());
                            Preferences.save("Owner_ID", teamsArrayList.get(0).getOwnerId());

                            Intent detailsIntent = new Intent(activity.getApplicationContext(), TeamDetailsActivity.class);
                            detailsIntent.putExtra(General.TEAM, teamsArrayList.get(0));
                            detailsIntent.putExtra("showIcon", false);
                            startActivity(detailsIntent);
                        }
                    }
                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 64 /*&&
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage030") ||
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage031")*/) { //progress_note_mhaw
                if (notificationList.get(position).getIs_delete() == 0) {
                    isShowErrorMsg = false;
                    Intent progressNoteDetails = new Intent(activity.getApplicationContext(), SenjamSowsDetailsActivity.class);
                    progressNoteDetails.putExtra(General.SOWS_FROM_NOTIFICATION, notificationList.get(position).getRef_id());
                    startActivity(progressNoteDetails);
                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 65) { //immunity_survey (Daily Survey)
                if (notificationList.get(position).getIs_delete() == 0) {
                    isShowErrorMsg = false;
                    Intent oneTimeSurvey = new Intent(activity.getApplicationContext(), DailySurveyDetailsActivity.class);
                    oneTimeSurvey.putExtra(General.SOWS_FROM_NOTIFICATION, notificationList.get(position).getRef_id());
                    oneTimeSurvey.putExtra("added_by_id", notificationList.get(position).getAdded_by_id());
                    startActivity(oneTimeSurvey);
                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 66) { //submit_one_time_survey (OneTime Survey)
                if (notificationList.get(position).getIs_delete() == 0) {
                    isShowErrorMsg = false;
                    Intent dailySurvey = new Intent(activity.getApplicationContext(), OnTimeSurveyDetailsActivity.class);
                    dailySurvey.putExtra(General.SOWS_FROM_NOTIFICATION, notificationList.get(position).getRef_id());
                    dailySurvey.putExtra("added_by_id", notificationList.get(position).getAdded_by_id());
                    startActivity(dailySurvey);

                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 67) { // (Daily Dosing)
                if (notificationList.get(position).getIs_delete() == 0) {
                    isShowErrorMsg = false;
                    if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                        String ref_id = String.valueOf(notificationList.get(position).getRef_id());
                        String ampm = notificationList.get(position).getAmpm();
                        String ampmMsg = notificationList.get(position).getAmpm_msg();
                        String date = getDateForDailyDosing(notificationList.get(position).getTimestamp());
                        Log.e("StartDate", "" + date);
                        ((MainActivity) getActivity()).showDailyDosingComplianceDialog(date, ref_id, ampm, ampmMsg, activity);
                    } else {

                    }

                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 68) { //submit_one_time_survey (OneTime Survey)
                if (notificationList.get(position).getIs_delete() == 0) {
                    isShowErrorMsg = false;
                    if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                        try {
                            CurrentDate = sdfDate.parse(mCurrentDate);
                            StartDate = sdfDate.parse(getDate(notificationList.get(position).getTimestamp()));

//                            Log.e("CurrentDate", "" + CurrentDate);
//                            Log.e("StartDate", "" + StartDate);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

//                        if (StartDate.before(CurrentDate)) {  // TODO : if Date is Pervious from ToDay Date then Dialog not open otherwise open dialog
//                            Toast.makeText(getActivity(), "You can not submit Daily Survey.", Toast.LENGTH_LONG).show();
//                        } else {
//                            ((MainActivity) getActivity()).getImmunityRespiratoryCovid19SurveyListAPICalled(true, activity);
//                        }

                        if (StartDate.before(CurrentDate)) { // TODO : if Date is Pervious from ToDay Date then Dialog not open
//                            ((MainActivity) getActivity()).AlertDialogForMessage("You can not submit Daily Survey.");
                            Toast.makeText(getActivity(), "You can not submit Daily Survey.", Toast.LENGTH_LONG).show();
                        } else {
                            if (Preferences.getBoolean(General.SHOW_DAILY_SURVEY_FILLED)) {  // TODO : if show_daily_survey_filled is true then open dialog otherwise give message in Toast.
                                ((MainActivity) getActivity()).getImmunityRespiratoryCovid19SurveyListAPICalled(true, activity);
                            } else {
                                Toast.makeText(getActivity(), "You already submitted Daily Survey.", Toast.LENGTH_LONG).show();
//                                ((MainActivity) getActivity()).AlertDialogForMessage("You already submitted Daily Survey.");
                            }
                        }
                    }
                } else {
                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
                }
            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 69) { //caseload condition newly aadded by kishor k 06/08/2020
                isShowErrorMsg = false;
                Fragment fragment = new CaseloadFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                Fragment oldFragment = fragmentManager.findFragmentByTag(TAG);
                if (oldFragment != null) {
                    ft.remove(oldFragment);
                }
                oldFragment = fragmentManager.findFragmentByTag(TAG);
                if (oldFragment != null) {
                    ft.remove(oldFragment);
                }
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                ft.replace(R.id.app_bar_main_container, fragment, TAG);
                ft.commit();
            } else
                if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 70) { //caseload condition newly aadded by kishor k 06/08/2020
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_group_invition);

                String action = "decline_invite";
                String userId = Preferences.get(General.USER_ID);
                String GUID = String.valueOf(notificationList.get(position).getGroup_id());
                String member_Id = userId;
                Log.i(TAG, "Member_user_id : "+notificationList.get(position).getMember_user_id());
                RelativeLayout linearLayout = dialog.findViewById(R.id.invition_status);
                Log.i(TAG, "onItemClick: title 2 "+notificationList.get(position).getTitle1());
                Log.i(TAG, "onItemClick: title  "+notificationList.get(position).getTitle());
                Log.i(TAG, "onItemClick: title  "+notificationList.get(position).getGroup_type());
                    Log.i(TAG, "onItemClick: check user id "+Preferences.get(General.USER_ID) +" owner id"+notificationList.get(position).getGroup_owner_id());
                if (notificationList.get(position).getIs_member().equals("1")) {
                    linearLayout.setVisibility(View.GONE);
                }else if(notificationList.get(position).getGroup_type().equals("private")){
                    linearLayout.setVisibility(View.GONE);

                } else if (notificationList.get(position).getGroup_owner_id().equalsIgnoreCase(Preferences.get(General.USER_ID))){
                    linearLayout.setVisibility(View.GONE);
                }else{
                    linearLayout.setVisibility(View.VISIBLE);
                }

                TextView txtMessage = dialog.findViewById(R.id.txt_friend_request);
                txtMessage.setText(notificationList.get(position).getTitle1());

                TextView gType = dialog.findViewById(R.id.txt_group_type);

                if (notificationList.get(position).getGroup_type() != null) {
                    gType.setText(notificationList.get(position).getGroup_type());
                } else {
                    gType.setText("");
                }


                TextView txtGName = dialog.findViewById(R.id.txt_request_type);
                if (notificationList.get(position).getGroup_name() != null) {
                    txtGName.setText(notificationList.get(position).getGroup_name());
                } else {
                    txtGName.setText("");
                }


                TextView txt_timestamp = dialog.findViewById(R.id.txt_timestamp);
                String date = getDateForDailyDosing(notificationList.get(position).getTimestamp());
                if (date != null) {
                    txt_timestamp.setText(date);
                } else {
                    txt_timestamp.setText("");
                }

                TextView txtGPassword = dialog.findViewById(R.id.txt_password_request);
                if (notificationList.get(position).getGroup_password() != null) {
                    txtGPassword.setText(notificationList.get(position).getGroup_password());
                } else {
                    txtGPassword.setText("");
                }

                LinearLayout linearLayout2 = dialog.findViewById(R.id.lv_password_info);

                if (notificationList.get(position).getGroup_type().equals("password")) {
                    linearLayout2.setVisibility(View.VISIBLE);
                } else {
                    linearLayout2.setVisibility(View.GONE);
                }

                ImageView close = dialog.findViewById(R.id.close_invition);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Button btnDecline = dialog.findViewById(R.id.btnDeline_request);
                btnDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "onClick: memberID"+member_Id);
                        Log.i(TAG, "onClick: userID"+userId);
                        Log.i(TAG, "onClick: userGUID"+GUID);
                        declineinvite(action, userId, GUID, member_Id, dialog);
                    }
                });

                Button btnAccept = dialog.findViewById(R.id.btnAccept_request);
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // join group by self
                        String GUID, groupType, password;
                        GUID = String.valueOf(notificationList.get(position).getGroup_id());
                        Log.e("GUID is -> "," "+GUID);
                        groupType = notificationList.get(position).getGroup_type();
                        password = notificationList.get(position).getGroup_password();

                        final String action = "accept_invite";
                        final String userId = Preferences.get(General.USER_ID);
                        final String groupId = GUID;
                        final String memberId = notificationList.get(position).getMember_user_id();

                        Log.e("notification params ", "GUID "+GUID + "groupType " + groupType + "password" + password +" memberId"+ memberId);
                        CometChat.joinGroup(GUID, groupType, password, new CometChat.CallbackListener<Group>() {
                            @Override
                            public void onSuccess(Group joinedGroup) {
                                Log.d(TAG, joinedGroup.toString());
                                // update self joined group in our db
                              /*  GetGroupsCometchat item = groupList.get(position);
                                item.setIs_member(1);
                                groupList.set(position, item);
                                notifyItemChanged(position);*/
                                selfinvite(action, userId, groupId, memberId, dialog);
                            }

                            @Override
                            public void onError(CometChatException e) {
                                Log.d(TAG, "Group joining failed with exception: " + e.getMessage());

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                                builder1.setMessage(e.getMessage());
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();


                            }
                        });

                    }
                });
                TextView txt_request_message = dialog.findViewById(R.id.txt_request_message);

                if (notificationList.get(position).getIs_decline().equals("1")) {
                    btnDecline.setVisibility(View.GONE);
                    btnAccept.setVisibility(View.GONE);
                    txt_request_message.setVisibility(View.VISIBLE);
                    txt_request_message.setText("Group invitation already declined");
                }

                if (notificationList.get(position).getIs_member().equals("1")) {
                    btnDecline.setVisibility(View.GONE);
                    btnAccept.setVisibility(View.GONE);
                    txt_request_message.setVisibility(View.VISIBLE);
                    txt_request_message.setText("Group invitation already accepted");
                }


                if (notificationList.get(position).getIs_added().equals("1") && notificationList.get(position).getIs_member().equals("1")) {
                    btnDecline.setVisibility(View.GONE);
                    btnAccept.setVisibility(View.GONE);
                    txt_request_message.setVisibility(View.GONE);
                }

                dialog.show();

            } else {
                PerformReadTask.readAlert_Two("" + notificationList.get(position).getRef_id(), "" + notificationList.get(position).getId(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);
                //  PerformReadTask.readAlert_One("" + notificationList.get(position).getRef_id(), notificationList.get(position).getSub_type(), TAG, activity.getApplicationContext(), activity);
                if (isShowErrorMsg) {
                    SubmitSnackResponse.showSnack(2, "Record has been deleted.", activity.getApplicationContext());
                    fetchNotification();
                }
            }

//            else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 58) { //edit_progress_note_mhaw
//                if (notificationList.get(position).getIs_delete() == 0) {
//                    isShowErrorMsg = false;
//                    Intent progressNoteDetails = new Intent(activity.getApplicationContext(), MhawProgressNoteDetailsActivity.class);
//                    progressNoteDetails.putExtra(General.NOTE_FROM_NOTIFICATION, notificationList.get(position).getRef_id());
//                    startActivity(progressNoteDetails);
//                } else {
//                    Toast.makeText(activity, "Record has been deleted", Toast.LENGTH_LONG).show();
//                }
//            } else if (NotificationTypeDetector.getType(notificationList.get(position).getType()) == 59) { //delete_progress_note_mhaw
//            }


        }

        private void showClientOtReachMessage(String title1, String description1) {
            View view = getLayoutInflater().inflate(R.layout.dialog_outreach_message, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(view);
            final AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.setCanceledOnTouchOutside(false);

            WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
            wmlp.gravity = Gravity.CENTER;

            TextView subject = view.findViewById(R.id.subject_txt);
            TextView message = view.findViewById(R.id.message_txt);
            ImageView closeIcon = view.findViewById(R.id.close_icon);

            subject.setText(title1);
            message.setText(description1);

            closeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();
        }
    };

    private void declineinvite(String action, String userId, String guid, String member_id, Dialog dialog) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, userId);
        requestMap.put(General.GROUP_ID, guid);
        requestMap.put("member_id", member_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getActivity());
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getActivity());
                if (response != null) {
                    Toast.makeText(getActivity(), "request decline successfully", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    fetchNotification();
                    //notifyAll();
                    //MyFirebaseMessagingService.subscribeGroupNotification(userId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void selfinvite(String action, String userId, String gId, String memberId, Dialog dialog) {

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, userId);
        requestMap.put(General.GROUP_ID, gId);
        requestMap.put("member_id", memberId);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;
        Log.e("accept", url + requestMap);
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getActivity());
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getActivity());
                if (response != null) {
                    Toast.makeText(getActivity(), "request accepted successfully", Toast.LENGTH_LONG).show();
                    fetchNotification();
                    dialog.dismiss();
                    //MyFirebaseMessagingService.subscribeGroupNotification(userId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private void updateFMSReadRecord(int file_id) {
        // Make network call to read entry
        int status = PerformReadTask.readAlert("" + file_id, General.FMS_GRP, TAG, activity.getApplicationContext(), activity);
        if (status == 1) {
            // Update record for read/unread
            DatabaseUpdate_ databaseUpdate_ = new DatabaseUpdate_(activity.getApplicationContext());
            databaseUpdate_.updateRead(General.IS_READ, TableList_.TABLE_FMS, "1", "" + file_id);
        }
    }

    //open file details pop up with file details
    @SuppressLint("InflateParams")
    private void initiatePopupWindow(View v, final int position) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        try {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View customView = inflater.inflate(R.layout.file_details_pop_up_dialog, null);
            popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            AppCompatImageView icon = (AppCompatImageView) customView.findViewById(R.id.file_details_pop_up_icon);
            RelativeLayout background = (RelativeLayout) customView.findViewById(R.id.file_details_pop_up_icon_background);
            AppCompatImageButton closeButton = (AppCompatImageButton) customView.findViewById(R.id.file_details_pop_up_cancel);

            TextView fileName = (TextView) customView.findViewById(R.id.file_details_pop_up_file_name);
            TextView nameText = (TextView) customView.findViewById(R.id.file_details_pop_up_name);
            TextView dateText = (TextView) customView.findViewById(R.id.file_details_pop_up_time);
            TextView sizeText = (TextView) customView.findViewById(R.id.file_details_pop_up_file_size);
            TextView commentText = (TextView) customView.findViewById(R.id.file_details_pop_up_comment);
            TextView statusText = (TextView) customView.findViewById(R.id.file_details_pop_up_file_status);
            TextView downloadButton = (TextView) customView.findViewById(R.id.file_details_pop_up_download);

            fileName.setText(fileArrayList.get(position).getRealName());
            nameText.setText(fileArrayList.get(position).getFullName());
            dateText.setText(GetTime.wallTime(fileArrayList.get(position).getDate()));
            sizeText.setText(FileOperations.bytes2String(fileArrayList.get(position).getSize()));
            commentText.setText(fileArrayList.get(position).getComment());

            if (fileArrayList.get(position).getCheckIn() == 1) {
                statusText.setText(activity.getApplicationContext().getResources().getString(R.string.closed));
            } else {
                statusText.setText(activity.getApplicationContext().getResources().getString(R.string.open));
            }

            background.setBackgroundColor(activity.getApplicationContext().getResources().getColor(GetColor.getFileIconBackgroundColor(fileArrayList.get(position).getRealName())));
            icon.setImageResource(GetThumbnails.fileSharing(fileArrayList.get(position).getRealName()));

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDownload(fileArrayList.get(position).getId(), fileArrayList.get(position).getRealName());
                    popupWindow.dismiss();
                }
            });
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
            customView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get file download url and send it to global downloader for downloading
    private void getDownload(int id, String real_name) {
        String url = FileSharingOperations.getDownload("" + id, activity.getApplicationContext(), activity);
        if (url.trim().length() > 0) {
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.download(id, url, real_name, DirectoryList.DIR_SHARED_FILES, activity);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.notification));
        mainActivityInterface.setToolbarBackgroundColor();
        //Call API for fetch Notification Data
        fetchNotification();
    }

    private void fetchNotification() {
        notificationList.clear();

        showError(true, 20);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.NOTIFICATION);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_YOUTH_OPERATIONS_URL;
        Log.e("fetchNotificationurl", url);
        Log.e("noti id", Preferences.get(General.USER_ID));

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.e("NOTIFICATION_LIST-->", response);
                if (response != null) {
                    notificationList = Notifications_.parseSpams(response, activity.getApplicationContext(), TAG);
                    if (notificationList.size() > 0) {
                        if ((notificationList.get(0).getStatus() == 1 && notificationList.get(0).getType() != null) || notificationList.get(0).getRef_type() != null) {
                            showError(false, 1);
                            notificationAdapter = new NotificationAdapter(activity, notificationList);
                            listView.setAdapter(notificationAdapter);
                            notificationAdapter.notifyDataSetChanged();
                        } else {
                            showError(true, notificationList.get(0).getStatus());
                        }
                    } else {
                        showError(true, 2);
                    }
                    return;
                }
            } catch (Exception e) {
//                e.printStackTrace();
                Log.e("fetchNotificationError", e.getMessage());
            }
        }
        showError(true, 11);
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

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy", cal).toString();
        return date;
    }

    private String getDateForDailyDosing(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("yyyy-MM-dd", cal).toString();
        return date;
    }
}
