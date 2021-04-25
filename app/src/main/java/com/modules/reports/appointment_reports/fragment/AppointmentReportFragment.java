package com.modules.reports.appointment_reports.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.modules.reports.appointment_reports.activity.AppointmentReportSelectActivity;
import com.modules.reports.appointment_reports.model.ClientListModel;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.MhawSelectClientList_;
import com.sagesurfer.snack.ShowSnack;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AppointmentReportFragment extends Fragment {

    private static final String TAG = AppointmentReportFragment.class.getSimpleName();
    private static final String KEY_ID = "key_id";
    private static final String KEY_VALUE = "key_value";

    private Activity mContext;
    private MainActivityInterface mainActivityInterface;
    private Unbinder unbinder;
    private View view;
    private TextView mTxtClient, mTxtStaff, mTxtSpinnerClient, mTxtCounterAppointment, mTxtCounterShows, mTxtCounterSatisfication, mTxtSpinnerStaff, mTxtSatisficationSurveyProgress;
    private TextView mTxtWeekServiceCounter, mTxtWeekAppoSatisficationSurvey, mTxtWeekAppoStatus, mTxtWeekAppoAttends, mTxtWeekSatisficationSurveyProgress;
    private TextView mTxtStartValueServiceCount, mTxtStartValueAppointSatisSur, mTxtStartValueAppointmentStatus, mTxtStartValueAppointAttend, mTxtStartValueSatisSurPro;
    private TextView mTxtEndValueServiceCount, mTxtEndValueAppointSatisSur, mTxtEndValueAppointmentStatus, mTxtEndValueAppointAttend, mTxtEndValueSatisSurPro;
    private ImageView mImageViewServiceCountDownArrow, mImageViewSatisfSurDownArrow, mImageViewAppointmentStatusDownArrow, mImageViewAppointmentAttendDownArrow, mImageViewSatisSurProDownArrow;
    private ImageView mImageViewServiceCountUpArrow, mImageViewSatisfSurUpArrow, mImageViewAppointmentStatusUpArrow, mImageViewAppointmentAttendUpArrow, mImageViewSatisSurProUpArrow;
    private RelativeLayout mRelativeServiceCounter, mRelativeAppoSatisficationSurvey, mRelativeAppointmentStatus, mRelativeAppointmentAttend, mRelativeSatisficationSurveyProgress;
    private LinearLayout mLinearTxtClient, mLinearTxtStaff, mLinearSpinnerTxtClient, mLinearServiceCount, mLinearTitleStaff, mLinearTitleClient, mLinearSpinnerSatisSurPro;
    private LinearLayout mLinearAppoSatisficationSurvey, mLinearAppoStatus, mLinearAppoAttend, mLinearSatisficationSurveyProgress, mLinearSpinnerTxtStaff, mLinearFilterSatisSurPro;
    private LinearLayout mLinearMainDateServiceCount, mLinearMainDateAppointSatisSur, mLinearMainDateAppointmentStatus, mLinearMainDateAppointmentAttend, mLinearMainDateSatisfactionSurPro;
    private LinearLayout mLinearTitleServiceCount, mLinearTitleAppointSatisSur, mLinearTitleAppointmentStatus, mLinearTitleAppointmentAttend, mLinearTitleSatisSurPro;
    private WebView mWebServiceCount, mWebAppointmentSatisSurvey, mWebAppointmentStatus, mWebAppointmentAttend, mWebSatisfactionSurProgress;
//    private String strEditText = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String name;
    private String selectedClientId = "0", selectedStaffId = "0", selectedSatisSurProgID = "0";

    private int MODE_CLIENT = 1;
    private int MODE_STAFF = 2;
    private int MODE_STAFF_CLIENT = 3;
    private int MODE_SATIS_SUR_PRO = 4;
    private int current_mode = 1;

    //    private ArrayList<ClientListModel> clientArrayList = new ArrayList<>();
    private ScrollView mainScrollView;
    private int CLIENT_REQUEST_CODE = 13;
    private int STAFF_REQUEST_CODE = 14;
    private int CLIENT_STAFF_REQUEST_CODE = 15;
    private int SATIS_SUR_PROG_REQUEST_CODE = 16;

    private ArrayList<ClientListModel> satisfactionProgressArrayList = new ArrayList<>();
    private ArrayList<ClientListModel> clientArrayList = new ArrayList<>();
    private ArrayList<ClientListModel> staffArrayList = new ArrayList<>();
    private ArrayList<ClientListModel> staffclientArrayList = new ArrayList<>();
    private ArrayList<ClientListModel> counterReportArrayList = new ArrayList<>();

    //Progress Note filter part
    private PopupWindow popupWindow = new PopupWindow();
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String date = "", end_date = "";
    private String lastWeekly = "", lastMonthly = "", lastQuarterly = "", lastYearly = "";
    private Boolean isFilterGlobal = false;
    private ProgressBar mProgressBar;


    // this Function is for display icon of filter from Mani activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mainActivityInterface = (MainActivityInterface) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString() + " " + e.toString());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (getArguments() != null) {
            mParam1 = getArguments().getString(KEY_ID);
            mParam2 = getArguments().getString(KEY_VALUE);
            String strEditText = getArguments().getString(KEY_VALUE);
            Log.e(TAG, mParam1);
        }

        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        view = inflater.inflate(R.layout.fragment_appointment, container, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getActivity();

//        toolbar = (Toolbar) view.findViewById(R.id.main_toolbar_layout);
//        addFilter = (AppCompatImageView) toolbar.findViewById(R.id.main_filter_add);
//        addBell = (RelativeLayout) toolbar.findViewById(R.id.main_toolbar_bell_layout);
//
//        addBell.setVisibility(View.GONE);
//        addFilter.setVisibility(View.VISIBLE);

        mFunctionFindViewById(view);

        //Default Text set for Dropdown of Client & Staff
        mTxtSpinnerClient.setText("All Client");
        mTxtSpinnerStaff.setText("All Staff");

        //Common funciton for click event handler for all functions
        mAllClickEventFunction();

        mProgressBar.setVisibility(View.VISIBLE);

        //Initially fetch all client
        getSelectClientData();

        //Call count webservice for all client by default
        getAppointmentCounterReportData();

        //Call webservice to get satisfactory survery progress dropdown
        getSelectSatisfactionProgressData();

        return view;

    }


    // All Variable Declaration Function & controls initialisation
    private void mFunctionFindViewById(View view) {

        mainScrollView = view.findViewById(R.id.scrollview);
        mainScrollView.fullScroll(ScrollView.FOCUS_UP);
        mainScrollView.setNestedScrollingEnabled(false);

        mTxtClient = view.findViewById(R.id.txt_client);
        mTxtStaff = view.findViewById(R.id.txt_staff);
        mTxtSpinnerClient = view.findViewById(R.id.txt_spinner_client);
        mTxtSpinnerStaff = view.findViewById(R.id.txt_spinner_staff);
        mTxtCounterAppointment = view.findViewById(R.id.txt_counter_appointment);
        mTxtCounterShows = view.findViewById(R.id.txt_counter_shows);
        mTxtCounterSatisfication = view.findViewById(R.id.txt_counter_satisfaction);
        mTxtWeekServiceCounter = view.findViewById(R.id.txt_week_title_service_count);
        mTxtWeekAppoSatisficationSurvey = view.findViewById(R.id.txt_week_appo_satisf_sur);
        mTxtWeekAppoStatus = view.findViewById(R.id.txt_week_appo_status);
        mTxtWeekAppoAttends = view.findViewById(R.id.txt_week_appo_attend);
        mTxtWeekSatisficationSurveyProgress = view.findViewById(R.id.txt_week_satis_sur_pro);
        mTxtSatisficationSurveyProgress = view.findViewById(R.id.txt_satis_sur_pro);

        mTxtStartValueServiceCount = view.findViewById(R.id.txt_start_value_service_count);
        mTxtEndValueServiceCount = view.findViewById(R.id.txt_end_value_service_count);

        mTxtStartValueAppointSatisSur = view.findViewById(R.id.txt_start_value_appoint_satis_sur);
        mTxtEndValueAppointSatisSur = view.findViewById(R.id.txt_end_value_appoint_satis_sur);

        mTxtStartValueAppointmentStatus = view.findViewById(R.id.txt_start_value_appo_status);
        mTxtEndValueAppointmentStatus = view.findViewById(R.id.txt_end_value_appo_status);

        mTxtStartValueAppointAttend = view.findViewById(R.id.txt_start_value_appo_attend);
        mTxtEndValueAppointAttend = view.findViewById(R.id.txt_end_value_appo_attend);

        mTxtStartValueSatisSurPro = view.findViewById(R.id.txt_start_value_satis_sur_pro);
        mTxtEndValueSatisSurPro = view.findViewById(R.id.txt_end_value_satis_sur_pro);


        mLinearTxtClient = view.findViewById(R.id.linear_txt_client);
        mLinearTxtStaff = view.findViewById(R.id.linear_txt_staff);
        mLinearSpinnerTxtClient = view.findViewById(R.id.linear_spinner_txt_client);
        mLinearSpinnerTxtStaff = view.findViewById(R.id.linear_spinner_txt_staff);
        mLinearSpinnerSatisSurPro = view.findViewById(R.id.linear_spinner_satis_sur_pro);
        mLinearFilterSatisSurPro = view.findViewById(R.id.linear_filter_satis_sur_pro);

        mLinearServiceCount = view.findViewById(R.id.linear_service_count);
        mLinearAppoSatisficationSurvey = view.findViewById(R.id.linear_appo_satisf_sur);
        mLinearAppoStatus = view.findViewById(R.id.linear_appo_status);
        mLinearAppoAttend = view.findViewById(R.id.linear_appo_attend);
        mLinearSatisficationSurveyProgress = view.findViewById(R.id.linear_satis_sur_pro);

        mLinearMainDateServiceCount = view.findViewById(R.id.linear_main_date_service);
        mLinearMainDateAppointSatisSur = view.findViewById(R.id.linear_main_date_appoint_satis_sur);
        mLinearMainDateAppointmentStatus = view.findViewById(R.id.linear_main_date_appo_status);
        mLinearMainDateAppointmentAttend = view.findViewById(R.id.linear_main_date_appo_attend);
        mLinearMainDateSatisfactionSurPro = view.findViewById(R.id.linear_main_date_satis_sur_pro);

        mLinearTitleServiceCount = view.findViewById(R.id.linear_title_service_count);
        mLinearTitleAppointSatisSur = view.findViewById(R.id.linear_title_appo_satisf_sur);
        mLinearTitleAppointmentStatus = view.findViewById(R.id.linear_title_appo_status);
        mLinearTitleAppointmentAttend = view.findViewById(R.id.linear_title_appo_attend);
        mLinearTitleSatisSurPro = view.findViewById(R.id.linear_title_satis_sur_pro);

        mLinearTitleStaff = view.findViewById(R.id.linear_title_staff);
        mLinearTitleClient = view.findViewById(R.id.linear_title_client);

        mRelativeServiceCounter = view.findViewById(R.id.relative_service_count);
        mRelativeAppoSatisficationSurvey = view.findViewById(R.id.relative_appo_satisf_sur);
        mRelativeAppointmentStatus = view.findViewById(R.id.relative_appo_status);
        mRelativeAppointmentAttend = view.findViewById(R.id.relative_appo_attend);
        mRelativeSatisficationSurveyProgress = view.findViewById(R.id.relative_satis_sur_pro);

        mImageViewServiceCountDownArrow = view.findViewById(R.id.img_service_count_down_arrow);
        mImageViewServiceCountUpArrow = view.findViewById(R.id.img_service_count_up_arrow);

        mImageViewSatisfSurDownArrow = view.findViewById(R.id.img_appo_satisf_sur_down_arrow);
        mImageViewSatisfSurUpArrow = view.findViewById(R.id.img_appo_satisf_sur_up_arrow);

        mImageViewAppointmentStatusDownArrow = view.findViewById(R.id.img_appo_status_down_arrow);
        mImageViewAppointmentStatusUpArrow = view.findViewById(R.id.img_appo_status_up_arrow);

        mImageViewAppointmentAttendDownArrow = view.findViewById(R.id.img_appo_attend_down_arrow);
        mImageViewAppointmentAttendUpArrow = view.findViewById(R.id.img_appo_attend_up_arrow);

        mImageViewSatisSurProDownArrow = view.findViewById(R.id.img_satis_sur_pro_down_arrow);
        mImageViewSatisSurProUpArrow = view.findViewById(R.id.img_satis_sur_pro_up_arrow);

        mWebServiceCount = view.findViewById(R.id.web_service_count);
        mWebAppointmentSatisSurvey = view.findViewById(R.id.web_appo_satisf_sur);
        mWebAppointmentStatus = view.findViewById(R.id.web_appo_status);
        mWebAppointmentAttend = view.findViewById(R.id.web_appo_attend);
        mWebSatisfactionSurProgress = view.findViewById(R.id.web_satis_sur_pro);

        mProgressBar = view.findViewById(R.id.ProgressBar);

        mLinearTxtClient.setBackground(getResources().getDrawable(R.drawable.appointment_report_selector_client));

//        Bundle args = getArguments();
//        strEditText = args.getString("key_value");


    }

    // All Clicked Event Function
    private void mAllClickEventFunction() {

        //Click event for client dropdown
        mLinearSpinnerTxtClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Pass Client ArrayList
                Intent intent = new Intent(mContext, AppointmentReportSelectActivity.class);

                if (current_mode == MODE_STAFF) {
                    //Flag if client selected for staff
                    intent.putExtra("Mode", MODE_STAFF_CLIENT);
                    intent.putExtra("selectedStaffId", selectedStaffId);
                    intent.putExtra("selectedClientId", selectedClientId);
                    intent.putExtra("staffclientArrayList", staffclientArrayList);
                    startActivityForResult(intent, CLIENT_STAFF_REQUEST_CODE);
                } else {
                    //Flag if only client selection
                    intent.putExtra("Mode", MODE_CLIENT);
                    intent.putExtra("selectedClientId", selectedClientId);
                    intent.putExtra("clientArrayList", clientArrayList);
                    startActivityForResult(intent, CLIENT_REQUEST_CODE);
                }

            }
        });

        //Click event for Staff Dropdown
        mLinearSpinnerTxtStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // pass Staff ArrayList
                Intent intent = new Intent(mContext, AppointmentReportSelectActivity.class);
                intent.putExtra("selectedStaffId", selectedStaffId);
                intent.putExtra("Mode", MODE_STAFF);
                intent.putExtra("staffArrayList", staffArrayList);
                startActivityForResult(intent, STAFF_REQUEST_CODE);
            }
        });


        //Click event of Satisfaction survey progress dropdown
        mLinearSpinnerSatisSurPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AppointmentReportSelectActivity.class);
