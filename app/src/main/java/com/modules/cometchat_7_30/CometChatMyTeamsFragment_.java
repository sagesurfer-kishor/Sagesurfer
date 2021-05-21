package com.modules.cometchat_7_30;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.sagesurfer.adapters.MyTeamsChatExpandableListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Members_;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.tasks.PerformGetTeamsTask;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import constant.StringContract;
import okhttp3.RequestBody;
import screen.messagelist.CometChatMessageListActivity;
import screen.messagelist.NetworkCall_;
import screen.messagelist.Urls_;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Monika on 7/16/2018.
 */

public class CometChatMyTeamsFragment_ extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MyTeamsChatExpandableListAdapter.TeamsChatExpandableListAdapterListener {
    private static final String TAG = CometChatMyTeamsFragment_.class.getSimpleName();
    private final int CHATS_LOADER = 1;
    private final int CHATS_SEARCH_LOADER = 2;
    private static ExpandableListView expandableListView;
    private MyTeamsChatExpandableListAdapter teamsListAdapter;
    public ArrayList<Teams_> al_friends = new ArrayList<>();
    public ArrayList<Teams_> al_search_friend = new ArrayList<>();
    private BroadcastReceiver customReceiver;
    private static LinearLayout errorLayout;
    private EditText et_search_friend;
    private LinearLayout ll_cardview_actions;
    private TextView tv_error,tv_team_count;
    SharedPreferences preferenOpenActivity;
    private AppCompatImageView iv_errorIcon;
    private final int CONTACTS_LOADER = 1, CONTACTS_SEARCH_LOADER = 2;
    private boolean isSearching = false;
    private String SearchingName;
    private CometChatMyTeamsFragment_ cometChat;
    private final int GROUPS_LOADER = 1, GROUPS_SEARCH_LOADER = 2;
    private String team_member_id, team_provider;

    public CometChatMyTeamsFragment_() {
        // Required empty public constructor
    }

    public static CometChatMyTeamsFragment_ newInstance(String param1, String param2) {
        CometChatMyTeamsFragment_ fragment = new CometChatMyTeamsFragment_();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_teams, container, false);
        //SubcribeCometChat_.SubcribeToCometChat(getActivity());
        et_search_friend = view.findViewById(R.id.ed_search_friend);
        errorLayout = view.findViewById(R.id.linealayout_error);
        tv_error = view.findViewById(R.id.textview_error_message);
        tv_team_count = view.findViewById(R.id.tv_team_count);
        iv_errorIcon = view.findViewById(R.id.imageview_error_icon);

        expandableListView = view.findViewById(R.id.expandablelistview_teams);

        ll_cardview_actions = view.findViewById(R.id.cardview_actions);

