package com.modules.caseload;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
 import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.CSGoals_;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CSGoalsParser_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Monika on 6/04/2018.
 */

public class SummaryGoalsGraphFragment extends Fragment {

    private static final String TAG = SummaryGoalsGraphFragment.class.getSimpleName();
    @BindView(R.id.linearlayout_goals_graph)
    LinearLayout linearLayoutGoalsGraph;
    @BindView(R.id.textview_moodswing)
    TextView textViewMoodSwing;
    @BindView(R.id.linearlayout_moodswing)
    LinearLayout linearLayoutMoodSwing;
    @BindView(R.id.webview_moodswing)
    WebView webViewMoodSwing;
    //@BindView(R.id.cardview_goals)
    //CardView cardViewGoals;
    @BindView(R.id.relativelayout_goals)
    RelativeLayout relativeLayoutGoals;
    @BindView(R.id.textview_goals)
    TextView textViewGoals;
    @BindView(R.id.linearlayout_goals_data)
    LinearLayout linearLayoutGoalsData;
    //@BindView(R.id.cardview_personal_goals)
    //CardView cardViewPersonalGoals;
    @BindView(R.id.relativelayout_personal_goals)
    RelativeLayout relativeLayoutPersonalGoals;
    @BindView(R.id.textview_personal_goals)
    TextView textViewPersonalGoals;
    @BindView(R.id.linearlayout_personal_goals_data)
    LinearLayout linearLayoutPersonalGoalsData;

    /*private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;*/

    private Activity activity;
    private Unbinder unbinder;

    //private int cWidth = 0, cHeight = 0;
    //private ArrayList<String> arrayListMoodSwingDimension = new ArrayList<>();

    private ArrayList<CSGoals_> csGoalsArrayList;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.fragment_summary_goals_graph, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();

        int  color = GetColor.getHomeIconBackgroundColorColorParse(true);
        textViewMoodSwing.setTextColor(color);
        textViewMoodSwing.setText(getResources().getString(R.string.mood_swings_questionnaire_scores));
        textViewGoals.setTextColor(color);
        textViewGoals.setText(getResources().getString(R.string.goals));
        textViewPersonalGoals.setTextColor(color);
        textViewPersonalGoals.setText(getResources().getString(R.string.personal_goals));

