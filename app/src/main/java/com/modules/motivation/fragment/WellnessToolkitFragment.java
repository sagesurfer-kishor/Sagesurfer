package com.modules.motivation.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.modules.motivation.activity.AddToolkitItemAcivity;
import com.modules.motivation.activity.WellnessSelectedItemsActivity;
import com.modules.motivation.adapter.WellnessToolkitItemAdapter;
import com.modules.motivation.model.ToolKitData;
import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.ToolkitParser_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by kailash karankal.
 */

public class WellnessToolkitFragment extends Fragment implements WellnessToolkitItemAdapter.OnItemClickListener {
    private static final String TAG = WellnessToolkitFragment.class.getSimpleName();
    private ListView listView;
    private LinearLayout errorLayout, wellnessToolkitLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private Activity activity;
    private ArrayList<ToolKitData> toolkitArrayList;
    private MainActivityInterface mainActivityInterface;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FrameLayout frameLayoutFab;
    private static FloatingActionButton toolkitAddBtn;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.hidePlusIcon(false);
        }

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

        View view = inflater.inflate(R.layout.fragment_wellness_toolkit, null);
        activity = getActivity();

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);
        wellnessToolkitLayout = view.findViewById(R.id.wellness_toolkit_layout);
        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(0);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getToolkitData();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        frameLayoutFab = (FrameLayout) view.findViewById(R.id.framelayout_fab);
        toolkitAddBtn = (FloatingActionButton) view.findViewById(R.id.toolkit_float_btn);

        toolkitAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filterIntent = new Intent(activity.getApplicationContext(), AddToolkitItemAcivity.class);
                filterIntent.putExtra("editToolkit", false);
                startActivity(filterIntent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle("WELLNESS TOOLKIT");
        mainActivityInterface.setToolbarBackgroundColor();
        getToolkitData();
    }

    //  make network call to fetch toolkit data
    private void getToolkitData() {
        int status = 11;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_TOOLKIT);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.GET_TOOLKIT_LIST;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    toolkitArrayList = ToolkitParser_.parseGetToolkit(response, Actions_.GET_TOOLKIT, activity.getApplicationContext(), TAG);
                    if (toolkitArrayList.size() > 0) {
                        if (toolkitArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            WellnessToolkitItemAdapter wellnessToolkitAdapter = new WellnessToolkitItemAdapter(activity, toolkitArrayList, this);
                            listView.setAdapter(wellnessToolkitAdapter);
                        } else {
                            showError(true, toolkitArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(true, 2);
                    }
                } else {
                    showError(true, 11);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showError(true, status);
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            wellnessToolkitLayout.setVisibility(View.GONE);
            frameLayoutFab.setVisibility(View.VISIBLE);
            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            wellnessToolkitLayout.setVisibility(View.VISIBLE);
            frameLayoutFab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClickListener(ToolKitData toolKitData) {
        if (toolKitData != null) {
            Intent wellnessIntent = new Intent(activity.getApplicationContext(), WellnessSelectedItemsActivity.class);
            wellnessIntent.putExtra("time_stamp", toolKitData.getTimestamp());
            wellnessIntent.putExtra("toolkit_ids", toolKitData.getToolkit_ids());
            wellnessIntent.putExtra("datetime", toolKitData.getDatetime());
            wellnessIntent.putExtra("id", toolKitData.getId());
            startActivity(wellnessIntent, ActivityTransition.moveToNextAnimation(activity.getApplicationContext()));
        }
    }
}
