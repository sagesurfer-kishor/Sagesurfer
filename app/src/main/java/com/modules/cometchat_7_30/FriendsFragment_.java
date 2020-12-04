package com.modules.cometchat_7_30;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

import com.sagesurfer.adapters.FriendListAdapter;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import constant.StringContract;
import okhttp3.RequestBody;
import screen.messagelist.CometChatMessageListActivity;

/**
 * @author kishor k
 * Created on 13/11/2020
 */

public class FriendsFragment_ extends Fragment {
    private static final String TAG = FriendsFragment_.class.getSimpleName();

    private RecyclerView conversionList;
    private FriendListAdapter adapter;
    private EditText ed_search_friend;
    private TextView friend_list_error;
    private LinearLayout cardview_actions;

    public List<User> filteredNameList = new ArrayList<>();
    public List<User> friendList = new ArrayList<>();

    private ChatFragment_ parentActivity;

    public FriendsFragment_() {
    }

    public static FriendsFragment_ newInstance() {
        FriendsFragment_ fragment = new FriendsFragment_();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        conversionList = view.findViewById(R.id.friend_conversion_list);
        ed_search_friend = view.findViewById(R.id.ed_search_friend);
        friend_list_error = view.findViewById(R.id.friend_conversion_error);
        cardview_actions = view.findViewById(R.id.cardview_actions);

        // friend conversion search action
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

        // get all friend list from comet chat
        // getFriendsConversionList();
        //filterModuleWiseNotification();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        friendList.clear();
        // get all friend list from comet chat
        getFriendsConversionList();
    }

    private void getFriendsConversionList() {
        friendList.clear();

        UsersRequest usersRequest = null;
        int limit = 30;

        usersRequest = new UsersRequest.UsersRequestBuilder().friendsOnly(true).setLimit(limit).hideBlockedUsers(true).build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> list) {

                if (!list.isEmpty()) {
                    Log.e(TAG, "User list received: " + list.toString());
                    // set all friend list to friend adapter
                    friendList.addAll(list);
                    filteredNameList.addAll(list);
                    cardview_actions.setVisibility(View.VISIBLE);

                    adapter = new FriendListAdapter(FriendsFragment_.this, getContext(), friendList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    conversionList.setLayoutManager(mLayoutManager);
                    conversionList.setItemAnimator(new DefaultItemAnimator());
                    conversionList.setAdapter(adapter);
                    checkIntent();
                } else {
                    friend_list_error.setVisibility(View.VISIBLE);
                    conversionList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "User list fetching failed with exception: " + e.getMessage());
            }
        });
    }

    private void filterModuleWiseNotification() {

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "my_friends");
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;
        Log.e("urlurlurl", url);
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getActivity(), getActivity());
        if (requestBody != null) {
            Log.e("friendList", requestBody.toString());
            try {
                /*String response = NetworkCall_.post(url, requestBody, TAG, getActivity(), getActivity());

                if (response != null) {
                    notificationList = FriendList.parseSpams(response, activity, TAG);

                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // friend search filter
    private void searchFriend(String search) {
        adapter.getFilter().filter(search);
    }


    // Code by Debopam
    private void checkIntent() {
        if (getActivity() != null && getActivity().getIntent().hasExtra("receiverType")) {
            String receiverType = getActivity().getIntent().getStringExtra("receiverType");
            if (receiverType.equals("user")) {
                // Either tab 1 or 3 or 4
                String team_logs_id = getActivity().getIntent().getStringExtra("team_logs_id");
                if (team_logs_id.equals("0") || team_logs_id.isEmpty()) {
                    String sender = getActivity().getIntent().getStringExtra("sender");
                    for (int i = 0; i < filteredNameList.size(); i++) {
                        if (filteredNameList.get(i).getUid().equals(sender)) {
                            performAdapterClick(i);
                            break;
                        }
                    }
                }
            }
        }
    }


    // The adapter click operation is brought here to handle push notification click
    public void performAdapterClick(int position) {
        User user = filteredNameList.get(position);
        String providerArray = Preferences.get("providers");
        String youth = Preferences.get("youths");
        if (!youth.isEmpty()) {
            if (providerArray.contains(user.getUid())) {

                String status = user.getStatus();
                if (status.equals("online")) {
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
                                    intent.putExtra(StringContract.IntentStrings.UID, (user.getUid()));
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
                                    intent.putExtra(StringContract.IntentStrings.UID, (user.getUid()));
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
                }

            } else {
                // open comet chat chat form uikit
                Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                intent.putExtra(StringContract.IntentStrings.NAME, (user.getName()));
                intent.putExtra(StringContract.IntentStrings.UID, (user.getUid()));
                intent.putExtra(StringContract.IntentStrings.AVATAR, (user.getAvatar()));
                intent.putExtra(StringContract.IntentStrings.STATUS, (user.getStatus()));
                intent.putExtra(StringContract.IntentStrings.TABS, "1");
                getActivity().startActivity(intent);
            }
        }
    }
}