//                if (current_mode == MODE_STAFF) {
//                    intent.putExtra("Mode", MODE_SATIS_SUR_PRO);
//                    intent.putExtra("selectedStaffId", selectedStaffId);
//                    intent.putExtra("selectedClientId", selectedClientId);
//                    startActivityForResult(intent, SATIS_SUR_PROG_REQUEST_CODE);
//                } else {
//                    intent.putExtra("Mode", MODE_SATIS_SUR_PRO);
//                    intent.putExtra("selectedSatisSurProg", selectedSatisSurProgID);
//                    startActivityForResult(intent, SATIS_SUR_PROG_REQUEST_CODE);
//                }
                intent.putExtra("Mode", MODE_SATIS_SUR_PRO);
                intent.putExtra("selectedSatisSurProg", selectedSatisSurProgID);
                intent.putExtra("satisfactionProgressArrayList", satisfactionProgressArrayList);
                startActivityForResult(intent, SATIS_SUR_PROG_REQUEST_CODE);

//                intent.putExtra("satisfactionProgressArrayList",satisfactionProgressArrayList);
//                startActivityForResult(intent, SATIS_SUR_PROG_REQUEST_CODE);
            }
        });


        //Click event for Staff segment
        mLinearTxtStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_mode = MODE_STAFF;
                mLinearTitleStaff.setVisibility(View.VISIBLE);
                mTxtSpinnerStaff.setText("All Staff");
                mTxtSpinnerClient.setText("All Client");
                selectedStaffId = "0";
                selectedClientId = "0";
                mLinearTxtStaff.setBackground(getResources().getDrawable(R.drawable.appointment_report_selector_staff));
                mLinearTxtClient.setBackground(getResources().getDrawable(R.drawable.appointment_report_selector_white_client));
                mProgressBar.setVisibility(View.VISIBLE);
                getSelectStaffData();

            }
        });

        //Click event for client segment
        mLinearTxtClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_mode = MODE_CLIENT;
                mLinearTitleStaff.setVisibility(View.GONE);
                mTxtSpinnerClient.setText("All Client");
                selectedClientId = "0";
                mLinearTxtClient.setBackground(getResources().getDrawable(R.drawable.appointment_report_selector_client));
                mLinearTxtStaff.setBackground(getResources().getDrawable(R.drawable.appointment_report_selector_white_staff));
                getAppointmentCounterReportData();
                //getSelectClientData();
            }
        });

        //Click Event to collapse/expand service count
        mRelativeServiceCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLinearServiceCount.getVisibility() == View.VISIBLE) {
                    mImageViewServiceCountUpArrow.setVisibility(View.GONE);
                    mImageViewServiceCountDownArrow.setVisibility(View.VISIBLE);
                    mLinearServiceCount.setVisibility(View.GONE);
                } else {
                    mImageViewServiceCountUpArrow.setVisibility(View.VISIBLE);
                    mImageViewServiceCountDownArrow.setVisibility(View.GONE);
                    mLinearServiceCount.setVisibility(View.VISIBLE);
                }

            }
        });

        //Click Event to collapse/expand Satisfaction Survey
        mRelativeAppoSatisficationSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLinearAppoSatisficationSurvey.getVisibility() == View.VISIBLE) {
                    mImageViewSatisfSurDownArrow.setVisibility(View.VISIBLE);
                    mImageViewSatisfSurUpArrow.setVisibility(View.GONE);
                    mLinearAppoSatisficationSurvey.setVisibility(View.GONE);
                } else {
                    mImageViewSatisfSurDownArrow.setVisibility(View.GONE);
                    mImageViewSatisfSurUpArrow.setVisibility(View.VISIBLE);
                    mLinearAppoSatisficationSurvey.setVisibility(View.VISIBLE);
                }

            }
        });

        //Click Event to collapse/expand Appointment Status
        mRelativeAppointmentStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLinearAppoStatus.getVisibility() == View.VISIBLE) {
                    mImageViewAppointmentStatusDownArrow.setVisibility(View.VISIBLE);
                    mImageViewAppointmentStatusUpArrow.setVisibility(View.GONE);
                    mLinearAppoStatus.setVisibility(View.GONE);
                } else {
                    mImageViewAppointmentStatusDownArrow.setVisibility(View.GONE);
                    mImageViewAppointmentStatusUpArrow.setVisibility(View.VISIBLE);
                    mLinearAppoStatus.setVisibility(View.VISIBLE);
                }

            }
        });

        //Click Event to collapse/expand Appointment No Show/ Attend
        mRelativeAppointmentAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLinearAppoAttend.getVisibility() == View.VISIBLE) {
                    mImageViewAppointmentAttendDownArrow.setVisibility(View.VISIBLE);
                    mImageViewAppointmentAttendUpArrow.setVisibility(View.GONE);
                    mLinearAppoAttend.setVisibility(View.GONE);
                } else {
                    mImageViewAppointmentAttendDownArrow.setVisibility(View.GONE);
                    mImageViewAppointmentAttendUpArrow.setVisibility(View.VISIBLE);
                    mLinearAppoAttend.setVisibility(View.VISIBLE);
                }

            }
        });

        //Click Event to collapse/expand Appointment Satisfactory Survey Progress
        mRelativeSatisficationSurveyProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLinearSatisficationSurveyProgress.getVisibility() == View.VISIBLE) {
                    mImageViewSatisSurProDownArrow.setVisibility(View.VISIBLE);
                    mImageViewSatisSurProUpArrow.setVisibility(View.GONE);
                    mLinearSatisficationSurveyProgress.setVisibility(View.GONE);
                } else {
                    mImageViewSatisSurProDownArrow.setVisibility(View.GONE);
                    mImageViewSatisSurProUpArrow.setVisibility(View.VISIBLE);
                    mLinearSatisficationSurveyProgress.setVisibility(View.VISIBLE);
                }

            }
        });

        //Click Event for Date Filter only for Satisfaction survey progress
        mLinearFilterSatisSurPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterAppointmentReportData(v, isFilterGlobal);
            }
        });

    }


    // Call Back Function after selecting Client, Staff, Client of Staff & Survey Progress
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("Activity", "The activity has finished");

        // this is for selected Client name and set name on spinner
        if (resultCode == CLIENT_REQUEST_CODE) {
            name = data.getStringExtra(KEY_VALUE);
            selectedClientId = data.getStringExtra(KEY_ID);
            mTxtSpinnerClient.setText(name);
        }
        // this is for selected Staff name and set name on spinner
        else if (resultCode == STAFF_REQUEST_CODE) {
            name = data.getStringExtra(KEY_VALUE);
            selectedStaffId = data.getStringExtra(KEY_ID);
            mTxtSpinnerStaff.setText(name);
            selectedClientId = "0";
            mTxtSpinnerClient.setText("");
            getSelectStaffClientData(selectedStaffId);
        }
        // this is for selected StaffClient name and set name on both spinner
        else if (resultCode == CLIENT_STAFF_REQUEST_CODE) {
            name = data.getStringExtra(KEY_VALUE);
            selectedClientId = data.getStringExtra(KEY_ID);
            mTxtSpinnerClient.setText(name);
        }
        // this is for selected Satisfaction Survey name and set name on spinner
        else if (resultCode == SATIS_SUR_PROG_REQUEST_CODE) {
            name = data.getStringExtra(KEY_VALUE);
            selectedSatisSurProgID = data.getStringExtra(KEY_ID);
            mTxtSatisficationSurveyProgress.setText(name);
        }
        getAppointmentCounterReportData();
    }

    // Called when Clicked on Filter Icon. Same function for Global filter & survey progress filter.
    public void filterAppointmentReportData(View v, final Boolean isFilterGlobal) {
        try {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View customView = inflater.inflate(R.layout.appointment_report_filter, null);
            popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            popupWindow.isShowing();

            final LinearLayout dateLayout, dateSelectionLayout;
            final TextView clearSelection, clearDateSelection;
            final TextView startDate, endDate;
            final ImageView imageviewSave, closeNotificationFilterDialog;
            final Calendar calendar;
            final CheckBox lastWeekCheckBox, lastMonthCheckBox, lastQuarterCheckBox, lastYearCheckBox;

            dateLayout = customView.findViewById(R.id.date_layout);
            dateSelectionLayout = customView.findViewById(R.id.date_selection);
            lastWeekCheckBox = customView.findViewById(R.id.check_box_week);
            lastMonthCheckBox = customView.findViewById(R.id.check_box_month);
            lastQuarterCheckBox = customView.findViewById(R.id.check_box_quarterly);
            lastYearCheckBox = customView.findViewById(R.id.check_box_yearly);
            clearSelection = customView.findViewById(R.id.clear_selection);
            clearDateSelection = customView.findViewById(R.id.clear_selection_date);
            imageviewSave = customView.findViewById(R.id.imageview_toolbar_save);
            closeNotificationFilterDialog = customView.findViewById(R.id.imageview_back);
            startDate = customView.findViewById(R.id.start_date_txt);
            endDate = customView.findViewById(R.id.end_date_txt);

            clearData();

            //Click event for clear button
            clearDateSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastWeekCheckBox.setChecked(false);
                    lastMonthCheckBox.setChecked(false);
                    lastQuarterCheckBox.setChecked(false);
                    lastYearCheckBox.setChecked(false);
                    startDate.setText("");
                    endDate.setText("");
                }
            });

            dateSelectionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateLayout.setVisibility(View.VISIBLE);
                    lastWeekCheckBox.setChecked(false);
                    lastMonthCheckBox.setChecked(false);
                    lastQuarterCheckBox.setChecked(false);
                    lastYearCheckBox.setChecked(false);
                }
            });

            calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);

            startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    monthOfYear = (monthOfYear + 1);
                                    sDay = dayOfMonth;
                                    sMonth = monthOfYear;
                                    sYear = year;

                                    date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);

                                    if (isFilterGlobal) {
                                        mTxtStartValueServiceCount.setText(date);
                                        mTxtStartValueAppointSatisSur.setText(date);
                                        mTxtStartValueAppointmentStatus.setText(date);
                                        mTxtStartValueAppointAttend.setText(date);
                                        mTxtStartValueSatisSurPro.setText(date);

                                        mLinearMainDateServiceCount.setVisibility(View.VISIBLE);
                                        mLinearMainDateAppointSatisSur.setVisibility(View.VISIBLE);
                                        mLinearMainDateAppointmentStatus.setVisibility(View.VISIBLE);
                                        mLinearMainDateAppointmentAttend.setVisibility(View.VISIBLE);
                                        mLinearMainDateSatisfactionSurPro.setVisibility(View.VISIBLE);

                                        mLinearTitleServiceCount.setVisibility(View.GONE);
                                        mLinearTitleAppointSatisSur.setVisibility(View.GONE);
                                        mLinearTitleAppointmentStatus.setVisibility(View.GONE);
                                        mLinearTitleAppointmentAttend.setVisibility(View.GONE);
                                        mLinearTitleSatisSurPro.setVisibility(View.GONE);
                                    } else {
                                        mTxtStartValueSatisSurPro.setText(date);
                                        mLinearMainDateSatisfactionSurPro.setVisibility(View.VISIBLE);
                                        mLinearTitleSatisSurPro.setVisibility(View.GONE);
                                    }

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
                    DatePickerDialog datePickerDialog1 = new DatePickerDialog(mContext,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    monthOfYear = (monthOfYear + 1);
                                    sDay = dayOfMonth;
                                    sMonth = monthOfYear;
                                    sYear = year;

                                    end_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                    if (isFilterGlobal) {
                                        mTxtEndValueServiceCount.setText(end_date);
                                        mTxtEndValueAppointSatisSur.setText(end_date);
                                        mTxtEndValueAppointmentStatus.setText(end_date);
                                        mTxtEndValueAppointAttend.setText(end_date);
                                        mTxtEndValueSatisSurPro.setText(end_date);

                                        mLinearMainDateServiceCount.setVisibility(View.VISIBLE);
                                        mLinearMainDateAppointSatisSur.setVisibility(View.VISIBLE);
                                        mLinearMainDateAppointmentStatus.setVisibility(View.VISIBLE);
                                        mLinearMainDateAppointmentAttend.setVisibility(View.VISIBLE);
                                        mLinearMainDateSatisfactionSurPro.setVisibility(View.VISIBLE);

                                        mLinearTitleServiceCount.setVisibility(View.GONE);
                                        mLinearTitleAppointSatisSur.setVisibility(View.GONE);
                                        mLinearTitleAppointmentStatus.setVisibility(View.GONE);
                                        mLinearTitleAppointmentAttend.setVisibility(View.GONE);
                                        mLinearTitleSatisSurPro.setVisibility(View.GONE);
                                    } else {
                                        mTxtEndValueSatisSurPro.setText(end_date);
                                        mLinearMainDateSatisfactionSurPro.setVisibility(View.VISIBLE);
                                        mLinearTitleSatisSurPro.setVisibility(View.GONE);
                                    }


                                    try {
                                        int result = Compare.validEndDate(end_date, date);
                                        if (result == 1) {
                                            endDate.setText(end_date);
                                        } else {
                                            end_date = null;
                                            endDate.setText(null);
                                            ShowSnack.textViewWarning(endDate, mContext.getResources()
                                                    .getString(R.string.invalid_date), mContext);

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

            if (isFilterGlobal) {
                if (Preferences.getBoolean("w")) {
                    lastWeekCheckBox.setChecked(true);
                } else {
                    lastWeekCheckBox.setChecked(false);
                }
            } else {
                if (Preferences.getBoolean("wf")) {
                    lastWeekCheckBox.setChecked(true);
                } else {
                    lastWeekCheckBox.setChecked(false);
                }
            }
//            if (Preferences.getBoolean("w")) {
//                lastWeekCheckBox.setChecked(true);
//            } else {
//                lastWeekCheckBox.setChecked(false);
//            }

            lastWeekCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                    if (isFilterGlobal) {
                        if (checked) {
                            lastWeekly = "w";
                            lastYearCheckBox.setChecked(false);
                            lastMonthCheckBox.setChecked(false);
                            lastQuarterCheckBox.setChecked(false);
                            Preferences.save("w", true);
                            startDate.setText("");
                            endDate.setText("");

                            if (isFilterGlobal) {
                                mTxtWeekServiceCounter.setText("WeekWise");
                                mTxtWeekAppoSatisficationSurvey.setText("WeekWise");
                                mTxtWeekAppoStatus.setText("WeekWise");
                                mTxtWeekAppoAttends.setText("WeekWise");
                                mTxtWeekSatisficationSurveyProgress.setText("WeekWise");

                                mLinearMainDateServiceCount.setVisibility(View.GONE);
                                mLinearMainDateAppointSatisSur.setVisibility(View.GONE);
                                mLinearMainDateAppointmentStatus.setVisibility(View.GONE);
                                mLinearMainDateAppointmentAttend.setVisibility(View.GONE);
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);

                                mLinearTitleServiceCount.setVisibility(View.VISIBLE);
                                mLinearTitleAppointSatisSur.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentStatus.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentAttend.setVisibility(View.VISIBLE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            } else {
                                mTxtWeekSatisficationSurveyProgress.setText("WeekWise");
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            }

                        } else {
                            lastWeekly = "";
                            Preferences.save("w", false);
                        }
                    } else {
                        if (checked) {
                            lastWeekly = "w";
                            lastYearCheckBox.setChecked(false);
                            lastMonthCheckBox.setChecked(false);
                            lastQuarterCheckBox.setChecked(false);
                            Preferences.save("wf", true);
                            startDate.setText("");
                            endDate.setText("");

                            if (isFilterGlobal) {
                                mTxtWeekServiceCounter.setText("WeekWise");
                                mTxtWeekAppoSatisficationSurvey.setText("WeekWise");
                                mTxtWeekAppoStatus.setText("WeekWise");
                                mTxtWeekAppoAttends.setText("WeekWise");
                                mTxtWeekSatisficationSurveyProgress.setText("WeekWise");

                                mLinearMainDateServiceCount.setVisibility(View.GONE);
                                mLinearMainDateAppointSatisSur.setVisibility(View.GONE);
                                mLinearMainDateAppointmentStatus.setVisibility(View.GONE);
                                mLinearMainDateAppointmentAttend.setVisibility(View.GONE);
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);

                                mLinearTitleServiceCount.setVisibility(View.VISIBLE);
                                mLinearTitleAppointSatisSur.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentStatus.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentAttend.setVisibility(View.VISIBLE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            } else {
                                mTxtWeekSatisficationSurveyProgress.setText("WeekWise");
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            }

                        } else {
                            lastWeekly = "";
                            Preferences.save("wf", false);
                        }
                    }

                }
            });

            if (isFilterGlobal) {
                if (Preferences.getBoolean("m")) {
                    lastMonthCheckBox.setChecked(true);
                } else {
                    lastMonthCheckBox.setChecked(false);
                }
            } else {
                if (Preferences.getBoolean("mf")) {
                    lastMonthCheckBox.setChecked(true);
                } else {
                    lastMonthCheckBox.setChecked(false);
                }
            }
