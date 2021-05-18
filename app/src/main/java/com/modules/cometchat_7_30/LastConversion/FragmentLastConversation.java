package com.modules.cometchat_7_30.LastConversion;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.Avatar;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.models.GetGroupsCometchat;
import com.sagesurfer.models.Members_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.secure.GroupTeam_;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import constant.StringContract;
import listeners.OnItemClickListener;
import okhttp3.RequestBody;
import screen.messagelist.CometChatMessageListActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentLastConversation#newInstance} factory method to
 * create an instance of this fragment.
 * created by rahul maske
 */
public class FragmentLastConversation extends Fragment {
    private static final String TAG = "FragmentLastConversatio";
    ConversationsRequest conversationsRequest;
    private RecyclerView recyclerView;
    private String conversationListType = null;
    private static OnItemClickListener events;
    private List<Conversation> conversationList = new ArrayList<>();
    private AdapterLastConversation adapter;
    Toolbar toolbar;
    Handler handler = new Handler();
    LinearLayout cardview_actions;
    private FragmentActivity mContext;
    Activity activity;
    AlertDialog.Builder builder;
    AlertDialog alert;
    EditText ed_search_friend;
    ArrayList<User> arrayListUsers;
    int gotProviderCountOnlineStatus,gotMemberCountOnlineStatus;
    private MainActivityInterface mainActivityInterface;
    public ArrayList<GetGroupsCometchat> primaryGroupList = new ArrayList<>();
    public ArrayList<GetGroupsCometchat> searchGroupList = new ArrayList<>();
    public String group_member_id, group_provider;
    ArrayList<Members_> membersArrayList_;
    List<String> arrayListProviderId = new ArrayList<>();
    ProgressBar progressBar;
    public FragmentLastConversation() {
        // Required empty public constructor
    }

    public static FragmentLastConversation newInstance(String param1, String param2) {
        FragmentLastConversation fragment = new FragmentLastConversation();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        Runnable runnableGetProviders = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: runnableGetProviders");
                getProvider();
            }
        };

        Thread threadGetProviders = new Thread(runnableGetProviders);
        threadGetProviders.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_last_conversation, container, false);
        recyclerView = view.findViewById(R.id.rv_last_conversion);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        
        cardview_actions = view.findViewById(R.id.cardview_actions);
        ed_search_friend = view.findViewById(R.id.ed_search_friend);
        progressBar = view.findViewById(R.id.progressBarLastConversation);
        arrayListUsers=new ArrayList<>();
        activity = getActivity();
        membersArrayList_=new ArrayList<>();
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.showHideBellIcon2(true);
            mainActivity.hidesettingIcon(true);
        }

        Thread threadMakeConversation = new Thread(new Runnable() {
            @Override
            public void run() {
                //code to do the HTTP request
                makeConversationList();
            }
        });
        threadMakeConversation.start();

        Thread threadGetGroupList = new Thread(new Runnable() {
            @Override
            public void run() {
                //code to do the HTTP request
                getGroups();
            }
        });
        threadGetGroupList.start();

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

