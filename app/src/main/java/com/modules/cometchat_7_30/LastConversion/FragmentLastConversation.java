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
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.modules.cometchat_7_30.AdapterMembersList;
import com.modules.cometchat_7_30.UserModel;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.logger.Logger;
import com.sagesurfer.models.GetGroupsCometchat;
import com.sagesurfer.models.Members_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.secure.GroupTeam_;
import com.storage.preferences.Preferences;
import com.utils.AppLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import constant.StringContract;
import de.measite.minidns.record.A;
import listeners.OnItemClickListener;
import okhttp3.RequestBody;
import screen.messagelist.CometChatMessageListActivity;
import screen.messagelist.LogInfo;
import utils.Utils;

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
    private SharedPreferences sp;
    AlertDialog.Builder builder;
    AlertDialog alert;
    String ClickedUserId, groups_members;
    EditText ed_search_friend;
    ArrayList<User> arrayListUsers;
    int gotProviderCountOnlineStatus, gotMemberCountOnlineStatus;
    private MainActivityInterface mainActivityInterface;
    public ArrayList<GetGroupsCometchat> primaryGroupList = new ArrayList<>();
    public ArrayList<GetGroupsCometchat> searchGroupList = new ArrayList<>();
    public String group_member_id, group_provider;
    ArrayList<Members_> membersArrayList_;
    List<String> arrayListProviderId = new ArrayList<>();
    ProgressBar progressBar;
    String tabs, member_ids, provider_ids;
    String JoinTeam, MyTeam;
    ArrayList<String> myTeamArrayList = new ArrayList<>();
    ArrayList<String> joinTeamArrayList = new ArrayList<>();
    LinearLayout linealayout_error;

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

        Runnable runnableGetProviders = () -> {
            Log.i(TAG, "run: runnableGetProviders");
            getProvider();
        };

        Thread threadGetProviders = new Thread(runnableGetProviders);
        threadGetProviders.start();
        SharedPreferences preferencesTeamsData = getActivity().getSharedPreferences("prefrencesPushRedirection", MODE_PRIVATE);
        JoinTeam = preferencesTeamsData.getString("JoinTeam", null);
        MyTeam = preferencesTeamsData.getString("MyTeams", null);

        if (MyTeam != null) {
            String[] joinTeam = MyTeam.split(",");
            myTeamArrayList.addAll(Arrays.asList(joinTeam));
        }

        if (JoinTeam != null) {
            String[] joinTeam = JoinTeam.split(",");
            joinTeamArrayList.addAll(Arrays.asList(joinTeam));
        }

        if (!joinTeamArrayList.isEmpty()) {
            for (String item : joinTeamArrayList) {
                Log.i(TAG, "onCreateView: joinTeam arraylist item" + item);
            }
        }
        if (!myTeamArrayList.isEmpty()) {
            for (String item : myTeamArrayList) {
                Log.i(TAG, "onCreateView: myTeam arraylist item" + item);
            }
        }
    }

    private void checkIntent(Conversation conversation) {
        Log.i(TAG, "checkIntent: reached 1");
        /*if (getActivity() != null && getActivity().getIntent().getExtras() != null)
        {
            if (conversation.getLastMessage().getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)){
                Log.i(TAG, "checkIntent: user"+conversation.getLastMessage());
                try {
                    Log.i(TAG, "checkIntent: user"+conversation.getLastMessage().getMetadata().getString("team_logs_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Log.i(TAG, "checkIntent: group"+conversation.getLastMessage());
            }*/

        try {
            conversation.getLastMessage().getMetadata();
            Log.i(TAG, "checkIntent: " + conversation.getLastMessage());
            if (conversation.getLastMessage().getMetadata().has("team_logs_id")) {
                String team_logs_id = conversation.getLastMessage().getMetadata().getString("team_logs_id");
                Log.i(TAG, "checkIntent: " + team_logs_id + " conversation type " + conversation.getLastMessage().getReceiverType());
                if (conversation.getLastMessage().getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                    String[] teamLogArray = team_logs_id.split("_-");
                    if (team_logs_id.equals("0") || team_logs_id.isEmpty()) {
                        onUserClickPerform(conversation);
                    } else {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("membersIds", teamLogArray[0]);
                        editor.putString("teamIds", teamLogArray[1]);
                        editor.apply();
                        if (teamLogArray[2].equalsIgnoreCase("3")) {
                            tabs = "3";
                            Log.i(TAG, "onUserClickPerform: tab 3" + tabs);
                        } else if (teamLogArray[2].equalsIgnoreCase("4")) {
                            tabs = "4";
                            Log.i(TAG, "onUserClickPerform: tab 4" + tabs);
                        }
                        if (myTeamArrayList.contains(teamLogArray[1])) {
                            Log.i(TAG, "checkIntent: myTeam" + teamLogArray[1]);
                            //tabLayout.getTabAt(2).select();
                            performActionForTeamUser(teamLogArray[1], conversation);
                        } else if (joinTeamArrayList.contains(teamLogArray[1])) {
                            //tabLayout.getTabAt(3).select();
                            Log.i(TAG, "checkIntent: joinTeam" + teamLogArray[1]);
                            performActionForTeamUser(teamLogArray[1], conversation);
                        }
                    }
                }
            } else {
                //prepareToSendGroupChatScreen()
                Log.i(TAG, "checkIntent: reached at group");
                String groupId = ((Group) conversation.getConversationWith()).getGuid();
                for (GetGroupsCometchat model : primaryGroupList) {
                    if (model.getGroupId().equals(groupId)) {
                        prepareToSendGroupChatScreen(model, conversation);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_last_conversation, container, false);
        recyclerView = view.findViewById(R.id.rv_last_conversion);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        cardview_actions = view.findViewById(R.id.cardview_actions);
        ed_search_friend = view.findViewById(R.id.ed_search_friend);
        linealayout_error = view.findViewById(R.id.linealayout_error);
        progressBar = view.findViewById(R.id.progressBarLastConversation);
        arrayListUsers = new ArrayList<>();
        activity = getActivity();
        membersArrayList_ = new ArrayList<>();
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.showHideBellIcon2(true);
            mainActivity.hidesettingIcon(true);
        }

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

    @Override
    public void onResume() {
        super.onResume();
        /*Thread threadMakeConversation = new Thread(new Runnable() {
            @Override
            public void run() {
                //code to do the HTTP request

            }
        });
        threadMakeConversation.start();*/
        conversationsRequest=null;
        fetchConversationList();
        Thread threadGetGroupList = new Thread(new Runnable() {
            @Override
            public void run() {
                //code to do the HTTP request
                getGroups();
            }
        });
        threadGetGroupList.start();

    }

    /**
     * This method is used to retrieve list of conversations you have done.
     * For more detail please visit our official documentation {@link "https://prodocs.cometchat.com/docs/android-messaging-retrieve-conversations" }
     *
     * @see ConversationsRequest
     */
    private void fetchConversationList() {
        if (conversationsRequest == null) {
            conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(50).build();
            //if (conversationListType != null)
                /*conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder()
                        .setConversationType(conversationListType).setLimit(50).build();*/
                /*conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder()
                        .setLimit(50).build();*/
        }

        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                for (Conversation conversation : conversations) {
                    AppLog.i(TAG, "onSuccess: conversation " + conversation);
                    if (conversation.getConversationType().equals("user")) {
                        conversationList.add(conversation);
                    } else if (conversation.getConversationType().equals("group")) {
                        conversationList.add(conversation);

                    }
                }

                if (conversationList.size() != 0) {
                    linealayout_error.setVisibility(View.GONE);
                    adapter = new AdapterLastConversation(FragmentLastConversation.this, conversationList, getActivity(), FragmentLastConversation.this);
                    cardview_actions.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapter);
                    Intent mainIntent = requireActivity().getIntent();
                    if (requireActivity().getIntent().getExtras() != null) {
                        if (mainIntent.hasExtra("category")) {
                            if (mainIntent.getStringExtra("category").equals("call")) {
                                /*Here we are getting intent  for call redirection*/
                                Log.i(TAG, "handleIntent: call block");
                                String lastActiveAt = mainIntent.getStringExtra("lastActiveAt");
                                String uid = mainIntent.getStringExtra("uid");
                                String role = mainIntent.getStringExtra("role");
                                String name = mainIntent.getStringExtra("name");
                                String avatar = mainIntent.getStringExtra("avatar");
                                String status = mainIntent.getStringExtra("status");
                                String callType = mainIntent.getStringExtra("callType");
                                String sessionid = mainIntent.getStringExtra("sessionid");

                                User user = new User();
                                user.setLastActiveAt(Long.parseLong(lastActiveAt));
                                user.setUid(uid);
                                user.setRole(role);
                                user.setName(name);
                                user.setAvatar(avatar);
                                user.setStatus(status);
                                Utils.startCallIntent(getActivity(), user, callType, false, sessionid);
                                clearIntent();
                            }
                        } else {
                            AppLog.i(TAG,"has type " +mainIntent.getStringExtra("type"));
                            if (mainIntent.hasExtra("type")) {
                                if (mainIntent.getStringExtra("type").equalsIgnoreCase("whiteboard_push")
                                ||mainIntent.getStringExtra("type").equalsIgnoreCase("extension_whiteboard")) {
                                    AppLog.i(TAG,"has type");
                                    checkIntent(conversationList.get(0));
                                    requireActivity().getIntent().removeExtra("type");
                                }
                            }
                        }
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            cardview_actions.setVisibility(View.GONE);
                            Log.i(TAG, "run: data set else");
                            linealayout_error.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.i(TAG, "makeConversationList onError: "+e.getMessage());
            }
        });
    }

    private void clearIntent() {
        requireActivity().getIntent().removeExtra("lastActiveAt");
        requireActivity().getIntent().removeExtra("category");
        requireActivity().getIntent().removeExtra("uid");
        requireActivity().getIntent().removeExtra("role");
        requireActivity().getIntent().removeExtra("name");
        requireActivity().getIntent().removeExtra("status");
        requireActivity().getIntent().removeExtra("callType");
        requireActivity().getIntent().removeExtra("sessionid");


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
                        if (injectedObject.getJSONArray("provider_user_id").getJSONObject(0).has("provider_id")) {
                            Preferences.save("providers", injectedObject.getJSONArray("provider_user_id").getJSONObject(0).getString("provider_id"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*In last conversation if user click on any user then this method will invoke
     * added by rahul maske*/
    public void onUserClickPerform(Conversation conversation) throws JSONException {
        String allProvidersString = Preferences.get("providers");
        String ProviderIds[] = allProvidersString.split(",");
        Log.i(TAG, "performAdapterClick:  " + ((User) conversation.getConversationWith()).getUid());
        //Log.i(TAG, "performAdapterClick:  "+((User) conversation.getConversationWith()).getMetadata().getString("team_logs_id"));
        Log.i(TAG, ": has team_logs_id medadata for message" + conversation.getLastMessage().getMetadata());

        if (conversation.getLastMessage().getMetadata().has("team_logs_id")) {
            if (conversation.getLastMessage().getMetadata().getString("team_logs_id").equals("0")) {
                tabs = "1";
                Log.i(TAG, "onUserClickPerform: tab 1" + tabs);
                performActionForFriendUser(ProviderIds, conversation);
            } else {
                String team_logs_id = conversation.getLastMessage().getMetadata().getString("team_logs_id");
                String[] array = team_logs_id.split("_-");
                if (array[2].equalsIgnoreCase("3")) {
                    tabs = "3";
                    Log.i(TAG, "onUserClickPerform: tab 3" + tabs);
                } else if (array[2].equalsIgnoreCase("4")) {
                    tabs = "4";
                    Log.i(TAG, "onUserClickPerform: tab 4" + tabs);
                }

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("membersIds", array[0]);
                editor.putString("teamIds", array[1]);
                editor.apply();
                Log.i(TAG, "onUserClickPerform: Team id is " + array[1]);
                performActionForTeamUser(array[1], conversation);
            }
        }
    }

    /*If clicked user is from friend then this method will invoke
     *In this method we are going to get team member details to check user role and
     * on the basis of that we will show the popup for the user like -> provider's or member's popup
     * commented by rahul maske*/
    private void performActionForTeamUser(String team_id, Conversation conversation) {
        Runnable runnableUserArray = new Runnable() {
            @Override
            public void run() {
                SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
                String UserId = UserInfoForUIKitPref.getString(screen.messagelist.General.USER_ID, null);
                SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
                String DomainURL = domainUrlPref.getString(screen.messagelist.General.DOMAIN, null);
                String url = screen.messagelist.Urls_.MOBILE_COMET_CHAT_TEAMS;
                HashMap<String, String> requestMap = new HashMap<>();
                requestMap.put(screen.messagelist.General.ACTION, "get_team_members_array");
                requestMap.put(screen.messagelist.General.TEAM_ID, team_id);
                requestMap.put(screen.messagelist.General.USER_ID, UserId);
                // Log.i(TAG, "onTeamClickFetchTeamData: User Id " + UserId);
                RequestBody requestBody = screen.messagelist.NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());
                Log.i(TAG, "onTeamClickFetchTeamData: Domain " + DomainURL + url);
                try {
                    if (requestBody != null) {
                        String response = screen.messagelist.NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                        if (response != null) {

                            // Log.i(TAG, "onTeamClickFetchTeamData:  response " + response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray get_team_members_array = jsonObject.getJSONArray("get_team_members_array");
                            member_ids = get_team_members_array.getJSONObject(0).getString("team_member_id");
                            provider_ids = get_team_members_array.getJSONObject(0).getString("team_provider");
                            Log.i(TAG, "run: team_member_ids " + member_ids + " providers_id " + provider_ids);
                            onLastConversationListUserClicked(conversation, team_id);

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
        Thread thread = new Thread(runnableUserArray);
        thread.start();
    }

    /*after clicking on my team member item this method will execute
     * in this method we are fetching all
     * added by rahul maske
     *  */
    public void onLastConversationListUserClicked(Conversation conversation, String team_id) {
        Log.i(TAG, "onLastConversationListUserClicked: team_id " + team_id);
        Preferences.save(General.BANNER_IMG, "");
        Preferences.save(General.TEAM_ID, team_id);
        Preferences.save(General.TEAM_NAME, "");
        ClickedUserId = ((User) conversation.getConversationWith()).getUid();
        String[] ProviderArray = provider_ids.split(",");
        String[] MemberArray = member_ids.split(",");
        //((User) conversation.getConversationWith()).getName()
        if (Arrays.asList(ProviderArray).contains(ClickedUserId)) {
            //if user is provider
            getUserTeamDetails(ClickedUserId, "provider", conversation, team_id);
            Log.i(TAG, "onMemberRelativeLayoutClicked: this is provider --> " + ((User) conversation.getConversationWith()).getUid());
        } else if (Arrays.asList(MemberArray).contains(ClickedUserId)) {
            //if user is member selected by admin dashboard
            getUserTeamDetails(ClickedUserId, "member", conversation, team_id);
            Log.i(TAG, "onMemberRelativeLayoutClicked: This is member --> " + ClickedUserId);
        } else {
            //if user is normal user
            Log.i(TAG, "onMemberRelativeLayoutClicked: ");

            final String CometchatIdOfSender = ((User) conversation.getConversationWith()).getUid();
            final String username = ((User) conversation.getConversationWith()).getName();
            final String status = ((User) conversation.getConversationWith()).getStatus();
            openChatActivityForTeamUser("" + username,
                    "" + CometchatIdOfSender,
                    status,
                    "" + String.valueOf(team_id));
        }
    }

    /*team user related method*/
    public void getUserTeamDetails(String clickedUserId, String userType, Conversation conversation, String teamId) {
        Log.i(TAG, "getUserTeamDetails: team_id " + teamId);
        CometChat.getUser(clickedUserId, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "getUserDetails -> user details : " + user.toString());
                String statuss = user.getStatus();
                if (statuss.equals("online")) {
                    Log.i(TAG, "getUserDetails -> status if online -> " + statuss);
                    if (userType.equalsIgnoreCase("provider")) {
                        showProviderOnlineDialogForTeam(conversation, clickedUserId, teamId);
                    } else {
                        //Send direct Inside chat screen
                        final String CometchatIdOfSender = clickedUserId;
                        final String username = ((User) conversation.getConversationWith()).getName();
                        final String status = ((User) conversation.getConversationWith()).getStatus();
                        openChatActivityForTeamUser("" + username,
                                "" + CometchatIdOfSender,
                                status,
                                "" + teamId);

                    }
                } else {
                    if (userType.equalsIgnoreCase("provider")) {
                        Log.i(TAG, "getUserDetails -> status if offline  -> " + statuss);
                        checkProviderAvailableTime(clickedUserId, conversation);
                    } else {
                        checkOtherMemberAvailable(conversation);
                    }

                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "User details fetching failed with exception: " + e.getMessage());
            }
        });
    }

    private void showProviderOnlineDialogForTeam(Conversation conversation, String clickedUserId, String teamId) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        //Setting message manually and performing action on button click
        builder.setMessage("Are you sure you want chat with provider?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        final String CometchatIdOfSender = String.valueOf(clickedUserId);
                        final String username = ((User) conversation.getConversationWith()).getName();
                        final String status = ((User) conversation.getConversationWith()).getStatus();
                        Log.i(TAG, "onClick: CometchatIdOfSender " + CometchatIdOfSender + " username " + username + " status " + status + " team_.getId() "
                                + "CometchatIdOfSender " + CometchatIdOfSender);

                        openChatActivityForTeamUser("" + username,
                                "" + CometchatIdOfSender,
                                status,
                                "" + String.valueOf(teamId));
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

    private void openChatActivityForTeamUser(String username, String userCometchatId, String status, String teamId) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Log.i(TAG, "handleMessage: " + message.getData());
                Log.i(TAG, "handleMessage: " + message.getWhen());
                Log.i(TAG, "handleMessage: teamId " + teamId);
                progressBar.setVisibility(View.GONE);
            }
        };

        Message message = handler.obtainMessage(1, teamId);
        message.sendToTarget();

        SharedPreferences sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("membersIds", userCometchatId);
        editor.putString("teamIds", teamId);
        editor.apply();
        Intent intent = new Intent(getContext(), CometChatMessageListActivity.class);
        intent.putExtra(StringContract.IntentStrings.NAME, username);
        intent.putExtra(StringContract.IntentStrings.UID, userCometchatId);
        intent.putExtra(StringContract.IntentStrings.STATUS, status);
        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
        intent.putExtra("GID", Preferences.get(General.GROUP_ID));
        intent.putExtra(StringContract.IntentStrings.TABS, tabs);
        intent.putExtra("teamId", teamId);
        intent.putExtra("membersIds", userCometchatId);
        startActivity(intent);
    }


    /*If clicked user is from friend then this method will invoke
     * commented by rahul maske*/
    public void performActionForFriendUser(String[] providerIds, Conversation conversation) {
        if (Arrays.asList(providerIds).contains(((User) conversation.getConversationWith()).getUid())) {
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
                                intent.putExtra(StringContract.IntentStrings.TABS, tabs);
                                if (tabs.equalsIgnoreCase("3") || tabs.equalsIgnoreCase("4")) {
                                    intent.putExtra("teamId", sp.getString("teamIds", null));
                                    intent.putExtra("membersIds", sp.getString("membersIds", null));
                                }
                                progressBar.setVisibility(View.GONE);
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

    /*This method is to check if the provider is offline and it will check its available time on server
     * this is for user click event in this class there is group click event is also
     * */
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
                    showDialogForUsers(success, conversation);
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

    /*User related functionality //friend
     *This show message is for user not for user related functionality */
    private void showDialogForUsers(String success, Conversation conversation) {
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
                        intent.putExtra(StringContract.IntentStrings.TABS, tabs);
                        if (tabs.equalsIgnoreCase("3") || tabs.equalsIgnoreCase("4")) {
                            intent.putExtra("teamId", sp.getString("teamIds", null));
                            intent.putExtra("membersIds", sp.getString("membersIds", null));
                        }
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

    /* User related functionality
     *In this method we will get all the members list and we will match that member with clicked member and then if it matched
     * then that member has set availability time authority
     * then we will check its status and  if this member is online then we will send directly to the chat screen
     * else we will check its available from calling checkOtherMemberAvailable() method */
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
                                    intent.putExtra(StringContract.IntentStrings.TABS, tabs);
                                    if (tabs.equalsIgnoreCase("3") || tabs.equalsIgnoreCase("4")) {
                                        intent.putExtra("teamId", sp.getString("teamIds", null));
                                        intent.putExtra("membersIds", sp.getString("membersIds", null));
                                    }
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

    /* User related functionality need to check  //Friend
     * checkOtherMemberAvailable for userclick functionality  */
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
                    Log.i(TAG, "checkOtherMemberAvailable:  response " + response + " tabs " + tabs);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray provider_time_check_in_db = jsonObject.getJSONArray("other_members_time_check_in_db");
                    String success = provider_time_check_in_db.getJSONObject(0).getString("Success");
                    Log.i(TAG, "checkOtherMemberAvailable: " + success);
                    if (success.equalsIgnoreCase("success") || success.equalsIgnoreCase("fail")) {
                        Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                        Log.i(TAG, "checkOtherMemberAvailable: name " + ((User) conversation.getConversationWith()).getName());
                        Log.i(TAG, "checkOtherMemberAvailable: SENDER_ID " + ((User) conversation.getConversationWith()).getUid());
                        Log.i(TAG, "checkOtherMemberAvailable: teamId " + sp.getString("teamIds", null) + " member id " + sp.getString("membersIds", null));
                        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                        intent.putExtra(StringContract.IntentStrings.NAME, (((User) conversation.getConversationWith()).getName()));
                        intent.putExtra(StringContract.IntentStrings.SENDER_ID, (((User) conversation.getConversationWith()).getUid()));
                        intent.putExtra(StringContract.IntentStrings.AVATAR, (((User) conversation.getConversationWith()).getAvatar()));
                        intent.putExtra(StringContract.IntentStrings.STATUS, (((User) conversation.getConversationWith()).getStatus()));
                        intent.putExtra(StringContract.IntentStrings.TABS, tabs);
                        if (tabs.equalsIgnoreCase("3") || tabs.equalsIgnoreCase("4")) {
                            intent.putExtra("teamId", sp.getString("teamIds", null));
                            intent.putExtra("membersIds", sp.getString("membersIds", null));
                        }
                        getActivity().startActivity(intent);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        showDialogForUsers(success, conversation);
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
        Log.i(TAG, "onGroupClickedPerform:1 ");
        ArrayList<Members_> groupMembersArrayList;
        for (GetGroupsCometchat group : primaryGroupList) {
            Log.i(TAG, "onGroupClickedPerform:2 group.getGroupId() " + group.getGroupId() + "  " + ((Group) conversation.getConversationWith()).getGuid());
            Log.i(TAG, "onGroupClickedPerform:2 main group list grop  " + group.getGroupId());
            if (group.getGroupId().equals(((Group) conversation.getConversationWith()).getGuid())) {
                groupMembersArrayList = group.getMembersArrayList();
                Log.i(TAG, "onGroupClickedPerform:3 ");
                if (Integer.parseInt(group.getMembers_count()) > 1) {
                    Log.i(TAG, "onGroupClickedPerform:4 ");
                    groupClickServerCall(group.getGroupId(), conversation, group);
                    /*below commented code is working
                     * this is previous implementation for group clicked and now we have changed flow for this so we are commenting this code
                     * commented by rahul maske on 15-06-2021
                     * */
                    /*getGroupMembersAndProviders(group.getGroupId(), conversation, group, groupMembersArrayList);*/
                } else {
                    Log.i(TAG, "onGroupClickedPerform:5 ");
                    StringBuffer stringBufferIMembersId = new StringBuffer();
                    prepareToSendGroupChatScreen(group, conversation);
                    //openActivity(group.getName(), group.getGroupId(), group.getType(), group.getOwner_id(), group.getMembers_count(), "" + group.getPassword(), stringBufferIMembersId);
                }
            }
        }
    }

    private void groupClickServerCall(String GID, Conversation conversation, GetGroupsCometchat group) {
        SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(screen.messagelist.General.USER_ID, null);
        String DomainCode = UserInfoForUIKitPref.getString(screen.messagelist.General.DOMAIN_CODE, null);

        Log.i(TAG, "groupClickServerCall: Domain code " + DomainCode);
        Log.i(TAG, "groupClickServerCall: User Id " + UserId);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "group_pop_message");
        requestMap.put(General.GROUP_ID, GID);
        requestMap.put(General.USER_ID, UserId);
        requestMap.put(General.DOMAIN_CODE, DomainCode);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getActivity(), getActivity());
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getActivity(), getActivity());
                if (response != null) {
                    Log.e(TAG, "groupClickServerCall call response " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    String CheckMembers = jsonObject.getJSONArray("group_pop_message").getJSONObject(0).getString("CheckMembers");
                    String Message = jsonObject.getJSONArray("group_pop_message").getJSONObject(0).getString("msg");
                    groups_members = jsonObject.getJSONArray("group_pop_message").getJSONObject(0).getString("groups_members");
                    if (CheckMembers.equalsIgnoreCase("0") && !Message.equalsIgnoreCase("Direct Open pop up")) {
                        //direct pop up
                        showDialogNewImplementation(Message, group, conversation, "hide");
                    } else if (CheckMembers.equalsIgnoreCase("1")) {
                        // click here message
                        showDialogNewImplementation(Message, group, conversation, "show");
                    } else {
                        prepareToSendGroupChatScreen(group, conversation);
                    }
                } else {
                    Log.i(TAG, "groupClickServerCall: null response");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDialogNewImplementation(String message, GetGroupsCometchat group, Conversation conversation, String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_group_availability, null);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        Button ok = view.findViewById(R.id.ok_btn);
        TextView tv_message = view.findViewById(R.id.tv_message);
        Button cancel = view.findViewById(R.id.btn_cancel);
        Button iv_close_dialog = view.findViewById(R.id.iv_close_dialog);
        Button btn_members = view.findViewById(R.id.btn_members);
        tv_message.setText("" + message);
        if (action.equalsIgnoreCase("show")) {
            btn_members.setVisibility(View.VISIBLE);
        } else {
            btn_members.setVisibility(View.GONE);
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareToSendGroupChatScreen(group, conversation);
                alertDialog.dismiss();
            }
        });
        iv_close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Log.i(TAG, "onClick dialog btn:  cancel clicked ");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: its members button");
                getMembersListFromServer(group, conversation);

            }
        });
        alertDialog.show();
        // alertDialog.setCanceledOnTouchOutside(false);
    }

    private void getMembersListFromServer(GetGroupsCometchat groupModel, Conversation conversation) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "group_members_popup");
        requestMap.put(General.GROUP_ID, groupModel.getGroupId());

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getActivity(), getActivity());
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getActivity(), getActivity());
                if (response != null) {
                    int counterRotate = 0;
                    ArrayList<UserModel> userModelsArrayList = new ArrayList<UserModel>();
                    Log.e(TAG, " getMembersListFromServer " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("group_members_popup");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        counterRotate++;
                        if (jsonArray.getJSONObject(i).getString("status").equalsIgnoreCase("1")) {
                            userModelsArrayList.add(new UserModel(
                                    "" + jsonArray.getJSONObject(i).getString("photo"),
                                    "" + jsonArray.getJSONObject(i).getString("firstname"),
                                    "" + jsonArray.getJSONObject(i).getString("lastname"),
                                    "" + jsonArray.getJSONObject(i).getString("From_time"),
                                    "" + jsonArray.getJSONObject(i).getString("To_time"),
                                    "" + jsonArray.getJSONObject(i).getString("status")
                            ));
                        }
                    }
                    showMembersDialog(userModelsArrayList);
                } else {
                    Log.i(TAG, "getMembersListFromServer: ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showMembersDialog(ArrayList<UserModel> userModelsArrayList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_group_members, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        Log.i(TAG, "showMembersDialog: ");
        RecyclerView rv_members_list = view.findViewById(R.id.rv_members_list);
        ImageView iv_close_dialog = view.findViewById(R.id.iv_close_dialog);
        AdapterMembersList adapterMembersList = new AdapterMembersList(userModelsArrayList, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv_members_list.setLayoutManager(mLayoutManager);
        rv_members_list.setItemAnimator(new DefaultItemAnimator());
        rv_members_list.setAdapter(adapterMembersList);

        iv_close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
                                        Log.i(TAG, "getGroupMembersAndProviders : " + user.getUid());
                                        for (Members_ member : groupMembersArrayList) {
                                            Log.i(TAG, "getGroupMembersAndProviders : " + member.getComet_chat_id());
                                            if (user.getUid().equals(member.getComet_chat_id())) {
                                                Log.i(TAG, "getGroupMembersAndProviders: 2" + user.getUid());
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
                                                    showDialogGroup(message, group, searchGroupList, conversation);
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

    public void getUserDetails(String uid) {
        CometChat.getUser(uid, new CometChat.CallbackListener<User>() {
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
                        prepareToSendGroupChatScreen(group, conversation);
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
        progressBar.setVisibility(View.GONE);
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
        intent.putExtra(StringContract.IntentStrings.ALL_MEMBERS_STRING, groups_members);
        getActivity().startActivity(intent);
    }

    /*private void showDialogForGroup(String success, Conversation conversation) {
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

                        ((Group) conversation.getConversationWith()).getName()
                        intent.putExtra(StringContract.IntentStrings.NAME, (((Group) conversation.getConversationWith()).getName()));
                        intent.putExtra(StringContract.IntentStrings.GUID, (((Group) conversation.getConversationWith()).getGuid()));
                        intent.putExtra(StringContract.IntentStrings.AVATAR, "https://designstaging.sagesurfer.com/static//avatar/thumb/man.jpg");
                        intent.putExtra(StringContract.IntentStrings.STATUS, (((User) conversation.getConversationWith()).getStatus()));
                        intent.putExtra(StringContract.IntentStrings.TABS, "1");

                        intent.putExtra(StringContract.IntentStrings.TYPE, "group");
                        intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, ((Group) conversation.getConversationWith()).getOwner());
                        intent.putExtra(StringContract.IntentStrings.AVATAR, "https://designstaging.sagesurfer.com/static//avatar/thumb/man.jpg");
                        intent.putExtra(StringContract.IntentStrings.GROUP_TYPE, ((Group) conversation.getConversationWith()).getGroupType());
                        intent.putExtra(StringContract.IntentStrings.MEMBER_COUNT, ((Group) conversation.getConversationWith()).getMembersCount());
                        intent.putExtra(StringContract.IntentStrings.GROUP_DESC, "");
                        intent.putExtra(StringContract.IntentStrings.GROUP_PASSWORD, ((Group) conversation.getConversationWith()).getPassword());
                        intent.putExtra(StringContract.IntentStrings.TABS, "2");
                        intent.putExtra(StringContract.IntentStrings.ALL_MEMBERS_STRING, "" + groupMembersId);




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
    }*/
}