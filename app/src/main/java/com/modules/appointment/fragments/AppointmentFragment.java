package com.modules.appointment.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.modules.appointment.activity.AppointmentDetailsActivity;
import com.modules.appointment.activity.CreateAppointmentActivity;
import com.modules.appointment.adapter.AppointmentListAdapter;
import com.modules.appointment.model.Appointment_;
import com.modules.appointment.model.Staff;
import com.modules.calendar.CustomCalendar;
import com.modules.calendar.CustomCalendarAdapter;
import com.modules.calendar.CustomCalendarWeekAdapter;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.library.KeyboardUtils;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Appointments_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.views.ExpandableHeightGridView;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import org.json.JSONObject;

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
 * Created by Kailash Karankal on 2/4/2020.
 */
public class AppointmentFragment extends Fragment implements AppointmentListAdapter.AppointmentAdapterListener,
        View.OnClickListener, GridView.OnItemClickListener, KeyboardUtils.SoftKeyboardToggleListener {
    private static final String TAG = AppointmentFragment.class.getSimpleName();
    @BindView(R.id.relativelayout_header)
    RelativeLayout relativeLayoutHeader;
    @BindView(R.id.edittext_search)
    EditText editTextSearch;
    @BindView(R.id.dailyplanner_tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.lineralayout_month)
    RelativeLayout lineraLayoutMonth;
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
    private FloatingActionButton appointmentCreateButton;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private static String date;
    private ArrayList<Appointment_> appointmentArrayList = new ArrayList<>(), appointmentSearchArrayList = new ArrayList<>();
    private static Activity activity;
    private MainActivityInterface mainActivityInterface;
    private Unbinder unbinder;
    private AppointmentListAdapter appointmentListAdapter;
    private CustomCalendarAdapter mMaterialCalendarAdapter;
    private CustomCalendarWeekAdapter mMaterialCalendarWeekAdapter;
    protected static int mNumEventsOnDay = 0;
    private static String selectedType = "";
    public static ArrayList<Integer> mSavedEventDays = new ArrayList<>();
    int tabPosition = 0;
    int tabSearchPosition = 0;
    boolean isKeyboardClosed = true;
    private View view;
    private Dialog dialog;
    private String yesNoValue = "", ratingValue = "0";


    // Called Function To Show Setting Icon in Appointment
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.showAppointSettingIcon(true);
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

        view = inflater.inflate(R.layout.appointment_layout, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        KeyboardUtils.addKeyboardToggleListener(getActivity(), this);

        appointmentArrayList = new ArrayList<>();

        mCalendar.setExpanded(true);
        mCalendar.setOnItemClickListener(this);

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        date = mYear + "-" + (mMonth + 1) + "-" + mDay;

        appointmentListAdapter = new AppointmentListAdapter(activity.getApplicationContext(), appointmentArrayList, this, activity);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerViewSwipeRefresh.setLayoutManager(mLayoutManager);
        recyclerViewSwipeRefresh.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSwipeRefresh.setAdapter(appointmentListAdapter);

        tabLayout.setOnTabSelectedListener(tabSelectedListener);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.day)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.week)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.month)));


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
                performSearch();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        appointmentCreateButton = (FloatingActionButton) view.findViewById(R.id.appointment_float);
        appointmentCreateButton.setImageResource(R.drawable.ic_add_white);
        appointmentCreateButton.setOnClickListener(this);

        if (Preferences.get(General.IS_APPOINTMENT_ADD).equals("1")) {
            appointmentCreateButton.setVisibility(View.GONE);
        } else {
            appointmentCreateButton.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.appointment));
        mainActivityInterface.setToolbarBackgroundColor();
        Preferences.save(Actions_.APPOINTMENT_COUNTER, true);

        // Call API for appointment
        getAppointments(date);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        Preferences.save(Actions_.APPOINTMENT_COUNTER, false);
    }

    // Called For Search Operation
    public void performSearch() {
        appointmentSearchArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (Appointment_ appointment : appointmentArrayList) {
            if (appointment.getDescription() != null) {
                if ((appointment.getDescription() != null && appointment.getDescription().toLowerCase().contains(searchText.toLowerCase()))) {
                    appointmentSearchArrayList.add(appointment);
                }
            }
        }
        if (appointmentSearchArrayList.size() > 0) {
            showError(false, 1);
            appointmentListAdapter = new AppointmentListAdapter(activity.getApplicationContext(), appointmentSearchArrayList, this, activity);
            recyclerViewSwipeRefresh.setAdapter(appointmentListAdapter);
        } else {
            showError(true, 2);
        }
    }

    // Called For Tab Clicked Listener For Example Day , Week And Month Displaying in Appointment
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemClicked(int position, String value) {
        if (value.equals("1")) {
            Intent appointmentDetails = new Intent(activity, AppointmentDetailsActivity.class);
            appointmentDetails.putExtra(General.APPOINTMENT, appointmentArrayList.get(position));
            appointmentDetails.putExtra("showIcon", false);
            activity.startActivity(appointmentDetails);
        } else {
            showAppointmentFeedbackDialog(view);
        }

    }

    // Clicked Event For this Activity
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.planner_fragment_month_back:
                mSavedEventDays.clear();
                CustomCalendar.previousOnClick(mPrevious, mMonthName, mCalendar, mMaterialCalendarAdapter);
                date = CustomCalendar.mYear + "-" + (CustomCalendar.mMonth + 1) + "-" + mDay;
                getAppointments("");
                break;

            case R.id.planner_fragment_month_next:
                mSavedEventDays.clear();
                CustomCalendar.nextOnClick(mNext, mMonthName, mCalendar, mMaterialCalendarAdapter);
                date = CustomCalendar.mYear + "-" + (CustomCalendar.mMonth + 1) + "-" + mDay;
                getAppointments("");
                break;

            case R.id.appointment_float:
                Intent appointmentIntent = new Intent(activity.getApplicationContext(), CreateAppointmentActivity.class);
                activity.startActivity(appointmentIntent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.planner_fragment_grid_view:
                if (selectedType.equalsIgnoreCase("week")) {
                    CustomCalendar.selectCalendarDay(mMaterialCalendarWeekAdapter, position);
                    int i = position;
                    Calendar calendar = Calendar.getInstance();
                    String currentDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
                    SimpleDateFormat dateFormat = new SimpleDateFormat();
                    try {
                        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date myDate = dateFormat.parse(currentDate);
                        calendar = Calendar.getInstance();
                        calendar.setTime(myDate);
                        calendar.add(Calendar.DAY_OF_YEAR, i);
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
                getAppointments(date);
                break;
            default:
                break;
        }
    }

    @Override
    public void onToggleSoftKeyboard(boolean isVisible) {
        if (isVisible) {
            //Keyboard Open
            isKeyboardClosed = false;
            if (relativeLayoutHeader != null) {
                relativeLayoutHeader.setVisibility(View.GONE);
                tabLayout.setOnTabSelectedListener(tabSelectedListener);
                if (tabSearchPosition == 0) {
                    setSelectedTabLayout(tabSearchPosition);
                } else if (tabSearchPosition == 1) {
                    setSelectedTabLayout(tabSearchPosition);
                } else if (tabSearchPosition == 2) {
                    setSelectedTabLayout(tabSearchPosition);
                }
            }
        } else {
            //Keyboard Closed
            isKeyboardClosed = true;
            relativeLayoutHeader.setVisibility(View.VISIBLE);
            if (relativeLayoutHeader != null) {
                relativeLayoutHeader.setVisibility(View.VISIBLE);
                tabLayout.setOnTabSelectedListener(tabSelectedListener);
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

    //Called for Selected Tab to set For Example Day, Week Or Month Dispalying in Appointment Fragment
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
                mMaterialCalendarWeekAdapter = new CustomCalendarWeekAdapter(getActivity(), true);
                mCalendar.setAdapter(mMaterialCalendarWeekAdapter);
                if (CustomCalendar.mCurrentDay != -1 && CustomCalendar.mFirstDay != -1) {
                    int currentDayPosition = 0;
                    mCalendar.setItemChecked(currentDayPosition, true);
                    if (mMaterialCalendarWeekAdapter != null) {
                        mMaterialCalendarWeekAdapter.notifyDataSetChanged();
                    }
                }
            }
            date = mYear + "-" + (mMonth + 1) + "-" + mDay;
            getCounters("week");
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
            selectedType = "month";
        }
        getAppointments("");
    }

    // Api called for getting Appointment List
    private void getAppointments(String date) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_APPOINTMENT);
        requestMap.put(General.SEARCH, "");

        if (date.equals("")) {
            requestMap.put(General.FETCH_DATE, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        } else {
            requestMap.put(General.FETCH_DATE, date);
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    appointmentArrayList = Appointments_.appointmentList(response, Actions_.GET_APPOINTMENT, activity.getApplicationContext(), TAG);
                    if (appointmentArrayList.size() > 0) {
                        if (appointmentArrayList.get(0).getStatus() == 1) {

                            linearLayoutSwipeRefreshError.setVisibility(View.GONE);
                            recyclerViewSwipeRefresh.setVisibility(View.VISIBLE);
                            /*ArrayList bind and Set into Adapter */
                            appointmentListAdapter = new AppointmentListAdapter(activity.getApplicationContext(), appointmentArrayList, this, activity);
                            recyclerViewSwipeRefresh.setAdapter(appointmentListAdapter);
                        } else {
                            showError(true, appointmentArrayList.get(0).getStatus());
                            Toast.makeText(activity, "No Data.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        showError(true, 12);
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showError(true, 11);
    }

    // Make network call to get calendar counters week and month
    public static void getCounters(String type) {
        mSavedEventDays = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.APPOINTMENT_COUNTER);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.TYPE, type);

        if (type.equals("month")) {
            requestMap.put("year", "" + CustomCalendar.mYear);
            requestMap.put("month", "" + GetCounters.checkDigit(CustomCalendar.mMonth + 1));
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MHAW_APPOINTMENT;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonCounter = jsonObject.getJSONObject(Actions_.APPOINTMENT_COUNTER);
                    String calendar_counts = jsonCounter.get("days_count").toString();

                    if (calendar_counts.trim().length() > 0) {
                        List<String> countList = Arrays.asList(calendar_counts.split("\\s*,\\s*"));
                        for (int i = 0; i < countList.size(); i++) {
                            String object = countList.get(i).substring(8, 10);
                            mSavedEventDays.add(Integer.parseInt(String.valueOf(object)));
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
        Log.e("roleId", roleId);

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
                    submitClientSeveyAPII(General.SUBMIT_STAFF_SURVEY, yesNoValue, ratingValue, closePopup);
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

    private void submitClientSeveyAPII(String action, String yesNoValue, String ratingValue, ImageButton close) {
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
                        close.setVisibility(View.GONE);
                        Preferences.save(General.SHOW_APPOINTMENT_FILLED, false);
                        Toast.makeText(activity, String.valueOf(JsonObjectLike_dislike.get("msg")), Toast.LENGTH_LONG).show();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

}
