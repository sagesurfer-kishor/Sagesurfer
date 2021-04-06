package com.modules.admin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Admin_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 22-08-2017
 * Last Modified on 13-12-2017
 */

public class DeletingListFragment extends Fragment {

    private static final String TAG = DeletingListFragment.class.getSimpleName();

    private SwipeMenuListView listView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    private Activity activity;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.list_view_layout, null);

        activity = getActivity();

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        FloatingActionButton createButton = (FloatingActionButton) view.findViewById(R.id.fab_listview);
        createButton.setVisibility(View.GONE);

        listView = (SwipeMenuListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight((int) activity.getApplicationContext().getResources().getDimension(R.dimen.list_divider));

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getList();
    }

    // Show respective error message on screen if anything went wrong
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

    // Make network call to fetch spammed content from server
    private void getList() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ADMIN_ACTIVITY);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CC_OPERATIONS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response == null) {
                    showError(true, 12);
                } else {
                    ArrayList<SpamItem_> spamItemArrayList = Admin_.parseSpams(response,
                            activity.getApplicationContext(), TAG);
                    if (spamItemArrayList.size() > 0) {
                        if (spamItemArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            DeletingListAdapter deletingListAdapter = new DeletingListAdapter(activity, spamItemArrayList);
                            listView.setAdapter(deletingListAdapter);
                        } else {
                            showError(true, spamItemArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(false, 12);
                    }
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showError(true, 11);
    }
}
