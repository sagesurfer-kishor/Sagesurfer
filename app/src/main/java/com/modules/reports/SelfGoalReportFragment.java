package com.modules.reports;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Consumers_;
import com.sagesurfer.models.Goal_;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.ReportsParser_;
import com.sagesurfer.parser.SelfGoal_;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Monika on 8/3/2018.
 */

public class SelfGoalReportFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = SelfGoalReportFragment.class.getSimpleName();
    @BindView(R.id.linearlayout_selfgoals_spinner)
    LinearLayout linearLayoutSelfGoalsSpinner;
    @BindView(R.id.spinner_selfgoals_report)
    Spinner spinnerSelfGoalsReport;
    @BindView(R.id.linearlayout_consumer_spinner)
    LinearLayout linearLayoutConsumerSpinner;
    @BindView(R.id.spinner_consumers)
    Spinner spinnerConsumers;
    @BindView(R.id.linearlayout_self_goal_report)
    LinearLayout linearLayoutSelfGoalReport;
    @BindView(R.id.textview_label)
    TextView textViewLabel;
    @BindView(R.id.webview_goal_status)
    WebView webViewGoalStatus;
    @BindView(R.id.linearlayout_assessment)
    LinearLayout linearLayoutAssessment;
    @BindView(R.id.linearlayout_assessment_spinner)
    LinearLayout linearLayoutAssessmentSpinner;
    @BindView(R.id.spinner_assessment)
    Spinner spinnerAssessment;
    @BindView(R.id.linearlayout_assessment_answer_type)
    LinearLayout linearLayoutAssessmentAnswerType;
    @BindView(R.id.spinner_assessment_type)
    Spinner spinnerAssessmentType;
    @BindView(R.id.linearlayout_assessment_graph)
    LinearLayout linearLayoutAssessmentGraph;
    @BindView(R.id.webview_assessment_graph)
    WebView webViewAssessmentGraph;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private Unbinder unbinder;

    ArrayList<Consumers_> consumerArrayList = new ArrayList<>();
    ArrayList<Consumers_> assessmentArrayList = new ArrayList<>();
    ArrayList<Goal_> goalArrayList = new ArrayList<>();
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

        View view = inflater.inflate(R.layout.fragment_self_goal_reports, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();

        Preferences.save(General.CONSUMER_ID, "");
        Preferences.save(General.START, "y");
        Preferences.save(General.END, "");

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);
        progressBar.setVisibility(View.VISIBLE);

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
        mainActivityInterface.setToolbarBackgroundColor();
        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.self_goal_report));
            linearLayoutConsumerSpinner.setVisibility(View.GONE);
            fetchAllSelfGoals();
            Log.i(TAG, "onResume: ");
        } else {
            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))
             || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage048))) {
                mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.peer_participant_report));
            } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021))
                    || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
                mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.guest_report));
            } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024)) 
			|| Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026))
                    || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))
                    || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage052))
                    ||Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage049)) ) {
                mainActivityInterface.setMainTitle("Client Report");
            } else {
                mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.consumer_report));
            }
            textViewLabel.setVisibility(View.GONE);
            linearLayoutConsumerSpinner.setVisibility(View.VISIBLE);
            fetchAllConsumers();
        }
    }

    private final AdapterView.OnItemSelectedListener onConsumerSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerConsumers.setSelection(position);
            if (position == 0) {
                Preferences.save(General.CONSUMER_ID, "");
                linearLayoutSelfGoalReport.setVisibility(View.GONE);
                linearLayoutAssessmentGraph.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);

                ArrayList<String> goalNameList = new ArrayList<String>();
                goalNameList.add("Select Goal");
                ArrayAdapter<String> adapterGoals = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, goalNameList);
                adapterGoals.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                spinnerSelfGoalsReport.setAdapter(adapterGoals);
                spinnerSelfGoalsReport.setOnItemSelectedListener(onGoalsSelected);
                spinnerSelfGoalsReport.setSelection(0);
            } else {
                Preferences.save(General.CONSUMER_ID, consumerArrayList.get(position).getUser_id());
                linearLayoutSelfGoalReport.setVisibility(View.VISIBLE);
                linearLayoutAssessmentGraph.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                //fetchAllConsumerGoals();
                fetchAllSelfGoals();
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage013)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) {
                } else {
                    fetchAllAssessmentForms();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final AdapterView.OnItemSelectedListener onGoalsSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerSelfGoalsReport.setSelection(position);
            if (goalArrayList.size() > 0) {
                Preferences.save(General.GOAL_ID, goalArrayList.get(position).getGoal_id());
                fetchGoalsGraph();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final AdapterView.OnItemSelectedListener onAssessmentFormSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerAssessment.setSelection(position);
            Preferences.save(General.ANSWER_TYPE, "1"); //default answer_type = 1
            if (position == 0) {
                Preferences.save(General.ASSESSMENT_ID, "");
                linearLayoutAssessmentGraph.setVisibility(View.GONE);
                linearLayoutAssessmentAnswerType.setVisibility(View.GONE);
            } else {
                Preferences.save(General.ASSESSMENT_ID, assessmentArrayList.get(position).getId());
                linearLayoutAssessmentGraph.setVisibility(View.VISIBLE);

                if (assessmentArrayList.get(position).getQol() == 1) {
                    linearLayoutAssessmentAnswerType.setVisibility(View.VISIBLE);
                    final ArrayAdapter<CharSequence> assessmentAnswerTypeAdapter = ArrayAdapter.createFromResource(activity, R.array.assessment_answer_type, R.layout.drop_down_text_item_layout);
                    assessmentAnswerTypeAdapter.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                    spinnerAssessmentType.setAdapter(assessmentAnswerTypeAdapter);
                    spinnerAssessmentType.setOnItemSelectedListener(onAssessmentAnswerTypeSelected);
                } else {
                    linearLayoutAssessmentAnswerType.setVisibility(View.GONE);
                }

                fetchAssessmentFormGraph();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final AdapterView.OnItemSelectedListener onAssessmentAnswerTypeSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Preferences.save(General.ANSWER_TYPE, String.valueOf(spinnerAssessmentType.getSelectedItemPosition() + 1));
            fetchAssessmentFormGraph();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
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
                Log.i(TAG, "fetchAllConsumers: "+response);
                if (response != null) {
                    consumerArrayList = new ArrayList<>();
                    Consumers_ consumers = new Consumers_();
                    consumers.setUser_id(0);

                    if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))
                            || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage048))) {
                        consumers.setName("Select Peer Participant");
                    } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021))
                            || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
                        consumers.setName("Select Guest");
                    } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))
                            || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage050))) {
                        consumers.setName("Select Student");
                    } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage053))) {
                        consumers.setName("Select KV member");
                    } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))
                            || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026))
                            || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))
                            || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage049))
                            || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage052))) {
                        consumers.setName("Select Client");
                    } else {
                        if (Preferences.get(General.USER_ID).equalsIgnoreCase("853")) {
                            isFrancescaLoggedIn = true;
                        }
                        consumers.setName("Select Consumer");
                    }
                    consumers.setStatus(1);
                    consumerArrayList.add(consumers);
                    tempConsumerArrayList = ReportsParser_.parseConsumers(response, Actions_.GET_CONSUMER_ANALYTICS, activity.getApplicationContext(), TAG);

                    if (tempConsumerArrayList.get(0).getStatus() == 2) {

                        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))
                                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage048))) {
                            ShowToast.toast("No Peer Participant", activity);
                        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
                            ShowToast.toast("No Guest", activity);
                        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))
                                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage050))) {
                            ShowToast.toast("No Student", activity);
                        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage053))) {
                            ShowToast.toast("No KV member", activity);
                        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))
                                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026))
                                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))
                                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage049))
                                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage052))) {
                            ShowToast.toast("No Client", activity);
                        } else {
                            ShowToast.toast("No Consumer", activity);
                        }

                        spinnerConsumers.setEnabled(false);
                    }

                    if (tempConsumerArrayList.size() > 0) {
                        consumerArrayList.addAll(tempConsumerArrayList);
                    }
                    ArrayList<String> consumerNameList = new ArrayList<String>();
                    for (int i = 0; i < consumerArrayList.size(); i++) {
                        if (isFrancescaLoggedIn && consumerArrayList.get(i).getUser_id() == 1976) {
                            consumerArrayListPosition = i;
                        }
                        consumerNameList.add(consumerArrayList.get(i).getName());
                    }
                    if (consumerArrayList.size() > 0) {
                        ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, consumerNameList);
                        adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                        spinnerConsumers.setAdapter(adapterConsumer);
                        spinnerConsumers.setOnItemSelectedListener(onConsumerSelected);
                        spinnerConsumers.setSelection(consumerArrayListPosition);

                        Preferences.save(General.CONSUMER_ID, consumerArrayList.get(consumerArrayListPosition).getUser_id());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /*fethching all the selfgoal list*/
    private void fetchAllSelfGoals() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PEER_PARTICIPANT_GOAL);
        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            requestMap.put("youth_id", Preferences.get(General.USER_ID));
        } else {
            requestMap.put("youth_id", Preferences.get(General.CONSUMER_ID));
        }
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.NATIVE_ID, "1");

        String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.AJAX_GOAL_LIST;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                int goalArrayListPosition = 0;
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.i(TAG, "fetchAllSelfGoals: "+response);
                if (response != null) {
                    goalArrayList = new ArrayList<>();
                    goalArrayList.addAll(SelfGoal_.parseSelfGoal(response, General.GOAL_LIST, activity.getApplicationContext(), TAG));
                    ArrayList<String> goalNameList = new ArrayList<String>();
                    for (int i = 0; i < goalArrayList.size(); i++) {
                        if (goalArrayList.get(i).getName().equalsIgnoreCase("Appointments attended")) {
                            goalArrayListPosition = i;
                        }
                        goalNameList.add(goalArrayList.get(i).getName());
                    }
                    if (goalArrayList.size() > 0) {
                        ArrayAdapter<String> adapterGoals = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, goalNameList);
                        adapterGoals.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                        spinnerSelfGoalsReport.setAdapter(adapterGoals);
                        spinnerSelfGoalsReport.setOnItemSelectedListener(onGoalsSelected);
                        spinnerSelfGoalsReport.setSelection(goalArrayListPosition);
                        Preferences.save(General.GOAL_ID, goalArrayList.get(goalArrayListPosition).getGoal_id());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //make network call get Number of Missed Dosages graph from server
    private void fetchGoalsGraph() {
        int status = 11;
        webViewGoalStatus.getSettings().setLoadWithOverviewMode(true);
        webViewGoalStatus.setInitialScale(120);
        webViewGoalStatus.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewGoalStatus.setScrollbarFadingEnabled(false);

        webViewGoalStatus.setWebChromeClient(new WebChromeClient());
        webViewGoalStatus.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webViewGoalStatus.getSettings().setLoadsImagesAutomatically(true);
        webViewGoalStatus.getSettings().setJavaScriptEnabled(true);
        webViewGoalStatus.getSettings().setDomStorageEnabled(true);
        webViewGoalStatus.getSettings().setUseWideViewPort(true);
        webViewGoalStatus.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewGoalStatus.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewGoalStatus.setVerticalScrollBarEnabled(false);
        webViewGoalStatus.setHorizontalScrollBarEnabled(false);
        webViewGoalStatus.getSettings().setLoadsImagesAutomatically(true);
        webViewGoalStatus.getSettings().setBuiltInZoomControls(true);
        webViewGoalStatus.getSettings().setUserAgentString("Android WebView");

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PEER_PARTICIPANT);
        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            requestMap.put(General.CONSUMER_ID, Preferences.get(General.USER_ID));
        } else {
            requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        }
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GOAL_ID, Preferences.get(General.GOAL_ID));
        requestMap.put(General.HC_ROLE, Preferences.get(General.ROLE_ID));
        requestMap.put(General.GOAL_TYPE, "2");
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
                webViewGoalStatus.loadUrl(url);
                progressBar.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchAllAssessmentForms() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_FORMS);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_FORM_BUILDER;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    assessmentArrayList = new ArrayList<>();
                    Consumers_ assessments = new Consumers_();
                    assessments.setId(0);
                    assessments.setName("Select Assessment Form");
                    assessments.setStatus(1);
                    assessmentArrayList.add(assessments);
                    ArrayList<Consumers_> tempAssessmentsArrayList = ReportsParser_.parseConsumers(response, Actions_.GET_FORMS, activity.getApplicationContext(), TAG);
                    if (tempAssessmentsArrayList.size() > 0 && tempAssessmentsArrayList.get(0).getStatus() == 1) {
                        assessmentArrayList.addAll(tempAssessmentsArrayList);
                    }
                    ArrayList<String> assessmentNameList = new ArrayList<String>();
                    for (int i = 0; i < assessmentArrayList.size(); i++) {
                        assessmentNameList.add(assessmentArrayList.get(i).getName());
                    }
                    if (assessmentArrayList.size() > 0) {
                        linearLayoutAssessment.setVisibility(View.VISIBLE);
                        ArrayAdapter<String> adapterAssessments = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, assessmentNameList);
                        adapterAssessments.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                        spinnerAssessment.setAdapter(adapterAssessments);
                        spinnerAssessment.setOnItemSelectedListener(onAssessmentFormSelected);
                        spinnerAssessment.setSelection(0);

                        Preferences.save(General.ASSESSMENT_ID, assessmentArrayList.get(0).getId());
                    } else {
                        linearLayoutAssessment.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //make network call get Assessment Form graph from server
    private void fetchAssessmentFormGraph() {
        int status = 11;
        webViewAssessmentGraph.getSettings().setLoadWithOverviewMode(true);
        webViewAssessmentGraph.setInitialScale(140);
        webViewAssessmentGraph.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webViewAssessmentGraph.setScrollbarFadingEnabled(false);

        webViewAssessmentGraph.setWebChromeClient(new WebChromeClient());
        webViewAssessmentGraph.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewAssessmentGraph.getSettings().setLoadsImagesAutomatically(true);
        webViewAssessmentGraph.getSettings().setJavaScriptEnabled(true);
        webViewAssessmentGraph.getSettings().setDomStorageEnabled(true);
        webViewAssessmentGraph.getSettings().setUseWideViewPort(true);
        webViewAssessmentGraph.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewAssessmentGraph.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewAssessmentGraph.setVerticalScrollBarEnabled(false);
        webViewAssessmentGraph.setHorizontalScrollBarEnabled(false);
        webViewAssessmentGraph.getSettings().setLoadsImagesAutomatically(true);
        webViewAssessmentGraph.getSettings().setBuiltInZoomControls(true);
        webViewAssessmentGraph.getSettings().setUserAgentString("Android WebView");

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_ASSESSMENT_REPORT);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.ANSWER_TYPE, Preferences.get(General.ANSWER_TYPE));
        requestMap.put(General.ID, Preferences.get(General.ASSESSMENT_ID));

        requestMap.put(General.HC_ROLE, ""+Preferences.get(General.ROLE_ID));
        Log.i(TAG, "fetchAssessmentFormGraph: consumer id"+Preferences.get(General.CONSUMER_ID)
                +" Answer type "+Preferences.get(General.ANSWER_TYPE)
                +" ID"+Preferences.get(General.ASSESSMENT_ID)
        +"hc_role "+Preferences.get(General.ROLE_ID) );

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

                Log.e(TAG, "Url graph fetch-> " + url);
                //webViewAssessmentGraph.clearHistory();
                webViewAssessmentGraph.loadUrl(url);
                progressBar.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
