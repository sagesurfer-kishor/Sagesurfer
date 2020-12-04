package com.modules.crisis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
 import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.adapters.SingleItemAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 07-09-2017
 *         Last Modified on 16-11-2017
 */

public class InterventionListFragment extends Fragment {

    private static final String TAG = InterventionListFragment.class.getSimpleName();
    private String action = "";
    private Activity activity;
    private List<String> idList;

    private SwipeMenuListView listView;
    private TextView strengthText, labelTextView;

    private SingleItemAdapter singleItemAdapter;

    //static constructor
    static InterventionListFragment newInstance(String action) {
        InterventionListFragment interventionListFragment = new InterventionListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(General.ACTION, action);
        interventionListFragment.setArguments(bundle);
        return interventionListFragment;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.intervention_fragment_layout, null);
        activity = getActivity();

        Bundle data = getArguments();
        if (data != null) {
            action = data.getString(General.ACTION);
        }
        int  color = GetColor.getHomeIconBackgroundColorColorParse(true);
        labelTextView = (TextView) view.findViewById(R.id.textview_label);
        labelTextView.setTextColor(color);
        labelTextView.setText(getResources().getString(R.string.strength));
        strengthText = (TextView) view.findViewById(R.id.intervention_fragment_strength);

        listView = (SwipeMenuListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight((int) activity.getApplicationContext().getResources().getDimension(R.dimen.list_divider));
        listView.setOnItemClickListener(onItemClick);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        return view;
    }

    // Make network call to fetch strength and crisis
    private void getData() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_STRENGTH);
        requestMap.put(General.TYPE, action.toLowerCase());
        requestMap.put("gid", Preferences.get(General.GROUP_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CRISIS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, "strength");
                    if (jsonArray != null) {
                        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                        String strength = jsonObject.get("strength").getAsString();
                        if (strength.trim().length() <= 0) {
                            strength = "-- NA --";
                        }
                        String potential_crisis = jsonObject.get("potential_crisis").getAsString();
                        idList = Arrays.asList(potential_crisis.split(","));
                        setData(idList, strength);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // handle list item click
    private final AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            openDialog(position, singleItemAdapter.getItem(position));
        }
    };

    // open intervention dialog
    private void openDialog(int position, String title) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentByTag(TAG);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
        InterventionDialogFrag detailsFragment = new InterventionDialogFrag();
        Bundle bundle = new Bundle();
        bundle.putString(General.TITLE, title);
        bundle.putString(General.ID, idList.get(position));
        detailsFragment.setArguments(bundle);
        detailsFragment.show(fragmentManager, TAG);
    }

    // set strength and crisis list to respective widgets
    private void setData(List<String> list, String strength) {
        strengthText.setText(strength);
        ArrayList<String> potentialList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).equalsIgnoreCase("0")) {
                potentialList.add("Potential Crisis " + (i + 1));
            }
        }
        if (potentialList.size() > 0) {
            singleItemAdapter = new SingleItemAdapter(activity.getApplicationContext(), potentialList, true);
            listView.setAdapter(singleItemAdapter);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
}
