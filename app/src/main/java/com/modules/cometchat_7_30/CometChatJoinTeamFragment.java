package com.modules.cometchat_7_30;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.sagesurfer.adapters.JoinChatExpandableListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.tasks.PerformGetTeamsTask;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import constant.StringContract;
import okhttp3.RequestBody;
import screen.messagelist.CometChatMessageListActivity;
import screen.messagelist.NetworkCall_;
import screen.messagelist.Urls_;

import static android.content.Context.MODE_PRIVATE;

public class CometChatJoinTeamFragment extends Fragment implements JoinChatExpandableListAdapter.JoinChatExpandableListAdapterListener {
    private static final String TAG = CometChatJoinTeamFragment.class.getSimpleName();

    private static ExpandableListView expandableListView;
    private JoinChatExpandableListAdapter teamsListAdapter;
    public ArrayList<Teams_> primaryList = new ArrayList<>();
    public ArrayList<Teams_> searchList = new ArrayList<>();
    private static LinearLayout errorLayout;
    private EditText editTextSearch;
    private TextView errorText;
    private LinearLayout cardview_actions;
    private AppCompatImageView errorIcon;
    SharedPreferences preferenOpenActivity;
    private BroadcastReceiver customReceiver;
//    private CometChatJoinTeamFragment cometChat;
//    private CometChatMainFragment parentActivity;
    private String team_member_id, team_provider;
    SharedPreferences sp;

    public CometChatJoinTeamFragment() {
        // Required empty public constructor
    }

    public static CometChatJoinTeamFragment newInstance(String param1, String param2) {
        CometChatJoinTeamFragment fragment = new CometChatJoinTeamFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_teams, container, false);
        sp = getActivity().getSharedPreferences("login", getActivity().MODE_PRIVATE);
        //SubcribeCometChat_.SubcribeToCometChat(getActivity());

        editTextSearch = view.findViewById(R.id.ed_search_friend);
        errorLayout = view.findViewById(R.id.linealayout_error);
        errorText = view.findViewById(R.id.textview_error_message);
        errorIcon = view.findViewById(R.id.imageview_error_icon);
        expandableListView = view.findViewById(R.id.expandablelistview_teams);
        cardview_actions = view.findViewById(R.id.cardview_actions);

        editTextSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    editTextSearch.clearFocus();
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        // search join team from list
        editTextSearch.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                //performSearch();
                searchUser(editTextSearch.getText().toString().trim());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(editTextSearch.getText().toString().trim());
            }
        });

        preferenOpenActivity = getActivity().getSharedPreferences("sp_check_push_intent", MODE_PRIVATE);

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        getTeams();
        //refreshFragment();
    }

    private void searchUser(String search) {
        teamsListAdapter.getFilter().filter(search);
    }

    // open chat window join team
    private void openChatActivity(String username, String userId, int status, String gid,String memberId) {
        Log.e("userName", username);
        SharedPreferences sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("membersIds",memberId);
        editor.putString("teamIds",gid);
        Intent intent = new Intent(getContext(), CometChatMessageListActivity.class);
        intent.putExtra(StringContract.IntentStrings.NAME, username);
        intent.putExtra(StringContract.IntentStrings.UID, userId);
        intent.putExtra(StringContract.IntentStrings.STATUS, status);
        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
        intent.putExtra(StringContract.IntentStrings.TABS, "4");
        intent.putExtra("teamId", gid);

        startActivity(intent);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    // make call to fetch all teams
    private void getTeams() {
        String team_type = "2";
        if (!primaryList.isEmpty()){
            primaryList.clear();
        }
        if (!searchList.isEmpty()){
            searchList.clear();
        }
        primaryList = PerformGetTeamsTask.getMyteam(Actions_.ALL_TEAMS_CHAT, team_type, getActivity(), TAG, false, getActivity());
        if (primaryList.size() == 0) {
            errorLayout.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.GONE);
            expandableListView.setVisibility(View.VISIBLE);
            cardview_actions.setVisibility(View.VISIBLE);
            searchList.addAll(primaryList);
            teamsListAdapter = new JoinChatExpandableListAdapter(getContext(), primaryList, searchList, this, getActivity());
            expandableListView.setAdapter(teamsListAdapter);
            checkIntent();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        try {
            super.onStop();
            if (null != customReceiver) {
                getActivity().unregisterReceiver(customReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMemberRelativeLayoutClicked(int memberPosition, Teams_ team_) {
        Log.e(TAG, "position: " + memberPosition + " user name: " + team_.getMembersArrayList().get(memberPosition).getUsername());
        Log.e(TAG, "team id: " + team_.getId() + " team name: " + team_.getName());
        Log.e(TAG, "member id: " + team_.getMembersArrayList().get(memberPosition).getComet_chat_id());

        String memberId = String.valueOf(team_.getMembersArrayList().get(memberPosition).getUser_id());
        String teamId = String.valueOf(team_.getId());

        Preferences.save(General.BANNER_IMG, team_.getBanner());
        Preferences.save(General.TEAM_ID, team_.getId());
        Preferences.save(General.TEAM_NAME, team_.getName());

        String[] ProviderArray = team_provider.split(",");
        for (String providerId : ProviderArray) {
            /*If provider*/
            Log.i(TAG, "onMemberRelativeLayoutClicked: 2 from custom array" + providerId +
                    " from list clicked" + team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
            String[] spitId = providerId.split("_");
            if (spitId[0].equals("" + team_.getMembersArrayList().get(memberPosition).getUser_id())) {
                Log.i(TAG, "onMemberRelativeLayoutClicked: 3 -" + spitId[0]);
                getUserDetails(providerId, team_, memberPosition);
                //if (team_.getMembersArrayList().get(memberPosition).getStatus())
            } else {
                /*if not provider check member list*/
                String[] spitIdMemberList = providerId.split("_");
                if (spitIdMemberList[0].equals("" + team_.getMembersArrayList().get(memberPosition).getUser_id()))
                {
                    //checkMemberList(providerId, team_, memberPosition);
                    checkOtherMemberAvailable(team_, memberPosition);
                }else{
                    final String CometchatIdOfSender = String.valueOf(team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
                    final String username = team_.getMembersArrayList().get(memberPosition).getUsername();
                    final int status = team_.getMembersArrayList().get(memberPosition).getStatus();

                    openChatActivity(""+username,
                            ""+CometchatIdOfSender,
                            status,
                            ""+String.valueOf(team_.getId()),
                            "" + CometchatIdOfSender);
                }
            }
        }



        /*final String receiverCometchatId = String.valueOf(team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
        final String username = team_.getMembersArrayList().get(memberPosition).getUsername();
        final int status = team_.getMembersArrayList().get(memberPosition).getStatus();

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("teamIds", teamId);
        editor.putString("membersIds", receiverCometchatId);

        editor.commit();

        openChatActivity(username, receiverCometchatId, status, String.valueOf(team_.getId()),""+team_.getMembersArrayList().get(memberPosition).getUser_id());
*/
    }

    private void checkOtherMemberAvailable(Teams_ team_, int position) {
        SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(screen.messagelist.General.USER_ID, null);
        SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String DomainURL = domainUrlPref.getString(screen.messagelist.General.DOMAIN, null);
        Log.i(TAG, "checkOtherMemberAvailable: 1");
        String url = Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();

        requestMap.put(screen.messagelist.General.ACTION, "other_members_time_check_in_db");
        requestMap.put(screen.messagelist.General.USER_ID, "" + UserId);
        requestMap.put(screen.messagelist.General.RECEIVER_ID, "" + team_.getMembersArrayList().get(position).getUser_id());
        //team_.getMembersArrayList().get(memberPosition).getUser_id()
        RequestBody requestBody = NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());
        Log.i(TAG, "checkOtherMemberAvailable: 2");
        Log.i(TAG, "checkOtherMemberAvailable: Domain " + DomainURL + url);
        try {
            if (requestBody != null) {
                String response = NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                if (response != null) {
                    Log.i(TAG, "checkOtherMemberAvailable:  response " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray provider_time_check_in_db = jsonObject.getJSONArray("other_members_time_check_in_db");
                    String success = provider_time_check_in_db.getJSONObject(0).getString("Success");
                    if (success.equals("success") || success.equals("fail")){

                        final String CometchatIdOfSender = String.valueOf(team_.getMembersArrayList().get(position).getComet_chat_id());
                        final String username = team_.getMembersArrayList().get(position).getUsername();
                        final int status = team_.getMembersArrayList().get(position).getStatus();

                        openChatActivity(""+username,
                                ""+CometchatIdOfSender,
                                status,
                                ""+String.valueOf(team_.getId()),
                                "" + CometchatIdOfSender);

                        /*Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                        intent.putExtra(StringContract.IntentStrings.NAME, (user.getName()));
                        intent.putExtra(StringContract.IntentStrings.SENDER_ID, (user.getUid()));
                        intent.putExtra(StringContract.IntentStrings.AVATAR, (user.getAvatar()));
                        intent.putExtra(StringContract.IntentStrings.STATUS, (user.getStatus()));
                        intent.putExtra(StringContract.IntentStrings.TABS, "1");
                        getActivity().startActivity(intent);*/
                    }else {
                        showDialog(success, position,team_);
                    }
                } else {
                    Log.i(TAG, "checkOtherMemberAvailable:  null  ");
                }
            } else {
                Log.i(TAG, "checkOtherMemberAvailable:  null2");
            }
        } catch (Exception e) {
            Log.i(TAG, "checkOtherMemberAvailable: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showOnlineDialog(Teams_ team_, int memberPosition) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        //Setting message manually and performing action on button click
        builder.setMessage("Are you sure you want chat with provider?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        final String CometchatIdOfSender = String.valueOf(team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
                        final String username = team_.getMembersArrayList().get(memberPosition).getUsername();
                        final int status = team_.getMembersArrayList().get(memberPosition).getStatus();

                        openChatActivity("" + username,
                                "" + CometchatIdOfSender,
                                status,
                                "" + String.valueOf(team_.getId()),
                                "" + CometchatIdOfSender);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Alert Notification");
        alert.show();
    }

    public void getUserDetails(String providerId, Teams_ team_, int memberPosition) {

        CometChat.getUser(providerId, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "getUserDetails -> user details : " + user.toString());
                String statuss = user.getStatus();

                if (statuss.equals("online")) {
                    Log.i(TAG, "getUserDetails -> status if online -> " + statuss);
                    showOnlineDialog(team_, memberPosition);
                } else {
                    Log.i(TAG, "getUserDetails -> status if offline  -> " + statuss);
                    checkProviderOrMembersAvailableOnServer(team_, memberPosition);
                }
                //Log.i(TAG, "onMemberRelativeLayoutClicked: member Detail "+team_.getMembersArrayList().get(memberPosition));

            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "User details fetching failed with exception: " + e.getMessage());
            }
        });
    }

    private void checkProviderOrMembersAvailableOnServer(Teams_ team_, int memberPosition) {
        SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(screen.messagelist.General.USER_ID, null);
        SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String DomainURL = domainUrlPref.getString(screen.messagelist.General.DOMAIN, null);
        Log.i(TAG, "checkProviderAvailableTime: 1");
        String url = Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();

        requestMap.put(screen.messagelist.General.ACTION, "provider_time_check_in_db");
        requestMap.put(screen.messagelist.General.USER_ID, "" + UserId);
        requestMap.put(screen.messagelist.General.RECEIVER_ID, "" + team_.getMembersArrayList().get(memberPosition).getComet_chat_id());

        RequestBody requestBody = NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());
        Log.i(TAG, "checkProviderAvailableTime: 2");
        Log.i(TAG, "checkProviderAvailableTime: Domain " + DomainURL + url);
        try {
            if (requestBody != null) {
                String response = NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                if (response != null) {
                    Log.i(TAG, "checkProviderAvailableTime:  response " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray provider_time_check_in_db = jsonObject.getJSONArray("provider_time_check_in_db");
                    String success = provider_time_check_in_db.getJSONObject(0).getString("Success");
                    showDialog(success, memberPosition, team_);
                } else {
                    Log.i(TAG, "checkProviderAvailableTime:  null  ");
                }
            } else {
                Log.i(TAG, "checkProviderAvailableTime:  null2");
            }
        } catch (Exception e) {
            Log.i(TAG, "checkProviderAvailableTime: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void showDialog(String success, int position, Teams_ team_) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        String message;
        if (success.equals("Success") || success.equals("Fail")) {
            message = "Are you sure you want chat with provider?";
        } else {
            message = success;
        }
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //team_.getMembersArrayList().get(memberPosition).getComet_chat_id()
                        Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                        intent.putExtra(StringContract.IntentStrings.NAME, (team_.getMembersArrayList().get(position).getFullname()));
                        intent.putExtra(StringContract.IntentStrings.UID, (team_.getMembersArrayList().get(position).getUser_id()));
                        intent.putExtra(StringContract.IntentStrings.AVATAR, (team_.getMembersArrayList().get(position).getPhoto()));
                        intent.putExtra(StringContract.IntentStrings.STATUS, (team_.getMembersArrayList().get(position).getStatus()));
                        intent.putExtra(StringContract.IntentStrings.TABS, "1");
                        getActivity().startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Alert Notification");
        alert.show();
    }

    @Override
    public void onTeamClickFetchTeamData(Teams_ item) {
        SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(screen.messagelist.General.USER_ID, null);
        SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String DomainURL = domainUrlPref.getString(screen.messagelist.General.DOMAIN, null);
        String url = Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(screen.messagelist.General.ACTION, "get_team_members_array");
        requestMap.put(screen.messagelist.General.TEAM_ID, "" + item.getId());
        requestMap.put(screen.messagelist.General.USER_ID, UserId);
        Log.i(TAG, "onTeamClickFetchTeamData: User Id " + UserId);
        RequestBody requestBody = NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());
        Log.i(TAG, "onTeamClickFetchTeamData: Domain " + DomainURL + url);
        try {
            if (requestBody != null) {
                String response = NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                if (response != null) {

                    Log.i(TAG, "onTeamClickFetchTeamData:  response " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray get_team_members_array = jsonObject.getJSONArray("get_team_members_array");
                    team_member_id = get_team_members_array.getJSONObject(0).getString("team_member_id");
                    team_provider = get_team_members_array.getJSONObject(0).getString("team_provider");

                } else {
                    Log.i(TAG, "checkMemberList:  null  ");
                }
            } else {
                Log.i(TAG, "checkMemberList:  null2");
            }
        } catch (Exception e) {
            Log.i(TAG, "checkMemberList: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, getContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            expandableListView.setVisibility(View.VISIBLE);
        }
    }

    /*created this method for redirecting pushnotification message to chat screen
    * created by rahulmsk */
    private void checkIntent() {
        if (preferenOpenActivity.getBoolean("checkIntent", false)) {
            if (getActivity() != null && getActivity().getIntent().hasExtra("receiverType")) {
                String receiverType = getActivity().getIntent().getStringExtra("receiverType");
                if (receiverType.equals("user")) {
                    String team_logs_id = getActivity().getIntent().getStringExtra("team_logs_id");
                    if (team_logs_id != null && !team_logs_id.isEmpty()) {
                        if (team_logs_id.endsWith("3")) {
                            String ids[] = team_logs_id.split(Pattern.quote("_"));
                            Log.i(TAG, "checkIntent: teamIdIs " + ids[2].substring(1));
                            Log.i(TAG, "checkIntent: memberId: " + ids[0].substring(0));

                            SharedPreferences.Editor editor = preferenOpenActivity.edit();
                            editor.putBoolean("checkIntent", false);
                            editor.apply();

                            openChatActivity(
                                    "" + getActivity().getIntent().getStringExtra("username"),
                                    "" + getActivity().getIntent().getStringExtra("sender"),
                                    0,
                                    "" + Integer.parseInt(ids[2].substring(1)),
                                    "" + ids[0].substring(0));
                        }
                    }
                }
            }
        }
    }
}

        //below code is commented by rahul msk
        /* // Code By Debopam
        if (getActivity() != null && getActivity().getIntent().hasExtra("receiverType")) {
            String receiverType = getActivity().getIntent().getStringExtra("receiverType");
            if (receiverType.equals("user")) {
                // Either tab 1 or 3 or 4
                String team_logs_id = getActivity().getIntent().getStringExtra("team_logs_id");
                if (team_logs_id != null && !team_logs_id.isEmpty()) {
                    if (team_logs_id.endsWith("3")) {
                        String ids[] = team_logs_id.split(Pattern.quote("_"));
                        if (ids.length == 4) {
                            for (int i = 0; i < primaryList.size(); i++) {
                                Teams_ team = primaryList.get(i);
                                if (team.getId() == Integer.parseInt(ids[2].substring(1))) ;
                                {
                                    for (int j = 0; j < team.getMembersArrayList().size(); j++) {
                                        if (team.getMembersArrayList().get(j).getUser_id() ==
                                                Integer.parseInt(getActivity().getIntent().getStringExtra("sender").split(Pattern.quote("_"))[0])) {
                                            onMemberRelativeLayoutClicked(j, team);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }*/
