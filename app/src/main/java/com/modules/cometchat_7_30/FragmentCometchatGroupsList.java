package com.modules.cometchat_7_30;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.firebase.MessagingService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sagesurfer.adapters.GroupsListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.GetGroupsCometchat;
import com.sagesurfer.models.Members_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.secure.GroupTeam_;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import constant.StringContract;
import okhttp3.RequestBody;
import screen.messagelist.CometChatMessageListActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */

public class FragmentCometchatGroupsList extends Fragment {
    private static final String TAG = FragmentCometchatGroupsList.class.getSimpleName();
    private final int CONTACTS_LOADER = 1, CONTACTS_SEARCH_LOADER = 2;
    private RecyclerView recyclerView;
    private FloatingActionButton createGroup;
    private ArrayAdapter<String> reasonAdapter;
    private String creategroup;
    private String password;
    private String selectedReason;
    LocalBroadcastManager bm;
    private String numbers;
    private GroupsRequest groupsRequest = null;
    private int limit = 30;
    int rotatingCounter = 0;
    private ArrayList<User> myGroupMembersWithStatusArrayList;
    private GroupsListAdapter groupListAdapter;
    private EditText edittext_search;
    public List<Group> filteredNameList = new ArrayList<>();
    public List<Group> groupList = new ArrayList<>();
    private LinearLayout cardview_actions;
    public ArrayList<GetGroupsCometchat> primaryGroupList = new ArrayList<>();
    public ArrayList<GetGroupsCometchat> MainGroupList = new ArrayList<>();
    public ArrayList<GetGroupsCometchat> searchGroupList = new ArrayList<>();
    private TextView error, tv_total_groupCount, tv_total_groupTypeCount;
    SharedPreferences preferenOpenActivity;
    SharedPreferences.Editor editor;
    SharedPreferences preferenCheckIntent;
    ArrayList<Members_> membersArrayList_;
    int privateCount = 0;
    int passwordCount = 0;
    int publicCount = 0;
    int counter = 0;
    int iteratorCounter1;
    int iterationMemberId = 0;
    int providerIdCounter, membersCounter, iteratingCounter, iteratingCounterMemberList, providerOnlineStatusCounter, memberOnlineStatusCounter = 0;
    SharedPreferences.Editor editorCheckIntent;
    String group_member_id, group_provider;
    int gotProviderCount = 0;
    int gotMemberCount = 0;
    int membersIterationCounter = 0;
    int gotMemberCountOnlineStatus = 0;
    int gotProviderCountOnlineStatus = 0;
    int membersIterationCounter2 = 0;
    boolean isCheckMembers = true;
    //public ArrayList<GetGroupsCometchat> groupList = new ArrayList<>();
    AlertDialog.Builder builder;
    AlertDialog alert;
    ArrayList<User> arrayListUsers;