// Uses to fetch next list of conversations if rvConversationList (RecyclerView) is scrolled in upward direction.
      /*  recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1)) {
                    makeConversationList();
                }
            }
        });*/
        return view;
    }

    private void searchFriend(String search) {
        if (adapter != null) {
            adapter.getFilter().filter(search);
        }
    }

    /**
     * This method is used to retrieve list of conversations you have done.
     * For more detail please visit our official documentation {@link "https://prodocs.cometchat.com/docs/android-messaging-retrieve-conversations" }
     *
     * @see ConversationsRequest
     */
    private void makeConversationList() {
        if (conversationsRequest == null) {
            conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(50).build();
            if (conversationListType != null)
                conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder()
                        .setConversationType(conversationListType).setLimit(50).build();
        }

        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                try {
                    for (Conversation conversation : conversations) {
                        if (conversation.getConversationType().equals("user")) {
                            Log.i(TAG, "onSuccess: team_logs_id ---"+conversation);
                            if (conversation.getLastMessage().getMetadata().getString("team_logs_id").equalsIgnoreCase("0")) {
                                conversationList.add(conversation);
                                Log.i(TAG, "onSuccess: equals to zero" +((User) conversation.getConversationWith()).getName());
                            } else {
                                Log.i(TAG, "onSuccess: !=0 ---->" + ((User) conversation.getConversationWith()).getName());
                            }
                        } else if (conversation.getConversationType().equals("group")) {
                            conversationList.add(conversation);
                        }
                    }

                    if (conversationList.size() != 0) {
                        adapter = new AdapterLastConversation(FragmentLastConversation.this, conversationList, getActivity(), FragmentLastConversation.this);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG, "run: data set...");
                                cardview_actions.setVisibility(View.VISIBLE);
                                recyclerView.setAdapter(adapter);
                            }
                        });

                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                cardview_actions.setVisibility(View.VISIBLE);
                                Log.i(TAG, "run: data set else");
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.i(TAG, "makeConversationList onError: ");
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (FragmentActivity) context;
        mainActivityInterface = (MainActivityInterface) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        mainActivityInterface.setMainTitle("Last Conversation");
        mainActivityInterface.setToolbarBackgroundColor();
    }

    private void getGroups() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "get_groups_cometchat");
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getActivity(), getActivity());
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getActivity(), getActivity());
                if (response != null) {
                    Log.e("groups ", " fetched by server ---->" + response);
                    primaryGroupList = GroupTeam_.parseTeams(response, "get_groups_cometchat", getActivity(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method will get all the providers list from server
     */
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
                    Log.e(TAG, "getProvider provider response " + response);
                    try {
                        JSONObject injectedObject = new JSONObject(response);
                        Preferences.save("providers", injectedObject.getJSONArray("provider_user_id").getJSONObject(0).getString("provider_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onUserClickPerform(Conversation conversation) {
        String allProvidersString = Preferences.get("providers");
        String ProviderIds[] = allProvidersString.split(",");
        Log.i(TAG, "performAdapterClick: 1 "+((User) conversation.getConversationWith()).getUid());
        Log.i(TAG, "performAdapterClick: 1 "+((User) conversation.getConversationWith()).getUid());
        if (Arrays.asList(ProviderIds).contains(((User) conversation.getConversationWith()).getUid())) {
            Log.i(TAG, "performAdapterClick: 2");
            String status = ((User) conversation.getConversationWith()).getStatus();
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
                                intent.putExtra(StringContract.IntentStrings.NAME, (((User) conversation.getConversationWith()).getName()));
                                intent.putExtra(StringContract.IntentStrings.SENDER_ID, (((User) conversation.getConversationWith()).getUid()));
                                intent.putExtra(StringContract.IntentStrings.AVATAR, (((User) conversation.getConversationWith()).getAvatar()));
                                intent.putExtra(StringContract.IntentStrings.STATUS, (((User) conversation.getConversationWith()).getStatus()));
                                intent.putExtra(StringContract.IntentStrings.TABS, "1");
                                getActivity().startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressBar.setVisibility(View.GONE);
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Alert Notification");
                alert.show();
            } else {
                checkProviderAvailableTime(((User) conversation.getConversationWith()).getUid(), conversation);
            }
        } else {
            checkMemberList(conversation);

        }
    }

    private void checkProviderAvailableTime(String receiver_id, Conversation conversation) {
        SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(screen.messagelist.General.USER_ID, null);

        SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String DomainURL = domainUrlPref.getString(screen.messagelist.General.DOMAIN, null);

        String url = screen.messagelist.Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(screen.messagelist.General.ACTION, "provider_time_check_in_db");
        requestMap.put(screen.messagelist.General.USER_ID, "" + UserId);
        requestMap.put(screen.messagelist.General.RECEIVER_ID, "" + receiver_id);

        RequestBody requestBody = screen.messagelist.NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());
        try {
            if (requestBody != null) {
                String response = screen.messagelist.NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                if (response != null) {
                    Log.i(TAG, "checkProviderAvailableTime:  response " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray provider_time_check_in_db = jsonObject.getJSONArray("provider_time_check_in_db");
                    String success = provider_time_check_in_db.getJSONObject(0).getString("Success");
                    showDialog(success, conversation);
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

    private void showDialog(String success, Conversation conversation) {
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
                        Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                        intent.putExtra(StringContract.IntentStrings.NAME, (((User) conversation.getConversationWith()).getName()));
                        intent.putExtra(StringContract.IntentStrings.SENDER_ID, (((User) conversation.getConversationWith()).getUid()));
                        intent.putExtra(StringContract.IntentStrings.AVATAR, (((User) conversation.getConversationWith()).getAvatar()));
                        intent.putExtra(StringContract.IntentStrings.STATUS, (((User) conversation.getConversationWith()).getStatus()));
                        intent.putExtra(StringContract.IntentStrings.TABS, "1");
                        getActivity().startActivity(intent);
                        dialog.dismiss();
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        progressBar.setVisibility(View.GONE);
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle("Alert Notification");
        alert.show();
    }

    private void checkMemberList(Conversation conversation) {
        SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(screen.messagelist.General.USER_ID, null);
        SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String DomainURL = domainUrlPref.getString(screen.messagelist.General.DOMAIN, null);
        String url = screen.messagelist.Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(screen.messagelist.General.ACTION, "get_admin_set_role");
        requestMap.put(screen.messagelist.General.IS_TEST_USER, "" + 0);
        requestMap.put(screen.messagelist.General.USER_ID, UserId);
        Log.i(TAG, "checkMemberList: User Id " + UserId);
        RequestBody requestBody = screen.messagelist.NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());
        Log.i(TAG, "checkMemberList: Domain " + DomainURL + url);

        try {
            if (requestBody != null) {
                String response = screen.messagelist.NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                if (response != null) {
                    Log.i(TAG, "checkMemberList:  response " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray provider_time_check_in_db = jsonObject.getJSONArray("get_admin_set_role");
                    String other_user_id = provider_time_check_in_db.getJSONObject(0).getString("other_user_id");
                    Log.i(TAG, "checkMemberList: user cometchat id " + ((User) conversation.getConversationWith()).getUid());
                    if (!other_user_id.equals("") || other_user_id != null) {
                        String[] arrayMembersId = other_user_id.split(",");
                        List<String> arrayListMembersId = new ArrayList<>(Arrays.asList(arrayMembersId));
                        for (String listItem : arrayListMembersId) {
                            Log.i(TAG, "checkMemberList: " + listItem);
                            if (listItem.equals("" + ((User) conversation.getConversationWith()).getUid())) {
                                //Log.i(TAG, "checkMemberList: "+user.getUid() + " is "+user.getStatus());
                                String status = ((User) conversation.getConversationWith()).getStatus();
                                if (status.equals("online")) {
                                    Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                                    intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                                    intent.putExtra(StringContract.IntentStrings.NAME, (((User) conversation.getConversationWith()).getName()));
                                    intent.putExtra(StringContract.IntentStrings.SENDER_ID, (((User) conversation.getConversationWith()).getUid()));
                                    intent.putExtra(StringContract.IntentStrings.AVATAR, (((User) conversation.getConversationWith()).getAvatar()));
                                    intent.putExtra(StringContract.IntentStrings.STATUS, (((User) conversation.getConversationWith()).getStatus()));
                                    intent.putExtra(StringContract.IntentStrings.TABS, "1");
                                    getActivity().startActivity(intent);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    checkOtherMemberAvailable(conversation);
                                }
                            }
                        }
                    }
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

    private void checkOtherMemberAvailable(Conversation conversation) {
        SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(screen.messagelist.General.USER_ID, null);
        SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String DomainURL = domainUrlPref.getString(screen.messagelist.General.DOMAIN, null);
        Log.i(TAG, "checkOtherMemberAvailable: 1");
        String url = screen.messagelist.Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();

        requestMap.put(screen.messagelist.General.ACTION, "other_members_time_check_in_db");
        requestMap.put(screen.messagelist.General.USER_ID, "" + UserId);
        requestMap.put(screen.messagelist.General.RECEIVER_ID, "" + ((User) conversation.getConversationWith()).getUid());

        RequestBody requestBody = screen.messagelist.NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());
        Log.i(TAG, "checkOtherMemberAvailable: 2");
        Log.i(TAG, "checkOtherMemberAvailable: Domain " + DomainURL + url);
        try {
            if (requestBody != null) {
                String response = screen.messagelist.NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                if (response != null) {
                    Log.i(TAG, "checkOtherMemberAvailable:  response " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray provider_time_check_in_db = jsonObject.getJSONArray("other_members_time_check_in_db");
                    String success = provider_time_check_in_db.getJSONObject(0).getString("Success");
                    Log.i(TAG, "checkOtherMemberAvailable: " + success);
                    if (success.equals("success") || success.equals("fail")) {
                        Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                        intent.putExtra(StringContract.IntentStrings.NAME, (((User) conversation.getConversationWith()).getName()));
                        intent.putExtra(StringContract.IntentStrings.SENDER_ID, (((User) conversation.getConversationWith()).getUid()));
                        intent.putExtra(StringContract.IntentStrings.AVATAR, (((User) conversation.getConversationWith()).getAvatar()));
                        intent.putExtra(StringContract.IntentStrings.STATUS, (((User) conversation.getConversationWith()).getStatus()));
                        intent.putExtra(StringContract.IntentStrings.TABS, "1");
                        getActivity().startActivity(intent);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        showDialog(success, conversation);
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

    public void onGroupClickedPerform(Conversation conversation) {
        //((Group) conversation.getConversationWith()).getGuid();
        //primaryGroupList.get(((Group) conversation.getConversationWith()).getGuid();
        Log.i(TAG, "onGroupClickedPerform:1 ");
        ArrayList<Members_> groupMembersArrayList;
        for (GetGroupsCometchat group : primaryGroupList) {
            Log.i(TAG, "onGroupClickedPerform:2 group.getGroupId() "+group.getGroupId()  +"  "+((Group) conversation.getConversationWith()).getGuid());
            Log.i(TAG, "onGroupClickedPerform:2 main group list grop  "+group.getGroupId() );
            if (group.getGroupId().equals(((Group) conversation.getConversationWith()).getGuid())) {
                groupMembersArrayList = group.getMembersArrayList();
                Log.i(TAG, "onGroupClickedPerform:3 ");
                if (Integer.parseInt(group.getMembers_count()) > 1) {
                    Log.i(TAG, "onGroupClickedPerform:4 ");
                    getGroupMembersAndProviders(group.getGroupId(), conversation, group, groupMembersArrayList);
                } else {
                    Log.i(TAG, "onGroupClickedPerform:5 ");
                    StringBuffer stringBufferIMembersId = new StringBuffer();

                    prepareToSendGroupChatScreen(group, conversation);
                    //openActivity(group.getName(), group.getGroupId(), group.getType(), group.getOwner_id(), group.getMembers_count(), "" + group.getPassword(), stringBufferIMembersId);
                }
            }
        }
    }

    public void getGroupMembersAndProviders(String GroupId, Conversation conversation, GetGroupsCometchat group, ArrayList<Members_> groupMembersArrayList) {
        SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(screen.messagelist.General.USER_ID, null);

        SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String DomainCode = domainUrlPref.getString(screen.messagelist.General.DOMAIN_CODE, null);

        String url = screen.messagelist.Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();

        requestMap.put(screen.messagelist.General.ACTION, "get_group_members_array");
        String DomainURL = domainUrlPref.getString(screen.messagelist.General.DOMAIN, null);

        requestMap.put(screen.messagelist.General.GROUP_ID, "" + GroupId);
        requestMap.put(screen.messagelist.General.DOMAIN_CODE, DomainCode);
        Log.i(TAG, "getGroupMembersAndProviders: 1");
        RequestBody requestBody = screen.messagelist.NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());
        Log.i(TAG, screen.messagelist.General.MY_TAG + " onTeamClickFetchTeamData: Domain " + DomainURL + url);
        try {
            if (requestBody != null) {
                String response = screen.messagelist.NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                if (response != null) {
                    Log.i(TAG, screen.messagelist.General.MY_TAG + " getGroupMembersAndProviders :  response " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray get_team_members_array = jsonObject.getJSONArray("get_group_members_array");

                    if (get_team_members_array.getJSONObject(0).getString("status").equals("1")) {
                        group_member_id = get_team_members_array.getJSONObject(0).getString("group_member_id");
                        group_provider = get_team_members_array.getJSONObject(0).getString("group_provider");
                        Log.i(TAG, "getGroupMembersAndProviders: group_member_id " + group_member_id + " group_provider " + group_provider);

                        /*GetGroupsCometchat groupsCometchat = mySearchList.get(position);
                        membersArrayList_ = groupsCometchat.getMembersArrayList();*/

                        if (!group_provider.equals("")) {
                            String[] arrayProvidersId = group_provider.split(",");
                            getProvidersDetail(arrayProvidersId);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    outerLoop:
                                    for (User user : arrayListUsers) {
                                        for (Members_ member : groupMembersArrayList) {
                                            if (user.getUid().equals(member.getComet_chat_id())) {
                                                String status = user.getStatus();
                                                if (status.equalsIgnoreCase("online")) {
                                                    //show provider online status
                                                    gotProviderCountOnlineStatus++;
                                                    if (arrayListUsers.size() == gotProviderCountOnlineStatus) {
                                                        //show all providers are online
                                                        String message = "Are you sure want to chat with provider?";
                                                        showDialogGroup(message, group, searchGroupList, conversation);
                                                        arrayListUsers.clear();
                                                        gotProviderCountOnlineStatus = 0;
                                                    }
                                                } else {
                                                    arrayListUsers.clear();
                                                    gotProviderCountOnlineStatus = 0;
                                                    //show some providers are offline
                                                    String message = "Some clinician staff may not be available. Please call 911 for emergency issues. \nStill you want to leave a message?";
                                                    showDialogGroup(message, group, searchGroupList, conversation);
                                                    break outerLoop;
                                                }
                                            }
                                        }
                                    }
                                }
                            }, 1000);
                        } else if (!group_member_id.equals("")) {
                            Log.i(TAG, "getGroupMembersAndProviders: this group has only members...");
                            String[] arrayMembers = group_member_id.split(",");
                            getProvidersDetail(arrayMembers);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    outerLoop:
                                    for (User user : arrayListUsers) {
                                        Log.i(TAG, "getGroupMembersAndProviders : "+user.getUid());
                                        for (Members_ member : groupMembersArrayList) {
                                            Log.i(TAG, "getGroupMembersAndProviders : "+member.getComet_chat_id());
                                            if (user.getUid().equals(member.getComet_chat_id())) {
                                                Log.i(TAG, "getGroupMembersAndProviders: 2"+user.getUid());
                                                String status = user.getStatus();
                                                if (status.equalsIgnoreCase("online")) {
                                                    //show members online status
                                                    Log.i(TAG, "getGroupMembersAndProviders: online member: ");
                                                    gotMemberCountOnlineStatus++;
                                                    if (arrayListUsers.size() == gotMemberCountOnlineStatus) {
                                                        //show all providers are online
                                                        progressBar.setVisibility(View.GONE);
                                                        prepareToSendGroupChatScreen(group, conversation);
                                                        gotMemberCountOnlineStatus = 0;
                                                        arrayListUsers.clear();
                                                    }
                                                } else {
                                                    //show some members are offline
                                                    Log.i(TAG, "getGroupMembersAndProviders: offline member: ");
                                                    gotMemberCountOnlineStatus = 0;
                                                    arrayListUsers.clear();
                                                    String message = "Some group members may not be available to review your message. Please call 911 for emergency issues. \nStill you want to leave a message?";
                                                    showDialog(message, conversation);
                                                    //openActivity(gName, groupIds, groupType, ownerId, memberCount, "", sbGroupMemberIds);
                                                    break outerLoop;
                                                }
                                            }
                                        }
                                    }
                                }
                            }, 1000);
                        } else {
                            Log.i(TAG, "getGroupMembersAndProviders: no provider and members found ");
                            progressBar.setVisibility(View.GONE);
                            prepareToSendGroupChatScreen(group, conversation);
                        }
                    } else {
                        //prepareToSendGroupChatScreen(group, mySearchList, position);
                        progressBar.setVisibility(View.GONE);
                        prepareToSendGroupChatScreen(group, conversation);
                    }
                    /*for (Members_ members_ : membersArrayList_) {
                        getUserDetails(members_.getComet_chat_id(), membersArrayList_, group, mySearchList, position);
                    }*/

                } else {
                    Log.i(TAG, "getGroupMembersAndProviders: null response");
                }

            } else {
                Log.i(TAG, "checkMemberList:  null2");
            }
        } catch (Exception e) {
            Log.i(TAG, "checkMemberList: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void getProvidersDetail(String[] arrayData) {
        Runnable providerRunnable = new Runnable() {
            @Override
            public void run() {
                for (String item : arrayData) {
                    getUserDetails(item);
                }
            }
        };

        Thread getProviderDataThread = new Thread(providerRunnable);
        getProviderDataThread.start();
    }

    public void getUserDetails(String providerId) {
        CometChat.getUser(providerId, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.i(TAG, "onSuccess: got members details");
                arrayListUsers.add(user);
            }

            @Override
            public void onError(CometChatException e) {
                Log.i(TAG, "onError: getUserDetails");
            }
        });
    }

    private void showDialogGroup(String success, GetGroupsCometchat group, ArrayList<GetGroupsCometchat> mySearchList, Conversation conversation) {
        builder = new AlertDialog.Builder(getActivity());
        //Creating dialog box
        builder.setMessage(success)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        progressBar.setVisibility(View.GONE);
                        prepareToSendGroupChatScreen(group,conversation );
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.setTitle("Alert Notification");
        alert.show();
    }

    private void prepareToSendGroupChatScreen(GetGroupsCometchat group, Conversation conversation) {
        //editor.putBoolean("makeHighlight", false);
        //editor.commit();
        /*sending group member ids to next activity to save group message log*/
        StringBuffer sbGroupMemberIds = new StringBuffer("");
        for (Members_ teamsItem : group.getMembersArrayList()) {
            String s = String.valueOf(teamsItem.getUser_id());
            //l.add(s);
            if (sbGroupMemberIds.length() == 0) {
                sbGroupMemberIds.append(s);
            } else {
                sbGroupMemberIds.append("," + s);
            }
        }

        Log.i(TAG, "performAdapterClick: members id " + sbGroupMemberIds);
        final String gName = group.getName();
        final String groupIds = String.valueOf(group.getGroupId());
        final String groupType = group.getType();
        final String ownerId = group.getOwner_id();
        String Uid = Preferences.get(General.USER_ID);
        final String memberCount = group.getMembers_count();
        int isMembers = group.getIs_member();
        Log.i(TAG, "performAdapterClick: isMember" + isMembers);

        switch (group.getType()) {
            case "public":
                if (isMembers == 0) {
                    // compare he is owner of that group or not
                    if (ownerId.equals(Uid)) {
                        Log.i(TAG, "performAdapterClick: isMember" + isMembers);
                        Log.i(TAG, "performAdapterClick: 1");
                        openActivity(gName, groupIds, groupType, ownerId, memberCount, "", sbGroupMemberIds);
                    } else {
                        Log.i(TAG, "performAdapterClick: 2");
                        Log.i(TAG, "performAdapterClick: isMember" + isMembers);
                        //if not then show this error
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("You are not member of this group. please join this group")
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert = builder.create();
                        alert.setTitle("Alert Notification");
                        alert.show();
                    }

                } else {
                    Log.i(TAG, "performAdapterClick: 2");
                    Log.i(TAG, "performAdapterClick: isMember" + isMembers);
                    openActivity(gName, groupIds, groupType, ownerId, memberCount, "", sbGroupMemberIds);
                    //openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
                }

                break;

            case "private":
                if (ownerId.equals(Uid)) {
                    openActivity(gName, groupIds, groupType, ownerId, memberCount, "", sbGroupMemberIds);
                    Log.i(TAG, "performAdapterClick: private 1");
                } else if (isMembers == 1) {
                    openActivity(gName, groupIds, groupType, ownerId, memberCount, "", sbGroupMemberIds);
                    Log.i(TAG, "performAdapterClick: private 2");
                } else {
                    Log.i(TAG, "performAdapterClick: private 3");
                    //if not then show this error
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("You are not member of this group. please join this group")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.setTitle("Alert Notification");
                    alert.show();
                }

                break;
            case "password":
                if (ownerId.equals(Uid)) {
                    openActivity(gName, groupIds, groupType, ownerId, memberCount, "", sbGroupMemberIds);
                    Log.i(TAG, "performAdapterClick: pw1");
                } else {
                    Log.i(TAG, "performAdapterClick: pw2");
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.dialog_password);

                    final EditText editText = dialog.findViewById(R.id.groupPassword);
                    ImageView imageView = dialog.findViewById(R.id.btnPasswordSubmit);
                    ImageView imageView1 = dialog.findViewById(R.id.btncancelPasswordSubmit);

                    imageView1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String pass = editText.getText().toString().trim();
                            if (!TextUtils.isEmpty(pass)) {

                                if (pass.equals(group.getPassword())) {
                                    openActivity(gName, groupIds, groupType, ownerId, memberCount, pass, sbGroupMemberIds);
                                    dialog.dismiss();

                                } else {
                                    editText.setError("Enter correct password");
                                }

                            } else {
                                editText.setError("Please enter password");
                            }
                        }
                    });
                    dialog.show();
                }
                break;
        }
    }

    // open chat window on comet chat error
    private void openActivity(String name, String groupId, String ownerId, String GroupType, String memberCount, String GroupPass, StringBuffer groupMembersId) {
        Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
        intent.putExtra(StringContract.IntentStrings.TYPE, "group");
        intent.putExtra(StringContract.IntentStrings.NAME, name);
        intent.putExtra(StringContract.IntentStrings.GUID, groupId);
        intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, ownerId);
        intent.putExtra(StringContract.IntentStrings.AVATAR, "https://designstaging.sagesurfer.com/static//avatar/thumb/man.jpg");
        intent.putExtra(StringContract.IntentStrings.GROUP_TYPE, GroupType);
        intent.putExtra(StringContract.IntentStrings.MEMBER_COUNT, memberCount);
        intent.putExtra(StringContract.IntentStrings.GROUP_DESC, "");
        intent.putExtra(StringContract.IntentStrings.GROUP_PASSWORD, GroupPass);
        intent.putExtra(StringContract.IntentStrings.TABS, "2");
        intent.putExtra(StringContract.IntentStrings.ALL_MEMBERS_STRING, "" + groupMembersId);
        getActivity().startActivity(intent);
    }
}