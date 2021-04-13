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
import com.firebase.MessagingService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sagesurfer.adapters.FriendListAdapter;
import com.sagesurfer.adapters.GroupsListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.GetGroupsCometchat;
import com.sagesurfer.models.Members_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.secure.GroupTeam_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
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
    private GroupsListAdapter groupListAdapter;
    private EditText edittext_search;
    public List<Group> filteredNameList = new ArrayList<>();
    public List<Group> groupList = new ArrayList<>();
    private LinearLayout cardview_actions;
    public ArrayList<GetGroupsCometchat> primaryGroupList = new ArrayList<>();
    public ArrayList<GetGroupsCometchat> searchGroupList = new ArrayList<>();
    private TextView error;
    SharedPreferences preferenOpenActivity;
    SharedPreferences.Editor editor;
    SharedPreferences preferenCheckIntent;
    SharedPreferences.Editor editorCheckIntent;
    //public ArrayList<GetGroupsCometchat> groupList = new ArrayList<>();

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
        error = view.findViewById(R.id.group_list_error);
        cardview_actions = view.findViewById(R.id.cardview_actions);
        Log.i(TAG, "onCreateView: activity name " + getActivity().getClass().getSimpleName());

        preferenOpenActivity = getActivity().getSharedPreferences("highlighted_group", Context.MODE_PRIVATE);
        editor = preferenOpenActivity.edit();

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
                                            MessagingService.subscribeGroupNotification(GUID);
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
                                        MessagingService.subscribeGroupNotification(GUID);

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
                    primaryGroupList = GroupTeam_.parseTeams(response, "get_groups_cometchat", getActivity(), TAG);
                    searchGroupList.clear();
                    Handler handler = new Handler();
                    searchGroupList.addAll(primaryGroupList);
                    cardview_actions.setVisibility(View.VISIBLE);
                    //getUnreadMessageCountList();

                    groupListAdapter = new GroupsListAdapter(FragmentCometchatGroupsList.this, getContext(), searchGroupList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(groupListAdapter);

                     /*ArrayList<ModelUserCount> al_unreadCountList = new ArrayList<>();
                    al_unreadCountList=getUnreadMessageCountList();
                    if (!al_unreadCountList.isEmpty()){
                        groupListAdapter = new GroupsListAdapter(FragmentCometchatGroupsList.this, getContext(), searchGroupList, al_unreadCountList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(groupListAdapter);
                    }*/

                    /*handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "run: ");
                            groupListAdapter = new GroupsListAdapter(FragmentCometchatGroupsList.this, getContext(), searchGroupList, al_unreadCountList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(groupListAdapter);
                        }
                    }, 4000);*/
                    //checkIntent();


                } else {
                    recyclerView.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    /*This method is used to get unread messages from cometchat */
    private void getUnreadMessageCountList() {
        Handler handler = new Handler();
        ArrayList<ModelUserCount> al_unreadCountList = new ArrayList<>();
        if (!this.primaryGroupList.isEmpty()) {
           /* for (GetGroupsCometchat item : primaryGroupList) {
                Log.e(TAG, "getUnreadMessageCountList: checking GID " + item.getGroupId() + "  list size" + primaryGroupList.size());*/

            //getUnreadmessagesForParticularGroup
            CometChat.getUnreadMessageCountForGroup("670270470", new CometChat.CallbackListener<HashMap<String, Integer>>() {
                @Override
                public void onSuccess(HashMap<String, Integer> stringIntegerHashMap) {
                    Log.e(TAG, "onSuccess: cometchat groupId " + stringIntegerHashMap.get("670270470"));    //getting null values for group
                    for (Map.Entry<String, Integer> entry : stringIntegerHashMap.entrySet()) {
                        String key = entry.getKey();
                        String value = String.valueOf(entry.getValue());
                        Log.e(TAG, "get groups cometchat onSuccess: group id " + key + " count " + value);
                        al_unreadCountList.add(new ModelUserCount("" + value, "" + key));
                    }
                    //Log.e(TAG, "onSuccess: groupId"+stringIntegerHashMap.get(item.getGroupId()));
                }

                @Override
                public void onError(CometChatException e) {
                    Log.i(TAG, "onError get groups " + e.getMessage());
                }
            });
               /* handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        groupListAdapter = new GroupsListAdapter(FragmentCometchatGroupsList.this, getContext(), searchGroupList, al_unreadCountList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(groupListAdapter);
                        checkIntent();
                    }
                }, 10000);*/
            //checkIntent();

/*

            }
*/

            //getCometchatUnreadMessagesForAllGroups
            /*CometChat.getUnreadMessageCountForAllGroups(new CometChat.CallbackListener<HashMap<String, Integer>>() {
                @Override
                public void onSuccess(HashMap<String, Integer> stringIntegerHashMap) {
                    // Handle success
                    for (Map.Entry<String, Integer> entry : stringIntegerHashMap.entrySet()) {
                        String GID = entry.getKey();
                        String unreard_count = String.valueOf(entry.getValue());
                        Log.e(TAG, "onSuccess: groupId " + GID + " counter " + unreard_count);

                        al_unreadCountList.add(new ModelUserCount("" + unreard_count, "" + GID));
                    }
                    groupListAdapter = new GroupsListAdapter(FragmentCometchatGroupsList.this, getContext(), searchGroupList, al_unreadCountList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(groupListAdapter);
                    checkIntent();

                }

                @Override
                public void onError(CometChatException e) {
                    // Handle Error
                    Log.e(TAG, "onError: allGroupCount");
                }
            });*/

            //for (GetGroupsCometchat item : this.primaryGroupList) {
            //Log.e(TAG, "getGroups: groupId " + item.getGroupId() + "groupName " + item.getName() + "ListSize " + this.primaryGroupList.size() + " " + rotatingCounter);
                /*CometChat.getUnreadMessageCountForAllGroups(new CometChat.CallbackListener<HashMap<String, Integer>>() {
                    @Override
                    public void onSuccess(HashMap<String, Integer> stringIntegerHashMap) {
                        // Handle success
                       //getting hasmap with values but not available ids which we are finding which we got from our server
                        for (Map.Entry<String, Integer> entry : stringIntegerHashMap.entrySet()) {
                            String GID = entry.getKey();
                            String unreard_count = String.valueOf(entry.getValue());
                            Log.e(TAG, "onSuccess: groupId " + GID + " counter " + unreard_count);
                            al_unreadCountList.add(new ModelUserCount("" + unreard_count, "" + GID));  // creating list with counter and gid
                        }
                        groupListAdapter = new GroupsListAdapter(FragmentCometchatGroupsList.this, getContext(), searchGroupList, al_unreadCountList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(groupListAdapter);
                        checkIntent();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        // Handle Error
                        Log.e(TAG, "onError: allGroupCount");
                    }
                });*/


        }
        //}
    }

                /*CometChat.getUnreadMessageCountForGroup(item.getGroupId(), new CometChat.CallbackListener<HashMap<String, Integer>>() {
                    @Override
                    public void onSuccess(HashMap<String, Integer> stringIntegerHashMap) {
                        rotatingCounter++;
                        Log.e(TAG, "onSuccess: cometchatUnreadMessage" );
                        for (Map.Entry<String, Integer> entry : stringIntegerHashMap.entrySet()) {
                            String key = entry.getKey();
                            String value = String.valueOf(entry.getValue());
                            Log.e(TAG, "get groups cometchat onSuccess: group id " + key + " count " + value );
                            al_unreadCountList.add(new ModelUserCount("" + value, "" + key));
                            if (rotatingCounter == listSize){
                                groupListAdapter = new GroupsListAdapter(FragmentCometchatGroupsList.this, getContext(), searchGroupList, al_unreadCountList);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(groupListAdapter);
                                checkIntent();
                            }
                        }
                    }

                    @Override
                    public void onError(CometChatException e) {
                        Log.i(TAG, "get groups onError: " + e.getMessage());
                        if (rotatingCounter==0){
                            groupListAdapter = new GroupsListAdapter(FragmentCometchatGroupsList.this, getContext(), searchGroupList, al_unreadCountList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(groupListAdapter);
                            checkIntent();
                        }
                    }
                });
            }
            }
    }*/


    /*public void getGroups2()
    {
        GroupsRequest groupsRequest=null;
        int limit = 30;
        if (!groupList.isEmpty()){
        groupList.clear();}

        groupsRequest = new GroupsRequest.GroupsRequestBuilder().setLimit(limit).build();

        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List <Group> list) {
                if (!list.isEmpty()) {
                    Log.d(TAG, "Groups list fetched successfully: " + list.size());
                    Handler handler = new Handler();
                    ArrayList<ModelUserCount> al_unreadCountList = new ArrayList<>();
                    groupList.addAll(list);
                    groupList.addAll(list);
                    cardview_actions.setVisibility(View.VISIBLE);
                    for (Group item : list){
                        CometChat.getUnreadMessageCountForGroup(item.getGuid(), new CometChat.CallbackListener<HashMap<String, Integer>>() {
                            @Override
                            public void onSuccess(HashMap<String, Integer> stringIntegerHashMap) {
                                if (!String.valueOf(stringIntegerHashMap.get(item.getGuid())).equalsIgnoreCase("null")) {
                                    al_unreadCountList.add(new ModelUserCount("" + String.valueOf(stringIntegerHashMap.get(item.getGuid())), item.getGuid()));
                                }
                            }

                            @Override
                            public void onError(CometChatException e) {
                                Log.i(TAG, "onError: cometchat getUnreadMessageCounter");
                            }
                        });
                    }

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new GroupsListAdapter(FragmentCometchatGroupsList.this, getContext(), groupList, al_unreadCountList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(adapter);
                        }
                    }, 3000);
                    checkIntent();
                } else {
                    friend_list_error.setVisibility(View.VISIBLE);
                    rv_conversionList.setVisibility(View.GONE);
                }


                }
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Groups list fetching failed with exception: " + e.getMessage());
            }
        });

    }*/


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
                Log.e("33333", response);
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
         *
         * */
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
            getGroups();
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

    // The adapter click operation is brought here to handle push notification click
    public void performAdapterClick(int position, ArrayList<GetGroupsCometchat> mySearchList) {
        GetGroupsCometchat groupList = mySearchList.get(position);
        String GID = groupList.getGroupId();
        Log.e("grId", GID);
        List<String> l = new ArrayList<>();
        String providerArray = Preferences.get("providers");
        editor.putBoolean("makeHighlight", false);
        editor.commit();

        for (Members_ teamsItem : groupList.getMembersArrayList()) {
            String s = String.valueOf(teamsItem.getUser_id());
            l.add(s);
        }

        final String gName = groupList.getName();
        final String groupIds = String.valueOf(groupList.getGroupId());
        final String groupType = mySearchList.get(position).getType();
        final String ownerId = groupList.getOwner_id();
        String Uid = Preferences.get(General.USER_ID);
        final String memberCount = groupList.getMembers_count();
        int isMembers = groupList.getIs_member();
        Log.i(TAG, "performAdapterClick: isMember" + isMembers);

        switch (groupList.getType()) {
            case "public":
                if (isMembers == 0) {
                    // compare he is owner of that group or not
                    if (ownerId.equals(Uid)) {
                        Log.i(TAG, "performAdapterClick: isMember" + isMembers);
                        Log.i(TAG, "performAdapterClick: 1");
                        openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
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
                    openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
                    //openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
                }

                break;

            case "private":
                if (ownerId.equals(Uid)) {
                    openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
                    Log.i(TAG, "performAdapterClick: private 1");
                } else if (isMembers == 1) {
                    openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
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
                    openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
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

                                if (pass.equals(groupList.getPassword())) {
                                    openActivity(gName, groupIds, groupType, ownerId, memberCount, pass);
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
    private void openActivity(String name, String groupId, String ownerId, String GroupType, String memberCount, String GroupPass) {
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
        getActivity().startActivity(intent);
    }
}
