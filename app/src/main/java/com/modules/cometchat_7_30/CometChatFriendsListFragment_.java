package com.modules.cometchat_7_30;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

import com.cometchat.pro.rtc.Constants;
import com.google.gson.JsonObject;
import com.sagesurfer.adapters.FriendListAdapter;

import com.sagesurfer.collaborativecares.R;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import constant.StringContract;
import okhttp3.RequestBody;
import screen.messagelist.CometChatMessageListActivity;
import screen.messagelist.General;
import screen.messagelist.NetworkCall_;
import screen.messagelist.Urls_;
import utils.FontUtils;

import static android.content.Context.MODE_PRIVATE;
import static com.modules.mood.ConstantMood.context;

/**
 * @author kishor k
 * Created on 13/11/2020
 */

public class CometChatFriendsListFragment_ extends Fragment {
    private static final String TAG = CometChatFriendsListFragment_.class.getSimpleName();
    private RecyclerView rv_conversionList;
    private FriendListAdapter adapter;
    private EditText ed_search_friend;
    private TextView friend_list_error;
    private LinearLayout cardview_actions;
    SharedPreferences preferenOpenActivity;
    public List<User> filteredNameList = new ArrayList<>();
    public List<User> friendList = new ArrayList<>();
    LocalBroadcastManager bm;
    public CometChatFriendsListFragment_() {
    }

