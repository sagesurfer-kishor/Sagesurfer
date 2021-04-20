package com.modules.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.modules.announcement.AnnouncementDetailsActivity;
import com.modules.appointment.model.Appointment_;
import com.modules.appointment.model.Staff;
import com.modules.calendar.EventDetailsActivity;
import com.modules.calendar.InviteListActivity;
import com.modules.caseload.PeerNoteDetailsActivity;
import com.modules.fms.FileSharingOperations;
import com.modules.fms.FileSharing_;
import com.modules.goal_assignment.model.AssignedGoals;
import com.modules.messageboard.MessageBoardDetailsActivity;
import com.modules.mood.MoodAddActivity;
import com.modules.mood.MoodFragment_;
import com.modules.selfcare.SelfCareDetailsActivity;
import com.modules.selfcare.UploadCareDetailsActivity;
import com.modules.selfgoal.SelfGoalDetailsActivity;
import com.modules.task.TaskDetailsActivity;
import com.modules.team.PollListFragment;
import com.modules.team.TeamDetailsActivity;
import com.modules.teamtalk.activity.TalkDetailsActivity;
import com.modules.teamtalk.model.TeamTalk_;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.Broadcast;
import com.sagesurfer.constant.General;
import com.sagesurfer.constant.Quote;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.directory.DirectoryList;
import com.sagesurfer.download.DownloadFile;
import com.sagesurfer.icons.GetHomeMenuIcon;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetFragments;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.library.NotificationTypeDetector;
import com.sagesurfer.logger.Logger;
import com.sagesurfer.models.Announcement_;
import com.sagesurfer.models.CareUploaded_;
import com.sagesurfer.models.CaseloadPeerNote_;
import com.sagesurfer.models.Content_;
import com.sagesurfer.models.DrawerMenu_;
import com.sagesurfer.models.Event_;
import com.sagesurfer.models.Goal_;
import com.sagesurfer.models.HomeMenu_;
import com.sagesurfer.models.HomeRecentUpdates_;
import com.sagesurfer.models.MessageBoard_;
import com.sagesurfer.models.Task_;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.sagesurfer.parser.Appointments_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.HomeParser_;
import com.sagesurfer.parser.Login_;
import com.sagesurfer.parser.SelfGoal_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformGetTeamsTask;
import com.sagesurfer.tasks.PerformLogoutTask;
import com.sagesurfer.tasks.PerformReadTask;
import com.sagesurfer.webservices.Teams;
import com.storage.database.constants.TableList_;
import com.storage.database.operations.DatabaseUpdate_;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.RequestBody;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 15/03/2018
 * Last Modified on
 */

public class HomeFragment extends Fragment implements HomeRecentUpdateAdapter.HomeRecentUpdateAdapterListener, PositiveQuoteAdapter.PositiveQuoteListener {
    private static final String TAG = HomeFragment.class.getSimpleName();
    @BindView(R.id.linearlayout_home)
    LinearLayout linearLayoutHome;
    @BindView(R.id.linearlayout_home_one)
    LinearLayout linearLayoutHomeOne;
    @BindView(R.id.button_home_one)
    Button buttonHomeOne;
    @BindView(R.id.imageview_home_one)
    AppCompatImageView imageViewHomeOne;
    @BindView(R.id.linearlayout_home_two)
    LinearLayout linearLayoutHomeTwo;
    @BindView(R.id.button_home_two)
    Button buttonHomeTwo;
    @BindView(R.id.imageview_home_two)
    AppCompatImageView imageViewHomeTwo;
    @BindView(R.id.linearlayout_home_three)
    LinearLayout linearLayoutHomeThree;
    @BindView(R.id.button_home_three)
    Button buttonHomeThree;
    @BindView(R.id.imageview_home_three)
    AppCompatImageView imageViewHomeThree;
    @BindView(R.id.linearlayout_home_four)
    LinearLayout linearLayoutHomeFour;
    @BindView(R.id.button_home_four)
    Button buttonHomeFour;
    @BindView(R.id.imageview_home_four)
    AppCompatImageView imageViewHomeFour;
    @BindView(R.id.linearlayout_home_five)
    LinearLayout linearLayoutHomeFive;
    @BindView(R.id.button_home_five)
    Button buttonHomeFive;
    @BindView(R.id.imageview_home_five)
    AppCompatImageView imageViewHomeFive;
    @BindView(R.id.linearlayout_home_six)
    LinearLayout linearLayoutHomeSix;
    @BindView(R.id.button_home_six)
    Button buttonHomeSix;
    @BindView(R.id.imageview_home_six)
    AppCompatImageView imageViewHomeSix;
    @BindView(R.id.textview_home_one)
    TextView textViewHomeOne;
    @BindView(R.id.textview_home_two)
    TextView textViewHomeTwo;
    @BindView(R.id.textview_home_three)
    TextView textViewHomeThree;
    @BindView(R.id.textview_home_four)
    TextView textViewHomeFour;
    @BindView(R.id.textview_home_five)
    TextView textViewHomeFive;
    @BindView(R.id.textview_home_six)
    TextView textViewHomeSix;
    @BindView(R.id.app_bar_main_container)
    FrameLayout app_bar_main_container;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private List<HomeMenu_> homeMenuList;
    private List<DrawerMenu_> drawerMenuList;
    private FragmentActivity myContext;
    private View view;
    private int color;
    private TextView textViewUsername, textViewMood, textViewRecentUpdates;
    private RecyclerView recyclerView;
    private HomeRecentUpdateAdapter homeRecentUpdatesAdapter;
    private ArrayList<HomeRecentUpdates_> recentUpdatesArrayList = new ArrayList<>();
    private ArrayList<FileSharing_> fileArrayList = new ArrayList<>();
    private PopupWindow popupWindow = new PopupWindow();
    private Button countRecentUpdates;
    private LinearLayout headerMoodLayout;
    // for positive quote
    private RecyclerView positiveQuoteList;
    private PositiveQuoteAdapter positiveQuoteAdapter;
    final int speed = 10000; // delay time
    final Handler handler = new Handler();
    // show Behaviour
    private Boolean quesYes, quesNo, quesNa, quesOneYes, quesOneNo, quesOneNa, quesThreeYes, quesTwoNo, quesTwoNa;
    //reset password
    private Dialog dialog;
    PerformLogoutTask performLogoutTask;
    private Boolean otpVerify = true;
    //Goal popup
    private ArrayList<AssignedGoals> goalArrayList = new ArrayList<>();
    private ArrayList<String> goalIds = new ArrayList<String>();
    // for appointment
    private ArrayList<Appointment_> appointmentArrayList = new ArrayList<>();
    private String yesNoValue = "", ratingValue = "0";
    private Window window;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myContext = (FragmentActivity) activity;
        mainActivityInterface = (MainActivityInterface) activity;

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
//            mainActivity.showHideBellIcon(false);
            mainActivity.hidesettingIcon(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));
        performLogoutTask=new PerformLogoutTask();
        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) ||
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025))) {
                view = inflater.inflate(R.layout.fragment_home_student_layout, null);
            } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) {
                view = inflater.inflate(R.layout.fragment_home_mhaw_layout, null);
            } else {
                view = inflater.inflate(R.layout.fragment_home_consumer_layout, null);
            }

            headerMoodLayout = view.findViewById(R.id.linearlayout_header);
            positiveQuoteList = (RecyclerView) view.findViewById(R.id.quote_list);

            if (Preferences.get(Quote.QUOTE_NAME).equals("") || Preferences.get(Quote.QUOTE_AUTHER_NAME).equals("")) {
                positiveQuoteList.setVisibility(View.GONE);
                headerMoodLayout.setVisibility(View.VISIBLE);
            } else {
                if (Preferences.getBoolean("showQuote")) {
                    positiveQuoteList.setVisibility(View.GONE);
                    headerMoodLayout.setVisibility(View.VISIBLE);
                } else {
                    qouteDetailsLayout();
                }
            }

            if (Preferences.getBoolean(General.SHOW_BEHAVIOURAL_FILLED)) {
                showBehaviouralDialog(view);
            }

            if (Preferences.getBoolean(General.SHOW_GOAL_FILLED)) {
                showGoalDialog(view);
            }

          /*  if (Preferences.getBoolean(General.SHOW_APPOINTMENT_FILLED)) {
                showAppointmentFeedbackDialog(view);
            }*/

        } else {
            view = inflater.inflate(R.layout.fragment_home_layout, null);
        }

        if (Preferences.getBoolean(General.IS_RESET_PASSWORD_FILLED)) {
            isResetPasswordDialog(view);
        }

        if (Preferences.getBoolean(General.SHOW_APPOINTMENT_FILLED)) {
            showAppointmentFeedbackDialog(view);
        }

        ButterKnife.bind(this, view);
        activity = getActivity();
        Preferences.initialize(activity);
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.home));

        linearLayoutHome.setFocusable(true);

        //for re-open of list items
        Preferences.save(General.CASELOAD_LIST, false);
        Preferences.save(General.DoCTOR_NOTES_LIST, false);
        Preferences.save(General.NOTIFICATION_COACH_ID, 0);
        Preferences.save(General.NOTIFICATION_LEAVE_ID, 0);
        Preferences.save(General.NOTIFICATION_BHS_REF_ID, 0);
        Preferences.save(General.NOTIFICATION_BHS_ID, 0);
        Preferences.save(General.STUDENT_ASSIGN_ID, 0);
        Preferences.save("assign_start_date", "");
        Preferences.save("assign_end_date", "");
        Preferences.save("start_date_one", "");
        Preferences.save("end_date_one", "");
        return view;


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showAppointmentFeedbackDialog(View view) {
        activity = getActivity();

        dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_appointment_reminder_dialog);
