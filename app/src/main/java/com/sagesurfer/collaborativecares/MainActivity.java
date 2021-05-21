package com.sagesurfer.collaborativecares;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cometchat.pro.models.User;
import com.firebase.Config;
import com.firebase.NotificationUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.modules.Dialog.DailogHelper;
import com.modules.assessment.FormShowActivity;
import com.modules.beahivoural_survey.fragment.BeahivouralSurveyFragment;
import com.modules.calendar.CreateEventActivity;
import com.modules.calendar.CustomCalendar;
import com.modules.calendar.CustomCalendarAdapter;
import com.modules.calendar.EventDetailsActivity;
import com.modules.caseload.CaseloadFragment;
import com.modules.caseload.PeerNoteDetailsActivity;
import com.modules.contacts.EmergencyContactDialogFragment;
import com.modules.contacts.ParentEmergencyDialogFragment;
import com.modules.covid_19.adapter.CovidExpandableListAdapter;
import com.modules.covid_19.model.CovidModel;
import com.modules.covid_19.model.CovidTitleModel;
import com.modules.covid_19.model.OptionModel;
import com.modules.journaling.activity.JournalDetailsActivity;
import com.modules.journaling.model.Journal_;
import com.modules.landing_question_form.activity.LandingQuestionFormActivity;
import com.modules.leave_management.fragment.LeaveListingFragment;
import com.modules.mood.CCMoodActivity;
import com.modules.mood.JournalFragment;
import com.modules.mood.MoodCalendarFragment;
import com.modules.mood.MoreFrgament;
import com.modules.mood.StatsFragment;
import com.modules.motivation.activity.CreateMotivationActivity;
import com.modules.onetimehomehealthsurvey.adapter.OneTimeHealthSurveyAdapter;
import com.modules.onetimehomehealthsurvey.model.OneTimeHealthSurvey;
import com.modules.onetimehomehealthsurvey.model.Options;
import com.modules.postcard.MailDetailsActivity;
import com.modules.postcard.Postcard_;
import com.modules.reports.appointment_reports.fragment.AppointmentReportFragment;
import com.modules.selfcare.SelfCareOperations;
import com.modules.selfgoal.AddGoalActivity;
import com.modules.selfgoal.LogBookActivity;
import com.modules.selfgoal.SelfGoalDetailsActivity;
import com.modules.sows.activity.SenjamSowsActivity;
import com.modules.task.CreateTaskActivity;
import com.modules.task.TeamTaskListFragment;
import com.modules.team.TeamDetailsActivity;
import com.modules.team.TeamPeerStaffListActivity;
import com.modules.team.TeamPeerSupervisorListActivity;
import com.sagesurfer.adapters.DrawerListAdapter;
import com.sagesurfer.adapters.DrawerMenuAdapter;
import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.Broadcast;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.interfaces.GoalDetailsInterface;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.library.GetFragments;
import com.sagesurfer.library.GetIntents;
import com.sagesurfer.library.GetSelected;
import com.sagesurfer.logger.Logger;
import com.sagesurfer.models.CaseloadPeerNote_;
import com.sagesurfer.models.DailyDosingSelfGoal_;
import com.sagesurfer.models.DrawerMenu_;
import com.sagesurfer.models.Event_;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.models.Goal_;
import com.sagesurfer.models.HomeMenu_;
import com.sagesurfer.models.LanguageList;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.Invitation_parser;
import com.sagesurfer.parser.Invitations_;
import com.sagesurfer.parser.Login_;
import com.sagesurfer.parser.SenjamDoctorNoteList_;
import com.sagesurfer.selectors.MultiUserSelectorDialog;
import com.sagesurfer.selectors.SingleTeamSelectorDialog;
import com.sagesurfer.services.CounterService;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.tasks.PerformGetTeamsTask;
import com.sagesurfer.tasks.PerformLogoutTask;
import com.sagesurfer.tasks.SaveFirabase;
import com.sagesurfer.tasks.SyncUserData;
import com.sagesurfer.views.Circle;
import com.sagesurfer.views.CircleTransform;
import com.sagesurfer.webservices.Teams;
import com.storage.preferences.AddGoalPreferences;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.RequestBody;
import utils.Utils;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
/**
 * @author Kailash Karankal
 * Created on 13/03/2018
 * Last Modified on
 */