//            if (Preferences.getBoolean("m")) {
//                lastMonthCheckBox.setChecked(true);
//            } else {
//                lastMonthCheckBox.setChecked(false);
//            }

            lastMonthCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                    if (isFilterGlobal) {
                        if (checked) {
                            lastMonthly = "m";
                            lastWeekCheckBox.setChecked(false);
                            lastQuarterCheckBox.setChecked(false);
                            lastYearCheckBox.setChecked(false);
                            Preferences.save("m", true);
                            startDate.setText("");
                            endDate.setText("");

                            if (isFilterGlobal) {
                                mTxtWeekServiceCounter.setText("MonthWise");
                                mTxtWeekAppoSatisficationSurvey.setText("MonthWise");
                                mTxtWeekAppoStatus.setText("MonthWise");
                                mTxtWeekAppoAttends.setText("MonthWise");
                                mTxtWeekSatisficationSurveyProgress.setText("MonthWise");

                                mLinearMainDateServiceCount.setVisibility(View.GONE);
                                mLinearMainDateAppointSatisSur.setVisibility(View.GONE);
                                mLinearMainDateAppointmentStatus.setVisibility(View.GONE);
                                mLinearMainDateAppointmentAttend.setVisibility(View.GONE);
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);

                                mLinearTitleServiceCount.setVisibility(View.VISIBLE);
                                mLinearTitleAppointSatisSur.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentStatus.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentAttend.setVisibility(View.VISIBLE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            } else {
                                mTxtWeekSatisficationSurveyProgress.setText("MonthWise");
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            }


                        } else {
                            lastMonthly = "";
                            Preferences.save("m", false);
                        }
                    } else {
                        if (checked) {
                            lastMonthly = "m";
                            lastWeekCheckBox.setChecked(false);
                            lastQuarterCheckBox.setChecked(false);
                            lastYearCheckBox.setChecked(false);
                            Preferences.save("mf", true);
                            startDate.setText("");
                            endDate.setText("");

                            if (isFilterGlobal) {
                                mTxtWeekServiceCounter.setText("MonthWise");
                                mTxtWeekAppoSatisficationSurvey.setText("MonthWise");
                                mTxtWeekAppoStatus.setText("MonthWise");
                                mTxtWeekAppoAttends.setText("MonthWise");
                                mTxtWeekSatisficationSurveyProgress.setText("MonthWise");

                                mLinearMainDateServiceCount.setVisibility(View.GONE);
                                mLinearMainDateAppointSatisSur.setVisibility(View.GONE);
                                mLinearMainDateAppointmentStatus.setVisibility(View.GONE);
                                mLinearMainDateAppointmentAttend.setVisibility(View.GONE);
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);

                                mLinearTitleServiceCount.setVisibility(View.VISIBLE);
                                mLinearTitleAppointSatisSur.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentStatus.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentAttend.setVisibility(View.VISIBLE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            } else {
                                mTxtWeekSatisficationSurveyProgress.setText("MonthWise");
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            }


                        } else {
                            lastMonthly = "";
                            Preferences.save("mf", false);
                        }
                    }

                }
            });

            if (isFilterGlobal) {
                if (Preferences.getBoolean("3m")) {
                    lastQuarterCheckBox.setChecked(true);
                } else {
                    lastQuarterCheckBox.setChecked(false);
                }
            } else {
                if (Preferences.getBoolean("3mf")) {
                    lastQuarterCheckBox.setChecked(true);
                } else {
                    lastQuarterCheckBox.setChecked(false);
                }
            }

