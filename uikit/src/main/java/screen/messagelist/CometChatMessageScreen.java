package screen.messagelist;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.emojiview.AXEmojiManager;
import com.aghajari.emojiview.emoji.iosprovider.AXIOSEmojiProvider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.constants.UIKitConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupMembersRequest;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Action;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.uikit.ComposeBox;
import com.cometchat.pro.uikit.DefaultSticker;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.uikit.Avatar;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.SmartReplyList;
import com.cometchat.pro.uikit.Sticker;
import com.cometchat.pro.uikit.sticker.StickerView;
import com.cometchat.pro.uikit.sticker.listener.StickerClickListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import adapter.MessageAdapter;
import adapter.WhiteboardActivity;
import constant.StringContract;
import listeners.ComposeActionListener;
import listeners.ExtensionResponseListener;
import listeners.MessageActionCloseListener;
import listeners.OnItemClickListener;
import listeners.OnMessageLongClick;
import listeners.StickyHeaderDecoration;
import okhttp3.RequestBody;
import screen.CometChatForwardMessageScreenActivity;
import screen.CometChatGroupDetailScreenActivity;
import screen.CometChatUserDetailScreenActivity;
import screen.Cometchat_log;
import screen.GroupDetailsScreen.FragmentGroupDetailScreen;
import screen.MessageActionFragment;
import screen.Models.TestModel;
import screen.message_information.CometChatMessageInfoScreenActivity;
import screen.threadconversation.CometChatThreadMessageActivity;
import utils.Extensions;
import utils.FontUtils;
import utils.MediaUtils;
import utils.KeyBoardUtils;
import utils.Utils;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

/**
 * Purpose - CometChatMessageScreen class is a fragment used to display list of messages and perform certain action on click of message.
 * It also provide search bar to perform search operation on the list of messages. User can send text,images,video and file as messages
 * to each other and in groups. User can also perform actions like edit message,delete message and forward messages to other user and groups.
 *
 * @see CometChat
 * @see User
 * @see Group
 * @see TextMessage
 * @see MediaMessage
 * <p>
 * Created on - 20th December 2019
 * <p>
 * Modified on - 16th January 2020
 */

public class CometChatMessageScreen extends Fragment implements View.OnClickListener, OnMessageLongClick, MessageActionCloseListener, ICometChatMessageScreen {
    private static final String TAG = "CometChatMessageScreen";
    private static final int LIMIT = 50;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE_PERMISSION = 22;
    private String name = "";
    private String status = "";
    private String conversationListType = null;
    private MessagesRequest messagesRequest;    //Used to fetch messages.
    private ComposeBox composeBox;
    private MessageAdapter messageAdapter;
    private SmartReplyList rvSmartReply;
    private ShimmerFrameLayout messageShimmer;
    SharedPreferences sharedPreferencesSessionId;
    SharedPreferences.Editor spSessionEditor;
    String Message, JoinTeam, MyTeam;
    LocalBroadcastManager bm;
    SharedPreferences preferensesCalling;
    Fragment fragment = new FragmentGroupDetailScreen();

    /**
     * <b>Avatar</b> is a UI Kit Component which is used to display user and group avatars.
     */
    private Avatar userAvatar;
    private Context context;
    private StickyHeaderDecoration stickyHeaderDecoration;
    private BaseMessage baseMessage;
    private Toolbar toolbar;
    private View view;
    private LinearLayout blockUserLayout;
    private ImageView replyMedia, replyClose, onGoingCallClose, btn_audio_call, btn_video_call;
    private RelativeLayout replyMessageLayout, editMessageLayout, onGoingCallView, bottomLayout;
    private TextView tvMessageTitle, tvMessageSubTitle, replyTitle, replyMessage, tvName,
            tvStatus, blockedUserName, onGoingCallTxt;
    private RecyclerView rvChatListView;
    //Used to display list of messages.
    private LinearLayoutManager linearLayoutManager;

    private String type, groupType, loggedInUserScope, groupOwnerId, NextGroupOrUserId, memberNames, groupDesc, currentLang,
            tabs, groupPassword, avatarUrl;
    private String team_Ids = "";
    private static String teamId, receiverId, team_logs_id;
    private int memberCount;
    private boolean isEdit, isReply, isBlockedByMe, isNoMoreMessages, isInProgress, isSmartReplyClicked;

    private Timer timer = new Timer();
    private Timer typingTimer = new Timer();
    private FontUtils fontUtils;
    private User loggedInUser = CometChat.getLoggedInUser();
    private String[] CAMERA_PERMISSION = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public int count = 0;
    private SharedPreferences sp;
    private ComposeActionListener composeActionListener;
    private StickerView stickersView;
    private RelativeLayout stickerLayout;
    private ImageView closeStickerView;
    //    private Fragment fragment = new CometChatMessageScreen();
    private Cometchat_log db;
    /*String from_chat = null;
   String to_chat = null;*/
    int comet_chat_type;
    String tabForReceivedMessages;
    /*String chat_message_id = null;
    String chat_group_id = null;
    String private_group_id = null;
    String chat_type = null;
    String read_chat = null;
    String custom_date = null;
    String duration = null;
    String is_duration_added = null;
    String device_tracking = null;
    String date_time = null;
    String modified_date = null;*/

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;
    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double LATITUDE = 0;
    private double LONGITUDE = 0;
    private SharedPreferences preferencesCheckCurrentActivity;
    private SharedPreferences.Editor editor;
    private ArrayList<View> optionsArrayList = new ArrayList<>();
    private List<BaseMessage> baseMessages = new ArrayList<>();
    private List<BaseMessage> messageList = new ArrayList<>();
    ArrayList<DefaultSticker> modelArrayList = new ArrayList<>();
    private MessageActionFragment messageActionFragment;
    private String StickerURL;
    SharedPreferences preferenCheckIntent;
    SharedPreferences.Editor editorCheckIntent;
    SharedPreferences.Editor editorCalling;
    String ChatGroupId;
    String ChatType = "Text";
    String Group_id, receiverMail, allMembersString = "";
    String group_all_members_id;
    ArrayList<String> myTeamArrayList = new ArrayList<>();
    ArrayList<String> joinTeamArrayList = new ArrayList<>();

    public CometChatMessageScreen() throws ClassNotFoundException {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = context.getSharedPreferences("login", MODE_PRIVATE);
        currentLang = sp.getString("currentLang", currentLang);
        teamId = sp.getString("teamIds", teamId);
        receiverId = sp.getString("membersIds", receiverId);
        Log.i(TAG, "onCreate: receiverId " + receiverId + " teamId " + teamId);
        handleArguments();

        if (getActivity() != null) fontUtils = FontUtils.getInstance(getActivity());
        preferencesCheckCurrentActivity = getActivity().getSharedPreferences("preferencesCheckCurrentActivity", MODE_PRIVATE);
        editor = preferencesCheckCurrentActivity.edit();
        editor.putBoolean("IsChatScreen", true);
        editor.apply();
        /*this is creted to remove highlighted group
         * added by rahul maske
         * */
        /*preferenOpenActivity
        = getActivity().getSharedPreferences("highlighted_group", Context.MODE_PRIVATE);
        editorOpenAct = preferenOpenActivity.edit();
        editorOpenAct.putBoolean("CheckIntent", false);
        editorOpenAct.apply();*/

        preferenCheckIntent = getActivity().getSharedPreferences("sp_check_push_intent", MODE_PRIVATE);
        editorCheckIntent = preferenCheckIntent.edit();
        editorCheckIntent.putBoolean("highlightList", false);
        editorCheckIntent.putBoolean("checkIntent", false);
        editorCheckIntent.putBoolean("checkedHighlightedGroup", true);
        editorCheckIntent.apply();

        /*here we are getting join team and myteam ids so that we can check that received message team id we get in myTeam column or in join team
         * so that we can redirect accordingly because tabs in team_logs_id can me wrong
         * commented and created by rahul maske*/
        SharedPreferences preferencesTeamsData = getActivity().getSharedPreferences("prefrencesPushRedirection", MODE_PRIVATE);
        JoinTeam = preferencesTeamsData.getString("JoinTeam", null);
        MyTeam = preferencesTeamsData.getString("MyTeams", null);
        Log.i(TAG, "onCreateView: MyTeamList " + MyTeam + " JoinTeamList " + JoinTeam);

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
        CheckUsersBlockStatus();
    }

