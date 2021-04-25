package com.modules.reports;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.modules.postcard.DividerItemDecoration;
import com.sagesurfer.adapters.DeviceBaseDataListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.models.Consumers_;
import com.sagesurfer.models.PURDeviceData_;
import com.sagesurfer.models.PURDeviceStatistics_;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.ReportsParser_;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Monika on 8/1/2018.
 */

public class PlatformUsageReportFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = PlatformUsageReportFragment.class.getSimpleName();

    @BindView(R.id.spinner_user)
    Spinner spinnerUser;
    @BindView(R.id.imagebutton_filter)
    ImageButton imageButtonFilter;
    @BindView(R.id.linearlayout_platformusagereport)
    LinearLayout linearLayoutPlatformUsageReport;
    @BindView(R.id.webview_user_activity)
    WebView webViewUserActivity;
    @BindView(R.id.linearlayout_user_statastics)
    LinearLayout linearLayoutUserStatastics;
    @BindView(R.id.textview_total_mobile_usage)
    TextView textViewTotalMobileUsage;
    @BindView(R.id.textview_daily_time_on_platform)
    TextView textViewDailyTimeOnPlatform;
    @BindView(R.id.textview_avg_time_spent)
    TextView textViewAvgTimeSpent;
    @BindView(R.id.linearlayout_table)
    LinearLayout linearLayoutTable;
    @BindView(R.id.spinner_table)
    Spinner spinnerTable;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.linearlayout_total_number_of_users)
    LinearLayout linearLayoutTotalNumberOfUsers;
    @BindView(R.id.webview_total_number_of_users)
    WebView webViewTotalNumberOfUsers;
    @BindView(R.id.webview_age_distribution)
    WebView webViewAgeDistribution;
    @BindView(R.id.linearlayout_new_user)
    LinearLayout linearLayoutNewUser;
    @BindView(R.id.webview_new_user)
    WebView webViewNewUser;
    @BindView(R.id.linearlayout_active_user)
    LinearLayout linearLayoutActiveUser;
    @BindView(R.id.webview_active_user)
    WebView webViewActiveUser;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private Unbinder unbinder;

    ArrayList<Consumers_> consumerArrayList = new ArrayList<>();
    ArrayList<PURDeviceStatistics_> deviceStatisticsArrayList = new ArrayList<>();
    ArrayList<PURDeviceData_> deviceDataArrayList = new ArrayList<>();
    DeviceBaseDataListAdapter deviceBaseDataListAdapter;
    boolean fromDeviceStats = true;
    int totalPageCount = -1;
    ArrayList<String> pagesList = new ArrayList<String>();
    private LinearLayout errorLayout;
    private ArrayList<Consumers_> tempConsumerArrayList;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @Nullable
    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.fragment_platform_usage_report, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();

        Preferences.save(General.PAGE, "1");
        Preferences.save(General.CONSUMER_ID, "");
        Preferences.save(General.START, "y");
        Preferences.save(General.END, "0");

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);
        progressBar.setVisibility(View.VISIBLE);
        imageButtonFilter.setOnClickListener(this);
        fetchAllConsumers();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.platform_usage_report));
        mainActivityInterface.setToolbarBackgroundColor();
    }

    private final AdapterView.OnItemSelectedListener onItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerUser.setSelection(position);
            totalPageCount = -1;
            if (position == 0) {
                Preferences.save(General.CONSUMER_ID, "");
                linearLayoutPlatformUsageReport.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            } else if (position == 1) {
                Preferences.save(General.CONSUMER_ID, "All");
                linearLayoutPlatformUsageReport.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                fetchUserActivityGraph();
            } else {
                Preferences.save(General.CONSUMER_ID, consumerArrayList.get(position).getUser_id());
                linearLayoutPlatformUsageReport.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                fetchUserActivityGraph();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final AdapterView.OnItemSelectedListener onPageSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerTable.setSelection(position);
            Preferences.save(General.PAGE, position + 1);
            if (!fromDeviceStats) {
                fetchDeviceBaseData();
            } else {
                fromDeviceStats = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imagebutton_filter:
                openFilterDialog();
                break;
        }
    }

    private void openFilterDialog() {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_platform_usage_filter);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final TextView textViewWeekly = (TextView) dialog.findViewById(R.id.textview_weekly);
        final TextView textViewMonthly = (TextView) dialog.findViewById(R.id.textview_monthly);
        final TextView textViewQuarterly = (TextView) dialog.findViewById(R.id.textview_quarterly);
        final TextView textViewYearly = (TextView) dialog.findViewById(R.id.textview_yearly);
        final TextView textViewStartDate = (TextView) dialog.findViewById(R.id.textview_startdate);
        final TextView textViewEndDate = (TextView) dialog.findViewById(R.id.textview_enddate);
        TextView buttonApply = (TextView) dialog.findViewById(R.id.button_apply);

        final String start_date = GetTime.todaysDate();

        textViewWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.START, "w");
            }
        });

        textViewMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.START, "m");
            }
        });

        textViewQuarterly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.START, "3m");
            }
        });

        textViewYearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.START, "y");
            }
        });

        textViewStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewEndDate.setText("");
                textViewEndDate.setHint(activity.getApplicationContext().getResources().getString(R.string.end_date));
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                int sYear, sMonth, sDay;
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                String start_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                textViewStartDate.setText(start_date);
                                Preferences.save(General.START, start_date);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        textViewEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                final int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                final int mDay = c.get(Calendar.DAY_OF_MONTH);
                if (start_date.length() <= 0) {
                    ShowToast.toast("Select Start Date First", activity.getApplicationContext());
                    return;
                }
                final DatePickerDialog endDate = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                int sYear, sMonth, sDay;
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;
                                String end_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                try {
                                    int result = Compare.startDate(mYear + "-" + (mMonth + 1) + "-" + mDay, end_date);
                                    if (result == 1) {
                                        end_date = "";
                                        textViewEndDate.setText(end_date);
                                        textViewEndDate.setHint(activity.getApplicationContext().getResources().getString(R.string.end_date));
                                        ShowToast.toast("Invalid Date", activity.getApplicationContext());
                                    } else {
                                        textViewEndDate.setText(end_date);
                                        Preferences.save(General.END, end_date);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                endDate.show();
            }
        });

        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!Preferences.get(General.CONSUMER_ID).equalsIgnoreCase("")) {
                    fetchUserActivityGraph();
                }
            }
        });

        dialog.show();
    }

    private void fetchAllConsumers() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_CONSUMER_ANALYTICS);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.START, "0");
        requestMap.put(General.LIMIT, "30");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.PLATFORM_REPORTS_MOBILE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                boolean isFrancescaLoggedIn = false;
                int consumerArrayListPosition = 0;
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    consumerArrayList = new ArrayList<>();
                    Consumers_ consumers = new Consumers_();
                    consumers.setUser_id(0);

                    if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))) {
                        consumers.setName("Choose Peer Participant");
                    } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
                        consumers.setName("Choose Guest");
                    } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
                        consumers.setName("Choose Student");
                    } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) {
                        consumers.setName("Choose Client");
                    } else {
                        if (Preferences.get(General.USER_ID).equalsIgnoreCase("853")) {
                            isFrancescaLoggedIn = true;
                        }
                        consumers.setName("Choose Consumer");
                    }
                    consumers.setStatus(1);
                    consumerArrayList.add(consumers);
                    consumers = new Consumers_();
                    consumers.setUser_id(1);

                    if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))) {
                        consumers.setName("All Peer Participant");
                    } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
                        consumers.setName("All Guests");
                    } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
                        consumers.setName("All Student");
                    } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) {
                        consumers.setName("All Client");
                    } else {
                        consumers.setName(" Consumers");
                    }

                    consumers.setStatus(1);
                    consumerArrayList.add(consumers);

                    tempConsumerArrayList = ReportsParser_.parseConsumers(response, Actions_.GET_CONSUMER_ANALYTICS, activity.getApplicationContext(), TAG);

                    if (tempConsumerArrayList.get(0).getStatus() == 2) {

                        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))) {
                            ShowToast.toast("No Peer Participant", activity);
                        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
                            ShowToast.toast("No Guest", activity);
                        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
                            ShowToast.toast("No Student", activity);
                        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) {
                            ShowToast.toast("No Client", activity);
                        } else {
                            ShowToast.toast("No Consumer", activity);
                        }

                        spinnerUser.setEnabled(false);
                    }

                    if (tempConsumerArrayList.size() > 0) {
                        consumerArrayList.addAll(tempConsumerArrayList);
                    } else {
                        linearLayoutPlatformUsageReport.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    }

                    ArrayList<String> consumerNameList = new ArrayList<String>();
                    for (int i = 0; i < consumerArrayList.size(); i++) {
                        if (isFrancescaLoggedIn && consumerArrayList.get(i).getUser_id() == 1976) {
                            consumerArrayListPosition = i;
                        }
                        consumerNameList.add(consumerArrayList.get(i).getName());
                    }
                    ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, consumerNameList);
                    adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);

                    spinnerUser.setAdapter(adapterConsumer);
                    spinnerUser.setOnItemSelectedListener(onItemSelected);
                    spinnerUser.setSelection(consumerArrayListPosition);
                }
                 /*else {
                    fetchUserActivityGraph();
                }*/

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //make network call get Number of Missed Dosages graph from server
    private void fetchUserActivityGraph() {
        int status = 11;

        webViewUserActivity.getSettings().setLoadWithOverviewMode(true);
        webViewUserActivity.setInitialScale(140);
        webViewUserActivity.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewUserActivity.setScrollbarFadingEnabled(false);

        webViewUserActivity.setWebChromeClient(new WebChromeClient());
        webViewUserActivity.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewUserActivity.getSettings().setLoadsImagesAutomatically(true);
        webViewUserActivity.getSettings().setJavaScriptEnabled(true);
        webViewUserActivity.getSettings().setDomStorageEnabled(true);
        webViewUserActivity.getSettings().setUseWideViewPort(true);
        webViewUserActivity.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewUserActivity.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewUserActivity.setVerticalScrollBarEnabled(false);
        webViewUserActivity.setHorizontalScrollBarEnabled(false);
        webViewUserActivity.getSettings().setLoadsImagesAutomatically(true);
        webViewUserActivity.getSettings().setBuiltInZoomControls(true);
        webViewUserActivity.getSettings().setUserAgentString("Android WebView");

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PLATFORM_USAGE_CHART);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.START, Preferences.get(General.START));
        requestMap.put(General.END, Preferences.get(General.END));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.REPORT, General.USER_ACTIVITY);

        String url = Preferences.get(General.DOMAIN) + Urls_.PLATFORM_REPORTS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                Request request_new = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .tag(TAG)
                        .build();
                url = url + "?" + MakeCall.bodyToString(request_new);

                Log.e(TAG, "Url -> " + url);
                webViewUserActivity.loadUrl(url);
                fetchDeviceStatistics();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchDeviceStatistics() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PLATFORM_USAGE_CHART);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.START, Preferences.get(General.START));
        requestMap.put(General.END, Preferences.get(General.END));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.REPORT, General.DEVICE_BASE_STATISTICS);

        String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.AJAX_PLATFORM_USAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    deviceStatisticsArrayList = ReportsParser_.parseDeviceStatistics(response, General.DEVICE_STAT, activity.getApplicationContext(), TAG);
                    if (deviceStatisticsArrayList.size() > 0) {
                        if (deviceStatisticsArrayList.get(0).getTot_mob_usage() == 0
                                && deviceStatisticsArrayList.get(0).getDaily_time_platform() == 0
                                && deviceStatisticsArrayList.get(0).getAvg_time_team() == 0) {
                            linearLayoutUserStatastics.setVisibility(View.GONE);
                        } else {
                            linearLayoutUserStatastics.setVisibility(View.VISIBLE);
                            textViewTotalMobileUsage.setText(String.valueOf(deviceStatisticsArrayList.get(0).getTot_mob_usage()));
                            textViewDailyTimeOnPlatform.setText(String.valueOf(deviceStatisticsArrayList.get(0).getDaily_time_platform()));
                            textViewAvgTimeSpent.setText(String.valueOf(deviceStatisticsArrayList.get(0).getAvg_time_team()));
                        }
                    }
                } else {
                    linearLayoutUserStatastics.setVisibility(View.GONE);
                }
                fromDeviceStats = true;
                fetchDeviceBaseData();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchDeviceBaseData() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PLATFORM_USAGE_CHART);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.START, Preferences.get(General.START));
        requestMap.put(General.END, Preferences.get(General.END));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.REPORT, General.DEVICE_BASE_DATA);
        requestMap.put(General.PAGE, Preferences.get(General.PAGE));
        requestMap.put(General.LIMIT, "5");
