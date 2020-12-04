package com.modules.consent;


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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.clans.fab.FloatingActionButton;
import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.logger.Logger;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Consent_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 09-08-2017
 * Last Modified on 13-12-2017
 */

public class ConsentListFragment extends Fragment {

    private static final String TAG = ConsentListFragment.class.getSimpleName();
    private long consumer_id = 0;
    private ArrayList<ConsentFile_> consentFileArrayList;

    private Activity activity;

    private ListView listView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    public static ConsentListFragment newInstance(long user_id) {
        ConsentListFragment myFragment = new ConsentListFragment();
        Bundle args = new Bundle();
        args.putLong(General.ID, user_id);
        myFragment.setArguments(args);
        return myFragment;
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view_layout, null);
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        activity = getActivity();

        FloatingActionButton createButton = (FloatingActionButton) view.findViewById(R.id.listview_fab);
        createButton.setVisibility(View.GONE);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(3);
        listView.setPadding(0, 20, 0, 0);
        listView.setOnItemClickListener(onItemClickListener);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        Bundle data = getArguments();
        if (data.containsKey(General.ID)) {
            consumer_id = data.getLong(General.ID);
        }
        return view;
    }

    // List view handle item click to open respective details page
    private final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (consentFileArrayList.get(0).getStatus() == 1) {
                Intent detailsIntent = new Intent(activity.getApplicationContext(), ConsentDetailsActivity.class);
                detailsIntent.putExtra(Actions_.GET_SHARED_FILES, consentFileArrayList.get(position));
                startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
            }
        }
    };

    // Make network call to fetch consent files based on consumer id
    private void getConsent() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_SHARED_FILES);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put("consumer_id", "" + consumer_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CONSENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    consentFileArrayList = Consent_.parseConsent(response, activity.getApplicationContext(), TAG);
                    Logger.error(TAG, "" + consentFileArrayList.size(), activity.getApplicationContext());
                    if (consentFileArrayList.size() > 0) {
                        if (consentFileArrayList.get(0).getStatus() == 1) {
                            ConsentFileListAdapter consentFileListAdapter = new ConsentFileListAdapter(activity, consentFileArrayList);
                            listView.setAdapter(consentFileListAdapter);
                        } else {
                            showError(true, consentFileArrayList.get(0).getStatus());
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
        getConsent();
    }
}