        return view;
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            relativeLayoutGoals.setVisibility(View.GONE);
            relativeLayoutPersonalGoals.setVisibility(View.GONE);
        } else {
            if(csGoalsArrayList.get(0).getGoals().size() == 0) {
                relativeLayoutGoals.setVisibility(View.GONE);
            } else {
                setGoalsData();
            }

            if(csGoalsArrayList.get(0).getPersonal_goals().size() == 0) {
                relativeLayoutPersonalGoals.setVisibility(View.GONE);
            } else {
                setPersonalGoalsData();
            }
        }
    }

    //make network call get moodswing graph from server
    private void fetchCaseloadSummaryMoodGraph() {
        int status = 11;

        webViewMoodSwing.setWebViewClient(new WebViewClient());
        webViewMoodSwing.getSettings().setJavaScriptEnabled(true);
        webViewMoodSwing.getSettings().setDomStorageEnabled(true);
        webViewMoodSwing.getSettings().setUseWideViewPort(true);
        webViewMoodSwing.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewMoodSwing.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webViewMoodSwing.setVerticalScrollBarEnabled(false);
        webViewMoodSwing.setHorizontalScrollBarEnabled(false);
        webViewMoodSwing.getSettings().setLoadsImagesAutomatically(true);
        webViewMoodSwing.getSettings().setBuiltInZoomControls(true);
        /*String size = ArrayOperations.stringListToString(arrayListMoodSwingDimension);
        //Log.e(TAG, "showClinical() size: " + size);
        if (size.equalsIgnoreCase("0,0")) {
            size = cWidth + "," + cHeight;
            //Log.e(TAG, "showClinical() if size: " + size);
        }*/

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_MOODSWING_GRAPH);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                Request request_new = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .tag(TAG)
                        .build();
                url = url + "?" + MakeCall.bodyToString(request_new);
                //Log.e(TAG, "Url -> " + url);
                webViewMoodSwing.loadUrl(url);

                fetchCaseloadSummaryGoals();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //make network call get goals data from server
    private void fetchCaseloadSummaryGoals() {
        int status = 11;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_GOALS_DATA);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    csGoalsArrayList = CSGoalsParser_.parseCSProfile(response, Actions_.GET_GOALS_DATA, activity.getApplicationContext(), TAG);
                    if (csGoalsArrayList.size() <= 0 || csGoalsArrayList.get(0).getStatus() != 1) {
                        if (csGoalsArrayList.size() <= 0) {
                            status = 12;
                        } else {
                            status = csGoalsArrayList.get(0).getStatus();
                        }
                        showError(true, status);
                        return;
                    }
                    showError(false, status);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*private void dimensions() {
        //Log.e(TAG, "dimensions() cWidth: " + cWidth + " cHeight: " + cHeight);
        arrayListMoodSwingDimension.clear();
        arrayListMoodSwingDimension.add(0, "0");
        arrayListMoodSwingDimension.add(1, "0");

        ViewTreeObserver viewTreeObserver = webViewMoodSwing.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int height = webViewMoodSwing.getMeasuredHeight();
                int width = webViewMoodSwing.getMeasuredWidth();
                if (height != 0) {
                    webViewMoodSwing.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                if (height != 0 && width != 0) {
                    arrayListMoodSwingDimension.clear();
                    arrayListMoodSwingDimension.add(0, "" + width);
                    arrayListMoodSwingDimension.add(1, "" + height);
                }
                return false;
            }
        });
    }*/

    @Override
    public void onStart() {
        super.onStart();
        //dimensions();
        //cWidth = linearLayoutMoodSwing.getWidth();
        //cHeight = linearLayoutMoodSwing.getHeight();
        fetchCaseloadSummaryMoodGraph();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setGoalsData() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < csGoalsArrayList.get(0).getGoals().size(); i++) {
            // Create LinearLayout
            LinearLayout ll = new LinearLayout(activity.getApplicationContext());
            ll.setOrientation(LinearLayout.HORIZONTAL);

            // Create TextView
            TextView textViewGoals = new TextView(activity.getApplicationContext());
            textViewGoals.setText(csGoalsArrayList.get(0).getGoals().get(i));
            textViewGoals.setLayoutParams(new android.view.ViewGroup.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT));
            textViewGoals.setGravity(Gravity.LEFT);
            textViewGoals.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size));
            textViewGoals.setTextColor(getResources().getColor(R.color.text_color_primary));
            textViewGoals.setPadding(6, 6, 6, 6);
            ll.addView(textViewGoals);

            linearLayoutGoalsData.addView(ll);
        }
    }

    private void setPersonalGoalsData() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < csGoalsArrayList.get(0).getPersonal_goals().size(); i++) {
            // Create LinearLayout
            LinearLayout ll = new LinearLayout(activity.getApplicationContext());
            ll.setOrientation(LinearLayout.HORIZONTAL);

            // Create TextView
            TextView textViewPersonalGoals = new TextView(activity.getApplicationContext());
            textViewPersonalGoals.setText(csGoalsArrayList.get(0).getPersonal_goals().get(i));
            textViewPersonalGoals.setLayoutParams(new android.view.ViewGroup.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT));
            textViewPersonalGoals.setGravity(Gravity.LEFT);
            textViewPersonalGoals.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size));
            textViewPersonalGoals.setTextColor(getResources().getColor(R.color.text_color_primary));
            textViewPersonalGoals.setPadding(6, 6, 6, 6);
            ll.addView(textViewPersonalGoals);

            linearLayoutPersonalGoalsData.addView(ll);
        }
    }
}