//		{"start":0,"status":1,"total_pages":0,"total_rec":0,"device_base_data":[]}
        String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.AJAX_PLATFORM_USAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    pagesList = new ArrayList<String>();
                    deviceDataArrayList = new ArrayList<>();
                    PURDeviceData_ deviceData = new PURDeviceData_();
                    deviceData.setAct_name("Activity");
                    deviceData.setDevice("Device");
                    deviceDataArrayList.add(deviceData);
                    deviceDataArrayList.addAll(ReportsParser_.parseDeviceData(response, General.DEVICE_BASE_DATA, activity.getApplicationContext(), TAG));
                    JSONObject jsonObject = new JSONObject(response);
                    int pageCount = 0;
                    if (jsonObject.has("total_pages")) {
                        pageCount = jsonObject.getInt("total_pages");
                        if (totalPageCount != pageCount) {
                            fromDeviceStats = true;
                            totalPageCount = jsonObject.getInt("total_pages");
                            for (int i = 1; i <= totalPageCount; i++) {
                                pagesList.add("Page " + i);
                            }
                        }
                    }
                    if (pageCount != 0) {
                        //if (deviceDataArrayList.size() > 0) {
                        linearLayoutTable.setVisibility(View.VISIBLE);
                        if (fromDeviceStats) {
                            ArrayAdapter<String> adapterPages = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, pagesList);
                            adapterPages.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                            spinnerTable.setAdapter(adapterPages);
                            spinnerTable.setOnItemSelectedListener(onPageSelected);
                        }

                        deviceBaseDataListAdapter = new DeviceBaseDataListAdapter(activity.getApplicationContext(), deviceDataArrayList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(new DividerItemDecoration(activity.getApplicationContext(), LinearLayoutManager.VERTICAL));
                        recyclerView.setAdapter(deviceBaseDataListAdapter);
                        //}
                    } else {
                        linearLayoutTable.setVisibility(View.GONE);
                    }
                } else {
                    linearLayoutTable.setVisibility(View.GONE);
                }
                if (Preferences.get(General.CONSUMER_ID).equalsIgnoreCase("All")) {
                    linearLayoutTotalNumberOfUsers.setVisibility(View.GONE);
                    linearLayoutActiveUser.setVisibility(View.VISIBLE);
                    linearLayoutNewUser.setVisibility(View.VISIBLE);
                    fetchAgeDistributionGraph();
                } else {
                    linearLayoutTotalNumberOfUsers.setVisibility(View.VISIBLE);
                    linearLayoutActiveUser.setVisibility(View.GONE);
                    linearLayoutNewUser.setVisibility(View.GONE);
                    fetchTotalNumberOfUsersGraph();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchTotalNumberOfUsersGraph() {
        int status = 11;
        webViewTotalNumberOfUsers.getSettings().setLoadWithOverviewMode(true);
        webViewTotalNumberOfUsers.setInitialScale(70);
        webViewTotalNumberOfUsers.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewTotalNumberOfUsers.setScrollbarFadingEnabled(false);

        webViewTotalNumberOfUsers.setWebChromeClient(new WebChromeClient());
        webViewTotalNumberOfUsers.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewTotalNumberOfUsers.getSettings().setLoadsImagesAutomatically(true);
        webViewTotalNumberOfUsers.getSettings().setJavaScriptEnabled(true);
        webViewTotalNumberOfUsers.getSettings().setDomStorageEnabled(true);
        webViewTotalNumberOfUsers.getSettings().setUseWideViewPort(true);
        webViewTotalNumberOfUsers.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewTotalNumberOfUsers.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewTotalNumberOfUsers.setVerticalScrollBarEnabled(false);
        webViewTotalNumberOfUsers.setHorizontalScrollBarEnabled(false);
        webViewTotalNumberOfUsers.getSettings().setLoadsImagesAutomatically(true);
        webViewTotalNumberOfUsers.getSettings().setBuiltInZoomControls(true);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PLATFORM_USAGE_CHART);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.START, Preferences.get(General.START));
        requestMap.put(General.END, Preferences.get(General.END));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.REPORT, General.TOTAL_NO_USER);

        String url = Preferences.get(General.DOMAIN) + Urls_.PLATFORM_REPORTS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                Request request_new = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .tag(TAG)
                        .build();
                url = url + "?" + MakeCall.bodyToString(request_new);

                Log.e(TAG, "Url -> " + url);
                webViewTotalNumberOfUsers.loadUrl(url);

                fetchAgeDistributionGraph();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //make network call get Number of Missed Dosages graph from server
    private void fetchAgeDistributionGraph() {
        int status = 11;
        webViewAgeDistribution.getSettings().setLoadWithOverviewMode(true);
        webViewAgeDistribution.setInitialScale(70);
        webViewAgeDistribution.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewAgeDistribution.setScrollbarFadingEnabled(false);

        webViewAgeDistribution.setWebChromeClient(new WebChromeClient());
        webViewAgeDistribution.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewAgeDistribution.getSettings().setLoadsImagesAutomatically(true);
        webViewAgeDistribution.getSettings().setJavaScriptEnabled(true);
        webViewAgeDistribution.getSettings().setDomStorageEnabled(true);
        webViewAgeDistribution.getSettings().setUseWideViewPort(true);
        webViewAgeDistribution.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewAgeDistribution.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewAgeDistribution.setVerticalScrollBarEnabled(false);
        webViewAgeDistribution.setHorizontalScrollBarEnabled(false);
        webViewAgeDistribution.getSettings().setLoadsImagesAutomatically(true);
        webViewAgeDistribution.getSettings().setBuiltInZoomControls(true);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PLATFORM_USAGE_CHART);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.START, Preferences.get(General.START));
        requestMap.put(General.END, Preferences.get(General.END));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.REPORT, General.AGE_DISTRIBUTION);

        String url = Preferences.get(General.DOMAIN) + Urls_.PLATFORM_REPORTS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                Request request_new = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .tag(TAG)
                        .build();
                url = url + "?" + MakeCall.bodyToString(request_new);

                Log.e(TAG, "Url -> " + url);
                webViewAgeDistribution.loadUrl(url);

                fetchNewUserActivity();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //make network call get Number of Missed Dosages graph from server
    private void fetchNewUserActivity() {
        int status = 11;
        webViewNewUser.getSettings().setLoadWithOverviewMode(true);
        webViewNewUser.setInitialScale(70);
        webViewNewUser.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewNewUser.setScrollbarFadingEnabled(false);

        webViewNewUser.setWebChromeClient(new WebChromeClient());
        webViewNewUser.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewNewUser.getSettings().setLoadsImagesAutomatically(true);
        webViewNewUser.getSettings().setJavaScriptEnabled(true);
        webViewNewUser.getSettings().setDomStorageEnabled(true);
        webViewNewUser.getSettings().setUseWideViewPort(true);
        webViewNewUser.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewNewUser.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewNewUser.setVerticalScrollBarEnabled(false);
        webViewNewUser.setHorizontalScrollBarEnabled(false);
        webViewNewUser.getSettings().setLoadsImagesAutomatically(true);
        webViewNewUser.getSettings().setBuiltInZoomControls(true);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PLATFORM_USAGE_CHART);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.START, Preferences.get(General.START));
        requestMap.put(General.END, Preferences.get(General.END));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.REPORT, General.NEW_USER);

        String url = Preferences.get(General.DOMAIN) + Urls_.PLATFORM_REPORTS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                Request request_new = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .tag(TAG)
                        .build();
                url = url + "?" + MakeCall.bodyToString(request_new);

                Log.e(TAG, "Url -> " + url);
                webViewNewUser.loadUrl(url);

                fetchActiveUserGraph();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //make network call get Number of Missed Dosages graph from server
    private void fetchActiveUserGraph() {
        int status = 11;
        webViewActiveUser.getSettings().setLoadWithOverviewMode(true);
        webViewActiveUser.setInitialScale(70);
        webViewActiveUser.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewActiveUser.setScrollbarFadingEnabled(false);

        webViewActiveUser.setWebChromeClient(new WebChromeClient());
        webViewActiveUser.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity.getApplicationContext(), "Error:" + description, Toast.LENGTH_SHORT).show();

            }
        });
        webViewActiveUser.getSettings().setLoadsImagesAutomatically(true);
        webViewActiveUser.getSettings().setJavaScriptEnabled(true);
        webViewActiveUser.getSettings().setDomStorageEnabled(true);
        webViewActiveUser.getSettings().setUseWideViewPort(true);
        webViewActiveUser.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewActiveUser.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewActiveUser.setVerticalScrollBarEnabled(false);
        webViewActiveUser.setHorizontalScrollBarEnabled(false);
        webViewActiveUser.getSettings().setLoadsImagesAutomatically(true);
        webViewActiveUser.getSettings().setBuiltInZoomControls(true);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PLATFORM_USAGE_CHART);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.START, Preferences.get(General.START));
        requestMap.put(General.END, Preferences.get(General.END));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.REPORT, General.ACTIVE_USER);

        String url = Preferences.get(General.DOMAIN) + Urls_.PLATFORM_REPORTS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                Request request_new = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .tag(TAG)
                        .build();
                url = url + "?" + MakeCall.bodyToString(request_new);

                Log.e(TAG, "Url -> " + url);
                webViewActiveUser.loadUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