//            if (Preferences.getBoolean("3m")) {
//                lastQuarterCheckBox.setChecked(true);
//            } else {
//                lastQuarterCheckBox.setChecked(false);
//            }

            lastQuarterCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                    if (isFilterGlobal) {
                        if (checked) {
                            lastQuarterly = "3m";
                            lastMonthCheckBox.setChecked(false);
                            lastYearCheckBox.setChecked(false);
                            lastWeekCheckBox.setChecked(false);
                            Preferences.save("3m", true);
                            startDate.setText("");
                            endDate.setText("");

                            if (isFilterGlobal) {
                                mTxtWeekServiceCounter.setText("Quarter Wise");
                                mTxtWeekAppoSatisficationSurvey.setText("Quarter Wise");
                                mTxtWeekAppoStatus.setText("Quarter Wise");
                                mTxtWeekAppoAttends.setText("Quarter Wise");
                                mTxtWeekSatisficationSurveyProgress.setText("Quarter Wise");

                                mLinearMainDateServiceCount.setVisibility(View.GONE);
                                mLinearMainDateAppointSatisSur.setVisibility(View.GONE);
                                mLinearMainDateAppointmentStatus.setVisibility(View.GONE);
                                mLinearMainDateAppointmentAttend.setVisibility(View.GONE);
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);

                                mLinearTitleServiceCount.setVisibility(View.VISIBLE);
                                mLinearTitleAppointSatisSur.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentStatus.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentAttend.setVisibility(View.VISIBLE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            } else {
                                mTxtWeekSatisficationSurveyProgress.setText("Quarter Wise");
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            }


                        } else {
                            lastQuarterly = "";
                            Preferences.save("3m", false);
                        }
                    } else {
                        if (checked) {
                            lastQuarterly = "3m";
                            lastMonthCheckBox.setChecked(false);
                            lastYearCheckBox.setChecked(false);
                            lastWeekCheckBox.setChecked(false);
                            Preferences.save("3mf", true);
                            startDate.setText("");
                            endDate.setText("");

                            if (isFilterGlobal) {
                                mTxtWeekServiceCounter.setText("Quarter Wise");
                                mTxtWeekAppoSatisficationSurvey.setText("Quarter Wise");
                                mTxtWeekAppoStatus.setText("Quarter Wise");
                                mTxtWeekAppoAttends.setText("Quarter Wise");
                                mTxtWeekSatisficationSurveyProgress.setText("Quarter Wise");

                                mLinearMainDateServiceCount.setVisibility(View.GONE);
                                mLinearMainDateAppointSatisSur.setVisibility(View.GONE);
                                mLinearMainDateAppointmentStatus.setVisibility(View.GONE);
                                mLinearMainDateAppointmentAttend.setVisibility(View.GONE);
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);

                                mLinearTitleServiceCount.setVisibility(View.VISIBLE);
                                mLinearTitleAppointSatisSur.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentStatus.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentAttend.setVisibility(View.VISIBLE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            } else {
                                mTxtWeekSatisficationSurveyProgress.setText("Quarter Wise");
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            }


                        } else {
                            lastQuarterly = "";
                            Preferences.save("3mf", false);
                        }
                    }
                }
            });

            if (isFilterGlobal) {
                if (Preferences.getBoolean("y")) {
                    lastYearCheckBox.setChecked(true);
                } else {
                    lastYearCheckBox.setChecked(false);
                }
            } else {
                if (Preferences.getBoolean("yf")) {
                    lastYearCheckBox.setChecked(true);
                } else {
                    lastYearCheckBox.setChecked(false);
                }
            }
