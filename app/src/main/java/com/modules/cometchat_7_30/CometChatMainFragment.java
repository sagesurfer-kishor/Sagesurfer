package com.modules.cometchat_7_30;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.google.android.material.tabs.TabLayout;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.storage.database.Cometchat_log;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

/**
 * @author kishor k
 * Created on 13/11/2020
 */
public class CometChatMainFragment extends Fragment {
    public static final String TAG = CometChatMainFragment.class.getSimpleName();
    private TabLayout tabLayout;
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private FragmentActivity mContext;
    private String fragment_name;
    private List<String> unreadCount = new ArrayList<>();
    SharedPreferences sp;
    private Cometchat_log db;

    @Override
    public void onStop() {
        super.onStop();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = (FragmentActivity) activity;
        mainActivityInterface = (MainActivityInterface) activity;
        //
/*        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.hidesettingIcon(true);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.chat_main_layout, null);
        activity = getActivity();
        sp = getActivity().getSharedPreferences("login", getActivity().MODE_PRIVATE);
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.chat));
        db = new Cometchat_log(getActivity());
        tabLayout = view.findViewById(R.id.chat_tab_layout);
        tabLayout.setOnTabSelectedListener(tabSelectedListener);

        // add all tabs title here
        tabLayout.addTab(tabLayout.newTab().setText("Friends"));
        tabLayout.addTab(tabLayout.newTab().setText("Group"));
        tabLayout.addTab(tabLayout.newTab().setText("My Team"));
        tabLayout.addTab(tabLayout.newTab().setText("Join Team"));

        //Toolbar toolbar=(Toolbar)getActivity().findViewById(R.id.toolbar);

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            /*if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                mainActivity.hideLogBookIcon(true);
            } else {
                mainActivity.hideLogBookIcon(false);
            }*/
            mainActivity.showHideBellIcon2(true);
            mainActivity.hidesettingIcon(true);
            mainActivity.showChatIcon(false);
            //mainActivity.hidePopup();
        }

        // action bar setting button
        /*setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingPopup(view);
            }
        });*/


        // get all provider
        getProvider();
        // get all youth
        getYouth();
        // get current language
        getCurrentLanguage("current_language", Preferences.get(General.USER_ID));
        // Checking the intent for navigation from push notification

        Cursor cursor = db.getNames();
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(Cometchat_log.COMET_CHAT_TYPE)) != null) {
                }
            } while (cursor.moveToNext());
        }
        return view;
    }


    private final TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            assert tab.getText() != null;
            fragment_name = tab.getText().toString();
            setTabFragment(fragment_name);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void setTabFragment(String fragment_name) {
        Fragment fragment;

        if (fragment_name.equalsIgnoreCase("Friends")) {
            fragment = new CometChatFriendsListFragment_();
        } else if (fragment_name.equalsIgnoreCase("Group")) {
            fragment = new FragmentCometchatGroupsList();
        } else if (fragment_name.equalsIgnoreCase("My Team")) {
            fragment = new CometChatMyTeamsFragment_();
        } else {
            fragment = new CometChatJoinTeamFragment();
        }

        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.pager, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.chat));
        mainActivityInterface.setToolbarBackgroundColor();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkIntent();
    }

    public void addConversationListener() {
        CometChat.addMessageListener(TAG, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage message) {
                setUnreadCount(message);
            }

            @Override
            public void onMediaMessageReceived(MediaMessage message) {
                setUnreadCount(message);
            }

            @Override
            public void onCustomMessageReceived(CustomMessage message) {
                setUnreadCount(message);
            }
        });
    }

    private void setUnreadCount(BaseMessage message) {

        if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
            if (!unreadCount.contains(message.getReceiverUid())) {
                unreadCount.add(message.getReceiverUid());
            }
        } else {

            if (!unreadCount.contains(message.getSender().getUid())) {
                unreadCount.add(message.getSender().getUid());
            }
        }
    }

    private void getProvider() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "provider_user_id");
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getActivity(), getActivity());
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getActivity(), getActivity());
                if (response != null) {
                    Log.e("provider", response);

                    try {
                        JSONObject injectedObject = new JSONObject(response);
                        Preferences.save("providers", injectedObject.getJSONArray("provider_user_id").toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getYouth() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "youth_user_id");
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getActivity(), getActivity());
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getActivity(), getActivity());
                if (response != null) {
                    Log.e("youth", response);
                    try {
                        JSONObject injectedObject = new JSONObject(response);
                        Preferences.save("youths", injectedObject.getJSONArray("youth_user_id").toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getCurrentLanguage(String action, String UserId) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, UserId);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getActivity());
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getActivity());

                if (response != null) {
                    Log.e("33333", response);
                    try {
                        JSONObject injectedObject = new JSONObject(response);
                        JSONArray translations = injectedObject.getJSONArray("current_language");

                        String s = null, full_name = null;
                        for (int i = 0; i < translations.length(); i++) {
                            JSONObject translation = translations.getJSONObject(i);

                            full_name = translation.getString("full_name");
                            s = translation.getString("name");
                        }

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("currentLang", s);
                        editor.putString("full_name", full_name);
                        editor.commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*This method is use to check the intent provided by main activity to redirect from firebase push notification
    * here we will redirect as per team log id tab*/
    private void checkIntent() {
        Log.i(TAG, "checkIntent: in main now it will redirect");
        if (getArguments() != null) {
            if (getArguments().getString("receiverType") != null) {
                String receiverType = getArguments().getString("receiverType");
                if (receiverType.equals("user")) {
                    // Either tab 1 or 3 or 4
                    String team_logs_id = getArguments().getString("team_logs_id");
                    if (team_logs_id.equals("0") || team_logs_id.isEmpty()) {
                        tabLayout.getTabAt(0).select();
                    } else {
                        // The index is done opposite for temoporary testing. It has to be fixed later
                        if (team_logs_id.endsWith("3"))
                            tabLayout.getTabAt(3).select();
                        else if (team_logs_id.endsWith("4"))
                            tabLayout.getTabAt(2).select();
                    }
                } else {
                    // tab 2
                    tabLayout.getTabAt(1).select();
                }
            }
        }
    }
}