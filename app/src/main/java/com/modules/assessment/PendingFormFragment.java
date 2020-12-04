package com.modules.assessment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Assessment_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Kailash Karankal
 * Created on 09-05-2019
 **/

public class PendingFormFragment extends Fragment {
    private static final String TAG = PendingFormFragment.class.getSimpleName();
    private ArrayList<Forms_> formsArrayList, counterFormList;
    private Activity activity;
    private ListView listView;
    private LinearLayout errorLayout;
    private TextView errorText, formTxt, formCounter;
    private AppCompatImageView errorIcon;
    private MainActivityInterface mainActivityInterface;
    private RelativeLayout pendingCounterLayout;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
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

        View view = inflater.inflate(R.layout.submit_form_list_view_layout, null);

        activity = getActivity();


        FloatingActionButton createButton = (FloatingActionButton) view.findViewById(R.id.listview_fab);
        createButton.setVisibility(View.GONE);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(3);
        listView.setOnItemClickListener(onItemClick);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        formTxt = (TextView) view.findViewById(R.id.form_name);
        formCounter = (TextView) view.findViewById(R.id.counter_forms);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);
        pendingCounterLayout = (RelativeLayout) view.findViewById(R.id.pending_submit_form_counter_layout);

        return view;
    }

    // Handle on click for list item to open respective form in details
    private final AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent detailsIntent = new Intent(activity.getApplicationContext(), FormShowActivity.class);
            detailsIntent.putExtra(Actions_.GET_LIST, formsArrayList.get(position));
            startActivity(detailsIntent);
            activity.overridePendingTransition(0, 0);
        }
    };

    // Make network call to fetch all forms list
    private void getList() {
        showError(true, 20);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_LIST);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_FORM_BUILDER;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    formsArrayList = Assessment_.parseList(response, activity.getApplicationContext(), TAG);
                    if (formsArrayList.size() > 0) {
                        if (formsArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            FormListAdapter formListAdapter = new FormListAdapter(activity, formsArrayList);
                            listView.setAdapter(formListAdapter);
                        } else {
                            showError(true, formsArrayList.get(0).getStatus());
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

    @Override
    public void onResume() {
        super.onResume();
        getList();
        getCounterPendingForms();
    }

    private void getCounterPendingForms() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.PENDING_LIST_COUNTER);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_FORM_BUILDER;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    counterFormList = Assessment_.parsePendingFormsCounter(response, activity.getApplicationContext(), TAG);
                    if (counterFormList.size() > 0) {
                        if (counterFormList.get(0).getStatus() == 1) {
                            formCounter.setText("(" + counterFormList.get(0).getCount() + ")");
                        }
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
