package com.modules.planner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.modules.announcement.AnnouncementDetailsActivity;
import com.modules.calendar.CustomCalendar;
import com.modules.calendar.CustomCalendarAdapter;
import com.modules.calendar.CustomCalendarWeekAdapter;
import com.modules.calendar.EventDetailsActivity;
import com.modules.calendar.InviteListActivity;
import com.modules.selfgoal.SelfGoalDetailsActivity;
import com.modules.task.TaskDetailsActivity;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.library.KeyboardUtils;
import com.sagesurfer.models.DailyPlanner_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.Planner_;
import com.sagesurfer.views.ExpandableHeightGridView;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.RequestBody;

/**
 * @author Monika M(monikam@sagesurfer.com)
 * Created on 31-03-2018
 * Last Modified on
 */

public class DailyPlannerFragment extends Fragment implements PlannerListAdapter.PlannerAdapterListener,
        View.OnClickListener, GridView.OnItemClickListener, KeyboardUtils.SoftKeyboardToggleListener, SenjamPlannerListAdapter.SenjamPlannerListAdapterListener {

    private static final String TAG = DailyPlannerFragment.class.getSimpleName();

    @BindView(R.id.relativelayout_header_search)
    RelativeLayout relativeLayoutHeaderSearch;
    @BindView(R.id.relativelayout_header)
    RelativeLayout relativeLayoutHeader;
    @BindView(R.id.edittext_search)
    EditText editTextSearch;
    @BindView(R.id.linearlayout_dailyplanner)
    LinearLayout linearLayoutDailyPlanner;
    @BindView(R.id.textview_dailyplanner_greet)
    TextView textViewDailyPlannerGreet;
    @BindView(R.id.textview_fragmentdailyplanner_name)
    TextView textViewFragmentDailyPlannerName;
    @BindView(R.id.textview_dailyplanner_message)
    TextView textViewDailyPlannerMessage;
    /*@BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;*/
    @BindView(R.id.dailyplanner_tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.dailyplanner_tab_layout_search)
    TabLayout tabLayoutSearch;
    @BindView(R.id.lineralayout_month)
    LinearLayout lineraLayoutMonth;
    @BindView(R.id.planner_fragment_month_back)
    AppCompatImageView mPrevious;
    @BindView(R.id.planner_fragment_month_name)
    TextView mMonthName;
    @BindView(R.id.planner_fragment_month_next)
    AppCompatImageView mNext;
    @BindView(R.id.planner_fragment_grid_view)
    ExpandableHeightGridView mCalendar;
    @BindView(R.id.recyclerview_swiperefresh)
    RecyclerView recyclerViewSwipeRefresh;
    @BindView(R.id.linearlayout_swiperefresh_error)
    LinearLayout linearLayoutSwipeRefreshError;
    @BindView(R.id.imageview_swiperefresh_error)
    AppCompatImageView imageViewSwipeRefreshError;
    @BindView(R.id.textview_swiperefresh_error_message)
    TextView textViewSwipeRefreshErrorMessage;
    Handler sageHandler = new Handler();
    Handler senjamHandler = new Handler();
    static Handler invitationCounterHandler = new Handler();
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private static String date;
    private ArrayList<DailyPlanner_> dailyPlannerArrayList = new ArrayList<>();
    private ArrayList<DailyPlanner_> dailyPlannerSearchArrayList = new ArrayList<>();
    private static Activity activity;
    private MainActivityInterface mainActivityInterface;
    private Unbinder unbinder;
    private static TextView countText;
    private PlannerListAdapter plannerListAdapter;
    private SenjamPlannerListAdapter senjamPlannerListAdapter;
    private CustomCalendarAdapter mMaterialCalendarAdapter;
    private CustomCalendarWeekAdapter mMaterialCalendarWeekAdapter;
    protected static int mNumEventsOnDay = 0;
    private static String selectedType = "";
    public static ArrayList<Integer> mSavedEventDays = new ArrayList<>();
    int tabPosition = 0;
    int tabSearchPosition = 0;
    boolean isKeyboardClosed = true;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                //Hide + button for senjam
                mainActivity.showEventIcon(false);
            } else {
                mainActivity.showEventIcon(true);
            }

        }
    }

    @SuppressLint({"InflateParams", "SourceLockedOrientationActivity"})
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.fragment_daily_planner, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        KeyboardUtils.addKeyboardToggleListener(getActivity(), this);

        dailyPlannerArrayList = new ArrayList<>();

        countText = (TextView) view.findViewById(R.id.invite_counter);
        int color = Color.parseColor("#ffffff"); //white
        AppCompatImageView inviteIcon = (AppCompatImageView) view.findViewById(R.id.invitation_icon);
        inviteIcon.setColorFilter(color);
        inviteIcon.setOnClickListener(this);
        mCalendar.setExpanded(true);
        mCalendar.setOnItemClickListener(this);

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        date = mYear + "-" + (mMonth + 1) + "-" + mDay;

        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            senjamPlannerListAdapter = new SenjamPlannerListAdapter(activity, dailyPlannerArrayList, this);
        } else {
            plannerListAdapter = new PlannerListAdapter(activity.getApplicationContext(), dailyPlannerArrayList, this, activity);
        }