    private void CheckUsersBlockStatus() {
        if (type != null && type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
            CometChat.getUser(NextGroupOrUserId, new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Log.d(TAG, "User details fetched for user: " + user.toString());
                    if (user.isHasBlockedMe()) {
                        /*blocked*/
                        ShowBlockedUserDialog();
                    } else {
                        /*not blocked*/
                        //ShowBlockedUserDialog();
                    }
                }

                @Override
                public void onError(CometChatException e) {
                    Log.d(TAG, "User details fetching failed with exception: " + e.getMessage());
                }
            });
        }
    }

    public void ShowBlockedUserDialog() {

        Log.i(TAG, "performAdapterClick: 3");
        androidx.appcompat.app.AlertDialog.Builder builder;
        builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        //Setting message manually and performing action on button click
        builder.setMessage(getActivity().getResources().getString(R.string.block_user_message))
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().onBackPressed();
                    }
                });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.setTitle("Alert Notification");
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }


    /**
     * This method is used to handle arguments passed to this fragment.
     */
    public void handleArguments() {
        if (getArguments() != null) {
            NextGroupOrUserId = getArguments().getString(StringContract.IntentStrings.SENDER_ID);
            avatarUrl = getArguments().getString(StringContract.IntentStrings.AVATAR);
            status = getArguments().getString(StringContract.IntentStrings.STATUS);
            name = getArguments().getString(StringContract.IntentStrings.NAME);
            if (status == null) {
                status = "";
            }

            type = getArguments().getString(StringContract.IntentStrings.TYPE);
            tabs = getArguments().getString(StringContract.IntentStrings.TABS);

            Log.e(TAG, "received tab is :" + tabs);
            SharedPreferences tabsPreferences = getActivity().getSharedPreferences("tabsPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editortabsPreferences = tabsPreferences.edit();
            editortabsPreferences.putString("tabs", tabs);
            editortabsPreferences.apply();

            team_Ids = getArguments().getString("teamId");
            Log.i(TAG, "handleArguments: team_ids" + teamId + " receiverId " + receiverId);
            Log.i(TAG, "handleArguments: 2 team_Ids" + team_Ids + " tabs  " + tabs);
            if (type != null && type.equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                NextGroupOrUserId = getArguments().getString(StringContract.IntentStrings.GUID);
                Group_id = "" + getArguments().getString(StringContract.IntentStrings.GUID);
                memberCount = getArguments().getInt(StringContract.IntentStrings.MEMBER_COUNT);
                groupDesc = getArguments().getString(StringContract.IntentStrings.GROUP_DESC);
                groupPassword = getArguments().getString(StringContract.IntentStrings.GROUP_PASSWORD);
                groupType = getArguments().getString(StringContract.IntentStrings.GROUP_TYPE);
                Log.i(TAG, "handleArguments: 1 userId" + NextGroupOrUserId + " groupId " + Group_id);
                if (getArguments().containsKey(StringContract.IntentStrings.ALL_MEMBERS_STRING)) {
                    group_all_members_id = getArguments().getString(StringContract.IntentStrings.ALL_MEMBERS_STRING);
                    Log.i(TAG, "handleArguments: group_all_members_id" + group_all_members_id);
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("IDS", NextGroupOrUserId);
                editor.commit();
            }

            if (tabs.equals("1")) {
                comet_chat_type = 1;
                ChatGroupId = "0";
                receiverMail = "onetoone_mail";
                Group_id = "";
            } else if (tabs.equals("2")) {
                receiverMail = "group_mail";
                comet_chat_type = 2;
                ChatGroupId = Group_id;
            } else if (tabs.equals("3") || tabs.equals("4")) {
                receiverMail = "myandjointeam";
                comet_chat_type = 3;
                Group_id = "";
                ChatGroupId = "0";
            }
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("IDS", NextGroupOrUserId);
            editor.apply();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        AXEmojiManager.install(context, new AXIOSEmojiProvider(context));
        view = inflater.inflate(R.layout.fragment_chat_screen, container, false);
        initViewComponent(view);
        return view;
    }

    /**
     * This is a main method which is used to initialize the view for this fragment.
     *
     * @param view
     */
    public void initViewComponent(View view) {
        setHasOptionsMenu(true);
        bottomLayout = view.findViewById(R.id.bottom_layout);
        composeBox = view.findViewById(R.id.message_box);
        messageShimmer = view.findViewById(R.id.shimmer_layout);
        btn_audio_call = view.findViewById(R.id.callBtn_iv);
        btn_video_call = view.findViewById(R.id.video_callBtn_iv);
        setComposeBoxListener();
        rvSmartReply = view.findViewById(R.id.rv_smartReply);
        editMessageLayout = view.findViewById(R.id.editMessageLayout);
        tvMessageTitle = view.findViewById(R.id.tv_message_layout_title);
        tvMessageSubTitle = view.findViewById(R.id.tv_message_layout_subtitle);
        ImageView ivMessageClose = view.findViewById(R.id.iv_message_close);
        ivMessageClose.setOnClickListener(this);
        preferensesCalling = getActivity().getSharedPreferences("callingPreferences", MODE_PRIVATE);
        replyMessageLayout = view.findViewById(R.id.replyMessageLayout);
        replyTitle = view.findViewById(R.id.tv_reply_layout_title);
        replyMessage = view.findViewById(R.id.tv_reply_layout_subtitle);
        replyMedia = view.findViewById(R.id.iv_reply_media);
        replyClose = view.findViewById(R.id.iv_reply_close);
        replyClose.setOnClickListener(this);

        rvChatListView = view.findViewById(R.id.rv_message_list);
        Button unblockUserBtn = view.findViewById(R.id.btn_unblock_user);
        unblockUserBtn.setOnClickListener(this);
        blockedUserName = view.findViewById(R.id.tv_blocked_user_name);
        blockUserLayout = view.findViewById(R.id.blocked_user_layout);
        tvName = view.findViewById(R.id.tv_name);
        tvStatus = view.findViewById(R.id.tv_status);
        userAvatar = view.findViewById(R.id.iv_chat_avatar);
        toolbar = view.findViewById(R.id.chatList_toolbar);
        toolbar.setOnClickListener(this);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        tvName.setTypeface(fontUtils.getTypeFace(FontUtils.robotoMedium));
        tvName.setText(name);
        setAvatar();
        rvChatListView.setLayoutManager(linearLayoutManager);
        db = new Cometchat_log(getActivity());


        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Utils.isDarkMode(context)) {
            bottomLayout.setBackgroundColor(getResources().getColor(R.color.darkModeBackground));
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            editMessageLayout.setBackground(getResources().getDrawable(R.drawable.left_border_dark));
            replyMessageLayout.setBackground(getResources().getDrawable(R.drawable.left_border_dark));
            composeBox.setBackgroundColor(getResources().getColor(R.color.darkModeBackground));
            rvChatListView.setBackgroundColor(getResources().getColor(R.color.darkModeBackground));
            tvName.setTextColor(getResources().getColor(R.color.textColorWhite));
        } else {
            bottomLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhite)));
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            editMessageLayout.setBackground(getResources().getDrawable(R.drawable.left_border));
            replyMessageLayout.setBackground(getResources().getDrawable(R.drawable.left_border));
            composeBox.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
            rvChatListView.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
            tvName.setTextColor(getResources().getColor(R.color.textColorWhite));
        }

        KeyBoardUtils.setKeyboardVisibilityListener(getActivity(), (View) rvChatListView.getParent(), keyboardVisible -> {
            if (keyboardVisible) {
                scrollToBottom();
                composeBox.ivMic.setVisibility(GONE);
                composeBox.ivSend.setVisibility(View.VISIBLE);
            } else {
                if (isEdit) {
                    composeBox.ivMic.setVisibility(GONE);
                    composeBox.ivSend.setVisibility(View.VISIBLE);
                } else {
                    composeBox.ivMic.setVisibility(View.VISIBLE);
                    composeBox.ivSend.setVisibility(GONE);

                }
            }
        });

        // Uses to fetch next list of messages if rvChatListView (RecyclerView) is scrolled in downward direction.
        rvChatListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                //for toolbar elevation animation i.e stateListAnimator
                toolbar.setSelected(rvChatListView.canScrollVertically(-1));
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (!isNoMoreMessages && !isInProgress) {
                    if (linearLayoutManager.findFirstVisibleItemPosition() == 10 || !rvChatListView.canScrollVertically(-1)) {
                        isInProgress = true;
                        fetchMessage();
                    }
                }
            }

        });
        rvSmartReply.setItemClickListener(new OnItemClickListener<String>() {
            @Override
            public void OnItemClick(String var, int position) {
                if (!isSmartReplyClicked) {
                    isSmartReplyClicked = true;
                    rvSmartReply.setVisibility(GONE);
                    sendMessage(var);
                }
            }
        });


        btn_audio_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatType = "Audio";
                SharedPreferences callingPreferences = getActivity().getSharedPreferences("callingPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editorCallingPref = callingPreferences.edit();
                editorCallingPref.putBoolean("callStarted", true);
                editorCallingPref.putString("type", type);
                editorCallingPref.putString("comet_chat_type", "" + comet_chat_type);
                editorCallingPref.putString("chat_type", "" + ChatType);
                editorCallingPref.putString("group_id", "" + Group_id);
                editorCallingPref.apply();
                Log.i(TAG, "Calling test tag  checkOnGoingCall: type " + callingPreferences.getString("type", "")
                        + " comet_chat_type " + callingPreferences.getString("comet_chat_type", "")
                        + " ChatType " + callingPreferences.getString("chat_type", "")
                        + " group_id " + callingPreferences.getString("group_id", ""));
                //private void saveChatLogToTheServer(String ChatType, String Message, int chat_group_id, int chat_message_id) {
                //saveChatLogToTheServer(ChatType, "", ChatGroupId, 0);
                checkOnGoingCall(CometChatConstants.CALL_TYPE_AUDIO);
            }
        });

        rvChatListView.removeItemDecoration(stickyHeaderDecoration);
        messageAdapter = null;
        messagesRequest = null;

        btn_video_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatType = "Video";
                SharedPreferences callingPreferences = getActivity().getSharedPreferences("callingPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editorCallingPref = callingPreferences.edit();
                editorCallingPref.putBoolean("callStarted", true);
                editorCallingPref.putString("type", type);
                editorCallingPref.putString("comet_chat_type", "" + comet_chat_type);
                editorCallingPref.putString("chat_type", "" + ChatType);
                editorCallingPref.putString("group_id", "" + Group_id);
                editorCallingPref.apply();
                Log.i(TAG, "Calling test tag  checkOnGoingCall: type " + callingPreferences.getString("type", "")
                        + " comet_chat_type " + callingPreferences.getString("comet_chat_type", "")
                        + " ChatType " + callingPreferences.getString("chat_type", "")
                        + " group_id " + callingPreferences.getString("group_id", ""));
                checkOnGoingCall(CometChatConstants.CALL_TYPE_VIDEO);
            }
        });

        //Check Ongoing Call
        onGoingCallView = view.findViewById(R.id.ongoing_call_view);
        onGoingCallClose = view.findViewById(R.id.close_ongoing_view);
        onGoingCallTxt = view.findViewById(R.id.ongoing_call);
        checkOnGoingCall();
    }

    public void checkOnGoingCall(String callType) {
        if (CometChat.getActiveCall() != null
                && CometChat.getActiveCall().getCallStatus().equals(CometChatConstants.CALL_STATUS_ONGOING)
                && CometChat.getActiveCall().getSessionId() != null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(getResources().getString(R.string.ongoing_call))
                    .setMessage(getResources().getString(R.string.ongoing_call_message))
                    .setPositiveButton(getResources().getString(R.string.join), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utils.joinOnGoingCall(context);
                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    btn_audio_call.setEnabled(true);
                    btn_video_call.setEnabled(true);
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            if (type.equals("user")) {
                initiatecall(context, NextGroupOrUserId, CometChatConstants.RECEIVER_TYPE_USER, callType);
            } else {
                initiateGroupCall(NextGroupOrUserId, CometChatConstants.RECEIVER_TYPE_GROUP, callType);
            }
        }
    }

    public void initiatecall(Context context, String recieverID, String receiverType, String callType) {
        Call call = new Call(recieverID, receiverType, callType);
        JSONObject jsonObject = new JSONObject();
        JSONArray languageArray = new JSONArray();
        languageArray.put("");

        switch (tabs) {
            case "1":
                try {
                    jsonObject.put("team_logs_id", 0);
                    jsonObject.put("message_translation_languages", languageArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "3":
                team_logs_id = receiverId + "_-" + teamId + "_-" + 3;
                try {
                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("message_translation_languages", languageArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "4":
                team_logs_id = receiverId + "_-" + teamId + "_-" + 4;
                try {
                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("message_translation_languages", languageArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        call.setMetadata(jsonObject);
        CometChat.initiateCall(call, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                /*  shared preferences created to store sessionId to compare in onOutgoingCallAccepted in CometChatCallListener
                 * to not create new instances for call only one session should be create
                 * update on 15-03-2021 by rahul maske*/
                User user = (User) call.getCallReceiver();
                Log.e(TAG, "onSuccess: receiver avtar detail" + user.getAvatar());
                Log.e(TAG, "onSuccess: receiver uid detail" + user.getUid());
                sharedPreferencesSessionId = getActivity().getSharedPreferences("login", MODE_PRIVATE);
                spSessionEditor = sharedPreferencesSessionId.edit();
                spSessionEditor.putString("sessionId", "" + call.getSessionId());
                spSessionEditor.putBoolean("onGoingCall", false);
                spSessionEditor.apply();

                if (type.equals("user")) {
                    //
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            saveChatLogToTheServer(ChatType, " ", ChatGroupId, call.getId());
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                    Utils.startCallIntent(context, ((User) call.getCallReceiver()), call.getType(), true, call.getSessionId());
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: " + e.getMessage());
                Snackbar.make(((Activity) context).getWindow().getDecorView().getRootView(), context.getResources().getString(R.string.call_initiate_error) + ":" + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void initiateGroupCall(String recieverID, String receiverType, String callType) {
        Call call = new Call(recieverID, receiverType, callType);
        JSONObject jsonObject = new JSONObject();
        JSONArray languageArray = new JSONArray();
        languageArray.put("");

        try {
            jsonObject.put("message_translation_languages", languageArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        call.setMetadata(jsonObject);
        CometChat.initiateCall(call, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                Log.i(TAG, "onSuccess: initiateGroupCall");
                sharedPreferencesSessionId = getActivity().getSharedPreferences("login", MODE_PRIVATE);
                spSessionEditor = sharedPreferencesSessionId.edit();
                spSessionEditor.putString("sessionId", "" + call.getSessionId());
                spSessionEditor.putBoolean("onGoingCall", false);
                spSessionEditor.apply();

                SharedPreferences spCallScreenFlag = context.getSharedPreferences(" call_popup_preferences ", MODE_PRIVATE);
                SharedPreferences.Editor spEditorCallScreenFlag = spCallScreenFlag.edit();
                spEditorCallScreenFlag.putBoolean("openCallPopup", true);
                spEditorCallScreenFlag.apply();

                Log.i(TAG, General.MY_TAG + " onSuccess: sessionId " + sharedPreferencesSessionId.getString("sessionId", null));
                Utils.startGroupCallIntent(getActivity(), ((Group) call.getCallReceiver()), call.getType(), true, call.getSessionId());
                Group group = ((Group) call.getCallReceiver());
                Log.e(TAG, "onSuccess: " + group.getGroupType());

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        saveChatLogToTheServer(ChatType, " ", ChatGroupId, call.getId());
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();

                //
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }

    public void checkOnGoingCall() {
        if (CometChat.getActiveCall() != null && CometChat.getActiveCall().getCallStatus().equals(CometChatConstants.CALL_STATUS_ONGOING) && CometChat.getActiveCall().getSessionId() != null) {
            if (onGoingCallView != null)
                onGoingCallView.setVisibility(View.VISIBLE);
            if (onGoingCallTxt != null) {
                onGoingCallTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onGoingCallView.setVisibility(View.GONE);
                        Utils.joinOnGoingCall(getContext());
                    }
                });
            }
            if (onGoingCallClose != null) {
                onGoingCallClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onGoingCallView.setVisibility(GONE);
                    }
                });
            }
        } else if (CometChat.getActiveCall() != null) {
            if (onGoingCallView != null)
                onGoingCallView.setVisibility(GONE);
            Log.e(TAG, "checkOnGoingCall: " + CometChat.getActiveCall().toString());
        }
    }

    public void setComposeBoxListener() {
        composeBox.setComposeBoxListener(new ComposeActionListener() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    sendTypingIndicator(false);
                } else {
                    sendTypingIndicator(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (typingTimer == null) {
                    typingTimer = new Timer();
                }
                endTypingTimer();
            }

            @Override
            public void onAudioActionClicked(ImageView audioIcon) {
                if (Utils.hasPermissions(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    startActivityForResult(MediaUtils.openAudio(getActivity()), StringContract.RequestCode.AUDIO);
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StringContract.RequestCode.AUDIO);
                }
            }

            @Override
            public void onCameraActionClicked(ImageView cameraIcon) {
                if (Utils.hasPermissions(getContext(), CAMERA_PERMISSION)) {
                    startActivityForResult(MediaUtils.openCamera(getContext()), StringContract.RequestCode.CAMERA);
                } else {
                    requestPermissions(CAMERA_PERMISSION, StringContract.RequestCode.CAMERA);
                }
            }


            @Override
            public void onGalleryActionClicked(ImageView galleryIcon) {
                if (Utils.hasPermissions(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    startActivityForResult(MediaUtils.openGallery(getActivity()), StringContract.RequestCode.GALLERY);
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StringContract.RequestCode.GALLERY);

                }
            }

            @Override
            public void onFileActionClicked(ImageView fileIcon) {
                if (Utils.hasPermissions(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                    startActivityForResult(MediaUtils.getFileIntent(StringContract.IntentStrings.EXTRA_MIME_DOC), StringContract.RequestCode.FILE);
                    startActivityForResult(MediaUtils.getFileIntent(StringContract.IntentStrings.EXTRA_MIME_DOC), StringContract.RequestCode.FILE);

                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StringContract.RequestCode.FILE);
                }
            }

            @Override
            public void onSendActionClicked(EditText editText) {
                String message = editText.getText().toString().trim();
                editText.setText("");
                editText.setHint(getString(R.string.message));
                if (isEdit) {
                    editMessage(baseMessage, message);
                    editMessageLayout.setVisibility(GONE);
                } else if (isReply) {
                    replyMessage(baseMessage, message);
                    replyMessageLayout.setVisibility(GONE);
                } else if (!message.isEmpty()) {
                    Log.e("Send Method", message);
                    Message = message;
                    sendMessage(message);
                }
            }

            @Override
            public void onVoiceNoteComplete(String string) {
                if (string != null) {
                    File audioFile = new File(string);
                    sendMediaMessage(audioFile, CometChatConstants.MESSAGE_TYPE_AUDIO);
                }
            }

            @Override
            public void onLocationActionClicked() {
                if (Utils.hasPermissions(getContext(), Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    initLocation();
//                    locationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
                    boolean provider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (!provider) {
                        turnOnLocation();
                    } else {
                        getLocation();
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, StringContract.RequestCode.LOCATION);
                }
            }

            @Override
            public void onSendCustomActionClicked(String string) {
                if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
                    SendCustomMessage(string, NextGroupOrUserId);
                else
                    SendCustomMessage(string, NextGroupOrUserId);
            }

            @Override
            public void onStickerClicked() {
                //checkUserPermissions();
                stickersView = view.findViewById(R.id.stickersView);
                stickerLayout = view.findViewById(R.id.sticker_layout);
                closeStickerView = view.findViewById(R.id.close_sticker_layout);
                stickerLayout.setVisibility(View.VISIBLE);
                /*if (CometChat.isExtensionEnabled("stickers")) {
                    Extensions.fetchStickers(new ExtensionResponseListener() {
                        @Override
                        public void OnResponseSuccess(Object var) {
                            JSONObject stickersJSON = (JSONObject) var;
                            stickersView.setData(Id, type, Extensions.extractStickersFromJSON(stickersJSON));
                        }
                        @Override
                        public void OnResponseFailed(CometChatException e) {
                            Toast.makeText(context, "Error:" + e.getCode()+ "msg "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }*/
                if (CometChat.isExtensionEnabled("stickers")) {
                    Extensions.fetchStickers(new ExtensionResponseListener() {
                        @Override
                        public void OnResponseSuccess(Object var) {
                            JSONObject stickersJSON = (JSONObject) var;
                            stickersView.setData(NextGroupOrUserId, type, Extensions.extractStickersFromJSON(stickersJSON));
                        }

                        @Override
                        public void OnResponseFailed(CometChatException e) {
                            Toast.makeText(context, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                /*CometChat.callExtension("stickers", "GET", "/v1/fetch", null, new
                        CometChat.CallbackListener<JSONObject>() {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {
                                Log.e(TAG, "onSuccess: stickers " );
                                //JSONObject stickersJSON = (JSONObject) jsonObject;
                                try {
                                    List<Sticker> listDefaultStickers=new ArrayList<>();
                                    List<Sticker> listCustomStickers=new ArrayList<>();
                                    listDefaultStickers= (List<Sticker>) jsonObject.getJSONArray("defaultStickers");
                                    listCustomStickers= (List<Sticker>) jsonObject.getJSONArray("customStickers");
                                    HashMap<String,List<Sticker>> stickersHashmap=new HashMap<>();
                                    stickersHashmap.put("defaultStickers",listDefaultStickers);
                                    stickersHashmap.put("customStickers",listCustomStickers);
                                    stickersView.setData(Id, type, stickersHashmap);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(CometChatException e) {
                                Toast.makeText(context, "msg:" + e.getMessage() + " error " + e.getCode(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "stickers fetching onError: " + e.getMessage());
                            }
                        });*/
                closeStickerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stickerLayout.setVisibility(GONE);
                    }
                });
                /* here we are setting sticker clicked listener
                 * on click on any sticker the sticker will send to the user
                 * updated by rahulmsk */

                stickersView.setStickerClickListener(new StickerClickListener() {
                    @Override
                    public void onClickListener(Sticker sticker) /*throws MalformedURLException*/ {
                        StickerURL = sticker.getUrl();
                        /*try {
                            stickerData.put("url", sticker.getUrl());
                            stickerData.put("name", sticker.getName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //sendCustomMessage(StringContract.IntentStrings.STICKERS, stickerData);
                        //sendCustomSticker(StringContract.IntentStrings.IMAGE, stickerData);*/
                        //sendMediaMessageForStickers("" + sticker.getUrl());
                        //saveStickerToServer(General.ACTION_SAVE_STICKER, sticker.getUrl(),sticker.getName());

                        /*new DownloadImage().execute(sticker.getUrl());
                        stickerLayout.setVisibility(GONE);*/
                        checkUserPermissions();
                    }
                });
            }
        });
    }

    public void checkUserPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle("Storage permission required")
                        .setMessage("To upload image need to allow storage permission")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_STORAGE_PERMISSION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE_PERMISSION);
            }
        } else {
            new DownloadImage().execute(StickerURL);
            stickerLayout.setVisibility(GONE);
            Log.i(TAG, "checkUserPermissions: main else");

        }
    }


    public void getLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double lon = location.getLongitude();
                    double lat = location.getLatitude();

                    JSONObject customData = new JSONObject();
                    try {
                        customData.put("latitude", lat);
                        customData.put("longitude", lon);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    initAlert(customData);
                } else {
                    Toast.makeText(context, getString(R.string.unable_to_get_location), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void initAlert(JSONObject customData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(context).inflate(R.layout.map_share_layout, null);
        builder.setView(view);
        try {
            LATITUDE = customData.getDouble("latitude");
            LONGITUDE = customData.getDouble("longitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView address = view.findViewById(R.id.address);
        address.setText("Address: " + Utils.getAddress(context, LATITUDE, LONGITUDE));
        ImageView mapView = view.findViewById(R.id.map_vw);
        String mapUrl = StringContract.MapUrl.MAPS_URL + LATITUDE + "," + LONGITUDE + "&key=" +
                StringContract.MapUrl.MAP_ACCESS_KEY;
        Glide.with(this)
                .load(mapUrl)
                .into(mapView);

        builder.setPositiveButton(getString(R.string.share), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendCustomMessage(StringContract.IntentStrings.LOCATION, customData);
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }


    public void sendCustomMessage(String stickers, JSONObject stickerData) {
        CustomMessage customMessage;
        if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
            customMessage = new CustomMessage(NextGroupOrUserId, CometChatConstants.RECEIVER_TYPE_USER, stickers, stickerData);
        else
            customMessage = new CustomMessage(NextGroupOrUserId, CometChatConstants.RECEIVER_TYPE_GROUP, stickers, stickerData);

        JSONObject metadataObject = new JSONObject();
        JSONArray languageArray = new JSONArray();
        languageArray.put("");

        if (tabs.equals("1")) {
            try {
                metadataObject.put("team_logs_id", 0);
                metadataObject.put("message_translation_languages", languageArray);
                customMessage.setMetadata(metadataObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (tabs.equals("3")) {
            if (receiverId != null && teamId != null) {
                team_logs_id = receiverId + "_-" + teamId + "_-" + tabs;
                try {
                    metadataObject.put("team_logs_id", team_logs_id);
                    metadataObject.put("message_translation_languages", languageArray);
                    customMessage.setMetadata(metadataObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } else if (tabs.equals("4")) {
            if (receiverId != null && teamId != null) {
                team_logs_id = receiverId + "_-" + teamId + "_-" + tabs;
                try {
                    metadataObject.put("team_logs_id", team_logs_id);
                    metadataObject.put("message_translation_languages", languageArray);
                    customMessage.setMetadata(metadataObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.e("stickerData", customMessage.toString());

        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage customMessage) {
                Log.e(TAG, "CometChat.sendCustomMessage Stickers => onSuccess: " + customMessage);
                MediaUtils.playSendSound(context, R.raw.outgoing_message);
                if (messageAdapter != null) {
                    messageAdapter.addMessage(customMessage);
                    scrollToBottom();
                }
            }

            @Override
            public void onError(CometChatException e) {
                if (getActivity() != null) {
                    Log.e(TAG, "CometChat.sendCustomMessage Stickers => onError: " + e.getMessage());
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    /*new created for test sticker */
    public void sendCustomSticker(String stickers, JSONObject stickerData) {
        CustomMessage customMessage;

        if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
            customMessage = new CustomMessage(NextGroupOrUserId, CometChatConstants.RECEIVER_TYPE_USER, "image", stickerData);
        else
            customMessage = new CustomMessage(NextGroupOrUserId, CometChatConstants.RECEIVER_TYPE_GROUP, "image", stickerData);

        JSONObject metadataObject = new JSONObject();
        JSONArray languageArray = new JSONArray();
        languageArray.put("");

        if (tabs.equals("1")) {
            try {
                metadataObject.put("team_logs_id", 0);
                metadataObject.put("message_translation_languages", languageArray);
                customMessage.setMetadata(metadataObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (tabs.equals("3")) {
            if (receiverId != null && teamId != null) {
                team_logs_id = receiverId + "_-" + teamId + "_-" + tabs;
                try {
                    metadataObject.put("team_logs_id", team_logs_id);
                    metadataObject.put("message_translation_languages", languageArray);
                    customMessage.setMetadata(metadataObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } else if (tabs.equals("4")) {
            if (receiverId != null && teamId != null) {
                team_logs_id = receiverId + "_-" + teamId + "_-" + tabs;
                try {
                    metadataObject.put("team_logs_id", team_logs_id);
                    metadataObject.put("message_translation_languages", languageArray);
                    customMessage.setMetadata(metadataObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.e("stickerData", customMessage.toString());

        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage customMessage) {
                Log.e(TAG, "CometChat.sendCustomMessage => onSuccess: " + customMessage);
                MediaUtils.playSendSound(context, R.raw.outgoing_message);
                if (messageAdapter != null) {
                    messageAdapter.addMessage(customMessage);
                    scrollToBottom();
                }
            }

            @Override
            public void onError(CometChatException e) {
                if (getActivity() != null) {
                    Log.e(TAG, "CometChat.sendCustomMessage => onError: " + e.getMessage());
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void turnOnLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.turn_on_gps));
        builder.setPositiveButton(getString(R.string.on), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), StringContract.RequestCode.LOCATION);
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    public void SendCustomMessage(String loudScreaming, String id) {
        String UID = id;
        String customType = "extension_whiteboard";
        JSONObject customData = new JSONObject();
        try {
            customData.put("message", "whiteboard request send successfully");
            customData.put("url", loudScreaming);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject metadataObject = new JSONObject();
        JSONArray languageArray = new JSONArray();
        languageArray.put("");

        CustomMessage customMessage;

        if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
            customMessage = new CustomMessage(UID, CometChatConstants.RECEIVER_TYPE_USER, customType, customData);
        else
            customMessage = new CustomMessage(UID, CometChatConstants.RECEIVER_TYPE_GROUP, customType, customData);

        if (tabs.equals("1")) {
            try {
                metadataObject.put("team_logs_id", 0);
                metadataObject.put("message_translation_languages", languageArray);
                metadataObject.put("whiteboard_URL_one", loudScreaming);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (tabs.equals("3")) {
            team_logs_id = receiverId + "_-" + teamId + "_-" + 3;
            try {
                metadataObject.put("team_logs_id", team_logs_id);
                metadataObject.put("message_translation_languages", languageArray);
                metadataObject.put("whiteboard_URL", loudScreaming);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else if (tabs.equals("4")) {
            team_logs_id = receiverId + "_-" + teamId + "_-" + 4;
            try {
                metadataObject.put("team_logs_id", team_logs_id);
                metadataObject.put("message_translation_languages", languageArray);
                metadataObject.put("whiteboard_URL", loudScreaming);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            try {
                metadataObject.put("message_translation_languages", languageArray);
                metadataObject.put("whiteboard_URL_group", loudScreaming);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        customMessage.setMetadata(metadataObject);
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage customMessage) {
                Log.e(TAG, "whiteboard response - " + customMessage.toString());
                MediaUtils.playSendSound(context, R.raw.outgoing_message);
                if (loudScreaming != null) {
                    Intent intent = new Intent(context, WhiteboardActivity.class);
                    intent.putExtra("whiteBoardUrl", loudScreaming);
                    context.startActivity(intent);
                    // Log.i(TAG, "onSuccess: whiteBoardUrl "+loudScreaming +" customMessage "+customMessage.ge);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, e.getMessage());
                Log.e("ERROR", e.toString());
            }
        });

    }

    public void initLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //
                //
                //
                //
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: ");
        switch (requestCode) {

            case StringContract.RequestCode.CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)

                    startActivityForResult(MediaUtils.openCamera(getActivity()), StringContract.RequestCode.CAMERA);
                else
                    showSnackBar(view.findViewById(R.id.message_box), getResources().getString(R.string.grant_camera_permission));
                break;
            case StringContract.RequestCode.GALLERY:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startActivityForResult(MediaUtils.openGallery(getActivity()), StringContract.RequestCode.GALLERY);
                else
                    showSnackBar(view.findViewById(R.id.message_box), getResources().getString(R.string.grant_storage_permission));
                break;
            case StringContract.RequestCode.FILE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startActivityForResult(MediaUtils.getFileIntent(StringContract.IntentStrings.EXTRA_MIME_DOC), StringContract.RequestCode.FILE);
                else
                    showSnackBar(view.findViewById(R.id.message_box), getResources().getString(R.string.grant_storage_permission));
                break;
            case StringContract.RequestCode.LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocation();
//                    locationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
                    boolean provider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (!provider) {
                        turnOnLocation();
                    } else {
                        getLocation();
                    }
                } else
                    showSnackBar(view.findViewById(R.id.message_box), getResources().getString(R.string.grant_location_permission));
                break;

            case StringContract.RequestCode.WRITE_EXT_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult: StickerURL : " + StickerURL);
                    new DownloadImage().execute(StickerURL);
                    stickerLayout.setVisibility(GONE);
                } else {
                    showSnackBar(view.findViewById(R.id.message_box), getResources().getString(R.string.grant_storage_permission));
                }

        }
    }

    public void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * This method is used to get Group Members and display names of group member.
     *
     * @see GroupMember
     * @see GroupMembersRequest
     */
    public void getMember() {
        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(NextGroupOrUserId).setLimit(100).build();
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> list) {
                String s[] = new String[0];
                if (list != null && list.size() != 0) {
                    s = new String[list.size()];
                    for (int j = 0; j < list.size(); j++) {
                        s[j] = list.get(j).getName();
                    }
                }
                setSubTitle(s);
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Group Member list fetching failed with exception: " + e.getMessage());
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Incase if user is blocked already, then this method is used to unblock the user .
     *
     * @see CometChat#unblockUsers(List, CometChat.CallbackListener)
     */
    public void unblockUser() {
        ArrayList<String> uids = new ArrayList<>();
        uids.add(NextGroupOrUserId);
        CometChat.unblockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                Snackbar.make(rvChatListView, String.format(getResources().getString(R.string.user_unblocked), name), Snackbar.LENGTH_LONG).show();
                blockUserLayout.setVisibility(GONE);
                isBlockedByMe = false;
                messagesRequest = null;
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * This method is used to set GroupMember names as subtitle in toolbar.
     *
     * @param users
     */

    public void setSubTitle(String... users) {
        if (users != null && users.length != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String user : users) {
                stringBuilder.append(user).append(",");
            }
            memberNames = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
            tvStatus.setText(memberNames);
        }
    }

    /**
     * This method is used to fetch message of users & groups. For user it fetches previous 100 messages at
     * a time and for groups it fetches previous 30 messages. You can change limit of messages by modifying
     * number in <code>setLimit()</code>
     * This method also mark last message as read using markMessageAsRead() present in this class.
     * So all the above messages get marked as read.
     *
     * @see MessagesRequest#fetchPrevious(CometChat.CallbackListener)
     */
    public void fetchMessage() {
        Log.i(TAG, "fetchMessage: called");
        if (messagesRequest == null) {
            if (type != null) {
                if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                    Log.e(TAG, "fetchMessage: ID = " + NextGroupOrUserId);
                    /* previous request commenting and adding new request as per cometchat sample app
                     * commenting on 25-06-2021 by rahul maske */
                    messagesRequest = new MessagesRequest.MessagesRequestBuilder().setLimit(LIMIT)
                            .setTypes(UIKitConstants.MessageRequest.messageTypesForUser)
                            .setCategories(UIKitConstants.MessageRequest.messageCategoriesForUser)
                            .hideReplies(true).setUID(NextGroupOrUserId).build();
                    /*messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                            .setLimit(LIMIT).hideReplies(true).setUID(SenderId).build();*/
                } else {
                    messagesRequest = new MessagesRequest.MessagesRequestBuilder().setLimit(LIMIT)
                            .setTypes(UIKitConstants.MessageRequest.messageTypesForGroup)
                            .setCategories(UIKitConstants.MessageRequest.messageCategoriesForGroup)
                            .hideReplies(true).setGUID(NextGroupOrUserId).hideMessagesFromBlockedUsers(true).build();

                    /* previous request commenting and adding new request as per cometchat sample app
                     * commenting on 25-06-2021 by rahul maske */
                    /* messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                            .setLimit(LIMIT).hideReplies(true).setGUID(SenderId).hideMessagesFromBlockedUsers(true).build();*/
                }
            }
        }

        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> baseMessages) {
                isInProgress = false;
                List<BaseMessage> filteredMessageList = filterBaseMessages(baseMessages);
                initMessageAdapter(filteredMessageList);
                for (BaseMessage bm : baseMessages) {
                    Log.i(TAG, "onSuccess: baseMessages " + bm);
                }

                if (baseMessages.size() != 0) {
                    stopHideShimmer();
                    BaseMessage baseMessage = baseMessages.get(baseMessages.size() - 1);
                    markMessageAsRead(baseMessage);
                }
                if (baseMessages.size() == 0) {
                    stopHideShimmer();
                    isNoMoreMessages = true;
                }
                //pter adapter=new MessageAdapter(getActivity(),filteredMessageList,"notUsedArgument");
                //rvChatListView.setAdapter(adapter);
                /*for (BaseMessage message: baseMessages) {
                    if (message instanceof TextMessage) {
                        Log.d(TAG, "Text message received successfully: " +
                                ((TextMessage) message).toString());
                    } else if (message instanceof MediaMessage) {
                        Log.d(TAG, "Media message received successfully: " +
                                ((MediaMessage) message).toString());
                    }
                }*/
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: " + e.getMessage());
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(context);
                builder.setMessage(e.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                getActivity().finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Alert Notification");
                alert.show();
            }
        });
    }

    public List<BaseMessage> filterBaseMessages(List<BaseMessage> baseMessages) {
        List<BaseMessage> tempList = new ArrayList<>();
        int editAction = 0;
        for (BaseMessage baseMessage : baseMessages) {
            JSONObject metadata = baseMessage.getMetadata();
            Log.e("base message => ", baseMessage.toString());
            switch (tabs) {
                // Frirends chat
                case "1":
                    Log.e("tab 1 friend", baseMessage.toString());
                    if (baseMessage.getCategory().equals("custom")) {
                        int team_log;
                        Log.e(TAG, General.MY_TAG + " filterBaseMessages: metadata " + baseMessage.getMetadata());
                        try {
                            team_log = metadata.getInt("team_logs_id");
                            Log.e(TAG, General.MY_TAG + " filterBaseMessages: team_log_id " + metadata.getInt("team_logs_id"));
                            if (team_log == 0) {
                                if (baseMessage.getType().equals("extension_whiteboard")) {
                                    try {
                                        if (baseMessage.getMetadata().has("deleted_one_to_one")) {
                                            if (baseMessage.getMetadata().getString("deleted_one_to_one").equalsIgnoreCase("0")) {
                                                baseMessage.setDeletedAt(12345);
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            Log.i(TAG, General.MY_TAG + " filterBaseMessages: type = extension_whiteboard condition");
                                            String s = null;
                                            s = baseMessage.getMetadata().getString("whiteboard_URL_one");
                                            Log.i(TAG, General.MY_TAG + " filterBaseMessages: whiteboard_URL_one" + s);
                                            if (!s.isEmpty()) {
                                                Log.e(TAG, General.MY_TAG + " filterBaseMessages2: " + baseMessage.toString());
                                                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                    Action action = ((Action) baseMessage);
                                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                    } else {
                                                        Log.i(TAG, "filterBaseMessages:  " + General.MY_TAG + " delete else ");
                                                        tempList.add(baseMessage);
                                                    }
                                                } else {
                                                    Log.i(TAG, "filterBaseMessages:  " + General.MY_TAG + " action else ");
                                                    tempList.add(baseMessage);
                                                }
                                            }

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else if ((baseMessage.getType().equals("stickers"))) {
                                    String s = null;
                                    try {
                                        s = baseMessage.getMetadata().getString("stickers");
                                        if (!s.isEmpty()) {
                                            Log.e(TAG, "filterBaseMessages: " + baseMessage.toString());
                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                Action action = ((Action) baseMessage);
                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                } else {
                                                    tempList.add(baseMessage);
                                                }
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //implementation 'com.cometchat:pro-android-chat-sdk:2.3.1'
                    } else if (baseMessage.getCategory().equals("message")) {
                        String team_log;
//                        Log.i(TAG, "filterBaseMessages: Reply at checking " + baseMessage.getMetadata() + " message " + ((TextMessage) baseMessage).getText());
                        if (metadata != null || baseMessage.getDeletedAt() != 0) {
                            try {
                                if (metadata != null) {
                                    team_log = metadata.getString("team_logs_id");
                                } else {
                                    team_log = "0";
                                }
                                if (team_log.equals("0") || baseMessage.getDeletedAt() != 0) {
                                    if (baseMessage.getType().equals("text")) {
                                        if (metadata != null && baseMessage.getEditedAt() != 0) {
                                            /*Here we are marking message as deleted so that user can check that it is deleted */
                                            //Log.i(TAG, "filterBaseMessages: edited at block 0" + ((TextMessage) baseMessage).getText());
                                            if (metadata.has("deleted_one_to_one")) {
                                                if (metadata.getString("deleted_one_to_one").equals("0")) {
                                                    /*setting delated at with some value to understand this is deleted message.. this value is just random value...*/
                                                    baseMessage.setDeletedAt(12345);
                                                }
                                            } else {
                                                //                                             Log.i(TAG, "filterBaseMessages: edited at block 1");
                                                try {
                                                    long id = baseMessage.getId();
                                                    String textMessage = ((TextMessage) baseMessage).getText();
                                                    String currentLang = sp.getString("currentLang", null);
                                                    JSONObject body = new JSONObject();
                                                    JSONArray languages = new JSONArray();
                                                    languages.put(currentLang);
                                                    body.put("msgId", id);
                                                    body.put("languages", languages);
                                                    body.put("text", textMessage);
                                                    Log.i(TAG, "filterBaseMessages: edited at block 2");
                                                    // Do something after 5s = 5000ms
                                                    CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                            new CometChat.CallbackListener<JSONObject>() {
                                                                @Override
                                                                public void onSuccess(JSONObject jsonObject) {
                                                                    try {
                                                                        Log.i(TAG, "filterBaseMessages: edited at block 3");
                                                                        JSONObject meta = baseMessage.getMetadata();
                                                                        meta.accumulate("values", jsonObject);
                                                                        String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");

                                                                        // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                                        //baseMessage.setMetadata(meta);

                                                                        ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                                        baseMessage.setMetadata(meta);
                                                                        messageAdapter.setUpdatedMessage(baseMessage);
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onError(CometChatException e) {
                                                                    // Some error occured
                                                                    Log.i(TAG, "onError: " + e.getMessage());
                                                                    Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                                }
                                                            });


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                Action action = ((Action) baseMessage);
                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                    tempList.add(baseMessage);
                                                } else {
                                                    tempList.add(baseMessage);
                                                }
                                            } else {
                                                //                                          Log.i(TAG, "filterBaseMessages: edited at block 5");
                                                tempList.add(baseMessage);
                                            }
                                        } else if (metadata != null && metadata.has("type")) {
                                            //this is for replied message
                                            if (metadata.has("message") && metadata.getString("type").equalsIgnoreCase("reply")) {
                                                Log.i(TAG, "filterBaseMessages: " + metadata.getString("message"));
                                                JSONObject body = new JSONObject();
                                                JSONArray languages = new JSONArray();
                                                languages.put(sp.getString("currentLang", currentLang));
                                                body.put("msgId", baseMessage.getId());
                                                body.put("languages", languages);
                                                body.put("text", metadata.getString("message"));
                                                Log.i(TAG, "filterBaseMessages: edited at block 2");
                                                // Do something after 5s = 5000ms
                                                CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                        new CometChat.CallbackListener<JSONObject>() {
                                                            @Override
                                                            public void onSuccess(JSONObject jsonObject) {
                                                                try {
                                                                    // Log.i(TAG, "filterBaseMessages: edited at block 3 translated response for replied message "+jsonObject);
                                                                    JSONObject meta = baseMessage.getMetadata();
                                                                    //meta.accumulate("values", jsonObject);
                                                                    String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");
                                                                    Log.i(TAG, "filterBaseMessages: onSuccess at block 3 messageTranslatedString " + messageTranslatedString);
                                                                    metadata.remove("message");
                                                                    metadata.put("message", messageTranslatedString);
                                                                    messageAdapter.setUpdatedMessage(baseMessage);
                                                                    Log.i(TAG, "filterBaseMessages onSuccess: metadata for replied translate " + metadata);
                                                                    // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                                    //baseMessage.setMetadata(meta);
                                                                    // ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                                    //baseMessage.setMetadata(meta);


                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(CometChatException e) {
                                                                // Some error occured
                                                                Log.i(TAG, "onError: " + e.getMessage());
                                                                Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                            }
                                                        });
                                                try {
                                                    if (metadata.has("@injected")) {
                                                        JSONObject injectedObject = null;
                                                        injectedObject = metadata.getJSONObject("@injected");
                                                        if (injectedObject.has("extensions")) {
                                                            JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                                            if (extensionsObject.has("message-translation")) {
                                                                JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                                JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                                HashMap<String, String> translationsMap = new HashMap<String, String>();
                                                                for (int i = 0; i < translations.length(); i++) {
                                                                    JSONObject translation = translations.getJSONObject(i);
                                                                    String translatedText = translation.getString("message_translated");
                                                                    String translatedLanguage = translation.getString("language_translated");
                                                                    translationsMap.put(translatedLanguage, translatedText);
                                                                    Log.i(TAG, "filterBaseMessages: currentLang " + sp.getString("currentLang", "en"));

                                                                    if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                                        if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                                            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                                ((TextMessage) baseMessage).setText(translatedText);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                                    }
                                                                }
                                                            }

                                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                                Action action = ((Action) baseMessage);
                                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) || action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                                    tempList.add(baseMessage);
                                                                } else {
                                                                    tempList.add(baseMessage);
                                                                }
                                                            } else {
                                                                tempList.add(baseMessage);
                                                            }
                                                        }
                                                    } else {
                                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                            Action action = ((Action) baseMessage);
                                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                                tempList.add(baseMessage);
                                                            } else {
                                                                tempList.add(baseMessage);
                                                            }
                                                        } else {
                                                            tempList.add(baseMessage);
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                        } else if (metadata != null || baseMessage.getReadAt() != 0) {
                                            /*This code is use to check if the text message is reply message from ios or normal message
                                             * if it is replied msg and send from ios then we change metadata to display the text message
                                             * code is written by rahul maske
                                             */

                                                /*Handler handler=new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {*/
                                            try {
                                                if (metadata.has("@injected")) {
                                                    JSONObject injectedObject = null;
                                                    injectedObject = metadata.getJSONObject("@injected");
                                                    if (injectedObject.has("extensions")) {
                                                        JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                                        if (extensionsObject.has("message-translation")) {
                                                            JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                            JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                            HashMap<String, String> translationsMap = new HashMap<String, String>();
                                                            for (int i = 0; i < translations.length(); i++) {
                                                                JSONObject translation = translations.getJSONObject(i);
                                                                String translatedText = translation.getString("message_translated");
                                                                String translatedLanguage = translation.getString("language_translated");
                                                                translationsMap.put(translatedLanguage, translatedText);
                                                                Log.i(TAG, "filterBaseMessages: currentLang " + sp.getString("currentLang", "en"));

                                                                if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                                    if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                                        if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                            ((TextMessage) baseMessage).setText(translatedText);
                                                                        }
                                                                    }
                                                                } else {
                                                                    ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                                }
                                                            }
                                                        }

                                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                            Action action = ((Action) baseMessage);
                                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) || action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                                tempList.add(baseMessage);
                                                            } else {
                                                                tempList.add(baseMessage);
                                                            }
                                                        } else {

                                                            tempList.add(baseMessage);
                                                        }
                                                    }
                                                } else {
                                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                        Action action = ((Action) baseMessage);
                                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                            tempList.add(baseMessage);
                                                        } else {
                                                            tempList.add(baseMessage);
                                                        }
                                                    } else {
                                                        tempList.add(baseMessage);
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                                   /* }
                                                },1000);*/

                                            /*else {
                                                try {
                                                    if (metadata.has("@injected")) {
                                                        JSONObject injectedObject = null;
                                                        injectedObject = metadata.getJSONObject("@injected");
                                                        if (injectedObject.has("extensions")) {
                                                            JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                                            if (extensionsObject.has("message-translation")) {
                                                                JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                                JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                                HashMap<String, String> translationsMap = new HashMap<String, String>();
                                                                for (int i = 0; i < translations.length(); i++) {
                                                                    JSONObject translation = translations.getJSONObject(i);
                                                                    String translatedText = translation.getString("message_translated");
                                                                    String translatedLanguage = translation.getString("language_translated");
                                                                    translationsMap.put(translatedLanguage, translatedText);
                                                                    Log.i(TAG, "filterBaseMessages: currentLang " + sp.getString("currentLang", "en"));

                                                                    if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                                        if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                                            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                                ((TextMessage) baseMessage).setText(translatedText);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                                    }

                                                                }
                                                            }

                                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                                Action action = ((Action) baseMessage);
                                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) || action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                                    tempList.add(baseMessage);
                                                                } else {
                                                                    tempList.add(baseMessage);
                                                                }
                                                            } else {

                                                                tempList.add(baseMessage);
                                                            }
                                                        }
                                                    } else {
                                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                            Action action = ((Action) baseMessage);
                                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                                tempList.add(baseMessage);
                                                            } else {
                                                                tempList.add(baseMessage);
                                                            }
                                                        } else {
                                                            tempList.add(baseMessage);
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }*/

                                        } else {
                                            Log.e(TAG, "filterBaseMessages: 1.3");
                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                Action action = ((Action) baseMessage);
                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                    tempList.add(baseMessage);
                                                } else {
                                                    tempList.add(baseMessage);

                                                }
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        }

                                    } else if (baseMessage.getType().equals("image")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                if (metadata.getString("deleted_one_to_one").equals("0")) {
                                                    baseMessage.setDeletedAt(12345);
                                                }
                                            }
                                        }
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {

                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else if (baseMessage.getType().equals("file")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                if (metadata.getString("deleted_one_to_one").equals("0")) {
                                                    baseMessage.setDeletedAt(12345);
                                                }
                                            }
                                        }
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {

                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else if (baseMessage.getType().equals("audio")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                if (metadata.getString("deleted_one_to_one").equals("0")) {
                                                    baseMessage.setDeletedAt(12345);
                                                }
                                            }
                                        }
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else if (baseMessage.getType().equals("video")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                if (metadata.getString("deleted_one_to_one").equals("0")) {
                                                    baseMessage.setDeletedAt(12345);
                                                }
                                            }
                                        }
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else {
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.i(TAG, "filterBaseMessages: message id " + baseMessage.getId());
                        }
                    } else if (baseMessage.getCategory().equals("call")) {
                        int team_log;
                        try {
                            // team_log = metadata.getInt("team_logs_id");
                            team_log = metadata.getInt("team_logs_id");
                            if (team_log == 0) {
                                if (baseMessage.getType().equals("audio")) {
                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                        Action action = ((Action) baseMessage);
                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                        } else {
                                            Log.i(TAG, "filterBaseMessages: friend audio call log1");
                                            //Log.i(TAG, "filterBaseMessages: friend audio call log3 "+baseMessage);

                                            tempList.add(baseMessage);
                                        }
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else if (baseMessage.getType().equals("video")) {
                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                        Action action = ((Action) baseMessage);
                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                        } else {
                                            Log.i(TAG, "filterBaseMessages: friend video call log1");
                                          /*  Log.i(TAG, "filterBaseMessages: call BaseMessage" + ((Action) baseMessage).getAction());
                                            Log.i(TAG, "filterBaseMessages: call BaseMessage" + baseMessage.getSentAt());
                                            Log.i(TAG, "filterBaseMessages: call BaseMessage" + baseMessage.getDeletedAt());
                                            Log.i(TAG, "filterBaseMessages: call BaseMessage" + ((Action) baseMessage).getMessage());*/
                                            tempList.add(baseMessage);
                                        }
                                    } else {
                                        /*Log.i(TAG, "filterBaseMessages: call BaseMessage" + ((Action) baseMessage).getAction());
                                        Log.i(TAG, "filterBaseMessages: call BaseMessage" + baseMessage.getSentAt());
                                        Log.i(TAG, "filterBaseMessages: call BaseMessage" + baseMessage.getDeletedAt());
                                        Log.i(TAG, "filterBaseMessages: call BaseMessage" + ((Action) baseMessage).getMessage());*/
                                        Log.i(TAG, "filterBaseMessages: friend video call log2");
                                        tempList.add(baseMessage);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        Log.e(TAG, "filterBaseMessages: " + baseMessage.toString());
                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                            Action action = ((Action) baseMessage);
                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                            } else {
                                tempList.add(baseMessage);
                            }
                        } else {
                            tempList.add(baseMessage);
                        }
                    }
                    break;

                // group chat
                case "2":
                    Log.i(TAG, "filterBaseMessages: case Groups");
                    Log.e("tab 2 group ", baseMessage.toString());
                    if (baseMessage.getCategory().equals("custom")) {
                        if (baseMessage.getType().equals("extension_whiteboard")) {
                            String s = null;
                            try {
                                if (baseMessage.getDeletedAt() != 0) {
                                    tempList.add(baseMessage);
                                } else {
                                    s = baseMessage.getMetadata().getString("whiteboard_URL_group");
                                    if (!s.isEmpty()) {
                                        Log.i(TAG, "filterBaseMessages: whiteboard custom url " + s);
                                        Log.e(TAG, "filterBaseMessages: 2.1" + baseMessage.toString());
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (baseMessage.getCategory().equals("message")) {
                        if (baseMessage.getType().equals("text")) {
                            /*This code is use to check if the text message is reply message from ios or normal message
                             * if it is replied msg and send from ios then we change metadata to display the text message
                             * code is written by rahul maske */
                            if (baseMessage.getDeletedAt() != 0) {
                                Log.i(TAG, "filterBaseMessages block check: deleted message..1");
                                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else if (metadata != null && baseMessage.getEditedAt() != 0) {
                                Log.i(TAG, "filterBaseMessages block check: edited message..");
                                Log.i(TAG, "filterBaseMessages: edit and deleted message message");
                                /*Here we are marking message as deleted so that user can check that it is deleted */
                                try {
                                    Log.i(TAG, "filterBaseMessages: edited at block 0" + ((TextMessage) baseMessage).getText());
                                    if (metadata.has("deleted_one_to_one")) {
                                        if (metadata.getString("deleted_one_to_one").equals("0")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                    } else {
                                        /*this is normal edited message functionality */
                                        Log.i(TAG, "filterBaseMessages: edited at block 1");
                                        long id = baseMessage.getId();
                                        String textMessage = ((TextMessage) baseMessage).getText();
                                        String currentLang = sp.getString("currentLang", null);
                                        JSONObject body = new JSONObject();
                                        JSONArray languages = new JSONArray();
                                        languages.put(currentLang);
                                        body.put("msgId", id);
                                        body.put("languages", languages);
                                        body.put("text", textMessage);
                                        Log.i(TAG, "filterBaseMessages: edited at block 2");
                                        // Do something after 5s = 5000ms
                                        CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                new CometChat.CallbackListener<JSONObject>() {
                                                    @Override
                                                    public void onSuccess(JSONObject jsonObject) {
                                                        try {
                                                            Log.i(TAG, "filterBaseMessages: edited at block 3");
                                                            JSONObject meta = baseMessage.getMetadata();
                                                            meta.accumulate("values", jsonObject);
                                                            String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");

                                                            // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                            //baseMessage.setMetadata(meta);

                                                            ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                            baseMessage.setMetadata(meta);
                                                            messageAdapter.setUpdatedMessage(baseMessage);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(CometChatException e) {
                                                        // Some error occured
                                                        Log.i(TAG, "onError: " + e.getMessage());
                                                        Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                    }
                                                });
                                    }

                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                        Action action = ((Action) baseMessage);
                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            tempList.add(baseMessage);
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else {
                                        Log.i(TAG, "filterBaseMessages: edited at block 5");
                                        tempList.add(baseMessage);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (metadata != null && metadata.has("type")) {
                                Log.i(TAG, "filterBaseMessages block check: replied message..");
                                if (metadata.has("type")) {
                                    try {
                                        if (metadata.has("message")) {
                                            Log.i(TAG, "filterBaseMessages: " + metadata.getString("message"));
                                            JSONObject body = new JSONObject();
                                            JSONArray languages = new JSONArray();
                                            languages.put(sp.getString("currentLang", currentLang));
                                            body.put("languages", languages);
                                            body.put("text", metadata.getString("message"));
                                            Log.i(TAG, "filterBaseMessages: edited at block 2");
                                            // Do something after 5s = 5000ms
                                            CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                    new CometChat.CallbackListener<JSONObject>() {
                                                        @Override
                                                        public void onSuccess(JSONObject jsonObject) {
                                                            try {
                                                                // Log.i(TAG, "filterBaseMessages: edited at block 3 translated response for replied message "+jsonObject);
                                                                JSONObject meta = baseMessage.getMetadata();

                                                                //meta.accumulate("values", jsonObject);
                                                                String messageTranslatedString = null;

                                                                messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");

                                                                Log.i(TAG, "filterBaseMessages: onSuccess at block 3 messageTranslatedString " + messageTranslatedString);
                                                                metadata.remove("message");
                                                                metadata.put("message", messageTranslatedString);
                                                                messageAdapter.setUpdatedMessage(baseMessage);
                                                                Log.i(TAG, "filterBaseMessages onSuccess: metadata for replied translate " + metadata);
                                                                // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                                //baseMessage.setMetadata(meta);
                                                                // ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                                //baseMessage.setMetadata(meta);
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }

                                                        }

                                                        @Override
                                                        public void onError(CometChatException e) {
                                                            // Some error occured
                                                            Log.i(TAG, "onError: " + e.getMessage());
                                                            Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                        }
                                                    });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        JSONObject injectedObject = metadata.getJSONObject("@injected");
                                        if (injectedObject.has("extensions")) {
                                            JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                            if (extensionsObject.has("message-translation")) {
                                                JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                HashMap<String, String> translationsMap = new HashMap<String, String>();

                                                for (int i = 0; i < translations.length(); i++) {
                                                    JSONObject translation = translations.getJSONObject(i);
                                                    String translatedText = translation.getString("message_translated");
                                                    String translatedLanguage = translation.getString("language_translated");
                                                    translationsMap.put(translatedLanguage, translatedText);
                                                                /*if (translatedLanguage.equals(currentLang)) {
                                                                    if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                        ((TextMessage) baseMessage).setText(translatedText);
                                                                        Log.i(TAG, "filterBaseMessages: case 3 translated text " + translatedText);
                                                                    }
                                                                }*/
                                                    if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                        if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                ((TextMessage) baseMessage).setText(translatedText);
                                                            }
                                                        }
                                                    } else {
                                                        ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                    }
                                                }
                                            }
                                        }

                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                tempList.add(baseMessage);
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            Log.i(TAG, "filterBaseMessages: edited at block 5");
                                            tempList.add(baseMessage);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            } else {
                                //normal message
                                Log.i(TAG, "filterBaseMessages block check: normal message..");
                                try {
                                    if (metadata.has("@injected")) {
                                        JSONObject injectedObject = null;
                                        injectedObject = metadata.getJSONObject("@injected");
                                        if (injectedObject.has("extensions")) {
                                            JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                            if (extensionsObject.has("message-translation")) {
                                                JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                HashMap<String, String> translationsMap = new HashMap<String, String>();
                                                for (int i = 0; i < translations.length(); i++) {
                                                    JSONObject translation = translations.getJSONObject(i);
                                                    String translatedText = translation.getString("message_translated");
                                                    String translatedLanguage = translation.getString("language_translated");
                                                    translationsMap.put(translatedLanguage, translatedText);
                                                    Log.i(TAG, "filterBaseMessages: currentLang " + sp.getString("currentLang", "en"));

                                                    if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                        if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                ((TextMessage) baseMessage).setText(translatedText);
                                                            }
                                                        }
                                                    } else {
                                                        ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                    }
                                                }
                                            }

                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                Action action = ((Action) baseMessage);
                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) || action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                    tempList.add(baseMessage);
                                                } else {
                                                    tempList.add(baseMessage);
                                                }
                                            } else {

                                                tempList.add(baseMessage);
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (baseMessage.getType().equals("image")) {
                            if (baseMessage.getEditedAt() != 0) {
                                if (metadata.has("deleted_one_to_one")) {
                                    try {
                                        if (metadata.getString("deleted_one_to_one").equals("0")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                Action action = ((Action) baseMessage);
                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else {
                                tempList.add(baseMessage);
                            }

                        } else if (baseMessage.getType().equals("file")) {
                            if (baseMessage.getEditedAt() != 0) {
                                if (metadata.has("deleted_one_to_one")) {
                                    try {
                                        if (metadata.getString("deleted_one_to_one").equals("0")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                Action action = ((Action) baseMessage);
                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else {
                                tempList.add(baseMessage);
                            }
                        } else if (baseMessage.getType().equals("audio")) {
                            if (baseMessage.getEditedAt() != 0) {
                                if (metadata.has("deleted_one_to_one")) {
                                    try {
                                        if (metadata.getString("deleted_one_to_one").equals("0")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                Action action = ((Action) baseMessage);
                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else {
                                tempList.add(baseMessage);
                            }

                        } else if (baseMessage.getType().equals("video")) {
                            if (baseMessage.getEditedAt() != 0) {
                                if (metadata.has("deleted_one_to_one")) {
                                    try {
                                        if (metadata.getString("deleted_one_to_one").equals("0")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                Action action = ((Action) baseMessage);
                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else {
                                tempList.add(baseMessage);
                            }
                        } else {
                            if (baseMessage.getEditedAt() != 0) {
                                if (metadata.has("deleted_one_to_one")) {
                                    try {
                                        if (metadata.getString("deleted_one_to_one").equals("0")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                Action action = ((Action) baseMessage);
                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else {
                                tempList.add(baseMessage);
                            }

                        }

                    } else {
                        Log.e(TAG, "filterBaseMessages: 2.3" + baseMessage.toString());
                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                            Action action = ((Action) baseMessage);

                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                            } else {
                                tempList.add(baseMessage);
                            }
                        } else {
                            tempList.add(baseMessage);
                        }
                    }
                    break;
                // myteam chat
                case "3":
                    Log.i(TAG, "filterBaseMessages: case my team");
                    Log.e("tab 3 myteam", baseMessage.toString());
                    if (baseMessage.getCategory().equals("custom")) {
                        String team_log;
                        try {
                            team_log = metadata.getString("team_logs_id");
                            String ids[] = team_log.split(Pattern.quote("_"));
                            Log.e("tabs case 3", String.valueOf(ids.length));

                            if (ids.length == 3 || ids.length == 4) {

                                String[] arrayTeamId = team_log.split("_-");
                                String part2 = arrayTeamId[1]; // 034556 //team id fetching

                                if (team_Ids.equals(part2)) {
                                    if (baseMessage.getType().equals("extension_whiteboard")) {
                                        String s = null;
                                        try {
                                            if (baseMessage.getMetadata().has("deleted_one_to_one")) {
                                                if (baseMessage.getMetadata().getString("deleted_one_to_one").equalsIgnoreCase("1")
                                                        || baseMessage.getMetadata().getString("deleted_one_to_one").equalsIgnoreCase("2")) {
                                                    baseMessage.setDeletedAt(12345);
                                                    tempList.add(baseMessage);
                                                }
                                            } else {
                                                s = baseMessage.getMetadata().getString("whiteboard_URL");
                                                if (!s.isEmpty()) {
                                                    Log.e(TAG, "filterBaseMessages:3.1 " + baseMessage.toString());
                                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                        Action action = ((Action) baseMessage);
                                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                        } else {
                                                            tempList.add(baseMessage);
                                                        }
                                                    } else {
                                                        tempList.add(baseMessage);
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (baseMessage.getCategory().equals("message")) {
                        String team_log;
                        try {
                            if (baseMessage.getDeletedAt() != 0) {
                                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }
                            }
                            team_log = metadata.getString("team_logs_id");
                            String underscoreSplittedArray[] = team_log.split(Pattern.quote("_"));
                            Log.e("tabs case 3", String.valueOf(underscoreSplittedArray.length));
                            if (underscoreSplittedArray.length == 3 || underscoreSplittedArray.length == 4) {
                                String[] underscoreDashSplittedArray = team_log.split("_-");
                                String part2 = underscoreDashSplittedArray[1];
                                if (team_Ids.equals(part2)) {
                                    if (baseMessage.getType().equals("text")) {
                                        if (metadata != null && baseMessage.getEditedAt() != 0) {
                                            /*Here we are marking message as deleted so that user can check that it is deleted */
                                            try {
                                                Log.i(TAG, "filterBaseMessages: edited at block 0" + ((TextMessage) baseMessage).getText());
                                                if (metadata.has("deleted_one_to_one")) {
                                                    if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                        baseMessage.setDeletedAt(12345);
                                                    }
                                                } else {
                                                    Log.i(TAG, "filterBaseMessages: edited at block 1");

                                                    long id = baseMessage.getId();
                                                    String textMessage = ((TextMessage) baseMessage).getText();
                                                    String currentLang = sp.getString("currentLang", null);
                                                    JSONObject body = new JSONObject();
                                                    JSONArray languages = new JSONArray();
                                                    languages.put(currentLang);
                                                    body.put("msgId", id);
                                                    body.put("languages", languages);
                                                    body.put("text", textMessage);
                                                    Log.i(TAG, "filterBaseMessages: edited at block 2");
                                                    // Do something after 5s = 5000ms
                                                    CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                            new CometChat.CallbackListener<JSONObject>() {
                                                                @Override
                                                                public void onSuccess(JSONObject jsonObject) {
                                                                    try {
                                                                        Log.i(TAG, "filterBaseMessages: edited at block 3");
                                                                        JSONObject meta = baseMessage.getMetadata();
                                                                        meta.accumulate("values", jsonObject);
                                                                        String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");

                                                                        // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                                        //baseMessage.setMetadata(meta);

                                                                        ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                                        baseMessage.setMetadata(meta);
                                                                        messageAdapter.setUpdatedMessage(baseMessage);
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onError(CometChatException e) {
                                                                    // Some error occured
                                                                    Log.i(TAG, "onError: " + e.getMessage());
                                                                    Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                                }
                                                            });
                                                }

                                                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                    Action action = ((Action) baseMessage);
                                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                        tempList.add(baseMessage);
                                                    } else {
                                                        tempList.add(baseMessage);
                                                    }
                                                } else {
                                                    Log.i(TAG, "filterBaseMessages: edited at block 5");
                                                    tempList.add(baseMessage);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else if (metadata != null && metadata.has("type")) {
                                            if (metadata.has("type")) {
                                                try {
                                                    if (metadata.has("message")) {
                                                        Log.i(TAG, "filterBaseMessages: " + metadata.getString("message"));
                                                        JSONObject body = new JSONObject();
                                                        JSONArray languages = new JSONArray();
                                                        languages.put(sp.getString("currentLang", currentLang));
                                                        body.put("languages", languages);
                                                        body.put("text", metadata.getString("message"));
                                                        Log.i(TAG, "filterBaseMessages: edited at block 2");
                                                        // Do something after 5s = 5000ms
                                                        CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                                new CometChat.CallbackListener<JSONObject>() {
                                                                    @Override
                                                                    public void onSuccess(JSONObject jsonObject) {
                                                                        try {
                                                                            // Log.i(TAG, "filterBaseMessages: edited at block 3 translated response for replied message "+jsonObject);
                                                                            JSONObject meta = baseMessage.getMetadata();

                                                                            //meta.accumulate("values", jsonObject);
                                                                            String messageTranslatedString = null;

                                                                            messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");

                                                                            Log.i(TAG, "filterBaseMessages: onSuccess at block 3 messageTranslatedString " + messageTranslatedString);
                                                                            metadata.remove("message");
                                                                            metadata.put("message", messageTranslatedString);
                                                                            messageAdapter.setUpdatedMessage(baseMessage);
                                                                            Log.i(TAG, "filterBaseMessages onSuccess: metadata for replied translate " + metadata);
                                                                            // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                                            //baseMessage.setMetadata(meta);
                                                                            // ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                                            //baseMessage.setMetadata(meta);
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }

                                                                    }

                                                                    @Override
                                                                    public void onError(CometChatException e) {
                                                                        // Some error occured
                                                                        Log.i(TAG, "onError: " + e.getMessage());
                                                                        Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                                    }
                                                                });
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    JSONObject injectedObject = metadata.getJSONObject("@injected");
                                                    if (injectedObject.has("extensions")) {
                                                        JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                                        if (extensionsObject.has("message-translation")) {
                                                            JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                            JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                            HashMap<String, String> translationsMap = new HashMap<String, String>();

                                                            for (int i = 0; i < translations.length(); i++) {
                                                                JSONObject translation = translations.getJSONObject(i);
                                                                String translatedText = translation.getString("message_translated");
                                                                String translatedLanguage = translation.getString("language_translated");
                                                                translationsMap.put(translatedLanguage, translatedText);
                                                                /*if (translatedLanguage.equals(currentLang)) {
                                                                    if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                        ((TextMessage) baseMessage).setText(translatedText);
                                                                        Log.i(TAG, "filterBaseMessages: case 3 translated text " + translatedText);
                                                                    }
                                                                }*/
                                                                if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                                    if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                                        if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                            ((TextMessage) baseMessage).setText(translatedText);
                                                                        }
                                                                    }
                                                                } else {
                                                                    ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                                }
                                                            }
                                                        }
                                                    }

                                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                        Action action = ((Action) baseMessage);
                                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                            tempList.add(baseMessage);
                                                        } else {
                                                            tempList.add(baseMessage);
                                                        }
                                                    } else {
                                                        Log.i(TAG, "filterBaseMessages: edited at block 5");
                                                        tempList.add(baseMessage);
                                                    }

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                        } else {
                                            try {
                                                JSONObject injectedObject = metadata.getJSONObject("@injected");
                                                if (injectedObject.has("extensions")) {
                                                    JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                                    if (extensionsObject.has("message-translation")) {
                                                        JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                        JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                        HashMap<String, String> translationsMap = new HashMap<String, String>();

                                                        for (int i = 0; i < translations.length(); i++) {
                                                            JSONObject translation = translations.getJSONObject(i);
                                                            String translatedText = translation.getString("message_translated");
                                                            String translatedLanguage = translation.getString("language_translated");
                                                            translationsMap.put(translatedLanguage, translatedText);
                                                                /*if (translatedLanguage.equals(currentLang)) {
                                                                    if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                        ((TextMessage) baseMessage).setText(translatedText);
                                                                        Log.i(TAG, "filterBaseMessages: case 3 translated text " + translatedText);
                                                                    }
                                                                }*/
                                                            if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                                if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                                    if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                        ((TextMessage) baseMessage).setText(translatedText);
                                                                    }
                                                                }
                                                            } else {
                                                                ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                            }
                                                        }
                                                    }
                                                }

                                                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                    Action action = ((Action) baseMessage);
                                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                        //tempList.add(baseMessage);
                                                    } else {
                                                        tempList.add(baseMessage);
                                                    }
                                                } else {
                                                    Log.i(TAG, "filterBaseMessages: edited at block 5");
                                                    tempList.add(baseMessage);
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else if (baseMessage.getType().equals("image")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                try {
                                                    if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                        baseMessage.setDeletedAt(12345);
                                                        tempList.add(baseMessage);

                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }

                                    } else if (baseMessage.getType().equals("file")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                try {
                                                    if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                        baseMessage.setDeletedAt(12345);
                                                        tempList.add(baseMessage);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else if (baseMessage.getType().equals("audio")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                try {
                                                    if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                        baseMessage.setDeletedAt(12345);
                                                        tempList.add(baseMessage);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else if (baseMessage.getType().equals("video")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                try {
                                                    if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                        baseMessage.setDeletedAt(12345);
                                                        tempList.add(baseMessage);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                try {
                                                    if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                        baseMessage.setDeletedAt(12345);
                                                        tempList.add(baseMessage);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }

                                    }

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (baseMessage.getCategory().equals("call")) {
                        String team_log;
                        try {
                            team_log = metadata.getString("team_logs_id");
                            String ids[] = team_log.split(Pattern.quote("_"));
                            Log.e("tabs", String.valueOf(ids.length));

                            if (ids.length == 3 || ids.length == 4) {

                                String[] parts = team_log.split("_-");
                                String part2 = parts[1]; // 034556

                                if (team_Ids.equals(part2)) {

                                    if (baseMessage.getType().equals("audio")) {
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else if (baseMessage.getType().equals("video")) {
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(TAG, "filterBaseMessages: 3.3 else" + baseMessage.toString());
                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                            Action action = ((Action) baseMessage);
                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                            } else {
                                tempList.add(baseMessage);
                            }
                        } else {
                            tempList.add(baseMessage);
                        }
                    }
                    break;

                // join team
                case "4":
                    Log.i(TAG, "filterBaseMessages: case join team " + baseMessage.getCategory());
                    if (baseMessage.getCategory().equals("custom")) {
                        String team_log;
                        try {
                            team_log = metadata.getString("team_logs_id");
                            String ids[] = team_log.split(Pattern.quote("_"));
                            Log.e("tabs", String.valueOf(ids.length));
                            if (ids.length == 4 || ids.length == 3) {

                                String[] parts = team_log.split("_-");
                                String part2 = parts[1]; // 034556

                                if (team_Ids.equals(part2)) {
                                    Log.i(TAG, "filterBaseMessages: message for join team  " + baseMessage);
                                    if (baseMessage.getType().equals("extension_whiteboard")) {
                                        String s = null;
                                        try {
                                            if (baseMessage.getMetadata().has("deleted_one_to_one")) {
                                                if (baseMessage.getMetadata().getString("deleted_one_to_one").equalsIgnoreCase("2")
                                                        || baseMessage.getMetadata().getString("deleted_one_to_one").equalsIgnoreCase("1")) {
                                                    baseMessage.setDeletedAt(12345);
                                                    tempList.add(baseMessage);
                                                }
                                            } else {
                                                s = baseMessage.getMetadata().getString("whiteboard_URL");
                                                if (!s.isEmpty()) {
                                                    Log.e(TAG, "filterBaseMessages: 4.1" + baseMessage.toString());
                                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                        Action action = ((Action) baseMessage);
                                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                        } else {
                                                            tempList.add(baseMessage);
                                                        }
                                                    } else {
                                                        tempList.add(baseMessage);
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (baseMessage.getCategory().equals("message")) {
                        Log.i(TAG, "filterBaseMessages: category is Message");
                        String team_log;
                        try {
                            if (baseMessage.getDeletedAt() != 0) {
                                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }
                            }
                            team_log = metadata.getString("team_logs_id");
                            Log.i(TAG, "filterBaseMessages: team_log_ids " + team_log);
                            String ids[] = team_log.split(Pattern.quote("_"));
                            Log.e("Team tabs", "" + String.valueOf(ids.length));
                            Log.e("Team team_logs_id", team_log);
                            if (ids.length == 4 || ids.length == 3) {

                                String[] parts = team_log.split("_-");
                                String part2 = parts[1]; // 034556
                                Log.i("Teams parts2", " " + part2);
                                Log.i("Teams team_Ids", " " + team_Ids);

                                if (team_Ids.equals(part2)) {
                                    Log.i(TAG, "filterBaseMessages: message for join team  " + baseMessage);
                                    if (baseMessage.getType().equals("text")) {
                                        if (metadata != null) {
                                            /*This code is use to check if the text message is reply message from ios or normal message
                                             * if it is replied msg and send from ios then we change metadata to display the text message
                                             * code is written by rahul maske 27-03-2021*/
                                            /* if (metadata.has("type")) {
                                             */
                                            /*try {
                                                    JSONObject customMetadata = new JSONObject();
                                                    JSONObject reply = new JSONObject();
                                                    reply.put("name", baseMessage.getSender().getName());
                                                    reply.put("type", baseMessage.getType());
                                                    reply.put("avatar", baseMessage.getSender().getAvatar());
                                                    reply.put("message", baseMessage.getMetadata().getString("message"));
                                                    customMetadata.put("reply", reply);
                                                    customMetadata.put("readByMeAt", baseMessage.getReadAt());
                                                    customMetadata.put("deliveredToMeAt", baseMessage.getDeliveredToMeAt());
                                                    customMetadata.put("deletedAt", baseMessage.getDeletedAt());
                                                    customMetadata.put("editedAt", baseMessage.getEditedAt());
                                                    customMetadata.put("deletedBy", baseMessage.getDeletedBy());
                                                    customMetadata.put("editedBy", baseMessage.getEditedBy());
                                                    customMetadata.put("updatedAt", baseMessage.getUpdatedAt());
                                                    //readByMeAt=0, deliveredToMeAt=0, deletedAt=0, editedAt=0, deletedBy='null',
                                                    // editedBy='null', updatedAt=1616823540
                                                    baseMessage.setMetadata(customMetadata);
                                                    tempList.add(baseMessage);
                                                    Log.i(TAG, "filterBaseMessages: iosReply name" + baseMessage.getSender().getName());
                                                    Log.i(TAG, "filterBaseMessages: iosReply type " + baseMessage.getType());
                                                    Log.i(TAG, "filterBaseMessages: iosReply avtar " + baseMessage.getSender().getAvatar());
                                                    Log.i(TAG, "filterBaseMessages: iosReply message " + baseMessage.getMetadata().getString("message"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }*/
                                            /*
                                                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                    Action action = ((Action) baseMessage);
                                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                        tempList.add(baseMessage);
                                                    } else {
                                                        tempList.add(baseMessage);
                                                    }
                                                } else {
                                                    Log.i(TAG, "filterBaseMessages: edited at block 5");
                                                    tempList.add(baseMessage);
                                                }
                                            }*/

                                            if (metadata != null && baseMessage.getEditedAt() != 0) {
                                                /*Here we are marking message as deleted so that user can check that it is deleted */
                                                try {
                                                    Log.i(TAG, "filterBaseMessages: edited at block 0" + ((TextMessage) baseMessage).getText());
                                                    if (metadata.has("deleted_one_to_one")) {
                                                        if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                            baseMessage.setDeletedAt(12345);
                                                        }
                                                    } else {
                                                        Log.i(TAG, "filterBaseMessages: edited at block 1");

                                                        long id = baseMessage.getId();
                                                        String textMessage = ((TextMessage) baseMessage).getText();
                                                        String currentLang = sp.getString("currentLang", null);
                                                        JSONObject body = new JSONObject();
                                                        JSONArray languages = new JSONArray();
                                                        languages.put(currentLang);
                                                        body.put("languages", languages);
                                                        body.put("text", textMessage);
                                                        Log.i(TAG, "filterBaseMessages: edited at block 2");
                                                        // Do something after 5s = 5000ms
                                                        CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                                new CometChat.CallbackListener<JSONObject>() {
                                                                    @Override
                                                                    public void onSuccess(JSONObject jsonObject) {
                                                                        try {
                                                                            Log.i(TAG, "filterBaseMessages: edited at block 3");
                                                                            JSONObject meta = baseMessage.getMetadata();
                                                                            meta.accumulate("values", jsonObject);
                                                                            String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");

                                                                            // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                                            //baseMessage.setMetadata(meta);

                                                                            ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                                            baseMessage.setMetadata(meta);
                                                                            messageAdapter.setUpdatedMessage(baseMessage);
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onError(CometChatException e) {
                                                                        // Some error occured
                                                                        Log.i(TAG, "onError: " + e.getMessage());
                                                                        Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                                    }
                                                                });

                                                    }
                                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                        Action action = ((Action) baseMessage);
                                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                            tempList.add(baseMessage);
                                                        } else {
                                                            tempList.add(baseMessage);
                                                        }
                                                    } else {
                                                        Log.i(TAG, "filterBaseMessages: edited at block 5");
                                                        tempList.add(baseMessage);
                                                    }

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (metadata != null && metadata.has("type")) {
                                                if (metadata.has("type")) {
                                                    try {
                                                        if (metadata.has("message")) {
                                                            Log.i(TAG, "filterBaseMessages: " + metadata.getString("message"));
                                                            JSONObject body = new JSONObject();
                                                            JSONArray languages = new JSONArray();
                                                            languages.put(sp.getString("currentLang", currentLang));
                                                            body.put("languages", languages);
                                                            body.put("text", metadata.getString("message"));
                                                            Log.i(TAG, "filterBaseMessages: edited at block 2");
                                                            // Do something after 5s = 5000ms
                                                            CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                                    new CometChat.CallbackListener<JSONObject>() {
                                                                        @Override
                                                                        public void onSuccess(JSONObject jsonObject) {
                                                                            try {
                                                                                // Log.i(TAG, "filterBaseMessages: edited at block 3 translated response for replied message "+jsonObject);
                                                                                JSONObject meta = baseMessage.getMetadata();

                                                                                //meta.accumulate("values", jsonObject);
                                                                                String messageTranslatedString = null;

                                                                                messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");

                                                                                Log.i(TAG, "filterBaseMessages: onSuccess at block 3 messageTranslatedString " + messageTranslatedString);
                                                                                metadata.remove("message");
                                                                                metadata.put("message", messageTranslatedString);
                                                                                messageAdapter.setUpdatedMessage(baseMessage);
                                                                                Log.i(TAG, "filterBaseMessages onSuccess: metadata for replied translate " + metadata);
                                                                                // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                                                //baseMessage.setMetadata(meta);
                                                                                // ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                                                //baseMessage.setMetadata(meta);

                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }

                                                                        }

                                                                        @Override
                                                                        public void onError(CometChatException e) {
                                                                            // Some error occured
                                                                            Log.i(TAG, "onError: " + e.getMessage());
                                                                            Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                                        }
                                                                    });
                                                        }

                                                        try {
                                                            JSONObject injectedObject = metadata.getJSONObject("@injected");
                                                            if (injectedObject.has("extensions")) {
                                                                JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                                                if (extensionsObject.has("message-translation")) {
                                                                    JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                                    JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                                    HashMap<String, String> translationsMap = new HashMap<String, String>();

                                                                    for (int i = 0; i < translations.length(); i++) {
                                                                        JSONObject translation = translations.getJSONObject(i);
                                                                        String translatedText = translation.getString("message_translated");
                                                                        String translatedLanguage = translation.getString("language_translated");
                                                                        translationsMap.put(translatedLanguage, translatedText);
                                                                /*if (translatedLanguage.equals(currentLang)) {
                                                                    if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                        ((TextMessage) baseMessage).setText(translatedText);
                                                                        Log.i(TAG, "filterBaseMessages: case 3 translated text " + translatedText);
                                                                    }
                                                                }*/
                                                                        if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                                            if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                                                if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                                    ((TextMessage) baseMessage).setText(translatedText);
                                                                                }
                                                                            }
                                                                        } else {
                                                                            ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                                Action action = ((Action) baseMessage);
                                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                                    tempList.add(baseMessage);
                                                                } else {
                                                                    tempList.add(baseMessage);
                                                                }
                                                            } else {
                                                                Log.i(TAG, "filterBaseMessages: edited at block 5");
                                                                tempList.add(baseMessage);
                                                            }

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            } else {

                                                Log.i(TAG, "filterBaseMessages: normal message...");
                                                try {
                                                    JSONObject injectedObject = metadata.getJSONObject("@injected");
                                                    if (injectedObject.has("extensions")) {
                                                        JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                                        if (extensionsObject.has("message-translation")) {
                                                            JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                            JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                            HashMap<String, String> translationsMap = new HashMap<String, String>();

                                                            for (int i = 0; i < translations.length(); i++) {
                                                                JSONObject translation = translations.getJSONObject(i);
                                                                String translatedText = translation.getString("message_translated");
                                                                String translatedLanguage = translation.getString("language_translated");
                                                                translationsMap.put(translatedLanguage, translatedText);
                                                                /*if (translatedLanguage.equals(currentLang)) {
                                                                    if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                        ((TextMessage) baseMessage).setText(translatedText);
                                                                        Log.i(TAG, "filterBaseMessages: case 4 translated text " + translatedText);
                                                                    }
                                                                }*/
                                                                if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                                    if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                                        if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                            ((TextMessage) baseMessage).setText(translatedText);
                                                                        }
                                                                    }
                                                                } else {
                                                                    ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                                }
                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                Log.e(TAG, "filterBaseMessages: 4.2" + baseMessage.toString());
                                                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                    Action action = ((Action) baseMessage);
                                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                    } else {
                                                        tempList.add(baseMessage);
                                                    }
                                                } else {
                                                    tempList.add(baseMessage);
                                                }
                                            }
                                           /* Log.e(TAG, "filterBaseMessages: 4.2" + baseMessage.toString());
                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                Action action = ((Action) baseMessage);
                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                } else {
                                                    tempList.add(baseMessage);
                                                }
                                            } else {
                                                tempList.add(baseMessage);
                                            }*/
                                        } else {
                                            Log.e(TAG, "filterBaseMessages: 3.2" + baseMessage.toString());
                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                Action action = ((Action) baseMessage);
                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                } else {
                                                    tempList.add(baseMessage);
                                                }
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        }
                                    } else if (baseMessage.getType().equals("image")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                    baseMessage.setDeletedAt(12345);
                                                }
                                                tempList.add(baseMessage);
                                            }
                                        } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }

                                    } else if (baseMessage.getType().equals("file")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                    baseMessage.setDeletedAt(12345);
                                                }
                                                tempList.add(baseMessage);
                                            }
                                        } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else if (baseMessage.getType().equals("audio")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                    baseMessage.setDeletedAt(12345);
                                                }
                                                tempList.add(baseMessage);
                                            }
                                        } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else if (baseMessage.getType().equals("video")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                    baseMessage.setDeletedAt(12345);
                                                }
                                                tempList.add(baseMessage);
                                            }
                                        } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else {
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (baseMessage.getCategory().equals("call")) {
                        String team_log;
                        try {
                            team_log = metadata.getString("team_logs_id");
                            String ids[] = team_log.split(Pattern.quote("_"));
                            Log.e("tabs", String.valueOf(ids.length));
                            if (ids.length == 4 || ids.length == 3) {

                                String[] parts = team_log.split("_-");
                                String part2 = parts[1]; // 034556

                                if (team_Ids.equals(part2)) {
                                    if (baseMessage.getType().equals("audio")) {
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else if (baseMessage.getType().equals("video")) {
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    }

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.e(TAG, "filterBaseMessages: 4.3" + baseMessage.toString());
                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                            Action action = ((Action) baseMessage);
                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                            } else {
                                tempList.add(baseMessage);
                            }
                        } else {
                            tempList.add(baseMessage);
                        }
                    }
                    break;
            }
        }
        tempList.size();
        return tempList;
    }


    public void stopHideShimmer() {
        messageShimmer.stopShimmer();
        messageShimmer.setVisibility(GONE);
    }

    public void getSmartReplyList(BaseMessage baseMessage) {
        Log.i(TAG, "getSmartReplyList: " + baseMessage);
        HashMap<String, JSONObject> extensionList = Extensions.extensionCheck(baseMessage);
        if (extensionList != null && extensionList.containsKey("smartReply")) {
            Log.i(TAG, "getSmartReplyList: extensionList contains key");
            rvSmartReply.setVisibility(View.VISIBLE);
            JSONObject replyObject = extensionList.get("smartReply");
            List<String> replyList = new ArrayList<>();
            try {
                replyList.add(replyObject.getString("reply_positive"));
                replyList.add(replyObject.getString("reply_neutral"));
                replyList.add(replyObject.getString("reply_negative"));
            } catch (Exception e) {
                Log.e(TAG, "onSuccess: " + e.getMessage());
            }
            setSmartReplyAdapter(replyList);
        } else {
            Log.i(TAG, "getSmartReplyList: not contains key..");
            rvSmartReply.setVisibility(GONE);
        }
    }

    public void setSmartReplyAdapter(List<String> replyList) {
        Log.i(TAG, "setSmartReplyAdapter: ");
        rvSmartReply.setSmartReplyList(replyList);
        rvSmartReply.setVisibility(GONE);
        scrollToBottom();
    }

    /**
     * This method is used to initialize the message adapter if it is empty else it helps
     * to update the message list in adapter.
     *
     * @param messageList is a list of messages which will be added.
     */
    public void initMessageAdapter(List<BaseMessage> messageList) {
        if (messageAdapter == null) {
            Log.i(TAG, "initMessageAdapter: equal to null ");
            messageAdapter = new MessageAdapter(getActivity(), messageList, CometChatMessageScreen.class.getName());
            rvChatListView.setAdapter(messageAdapter);
            stickyHeaderDecoration = new StickyHeaderDecoration(messageAdapter);
            rvChatListView.addItemDecoration(stickyHeaderDecoration, 0);
            scrollToBottom();
            messageAdapter.notifyDataSetChanged();
        } else {
            Log.i(TAG, "initMessageAdapter: not equal null ");
            messageAdapter.updateList(messageList);
            messageAdapter.notifyDataSetChanged();
            scrollToBottom();
        }
        if (!isBlockedByMe && rvSmartReply.getAdapter().getItemCount() == 0 && rvSmartReply.getVisibility() == GONE) {
            BaseMessage lastMessage = messageAdapter.getLastMessage();
            checkSmartReply(lastMessage);
        }
    }

    /**
     * This method is used to send typing indicator to other users and groups.
     *
     * @param isEnd is boolean which is used to differentiate between startTyping & endTyping Indicators.
     * @see CometChat#startTyping(TypingIndicator)
     * @see CometChat#endTyping(TypingIndicator)
     */
    public void sendTypingIndicator(boolean isEnd) {
        if (isEnd) {
            if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                CometChat.endTyping(new TypingIndicator(NextGroupOrUserId, CometChatConstants.RECEIVER_TYPE_USER));
            } else {
                CometChat.endTyping(new TypingIndicator(NextGroupOrUserId, CometChatConstants.RECEIVER_TYPE_GROUP));
            }
        } else {
            if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                CometChat.startTyping(new TypingIndicator(NextGroupOrUserId, CometChatConstants.RECEIVER_TYPE_USER));
            } else {
                CometChat.startTyping(new TypingIndicator(NextGroupOrUserId, CometChatConstants.RECEIVER_TYPE_GROUP));
            }
        }
    }

    public void endTypingTimer() {
        if (typingTimer != null) {
            typingTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendTypingIndicator(true);
                }
            }, 2000);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");

        switch (requestCode) {
            case StringContract.RequestCode.AUDIO:
                if (data != null) {
                    File file = MediaUtils.getRealPath(getContext(), data.getData());
                    ContentResolver cr = getActivity().getContentResolver();
                    sendMediaMessage(file, CometChatConstants.MESSAGE_TYPE_AUDIO);
                }
                break;
            case StringContract.RequestCode.GALLERY:
                if (data != null) {

                    File file = MediaUtils.getRealPath(getContext(), data.getData());
                    Log.e(TAG, "onActivityResult: image data content " + data.getData());
                    ContentResolver cr = getActivity().getContentResolver();
                    String mimeType = cr.getType(data.getData());
                    if (mimeType != null && mimeType.contains("image")) {
                        if (file.exists())
                            sendMediaMessage(file, CometChatConstants.MESSAGE_TYPE_IMAGE);
                        else
                            Snackbar.make(rvChatListView, R.string.file_not_exist, Snackbar.LENGTH_LONG).show();
                    } else {
                        if (file.exists())
                            sendMediaMessage(file, CometChatConstants.MESSAGE_TYPE_VIDEO);
                        else
                            Snackbar.make(rvChatListView, R.string.file_not_exist, Snackbar.LENGTH_LONG).show();
                    }
                }

                break;
            case StringContract.RequestCode.CAMERA:
                File file;
                if (Build.VERSION.SDK_INT >= 29) {
                    file = MediaUtils.getRealPath(getContext(), MediaUtils.uri);
                } else {
                    file = new File(MediaUtils.pictureImagePath);
                }
                if (file.exists())
                    sendMediaMessage(file, CometChatConstants.MESSAGE_TYPE_IMAGE);
                else
                    Snackbar.make(rvChatListView, R.string.file_not_exist, Snackbar.LENGTH_LONG).show();

                break;
            case StringContract.RequestCode.FILE:
                if (data != null)
                    sendMediaMessage(MediaUtils.getRealPath(getActivity(), data.getData()), CometChatConstants.MESSAGE_TYPE_FILE);
                break;
            case StringContract.RequestCode.BLOCK_USER:
                name = data.getStringExtra("");
                break;
        }

    }


    /**
     * This method is used to send media messages to other users and group.
     *
     * @param file     is an object of File which is been sent within the message.
     * @param filetype is a string which indicate a type of file been sent within the message.
     * @see CometChat#sendMediaMessage(MediaMessage, CometChat.CallbackListener)
     * @see MediaMessage
     */
    public void sendMediaMessage(File file, String filetype) {
        MediaMessage mediaMessage;

        if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
            mediaMessage = new MediaMessage(NextGroupOrUserId, file, filetype, CometChatConstants.RECEIVER_TYPE_USER);
        else
            mediaMessage = new MediaMessage(NextGroupOrUserId, file, filetype, CometChatConstants.RECEIVER_TYPE_GROUP);

        JSONObject jsonObject = new JSONObject();
        JSONArray languageArray = new JSONArray();
        languageArray.put("");

        switch (tabs) {
            case "1":
                try {
                    jsonObject.put("team_logs_id", 0);
                    jsonObject.put("message_translation_languages", languageArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "2":
                try {
                    jsonObject.put("message_translation_languages", languageArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "3":
                team_logs_id = receiverId + "_-" + teamId + "_-" + 3;
                try {
                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("message_translation_languages", languageArray);
                    jsonObject.put("path", file.getAbsolutePath());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "4":
                team_logs_id = receiverId + "_-" + teamId + "_-" + 4;
                try {
                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("message_translation_languages", languageArray);
                    jsonObject.put("path", file.getAbsolutePath());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }
        mediaMessage.setMetadata(jsonObject);

        CometChat.sendMediaMessage(mediaMessage, new CometChat.CallbackListener<MediaMessage>() {
            @Override
            public void onSuccess(MediaMessage mediaMessage) {
                Log.d(TAG, "sendMediaMessage onSuccess: " + mediaMessage.toString());
                if (messageAdapter != null) {
                    messageAdapter.addMessage(mediaMessage);
                    scrollToBottom();
                }
            }

            @Override
            public void onError(CometChatException e) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    /*creating new method for testing */

    /**
     * This method is used to get details of reciever.
     *
     * @see CometChat#getUser(String, CometChat.CallbackListener)
     */
    public void getUser() {
        CometChat.getUser(NextGroupOrUserId, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {

                if (getActivity() != null) {
                    if (user.isBlockedByMe()) {
                        isBlockedByMe = true;
                        rvSmartReply.setVisibility(GONE);
                        toolbar.setSelected(false);
                        blockedUserName.setText("You've blocked " + user.getName());
                        blockUserLayout.setVisibility(View.VISIBLE);
                    } else {
                        isBlockedByMe = false;
                        blockUserLayout.setVisibility(GONE);
                        avatarUrl = user.getAvatar();
                        if (user.getStatus().equals(CometChatConstants.USER_STATUS_ONLINE)) {
                            tvStatus.setTextColor(getActivity().getResources().getColor(R.color.textColorWhite));
                        }
                        status = user.getStatus().toString();
                        setAvatar();
                        tvStatus.setText(status);

                    }
                    name = user.getName();
                    tvName.setText(name);
                    Log.d(TAG, "onSuccess: " + user.toString());
                }

            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setAvatar() {
        if (avatarUrl != null && !avatarUrl.isEmpty())
            userAvatar.setAvatar(avatarUrl);
        else {
            userAvatar.setInitials(name);
        }
    }

    /**
     * This method is used to get Group Details.
     *
     * @see CometChat#getGroup(String, CometChat.CallbackListener)
     */
    public void getGroup() {

        CometChat.getGroup(NextGroupOrUserId, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                if (getActivity() != null) {
                    name = group.getName();
                    avatarUrl = group.getIcon();
                    loggedInUserScope = group.getScope();
                    groupOwnerId = group.getOwner();

                    tvName.setText(name);
                    if (context != null) {
                        userAvatar.setAvatar(getActivity().getResources().getDrawable(R.drawable.ic_account), avatarUrl);
                    }
                    setAvatar();
                }
            }

            @Override
            public void onError(CometChatException e) {
            }
        });
    }

    /**
     * This method is used to send Text Message to other users and groups.
     *
     * @param message is a String which is been sent as message.
     * @see TextMessage
     * @see CometChat#sendMessage(TextMessage, CometChat.CallbackListener)
     */
    public void sendMessage(String message) {
        ChatType = "Text";
        TextMessage textMessage;
        final TextMessage[] textMessageProfinity = new TextMessage[1];
        if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
            textMessage = new TextMessage(NextGroupOrUserId, message, CometChatConstants.RECEIVER_TYPE_USER);
        else
            textMessage = new TextMessage(NextGroupOrUserId, message, CometChatConstants.RECEIVER_TYPE_GROUP);

        sendTypingIndicator(true);
        JSONArray languageArray = new JSONArray();
        languageArray.put("en");
        languageArray.put("hi");
        languageArray.put("ar");
        languageArray.put("ko");
        languageArray.put("de");
        languageArray.put("fr");
        languageArray.put("fr-CA");
        languageArray.put("es");
        languageArray.put("ru");
        languageArray.put("pt");
        languageArray.put("vi");
        languageArray.put("ur");

        JSONObject jsonObject = new JSONObject();
        switch (tabs) {
            //Friend chat
            case "1":
                try {
                    Log.i(TAG, "sendMessage: tab 1");
                    group_all_members_id = "";
                    jsonObject.put("team_logs_id", 0);
                    jsonObject.put("message_translation_languages", languageArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            //group
            case "2":
                try {
                    Log.i(TAG, "sendMessage: tab 2");
                    jsonObject.put("message_translation_languages", languageArray);
                    Log.i(TAG, "sendMessage: Hello its tab 2 sending msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "3":
                Log.i(TAG, "sendMessage: tab 3");
                team_logs_id = receiverId + "_-" + teamId + "_-" + 3;                                                       //+"_sage036"
                try {
                    Log.i(TAG, "sendMessage: ");
                    group_all_members_id = "";

                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("message_translation_languages", languageArray);
                    Log.i(TAG, "sendMessage: TeamId " + teamId);
                    Log.i(TAG, "sendMessage: Hello its tab3 sending msg " + team_logs_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "4":
                Log.i(TAG, "sendMessage: tab 4");
                team_logs_id = receiverId + "_-" + teamId + "_-" + 4;                                                              //+"_sage036"
                try {
                    group_all_members_id = "";
                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("message_translation_languages", languageArray);
                    Log.i(TAG, "sendMessage: Hello its tab4 sending msg" + team_logs_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }

        textMessage.setMetadata(jsonObject);

        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                Log.i(TAG, "onSuccess: send message " + textMessage);
                Thread threadSaveChatLogToServer = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //team_Ids,comet_chat_type group_all_members_id,receiverMail
                        saveChatLogToTheServer("Text", message, ChatGroupId, textMessage.getId());
                    }
                });
                threadSaveChatLogToServer.start();

                JSONObject metadata = textMessage.getMetadata();
                /*here we are getting translated sent message and showing that message in selected language
                 * code is added by rahul maske */
                if (metadata != null) {
                    JSONObject injectedObject = null;
                    try {
                        injectedObject = metadata.getJSONObject("@injected");

                        if (injectedObject.has("extensions")) {
                            JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                            if (extensionsObject.has("message-translation")) {
                                JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                HashMap<String, String> translationsMap = new HashMap<String, String>();

                                for (int i = 0; i < translations.length(); i++) {
                                    JSONObject translation = translations.getJSONObject(i);
                                    String translatedText = translation.getString("message_translated");
                                    String translatedLanguage = translation.getString("language_translated");
                                    translationsMap.put(translatedLanguage, translatedText);
                                    Log.i(TAG, "filterBaseMessages: currentLang " + sp.getString("currentLang", "en"));
                                    if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                        if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                            if (textMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                ((TextMessage) textMessage).setText(translatedText);
                                            }
                                        }
                                    } else {
                                        ((TextMessage) textMessage).setText(((TextMessage) textMessage).getText());
                                    }
                                }
                            }

                            Log.e(TAG, "filterBaseMessages: on MessageTextSend" + textMessage.toString());
                            if (textMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                           /* Action action = ((Action) textMessage);
                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                            } else {
                                //tempList.add(baseMessage);
                                messageAdapter.addMessage(textMessage);
                            }*/
                                Log.i(TAG, "onSuccess: category action");
                            } else {
//                            tempList.add(baseMessage);
                                messageAdapter.addMessage(textMessage);
                                scrollToBottom();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                /*isSmartReplyClicked = false;
                Log.i(TAG, "onSuccess: " + textMessage.toString());
                if (messageAdapter != null) {
                    Log.e("success Metadata", textMessage.toString());
                    MediaUtils.playSendSound(context, R.raw.outgoing_message);
                    messageAdapter.addMessage(textMessage);
                    currentLang = sp.getString("currentLang", currentLang);
                    scrollToBottom();
                    if (!currentLang.equals("en")){
                        fetchMessage();
                        scrollToBottom();

                    }else{

                        scrollToBottom();
                    }
                }*/

            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: message " + e);
                String[] profinityArray = Profinity.getProfinityArray();
                String abuseString = "";
                int counter = 0;
                String[] parts = message.split(" ");
                int length = parts.length;
                for (String messageWord : parts) {
                    counter = counter + 1;
                    for (String word : profinityArray) {
                        if (messageWord.equals(word)) {
                            Log.i(TAG, " onError : counter " + counter + " messageWord " + messageWord + " word " + word);
                            abuseString = abuseString + ", " + messageWord;
                            if (counter == length) {
                                profinityAlert(e.getMessage(), abuseString);
                            }
                        }
                    }
                }
            }
        });
    }

    public void profinityAlert(String message, String abuseString) {
        if (message.equals("The operation has been blocked by extension profanity-filter.")) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(context);
            //Setting message manually and performing action on button click
            builder.setMessage("Please remove the word(s) which are showing in bold : " + abuseString)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
    }

    /**
     * This method is used to delete the message.
     *
     * @param baseMessage is an object of BaseMessage which is being used to delete the message.
     * @see BaseMessage
     * @see CometChat#deleteMessage(int, CometChat.CallbackListener)
     */
    public void deleteMessage(BaseMessage baseMessage) {
        CometChat.deleteMessage(baseMessage.getId(), new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage baseMessage) {
                if (messageAdapter != null)
                    messageAdapter.setUpdatedMessage(baseMessage);
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }
        });
    }

    /**
     * This method is used to edit the message. This methods takes old message and change text of old
     * message with new message i.e String and update it.
     *
     * @param baseMessage is an object of BaseMessage, It is a old message which is going to be edited.
     * @param message     is String, It is a new message which will be replaced with text of old message.
     * @see TextMessage
     * @see BaseMessage
     * @see CometChat#editMessage(BaseMessage, CometChat.CallbackListener)
     */
    public void editMessage(BaseMessage baseMessage, String message) {
        isEdit = false;
        TextMessage textMessage;
        JSONObject jsonObject = new JSONObject();

        if (baseMessage.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER)) {
            textMessage = new TextMessage(baseMessage.getReceiverUid(), message, CometChatConstants.RECEIVER_TYPE_USER);
            Log.i(TAG, "editMessage: Type is " + CometChatConstants.RECEIVER_TYPE_USER);
        } else {
            textMessage = new TextMessage(baseMessage.getReceiverUid(), message, CometChatConstants.RECEIVER_TYPE_GROUP);
            Log.i(TAG, "editMessage: Type is " + CometChatConstants.RECEIVER_TYPE_GROUP);
        }
        sendTypingIndicator(true);
        textMessage.setId(baseMessage.getId());
        /*now we are not sending new metadata for edited message because it is shuffling in  web
         * 05-06-2021 commented by rahul*/
        /*        JSONArray languageArray = new JSONArray();
        languageArray.put("en");
        languageArray.put("hi");
        languageArray.put("ar");
        languageArray.put("ko");
        languageArray.put("de");
        languageArray.put("fr");
        languageArray.put("fr-CA");
        languageArray.put("es");
        languageArray.put("ru");
        languageArray.put("pt");
        languageArray.put("vi");
        languageArray.put("ur");

        switch (tabs) {
            //Friend chat
            case "1":
                try {
                    jsonObject.put("team_logs_id", 0);
                    jsonObject.put("message_translation_languages", languageArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            //group
            case "2":
                try {
                    jsonObject.put("message_translation_languages", languageArray);
                    Log.i(TAG, "sendMessage: Hello its tab 2 sending msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "3":
                team_logs_id = receiverId + "_-" + teamId + "_-" + 3; //+"_sage036"
                try {
                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("message_translation_languages", languageArray);
                    Log.i(TAG, "sendMessage: TeamId " + teamId);
                    Log.i(TAG, "sendMessage: Hello its tab3 sending msg " + team_logs_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "4":
                team_logs_id = receiverId + "_-" + teamId + "_-" + 4; //+"_sage036"
                try {
                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("message_translation_languages", languageArray);
                    Log.i(TAG, "sendMessage: Hello its tab4 sending msg" + team_logs_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }*/
        Log.i(TAG, "editMessage:  metadata" + jsonObject);

        //textMessage.setMetadata(baseMessage.getMetadata());
        CometChat.editMessage(textMessage, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage baseMessage1) {
                Log.e(TAG, "onSuccess: edited message" + baseMessage1.toString());
                Log.e(TAG, "onSuccess: previous message" + baseMessage.toString());
                Thread threadSaveEditChatLogToServer = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //team_Ids,comet_chat_type group_all_members_id,receiverMail
                        saveChatLogForEditMessage(message, textMessage.getId());
                    }
                });
                threadSaveEditChatLogToServer.start();
                //messageAdapter.setUpdatedMessage(baseMessage1);
                if (messageAdapter != null) {
                    try {
                        JSONObject body = new JSONObject();
                        JSONArray languages = new JSONArray();
                        languages.put(sp.getString("currentLang", currentLang));
                        body.put("msgId", baseMessage1.getId());
                        body.put("languages", languages);
                        body.put("text", "" + message);
                        CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                new CometChat.CallbackListener<JSONObject>() {
                                    @Override
                                    public void onSuccess(JSONObject jsonObject) {
                                        Log.e(TAG, "onSuccess: message-translation " + jsonObject);
                                        //baseMessage.setMetadata();
                                        BaseMessage baseMessage2 = createMetadataForEditedMessageNew(baseMessage1, jsonObject);
                                        Log.i(TAG, "onSuccess: baseMessage 2 " + baseMessage2);
                                        Log.i(TAG, "onSuccess: baseMessage 1 " + baseMessage1);
                                        Log.i(TAG, "onSuccess: baseMessage " + baseMessage);
                                        messageAdapter.setUpdatedMessage(baseMessage2);
                                        //messageAdapter.setEditedMessage(baseMessage1, baseMessage2);

                                    }

                                    @Override
                                    public void onError(CometChatException e) {
                                        Log.e(TAG, "onError: " + e.getMessage());
                                    }
                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //messageAdapter.setUpdatedMessage(message);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }
        });
    }

    /*In this method we are making the matadata for edited message
     * method is created by rahul maske */
    public BaseMessage createMetadataForEditedMessage(BaseMessage baseMessage1, JSONObject
            jsonObject) {
        try {
            String team_logs_id = baseMessage1.getMetadata().getString("team_logs_id");
            long readByMeAt = baseMessage1.getReadByMeAt();
            long deliveredToMeAt = baseMessage1.getDeliveredToMeAt();
            long deletedAt = baseMessage1.getDeletedAt();
            long editedAt = baseMessage1.getEditedAt();
            String deletedBy = baseMessage1.getDeletedBy();
            String editedBy = baseMessage1.getEditedBy();
            long updatedAt = baseMessage1.getUpdatedAt();
            JSONArray message_translation_languages = baseMessage1.getMetadata().getJSONArray("message_translation_languages");
            JSONObject metadata = new JSONObject();
            JSONObject injected = new JSONObject();
            JSONObject extensions = new JSONObject();
            JSONObject message_translation = new JSONObject();
            if (jsonObject.has("data")) {
                //translations.put(jsonObject.getJSONObject("data").getJSONArray("translations"));
                message_translation.put("translations", jsonObject.getJSONObject("data").getJSONArray("translations"));
                extensions.put("message-translation", message_translation);
                injected.put("extensions", extensions);
                metadata.put("@injected", injected);
                metadata.put("team_logs_id", team_logs_id);
                metadata.put("readByMeAt", readByMeAt);
                metadata.put("deliveredToMeAt", deliveredToMeAt);
                metadata.put("deletedAt", deletedAt);
                metadata.put("editedAt", editedAt);
                metadata.put("deletedBy", deletedBy);
                metadata.put("editedBy", editedBy);
                metadata.put("updatedAt", updatedAt);

                baseMessage1.setMetadata(metadata);
                Log.i(TAG, "createMetadataForEditedMessage: metadata is " + metadata);
            } else {
                Log.i(TAG, "createMetadataForEditedMessage: message-translation not found");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return baseMessage1;
    }


    /*In this method we are making the matadata for edited message
     * method is created by rahul maske */
    public BaseMessage createMetadataForEditedMessageNew(BaseMessage baseMessage1, JSONObject
            jsonObject) {
        try {
            // String team_logs_id = baseMessage1.getMetadata().getString("team_logs_id");
            long readByMeAt = baseMessage1.getReadByMeAt();
            long deliveredToMeAt = baseMessage1.getDeliveredToMeAt();
            long deletedAt = baseMessage1.getDeletedAt();
            long editedAt = baseMessage1.getEditedAt();
            String deletedBy = baseMessage1.getDeletedBy();
            String editedBy = baseMessage1.getEditedBy();
            long updatedAt = baseMessage1.getUpdatedAt();
            JSONArray message_translation_languages = baseMessage1.getMetadata().getJSONArray("message_translation_languages");
            JSONObject metadata = new JSONObject();
            JSONObject injected = new JSONObject();
            JSONObject extensions = new JSONObject();
            JSONObject message_translation = new JSONObject();
            if (jsonObject.has("data")) {
                //translations.put(jsonObject.getJSONObject("data").getJSONArray("translations"));
                message_translation.put("translations", jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0));
                extensions.put("message-translation", message_translation);
                injected.put("extensions", extensions);
                metadata.put("@injected", injected);
                // metadata.put("team_logs_id", team_logs_id);
                metadata.put("readByMeAt", readByMeAt);
                metadata.put("deliveredToMeAt", deliveredToMeAt);
                metadata.put("deletedAt", deletedAt);
                metadata.put("editedAt", editedAt);
                metadata.put("deletedBy", deletedBy);
                metadata.put("editedBy", editedBy);
                metadata.put("updatedAt", updatedAt);
                ((TextMessage) baseMessage1).setText(jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated"));
                baseMessage1.setMetadata(metadata);

                Log.i(TAG, "createMetadataForEditedMessage: metadata is " + metadata);
            } else {
                Log.i(TAG, "createMetadataForEditedMessage: message-translation not found");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return baseMessage1;
    }


    /**
     * This method is used to send reply message by link previous message with new message.
     *
     * @param baseMessage is a linked message
     * @param message     is a String. It will be new message sent as reply.
     */

    public void replyMessage(BaseMessage baseMessage, String message) {
        isReply = false;
        try {
            TextMessage textMessage;
            if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
                textMessage = new TextMessage(NextGroupOrUserId, message, CometChatConstants.RECEIVER_TYPE_USER);
            else
                textMessage = new TextMessage(NextGroupOrUserId, message, CometChatConstants.RECEIVER_TYPE_GROUP);

            JSONObject replyObject = new JSONObject();
            //replyObject.put("replyToMessage", baseMessage);
            replyObject.put("type", "reply");
            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
//                replyObject.put("type", CometChatConstants.MESSAGE_TYPE_TEXT);
                replyObject.put("message", ((TextMessage) baseMessage).getText());
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_IMAGE)) {
//                replyObject.put("type", CometChatConstants.MESSAGE_TYPE_IMAGE);
                replyObject.put("message", "image");
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_VIDEO)) {
//                replyObject.put("type", CometChatConstants.MESSAGE_TYPE_VIDEO);
                replyObject.put("message", "video");
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_FILE)) {
//                replyObject.put("type", CometChatConstants.MESSAGE_TYPE_FILE);
                replyObject.put("message", "file");
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_AUDIO)) {
                // replyObject.put("type", CometChatConstants.MESSAGE_TYPE_AUDIO);
                replyObject.put("message", "audio");
            }
            replyObject.put("name", baseMessage.getSender().getName());
            replyObject.put("avatar", baseMessage.getSender().getAvatar());
            // jsonObject.put("reply", replyObject);

            JSONArray languageArray = new JSONArray();
            languageArray.put("en");
            languageArray.put("hi");
            languageArray.put("ar");
            languageArray.put("ko");
            languageArray.put("de");
            languageArray.put("fr");
            languageArray.put("fr-CA");
            languageArray.put("es");
            languageArray.put("ru");
            languageArray.put("pt");
            languageArray.put("vi");
            languageArray.put("ur");

            replyObject.put("message_translation_languages", languageArray);
            switch (tabs) {
                case "1":
                    try {
                        replyObject.put("team_logs_id", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "2":

                    //textMessage.setMetadata(jsonObject);
                    break;

                case "3":
                    if (receiverId != null && teamId != null) {
                        team_logs_id = receiverId + "_-" + teamId + "_-" + 3;
                        try {
                            replyObject.put("team_logs_id", team_logs_id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //textMessage.setMetadata(jsonObject);
                    }
                    break;

                case "4":
                    if (receiverId != null && teamId != null) {
                        team_logs_id = receiverId + "_-" + teamId + "_-" + 4;
                        try {
                            replyObject.put("team_logs_id", team_logs_id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // textMessage.setMetadata(jsonObject);
                    }
                    break;
            }

            textMessage.setMetadata(replyObject);
            sendTypingIndicator(true);
            CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
                @Override
                public void onSuccess(TextMessage textMessage) {
                    if (messageAdapter != null) {
                        MediaUtils.playSendSound(context, R.raw.outgoing_message);
                        Log.e(TAG, "onSuccess: replied message response is " + textMessage);
                        filterTextMessage(textMessage, "");
                        //messageAdapter.addMessage(textMessage);
                        scrollToBottom();
                    }
                }

                @Override
                public void onError(CometChatException e) {
                    Log.e(TAG, "onError: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "replyMessage: " + e.getMessage());
        }
    }

    public void scrollToBottom() {
        if (messageAdapter != null && messageAdapter.getItemCount() > 0) {
            rvChatListView.scrollToPosition(messageAdapter.getItemCount() - 1);
        }
    }

    /**
     * This method is used to recieve real time group events like onMemberAddedToGroup, onGroupMemberJoined,
     * onGroupMemberKicked, onGroupMemberLeft, onGroupMemberBanned, onGroupMemberUnbanned,
     * onGroupMemberScopeChanged.
     *
     * @see CometChat#addGroupListener(String, CometChat.GroupListener)
     */
    public void addGroupListener() {
        CometChat.addGroupListener(TAG, new CometChat.GroupListener() {
            @Override
            public void onGroupMemberJoined(Action action, User joinedUser, Group joinedGroup) {
                super.onGroupMemberJoined(action, joinedUser, joinedGroup);
                Log.i(TAG, "onGroupMemberJoined: ");
                if (joinedGroup.getGuid().equals(NextGroupOrUserId))
                    tvStatus.setText(memberNames + "," + joinedUser.getName());

                onMessageReceived(action);
            }

            @Override
            public void onGroupMemberLeft(Action action, User leftUser, Group leftGroup) {
                super.onGroupMemberLeft(action, leftUser, leftGroup);
                Log.d(TAG, "onGroupMemberLeft: " + leftUser.getName());
                if (leftGroup.getGuid().equals(NextGroupOrUserId)) {
                    if (memberNames != null)
                        tvStatus.setText(memberNames.replace("," + leftUser.getName(), ""));
                }
                onMessageReceived(action);
            }

            @Override
            public void onGroupMemberKicked(Action action, User kickedUser, User kickedBy, Group kickedFrom) {
                super.onGroupMemberKicked(action, kickedUser, kickedBy, kickedFrom);
                Log.d(TAG, "onGroupMemberKicked: " + kickedUser.getName());
                if (kickedUser.getUid().equals(CometChat.getLoggedInUser().getUid())) {
                    if (getActivity() != null)
                        getActivity().finish();

                }
                if (kickedFrom.getGuid().equals(NextGroupOrUserId))
                    tvStatus.setText(memberNames.replace("," + kickedUser.getName(), ""));
                onMessageReceived(action);
            }

            @Override
            public void onGroupMemberBanned(Action action, User bannedUser, User bannedBy, Group bannedFrom) {
                Log.i(TAG, "onGroupMemberBanned: ");
                if (bannedUser.getUid().equals(CometChat.getLoggedInUser().getUid())) {
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                        Toast.makeText(getActivity(), "You have been banned", Toast.LENGTH_SHORT).show();
                    }
                }
                onMessageReceived(action);

            }

            @Override
            public void onGroupMemberUnbanned(Action action, User unbannedUser, User unbannedBy, Group unbannedFrom) {
                onMessageReceived(action);
                Log.i(TAG, "onGroupMemberUnbanned: ");
            }

            @Override
            public void onGroupMemberScopeChanged(Action action, User updatedBy, User updatedUser, String scopeChangedTo, String scopeChangedFrom, Group group) {
                onMessageReceived(action);
                Log.i(TAG, "onGroupMemberScopeChanged: ");
            }

            @Override
            public void onMemberAddedToGroup(Action action, User addedby, User userAdded, Group addedTo) {
                Log.i(TAG, "onMemberAddedToGroup: ");
                if (addedTo.getGuid().equals(NextGroupOrUserId))
                    tvStatus.setText(memberNames + "," + userAdded.getName());
                onMessageReceived(action);
            }
        });
    }

    /**
     * This method is used to get real time user status i.e user is online or offline.
     *
     * @see CometChat#addUserListener(String, CometChat.UserListener)
     */
    public void addUserListener() {
        if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {

            CometChat.addUserListener(TAG, new CometChat.UserListener() {
                @Override
                public void onUserOnline(User user) {
                    //Log.d(TAG, "onUserOnline: " + user.toString());
                    if (user.getUid().equals(NextGroupOrUserId)) {
                        status = CometChatConstants.USER_STATUS_ONLINE;
                        tvStatus.setText(user.getStatus());
                        tvStatus.setTextColor(getResources().getColor(R.color.textColorWhite));
                    }
                }

                @Override
                public void onUserOffline(User user) {
                    //Log.d(TAG, "onUserOffline: " + user.toString());
                    if (user.getUid().equals(NextGroupOrUserId)) {
                        if (Utils.isDarkMode(getContext()))
                            tvStatus.setTextColor(getResources().getColor(R.color.textColorWhite));
                        else
                            tvStatus.setTextColor(getResources().getColor(android.R.color.black));
                        tvStatus.setText(user.getStatus());
                        status = CometChatConstants.USER_STATUS_OFFLINE;
                    }
                }
            });
        }
    }


    /**
     * This method is used to mark users & group message as read.
     *
     * @param baseMessage is object of BaseMessage.class. It is message which is been marked as read.
     */
    public void markMessageAsRead(BaseMessage baseMessage) {
        if (type.equals(CometChatConstants.RECEIVER_TYPE_USER))
            CometChat.markAsRead(baseMessage.getId(), baseMessage.getSender().getUid(), baseMessage.getReceiverType());
        else
            CometChat.markAsRead(baseMessage.getId(), baseMessage.getReceiverUid(), baseMessage.getReceiverType());
    }


    /**
     * This method is used to add message listener to recieve real time messages between users &
     * groups. It also give real time events for typing indicators, edit message, delete message,
     * message being read & delivered.
     *
     * @see CometChat#addMessageListener(String, CometChat.MessageListener)
     */
    public void addMessageListener() {

        CometChat.addMessageListener(TAG, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage message) {
                /* try {*/
                Log.i(TAG, "onTextMessageReceived: message parent id" + message.getParentMessageId());
                Log.i(TAG, "onTextMessageReceived: message id" + message.getId());
                /*real time reply message counter chaned by below if condition
                 * commented and created by rahul maske */
                if (message.getParentMessageId() != 0 && message.getEditedAt() == 0) {
                    Log.i(TAG, "onTextMessageReceived: set reply message ");
                    messageAdapter.setReplyMessage(message);
                } else if (message.getReceiverType().equalsIgnoreCase("group")
                        && message.getReceiverUid().equalsIgnoreCase(NextGroupOrUserId)) {
                    Log.i(TAG, "onTextMessageReceived: set group messages ");
                    onMessageReceived(message);
                } else if (message.getReceiverType().equalsIgnoreCase("user")) {
                    Log.i(TAG, "onTextMessageReceived: set group messages ");
                        /*if (message.getMetadata().getString("team_logs_id").equalsIgnoreCase("0")) {
                            if (message.getParentMessageId() != 0 && message.getEditedAt() == 0) {
                                Log.i(TAG, "onTextMessageReceived: ");
                                for (int i = 0; i < messageList.size(); i++)
                                    if (messageList.get(i).getId() == message.getParentMessageId()) {
                                        int count = messageList.get(i).getReplyCount();
                                        messageList.get(i).setReplyCount(count + 1);
                                        Log.i(TAG, "onTextMessageReceived: user1");
                                    }
                            } else {*/

                    Log.i(TAG, "onTextMessageReceived: user2");
                    onMessageReceived(message);

                    /* }*/
                } else {
                    //Log.e(TAG, "onTextMessageReceived: Team");
                    if (message.getParentMessageId() != 0 && message.getEditedAt() == 0) {
                                /*for (int i = 0; i < messageList.size(); i++){
                                    if (messageList.get(i).getId() == message.getParentMessageId()) {
                                        int count = messageList.get(i).getReplyCount();
                                        messageList.get(i).setReplyCount(count + 1);
                                        Log.i(TAG, "onTextMessageReceived: group else");
                                    }
                                }
                            } else {*/
                        Log.i(TAG, "onTextMessageReceived: else2");
                        onMessageReceived(message);
                        /* }*/
                    }
                       /* if(message.getParentMessageId() == ) {
                            Log.d(TAG,"Text Message Received for active thread");
                        }*/
                }
             /*   } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }

            @Override
            public void onMediaMessageReceived(MediaMessage message) {
                //Log.d(TAG, "onMediaMessageReceived: " + message.toString());

                //onMessageReceived(message);
                if (message.getReceiverType().equalsIgnoreCase("group")
                        && message.getReceiverUid().equalsIgnoreCase(NextGroupOrUserId)) {
                    onMessageReceived(message);
                } else if (message.getReceiverType().equalsIgnoreCase("user")) {
                    onMessageReceived(message);
                }
            }

            @Override
            public void onTypingStarted(TypingIndicator typingIndicator) {
                //Log.e(TAG, "onTypingStarted: " + typingIndicator);
                setTypingIndicator(typingIndicator, true);
            }

            @Override
            public void onTypingEnded(TypingIndicator typingIndicator) {
                //Log.d(TAG, "onTypingEnded: " + typingIndicator.toString());
                setTypingIndicator(typingIndicator, false);
            }

            @Override
            public void onMessagesDelivered(MessageReceipt messageReceipt) {
                //Log.d(TAG, "onMessagesDelivered: " + messageReceipt.toString());
                setMessageReciept(messageReceipt);

            }


            @Override
            public void onMessagesRead(MessageReceipt messageReceipt) {
                Log.e(TAG, "onMessagesRead: " + messageReceipt.toString());
                setMessageReciept(messageReceipt);
            }

            @Override
            public void onMessageEdited(BaseMessage message) {
                Log.d(TAG, "onMessageEdited: " + message.toString());
                /*delete one_to_one is tag which we user for messaged deletion if it has this tag that means that message is deleted
                 * commented and created by rahul maske on 24-06-2021*/
                if (message.getMetadata().has("deleted_one_to_one")) {
                    message.setDeletedAt(12324);
                    messageAdapter.setUpdatedMessage(message);
                    Log.i(TAG, "onMessageEdited: 1");
                } else {
                    Log.i(TAG, "onMessageEdited: 2");
                    updateMessage(message);
                }
            }

            @Override
            public void onMessageDeleted(BaseMessage message) {
                Log.d(TAG, "onMessageDeleted: ");
                updateMessage(message);
            }

            @Override
            public void onCustomMessageReceived(CustomMessage customMessage) {
                //Log.e(TAG, "Custom message received successfully: " + customMessage.toString());
                //Log.e(TAG, "onCustomMessageReceived: " + customMessage.getMetadata());
                //Log.e(TAG, "onCustomMessageReceived: receiverType" + customMessage.getReceiverType());

                if (customMessage.getReceiverType().equalsIgnoreCase("group")
                        && customMessage.getReceiverUid().equalsIgnoreCase(NextGroupOrUserId)) {
                    if (customMessage.getType().equals("extension_whiteboard")) {
                        String s = null;

                        try {
                            s = customMessage.getMetadata().getString("whiteboard_URL_group");
                            if (s != null) {
                                onMessageReceived(customMessage);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Log.i(TAG, "onCustomMessageReceived: whiteboard_URL_group" + s);
                    }


                } else if (customMessage.getReceiverType().equalsIgnoreCase("user")) {
                    Log.e(TAG, "onCustomMessageReceived: customMessageMainElse");
                    try {
                        String s = null;
                        //Log.e(TAG, "onCustomMessageReceived: Metadata logid" + customMessage.getMetadata().getString("whiteboard_URL_group"));
                        Log.e(TAG, "onCustomMessageReceived: teamLogId" + customMessage.getMetadata().getString("team_logs_id"));
                        if (customMessage.getMetadata().getString("team_logs_id").equalsIgnoreCase("0")) {
                            Log.e(TAG, "onCustomMessageReceived: team_logs_id=0");
                            s = customMessage.getMetadata().getString("whiteboard_URL_one");
                            if (s != null) {
                                onMessageReceived(customMessage);
                            }

                        } else {
                            Log.e(TAG, "onCustomMessageReceived: team_logs_id != 0");
                            s = customMessage.getMetadata().getString("whiteboard_URL");
                            if (s != null) {
                                onMessageReceived(customMessage);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }
        });
    }

    public void setMessageReciept(MessageReceipt messageReceipt) {
        if (messageAdapter != null) {
            if (messageReceipt.getReceivertype().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                if (NextGroupOrUserId != null && messageReceipt.getSender().getUid().equals(NextGroupOrUserId)) {
                    if (messageReceipt.getReceiptType().equals(MessageReceipt.RECEIPT_TYPE_DELIVERED))
                        messageAdapter.setDeliveryReceipts(messageReceipt);
                    else
                        messageAdapter.setReadReceipts(messageReceipt);
                }
            }
        }
    }

    public void setTypingIndicator(TypingIndicator typingIndicator, boolean isShow) {
        if (typingIndicator.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER)) {
            Log.e(TAG, "onTypingStarted: " + typingIndicator);
            if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(typingIndicator.getSender().getUid())) {
                if (typingIndicator.getMetadata() == null)
                    typingIndicator(typingIndicator, isShow);
            }
        } else {
            if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(typingIndicator.getReceiverId()))
                typingIndicator(typingIndicator, isShow);
        }
    }

    public void onMessageReceived(BaseMessage message) {
//        MediaUtils.playSendSound(context, R.raw.incoming_message);

        JSONObject metadata = message.getMetadata();
        try {
            /*below code will decide tab number basis on team_logs_id*/
            if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                String team_logs_id = metadata.getString("team_logs_id");
                if (team_logs_id.equals("0") || team_logs_id.isEmpty()) {
                    tabForReceivedMessages = "1";
                    Log.i(TAG, "checkIntent: This is for friend ");
                } else {
                    String[] teamLogArray = team_logs_id.split("_-");
                    teamLogArray = metadata.getString("team_logs_id").split("_-");
                    if (myTeamArrayList.contains(teamLogArray[1])) {
                        //message is from my team
                        Log.i(TAG, "checkIntent: myTeam" + teamLogArray[1]);
                        tabForReceivedMessages = "3";
                    } else if (joinTeamArrayList.contains(teamLogArray[1])) {
                        //message is from join team
                        tabForReceivedMessages = "4";
                        Log.i(TAG, "checkIntent: joinTeam" + teamLogArray[1]);
                    }
                }
            } else {
                //message from group
                tabForReceivedMessages = "2";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "onMessageReceived: tab" + tabs + " tabForReceivedMessages " + tabForReceivedMessages);
        switch (tabForReceivedMessages) {
            case "1":
                int team_log;
                try {
                    if (tabForReceivedMessages.equalsIgnoreCase(tabs)) {
                        if (metadata.has("team_logs_id")) {
                            team_log = metadata.getInt("team_logs_id");
                            if (team_log == 0) {
                                /*for normal message and for android  reply message this block will execute*/
                                if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                                    if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(message.getSender().getUid())) {

                                        Log.i(TAG, "onMessageReceived: case 1 reply block");
                                        filterTextMessage(message, "");
                                        //scrollToBottom();
                                        //}
                                    } else if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(message.getReceiverUid())
                                            && message.getSender().getUid().equalsIgnoreCase(loggedInUser.getUid())) {
                                        Log.i(TAG, "onMessageReceived: else part");
                                        //if (!checkIOSReplyOrNot(metadata, message, General.CHATSCREEN_RECEIVED_MESSAGE)) {
                                        setMessage(message);
                                        scrollToBottom();
                                        //}
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "2":
                Log.i(TAG, "onMessageReceived: case 2 ");
                try {
                    if (tabForReceivedMessages.equalsIgnoreCase(tabs)) {
                        if (!metadata.has("team_logs_id")) {
                            /*if (!checkIOSReplyOrNot(metadata, message, General.CHATSCREEN_RECEIVED_MESSAGE)) {*/
                           /* setMessage(message);
                            scrollToBottom();*/
                            filterTextMessage(message, "");
                            /*}*/
                            Log.i(TAG, "onMessageReceived: group case setMessage");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case "3":
                String logss;
                try {
                    if (tabForReceivedMessages.equalsIgnoreCase(tabs)) {
                        logss = metadata.getString("team_logs_id");
                        if (!logss.isEmpty() && !logss.equals("0")) {
                            if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                                if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(message.getSender().getUid())) {
                                    /*for normal message and for android  reply message this block will execute*/

                                    Log.i(TAG, "onMessageReceived: case 1 reply block");
                                    filterTextMessage(message, "");

                                } else if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(message.getReceiverUid())
                                        && message.getSender().getUid().equalsIgnoreCase(loggedInUser.getUid())) {
                                    Log.i(TAG, "onMessageReceived: else part");

                                    setMessage(message);
                                    scrollToBottom();

                                }
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "4":
                String logs;
                try {
                    if (tabForReceivedMessages.equalsIgnoreCase(tabs)) {
                        Log.i(TAG, "onMessageReceived: case 4 ");
                        logs = metadata.getString("team_logs_id");
                        Log.e(TAG, "onMessageReceived: " + logs);
                        if (!logs.isEmpty() && !logs.equals("0")) {
                            if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {

                                if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(message.getSender().getUid())) {
                                    /*for normal message and for android  reply message this block will execute*/

                                    Log.i(TAG, "onMessageReceived: case 1 reply block");
                                    filterTextMessage(message, "");

                                } else if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(message.getReceiverUid())
                                        && message.getSender().getUid().equalsIgnoreCase(loggedInUser.getUid())) {
                                    Log.i(TAG, "onMessageReceived: else part");

                                    setMessage(message);
                                    scrollToBottom();

                                }
                                /*commented because it was not working for ios replied messages on 17-06-2021 by rahul maske*/
                                /* if (SenderId != null && SenderId.equalsIgnoreCase(message.getSender().getUid())) {
                                 *//*if (!checkIOSReplyOrNot(metadata, message, General.CHATSCREEN_RECEIVED_MESSAGE)) {*//*
                                 *//*setMessage(message);
                                    scrollToBottom();*//*
                                filterTextMessage(message, "");
                                *//*}*//*
                            } else if (SenderId != null && SenderId.equalsIgnoreCase(message.getReceiverUid()) && message.getSender().getUid().equalsIgnoreCase(loggedInUser.getUid())) {

                                *//*if (!checkIOSReplyOrNot(metadata, message, General.CHATSCREEN_RECEIVED_MESSAGE)) {*//*
                                setMessage(message);
                                scrollToBottom();
                                *//*}*//*
                            }*/
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /*In this method we are checking metadata for reply messages from ios and if the
     * msg is send by ios then we convert that metadata to our required metadata and set message
     * code is added by rahul maske */
    public boolean checkIOSReplyOrNot(JSONObject metadata, BaseMessage message, String
            msgFrom) {
        if (metadata.has("type")) {
            try {
                JSONObject customMetadata = new JSONObject();
                JSONObject reply = new JSONObject();
                reply.put("name", message.getSender().getName());
                reply.put("type", message.getType());
                reply.put("avatar", message.getSender().getAvatar());
                reply.put("message", message.getMetadata().getString("message"));
                customMetadata.put("reply", reply);
                customMetadata.put("readByMeAt", message.getReadAt());
                customMetadata.put("deliveredToMeAt", message.getDeliveredToMeAt());
                customMetadata.put("deletedAt", message.getDeletedAt());
                customMetadata.put("editedAt", message.getEditedAt());
                customMetadata.put("deletedBy", message.getDeletedBy());
                customMetadata.put("editedBy", message.getEditedBy());
                customMetadata.put("updatedAt", message.getUpdatedAt());
                //readByMeAt=0, deliveredToMeAt=0, deletedAt=0, editedAt=0, deletedBy='null',
                // editedBy='null', updatedAt=1616823540
                message.setMetadata(customMetadata);
                setMessage(message);
                scrollToBottom();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //customMetadata.put("reply", );
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method is used to update edited message by calling <code>setEditMessage()</code> of adapter
     *
     * @param message is an object of BaseMessage and it will replace with old message.
     * @see BaseMessage
     */
    private void updateMessage(BaseMessage message) {
        JSONObject metadata = message.getMetadata();
        if (metadata != null) {
            try {
                JSONObject injectedObject = metadata.getJSONObject("@injected");
                if (injectedObject != null && injectedObject.has("extensions")) {
                    JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                    if (extensionsObject != null && extensionsObject.has("voice-transcription")) {
                        JSONObject transcriptionObject = extensionsObject.getJSONObject("voice-transcription");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        messageAdapter.setUpdatedMessage(message);
    }

    /**
     * This method is used to mark message as read before adding them to list. This method helps to
     * add real time message in list.
     *
     * @param message is an object of BaseMessage, It is recieved from message listener.
     * @see BaseMessage
     */
    public void setMessage(BaseMessage message) {
        Log.e("set message", message.toString());
        if (message.getParentMessageId() == 0) {
            if (messageAdapter != null) {
                messageAdapter.addMessage(message);
                //filterTextMessage(baseMessage);
                checkSmartReply(message);
                markMessageAsRead(message);
                if ((messageAdapter.getItemCount() - 1) - ((LinearLayoutManager) rvChatListView.getLayoutManager()).findLastVisibleItemPosition() < 5)
                    scrollToBottom();
            } else {
                messageList.add(message);
                initMessageAdapter(messageList);
            }
        } /*else {
            messageAdapter.addMessage(message);
            checkSmartReply(message);
        }*/
    }

    public void filterTextMessage(@NotNull BaseMessage message, String string) {
        Log.i(TAG, "filterTextMessage: string -> " + string + " message " + message);
        JSONObject metadata = message.getMetadata();
        if (message.getType().equals("text")) {
            try {
                if (metadata.has("message") && metadata.getString("type").equalsIgnoreCase("reply")) {
                    Log.i(TAG, "filterBaseMessages: " + metadata.getString("message"));
                    JSONObject body = new JSONObject();
                    JSONArray languages = new JSONArray();
                    languages.put(sp.getString("currentLang", currentLang));
                    body.put("msgId", message.getId());
                    body.put("languages", languages);
                    body.put("text", metadata.getString("message"));
                    Log.i(TAG, "filterTextMessage: reply from iOS ");
                    // Do something after 5s = 5000ms
                    CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                            new CometChat.CallbackListener<JSONObject>() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {
                                    try {
                                        // Log.i(TAG, "filterBaseMessages: edited at block 3 translated response for replied message "+jsonObject);
                                        JSONObject meta = message.getMetadata();
                                        //meta.accumulate("values", jsonObject);
                                        String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");
                                        Log.i(TAG, "filterBaseMessages: onSuccess at block 3 messageTranslatedString " + messageTranslatedString);
                                        metadata.remove("message");
                                        metadata.put("message", messageTranslatedString);
                                        //messageAdapter.setUpdatedMessage(message);
                                        Log.i(TAG, "filterBaseMessages onSuccess: metadata for replied translate " + metadata);
                                        // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                        //baseMessage.setMetadata(meta);
                                        // ((TextMessage) baseMessage).setText(messageTranslatedString);
                                        //baseMessage.setMetadata(meta);
                                        transaletAndSetMessage(metadata, message, string);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(CometChatException e) {
                                    // Some error occured
                                    Log.i(TAG, "onError: " + e.getMessage());
                                    Log.i(TAG, "filterBaseMessages: edited at block 4");
                                }
                            });
                } else {
                    transaletAndSetMessage(metadata, message, string);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            setMessage(message);
            scrollToBottom();
        }

    }

    private void realTimeReplyFromAndroid(BaseMessage baseMessage1) throws JSONException {
        //((TextMessage) baseMessage).setText(baseMessages);
        Log.i(TAG, "realTimeReplyFromAndroid: ");
        JSONObject metadata = baseMessage1.getMetadata();
        if (metadata.has("message") && metadata.getString("type").equalsIgnoreCase("reply")) {
            Log.i(TAG, "filterBaseMessages: " + metadata.getString("message"));
            JSONObject body = new JSONObject();
            JSONArray languages = new JSONArray();
            languages.put(sp.getString("currentLang", currentLang));
            body.put("msgId", baseMessage1.getId());
            body.put("languages", languages);
            body.put("text", metadata.getString("message"));
            Log.i(TAG, "filterBaseMessages: edited at block 2");
            // Do something after 5s = 5000ms
            CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                    new CometChat.CallbackListener<JSONObject>() {
                        @Override
                        public void onSuccess(JSONObject jsonObject) {
                            try {
                                // Log.i(TAG, "filterBaseMessages: edited at block 3 translated response for replied message "+jsonObject);

                                //meta.accumulate("values", jsonObject);
                                String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");
                                Log.i(TAG, "filterBaseMessages: onSuccess at block 3 messageTranslatedString " + messageTranslatedString);
                                metadata.remove("message");
                                metadata.put("message", messageTranslatedString);
                                messageAdapter.setUpdatedMessage(baseMessage1);
                                Log.i(TAG, "filterBaseMessages onSuccess: metadata for replied translate " + metadata);
                                // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                //baseMessage.setMetadata(meta);
                                // ((TextMessage) baseMessage).setText(messageTranslatedString);
                                //baseMessage.setMetadata(meta);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(CometChatException e) {
                            // Some error occured
                            Log.i(TAG, "onError: " + e.getMessage());
                            Log.i(TAG, "filterBaseMessages: edited at block 4");
                        }
                    });
        }

        try {
            if (metadata.has("@injected")) {
                JSONObject injectedObject = null;
                injectedObject = metadata.getJSONObject("@injected");
                if (injectedObject.has("extensions")) {
                    JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                    if (extensionsObject.has("message-translation")) {
                        JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                        JSONArray translations = messageTranslationObject.getJSONArray("translations");
                        HashMap<String, String> translationsMap = new HashMap<String, String>();
                        for (int i = 0; i < translations.length(); i++) {
                            JSONObject translation = translations.getJSONObject(i);
                            String translatedText = translation.getString("message_translated");
                            String translatedLanguage = translation.getString("language_translated");
                            translationsMap.put(translatedLanguage, translatedText);
                            Log.i(TAG, "filterBaseMessages: currentLang " + sp.getString("currentLang", "en"));

                            if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                    if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                        ((TextMessage) baseMessage).setText(translatedText);
                                    }
                                }
                            } else {
                                ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                            }
                        }
                    }
                    messageAdapter.setUpdatedMessage(baseMessage1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void transaletAndSetMessage(JSONObject metadata, BaseMessage message, String
            string) throws
            JSONException {
        Log.i(TAG, "transaletAndSetMessage: block 1 " + message);
        if (metadata != null) {
            if (!string.equalsIgnoreCase("reply")) {
                JSONObject injectedObject = metadata.getJSONObject("@injected");
                if (injectedObject.has("extensions")) {
                    JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                    if (extensionsObject.has("message-translation")) {
                        Log.i(TAG, "transaletAndSetMessage: block 2");
                        JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                        JSONArray translations = messageTranslationObject.getJSONArray("translations");
                        HashMap<String, String> translationsMap = new HashMap<String, String>();
                        for (int i = 0; i < translations.length(); i++) {
                            JSONObject translation = translations.getJSONObject(i);
                            String translatedText = translation.getString("message_translated");
                            String translatedLanguage = translation.getString("language_translated");
                            translationsMap.put(translatedLanguage, translatedText);
                            Log.i(TAG, "transaletAndSetMessage: currentLang " + sp.getString("currentLang", "en"));
                            /* if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {*/
                            if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                if (message.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                    ((TextMessage) message).setText(translatedText);
                                }
                            }

                        /*} else {
                            ((TextMessage) message).setText(((TextMessage) message).getText());
                        }*/
                        }
                    }
                }
            }
            if (messageAdapter != null) {
                Log.i(TAG, "transaletAndSetMessage: block 5 " + string);
                if (string.equals("reply")) {
                    Log.i(TAG, "transaletAndSetMessage: block 3");
                    messageAdapter.addMessage(message);
                    markMessageAsRead(message);
                    scrollToBottom();
                } else {
                    Log.i(TAG, "transaletAndSetMessage: block 4");
                    messageAdapter.addMessage(message);
                    checkSmartReply(message);
                    markMessageAsRead(message);
                    scrollToBottom();
                    if ((messageAdapter.getItemCount() - 1) - ((LinearLayoutManager) rvChatListView.getLayoutManager()).findLastVisibleItemPosition() < 5)
                        scrollToBottom();
                }
            } else {
                Log.i(TAG, "transaletAndSetMessage: block 4");
                messageList.add(message);
                initMessageAdapter(messageList);
            }
        }
    }


    public void checkSmartReply(BaseMessage lastMessage) {
        Log.i(TAG, "checkSmartReply: ");
        if (lastMessage != null && !lastMessage.getSender().getUid().equals(loggedInUser.getUid())) {

            if (lastMessage.getMetadata() != null) {
                getSmartReplyList(lastMessage);

            }
        }
    }

    /**
     * This method is used to display typing status to user.
     *
     * @param show is boolean, If it is true then <b>is Typing</b> will be shown to user
     *             If it is false then it will show user status i.e online or offline.
     */
    public void typingIndicator(TypingIndicator typingIndicator, boolean show) {
        if (messageAdapter != null) {
            if (show) {
                if (typingIndicator.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER))
                    tvStatus.setText("is Typing...");
                else
                    tvStatus.setText(typingIndicator.getSender().getName() + " is Typing...");
            } else {
                if (typingIndicator.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER))
                    if (typingIndicator.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER))
                        tvStatus.setText(status);
                    else
                        tvStatus.setText(memberNames);
            }
        }
    }


    /**
     * This method is used to remove message listener
     *
     * @see CometChat#removeMessageListener(String)
     */
    public void removeMessageListener() {
        CometChat.removeMessageListener(TAG);
    }

    /**
     * This method is used to remove user presence listener
     *
     * @see CometChat#removeUserListener(String)
     */
    public void removeUserListener() {
        CometChat.removeUserListener(TAG);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();

        //forward broadcast broadcast
        bm = LocalBroadcastManager.getInstance(requireActivity());
        IntentFilter actionReceiver = new IntentFilter();
        actionReceiver.addAction("forwardIntent");
        bm.registerReceiver(onJsonReceived, actionReceiver);

        if (messageAdapter != null)
            messageAdapter.stopPlayingAudio();
        removeMessageListener();
        removeUserListener();
        removeGroupListener();
        sendTypingIndicator(true);
    }

    public void removeGroupListener() {
        CometChat.removeGroupListener(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        //forward broadcast broadcast
        /*bm = LocalBroadcastManager.getInstance(requireActivity());
        IntentFilter actionReceiver = new IntentFilter();
        actionReceiver.addAction("forwardIntent");
        bm.registerReceiver(onJsonReceived, actionReceiver);*/
        messageAdapter = null;
        messagesRequest = null;
        rvChatListView.removeItemDecoration(stickyHeaderDecoration);
        checkOnGoingCall();
        fetchMessage();

        Log.i(TAG, "onResume: called");


        addMessageListener();
        if (messageActionFragment != null)
            messageActionFragment.dismiss();

        if (type != null) {
            if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                addUserListener();
                tvStatus.setText(status);
                new Thread(this::getUser).start();
            } else {
                addGroupListener();
                new Thread(this::getGroup).start();
                new Thread(this::getMember).start();
            }
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: called");
        logOutEnrtyCometchat();
        preferencesCheckCurrentActivity = getActivity().getSharedPreferences("preferencesCheckCurrentActivity", MODE_PRIVATE);
        editor = preferencesCheckCurrentActivity.edit();
        editor.putBoolean("IsChatScreen", false);
        editor.commit();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_message_close) {
            if (messageAdapter != null) {
                messageAdapter.clearLongClickSelectedItem();
                messageAdapter.notifyDataSetChanged();
            }
            isEdit = false;
            baseMessage = null;
            editMessageLayout.setVisibility(GONE);
        } else if (id == R.id.iv_reply_close) {
            if (messageAdapter != null) {
                messageAdapter.clearLongClickSelectedItem();
                messageAdapter.notifyDataSetChanged();
            }
            isReply = false;
            baseMessage = null;
            replyMessageLayout.setVisibility(GONE);
        } else if (id == R.id.btn_unblock_user) {
            unblockUser();
        } else if (id == R.id.chatList_toolbar) {
            if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                Intent intent = new Intent(getContext(), CometChatUserDetailScreenActivity.class);
                intent.putExtra(StringContract.IntentStrings.UID, NextGroupOrUserId);
                intent.putExtra(StringContract.IntentStrings.NAME, name);
                intent.putExtra(StringContract.IntentStrings.AVATAR, avatarUrl);
                intent.putExtra(StringContract.IntentStrings.IS_BLOCKED_BY_ME, isBlockedByMe);
                intent.putExtra(StringContract.IntentStrings.STATUS, status);
                intent.putExtra(StringContract.IntentStrings.TYPE, type);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), CometChatGroupDetailScreenActivity.class);
                intent.putExtra(StringContract.IntentStrings.GUID, NextGroupOrUserId);
                intent.putExtra(StringContract.IntentStrings.NAME, name);
                intent.putExtra(StringContract.IntentStrings.AVATAR, avatarUrl);
                intent.putExtra(StringContract.IntentStrings.TYPE, type);
                intent.putExtra(StringContract.IntentStrings.GROUP_TYPE, groupType);
                intent.putExtra(StringContract.IntentStrings.MEMBER_SCOPE, loggedInUserScope);
                intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, groupOwnerId);
                intent.putExtra(StringContract.IntentStrings.MEMBER_COUNT, memberCount);
                intent.putExtra(StringContract.IntentStrings.GROUP_DESC, groupDesc);
                intent.putExtra(StringContract.IntentStrings.GROUP_PASSWORD, groupPassword);
                startActivity(intent);

                //Intent intent = new Intent(getContext(), CometChatGroupDetailScreenActivity.class);
                Log.i(TAG, "onClick: group toolbar " + Group_id);
               /* Bundle bundle = new Bundle();
                bundle.putString(StringContract.IntentStrings.GUID, Group_id);
                bundle.putString(StringContract.IntentStrings.NAME, name);
                bundle.putString(StringContract.IntentStrings.AVATAR, avatarUrl);
                bundle.putString(StringContract.IntentStrings.TYPE, type);
                bundle.putString(StringContract.IntentStrings.GROUP_TYPE, groupType);
                bundle.putString(StringContract.IntentStrings.MEMBER_SCOPE, loggedInUserScope);
                bundle.putString(StringContract.IntentStrings.GROUP_OWNER, groupOwnerId);
                bundle.putString(StringContract.IntentStrings.MEMBER_COUNT, ""+ memberCount);
                bundle.putString(StringContract.IntentStrings.GROUP_DESC, groupDesc);
                bundle.putString(StringContract.IntentStrings.GROUP_PASSWORD, groupPassword);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.ChatFragment, fragment,"FragmentGroupDetailScreen").commit();*/
            }
        }
    }

    public void startForwardMessageActivity() {
        //forword flag
        SharedPreferences forwardPref = getActivity().getSharedPreferences("forwardPref", MODE_PRIVATE);
        SharedPreferences.Editor editorForward = forwardPref.edit();
        editorForward.putString("UserId", NextGroupOrUserId);
        editorForward.commit();

        Intent intent = new Intent(getContext(), CometChatForwardMessageScreenActivity.class);
        intent.putExtra(StringContract.IntentStrings.AVATAR, getArguments().getString(StringContract.IntentStrings.AVATAR));
        intent.putExtra(StringContract.IntentStrings.NAME, getArguments().getString(StringContract.IntentStrings.NAME));
        intent.putExtra(StringContract.IntentStrings.TYPE, getArguments().getString(StringContract.IntentStrings.TYPE));
        intent.putExtra(StringContract.IntentStrings.STATUS, getArguments().getString(StringContract.IntentStrings.STATUS));
        intent.putExtra(StringContract.IntentStrings.TABS, getArguments().getString(StringContract.IntentStrings.TABS));
        intent.putExtra(StringContract.IntentStrings.UID, NextGroupOrUserId);
        intent.putExtra("teamId", getArguments().getString("teamId"));
        if (type != null && type.equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
            intent.putExtra(StringContract.IntentStrings.GUID, getArguments().getString(StringContract.IntentStrings.GUID));
//          intent.putExtra(StringContract.IntentStrings.MEMBER_COUNT, getArguments().getString(StringContract.IntentStrings.MEMBER_COUNT));
            intent.putExtra(StringContract.IntentStrings.GROUP_DESC, getArguments().getString(StringContract.IntentStrings.GROUP_DESC));
            intent.putExtra(StringContract.IntentStrings.GROUP_PASSWORD, getArguments().getString(StringContract.IntentStrings.GROUP_PASSWORD));
            intent.putExtra(StringContract.IntentStrings.GROUP_TYPE, getArguments().getString(StringContract.IntentStrings.GROUP_TYPE));
            intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, getArguments().getString(StringContract.IntentStrings.GROUP_TYPE));
            intent.putExtra(StringContract.IntentStrings.ALL_MEMBERS_STRING, group_all_members_id);
        }
        //intent.putExtra(StringContract.IntentStrings.TIME_ZONE, getArguments().getString(StringContract.IntentStrings.TIME_ZONE));
        if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
            intent.putExtra(CometChatConstants.MESSAGE_TYPE_TEXT, ((TextMessage) baseMessage).getText());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE, CometChatConstants.MESSAGE_TYPE_TEXT);
        } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_IMAGE) ||
                baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_AUDIO) ||
                baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_VIDEO) ||
                baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_FILE)) {
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_NAME, ((MediaMessage) baseMessage).getAttachment().getFileName());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_URL, ((MediaMessage) baseMessage).getAttachment().getFileUrl());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_MIME_TYPE, ((MediaMessage) baseMessage).getAttachment().getFileMimeType());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_EXTENSION, ((MediaMessage) baseMessage).getAttachment().getFileExtension());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_SIZE, ((MediaMessage) baseMessage).getAttachment().getFileSize());
            intent.putExtra(StringContract.IntentStrings.TYPE, baseMessage.getType());
        }
        startActivity(intent);
        getActivity().finish();
    }

    public void shareMessage() {
        Log.i(TAG, "shareMessage: ");
        if (baseMessage != null && baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TITLE, getResources().getString(R.string.app_name));
            shareIntent.putExtra(Intent.EXTRA_TEXT, ((TextMessage) baseMessage).getText());
            shareIntent.setType("text/plain");
            Intent intent = Intent.createChooser(shareIntent, getResources().getString(R.string.share_message));
            startActivity(intent);
        } else if (baseMessage != null && baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_IMAGE)) {
            String mediaName = ((MediaMessage) baseMessage).getAttachment().getFileName();
            Glide.with(context).asBitmap().load(((MediaMessage) baseMessage).getAttachment().getFileUrl()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), resource, mediaName, null);
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                    shareIntent.setType(((MediaMessage) baseMessage).getAttachment().getFileMimeType());
                    Intent intent = Intent.createChooser(shareIntent, getResources().getString(R.string.share_message));
                    startActivity(intent);
                }
            });
        }
    }

    public void startThreadActivity() {
        Log.i(TAG, "startThreadActivity: tabs" + tabs);
        Log.i(TAG, "startThreadActivity: clicked type" + type);
        Log.i(TAG, "startThreadActivity: clicked message type" + baseMessage.getType());
        Intent intent = new Intent(getContext(), CometChatThreadMessageActivity.class);
        intent.putExtra(StringContract.IntentStrings.CONVERSATION_NAME, name);
        intent.putExtra(StringContract.IntentStrings.NAME, baseMessage.getSender().getName());
        intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getSender().getUid());
        intent.putExtra(StringContract.IntentStrings.AVATAR, baseMessage.getSender().getAvatar());
        intent.putExtra(StringContract.IntentStrings.PARENT_ID, baseMessage.getId());
        intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE, baseMessage.getType());
        intent.putExtra(StringContract.IntentStrings.REPLY_COUNT, baseMessage.getReplyCount());
        intent.putExtra(StringContract.IntentStrings.SENTAT, baseMessage.getSentAt());
        intent.putExtra(StringContract.IntentStrings.TABS, tabs);
        if (tabs.equals("3") || tabs.equals("4")) {
            intent.putExtra(StringContract.IntentStrings.TEAM_ID, team_Ids);
        }
        if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT))
            intent.putExtra(StringContract.IntentStrings.TEXTMESSAGE, ((TextMessage) baseMessage).getText());
        else {
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_NAME, ((MediaMessage) baseMessage).getAttachment().getFileName());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_EXTENSION, ((MediaMessage) baseMessage).getAttachment().getFileExtension());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_URL, ((MediaMessage) baseMessage).getAttachment().getFileUrl());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_SIZE, ((MediaMessage) baseMessage).getAttachment().getFileSize());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_MIME_TYPE, ((MediaMessage) baseMessage).getAttachment().getFileMimeType());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_MIME_TYPE, ((MediaMessage) baseMessage).getAttachment().getFileMimeType());
        }
        intent.putExtra(StringContract.IntentStrings.TYPE, type);
        if (type.equals(CometChatConstants.CONVERSATION_TYPE_GROUP)) {
            intent.putExtra(StringContract.IntentStrings.GUID, NextGroupOrUserId);
        } else {
            intent.putExtra(StringContract.IntentStrings.UID, NextGroupOrUserId);
        }
        startActivity(intent);
    }

    @Override
    public void setLongMessageClick(List<BaseMessage> baseMessagesList) {
        Log.e(TAG, "setLongMessageClick: " + baseMessagesList + " tabs " + tabs);
        isReply = false;
        isEdit = false;
        messageActionFragment = new MessageActionFragment();
        replyMessageLayout.setVisibility(GONE);
        editMessageLayout.setVisibility(GONE);
        boolean shareVisible = true;
        boolean messageInfoVisible = true;
        boolean copyVisible = true;
        boolean threadVisible = true;
        boolean replyVisible = true;
        boolean editVisible = true;
        boolean deleteVisible = true;
        boolean forwardVisible;

        if (tabs.equals("1") || tabs.equals("2")) {
            forwardVisible = true;
            Log.i(TAG, "setLongMessageClick: 1 " + forwardVisible);
        } else {
            forwardVisible = false;
            Log.i(TAG, "setLongMessageClick: 2 " + forwardVisible);
        }

        List<BaseMessage> textMessageList = new ArrayList<>();
        List<BaseMessage> mediaMessageList = new ArrayList<>();

        for (BaseMessage baseMessage : baseMessagesList) {
            Log.i(TAG, "setLongMessageClick: category " + baseMessage.getCategory());
            Log.i(TAG, "setLongMessageClick: category " + baseMessage.getType());
            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                textMessageList.add(baseMessage);
            } else {
                mediaMessageList.add(baseMessage);
            }


        }
        if (textMessageList.size() == 1) {
            BaseMessage basemessage = textMessageList.get(0);
            if (basemessage != null && basemessage.getSender() != null) {
                if (!(basemessage instanceof Action) && basemessage.getDeletedAt() == 0) {
                    baseMessage = basemessage;
                    if (basemessage.getReplyCount() > 0)
                        threadVisible = false;
                    else
                        threadVisible = true;
                    if (basemessage.getSender().getUid().equals(CometChat.getLoggedInUser().getUid())) {
                        deleteVisible = true;
                        editVisible = true;
                        if (tabs.equals("1") || tabs.equals("2")) {
                            forwardVisible = true;
                            Log.i(TAG, "setLongMessageClick: 3 " + forwardVisible);
                        } else {
                            forwardVisible = false;
                            Log.i(TAG, "setLongMessageClick: 4 " + forwardVisible);
                        }
                    } else {
                        editVisible = false;
                        if (tabs.equals("1") || tabs.equals("2")) {
                            forwardVisible = true;
                            Log.i(TAG, "setLongMessageClick: 5 " + forwardVisible);
                        } else {
                            forwardVisible = false;
                            Log.i(TAG, "setLongMessageClick: 6 " + forwardVisible);
                        }
                        if (loggedInUserScope != null && (loggedInUserScope.equals(CometChatConstants.SCOPE_ADMIN) || loggedInUserScope.equals(CometChatConstants.SCOPE_MODERATOR))) {
                            deleteVisible = true;
                        } else {
                            deleteVisible = false;
                        }
                    }
                }
            }
        }

        if (mediaMessageList.size() == 1) {
            BaseMessage basemessage = mediaMessageList.get(0);
            if (basemessage != null && basemessage.getSender() != null) {
                if (!(basemessage instanceof Action) && basemessage.getDeletedAt() == 0) {
                    baseMessage = basemessage;
                    if (basemessage.getReplyCount() > 0)
                        threadVisible = false;
                    else
                        threadVisible = true;
                    copyVisible = false;
                    if (basemessage.getSender().getUid().equals(CometChat.getLoggedInUser().getUid())) {
                        deleteVisible = true;
                        editVisible = false;
                        if (tabs.equals("1") || tabs.equals("2")) {
                            Log.i(TAG, "setLongMessageClick: 7 " + forwardVisible);
                            forwardVisible = true;
                        } else {
                            forwardVisible = false;
                            Log.i(TAG, "setLongMessageClick: 8 " + forwardVisible);
                        }
                    } else {
                        if (loggedInUserScope != null && (loggedInUserScope.equals(CometChatConstants.SCOPE_ADMIN) || loggedInUserScope.equals(CometChatConstants.SCOPE_MODERATOR))) {
                            deleteVisible = true;
                        } else {
                            deleteVisible = false;
                        }
                        if (tabs.equals("1") || tabs.equals("2")) {
                            Log.i(TAG, "setLongMessageClick: 9 " + forwardVisible);
                            forwardVisible = true;
                        } else {
                            forwardVisible = false;
                            Log.i(TAG, "setLongMessageClick: 10 " + forwardVisible);
                        }
                        editVisible = false;
                    }
                }
            }
        }

        if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP) &&
                baseMessage.getSender().getUid().equals(loggedInUser.getUid()))
            messageInfoVisible=true;

        if (baseMessage.getType().equals("extension_whiteboard") && baseMessage.getCategory().equalsIgnoreCase("custom")) {
            shareVisible = false;
            copyVisible = false;
            threadVisible = false;
            replyVisible = false;
            editVisible = false;
            deleteVisible = false;
            forwardVisible = false;
            messageInfoVisible=false;
            if (baseMessage.getSender().getUid().equals(CometChat.getLoggedInUser().getUid())) {
                deleteVisible = true;
            }
        }
        baseMessages = baseMessagesList;
        Log.i(TAG, "setLongMessageClick: forwardVisible final" + forwardVisible);

        Bundle bundle = new Bundle();
        bundle.putBoolean("copyVisible", copyVisible);
        bundle.putBoolean("threadVisible", threadVisible);
        bundle.putBoolean("shareVisible", shareVisible);
        bundle.putBoolean("editVisible", editVisible);
        bundle.putBoolean("deleteVisible", deleteVisible);
        bundle.putBoolean("replyVisible", replyVisible);
        bundle.putBoolean("messageInfoVisible", messageInfoVisible);
        bundle.putBoolean("forwardVisible", forwardVisible);
        bundle.putString("type", CometChatMessageListActivity.class.getName());
        messageActionFragment.setArguments(bundle);
        messageActionFragment.show(getFragmentManager(), messageActionFragment.getTag());
        messageActionFragment.setMessageActionListener(new MessageActionFragment.MessageActionListener() {
            @Override
            public void onThreadMessageClick() {
                Log.i(TAG, "onThreadMessageClick: ");
                messageActionFragment.dismiss();
                startThreadActivity();
            }

            @Override
            public void onEditMessageClick() {
                if (baseMessage != null && baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                    isEdit = true;
                    isReply = false;
                    tvMessageTitle.setText(getResources().getString(R.string.edit_message));
                    tvMessageSubTitle.setText(((TextMessage) baseMessage).getText());
                    composeBox.ivMic.setVisibility(GONE);
                    composeBox.ivSend.setVisibility(View.VISIBLE);
                    editMessageLayout.setVisibility(View.VISIBLE);

                    //baseMessage.getId()
                    try {
                        /*when user long press on message and then select edit
                         * 1. we will convert that message in english and show it in composbox edittext so that user can edit that message
                         * 2. and when we send message then edit message will called and then message will edited
                         * code added by rahul maske */
                        JSONObject body = new JSONObject();
                        JSONArray languages = new JSONArray();
                        languages.put("en");
                        body.put("msgId", baseMessage.getId());
                        body.put("languages", languages);
                        body.put("text", "" + ((TextMessage) baseMessage).getText());

                        CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                new CometChat.CallbackListener<JSONObject>() {
                                    @Override
                                    public void onSuccess(JSONObject jsonObject) {
                                        // Result of translations
                                        Log.i(TAG, "onSuccess: edited message json response" + jsonObject);
                                        try {
                                            String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");
                                            Log.i(TAG, "onSuccess: translated string  " + messageTranslatedString);

                                            composeBox.etComposeBox.setText(messageTranslatedString);
                                            if (messageAdapter != null) {
                                                messageAdapter.setSelectedMessage(baseMessage.getId());
                                                messageAdapter.notifyDataSetChanged();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(CometChatException e) {
                                        // Some error occured
                                    }
                                });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onReplyMessageClick() {
                replyMessage();
            }

            @Override
            public void onForwardMessageClick() {
                if (tabs.equals("3") || tabs.equals("4")) {

                } else {
                    startForwardMessageActivity();
                }
            }

            @Override
            public void onDeleteMessageClick() {
                //deleteMessage(baseMessage);
                if (tabs.equals("2")/* || !baseMessage.getType().equals("text")*/) {
                    deleteMessage(baseMessage);
                } else {
                    onDeleteClickForFriendAndTeams("");
                }

                /*if (messageAdapter != null) {
                    if (type=="group"){
                        baseMessage.get
                    }

                    messageAdapter.clearLongClickSelectedItem();
                    messageAdapter.notifyDataSetChanged();
                }*/
                Log.i(TAG, "onDeleteMessageClick:  message for delete " + baseMessage);
            }

            @Override
            public void onCopyMessageClick() {
                String message = "";
                for (BaseMessage bMessage : baseMessages) {
                    if (bMessage.getDeletedAt() == 0 && bMessage instanceof TextMessage) {
                        message = message + "[" + Utils.getLastMessageDate(bMessage.getSentAt()) + "] " + bMessage.getSender().getName() + ": " + ((TextMessage) bMessage).getText();
                    }
                }
                Log.e(TAG, "onCopy: " + message);
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("MessageAdapter", message);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(context, getResources().getString(R.string.text_copied_clipboard), Toast.LENGTH_LONG).show();
                if (messageAdapter != null) {
                    messageAdapter.clearLongClickSelectedItem();
                    messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onShareMessageClick() {
                shareMessage();
            }

            @Override
            public void onMessageInfoClick() {
                Intent intent = new Intent(context, CometChatMessageInfoScreenActivity.class);
                intent.putExtra(UIKitConstants.IntentStrings.ID, baseMessage.getId());
                intent.putExtra(UIKitConstants.IntentStrings.MESSAGE_TYPE, baseMessage.getType());
                intent.putExtra(UIKitConstants.IntentStrings.SENTAT, baseMessage.getSentAt());
                if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                    intent.putExtra(UIKitConstants.IntentStrings.TEXTMESSAGE, ((TextMessage) baseMessage).getText());
                } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_CUSTOM)) {
                    if (((CustomMessage) baseMessage).getCustomData() != null)
                        intent.putExtra(UIKitConstants.IntentStrings.CUSTOM_MESSAGE,
                                ((CustomMessage) baseMessage).getCustomData().toString());
                    if (baseMessage.getType().equals(UIKitConstants.IntentStrings.LOCATION)) {
                        intent.putExtra(UIKitConstants.IntentStrings.MESSAGE_TYPE,
                                UIKitConstants.IntentStrings.LOCATION);
                    } else if (baseMessage.getType().equals(UIKitConstants.IntentStrings.STICKERS)) {
                        intent.putExtra(UIKitConstants.IntentStrings.MESSAGE_TYPE, UIKitConstants.IntentStrings.STICKERS);
                    } /*else if (baseMessage.getType().equals(UIKitConstants.IntentStrings.WHITEBOARD)) {
                        intent.putExtra(UIKitConstants.IntentStrings.MESSAGE_TYPE,
                                UIKitConstants.IntentStrings.WHITEBOARD);
                        intent.putExtra(UIKitConstants.IntentStrings.TEXTMESSAGE, Extensions.getWhiteBoardUrl(baseMessage));
                    } else if (baseMessage.getType().equals(UIKitConstants.IntentStrings.WRITEBOARD)) {
                        intent.putExtra(UIKitConstants.IntentStrings.MESSAGE_TYPE,
                                UIKitConstants.IntentStrings.WRITEBOARD);
                        intent.putExtra(UIKitConstants.IntentStrings.TEXTMESSAGE, Extensions.getWriteBoardUrl(baseMessage));
                    } */else if (baseMessage.getType().equals(UIKitConstants.IntentStrings.GROUP_CALL)) {
                        intent.putExtra(UIKitConstants.IntentStrings.MESSAGE_TYPE,
                                UIKitConstants.IntentStrings.GROUP_CALL);
                    } else {
                        intent.putExtra(UIKitConstants.IntentStrings.MESSAGE_TYPE,
                                UIKitConstants.IntentStrings.POLLS);
                    }
                } else {
                    boolean isImageNotSafe = Extensions.getImageModeration(context, baseMessage);
                    intent.putExtra(UIKitConstants.IntentStrings.MESSAGE_TYPE_IMAGE_URL,
                            ((MediaMessage) baseMessage).getAttachment().getFileUrl());
                    intent.putExtra(UIKitConstants.IntentStrings.MESSAGE_TYPE_IMAGE_NAME,
                            ((MediaMessage) baseMessage).getAttachment().getFileName());
                    intent.putExtra(UIKitConstants.IntentStrings.MESSAGE_TYPE_IMAGE_SIZE,
                            ((MediaMessage) baseMessage).getAttachment().getFileSize());
                    intent.putExtra(UIKitConstants.IntentStrings.MESSAGE_TYPE_IMAGE_EXTENSION,
                            ((MediaMessage) baseMessage).getAttachment().getFileExtension());
                    intent.putExtra(UIKitConstants.IntentStrings.IMAGE_MODERATION, isImageNotSafe);
                }
                getActivity().startActivity(intent);
            }
        });
    }

    public void replyMessage() {
        if (baseMessage != null) {
            isReply = true;
            replyTitle.setText(baseMessage.getSender().getName());
            replyMedia.setVisibility(View.VISIBLE);
            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                replyMessage.setText(((TextMessage) baseMessage).getText());
                replyMedia.setVisibility(GONE);
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_IMAGE)) {
                replyMessage.setText(getResources().getString(R.string.shared_a_image));
                Glide.with(context).load(((MediaMessage) baseMessage).getAttachment().getFileUrl()).into(replyMedia);
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_AUDIO)) {
                String messageStr = String.format(getResources().getString(R.string.shared_a_audio),
                        Utils.getFileSize(((MediaMessage) baseMessage).getAttachment().getFileSize()));
                replyMessage.setText(messageStr);
                replyMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_library_music_24dp, 0, 0, 0);
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_VIDEO)) {
                replyMessage.setText(getResources().getString(R.string.shared_a_video));
                Glide.with(context).load(((MediaMessage) baseMessage).getAttachment().getFileUrl()).into(replyMedia);
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_FILE)) {
                String messageStr = String.format(getResources().getString(R.string.shared_a_file),
                        Utils.getFileSize(((MediaMessage) baseMessage).getAttachment().getFileSize()));
                replyMessage.setText(messageStr);
                replyMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_insert_drive_file_black_24dp, 0, 0, 0);
            }
            composeBox.ivMic.setVisibility(GONE);
            composeBox.ivSend.setVisibility(View.VISIBLE);
            replyMessageLayout.setVisibility(View.VISIBLE);
            if (messageAdapter != null) {
                messageAdapter.setSelectedMessage(baseMessage.getId());
                messageAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        if (messageAdapter != null)
            messageAdapter.clearLongClickSelectedItem();
        dialog.dismiss();
    }

    /*this method we are creating to store sticker on our server
     * before sending sticker to another user we have to save that sticker to our server db
     *but not used
     * */
    private void saveStickerToServer(String action, String img_url, String name) {
        String url = Urls_.SAVE_STICKER_TO_THE_SERVER;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.IMG, img_url);

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getActivity());
        Log.e(TAG, "saveStickerToServer: request body " + requestBody);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getActivity());
                if (response != null) {
                    JSONObject responseJsonObj = new JSONObject(response);
                    JSONArray getStickersArray = responseJsonObj.getJSONArray("get_sticker");
                    JSONObject stickerData = new JSONObject();
                    Log.e(TAG, "saveStickerToServer : response" + response);

                    for (int i = 0; i <= getStickersArray.length(); i++) {
                        try {
                            String server_img_url = getStickersArray.getJSONObject(i).getString("url");
                            //sendMediaMessageForStickers(server_img_url, name);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    Toast.makeText(context, "Server error..", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* before sending sticker to the user first we need to download that sticker and store in our android device
     * then only we can send to the user from cometchat
     * created by rahul maske..*/
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";

        private Bitmap downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
                Log.d(TAG, "success!!");
            } catch (Exception e) {
                Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            Log.d(TAG, "onPostExe");
            saveImage(result, "temp_sticker.png");
        }

    }

    /*method created to store sticker in device
     *
     *  create by rahul maske */
    public void saveImage(Bitmap imageBitmap, String ImageName) {
        FileOutputStream foStream;
        try {
            //creating new code for storing image
            String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sagesurfer";
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream fOut = null;
            File file = new File(fullPath, "sticker.png");
            if (file.exists())
                file.delete();
            file.createNewFile();

            fOut = new FileOutputStream(file);
            // 100 means no compression, the lower you go, the stronger the compression
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            String imageFullPath = file.getAbsolutePath();
            Log.e(TAG, "saveImage: " + imageFullPath);
            sendMediaMessageForStickers(imageFullPath);
        } catch (Exception e) {
            Log.d("saveImage", "Exception 2, Something went wrong!");
            e.printStackTrace();
        }
    }

    /*method created to send sticker to cometchat user from device
     * create by rahul maske */

    public void sendMediaMessageForStickers(String file_Path) throws
            MalformedURLException, URISyntaxException {
        String receiverID = NextGroupOrUserId;
        String messageType = CometChatConstants.MESSAGE_TYPE_IMAGE;

        String filePath = file_Path;

        MediaMessage mediaMessage;
        if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER)) {
            String receiverType = CometChatConstants.RECEIVER_TYPE_USER;
            mediaMessage = new MediaMessage(receiverID, new File(filePath), messageType, receiverType);
        } else {
            String receiverType = CometChatConstants.RECEIVER_TYPE_GROUP;
            mediaMessage = new MediaMessage(receiverID, new File(filePath), messageType, receiverType);
        }
        JSONObject jsonObject = new JSONObject();
        JSONArray languageArray = new JSONArray();
        languageArray.put("");

        switch (tabs) {
            case "1":
                try {
                    jsonObject.put("team_logs_id", 0);
                    jsonObject.put("message_translation_languages", languageArray);
                    Log.i(TAG, "sendMediaMessageForStickers: tab 1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "2":
                try {
                    jsonObject.put("message_translation_languages", languageArray);
                    Log.i(TAG, "sendMediaMessageForStickers: tab 2");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "3":
                team_logs_id = receiverId + "_-" + teamId + "_-" + 3;
                try {
                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("message_translation_languages", languageArray);
                    Log.i(TAG, "sendMediaMessageForStickers: tab 3");
                    //jsonObject.put("path", file.getAbsolutePath());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "4":
                team_logs_id = receiverId + "_-" + teamId + "_-" + 4;
                try {
                    Log.i(TAG, "sendMediaMessageForStickers: tab 4");
                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("message_translation_languages", languageArray);
                    //jsonObject.put("path", file.getAbsolutePath());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }
        mediaMessage.setMetadata(jsonObject);

        CometChat.sendMediaMessage(mediaMessage, new CometChat.CallbackListener<MediaMessage>() {
            @Override
            public void onSuccess(MediaMessage mediaMessage) {
                if (messageAdapter != null) {
                    messageAdapter.addMessage(mediaMessage);
                    scrollToBottom();
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "sendMediaMessageForStickers onError: " + e.getMessage());
            }
        });
    }


    /*this method is created to send chat log to the server
     *When user start chatting that means when send message we will call this webservice
     * From friend, group, team data will send data with there info
     *created by rahul maske
     * */
    private void saveChatLogToTheServer(String ChatType, String Message, String
            chat_group_id,
                                        int chat_message_id) {
        String url = Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();
        SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(General.USER_ID, null);
        String DomainURL = domainUrlPref.getString(General.DOMAIN, null);
        if (tabs.equals("1")) {
            team_Ids = "";
        } else if (tabs.equals("2")) {
            team_Ids = "" + chat_group_id;
        }

        /*This block is created for removing login user id from group members string */
        StringBuffer groupMembersId = new StringBuffer();
        if (group_all_members_id == null || group_all_members_id.isEmpty()) {
            group_all_members_id = "";
        } else {
            String[] groupAllMembers = group_all_members_id.split(",");
            for (String id : groupAllMembers) {
                if (!id.equals("" + UserId)) {
                    if (groupMembersId.length() == 0) {
                        groupMembersId.append("" + id);
                    } else {
                        groupMembersId.append("," + id);
                    }
                }
            }
        }
        Log.i(TAG, "Calling test tag  saveChatLogToTheServer: groupMembersId" + groupMembersId);
        requestMap.put(General.ACTION, "cometchat_log");
        requestMap.put(General.FROM_CHAT, UserId);
        requestMap.put(General.USER_ID, UserId);
        requestMap.put(General.TO_CHAT, NextGroupOrUserId);
        requestMap.put(General.CHAT_TYPE, ChatType);
        requestMap.put(General.DIRECTION, "1");
        requestMap.put(General.MESSAGE, Message);
        requestMap.put(General.CHAT_GROUP_ID, "" + chat_group_id);
        requestMap.put(General.PRIVATE_GROUP_ID, team_Ids);
        requestMap.put(General.READ_CHAT, "0");
        requestMap.put(General.COMET_CHAT_TYPE, "" + comet_chat_type);
        requestMap.put(General.CHAT_MSG_ID, "" + chat_message_id);
        requestMap.put(General.GROUP_ALL_MEMBERS_ID_, "" + group_all_members_id);
        requestMap.put(General.RECEIVE_MAIL, receiverMail);
        requestMap.put(General.LAST_CONVERSATION_STATUS, status);
        Log.i(TAG, "Calling test tag saveChatLogToTheServer: From_chat " + UserId + " To_chat " + NextGroupOrUserId + " CHAT_TYPE " + ChatType
                + " MESSAGE " + Message + " CHAT_GROUP_ID " + chat_group_id + " PRIVATE_GROUP_ID " + team_Ids + " COMET_CHAT_TYPE " + comet_chat_type
                + " CHAT_MSG_ID " + chat_message_id + " GROUP_ALL_MEMBERS_ID_ " + group_all_members_id + " receiverMail " + receiverMail + " LAST_CONVERSATION_STATUS " + status);

        RequestBody requestBody = NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());
        //Log.e(TAG, "saveChatLogToTheServer: request body " + requestBody);
        try {
            if (requestBody != null) {
                String response = NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                if (response != null) {
                    Log.i(TAG, "Calling test tag saveChatLogToTheServerRequest:  response " + response);
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "Calling test tag saveChatLogToTheServerRequest: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void logOutEnrtyCometchat() {
        SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(General.USER_ID, null);
        SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String DomainURL = domainUrlPref.getString(General.DOMAIN, null);
        if (!getArguments().getString(StringContract.IntentStrings.TYPE).equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
            Group_id = "";
        }

        String url = Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "cometchat_outlog");
        requestMap.put(General.MISSED_CALL, "");
        requestMap.put(General.COMET_CHAT_TYPE, "" + comet_chat_type);
        requestMap.put(General.CHAT_TYPE, ChatType);
        requestMap.put(General.GROUP_ID, Group_id);
        requestMap.put(General.USER_ID, UserId);

        RequestBody requestBody = NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());
        // Log.e(TAG, "saveChatLogToTheServer: request body " + requestBody);
        Log.i(TAG, "logOutEnrtyCometchat: Domain " + DomainURL + url);
        try {
            if (requestBody != null) {
                String response = NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                if (response != null) {
                    Log.i(TAG, "logOutEnrtyCometchat:  response " + response);
                } else {
                    Log.i(TAG, "logOutEnrtyCometchat:  null  ");
                }
            } else {
                Log.i(TAG, "logOutEnrtyCometchat:  null2");
            }
        } catch (Exception e) {
            Log.i(TAG, "logOutEnrtyCometchat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*delete message method created for friend, team and */
    public void onDeleteClickForFriendAndTeams(String message) {
        isEdit = false;
        TextMessage textMessage;
        JSONObject jsonObject = new JSONObject();

        if (baseMessage.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
            textMessage = new TextMessage(baseMessage.getReceiverUid(), message, CometChatConstants.RECEIVER_TYPE_USER);
        else
            textMessage = new TextMessage(baseMessage.getReceiverUid(), message, CometChatConstants.RECEIVER_TYPE_GROUP);

        sendTypingIndicator(true);

        textMessage.setId(baseMessage.getId());
        JSONArray languageArray = new JSONArray();
        languageArray.put("en");
        languageArray.put("hi");
        languageArray.put("ar");
        languageArray.put("ko");
        languageArray.put("de");
        languageArray.put("fr");
        languageArray.put("fr-CA");
        languageArray.put("es");
        languageArray.put("ru");
        languageArray.put("pt");
        languageArray.put("vi");
        languageArray.put("ur");

        switch (tabs) {
            //Friend chat
            case "1":
                try {
                    jsonObject.put("team_logs_id", 0);
                    jsonObject.put("deleted_one_to_one", 0);
                    jsonObject.put("message_translation_languages", languageArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "3":
                team_logs_id = receiverId + "_-" + teamId + "_-" + 3; //+"_sage036"
                try {
                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("deleted_one_to_one", 1);
                    jsonObject.put("message_translation_languages", languageArray);
                    Log.i(TAG, "sendMessage: TeamId " + teamId);
                    Log.i(TAG, "sendMessage: Hello its tab3 sending msg " + team_logs_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "4":
                team_logs_id = receiverId + "_-" + teamId + "_-" + 4; //+"_sage036"
                try {
                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("deleted_one_to_one", 1);
                    jsonObject.put("message_translation_languages", languageArray);
                    Log.i(TAG, "sendMessage: Hello its tab4 sending msg" + team_logs_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        Log.i(TAG, "editMessage:  metadata" + jsonObject);
        textMessage.setMetadata(jsonObject);
        CometChat.editMessage(textMessage, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage baseMessage1) {
                Log.e(TAG, "onSuccess: onDeleteClickForFriendAndTeams" + baseMessage1.toString());
                if (messageAdapter != null) {
                    try {
                        JSONObject body = new JSONObject();
                        JSONArray languages = new JSONArray();
                        languages.put(sp.getString("currentLang", currentLang));
                        body.put("msgId", baseMessage1.getId());
                        body.put("languages", languages);
                        body.put("text", "" + message);
                        baseMessage1.setDeletedAt(12324);
                        messageAdapter.setUpdatedMessage(baseMessage1);
                        /*CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                new CometChat.CallbackListener<JSONObject>() {
                                    @Override
                                    public void onSuccess(JSONObject jsonObject) {
                                        Log.i(TAG, "onSuccess: message-translation " + jsonObject);
                                        baseMessage.setDeletedAt(12324);
                                        messageAdapter.setUpdatedMessage(baseMessage);
                                    }
                                    @Override
                                    public void onError(CometChatException e) {
                                        Log.i(TAG, "onError: " + e.getMessage());
                                    }
                                });*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //messageAdapter.setUpdatedMessage(message);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: onDeleteClickForFriendAndTeams " + e.getMessage());
            }
        });
    }

    /*this method is created to send chat log to the server
     *When user start chatting that means when send message we will call this webservice
     * From friend, group, team data will send data with there info
     *created by rahul maske
     * */
    private void saveChatLogForEditMessage(String Message, int chat_message_id) {
        String url = Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();
        SharedPreferences UserInfoForUIKitPref = getActivity().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(General.USER_ID, null);
        String DomainURL = domainUrlPref.getString(General.DOMAIN, null);

        requestMap.put(General.ACTION, "update_message");
        requestMap.put("messageText", Message);
        requestMap.put("messageId", "" + chat_message_id);

        RequestBody requestBody = NetworkCall_.make(requestMap, DomainURL + url, TAG, getActivity());

        try {
            if (requestBody != null) {
                String response = NetworkCall_.post(DomainURL + url, requestBody, TAG, getActivity());
                if (response != null) {
                    Log.e(TAG, "saveChatLogForEditMessage:  response " + response);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "saveChatLogForEditMessage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*broadcast for message to show after forwarding*/
    private BroadcastReceiver onJsonReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String textMessage = intent.getStringExtra("textMessage");
                String uid = intent.getStringExtra("uid");
                TestModel message = intent.getParcelableExtra("message");
                String type = intent.getStringExtra("type");
                Log.i(TAG, "onReceive: getBaseMessage" + message.getBaseMessage());
                Toast.makeText(context, "received : " + textMessage, Toast.LENGTH_SHORT).show();
                /* new Handler().post(new Runnable() {
                public void run() {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(CometChatMessageScreen.this).attach(CometChatMessageScreen.this).commitAllowingStateLoss();
                }
//            });*/

                // onMessageReceived(baseMessage);

               /* rvChatListView.removeItemDecoration(stickyHeaderDecoration);
                messageAdapter = null;
                messagesRequest = null;*/




            /*ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(1).build();
            if (conversationsRequest == null) {
            conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(50).build();
            if (conversationListType != null)
                conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder()
                        .setConversationType(conversationListType).setLimit(50).build();
        }
            conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
                @Override
                public void onSuccess(List<Conversation> conversations) {
                    // Handle list of conversations
                    for (Conversation conversation:conversations){
                        Log.i(TAG, "onReceive onSuccess: "+conversation);
                    }
                }
                @Override
                public void onError(CometChatException e) {
                    // Handle failure
                }
            });*/
            }
        }
    };
}