    public FragmentCometchatGroupsList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        View view = inflater.inflate(R.layout.fragment_chatrooms, null);
        recyclerView = view.findViewById(R.id.groupsList);
        createGroup = view.findViewById(R.id.creatGrp);
        edittext_search = view.findViewById(R.id.ed_search_friend);
        tv_total_groupTypeCount = view.findViewById(R.id.tv_total_groupTypeCount);
        tv_total_groupCount = view.findViewById(R.id.tv_total_groupCount);
        error = view.findViewById(R.id.group_list_error);
        cardview_actions = view.findViewById(R.id.cardview_actions);
        Log.i(TAG, "onCreateView: activity name " + getActivity().getClass().getSimpleName());
        arrayListUsers = new ArrayList<>();
        preferenOpenActivity = getActivity().getSharedPreferences("highlighted_group", Context.MODE_PRIVATE);
        editor = preferenOpenActivity.edit();
        myGroupMembersWithStatusArrayList = new ArrayList<>();
        /*created by rahul to know this fragment is open
         * if it is open and id any messages comes from firebase then counter should change */
        SharedPreferences preferencesCheckCurrentActivity = getActivity().getSharedPreferences("preferencesCheckCurrentActivity", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesCheckCurrentActivity.edit();
        editor.putBoolean("IsGroupListingPage", true);
        editor.apply();
        preferenCheckIntent = getActivity().getSharedPreferences("sp_check_push_intent", MODE_PRIVATE);

        /*
        editorCheckIntent = preferenCheckIntent.edit();
        editorCheckIntent.putBoolean("openActivity", true);
        editorCheckIntent.putBoolean("CheckIntent", true);
        editorCheckIntent.apply();*/
        // search group from list
        edittext_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchGroup(s.toString());
            }
        });

        // create group
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //here we are showing dialog
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_creategroup);

                final Spinner groupStatus = dialog.findViewById(R.id.GroupType);
                final EditText edCreateGroup = dialog.findViewById(R.id.groupName);
                final EditText edPassword = dialog.findViewById(R.id.edPassword);

                // group type for drop down
                ArrayList<String> reason = new ArrayList<>();
                reason.add(CometChatConstants.GROUP_TYPE_PUBLIC);
                reason.add(CometChatConstants.GROUP_TYPE_PRIVATE);
                reason.add(CometChatConstants.GROUP_TYPE_PASSWORD);

                //setting adapter to dropdown
                reasonAdapter = new ArrayAdapter<String>(getActivity(), R.layout.drop_down_selected_text_item_layout, reason);
                reasonAdapter.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                groupStatus.setAdapter(reasonAdapter);


                groupStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedReason = groupStatus.getSelectedItem().toString().trim();
                        if (selectedReason.equals(CometChatConstants.GROUP_TYPE_PASSWORD)) {
                            edPassword.setVisibility(View.VISIBLE);
                        } else {
                            edPassword.setVisibility(View.GONE);
                        }
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                ImageView dialogButton1 = dialog.findViewById(R.id.btnCancel);
                dialogButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                /*Imageview button for creating group
                 * here we will create group first to cometchat database and then take groupId and save
                 * in our database*/

                ImageView btnCreateGroup = dialog.findViewById(R.id.btnCreategroups);
                btnCreateGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        creategroup = edCreateGroup.getText().toString().trim();
                        password = edPassword.getText().toString().trim();

                        if (!TextUtils.isEmpty(creategroup)) {
                            // check group type - pubilc, private, password group
                            if (selectedReason.equals(CometChatConstants.GROUP_TYPE_PASSWORD)) {
                                password = edPassword.getText().toString().trim();
                                if (!TextUtils.isEmpty(password)) {
                                    int counter;
                                    Random rnum = new Random(System.currentTimeMillis());
                                    numbers = String.valueOf(rnum.nextInt(900000000) + 100000000);

                                    final String GUID = numbers;
                                    final String groupName = creategroup;
                                    final String groupType = selectedReason;

                                    Group group = new Group(GUID, groupName, groupType, password);
                                    //  doLogin(view, uid, GUID, groupName);
                                    //creating group in cometchat database

                                    CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
                                        @Override
                                        public void onSuccess(Group group) {
                                            Log.d(TAG, "Group created successfully: " + group.toString());
                                            // Toast.makeText(CreateGroupActivity.this, "Group created successfully: " + group.toString(), Toast.LENGTH_SHORT).show();
                                            String UID = Preferences.get(General.USER_ID);
                                            String action = "create_cometchat_group";
                                            //MessagingService.subscribeGroupNotification(GUID);
                                            // add created group in to our db
                                            CreateGroup(action, UID, GUID, groupName, groupType, password);
                                            getGroups();
                                            dialog.cancel();
                                        }

                                        @Override
                                        public void onError(CometChatException e) {
                                            Log.d(TAG, "Group creation failed with exception: " + e.getMessage());
                                            dialog.dismiss();
                                        }
                                    });

                                } else {
                                    edCreateGroup.setError("Please enter password");
                                }
                            } else {
                                int counter;
                                Random rnum = new Random();
                                numbers = String.valueOf(rnum.nextInt(900000000) + 100000000);
                                final String GUID = numbers;
                                final String groupName = creategroup;
                                final String groupType = selectedReason;

                                Group group = new Group(GUID, groupName, groupType, "");
                                CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
                                    @Override
                                    public void onSuccess(Group group) {
                                        Log.d(TAG, "Group created successfully: " + group.toString());
                                        // Toast.makeText(CreateGroupActivity.this, "Group created successfully: " + group.toString(), Toast.LENGTH_SHORT).show();
                                        String UID = Preferences.get(General.USER_ID);
                                        String action = "create_cometchat_group";
                                        //MessagingService.subscribeGroupNotification(GUID);

                                        // add group in our db
                                        CreateGroup(action, UID, GUID, groupName, groupType, password);
                                        getGroups();
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onError(CometChatException e) {
                                        Log.d(TAG, "Group creation failed with exception: " + e.getMessage());
                                    }
                                });
                            }

                        } else {
                            edCreateGroup.setError("Please enter group name");
                        }
                    }
                });
                dialog.show();
            }
        });

        // load all team from sagesurfer db
        //getGroups();
      /*  String providerArray = Preferences.get("providers");
        Log.e("w", providerArray);*/
        getGroups();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        bm = LocalBroadcastManager.getInstance(context);
        IntentFilter actionReceiver = new IntentFilter();
        actionReceiver.addAction("ActionGroup");
        bm.registerReceiver(onJsonReceived, actionReceiver);
    }

    private BroadcastReceiver onJsonReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String sender = intent.getStringExtra("sender");
                groupListAdapter.changeUnreadCount(sender);
                Log.i(TAG, "onReceive: broadcast" + sender);
            }
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        SharedPreferences preferencesCheckCurrentActivity = getActivity().getSharedPreferences("preferencesCheckCurrentActivity", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesCheckCurrentActivity.edit();
        editor.putBoolean("IsGroupListingPage", false);
        editor.apply();
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
                    Log.e("groups", response);
                    if (!MainGroupList.isEmpty()){
                        MainGroupList.clear();
                    }
                    if (!primaryGroupList.isEmpty()){
                        primaryGroupList.clear();
                    }
                    MainGroupList = GroupTeam_.parseTeams(response, "get_groups_cometchat", getActivity(), TAG);
                    for (GetGroupsCometchat item : MainGroupList) {
                        Log.i(TAG, "getGroups: Group info -> " + item.getName() + " GroupId " + item.getGroupId()
                                + " isMember " + item.getIs_member() + " group_color " + item.getGroup_color()+" getIs_ban_member "+item.getGroup_banned_members());
                        if (item.getType().equalsIgnoreCase("public") || item.getType().equalsIgnoreCase("private") || item.getType().equalsIgnoreCase("password")){
                            if (!item.getGroup_banned_members().equals("1")){
                                Log.i(TAG, "getGroups: Group info -> ban_member != 1  name " +item.getName());
                                primaryGroupList.add(item);
                            }else{
                                Log.i(TAG, "getGroups: Group info -> ban_member = 1 "+item.getName());

                            }
                        }
                    }
                    searchGroupList.clear();
                    searchGroupList.addAll(primaryGroupList);
                    cardview_actions.setVisibility(View.VISIBLE);
                    //getUnreadMessageCountList();
                    getGroupsCount();

                    tv_total_groupCount.setText("No of Groups : " + searchGroupList.size());
                    groupListAdapter = new GroupsListAdapter(FragmentCometchatGroupsList.this, getContext(), primaryGroupList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(groupListAdapter);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getGroupsCount() {
        counter=0;
        publicCount=0;
        privateCount=0;
        passwordCount=0;
        for (GetGroupsCometchat item : primaryGroupList) {
            counter++;
            if (item.getType().equals("public")) {
                publicCount = publicCount + 1;
            } else if (item.getType().equals("private")) {
                privateCount = privateCount + 1;
            } else if (item.getType().equals("password")) {
                passwordCount = passwordCount + 1;
            }
        }
        if (counter == primaryGroupList.size()) {
            tv_total_groupTypeCount.setText("(Public Grp : " + publicCount + ", Private Grp : " + privateCount + ", Password Grp : " + passwordCount + ")");
        }
    }

    /* for fetching all groups from cometchat and then we will check the unread message counter */
    public void getGroupsByCometchat() {
        groupsRequest = new GroupsRequest.GroupsRequestBuilder().setLimit(limit).build();
        Log.e(TAG, "getGroupsByCometchat: ");

        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> list) {
                Log.d(TAG, "Groups list fetched successfully: " + list.size());
                for (Group group : list) {
                    Log.i(TAG, "onSuccess: group " + group.toString());
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Groups list fetching failed with exception: " + e.getMessage());
            }
        });
    }



    private void CreateGroup(String action, String userId, String gId, String gNAme, String gType, String pass) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put("uid", userId);
        requestMap.put("gid", gId);
        requestMap.put("gname", gNAme);
        requestMap.put("gType", gType);
        requestMap.put("gPass", pass);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getActivity());
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getActivity());
                Log.e(TAG,"CreateGroup response"+ response);
                if (response != null) {
                    Toast.makeText(getActivity(), "Group Created Successfully", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if preferences is true that means user is navigating  from pushnotification and
         *that time we shoud check intent
         * if preferences is false that means user is returning back from chat screen and
         * */
        Log.i(TAG, "onResume:  is called");
        if (preferenCheckIntent.getBoolean("checkIntent", false)) {
            checkIntent();
        }
        /*after highlight the group then user will go to the chat screen when it return back then it will have checkedHighlightedGroup = true so that we can unhighlight the group */
        if (preferenCheckIntent.getBoolean("checkedHighlightedGroup", false)) {
            Log.i(TAG, "checkIntent: receiver is " + "" + getActivity().getIntent().getStringExtra("receiver"));
            editor.putString("receiver", "" + getActivity().getIntent().getStringExtra("receiver"));
            editor.putBoolean("makeHighlight", false);
            editor.putBoolean("checkedHighlightedGroup", false);
            editor.apply();
            //getGroups();
        }
    }

    private void searchGroup(String search) {
        if (groupListAdapter != null) {
            groupListAdapter.getFilter().filter(search);
        }
    }


    private void checkIntent() {
        if (getActivity() != null && getActivity().getIntent().getExtras() != null) {
            /*Here we open chat screen for the particular group
             *  added by rahul maske*/
            String type = getActivity().getIntent().getStringExtra("type");
            if (type.equals("")) {
                String receiver = getActivity().getIntent().getStringExtra("receiver");
                Log.i(TAG, "checkIntent: Sender Id " + receiver);
                for (int i = 0; i < primaryGroupList.size(); i++) {
                    Log.i(TAG, "checkIntent: primaryListItems " + primaryGroupList.get(i).getGroupId());
                    if (primaryGroupList.get(i).getGroupId().equals(receiver)) {
                        performAdapterClick(i, primaryGroupList);
                        break;
                    }
                }
            } else {
                /*Here we highlight the group after clicking push notification for added in group
                 * if highlightList is equals to true then group will highlight else not highlight
                 * added by rahul maske*/
                if (preferenCheckIntent.getBoolean("highlightList", true)) {
                    Log.i(TAG, "checkIntent: receiver is " + "" + getActivity().getIntent().getStringExtra("receiver"));
                    editor.putString("receiver", "" + getActivity().getIntent().getStringExtra("receiver"));
                    editor.putBoolean("makeHighlight", true);
                    editor.apply();
                    getGroups();
                } else {
                    //if (!preferenCheckIntent.getBoolean("highlightList", false))
                    Log.i(TAG, "checkIntent: receiver is " + "" + getActivity().getIntent().getStringExtra("receiver"));
                    editor.putString("receiver", "" + getActivity().getIntent().getStringExtra("receiver"));
                    editor.putBoolean("makeHighlight", false);
                    editor.apply();
                    getGroups();
                }
            }
        }
    }

    /*In this method we have fetched group memebers and providers string
     *       1. first check providers are available in list or not if available then check it its status
     *       2. if status is online then show dialog as do u want to talk with provider
     *       3. if offline then show offline dialog
     *       4. if provider is not available in list then check members list
     *       commented and created methods by rahul maske
     *  */
    public void getGroupMembersAndProviders(String GroupId, ArrayList<GetGroupsCometchat> mySearchList, int position, GetGroupsCometchat group) {
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
                        GetGroupsCometchat groupsCometchat = mySearchList.get(position);
                        membersArrayList_ = groupsCometchat.getMembersArrayList();
                        if (!group_provider.equals("")) {
                            Log.i(TAG, "getGroupMembersAndProviders: provider block");
                            String[] arrayProvidersId = group_provider.split(",");
                            //getting user details from server
                            getProvidersDetail(arrayProvidersId);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    outerLoop:
                                    /*fetching all provider from list which we have fetched*/
                                    for (User user : arrayListUsers) {
                                        /* getting members  from member list from our server
                                        * and checking that member is matched with server list if it matched
                                        * that means provider is exist in list so that we can check its status and show popup accordingly
                                        * */
                                        for (Members_ member : membersArrayList_) {
                                            if (user.getUid().equals(member.getComet_chat_id())) {
                                                String status = user.getStatus();
                                                if (status.equalsIgnoreCase("online")) {
                                                    //show provider online status
                                                    gotProviderCountOnlineStatus++;
                                                    if (arrayListUsers.size() == gotProviderCountOnlineStatus) {
                                                        //show all providers are online
                                                        String message = "Are you sure want to chat with provider?";
                                                        showDialog(message, group, searchGroupList, position);
                                                        arrayListUsers.clear();
                                                        gotProviderCountOnlineStatus = 0;
                                                    }
                                                } else {
                                                    arrayListUsers.clear();
                                                    gotProviderCountOnlineStatus = 0;
                                                    //show some providers are offline
                                                    String message = "Some clinician staff may not be available. Please call 911 for emergency issues. \nStill you want to leave a message?";
                                                    showDialog(message, group, searchGroupList, position);
                                                    break outerLoop;
                                                }
                                            }
                                        }
                                    }
                                }
                            }, 1000);
                        }
                        else if (!group_member_id.equals("")) {
                            Log.i(TAG, "getGroupMembersAndProviders: members block 1");
                            String[] arrayMembers = group_member_id.split(",");
                            getProvidersDetail(arrayMembers);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    outerLoop:
                                    for (User user : arrayListUsers) {
                                        for (Members_ member : membersArrayList_) {
                                            if (user.getUid().equals(member.getComet_chat_id())) {
                                                String status = user.getStatus();
                                                Log.i(TAG, "run: getGroupMembersAndProviders member block 2");
                                                if (status.equalsIgnoreCase("online")) {
                                                    Log.i(TAG, "run: getGroupMembersAndProviders member block 3");
                                                    //show members online status
                                                    gotMemberCountOnlineStatus++;
                                                    if (arrayListUsers.size() == gotMemberCountOnlineStatus) {
                                                        Log.i(TAG, "run: getGroupMembersAndProviders member block 4");
                                                        //show all providers are online
                                                        prepareToSendGroupChatScreen(group, mySearchList, position);
                                                        gotMemberCountOnlineStatus = 0;
                                                        arrayListUsers.clear();
                                                    }
                                                } else {
                                                    //show some members are offline
                                                    gotMemberCountOnlineStatus = 0;
                                                    arrayListUsers.clear();
                                                    String message = "Some group members may not be available to review your message. Please call 911 for emergency issues. \nStill you want to leave a message?";
                                                    showDialog(message, group, searchGroupList, position);
                                                    break outerLoop;
                                                }
                                            }
                                        }
                                    }
                                }
                            }, 1000);

             /*Log.i(TAG, screen.messagelist.General.MY_TAG + " getGroupMembersAndProviders: provider array has data");
                                        List<String> arrayListProviderId = new ArrayList<>();
                                        String[] arrayProvidersId = group_provider.split(",");
                                        arrayListProviderId = Arrays.asList(arrayProvidersId);
                                        membersIterationCounter = 0;
                                        gotProviderCount = 0;
                                        for (Members_ member : membersArrayList_) {
                                            membersIterationCounter++;
                                            for (String id : arrayListProviderId) {
                                                Log.i(TAG, screen.messagelist.General.MY_TAG + " getGroupMembersAndProviders: provider array id" + id);
                                                Log.i(TAG, screen.messagelist.General.MY_TAG + " getGroupMembersAndProviders: member array id" + member.getComet_chat_id());
                                                if (id.equals(member.getComet_chat_id())) {
                                                    gotProviderCount++;
                                                    getUserDetails2("" + member.getComet_chat_id(), "provider", group, position, mySearchList);
                                                }
                                            }
                                        }*/
                            //Log.i(TAG, "getGroupMembersAndProviders: member array has data");
                            /*List<String> arrayListMemberId = new ArrayList<>();
                            String[] arrayMembersId = group_member_id.split(",");
                            arrayListMemberId = Arrays.asList(arrayMembersId);
                            membersIterationCounter2 = 0;
                            gotMemberCount = 0;

                            for (Members_ member : membersArrayList_) {
                                membersIterationCounter2++;
                                Log.i(TAG, "getGroupMembersAndProviders: memberId from mainList " + member.getComet_chat_id());
                                for (String memberId : arrayListMemberId) {
                                    Log.i(TAG, screen.messagelist.General.MY_TAG + " getGroupMembersAndProviders: member array id" + memberId + "membersArrayList_ id" + member.getComet_chat_id());
                                    if (memberId.equals(member.getComet_chat_id())) {
                                        Log.i(TAG, screen.messagelist.General.MY_TAG + "getGroupMembersAndProviders: matched id ---------->" + memberId);
                                        gotMemberCount++;
                                        Log.i(TAG, "getGroupMembersAndProviders: got memberCount" + gotMemberCount);
                                        getUserDetails2("" + member.getComet_chat_id(), "member", group, position, mySearchList);
                                    }
                                }
                            }*/

                        }
                        else {
                            Log.i(TAG, "getGroupMembersAndProviders: no provider and members found ");
                            prepareToSendGroupChatScreen(group, mySearchList, position);
                        }
                    } else {
                        prepareToSendGroupChatScreen(group, mySearchList, position);
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
    /*from cometchat getting user details to know user is online or offline and store that all data in array*/
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
    /*getting user details from cometchat*/
    public void getUserDetails(String providerId) {
        CometChat.getUser(providerId, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                arrayListUsers.add(user);
            }

            @Override
            public void onError(CometChatException e) {
                Log.i(TAG, "onError: getUserDetails");
            }
        });
    }

    private void showDialog(String success, GetGroupsCometchat groupList, ArrayList<GetGroupsCometchat> mySearchList, int position) {
        builder = new AlertDialog.Builder(getActivity());
        //Creating dialog box
        builder.setMessage(success)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        prepareToSendGroupChatScreen(groupList, mySearchList, position);
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

    /*When user clicked on group list  item this method will invoke
     * added by rahul maske*/
    public void performAdapterClick(int position, ArrayList<GetGroupsCometchat> mySearchList) {

        GetGroupsCometchat group = mySearchList.get(position);
        //group.get
        String GID = group.getGroupId();
        colorChangeCall(GID,position,mySearchList,group);
       Log.i(TAG, "performAdapterClick: "+Integer.parseInt(group.getMembers_count()));
        if (Integer.parseInt(group.getMembers_count()) > 1) {
            Log.i(TAG, "performAdapterClick: if part");
            getGroupMembersAndProviders(GID, mySearchList, position, group);
        } else {
            Log.i(TAG, "performAdapterClick: else part");
            StringBuffer stringBufferIMembersId = new StringBuffer();
            openActivity(group.getName(), group.getGroupId(), group.getType(), group.getOwner_id(), group.getMembers_count(), "" + group.getPassword(), stringBufferIMembersId);
        }


        Log.e("grId", GID);
        /*commented code */
        //Log.i(TAG, "performAdapterClick: member count " + group.getMembers_count());
        //Log.i(TAG, "performAdapterClick: member list " + group.getMembersArrayList().get(0).getUser_id());

    }

    private void colorChangeCall(String GID, int position, ArrayList<GetGroupsCometchat> mySearchList, GetGroupsCometchat group) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> requestMap = new HashMap<>();
                requestMap.put(General.ACTION, "unread_group_color");
                requestMap.put("g_id", GID);

                String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;
                RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getActivity(), getActivity());
                if (requestBody != null) {
                    try {
                        String response = NetworkCall_.post(url, requestBody, TAG, getActivity(), getActivity());
                        if (response != null) {
                            Log.e("colorChangeCall", response);
                            //getGroups();
                            groupListAdapter.setColorDefault(position);

                        } else {
                            Log.i(TAG, "colorChangeCall: null response ");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread=new Thread(runnable);
        thread.start();
    }

    private void prepareToSendGroupChatScreen(GetGroupsCometchat group, ArrayList<GetGroupsCometchat> mySearchList, int position) {
        editor.putBoolean("makeHighlight", false);
        editor.commit();

        StringBuffer sbGroupMemberIds = new StringBuffer("");
        Log.i(TAG, "prepareToSendGroupChatScreen : getMembers_count"+group.getMembers_count());
        for (Members_ teamsItem : group.getMembersArrayList()) {
            String s = String.valueOf(teamsItem.getUser_id());
            //l.add(s);
            if (sbGroupMemberIds.length() == 0) {
                Log.i(TAG, "prepareToSendGroupChatScreen: "+sbGroupMemberIds);
                sbGroupMemberIds.append(s);
            } else {
                Log.i(TAG, "prepareToSendGroupChatScreen: "+sbGroupMemberIds);
                sbGroupMemberIds.append("," + s);
            }
        }

        Log.i(TAG, "performAdapterClick: members id " + sbGroupMemberIds);
        final String gName = group.getName();
        final String groupIds = String.valueOf(group.getGroupId());
        final String groupType = mySearchList.get(position).getType();
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

                }
                else {
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
        Log.i(TAG, "openActivity: groupMembersId "+groupMembersId);
        Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
        intent.putExtra(StringContract.IntentStrings.TYPE, "group");
        intent.putExtra(StringContract.IntentStrings.NAME, name);
        intent.putExtra(StringContract.IntentStrings.GUID, groupId);
        intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, ownerId);
        intent.putExtra(StringContract.IntentStrings.AVATAR, "https://designstagingtest.sagesurfer.com/static/avatar/thumb/man.jpg");
        intent.putExtra(StringContract.IntentStrings.GROUP_TYPE, GroupType);
        intent.putExtra(StringContract.IntentStrings.MEMBER_COUNT, memberCount);
        intent.putExtra(StringContract.IntentStrings.GROUP_DESC, "");
        intent.putExtra(StringContract.IntentStrings.GROUP_PASSWORD, GroupPass);
        intent.putExtra(StringContract.IntentStrings.TABS, "2");
        intent.putExtra(StringContract.IntentStrings.ALL_MEMBERS_STRING, "" + groupMembersId);
        getActivity().startActivity(intent);
    }


    /*public void getUserDetails2(String providerId, String provider, GetGroupsCometchat group, int position, ArrayList<GetGroupsCometchat> mySearchList) {
        CometChat.getUser(providerId, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                String statuss = user.getStatus();
                if (statuss.equals("online")) {
                    if (provider.equals("provider")) {
                        Log.i(TAG, screen.messagelist.General.MY_TAG + "getUserDetails2 onSuccess: provider id " + providerId + " is online");
                        gotProviderCountOnlineStatus++;
                        prepareForDialogProvider(group, position, provider, mySearchList);
                    } else if (provider.equals("member")) {
                        //prepareToSendGroupChatScreen(GetGroupsCometchat group, ArrayList<GetGroupsCometchat> mySearchList, int position)
                        *//*Log.i(TAG, screen.messagelist.General.MY_TAG + "getUserDetails2 onSuccess: member id " + providerId + " is online");
                        gotMemberCountOnlineStatus++;
                        prepareForDialogMembers(group, position, provider, mySearchList);*//*
                        gotMemberCountOnlineStatus++;
                        Log.i(TAG, "getGroupMembersAndProviders onSuccess: memberOnline" + gotMemberCountOnlineStatus);
                        prepareForDialogMembers(group, position, provider, mySearchList);
                    }

                } else {
                    if (provider.equals("provider")) {
                        Log.i(TAG, screen.messagelist.General.MY_TAG + " getUserDetails2 onSuccess: provider offline");
                        prepareForDialogProvider(group, position, provider, mySearchList);
                    } else if (provider.equals("member")) {
                       *//* Log.i(TAG, screen.messagelist.General.MY_TAG + " getUserDetails2 onSuccess: member offline");
                        prepareForDialogMembers(group, position, provider, mySearchList);*//*
                        //prepareToSendGroupChatScreen(GetGroupsCometchat group, ArrayList<GetGroupsCometchat> mySearchList, int position)
                        Log.i(TAG, "getGroupMembersAndProviders onSuccess: member offline");
                        prepareForDialogMembers(group, position, provider, mySearchList);
                    }
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, screen.messagelist.General.MY_TAG + " getUserDetails User details fetching failed with exception: " + e.getMessage());
            }
        });
    }*/

    /*private void prepareForDialogProvider(GetGroupsCometchat group, int position, String provider, ArrayList<GetGroupsCometchat> mySearchList) {
        if (membersIterationCounter == membersArrayList_.size()) {
            Log.i(TAG, screen.messagelist.General.MY_TAG + " getGroupMembersAndProviders: gotProviderCount " + gotProviderCount + " gotProviderCountOnlineStatus" + gotProviderCountOnlineStatus);
            if (gotProviderCount > 0) {
                Log.i(TAG, "prepareForDialogProvider: gotProviderCount > 0 ");
                if (gotProviderCount == gotProviderCountOnlineStatus) {
                    Log.i(TAG, screen.messagelist.General.MY_TAG + " getGroupMembersAndProviders: all providers are online ");
                    String message = "Are you sure want to chat with provider?";
                    gotProviderCountOnlineStatus = 0;
                    gotProviderCount = 0;
                    membersIterationCounter = 0;
                    showDialog(message, group, searchGroupList, position);
                } else {
                    gotProviderCountOnlineStatus = 0;
                    gotProviderCount = 0;
                    membersIterationCounter = 0;
                    Log.i(TAG, screen.messagelist.General.MY_TAG + " getUserDetails2: some providers are not online ");
                    String message = "Some clinician staff may not be available. Please call 911 for emergency issues. \nStill you want to leave a message?";
                    showDialog(message, group, searchGroupList, position);
                }
            } else {
                gotProviderCountOnlineStatus = 0;
                gotProviderCount = 0;
                membersIterationCounter = 0;
                Log.i(TAG, screen.messagelist.General.MY_TAG + " getUserDetails2: some providers are not online ");
                String message = "Some clinician staff may not be available. Please call 911 for emergency issues. \nStill you want to leave a message?";
                showDialog(message, group, searchGroupList, position);
            }
        }
    }*/

    /*private void prepareForDialogMembers(GetGroupsCometchat group, int position, String provider, ArrayList<GetGroupsCometchat> mySearchList) {
        Log.i(TAG, "getGroupMembersAndProviders prepareForDialogMembers: membersIterationCounter2 " + membersIterationCounter2);
        Log.i(TAG, "getGroupMembersAndProviders prepareForDialogMembers: membersArrayList_ " + membersArrayList_.size());
        if (membersIterationCounter2 == membersArrayList_.size()) {
            Log.i(TAG, screen.messagelist.General.MY_TAG + " getGroupMembersAndProviders: gotMemberCount " + gotMemberCount + " gotMemberCountOnlineStatus" + gotMemberCountOnlineStatus);
            if (gotMemberCount > 0) {
                Log.i(TAG, "getGroupMembersAndProviders prepareForDialogProvider: gotProviderCount > 0 ");
                if (gotMemberCount == gotMemberCountOnlineStatus) {
                    Log.i(TAG, screen.messagelist.General.MY_TAG + " getGroupMembersAndProviders: all providers are online ");
                    gotMemberCountOnlineStatus = 0;
                    gotMemberCount = 0;
                    membersIterationCounter2 = 0;
                    prepareToSendGroupChatScreen(group, mySearchList, position);
                } else {
                    gotMemberCountOnlineStatus = 0;
                    gotMemberCount = 0;
                    membersIterationCounter2 = 0;
                    Log.i(TAG, screen.messagelist.General.MY_TAG + " getUserDetails2: some members are not online ");
                    String message = "Some group members may not be available to review your message. Please call 911 for emergency issues. \nStill you want to leave a message?";
                    showDialog(message, group, searchGroupList, position);
                }
            } else {
                gotMemberCountOnlineStatus = 0;
                gotMemberCount = 0;
                membersIterationCounter2 = 0;
                Log.i(TAG, screen.messagelist.General.MY_TAG + " getUserDetails2: some members are not online ");
                String message = "Some group members may not be available to review your message. Please call 911 for emergency issues. \nStill you want to leave a message?";
                showDialog(message, group, searchGroupList, position);
            }
        }
    }*/

    /*public void prepareForDialog(GetGroupsCometchat group, int position, String provider, ArrayList<GetGroupsCometchat> mySearchList) {
        Log.i(TAG, "getUserDetails:  online");
        if (provider.equals("provider")) {
            gotProviderCountOnlineStatus++;
            if (membersIterationCounter == membersArrayList_.size() && gotProviderCount != 0) {
                Log.i(TAG, screen.messagelist.General.MY_TAG
                        + " getGroupMembersAndProviders: gotProviderCount "
                        + gotProviderCount + " gotProviderCountOnlineStatus"
                        + gotProviderCountOnlineStatus);

                if (gotMemberCount == gotMemberCountOnlineStatus) {
                    //show do you want to chat with provider
                    Log.i(TAG, screen.messagelist.General.MY_TAG + " getGroupMembersAndProviders: all providers are online ");
                    String message = "Are you sure want to chat with provider?";
                    gotProviderCountOnlineStatus = 0;
                    gotProviderCount = 0;
                    membersIterationCounter = 0;
                    Log.i(TAG, screen.messagelist.General.MY_TAG + " getUserDetails2 onSuccess: ");
                    showDialog(message, group, searchGroupList, position);
                } else {
                    gotProviderCountOnlineStatus = 0;
                    gotProviderCount = 0;
                    membersIterationCounter = 0;
                    //Show some clinistion are not available
                    Log.i(TAG, screen.messagelist.General.MY_TAG + " getUserDetails2: some providers are not online ");
                    String message = "Some clinician staff may not be available. Please call 911 for emergency issues. \nStill you want to leave a message?";
                    showDialog(message, group, searchGroupList, position);
                }
            }

        } else if (provider.equals("member")) {
            //prepareToSendGroupChatScreen(GetGroupsCometchat group, ArrayList<GetGroupsCometchat> mySearchList, int position)
            gotMemberCountOnlineStatus++;
            Log.i(TAG, "getUserDetails2 onSuccess: gotMemberCount" + gotMemberCount);
            Log.i(TAG, "getUserDetails2 onSuccess: gotMemberCountOnlineStatus" + gotMemberCountOnlineStatus);
            if (membersIterationCounter2 == membersArrayList_.size() && gotMemberCount != 0) {
                if (gotMemberCount == gotMemberCountOnlineStatus) {
                    *//*direct take to chat screen*//*
                    gotMemberCountOnlineStatus = 0;
                    membersIterationCounter2 = 0;
                    membersIterationCounter = 0;
                    Log.i(TAG, "getUserDetails2 : all members are online");

                } else {
                    *//*show some users are not available in group*//*
                    gotMemberCountOnlineStatus = 0;
                    gotMemberCount = 0;
                    membersIterationCounter2 = 0;
                    membersIterationCounter = 0;
                    Log.i(TAG, "getUserDetails2: some members are offline");
                    String message = "Some group members may not be available to review your message. Please call 911 for emergency issues. \nStill you want to leave a message?";
                    showDialog(message, group, searchGroupList, position);
                }
            }
        }
    }*/
}