//            if (Preferences.getBoolean("y")) {
//                lastYearCheckBox.setChecked(true);
//            } else {
//                lastYearCheckBox.setChecked(false);
//            }

            lastYearCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                    if (isFilterGlobal) {
                        if (checked) {
                            lastYearly = "y";
                            lastQuarterCheckBox.setChecked(false);
                            lastMonthCheckBox.setChecked(false);
                            lastWeekCheckBox.setChecked(false);
                            Preferences.save("y", true);
                            startDate.setText("");
                            endDate.setText("");

                            if (isFilterGlobal) {
                                mTxtWeekServiceCounter.setText("YearWise");
                                mTxtWeekAppoSatisficationSurvey.setText("YearWise");
                                mTxtWeekAppoStatus.setText("YearWise");
                                mTxtWeekAppoAttends.setText("YearWise");
                                mTxtWeekSatisficationSurveyProgress.setText("YearWise");

                                mLinearMainDateServiceCount.setVisibility(View.GONE);
                                mLinearMainDateAppointSatisSur.setVisibility(View.GONE);
                                mLinearMainDateAppointmentStatus.setVisibility(View.GONE);
                                mLinearMainDateAppointmentAttend.setVisibility(View.GONE);
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);

                                mLinearTitleServiceCount.setVisibility(View.VISIBLE);
                                mLinearTitleAppointSatisSur.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentStatus.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentAttend.setVisibility(View.VISIBLE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            } else {
                                mTxtWeekSatisficationSurveyProgress.setText("YearWise");
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            }


                        } else {
                            lastYearly = "";
                            Preferences.save("y", false);
                        }
                    } else {
                        if (checked) {
                            lastYearly = "y";
                            lastQuarterCheckBox.setChecked(false);
                            lastMonthCheckBox.setChecked(false);
                            lastWeekCheckBox.setChecked(false);
                            Preferences.save("yf", true);
                            startDate.setText("");
                            endDate.setText("");

                            if (isFilterGlobal) {
                                mTxtWeekServiceCounter.setText("YearWise");
                                mTxtWeekAppoSatisficationSurvey.setText("YearWise");
                                mTxtWeekAppoStatus.setText("YearWise");
                                mTxtWeekAppoAttends.setText("YearWise");
                                mTxtWeekSatisficationSurveyProgress.setText("YearWise");

                                mLinearMainDateServiceCount.setVisibility(View.GONE);
                                mLinearMainDateAppointSatisSur.setVisibility(View.GONE);
                                mLinearMainDateAppointmentStatus.setVisibility(View.GONE);
                                mLinearMainDateAppointmentAttend.setVisibility(View.GONE);
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);

                                mLinearTitleServiceCount.setVisibility(View.VISIBLE);
                                mLinearTitleAppointSatisSur.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentStatus.setVisibility(View.VISIBLE);
                                mLinearTitleAppointmentAttend.setVisibility(View.VISIBLE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            } else {
                                mTxtWeekSatisficationSurveyProgress.setText("YearWise");
                                mLinearMainDateSatisfactionSurPro.setVisibility(View.GONE);
                                mLinearTitleSatisSurPro.setVisibility(View.VISIBLE);
                            }


                        } else {
                            lastYearly = "";
                            Preferences.save("yf", false);
                        }
                    }
                }
            });

            imageviewSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isFilterGlobal) {
                        getAppointmentCounterReportData();
                    } else {
                        LoadAppointmentSatisfcatonSurveyProgressGraphs();
                    }
