package com.modules.cometchat_7_30;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import constant.StringContract;
import okhttp3.RequestBody;
import screen.messagelist.CometChatMessageListActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatroomFragment_ extends Fragment {

    private static final String TAG = ChatroomFragment_.class.getSimpleName();

    private final int CONTACTS_LOADER = 1, CONTACTS_SEARCH_LOADER = 2;
    private RecyclerView recyclerView;
    private FloatingActionButton createGroup;
    private ArrayAdapter<String> reasonAdapter;
    private String creategroup;
    private String password;
    private String selectedReason;
    private String numbers;

    private GroupsListAdapter groupListAdapter;
    private EditText edittext_search;
    private LinearLayout cardview_actions;

    public ArrayList<GetGroupsCometchat> primaryGroupList = new ArrayList<>();
    public ArrayList<GetGroupsCometchat> searchGroupList = new ArrayList<>();

    private TextView error;

    //public ArrayList<GetGroupsCometchat> groupList = new ArrayList<>();

    public ChatroomFragment_() {
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

                ImageView dialogButton = dialog.findViewById(R.id.btnCreategroups);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        creategroup = edCreateGroup.getText().toString().trim();
                        password = edPassword.getText().toString().trim();

                        if (!TextUtils.isEmpty(creategroup)) {

                            // check group type - pubilc,private.password group
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
                                            getTeams();
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
                                        getTeams();
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
        getTeams();

      /*  String providerArray = Preferences.get("providers");
        Log.e("w", providerArray);*/

        return view;
    }

    private void getTeams() {
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

                    searchGroupList.addAll(primaryGroupList);
                    cardview_actions.setVisibility(View.VISIBLE);
                    groupListAdapter = new GroupsListAdapter(this, getContext(), searchGroupList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(groupListAdapter);
                    checkIntent();

                } else {
                    recyclerView.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        getTeams();
    }

    private void searchGroup(String search) {
        groupListAdapter.getFilter().filter(search);
    }

    // Code by Debopam
    private void checkIntent() {
        if (getActivity() != null) {
            String sender = getActivity().getIntent().getStringExtra("sender");
            for (int i = 0; i < primaryGroupList.size(); i++) {
                if (primaryGroupList.get(i).getGroupId().equals(sender)) {
                    performAdapterClick(i);
                    break;
                }
            }
        }
    }

    // The adapter click operation is brought here to handle push notification click
    public void performAdapterClick(int position) {
        GetGroupsCometchat groupList = primaryGroupList.get(position);
        String GID = groupList.getGroupId();
        Log.e("grId", GID);
        List<String> l = new ArrayList<>();
        String providerArray = Preferences.get("providers");

        for (Members_ teamsItem : groupList.getMembersArrayList()) {
            String s = String.valueOf(teamsItem.getUser_id());
            l.add(s);
        }

        final String gName = groupList.getName();
        final String groupIds = String.valueOf(groupList.getGroupId());
        final String groupType = primaryGroupList.get(position).getType();
        final String ownerId = groupList.getOwner_id();
        String Uid = Preferences.get(General.USER_ID);
        final String memberCount = groupList.getMembers_count();
        int isMembers = groupList.getIs_member();


        switch (groupList.getType()) {
            case "public":
                if (isMembers == 0) {
                    // compare   he is owner of that group or not
                    if (ownerId.equals(Uid)) {
                        openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
                    } else {
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
                    openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
                }

                break;

            case "private":
                if (ownerId.equals(Uid)) {
                    openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
                } else if (isMembers == 1) {
                    openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
                } else {
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
                } else {
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
