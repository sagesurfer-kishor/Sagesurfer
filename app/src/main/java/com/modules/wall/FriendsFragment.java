package com.modules.wall;

/**
 * Created by Kailash Karankal on 5/30/2019.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;


import androidx.core.content.ContextCompat;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Kailash Karankal on 5/28/2019.
 */
public class FriendsFragment extends Fragment {
    private static final String TAG = FriendsFragment.class.getSimpleName();
    private ArrayList<Feed_> friendArrayList;
    private Activity activity;

    private ListView listView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private MainActivityInterface mainActivityInterface;


    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.list_view_layout, null);

        activity = getActivity();


        FloatingActionButton createButton = (FloatingActionButton) view.findViewById(R.id.listview_fab);
        createButton.setVisibility(View.GONE);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(3);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        return view;
    }


    // Make network call to fetch all forms list
    private void getList() {
        friendArrayList = new ArrayList<>();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_SHARE_LIST_FRIEND);
        requestMap.put("wall_id", "" + Preferences.get("wall_id"));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.WALL_URL;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    friendArrayList = Alerts_.parseShareFriendList(response, Actions_.GET_SHARE_LIST_TEAM, activity, TAG);
                    if (friendArrayList.size() > 0) {
                        if (friendArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            TeamListAdapter teamListAdapter = new TeamListAdapter(activity, friendArrayList);
                            listView.setAdapter(teamListAdapter);
                        } else {
                            showError(true, friendArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(true, 2);
                    }
                    return;
                } else {
                    showError(true, 11);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showError(true, 4);
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
    }

}