//                    getSelectSatisfactionProgressData();
//                    getAppointmentCounterReportData();
//                    Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
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

    // Called For clear or reset Data.
    private void clearData() {
        date = "";
        end_date = "";
        lastWeekly = "";
        lastMonthly = "";
        lastQuarterly = "";
        lastYearly = "";

    }

    public AppointmentReportFragment() {
        // Required empty public constructor
    }

    // Creating newInstance for ths Frgment
    public static AppointmentReportFragment newInstance(String param1, String param2) {
        AppointmentReportFragment fragment = new AppointmentReportFragment();
        Bundle args = new Bundle();
        args.putString(KEY_ID, param1);
        args.putString(KEY_VALUE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(KEY_ID);
//            mParam2 = getArguments().getString(KEY_VALUE);
//            String strEditText = getArguments().getString(KEY_VALUE);
//            Log.e(TAG,mParam1);
//        }
    }

    //===== Webservice Calling START =====//

    // Api call for get date of count section
    private void getAppointmentCounterReportData() {
        String action = Actions_.GET_APPOINTMENT_COUNTER_REPORT;
        HashMap<String, String> requestMap = new HashMap<>();

        counterReportArrayList.clear();
        requestMap.put(General.ACTION, action);
        if (selectedClientId.equals("0")) {
            requestMap.put(General.YOUTH_ID, "All");
        } else {
            requestMap.put(General.YOUTH_ID, selectedClientId);
        }

        if (current_mode == MODE_STAFF) {

            if (selectedStaffId.equals("0")) {
                requestMap.put(General.STAFF_ID, "All");
            } else {
                requestMap.put(General.STAFF_ID, selectedStaffId);
            }
        } else {
            requestMap.put(General.STAFF_ID, "All");
        }


        requestMap.put(General.VIEW_MODE, String.valueOf(current_mode));
//        requestMap.put(General.DATE_TYPE, "3m");

        if (!date.equals("") && !end_date.equals("")) {
            requestMap.put(General.DATE_TYPE, date);
//            requestMap.put(General.START_DATE, date);
            requestMap.put(General.END, end_date);
        } else if (lastWeekly.equals("w")) {
            requestMap.put(General.DATE_TYPE, lastWeekly);
        } else if (lastMonthly.equals("m")) {
            requestMap.put(General.DATE_TYPE, lastMonthly);
        } else if (lastQuarterly.equals("3m")) {
            requestMap.put(General.DATE_TYPE, lastQuarterly);
        } else if (lastYearly.equals("y")) {
            requestMap.put(General.DATE_TYPE, lastYearly);
        } else {
            requestMap.put(General.DATE_TYPE, "3m");
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext, mContext);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, mContext, mContext);
                if (response != null) {

                    Log.e("Response", response);
                    counterReportArrayList = MhawSelectClientList_.parseSelectClientList(response, action, mContext, TAG);
                    if (counterReportArrayList.size() > 0) {
                        if (counterReportArrayList.get(0).getStatus() == 1) {
                            mTxtCounterAppointment.setText(counterReportArrayList.get(0).getTotal());
                            mTxtCounterShows.setText(counterReportArrayList.get(0).getNo_shows());
                            mTxtCounterSatisfication.setText(counterReportArrayList.get(0).getSatisfaction_avg());

                            //Load Graphs
                            LoadServiceCountGraphs();
                            LoadAppointmentSatisSurveyGraphs();
                            LoadAppointmentStatusGraphs();
                            LoadAppointmentAttendGraphs();
                            LoadAppointmentSatisfcatonSurveyProgressGraphs();

                            mProgressBar.setVisibility(View.GONE);
//
                        } else {
//                            showError(true, caseloadArrayList.get(0).getStatus());
                            Log.e("ErrorClientListOne", "" + counterReportArrayList.get(0).getStatus());
                        }
                    } else {
//                        showError(true, caseloadArrayList.get(0).getStatus());
                        Log.e("ErrorClientListTwo", "" + counterReportArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ErrorAppoResuClient", e.getMessage());
            }
        }
    }

    // Api call for Satisfaction Survey Progress
    private void getSelectSatisfactionProgressData() {
        String action = Actions_.GET_SERVICE;
        HashMap<String, String> requestMap = new HashMap<>();

        satisfactionProgressArrayList.clear();
        requestMap.put(General.ACTION, action);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext, mContext);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, mContext, mContext);
                if (response != null) {
                    satisfactionProgressArrayList = MhawSelectClientList_.parseSelectClientList(response, action, mContext, TAG);
                    if (satisfactionProgressArrayList.size() > 0) {
                        if (satisfactionProgressArrayList.get(0).getStatus() == 1) {

                            Log.e("Response", "" + satisfactionProgressArrayList.size());
                            mTxtSatisficationSurveyProgress.setText(satisfactionProgressArrayList.get(0).getName());

                            selectedSatisSurProgID = satisfactionProgressArrayList.get(0).getId();
                            LoadAppointmentSatisfcatonSurveyProgressGraphs();

                            mProgressBar.setVisibility(View.GONE);

                        } else {
//                            showError(true, caseloadArrayList.get(0).getStatus());
                            Log.e("ErrorClientListOne", "" + satisfactionProgressArrayList.get(0).getStatus());
                        }
                    } else {
//                        showError(true, caseloadArrayList.get(0).getStatus());
                        Log.e("ErrorClientListTwo", "" + satisfactionProgressArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ErrorAppoResuClient", e.getMessage());
            }
        }
    }

    //Api Call for Client Data
    private void getSelectClientData() {
        String action = Actions_.GET_CLIENT_REPORT;
        HashMap<String, String> requestMap = new HashMap<>();
        clientArrayList.clear();
        requestMap.put(General.ACTION, action);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext, mContext);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, mContext, mContext);
                if (response != null) {
                    clientArrayList = MhawSelectClientList_.parseSelectClientList(response, action, mContext, TAG);
                    if (clientArrayList.size() > 0) {
                        if (clientArrayList.get(0).getStatus() == 1) {
//                            showError(false, 1);
                            ClientListModel model = new ClientListModel();
                            model.setId("0");
                            model.setName("All Client");
                            clientArrayList.add(0, model);

                            mProgressBar.setVisibility(View.GONE);


                        } else {
//                            showError(true, caseloadArrayList.get(0).getStatus());
                            Log.e("ErrorClientListOne", "" + clientArrayList.get(0).getStatus());
                        }
                    } else {
//                        showError(true, caseloadArrayList.get(0).getStatus());
                        Log.e("ErrorClientListTwo", "" + clientArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ErrorAppoResuClient", e.getMessage());
            }
        }
    }

    //Api Call for Staff Data
    private void getSelectStaffData() {
        String action = Actions_.GET_STAFFS_REPORT;
        HashMap<String, String> requestMap = new HashMap<>();

        staffArrayList.clear();
        requestMap.put(General.ACTION, action);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext, mContext);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, mContext, mContext);
                if (response != null) {
                    staffArrayList = MhawSelectClientList_.parseSelectClientList(response, action, mContext, TAG);
                    if (staffArrayList.size() > 0) {
                        if (staffArrayList.get(0).getStatus() == 1) {
//                            showError(false, 1);
                            ClientListModel model = new ClientListModel();
                            model.setId("0");
                            model.setName("All Staff");
                            staffArrayList.add(0, model);
                            mTxtSpinnerStaff.setText("All Staff");
                            selectedStaffId = staffArrayList.get(0).getId();
                            getSelectStaffClientData(selectedStaffId);

                            mProgressBar.setVisibility(View.GONE);

                        } else {
//                            showError(true, caseloadArrayList.get(0).getStatus());
                            Log.e("ErrorStaffListOne", "" + staffArrayList.get(0).getStatus());
                        }
                    } else {
//                        showError(true, caseloadArrayList.get(0).getStatus());
                        Log.e("ErrorStaffListTwo", "" + staffArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ErrorAppoResuClient", e.getMessage());
            }
        }
    }

    //Api Call for Selected Staff Client Data
    private void getSelectStaffClientData(String staffId) {
        String action = Actions_.GET_CLIENT_UNDER_STAFF_REPORT;
        HashMap<String, String> requestMap = new HashMap<>();

        staffclientArrayList.clear();
        requestMap.put(General.ACTION, action);
        if (staffId.equals("0")) {
            requestMap.put(General.STAFF_ID, "All");
        } else {
            requestMap.put(General.STAFF_ID, staffId);
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext, mContext);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, mContext, mContext);
                if (response != null) {
                    staffclientArrayList = MhawSelectClientList_.parseSelectClientList(response, action, mContext, TAG);
                    if (staffclientArrayList.size() > 0) {
                        if (staffclientArrayList.get(0).getStatus() == 1) {
//                            showError(false, 1);
                            if (!staffId.equals("0")) {
                                ClientListModel model = new ClientListModel();
                                model.setId("0");
                                model.setName("All Client");
                                staffclientArrayList.add(0, model);
                                mTxtSpinnerClient.setText(staffclientArrayList.get(0).getName());
                            }

                            mTxtSpinnerClient.setText(staffclientArrayList.get(0).getName());
                            selectedClientId = staffclientArrayList.get(0).getId();
                            getAppointmentCounterReportData();

                        } else {
//                            showError(true, staffClientArrayList.get(0).getStatus());
                            Log.e("ErrorStaffListOne", "" + staffclientArrayList.get(0).getStatus());
                        }
                    } else {
//                        showError(true, staffClientArrayList.get(0).getStatus());
                        Log.e("ErrorStaffListTwo", "" + staffclientArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ErrorAppoResuClient", e.getMessage());
            }
        }
    }

    //===== Webservice Calling END =====//

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    //===== Load Grpahs for all section START =====//

    public void LoadServiceCountGraphs() {
        mWebServiceCount.getSettings().setLoadWithOverviewMode(true);
        mWebServiceCount.setInitialScale(85);
        mWebServiceCount.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebServiceCount.setScrollbarFadingEnabled(false);

        mWebServiceCount.setWebChromeClient(new WebChromeClient());
        mWebServiceCount.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebServiceCount.getSettings().setLoadsImagesAutomatically(true);
        mWebServiceCount.getSettings().setJavaScriptEnabled(true);
        mWebServiceCount.getSettings().setDomStorageEnabled(true);
        mWebServiceCount.getSettings().setUseWideViewPort(true);
        mWebServiceCount.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebServiceCount.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebServiceCount.setVerticalScrollBarEnabled(false);
        mWebServiceCount.setHorizontalScrollBarEnabled(false);
        mWebServiceCount.getSettings().setLoadsImagesAutomatically(true);
        mWebServiceCount.getSettings().setBuiltInZoomControls(true);
        mWebServiceCount.getSettings().setUserAgentString("Android WebView");

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_SERVICE_COUNT_REPORT);
        if (selectedClientId.equals("0")) {
            requestMap.put(General.YOUTH_ID, "All");
        } else {
            requestMap.put(General.YOUTH_ID, selectedClientId);
        }
        if (current_mode == MODE_STAFF) {

            if (selectedStaffId.equals("0")) {
                requestMap.put(General.STAFF_ID, "All");
            } else {
                requestMap.put(General.STAFF_ID, selectedStaffId);
            }
        } else {

            requestMap.put(General.STAFF_ID, "All");
        }
        requestMap.put(General.VIEW_MODE, String.valueOf(current_mode));
        // requestMap.put(General.DATE_TYPE, "3m");
        if (!date.equals("") && !end_date.equals("")) {
            requestMap.put(General.DATE_TYPE, date);
//            requestMap.put(General.START_DATE, date);
            requestMap.put(General.END, end_date);
        } else if (lastWeekly.equals("w")) {
            requestMap.put(General.DATE_TYPE, lastWeekly);
        } else if (lastMonthly.equals("m")) {
            requestMap.put(General.DATE_TYPE, lastMonthly);
        } else if (lastQuarterly.equals("3m")) {
            requestMap.put(General.DATE_TYPE, lastQuarterly);
        } else if (lastYearly.equals("y")) {
            requestMap.put(General.DATE_TYPE, lastYearly);
        } else {
            requestMap.put(General.DATE_TYPE, "3m");
        }

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext, mContext);
        if (requestBody != null) {
            try {
                Request request_new = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .tag(TAG)
                        .build();
                url = url + "?" + MakeCall.bodyToString(request_new);

                Log.e(TAG, "Url -> " + url);
                mWebServiceCount.loadUrl(url);
//                LoadAppointmentSatisSurveyGraphs();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void LoadAppointmentSatisSurveyGraphs() {
        mWebAppointmentSatisSurvey.getSettings().setLoadWithOverviewMode(true);
        mWebAppointmentSatisSurvey.setInitialScale(85);
        mWebAppointmentSatisSurvey.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebAppointmentSatisSurvey.setScrollbarFadingEnabled(false);

        mWebAppointmentSatisSurvey.setWebChromeClient(new WebChromeClient());
        mWebAppointmentSatisSurvey.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebAppointmentSatisSurvey.getSettings().setLoadsImagesAutomatically(true);
        mWebAppointmentSatisSurvey.getSettings().setJavaScriptEnabled(true);
        mWebAppointmentSatisSurvey.getSettings().setDomStorageEnabled(true);
        mWebAppointmentSatisSurvey.getSettings().setUseWideViewPort(true);
        mWebAppointmentSatisSurvey.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebAppointmentSatisSurvey.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebAppointmentSatisSurvey.setVerticalScrollBarEnabled(false);
        mWebAppointmentSatisSurvey.setHorizontalScrollBarEnabled(false);
        mWebAppointmentSatisSurvey.getSettings().setLoadsImagesAutomatically(true);
        mWebAppointmentSatisSurvey.getSettings().setBuiltInZoomControls(true);
        mWebAppointmentSatisSurvey.getSettings().setUserAgentString("Android WebView");

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_SERVICE_RATING_REPORT);
        if (selectedClientId.equals("0")) {
            requestMap.put(General.YOUTH_ID, "All");
        } else {
            requestMap.put(General.YOUTH_ID, selectedClientId);
        }
        if (current_mode == MODE_STAFF) {

            if (selectedStaffId.equals("0")) {
                requestMap.put(General.STAFF_ID, "All");
            } else {
                requestMap.put(General.STAFF_ID, selectedStaffId);
            }
        } else {

            requestMap.put(General.STAFF_ID, "All");
        }
        requestMap.put(General.VIEW_MODE, String.valueOf(current_mode));
//        requestMap.put(General.DATE_TYPE, "3m");

        if (!date.equals("") && !end_date.equals("")) {
            requestMap.put(General.DATE_TYPE, date);
//            requestMap.put(General.START_DATE, date);
            requestMap.put(General.END, end_date);
        } else if (lastWeekly.equals("w")) {
            requestMap.put(General.DATE_TYPE, lastWeekly);
        } else if (lastMonthly.equals("m")) {
            requestMap.put(General.DATE_TYPE, lastMonthly);
        } else if (lastQuarterly.equals("3m")) {
            requestMap.put(General.DATE_TYPE, lastQuarterly);
        } else if (lastYearly.equals("y")) {
            requestMap.put(General.DATE_TYPE, lastYearly);
        } else {
            requestMap.put(General.DATE_TYPE, "3m");
        }

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext, mContext);
        if (requestBody != null) {
            try {
                Request request_new = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .tag(TAG)
                        .build();
                url = url + "?" + MakeCall.bodyToString(request_new);

                Log.e(TAG, "Url -> " + url);
                mWebAppointmentSatisSurvey.loadUrl(url);
//                LoadAppointmentStatusGraphs();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void LoadAppointmentStatusGraphs() {
        mWebAppointmentStatus.getSettings().setLoadWithOverviewMode(true);
        mWebAppointmentStatus.setInitialScale(85);
        mWebAppointmentStatus.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebAppointmentStatus.setScrollbarFadingEnabled(false);

        mWebAppointmentStatus.setWebChromeClient(new WebChromeClient());
        mWebAppointmentStatus.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebAppointmentStatus.getSettings().setLoadsImagesAutomatically(true);
        mWebAppointmentStatus.getSettings().setJavaScriptEnabled(true);
        mWebAppointmentStatus.getSettings().setDomStorageEnabled(true);
        mWebAppointmentStatus.getSettings().setUseWideViewPort(true);
        mWebAppointmentStatus.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebAppointmentStatus.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebAppointmentStatus.setVerticalScrollBarEnabled(false);
        mWebAppointmentStatus.setHorizontalScrollBarEnabled(false);
        mWebAppointmentStatus.getSettings().setLoadsImagesAutomatically(true);
        mWebAppointmentStatus.getSettings().setBuiltInZoomControls(true);
        mWebAppointmentStatus.getSettings().setUserAgentString("Android WebView");

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_APPOINTMENT_STATUS_REPORT);
        if (selectedClientId.equals("0")) {
            requestMap.put(General.YOUTH_ID, "All");
        } else {
            requestMap.put(General.YOUTH_ID, selectedClientId);
        }
        if (current_mode == MODE_STAFF) {

            if (selectedStaffId.equals("0")) {
                requestMap.put(General.STAFF_ID, "All");
            } else {
                requestMap.put(General.STAFF_ID, selectedStaffId);
            }
        } else {

            requestMap.put(General.STAFF_ID, "All");
        }
        requestMap.put(General.VIEW_MODE, String.valueOf(current_mode));
//        requestMap.put(General.DATE_TYPE, "3m");

        if (!date.equals("") && !end_date.equals("")) {
            requestMap.put(General.DATE_TYPE, date);
//            requestMap.put(General.START_DATE, date);
            requestMap.put(General.END, end_date);
        } else if (lastWeekly.equals("w")) {
            requestMap.put(General.DATE_TYPE, lastWeekly);
        } else if (lastMonthly.equals("m")) {
            requestMap.put(General.DATE_TYPE, lastMonthly);
        } else if (lastQuarterly.equals("3m")) {
            requestMap.put(General.DATE_TYPE, lastQuarterly);
        } else if (lastYearly.equals("y")) {
            requestMap.put(General.DATE_TYPE, lastYearly);
        } else {
            requestMap.put(General.DATE_TYPE, "3m");
        }

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext, mContext);
        if (requestBody != null) {
            try {
                Request request_new = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .tag(TAG)
                        .build();
                url = url + "?" + MakeCall.bodyToString(request_new);

                Log.e(TAG, "Url -> " + url);
                mWebAppointmentStatus.loadUrl(url);
//                LoadAppointmentAttendGraphs();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void LoadAppointmentAttendGraphs() {
        mWebAppointmentAttend.getSettings().setLoadWithOverviewMode(true);
        mWebAppointmentAttend.setInitialScale(85);
        mWebAppointmentAttend.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebAppointmentAttend.setScrollbarFadingEnabled(false);

        mWebAppointmentAttend.setWebChromeClient(new WebChromeClient());
        mWebAppointmentAttend.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebAppointmentAttend.getSettings().setLoadsImagesAutomatically(true);
        mWebAppointmentAttend.getSettings().setJavaScriptEnabled(true);
        mWebAppointmentAttend.getSettings().setDomStorageEnabled(true);
        mWebAppointmentAttend.getSettings().setUseWideViewPort(true);
        mWebAppointmentAttend.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebAppointmentAttend.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebAppointmentAttend.setVerticalScrollBarEnabled(false);
        mWebAppointmentAttend.setHorizontalScrollBarEnabled(false);
        mWebAppointmentAttend.getSettings().setLoadsImagesAutomatically(true);
        mWebAppointmentAttend.getSettings().setBuiltInZoomControls(true);
        mWebAppointmentAttend.getSettings().setUserAgentString("Android WebView");

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_APPOINTMENT_NO_SHOW_ATTEND_REPORT);
        if (selectedClientId.equals("0")) {
            requestMap.put(General.YOUTH_ID, "All");
        } else {
            requestMap.put(General.YOUTH_ID, selectedClientId);
        }
        if (current_mode == MODE_STAFF) {

            if (selectedStaffId.equals("0")) {
                requestMap.put(General.STAFF_ID, "All");
            } else {
                requestMap.put(General.STAFF_ID, selectedStaffId);
            }
        } else {

            requestMap.put(General.STAFF_ID, "All");
        }
        requestMap.put(General.VIEW_MODE, String.valueOf(current_mode));
//        requestMap.put(General.DATE_TYPE, "3m");

        if (!date.equals("") && !end_date.equals("")) {
            requestMap.put(General.DATE_TYPE, date);
//            requestMap.put(General.START_DATE, date);
            requestMap.put(General.END, end_date);
        } else if (lastWeekly.equals("w")) {
            requestMap.put(General.DATE_TYPE, lastWeekly);
        } else if (lastMonthly.equals("m")) {
            requestMap.put(General.DATE_TYPE, lastMonthly);
        } else if (lastQuarterly.equals("3m")) {
            requestMap.put(General.DATE_TYPE, lastQuarterly);
        } else if (lastYearly.equals("y")) {
            requestMap.put(General.DATE_TYPE, lastYearly);
        } else {
            requestMap.put(General.DATE_TYPE, "3m");
        }

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext, mContext);
        if (requestBody != null) {
            try {
                Request request_new = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .tag(TAG)
                        .build();
                url = url + "?" + MakeCall.bodyToString(request_new);

                Log.e(TAG, "Url -> " + url);
                mWebAppointmentAttend.loadUrl(url);
//                LoadAppointmentSatisSurveyGraphs();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void LoadAppointmentSatisfcatonSurveyProgressGraphs() {
        mWebSatisfactionSurProgress.getSettings().setLoadWithOverviewMode(true);
        mWebSatisfactionSurProgress.setInitialScale(85);
        mWebSatisfactionSurProgress.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebSatisfactionSurProgress.setScrollbarFadingEnabled(false);

        mWebSatisfactionSurProgress.setWebChromeClient(new WebChromeClient());
        mWebSatisfactionSurProgress.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebSatisfactionSurProgress.getSettings().setLoadsImagesAutomatically(true);
        mWebSatisfactionSurProgress.getSettings().setJavaScriptEnabled(true);
        mWebSatisfactionSurProgress.getSettings().setDomStorageEnabled(true);
        mWebSatisfactionSurProgress.getSettings().setUseWideViewPort(true);
        mWebSatisfactionSurProgress.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebSatisfactionSurProgress.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebSatisfactionSurProgress.setVerticalScrollBarEnabled(false);
        mWebSatisfactionSurProgress.setHorizontalScrollBarEnabled(false);
        mWebSatisfactionSurProgress.getSettings().setLoadsImagesAutomatically(true);
        mWebSatisfactionSurProgress.getSettings().setBuiltInZoomControls(true);
        mWebSatisfactionSurProgress.getSettings().setUserAgentString("Android WebView");

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_APPOINTMENT_SATISFACTION_SURVEY_PROGRESS_REPORT);
        if (selectedClientId.equals("0")) {
            requestMap.put(General.YOUTH_ID, "All");
        } else {
            requestMap.put(General.YOUTH_ID, selectedClientId);
        }
        if (current_mode == MODE_STAFF) {

            if (selectedStaffId.equals("0")) {
                requestMap.put(General.STAFF_ID, "All");
            } else {
                requestMap.put(General.STAFF_ID, selectedStaffId);
            }
        } else {
            requestMap.put(General.STAFF_ID, "All");

        }
        requestMap.put(General.SERVICE_ID, selectedSatisSurProgID);
        requestMap.put(General.VIEW_MODE, String.valueOf(current_mode));
//        requestMap.put(General.DATE_TYPE, "3m");

        if (!date.equals("") && !end_date.equals("")) {
            requestMap.put(General.DATE_TYPE, date);
//            requestMap.put(General.START_DATE, date);
            requestMap.put(General.END, end_date);
        } else if (lastWeekly.equals("w")) {
            requestMap.put(General.DATE_TYPE, lastWeekly);
        } else if (lastMonthly.equals("m")) {
            requestMap.put(General.DATE_TYPE, lastMonthly);
        } else if (lastQuarterly.equals("3m")) {
            requestMap.put(General.DATE_TYPE, lastQuarterly);
        } else if (lastYearly.equals("y")) {
            requestMap.put(General.DATE_TYPE, lastYearly);
        } else {
            requestMap.put(General.DATE_TYPE, "3m");
        }

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext, mContext);
        if (requestBody != null) {
            try {
                Request request_new = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .tag(TAG)
                        .build();
                url = url + "?" + MakeCall.bodyToString(request_new);

                Log.e(TAG, "Url -> " + url);
                mWebSatisfactionSurProgress.loadUrl(url);
//                clearData();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //===== Load Grpahs for all section END =====//
}
