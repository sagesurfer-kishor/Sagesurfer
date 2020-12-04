package com.modules.reports;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
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

import com.modules.assessment.FormShowActivity;
import com.modules.postcard.DividerItemDecoration;
import com.sagesurfer.adapters.PORActivityDataListAdapter;
import com.sagesurfer.adapters.PORDocumentDataListAdapter;
import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.models.Consumers_;
import com.sagesurfer.models.PORActivityData_;
import com.sagesurfer.models.PORDocumentData_;
import com.sagesurfer.models.Teams_;
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
 * Created by Monika on 8/7/2018.
 */

public class ProcessOutcomeReportFragment extends Fragment implements View.OnClickListener, PORDocumentDataListAdapter.PORDocumentDataListAdapterListener {
    private static final String TAG = ProcessOutcomeReportFragment.class.getSimpleName();

    /*@BindView(R.id.spinner_user)
    Spinner spinnerUser;
    @BindView(R.id.imagebutton_filter)
    ImageButton imageButtonFilter;*/
    @BindView(R.id.linearlayout_activity)
    LinearLayout linearLayoutActivity;
    @BindView(R.id.webview_activity)
    WebView webViewActivity;
    @BindView(R.id.linearlayout_user_statastics)
    LinearLayout linearLayoutUserStatastics;
    @BindView(R.id.textview_total_strength)
    TextView textViewTotalStrength;
    @BindView(R.id.textview_avg_strength_per_team)
    TextView textViewAvgStrengthPerTeam;
    @BindView(R.id.textview_avg_team_size)
    TextView textViewAvgTeamSize;
    @BindView(R.id.linearlayout_table)
    LinearLayout linearLayoutTable;
    @BindView(R.id.spinner_table)
    Spinner spinnerTable;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.linearlayout_total_number_of_strength)
    LinearLayout linearLayoutTotalNumberOfStrength;
    @BindView(R.id.webview_total_number_of_strength)
    WebView webViewTotalNumberOfStrength;
    @BindView(R.id.webview_natural_support_engaged_per_team)
    WebView webViewNaturalSupportEngagedPerTeam;
    @BindView(R.id.spinner_document_activity)
    Spinner spinnerDocumentActivity;
    @BindView(R.id.recycler_view_document_activity)
    RecyclerView recyclerViewDocumentActivity;
    @BindView(R.id.textview_document_activity)
    TextView textViewDocumentActivity;
    @BindView(R.id.spinner_team)
    Spinner spinneTeam;
    @BindView(R.id.webview_no_of_users)
    WebView webViewNoOfUsers;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private Unbinder unbinder;

    ArrayList<PORActivityData_> activityDataArrayList = new ArrayList<>();
    ArrayList<PORDocumentData_> documentActivityArrayList = new ArrayList<>();
    ArrayList<PORDocumentData_> documentDataArrayList = new ArrayList<>();
    ArrayList<Teams_> teamsArrayList = new ArrayList<>();
    PORActivityDataListAdapter activityDataListAdapter;
    PORDocumentDataListAdapter documentDataListAdapter;
    boolean fromDeviceStats = true;
    int totalPageCount = -1;
    ArrayList<String> pagesList = new ArrayList<String>();

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

        View view = inflater.inflate(R.layout.fragment_process_outcome_report, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();

        Preferences.save(General.PAGE, "1");
        Preferences.save(General.CONSUMER_ID, "All");
        Preferences.save(General.START, "y");
        Preferences.save(General.END, "");

        progressBar.setVisibility(View.VISIBLE);
        if(Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage013") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage015") ||
         Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage022")) {
            linearLayoutActivity.setVisibility(View.GONE);
            linearLayoutTotalNumberOfStrength.setVisibility(View.GONE);
        } else {
            fetchUserActivityGraph();
        }
        fetchDocumentActivityList();
        fetchTeams();

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.process_outcomes_reports));
        mainActivityInterface.setToolbarBackgroundColor();

    }

    private final AdapterView.OnItemSelectedListener onPageSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerTable.setSelection(position);
            Preferences.save(General.PAGE, position+1);
            if(!fromDeviceStats) {
                fetchStatistics();
            } else {
                fromDeviceStats = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final AdapterView.OnItemSelectedListener onDocumentActivitySelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerDocumentActivity.setSelection(position);
            if(position != 0) {
                recyclerViewDocumentActivity.setVisibility(View.VISIBLE);
                textViewDocumentActivity.setVisibility(View.GONE);
                fetchDocumentActivityData(position);
            } else {
                recyclerViewDocumentActivity.setVisibility(View.GONE);
                textViewDocumentActivity.setVisibility(View.GONE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final AdapterView.OnItemSelectedListener onTeamSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinneTeam.setSelection(position);
            if(position != 0) {
                webViewNoOfUsers.setVisibility(View.VISIBLE);
                Preferences.save(General.GROUP_ID, teamsArrayList.get(position).getId());
                fetchNoOfUsersGraph();
            } else {
                webViewNoOfUsers.setVisibility(View.GONE);
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
                fetchUserActivityGraph();
            }
        });

        dialog.show();
    }

    //make network call get Number of Missed Dosages graph from server
    private void fetchUserActivityGraph() {
        int status = 11;
        webViewActivity.getSettings().setLoadWithOverviewMode(true);
        webViewActivity.setInitialScale(70);
        webViewActivity.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewActivity.setScrollbarFadingEnabled(false);

        webViewActivity.setWebChromeClient(new WebChromeClient());
        webViewActivity.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewActivity.getSettings().setLoadsImagesAutomatically(true);
        webViewActivity.getSettings().setJavaScriptEnabled(true);
        webViewActivity.getSettings().setDomStorageEnabled(true);
        webViewActivity.getSettings().setUseWideViewPort(true);
        webViewActivity.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewActivity.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewActivity.setVerticalScrollBarEnabled(false);
        webViewActivity.setHorizontalScrollBarEnabled(false);
        webViewActivity.getSettings().setLoadsImagesAutomatically(true);
        webViewActivity.getSettings().setBuiltInZoomControls(true);
        webViewActivity.getSettings().setUserAgentString("Android WebView");

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROCESS_OUTCOME);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.START, Preferences.get(General.START));
        requestMap.put(General.END, Preferences.get(General.END));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.REPORT, General.ACTIVITY);

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
                webViewActivity.loadUrl(url);

                fromDeviceStats = true;
                fetchStatistics();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchStatistics() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROCESS_OUTCOME);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.START, Preferences.get(General.START));
        requestMap.put(General.END, Preferences.get(General.END));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.REPORT, General.MEM_STATISTICS);
        requestMap.put(General.PAGE, Preferences.get(General.PAGE));
        requestMap.put(General.LIMIT, "5");
        requestMap.put(General.NATIVE_ID, "1");

        String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.AJAX_OUTCOME_PROCESS_PREFETCH_MOBILE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    activityDataArrayList = new ArrayList<>();
                    PORActivityData_ deviceData = new PORActivityData_();
                    deviceData.setGroup_name("Team");
                    deviceData.setMember_count(0);
                    deviceData.setCount(0);
                    activityDataArrayList.add(deviceData);
                    activityDataArrayList.addAll(ReportsParser_.parseActivityData(response, General.MEM_STATISTICS, activity.getApplicationContext(), TAG));
                    JSONObject jsonObject = new JSONObject(response);
                    int pageCount = 0;
                    if (jsonObject.has("total_pages")) {
                        pageCount = jsonObject.getInt("total_pages");
                        if(totalPageCount != pageCount) {
                            fromDeviceStats = true;
                            totalPageCount = jsonObject.getInt("total_pages");
                            for (int i = 1; i <= totalPageCount; i++) {
                                pagesList.add("Page " + i);
                            }
                        }
                    }
                    if (pageCount != 0) {
                        if(fromDeviceStats) {
                            ArrayAdapter<String> adapterPages = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, pagesList);
                            adapterPages.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                            spinnerTable.setAdapter(adapterPages);
                            spinnerTable.setOnItemSelectedListener(onPageSelected);
                        }

                        activityDataListAdapter = new PORActivityDataListAdapter(activity.getApplicationContext(), activityDataArrayList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(new DividerItemDecoration(activity.getApplicationContext(), LinearLayoutManager.VERTICAL));
                        recyclerView.setAdapter(activityDataListAdapter);
                    } else {
                        linearLayoutTable.setVisibility(View.GONE);
                    }
                } else {
                    linearLayoutTable.setVisibility(View.GONE);
                }
                fetchTotalNumberOfStrengthGraph();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchTotalNumberOfStrengthGraph() {
        int status = 11;
        webViewTotalNumberOfStrength.getSettings().setLoadWithOverviewMode(true);
        webViewTotalNumberOfStrength.setInitialScale(70);
        webViewTotalNumberOfStrength.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewTotalNumberOfStrength.setScrollbarFadingEnabled(false);

        webViewTotalNumberOfStrength.setWebChromeClient(new WebChromeClient());
        webViewTotalNumberOfStrength.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewTotalNumberOfStrength.getSettings().setLoadsImagesAutomatically(true);
        webViewTotalNumberOfStrength.getSettings().setJavaScriptEnabled(true);
        webViewTotalNumberOfStrength.getSettings().setDomStorageEnabled(true);
        webViewTotalNumberOfStrength.getSettings().setUseWideViewPort(true);
        webViewTotalNumberOfStrength.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewTotalNumberOfStrength.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewTotalNumberOfStrength.setVerticalScrollBarEnabled(false);
        webViewTotalNumberOfStrength.setHorizontalScrollBarEnabled(false);
        webViewTotalNumberOfStrength.getSettings().setLoadsImagesAutomatically(true);
        webViewTotalNumberOfStrength.getSettings().setBuiltInZoomControls(true);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROCESS_OUTCOME);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.START, Preferences.get(General.START));
        requestMap.put(General.END, Preferences.get(General.END));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.REPORT, General.NO_OF_STRENGTH);

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
                webViewTotalNumberOfStrength.loadUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchDocumentActivityList() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROCESS_OUTCOME);
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.REPORT, General.DOCS_ACTIVITY_LIST);

        String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.AJAX_OUTCOME_PROCESS_ACTIVITY_MOBILE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    documentActivityArrayList = new ArrayList<>();
                    PORDocumentData_ deviceData = new PORDocumentData_();
                    deviceData.setKey("0");
                    deviceData.setValue("Select Document Activity");
                    documentActivityArrayList.add(deviceData);
                    ArrayList<PORDocumentData_> tempDocumentActivityArrayList = ReportsParser_.parseDocumentData(response, General.ACTIVITY_LIST, activity.getApplicationContext(), TAG);
                    if (tempDocumentActivityArrayList.size() > 0) {
                        documentActivityArrayList.addAll(tempDocumentActivityArrayList);
                    }

                    ArrayList<String> documentNameList = new ArrayList<String>();
                    for (int i = 0; i < documentActivityArrayList.size(); i++) {
                        documentNameList.add(documentActivityArrayList.get(i).getValue());
                    }
                    if (documentActivityArrayList.size() > 0) {
                        ArrayAdapter<String> adapterDocumentActivity = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, documentNameList);
                        adapterDocumentActivity.setDropDownViewResource(R.layout.drop_down_text_item_layout);

                        spinnerDocumentActivity.setAdapter(adapterDocumentActivity);
                        spinnerDocumentActivity.setOnItemSelectedListener(onDocumentActivitySelected);
                        spinnerDocumentActivity.setSelection(0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchDocumentActivityData(int position) {
        String activity_id = documentActivityArrayList.get(position).getKey();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_ACTIVITY);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.START, Preferences.get(General.START));
        requestMap.put(General.END, Preferences.get(General.END));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.ACTIVITY_ID, activity_id);
        requestMap.put(General.REPORT, General.DOCS_ACTIVITY_DATA);

        String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.AJAX_OUTCOME_PROCESS_ACTIVITY_MOBILE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    documentDataArrayList = new ArrayList<>();
                    PORDocumentData_ documentData = new PORDocumentData_();
                    documentData.setLable("Title");
                    documentDataArrayList.add(documentData);
                    ArrayList<PORDocumentData_> tempDocumentDataArrayList = ReportsParser_.parseDocumentData(response, General.API_DOCS_DATA, activity.getApplicationContext(), TAG);
                    if (tempDocumentDataArrayList.size() > 0) {
                        documentDataArrayList.addAll(tempDocumentDataArrayList);
                    }

                    if (documentDataArrayList.size() > 1) {
                        documentDataListAdapter = new PORDocumentDataListAdapter(activity.getApplicationContext(), documentDataArrayList, this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
                        recyclerViewDocumentActivity.setLayoutManager(mLayoutManager);
                        recyclerViewDocumentActivity.setItemAnimator(new DefaultItemAnimator());
                        recyclerViewDocumentActivity.addItemDecoration(new DividerItemDecoration(activity.getApplicationContext(), LinearLayoutManager.VERTICAL));
                        recyclerViewDocumentActivity.setAdapter(documentDataListAdapter);
                    } else {
                        recyclerViewDocumentActivity.setVisibility(View.GONE);
                        textViewDocumentActivity.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerViewDocumentActivity.setVisibility(View.GONE);
                    textViewDocumentActivity.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchTeams() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROCESS_OUTCOME);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.REPORT, General.TEAM_LIST);

        String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.AJAX_OUTCOME_PROCESS_ACTIVITY_MOBILE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    Teams_ team = new Teams_();
                    team.setId(0);
                    team.setName("Select Team");
                    team.setStatus(1);
                    teamsArrayList = new ArrayList<>();
                    teamsArrayList.add(team);
                    ArrayList<Teams_> tempTeamsArrayList = ReportsParser_.parseTeamList(response, General.TEAM_LIST, activity.getApplicationContext(), TAG);
                    if (tempTeamsArrayList.size() > 0) {
                        teamsArrayList.addAll(tempTeamsArrayList);
                    }

                    ArrayList<String> teamNameList = new ArrayList<String>();
                    for (int i = 0; i < teamsArrayList.size(); i++) {
                        teamNameList.add(teamsArrayList.get(i).getName());
                    }
                    if (teamsArrayList.size() > 0) {
                        ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, teamNameList);
                        adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                        spinneTeam.setAdapter(adapterConsumer);
                        spinneTeam.setOnItemSelectedListener(onTeamSelected);
                        spinneTeam.setSelection(0);
                    }
                }
                progressBar.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchNoOfUsersGraph() {
        webViewNoOfUsers.getSettings().setLoadWithOverviewMode(true);
        webViewNoOfUsers.setInitialScale(70);
        webViewNoOfUsers.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewNoOfUsers.setScrollbarFadingEnabled(false);

        webViewNoOfUsers.setWebChromeClient(new WebChromeClient());
        webViewNoOfUsers.setWebViewClient(new WebViewClient(){
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
        webViewNoOfUsers.getSettings().setLoadsImagesAutomatically(true);
        webViewNoOfUsers.getSettings().setJavaScriptEnabled(true);
        webViewNoOfUsers.getSettings().setDomStorageEnabled(true);
        webViewNoOfUsers.getSettings().setUseWideViewPort(true);
        webViewNoOfUsers.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewNoOfUsers.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewNoOfUsers.setVerticalScrollBarEnabled(false);
        webViewNoOfUsers.setHorizontalScrollBarEnabled(false);
        webViewNoOfUsers.getSettings().setLoadsImagesAutomatically(true);
        webViewNoOfUsers.getSettings().setBuiltInZoomControls(false);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROCESS_OUTCOME);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.START, Preferences.get(General.START));
        requestMap.put(General.END, Preferences.get(General.END));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        requestMap.put(General.REPORT, General.NO_OF_USER_NOTE);

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
                webViewNoOfUsers.loadUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCompareImageClicked(int position, PORDocumentData_ documentData_) {
        /*String url = fetchCompareGraph();
        Intent detailsIntent = new Intent(activity.getApplicationContext(), FormShowActivity.class);
        detailsIntent.putExtra(General.URL, url);
        detailsIntent.putExtra(General.LABLE, documentData_.getLable());
        startActivity(detailsIntent, ActivityTransition.moveToNextAnimation(activity.getApplicationContext()));*/
        String url = fetchCompareGraph(documentData_);
        DialogFragment dialogFrag = new CompareGraphDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(General.URL, url);
        bundle.putString(General.LABLE, documentData_.getLable());
        dialogFrag.setArguments(bundle);
        dialogFrag.show(activity.getFragmentManager().beginTransaction(), General.COMMENT_COUNT);
    }

    private String fetchCompareGraph(PORDocumentData_ documentData_) {
        /*webViewNoOfUsers.setWebChromeClient(new WebChromeClient());
        webViewNoOfUsers.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity.getApplicationContext(), "Error:" + description, Toast.LENGTH_SHORT).show();

            }
        });
        webViewNoOfUsers.getSettings().setLoadsImagesAutomatically(true);
        webViewNoOfUsers.getSettings().setJavaScriptEnabled(true);
        webViewNoOfUsers.getSettings().setDomStorageEnabled(true);
        webViewNoOfUsers.getSettings().setUseWideViewPort(true);
        webViewNoOfUsers.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewNoOfUsers.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewNoOfUsers.setVerticalScrollBarEnabled(false);
        webViewNoOfUsers.setHorizontalScrollBarEnabled(false);
        webViewNoOfUsers.getSettings().setLoadsImagesAutomatically(true);
        webViewNoOfUsers.getSettings().setBuiltInZoomControls(true);*/

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROCESS_OUTCOME);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.ACTIVITY_ID, "" + documentData_.getActivity_id());
        requestMap.put(General.LABEL, documentData_.getLable());
        requestMap.put(General.REPORT, General.COMPARISION_CHART);

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
                //webViewNoOfUsers.loadUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return url;
    }
}