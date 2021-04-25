package com.modules.team;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
 import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Contacts_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Monika on 6/15/2018.
 */

public class MemberStatisticsFragment extends Fragment {

    private static final String TAG = ContactsListFragment.class.getSimpleName();
    private ArrayList<Contacts_> contactsArrayList;

    private Activity activity;

    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private ListView listView;

    private TextView memberCount, successCount, crisisCount, supportCount, timeCount;
    private MainActivityInterface mainActivityInterface;

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

        View view = inflater.inflate(R.layout.fragment_member_statistics, null);
        activity = getActivity();

        Preferences.initialize(activity.getApplicationContext());

        memberCount = (TextView) view.findViewById(R.id.team_details_members_count);
        successCount = (TextView) view.findViewById(R.id.team_details_success_count);
        crisisCount = (TextView) view.findViewById(R.id.team_details_crisis_count);
        supportCount = (TextView) view.findViewById(R.id.team_details_support_count);
        timeCount = (TextView) view.findViewById(R.id.team_details_time_count);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.statistics));
        mainActivityInterface.setToolbarBackgroundColor();
        fetchGlance();
    }

    @Override
    public void onResume() {
        super.onResume();
        try{
            ((TeamDetailsActivity) getActivity()).inviteButtonSetVisibility();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // make network call to fetch team at a glance
    private void fetchGlance() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.AT_A_GLANCE);
        requestMap.put(General.ID, "" + Preferences.get(General.TEAM_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DETAILS_PAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.AT_A_GLANCE);
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        setTeamGlanceCounters(object);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // set counter of team at a glance to respective fields
    private void setTeamGlanceCounters(JsonObject object) {
        if (object.get(General.STATUS).getAsInt() == 1) {
            memberCount.setText(object.get("group_members").getAsString());
            successCount.setText(object.get("success").getAsString());
            crisisCount.setText(object.get("crisis").getAsString());
            supportCount.setText(object.get("no_of_support").getAsString());
            timeCount.setText(object.get("time_spend").getAsString());
        } else {
            memberCount.setText("0");
            successCount.setText("0");
            crisisCount.setText("0");
            supportCount.setText("0");
            timeCount.setText("0");
        }
    }
}