//        assert dialog.getWindow() != null;
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        final TextView userName = (TextView) dialog.findViewById(R.id.user_name);
        final TextView description = (TextView) dialog.findViewById(R.id.description_txt);
        final TextView dateTxt = (TextView) dialog.findViewById(R.id.date_txt);
        final TextView timeTxt = (TextView) dialog.findViewById(R.id.time_txt);
        final TextView serviceValueTxt = (TextView) dialog.findViewById(R.id.txt_service_value);
        final TextView haveyouattendText = (TextView) dialog.findViewById(R.id.haveyouattendText);
        final Button buttonNO = (Button) dialog.findViewById(R.id.no_btn);
        final Button buttonYes = (Button) dialog.findViewById(R.id.yes_btn);
        final Button buttonSubmit = (Button) dialog.findViewById(R.id.submit_btn);
        final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.appointment_rating);
        final LinearLayout ratingLayout = (LinearLayout) dialog.findViewById(R.id.rating_layout);
        final LinearLayout yesnoSection = (LinearLayout) dialog.findViewById(R.id.yes_no_section);
        final ImageButton closePopup = dialog.findViewById(R.id.closePopup);

        String roleId = Preferences.get(General.ROLE_ID);
        Log.e("appon", Preferences.get(General.APP_ID));

        if (roleId.equals("31")) {
            closePopup.setVisibility(View.VISIBLE);
        } else {
            closePopup.setVisibility(View.GONE);
        }


        if (!Preferences.get(General.APP_ID).equals("0")) {
            callDeatilsAPI(Preferences.get(General.APP_ID), userName, description, dateTxt, timeTxt, serviceValueTxt);
        }

        if (CheckRole.isClient(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            userName.setVisibility(View.GONE);
        } else {
            userName.setVisibility(View.VISIBLE);
        }

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitClosepop(General.CLOSE_LOG, Preferences.get(General.APP_ID));
            }
        });
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesNoValue = "1";
                if (CheckRole.isClient(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                    ratingLayout.setVisibility(View.VISIBLE);
                    yesnoSection.setVisibility(View.GONE);
                    haveyouattendText.setVisibility(View.GONE);
                } else {
                    // submitClientSeveyAPI(General.SUBMIT_CLIENT_SURVEY, yesNoValue, ratingValue);
                    //added by kishor k 08-09-2020
                    submitClientSeveyAPI(General.SUBMIT_STAFF_SURVEY, yesNoValue, ratingValue);
                }
            }
        });

        buttonNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesNoValue = "0";
                if (CheckRole.isClient(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {

                    submitClientSeveyAPI(General.SUBMIT_CLIENT_SURVEY, yesNoValue, ratingValue);
                } else {
                    submitClientSeveyAPI(General.SUBMIT_STAFF_SURVEY, yesNoValue, ratingValue);
                }
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingValue = String.valueOf(ratingBar.getRating());
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckRole.isClient(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                    if (!ratingValue.isEmpty() && !ratingValue.equals("0")) {
                        submitClientSeveyAPI(General.SUBMIT_CLIENT_SURVEY, yesNoValue, ratingValue);
                    } else {
                        Toast.makeText(activity, "Please choose rating..", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!ratingValue.isEmpty() && !ratingValue.equals("0")) {
                        submitClientSeveyAPI(General.SUBMIT_STAFF_SURVEY, yesNoValue, ratingValue);
                    } else {
                        Toast.makeText(activity, "Please choose rating..", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        dialog.show();
    }

    private void submitClosepop(String closeLog, String app_id) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, closeLog);
        requestMap.put(General.ID, app_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    callDismiss();
                    Toast.makeText(activity, "Data updated successfully.", Toast.LENGTH_LONG).show();
                    Preferences.save(General.SHOW_APPOINTMENT_FILLED, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void submitClientSeveyAPI(String action, String yesNoValue, String ratingValue) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.YES_NO, yesNoValue);
        requestMap.put(General.ID, Preferences.get(General.APP_ID));

        if (action.equals(General.SUBMIT_CLIENT_SURVEY)) {
            if (yesNoValue.equals("0")) {
                requestMap.put(General.RATING, "0");
            } else {
                requestMap.put(General.RATING, ratingValue);
            }
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject JsonObjectLike_dislike = jsonObject.getAsJsonObject(action);
                    if (JsonObjectLike_dislike.get(General.STATUS).getAsInt() == 1) {
                        callDismiss();

                        Preferences.save(General.SHOW_APPOINTMENT_FILLED, false);
                        Toast.makeText(activity, String.valueOf(JsonObjectLike_dislike.get("msg")), Toast.LENGTH_LONG).show();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void callDismiss() {
        dialog.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void callDeatilsAPI(String app_id, TextView userName, TextView description, TextView dateTxt, TextView timeTxt, TextView serviceValueTxt) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.VIEW_DETAILS);
        requestMap.put(General.ID, app_id);

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    appointmentArrayList = Appointments_.appointmentList(response, Actions_.VIEW_DETAILS, activity, TAG);
                    if (appointmentArrayList.size() > 0) {
                        if (appointmentArrayList.get(0).getStatus() == 1) {
                            for (int i = 0; i < appointmentArrayList.size(); i++) {
                                userName.setText("Appointment with " + appointmentArrayList.get(i).getClient_name());
                                description.setText(appointmentArrayList.get(i).getDescription());
                                dateTxt.setText(GetTime.dateCaps(appointmentArrayList.get(i).getDate()));
                                setAppointmentDuration(timeTxt, appointmentArrayList.get(i).getStart_time().substring(0, 5), appointmentArrayList.get(i).getEnd_time().substring(0, 5));
                                String text = "";
                                for (Staff details : appointmentArrayList.get(i).getServices()) {
                                    text = text + "- " + details.getName() + "\n";
                                }
                                serviceValueTxt.setText(text);
                            }

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setAppointmentDuration(TextView timeTxt, String start_time, String end_time) {
        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");

        Date start = null;
        try {
            start = _24HourSDF.parse(start_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date end = null;
        try {
            end = _24HourSDF.parse(end_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeTxt.setText(_12HourSDF.format(start) + " to " + _12HourSDF.format(end));
    }

    private void showGoalDialog(View view) {
        activity = getActivity();
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_goal_dialog);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;

        final TextView todayDate = (TextView) dialog.findViewById(R.id.today_date_txt);
        final Button buttonSubmit = (Button) dialog.findViewById(R.id.button_submit);
        final Button buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);
        final RecyclerView goalList = (RecyclerView) dialog.findViewById(R.id.goal_popup_list);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        goalList.setLayoutManager(mLinearLayoutManager);

        todayDate.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date()));

        //load goal pop list data
        loadGoalPopUpList(goalList);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.SHOW_GOAL_FILLED, false);
                addInputGoalAPI();
                dialog.dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void loadGoalPopUpList(RecyclerView goalList) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_GOALS_POPUP);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    goalArrayList = SelfGoal_.parseGoalPopup(response, Actions_.GET_GOALS_POPUP, activity, TAG);
                    if (goalArrayList.size() > 0) {
                        if (goalArrayList.get(0).getStatus() == 1) {
                            GoalPopUpAdapter goalPopUpAdapter = new GoalPopUpAdapter(activity, goalArrayList);
                            goalList.setAdapter(goalPopUpAdapter);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addInputGoalAPI() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ADD_COUNT_POPUP);
        requestMap.put(General.GOAL_IDS, getIds());
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray(Actions_.ADD_COUNT_POPUP);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        if (Integer.parseInt(object.getString("status")) == 1) {
                            Toast.makeText(activity, object.getString("msg"), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getIds() {
        goalIds.clear();
        if (goalArrayList != null && goalArrayList.size() > 0) {
            for (int i = 0; i < goalArrayList.size(); i++) {
                if (goalArrayList.get(i).getSelected()) {
                    goalIds.add(String.valueOf(goalArrayList.get(i).getMain_goal_id() + "_" + goalArrayList.get(i).getWer_goal_id()
                            + "_" + goalArrayList.get(i).getYes()));
                }
            }
        }
        return goalIds.toString()
                .replace("[", "")
                .replace("]", "").trim();
    }

    private void isResetPasswordDialog(View view) {
        dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.reset_password_dialog);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        final Button buttonSubmit = (Button) dialog.findViewById(R.id.button_btn);
        final ImageView closeIcon = (ImageView) dialog.findViewById(R.id.close_icon);
        final EditText verifyOTP = (EditText) dialog.findViewById(R.id.edittext_veri_code);

        final EditText password = (EditText) dialog.findViewById(R.id.edittext_password);
        final EditText re_password = (EditText) dialog.findViewById(R.id.edittext_re_password);

        final LinearLayout otpLayout = (LinearLayout) dialog.findViewById(R.id.otp_layout);
        final LinearLayout passLayout = (LinearLayout) dialog.findViewById(R.id.pass_layout);

        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogoutTask.logout(activity);
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otpVerify) {
                    if (otpValidation(v, verifyOTP.getText().toString().trim())) {
                        veryfyOTP(verifyOTP.getText().toString().trim(), otpLayout, passLayout);
                    }
                } else {
                    if (passwordValidation(v, password.getText().toString().trim(), re_password.getText().toString().trim())) {
                        resetPassword(password.getText().toString().trim(), re_password.getText().toString().trim());
                    }
                }
            }
        });

        dialog.show();
    }

    private boolean passwordValidation(View v, String pass, String re_pass) {
        if (pass == null || pass.trim().length() <= 0) {
            Toast.makeText(activity, "Please enter new password", Toast.LENGTH_LONG).show();
            return false;
        }

        if (re_pass == null || re_pass.trim().length() <= 0) {
            Toast.makeText(activity, "Please enter re-type new password", Toast.LENGTH_LONG).show();
            return false;
        } else if (!pass.equals(re_pass)) {
            Toast.makeText(activity, "New password and re-type new password did not match", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void resetPassword(String password, String re_password) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.RESET_PASSWORD);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.PASSWORD, password);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_RESET_PASSWORD;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject JsonObjectStudent = jsonObject.getAsJsonObject(Actions_.RESET_PASSWORD);

                    if (JsonObjectStudent.get(General.STATUS).getAsInt() == 1) {
                        String msg = String.valueOf(JsonObjectStudent.get("msg"));
                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                        otpVerify = false;
                        dialog.dismiss();
                        Preferences.save(General.IS_RESET_PASSWORD_FILLED, false);
                    } else {
                        String error = String.valueOf(JsonObjectStudent.get("error"));
                        Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean otpValidation(View view, String otpTxt) {
        if (otpTxt == null || otpTxt.trim().length() <= 0) {
            Toast.makeText(activity, "Please enter verification code", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void veryfyOTP(String otp, LinearLayout otpLayout, LinearLayout passLayout) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.VERIFY_CODE);
        requestMap.put(General.CODE, otp);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_RESET_PASSWORD;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject JsonObjectStudent = jsonObject.getAsJsonObject(Actions_.VERIFY_CODE);

                    if (JsonObjectStudent.get(General.STATUS).getAsInt() == 1) {
                        String msg = String.valueOf(JsonObjectStudent.get("msg"));
                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                        otpVerify = false;
                        otpLayout.setVisibility(View.GONE);
                        passLayout.setVisibility(View.VISIBLE);
                    } else {
                        String error = String.valueOf(JsonObjectStudent.get("error"));
                        Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showBehaviouralDialog(View view) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_behavioural_dialog);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final Button buttonSubmit = (Button) dialog.findViewById(R.id.button_submit);
        final Button buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);

        final RadioGroup questionOne = (RadioGroup) dialog.findViewById(R.id.ques1_radio_group);
        final RadioGroup questionTwo = (RadioGroup) dialog.findViewById(R.id.ques2_radio_group);
        final RadioGroup questionThree = (RadioGroup) dialog.findViewById(R.id.ques3_radio_group);

        final RadioButton yesQues1 = (RadioButton) dialog.findViewById(R.id.ques1_yes);
        final RadioButton noQues1 = (RadioButton) dialog.findViewById(R.id.ques1_no);
        final RadioButton naQues1 = (RadioButton) dialog.findViewById(R.id.ques1_na);

        final RadioButton yesQues2 = (RadioButton) dialog.findViewById(R.id.ques2_yes);
        final RadioButton noQues2 = (RadioButton) dialog.findViewById(R.id.ques2_no);
        final RadioButton naQues2 = (RadioButton) dialog.findViewById(R.id.ques2_na);

        final RadioButton yesQues3 = (RadioButton) dialog.findViewById(R.id.ques3_yes);
        final RadioButton noQues3 = (RadioButton) dialog.findViewById(R.id.ques3_no);
        final RadioButton naQues3 = (RadioButton) dialog.findViewById(R.id.ques3_na);

        final LinearLayout quesThreeLayout = (LinearLayout) dialog.findViewById(R.id.optional_layout);

        questionTwo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.ques2_yes) {
                    quesThreeLayout.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.ques2_no) {
                    quesThreeLayout.setVisibility(View.GONE);
                } else if (checkedId == R.id.ques2_na) {
                    quesThreeLayout.setVisibility(View.GONE);
                }
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.SHOW_BEHAVIOURAL_FILLED, false);
                quesYes = yesQues1.isChecked();
                quesNo = noQues1.isChecked();
                quesNa = naQues1.isChecked();

                quesOneYes = yesQues2.isChecked();
                quesOneNo = noQues2.isChecked();
                quesOneNa = naQues2.isChecked();

                quesThreeYes = yesQues3.isChecked();
                quesTwoNo = noQues3.isChecked();
                quesTwoNa = naQues3.isChecked();

                submitDataOfStudentBehaviour();

                dialog.dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void submitDataOfStudentBehaviour() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.SUBMIT_FORM);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        if (quesYes) {
            requestMap.put(General.QUES1, "1");
        } else if (quesNo) {
            requestMap.put(General.QUES1, "2");
        } else if (quesNa) {
            requestMap.put(General.QUES1, "N/A");
        }

        if (quesOneYes) {
            requestMap.put(General.QUES2, "1");

            if (quesThreeYes) {
                requestMap.put(General.QUES3, "1");
            } else if (quesTwoNo) {
                requestMap.put(General.QUES3, "2");
            } else if (quesTwoNa) {
                requestMap.put(General.QUES3, "N/A");
            }

        } else if (quesOneNo) {
            requestMap.put(General.QUES2, "2");
            requestMap.put(General.QUES3, "0");
        } else if (quesOneNa) {
            requestMap.put(General.QUES2, "N/A");
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_BEHAVIOURAL_HEALTH_SERVEY;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject JsonObjectStudent = jsonObject.getAsJsonObject(Actions_.SUBMIT_FORM);
                    String msg = String.valueOf(JsonObjectStudent.get("msg"));
                    int status = JsonObjectStudent.get(General.STATUS).getAsInt();
                    if (status == 1) {
                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void qouteDetailsLayout() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        positiveQuoteList.setLayoutManager(mLinearLayoutManager);

        final Runnable runnable = new Runnable() {
            int count = 0;
            boolean flag = true;

            @Override
            public void run() {
                if (count < positiveQuoteAdapter.getItemCount()) {
                    if (count == positiveQuoteAdapter.getItemCount() - 1) {
                        flag = false;
                    } else if (count == 0) {
                        flag = true;
                    }
                    if (flag) count++;
                    else count--;

                    positiveQuoteList.smoothScrollToPosition(count);
                    handler.postDelayed(this, speed);
                }
            }
        };
        handler.postDelayed(runnable, speed);
        positiveQuoteAdapter = new PositiveQuoteAdapter(activity, this);
        positiveQuoteList.setAdapter(positiveQuoteAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        homeMenuList = Login_.homeMenuParser();
        drawerMenuList = Login_.drawerMenuParser();

        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.home));
        mainActivityInterface.setToolbarBackgroundColor();
        mainActivityInterface.toggleAdd(false);
        activity.registerReceiver(broadcastReceiver, new IntentFilter(Broadcast.COUNTER_BROADCAST));

        LinearLayout[] linearLayout = {linearLayoutHome, linearLayoutHomeTwo, linearLayoutHomeThree, linearLayoutHomeFour, linearLayoutHomeFive, linearLayoutHomeSix};

        AppCompatImageView[] imageViewArray = {imageViewHomeOne, imageViewHomeTwo, imageViewHomeThree,
                imageViewHomeFour, imageViewHomeFive, imageViewHomeSix};

        TextView[] textViews = {textViewHomeOne, textViewHomeTwo, textViewHomeThree, textViewHomeFour,
                textViewHomeFive, textViewHomeSix};

        Button[] buttonViews = {buttonHomeOne, buttonHomeTwo, buttonHomeThree, buttonHomeFour,
                buttonHomeFive, buttonHomeSix};

        for (int i = 0; i < homeMenuList.size(); i++) {
            if (homeMenuList.get(i).getId() != 0) {
                color = Color.parseColor("#ffffff"); //The color u want
                if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                    color = GetColor.getHomeIconBackgroundDrawable(i + 1);
                    if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                            && (homeMenuList.get(i).getId() == 24 || homeMenuList.get(i).getId() == 25)) {
                        if (GetCounters.drawer(homeMenuList.get(i).getId()) > 0) {
                            buttonViews[i].setVisibility(View.VISIBLE);
                        }
                    }
                }
                //Teams, Chat, Planner, SOS update, Mood and Notification  60,36,27,29,34,26
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) ||
                        Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025))) {
                    if (CheckRole.isWerHope(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                        imageViewArray[i].setColorFilter(color);
                        imageViewArray[i].setImageResource(GetHomeMenuIcon.get(homeMenuList.get(i).getId()));
                    }
                } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
                    imageViewArray[i].setColorFilter(color);
                    imageViewArray[i].setImageResource(GetHomeMenuIcon.get(homeMenuList.get(i).getId()));
                } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) ||
                        Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) {
                    /*if (CheckRole.isMhaw(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {*/
                    color = Color.parseColor("#ffffff"); //The color u want
                    imageViewArray[i].setColorFilter(color);
                    imageViewArray[i].setImageResource(GetHomeMenuIcon.get(homeMenuList.get(i).getId()));
                    /*}*/
                }
                /*else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) {
                    if (CheckRole.isMhaw(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                        color = Color.parseColor("#ffffff"); //The color u want
                        imageViewArray[i].setColorFilter(color);
                        imageViewArray[i].setImageResource(GetHomeMenuIcon.get(homeMenuList.get(i).getId()));
                    }
                } */
                else {
                    imageViewArray[i].setColorFilter(color);
                    imageViewArray[i].setImageResource(GetHomeMenuIcon.get(homeMenuList.get(i).getId()));
                }

                String sentence = ChangeCase.toTitleCase(homeMenuList.get(i).getMenu());
                if (homeMenuList.get(i).getId() == 32) { //Assignment instead of Admin Approval
                    if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage013") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage015")) {
                        sentence = activity.getResources().getString(R.string.assignment);
                    }
                }

                textViews[i].setText(sentence);
            } else {

                linearLayout[i].setVisibility(View.GONE);

            }
        }

        updateHomeMenuList();

        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            textViewUsername = (TextView) view.findViewById(R.id.textview_username);
            textViewUsername.setText(ChangeCase.toTitleCase(Preferences.get(General.NAME)));
            textViewMood = (TextView) view.findViewById(R.id.textview_mood);
            textViewMood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Preferences.save(General.IS_ADD_MOOD_ACTIVITY_BACK_PRESSED, true);
                    Intent createIntent = new Intent(activity.getApplicationContext(), MoodAddActivity.class);
                    startActivity(createIntent);
                }
            });
            textViewRecentUpdates = (TextView) view.findViewById(R.id.textview_recent_updates);
            countRecentUpdates = view.findViewById(R.id.counter);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            homeRecentUpdatesAdapter = new HomeRecentUpdateAdapter(activity, recentUpdatesArrayList, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setFocusable(false);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(homeRecentUpdatesAdapter);

            // for quote read API
            readQuoteCall();
            fetchRecentUpdatesForConsumer();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        activity.unregisterReceiver(broadcastReceiver);
    }

    // Update drawer list when new counters received
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                homeMenuList = Login_.homeMenuParser();
                drawerMenuList = Login_.drawerMenuParser();
                updateHomeMenuList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //update drawer list with new content
    private void updateHomeMenuList() {
        Button[] buttonViews = {buttonHomeOne, buttonHomeTwo, buttonHomeThree, buttonHomeFour,
                buttonHomeFive, buttonHomeSix};

        for (int i = 0; i < homeMenuList.size(); i++) {

            if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                    && (homeMenuList.get(i).getId() == 24 || homeMenuList.get(i).getId() == 25)) {
                if (GetCounters.drawer(homeMenuList.get(i).getId()) > 0) {
                    buttonViews[i].setVisibility(View.VISIBLE);
                }
            }

        }
    }

    @OnClick({R.id.linearlayout_home_one, R.id.linearlayout_home_two,
            R.id.linearlayout_home_three, R.id.linearlayout_home_four,
            R.id.linearlayout_home_five, R.id.linearlayout_home_six})
    public void onButtonClick(View view) {
        linearLayoutHome.setVisibility(View.GONE);
        switch (view.getId()) {
            case R.id.linearlayout_home_one:
                //For Toolbar color changing depending on background color of home icons
                Preferences.save(General.HOME_ICON_NUMBER, "1");
                replaceFragment(homeMenuList.get(0).getId(), homeMenuList.get(0).getMenu(), null);
                break;

            case R.id.linearlayout_home_two:
                Preferences.save(General.HOME_ICON_NUMBER, "2");
                replaceFragment(homeMenuList.get(1).getId(), homeMenuList.get(1).getMenu(), null);
                break;

            case R.id.linearlayout_home_three:
                Preferences.save(General.HOME_ICON_NUMBER, "3");
                replaceFragment(homeMenuList.get(2).getId(), homeMenuList.get(2).getMenu(), null);
                break;

            case R.id.linearlayout_home_four:
                Preferences.save(General.HOME_ICON_NUMBER, "4");
                replaceFragment(homeMenuList.get(3).getId(), homeMenuList.get(3).getMenu(), null);
                break;

            case R.id.linearlayout_home_five:
                Preferences.save(General.HOME_ICON_NUMBER, "5");
                replaceFragment(homeMenuList.get(4).getId(), homeMenuList.get(4).getMenu(), null);
                break;

            case R.id.linearlayout_home_six:
                Preferences.save(General.HOME_ICON_NUMBER, "6");
                replaceFragment(homeMenuList.get(5).getId(), homeMenuList.get(5).getMenu(), null);
                break;
        }
    }

    private void replaceFragment(int id, String name, Bundle bundle) {
        Logger.error(TAG, "menu_id: " + id, activity.getApplicationContext());
        Preferences.save(General.MODULE_ID, id);

        //Kailash added to move to team details page if user has only 1 team(no intermediate team listing screen required)
        //if only 1 team available than directly move to team details and manage the onBackPressed call from TeamDetailsActivity
        ArrayList<Teams_> teamsArrayList = new ArrayList<Teams_>();
        boolean isMoveToTeamDetails = false;
        if (id == 8 || id == 47 || id == 60) {
            teamsArrayList = PerformGetTeamsTask.get(Actions_.ALL_TEAMS, activity, TAG, false, activity);
            if (teamsArrayList.size() == 1) {
                isMoveToTeamDetails = false;
            }
        }

        if (isMoveToTeamDetails) {
            Preferences.save(General.BANNER_IMG, teamsArrayList.get(0).getBanner());
            Preferences.save(General.TEAM_ID, teamsArrayList.get(0).getId());
            Preferences.save(General.TEAM_NAME, teamsArrayList.get(0).getName());
            Preferences.save(General.ISMOVETOTEAMDETAILS, true);

            Intent detailsIntent = new Intent(activity.getApplicationContext(), TeamDetailsActivity.class);
            detailsIntent.putExtra(General.TEAM, teamsArrayList.get(0));
            startActivity(detailsIntent);
            activity.overridePendingTransition(0, 0);
        } else {
            if (id == 27 || id == 54) {
                if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                    //Hide + button for senjam
                    mainActivityInterface.toggleAdd(false);
                } else {
                    mainActivityInterface.toggleAdd(true);
                }

            } else {
                mainActivityInterface.toggleAdd(false);
            }
            Log.i(TAG, "replaceFragment: id"+id);
            if (id==36){
                id=82;
            }
            Fragment fragment = GetFragments.get(id, bundle);
            FragmentTransaction ft = myContext.getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
            ft.replace(R.id.app_bar_main_container, fragment, TAG);
            ft.commit();
        }
    }

    private void fetchRecentUpdatesForConsumer() {
        showError(true, 20);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.HOME);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_YOUTH_OPERATIONS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    recentUpdatesArrayList = HomeParser_.parseHomeRecentUpdates(response, Actions_.HOME, activity.getApplicationContext(), TAG);
                    if (recentUpdatesArrayList.size() > 0) {
                        if (recentUpdatesArrayList.get(0).getStatus() == 1) {
                            showError(false, 20);
                            homeRecentUpdatesAdapter = new HomeRecentUpdateAdapter(activity, recentUpdatesArrayList, this);
                            recyclerView.setAdapter(homeRecentUpdatesAdapter);
                            countRecentUpdates.setText("" + recentUpdatesArrayList.size());
                        } else {
                            showError(true, recentUpdatesArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(true, 2);
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showError(true, 11);
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            textViewRecentUpdates.setVisibility(View.GONE);
            countRecentUpdates.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textViewRecentUpdates.setVisibility(View.VISIBLE);
            countRecentUpdates.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClicked(HomeRecentUpdates_ recentUpdates) {
        boolean isShowErrorMsg = true;
        if (NotificationTypeDetector.getType(recentUpdates.getType()) == 1) {//Messageboard
            Preferences.save(General.GROUP_ID, recentUpdates.getGroup_id());
            String response = Teams.getMessageboardDetails(TAG, activity);
            ArrayList<MessageBoard_> messageBoardArrayList = Alerts_.parseMessageBoard(response, Actions_.GET_MESSAGEBOARD, activity.getApplicationContext(), TAG);
            if (messageBoardArrayList.size() > 0) {
                if (messageBoardArrayList.get(0).getStatus() == 1) {
                    for (int i = 0; i < messageBoardArrayList.size(); i++) {
                        if (recentUpdates.getId() == messageBoardArrayList.get(i).getId()) {
                            isShowErrorMsg = false;
                            Intent detailsIntent = new Intent(activity.getApplicationContext(), MessageBoardDetailsActivity.class);
                            detailsIntent.putExtra(General.MESSAGEBOARD, messageBoardArrayList.get(i));
                            startActivity(detailsIntent);
                            activity.overridePendingTransition(0, 0);
                        }
                    }
                }
            }
        } else if (NotificationTypeDetector.getType(recentUpdates.getType()) == 2) {//Announcement
            Preferences.save(General.GROUP_ID, recentUpdates.getGroup_id());
            String response = Teams.getAnnouncement(TAG, activity);
            ArrayList<Announcement_> announcementList = Alerts_.parseAnnouncement(response, Actions_.ANNOUNCEMENT, activity.getApplicationContext(), TAG);
            if (announcementList.size() > 0) {
                if (announcementList.get(0).getStatus() == 1) {
                    for (int i = 0; i < announcementList.size(); i++) {
                        if (recentUpdates.getId() == announcementList.get(i).getId()) {
                            isShowErrorMsg = false;
                            Intent detailsIntent = new Intent(activity.getApplicationContext(), AnnouncementDetailsActivity.class);
                            detailsIntent.putExtra(Actions_.ANNOUNCEMENT, announcementList.get(i));
                            startActivity(detailsIntent);
                            activity.overridePendingTransition(0, 0);
                        }
                    }
                }
            }
        } else if (NotificationTypeDetector.getType(recentUpdates.getType()) == 3) { //FMS
            Preferences.save(General.GROUP_ID, recentUpdates.getGroup_id());
            fileArrayList = Teams.getFMS(TAG, activity);
            if (fileArrayList.size() > 0) {
                if (fileArrayList.get(0).getStatus() == 1) {
                    isShowErrorMsg = false;
                    for (int i = 0; i < fileArrayList.size(); i++) {
                        if (recentUpdates.getId() == fileArrayList.get(i).getId() && fileArrayList.get(i).isFile()) {
                            updateFMSReadRecord(fileArrayList.get(i).getId());
                            //   if (fileArrayList.get(i).getPermission() != 1) {
                            initiatePopupWindow(view, i);
                            // }
                        }
                    }
                }
            }
        } else if (NotificationTypeDetector.getType(recentUpdates.getType()) == 7) { //TeamTalk
            Preferences.save(General.GROUP_ID, recentUpdates.getGroup_id());
            String response = Teams.getTeamTalk(TAG, activity);
            ArrayList<TeamTalk_> teamTalkList = Alerts_.parseTalk(response, Actions_.TEAMTALK, activity.getApplicationContext(), TAG);
            if (teamTalkList.size() > 0) {
                if (teamTalkList.get(0).getStatus() == 1) {
                    for (int i = 0; i < teamTalkList.size(); i++) {
                        if (recentUpdates.getId() == teamTalkList.get(i).getId()) {
                            isShowErrorMsg = false;
                            Intent detailsIntent = new Intent(activity.getApplicationContext(), TalkDetailsActivity.class);
                            detailsIntent.putExtra(Actions_.TEAM_TALK, teamTalkList.get(i));
                            startActivity(detailsIntent);
                        }
                    }
                }
            }
        } else if (NotificationTypeDetector.getType(recentUpdates.getType()) == 9) { //Poll
            isShowErrorMsg = false;
            PerformReadTask.readAlert_One("" + recentUpdates.getId(), recentUpdates.getType(), TAG, activity.getApplicationContext(), activity);

            Preferences.save(General.TEAM_ID, recentUpdates.getGroup_id());
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
        } else if (NotificationTypeDetector.getType(recentUpdates.getType()) == 10) { //Group Invitation
            isShowErrorMsg = false;
            Intent detailsIntent = new Intent(activity.getApplicationContext(), InviteListActivity.class);
            detailsIntent.putExtra(General.TEAM_ID, recentUpdates.getGroup_id());
            startActivity(detailsIntent);
            activity.overridePendingTransition(0, 0);
        } else if (NotificationTypeDetector.getType(recentUpdates.getType()) == 11) { //My invitation
            isShowErrorMsg = false;
            Intent detailsIntent = new Intent(activity.getApplicationContext(), InviteListActivity.class);
            detailsIntent.putExtra(General.TEAM_ID, "0");
            startActivity(detailsIntent);
            activity.overridePendingTransition(0, 0);
        } else if (NotificationTypeDetector.getType(recentUpdates.getType()) == 12) { //TaskList
            Preferences.save(General.GROUP_ID, recentUpdates.getGroup_id());
            ArrayList<Task_> taskList = new ArrayList<>();
            Preferences.save(General.GROUP_ID, recentUpdates.getGroup_id());
            if (recentUpdates.getGroup_id() == 0) {
                taskList = Teams.getMyTaskList(TAG, activity);
            } else {
                taskList = Teams.getGroupTaskList(TAG, activity);
            }
            if (taskList.size() > 0) {
                if (taskList.get(0).getStatus() == 1) {
                    for (int i = 0; i < taskList.size(); i++) {
                        if (recentUpdates.getId() == taskList.get(i).getId()) {
                            isShowErrorMsg = false;
                            Intent detailsIntent = new Intent(activity.getApplicationContext(), TaskDetailsActivity.class);
                            detailsIntent.putExtra(Actions_.TASK_LIST, taskList.get(i));
                            startActivity(detailsIntent);
                            activity.overridePendingTransition(0, 0);
                        }
                    }
                }
            }
        } else if (NotificationTypeDetector.getType(recentUpdates.getType()) == 17) { //Upload Selfcare
            boolean action = false;
            ArrayList<CareUploaded_> careUploadedArrayList = Teams.getSelfCareUploader(Actions_.PENDING, TAG, activity);
            if (careUploadedArrayList.size() > 0) {
                if (careUploadedArrayList.get(0).getStatus() == 1) {
                    for (int i = 0; i < careUploadedArrayList.size(); i++) {
                        if (recentUpdates.getId() == careUploadedArrayList.get(i).getId()) {
                            action = true;
                            isShowErrorMsg = false;
                            Intent detailsIntent = new Intent(activity.getApplicationContext(), UploadCareDetailsActivity.class);
                            detailsIntent.putExtra(Actions_.GET_DATA, careUploadedArrayList.get(i));
                            detailsIntent.putExtra(General.ACTION, Actions_.PENDING);
                            startActivity(detailsIntent);
                            activity.overridePendingTransition(0, 0);
                        }
                    }
                    if (!action) {
                        careUploadedArrayList = Teams.getSelfCareUploader(Actions_.APPROVAL, TAG, activity);
                        if (careUploadedArrayList.size() > 0) {
                            if (careUploadedArrayList.get(0).getStatus() == 1) {
                                for (int i = 0; i < careUploadedArrayList.size(); i++) {
                                    if (recentUpdates.getId() == careUploadedArrayList.get(i).getId()) {
                                        action = true;
                                        isShowErrorMsg = false;
                                        Intent detailsIntent = new Intent(activity.getApplicationContext(), UploadCareDetailsActivity.class);
                                        detailsIntent.putExtra(Actions_.GET_DATA, careUploadedArrayList.get(i));
                                        detailsIntent.putExtra(General.ACTION, Actions_.APPROVAL);
                                        startActivity(detailsIntent);
                                        activity.overridePendingTransition(0, 0);
                                    }
                                }
                            }
                        }
                    }
                    if (!action) {
                        careUploadedArrayList = Teams.getSelfCareUploader(Actions_.REJECTED, TAG, activity);
                        if (careUploadedArrayList.size() > 0) {
                            if (careUploadedArrayList.get(0).getStatus() == 1) {
                                for (int i = 0; i < careUploadedArrayList.size(); i++) {
                                    if (recentUpdates.getId() == careUploadedArrayList.get(i).getId()) {
                                        action = true;
                                        isShowErrorMsg = false;
                                        Intent detailsIntent = new Intent(activity.getApplicationContext(), UploadCareDetailsActivity.class);
                                        detailsIntent.putExtra(Actions_.GET_DATA, careUploadedArrayList.get(i));
                                        detailsIntent.putExtra(General.ACTION, Actions_.REJECTED);
                                        startActivity(detailsIntent);
                                        activity.overridePendingTransition(0, 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (NotificationTypeDetector.getType(recentUpdates.getType()) == 18) { //Comment Selfcare
            ArrayList<Content_> contentArrayList = Teams.getSelfCare(TAG, activity);
            if (contentArrayList.size() > 0) {
                if (contentArrayList.get(0).getStatus() == 1) {
                    for (int i = 0; i < contentArrayList.size(); i++) {
                        if (recentUpdates.getId() == contentArrayList.get(i).getId()) {
                            isShowErrorMsg = false;
                            Intent detailsIntent = new Intent(activity.getApplicationContext(), SelfCareDetailsActivity.class);
                            detailsIntent.putExtra(Actions_.GET_DATA, contentArrayList.get(i));
                            startActivity(detailsIntent);
                            activity.overridePendingTransition(0, 0);
                        }
                    }
                }
            }
        } else if (NotificationTypeDetector.getType(recentUpdates.getType()) == 19) { //Decline Selfcare
            ArrayList<CareUploaded_> careUploadedArrayList = Teams.getSelfCareUploader(Actions_.REJECTED, TAG, activity);
            if (careUploadedArrayList.size() > 0) {
                if (careUploadedArrayList.get(0).getStatus() == 1) {
                    for (int i = 0; i < careUploadedArrayList.size(); i++) {
                        if (recentUpdates.getId() == careUploadedArrayList.get(i).getId()) {
                            isShowErrorMsg = false;
                            Intent detailsIntent = new Intent(activity.getApplicationContext(), UploadCareDetailsActivity.class);
                            detailsIntent.putExtra(Actions_.GET_DATA, careUploadedArrayList.get(i));
                            detailsIntent.putExtra(General.ACTION, Actions_.REJECTED);
                            startActivity(detailsIntent);
                            activity.overridePendingTransition(0, 0);
                        }
                    }
                }
            }
        } else if (NotificationTypeDetector.getType(recentUpdates.getType()) == 20) { //Approve Selfcare
            ArrayList<CareUploaded_> careUploadedArrayList = Teams.getSelfCareUploader(Actions_.APPROVAL, TAG, activity);
            if (careUploadedArrayList.size() > 0) {
                if (careUploadedArrayList.get(0).getStatus() == 1) {
                    for (int i = 0; i < careUploadedArrayList.size(); i++) {
                        if (recentUpdates.getId() == careUploadedArrayList.get(i).getId()) {
                            isShowErrorMsg = false;
                            Intent detailsIntent = new Intent(activity.getApplicationContext(), UploadCareDetailsActivity.class);
                            detailsIntent.putExtra(Actions_.GET_DATA, careUploadedArrayList.get(i));
                            detailsIntent.putExtra(General.ACTION, Actions_.APPROVAL);
                            startActivity(detailsIntent);
                            activity.overridePendingTransition(0, 0);
                        }
                    }
                }
            }
        } else if (NotificationTypeDetector.getType(recentUpdates.getType()) == 21 //Event
                || NotificationTypeDetector.getType(recentUpdates.getType()) == 25 //Accept Event
                || NotificationTypeDetector.getType(recentUpdates.getType()) == 26) { //Decline Event
            ArrayList<Event_> eventArrayList = Teams.getEventDetails(TAG, String.valueOf(recentUpdates.getId()), activity);
            if (eventArrayList.size() > 0) {
                if (eventArrayList.get(0).getStatus() == 1) {
                    isShowErrorMsg = false;
                    Intent addIntent = new Intent(activity.getApplicationContext(), EventDetailsActivity.class);
                    addIntent.putExtra(Actions_.GET_EVENTS, eventArrayList.get(0));
                    activity.startActivity(addIntent);
                    activity.overridePendingTransition(0, 0);
                }
            }
        } else if (NotificationTypeDetector.getType(recentUpdates.getType()) == 22) { //Self Goal
            ArrayList<Goal_> goalArrayList = Teams.getGoalDetails(TAG, String.valueOf(recentUpdates.getId()), activity);
            if (goalArrayList.size() > 0) {
                if (goalArrayList.get(0).getStatus() == 1) {
                    isShowErrorMsg = false;
                    Intent addIntent = new Intent(activity.getApplicationContext(), SelfGoalDetailsActivity.class);
                    addIntent.putExtra(Actions_.MY_GOAL, goalArrayList.get(0));
                    activity.startActivity(addIntent);
                    activity.overridePendingTransition(0, 0);
                }
            }
        } else if (NotificationTypeDetector.getType(recentUpdates.getType()) == 23 //Added Notes
                || NotificationTypeDetector.getType(recentUpdates.getType()) == 27 //Approved Notes
                || NotificationTypeDetector.getType(recentUpdates.getType()) == 28 //Rejected Notes
                || NotificationTypeDetector.getType(recentUpdates.getType()) == 29) { //Updated Notes
            ArrayList<CaseloadPeerNote_> caseloadPeerNoteArrayList = Teams.getPeerCaseloadNoteDetails(TAG, String.valueOf(recentUpdates.getId()), activity);
            if (caseloadPeerNoteArrayList.size() > 0) {
                if (caseloadPeerNoteArrayList.get(0).getStatus() == 1) {
                    isShowErrorMsg = false;
                    Intent detailsIntent = new Intent(activity, PeerNoteDetailsActivity.class);
                    detailsIntent.putExtra(Actions_.NOTES, caseloadPeerNoteArrayList);
                    activity.startActivity(detailsIntent);
                    activity.overridePendingTransition(0, 0);
                }
            }
        } else if (NotificationTypeDetector.getType(recentUpdates.getType()) == 24) { //Mood
            if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                isShowErrorMsg = false;
                Bundle bundle = new Bundle();
                Preferences.save(General.MODULE_ID, 34);
                mainActivityInterface.toggleAdd(false);
                mainActivityInterface.setMoodToolbar(51);
                Preferences.save(General.TEAM_ID, recentUpdates.getGroup_id());
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
            }
        }

        PerformReadTask.readAlert_One("" + recentUpdates.getId(), recentUpdates.getType(), TAG, activity.getApplicationContext(), activity);

        if (isShowErrorMsg) {
            SubmitSnackResponse.showSnack(2, "Record has been deleted.", activity.getApplicationContext());
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
            popupWindow.isShowing();

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
            downloadButton.setVisibility(View.GONE);

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

           /* downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDownload(fileArrayList.get(position).getId(), fileArrayList.get(position).getRealName());
                    popupWindow.dismiss();
                }
            });*/
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
    public void positiveQuoteItemClicked() {
        // mainActivityInterface.setMoodToolbar(51);
        Preferences.save(General.IS_ADD_MOOD_ACTIVITY_BACK_PRESSED, true);
        Intent createIntent = new Intent(activity.getApplicationContext(), MoodAddActivity.class);
        startActivity(createIntent);
        // replaceFragment(51, "", null);
    }

    @Override
    public void likeDislikeItemClicked(String likeDislike) {
        quoteLike_DislikeAPI(likeDislike);
    }

    private void readQuoteCall() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.READ);
        requestMap.put(General.ID, Preferences.get(Quote.QUOTE_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_QUOTES;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject JsonObjectLike_dislike = jsonObject.getAsJsonObject(Actions_.READ);
                    String msg = String.valueOf(JsonObjectLike_dislike.get("msg"));
                    int status = JsonObjectLike_dislike.get(General.STATUS).getAsInt();
                    if (status == 1) {
                        //Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void commentQuoteSend(String commentQuote) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.COMMENT);
        requestMap.put(General.ID, Preferences.get(Quote.QUOTE_ID));
        requestMap.put(General.COMMENT, commentQuote);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_QUOTES;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject JsonObjectLike_dislike = jsonObject.getAsJsonObject(Actions_.COMMENT);
                    String msg = String.valueOf(JsonObjectLike_dislike.get("msg"));
                    int status = JsonObjectLike_dislike.get(General.STATUS).getAsInt();
                    if (status == 1) {
                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void showMoodLayout() {
        positiveQuoteList.setVisibility(View.GONE);
        headerMoodLayout.setVisibility(View.VISIBLE);
        Preferences.save("showQuote", true);
    }

    private void quoteLike_DislikeAPI(String likeDislike) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.LIKE_DISLIKE);
        requestMap.put(General.ID, Preferences.get(Quote.QUOTE_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        if (likeDislike.equalsIgnoreCase("like")) {
            requestMap.put(General.IS_LIKE, String.valueOf(1));
        } else {
            requestMap.put(General.IS_LIKE, String.valueOf(2));
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_QUOTES;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject JsonObjectLike_dislike = jsonObject.getAsJsonObject(Actions_.LIKE_DISLIKE);
                    String msg = String.valueOf(JsonObjectLike_dislike.get("msg"));
                    int status = JsonObjectLike_dislike.get(General.STATUS).getAsInt();
                    if (status == 1) {
                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