    public static CometChatFriendsListFragment_ newInstance() {
        CometChatFriendsListFragment_ fragment = new CometChatFriendsListFragment_();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        rv_conversionList = view.findViewById(R.id.friend_conversion_list);
        ed_search_friend = view.findViewById(R.id.ed_search_friend);
        friend_list_error = view.findViewById(R.id.friend_conversion_error);
        cardview_actions = view.findViewById(R.id.cardview_actions);
        // friend conversion search action
        Log.i(TAG, "onCreateView: ");

        bm = LocalBroadcastManager.getInstance(requireActivity());
        IntentFilter actionReceiver = new IntentFilter();
        actionReceiver.addAction("ActionFriend");
        bm.registerReceiver(onJsonReceived , actionReceiver);

        ed_search_friend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchFriend(s.toString());
            }
        });

        /*created by rahul to know this fragment is open
         * if it is open and id any messages comes from firebase then counter should change */
        SharedPreferences preferencesCheckCurrentActivity = getActivity().getSharedPreferences("preferencesCheckCurrentActivity", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesCheckCurrentActivity.edit();
        editor.putBoolean("IsFriendListingPage", true);
        editor.apply();

        preferenOpenActivity = getActivity().getSharedPreferences("sp_check_push_intent", MODE_PRIVATE);
        Log.i(TAG, "onCreateView: "+preferenOpenActivity.getBoolean("checkIntent",false));
        return view;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    private BroadcastReceiver onJsonReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String sender = intent.getStringExtra("sender");
                if(adapter!=null) {
                    adapter.changeUnreadCount(sender);
                /*try {
                    JSONObject data = new JSONObject(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                    Log.i(TAG, "onReceive: broadcast");
                }
            }
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        SharedPreferences preferencesCheckCurrentActivity = getActivity().getSharedPreferences("preferencesCheckCurrentActivity", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesCheckCurrentActivity.edit();
        editor.putBoolean("IsFriendListingPage", false);
        editor.apply();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");

        //friendList.clear();
        // get all friend list from comet chat
        getFriendsList();

    }
    /*fetching friend list*/
    public void getFriendsList() {
        if (!friendList.isEmpty())
            friendList.clear();
        UsersRequest usersRequest = null;
        int limit = 30;
        usersRequest = new UsersRequest.UsersRequestBuilder().friendsOnly(true).setLimit(limit).hideBlockedUsers(true).build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> list) {
                if (!list.isEmpty()) {
                    ArrayList<ModelUserCount> al_unreadCountList = new ArrayList<>();
                    Log.e(TAG, "User list received: " + list.toString());
                    // set all friend list to friend adapter
                    friendList.addAll(list);
                    Log.i(TAG, "onSuccess: friendList " + friendList.toString());
                    filteredNameList.addAll(list);
                    cardview_actions.setVisibility(View.VISIBLE);
                    adapter = new FriendListAdapter(CometChatFriendsListFragment_.this, getContext(), friendList, al_unreadCountList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    rv_conversionList.setLayoutManager(mLayoutManager);
                    rv_conversionList.setItemAnimator(new DefaultItemAnimator());
                    rv_conversionList.setAdapter(adapter);

                    checkIntent();

                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "User list fetching failed with exception: " + e.getMessage());
            }
        });
    }


    // friend search filter
    private void searchFriend(String search) {
        if (adapter != null) {
            adapter.getFilter().filter(search);
        }
    }

    // Code by Debopam
    private void checkIntent() {//preferenOpenActivity.getBoolean("checkIntent",false)
        Log.i(TAG, "checkIntent: 1"+preferenOpenActivity.getBoolean("checkIntent",false));
        if (preferenOpenActivity.getBoolean("checkIntent",false)) {
            Log.i(TAG, "checkIntent: 2");
            if (getActivity() != null && getActivity().getIntent().hasExtra("receiverType")) {
                Log.i(TAG, "checkIntent: 3");
                String receiverType = getActivity().getIntent().getStringExtra("receiverType");
                if (receiverType.equals("user"))
                    Log.i(TAG, "checkIntent: 4");{
                    // tab 1
                    String team_logs_id = getActivity().getIntent().getStringExtra("team_logs_id");
                    if (team_logs_id.equals("0") || team_logs_id.isEmpty()) {
                        String sender = getActivity().getIntent().getStringExtra("sender");
                        Log.i(TAG, "checkIntent: Sender " + sender);
                        for (int i = 0; i < filteredNameList.size(); i++) {
                            if (filteredNameList.get(i).getUid().equals(sender)) {
                                Log.i(TAG, "checkIntent: " + filteredNameList.get(i).getUid());
                                SharedPreferences.Editor editor = preferenOpenActivity.edit();
                                editor.putBoolean("checkIntent", false);
                                editor.apply();
                                performAdapterClick(i);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    // The adapter click operation is brought here to handle push notification click
    public void performAdapterClick(int position) {
        User user = filteredNameList.get(position);
        String allProvidersString = Preferences.get("providers");
        //String youth = Preferences.get("youths");
      /*  if (!youth.isEmpty()) {*/

            String ProviderIds[]=allProvidersString.split(",");
            Log.i(TAG, "performAdapterClick: provider id from array "+ProviderIds[0]);

            if (Arrays.asList(ProviderIds).contains(user.getUid())) {
                String status = user.getStatus();
                if (status.equalsIgnoreCase("online")) {
                    Log.i(TAG, "performAdapterClick: 3");
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());
                    //Setting message manually and performing action on button click
                    builder.setMessage("Are you sure you want chat with provider?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    // open comet chat chat form uikit it check provider
                                    Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                                    intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                                    intent.putExtra(StringContract.IntentStrings.NAME, (user.getName()));
                                    intent.putExtra(StringContract.IntentStrings.SENDER_ID, (user.getUid()));
                                    intent.putExtra(StringContract.IntentStrings.AVATAR, (user.getAvatar()));
                                    intent.putExtra(StringContract.IntentStrings.STATUS, (user.getStatus()));
                                    intent.putExtra(StringContract.IntentStrings.TABS, "1");
                                    getActivity().startActivity(intent);
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
                } else {
                    checkProviderAvailableTime(user.getUid(), position);
                    /*Log.i(TAG, "performAdapterClick: 1");
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());
                    //Setting message manually and performing action on button click
                    builder.setMessage("Harsh pro provider staff is unavailable at present. He is available from 06:19 pm to 07:19 pm. Please call 911 for emergency issues.\n" +
                            "\n" + "still you want to chat with this provider?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                                    intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                                    intent.putExtra(StringContract.IntentStrings.NAME, (user.getName()));
                                    intent.putExtra(StringContract.IntentStrings.SENDER_ID, (user.getUid()));
                                    intent.putExtra(StringContract.IntentStrings.AVATAR, (user.getAvatar()));
                                    intent.putExtra(StringContract.IntentStrings.STATUS, (user.getStatus()));
                                    intent.putExtra(StringContract.IntentStrings.TABS, "1");
                                    getActivity().startActivity(intent);
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
                    alert.show();*/
                }
            } else {
                checkMemberList(user,position );
            }
        }
  /*  }*/

    private void checkMemberList(User user, int position) {
        SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(General.USER_ID, null);
        SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String DomainURL = domainUrlPref.getString(General.DOMAIN, null);
        String url = Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "get_admin_set_role");
        requestMap.put(General.IS_TEST_USER, ""+0);
        requestMap.put(General.USER_ID,UserId );

        Log.i(TAG, "checkMemberList: User Id "+UserId);
        RequestBody requestBody = NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());
        Log.i(TAG, "checkMemberList: Domain "+DomainURL+url);
        try {
            if (requestBody != null) {
                String response = NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                if (response != null) {
                    Log.i(TAG, "checkMemberList:  response " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray provider_time_check_in_db = jsonObject.getJSONArray("get_admin_set_role");
                    String other_user_id = provider_time_check_in_db.getJSONObject(0).getString("other_user_id");
                    Log.i(TAG, "checkMemberList: user cometchat id "+user.getUid());
                   if (!other_user_id.equals("") || other_user_id != null){
                       String[] arrayMembersId = other_user_id.split(",");
                       List<String> arrayListMembersId = new ArrayList<>(Arrays.asList(arrayMembersId));
                       for (String listItem : arrayListMembersId){
                           Log.i(TAG, "checkMemberList: "+ listItem);
                           if (listItem.equals(""+user.getUid())){
                               Log.i(TAG, "checkMemberList: "+user.getUid() + " is "+user.getStatus());
                               String status=user.getStatus();
                               if (status.equals("online")){
                                   Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                                   intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                                   intent.putExtra(StringContract.IntentStrings.NAME, (user.getName()));
                                   intent.putExtra(StringContract.IntentStrings.SENDER_ID, (user.getUid()));
                                   intent.putExtra(StringContract.IntentStrings.AVATAR, (user.getAvatar()));
                                   intent.putExtra(StringContract.IntentStrings.STATUS, (user.getStatus()));
                                   intent.putExtra(StringContract.IntentStrings.TABS, "1");
                                   getActivity().startActivity(intent);
                               }else{
                                   checkOtherMemberAvailable(user, position);
                               }
                           }
                       }
                   }
                   /* if (success.equals("") || success!=null) {
                        List<String> arrayListMembersId = new ArrayList<>();
                        String[] arrayMembersId = success.split(",");
                        int iteration = 0;
                        for (String item : arrayMembersId) {
                            iteration++;
                            String[] singleIdSplitArray = item.split("_");
                            arrayListMembersId.add("" + singleIdSplitArray[0]);

                            if (iteration==arrayMembersId.length){
                                if(arrayListMembersId.contains(user.getUid())){
                                    String status = user.getStatus();
                                    if (status.equals("online")) {
                                        Log.i(TAG, "checkMemberList: online member");
                                        Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                                        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                                        intent.putExtra(StringContract.IntentStrings.NAME, (user.getName()));
                                        intent.putExtra(StringContract.IntentStrings.SENDER_ID, (user.getUid()));
                                        intent.putExtra(StringContract.IntentStrings.AVATAR, (user.getAvatar()));
                                        intent.putExtra(StringContract.IntentStrings.STATUS, (user.getStatus()));
                                        intent.putExtra(StringContract.IntentStrings.TABS, "1");
                                        getActivity().startActivity(intent);
                                    } else {
                                        Log.i(TAG, "checkMemberList: offline member");
                                        checkOtherMemberAvailable(user, position);
                                    }
                                }else{
                                    Log.i(TAG, "checkMemberList: not in member list");
                                    Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                                    intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                                    intent.putExtra(StringContract.IntentStrings.NAME, (user.getName()));
                                    intent.putExtra(StringContract.IntentStrings.SENDER_ID, (user.getUid()));
                                    intent.putExtra(StringContract.IntentStrings.AVATAR, (user.getAvatar()));
                                    intent.putExtra(StringContract.IntentStrings.STATUS, (user.getStatus()));
                                    intent.putExtra(StringContract.IntentStrings.TABS, "1");
                                    getActivity().startActivity(intent);
                                }
                            }
                        }
                    }else {
                        Log.i(TAG, "checkMemberList: if empty member list ");
                        Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                        intent.putExtra(StringContract.IntentStrings.NAME, (user.getName()));
                        intent.putExtra(StringContract.IntentStrings.SENDER_ID, (user.getUid()));
                        intent.putExtra(StringContract.IntentStrings.AVATAR, (user.getAvatar()));
                        intent.putExtra(StringContract.IntentStrings.STATUS, (user.getStatus()));
                        intent.putExtra(StringContract.IntentStrings.TABS, "1");
                        getActivity().startActivity(intent);
                    }*/
                }else {
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

    private void checkProviderAvailableTime(String receiver_id, int position) {
        SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(screen.messagelist.General.USER_ID, null);
        SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String DomainURL = domainUrlPref.getString(screen.messagelist.General.DOMAIN, null);
        Log.i(TAG, "checkProviderAvailableTime: 1");
        String url = Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();

        requestMap.put(screen.messagelist.General.ACTION, "provider_time_check_in_db");
        requestMap.put(screen.messagelist.General.USER_ID, "" + UserId);
        requestMap.put(screen.messagelist.General.RECEIVER_ID, "" + receiver_id);

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
                    showDialog(success, position);
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

    public void insertBlockUserIntoDatabase(String blockedUID) {
        String action = "block_user_insert_db";
        String[] array = blockedUID.split("_");
        String url = Urls_.SAVE_BLOCK_USER_TO_THE_SERVER;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.RECEIVER_ID,  array[0]);
        requestMap.put(General.USER_ID, Preferences.get(com.sagesurfer.constant.General.USER_ID));
        Log.i(TAG, "insertBlockUserIntoDatabase:  receiver_Id  " + array[0] + " user_Id " + Preferences.get(com.sagesurfer.constant.General.USER_ID) + " url " + Preferences.get(com.sagesurfer.constant.General.DOMAIN) + url);
        RequestBody requestBody = NetworkCall_.make(requestMap, Preferences.get(com.sagesurfer.constant.General.DOMAIN) + url, TAG, getActivity());
        Log.e(TAG, "insertBlockUserIntoDatabase: request body " + requestBody);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(Preferences.get(com.sagesurfer.constant.General.DOMAIN) + url, requestBody, TAG, getActivity());
                if (response != null) {
                    Log.i(TAG, "insertBlockUserIntoDatabase:  response " + response);
                }
            } catch (Exception e) {
                Log.i(TAG, "insertBlockUserIntoDatabase: "+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showDialog(String success, int position) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        String message;
        if (success.equalsIgnoreCase("success") || success.equalsIgnoreCase("fail") ){
            message="Are you sure you want chat with provider?";
        }else{
            message=success;
        }
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                        intent.putExtra(StringContract.IntentStrings.NAME, (friendList.get(position).getName()));
                        intent.putExtra(StringContract.IntentStrings.UID, (friendList.get(position).getUid()));
                        intent.putExtra(StringContract.IntentStrings.AVATAR, (friendList.get(position).getAvatar()));
                        intent.putExtra(StringContract.IntentStrings.STATUS, (friendList.get(position).getStatus()));
                        intent.putExtra(StringContract.IntentStrings.TABS, "1");
                        getActivity().startActivity(intent);
                        dialog.dismiss();
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

    private void checkOtherMemberAvailable(User user, int position) {
        SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(screen.messagelist.General.USER_ID, null);
        SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String DomainURL = domainUrlPref.getString(screen.messagelist.General.DOMAIN, null);
        Log.i(TAG, "checkOtherMemberAvailable: 1");
        String url = Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();

        requestMap.put(screen.messagelist.General.ACTION, "other_members_time_check_in_db");
        requestMap.put(screen.messagelist.General.USER_ID, "" + UserId);
        requestMap.put(screen.messagelist.General.RECEIVER_ID, "" + user.getUid());

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
                    Log.i(TAG, "checkOtherMemberAvailable: "+success);
                    if (success.equals("success") || success.equals("fail")){
                        Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                        intent.putExtra(StringContract.IntentStrings.NAME, (user.getName()));
                        intent.putExtra(StringContract.IntentStrings.SENDER_ID, (user.getUid()));
                        intent.putExtra(StringContract.IntentStrings.AVATAR, (user.getAvatar()));
                        intent.putExtra(StringContract.IntentStrings.STATUS, (user.getStatus()));
                        intent.putExtra(StringContract.IntentStrings.TABS, "1");
                        getActivity().startActivity(intent);
                    }else {
                        showDialog(success, position);
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
}