//        plannerListAdapter = new PlannerListAdapter(activity.getApplicationContext(), dailyPlannerArrayList, this, activity);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerViewSwipeRefresh.setLayoutManager(mLayoutManager);
        recyclerViewSwipeRefresh.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSwipeRefresh.setAdapter(plannerListAdapter);

        tabLayout.setOnTabSelectedListener(tabSelectedListener);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.day)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.week)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.month)));

        tabLayoutSearch.setOnTabSelectedListener(tabSelectedListener);
        tabLayoutSearch.addTab(tabLayoutSearch.newTab().setText(getResources().getString(R.string.day)));
        tabLayoutSearch.addTab(tabLayoutSearch.newTab().setText(getResources().getString(R.string.week)));
        tabLayoutSearch.addTab(tabLayoutSearch.newTab().setText(getResources().getString(R.string.month)));

        editTextSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    editTextSearch.clearFocus();
                    InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert in != null;
                    in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                // Here you are! You got missing "backSpace" flag
                performSearch();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do something useful if you wish.
                // Or override it in TextWatcherExtended class if want to avoid it here
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.daily_planner));
        mainActivityInterface.setToolbarBackgroundColor();

        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            //getSenjamPlanner();
            getSenjamDataFromBackgroundThread();
        } else {
            getSageSurferDataFromBackgroundThread();
        }

        Preferences.save(General.IS_FROM_DAILYPLANNER, true);
        getInvitationCounterThread();
    }

    /* sageplanner data getting through background thread
     * created by rahul */
    private void getSageSurferDataFromBackgroundThread() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getSagesurfer(DailyPlannerFragment.this);
            }
        };

        Thread SageDataThread = new Thread(runnable);
        SageDataThread.start();
    }

    /*counter data getting through background thread
     * created by rahul */
    private void getInvitationCounterThread() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getInvitationCounters();
            }
        };

        Thread invitationCounter = new Thread(runnable);
        invitationCounter.start();
    }

    /*senjam planner data getting through background thread
     * created by rahul */
    private void getSenjamDataFromBackgroundThread() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getSenjamPlanner(DailyPlannerFragment.this);

            }
        };

        Thread SenjamDataThread = new Thread(runnable);
        SenjamDataThread.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        Preferences.save(General.IS_FROM_DAILYPLANNER, false);
    }

    private void setHeader() {
        //dateText.setText(showDate());
        sageHandler.post(new Runnable() {
            @Override
            public void run() {
                textViewFragmentDailyPlannerName.setText(Preferences.get(General.NAME));
                setSunIcon();
            }
        });

    }

    private String showDate() {
        String start_date = GetTime.getTodayMonthYear(GetTime.getDateTimestamp(date));
        String[] dateArray = start_date.split(",");
        if (Compare.isToday(GetTime.getCurrentDateYyyyMmDd(), date)) {
            return "Today, " + dateArray[1];
        }
        if (Compare.isTomorrow(date)) {
            return "Tomorrow, " + dateArray[1];
        }
        if (Compare.isYesterday(date)) {
            return "Yesterday, " + dateArray[1];
        }
        return GetTime.getTodayMonthYear(GetTime.getDateTimestamp(date));
    }

    private void setSunIcon() {
        String message = "Good Morning,";
        //int icon = R.drawable.vi_sun_morning;
        switch (GetTime.currentTimeSpan()) {
            case 1:
                message = "Good Morning";
                //icon = R.drawable.vi_sun_morning;
                break;
            case 2:
                message = "Good Afternoon";
                //icon = R.drawable.vi_sun_after_noon;
                break;
            case 3:
                message = "Good Evening";
                //icon = R.drawable.vi_sun_evening;
                break;
            case 4:
                message = "Good Night";
                //icon = R.drawable.vi_sun_night;
                break;
        }
        textViewDailyPlannerGreet.setText(message);
        //sunIcon.setImageResource(icon);
    }

    public void performSearch() {
        dailyPlannerSearchArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (DailyPlanner_ dailyPlannerItem : dailyPlannerArrayList) {
            if (dailyPlannerItem.getTask() != null) {
                if ((dailyPlannerItem.getTask().getTitle() != null && dailyPlannerItem.getTask().getTitle().toLowerCase().contains(searchText.toLowerCase()))) {
                    dailyPlannerSearchArrayList.add(dailyPlannerItem);
                }
            } else if (dailyPlannerItem.getEvent() != null) {
                if ((dailyPlannerItem.getEvent().getName() != null && dailyPlannerItem.getEvent().getName().toLowerCase().contains(searchText.toLowerCase()))) {
                    dailyPlannerSearchArrayList.add(dailyPlannerItem);
                }
            } else if (dailyPlannerItem.getGoal() != null) {
                if ((dailyPlannerItem.getGoal().getName() != null && dailyPlannerItem.getGoal().getName().toLowerCase().contains(searchText.toLowerCase()))) {
                    dailyPlannerSearchArrayList.add(dailyPlannerItem);
                }
            }
        }
        if (dailyPlannerSearchArrayList.size() > 0) {
            showError(false, 1);
            plannerListAdapter = new PlannerListAdapter(activity.getApplicationContext(), dailyPlannerSearchArrayList, this, activity);
            recyclerViewSwipeRefresh.setAdapter(plannerListAdapter);
        } else {
            showError(true, 2);
        }
    }

    private final TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            assert tab.getText() != null;
            if (tab.getPosition() == 0) {
                setSelectedTabLayout(tab.getPosition());
            } else if (tab.getPosition() == 1) {
                setSelectedTabLayout(tab.getPosition());
            } else if (tab.getPosition() == 2) {
                setSelectedTabLayout(tab.getPosition());
            }

            if (isKeyboardClosed) {
                tabPosition = tab.getPosition();
            } else {
                tabSearchPosition = tab.getPosition();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };

    // Make network call to get calendar counters and invitations
    /*updated by rahul for background threading..*/
    static void getInvitationCounters() {
        //ArrayList<Integer> mSavedEventDays = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        requestMap.put(General.ACTION, "calendar_counts");
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, "0");
        requestMap.put("year", "" + String.valueOf(year));
        requestMap.put("month", "" + String.valueOf(month + 1));
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CALENDAR;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    String calendar_counts = jsonObject.get("calendar_counts").getAsString();
                    if (calendar_counts.trim().length() > 0) {
                        List<String> countList = Arrays.asList(calendar_counts.split("\\s*,\\s*"));
                        for (int i = 0; i < countList.size(); i++) {
                            mSavedEventDays.add(Integer.parseInt(countList.get(i)));
                        }
                    }
                    int invite_count = jsonObject.get("invitation").getAsInt();
                    if (invite_count <= 0) {
                        invitationCounterHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                countText.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        invitationCounterHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                countText.setVisibility(View.VISIBLE);
                                countText.setText(GetCounters.convertCounter(invite_count));
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Make network call to get calendar counters week and month
    public static void getCounters(String type) {
        mSavedEventDays = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.PLANNER_COUNTER);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.TYPE, type);
        requestMap.put("year", "" + CustomCalendar.mYear);
        requestMap.put("month", "" + GetCounters.checkDigit(CustomCalendar.mMonth + 1));
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_DAY_PLANNER;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    String calendar_counts = jsonObject.get(Actions_.PLANNER_COUNTER).getAsString();
                    if (calendar_counts.trim().length() > 0) {
                        List<String> countList = Arrays.asList(calendar_counts.split("\\s*,\\s*"));
                        for (int i = 0; i < countList.size(); i++) {
                            mSavedEventDays.add(Integer.parseInt(countList.get(i)));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void showError(boolean isError, int status) {
        if (isError) {
            linearLayoutSwipeRefreshError.setVisibility(View.VISIBLE);
            recyclerViewSwipeRefresh.setVisibility(View.GONE);
            textViewSwipeRefreshErrorMessage.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            imageViewSwipeRefreshError.setImageResource(GetErrorResources.getIcon(status));
        } else {
            linearLayoutSwipeRefreshError.setVisibility(View.GONE);
            recyclerViewSwipeRefresh.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClicked(int position, String itemView) {
        if (itemView.equalsIgnoreCase("location")) {
            Intent meetIntent = new Intent(activity.getApplicationContext(), PlannerEventLocationActivity.class);
            meetIntent.putExtra(General.LOCATION, dailyPlannerArrayList.get(position).getEvent().getLocation());
            activity.startActivity(meetIntent);
            activity.overridePendingTransition(0, 0);
        } else if (itemView.equalsIgnoreCase("mainContainer")) {
            DailyPlanner_ dailyPlannerItem = dailyPlannerArrayList.get(position);
            if (dailyPlannerSearchArrayList.size() > 0) {
                dailyPlannerItem = dailyPlannerSearchArrayList.get(position);
            }
            if (dailyPlannerItem.getType() == 1) { //announcement
                Intent detailsIntent = new Intent(activity.getApplicationContext(), AnnouncementDetailsActivity.class);
                detailsIntent.putExtra(Actions_.ANNOUNCEMENT, dailyPlannerItem.getAnnouncement());
                startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
            } else if (dailyPlannerItem.getType() == 2 || dailyPlannerItem.getType() == 3) { //tasklist
                Intent detailsIntent = new Intent(activity.getApplicationContext(), TaskDetailsActivity.class);
                detailsIntent.putExtra(General.TASK_LIST, dailyPlannerItem.getTask());
                startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
            } else if (dailyPlannerItem.getType() == 4) { //event
                Intent detailsIntent = new Intent(activity.getApplicationContext(), EventDetailsActivity.class);
                detailsIntent.putExtra(Actions_.GET_EVENTS, dailyPlannerItem.getEvent());
                activity.startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
            } else if (dailyPlannerItem.getType() == 5) { //self goal
                Intent addIntent = new Intent(activity.getApplicationContext(), SelfGoalDetailsActivity.class);
                addIntent.putExtra(Actions_.MY_GOAL, dailyPlannerItem.getGoal());
                activity.startActivity(addIntent);
                activity.overridePendingTransition(0, 0);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.invitation_icon:
                Intent detailsIntent = new Intent(activity.getApplicationContext(), InviteListActivity.class);
                detailsIntent.putExtra(General.TEAM_ID, "0");
                startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
                break;

            case R.id.planner_fragment_month_back:
                mSavedEventDays.clear();
                CustomCalendar.previousOnClick(mPrevious, mMonthName, mCalendar, mMaterialCalendarAdapter);
                date = CustomCalendar.mYear + "-" + (CustomCalendar.mMonth + 1) + "-" + mDay;
                if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                    getSenjamDataFromBackgroundThread();
                } else {
                    getSageSurferDataFromBackgroundThread();
                }
                break;

            case R.id.planner_fragment_month_next:
                mSavedEventDays.clear();
                CustomCalendar.nextOnClick(mNext, mMonthName, mCalendar, mMaterialCalendarAdapter);
                date = CustomCalendar.mYear + "-" + (CustomCalendar.mMonth + 1) + "-" + mDay;
                if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                    getSenjamDataFromBackgroundThread();
                } else {
                    getSageSurferDataFromBackgroundThread();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.planner_fragment_grid_view:
                if (selectedType.equalsIgnoreCase("week")) {
                    CustomCalendar.selectCalendarDay(mMaterialCalendarWeekAdapter, position);
                    int i = 6 - position;
                    Calendar calendar = Calendar.getInstance();
                    String currentDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
                    SimpleDateFormat dateFormat = new SimpleDateFormat();
                    try {
                        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date myDate = dateFormat.parse(currentDate);
                        calendar = Calendar.getInstance();
                        calendar.setTime(myDate);
                        calendar.add(Calendar.DAY_OF_YEAR, -i);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date newDate = calendar.getTime();
                    String selectedDate = (String) DateFormat.format("dd", newDate);
                    date = CustomCalendar.mYear + "-" + (CustomCalendar.mMonth + 1) + "-" + selectedDate;
                } else {
                    CustomCalendar.selectCalendarDay(mMaterialCalendarAdapter, position);
                    mNumEventsOnDay = -1;

                    int selectedDate = position - (6 + CustomCalendar.mFirstDay);
                    date = CustomCalendar.mYear + "-" + (CustomCalendar.mMonth + 1) + "-" + selectedDate;
                }
                if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                    getSenjamDataFromBackgroundThread();
                } else {
                    getSageSurferDataFromBackgroundThread();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onToggleSoftKeyboard(boolean isVisible) {
        if (isVisible) { //Keyboard Open
            isKeyboardClosed = false;
            if (relativeLayoutHeader != null) {
                relativeLayoutHeader.setVisibility(View.GONE);
                relativeLayoutHeaderSearch.setVisibility(View.VISIBLE);
                tabLayout.setOnTabSelectedListener(tabSelectedListener);
                tabLayoutSearch.setOnTabSelectedListener(tabSelectedListener);
                if (tabSearchPosition == 0) {
                    setSelectedTabLayout(tabSearchPosition);
                } else if (tabSearchPosition == 1) {
                    setSelectedTabLayout(tabSearchPosition);
                } else if (tabSearchPosition == 2) {
                    setSelectedTabLayout(tabSearchPosition);
                }
            }
        } else { //Keyboard Closed
            isKeyboardClosed = true;
            relativeLayoutHeader.setVisibility(View.VISIBLE);
            relativeLayoutHeaderSearch.setVisibility(View.GONE);

            if (relativeLayoutHeader != null) {
                relativeLayoutHeader.setVisibility(View.VISIBLE);
                relativeLayoutHeaderSearch.setVisibility(View.GONE);
                tabLayout.setOnTabSelectedListener(tabSelectedListener);
                tabLayoutSearch.setOnTabSelectedListener(tabSelectedListener);
                if (tabPosition == 0) {
                    setSelectedTabLayout(tabPosition);
                } else if (tabPosition == 1) {
                    setSelectedTabLayout(tabPosition);
                } else if (tabPosition == 2) {
                    setSelectedTabLayout(tabPosition);
                }
            }
        }
    }

    private void setSelectedTabLayout(int selectedTab) {
        if (selectedTab == 0) {
            lineraLayoutMonth.setVisibility(View.GONE);
            mCalendar.setVisibility(View.GONE);
            date = mYear + "-" + (mMonth + 1) + "-" + mDay;
            selectedType = "";
        } else if (selectedTab == 1) {
            mCalendar.setVisibility(View.VISIBLE);

            CustomCalendar.getInitialCalendarInfo();
            lineraLayoutMonth.setVisibility(View.GONE);
            if (mCalendar != null) {
                mMaterialCalendarWeekAdapter = new CustomCalendarWeekAdapter(getActivity());
                mCalendar.setAdapter(mMaterialCalendarWeekAdapter);
                if (CustomCalendar.mCurrentDay != -1 && CustomCalendar.mFirstDay != -1) {
                    int currentDayPosition = 6;
                    mCalendar.setItemChecked(currentDayPosition, true);
                    if (mMaterialCalendarWeekAdapter != null) {
                        mMaterialCalendarWeekAdapter.notifyDataSetChanged();
                    }
                }
            }
            date = mYear + "-" + (mMonth + 1) + "-" + mDay;
            getCounters("week");
            //getInvitationCounters();
            selectedType = "week";
        } else if (selectedTab == 2) {
            mCalendar.setVisibility(View.VISIBLE);

            CustomCalendar.getInitialCalendarInfo();
            lineraLayoutMonth.setVisibility(View.VISIBLE);
            if (mPrevious != null) {
                mPrevious.setOnClickListener(this);
            }
            if (mMonthName != null) {
                Calendar cal = Calendar.getInstance();
                if (cal != null) {
                    String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + cal.get(Calendar.YEAR);
                    mMonthName.setText(monthName);
                }
            }
            if (mNext != null) {
                mNext.setOnClickListener(this);
            }
            if (mCalendar != null) {
                mMaterialCalendarAdapter = new CustomCalendarAdapter(getActivity());
                mCalendar.setAdapter(mMaterialCalendarAdapter);
                if (CustomCalendar.mCurrentDay != -1 && CustomCalendar.mFirstDay != -1) {
                    int startingPosition = 6 + CustomCalendar.mFirstDay;
                    int currentDayPosition = startingPosition + CustomCalendar.mCurrentDay;
                    mCalendar.setItemChecked(currentDayPosition, true);
                    if (mMaterialCalendarAdapter != null) {
                        mMaterialCalendarAdapter.notifyDataSetChanged();
                    }
                }
            }

            date = CustomCalendar.mYear + "-" + (CustomCalendar.mMonth + 1) + "-" + mDay;
            getCounters("month");
            //getInvitationCounters();
            selectedType = "month";
        }
        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            getSenjamDataFromBackgroundThread();
        } else {
            getSageSurferDataFromBackgroundThread();
        }
    }


    // Senjam planner call
    /*updated by rahul for background threading..*/
    private void getSenjamPlanner(DailyPlannerFragment dailyPlannerFragment) {
        senjamHandler.post(new Runnable() {
            @Override
            public void run() {
                setHeader();
            }
        });
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.ACTION, Actions_.GET_DATA);
        requestMap.put("fetch_date", date);
        //requestMap.put(General.SEARCH, date);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_DAY_PLANNER;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.e("PlannerResponse", response);
                if (response != null) {
                    dailyPlannerArrayList = Planner_.parseFeed(response, activity.getApplicationContext(), TAG);
                    if (dailyPlannerArrayList.size() > 0) {
                        if (dailyPlannerArrayList.get(0).getStatus() == 1) {
                            senjamHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    linearLayoutSwipeRefreshError.setVisibility(View.GONE);
                                    recyclerViewSwipeRefresh.setVisibility(View.VISIBLE);

                                    senjamPlannerListAdapter = new SenjamPlannerListAdapter(activity, dailyPlannerArrayList, dailyPlannerFragment);
                                    recyclerViewSwipeRefresh.setAdapter(senjamPlannerListAdapter);
                                }
                            });


//                            plannerListAdapter = new PlannerListAdapter(activity.getApplicationContext(), dailyPlannerArrayList, this, activity);
//                            recyclerViewSwipeRefresh.setAdapter(plannerListAdapter);
                        } else {
                            senjamHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showError(true, dailyPlannerArrayList.get(0).getStatus());
                                }
                            });

                        }
                    } else {
                        senjamHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showError(true, 12);
                            }
                        });

                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        senjamHandler.post(new Runnable() {
            @Override
            public void run() {
                showError(true, 11);
            }
        });


    }

    // Sagesurfer planner call
    /*updated by rahul for background threading..*/
    private void getSagesurfer(DailyPlannerFragment dailyPlannerFragment) {

        setHeader();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.ACTION, Actions_.GET_DATA);
        requestMap.put("fetch_date", date);
        //requestMap.put(General.SEARCH, date);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_DAY_PLANNER;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        Log.e(TAG, "getSagesurfer: " + requestBody);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.e("PlannerResponse", response);
                if (response != null) {
                    dailyPlannerArrayList = Planner_.parseFeed(response, activity.getApplicationContext(), TAG);
                    if (dailyPlannerArrayList.size() > 0) {
                        if (dailyPlannerArrayList.get(0).getStatus() == 1) {

                            sageHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    linearLayoutSwipeRefreshError.setVisibility(View.GONE);
                                    recyclerViewSwipeRefresh.setVisibility(View.VISIBLE);
                                    plannerListAdapter = new PlannerListAdapter(activity.getApplicationContext(), dailyPlannerArrayList, dailyPlannerFragment, activity);
                                    recyclerViewSwipeRefresh.setAdapter(plannerListAdapter);
                                }
                            });

                        } else {
                            sageHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showError(true, dailyPlannerArrayList.get(0).getStatus());
                                }
                            });

                        }
                    } else {
                        sageHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showError(true, 12);
                            }
                        });

                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sageHandler.post(new Runnable() {
            @Override
            public void run() {
                showError(true, 11);
            }
        });
    }

    @Override
    public void onSenjamPlannerLayoutClicked(DailyPlanner_ dailyPlanner) {

    }

}