public class MainActivity extends AppCompatActivity implements MainActivityInterface, NavigationView.OnNavigationItemSelectedListener,
        SingleTeamSelectorDialog.GetChoice, View.OnClickListener, MultiUserSelectorDialog.SelectedUsers, GoalDetailsInterface {

    private static final String TAG = MainActivity.class.getSimpleName();
    private List<HomeMenu_> homeMenuList;
    private List<DrawerMenu_> drawerMenuList;
    int mYear, mMonth, mDay, mHour, mMinute;
    private DrawerLayout drawerLayout;
    String other_user_id;
    SharedPreferences domainUrlPref;
    private RelativeLayout mainToolBarBellLayout;
    private AppCompatImageView searchButton, addButton, notificationImageView, addFilter, logBookIcon, setting;
    Menu popupMenuItem;
    private TextView notificationCounterButton;
    private ImageView profilePhoto, imageViewToolbarLeftArrow, imageViewToolbarRightArrow;
    private TextView titleText, nameText, roleText, moodTitleText, mTxtQuestionName, mTxtSelectDate, mTxtOneTimeSurveyHeading,
            mTxtSelectSupervisor, mTxtSelectStaff, mTxtAlreadyAddedBy;
    private Circle circle;
    private ExpandableListView expandableDrawerListView;
    private Toolbar toolbar;
    Spinner groupStatus;
    ImageView navigationHeaderSettingIcon;
    private PerformLogoutTask performLogoutTask;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Intent intent;
    private DrawerListAdapter drawerListAdapter;
    private LinearLayout linearLayoutMoodToolbar;
    private Spinner spinnerToolbarMood;
    private CustomCalendarAdapter mMaterialCalendarAdapter;
    Handler handlerFirebase = new Handler();
    DrawerMenuAdapter drawerMenuAdapter;
    private boolean closeWindowEnable, isGroup;
    boolean clickEvent = false, showFilterIcon = false;
    private String hour = "01", minute = "00", unit = "AM";
    private boolean setBackButton;
    private NotificationUtils notificationUtils;
    private PopupWindow popupWindow;
    // private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String start_date = "", end_date = "", mCurrentDate, selectedReason = "";
    private LeaveListingFragment leaveListingFragment;
    private AppointmentReportFragment appointmentReportFragment;
    private Activity activity;
    private String oneRem = "", twoRem = "", threeRem = "", fourRem = "", fiveRem = "";
    private ArrayList<Invitations_> invitationsArrayList = new ArrayList<>();
    private ArrayList<Invitations_> invitationsPeerStaffArrayList = new ArrayList<>();
    private ArrayList<Invitations_> alreadyaddedPeerStaffArrayList = new ArrayList<>();
    public Invitations_ selectedSupervisor;
    private int SUPERVISOR_REQUEST_CODE = 11;
    private int PEER_STAFF_REQUEST_CODE = 12;
    private int iconClick = 0;
    private int ICON_LEAVE_CLICKED = 1;
    private int ICON_SETTING_CLICKED = 2;
    private String superName, peerName, currentLang;
    private Dialog dialog;
    ArrayList<DailyDosingSelfGoal_> dailyDosingSelfGoal_arrayList = new ArrayList<>();
    private String mAnswer, mGoalID, mMainGoalID, onDate;
    private LinearLayoutManager mLinearLayoutManager;
    public ArrayList<OneTimeHealthSurvey> oneTimeHealthSurveyArrayList = new ArrayList<>();
    private OneTimeHealthSurveyAdapter oneTimeHealthSurveyAdapter;
    List<CovidTitleModel> expandableListTitle = new ArrayList<>();
    private RecyclerView mRecyclerViewOneSurVeyQuestionAnswers;
    private ExpandableListView mExpandableListView;
    private CovidExpandableListAdapter covidExpandableListAdapter;
    private Spinner spinner;
    private ArrayAdapter adapter;
    private ArrayList<String> spinnerList;
    private SimpleDateFormat sdfDate;
    private Date CurrentDate, StartDate;
    ArrayList<LanguageList> languageList;
    ArrayList<String> lanList;
    private ArrayAdapter<String> reasonAdapter;
    SharedPreferences sp;
    RecyclerView rv_drawerMenus;
    private static final int JOB_ID = 0;
    AppCompatImageView chat_icon, main_toolbar_bell;
    private JobScheduler mScheduler;
    private SharedPreferences preferencesCheckCurrentActivity;
    private SharedPreferences.Editor editor;
    PopupMenu popup;
    MenuItem menuIteSetAvailability;
    EditText et_startTime, et_endTime;
    int StartTimeHour, EndTimeHour, StartTimeMin, EndTimeMin;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"WrongConstant", "RestrictedApi", "SourceLockedOrientationActivity", "NewApi"})
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.initialize(getApplicationContext());
        activity = this;
        /*this preferences is created for firebase messaging service to know that the user is on which activity
         * here we are clearing preferences
         * created by rahulmsk */
        preferencesCheckCurrentActivity = getSharedPreferences("preferencesCheckCurrentActivity", MODE_PRIVATE);
        editor = preferencesCheckCurrentActivity.edit();
        editor.putBoolean("IsChatScreen", false);
        editor.putBoolean("IsFriendListingPage", false);
        editor.commit();
        performLogoutTask = new PerformLogoutTask();
        sp = getSharedPreferences("login", MODE_PRIVATE);


        if (Preferences.get(General.IS_LOGIN).equalsIgnoreCase("1")) {
            setContentView(R.layout.activity_main);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

           /* try {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Create channel to show notifications.
                    String channelId = getString(R.string.default_notification_channel_id);
                    String channelName = getString(R.string.notifications_admin_channel_name);
                    notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            createNotificationChannel();
            intent = new Intent(this, CounterService.class);
            homeMenuList = new ArrayList<>();
            drawerMenuList = new ArrayList<>();

            String fName = Preferences.get(General.FIRST_NAME);
            String lName = Preferences.get(General.LAST_NAME);

            if (!fName.isEmpty() && !lName.isEmpty()) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("flname", fName + "_" + lName);
                editor.commit();
            }

            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    assert intent.getAction() != null;
                    // checking for type intent filter
                    if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                        if (!Preferences.getBoolean(General.IS_PUSH_NOTIFICATION_SENT)) {
                            NotificationUtils.clearNotifications(getApplicationContext());
                            for (Map.Entry<String, List<String>> entry : Config.mapOfPosts.entrySet()) {
                                Preferences.save(General.IS_PUSH_NOTIFICATION_SENT, true);
                                showNotificationMessage(entry.getValue().get(0), entry.getValue().get(1), entry.getValue().get(2), entry.getKey());
                            }
                        }
                    }
                }
            };

            displayFirebaseRegId();

            //mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            toolbar = (Toolbar) findViewById(R.id.main_toolbar_layout);
            setSupportActionBar(toolbar);
            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
            mainToolBarBellLayout = toolbar.findViewById(R.id.main_toolbar_bell_layout);
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                @Override
                public void onDrawerStateChanged(int newState) {
                    toolbar.setNavigationIcon(R.drawable.vi_drawer_hamburger_icon);
                }
            };
            toggle.setDrawerIndicatorEnabled(false);
            toolbar.setNavigationIcon(R.drawable.vi_drawer_hamburger_icon);
            chat_icon = toolbar.findViewById(R.id.chat_icon);
            main_toolbar_bell = toolbar.findViewById(R.id.main_toolbar_bell);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(Gravity.START);
                }
            });
            drawerLayout.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            //circle = (Circle) navigationView.findViewById(R.id.nav_header_circle);
            profilePhoto = (ImageView) navigationView.findViewById(R.id.nav_header_image);
            roleText = (TextView) navigationView.findViewById(R.id.nav_header_role);
            nameText = (TextView) navigationView.findViewById(R.id.nav_header_name);
            navigationHeaderSettingIcon = (ImageView) navigationView.findViewById(R.id.nav_settingIcon);
            expandableDrawerListView = (ExpandableListView) navigationView.findViewById(R.id.drawer_list_view);
            rv_drawerMenus = (RecyclerView) navigationView.findViewById(R.id.rv_drawerMenus);
            rv_drawerMenus.setHasFixedSize(true);
            rv_drawerMenus.setLayoutManager(new LinearLayoutManager(this));

            /*Navigation Header Main Setting Icon click listner */
            navigationHeaderSettingIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: we reached on setting onclick... ");
                    drawerLayout.closeDrawer(Gravity.START);
                    Bundle bundle = new Bundle();
                    Fragment fragment = GetFragments.get(53, bundle);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                    ft.replace(R.id.app_bar_main_container, fragment, TAG);
                    ft.commit();
                }
            });

            linearLayoutMoodToolbar = (LinearLayout) toolbar.findViewById(R.id.linearlayout_mood_toolbar);
            imageViewToolbarLeftArrow = (ImageView) toolbar.findViewById(R.id.imageview_toolbar_left_arrorw);

            moodTitleText = (TextView) toolbar.findViewById(R.id.textview_toolbar_mood_title);
            imageViewToolbarRightArrow = (ImageView) toolbar.findViewById(R.id.imageview_toolbar_right_arrorw);
            titleText = (TextView) toolbar.findViewById(R.id.main_toolbar_title);
            titleText.setText(this.getString(R.string.home));
            notificationCounterButton = (TextView) toolbar.findViewById(R.id.main_toolbar_bell_counter);
            notificationImageView = (AppCompatImageView) toolbar.findViewById(R.id.main_toolbar_bell);

            notificationImageView.setOnClickListener(this);

            addButton = (AppCompatImageView) toolbar.findViewById(R.id.main_toolbar_add);
            logBookIcon = (AppCompatImageView) toolbar.findViewById(R.id.main_toolbar_log_book);

            // setting button on comet chat screen
            setting = toolbar.findViewById(R.id.setting);

            //setting.setVisibility(View.GONE);
            hidesettingIcon(true);

            setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // open menu popup for change language and blocked user list
                    showSettingPopup(v);
                }
            });

            chat_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    Fragment fragment = GetFragments.get(9, bundle);
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                    ft.replace(R.id.app_bar_main_container, fragment, TAG);
                    ft.commit();
                }
            });

            addButton.setVisibility(View.GONE);
            logBookIcon.setVisibility(View.GONE);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addButtonClick(v);
                }
            });

            logBookIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015)) ||
                            Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) ||
                            Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025)) ||
                            Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024)) ||
                            Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) ||
                            Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027)) ||
                            Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage006)) ||
                            Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                            Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) {
                        Intent intent = new Intent(MainActivity.this, LogBookActivity.class);
                        startActivity(intent);
                    }
                }
            });

            sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
            mCurrentDate = sdfDate.format(now);

            searchButton = (AppCompatImageView) toolbar.findViewById(R.id.main_toolbar_search);
            searchButton.setVisibility(View.GONE);

            addFilter = (AppCompatImageView) toolbar.findViewById(R.id.main_filter_add);
            addFilter.setOnClickListener(this);

            // setDrawerMenuList();
            setDrawerMenuListByRecyclerview();

            Preferences.save(General.HOME_ICON_NUMBER, "0");
            replaceFragment(50, drawerMenuList.get(1).getMenu(), null);
            homeMenuList = Login_.homeMenuParser();

            if (!Preferences.getBoolean(General.IS_COMETCHAT_LOGIN_SUCCESS)) {
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage013")
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage016")) {
                    Preferences.save(General.IS_COMETCHAT_LOGIN_SUCCESS, true);
                }
            }
            handleIntent(getIntent());
        } else {
            Intent mainIntent = new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(mainIntent);
            finish();
        }

        AddGoalPreferences.initialize(MainActivity.this);
        AddGoalPreferences.save(General.START_MINUTE, minute, TAG);
        AddGoalPreferences.save(General.START_HOUR, hour, TAG);
        AddGoalPreferences.save(General.TIME_UNIT, unit, TAG);
        Preferences.save(General.SELECTED_MOOD_FRAGMENT, getResources().getString(R.string.journal));

        if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026))
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) &&
                ((Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_ComplianceAdministrator) ||
                        Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_SystemAdministrator)) ||
                        Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_ClinicalApplicationsAdministrator))) {

//            popUpForManageRelationShip();
            getPeerStaffListData(true);
        }

        // get popup only for senjam instance
        if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030))
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031)))) {
            getAllPopUp();

        }
        /*This runnable is created for fetching admin given access for the user for set availability */
        if (!Preferences.get(General.ROLE_ID).equals("28")) {
            MyRunnableGetRole myRunnable = new MyRunnableGetRole();
            Thread t = new Thread(myRunnable);
            t.start();
        }
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("2", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    /*created method to get firebase token data in seperate thread
    * created by rahul..
    public void getFirebaseToken(){

    }*/

    /* @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.menu_setting, menu);
        // myMenu = menu;
         MenuItem item = menu.findItem(R.id.menu_block_member);
         if (item != null) {
             item.setVisible(false);
         }
         return true;
     }*/
    private void showSettingPopup(View view) {
        popup = new PopupMenu(MainActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.menu_setting, popup.getMenu());
        popupMenuItem = popup.getMenu();
        menuIteSetAvailability = popupMenuItem.findItem(R.id.set_available_time);

        /* if the user id is 28 or admin given access
         * then set availability option in settings icon will be visible
         * here we are hiding or showing the option
         * */
        if (Preferences.get(General.ROLE_ID).equals("28")) {
            if (menuIteSetAvailability != null) {
                menuIteSetAvailability.setVisible(true);
                Log.i(TAG, "getSetAvailabilityRolesFromServer: 1");
            }
        } else if (domainUrlPref.getString("other_user_id",null).equals("1")) {
            if (menuIteSetAvailability != null) {
                menuIteSetAvailability.setVisible(true);
                Log.i(TAG, "getSetAvailabilityRolesFromServer: 2");
            }
        } else {
            Log.i(TAG, "getSetAvailabilityRolesFromServer: 3");
            if (menuIteSetAvailability != null) {
                menuIteSetAvailability.setVisible(false);
            }
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_block_member:
                        Intent blockmembers = new Intent(getApplicationContext(), BlockedMembersActivity.class);
                        startActivity(blockmembers);
                        break;
                    case R.id.menu_change_language:
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.language_change);

                        groupStatus = dialog.findViewById(R.id.languageList);
                        ImageView cancelLanguage = dialog.findViewById(R.id.cancelLanguage);
                        ImageView btnsubmitLang = dialog.findViewById(R.id.btnsubmitLang);

                        // get all languages form db
                        getLanguage("language_list");
                        currentLang = sp.getString("full_name", currentLang);
                        Log.e(TAG, "onMenuItemClick: current language " + currentLang);
                        reasonAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.drop_down_selected_text_item_layout, lanList);
                        reasonAdapter.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                        groupStatus.setAdapter(reasonAdapter);

                        int index = lanList.indexOf(currentLang);
                        groupStatus.setSelection(index);

                        groupStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedReason = languageList.get(position).getId();
                            }

                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        btnsubmitLang.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.i(TAG, "onClick: " + selectedReason);
                                getUpdateLanguage("update_language", selectedReason, Preferences.get(General.USER_ID));
                                dialog.dismiss();
                            }
                        });

                        cancelLanguage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;

                    case R.id.chatt_icon:
                        Bundle bundle = new Bundle();
                        Fragment fragment = GetFragments.get(82, bundle);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                        ft.replace(R.id.app_bar_main_container, fragment, TAG);
                        ft.commit();
                        break;

                    case R.id.call_history:
                        Bundle bundleCalls = new Bundle();
                        Fragment fragmentCalls = GetFragments.get(83, bundleCalls);
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                        fragmentTransaction.replace(R.id.app_bar_main_container, fragmentCalls, TAG);
                        fragmentTransaction.commit();
                        break;

                    case R.id.set_available_time:
                        openSetAvailabilityDialog();
                        break;
                }
                popup.dismiss();
                return true;
            }
        });
        popup.show();
    }

    /*public void hideSetAvailabilityMenu(boolean toggle){
        if (menuIteSetAvailability != null) {
            Log.i(TAG, "hideSetAvailabilityMenu: ");
            menuIteSetAvailability.setVisible(toggle);
        }
    }*/

    public void openSetAvailabilityDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_provider_time_slot, null);
        builder.setView(view);
        dialog = builder.create();
        et_startTime = view.findViewById(R.id.et_startTime);
        et_endTime = view.findViewById(R.id.et_endTime);
        Button btn_add = (Button) view.findViewById(R.id.btn_add);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        fetchPreviousSavedAvailavleSlot("old_time_provider");

        et_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                try {
                                    StartTimeHour = hourOfDay;
                                    StartTimeMin = minute;
                                    et_startTime.setText(ConvertTimeIn12hr(hourOfDay + ":" + minute));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        et_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                try {
                                    EndTimeHour = hourOfDay;
                                    EndTimeMin = minute;
                                    et_endTime.setText("" + ConvertTimeIn12hr(hourOfDay + ":" + minute));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // saveProviderSlot("provider_time",)
                if (!et_startTime.getText().toString().equals("") || et_startTime.getText() == null) {
                    if (et_endTime.getText().toString().equals("") || et_endTime.getText() != null) {

                        String starthour, startMinute, startType, endhour, endMinute, endType;
                        String array[] = et_startTime.getText().toString().split(":");
                        String arrayAmPm[] = array[1].split(" ");
                        starthour = array[0];
                        startMinute = arrayAmPm[0];
                        startType = arrayAmPm[1];

                        String array2[] = et_endTime.getText().toString().split(":");
                        String arrayAmPm2[] = array2[1].split(" ");
                        endhour = array2[0];
                        endMinute = arrayAmPm2[0];
                        endType = arrayAmPm2[1];

                       /* if (array[0].equals("0")) {
                            starthour = "1";
                        }
                        if (array2[0].equals("0")) {
                            endhour = "1";
                        }
*/
                        if (StartTimeHour <= EndTimeHour) {
                            if (StartTimeHour == EndTimeHour) {
                                if (StartTimeMin != EndTimeMin) {
                                    // save data
                                    saveProviderSlot(starthour, startMinute, startType, endhour, endMinute, endType, "provider_time");
                                    dialog.dismiss();
                                } else {
                                    //et_endTime.setError("Invalid end time");
                                    Toast.makeText(MainActivity.this, "Invalid end time", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                saveProviderSlot(starthour, startMinute, startType, endhour, endMinute, endType, "provider_time");
                                dialog.dismiss();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Invalid end time", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //et_endTime.setError("Field required");
                        Toast.makeText(MainActivity.this, "End time required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Start time required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setCancelable(false);
    }

    public String ConvertTimeIn12hr(String time) throws ParseException {

        final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
        final Date dateObj = sdf.parse(time);
        Log.i(TAG, "ConvertTimeIn12hr: dateObj" + dateObj);
        Log.i(TAG, "ConvertTimeIn12hr: new SimpleDateFormat " + new SimpleDateFormat("K:mm a").format(dateObj));
        return new SimpleDateFormat("K:mm a").format(dateObj);
    }

    private void saveProviderSlot(String starthour, String startMinute, String startType, String endhour, String endMinute, String endType, String action) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.FROM_TIMEH, starthour);
        requestMap.put(General.FROM_TIMEM, startMinute);
        requestMap.put(General.FROM_TIMEMAM, startType);
        requestMap.put(General.TOTIMEH, endhour);
        requestMap.put(General.TOTIMEM, endMinute);
        requestMap.put(General.TOTIMES, endType);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this);
                if (response != null) {
                    Log.e(" saveProviderSlot data", response);
                    //{"provider_time":[{"status":1,"msg":"update successfully."}]}

                    JSONObject injectedObject = new JSONObject(response);
                    JSONArray array = injectedObject.getJSONArray("provider_time");
                    Log.i(TAG, "saveProviderSlot: " + array.getJSONObject(0).getString("msg"));
                    Toast.makeText(getApplicationContext(), "" + array.getJSONObject(0).getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchPreviousSavedAvailavleSlot(String action) {

        /*action=old_time_provider
            user_id=//login_user_id
            Return parameter
            from_date=//return form date
            to_date=//return to date */

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        Log.i(TAG, "fetchPreviousSavedAvailavleSlot: userId " + Preferences.get(General.USER_ID));
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this);
                if (response != null) {
                    Log.e(TAG, "fetchPreviousSavedAvailavleSlot " + response);
                    //{"provider_time":[{"status":1,"msg":"update successfully."}]}

                    JSONObject injectedObject = new JSONObject(response);
                    JSONArray array = injectedObject.getJSONArray("old_time_provider");
                    Log.i(TAG, "fetchPreviousSavedAvailavleSlot  " + array.getJSONObject(0).getString("msg"));
                    et_startTime.setText("" + array.getJSONObject(0).getString("from_date"));
                    et_endTime.setText("" + array.getJSONObject(0).getString("to_date"));
                    //Toast.makeText(getApplicationContext(), ""+array.getJSONObject(0).getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*public void hidePopup(){
        popup.getMenu().getItem(R.id.chatt_icon).setVisible(false);
    }
*/
    private void getLanguage(String action) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this);
                if (response != null) {
                    Log.e("data", response);
                    try {
                        JSONObject injectedObject = new JSONObject(response);
                        JSONArray translations = injectedObject.getJSONArray("language_list");

                        languageList = new ArrayList<LanguageList>();
                        lanList = new ArrayList<String>();

                        for (int i = 0; i < translations.length(); i++) {
                            LanguageList model = new LanguageList();
                            JSONObject translation = translations.getJSONObject(i);
                            model.setId(translation.getString("id"));
                            model.setName(translation.getString("name"));
                            languageList.add(model);

                            String names = translation.getString("name");
                            lanList.add(names);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getCurrentLanguage(String action, String UserId) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, UserId);
        Log.i(TAG, "getCurrentLanguage: ");
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this);

                if (response != null) {
                    Log.e(TAG, " getCurrentLanguage " + response);
                    try {
                        JSONObject injectedObject = new JSONObject(response);
                        JSONArray translations = injectedObject.getJSONArray("current_language");

                        String s = null;
                        for (int i = 0; i < translations.length(); i++) {
                            JSONObject translation = translations.getJSONObject(i);

                            s = translation.getString("name");
                            Log.e(TAG, " getCurrentLanguage is " + s);
                            Log.e("", s);
                        }
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("currentLang", s);
                        editor.commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getUpdateLanguage(String action, String language, String UserId) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put("language", language);
        requestMap.put(General.USER_ID, UserId);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this);

                if (response != null) {
                    Log.i(TAG, "getUpdateLanguage success response" + response);
                    getCurrentLanguage("current_language", Preferences.get(General.USER_ID));
                    Toast.makeText(MainActivity.this, "Language changed successfully", Toast.LENGTH_SHORT).show();
//                    CometChatMessageScreen messageScreen = new CometChatMessageScreen();
//                  messageScreen.fetchMessage();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getAllPopUp() {

        if (Preferences.getBoolean(General.SHOW_PRIVACY_POPUP_FILLED)) {
            popUpForPrivacy();
        } else if (Preferences.getBoolean(General.SHOW_ONE_TIME_SURVEY_FILLED)) {
            getOneSurveyQuestionAnswerAPICalled(true);
        }
        /*else if (Preferences.getBoolean(General.SHOW_DAILY_SURVEY_FILLED)) {
            getImmunityRespiratoryCovid19SurveyListAPICalled(true);
        }
        else if (Preferences.getBoolean(General.SHOW_DAILY_DOSAGE_FILLED)) { //Enable if want to open daily dose from Login/Sync
            showDailyDosingComplianceDialog(General.SENJAM_GOAL_ID, "AM");
        }*/
    }

    public void popUpForManageRelationShip() {
        try {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View customView = inflater.inflate(R.layout.popup_manage_relationship_layout, null);
            popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

            findViewById(R.id.app_bar_main_container).post(new Runnable() {
                public void run() {
                    popupWindow.showAtLocation(findViewById(R.id.app_bar_main_container), Gravity.CENTER, 0, 0);
                }
            });
            popupWindow.setOutsideTouchable(false);
            popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            popupWindow.isShowing();

            TextView mBtnCancel, mBtnAssignment;
            LinearLayout mLinearBtnCancel, mLinearBtnAssignment, mLinearTxtSupervisor, mLinearAlreadyAdded;

            mTxtSelectSupervisor = customView.findViewById(R.id.txt_select_supervisor);
            mLinearTxtSupervisor = customView.findViewById(R.id.linear_txt_title_supervisor);
            mTxtSelectStaff = customView.findViewById(R.id.txt_select_staff);
            mBtnCancel = customView.findViewById(R.id.btn_cancel);
            mLinearBtnCancel = customView.findViewById(R.id.linear_btn_cancel);
            mLinearBtnAssignment = customView.findViewById(R.id.linear_btn_assignment);
            mLinearAlreadyAdded = customView.findViewById(R.id.linear_already_added);
            mTxtAlreadyAddedBy = customView.findViewById(R.id.txt_added_by);
            mBtnAssignment = customView.findViewById(R.id.btn_assignment);

            mLinearAlreadyAdded.setVisibility(View.VISIBLE);

            getSupervisorListData();
//            getPeerStaffListData();

            mLinearTxtSupervisor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (mTxtSelectSupervisor.getText().toString().equalsIgnoreCase("Select Peer Supervisor")) {
                        // reset
                        for (int i = 0; i < invitationsArrayList.size(); i++) {
                            invitationsArrayList.get(i).setSelected(false);
                            selectedSupervisor = null;
                        }
                    }

                    Intent intent = new Intent(MainActivity.this, TeamPeerSupervisorListActivity.class);
                    intent.putExtra("invitationsArrayList", invitationsArrayList);
                    intent.putExtra("invitation", selectedSupervisor);
                    startActivityForResult(intent, SUPERVISOR_REQUEST_CODE);

                }
            });

            mTxtSelectStaff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (mTxtSelectStaff.getText().toString().equalsIgnoreCase("Select Peer Staff")) {
                        // reset
                        for (int i = 0; i < invitationsPeerStaffArrayList.size(); i++) {
                            invitationsPeerStaffArrayList.get(i).setSelected(false);
                        }
                    }

                    Intent intent = new Intent(MainActivity.this, TeamPeerStaffListActivity.class);
                    intent.putExtra("invitationsPeerStaffArrayList", invitationsPeerStaffArrayList);
                    startActivityForResult(intent, PEER_STAFF_REQUEST_CODE);
                }
            });

            mBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

            mBtnAssignment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (TextUtils.isEmpty(superName)) {
                        Toast.makeText(activity, "Please Select Peer Supervisor", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(peerName)) {
                        Toast.makeText(activity, "Please Select Peer Staff", Toast.LENGTH_SHORT).show();
                    } else {
                        showAddProgressNoteDialog(getResources().getString(R.string.are_you_sure_you_want_to_add_this_assignment));
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Show Privacy Popup
    @SuppressLint("SetJavaScriptEnabled")
    public void popUpForPrivacy() {
        try {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View customView = inflater.inflate(R.layout.popup_privacy_layout, null);
            popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

            findViewById(R.id.app_bar_main_container).post(new Runnable() {
                public void run() {
                    popupWindow.showAtLocation(findViewById(R.id.app_bar_main_container), Gravity.CENTER, 0, 0);
                }
            });
            popupWindow.setOutsideTouchable(false);
            popupWindow.setFocusable(false);
            popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            popupWindow.isShowing();

            WebView mWebView;
            TextView mTextViewAccept;

            mWebView = customView.findViewById(R.id.web_view);
            mTextViewAccept = customView.findViewById(R.id.btn_accept);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setWebViewClient(new WebViewClient());
            mWebView.loadUrl(Preferences.get(General.PRIVACY_URL));

            mTextViewAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Api Called when Accept button Clicked
                    acceptPrivacyApi();
                }
            });

            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Api call for Privacy submit*/
    @SuppressLint("LongLogTag")
    private void acceptPrivacyApi() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ACCEPT_PRIVACY);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_SENJAM_SURVEY;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("acceptPrivacyApiResponse", response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddJournal = jsonObject.getAsJsonObject(Actions_.ACCEPT_PRIVACY);
                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(this, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        popupWindow.dismiss();

                        // If Status is 1 then Call Api for One Time Survey Question Answer
                        Preferences.save(General.SHOW_PRIVACY_POPUP_FILLED, false);
                        if (Preferences.getBoolean(General.SHOW_ONE_TIME_SURVEY_FILLED)) {
                            getOneSurveyQuestionAnswerAPICalled(true);
                        }
                    } else {
                        Toast.makeText(this, jsonAddJournal.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(activity, "No Data", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getSupervisorListData() {
        String action = Actions_.GET_SUPERVISOR_LIST_MHAW;
        HashMap<String, String> requestMap = new HashMap<>();
        invitationsArrayList.clear();
        requestMap.put(General.ACTION, action);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("ResponseSupervisor", response);
                if (response != null) {
                    invitationsArrayList = Invitation_parser.parseSupervisorListPopup(response, action, this, TAG);
                    if (invitationsArrayList.size() > 0) {
                        if (invitationsArrayList.get(0).getStatus() == 1) {

                        } else {
                            Log.e("ErrorClientListOne", "" + invitationsArrayList.get(0).getStatus());
                        }
                    } else {
                        Log.e("ErrorClientListTwo", "" + invitationsArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ErrorAppoResuClient", e.getMessage());
            }
        }
    }

    private void getPeerStaffListData(Boolean isPopUpOpenOrClose) {
        String action = Actions_.GET_IDEAL_PREESTAFF_MHAW;
        HashMap<String, String> requestMap = new HashMap<>();

        invitationsPeerStaffArrayList.clear();
        requestMap.put(General.ACTION, action);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("ResponseSupervisor", response);
                if (response != null) {
                    invitationsPeerStaffArrayList = Invitation_parser.parseSupervisorListPopup(response, action, this, TAG);
                    if (invitationsPeerStaffArrayList.size() > 0) {
                        if (invitationsPeerStaffArrayList.get(0).getStatus() == 1) {
                            if (isPopUpOpenOrClose) {
                                popUpForManageRelationShip();
                            } else {

                            }
                        } else if (invitationsPeerStaffArrayList.get(0).getStatus() == 2) {
//                            Log.e("ErrorPeerStaffOne", "" + invitationsPeerStaffArrayList.get(0).getStatus());
                            // Toast.makeText(activity, ""+ invitationsPeerStaffArrayList.get(0).getError(), Toast.LENGTH_SHORT).show();
                            JSONObject object = new JSONObject(response);
                            JSONArray array = object.getJSONArray(action);
                            JSONObject actionObj = array.getJSONObject(0);
                            Toast.makeText(activity, "" + actionObj.optString("error"), Toast.LENGTH_LONG).show();
                            Log.e("DATAAA__", actionObj.optString("error"));
                            if (isPopUpOpenOrClose) {
                            } else {
                                popupWindow.dismiss();
                            }

                        }
                    } else {
                        Log.e("ErrorPeerStaffTwo", "" + invitationsPeerStaffArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ErrorPeerStaff", e.getMessage());
            }
        }
    }

    private void getPeerStaffAssignedSupervisorListData(String supervisorID) {
        String action = Actions_.GET_PEER_STAFF_ASSIGNED_SUPERVISOR_MHAW;
        HashMap<String, String> requestMap = new HashMap<>();

        alreadyaddedPeerStaffArrayList.clear();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.SUPERVISER_ID, supervisorID);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("ResponseStaffAssign", response);
                if (response != null) {
                    alreadyaddedPeerStaffArrayList = Invitation_parser.parseSupervisorListPopup(response, action, this, TAG);
                    if (alreadyaddedPeerStaffArrayList.size() > 0) {
                        if (alreadyaddedPeerStaffArrayList.get(0).getStatus() == 1) {
                            String strAlreadyAdded = "";
                            for (int i = 0; i < alreadyaddedPeerStaffArrayList.size(); i++) {
                                if (i == 0) {
                                    strAlreadyAdded = strAlreadyAdded + alreadyaddedPeerStaffArrayList.get(i).getUsername();
                                } else {
                                    strAlreadyAdded = strAlreadyAdded + ", " + alreadyaddedPeerStaffArrayList.get(i).getUsername();
                                }
                            }

                            mTxtAlreadyAddedBy.setText(strAlreadyAdded);
                        } else if (alreadyaddedPeerStaffArrayList.get(0).getStatus() == 2) {
                            mTxtAlreadyAddedBy.setText(alreadyaddedPeerStaffArrayList.get(0).getError());
                        }
                    } else {
                        Log.e("ErrorAlreadyAddedTwo", "" + alreadyaddedPeerStaffArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ErrorAlreadyAdded", e.getMessage());
            }
        }
    }

    /*Daily Dosing Compliance Dialog*/
    @SuppressLint("SetTextI18n")
    public void showDailyDosingComplianceDialog(final String date, String selfgoal_id, final String AM_PM, String message, final Activity activity) {
        dialog = new Dialog(this.activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_daily_dosing_compliance);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        mTxtQuestionName = (TextView) dialog.findViewById(R.id.txt_question_name_value);
        mTxtSelectDate = (TextView) dialog.findViewById(R.id.txt_select_date_com);
        final Button mButtonSubmit = (Button) dialog.findViewById(R.id.button_submit);
        final ImageButton buttonClose = (ImageButton) dialog.findViewById(R.id.button_close);
        final RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup1);
        final RadioButton radioButtonNo = dialog.findViewById(R.id.radio_no);

        mAnswer = String.valueOf(radioButtonNo.getTag());
        Log.e("mAnswer", mAnswer);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                mAnswer = String.valueOf(radioButton.getTag());
            }
        });

//        Date todayDate = Calendar.getInstance().getTime();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        String todayString = formatter.format(todayDate);

        mTxtSelectDate.setText(date);
        mTxtQuestionName.setText(message);

        /*Api call for Daily Dosing Compliance Detail*/

        dailyDosingDetailsAPICalled(selfgoal_id);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onDate = mTxtSelectDate.getText().toString();
                /*Add answer Api call on Submit button */
                dailyDosingAddAPICalled(mAnswer, mGoalID, date, mMainGoalID, AM_PM, activity);
            }
        });

        dialog.show();
    }

    // PopUp for One Time Health Survey
    public void popUpForOneTimeHealthSurvey(final Activity activity) {
        try {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View customView = inflater.inflate(R.layout.popup_one_time_health_survey_layout, null);
            popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

            findViewById(R.id.app_bar_main_container).post(new Runnable() {
                public void run() {
                    popupWindow.showAtLocation(findViewById(R.id.app_bar_main_container), Gravity.CENTER, 0, 0);
                }
            });
            popupWindow.setOutsideTouchable(false);
            popupWindow.setFocusable(false);
            popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            popupWindow.isShowing();


            Button mBtnSubmit;

            mRecyclerViewOneSurVeyQuestionAnswers = customView.findViewById(R.id.recyclerView);
            mTxtOneTimeSurveyHeading = customView.findViewById(R.id.txt_one_time_survey_heading);
            mBtnSubmit = customView.findViewById(R.id.button_submit);
            mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecyclerViewOneSurVeyQuestionAnswers.setLayoutManager(mLinearLayoutManager);

            mBtnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String ans_ids = oneTimeHealthSurveyAdapter.getAnsValue();
                    Log.e("Ids", ans_ids);

                    // when submit button clicked one time Health Survey Question Answer Api called
                    submitneSurveyQuestionAnswerAPICalled(ans_ids, activity);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Api call for One Survey Question Answer Data*/
    @SuppressLint("LongLogTag")
    private void getOneSurveyQuestionAnswerAPICalled(Boolean isPopUpOpenOrClose) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_ONE_SURVEY_QUESTION_ANSWERS);

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_SENJAM_SURVEY;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("OneSurveyQuestionAnswersResponse", response);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    String heading = jsonObject.getString(General.HEADING);
                    String checkStatus = jsonObject.getString(General.STATUS);

                    // if status is 1 then show popup
                    if (checkStatus.equalsIgnoreCase("1")) {
                        // check popup is open or close
                        if (isPopUpOpenOrClose) {
                            popUpForOneTimeHealthSurvey(activity);
                        }
                        mTxtOneTimeSurveyHeading.setText(heading);
                        JSONArray jsonArray = jsonObject.getJSONArray(Actions_.GET_ONE_SURVEY_QUESTION_ANSWERS);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            OneTimeHealthSurvey model = new OneTimeHealthSurvey();
                            model.setId(object.getString(General.ID));
                            model.setQues(object.getString(General.QUES));
                            model.setSelected(true);
                            JSONArray optionArray = object.getJSONArray(General.OPTIONS);
                            ArrayList<Options> optionsArrayList = new ArrayList<>();
                            for (int j = 0; j < optionArray.length(); j++) {
                                JSONObject optionObject = optionArray.getJSONObject(j);
                                Options optionsModel = new Options();
                                optionsModel.setAnswer(optionObject.getString(General.ANSWER));
                                optionsModel.setId(optionObject.getString(General.ID));
                                optionsArrayList.add(optionsModel);
                                if (j == 0) {
                                    model.setAnsId(optionObject.getString(General.ID));
                                    model.setSelectedRadioBtnId(Integer.parseInt(optionObject.getString(General.ID)));
                                }
                            }

                            model.setOptions(optionsArrayList);
                            oneTimeHealthSurveyArrayList.add(model);
                        }
                        oneTimeHealthSurveyAdapter = new OneTimeHealthSurveyAdapter(this, oneTimeHealthSurveyArrayList);
                        mRecyclerViewOneSurVeyQuestionAnswers.setAdapter(oneTimeHealthSurveyAdapter);
                        oneTimeHealthSurveyAdapter.notifyDataSetChanged();

//                        popUpForOneTimeHealthSurvey();
                    } else {
                        if (isPopUpOpenOrClose) {
                        } else {
                            popupWindow.dismiss();
                        }
                    }

                } else {
                    Toast.makeText(activity, "No Data", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Popup for Covid 19 Survey
    public void popUpForCovid19Survey(final Activity activity) {
        try {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View customView = inflater.inflate(R.layout.popup_immunity_respiratoty_covid_survey_layout, null);
            popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

            findViewById(R.id.app_bar_main_container).post(new Runnable() {
                public void run() {
                    popupWindow.showAtLocation(findViewById(R.id.app_bar_main_container), Gravity.CENTER, 0, 0);
                }
            });
            popupWindow.setOutsideTouchable(false);
            popupWindow.setFocusable(false);
            popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            popupWindow.isShowing();

            Button mBtnSubmit;
            spinnerList = new ArrayList<>();
            spinnerList.add("Immunity");
            spinnerList.add("Respiratory");
            spinnerList.add("COVID-19");


            mExpandableListView = customView.findViewById(R.id.expandableListView);
            mBtnSubmit = customView.findViewById(R.id.button_submit);
            spinner = customView.findViewById(R.id.spinner);

            adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(onItemSelectedCovid19Section);
//            getImmunityRespiratoryCovid19SurveyListAPICalled(true);
            mBtnSubmit.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onClick(View view) {

                    Log.e("covidExpandableListAdapterIds", covidExpandableListAdapter.getAnsValue());

                    String answer_id = covidExpandableListAdapter.getAnsValue();
                    submitImmunityRespiratoryCovid19SurveyAPICalled(answer_id, activity);

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final AdapterView.OnItemSelectedListener onItemSelectedCovid19Section = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mExpandableListView.setSelectedGroup(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    // Api called for Covid 19 List data
    @SuppressLint({"SetTextI18n", "LongLogTag"})
    public void getImmunityRespiratoryCovid19SurveyListAPICalled(Boolean isPopUpOpenOrClose, final Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_DAILY_SURVEY_QUESTIONS_ANSWERS);
        Log.e("requestMap", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_SENJAM_SURVEY;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("ImmunityRespiratotyCovid19SurveyResponse", response);

                if (response != null) {
//                    if (isPopUpOpenOrClose) {
//                        popUpForCovid19Survey();
//                    } else {
//
//                    }
                    JSONObject jsonObject = new JSONObject(response);

                    String checkStatus = jsonObject.getString(General.STATUS);

                    if (checkStatus.equalsIgnoreCase("1")) {
                        if (isPopUpOpenOrClose) {
                            popUpForCovid19Survey(activity);
                        }

                        JSONObject getDetailDailySurvey = jsonObject.getJSONObject(Actions_.GET_DAILY_SURVEY_QUESTIONS_ANSWERS);

                        JSONObject rateYourImmuneFitness = getDetailDailySurvey.getJSONObject(General.RATE_YOUR_IMMUNE_FITNESS);
                        setQuestionListData(rateYourImmuneFitness);

                        JSONObject respiratoryInfectionSymptom = getDetailDailySurvey.getJSONObject(General.RESPIRATORY_INFECTION_SYMPTOM);
                        setQuestionListData(respiratoryInfectionSymptom);

                        JSONObject covid19SpecificQuestion = getDetailDailySurvey.getJSONObject(General.COVID_19_SPECIFIC_QUESTION);
                        setQuestionListData(covid19SpecificQuestion);

                        HashMap<String, List<CovidModel>> childList = new HashMap<>();
                        for (int i = 0; i < expandableListTitle.size(); i++) {
                            childList.put(expandableListTitle.get(i).getTitle(), expandableListTitle.get(i).getQuestion());
                        }

                        covidExpandableListAdapter = new CovidExpandableListAdapter(this, expandableListTitle, childList);
                        mExpandableListView.setAdapter(covidExpandableListAdapter);
                        mExpandableListView.expandGroup(0);// first item of listview
                        mExpandableListView.expandGroup(1);
                        mExpandableListView.expandGroup(2);
                        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                            public boolean onGroupClick(ExpandableListView arg0, View itemView, int itemPosition, long itemId) {
                                mExpandableListView.expandGroup(itemPosition);
                                return true;
                            }
                        });
                    }


                } else {
                    Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ImmunityRespiratotyCovid19SurveyMainActivity", "" + e.getMessage());
            }
        }
    }

    /*Api call for Immunity Respiratoty Covid-19 submit*/
    @SuppressLint("LongLogTag")
    private void submitImmunityRespiratoryCovid19SurveyAPICalled(String ans_ids, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DAILY_SURVEY_SUBMIT);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.ANS_IDS, ans_ids);
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_SENJAM_SURVEY;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("submitOneSurveyQuestionAnswersResponse", response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddJournal = jsonObject.getAsJsonObject(Actions_.DAILY_SURVEY_SUBMIT);
                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(activity, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        popupWindow.dismiss();
                        Preferences.save(General.SHOW_DAILY_SURVEY_FILLED, false);
                        if (Preferences.getBoolean(General.SHOW_DAILY_DOSAGE_FILLED)) {
                            //showDailyDosingComplianceDialog(General.SENJAM_GOAL_ID, "AM"); //Enable if DailyDose popup from sync & Login
                        }
                    } else {
                        Toast.makeText(activity, jsonAddJournal.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(activity, "No Data", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*Api call for One Survey Question Answer submit*/
    @SuppressLint("LongLogTag")
    private void submitneSurveyQuestionAnswerAPICalled(String ans_ids, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.SUBMIT_ONE_TIME_SURVEY);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.ANS_IDS, ans_ids);
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_SENJAM_SURVEY;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("submitOneSurveyQuestionAnswersResponse", response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddJournal = jsonObject.getAsJsonObject(Actions_.SUBMIT_ONE_TIME_SURVEY);
                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(activity, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        popupWindow.dismiss();
                        Preferences.save(General.SHOW_ONE_TIME_SURVEY_FILLED, false);
                        //getImmunityRespiratoryCovid19SurveyListAPICalled(true);
                    } else {
                        Toast.makeText(activity, jsonAddJournal.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this.activity, "No Data", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // this function is for get Question answer List from Api and bind ArrayList
    private void setQuestionListData(JSONObject object) {
        ArrayList<CovidModel> questionModelArrayList = new ArrayList<>();
        CovidTitleModel covidTitleModel = new CovidTitleModel();
        try {
            JSONArray jsonArray = object.getJSONArray(General.QUESTIONS);
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject questionObject = jsonArray.getJSONObject(j);
                CovidModel questionModel = new CovidModel();
                questionModel.setId(questionObject.getString(General.ID));
                questionModel.setQues(questionObject.getString(General.QUES));
                JSONArray optionJsonArray = questionObject.getJSONArray(General.OPTIONS);
                ArrayList<OptionModel> optionArrayList = new ArrayList<>();
                for (int i = 0; i < optionJsonArray.length(); i++) {
                    JSONObject optionObject = optionJsonArray.getJSONObject(i);
                    OptionModel optionModel = new OptionModel();
                    optionModel.setId(optionObject.getString(General.ID));
                    optionModel.setAnswer(optionObject.getString(General.ANSWER));
                    optionArrayList.add(optionModel);
                    //if(i==0){
                    if (optionObject.getString("is_default").equalsIgnoreCase("1")) {
                        questionModel.setAnsId(optionObject.getString(General.ID));
                        questionModel.setSelectedRadioBtnId(Integer.parseInt(optionObject.getString(General.ID)));
                        questionModel.setSelected(true);
                    }
                }
                questionModel.setOption(optionArrayList);
                questionModelArrayList.add(questionModel);
            }

            String title = object.getString(General.TITLE);
            covidTitleModel.setTitle(title);
            covidTitleModel.setDescription(object.getString(General.DESCRIPTION));
            covidTitleModel.setQuestion(questionModelArrayList);
            expandableListTitle.add(covidTitleModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Api call for Daily Dosing Details*/
    @SuppressLint("LongLogTag")
    private void dailyDosingDetailsAPICalled(String ID) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_SELFGOAL_DETAILS_SENJAM);
        requestMap.put(General.ID, ID);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        Log.e("RequestMap", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("DailyDosingDetailsResponse", response);
                if (response != null) {
                    dailyDosingSelfGoal_arrayList = SenjamDoctorNoteList_.parseDailyDosingSelfGoal(response, Actions_.GET_SELFGOAL_DETAILS_SENJAM, this, TAG);
                    if (dailyDosingSelfGoal_arrayList.size() > 0) {
//                        mTxtQuestionName.setText(dailyDosingSelfGoal_arrayList.get(0).getQuestion());
//                        mTxtSelectDate.setText(getDate(dailyDosingSelfGoal_arrayList.get(0).getAdded_date()));
                        mGoalID = dailyDosingSelfGoal_arrayList.get(0).getGoal_id();
                        mMainGoalID = dailyDosingSelfGoal_arrayList.get(0).getMain_goal_id();
                    } else {
                        Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*APi Call for Add Answer for Daily Dosing Compliance*/
    @SuppressLint({"SetTextI18n", "LongLogTag"})
    private void dailyDosingAddAPICalled(String answerID, String goalID, String onDate, String mainGoalID, String AM_PM, Activity activity) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ADD_ANSWER_SENJAM);
        requestMap.put(General.ANSWER, answerID);
        requestMap.put(General.GOAL_ID, goalID);
        requestMap.put(General.ON_DATE, onDate);
        requestMap.put(General.GOAL_AM_PM, AM_PM);
        requestMap.put(General.MAIN_GOAL_ID, mainGoalID);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        Log.e("requestMap", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this.activity, this.activity);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this.activity, this.activity);
                Log.e(TAG, "dailyDosingAddAPICalled " + response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonDailyDosing = jsonObject.getAsJsonObject(Actions_.ADD_ANSWER_SENJAM);
                    if (jsonDailyDosing.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(activity, jsonDailyDosing.get(General.MSG).getAsString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(activity, jsonDailyDosing.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
//                        dialog.dismiss();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
//                Log.e("errorDailyDosingActivity", ""+e.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("Activity", "The activity has finished");
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == SUPERVISOR_REQUEST_CODE) {
            selectedSupervisor = (Invitations_) data.getExtras().getSerializable("invitation");
            superName = selectedSupervisor.getUsername();
            mTxtSelectSupervisor.setText(selectedSupervisor.getUsername());
            getPeerStaffAssignedSupervisorListData(String.valueOf(selectedSupervisor.getId()));
        } else if (resultCode == PEER_STAFF_REQUEST_CODE) {
            invitationsPeerStaffArrayList = (ArrayList<Invitations_>) data.getSerializableExtra("invitations_arrayList");
            String strSelectedStaff = "";
            int nSelectedCount = 0;
            for (int i = 0; i < invitationsPeerStaffArrayList.size(); i++) {
                Invitations_ model = invitationsPeerStaffArrayList.get(i);
                if (model.getSelected()) {
                    if (nSelectedCount == 0) {
                        strSelectedStaff = model.getUsername();
                    } else {
                        strSelectedStaff = strSelectedStaff + ", " + model.getUsername();
                    }
                    nSelectedCount = nSelectedCount + 1;
                }
            }
            peerName = strSelectedStaff;
            mTxtSelectStaff.setText(strSelectedStaff);
        } else {
            //appointmentReportFragment.onActivityResult(requestCode, resultCode,  data);
        }
    }

    @SuppressLint("SetTextI18n")
    private void showAddProgressNoteDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_team_peer_note);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView textViewMsg = (TextView) dialog.findViewById(R.id.textview_msg);
        final Button buttonYes = (Button) dialog.findViewById(R.id.button_yes);
        final Button buttonNo = (Button) dialog.findViewById(R.id.button_no);
        final ImageButton buttonClose = (ImageButton) dialog.findViewById(R.id.button_close);
        textViewMsg.setText(message);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strSelectedStaff = "";
                int nSelectedCount = 0;
                for (int i = 0; i < invitationsPeerStaffArrayList.size(); i++) {
                    Invitations_ model = invitationsPeerStaffArrayList.get(i);
                    if (model.getSelected()) {
                        if (nSelectedCount == 0) {
                            strSelectedStaff = model.getId() + "";
                        } else {
                            strSelectedStaff = strSelectedStaff + "," + model.getId();
                        }
                        nSelectedCount = nSelectedCount + 1;
                    }
                }

                getAssignPeerStaffToPeerSupervisorApiFunction(String.valueOf(selectedSupervisor.getId()), strSelectedStaff);
                dialog.dismiss();
//                getSupervisorListData();
//                getPeerStaffAssignedSupervisorListData(String.valueOf(selectedSupervisor.getId()));
                mTxtSelectSupervisor.setText("Select Peer Supervisor");
                mTxtSelectStaff.setText("Select Peer Staff");
                mTxtAlreadyAddedBy.setText("");
            }
        });

        dialog.show();
    }

    private void getAssignPeerStaffToPeerSupervisorApiFunction(String strSupervisorID, String strPeerID) {
        String action = Actions_.GET_ASSIGN_PEER_STAFF_FROM_POPUP_MHAW;
        HashMap<String, String> requestMap = new HashMap<>();

        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.SUPERVIOSR_ID, strSupervisorID);
        requestMap.put(General.PEER_STAFF, strPeerID);
        Log.e("requestMap", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        Log.e("Url", url);
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e(TAG, "getAssignPeerStaffToPeerSupervisorApiFunction" + response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddProgressNote;

                    if (action.equals(Actions_.GET_ASSIGN_PEER_STAFF_FROM_POPUP_MHAW)) {
                        jsonAddProgressNote = jsonObject.getAsJsonObject(Actions_.GET_ASSIGN_PEER_STAFF_FROM_POPUP_MHAW);

                        if (jsonAddProgressNote.get(General.STATUS).getAsInt() == 1) {
                            Toast.makeText(this, jsonAddProgressNote.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
//                            popupWindow.dismiss();
                            getPeerStaffListData(false);
                        } else {
                            Toast.makeText(this, jsonAddProgressNote.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("AssignError", e.getMessage());
            }
        }
    }

    public void changeDrawerIcon(boolean showHide) {
        if (showHide) {
            toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        } else {
            toolbar.setNavigationIcon(R.drawable.vi_drawer_hamburger_icon);
        }
    }

    public void showHideBellIcon(boolean showHide) {
        if (showHide) {
            mainToolBarBellLayout.setVisibility(View.INVISIBLE);
        } else {
            mainToolBarBellLayout.setVisibility(View.VISIBLE);
        }
    }

    public void showHideBellIcon2(boolean showHide) {
        if (showHide) {
            mainToolBarBellLayout.setVisibility(View.GONE);
            main_toolbar_bell.setVisibility(View.GONE);
        } else {
            mainToolBarBellLayout.setVisibility(View.VISIBLE);
            main_toolbar_bell.setVisibility(View.VISIBLE);
        }
    }

    public void showChatIcon(boolean showHide) {
        if (showHide) {
            chat_icon.setVisibility(View.VISIBLE);
        } else {
            chat_icon.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NewApi")
    public void handleIntentForPushNotification(final Context context, Intent mainIntent) {

    }

    //For cometchat push notification onclick events
    public void handleIntent(Intent mainIntent) {
        Log.i(TAG, "handleIntent: main activity");
        Log.i(TAG, "handleIntent: " + mainIntent.hasExtra("type"));
        if (mainIntent.getExtras() != null) {
            if (mainIntent.hasExtra("team_logs_id")) {
                String team_logs_id = mainIntent.getStringExtra("team_logs_id");
                String receiver = mainIntent.getStringExtra("receiver");
                String sender = mainIntent.getStringExtra("sender");
                String receiverType = mainIntent.getStringExtra("receiverType");
                String username = mainIntent.getStringExtra("username");

                /*In some notification we dont receive type so we make it empty string*/
                String type;
                if (mainIntent.hasExtra("type")) {
                    type = mainIntent.getStringExtra("type");
                } else {
                    type = "";
                }
                Log.i(TAG, "handleIntent: team_logs_id" + team_logs_id);
                Log.i(TAG, "handleIntent: receiver" + receiver);
                Log.i(TAG, "handleIntent: sender" + sender);
                Log.i(TAG, "handleIntent: receiverType" + receiverType);
                Log.i(TAG, "handleIntent: username" + username);
                Log.i(TAG, "handleIntent: type" + type);

                if (team_logs_id != null && !type.equals("extension_whiteboard")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("team_logs_id", team_logs_id);
                    bundle.putString("receiver", receiver);
                    bundle.putString("sender", sender);
                    bundle.putString("receiverType", receiverType);
                    bundle.putString("username", username);
                    bundle.putString("type", type);

                    /*creating preferences for intent to open chat screen or not*/
                    SharedPreferences preferenOpenActivity = this.getSharedPreferences("sp_check_push_intent", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferenOpenActivity.edit();
                    if (type.equals("groupMember")) {
                        editor.putBoolean("highlightList", true);
                    }
                    editor.putBoolean("checkIntent", true);
                    editor.apply();
                    Log.i(TAG, "handleIntent: checkIntent" + preferenOpenActivity.getBoolean("checkIntent", false));
                    Fragment fragment = GetFragments.get(9, bundle);
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                    ft.replace(R.id.app_bar_main_container, fragment, TAG);
                    ft.commit();
                }
            } else {
                Log.i(TAG, "handleIntent: else");
                if (mainIntent.hasExtra("sender")) {
                    /*Here we are getting intent  for call redirection*/
                    Log.i(TAG, "handleIntent: call block");
                    String lastActiveAt = mainIntent.getStringExtra("lastActiveAt");
                    String uid = mainIntent.getStringExtra("uid");
                    String role = mainIntent.getStringExtra("role");
                    String name = mainIntent.getStringExtra("name");
                    String avatar = mainIntent.getStringExtra("avatar");
                    String status = mainIntent.getStringExtra("status");
                    String callType = mainIntent.getStringExtra("callType");
                    String sessionid = mainIntent.getStringExtra("sessionid");

                    User user = new User();
                    user.setLastActiveAt(Long.parseLong(lastActiveAt));
                    user.setUid(uid);
                    user.setRole(role);
                    user.setName(name);
                    user.setAvatar(avatar);
                    user.setStatus(status);
                    Utils.startCallIntent(MainActivity.this, user, callType, false, sessionid);
                } else /*if (mainIntent.hasExtra("type"))*/ {
                    Log.i(TAG, "handleIntent: whiteboard block");
                    Bundle bundle = new Bundle();
                    Fragment fragment = GetFragments.get(82, bundle);
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                    ft.replace(R.id.app_bar_main_container, fragment, TAG);
                    ft.commit();
                }
            }
        }
    }


    // This function checks the intent data and redirects the user to particular tab


    private void openGroup(Context context, JSONObject message, String notificationMessage) throws JSONException {
        long chatroomId;
        if (message.has("cid")) {
            chatroomId = Long.parseLong(message.getString("cid"));
        }
        Pattern pattern = Pattern.compile("@(.*?):");
        Matcher matcher = pattern.matcher(notificationMessage);
        matcher.find();
        notificationMessage = matcher.group(1);

    }

    //
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(General.TYPE)) {
            String type = intent.getStringExtra(General.TYPE);
            String consumer_id = "";
            String mentor_id = "";
            String assessment_record_id = "";
            String assessment_form_name = "";
            String team_annouoncement_id = "";
            String tasklist_id = "";
            String event_id = "";
            String sos_id = "";
            String selfcare_id = "";
            String selfgoal_id = "";
            String AM_PM = Preferences.get(General.GOAL_AM_PM);
            String set_unset_id = "";
            String note_id = "";
            String mood_id = "";
            String journal_id = "";
            String bhs_id = "", bhs_ref_id = "";
            String message_id = "";
            String leave_management_id = "";
            String appointment_id = "";
            String appointmentType = "";
            String platform_youth_message = "";
            String appointment_report_id = "";
            String senjam_patient_id = "";
            if (type.contains("_")) {
                String[] splitStr = type.trim().split("_");
                type = splitStr[0];
                if (Integer.parseInt(Preferences.get(General.GROUP_ID)) != 0) {
                    if (Integer.parseInt(type) == 15) {
                        team_annouoncement_id = splitStr[1];
                    } else if (Integer.parseInt(type) == 16) {
                        tasklist_id = splitStr[1];
                    } else if (Integer.parseInt(type) == 18) {
                        event_id = splitStr[1];
                    }
                } else {
                    if (Integer.parseInt(type) == 2) {                                              //sos
                        sos_id = splitStr[1];
                    } else if (Integer.parseInt(type) == 25 || Integer.parseInt(type) == 28) {      //selfcare global and selfcare reviewer
                        selfcare_id = splitStr[1];
                    } else if (Integer.parseInt(type) == 24) { //self goal
                        selfgoal_id = splitStr[1];
                        if (splitStr.length > 2) {
                            set_unset_id = splitStr[3];
                        } else {
                            set_unset_id = "1";
                        }
                        if (AM_PM == null) {
                            AM_PM = "";
                        }
                    } else if (Integer.parseInt(type) == 16) { //tasklist
                        tasklist_id = splitStr[1];
                    } else if (Integer.parseInt(type) == 18) { //event
                        event_id = splitStr[1];
                    } else if (Integer.parseInt(type) == 34 || Integer.parseInt(type) == 51) {
                        consumer_id = splitStr[1];
                        mood_id = splitStr[2];
                    } else if (Integer.parseInt(type) == 62 || Integer.parseInt(type) == 64) { //notes
                        consumer_id = splitStr[1];
                        mentor_id = splitStr[2];
                        note_id = splitStr[3];
                    } else if (Integer.parseInt(type) == 65) { //journal
                        journal_id = splitStr[1];
                    } else if (Integer.parseInt(type) == 74) { //behavioral tracking(WRH)
                        bhs_id = splitStr[1];
                        bhs_ref_id = splitStr[2];
                    } else if (Integer.parseInt(type) == 3) { //message
                        message_id = splitStr[1];
                    } else if (Integer.parseInt(type) == 68) { //leave management
                        leave_management_id = splitStr[1];
                    } else if (Integer.parseInt(type) == 80) { //appointment
                        appointment_id = splitStr[1];
                        appointmentType = splitStr[2];
                    } else if (Integer.parseInt(type) == 82) { //platform_youth_message
                        platform_youth_message = splitStr[1];
                    } else if (Integer.parseInt(type) == 83) { //senjam_patient_id
                        senjam_patient_id = splitStr[1];
                    } else {
                        assessment_record_id = splitStr[1];
                        if (intent.hasExtra(General.TYPE)) {
                            String message = intent.getStringExtra(General.MESSAGE);
                            assessment_form_name = message;
                        }
                    }
                }
            }
            String intentTimestamp = intent.getStringExtra(General.TIMESTAMP);
            Preferences.save(General.IS_PUSH_NOTIFICATION, false);
            for (Map.Entry<String, List<String>> entry : Config.mapOfPosts.entrySet()) {
                String timestamp = entry.getKey();
                String group_id = entry.getValue().get(3);
                if (intentTimestamp.equalsIgnoreCase(timestamp)) {
                    Preferences.save(General.GROUP_ID, group_id);
                }
            }
            Config.mapOfPosts = new HashMap<>();

            //all the home main functions will redirect from here
            if (type.trim().length() > 0) {
                if (Integer.parseInt(type) == 52) {
                } else {
                    if (Integer.parseInt(Preferences.get(General.GROUP_ID)) != 0) {
                        ArrayList<Teams_> teamsArrayList = PerformGetTeamsTask.get(Actions_.TEAM_DATA, this, TAG, true, this);
                        Log.i(TAG, "onNewIntent: teamArrayList" + teamsArrayList);
                        if (teamsArrayList.size() > 0) {
                            if (teamsArrayList.get(0).getStatus() == 1) {
                                Preferences.save(General.BANNER_IMG, teamsArrayList.get(0).getBanner());
                                Preferences.save(General.TEAM_ID, teamsArrayList.get(0).getId());
                                Preferences.save(General.TEAM_NAME, teamsArrayList.get(0).getName());
                                Preferences.save(General.HOME_ICON_NUMBER, "0");

                                if (Integer.parseInt(type) == 15) {                                            // (15 code) Team Announcement fragment redirect
                                    Log.i(TAG, "onNewIntent:15 Team Announcement fragment redirect");
                                    Preferences.save(General.TEAM_ANNOUNCEMENT_ID, team_annouoncement_id);
                                    replaceFragment(15, intent.getStringExtra(General.TYPE), new Bundle());

                                } else if (Integer.parseInt(type) == 16) {                                            //(16 code ) TeamTaskListFragment
                                    Preferences.save(General.TASKLIST_ID, tasklist_id);
                                    Fragment fragment = new TeamTaskListFragment();
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    FragmentTransaction ft = fragmentManager.beginTransaction();
                                    Fragment oldFragment = fragmentManager.findFragmentByTag(Actions_.GET_PROFILE_DATA);
                                    if (oldFragment != null) {
                                        ft.remove(oldFragment);
                                    }
                                    Log.i(TAG, "onNewIntent: 16 TeamTaskListFragment fragment redirect");
                                    oldFragment = fragmentManager.findFragmentByTag(Actions_.GET_PROFILE_DATA);
                                    if (oldFragment != null) {
                                        ft.remove(oldFragment);
                                    }
                                    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                                    ft.replace(R.id.app_bar_main_container, fragment, Actions_.GET_PROFILE_DATA);
                                    ft.commit();
                                } else if (Integer.parseInt(type) == 18) {                                            // (18 code)EventsDetailsActivity redirection
                                    ArrayList<Event_> eventArrayList = Teams.getEventDetails(TAG, String.valueOf(event_id), this);
                                    if (eventArrayList.size() > 0) {
                                        if (eventArrayList.get(0).getStatus() == 1) {
                                            Log.i(TAG, "onNewIntent:18 EventsDetailsActivity redirect");
                                            Intent addIntent = new Intent(getApplicationContext(), EventDetailsActivity.class);
                                            addIntent.putExtra(Actions_.GET_EVENTS, eventArrayList.get(0));
                                            startActivity(addIntent);
                                        }
                                    }
                                } else {
                                    Intent detailsIntent = new Intent(getApplicationContext(), TeamDetailsActivity.class); // TeamDetailsActivity redirection
                                    detailsIntent.putExtra(General.TEAM, teamsArrayList.get(0));
                                    Log.d(TAG, "onNewIntent:18 TeamDetailsActivity redirection");
                                    startActivity(detailsIntent);
                                    overridePendingTransition(0, 0);
                                }
                            }
                        }
                    } else if (Integer.parseInt(Preferences.get(General.GROUP_ID)) == 0 && (type.equalsIgnoreCase("18"))) {//my event
                        Preferences.save(General.EVENT_ID, event_id);
                        Preferences.save(General.HOME_ICON_NUMBER, "0");
                        Log.d(TAG, "onNewIntent:18 my event redirection");
                        replaceFragment(27, intent.getStringExtra(General.TYPE), new Bundle());                      //Daily planner

                    } else if (Integer.parseInt(Preferences.get(General.GROUP_ID)) == 0 && (type.equalsIgnoreCase("16") || type.equalsIgnoreCase("35"))) {//my tasklist
                        Preferences.save(General.TASKLIST_ID, tasklist_id);
                        Preferences.save(General.HOME_ICON_NUMBER, "0");
                        Log.d(TAG, "onNewIntent:16 35 Daily planner redirection");
                        replaceFragment(27, intent.getStringExtra(General.TYPE), new Bundle());

                    } else if (Integer.parseInt(type) == 62 || Integer.parseInt(type) == 64) {                         //Notes
                        Preferences.save(General.HOME_ICON_NUMBER, "0");
                        Preferences.save(General.CONSUMER_ID, consumer_id);
                        Log.d(TAG, "onNewIntent: 62 65 Notes redirection");
                        if (!mentor_id.equalsIgnoreCase("0")) {
                            Preferences.save(General.MENTOR_ID, mentor_id);
                        }

                        ArrayList<CaseloadPeerNote_> caseloadPeerNoteArrayList = Teams.getPeerCaseloadNoteDetails(TAG, String.valueOf(note_id), this);
                        if (caseloadPeerNoteArrayList.size() > 0) {
                            if (caseloadPeerNoteArrayList.get(0).getStatus() == 1) {
                                Log.d(TAG, "onNewIntent:62 65 PeerNoteDetailsActivity redirection");
                                Preferences.save(General.CONSUMER_ID, caseloadPeerNoteArrayList.get(0).getConsumer_id());
                                Intent detailsIntent = new Intent(getApplicationContext(), PeerNoteDetailsActivity.class);      //PeerNoteDetailsActivity
                                detailsIntent.putExtra(Actions_.NOTES, caseloadPeerNoteArrayList);
                                startActivity(detailsIntent);
                                overridePendingTransition(0, 0);
                            }
                        }
                    } else if (Integer.parseInt(type) == 65) {                                       //Journaling  => JournalDetailsActivity
                        Preferences.save(General.HOME_ICON_NUMBER, "0");
                        Log.i(TAG, "onNewIntent: 65 JournalDetailsActivity redirection");
                        if (CheckRole.isWerHope(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                            ArrayList<Journal_> journalArrayList = Teams.getJournalDetails(TAG, journal_id, MainActivity.this);
                            if (journalArrayList.size() > 0) {
                                if (journalArrayList.get(0).getStatus() == 1) {
                                    Intent journalDetails = new Intent(MainActivity.this, JournalDetailsActivity.class);
                                    journalDetails.putExtra(General.JOURNAL, journalArrayList.get(0));
                                    journalDetails.putExtra("details", true);
                                    journalDetails.putExtra("not_show_map", true);
                                    startActivity(journalDetails);
                                }
                            }
                        }
                    } else if (Integer.parseInt(type) == 34 || Integer.parseInt(type) == 51) {
                        Preferences.save(General.HOME_ICON_NUMBER, "0");
                        Preferences.save(General.MOOD_ID, mood_id);
                        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                            replaceFragment(Integer.parseInt(type), intent.getStringExtra(General.TYPE), new Bundle());
                        } else if (CheckRole.isCoordinator(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                            Preferences.save(General.CONSUMER_ID, consumer_id);
                            Log.i(TAG, "onNewIntent:34 51CCMoodActivity redirection");
                            Intent detailsIntent = new Intent(getApplicationContext(), CCMoodActivity.class);             // CCMoodActivity
                            startActivity(detailsIntent);
                            overridePendingTransition(0, 0);
                        } else {
                            Log.i(TAG, "onNewIntent: 34 51 MainActivity redirection");
                            Intent detailsIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(detailsIntent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    } else if (Integer.parseInt(type) == 7) {
                        Intent detailsIntent = new Intent(getApplicationContext(), FormShowActivity.class);// FormShowActivity
                        Log.i(TAG, "onNewIntent: 7 FormShowActivity redirection");
                        detailsIntent.putExtra(General.ASSESSMENT_RECORD_ID, assessment_record_id);
                        detailsIntent.putExtra("assessment_form_name", assessment_form_name);
                        startActivity(detailsIntent);
                        overridePendingTransition(0, 0);
                    } else if (Integer.parseInt(type) == 24) {
                        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {              // showing dialog showDailyDosingComplianceDialog
                            Log.i(TAG, "onNewIntent:24 flavor : senjam = showing dialog showDailyDosingComplianceDialog");
                            String message = intent.getStringExtra(General.MESSAGE);
                            String timeStamp = intent.getStringExtra(General.TIMESTAMP);
                            String date = dateCaps(timeStamp);
                            showDailyDosingComplianceDialog(date, selfgoal_id, AM_PM, message, activity);

                        } else {
                            if (set_unset_id.equals("1")) {                                         // SelfGoalDetailsActivity
                                Log.i(TAG, "onNewIntent: 24 SelfGoalDetailsActivity redirection");
                                ArrayList<Goal_> goalArrayList = Teams.getGoalDetails(TAG, String.valueOf(selfgoal_id), this);
                                if (goalArrayList.size() > 0) {
                                    if (goalArrayList.get(0).getStatus() == 1) {
                                        Intent addIntent = new Intent(getApplicationContext(), SelfGoalDetailsActivity.class);
                                        addIntent.putExtra(Actions_.MY_GOAL, goalArrayList.get(0));
                                        startActivity(addIntent);
                                    }
                                }
                            }
                        }

                    } else if (Integer.parseInt(type) == 18) {                                      //My Events => EventDetailsActivity
                        Log.i(TAG, "onNewIntent:My Events => 18 EventDetailsActivity redirection");
                        ArrayList<Event_> eventArrayList = Teams.getEventDetails(TAG, String.valueOf(event_id), this);
                        if (eventArrayList.size() > 0) {
                            if (eventArrayList.get(0).getStatus() == 1) {
                                Intent addIntent = new Intent(getApplicationContext(), EventDetailsActivity.class);
                                addIntent.putExtra(Actions_.GET_EVENTS, eventArrayList.get(0));
                                startActivity(addIntent);
                            }
                        }
                    } else if (Integer.parseInt(type) == 82) {                                      //platform_youth_message
                        Log.i(TAG, "onNewIntent:My Events => 82 platform_youth_message redirection");
                        HashMap<String, String> requestMap = new HashMap<>();
                        requestMap.put(General.ACTION, Actions_.CLIENT_OUTREACH);
                        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
                        requestMap.put(General.MSG_ID, platform_youth_message);

                        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_DETAILS_CALL;
                        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
                        if (requestBody != null) {
                            try {
                                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                                if (response != null) {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray clientOutReach = jsonObject.getJSONArray("client_outreach");

                                    for (int i = 0; i < clientOutReach.length(); i++) {
                                        JSONObject object = clientOutReach.getJSONObject(i);
                                        if (object.getString("status").equals("1")) {
                                            String subject = object.getString("subject");
                                            String message = object.getString("message");

                                            designClientOutDialog(subject, message);
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    } else if (Integer.parseInt(type) == 80) {                                      //appointment reminder dialog
                        Log.i(TAG, "onNewIntent:appointment reminder dialog 80 redirection");

                        if (appointmentType.equals("24")) {
                            Log.i(TAG, "onNewIntent:appointment reminder dialog 24 redirection");
                            DailogHelper dailogHelper = new DailogHelper();
                            dailogHelper.appointmentReminderDialog(appointment_id, this);
                        }
                    } else if (Integer.parseInt(type) == 74) {
                        Log.i(TAG, "onNewIntent:74 behavioral tracking(WRH) redirection");
                        //behavioral tracking(WRH)
                        Preferences.save(General.HOME_ICON_NUMBER, "0");
                       /* ArrayList<BehaviouralHealth> behaviouralHealthArrayList = Teams.getBHSDetails(TAG, bhs_id, MainActivity.this);
                        if (behaviouralHealthArrayList.size() > 0) {*/
                        Preferences.save(General.NOTIFICATION_BHS_ID, bhs_ref_id);
                        Preferences.save(General.NOTIFICATION_BHS_REF_ID, bhs_id);

                        Fragment fragment = new BeahivouralSurveyFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
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

                        // }

                    } else if (Integer.parseInt(type) == 3) {                                       // Message => MailDetailsActivity
                        Preferences.save(General.HOME_ICON_NUMBER, "0");
                        Log.i(TAG, "onNewIntent: MailDetailsActivity redirection");
                        ArrayList<Postcard_> mailArrayList = Teams.getMessageDetails(TAG, message_id, MainActivity.this);
                        if (mailArrayList.size() > 0) {
                            for (int i = 0; mailArrayList.size() > 0; i++) {
                                if (mailArrayList.get(i).getStatus() != 0) {
                                    if (mailArrayList.get(i).getMessageId() == Integer.parseInt(message_id)) {
                                        Intent messageDetails = new Intent(MainActivity.this, MailDetailsActivity.class);
                                        messageDetails.putExtra(General.POSTCARD, mailArrayList.get(i));
                                        messageDetails.putExtra(General.ACTION, "inbox");
                                        messageDetails.putExtra("main_screen", true);
                                        startActivity(messageDetails);
                                    }
                                }
                            }
                        }
                    } else if ((Integer.parseInt(type) == 83)) {                                    // Redirect to CaseLoad Fragment
                        Log.i(TAG, "onNewIntent:Redirect to CaseLoad Fragment");
                        CaseloadFragment caseloadFragment = new CaseloadFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("senjam_patient_id", senjam_patient_id);
                        caseloadFragment.setArguments(bundle);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.app_bar_main_container, caseloadFragment);
                        ft.commit();

                    } else if ((Integer.parseInt(type) == 84)) {                                    // Covid 19 - Daily Survery popup
                        Log.i(TAG, "onNewIntent : Daily Survery popup");
                        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                            String date = intent.getStringExtra(General.TIMESTAMP);
                            try {
                                CurrentDate = sdfDate.parse(mCurrentDate);
                                StartDate = sdfDate.parse(date);

                                Log.e("CurrentDate", "" + CurrentDate);
                                Log.e("StartDate", "" + StartDate);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (StartDate.before(CurrentDate)) { // TODO : if Date is Pervious from ToDay Date then Dialog not open
//                                Toast.makeText(activity, "You can not submit Daily Survey.", Toast.LENGTH_LONG).show();
                                AlertDialogForMessage("You can not submit Daily Survey.");
                            } else {
                                if (Preferences.getBoolean(General.SHOW_DAILY_SURVEY_FILLED)) {  // TODO : if show_daily_survey_filled is true then open dialog otherwise give message in Toast.
                                    getImmunityRespiratoryCovid19SurveyListAPICalled(true, activity);
                                } else {
//                                    Toast.makeText(activity, "You already submitted Daily Survey.", Toast.LENGTH_LONG).show();
                                    AlertDialogForMessage("You already submitted Daily Survey.");
                                }
                            }

                        }
                    } else {
                        if (Integer.parseInt(type) == 2) {

                            Preferences.save(General.SOS_ID, sos_id);
                        } else if (Integer.parseInt(type) == 25 || Integer.parseInt(type) == 28) {
                            Preferences.save(General.SELFCARE_ID, selfcare_id);
                        }
                        if (Integer.parseInt(type) != 3) {
                            Preferences.save(General.HOME_ICON_NUMBER, "0");
                            replaceFragment(Integer.parseInt(type), intent.getStringExtra(General.TYPE), new Bundle());
                        }
                    }
                }
            }
        }

        notificationUtils = new NotificationUtils(this);
        Bundle extras = intent.getExtras();
        notificationUtils.removenotification(extras.getInt("notification_id"));
    }

    public void AlertDialogForMessage(String message) {
        dialog = new Dialog(this.activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.toast_dialog);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        final TextView mTxtToastTitle = (TextView) dialog.findViewById(R.id.txt_toast_title);
        final TextView mTxtOk = (TextView) dialog.findViewById(R.id.txt_ok);
        final LinearLayout mLinearLayoutTxtOk = (LinearLayout) dialog.findViewById(R.id.linear_text_view_ok);

        mTxtToastTitle.setText(message);

        mLinearLayoutTxtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void designClientOutDialog(String subject, String message) {
        View view = getLayoutInflater().inflate(R.layout.dialog_outreach_message, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;

        TextView subjectTxt = view.findViewById(R.id.subject_txt);
        TextView messageTxt = view.findViewById(R.id.message_txt);
        ImageView closeIcon = view.findViewById(R.id.close_icon);

        subjectTxt.setText(subject);
        messageTxt.setText(message);

        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SyncUserData.sync(getApplicationContext(), TAG, this);
        setHeader();
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        startService(intent);
        registerReceiver(broadcastReceiver, new IntentFilter(Broadcast.COUNTER_BROADCAST));
        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

        Preferences.save(General.IS_FROM_DAILYPLANNER, false);

        //if only 1 team available than directly move to team details and manage the onBackPressed call from TeamDetailsActivity
        if (Preferences.getBoolean(General.ISMOVETOTEAMDETAILS) || Preferences.getBoolean(General.ISMOVETOHOME)) {
            Preferences.save(General.ISMOVETOTEAMDETAILS, false);
            Preferences.save(General.ISMOVETOHOME, false);
            replaceFragment(50, drawerMenuList.get(1).getMenu(), null);
        }
        fetchNotification();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        unregisterReceiver(broadcastReceiver);
        stopService(intent);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (popupWindow != null && popupWindow.isShowing()) {
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void getChoice(Teams_ teams_, boolean isSelected, int menu_id) {
        /*if (menu_id == 17) {
            FileSharingListFragment fileSharingListFragment = (FileSharingListFragment) getSupportFragmentManager().findFragmentById(R.id.app_bar_main_container);
            fileSharingListFragment.GetChoice(teams_, isSelected);
        }*/
        /*if (menu_id == 5) {
            CrisisFragment crisisFragment = (CrisisFragment) getSupportFragmentManager().findFragmentById(R.id.app_bar_main_container);
            crisisFragment.GetChoice(teams_, isSelected);
        }*/
    }

    // show notification if push is received
    private void showNotificationMessage(String title, String message, String type, String
            timestamp) {
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        resultIntent.putExtra(General.MESSAGE, message);
        resultIntent.putExtra(General.TIMESTAMP, timestamp);
        resultIntent.putExtra(General.TITLE, title);
        resultIntent.putExtra(General.TYPE, type);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timestamp, type, resultIntent);
    }

    private void displayFirebaseRegId() {
        String regId = Preferences.get("regId");
        if (regId == null || regId.equals("")) {
            /*here getting firebase token and storing to the preferences
             * so that we can access it..
             * updated by rahulmsk */
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    String firebaseToken = task.getResult();
                    Log.e(TAG, "firebase token: " + firebaseToken);
                    Preferences.save("regId_save", false);
                    Preferences.initialize(getApplicationContext());
                    Preferences.save("regId", firebaseToken);

                    Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
                    registrationComplete.putExtra("token", firebaseToken);
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(registrationComplete);
                }
            });


            //String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        }
        if (Preferences.contains("regId_save") && !Preferences.getBoolean("regId_save")) {
            SaveFirabase.save(getApplicationContext(), regId, TAG, this);
        }
        Log.e(TAG, "Firebase Id: " + regId);
    }

    // Update drawer list when new counters received
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                //updateDrawerList();
                fetchNotification();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //update drawer list with new content
    private void updateDrawerList() {
        for (int i = 0; i < drawerMenuList.size(); i++) {
            drawerMenuList.get(i).setCounter(GetCounters.drawer(drawerMenuList.get(i).getId()));
            //we dont have sub menus so we have commented this code
            /*if (drawerMenuList.get(i).getSubMenu().size() > 0) {
                for (int j = 0; j < drawerMenuList.get(i).getSubMenu().size(); j++) {
                    drawerMenuList.get(i).getSubMenu().get(j).setCounter(GetCounters.drawer(drawerMenuList.get(i).getSubMenu().get(j).getId()));
                }
            }*/
        }
        drawerListAdapter.notifyDataSetChanged();
    }

    // set user profile in drawer head
    private void setHeader() {
        nameText.setText(ChangeCase.toTitleCase(Preferences.get(General.NAME)));
        roleText.setText(ChangeCase.toTitleCase(Preferences.get(General.ROLE)));
        //circle.setPercentage(30);

        String profileImage = Preferences.get(General.PROFILE_IMAGE);
        Glide.with(getApplicationContext())
                .load(profileImage)
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_user_male)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(getApplicationContext())))
                .into(profilePhoto);
    }

    // set all menus with counters in drawer
    /**
     * This is drawer menu list here we are showing drower menus and also setting onclick on drawer menu
     * now this code is working and i am commenting this code because when we are scrolling items some items are not working properly
     * for this i am creating new method and in that i will use recycler view instead of expandable view
     * commented by rahul maske
     */
    private void setDrawerMenuList() {
        if (drawerMenuList == null) {
            drawerMenuList = new ArrayList<>();
        } else if (drawerMenuList.size() > 0) {
            drawerMenuList.clear();
        }
        drawerMenuList = Login_.drawerMenuParser();

        //Adding Logout menu in Navigation Drawer menu list
        HomeMenu_ homeMenu = new HomeMenu_();
        List<HomeMenu_> subMenu = new ArrayList<>();
        DrawerMenu_ drawerMenu = new DrawerMenu_();
        drawerMenu.setId(0);
        drawerMenu.setMenu(getResources().getString(R.string.logout));
        drawerMenu.setSubMenu(subMenu);
        drawerMenuList.add(drawerMenu);

        HashMap<String, List<HomeMenu_>> childList = new HashMap<>();
        for (int i = 0; i < drawerMenuList.size(); i++) {
            childList.put(drawerMenuList.get(i).getMenu(), drawerMenuList.get(i).getSubMenu());
        }
        for (DrawerMenu_ homeMenu_ : drawerMenuList) {
            Log.i(TAG, "setDrawerMenuList: " + homeMenu_.getMenu());
        }
        drawerListAdapter = new DrawerListAdapter(getApplicationContext(), drawerMenuList, childList);
        expandableDrawerListView.setAdapter(drawerListAdapter);

        expandableDrawerListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition, long id) {
                if (drawerMenuList.get(groupPosition).getSubMenu().size() <= 0) {
                    view.setSelected(true);
                    drawerLayout.closeDrawer(Gravity.START);
                    parent.setItemChecked(groupPosition, true);
                    Preferences.save(General.HOME_ICON_NUMBER, "0");
                    setTitle(drawerMenuList.get(groupPosition).getMenu());
                    setToolbarBackgroundColor();
                    replaceFragment(drawerMenuList.get(groupPosition).getId(), drawerMenuList.get(groupPosition).getMenu(), null);
                } else {
                    if (expandableDrawerListView.isGroupExpanded(groupPosition)) {
                        expandableDrawerListView.collapseGroup(groupPosition);
                        drawerMenuList.get(groupPosition).setSelected(false);
                        parent.setItemChecked(groupPosition, false);
                    } else {
                        expandableDrawerListView.expandGroup(groupPosition);
                        drawerMenuList.get(groupPosition).setSelected(true);
                        parent.setItemChecked(groupPosition, true);
                    }
                }
                return true;
            }
        });

        expandableDrawerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                view.setSelected(true);
                drawerLayout.closeDrawer(Gravity.START);
                int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                parent.setItemChecked(index, true);
                Preferences.save(General.HOME_ICON_NUMBER, "0");
                setTitle(drawerMenuList.get(groupPosition).getSubMenu().get(childPosition).getMenu());
                setToolbarBackgroundColor();
                replaceFragment(drawerMenuList.get(groupPosition).getSubMenu().get(childPosition).getId(), drawerMenuList.get(groupPosition).getSubMenu()
                        .get(childPosition).getMenu(), null);

                return true;
            }
        });
    }


    private void setDrawerMenuListByRecyclerview() {
        if (drawerMenuList == null) {
            drawerMenuList = new ArrayList<>();
        } else if (drawerMenuList.size() > 0) {
            drawerMenuList.clear();
        }

        drawerMenuList = Login_.drawerMenuParser();
        DrawerMenu_ drawerMenu = new DrawerMenu_();
        drawerMenu.setId(0);
        drawerMenu.setMenu(getResources().getString(R.string.logout));
        drawerMenuList.add(drawerMenu);
        drawerMenuAdapter = new DrawerMenuAdapter(this, drawerMenuList);
        rv_drawerMenus.setAdapter(drawerMenuAdapter);
    }

    public void onDrawerMenuItemClickListner(DrawerMenu_ drawerMenu_) {
        Log.i(TAG, "onDrawerMenuItemClickListner: id "+drawerMenu_.getId() +" name "+drawerMenu_.getMenu());
        drawerLayout.closeDrawer(Gravity.LEFT);
        Preferences.save(General.HOME_ICON_NUMBER, "0");
        setTitle(drawerMenu_.getMenu());
        setToolbarBackgroundColor();
        replaceFragment(drawerMenu_.getId(), drawerMenu_.getMenu(), null);
    }


    // open emergency contact dialog fragment based on user platform role
    @SuppressLint("CommitTransaction")
    private void openEmergencyDialog() {
        //hideRevealView();
        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            DialogFragment dialogFrag = new EmergencyContactDialogFragment();
            dialogFrag.show(this.getFragmentManager().beginTransaction(), Actions_.GET_OWNER_NUMBER);
        } else {
            DialogFragment dialogFrag = new ParentEmergencyDialogFragment();
            dialogFrag.show(this.getFragmentManager().beginTransaction(), Actions_.GET_OWNER_NUMBER);
        }
    }

    private void replaceFragment(int id, String name, Bundle bundle) {
        Logger.error(TAG, "menu_id: " + id, getApplicationContext());
        if (id == 0) {
            performLogoutTask.logout(this);
            return;
        }

        if (id == 23) {
            openEmergencyDialog();
            return;
        }

        if (id == 65) {
            if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                Intent noteList = new Intent(MainActivity.this, SenjamSowsActivity.class);
                startActivity(noteList);
            }
        }

        //Daily Planner- show addbutton in toolbar to add event/task/goal
        Preferences.save(General.MODULE_ID, id);
        if (id == 27 || id == 54) {
            notificationImageView.setVisibility(View.VISIBLE);
            if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                addButton.setVisibility(View.GONE);
                logBookIcon.setVisibility(View.GONE);
            } else {
                //Dont show Add planner button for senjam
                addButton.setVisibility(View.VISIBLE);
                logBookIcon.setVisibility(View.GONE);
            }

        } else {
            notificationImageView.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.GONE);
            logBookIcon.setVisibility(View.GONE);
        }

        setMoodToolbar(id);

        //Kailash added to move to team details page if user has only 1 team(no intermediate team listing screen required)
        ArrayList<Teams_> teamsArrayList = new ArrayList<Teams_>();
        Log.i(TAG, "replaceFragment: only one team");
        boolean isMoveToTeamDetails = false;
        if (id == 8 || id == 47 || id == 60) {
            teamsArrayList = PerformGetTeamsTask.getNormalTeams(Actions_.ALL_TEAMS, this, TAG, false, this);
            if (teamsArrayList.size() == 1) {
                isMoveToTeamDetails = true;
            }
        }

        boolean isLandingQuestionFilled = Preferences.getBoolean(General.IS_LANDING_QUESTION_FILLED); //false
        boolean isFromSyncLandingQuestion = Preferences.getBoolean(General.IS_FORM_SYNC_LANDING_QUESTION);//true

        if (!CheckRole.isYouthOne(Integer.parseInt(Preferences.get(General.ROLE_ID)))) { //do not move to landing question for roles other than Youth
            isLandingQuestionFilled = true;
        }

        if (!isLandingQuestionFilled && !isFromSyncLandingQuestion) {
            Intent addIntakeFormIntent = new Intent(getApplicationContext(), LandingQuestionFormActivity.class);
            startActivity(addIntakeFormIntent);
            overridePendingTransition(0, 0);


        } else {
            Fragment fragment = GetFragments.get(id, bundle);
            if (id == 2) {                                                                          //for SOS search text
                bundle = new Bundle();
                bundle.putString(General.SEARCH_TEXT, "");
                fragment.setArguments(bundle);
            } else if (id == 68) {
                leaveListingFragment = (LeaveListingFragment) fragment;
            }
            // Appointment Report - Show FilterButton for Filter Data
            if (id == 81) {
                addFilter.setVisibility(View.VISIBLE);
                mainToolBarBellLayout.setVisibility(View.GONE);
                appointmentReportFragment = (AppointmentReportFragment) fragment;
            } else {
                addFilter.setVisibility(View.GONE);
                mainToolBarBellLayout.setVisibility(View.VISIBLE);
            }
            //Kailash added to move to team details page if user has only 1 team(no intermediate team listing screen required)
            //if only 1 team available than directly move to team details and manage the onBackPressed call from TeamDetailsActivity
            if (isMoveToTeamDetails) {
                Preferences.save(General.BANNER_IMG, teamsArrayList.get(0).getBanner());
                Preferences.save(General.TEAM_ID, teamsArrayList.get(0).getId());
                Preferences.save(General.TEAM_NAME, teamsArrayList.get(0).getName());
                Log.i(TAG, "replaceFragment: single teams intent");
                Intent detailsIntent = new Intent(getApplicationContext(), TeamDetailsActivity.class);
                detailsIntent.putExtra(General.TEAM, teamsArrayList.get(0));
                startActivity(detailsIntent);
                overridePendingTransition(0, 0);
            } else if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                ft.replace(R.id.app_bar_main_container, fragment, TAG);
                ft.commit();
            } else {
                Intent intent = GetIntents.get(id, getApplicationContext());
                if (intent != null) {
                    makeIntent(id, intent);
                }
            }
        }
    }


    /*toolbar button clicked
     * included help dialog for logbook help*/
    private void addButtonClick(View view) {
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage006)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) {
            if (clickEvent) {
                helpDialogActivity();
            } else if (showFilterIcon) {
                if (iconClick == ICON_LEAVE_CLICKED) {
                    filterFromHomeLeaveData(view);
                } else if (iconClick == ICON_SETTING_CLICKED) {
                    setAppointmentReminder();
                }
                /*filterFromHomeLeaveData(view);*/
            } else {
                int id = Integer.parseInt(Preferences.get(General.MODULE_ID));
                if (id == 27 || id == 54) {
                    showPlannerPopup(view);
                } else if (id == 52) {
                    Intent createEventIntent = new Intent(getApplicationContext(), CreateMotivationActivity.class);                 //CreateMotivationActivity
                    startActivity(createEventIntent, ActivityTransition.moveToNextAnimation(getApplicationContext()));
                }
            }

        }
        /*else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026))||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) {
            if (showFilterIcon) {
                setAppointmentReminder();
            }
        }*/
        else {
            int id = Integer.parseInt(Preferences.get(General.MODULE_ID));
            if (id == 27 || id == 54) {
                showPlannerPopup(view);
            } else if (id == 52) {
                Intent createEventIntent = new Intent(getApplicationContext(), CreateMotivationActivity.class);
                startActivity(createEventIntent, ActivityTransition.moveToNextAnimation(getApplicationContext()));
            }
        }
    }

    private void setAppointmentReminder() {
        View view = getLayoutInflater().inflate(R.layout.set_reminder_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;

        Button cancelBtn = view.findViewById(R.id.button_cancel);
        Button submitBtn = view.findViewById(R.id.button_submit);
        final CheckBox one = view.findViewById(R.id.none_check_box);
        final CheckBox two = view.findViewById(R.id.two_hours_check_box);
        final CheckBox three = view.findViewById(R.id.three_hours_check_box);
        final CheckBox four = view.findViewById(R.id.four_hours_check_box);
        final CheckBox five = view.findViewById(R.id.five_hours_check_box);


        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_REMINDER_SETTING_BEFORE);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray remObject = jsonObject.getJSONArray(Actions_.GET_REMINDER_SETTING_BEFORE);
                    JSONObject mainObject = remObject.getJSONObject(0);

                    if (mainObject.getString("status").equals("1")) {
                        if (mainObject.getString("time_before").equals("0")) {
                            one.setChecked(true);
                        } else if (mainObject.getString("time_before").equals("2")) {
                            two.setChecked(true);
                        } else if (mainObject.getString("time_before").equals("3")) {
                            three.setChecked(true);
                        } else if (mainObject.getString("time_before").equals("4")) {
                            four.setChecked(true);
                        } else {
                            five.setChecked(true);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneRem = "0";
                twoRem = "";
                threeRem = "";
                fourRem = "";
                fiveRem = "";
                two.setChecked(false);
                three.setChecked(false);
                four.setChecked(false);
                five.setChecked(false);
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneRem = "";
                twoRem = "2";
                threeRem = "";
                fourRem = "";
                fiveRem = "";
                one.setChecked(false);
                three.setChecked(false);
                four.setChecked(false);
                five.setChecked(false);
            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneRem = "";
                twoRem = "";
                threeRem = "3";
                fourRem = "";
                fiveRem = "";
                one.setChecked(false);
                two.setChecked(false);
                four.setChecked(false);
                five.setChecked(false);
            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneRem = "";
                twoRem = "";
                threeRem = "";
                fourRem = "4";
                fiveRem = "";
                one.setChecked(false);
                two.setChecked(false);
                three.setChecked(false);
                five.setChecked(false);
            }
        });

        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneRem = "";
                twoRem = "";
                threeRem = "";
                fourRem = "";
                fiveRem = "5";
                one.setChecked(false);
                two.setChecked(false);
                three.setChecked(false);
                four.setChecked(false);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callReminderAPI(alertDialog);
            }
        });

        alertDialog.show();
    }

    private void callReminderAPI(AlertDialog alertDialog) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.REMINDER_SETTING_BEFORE);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        if (oneRem.equals("0")) {
            requestMap.put(General.TIME_BEFORE, oneRem);
        } else if (twoRem.equals("2")) {
            requestMap.put(General.TIME_BEFORE, twoRem);
        } else if (threeRem.equals("3")) {
            requestMap.put(General.TIME_BEFORE, threeRem);
        } else if (fourRem.equals("4")) {
            requestMap.put(General.TIME_BEFORE, fourRem);
        } else if (fiveRem.equals("5")) {
            requestMap.put(General.TIME_BEFORE, fiveRem);
        }
        Log.e("REQUESTMAP", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MHAW_APPOINTMENT;
        Log.e("URLRESPONSE", url);
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject remObject = jsonObject.getJSONObject("reminder_setting_before");
                    if (remObject.getString("status").equals("1")) {
                        Toast.makeText(this, remObject.getString("msg"), Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    } else if (remObject.getString("status").equals("2")) {
                        Toast.makeText(this, remObject.getString("error"), Toast.LENGTH_LONG).show();
//                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(this, remObject.getString("msg"), Toast.LENGTH_LONG).show();
//                        alertDialog.dismiss();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void filterFromHomeLeaveData(View v) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View customView = inflater.inflate(R.layout.dialog_leave_filter, null);
            popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            popupWindow.isShowing();

            final TextView clearDateSelection;
            final TextView startDate, endDate;
            final ImageView imageviewSave, imageviewBack;
            final Calendar calendar;

            clearDateSelection = customView.findViewById(R.id.clear_selection_date);
            imageviewSave = customView.findViewById(R.id.imageview_toolbar_save);
            imageviewBack = customView.findViewById(R.id.imageview_back);
            startDate = customView.findViewById(R.id.start_date_txt);
            endDate = customView.findViewById(R.id.end_date_txt);

            if (Preferences.get("start_date").equals("") || Preferences.get("end_date").equals("")) {
                startDate.setText(Preferences.get(""));
                endDate.setText(Preferences.get(""));
            } else {
                startDate.setText(Preferences.get("start_date"));
                endDate.setText(Preferences.get("end_date"));
            }

            clearDateSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDate.setText("");
                    endDate.setText("");
                    Preferences.save("start_date", "");
                    Preferences.save("end_date", "");
                }
            });


            calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);

            startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    monthOfYear = (monthOfYear + 1);
                                    sDay = dayOfMonth;
                                    sMonth = monthOfYear;
                                    sYear = year;

                                    start_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                    try {
                                        startDate.setText(start_date);
                                        Preferences.save("start_date", start_date);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            });


            endDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog1 = new DatePickerDialog(MainActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    monthOfYear = (monthOfYear + 1);
                                    sDay = dayOfMonth;
                                    sMonth = monthOfYear;
                                    sYear = year;

                                    end_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);

                                    try {
                                        int result = Compare.validEndDate(end_date, start_date);
                                        if (result == 1) {
                                            endDate.setText(end_date);
                                            Preferences.save("end_date", end_date);
                                        } else {
                                            end_date = null;
                                            endDate.setText(null);
                                            ShowSnack.textViewWarning(endDate, getResources()
                                                    .getString(R.string.invalid_date), MainActivity.this);

                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog1.show();
                }
            });

            imageviewSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LeaveValidation(startDate.getText().toString().trim(), endDate.getText().toString().trim(), v)) {
                        leaveListingFragment.filterCoachDateWiseLeave(activity, popupWindow);
                    }
                }
            });

            imageviewBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean LeaveValidation(String startDate, String endDate, View view) {
        if (startDate == null || startDate.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please select Start Date", MainActivity.this);
            return false;
        }

        if (endDate == null || endDate.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please select End Date", MainActivity.this);
            return false;
        }
        return true;
    }

    private void helpDialogActivity() {
        View view = getLayoutInflater().inflate(R.layout.dialog_help_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM;

        Button okayBtn = view.findViewById(R.id.button_okay);
        LinearLayout helpLayout = view.findViewById(R.id.help_layout);

        RelativeLayout dashboardOption = view.findViewById(R.id.dashboard_option);
        View dashboardOptionSeprator = view.findViewById(R.id.dashboard_option_seprator);
        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            dashboardOption.setVisibility(View.GONE);
            dashboardOptionSeprator.setVisibility(View.GONE);
        }

        helpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    // show respective filter options menu
    private void showPlannerPopup(View view) {
        final PopupMenu popup = new PopupMenu(MainActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.menu_planner, popup.getMenu());
        Menu popupMenuItem = popup.getMenu();
        popupMenuItem.findItem(R.id.menu_planner_goals);
        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            popupMenuItem.findItem(R.id.menu_planner_goals).setEnabled(true);
            popupMenuItem.findItem(R.id.menu_planner_goals).setVisible(true);
        } else {
            popupMenuItem.findItem(R.id.menu_planner_goals).setEnabled(false);
            popupMenuItem.findItem(R.id.menu_planner_goals).setVisible(false);
        }

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage015") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage021") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage022")) {
            popupMenuItem.findItem(R.id.menu_planner_tasks).setVisible(false);
            popupMenuItem.findItem(R.id.menu_planner_tasks).setEnabled(false);
        } else {
            popupMenuItem.findItem(R.id.menu_planner_tasks).setVisible(true);
            popupMenuItem.findItem(R.id.menu_planner_tasks).setEnabled(true);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_planner_events:
                        Intent createEventIntent = new Intent(getApplicationContext(), CreateEventActivity.class);
                        createEventIntent.putExtra(General.IS_FROM_PLANNER, true);
                        startActivity(createEventIntent);
                        break;
                    case R.id.menu_planner_tasks:
                        Preferences.save(General.IS_FROM_TEAM_TASK, false);
                        Intent createTaskIntent = new Intent(getApplicationContext(), CreateTaskActivity.class);
                        startActivity(createTaskIntent);
                        break;
                    case R.id.menu_planner_goals:
                        Intent createGoalIntent = new Intent(getApplicationContext(), AddGoalActivity.class);
                        startActivity(createGoalIntent);
                        break;
                }
                popup.dismiss();
                return true;
            }
        });
        popup.show();
    }

    // call activity based on menu id
    private void makeIntent(int id, Intent intent) {
        startActivity(intent);
    }

    //set Title on toolbar
    public void setTitle(String title) {
        titleText.setText(title);
    }

    //set Title on toolbar from MainActivityInterface
    @Override
    public void setMainTitle(String title) {
        titleText.setText(title);
    }

    //set toolbar background color from MainActivityInterface
    public void setToolbarBackgroundColor() {
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }

    //set add icon on toolbar from MainActivityInterface
    public void toggleAdd(boolean isVisible) {
        if (isVisible) {
            notificationImageView.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            logBookIcon.setVisibility(View.GONE);
        } else {
            notificationImageView.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.GONE);
            logBookIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.imageview_toolbar_left_arrorw:
                    CustomCalendar.previousOnClick(imageViewToolbarLeftArrow, moodTitleText);
                    replaceFragmentMood();
                    break;
                case R.id.imageview_toolbar_right_arrorw:
                    CustomCalendar.nextOnClick(imageViewToolbarRightArrow, moodTitleText);
                    replaceFragmentMood();
                    break;
                case R.id.main_toolbar_bell:
                    replaceFragment(26, "Notification", null);
                    break;
                case R.id.main_filter_add:
                    appointmentReportFragment.filterAppointmentReportData(view, true);
                    break;
            }
        }
    }

    public void replaceFragmentMood() {
        Fragment fragment;
        FragmentTransaction ft;
        if (Preferences.get(General.SELECTED_MOOD_FRAGMENT).equalsIgnoreCase(getResources().getString(R.string.journal))) {
            fragment = new JournalFragment();
        } else if (Preferences.get(General.SELECTED_MOOD_FRAGMENT).equalsIgnoreCase(getResources().getString(R.string.stats))) {
            fragment = new StatsFragment();
        } else if (Preferences.get(General.SELECTED_MOOD_FRAGMENT).equalsIgnoreCase(getResources().getString(R.string.calendar))) {
            fragment = new MoodCalendarFragment();
        } else {
            fragment = new MoreFrgament();
        }
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mood_container, fragment, TAG);
        ft.commit();
    }

    // Get selected users from users list for self care share
    @Override
    public void selectedUsers(ArrayList<Friends_> users_arrayList, String selfCareContentId,
                              boolean isSelected) {
        if (isSelected) {
            String user_id = GetSelected.wallUsers(users_arrayList);
            if (!user_id.equalsIgnoreCase("0")) {
                int status = SelfCareOperations.share("" + selfCareContentId, user_id, TAG, getApplicationContext(), this);
                if (status == 1) {
                    GetErrorResources.showError(status, MainActivity.this);
                    replaceFragment(25, "Self Care", null); //move to self care i.e refresh self care list to get newly added shared with ids
                }
            }
        }
    }

    @Override
    public void setCountTime(String time, String unit) {
    }

    @Override
    public void setDurationTime(String time) {
    }

    @Override
    public void setMoodToolbar(int menuId) {

        if (menuId == 34 || menuId == 51) {
            //CustomCalendar.getInitialCalendarInfo();
            if (!Preferences.get(General.SELECTED_MOOD_FRAGMENT).equalsIgnoreCase(getResources().getString(R.string.more))) {
                titleText.setVisibility(View.GONE);
                linearLayoutMoodToolbar.setVisibility(View.VISIBLE);
                int color = Color.parseColor("#ffffff"); //green
                imageViewToolbarLeftArrow.setColorFilter(color);
                imageViewToolbarRightArrow.setColorFilter(color);
                mMaterialCalendarAdapter = new CustomCalendarAdapter(getApplicationContext());

                if (imageViewToolbarLeftArrow != null) {
                    imageViewToolbarLeftArrow.setOnClickListener(MainActivity.this);
                }
                if (moodTitleText != null) {
                    Calendar cal = Calendar.getInstance();
                    if (cal != null) {
                        String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + cal.get(Calendar.YEAR);
                        moodTitleText.setText(monthName);
                    }
                }
                if (imageViewToolbarRightArrow != null) {
                    imageViewToolbarRightArrow.setOnClickListener(MainActivity.this);
                }
            } else {
                titleText.setVisibility(View.VISIBLE);
                linearLayoutMoodToolbar.setVisibility(View.GONE);
            }
        } else {
            titleText.setVisibility(View.VISIBLE);
            linearLayoutMoodToolbar.setVisibility(View.GONE);
        }


    }

    public void hidePlusIcon(boolean showHide) {
        if (showHide) {
            addButton.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.GONE);
        }

    }

    public void hideHelpIcon(boolean showHide) {
        if (showHide) {
            addButton.setVisibility(View.VISIBLE);
            addButton.setImageResource(R.drawable.help_icon);
//            addButton.setImageResource(R.drawable.log_book_white_icon);
            clickEvent = true;
        } else {
            addButton.setVisibility(View.GONE);
        }
    }

    public void hideLogBookIcon(boolean showHide) {
        if (showHide) {
            logBookIcon.setVisibility(View.VISIBLE);
//            addButton.setImageResource(R.drawable.help_icon);
            logBookIcon.setImageResource(R.drawable.log_book_white_icon);
        } else {
            logBookIcon.setVisibility(View.GONE);
        }
    }

    public void hidesettingIcon(boolean showHide) {
        if (showHide) {
            setting.setVisibility(View.VISIBLE);
            chat_icon.setVisibility(View.VISIBLE);
        } else {
            setting.setVisibility(View.GONE);
            chat_icon.setVisibility(View.GONE);
        }
    }

    public void showEventIcon(boolean showHide) {
        if (showHide) {
            addButton.setVisibility(View.VISIBLE);
            addButton.setImageResource(R.drawable.ic_add_white);
            clickEvent = false;
        } else {
            addButton.setVisibility(View.GONE);
        }
    }

    public void showFilterIcon(boolean showHide) {
        if (showHide) {
            iconClick = ICON_LEAVE_CLICKED;
            mainToolBarBellLayout.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
            addButton.setImageResource(R.drawable.filter_white_img);
            showFilterIcon = true;
        } else {
            addButton.setVisibility(View.GONE);
            mainToolBarBellLayout.setVisibility(View.VISIBLE);
        }
    }

    public void showAppointSettingIcon(boolean showHide) {
        if (showHide) {
            iconClick = ICON_SETTING_CLICKED;
            mainToolBarBellLayout.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
            addButton.setImageResource(R.drawable.vi_drawer_setting);
            addButton.setColorFilter(Color.parseColor("#ffffff"));
            showFilterIcon = true;
        } else {
            addButton.setVisibility(View.GONE);
            mainToolBarBellLayout.setVisibility(View.VISIBLE);
        }
    }

    private void fetchNotification() {
        int status = 11;
        int counter = 0;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.NOTIFICATION_COUNTER);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_YOUTH_OPERATIONS_URL;
        //Log.e("fetchNotification", url);
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("notificationResponse", response);
                if (response != null) {
                    if (Error_.oauth(response, this) == 13) {
                        status = 13;
                    }
                    JsonArray jsonArray = GetJson_.getArray(response, General.MESSAGE);
                    if (jsonArray == null) {
                        status = 11;
                    } else {
                        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                        if (jsonObject.get(General.STATUS).getAsInt() == 1) {
                            status = jsonObject.get(General.STATUS).getAsInt();
                            counter = jsonObject.get(General.TOTAL_UNREAD_MESSAGES).getAsInt();
                        } else {
                            status = jsonObject.get(General.STATUS).getAsInt();
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("fetchNotificationError", e.getMessage());
            }
        }

        if (status == 1 && counter != 0) {

            notificationCounterButton.setVisibility(View.VISIBLE);
            Log.i(TAG, "fetchNotification: counterAfterConverted " + GetCounters.convertCounterLimit9(counter));
            notificationCounterButton.setText(GetCounters.convertCounterLimit9(counter));

        } else {
            notificationCounterButton.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String dateCaps(String dateValue) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = null;
        try {
            newDate = spf.parse(dateValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("yyyy-MM-dd");
        String date = spf.format(newDate);
        return date;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("yyyy-MM-dd", cal).toString();
        return date;
    }


    public class MyRunnableGetRole implements Runnable {
        @Override
        public void run() {
            getSetAvailabilityRolesFromServer();
        }
    }

    /* Getting members from server to hide or show set availability time menu icon
    Create by rahul maske*/
    private void getSetAvailabilityRolesFromServer() {
        String url = screen.messagelist.Urls_.MOBILE_COMET_CHAT_TEAMS;
        String DomainURL = Preferences.get(General.DOMAIN);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "get_admin_set_role_new");
        requestMap.put(General.IS_TEST_USER, "" + 0);
        requestMap.put(General.USER_ID, "" + Preferences.get(General.USER_ID));
        Log.i(TAG, "getSetAvailabilityRolesFromServer: uid comechat " + Preferences.get(General.USER_COMETCHAT_ID));
        Log.i(TAG, "getSetAvailabilityRolesFromServer: uid" + Preferences.get(General.USER_ID));
        Log.i(TAG, "getSetAvailabilityRolesFromServer: " + Preferences.get(General.DOMAIN));
        RequestBody requestBody = screen.messagelist.NetworkCall_.make(requestMap, DomainURL + url, TAG, MainActivity.this);
        Log.i(TAG, "getSetAvailabilityRolesFromServer: Domain " + DomainURL + url);
        try {
            if (requestBody != null) {
                String response = screen.messagelist.NetworkCall_.post(DomainURL + url, requestBody, TAG, MainActivity.this);
                if (response != null) {
                    Log.i(TAG, "getSetAvailabilityRolesFromServer:  response " + response);
                    Log.i(TAG, "getSetAvailabilityRolesFromServer: role " + Preferences.get(General.ROLE_ID));
                    //{"get_admin_set_role_new":[{"status":1,"msg":"other members set from admin list fetch successfully.","other_user_id":"1"}]}
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray provider_time_check_in_db = jsonObject.getJSONArray("get_admin_set_role_new");
                    other_user_id = provider_time_check_in_db.getJSONObject(0).getString("other_user_id");
                    Log.i(TAG, "getSetAvailabilityRolesFromServer: other_user_id" + other_user_id);

                    domainUrlPref = getSharedPreferences("domainUrlPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = domainUrlPref.edit();
                    editor.putString("other_user_id", "" + other_user_id);
                    editor.apply();
                } else {
                    Log.i(TAG, "getSetAvailabilityRolesFromServer:  null  ");
                }
            } else {
                Log.i(TAG, "getSetAvailabilityRolesFromServer:  null2");
            }
        } catch (Exception e) {
            Log.i(TAG, "getSetAvailabilityRolesFromServer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}