        et_search_friend.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    et_search_friend.clearFocus();
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(et_search_friend.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        // serach myteam from list
        et_search_friend.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                Log.i(TAG, "afterTextChanged: " + et_search_friend.getText().toString().trim());
                searchUser(et_search_friend.getText().toString().trim());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(et_search_friend.getText().toString().trim());
            }
        });
        preferenOpenActivity = getActivity().getSharedPreferences("sp_check_push_intent", MODE_PRIVATE);
        //Fetching all teams
        //getTeams();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        //refreshFragment();
        Log.i(TAG, "onResume: ");
        getTeams();
    }

    private void searchUser(String search) {
        Log.e("Seach Functionality", TAG + " getFilter :");
        if (teamsListAdapter!=null){
            teamsListAdapter.getFilter().filter(search);}
    }

    private void openChatActivity(String username, String CometchatIdOfSender, int status, String teamId, String memberId) {
        Log.i(TAG, "openChatActivity: " +
                " UserName " + username
                + " CometchatIdOfSender " + CometchatIdOfSender
                + " status" + status
                + " teamId " + teamId
                + " memberId " + memberId
        );
        SharedPreferences sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("membersIds", CometchatIdOfSender);
        editor.putString("teamIds", teamId);
        editor.apply();
        Intent intent = new Intent(getContext(), CometChatMessageListActivity.class);
        intent.putExtra(StringContract.IntentStrings.NAME, username);
        intent.putExtra(StringContract.IntentStrings.UID, CometchatIdOfSender);
        intent.putExtra(StringContract.IntentStrings.STATUS, status);
        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
        intent.putExtra("GID", Preferences.get(General.GROUP_ID));
        intent.putExtra(StringContract.IntentStrings.TABS, "3");
        intent.putExtra("teamId", teamId);
        intent.putExtra("membersIds", memberId);
        startActivity(intent);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    // make call to fetch all teams
    private void getTeams() {
        String team_type = "1";
        if (!al_friends.isEmpty()) {
            al_friends.clear();
        }
        if (!al_search_friend.isEmpty()) {
            al_search_friend.clear();
        }
        al_friends = PerformGetTeamsTask.getMyteam(Actions_.ALL_TEAMS_CHAT, team_type, getActivity(), TAG, false, getActivity());
        Log.i(TAG, "getTeams: teams array " + al_friends);

        for (Teams_ teams_ : al_friends ){
            ArrayList<Members_> arrayList=teams_.getMembersArrayList();
            for (Members_ members_ : arrayList){

                Log.i(TAG, "getTeam : user name "+members_.getFullname());
                Log.i(TAG, "getTeam : user status"+members_.getStatus());
            }
        }

        if (al_friends.size() == 0) {
            errorLayout.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.GONE);
            ll_cardview_actions.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.VISIBLE);
            al_search_friend.addAll(al_friends);
            tv_team_count.setText("No. of Teams : "+al_friends.size());
            teamsListAdapter = new MyTeamsChatExpandableListAdapter(getContext(), al_friends, al_search_friend, this, getActivity());
            expandableListView.setAdapter(teamsListAdapter);
            checkIntent();
        }
    }

    /*public void performSearch() {
        searchTeamsArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }

        for (Teams_ teamsItem : teamsArrayList) {
            if (teamsItem.getName() != null && teamsItem.getName().toLowerCase().contains(searchText.toLowerCase())) {
                searchTeamsArrayList.add(teamsItem);
            }
        }

        if (searchTeamsArrayList.size() > 0) {
            showError(false, 1);
            TeamsChatExpandableListAdapter teamsListAdapter = new TeamsChatExpandableListAdapter(getContext(), searchTeamsArrayList, this, getActivity());
            expandableListView.setAdapter(teamsListAdapter);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);
        }
    }*/

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

    /*after clicking on my team member item this method will execute
    * Here all the data is maintain for members type like provider, member and normal users
    * added by rahul maske */
    @Override
    public void onMemberRelativeLayoutClicked(int memberPosition, Teams_ team_) {

        Preferences.save(General.BANNER_IMG, team_.getBanner());
        Preferences.save(General.TEAM_ID, team_.getId());
        Preferences.save(General.TEAM_NAME, team_.getName());
        Log.i(TAG, "onMemberRelativeLayoutClicked: 1 getComet_chat_id"+team_member_id );
        Log.i(TAG, "onMemberRelativeLayoutClicked: 1 team_provider"+team_member_id );

        String[] ProviderArray = team_provider.split(",");
        String[] MemberArray = team_member_id.split(",");

        if (Arrays.asList(ProviderArray).contains( team_.getMembersArrayList().get(memberPosition).getComet_chat_id())){
            //if user is provider
            getUserDetails(team_.getMembersArrayList().get(memberPosition).getComet_chat_id(), team_, memberPosition,"provider");
            Log.i(TAG, "onMemberRelativeLayoutClicked: this is provider --> "+team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
        }else if (Arrays.asList(MemberArray).contains( team_.getMembersArrayList().get(memberPosition).getComet_chat_id())){
            //if user is member selected by admin dashboard
            getUserDetails(team_.getMembersArrayList().get(memberPosition).getComet_chat_id(), team_, memberPosition,"member");
            Log.i(TAG, "onMemberRelativeLayoutClicked: This is member --> "+team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
        }else{
            //if user is normal user
            Log.i(TAG, "onMemberRelativeLayoutClicked: ");
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

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);
            tv_error.setText(GetErrorResources.getMessage(status, getContext()));
            iv_errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            expandableListView.setVisibility(View.VISIBLE);
        }
    }

    public void getUserDetails(String providerId, Teams_ team_, int memberPosition,String UserType) {
        CometChat.getUser(providerId, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "getUserDetails -> user details : " + user.toString());
                String statuss = user.getStatus();

                if (statuss.equals("online")) {
                    Log.i(TAG, "getUserDetails -> status if online -> " + statuss);
                    if (UserType.equalsIgnoreCase("provider")) {
                        showOnlineDialog(team_, memberPosition);
                    }else{
                        //Send direct Inside chat screen
                        final String CometchatIdOfSender = String.valueOf(team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
                        final String username = team_.getMembersArrayList().get(memberPosition).getUsername();
                        final int status = team_.getMembersArrayList().get(memberPosition).getStatus();
                        openChatActivity(""+username,
                                ""+CometchatIdOfSender,
                                status,
                                ""+String.valueOf(team_.getId()),
                                "" + CometchatIdOfSender);
                    }
                } else {
                    if (UserType.equalsIgnoreCase("provider")) {
                        Log.i(TAG, "getUserDetails -> status if offline  -> " + statuss);
                        checkProviderAvailableTime(team_, memberPosition);
                    }else{
                        checkOtherMemberAvailable(team_,memberPosition);
                    }
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "User details fetching failed with exception: " + e.getMessage());
            }
        });
    }

    private void checkProviderAvailableTime(Teams_ team_, int memberPosition) {
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
        if (success.equalsIgnoreCase("success") || success.equalsIgnoreCase("fail")) {
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
                        Log.i(TAG, "showDialog onClick: FullName "+team_.getMembersArrayList().get(position).getFullname());
                        Log.i(TAG, "showDialog onClick: Uid "+team_.getMembersArrayList().get(position).getComet_chat_id());
                        Log.i(TAG, "showDialog onClick: image "+team_.getMembersArrayList().get(position).getPhoto());
                        Log.i(TAG, "showDialog onClick: status "+team_.getMembersArrayList().get(position).getStatus());
                       /* intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                        intent.putExtra(StringContract.IntentStrings.NAME, (team_.getMembersArrayList().get(position).getFullname()));
                        intent.putExtra(StringContract.IntentStrings.UID, (team_.getMembersArrayList().get(position).getComet_chat_id()));
                        intent.putExtra(StringContract.IntentStrings.AVATAR, (team_.getMembersArrayList().get(position).getPhoto()));
                        intent.putExtra(StringContract.IntentStrings.STATUS, (team_.getMembersArrayList().get(position).getStatus()));
                        intent.putExtra(StringContract.IntentStrings.TABS, "3");
                        getActivity().startActivity(intent);*/
                        final String CometchatIdOfSender = String.valueOf(team_.getMembersArrayList().get(position).getComet_chat_id());
                        final String username = team_.getMembersArrayList().get(position).getUsername();
                        final int status = team_.getMembersArrayList().get(position).getStatus();
                        Log.i(TAG, "onClick: CometchatIdOfSender "+CometchatIdOfSender +" username "+username +" status "+status+" team_.getId() "
                                +team_.getId()  +"CometchatIdOfSender "+CometchatIdOfSender);
                        openChatActivity("" + username,
                                "" + CometchatIdOfSender,
                                status,
                                "" + String.valueOf(team_.getId()),
                                "" + CometchatIdOfSender);
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
                        Log.i(TAG, "onClick: CometchatIdOfSender "+CometchatIdOfSender +" username "+username +" status "+status+" team_.getId() "
                                +team_.getId()  +"CometchatIdOfSender "+CometchatIdOfSender);
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

    public void refreshFragment() {

        try {
            if (getLoaderManager().getLoader(CHATS_LOADER) != null) {

                getLoaderManager().restartLoader(CHATS_LOADER, null, this);
            } else {
                getLoaderManager().initLoader(CHATS_LOADER, null, this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String selection;
        String[] selectionArgs;

        switch (id) {
            case CHATS_LOADER:

            case CHATS_SEARCH_LOADER:

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            errorLayout.setVisibility(View.VISIBLE);
        } else {
            errorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }


    /*execute only firebase message received
     * for redirecting manually it will call onMemberRelativeLayoutClicked (after clicking on my team member item)*/
    //team_logs_id = memberId + "_-" + teamId + "_-" + 4;

    /*created this method for redirecting pushnotification message to chat screen
     * created by rahulmsk */
    private void checkIntent() {
        if (preferenOpenActivity.getBoolean("checkIntent", false)) {
            if (getActivity() != null && getActivity().getIntent().hasExtra("receiverType")) {
                String receiverType = getActivity().getIntent().getStringExtra("receiverType");
                if (receiverType.equals("user")) {
                    String team_logs_id = getActivity().getIntent().getStringExtra("team_logs_id");
                    if (team_logs_id != null && !team_logs_id.isEmpty()) {
                        if (team_logs_id.endsWith("4")) {
                            String ids[] = team_logs_id.split(Pattern.quote("_"));
                            Log.i(TAG, "checkIntent: teamIdIs " + Integer.parseInt(ids[2].substring(1)));
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


                        /*openChatActivity(""+username,
                                ""+CometchatIdOfSender,
                                status,
                                ""+String.valueOf(team_.getId()),
                                "" + team_.getMembersArrayList().get(memberPosition).getUser_id());*/
                        }
                    }

                    //below code is commented by rahul msk
                    //Either tab 1 or 3 or 4
                /*// Code By Debopam
                String team_logs_id = getActivity().getIntent().getStringExtra("team_logs_id");
                if (team_logs_id != null && !team_logs_id.isEmpty()) {
                    if (team_logs_id.endsWith("4")) {
                        String ids[] = team_logs_id.split(Pattern.quote("_"));
                        if (ids.length == 4) {
                            for (int i = 0; i < al_friends.size(); i++) {
                                Teams_ team = al_friends.get(i);
                                if (team.getId() == Integer.parseInt(ids[2].substring(1))) {
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
                }*/
                }
            }
        }
    }

    /*In this method we are fetching provider ids and member ids of particular team on which user clicked
     * created by rahul maske*/

    @Override
    public void onTeamClickFetchTeamData(Teams_ item) {
        Runnable runnableUserArray = new Runnable() {
            @Override
            public void run() {
                SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
                String UserId = UserInfoForUIKitPref.getString(screen.messagelist.General.USER_ID, null);
                SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
                String DomainURL = domainUrlPref.getString(screen.messagelist.General.DOMAIN, null);
                String url = Urls_.MOBILE_COMET_CHAT_TEAMS;
                HashMap<String, String> requestMap = new HashMap<>();
                requestMap.put(screen.messagelist.General.ACTION, "get_team_members_array");
                requestMap.put(screen.messagelist.General.TEAM_ID, "" + item.getId());
                requestMap.put(screen.messagelist.General.USER_ID, UserId);
               // Log.i(TAG, "onTeamClickFetchTeamData: User Id " + UserId);
                RequestBody requestBody = NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());
                Log.i(TAG, "onTeamClickFetchTeamData: Domain " + DomainURL + url);
                try {
                    if (requestBody != null) {
                        String response = NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                        if (response != null) {

                           // Log.i(TAG, "onTeamClickFetchTeamData:  response " + response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray get_team_members_array = jsonObject.getJSONArray("get_team_members_array");
                            team_member_id = get_team_members_array.getJSONObject(0).getString("team_member_id");
                            team_provider = get_team_members_array.getJSONObject(0).getString("team_provider");
                        } else {
                           // Log.i(TAG, "checkMemberList:  null  ");
                        }
                    } else {
                        //Log.i(TAG, "checkMemberList:  null2");
                    }
                } catch (Exception e) {
                   // Log.i(TAG, "checkMemberList: " + e.getMessage());
                    e.printStackTrace();
                }

            }
        };
        Thread thread=new Thread(runnableUserArray);
        thread.start();
    }
}

