
package com.modules.cometchat_7_30;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import utils.AppLog;

import static android.content.Context.MODE_PRIVATE;

public class CometChatJoinTeamFragment extends Fragment implements JoinChatExpandableListAdapter.JoinChatExpandableListAdapterListener {
    private static final String TAG = CometChatJoinTeamFragment.class.getSimpleName();
    private static ExpandableListView expandableListView;
    private JoinChatExpandableListAdapter teamsListAdapter;
    public ArrayList<Teams_> primaryList = new ArrayList<>();
    public ArrayList<Teams_> searchList = new ArrayList<>();
    private static LinearLayout errorLayout;
    private EditText editTextSearch;
    private TextView errorText, tv_team_count;
    private LinearLayout cardview_actions;
    private AppCompatImageView errorIcon;
    SharedPreferences preferenOpenActivity;
    AlertDialog alert;
    private String tabs="4";
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
        tv_team_count = view.findViewById(R.id.tv_team_count);
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

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                //Toast.makeText(getActivity(), ""+primaryList.get(groupPosition).getMembersArrayList().get(childPosition).getUsername(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onChildClick: "+primaryList.get(groupPosition).getMembersArrayList().get(childPosition).getUsername());
                onMemberRelativeLayoutClicked(childPosition,primaryList.get(groupPosition));
                return false;
            }
        });

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
        if (teamsListAdapter != null) {
            teamsListAdapter.getFilter().filter(search);
        }
    }

    // open chat window join team
    private void openChatActivity(String username, String userId, int status, String teamId, String memberId) {
        SharedPreferences sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("membersIds", memberId);
        editor.putString("teamIds", teamId);
        editor.apply();
        Log.e("userName", username);
        AppLog.i(TAG, "openChatActivity: Username "+username);
        AppLog.i(TAG, "openChatActivity: UID "+userId);
        AppLog.i(TAG, "openChatActivity: STATUS "+status);
        AppLog.i(TAG, "openChatActivity: teamId "+teamId);
        AppLog.i(TAG, "openChatActivity: memberId "+memberId);
        Intent intent = new Intent(getContext(), CometChatMessageListActivity.class);
        intent.putExtra(StringContract.IntentStrings.NAME, username);
        intent.putExtra(StringContract.IntentStrings.UID, userId);
        intent.putExtra(StringContract.IntentStrings.STATUS, ""+status);
        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
        intent.putExtra(StringContract.IntentStrings.TABS, tabs);
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
        String team_type = "2";
        if (!primaryList.isEmpty()) {
            primaryList.clear();
        }
        if (!searchList.isEmpty()) {
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
//            AppLog.i(TAG , "Members id is " + primaryList.get(0).getMembersArrayList().get(0).getComet_chat_id());
            tv_team_count.setText("No. of Teams : " + primaryList.size());
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

    /*after clicking on my team member item this method will execute
     * Here all the data is maintain for members type like provider, member and normal users
     * added by rahul maske */
    @Override
    public void onMemberRelativeLayoutClicked(int memberPosition, Teams_ team_) {
       /* Log.e(TAG, "position: " + memberPosition + " user name: " + team_.getMembersArrayList().get(memberPosition).getUsername());
        Log.e(TAG, "team id: " + team_.getId() + " team name: " + team_.getName());
        Log.e(TAG, "member id: " + team_.getMembersArrayList().get(memberPosition).getComet_chat_id());*/

        Preferences.save(General.BANNER_IMG, team_.getBanner());
        Preferences.save(General.TEAM_ID, team_.getId());
        Preferences.save(General.TEAM_NAME, team_.getName());

        String[] ProviderArray = team_provider.split(",");
        String[] MemberArray = team_member_id.split(",");
        AppLog.i(TAG, "onMemberRelativeLayoutClicked: team_provider"+team_provider +" team_member_id "+team_member_id);
        AppLog.i(TAG, "onMemberRelativeLayoutClicked: clicked user id "+team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
        if (Arrays.asList(ProviderArray).contains( team_.getMembersArrayList().get(memberPosition).getComet_chat_id())){
            //if user is provider
            getUserDetails(team_.getMembersArrayList().get(memberPosition).getComet_chat_id(), team_, memberPosition,"provider");
            AppLog.i(TAG, "onMemberRelativeLayoutClicked: this is provider --> "+team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
        }else if (Arrays.asList(MemberArray).contains( team_.getMembersArrayList().get(memberPosition).getComet_chat_id())){
            //if user is member selected by admin dashboard
            getUserDetails(team_.getMembersArrayList().get(memberPosition).getComet_chat_id(), team_, memberPosition,"member");
            AppLog.i(TAG, "onMemberRelativeLayoutClicked: This is member --> "+team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
        }
        else{
            //if user is normal user
            AppLog.i(TAG, "onMemberRelativeLayoutClicked: ");
            final String CometchatIdOfSender = String.valueOf(team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
            final String username = team_.getMembersArrayList().get(memberPosition).getUsername();
            final int status = team_.getMembersArrayList().get(memberPosition).getStatus();

            openChatActivity(""+username,
                    ""+CometchatIdOfSender,
                    status,
                    ""+String.valueOf(team_.getId()),
                    "" + CometchatIdOfSender);
        }

       /* for (String providerId : ProviderArray) {
            //  If provider
            AppLog.i(TAG, "onMemberRelativeLayoutClicked checking new : 2 from provider id" + providerId +
                    " from list clicked" + team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
            String[] spitId = providerId.split("_");
            AppLog.i(TAG, "onMemberRelativeLayoutClicked checking new : memberCometchatId"+team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
            if (spitId[0].equals("" + team_.getMembersArrayList().get(memberPosition).getUser_id())) {
                AppLog.i(TAG, "onMemberRelativeLayoutClicked: 3 -" + spitId[0]);
                getUserDetails(providerId, team_, memberPosition);
                //if (team_.getMembersArrayList().get(memberPosition).getStatus())
            } else {
                //if not provider check member list
                String[] spitIdMemberList = providerId.split("_");
                if (spitIdMemberList[0].equals("" + team_.getMembersArrayList().get(memberPosition).getUser_id())) {
                    //checkMemberList(providerId, team_, memberPosition);
                    checkOtherMemberAvailable(team_, memberPosition);
                } else {
                    final String CometchatIdOfSender = String.valueOf(team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
                    final String username = team_.getMembersArrayList().get(memberPosition).getUsername();
                    final int status = team_.getMembersArrayList().get(memberPosition).getStatus();

                    openChatActivity("" + username,
                            "" + CometchatIdOfSender,
                            status,
                            "" + String.valueOf(team_.getId()),
                            "" + CometchatIdOfSender);
                }
            }
        }*/



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
        AppLog.i(TAG, "checkOtherMemberAvailable: 1");
        String url = Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();

        requestMap.put(screen.messagelist.General.ACTION, "other_members_time_check_in_db");
        requestMap.put(screen.messagelist.General.USER_ID, "" + UserId);
        requestMap.put(screen.messagelist.General.RECEIVER_ID, "" + team_.getMembersArrayList().get(position).getUser_id());
        //team_.getMembersArrayList().get(memberPosition).getUser_id()

        RequestBody requestBody = NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());

        try {
            if (requestBody != null) {
                String response = NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                if (response != null) {
                    AppLog.i(TAG, "checkOtherMemberAvailable:  response " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray provider_time_check_in_db = jsonObject.getJSONArray("other_members_time_check_in_db");
                    String success = provider_time_check_in_db.getJSONObject(0).getString("Success");
                    if (success.equalsIgnoreCase("success") || success.equalsIgnoreCase("fail")) {

                        final String CometchatIdOfSender = String.valueOf(team_.getMembersArrayList().get(position).getComet_chat_id());
                        final String username = team_.getMembersArrayList().get(position).getUsername();
                        final int status = team_.getMembersArrayList().get(position).getStatus();
                        AppLog.i(TAG, "checkOtherMemberAvailable: CometchatIdOfSender "+CometchatIdOfSender);
                        AppLog.i(TAG, "checkOtherMemberAvailable: teamId "+team_.getId());
                        openChatActivity("" + username,
                                "" + CometchatIdOfSender,
                                status,
                                "" + team_.getId(),
                                "" + CometchatIdOfSender);
                    } else {
                        showDialogForMember(success, position, team_);
                    }
                } else {
                    AppLog.i(TAG, "checkOtherMemberAvailable:  null  ");
                }
            } else {
                AppLog.i(TAG, "checkOtherMemberAvailable:  null2");
            }
        } catch (Exception e) {
            AppLog.i(TAG, "checkOtherMemberAvailable: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showOnlineDialogForProvider(Teams_ team_, int memberPosition) {
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

    private void showDialogForOfflineProvider(Teams_ team_, int memberPosition) {
        AppLog.i(TAG, " onMemberRelativeLayoutClicked: reached showDialogForOfflineProvider:");
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        //Setting message manually and performing action on button click
        builder.setMessage("Provider staff - "+team_.getMembersArrayList().get(memberPosition).getUsername() +" is not available at present. Please call 911 for emergency issues. \n" +
                "\n" +
                "Do you still want to leave a message for them? ")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        final String CometchatIdOfSender = String.valueOf(team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
                        final String username = team_.getMembersArrayList().get(memberPosition).getUsername();
                        final int status = team_.getMembersArrayList().get(memberPosition).getStatus();
                        alert.dismiss();
                        openChatActivity(""+username,
                                ""+CometchatIdOfSender,
                                status,
                                ""+String.valueOf(team_.getId()),
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

    public void getUserDetails(String providerId, Teams_ team_, int memberPosition, String UserType) {
        CometChat.getUser(providerId, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "getUserDetails -> user details : " + user.toString());
                String statuss = user.getStatus();
                if (UserType.equalsIgnoreCase("provider")){
                    if (statuss.equals("online")) {

                        showOnlineDialogForProvider(team_, memberPosition);
                    }else{
                        AppLog.i(TAG, "getUserDetails -> status if offline  -> " + statuss);
                        /*below code is running code for this checkProviderTimeOnServer() method
                         * and previously we were checking the data on server for the offiline user when that user is available
                         * but now after discuss with team we are showing static message for offline user
                         * commented by rahul maske
                         *  */
                        checkProviderTimeOnServer(team_, memberPosition);

                        //showDialogForOfflineProvider(team_,memberPosition);
                    }
                }else{
                    /*if this is member*/
                    if (statuss.equals("online")) {
                        //Send direct Inside chat screen
                        final String CometchatIdOfSender = String.valueOf(team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
                        final String username = team_.getMembersArrayList().get(memberPosition).getUsername();
                        final int status = team_.getMembersArrayList().get(memberPosition).getStatus();
                        openChatActivity(""+username,
                                ""+CometchatIdOfSender,
                                status,
                                ""+String.valueOf(team_.getId()),
                                "" + CometchatIdOfSender);
                    }else{

                        /*below code is running code for this checkOtherMemberAvailable(team_,memberPosition) method
                         * and previously we were checking the data on server for the offiline user when that user is available
                         * but now after discuss with team we are showing static message for offline user
                         * commented by rahul maske
                         *  */
                        checkOtherMemberAvailable(team_,memberPosition);
                        //showDialog("",memberPosition,team_);
                    }
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "User details fetching failed with exception: " + e.getMessage());
            }
        });
    }

    private void checkProviderTimeOnServer(Teams_ team_, int memberPosition) {
        SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(screen.messagelist.General.USER_ID, null);
        SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String DomainURL = domainUrlPref.getString(screen.messagelist.General.DOMAIN, null);
        AppLog.i(TAG, "checkProviderAvailableTime: 1");
        String url = Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();

        requestMap.put(screen.messagelist.General.ACTION, "provider_time_check_in_db");
        requestMap.put(screen.messagelist.General.USER_ID, "" + UserId);
        requestMap.put(screen.messagelist.General.RECEIVER_ID, "" + team_.getMembersArrayList().get(memberPosition).getComet_chat_id());

        RequestBody requestBody = NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());
        AppLog.i(TAG, "checkProviderAvailableTime: 2");
        AppLog.i(TAG, "checkProviderAvailableTime: Domain " + DomainURL + url);
        try {
            if (requestBody != null) {
                String response = NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                if (response != null) {
                    AppLog.i(TAG, "checkProviderAvailableTime:  response " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray provider_time_check_in_db = jsonObject.getJSONArray("provider_time_check_in_db");
                    String success = provider_time_check_in_db.getJSONObject(0).getString("Success");
                    showDialog(success, memberPosition, team_);
                } else {
                    AppLog.i(TAG, "checkProviderAvailableTime:  null  ");
                }
            } else {
                AppLog.i(TAG, "checkProviderAvailableTime:  null2");
            }
        } catch (Exception e) {
            AppLog.i(TAG, "checkProviderAvailableTime: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void showDialog(String success, int position, Teams_ team_) {
        AppLog.i(TAG, "showDialog: ");
        final String CometchatIdOfSender = String.valueOf(team_.getMembersArrayList().get(position).getComet_chat_id());
        final String username = team_.getMembersArrayList().get(position).getUsername();
        final int status = team_.getMembersArrayList().get(position).getStatus();
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
       //alert = builder.create();
        String message;
        if (success.equalsIgnoreCase("success") || success.equalsIgnoreCase("fail")) {
            message = "Are you sure you want chat with provider?";
        } else {
            message = success;
        }
      /*  String message = "The user "+username+" is not available at present. Please call 911 for emergency issues.\n" +
                "\n" +
                "Do you still want to leave a message?";*/
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AppLog.i(TAG, "onClick: team inclued data "+ team_.toString());
                        //team_.getMembersArrayList().get(memberPosition).getComet_chat_id()
                        alert.dismiss();
                        openChatActivity(""+username,
                                ""+CometchatIdOfSender,
                                status,
                                ""+String.valueOf(team_.getId()),
                                "" + CometchatIdOfSender);

                        //getActivity().startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       alert.dismiss();

                    }
                });

        //Setting the title manually
        alert = builder.create();
        //Setting the title manually
        alert.setTitle("Alert Notification");
        alert.show();
    }

    private void showDialogForMember(String success, int position, Teams_ team_) {
        AppLog.i(TAG, "showDialog: ");
        final String CometchatIdOfSender = String.valueOf(team_.getMembersArrayList().get(position).getComet_chat_id());
        final String username = team_.getMembersArrayList().get(position).getUsername();
        final int status = team_.getMembersArrayList().get(position).getStatus();
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());

      /*  String message = "The user "+username+" is not available at present. Please call 911 for emergency issues.\n" +
                "\n" +
                "Do you still want to leave a message?";*/
        builder.setMessage(success)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AppLog.i(TAG, "onClick: team inclued data "+ team_.toString());
                        //team_.getMembersArrayList().get(memberPosition).getComet_chat_id()
                        alert.dismiss();
                        openChatActivity(""+username,
                                ""+CometchatIdOfSender,
                                status,
                                ""+String.valueOf(team_.getId()),
                                "" + CometchatIdOfSender);

                        //getActivity().startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alert.dismiss();

                    }
                });

        //Setting the title manually
        alert = builder.create();
        //Setting the title manually
        alert.setTitle("Alert Notification");
        alert.show();
    }

    @Override
    public void onTeamClickFetchTeamData(int team_Id)
    {
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
                String UserId = UserInfoForUIKitPref.getString(screen.messagelist.General.USER_ID, null);
                SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
                String DomainURL = domainUrlPref.getString(screen.messagelist.General.DOMAIN, null);
                String url = Urls_.MOBILE_COMET_CHAT_TEAMS;

                HashMap<String, String> requestMap = new HashMap<>();
                requestMap.put(screen.messagelist.General.ACTION, "get_team_members_array");
                requestMap.put(screen.messagelist.General.TEAM_ID, "" + team_Id);
                requestMap.put(screen.messagelist.General.USER_ID, UserId);

                RequestBody requestBody = NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());

                try {
                    if (requestBody != null) {
                        String response = NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                        if (response != null) {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray get_team_members_array = jsonObject.getJSONArray("get_team_members_array");
                            team_member_id = get_team_members_array.getJSONObject(0).getString("team_member_id");
                            team_provider = get_team_members_array.getJSONObject(0).getString("team_provider");
                            AppLog.i(TAG, " run: onTeamClickFetchTeamData team_member_id "+team_member_id+" team_provider "+team_provider +"team_id "+team_Id);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread=new Thread(runnable);
        thread.start();
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
                    String ids[] = team_logs_id.split(Pattern.quote("_"));
                    onTeamClickFetchTeamData(Integer.parseInt(ids[2].substring(1)));
                    if (team_logs_id != null && !team_logs_id.isEmpty()) {
                       /* if (team_logs_id.endsWith("3")) {*/
                        if (team_logs_id.endsWith("4")) {
                            tabs="4";
                        }else  if (team_logs_id.endsWith("3")){
                            tabs="3";
                        }


                            AppLog.i(TAG, "checkIntent: teamIdIs " + ids[2].substring(1));
                            AppLog.i(TAG, "checkIntent: memberId: " + ids[0].substring(0));

                            SharedPreferences.Editor editor = preferenOpenActivity.edit();
                            editor.putBoolean("checkIntent", false);
                            editor.apply();
                        /*This working code and with this code we were directly opening the user chat and skipping the set availability scinario
                         * but we need set availability scinario so we are commenting this code and opening activity with set availability all scinario
                         * commented and created by rahul maske on 10-07-2021
                         * */
                        /*openChatActivity(
                                    "" + getActivity().getIntent().getStringExtra("username"),
                                    "" + getActivity().getIntent().getStringExtra("sender"),
                                    0,
                                    "" + Integer.parseInt(ids[2].substring(1)),
                                    "" + ids[0].substring(0));*/
                      /*  }*/

                        for (Teams_ teams_ : primaryList){
                            AppLog.i(TAG, "team id "+teams_.getId() +" team name "+teams_.getName());
                            if(ids[2].substring(1).equalsIgnoreCase(String.valueOf(teams_.getId()))){

                                ArrayList<Members_> membersArrayList= teams_.getMembersArrayList();
                                for(int i=0; i< membersArrayList.size(); i++){
                                    if (membersArrayList.get(i).getComet_chat_id().equalsIgnoreCase( getActivity().getIntent().getStringExtra("sender"))) {
                                        Handler handler=new Handler();
                                        int finalI = i;
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                onMemberRelativeLayoutClicked(finalI, teams_);
                                            }
                                        },1500);

                                    }
                                }
                                break;
                            }
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



